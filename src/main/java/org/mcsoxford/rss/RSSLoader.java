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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous loader for RSS feeds.
 * 
 * @author A. Horn
 */
public class RSSLoader {

  /**
   * Human-readable name of the thread loading RSS feeds
   */
  private final static String DEFAULT_THREAD_NAME = "Asynchronous RSS feed loader";

  /**
   * Thread-safe FIFO data structure
   */
  private final BlockingQueue<RSSFuture> futures;

  /**
   * Flag changes are visible after operations on {@link #futures} queue.
   */
  private boolean stopped;

  /**
   * Instantiate an object which can load RSS feeds asynchronously.
   * 
   * @param capacity
   *          expected number of URIs to be loaded at a given time
   */
  public RSSLoader(int capacity) {
    this.futures = new LinkedBlockingQueue<RSSFuture>(capacity);
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
   * Stop thread after finishing loading pending RSS feed URIs.
   */
  public void stop() {
    // flag writings happen-before enqueue
    stopped = true;
    futures.offer(SENTINEL);
  }

  /**
   * Loads the specified RSS feed URI asynchronously. Returns {@code null} if
   * the RSS feed URI cannot be loaded due to resource constraints or if
   * {@link #stop()} has been previously called.
   * 
   * @param uri
   *          RSS feed URI to be loaded
   * 
   * @return object holding the future outcome of the scheduled load operation
   *         or {@code null} if the RSS feed URI cannot be loaded
   */
  public Future<RSSFeed> load(String uri) {
    if (uri == null) {
      throw new IllegalArgumentException("RSS feed URI must not be null.");
    }

    // optimization (after flag changes have become visible)
    if (stopped) {
      return null;
    }

    // flag readings happen-after enqueue
    final RSSFuture future = new RSSFuture(uri);
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
  private final static RSSFuture SENTINEL = new RSSFuture(null);

  /**
   * Offer callers control over the asynchronous loading of an RSS feed.
   */
  static class RSSFuture implements Future<RSSFeed> {

    static final int READY = 0;
    static final int LOADING = 1;
    static final int LOADED = 2;
    static final int CANCELLED = 4;

    final String uri;

    AtomicInteger status;

    boolean waiting;
    RSSFeed feed;
    Exception cause;

    RSSFuture(String uri) {
      this.uri = uri;
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
    public RSSFeed get(long timeout, TimeUnit unit) throws InterruptedException,
        ExecutionException, TimeoutException {

      if (feed == null && cause == null) {
        try {
          waiting = true;

          // guard against spurious wakeups
          while (waiting) {
            wait(unit.toMillis(timeout));
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

  }

}

