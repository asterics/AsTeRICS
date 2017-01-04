
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

package eu.asterics.component.actuator.arewindow;

import java.awt.Frame;
import java.awt.Point;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * Provides basic manipulation of ARE Windows (set position / state)
 * 
 * 
 * 
 * @author Chris Veigl 29 12 2014
 * 
 */
public class AREWindowInstance extends AbstractRuntimeComponentInstance {
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propXPos = 0;
    int propYPos = 0;
    boolean propAutoSetPosition = false;
    boolean propAllowWindowModification = true;

    // declare member variables here

    /**
     * The class constructor.
     */
    public AREWindowInstance() {
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
        if ("xPos".equalsIgnoreCase(portID)) {
            return ipXPos;
        }
        if ("yPos".equalsIgnoreCase(portID)) {
            return ipYPos;
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
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("moveToTop".equalsIgnoreCase(eventPortID)) {
            return elpMoveToTop;
        }
        if ("moveToBottom".equalsIgnoreCase(eventPortID)) {
            return elpMoveToBottom;
        }
        if ("moveToLeft".equalsIgnoreCase(eventPortID)) {
            return elpMoveToLeft;
        }
        if ("moveToRight".equalsIgnoreCase(eventPortID)) {
            return elpMoveToRight;
        }
        if ("moveToCenter".equalsIgnoreCase(eventPortID)) {
            return elpMoveToCenter;
        }
        if ("minimize".equalsIgnoreCase(eventPortID)) {
            return elpMinimize;
        }
        if ("restore".equalsIgnoreCase(eventPortID)) {
            return elpRestore;
        }
        if ("bringToFront".equalsIgnoreCase(eventPortID)) {
            return elpBringToFront;
        }

        if ("setWindowFocusalbe".equalsIgnoreCase(eventPortID)) {
            return elpSetWindowFocusable;
        }
        if ("setWindowNotFocusalbe".equalsIgnoreCase(eventPortID)) {
            return elpSetWindowNotFocusable;
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
        if ("xPos".equalsIgnoreCase(propertyName)) {
            return propXPos;
        }
        if ("yPos".equalsIgnoreCase(propertyName)) {
            return propYPos;
        }
        if ("setAutoSetPosition".equalsIgnoreCase(propertyName)) {
            return propAutoSetPosition;
        }
        if ("allowWindowModification".equalsIgnoreCase(propertyName)) {
            return propAllowWindowModification;
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
        if ("xPos".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propXPos;
            propXPos = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("yPos".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propYPos;
            propYPos = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("autoSetPosition".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoSetPosition;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoSetPosition = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoSetPosition = false;
            }
            return oldValue;
        }
        if ("allowWindowModification".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAllowWindowModification;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAllowWindowModification = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAllowWindowModification = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipXPos = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propXPos = ConversionUtils.intFromBytes(data);
            if (propAutoSetPosition == true) {
                Point p = AREServices.instance.getAREWindowPosition();
                p.x = propXPos;
                AREServices.instance.setAREWindowPosition(p.x, p.y);
            }
        }
    };
    private final IRuntimeInputPort ipYPos = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propYPos = ConversionUtils.intFromBytes(data);
            if (propAutoSetPosition == true) {
                Point p = AREServices.instance.getAREWindowPosition();
                p.y = propYPos;
                AREServices.instance.setAREWindowPosition(p.x, p.y);
            }
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpMoveToTop = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // System.out.println("X found !!");
            Point p = AREServices.instance.getAREWindowPosition();
            p.y = propYPos;
            AREServices.instance.setAREWindowPosition(p.x + propXPos, p.y);
        }
    };
    final IRuntimeEventListenerPort elpMoveToBottom = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            Point pos = AREServices.instance.getAREWindowPosition();
            Point dim = AREServices.instance.getAREWindowDimension();
            Point screen = AREServices.instance.getScreenDimension();

            pos.y = screen.y - dim.y + propYPos;
            AREServices.instance.setAREWindowPosition(pos.x + propXPos, pos.y);
        }
    };
    final IRuntimeEventListenerPort elpMoveToLeft = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            Point p = AREServices.instance.getAREWindowPosition();
            p.x = propXPos;
            AREServices.instance.setAREWindowPosition(p.x, p.y + propYPos);
        }
    };
    final IRuntimeEventListenerPort elpMoveToRight = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            Point pos = AREServices.instance.getAREWindowPosition();
            Point dim = AREServices.instance.getAREWindowDimension();
            Point screen = AREServices.instance.getScreenDimension();

            pos.x = screen.x - dim.x + propXPos;
            AREServices.instance.setAREWindowPosition(pos.x, pos.y + propYPos);
        }
    };
    final IRuntimeEventListenerPort elpMoveToCenter = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            Point pos = AREServices.instance.getAREWindowPosition();
            Point dim = AREServices.instance.getAREWindowDimension();
            Point screen = AREServices.instance.getScreenDimension();

            pos.x = screen.x / 2 - dim.x / 2 + propXPos;
            pos.y = screen.y / 2 - dim.y / 2 + propYPos;
            AREServices.instance.setAREWindowPosition(pos.x, pos.y);
        }
    };

    final IRuntimeEventListenerPort elpMinimize = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            AREServices.instance.setAREWindowState(Frame.ICONIFIED);
        }
    };
    final IRuntimeEventListenerPort elpRestore = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            AREServices.instance.setAREWindowState(Frame.NORMAL);
        }
    };
    final IRuntimeEventListenerPort elpBringToFront = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            AREServices.instance.setAREWindowState(Frame.ICONIFIED);
            AREServices.instance.setAREWindowState(Frame.NORMAL);

            // AREServices.instance.setAREWindowToFront();
        }
    };

    final IRuntimeEventListenerPort elpSetWindowFocusable = new IRuntimeEventListenerPort() {

        @Override
        public void receiveEvent(String data) {
            AREServices.instance.setFocusableWindowState(true);
        }
    };

    final IRuntimeEventListenerPort elpSetWindowNotFocusable = new IRuntimeEventListenerPort() {

        @Override
        public void receiveEvent(String data) {
            AREServices.instance.setFocusableWindowState(false);
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        if (propAutoSetPosition == true) {
            Point p = AREServices.instance.getAREWindowPosition();
            p.x = propXPos;
            p.y = propYPos;
            AREServices.instance.setAREWindowPosition(p.x, p.y);
        }
        AREServices.instance.allowAREWindowModification(propAllowWindowModification);
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