/*
 * Copyright (C) 2011 A. Horn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mcsoxford.rss;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous loader for RSS feeds. RSS feeds can be loaded in FIFO order or
 * based on priority. Objects of this type can be constructed with one of the
 * provided static methods:
 * <ul>
 * <li>{@link #fifo()}</li>
 * <li>{@link #fifo(int)}</li>
 * <li>{@link #priority()}</li>
 * <li>{@link #priority(int)}</li>
 * </ul>
 * 
 * @author A. Horn
 */
public class RSSLoader {

  /**
   * Human-readable name of the thread loading RSS feeds
   */
  private final static String DEFAULT_THREAD_NAME = "Asynchronous RSS feed loader";

  /**
   * Thread-safe queue implementation that blocks on
   * {@link BlockingQueue#take()}.
   */
  private final BlockingQueue<RSSFuture> futures;

  /**
   * Flag changes are visible after operations on {@link #futures} queue.
   */
  private boolean stopped;

  /**
   * Create an object which can load RSS feeds asynchronously in FIFO order.
   * 
   * @see #fifo(int)
   */
  public static RSSLoader fifo() {
    return new RSSLoader(new LinkedBlockingQueue<RSSFuture>());
  }

  /**
   * Create an object which can load RSS feeds asynchronously in FIFO order.
   * 
   * @param capacity
   *          expected number of URIs to be loaded at a given time
   */
  public static RSSLoader fifo(int capacity) {
    return new RSSLoader(new LinkedBlockingQueue<RSSFuture>(capacity));
  }

  /**
   * Create an object which can load RSS feeds asynchronously based on priority.
   * 
   * @see #priority(int)
   */
  public static RSSLoader priority() {
    return new RSSLoader(new PriorityBlockingQueue<RSSFuture>());
  }

  /**
   * Create an object which can load RSS feeds asynchronously based on priority.
   * 
   * @param capacity
   *          expected number of URIs to be loaded at a given time
   */
  public static RSSLoader priority(int capacity) {
    return new RSSLoader(new PriorityBlockingQueue<RSSFuture>(capacity));
  }

  /**
   * Instantiate an object which can load RSS feeds asynchronously. The provided
   * {@link BlockingQueue} implementation determines the load behaviour.
   * 
   * @see LinkedBlockingQueue
   * @see PriorityBlockingQueue
   */
  RSSLoader(BlockingQueue<RSSFuture> futures) {
    this.futures = futures;
    new Thread(new Loader(new RSSReader()), DEFAULT_THREAD_NAME).start();
  }

  /**
   * Returns {@code true} if RSS feeds are currently being loaded, {@code false}
   * otherwise.
   */
  public boolean isLoading() {
    // order of conjuncts matters because of happens-before relationship
    return !futures.isEmpty() && !stopped;
  }

  /**
   * Stop thread after finishing loading pending RSS feed URIs. If this loader
   * has been constructed with {@link #priority()} or {@link #priority(int)},
   * only RSS feed loads with priority strictly greater than eight are going to
   * be completed.
   * <p>
   * Subsequent invocations of {@link #load(String)} and
   * {@link #load(String, int)} return {@code null}.
   */
  public void stop() {
    // flag writings happen-before enqueue
    stopped = true;
    futures.offer(SENTINEL);
  }

  /**
   * Loads the specified RSS feed URI asynchronously. If this loader has been
   * constructed with {@link #priority()} or {@link #priority(int)}, then a
   * default priority of three (3) is used. Otherwise, RSS feeds are loaded in
   * FIFO order.
   * <p>
   * Returns {@code null} if the RSS feed URI cannot be loaded due to resource
   * constraints or if {@link #stop()} has been previously called.
   * 
   * @param uri
   *          RSS feed URI to be loaded
   * 
   * @return object holding the future outcome of the scheduled load operation
   *         or {@code null} if the RSS feed URI cannot be loaded
   */
  public Future<RSSFeed> load(String uri) {
    return load(uri, RSSFuture.DEFAULT_PRIORITY);
  }

  /**
   * Loads the specified RSS feed URI asynchronously. For the specified priority
   * to determine the relative loading order of RSS feeds, this loader must have
   * been constructed with {@link #priority()} or {@link #priority(int)}.
   * Otherwise, RSS feeds are loaded in FIFO order.
   * <p>
   * Returns {@code null} if the RSS feed URI cannot be loaded due to resource
   * constraints or if {@link #stop()} has been previously called.
   * 
   * @param uri
   *          RSS feed URI to be loaded
   * @param priority
   *          larger integer gives higher priority
   * 
   * @return object holding the future outcome of the scheduled load operation
   *         or {@code null} if the RSS feed URI cannot be loaded
   */
  public Future<RSSFeed> load(String uri, int priority) {
    if (uri == null) {
      throw new IllegalArgumentException("RSS feed URI must not be null.");
    }

    // optimization (after flag changes have become visible)
    if (stopped) {
      return null;
    }

    // flag readings happen-after enqueue
    final RSSFuture future = new RSSFuture(uri, priority);
    final boolean ok = futures.offer(future);
    if (!ok || stopped) {
      return null;
    }

    return future;
  }

  /**
   * Internal consumer of RSS feed URIs stored in the blocking queue.
   */
  class Loader implements Runnable {

    private final RSSReader reader;

    Loader(RSSReader reader) {
      this.reader = reader;
    }

    /**
     * Keep on loading RSS feeds by dequeuing futures until the sentinel is
     * encountered.
     */
    @Override
    public void run() {
      RSSFuture future = null;
      try {
        RSSFeed feed;
        while ((future = futures.take()) != SENTINEL) {
          if (future.status.compareAndSet(RSSFuture.READY, RSSFuture.LOADING)) {
            // perform loading outside of locked region
            feed = reader.load(future.uri);

            // set successfully loaded RSS feed
            future.set(feed, /* error */null);

            // ensure RSSFuture::isDone() returns true
            future.status.compareAndSet(RSSFuture.LOADING, RSSFuture.LOADED);
          }
        }
      } catch (InterruptedException e) {
        // Restore the interrupted status
        Thread.currentThread().interrupt();
      } catch (RSSReaderException e) {
        // set cause for load() error
        future.set(/* feed */null, e);
      }
    }

  }

  /**
   * Internal sentinel to stop the thread that is loading RSS feeds.
   */
  private final static RSSFuture SENTINEL = new RSSFuture(null, /* priority */7);

  /**
   * Offer callers control over the asynchronous loading of an RSS feed.
   */
  static class RSSFuture implements Future<RSSFeed>, Comparable<RSSFuture> {

    static final int DEFAULT_PRIORITY = 3;
    static final int READY = 0;
    static final int LOADING = 1;
    static final int LOADED = 2;
    static final int CANCELLED = 4;

    /** RSS feed URI */
    final String uri;

    /** Larger integer gives higher priority */
    final int priority;

    AtomicInteger status;

    boolean waiting;
    RSSFeed feed;
    Exception cause;

    RSSFuture(String uri, int priority) {
      this.uri = uri;
      this.priority = priority;
      status = new AtomicInteger(READY);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return isCancelled() || status.compareAndSet(READY, CANCELLED);
    }

    @Override
    public boolean isCancelled() {
      return status.get() == CANCELLED;
    }

    @Override
    public boolean isDone() {
      return (status.get() & (LOADED | CANCELLED)) != 0;
    }

    @Override
    public synchronized RSSFeed get() throws InterruptedException, ExecutionException {
      if (feed == null && cause == null) {
        try {
          waiting = true;

          // guard against spurious wakeups
          while (waiting) {
            wait();
          }
        } finally {
          waiting = false;
        }
      }

      if (cause != null) {
        throw new ExecutionException(cause);
      }

      return feed;
    }

    @Override
    public synchronized RSSFeed get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {

      if (feed == null && cause == null) {
        try {
          waiting = true;

          final long timeoutMillis = unit.toMillis(timeout);
          final long startMillis = System.currentTimeMillis();

          // guard against spurious wakeups
          while (waiting) {
            wait(timeoutMillis);

            // check timeout
            if (System.currentTimeMillis() - startMillis < timeoutMillis) {
              break;
            }
          }
        } finally {
          waiting = false;
        }

        return feed;
      }

      if (feed == null) {
        throw new TimeoutException("RSS feed loading timed out");
      } else if (cause != null) {
        throw new ExecutionException(cause);
      }

      return feed;
    }

    synchronized void set(RSSFeed feed, Exception cause) {
      this.feed = feed;
      this.cause = cause;

      if (waiting) {
        waiting = false;
        notifyAll();
      }
    }

    @Override
    public int compareTo(RSSFuture other) {
      // Note: head of PriorityQueue implementation is the least element
      return other.priority - priority;
    }
  }

}

