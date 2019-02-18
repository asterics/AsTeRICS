
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.*;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

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
    private static final int NAVIGATION_DEBOUNCE_TIME_MS = 100;

    int propInitialX = -1;
    int propInitialY = -1;
    int propSelectTime = 1000;
    int propTooltipStartIndex = 3;
    String propTooltipFolder = "pictures/tooltips";

    // declare member variables here
    private GUI gui;
    double x = 0;
    double y = 0;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture selectionFuture;
    private long lastTooltipNavigationTime = 0;

    /**
     * The class constructor.
     */
    public TooltipInstance() {
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
        if ("deactivateTooltips".equalsIgnoreCase(eventPortID)) {
            return elpDeactivateTooltips;
        }
        if ("nextTooltip".equalsIgnoreCase(eventPortID)) {
            return elpNextTooltip;
        }
        if ("previousTooltip".equalsIgnoreCase(eventPortID)) {
            return elpPreviousTooltip;
        }
        if ("selectTooltip".equalsIgnoreCase(eventPortID)) {
            return elpSelectTooltip;
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
     * @param propertyName
     *            the name of the property
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
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("initialX".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInitialX;
            propInitialX = Integer.parseInt(newValue.toString());
            x = propInitialX;
            return oldValue;
        }
        if ("initialY".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInitialY;
            propInitialY = Integer.parseInt(newValue.toString());
            y = propInitialY;
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
            if (gui != null) {
                gui.repaintTooltip();
            }
        }
    };
    private final IRuntimeInputPort ipY = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            float inputValue = (float) ConversionUtils.doubleFromBytes(data);
            y = inputValue;
            if (gui != null) {
                gui.repaintTooltip();
            }
        }
    };

    final IRuntimeEventListenerPort elpActivateTooltips = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            cancelSelectionTimer(true);
            if (gui != null) {
                lastTooltipNavigationTime = System.currentTimeMillis();
                gui.activateTooltips();
            }
        }
    };

    final IRuntimeEventListenerPort elpDeactivateTooltips = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            cancelSelectionTimer(false);
            if (gui != null && gui.tooltipsActive()) {
                gui.deactivateTooltips();
            }
        }
    };

    final IRuntimeEventListenerPort elpNextTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if (gui != null && gui.tooltipsActive() && System.currentTimeMillis() - lastTooltipNavigationTime > NAVIGATION_DEBOUNCE_TIME_MS) {
                lastTooltipNavigationTime = System.currentTimeMillis();
                cancelSelectionTimer(true);
                gui.navigateNextTooltip();
            }
        }
    };

    final IRuntimeEventListenerPort elpPreviousTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if (gui != null && gui.tooltipsActive() && System.currentTimeMillis() - lastTooltipNavigationTime > NAVIGATION_DEBOUNCE_TIME_MS) {
                lastTooltipNavigationTime = System.currentTimeMillis();
                cancelSelectionTimer(true);
                gui.navigatePreviousTooltip();
            }
        }
    };

    final IRuntimeEventListenerPort elpSelectTooltip = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            if (gui != null && gui.tooltipsActive()) {
                cancelSelectionTimer(false);
                selectTooltipInternal();
            }
        }
    };

    /**
     * Starts/Restarts selection timer for ToolTip. Cancels the timer first, if it is already running.
     * 
     * @param restartTimer
     *            TODO
     */
    private void cancelSelectionTimer(boolean restartTimer) {
        if (selectionFuture != null) {
            AstericsErrorHandling.instance.getLogger().fine("Cancelling Tooltip selection timer");
            selectionFuture.cancel(true);
        }
        if (!restartTimer || propSelectTime == 0) {
            return;
        }

        selectionFuture = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                AstericsErrorHandling.instance.getLogger().fine("Tooltip selection timer finished");
                selectTooltipInternal();
            }
        }, propSelectTime, TimeUnit.MILLISECONDS);
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        gui = new GUI(this);
        x=propInitialX;
        y=propInitialY;
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
     * This method is called by the selection timer upon completion of it. Sends out the filename of the selected tooltip and deactivates the tooltip again.
     */
    private void selectTooltipInternal() {
        if (gui!=null && gui.tooltipsActive()) {
            String tmp = gui.getTooltipFilename();
            if (tmp != null && !tmp.equals("")) {
                //deactivate must be first, in order to re-activate e.g. another Mouse plugin,
                //that performs e.g. double click triggered by sendData().
                gui.deactivateTooltips();
                opTooltip.sendData(ConversionUtils.stringToBytes(tmp));
            }
        }
    }
}