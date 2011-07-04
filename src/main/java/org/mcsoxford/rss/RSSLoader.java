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
 * Completed RSS feed loads can be retrieved with {@link RSSLoader#take()},
 * {@link RSSLoader#poll()} or {@link RSSLoader#poll(long, TimeUnit)}.
 * 
 * <p>
 * <b>Usage Example</b>
 * 
 * Suppose you want to load an array of RSS feed URIs concurrently before
 * retrieving the results one at a time. You could write this as:
 * 
 * <pre>
 * {@code 
 *  void fetchRSS(String[] uris) throws InterruptedException {
 *     RSSLoader loader = RSSLoader.fifo();
 *     for (String uri : uris) {
 *       loader.load(uri);
 *     }
 *     
 *     Future&lt;RSSFeed&gt; future;
 *     RSSFeed feed;
 *     for (int i = 0; i &lt; uris.length; i++) {
 *       future = loader.take();
 *       try {
 *         feed = future.get();
 *         use(feed);
 *       } catch (ExecutionException ignore) {}
 *     }
 * }}
 * </pre>
 *
 * </p>
 * 
 * @author A. Horn
 */
public class RSSLoader {

  /**
   * Human-readable name of the thread loading RSS feeds
   */
  private final static String DEFAULT_THREAD_NAME = "Asynchronous RSS feed loader";

  /**
   * Arrange incoming load requests on this queue.
   */
  private final BlockingQueue<RSSFuture> in;

  /**
   * Once the an RSS feed has completed loading, place the result on this queue.
   */
  private final BlockingQueue<RSSFuture> out;

  /**
   * Flag changes are visible after operations on {@link #in} queue.
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
  RSSLoader(BlockingQueue<RSSFuture> in) {
    this.in = in;
    this.out = new LinkedBlockingQueue<RSSFuture>();

    // start separate thread for loading of RSS feeds
    new Thread(new Loader(new RSSReader()), DEFAULT_THREAD_NAME).start();
  }

  /**
   * Returns {@code true} if RSS feeds are currently being loaded, {@code false}
   * otherwise.
   */
  public boolean isLoading() {
    // order of conjuncts matters because of happens-before relationship
    return !in.isEmpty() && !stopped;
  }

  /**
   * Stop thread after finishing loading pending RSS feed URIs. If this loader
   * has been constructed with {@link #priority()} or {@link #priority(int)},
   * only RSS feed loads with priority strictly greater than seven (7) are going
   * to be completed.
   * <p>
   * Subsequent invocations of {@link #load(String)} and
   * {@link #load(String, int)} return {@code null}.
   */
  public void stop() {
    // flag writings happen-before enqueue
    stopped = true;
    in.offer(SENTINEL);
  }

  /**
   * Loads the specified RSS feed URI asynchronously. If this loader has been
   * constructed with {@link #priority()} or {@link #priority(int)}, then a
   * default priority of three (3) is used. Otherwise, RSS feeds are loaded in
   * FIFO order.
   * <p>
   * Returns {@code null} if the RSS feed URI cannot be scheduled for loading
   * due to resource constraints or if {@link #stop()} has been previously
   * called.
   * <p>
   * Completed RSS feed loads can be retrieved by calling {@link #take()}.
   * Alternatively, non-blocking polling is possible with {@link #poll()}.
   * 
   * @param uri
   *          RSS feed URI to be loaded
   * 
   * @return Future representing the RSS feed scheduled for loading,
   *         {@code null} if scheduling failed
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
   * Returns {@code null} if the RSS feed URI cannot be scheduled for loading
   * due to resource constraints or if {@link #stop()} has been previously
   * called.
   * <p>
   * Completed RSS feed loads can be retrieved by calling {@link #take()}.
   * Alternatively, non-blocking polling is possible with {@link #poll()}.
   * 
   * @param uri
   *          RSS feed URI to be loaded
   * @param priority
   *          larger integer gives higher priority
   * 
   * @return Future representing the RSS feed scheduled for loading,
   *         {@code null} if scheduling failed
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
    final boolean ok = in.offer(future);

    if (!ok || stopped) {
      return null;
    }

    return future;
  }

  /**
   * Retrieves and removes the next Future representing the result of loading an
   * RSS feed, waiting if none are yet present.
   * 
   * @return the {@link Future} representing the loaded RSS feed
   * 
   * @throws InterruptedException
   *           if interrupted while waiting
   */
  public Future<RSSFeed> take() throws InterruptedException {
    return out.take();
  }

  /**
   * Retrieves and removes the next Future representing the result of loading an
   * RSS feed or {@code null} if none are present.
   * 
   * @return the {@link Future} representing the loaded RSS feed, or
   *         {@code null} if none are present
   * 
   * @throws InterruptedException
   *           if interrupted while waiting
   */
  public Future<RSSFeed> poll() {
    return out.poll();
  }

  /**
   * Retrieves and removes the Future representing the result of loading an RSS
   * feed, waiting if necessary up to the specified wait time if none are yet
   * present.
   * 
   * @param timeout
   *          how long to wait before giving up, in units of {@code unit}
   * @param unit
   *          a {@link TimeUnit} determining how to interpret the
   *          {@code timeout} parameter
   * @return the {@link Future} representing the loaded RSS feed, or
   *         {@code null} if none are present within the specified time interval
   * @throws InterruptedException
   *           if interrupted while waiting
   */
  public Future<RSSFeed> poll(long timeout, TimeUnit unit) throws InterruptedException {
    return out.poll(timeout, unit);
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
     * Keep on loading RSS feeds by dequeuing incoming tasks until the sentinel
     * is encountered.
     */
    @Override
    public void run() {
      try {
        RSSFuture future = null;
        RSSFeed feed;
        while ((future = in.take()) != SENTINEL) {

          if (future.status.compareAndSet(RSSFuture.READY, RSSFuture.LOADING)) {
            try {
              // perform loading outside of locked region
              feed = reader.load(future.uri);

              // set successfully loaded RSS feed
              future.set(feed, /* error */null);

              // enable caller to consume the loaded RSS feed
              out.add(future);
            } catch (RSSException e) {
              // throw ExecutionException when calling RSSFuture::get()
              future.set(/* feed */null, e);
            } catch (RSSFault e) {
              // throw ExecutionException when calling RSSFuture::get()
              future.set(/* feed */null, e);
            } finally {
              // RSSFuture::isDone() returns true even if an error occurred
              future.status.compareAndSet(RSSFuture.LOADING, RSSFuture.LOADED);
            }
          }

        }
      } catch (InterruptedException e) {
        // Restore the interrupted status
        Thread.currentThread().interrupt();
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
            if (System.currentTimeMillis() - startMillis > timeoutMillis) {
              throw new TimeoutException("RSS feed loading timed out");
            }
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

