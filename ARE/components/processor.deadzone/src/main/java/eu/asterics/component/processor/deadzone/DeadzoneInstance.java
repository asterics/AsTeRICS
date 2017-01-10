
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

package eu.asterics.component.processor.deadzone;

import java.util.HashMap;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * Implements the DeadZone plugin, which can attenuate 1d or 2d signals which
 * are inside or outside a defined radius around a center value
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Apr 18, 2011 Time:
 *         08:35:00 PM
 */
public class DeadzoneInstance extends AbstractRuntimeComponentInstance {
    final private int MODE_INNER = 0;
    final private int MODE_OUTER = 1;
    final private int MODE_DEADZONE = 2;

    private double propXCenter = 0;
    private double propYCenter = 0;
    private double propRadius = 10;
    private int propMode = 0;

    private double xValue = 0;
    private double yValue = 0;
    private double dx = 0;
    private double dy = 0;
    private double dist = 0;

    private boolean eventCondition = false;

    private IRuntimeInputPort ipInX = new InputPortX();
    private IRuntimeInputPort ipInY = new InputPortY();
    private IRuntimeInputPort ipRadius = new InputPortRadius();
    private IRuntimeOutputPort opOutX = new OutputPort();
    private IRuntimeOutputPort opOutY = new OutputPort();

    final IRuntimeEventTriggererPort etpEnterZone = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpExitZone = new DefaultRuntimeEventTriggererPort();
    IRuntimeEventListenerPort elpSetCenter = new RuntimeEventListenerPortSetCenter();

    /**
     * The class constructor.
     */
    public DeadzoneInstance() {
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
        if ("inX".equalsIgnoreCase(portID)) {
            return ipInX;
        } else if ("inY".equalsIgnoreCase(portID)) {
            return ipInY;
        } else if ("radius".equalsIgnoreCase(portID)) {
            return ipRadius;
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
        if ("outX".equalsIgnoreCase(portID)) {
            return opOutX;
        } else if ("outY".equalsIgnoreCase(portID)) {
            return opOutY;
        } else {
            return null;
        }
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (eventPortID.equalsIgnoreCase("setCenter")) {
            return elpSetCenter;
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
        if (eventPortID.equalsIgnoreCase("enterZone")) {
            return etpEnterZone;
        } else if (eventPortID.equalsIgnoreCase("exitZone")) {
            return etpExitZone;
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
        if ("xCenter".equalsIgnoreCase(propertyName)) {
            return propXCenter;
        } else if ("yCenter".equalsIgnoreCase(propertyName)) {
            return propYCenter;
        } else if ("radius".equalsIgnoreCase(propertyName)) {
            return propRadius;
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            return propMode;
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
        try {
            if ("xCenter".equalsIgnoreCase(propertyName)) {
                final Object oldValue = propXCenter;

                if (newValue != null) {
                    propXCenter = Double.parseDouble((String) newValue);
                }
                return oldValue;
            } else if ("yCenter".equalsIgnoreCase(propertyName)) {
                final Object oldValue = propYCenter;

                if (newValue != null) {
                    propYCenter = Double.parseDouble((String) newValue);
                }
                return oldValue;
            } else if ("radius".equalsIgnoreCase(propertyName)) {
                final Object oldValue = propRadius;

                if (newValue != null) {
                    propRadius = Double.parseDouble((String) newValue);
                }
                return oldValue;
            } else if ("mode".equalsIgnoreCase(propertyName)) {
                final Object oldValue = propMode;

                if (newValue != null) {
                    propMode = Integer.parseInt(newValue.toString());
                }
                return oldValue;
            } else {
                return null;
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid property value for " + propertyName + ": " + newValue);
            // TBD: call asterics logger instead
        }
    }

    /**
     * processes the input values according to the selected mode of the deadzone
     * plugin: for MODE_INNER, only values inside the radius are routed to the
     * outputs for MODE_OUTER, only values outside the radius are routed to the
     * outputs for MODE_DEADZONE, only values outside the radius are routed to
     * the outputs, and the input values are modified to start with 0 at the
     * outer zone
     */
    synchronized private void process() {
        dx = xValue - propXCenter;
        dy = yValue - propYCenter;
        dist = Math.sqrt(dx * dx + dy * dy);

        switch (propMode) {
        case MODE_INNER:
            if (dist < propRadius) {
                opOutX.sendData(ConversionUtils.doubleToBytes(dx));
                opOutY.sendData(ConversionUtils.doubleToBytes(dy));
                if (eventCondition == false) {
                    eventCondition = true;
                    etpEnterZone.raiseEvent();
                }
            } else if (eventCondition == true) {
                eventCondition = false;
                etpExitZone.raiseEvent();
            }
            break;
        case MODE_OUTER:
            if (dist >= propRadius) {
                opOutX.sendData(ConversionUtils.doubleToBytes(dx));
                opOutY.sendData(ConversionUtils.doubleToBytes(dy));
                if (eventCondition == false) {
                    eventCondition = true;
                    etpEnterZone.raiseEvent();
                }
            } else if (eventCondition == true) {
                eventCondition = false;
                etpExitZone.raiseEvent();
            }
            break;
        case MODE_DEADZONE:
            if (dist >= propRadius) {
                opOutX.sendData(ConversionUtils.doubleToBytes(dx - propRadius * dx / dist));
                opOutY.sendData(ConversionUtils.doubleToBytes(dy - propRadius * dy / dist));
                if (eventCondition == false) {
                    eventCondition = true;
                    etpEnterZone.raiseEvent();
                }
            } else if (eventCondition == true) {
                eventCondition = false;
                etpExitZone.raiseEvent();
            }
            break;
        }
    }

    /**
     * Input Port for receiving x value.
     */
    private class InputPortX extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            xValue = ConversionUtils.doubleFromBytes(data);
            process();
        }
    }

    /**
     * Input Port for receiving y value.
     */
    private class InputPortY extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            yValue = ConversionUtils.doubleFromBytes(data);
            process();
        }
    }

    /**
     * Input Port for receiving radius.
     */
    private class InputPortRadius extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            propRadius = ConversionUtils.doubleFromBytes(data);
        }
    }

    /**
     * Output Port for sending values.
     */
    private class OutputPort extends DefaultRuntimeOutputPort {
        // empty
    }

    /**
     * Event Listener Port for reset x and y to center position.
     */
    class RuntimeEventListenerPortSetCenter implements IRuntimeEventListenerPort {
        @Override
        public void receiveEvent(final String data) {
            propXCenter = xValue;
            propYCenter = yValue;
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        eventCondition = false;
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
            if (s.equals("inX")) {
                xValue = ConversionUtils.doubleFromBytes(data);
            }
            if (s.equals("inY")) {
                yValue = ConversionUtils.doubleFromBytes(data);
            }
        }
        process();
    }

}