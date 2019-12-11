package eu.asterics.component.processor.amazonechocontrol;

import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Implements the time generation thread for the timer plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:14:00 PM Changed for openHAB by
 * @author Benjamin Aigner [aignerb@technikum-wien.at]
 */
public class TickGenerator implements Runnable {
    Thread t;
    long startTime, currentTime;
    boolean active = false;
    int count = 0;
    long timecount;

    private Thread runThread = null;

    final amazonEchoControlInstance owner;

    /**
     * The class constructor.
     */
    public TickGenerator(final amazonEchoControlInstance owner) {
        this.owner = owner;
    }

    /**
     * resets the time conter value.
     */
    public synchronized void reset() {
        count = 0;
    }

    // static int tcount=0;

    /**
     * the time generation thread.
     */
    @Override
    public void run() {
        runThread = Thread.currentThread();
        // System.out.println ("\n\n *** TimeGenThread "+ (++tcount) + "
        // started.\n");

        try {

            while (active == true) {
                currentTime = System.currentTimeMillis() - startTime;
                while ((currentTime > timecount) && (active == true)) {
                    owner.fetchState();
                    timecount += owner.updateRate;
                }
                Thread.sleep(owner.updateRate / 10);
            }
        } catch (InterruptedException e) {
            active = false;
        }

        runThread = null;
    }

    /**
     * called when model is started or resumed.
     */
    public synchronized void start() {
        if (runThread != null) {
            return;
        }

        startTime = System.currentTimeMillis();
        timecount = owner.updateRate;
        active = true;
        AstericsThreadPool.instance.execute(this);
    }

    /**
     * called when model is stopped or paused.
     */
    public synchronized void stop() {
        if (runThread != null) {
            runThread.interrupt();
        }

        active = false;
        count = 0;
    }

}
