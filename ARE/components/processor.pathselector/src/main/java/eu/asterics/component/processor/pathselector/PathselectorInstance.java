
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

package eu.asterics.component.processor.pathselector;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the pathselector plugin, which selects routes the input of one
 * input port to one out of 4 output ports. The selection can be done via event
 * ports.
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         04:55:00 PM
 */
public class PathselectorInstance extends AbstractRuntimeComponentInstance {
    final private String OUT_PORT1_KEY = "out1";
    final private String OUT_PORT2_KEY = "out2";
    final private String OUT_PORT3_KEY = "out3";
    final private String OUT_PORT4_KEY = "out4";
    final private String IN_PORT_KEY = "in";
    final private String EVENT_LISTENER_SELECT1_KEY = "select1";
    final private String EVENT_LISTENER_SELECT2_KEY = "select2";
    final private String EVENT_LISTENER_SELECT3_KEY = "select3";
    final private String EVENT_LISTENER_SELECT4_KEY = "select4";
    final private String EVENT_LISTENER_SELECT_NEXT_KEY = "selectNext";
    final private String EVENT_LISTENER_SELECT_PREV_KEY = "selectPrevious";

    final private String ACTIVE_PORTS_PROPERTY_KEY = "activePorts";

    final IRuntimeOutputPort opOut1 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opOut2 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opOut3 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opOut4 = new DefaultRuntimeOutputPort();

    int propActivePorts = 2;
    int selectedPort = 1;

    /**
     * The class constructor.
     */
    public PathselectorInstance() {
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
        if (IN_PORT_KEY.equalsIgnoreCase(portID)) {
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
        if (OUT_PORT1_KEY.equalsIgnoreCase(portID)) {
            return opOut1;
        } else if (OUT_PORT2_KEY.equalsIgnoreCase(portID)) {
            return opOut2;
        } else if (OUT_PORT3_KEY.equalsIgnoreCase(portID)) {
            return opOut3;
        } else if (OUT_PORT4_KEY.equalsIgnoreCase(portID)) {
            return opOut4;
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
        if (EVENT_LISTENER_SELECT1_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelect1;
        } else if (EVENT_LISTENER_SELECT2_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelect2;
        } else if (EVENT_LISTENER_SELECT3_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelect3;
        } else if (EVENT_LISTENER_SELECT4_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelect4;
        } else if (EVENT_LISTENER_SELECT_NEXT_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelectNext;
        } else if (EVENT_LISTENER_SELECT_PREV_KEY.equalsIgnoreCase(eventPortID)) {
            return elpSelectPrev;
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
        if (ACTIVE_PORTS_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
            return propActivePorts - 1;
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
        try {
            if (ACTIVE_PORTS_PROPERTY_KEY.equalsIgnoreCase(propertyName)) {
                final Object oldValue = propActivePorts - 1;
                propActivePorts = Integer.parseInt(newValue.toString()) + 1;
                if ((propActivePorts < 1) || (propActivePorts > 4)) {
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
     * Input Port for receiving values.
     */
    private final IRuntimeInputPort ipIn = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            double in = ConversionUtils.doubleFromBytes(data);
            switch (selectedPort) {
            case 1:
                opOut1.sendData(ConversionUtils.doubleToBytes(in));
                break;
            case 2:
                opOut2.sendData(ConversionUtils.doubleToBytes(in));
                break;
            case 3:
                opOut3.sendData(ConversionUtils.doubleToBytes(in));
                break;
            case 4:
                opOut4.sendData(ConversionUtils.doubleToBytes(in));
                break;
            default:
                break;
            }
        }

    };

    /**
     * Event Listener Port for selection of out port 1
     */
    final IRuntimeEventListenerPort elpSelect1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort = 1;
        }
    };

    /**
     * Event Listener Port for selection of out port 2
     */
    final IRuntimeEventListenerPort elpSelect2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort = 2;
        }
    };

    /**
     * Event Listener Port for selection of out port 3
     */
    final IRuntimeEventListenerPort elpSelect3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort = 3;
        }
    };

    /**
     * Event Listener Port for selection of out port 4
     */
    final IRuntimeEventListenerPort elpSelect4 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort = 4;
        }
    };

    /**
     * Event Listener Port for selection of next out port
     */
    final IRuntimeEventListenerPort elpSelectNext = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort++;
            if (selectedPort > propActivePorts) {
                selectedPort = 1;
            }
        }
    };

    /**
     * Event Listener Port for selection of previous out port
     */
    final IRuntimeEventListenerPort elpSelectPrev = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            selectedPort--;
            if (selectedPort < 1) {
                selectedPort = propActivePorts;
            }
        }
    };

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