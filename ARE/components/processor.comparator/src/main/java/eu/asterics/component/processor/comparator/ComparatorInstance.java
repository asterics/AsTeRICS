
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

package eu.asterics.component.processor.comparator;

import java.util.HashMap;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * Implements the comparator plugin, compares two input signals and generates
 * events on different conditions like "greater", "equal", "less",
 * "difference greater than", etc. The output of a signal can be set to max,
 * min, or to "signalA if condition is true".
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 5, 2011 Time:
 *         11:50:00 AM
 */
public class ComparatorInstance extends AbstractRuntimeComponentInstance {

    final private int MODE_MAX = 0;
    final private int MODE_MIN = 1;
    final private int MODE_OUTPUT_A_IF_TRUE = 2;

    final private int CONDITION_A_GREATER_B = 0;
    final private int CONDITION_A_EQUALS_B = 1;
    final private int CONDITION_A_LOWER_B = 2;
    final private int CONDITION_A_GREATER_THRESHOLD = 3;
    final private int CONDITION_A_EQUALS_THRESHOLD = 4;
    final private int CONDITION_A_LOWER_THRESHOLD = 5;
    final private int CONDITION_A_BETWEEN_THRESHOLDS = 6;

    final private int MODE_EVENT_ONCE = 0;
    final private int MODE_EVENT_ALWAYS = 1;

    private IRuntimeInputPort ipInA = new InputPort1();
    private IRuntimeInputPort ipInB = new InputPort2();

    private IRuntimeOutputPort opOutput = new OutputPort1();

    final IRuntimeEventTriggererPort etpConditionTrue = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpConditionFalse = new DefaultRuntimeEventTriggererPort();

    private double propThreshold = 1;
    private double propThreshold2 = 10;
    private int propCondition = CONDITION_A_GREATER_B;
    private int propOutputMode = MODE_OUTPUT_A_IF_TRUE;
    private int propEventMode = MODE_EVENT_ONCE;

    private boolean conditionMet = false;
    private double inA = 0, inB = 0;

    /**
     * The class constructor.
     */
    public ComparatorInstance() {
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
        if ("inA".equalsIgnoreCase(portID)) {
            return ipInA;
        } else if ("inB".equalsIgnoreCase(portID)) {
            return ipInB;
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
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the event triggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("conditionTrue".equalsIgnoreCase(eventPortID)) {
            return etpConditionTrue;
        } else if ("conditionFalse".equalsIgnoreCase(eventPortID)) {
            return etpConditionFalse;
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
        if ("condition".equalsIgnoreCase(propertyName)) {
            return propCondition;
        }
        if ("outputMode".equalsIgnoreCase(propertyName)) {
            return propOutputMode;
        }
        if ("eventMode".equalsIgnoreCase(propertyName)) {
            return propEventMode;
        }
        if ("threshold".equalsIgnoreCase(propertyName)) {
            return propThreshold;
        }
        if ("threshold2".equalsIgnoreCase(propertyName)) {
            return propThreshold2;
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
        if ("condition".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propCondition;
            propCondition = Integer.parseInt((String) newValue);
            return oldValue;
        }
        if ("outputMode".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propOutputMode;
            propOutputMode = Integer.parseInt((String) newValue);
            return oldValue;
        }
        if ("eventMode".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propEventMode;
            propEventMode = Integer.parseInt((String) newValue);
            return oldValue;
        }
        if ("threshold".equalsIgnoreCase(propertyName)) {
            final double oldValue = propThreshold;
            propThreshold = Double.parseDouble((String) newValue);
            return oldValue;
        }
        if ("threshold2".equalsIgnoreCase(propertyName)) {
            final double oldValue = propThreshold2;
            propThreshold2 = Double.parseDouble((String) newValue);
            return oldValue;
        }
        return null;
    }

    /**
     * checks for event conditions and raises events if condition met.
     */
    synchronized private void checkEvents() {
        switch (propCondition) {
        case CONDITION_A_GREATER_B:
            if (inA > inB) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_EQUALS_B:
            if (inA == inB) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_LOWER_B:
            if (inA < inB) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_GREATER_THRESHOLD:
            if (inA > propThreshold) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_EQUALS_THRESHOLD:
            if (inA == propThreshold) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_LOWER_THRESHOLD:
            if (inA < propThreshold) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        case CONDITION_A_BETWEEN_THRESHOLDS:
            if ((inA >= propThreshold) && (inA <= propThreshold2)) {
                if ((conditionMet == false) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionTrue.raiseEvent();
                    conditionMet = true;
                }
            } else {
                if ((conditionMet == true) || (propEventMode == MODE_EVENT_ALWAYS)) {
                    etpConditionFalse.raiseEvent();
                    conditionMet = false;
                }
            }
            break;
        }

    }

    /**
     * checks for event conditions and puts out signal if condition met.
     */
    synchronized private void checkOutput() {
        if (propCondition >= CONDITION_A_GREATER_THRESHOLD) {
            inB = propThreshold;
        }

        switch (propOutputMode) {
        case MODE_MAX:
            if (inA > inB) {
                opOutput.sendData(ConversionUtils.doubleToBytes(inA));
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(inB));
            }
            break;
        case MODE_MIN:
            if (inA < inB) {
                opOutput.sendData(ConversionUtils.doubleToBytes(inA));
            } else {
                opOutput.sendData(ConversionUtils.doubleToBytes(inB));
            }
            break;
        case MODE_OUTPUT_A_IF_TRUE:
            if (conditionMet == true) {
                opOutput.sendData(ConversionUtils.doubleToBytes(inA));
            }
            break;
        }

    }

    /**
     * Input Port for receiving A value.
     */
    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            // convert input to int
            inA = ConversionUtils.doubleFromBytes(data);
            checkEvents();
            checkOutput();
        }
    }

    /**
     * Input Port for receiving B value.
     */
    private class InputPort2 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            // convert input to integer
            inB = ConversionUtils.doubleFromBytes(data);
            checkEvents();
            checkOutput();
        }
    }

    /**
     * Input Port for sending result value.
     */
    private class OutputPort1 extends DefaultRuntimeOutputPort {
        @Override
        public void sendData(final byte[] data) {
            super.sendData(data);
        }
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

    @Override
    public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {
        for (String s : dataRow.keySet()) {

            byte[] data = dataRow.get(s);
            if (s.equals("inA")) {
                inA = ConversionUtils.doubleFromBytes(data);
            }
            if (s.equals("inB")) {
                inB = ConversionUtils.doubleFromBytes(data);
            }
        }

        checkEvents();
        checkOutput();
    }

}