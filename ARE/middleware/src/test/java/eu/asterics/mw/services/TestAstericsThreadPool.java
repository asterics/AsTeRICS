/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.services;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Date: 1/7/11 Time: 2:33 PM
 */
public class TestAstericsThreadPool extends TestCase {
    final int numOfThreads = 100;
    final Set<Thread> threads = new HashSet<Thread>();

    @Override
    public void setUp() {
        System.out.println("setup");

        for (int i = 0; i < numOfThreads; i++) {
            final long delay = Math.abs(new Random().nextInt(5000));
            threads.add(new Thread(new TestRunnable(i, delay)));
        }
    }

    public void testPool() {
        for (final Thread thread : threads) {
            System.out.println("starting thread: " + thread);
            thread.start();
        }

        try {
            Thread.currentThread();
            Thread.sleep(60000);
        } catch (InterruptedException ie) {
        }
    }

    private class TestRunnable implements Runnable {
        private final int threadID;
        private final long delay;

        public TestRunnable(final int threadID, final long delay) {
            this.threadID = threadID;
            this.delay = delay;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("loop");
                try {
                    Thread.currentThread();
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                }
                AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Hello world from thread " + threadID);
                    }
                });
            }
        }
    }
}