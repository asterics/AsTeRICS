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

package eu.asterics.component.processor.threshold;

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
 * ThresholdInstance provides a method to set a threshold and manipulate the
 * output data based on the input data being above or below the threshold.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Aug 20,
 *         2010 Time: 10:22:08 AM
 */
public class ThresholdInstance extends AbstractRuntimeComponentInstance {
    final private String OUT_VALUE_PORT_KEY = "out";
    final private String OUT_BOOL_PORT_KEY = "out_bool";
    final private String IN_PORT_KEY = "in";
    final private String EVENT_TRIGGER_POSEDGE_PORT_KEY = "eventPosEdge";
    final private String EVENT_TRIGGER_NEGEDGE_PORT_KEY = "eventNegEdge";

    final private String OUT_HIGH_PROPERTY_KEY = "outputHigh";
    final private String OUT_LOW_PROPERTY_KEY = "outputLow";
    final private String TRESHOLD_HIGH_VALUE_PROPERTY_KEY = "thresholdHigh";
    final private String TRESHOLD_LOW_VALUE_PROPERTY_KEY = "thresholdLow";

    final private String OPERATION_MODE_PROPERTY_KEY = "operationMode";
    final private int OPERATION_MODE_BINARY = 0;
    final private int OPERATION_MODE_CUTOFF = 1;
    final private int OPERATION_MODE_DEADZONE = 2;

    final private String EVENT_CONDITION_PROPERTY_KEY = "eventCondition";
    final private int EVENT_CONDITION_POS_EDGE = 0;
    final private int EVENT_CONDITION_NEG_EDGE = 1;
    final private int EVENT_CONDITION_BOTH_EDGE = 2;

    private IRuntimeInputPort ipInput = new ThresholdInputPort(this);
    private IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opOutputBool = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort etpEventPosEdge = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventNegEdge = new DefaultRuntimeEventTriggererPort();

    // property variables
    double propThresholdHigh;
    double propThresholdLow;
    double propOutputHigh;
    double propOutputLow;
    int propOperationMode = 0;
    int propEventCondition = 0;

    // internal variables
    boolean belowThreshold = false;
    boolean initialized = false;

    /**
     * Constructs the component
     */
    public ThresholdInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * Starts the component
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Pauses the component
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * Resumes the component
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * Stops the component
     */
    @Override
    public void stop() {
        initialized = false;
        super.stop();
    }

    /**
     * Returns the input ports of the component
     * 
     * @param portID
     *            the ID of the requested port
     * @return the requested port instance
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if (IN_PORT_KEY.equalsIgnoreCase(portID)) {
            return ipInput;
        } else {
            return null;
        }
    }

    /**
     * Returns the output ports of the component
     * 
     * @param portID
     *            the ID of the requested port
     * @return the requested port instance
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if (OUT_VALUE_PORT_KEY.equalsIgnoreCase(portID)) {
            return opOutput;
        } else if (OUT_BOOL_PORT_KEY.equalsIgnoreCase(portID)) {
            return opOutputBool;
        } else {
            return null;
        }
    }

    /**
     * Returns the event triggerer ports of the component
     * 
     * @param portID
     *            the ID of the requested port
     * @return the requested port instance
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if (EVENT_TRIGGER_POSEDGE_PORT_KEY.equalsIgnoreCase(eventPortID)) {
            return etpEventPosEdge;
        } else if (EVENT_TRIGGER_NEGEDGE_PORT_KEY.equalsIgnoreCase(eventPortID)) {
            return etpEventNegEdge;
        }
        return null;
    }

    /**
     * Returns the value of the specified property
     * 
     * @param propertyName
     *            the requested property's name
     * @return the value of the property as an Object
     * 
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if (TRESHOLD_HIGH_VALUE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propThresholdHigh;
        } else if (TRESHOLD_LOW_VALUE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propThresholdLow;
        } else if (OUT_HIGH_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propOutputHigh;
        } else if (OUT_LOW_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propOutputLow;
        } else if (OPERATION_MODE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
        	return propOperationMode;
        } else if (EVENT_CONDITION_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propEventCondition;
        } else {
            return null;
        }
    }

    /**
     * Sets the value of the specified property
     * 
     * @param propertyName
     *            the requested property's name
     * @param newValue
     *            the new value for the property
     * @return the old value of the property as an Object, null on errors
     * 
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        try {
            if (TRESHOLD_HIGH_VALUE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propThresholdHigh;
                propThresholdHigh = Double.parseDouble(newValue.toString());

                return oldValue;
            } else if (TRESHOLD_LOW_VALUE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propThresholdLow;
                propThresholdLow = Double.parseDouble(newValue.toString());

                return oldValue;
            } else if (OUT_HIGH_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propOutputHigh;
                propOutputHigh = Double.parseDouble(newValue.toString());

                return oldValue;
            } else if (OUT_LOW_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propOutputLow;
                propOutputLow = Double.parseDouble(newValue.toString());

                return oldValue;
            }

            else if (OPERATION_MODE_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propOperationMode;

                propOperationMode = Integer.parseInt(newValue.toString());
                if ((propOperationMode < OPERATION_MODE_BINARY)
                        || (propOperationMode > OPERATION_MODE_DEADZONE)) {
                    AstericsErrorHandling.instance.reportInfo(this,
                            "Property value out of range for " + propertyName + ": " + newValue);
                }
                return oldValue;
            } else if (EVENT_CONDITION_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propEventCondition;

                propEventCondition = Integer.parseInt(newValue.toString());
                if ((propEventCondition < EVENT_CONDITION_POS_EDGE)
                        || (propEventCondition > EVENT_CONDITION_BOTH_EDGE)) {
                    AstericsErrorHandling.instance.reportInfo(this,
                            "Property value out of range for " + propertyName + ": " + newValue);
                }
                return oldValue;
            }
        } catch (NumberFormatException nfe) {
            AstericsErrorHandling.instance.reportInfo(this,
                    "Invalid property value for " + propertyName + ": " + newValue);
        }
        return null;
    }

    /**
     * Processes the input for binary mode, output is either the fixed high
     * value or the fixed low input
     * 
     * @param in
     *            the input to be clipped
     * @return true if value is in area below threshold hysteresis, false
     *         otherwise
     */
    boolean processInputBinary(double in) {
        if (belowThreshold) {
            if (in > propThresholdHigh) {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputHigh));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
                return false;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputLow));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
            }
            return true;
        } else {
            if (in < propThresholdLow) {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputLow));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
                return true;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputHigh));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
            }
            return false;
        }
    }

    /**
     * Processes the input for cut off mode, output is either the fixed high
     * value or the input value if below the threshold
     * 
     * @param in
     *            the input to be clipped
     * @return true if value is in area below threshold hysteresis, false
     *         otherwise
     */
    boolean processInputCutoff(double in) {
        if (belowThreshold) {
            if (in > propThresholdHigh) {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputHigh));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
                return false;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(in));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
            }
            return true;
        } else {
            if (in < propThresholdLow) {
                opOutput.sendData(ConversionUtils.doubleToBytes(in));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
                return true;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputHigh));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
            }
            return false;
        }
    }

    /**
     * Processes the input for dead zone mode, output is either the fixed low
     * value or the input value if above the threshold
     * 
     * @param in
     *            the input to be clipped
     * @return true if value is in area below threshold hysteresis, false
     *         otherwise
     */
    boolean processInputDeadzone(double in) {
        if (belowThreshold) {
            if (in > propThresholdHigh) {
                opOutput.sendData(ConversionUtils.doubleToBytes(in));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
                return false;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputLow));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
            }
            return true;
        } else {
            if (in < propThresholdLow) {
                opOutput.sendData(ConversionUtils.doubleToBytes(propOutputLow));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(false));
                return true;
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(in));
                opOutputBool.sendData(ConversionUtils.booleanToBytes(true));
            }
            return false;
        }
    }

    /**
     * Checks for event conditions and raises events if necessary
     * 
     * @param in
     *            the input to be checked
     */
    void checkAndHandleEventCondition(double in) {
        if (initialized) {
            switch (propEventCondition) {
            case EVENT_CONDITION_POS_EDGE:
                if (belowThreshold && (in > propThresholdHigh)) {
                    etpEventPosEdge.raiseEvent();
                }
                break;
            case EVENT_CONDITION_NEG_EDGE:
                if (!belowThreshold && (in < propThresholdLow)) {
                    etpEventNegEdge.raiseEvent();
                }
                break;
            case EVENT_CONDITION_BOTH_EDGE:
                if (belowThreshold && (in > propThresholdHigh)) {
                    etpEventPosEdge.raiseEvent();
                }
                if (!belowThreshold && (in < propThresholdLow)) {
                    etpEventNegEdge.raiseEvent();
                }
                break;
            default:
                AstericsErrorHandling.instance.reportDebugInfo(this,
                		"Threshold component operated with non-existant event condition");
            }
        }
    }

    /**
     * Processes the input for all modes and chooses the clipping mode
     * accordingly
     * 
     * @param in
     *            the input to be clipped
     */
    void processInput(double in) {
        checkAndHandleEventCondition(in);

        switch (propOperationMode) {
        case OPERATION_MODE_BINARY:
            belowThreshold = processInputBinary(in);
            break;
        case OPERATION_MODE_CUTOFF:
            belowThreshold = processInputCutoff(in);
            break;
        case OPERATION_MODE_DEADZONE:
            belowThreshold = processInputDeadzone(in);
            break;
        default:
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "Threshold component operated in non-existant operation mode");
        }

        initialized = true;
    }

    /**
     * Input port implementation which processes input data for clipping
     * 
     * @author weissch
     *
     */
    private class ThresholdInputPort extends DefaultRuntimeInputPort {
        ThresholdInstance owner;

        public ThresholdInputPort(ThresholdInstance owner) {
            this.owner = owner;
        }

        @Override
        public void receiveData(byte[] data) {
            // convert input to int
            double in = ConversionUtils.doubleFromBytes(data);

            if (initialized == false) {
                if (in < propThresholdLow) {
                    belowThreshold = true;
                } else {
                    belowThreshold = false;
                }
            }
            owner.processInput(in);
        }

    }
}