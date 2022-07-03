
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

package eu.asterics.component.processor.eventdelay;

import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;

/**
 * 
 * This component collects events and sends them to the output port after
 * defined delay.
 * 
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Apr 03, 2012 Time: 12:10:20
 *         AM
 */
public class EventDelayInstance extends AbstractRuntimeComponentInstance {
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpOutput = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propDelay = 100;

    // declare member variables here
    private Vector<Long> eventTime = new Vector<Long>();
    private Lock lock = new ReentrantLock();
    private DelayTimer timer = new DelayTimer(this);
    private boolean stateActive = false;

    /**
     * The class constructor.
     */
    public EventDelayInstance() {
        // empty constructor
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
        if ("input".equalsIgnoreCase(eventPortID)) {
            return elpInput;
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
        if ("output".equalsIgnoreCase(eventPortID)) {
            return etpOutput;
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
        if ("delay".equalsIgnoreCase(propertyName)) {
            return propDelay;
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
        if ("delay".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDelay;
            propDelay = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpInput = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                lock.lock();
                if (stateActive) {
                    long time = System.currentTimeMillis();
                    eventTime.add(time);
                    if (timer.timerState() == false) {
                        timer.setDelayTime(propDelay);
                        timer.startDelay();
                    }
                }

            } finally {
                lock.unlock();

            }
        }
    };

    /**
     * Removes events from the collection.
     */
    void removeEvents() {
        try {
            lock.lock();
            eventTime.clear();

        } finally {
            lock.unlock();

        }
    }

    /**
     * Sends the event and sets the delay for the next event.
     * 
     * @return delay for the next event
     */
    long sendEvent() {
        long nextDelay = -1;
        try {
            lock.lock();
            if ((eventTime.size() > 0) && (stateActive == true)) {
                eventTime.remove(0);
                etpOutput.raiseEvent();

                boolean finish = true;

                do {
                    finish = true;
                    if (eventTime.size() > 0) {
                        long currentTime = System.currentTimeMillis();
                        long storedEventTime = eventTime.firstElement();
                        if (storedEventTime + propDelay > currentTime) {
                            // timer.setDelayTime(storedEventTime+propDelay-currentTime);
                            nextDelay = storedEventTime + propDelay - currentTime;
                        } else {
                            eventTime.remove(0);
                            etpOutput.raiseEvent();
                            finish = false;
                        }
                    }
                } while (finish == false);
            }

        } finally {
            lock.unlock();

        }

        return nextDelay;
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        super.start();

        stateActive = true;
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        stateActive = false;
        removeEvents();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        stateActive = true;
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {

        super.stop();
        stateActive = false;
        removeEvents();
    }
}