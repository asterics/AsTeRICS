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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.signaltranslation;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * SignalTranslationInstance incorporates a processor which transforms a signal
 * from within one range of values on the input to a different value range on
 * the output proportionally. The processor provides an translated output signal
 * for each input. It also incorporates two input ports which allow setting the
 * minimum and maximum of the input value range.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class SignalTranslationInstance extends AbstractRuntimeComponentInstance {
    private final String NAME_INPUT = "in";
    private final String NAME_SETMIN = "setMin";
    private final String NAME_SETMAX = "setMax";
    private final String NAME_OUTPUT = "out";

    private final String PROPERTY_KEY_IN_MIN = "inMin";
    private final String PROPERTY_KEY_IN_MAX = "inMax";
    private final String PROPERTY_KEY_OUT_MIN = "outMin";
    private final String PROPERTY_KEY_OUT_MAX = "outMax";

    // properties
    double propInMin, propInMax;
    double propOutMin, propOutMax;

    // ports
    private InputPort ipIn = new InputPort(this);
    private OutputPort opOut = new OutputPort();
    private final IRuntimeEventTriggererPort etpExitRangeBelow = new DefaultRuntimeEventTriggererPort();
    private final IRuntimeEventTriggererPort etpExitRangeAbove = new DefaultRuntimeEventTriggererPort();
    private final IRuntimeEventTriggererPort etpEnterRange = new DefaultRuntimeEventTriggererPort();
    
    private boolean inRange = false;
    private double lastValue = 0;

    /**
     * An input port implementation that will use incoming data to set the
     * minimum input value of the component
     */
    private final IRuntimeInputPort ipSetMin = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            double value = ConversionUtils.doubleFromBytes(data);
            propInMin = value;
        }

    };

    /**
     * An input port implementation that will use incoming data to set the
     * maximum input value of the component
     */
    private final IRuntimeInputPort ipSetMax = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            double value = ConversionUtils.doubleFromBytes(data);
            propInMax = value;

        }

    };

    /**
     * Constructs the component
     */
    public SignalTranslationInstance() {
        // empty constructor - needed for OSGi service factory operations
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
        if (NAME_INPUT.equalsIgnoreCase(portID)) {
            return ipIn;
        } else if (NAME_SETMIN.equalsIgnoreCase(portID)) {
            return ipSetMin;
        } else if (NAME_SETMAX.equalsIgnoreCase(portID)) {
            return ipSetMax;
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
        if (NAME_OUTPUT.equalsIgnoreCase(portID)) {
            return opOut;
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
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("enterRange".equalsIgnoreCase(eventPortID)) {
            return etpEnterRange;
        }
        if ("exitRangeBelow".equalsIgnoreCase(eventPortID)) {
            return etpExitRangeBelow;
        }
        if ("exitRangeAbove".equalsIgnoreCase(eventPortID)) {
            return etpExitRangeAbove;
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
        if (PROPERTY_KEY_IN_MIN.equalsIgnoreCase(propertyName)) {
            return propInMin;
        } else if (PROPERTY_KEY_IN_MAX.equalsIgnoreCase(propertyName)) {
            return propInMax;
        } else if (PROPERTY_KEY_OUT_MIN.equalsIgnoreCase(propertyName)) {
            return propOutMin;
        } else if (PROPERTY_KEY_OUT_MAX.equalsIgnoreCase(propertyName)) {
            return propOutMax;
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
     * @return null
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        try {
            if (PROPERTY_KEY_IN_MIN.equalsIgnoreCase(propertyName)) {
                propInMin = Double.parseDouble(newValue.toString());
                processInput(lastValue);
                AstericsErrorHandling.instance.reportInfo(this, String.format("Setting in_min to %f", propInMin));
            } else if (PROPERTY_KEY_IN_MAX.equalsIgnoreCase(propertyName)) {
                propInMax = Double.parseDouble(newValue.toString());
                processInput(lastValue);
                AstericsErrorHandling.instance.reportInfo(this, String.format("Setting in_max to %f", propInMax));
            } else if (PROPERTY_KEY_OUT_MIN.equalsIgnoreCase(propertyName)) {
                propOutMin = Double.parseDouble(newValue.toString());
                AstericsErrorHandling.instance.reportInfo(this, String.format("Setting out_min to %f", propOutMin));
            } else if (PROPERTY_KEY_OUT_MAX.equalsIgnoreCase(propertyName)) {
                propOutMax = Double.parseDouble(newValue.toString());
                AstericsErrorHandling.instance.reportInfo(this, String.format("Setting out_max to %f", propOutMax));
            }
        } catch (NumberFormatException nfe) {
            AstericsErrorHandling.instance.reportInfo(this,
                    "Invalid property value for " + propertyName + ": " + newValue);
        }
        return null;
    }

    /**
     * Starts the model
     */
    @Override
    public void start() {
        super.start();
        AstericsErrorHandling.instance.reportInfo(this, "SignalTranslationInstance started");
    }

    /**
     * Stops the model
     */
    @Override
    public void stop() {
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "SignalTranslationInstance stopped");
    }

    /**
     * Processes the input value and calculates the output
     * 
     * @param value
     */
    public synchronized void processInput(double originalValue) {
        double value = originalValue;
        if (value > propInMax) {
            value = propInMax;
            if(lastValue < propInMax) {
                inRange = false;
                etpExitRangeAbove.raiseEvent();
            }
        } else if (value < propInMin) {
            value = propInMin;
            if(lastValue > propInMin) {
                inRange = false;
                etpExitRangeBelow.raiseEvent();
            }
        } else if(!inRange) {
            inRange = true;
            etpEnterRange.raiseEvent();
        }

        lastValue = originalValue;
        double out = (value - propInMin) / (propInMax - propInMin) * (propOutMax - propOutMin) + propOutMin;
        opOut.sendData(out);
    }

    /**
     * An input port implementation which will initiate the translation of an
     * input value to an output value in another value range
     * 
     * @author weissch
     *
     */
    class InputPort extends DefaultRuntimeInputPort {
        SignalTranslationInstance owner;

        /**
         * Constructs the port
         * 
         * @param owner
         */
        public InputPort(SignalTranslationInstance owner) {
            this.owner = owner;
        }

        /**
         * Receives input byte array and initiates calculations
         * 
         * @param data
         *            the input data
         */
        @Override
        public void receiveData(byte[] data) {
            double value = ConversionUtils.doubleFromBytes(data);
            owner.processInput(value);
        }

    }

    /**
     * An output port implementation which will transfer doubles to the next
     * component
     * 
     * @author weissch
     *
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        /**
         * Sends data to the following component
         * 
         * @param data
         *            output data as a double
         */
        public void sendData(double data) {
            super.sendData(ConversionUtils.doubleToBytes(data));
        }
    }

}