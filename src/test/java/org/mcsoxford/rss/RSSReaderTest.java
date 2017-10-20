package org.mcsoxford.rss;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Integration test of HTTP client which retrieves RSS feeds.
 * 
 * @author Mr Horn
 */
public class RSSReaderTest {

  /**
   * URI of the "BBC News - World" RSS feed
   */
  private final static String BBC_NEWS = "http://feeds.bbci.co.uk/news/world/rss.xml";

  private final static int MAX_THREADS = 10;

  /**
   * Class under test
   */
  private RSSReader reader;

  @Before
  public void setup() {
    reader = new RSSReader();
  }

  @After
  public void teardown() {
    reader.close();
  }

  @Test
  public void get() throws RSSReaderException {
    final RSSFeed feed = reader.load(BBC_NEWS);

    assertEquals("BBC News - World", feed.getTitle());

    assertEquals(
        android.net.Uri.parse("http://www.bbc.co.uk/news/"),
        feed.getLink());

    assertEquals(
        "BBC News - World",
        feed.getDescription());
  }

  @Test
  public void notFound() {
    try {
      reader.load("http://www.google.co.uk/not/found");
    } catch (RSSReaderException e) {
      assertEquals(404, e.getStatus());
    }
  }



  @Test
  public void testConcurrency() throws InterruptedException {
    List<Runnable> runnables = new ArrayList<Runnable>();

    for(int i = 0; i < MAX_THREADS; ++i) {
      runnables.add(new Runnable() {
        @Override
        public void run()  {
          try {
            reader.load(BBC_NEWS);
          }
          catch (Exception e) {
            e.printStackTrace(System.err);
          }
        }
      });
    }

    assertConcurrent("Testing multipe connections", runnables, 30);
  }

  public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
    final int numThreads = runnables.size();
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
    try {
      final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
      final CountDownLatch afterInitBlocker = new CountDownLatch(1);
      final CountDownLatch allDone = new CountDownLatch(numThreads);
      for (final Runnable submittedTestRunnable : runnables) {
        threadPool.submit(new Runnable() {
          public void run() {
            allExecutorThreadsReady.countDown();
            try {
              afterInitBlocker.await();
              submittedTestRunnable.run();
            } catch (final Throwable e) {
              exceptions.add(e);
            } finally {
              allDone.countDown();
            }
          }
        });
      }
      // wait until all threads are ready
      assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent",
              allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
      // start all test runners
      afterInitBlocker.countDown();
      assertTrue(message +" timeout! More than " + maxTimeoutSeconds + " seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
    } finally {
      threadPool.shutdownNow();
    }
    assertTrue(message + "failed with exception(s) " + exceptions, exceptions.isEmpty());
  }
}
