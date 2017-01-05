/**
 * 
 */
package eu.asterics.component.sensor.timer;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

/**
 * Unit tests for class TimeGenerator.
 * 
 * @author mad
 *
 */
public class TimeGeneratorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link eu.asterics.component.sensor.timer.TimeGenerator#reset()}.
     */
    @Test
    public void testReset() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link eu.asterics.component.sensor.timer.TimeGenerator#start()}.
     */
    @Test
    public void testStart() {
        // fail("Not yet implemented");
    }

    @Test
    public void testStartNTimes() {
        executeStartOneShotOrNTimes(null, TimeGenerator.MODE_N_TIMES, 3);
    }

    @Test
    public void testStartNTimesStartNTimes() {
        TimeGenerator tg = executeStartOneShotOrNTimes(null, TimeGenerator.MODE_N_TIMES, 3);
        executeStartOneShotOrNTimes(tg, TimeGenerator.MODE_N_TIMES, 6);
    }

    @Test
    public void testStartOneShot() {
        executeStartOneShotOrNTimes(null, TimeGenerator.MODE_ONE_SHOT, 1);
    }

    @Test
    public void testStartOneShotStartOneShot() {
        TimeGenerator tg = executeStartOneShotOrNTimes(null, TimeGenerator.MODE_ONE_SHOT, 1);
        executeStartOneShotOrNTimes(tg, TimeGenerator.MODE_ONE_SHOT, 2);
    }

    /**
     * This method initializes and start the TimeGenerator instance with the
     * given mode and then checks if the given expectedEventCount is met.
     * 
     * @param tgOther
     *            : if null: a new TimeGenerator instance is created, otherwise
     *            the given one is reused.
     * @param mode
     *            : {@link TimeGenerator#MODE_ONE_SHOT} or
     *            {@link TimeGenerator#MODE_N_TIMES}
     * @param expectedEventCount
     *            : The expected eventCount. If the actual event count != the
     *            given expectedEventCount, the test fails
     * @return
     */
    private TimeGenerator executeStartOneShotOrNTimes(TimeGenerator tgOther, int mode, int expectedEventCount) {
        int propTimePeriod = 1000;

        TimeGenerator tg = tgOther;
        if (tg == null) {
            int propRepeatCounter = 0;
            if (mode == TimeGenerator.MODE_N_TIMES) {
                propRepeatCounter = expectedEventCount;
            }
            tg = getTimeGenerator(mode, propTimePeriod, 50, 0, propRepeatCounter, false, null,
                    new CounterDefaultRuntimeEventTriggererPort());
        }

        tg.start();
        try {
            // Wait double time of timePeriod to ensure the thread had enough
            // time to execute its task
            int waitTime = propTimePeriod * expectedEventCount + propTimePeriod;
            // System.out.println("Waiting "+waitTime);
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            // Should not happen actually, nevertheless it's not totally wrong.
            // Actually the result counts which is checked below.
            e.printStackTrace();
        }
        CounterDefaultRuntimeEventTriggererPort etpPeriodFinished = (CounterDefaultRuntimeEventTriggererPort) tg.owner.etpPeriodFinished;
        int actualEventCount = etpPeriodFinished.getEventCounter();
        if (actualEventCount != expectedEventCount) {
            fail("Mode: " + mode + ", eventCounter(" + actualEventCount + ") != " + expectedEventCount);
        }
        return tg;
    }

    /**
     * Test method for
     * {@link eu.asterics.component.sensor.timer.TimeGenerator#stop()}.
     */
    @Test
    public void testStop() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link eu.asterics.component.sensor.timer.TimeGenerator#stopAndSendData()}
     * .
     */
    @Test
    public void testStopAndSendData() {
        // fail("Not yet implemented");
    }

    /**
     * Method to create a TimeGenrator instance
     * 
     * @param mode
     * @param propTimePeriod
     * @param propResolution
     * @param propWaitPeriod
     * @param propRepeatCounter
     * @param propAutostart
     * @param opTime
     * @param etpPeriodFinished
     * @return
     */
    private TimeGenerator getTimeGenerator(int mode, int propTimePeriod, int propResolution, int propWaitPeriod,
            int propRepeatCounter, boolean propAutostart, IRuntimeOutputPort opTime,
            IRuntimeEventTriggererPort etpPeriodFinished) {
        TimerInstance ti = new TimerInstance();
        ti.propMode = mode;
        ti.propTimePeriod = propTimePeriod;
        ti.propResolution = propResolution;
        ti.propWaitPeriod = propWaitPeriod;
        ti.propRepeatCounter = propRepeatCounter;
        ti.propAutostart = propAutostart;
        if (opTime != null) {
            ti.opTime = opTime;
        }
        if (etpPeriodFinished != null) {
            ti.etpPeriodFinished = etpPeriodFinished;
        }
        return new TimeGenerator(ti);
    }

}

/**
 * Event Triggerer implementation that counts incoming events.
 * 
 * @author mad
 *
 */
class CounterDefaultRuntimeEventTriggererPort implements IRuntimeEventTriggererPort {
    private volatile int eventCounter = 0;
    private volatile long lastTimeStamp = 0;

    @Override
    public void setEventChannelID(String eventChannelID) {
    }

    @Override
    public void raiseEvent() {
        synchronized (this) {
            eventCounter++;
            lastTimeStamp = System.currentTimeMillis();
            System.out.println(
                    this + ".raiseEvent - eventCounter: " + eventCounter + ", lastTimeStamp: " + lastTimeStamp);
        }
    }

    @Override
    public void addEventListener(String targetComponentID, String eventPortID,
            IRuntimeEventListenerPort eventListenerPort) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeEventListener(String targetComponentID, String eventPortID) {
        // TODO Auto-generated method stub

    }

    public synchronized void reset() {
        eventCounter = 0;
        lastTimeStamp = 0;
    }

    /**
     * @return the eventCounter
     */
    public synchronized int getEventCounter() {
        System.out.println(this + ".getEventCounter: " + eventCounter);
        return eventCounter;
    }

    /**
     * @return the lastTimeStamp
     */
    public synchronized long getLastTimeStamp() {
        return lastTimeStamp;
    }

}
