
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

package eu.asterics.component.processor.stringexpander;

import java.util.HashMap;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * This plugin adds the preString and postString strings to the incoming string.
 * The new string is sent through the output port.
 * 
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Jul 07, 2012 Time: 10:12:27
 *         AM
 */
public class StringExpanderInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    String propPreString = "";
    String propPostString = "";
    boolean propTrim = false;

    // declare member variables here

    /**
     * The class constructor.
     */
    public StringExpanderInstance() {
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
        if ("input".equalsIgnoreCase(portID)) {
            return ipInput;
        }
        if ("preString".equalsIgnoreCase(portID)) {
            return ipPreString;
        }
        if ("postString".equalsIgnoreCase(portID)) {
            return ipPostString;
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
        if ("output".equalsIgnoreCase(portID)) {
            return opOutput;
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
        if ("preString".equalsIgnoreCase(propertyName)) {
            return propPreString;
        }
        if ("postString".equalsIgnoreCase(propertyName)) {
            return propPostString;
        }
        if ("trim".equalsIgnoreCase(propertyName)) {
            return propTrim;
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
        if ("preString".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPreString;
            propPreString = (String) newValue;
            return oldValue;
        }
        if ("postString".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPostString;
            propPostString = (String) newValue;
            return oldValue;
        }
        if ("trim".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propTrim;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propTrim = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propTrim = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipInput = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {

            String inputString = ConversionUtils.stringFromBytes(data);
            if (propTrim) {
                inputString = inputString.trim();
            }
            String newString = propPreString + inputString + propPostString;
            opOutput.sendData(ConversionUtils.stringToBytes(newString));

        }
    };

    private final IRuntimeInputPort ipPreString = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {

            String tmpString = ConversionUtils.stringFromBytes(data);
            if (propTrim) {
                tmpString = tmpString.trim();
            }
            propPreString = tmpString;
            // opOutput.sendData(ConversionUtils.stringToBytes(tmpString));
        }
    };

    private final IRuntimeInputPort ipPostString = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {

            String tmpString = ConversionUtils.stringFromBytes(data);
            if (propTrim) {
                tmpString = tmpString.trim();
            }
            propPostString = tmpString;
            // opOutput.sendData(ConversionUtils.stringToBytes(tmpString));
        }
    };

    @Override
    public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {

        String inputString = null;

        for (String s : dataRow.keySet()) {

            byte[] data = dataRow.get(s);
            if (s.equals("input")) {
                inputString = ConversionUtils.stringFromBytes(data);
                if (propTrim) {
                    inputString = inputString.trim();
                }
            }
            if (s.equals("preString")) {
                propPreString = ConversionUtils.stringFromBytes(data);
                if (propTrim) {
                    propPreString = propPreString.trim();
                }
            }
            if (s.equals("postString")) {
                propPostString = ConversionUtils.stringFromBytes(data);
                if (propTrim) {
                    propPostString = propPostString.trim();
                }
            }
        }

        opOutput.sendData(ConversionUtils.stringToBytes(propPreString + inputString + propPostString));
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        super.start();
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
}