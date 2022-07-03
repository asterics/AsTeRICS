
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

package eu.asterics.component.processor.delay;

import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class DelayInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();

    int propDelay = 1000;

    private class DelayItem {
        long time;
        double value;

        DelayItem(long time, double value) {
            this.time = time;
            this.value = value;
        }
    }

    // declare member variables here
    private Vector<DelayItem> eventTime = new Vector<DelayItem>();
    private Lock lock = new ReentrantLock();
    private DelayTimer timer = new DelayTimer(this);
    private boolean stateActive = false;

    /**
     * The class constructor.
     */
    public DelayInstance() {
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
        if ("in".equalsIgnoreCase(portID)) {
            return ipIn;
        }

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
        if ("out".equalsIgnoreCase(portID)) {
            return opOut;
        }

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
    private final IRuntimeInputPort ipIn = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            double value = ConversionUtils.doubleFromBytes(data);
            try {
                lock.lock();
                if (stateActive) {
                    long time = System.currentTimeMillis();

                    DelayItem actItem = new DelayItem(time, value);

                    eventTime.add(actItem);
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
                opOut.sendData(ConversionUtils.doubleToBytes(eventTime.firstElement().value));
                eventTime.remove(0);

                boolean finish = true;

                do {
                    finish = true;
                    if (eventTime.size() > 0) {
                        long currentTime = System.currentTimeMillis();
                        long storedEventTime = eventTime.firstElement().time;
                        if (storedEventTime + propDelay > currentTime) {
                            // timer.setDelayTime(storedEventTime+propDelay-currentTime);
                            nextDelay = storedEventTime + propDelay - currentTime;
                        } else {
                            opOut.sendData(ConversionUtils.doubleToBytes(eventTime.firstElement().value));
                            eventTime.remove(0);
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