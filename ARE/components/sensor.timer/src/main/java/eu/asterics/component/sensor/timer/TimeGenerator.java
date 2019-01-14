
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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.timer;

import java.util.concurrent.Future;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Implements the time generation thread for the timer plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:14:00 PM
 */
public class TimeGenerator implements Runnable {
    static final int MODE_ONE_SHOT = 0;
    static final int MODE_N_TIMES = 1;
    static final int MODE_LOOP = 2;
    static final int MODE_ONCE_STAY_ACTIVE = 3;
    static final int MEASURE_TIME = 4;

    volatile long startTime, currentTime;
    volatile boolean active = false;
    volatile int count = 0;

    Future<?> runningTaskFuture = null;

    final TimerInstance owner;

    /**
     * The class constructor.
     */
    public TimeGenerator(final TimerInstance owner) {
        this.owner = owner;
    }

    /**
     * resets the time conter value.
     */
    public void reset() {
        count = 0;
        startTime = System.currentTimeMillis();
        if (owner.propMode != MEASURE_TIME) {
            owner.opTime.sendData(ConversionUtils.intToBytes(0));
        }
    }

    // static int tcount=0;

    /**
     * the time generation thread.
     */
    @Override
    public void run() {
        // System.out.println ("\n\n *** TimeGenThread "+ (++tcount) + "
        // started.\n");

        try {
            startTime = System.currentTimeMillis();
            active = true;

            while (active == true) {
                currentTime = System.currentTimeMillis() - startTime;
                while ((currentTime > owner.propTimePeriod) && (active == true)) {
                    owner.etpPeriodFinished.raiseEvent();

                    switch (owner.propMode) {
                    case MODE_N_TIMES:
                        count++;
                        startTime = System.currentTimeMillis();
                        currentTime = 0;

                        if (count >= owner.propRepeatCounter) {
                            count = 0;
                            active = false;
                        }
                        break;

                    case MODE_LOOP:
                        startTime = System.currentTimeMillis();
                        currentTime = 0;

                        break;

                    case MODE_ONE_SHOT:
                        active = false;
                        break;

                    case MODE_ONCE_STAY_ACTIVE:
                        owner.opTime.sendData(ConversionUtils.intToBytes(owner.propTimePeriod));
                        Thread.sleep(owner.propResolution);
                        break;
                    }
                }
                Thread.sleep(owner.propResolution);
                if ((currentTime > owner.propWaitPeriod) && (owner.propMode != MEASURE_TIME) && (active == true)) {
                    owner.opTime.sendData(ConversionUtils.intToBytes((int) (currentTime - owner.propWaitPeriod)));
                }
            }
        } catch (InterruptedException e) {
            AstericsErrorHandling.instance.getLogger()
                    .fine("TimeGenerator thread <" + Thread.currentThread().getName() + "> got interrupted.");
            active = false;
        }
    }

    /**
     * called when model is started or resumed.
     */
    public void start() {
        // AstericsErrorHandling.instance.getLogger().fine("Invoking thread
        // <"+Thread.currentThread().getName()+">, .start called");
        if (runningTaskFuture == null || (runningTaskFuture != null && runningTaskFuture.isDone())) {
            runningTaskFuture = AstericsThreadPool.instance.execute(this);
        } else {
            reset();
        }

    }

    /**
     * called when model is stopped or paused.
     */
    public void stop() {
        // AstericsErrorHandling.instance.getLogger().fine("Invoking thread
        // <"+Thread.currentThread().getName()+">, : .stop called");

        if (runningTaskFuture != null && !runningTaskFuture.isDone()) {
            runningTaskFuture.cancel(true);
        }

        active = false;
        count = 0;
        runningTaskFuture = null;
    }

    /**
     * This method stops the runThread and sends the measured time (from start
     * to stop event) in mode MEASURE_TIME to the output port opTime. Note:
     * Don't use this method when stopping the plugin, it can lead into an
     * endless loop.
     */
    void stopAndSendData() {
        stop();
        if (owner.propMode == MEASURE_TIME) {
            owner.opTime.sendData(ConversionUtils.intToBytes((int) (currentTime - owner.propWaitPeriod)));
        }
    }

}
