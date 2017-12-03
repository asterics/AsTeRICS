
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

package eu.asterics.component.sensor.randomnumber;

import java.util.Random;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class RandomNumberInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opNumber = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propMin = 0;
    int propMax = 1;
    boolean propAutostart = true;

    // declare member variables here

    /**
     * The class constructor.
     */
    public RandomNumberInstance() {
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
        if ("number".equalsIgnoreCase(portID)) {
            return opNumber;
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
        if ("newNumber".equalsIgnoreCase(eventPortID)) {
            return elpNewNumber;
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
        if ("min".equalsIgnoreCase(propertyName)) {
            return propMin;
        }
        else if ("max".equalsIgnoreCase(propertyName)) {
            return propMax;
        }
        else if ("autostart".equalsIgnoreCase(propertyName)) {
            return propAutostart;
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
        if ("min".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMin;
            propMin = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        else if ("max".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMax;
            propMax = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        else if ("autostart".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutostart;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutostart = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutostart = false;
            }
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
    final IRuntimeEventListenerPort elpNewNumber = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            generateNumber();
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        super.start();
        if (propAutostart==true)
           generateNumber();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {

        super.stop();
    }

    private void generateNumber() {
        Random zufall = new Random();
        int x = zufall.nextInt((propMax - propMin) + 1) + propMin;
        // System.out.println("Time: " + x);
        opNumber.sendData(ConversionUtils.intToBytes(x));
    }
}