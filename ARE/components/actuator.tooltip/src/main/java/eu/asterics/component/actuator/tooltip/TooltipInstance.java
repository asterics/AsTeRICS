
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

package eu.asterics.component.actuator.tooltip;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.*;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsThreadPool;

import javax.swing.*;
import java.awt.*;

/**
 * The CrosshairCursorControl component allows mouse cursor positioning by software emulation with a limited number of input control channels. A crosshair
 * indicator is displayed on the screen next to the mouse cursor.
 *
 * @author Benjamin Klaus
 */
public class TooltipInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opTooltip = new DefaultRuntimeOutputPort();
    final IRuntimeEventTriggererPort etpTooltipActivated = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTooltipDeactivated = new DefaultRuntimeEventTriggererPort();

    int propInitialX = 0;
    int propInitialY = 0;
    int propSelectTime = 1000;
    int propTooltipStartIndex = 3;
    String propTooltipFolder = "pictures/tooltips";

    // declare member variables here
    private GUI gui;
    private float x = 0;
    private float y = 0;
    private long lastActionTime = 0;
    private boolean running = false;

    /**
     * The class constructor.
     */
    public TooltipInstance() {
        // empty constructor
    }

    /**
     * returns an Input Port.
     *
     * @param portID the name of the port
     * @return the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID) {
        if ("x".equalsIgnoreCase(portID)) {
            return ipX;
        }
        if ("y".equalsIgnoreCase(portID)) {
            return ipY;
        }

        return null;
    }

    /**
     * returns an Output Port.
     *
     * @param portID the name of the port
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
     * @param eventPortID the name of the port
     * @return the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("activateTooltips".equalsIgnoreCase(eventPortID)) {
            return elpActivateTooltips;
        }
        if ("deactivateTooltips".equalsIgnoreCase(eventPortID)) {
            return elpDeactivateTooltips;
        }
        if ("nextTooltip".equalsIgnoreCase(eventPortID)) {
            return elpNextTooltip;
        }
        if ("previousTooltip".equalsIgnoreCase(eventPortID)) {
            return elpPreviousTooltip;
        }
        return null;
    }

    /**
     * returns an Event Triggerer Port.
     *
     * @param eventPortID the name of the port
     * @return the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("tooltipActivated".equalsIgnoreCase(eventPortID)) {
            return etpTooltipActivated;
        }
        if ("tooltipDeactivated".equalsIgnoreCase(eventPortID)) {
            return etpTooltipDeactivated;
        }
        return null;
    }

    /**
     * returns the value of the given property.
     *
     * @param propertyName the name of the property
     * @return the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName) {
        if ("initialX".equalsIgnoreCase(propertyName)) {
            return propInitialX;
        }
        if ("initialY".equalsIgnoreCase(propertyName)) {
            return propInitialY;
        }
        if ("selectTime".equalsIgnoreCase(propertyName)) {
            return propSelectTime;
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
     * @param propertyName the name of the property
     * @param newValue     the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("initialX".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInitialX;
            propInitialX = Integer.parseInt(newValue.toString());
            x = propInitialX;
            if (gui != null) gui.setMouseXY(x, y);
            return oldValue;
        }
        if ("initialY".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInitialY;
            propInitialY = Integer.parseInt(newValue.toString());
            y = propInitialY;
            if (gui != null) gui.setMouseXY(x, y);
            return oldValue;
        }
        if ("selectTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSelectTime;
            propSelectTime = Integer.parseInt(newValue.toString());
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
            x = inputValue;
            gui.setMouseXY(x, y);
        }
    };
    private final IRuntimeInputPort ipY = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            float inputValue = (float) ConversionUtils.doubleFromBytes(data);
            y = inputValue;
            gui.setMouseXY(x, y);
        }
    };

    final IRuntimeEventListenerPort elpActivateTooltips = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lastActionTime = System.currentTimeMillis();
            if (!gui.tooltipsActive()) {
                gui.activateTooltips(propTooltipFolder, propTooltipStartIndex);
                etpTooltipActivated.raiseEvent();
            }
        }
    };

    final IRuntimeEventListenerPort elpDeactivateTooltips = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lastActionTime = System.currentTimeMillis();
            if (gui.tooltipsActive()) {
                gui.deactivateTooltips();
                etpTooltipDeactivated.raiseEvent();
            }
        }
    };

    final IRuntimeEventListenerPort elpNextTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if (gui.tooltipsActive()) {
                lastActionTime = System.currentTimeMillis();
                gui.navigateNextTooltip();
                if (!gui.tooltipsActive()) etpTooltipDeactivated.raiseEvent();
            }
        }
    };

    final IRuntimeEventListenerPort elpPreviousTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if (gui.tooltipsActive()) {
                lastActionTime = System.currentTimeMillis();
                gui.navigatePreviousTooltip();
                if (!gui.tooltipsActive()) etpTooltipDeactivated.raiseEvent();
            }
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        gui = new GUI(this);
        gui.setMouseXY(x, y);

        super.start();
        running = true;

        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    sleepInternal(20);
                    if (gui.tooltipsActive() && (System.currentTimeMillis() - lastActionTime) > propSelectTime) {
                        String tmp = gui.getTooltipFilename();
                        if (!tmp.equals("")) {
                            opTooltip.sendData(ConversionUtils.stringToBytes(tmp));
                            gui.deactivateTooltips();
                        }
                        etpTooltipDeactivated.raiseEvent();
                        gui.setOnTop();
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

    private void sleepInternal(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}