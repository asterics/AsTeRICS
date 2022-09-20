
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

package eu.asterics.component.processor.integrate;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the integrate plugin, which adds the current input value to and
 * accumulator and puts the result to the output port
 * 
 * @author Chris Veigl [veigl@tecnikum-wien.at] Date: Feb 15, 2011 Time:
 *         01:30:00 PM
 */
public class IntegrateInstance extends AbstractRuntimeComponentInstance {

    private IRuntimeInputPort ipIn = new InputPort1();
    private IRuntimeOutputPort opOut = new OutputPort1();

    private final String ELP_RESET_NAME = "reset";
    private double propResetValue = 0;
    private double propUpperLimit = 50000;
    private double propLowerLimit = -50000;
    private boolean propWrapAround = false;

    private double accu = 0;

    /**
     * The class constructor.
     */
    public IntegrateInstance() {
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
        if ("in".equalsIgnoreCase(portID)) {
            return ipIn;
        } else {
            return null;
        }
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
     * @param portID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (ELP_RESET_NAME.equalsIgnoreCase(eventPortID)) {
            return elpReset;
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
        if ("resetValue".equalsIgnoreCase(propertyName)) {
            return propResetValue;
        } else if ("upperLimit".equalsIgnoreCase(propertyName)) {
            return propUpperLimit;
        } else if ("lower_limit".equalsIgnoreCase(propertyName)) {
            return propLowerLimit;
        } else if ("wrapAround".equalsIgnoreCase(propertyName)) {
            return propWrapAround;
        } else {
            return null;
        }
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
        if ("resetValue".equalsIgnoreCase(propertyName)) {
            final Double oldValue = propResetValue;
            propResetValue = Double.parseDouble((String) newValue);
            AstericsErrorHandling.instance.reportDebugInfo(this, "reset_value set to " + propResetValue);
            return oldValue;
        } else if ("upperLimit".equalsIgnoreCase(propertyName)) {
            final Double oldValue = propUpperLimit;
            propUpperLimit = Double.parseDouble((String) newValue);
            AstericsErrorHandling.instance.reportDebugInfo(this, "upper_limit set to " + propUpperLimit);
            return oldValue;
        } else if ("lowerLimit".equalsIgnoreCase(propertyName)) {
            final Double oldValue = propLowerLimit;
            propLowerLimit = Double.parseDouble((String) newValue);
            AstericsErrorHandling.instance.reportDebugInfo(this, "lower_limit set to " + propLowerLimit);
            return oldValue;
        } else if ("wrapAround".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWrapAround;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propWrapAround = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propWrapAround = false;
            }
            AstericsErrorHandling.instance.reportDebugInfo(this, "wrap_around set to " + propWrapAround);
            return oldValue;
        }
        return null;
    }

    /**
     * Input Port for receiving values.
     */
    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            // convert input to double
            double in = ConversionUtils.doubleFromBytes(data);

            // compute integration
            accu += in;
            if (propLowerLimit != propUpperLimit) {
                if (accu > propUpperLimit) {
                    if (propWrapAround == true) {
                        accu = propLowerLimit;// + (accu-upper_limit);
                    } else {
                        accu = propUpperLimit;
                    }
                } else {
                    if (accu < propLowerLimit) {
                        if (propWrapAround == true) {
                            accu = propUpperLimit;// - (lower_limit-accu);
                        } else {
                            accu = propLowerLimit;
                        }
                    }
                }
            }

            // send output
            opOut.sendData(ConversionUtils.doubleToBytes(accu));
        }

    }

    /**
     * Output Port for sending values.
     */
    private class OutputPort1 extends DefaultRuntimeOutputPort {
        // empty
    }

    /**
     * Event Listener Port for reset integration value.
     */
    final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            accu = propResetValue;
            opOut.sendData(ConversionUtils.doubleToBytes(accu));

        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        accu = propResetValue;
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