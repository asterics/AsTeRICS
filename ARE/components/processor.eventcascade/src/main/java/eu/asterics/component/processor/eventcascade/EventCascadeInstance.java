
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.eventcascade;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * EventCascadeInstance allows to specify a sequence of events with arbitrary
 * delay times and loops
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at]
 * 
 */
public class EventCascadeInstance extends AbstractRuntimeComponentInstance implements Runnable {
    public final int NUMBER_OF_TRIGGERS = 15;
    private final String ETP_TRIGGER_PREFIX = "trigger";
    private final String DELAY_PROPERTY_PREFIX = "delayBeforeTrigger";

    // final IRuntimeEventTriggererPort etpTrigger1 = new
    // DefaultRuntimeEventTriggererPort();
    public final IRuntimeEventTriggererPort[] etpTrigger = new DefaultRuntimeEventTriggererPort[NUMBER_OF_TRIGGERS];
    int propActiveTriggers = 4;
    int propLoops = 0;
    boolean propAutoStart = false;
    public int[] propDelayBeforeTrigger = new int[NUMBER_OF_TRIGGERS];

    int currentTrigger = 0;
    int actLoop = 0;
    boolean threadActive = false;
    boolean threadRunning = false;
    boolean rememberThreadState = false;
    boolean firstTrigger = true;

    EventCascadeInstance me;

    /**
     * The class constructor.
     */
    public EventCascadeInstance() {
        me = this;
        for (int i = 0; i < NUMBER_OF_TRIGGERS; i++) {
            etpTrigger[i] = new DefaultRuntimeEventTriggererPort();
            ;
            propDelayBeforeTrigger[i] = 500;
        }
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {

        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {

        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("nextEvent".equalsIgnoreCase(eventPortID)) {
            return elpNextEvent;
        }
        if ("previousEvent".equalsIgnoreCase(eventPortID)) {
            return elpPreviousEvent;
        }
        if ("startCascade".equalsIgnoreCase(eventPortID)) {
            return elpStartCascade;
        }
        if ("stopCascade".equalsIgnoreCase(eventPortID)) {
            return elpStopCascade;
        }
        if ("reset".equalsIgnoreCase(eventPortID)) {
            return elpReset;
        }

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        String s;
        for (int i = 0; i < NUMBER_OF_TRIGGERS; i++) {
            s = ETP_TRIGGER_PREFIX + (i + 1);
            if (s.equalsIgnoreCase(eventPortID)) {
                return etpTrigger[i];
            }
        }
        return null;
    }

    /**
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        String s;
        if ("activeTriggers".equalsIgnoreCase(propertyName)) {
            return propActiveTriggers;
        }
        if ("loops".equalsIgnoreCase(propertyName)) {
            return propLoops;
        }
        if ("autoStart".equalsIgnoreCase(propertyName)) {
            return propAutoStart;
        }

        for (int i = 0; i < NUMBER_OF_TRIGGERS; i++) {
            s = DELAY_PROPERTY_PREFIX + (i + 1);
            if (s.equalsIgnoreCase(propertyName)) {
                return propDelayBeforeTrigger[i];
            }
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        String s;
        if ("activeTriggers".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propActiveTriggers;
            propActiveTriggers = Integer.parseInt(newValue.toString());
            if (propActiveTriggers < 2) {
                propActiveTriggers = 2;
            }
            return oldValue;
        }
        if ("loops".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propLoops;
            propLoops = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("autoStart".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoStart;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoStart = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoStart = false;
            }
            return oldValue;
        }

        for (int i = 0; i < NUMBER_OF_TRIGGERS; i++) {
            s = DELAY_PROPERTY_PREFIX + (i + 1);
            if (s.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propDelayBeforeTrigger[i];
                propDelayBeforeTrigger[i] = Integer.parseInt(newValue.toString());
                if (propDelayBeforeTrigger[i] < 0) {
                    propDelayBeforeTrigger[i] = 0;
                }
                return oldValue;
            }
        }
        return null;
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpNextEvent = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (firstTrigger == false) {
                currentTrigger++;
            }
            if (currentTrigger >= propActiveTriggers) {
                currentTrigger -= propActiveTriggers;
            }
            etpTrigger[currentTrigger].raiseEvent();
            firstTrigger = false;
        }
    };
    final IRuntimeEventListenerPort elpPreviousEvent = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            currentTrigger--;
            if (currentTrigger < 0) {
                currentTrigger += propActiveTriggers;
            }
            etpTrigger[currentTrigger].raiseEvent();
            firstTrigger = false;
        }
    };
    final IRuntimeEventListenerPort elpStartCascade = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (threadRunning == false) {
                threadRunning = true;
                AstericsThreadPool.instance.execute(me);
            }
            threadActive = true;
        }
    };
    final IRuntimeEventListenerPort elpStopCascade = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            threadActive = false;
            firstTrigger = true;
        }
    };
    final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            currentTrigger = 0;
            if (threadActive == false) {
                firstTrigger = true;
            }
            actLoop = 0;
        }
    };

    /**
     * Starts the component and starts thread for periodic trigger updates
     */
    @Override
    public void start() {
        currentTrigger = 0;
        firstTrigger = true;
        actLoop = 0;

        if (threadRunning == false) {
            threadRunning = true;
            AstericsThreadPool.instance.execute(this);
        }

        if (propAutoStart == true) {
            threadActive = true;
        }
        super.start();
        // AstericsErrorHandling.instance.reportInfo(this, "EventCascadeInstance
        // started");
    }

    /**
     * Stops the component and halts the thread
     */
    @Override
    public void stop() {
        super.stop();
        threadActive = false;
        threadRunning = false;
        // AstericsErrorHandling.instance.reportInfo(this, "EventCascadeInstance
        // stopped");
    }

    /**
     * Pauses the component and halts the thread
     */
    @Override
    public void pause() {
        super.pause();
        rememberThreadState = threadActive;
        threadActive = false;
        // AstericsErrorHandling.instance.reportInfo(this, "EventCascadeInstance
        // paused");
    }

    /**
     * Resumes the component and starts thread for periodic trigger updates.
     */
    @Override
    public void resume() {
        if (rememberThreadState == true) {
            threadActive = true;
        }
        super.resume();
        // AstericsErrorHandling.instance.reportInfo(this, "EventCascadeInstance
        // resumed");
    }

    /**
     * Runs a loop which sends periodically sends triggers
     */
    @Override
    public void run() {
        while (threadRunning) {
            while ((threadActive == false) && (threadRunning == true)) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }

            }

            if (threadRunning == true) {
                if (firstTrigger == false) {
                    currentTrigger++;
                }
                firstTrigger = false;
                if (currentTrigger >= propActiveTriggers) {
                    currentTrigger -= propActiveTriggers;
                    if (propLoops > 0) {
                        actLoop++;
                        if (actLoop >= propLoops) {
                            threadActive = false;
                            firstTrigger = true;
                        }
                    }
                }

                long starttime = System.currentTimeMillis();
                long endtime = starttime + propDelayBeforeTrigger[currentTrigger];
                try {

                    // if (propDelayBeforeTrigger[currentTrigger]>0)
                    // Thread.sleep(propDelayBeforeTrigger[currentTrigger]);
                    while ((System.currentTimeMillis() < endtime) && (threadRunning == true)
                            && (threadActive == true)) {
                        Thread.sleep(2);
                    }

                } catch (InterruptedException e) {
                }

                if ((threadRunning == true) && (threadActive == true)) {
                    etpTrigger[currentTrigger].raiseEvent();
                }
            }
        }
    }

}