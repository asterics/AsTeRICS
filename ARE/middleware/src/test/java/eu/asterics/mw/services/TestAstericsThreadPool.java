package eu.asterics.mw.services;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Date: 1/7/11
 * Time: 2:33 PM
 */
public class TestAstericsThreadPool extends TestCase
{
    final int numOfThreads = 100;
    final Set<Thread> threads = new HashSet<Thread>();

    public void setUp()
    {
        System.out.println("setup");

        for(int i = 0; i < numOfThreads; i++)
        {
            final long delay = Math.abs(new Random().nextInt(5000));
            threads.add(new Thread(new TestRunnable(i, delay)));
        }
    }

    public void testPool()
    {
        for(final Thread thread : threads)
        {
            System.out.println("starting thread: " + thread);
            thread.start();
        }

        try { Thread.currentThread().sleep(60000); } catch (InterruptedException ie) {}
    }

    private class TestRunnable implements Runnable
    {
        private final int threadID;
        private final long delay;

        public TestRunnable(final int threadID, final long delay)
        {
            this.threadID = threadID;
            this.delay = delay;
        }

        @Override
        public void run()
        {
            while(true)
            {
                System.out.println("loop");
                try { Thread.currentThread().sleep(delay); } catch (InterruptedException ie) {}
                AstericsThreadPool.instance.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        System.out.println("Hello world from thread " + threadID);
                    }
                });
            }
        }
    }
}