
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

package eu.asterics.component.sensor.mousecapture;

import eu.asterics.component.sensor.mousecapture.jni.Bridge;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import java.awt.MouseInfo;

/**
 * MouseCaptureInstance intercepts local mouse input and routes the mouse
 * actions to output ports of the plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Feb 10, 2011 Time:
 *         2:45:00 PM 
 */
public class MouseCaptureInstance extends AbstractRuntimeComponentInstance {
    private final OutputPort opMouseX = new OutputPort();
    private final OutputPort opMouseY = new OutputPort();
    private final EventTriggerPort etpLeftButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpLeftButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpRightButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpRightButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpMiddleButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpMiddleButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpWheelUp = new EventTriggerPort();
    private final EventTriggerPort etpWheelDown = new EventTriggerPort();

    private final Bridge bridge = new Bridge(opMouseX, opMouseY, etpLeftButtonPressed, etpLeftButtonReleased,
            etpRightButtonPressed, etpRightButtonReleased, etpMiddleButtonPressed, etpMiddleButtonReleased, etpWheelUp,
            etpWheelDown);

    /**
     * The class constructor.
     */
    public MouseCaptureInstance() {
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
        if ("mouseX".equalsIgnoreCase(portID)) {
            return opMouseX;
        } else if ("mouseY".equalsIgnoreCase(portID)) {
            return opMouseY;
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
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            return bridge.getProperty(propertyName);
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
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            final String oldValue = bridge.getProperty(propertyName);
            bridge.setProperty(propertyName, newValue.toString());

            return oldValue;
        }
        return null;
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpBlockEvents = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            bridge.setProperty("blockEvents", "true");
        }
    };

    final IRuntimeEventListenerPort elpForwardEvents = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            bridge.setProperty("blockEvents", "false");
        }
    };

    final IRuntimeEventListenerPort elpToggleBlock = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if ("true".equals(bridge.getProperty("blockEvents")) || "True".equals(bridge.getProperty("blockEvents"))) {
                bridge.setProperty("blockEvents", "false");
            } else {
                bridge.setProperty("blockEvents", "true");
            }
        }
    };

    final IRuntimeEventListenerPort elpPollMousePosition = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
        	opMouseX.sendData(MouseInfo.getPointerInfo().getLocation().x);
        	opMouseY.sendData(MouseInfo.getPointerInfo().getLocation().y);
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
        if ("blockEvents".equalsIgnoreCase(eventPortID)) {
            return elpBlockEvents;
        } else if ("forwardEvents".equalsIgnoreCase(eventPortID)) {
            return elpForwardEvents;
        } else if ("toggleBlock".equalsIgnoreCase(eventPortID)) {
            return elpToggleBlock;
        } else if ("pollMousePosition".equalsIgnoreCase(eventPortID)) {
            return elpPollMousePosition;
        }
        return null;
    }

    /**
     * returns an Event Triggerer Port
     * 
     * @param enventPortID
     *            the name of the event trigger port
     * @return the event trigger port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("leftButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpLeftButtonPressed;
        } else if ("leftButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpLeftButtonReleased;
        } else if ("rightButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpRightButtonPressed;
        } else if ("rightButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpRightButtonReleased;
        } else if ("middleButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpMiddleButtonPressed;
        } else if ("middleButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpMiddleButtonReleased;
        } else if ("wheelUp".equalsIgnoreCase(eventPortID)) {
            return etpWheelUp;
        } else if ("wheelDown".equalsIgnoreCase(eventPortID)) {
            return etpWheelDown;
        }
        return null;
    }

    /**
     * Output Port for mouse coordinate values.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }

    /**
     * Event Triggerer Port for mouse actions.
     */
    public class EventTriggerPort extends DefaultRuntimeEventTriggererPort {
        @Override
        public void raiseEvent() {
            super.raiseEvent();
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        bridge.activate();
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        bridge.deactivate();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        bridge.activate();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        bridge.deactivate();
        super.stop();
    }

}