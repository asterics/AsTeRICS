
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

package eu.asterics.component.actuator.event_visualizer;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * EventVisualizerInstance adds a GUI which displays incoming events
 * 
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Jan 09, 2010 Time:
 *         11:23:48 AM
 */
public class EventVisualizerInstance extends AbstractRuntimeComponentInstance {
    private VisualizerGUI visualizerGUI = null;
    public boolean propDisplayGUI = true;

    public EventVisualizerInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("displayGUI".equalsIgnoreCase(propertyName)) {
            return propDisplayGUI;
        }
        return null;
    }

    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("displayGUI".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDisplayGUI;

            if ("true".equalsIgnoreCase((String) newValue)) {
                propDisplayGUI = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propDisplayGUI = false;
            }
            return oldValue;
        }
        return null;
    }

    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        return null;
    }

    final IRuntimeEventListenerPort elpEventDisplay = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if (visualizerGUI != null) {
                try {
                    visualizerGUI.addEvent(data);
                } catch (java.lang.ClassCastException e) {
                    AstericsErrorHandling.instance.getLogger().fine("EventVisualizer: event ignored");

                }
            }

        }
    };

    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("eventDisplay".equalsIgnoreCase(eventPortID)) {
            return elpEventDisplay;
        }

        return null;
    }

    @Override
    public void start() {

        visualizerGUI = new VisualizerGUI(this, AREServices.instance.getAvailableSpace(this));
        if (propDisplayGUI) {
            AREServices.instance.displayPanel(visualizerGUI, this, true);
        }
        super.start();

    }

    @Override
    public void stop() {
        super.stop();

        visualizerGUI.setVisible(false);
    }
}