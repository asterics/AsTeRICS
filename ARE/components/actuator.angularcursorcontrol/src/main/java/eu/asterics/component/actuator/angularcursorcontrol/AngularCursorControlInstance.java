
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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.angularcursorcontrol;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * The AngularCursorControl component allows mouse cursor positioning by software emulation with a limited number of input control channels.
 * 
 * 
 * @author Chris, Date: 2019-01-20
 */
public class AngularCursorControlInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeEventTriggererPort etpClickEvent = new DefaultRuntimeEventTriggererPort();

    boolean propAbsoluteAngle = false;
    int propClickEventTime = 1000;
    int propArrowWidth = 200;
    int propArrowLength = 200;
    int propAcceleration = 100;
    int propBaseVelocity = 30;
    int propMaxVelocity = 1000;
    int propAccelerationAngle = 30;
    int propBaseVelocityAngle = 20;
    int propMaxVelocityAngle = 180;

    // declare member variables here
    private GUI gui = null;
    private float actangle = 0;
    private boolean running;
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveAngleLeft = false;
    private boolean moveAngleRight = false;
    private long lastMoveTime = 0;
    private long lastAngleMoveTime = 0;
    private double currentMoveSpeed = propBaseVelocity;
    private double currentMoveSpeedAngle = propBaseVelocityAngle;

    volatile long elapsedIdleTime = Long.MAX_VALUE;

    /**
     * The class constructor.
     */
    public AngularCursorControlInstance() {
        // empty constructor
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID) {
        if ("angle".equalsIgnoreCase(portID)) {
            return ipAngle;
        }
        if ("move".equalsIgnoreCase(portID)) {
            return ipMove;
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
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("select".equalsIgnoreCase(eventPortID)) {
            return elpSelect;
        }
        if ("startMoveForward".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveForward;
        }
        if ("startMoveBackward".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveBackward;
        }
        if ("startAngleLeft".equalsIgnoreCase(eventPortID)) {
            return elpStartAngleLeft;
        }
        if ("startAngleRight".equalsIgnoreCase(eventPortID)) {
            return elpStartAngleRight;
        }
        if ("stopMoveForward".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveForward;
        }
        if ("stopMoveBackward".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveBackward;
        }
        if ("stopAngleLeft".equalsIgnoreCase(eventPortID)) {
            return elpStopAngleLeft;
        }
        if ("stopAngleRight".equalsIgnoreCase(eventPortID)) {
            return elpStopAngleRight;
        }
        if ("stopMove".equalsIgnoreCase(eventPortID)) {
            return elpStopMove;
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
        if ("clickEvent".equalsIgnoreCase(eventPortID)) {
            return etpClickEvent;
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
    public Object getRuntimePropertyValue(String propertyName) {
        if ("absoluteAngle".equalsIgnoreCase(propertyName)) {
            return propAbsoluteAngle;
        }
        if ("clickEventTime".equalsIgnoreCase(propertyName)) {
            return propClickEventTime;
        }
        if ("arrowWidth".equalsIgnoreCase(propertyName)) {
            return propArrowWidth;
        }
        if ("arrowLength".equalsIgnoreCase(propertyName)) {
            return propArrowLength;
        }
        if ("acceleration".equalsIgnoreCase(propertyName)) {
            return propAcceleration;
        }
        if ("accelerationAngle".equalsIgnoreCase(propertyName)) {
            return propAccelerationAngle;
        }
        if ("maxVelocity".equalsIgnoreCase(propertyName)) {
            return propMaxVelocity;
        }
        if ("maxVelocityAngle".equalsIgnoreCase(propertyName)) {
            return propMaxVelocityAngle;
        }
        if ("baseVelocity".equalsIgnoreCase(propertyName)) {
            return propBaseVelocity;
        }
        if ("baseVelocityAngle".equalsIgnoreCase(propertyName)) {
            return propBaseVelocityAngle;
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
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("absoluteAngle".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAbsoluteAngle;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAbsoluteAngle = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAbsoluteAngle = false;
            }
            return oldValue;
        }
        if ("clickEventTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propClickEventTime;
            propClickEventTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("arrowWidth".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propArrowWidth;
            propArrowWidth = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("arrowLength".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propArrowLength;
            propArrowLength = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("acceleration".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAcceleration;
            propAcceleration = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("baseVelocity".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBaseVelocity;
            propBaseVelocity = Integer.parseInt(newValue.toString());
            currentMoveSpeed = propBaseVelocity;
            return oldValue;
        }
        if ("maxVelocity".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxVelocity;
            propMaxVelocity = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("accelerationAngle".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAccelerationAngle;
            propAccelerationAngle = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("baseVelocityAngle".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBaseVelocityAngle;
            propBaseVelocityAngle = Integer.parseInt(newValue.toString());
            currentMoveSpeedAngle = propBaseVelocityAngle;
            return oldValue;
        }
        if ("maxVelocityAngle".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxVelocityAngle;
            propMaxVelocityAngle = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipAngle = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            elapsedIdleTime = Long.MAX_VALUE;
            if (propAbsoluteAngle == true) {
                actangle = (float) ConversionUtils.doubleFromBytes(data);
            } else {
                actangle += (float) ConversionUtils.doubleFromBytes(data);
            }
            gui.setShape(getCurrentRad());
        }
    };
    private final IRuntimeInputPort ipMove = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            elapsedIdleTime = System.currentTimeMillis();
            double actmove = ConversionUtils.doubleFromBytes(data);
            double dx = actmove * Math.sin(getCurrentRad());
            double dy = -(actmove * Math.cos(getCurrentRad()));
            gui.moveCursor(dx, dy);
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpSelect = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStartMoveForward = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastMoveTime = System.currentTimeMillis();
            moveForward = true;
            moveBackward = false;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStartMoveBackward = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastMoveTime = System.currentTimeMillis();
            moveBackward = true;
            moveForward = false;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStartAngleLeft = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastAngleMoveTime = System.currentTimeMillis();
            moveAngleLeft = true;
            moveAngleRight = false;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStartAngleRight = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastAngleMoveTime = System.currentTimeMillis();
            moveAngleRight = true;
            moveAngleLeft = false;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStopMoveForward = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastMoveTime = System.currentTimeMillis();
            moveForward = false;
            currentMoveSpeed = propBaseVelocity;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStopMoveBackward = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastMoveTime = System.currentTimeMillis();
            moveBackward = false;
            currentMoveSpeed = propBaseVelocity;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStopAngleLeft = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastAngleMoveTime = System.currentTimeMillis();
            moveAngleLeft = false;
            currentMoveSpeedAngle = propBaseVelocityAngle;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStopAngleRight = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            lastAngleMoveTime = System.currentTimeMillis();
            moveAngleRight = false;
            currentMoveSpeedAngle = propBaseVelocityAngle;
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStopMove = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            moveBackward = false;
            moveForward = false;
            currentMoveSpeed = propBaseVelocity;
            moveAngleLeft = false;
            moveAngleRight = false;
            currentMoveSpeedAngle = propBaseVelocityAngle;
        }
    };

    Point position;
    Dimension dimension;

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        gui = new GUI(this, new Dimension(propArrowWidth, propArrowLength));
        super.start();

        elapsedIdleTime = Long.MAX_VALUE;
        running = true;

        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(20);
                        if ((System.currentTimeMillis() - elapsedIdleTime) > propClickEventTime) {
                            etpClickEvent.raiseEvent();
                            gui.setOnTop();
                            elapsedIdleTime = Long.MAX_VALUE;
                        } else {
                            doMove();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        });

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
        running = false;
        final GUI guiToDestroy = gui;
        gui = null;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // now the cleanup of the window can be done at any time in the event dispatch
                // thread wihtout interfering the other code.
                if (guiToDestroy != null) {
                    guiToDestroy.setVisible(false);
                    guiToDestroy.dispose();
                }
            }
        });
    }

    /**
     * moves if the class member boolean variables are set
     */
    private void doMove() {
        if (!this.moveForward && !this.moveBackward && !this.moveAngleLeft && !this.moveAngleRight) {
            return;
        }
        elapsedIdleTime = System.currentTimeMillis();

        if(moveForward || moveBackward) {
            long diffTime = System.currentTimeMillis() - this.lastMoveTime;
            float diffPx = (float) currentMoveSpeed * diffTime / 1000;
            int factor = moveForward ? 1 : -1;
            double dx = factor * diffPx * Math.sin(getCurrentRad());
            double dy = factor * -(diffPx * Math.cos(getCurrentRad()));
            this.lastMoveTime = System.currentTimeMillis();

            this.currentMoveSpeed = getNewSpeed(currentMoveSpeed, propBaseVelocity, propMaxVelocity, diffTime, propAcceleration);
            gui.moveCursor(dx, dy);
        }

        if(moveAngleRight || moveAngleLeft) {
            long diffTimeAngle = System.currentTimeMillis() - this.lastAngleMoveTime;
            float diffAngle = (float) currentMoveSpeedAngle * diffTimeAngle / 1000;
            this.lastAngleMoveTime = System.currentTimeMillis();
            if (moveAngleRight) {
                actangle += diffAngle;
            } else {
                actangle -= diffAngle;
            }

            this.currentMoveSpeedAngle = getNewSpeed(currentMoveSpeedAngle, propBaseVelocityAngle, propMaxVelocityAngle, diffTimeAngle, propAccelerationAngle);
            gui.setShape(getCurrentRad());
        }
    }

    /**
     * returns new speed according to given acceleration, min/max-speed and time difference.
     * If time difference is greater than 0.2 seconds, minSpeed is returned (assuming that it is the first call of the
     * method after initialization)
     *
     * @param currentSpeed
     * @param minSpeed
     * @param maxSpeed
     * @param diffTimeMs
     * @param acceleration
     * @return
     */
    private double getNewSpeed(double currentSpeed, double minSpeed, double maxSpeed, long diffTimeMs, double acceleration) {
        if(diffTimeMs > 200) {
            return  minSpeed;
        }
        float diffSpeed = (float) acceleration * diffTimeMs / 1000;
        if (currentSpeed + diffSpeed < maxSpeed) {
            currentSpeed += diffSpeed;
        } else {
            currentSpeed = maxSpeed;
        }
        if(currentSpeed < minSpeed) {
            currentSpeed = minSpeed;
        }
        return currentSpeed;
    }

    private double angleToRad(double angle) {
        return angle * Math.PI / 180;
    }

    private float getCurrentRad() {
        return (float) angleToRad(actangle);
    }
}