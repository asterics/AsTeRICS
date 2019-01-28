
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

package eu.asterics.component.actuator.crosshaircursorcontrol;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * The CrosshairCursorControl component allows mouse cursor positioning by software emulation with a limited number of input control channels. A crosshair
 * indicator is displayed on the screen next to the mouse cursor.
 * 
 * 
 * 
 * @author Chris Date: 2019-01-20
 */
public class CrosshairCursorControlInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opTooltip = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort etpClickEvent = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTooltipSelected = new DefaultRuntimeEventTriggererPort();

    boolean propAbsoluteValues = false;
    boolean propAutoColorAxis = true;
    boolean propHighlightClick = true;
    boolean propWrapAround = false;
    int propClickEventTime = 1000;
    int propLineWidth = 200;
    int propAccelerationH = 100;
    int propAccelerationV = 100;
    int propMaxVelocity = 100;
    int propBaseVelocity = 10;
    int propTooltipStartIndex = 3;
    String propTooltipFolder = "pictures/tooltips";

    // declare member variables here
    private GUI gui = null;
    private float x = 0;
    private float y = 0;
    private float lastStableX = -1;
    private float lastStableY = -1;
    private boolean running;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveUp = false;
    private boolean moveDown = false;
    private double currentMoveSpeedH = this.propBaseVelocity;
    private double currentMoveSpeedV = this.propBaseVelocity;
    private long lastMoveTimeH = 0;
    private long lastMoveTimeV = 0;
    int screenWidth = 0;
    int screenHeight = 0;
    double actAccel = 1.0;

    volatile long elapsedIdleTime = Long.MAX_VALUE;
    volatile long lastInputValue = Long.MAX_VALUE;

    /**
     * The class constructor.
     */
    public CrosshairCursorControlInstance() {
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
        if ("x".equalsIgnoreCase(portID)) {
            return ipX;
        }
        if ("y".equalsIgnoreCase(portID)) {
            return ipY;
        }
        if ("accelerationH".equalsIgnoreCase(portID)) {
            return ipAccelerationH;
        }
        if ("accelerationV".equalsIgnoreCase(portID)) {
            return ipAccelerationV;
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

        if ("tooltip".equalsIgnoreCase(portID)) {
            return opTooltip;
        }
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
        if ("activateTooltips".equalsIgnoreCase(eventPortID)) {
            return elpActivateTooltips;
        }
        if ("nextTooltip".equalsIgnoreCase(eventPortID)) {
            return elpNextTooltip;
        }
        if ("previousTooltip".equalsIgnoreCase(eventPortID)) {
            return elpPreviousTooltip;
        }
        if ("startMoveLeft".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveLeft;
        }
        if ("startMoveRight".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveRight;
        }
        if ("startMoveUp".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveUp;
        }
        if ("startMoveDown".equalsIgnoreCase(eventPortID)) {
            return elpStartMoveDown;
        }
        if ("stopMoveLeft".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveLeft;
        }
        if ("stopMoveRight".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveRight;
        }
        if ("stopMoveUp".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveUp;
        }
        if ("stopMoveDown".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveDown;
        }
        if ("stopMoveAll".equalsIgnoreCase(eventPortID)) {
            return elpStopMoveAll;
        }
        if ("moveToLastStable".equalsIgnoreCase(eventPortID)) {
            return elpMoveToLastStable;
        }
        if ("highlightXAxis".equalsIgnoreCase(eventPortID)) {
            return elpHighlightXAxis;
        }
        if ("highlightYAxis".equalsIgnoreCase(eventPortID)) {
            return elpHighlightYAxis;
        }
        if ("toggleAxisHighlight".equalsIgnoreCase(eventPortID)) {
            return elpToggleAxisHighlight;
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

        if ("tooltipSelected".equalsIgnoreCase(eventPortID)) {
            return etpTooltipSelected;
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
        if ("absoluteValues".equalsIgnoreCase(propertyName)) {
            return propAbsoluteValues;
        }
        if ("autoColorAxis".equalsIgnoreCase(propertyName)) {
            return propAutoColorAxis;
        }
        if ("highlightClick".equalsIgnoreCase(propertyName)) {
            return propHighlightClick;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            return propWrapAround;
        }
        if ("clickEventTime".equalsIgnoreCase(propertyName)) {
            return propClickEventTime;
        }
        if ("lineWidth".equalsIgnoreCase(propertyName)) {
            return propLineWidth;
        }
        if ("accelerationH".equalsIgnoreCase(propertyName)) {
            return propAccelerationH;
        }
        if ("accelerationV".equalsIgnoreCase(propertyName)) {
            return propAccelerationV;
        }
        if ("maxVelocity".equalsIgnoreCase(propertyName)) {
            return propMaxVelocity;
        }
        if ("baseVelocity".equalsIgnoreCase(propertyName)) {
            return propBaseVelocity;
        }
        if ("tooltipFolder".equalsIgnoreCase(propertyName)) {
            return propTooltipFolder;
        }
        if ("tooltipStartIndex".equalsIgnoreCase(propertyName)) {
            return propTooltipStartIndex;
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
        if ("absoluteValues".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAbsoluteValues;
            propAbsoluteValues = Boolean.parseBoolean((String) newValue);
            return oldValue;
        }
        if ("autoColorAxis".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoColorAxis;
            propAutoColorAxis = Boolean.parseBoolean((String) newValue);
            return oldValue;
        }
        if ("highlightClick".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propHighlightClick;
            propHighlightClick = Boolean.parseBoolean((String) newValue);
            return oldValue;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWrapAround;
            propWrapAround = Boolean.parseBoolean((String) newValue);
            return oldValue;
        }
        if ("clickEventTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propClickEventTime;
            propClickEventTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("lineWidth".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propLineWidth;
            propLineWidth = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("accelerationH".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAccelerationH;
            propAccelerationH = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("accelerationV".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAccelerationV;
            propAccelerationV = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("maxVelocity".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxVelocity;
            propMaxVelocity = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("baseVelocity".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBaseVelocity;
            propBaseVelocity = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("tooltipFolder".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propTooltipFolder;
            propTooltipFolder = (String) newValue;
            return oldValue;
        }
        if ("tooltipStartIndex".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propTooltipStartIndex;
            propTooltipStartIndex = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipX = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            float inputValue = (float) ConversionUtils.doubleFromBytes(data);
            elapsedIdleTime = System.currentTimeMillis();
            if (!gui.tooltipsActive()) {
                if (propAbsoluteValues == true) {
                    x = inputValue;
                } else {
                    x += inputValue;
                }
                x = normalizeValue(x, 0, screenWidth, propWrapAround);
                lastMoveTimeH = System.currentTimeMillis();
                gui.setCursor(x, y);
            } else
                gui.navigateTooltips(inputValue);
        }
    };
    private final IRuntimeInputPort ipY = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            float inputValue = (float) ConversionUtils.doubleFromBytes(data);
            elapsedIdleTime = System.currentTimeMillis();
            if (!gui.tooltipsActive()) {
                if (propAbsoluteValues == true) {
                    y = inputValue;
                } else {
                    y += inputValue;
                }
                lastMoveTimeV = System.currentTimeMillis();
                y = normalizeValue(y, 0, screenHeight, propWrapAround);
                gui.setCursor(x, y);
            } else
                gui.navigateTooltips(inputValue);
        }
    };

    private final IRuntimeInputPort ipAccelerationH = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            propAccelerationH = ConversionUtils.intFromBytes(data);
            elapsedIdleTime = System.currentTimeMillis();
        }
    };

    private final IRuntimeInputPort ipAccelerationV = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            propAccelerationV = ConversionUtils.intFromBytes(data);
            elapsedIdleTime = System.currentTimeMillis();
        }
    };

    final IRuntimeEventListenerPort elpActivateTooltips = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime = System.currentTimeMillis();
            gui.activateTooltips(propTooltipFolder, propTooltipStartIndex);
        }
    };

    final IRuntimeEventListenerPort elpNextTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) {
                elapsedIdleTime = System.currentTimeMillis();
                gui.navigateNextTooltip();
            }
        }
    };

    final IRuntimeEventListenerPort elpPreviousTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) {
                elapsedIdleTime = System.currentTimeMillis();
                gui.navigatePreviousTooltip();;
            }
        }
    };

    final IRuntimeEventListenerPort elpStartMoveLeft = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) return;
            lastMoveTimeH = System.currentTimeMillis();
            moveLeft = true;
            moveRight = false;
        }
    };

    final IRuntimeEventListenerPort elpStartMoveRight = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) return;
            lastMoveTimeH = System.currentTimeMillis();
            moveRight = true;
            moveLeft = false;
        }
    };

    final IRuntimeEventListenerPort elpStartMoveUp = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) return;
            lastMoveTimeV = System.currentTimeMillis();
            moveUp = true;
            moveDown = false;
        }
    };

    final IRuntimeEventListenerPort elpStartMoveDown = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(gui.tooltipsActive()) return;
            lastMoveTimeV = System.currentTimeMillis();
            moveDown = true;
            moveUp = false;
        }
    };

    final IRuntimeEventListenerPort elpStopMoveLeft = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(propAutoColorAxis) gui.resetAxis();
            moveLeft = false;
            currentMoveSpeedH = propBaseVelocity;
            lastStableX = x;
        }
    };

    final IRuntimeEventListenerPort elpStopMoveRight = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(propAutoColorAxis) gui.resetAxis();
            moveRight = false;
            currentMoveSpeedH = propBaseVelocity;
            lastStableX = x;
        }
    };

    final IRuntimeEventListenerPort elpStopMoveUp = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(propAutoColorAxis) gui.resetAxis();
            moveUp = false;
            currentMoveSpeedV = propBaseVelocity;
            lastStableY = y;
        }
    };

    final IRuntimeEventListenerPort elpStopMoveDown = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(propAutoColorAxis) gui.resetAxis();
            moveDown = false;
            currentMoveSpeedV = propBaseVelocity;
            lastStableY = y;
        }
    };

    final IRuntimeEventListenerPort elpStopMoveAll = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if(propAutoColorAxis) gui.resetAxis();
            moveLeft = false;
            moveRight = false;
            moveUp = false;
            moveDown = false;
            currentMoveSpeedH = propBaseVelocity;
            currentMoveSpeedV = propBaseVelocity;
            lastStableX = x;
            lastStableY = y;
        }
    };

    final IRuntimeEventListenerPort elpMoveToLastStable = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            x = lastStableX != -1 ? lastStableX : x;
            y = lastStableY != -1 ? lastStableY : y;
            gui.setCursor(x,y);
        }
    };

    final IRuntimeEventListenerPort elpHighlightXAxis = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime=System.currentTimeMillis();
            propAutoColorAxis = false;
            if (!gui.tooltipsActive()) {
                gui.setXAxisHighlight(true);
                gui.setYAxisHighlight(false);
            }
            gui.setOnTop();
        }
    };

    final IRuntimeEventListenerPort elpHighlightYAxis = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime=System.currentTimeMillis();
            propAutoColorAxis = false;
            if (!gui.tooltipsActive()) {
                gui.setYAxisHighlight(true);
                gui.setXAxisHighlight(false);
            }
            gui.setOnTop();
        }
    };

    final IRuntimeEventListenerPort elpToggleAxisHighlight = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            elapsedIdleTime=System.currentTimeMillis();
            propAutoColorAxis = false;
            if (!gui.tooltipsActive()) {
                gui.toggleAxis();
            }
            gui.setOnTop();
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        Dimension screenSize = new Dimension(width, height);

        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();
        // System.out.println("Screen width:" + screenWidth + " height:" + screenHeight);
        gui = new GUI(this, screenSize, propLineWidth);
        Point location = MouseInfo.getPointerInfo().getLocation();
        x = location.x;
        y = location.y;

        super.start();

        elapsedIdleTime = Long.MAX_VALUE;
        running = true;

        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        long currentTime = System.currentTimeMillis();
                        Thread.sleep(20);
                        if ((currentTime - elapsedIdleTime) > propClickEventTime) {
                            if (gui.tooltipsActive()) {
                                String tmp = gui.getTooltipFilename();
                                if (!tmp.equals("")) {
                                    opTooltip.sendData(ConversionUtils.stringToBytes(tmp));
                                    gui.deactivateTooltips();
                                }
                                etpTooltipSelected.raiseEvent();
                                gui.setOnTop();
                                if(propAutoColorAxis) gui.resetAxis();
                                elapsedIdleTime = Long.MAX_VALUE;
                            } else {
                                // gui.hideCrosshair();
                                gui.setCursor(x, y); // update cursor position (prevent JavaRobot positioning error when quickly updated)
                                if(propHighlightClick) gui.doAxisClickHighlight();
                                etpClickEvent.raiseEvent();
                                // Thread.sleep(200);
                                // gui.showCrosshair();
                                gui.setOnTop();
                                if(propAutoColorAxis) gui.resetAxis();
                                elapsedIdleTime = Long.MAX_VALUE;
                            }
                        } else if(!gui.tooltipsActive()) {
                            doMove();
                        }

                        if(propAutoColorAxis) {
                            gui.setYAxisHighlight(currentTime - lastMoveTimeH < 50);
                            gui.setXAxisHighlight(currentTime - lastMoveTimeV < 50);
                        }

                        if (((System.currentTimeMillis() - elapsedIdleTime) < 200) && (elapsedIdleTime != Long.MAX_VALUE)) {
                            actAccel += 0.001 * (double) propAccelerationH;
                            if (actAccel > propMaxVelocity)
                                actAccel = propMaxVelocity;
                            // System.out.println("Accel="+actAccel);
                        } else {
                            actAccel = 1.0;
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
                // now the cleanup of the window can be done at any time in the event dispatch thread wihtout interfering the other code.
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
        if(!this.moveLeft && !this.moveRight && !this.moveUp && !this.moveDown) {
            return;
        }
        elapsedIdleTime = System.currentTimeMillis();

        if ((this.moveLeft || this.moveRight)) {
            long diffTime = System.currentTimeMillis() - this.lastMoveTimeH;
            float diffPx = (float) currentMoveSpeedH * diffTime / 1000;
            this.lastMoveTimeH = System.currentTimeMillis();
            if (this.moveLeft) {
                this.x -= diffPx;
            } else if (this.moveRight) {
                this.x += diffPx;
            }

            float diffSpeed = (float) propAccelerationH * diffTime / 1000;
            if (this.currentMoveSpeedH + diffSpeed < propMaxVelocity) {
                this.currentMoveSpeedH += diffSpeed;
            } else {
                this.currentMoveSpeedH = propMaxVelocity;
            }
            if(this.currentMoveSpeedH < propBaseVelocity) {
                this.currentMoveSpeedH = propBaseVelocity;
            }
        }

        if ((this.moveUp || this.moveDown)) {
            long diffTime = System.currentTimeMillis() - this.lastMoveTimeV;
            float diffPx = (float) currentMoveSpeedV * diffTime / 1000;
            this.lastMoveTimeV = System.currentTimeMillis();
            if (this.moveUp) {
                this.y -= diffPx;
            } else if (this.moveDown) {
                this.y += diffPx;
            }

            float diffSpeed = (float) propAccelerationV * diffTime / 1000;
            if (this.currentMoveSpeedV + diffSpeed < propMaxVelocity) {
                this.currentMoveSpeedV += diffSpeed;
            } else {
                this.currentMoveSpeedV = propMaxVelocity;
            }
            if(this.currentMoveSpeedV < propBaseVelocity) {
                this.currentMoveSpeedV = propBaseVelocity;
            }
        }

        this.x = normalizeValue(x, 0, screenWidth, propWrapAround);
        this.y = normalizeValue(y, 0, screenHeight, propWrapAround);
        gui.setCursor(x, y);
    }

    /**
     * normalizes the given value to a given range.
     * @param value the value to normalize
     * @param minValue
     * @param maxValue
     * @return
     */
    private float normalizeValue(float value, float minValue, float maxValue, boolean wrapAround) {
        if (value < minValue) {
            return wrapAround ? maxValue : minValue;
        } else if (value > maxValue) {
            return wrapAround ? minValue : maxValue;
        }
        return value;
    }
}