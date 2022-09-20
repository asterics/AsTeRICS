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

package eu.asterics.component.processor.eventcounter;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Implements plugin which counts events.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Apr 06, 2011 Time: 12:48:18
 *         AM
 */
public class EventCounterInstance extends AbstractRuntimeComponentInstance {
    private final int MODE_LIMIT_MAX = 1;
    private final int MODE_LIMIT_MIN = 2;
    private final int MODE_LIMIT_BOTH = 3;

    private final String OP_OUTPUT = "output";
    private final String ELP_INCREASE = "increase";
    private final String ELP_DECREASE = "decrease";
    private final String ELP_RESET_TO_ZERO = "resetToZero";
    private final String ELP_RESET_TO_INITIAL = "resetToInitial";
    private final String ELP_SEND_NOW = "sendNow";

    private final OutputPort opOutput = new OutputPort();
    private int eventCounterValue = 0;

    private int propMode = 0;
    private int propMinValue = 0;
    private int propMaxValue = 1000;
    private int propInitialValue = 0;
    private boolean propWrapAround = false;
    private boolean propSendInitialValue = false;
    private boolean propAutoSend = true;

    /**
     * The class constructor.
     */
    public EventCounterInstance() {
    }

    /**
     * Returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("setValue".equalsIgnoreCase(portID)) {
            return ipSetValue;
        }
        return null;
    }

    /**
     * Returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if (OP_OUTPUT.equalsIgnoreCase(portID)) {
            return opOutput;
        }
        return null;
    }

    /**
     * Returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (ELP_INCREASE.equalsIgnoreCase(eventPortID)) {
            return elpIncrease;
        } else if (ELP_DECREASE.equalsIgnoreCase(eventPortID)) {
            return elpDecrease;
        } else if (ELP_RESET_TO_ZERO.equalsIgnoreCase(eventPortID)) {
            return elpResetToZero;
        } else if (ELP_RESET_TO_INITIAL.equalsIgnoreCase(eventPortID)) {
            return elpResetToInitial;
        } else if (ELP_SEND_NOW.equalsIgnoreCase(eventPortID)) {
            return elpSendNow;
        }

        return null;
    }

    /**
     * Returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("mode".equalsIgnoreCase(propertyName)) {
            return (propMode);
        } else if ("minValue".equalsIgnoreCase(propertyName)) {
            return (propMinValue);
        } else if ("maxValue".equalsIgnoreCase(propertyName)) {
            return (propMaxValue);
        } else if ("initialValue".equalsIgnoreCase(propertyName)) {
            return (propInitialValue);
        } else if ("wrapAround".equalsIgnoreCase(propertyName)) {
            return (propWrapAround);
        } else if ("sendInitialValue".equalsIgnoreCase(propertyName)) {
            return (propSendInitialValue);
        } else if ("autoSend".equalsIgnoreCase(propertyName)) {
            return (propAutoSend);
        }

        return null;
    }

    /**
     * Sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value
     * @return old property value
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("mode".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propMode;
            propMode = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("minValue".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propMinValue;
            propMinValue = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("maxValue".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propMaxValue;
            propMaxValue = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("initialValue".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propInitialValue;
            propInitialValue = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("minValue".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propMinValue;
            propMinValue = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("wrapAround".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWrapAround;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propWrapAround = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propWrapAround = false;
            }
            return oldValue;
        } else if ("sendInitialValue".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendInitialValue;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propSendInitialValue = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propSendInitialValue = false;
            }
            return oldValue;
        } else if ("autoSend".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendInitialValue;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoSend = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoSend = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Event Listener Port for increase the number of the events.
     */
    final IRuntimeEventListenerPort elpIncrease = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propMode == MODE_LIMIT_MAX) || (propMode == MODE_LIMIT_BOTH)) {
                if (eventCounterValue < propMaxValue) {
                    eventCounterValue = eventCounterValue + 1;
                    if (propAutoSend == true) {
                        opOutput.sendData(eventCounterValue);
                    }
                } else if (propWrapAround == true) {
                    eventCounterValue = propMinValue;
                    if (propAutoSend == true) {
                        opOutput.sendData(eventCounterValue);
                    }
                }
            } else {
                eventCounterValue = eventCounterValue + 1;
                if (propAutoSend == true) {
                    opOutput.sendData(eventCounterValue);
                }
            }
        }
    };

    /**
     * Event Listener Port for decrease the number of the events.
     */
    final IRuntimeEventListenerPort elpDecrease = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propMode == MODE_LIMIT_MIN) || (propMode == MODE_LIMIT_BOTH)) {
                if (eventCounterValue > propMinValue) {
                    eventCounterValue = eventCounterValue - 1;
                    if (propAutoSend == true) {
                        opOutput.sendData(eventCounterValue);
                    }
                } else if (propWrapAround == true) {
                    eventCounterValue = propMaxValue;
                    if (propAutoSend == true) {
                        opOutput.sendData(eventCounterValue);
                    }
                }
            } else {
                eventCounterValue = eventCounterValue - 1;
                if (propAutoSend == true) {
                    opOutput.sendData(eventCounterValue);
                }
            }
        }
    };

    /**
     * Event Listener Port for reset the event number.
     */
    final IRuntimeEventListenerPort elpResetToZero = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            eventCounterValue = 0;
            if (propAutoSend == true) {
                opOutput.sendData(eventCounterValue);
            }
        }
    };

    final IRuntimeEventListenerPort elpResetToInitial = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            eventCounterValue = propInitialValue;
            if (propAutoSend == true) {
                opOutput.sendData(eventCounterValue);
            }
        }
    };
    final IRuntimeEventListenerPort elpSendNow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            opOutput.sendData(eventCounterValue);
        }
    };

    /**
     * Plugin input port.
     */
    private final IRuntimeInputPort ipSetValue = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            int value = ConversionUtils.intFromBytes(data);
            if (value < propMinValue) {
                value = propMinValue;
            } else {
                if (value > propMaxValue) {
                    value = propMaxValue;
                }
            }
            eventCounterValue = value;
        }
    };

    /**
     * Plugin output port.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public synchronized void sendData(int data) {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }

    /**
     * Called when model is started.
     */
    @Override
    public void start() {
        super.start();
        eventCounterValue = propInitialValue;
        if (propSendInitialValue == true) {
            Runnable delay = new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }
                    opOutput.sendData(eventCounterValue);

                }

            };

            AstericsThreadPool.instance.execute(delay);

        }
    }

    /**
     * Called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
    }
}