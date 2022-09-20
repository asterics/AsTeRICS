
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

package eu.asterics.component.processor.eventrouter;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

/**
 * 
 * Routed incoming events to one of 8 event trigger ports
 * 
 * 
 * 
 * @author Chris Veigl Date: 2.7.2013
 */
public class EventRouterInstance extends AbstractRuntimeComponentInstance {
    public final int NUMBER_OF_ROUTES = 8;
    public final int NUMBER_OF_INPUTS = 6;
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpEventOut1 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut2 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut3 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut4 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut5 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut6 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut7 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEventOut8 = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    public final EventInListener[] elpEventIn = new EventInListener[NUMBER_OF_INPUTS];
    public final EventSelectListener[] elpSelect = new EventSelectListener[NUMBER_OF_ROUTES];
    public final IRuntimeEventTriggererPort[][] etpEventOut = new DefaultRuntimeEventTriggererPort[NUMBER_OF_INPUTS][NUMBER_OF_ROUTES];

    // declare member variables here

    private int actRoute = 1;
    private int propActiveRoutes = 3;
    private boolean propWrapAround = true;

    /**
     * The class constructor.
     */
    public EventRouterInstance() {
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            elpEventIn[i] = new EventInListener(i);
            for (int t = 0; t < NUMBER_OF_ROUTES; t++) {
                etpEventOut[i][t] = new DefaultRuntimeEventTriggererPort();
            }
        }
        for (int t = 0; t < NUMBER_OF_ROUTES; t++) {
            elpSelect[t] = new EventSelectListener(t);
        }
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
        if ("routeIndex".equalsIgnoreCase(portID)) {
            return ipRouteIndex;
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
        return null;
    }

    /**
     * Input Port for receiving values.
     */
    private final IRuntimeInputPort ipRouteIndex = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            actRoute = ConversionUtils.intFromBytes(data);
            if (actRoute < 1) {
                actRoute = 1;
            }

            if (actRoute > NUMBER_OF_ROUTES) {
                actRoute = NUMBER_OF_ROUTES;
            }
        }
    };

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        String s;
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            if (i == 0) {
                s = "eventIn"; // first without index for compatibility reasons
                               // !
            } else {
                s = "eventIn" + (i + 1);
            }

            if (s.equalsIgnoreCase(eventPortID)) {
                return elpEventIn[i];
            }
        }
        for (int i = 0; i < NUMBER_OF_ROUTES; i++) {
            s = "select" + (i + 1);

            if (s.equalsIgnoreCase(eventPortID)) {
                return elpSelect[i];
            }
        }

        if ("selectNext".equalsIgnoreCase(eventPortID)) {
            return elpSelectNext;
        }
        if ("selectPrevious".equalsIgnoreCase(eventPortID)) {
            return elpSelectPrevious;
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
        String s;
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            for (int t = 0; t < NUMBER_OF_ROUTES; t++) {
                if (i == 0) {
                    s = "eventOut" + (t + 1); // first without index for
                                              // compatibility reasons !
                } else {
                    s = "eventOut" + (i + 1) + "_" + (t + 1);
                }

                if (s.equalsIgnoreCase(eventPortID)) {
                    return etpEventOut[i][t];
                }
            }
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
        if ("activeRoutes".equalsIgnoreCase(propertyName)) {
            return propActiveRoutes;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            return propWrapAround;
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
        if ("activeRoutes".equalsIgnoreCase(propertyName)) {
            final int oldValue = propActiveRoutes;
            propActiveRoutes = Integer.parseInt((String) newValue);
            if (propActiveRoutes < 1) {
                propActiveRoutes = 1;
            }
            if (propActiveRoutes > NUMBER_OF_ROUTES) {
                propActiveRoutes = NUMBER_OF_ROUTES;
            }
            return oldValue;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            final boolean oldValue = propWrapAround;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propWrapAround = true;
            } else {
                propWrapAround = false;
            }
            return oldValue;
        }
        return null;
    }

    /**
     * Input Ports for receiving values.
     */

    /**
     * Event Listener Ports.
     */

    final IRuntimeEventListenerPort elpSelectNext = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            actRoute = actRoute + 1;
            if (actRoute > propActiveRoutes) {
                if (propWrapAround == true) {
                    actRoute = 1;
                } else {
                    actRoute = propActiveRoutes;
                }
            }
        }
    };

    final IRuntimeEventListenerPort elpSelectPrevious = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            actRoute = actRoute - 1;
            if (actRoute < 1) {
                if (propWrapAround == true) {
                    actRoute = propActiveRoutes;
                } else {
                    actRoute = 1;
                }
            }
        }
    };

    class EventInListener implements IRuntimeEventListenerPort {
        private int index;

        EventInListener(int index) {
            this.index = index;
        }

        @Override
        public void receiveEvent(final String data) {
            // etpEventOut[index][actRoute-1].setEventChannelID("event
            // "+(index+1)+" route "+actRoute);
            etpEventOut[index][actRoute - 1].raiseEvent();
        }
    }

    class EventSelectListener implements IRuntimeEventListenerPort {
        private int index;

        EventSelectListener(int index) {
            this.index = index;
        }

        @Override
        public void receiveEvent(final String data) {
            actRoute = index + 1;
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
}