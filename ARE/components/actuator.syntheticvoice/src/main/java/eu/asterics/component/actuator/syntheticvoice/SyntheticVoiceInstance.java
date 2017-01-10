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

package eu.asterics.component.actuator.syntheticvoice;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

/**
 * Implements the Synthetic Voice plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Aug 20, 2010 Time: 10:22:08
 *         AM
 */
public class SyntheticVoiceInstance extends AbstractRuntimeComponentInstance {

    private final String IP_INPUT = "input";
    private final String PROP_VOLUME = "volume";
    private final String PROP_SPEED = "speed";
    private final String PROP_VOICE = "voice";
    private final String PROP_XML_TAGS = "xmlTags";

    private IRuntimeInputPort ipInput = new InputPort();
    private final SyntheticVoiceBridge bridge = new SyntheticVoiceBridge(this);

    /**
     * The class constructor.
     */
    public SyntheticVoiceInstance() {
    }

    /**
     * Called when model is started.
     */
    @Override
    public void start() {
        bridge.start();
        super.start();
    }

    /**
     * Called when model is paused.
     */
    @Override
    public void pause() {
        bridge.stop();
        super.pause();
    }

    /**
     * Called when model is resumed.
     */
    @Override
    public void resume() {
        bridge.start();
        super.resume();
    }

    /**
     * Called when model is stopped.
     */
    @Override
    public void stop() {
        bridge.stop();
        super.stop();
    }

    /**
     * Returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if (IP_INPUT.equalsIgnoreCase(portID)) {
            return ipInput;
        } else {
            return null;
        }
    }

    /**
     * Returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        return null;
    }

    /**
     * Returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if (PROP_VOLUME.equalsIgnoreCase(propertyName)) {
            return bridge.getVolume();
        } else if (PROP_SPEED.equalsIgnoreCase(propertyName)) {
            return bridge.getSpeed();
        } else if (PROP_VOICE.equalsIgnoreCase(propertyName)) {
            return bridge.getVoice();
        } else if (PROP_XML_TAGS.equalsIgnoreCase(propertyName)) {
            return bridge.getXmlTags();
        }
        return null;
    }

    /**
     * Sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value
     * @return old property value
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if (PROP_VOLUME.equalsIgnoreCase(propertyName)) {
            int oldValue = bridge.getVolume();
            int newIntValue;
            try {
                newIntValue = Integer.parseInt((String) newValue);
            } catch (NumberFormatException ex) {
                return null;
            }

            bridge.setVolume(newIntValue);

            return oldValue;
        } else if (PROP_SPEED.equalsIgnoreCase(propertyName)) {
            int oldValue = bridge.getSpeed();
            int newIntValue;
            try {
                newIntValue = Integer.parseInt((String) newValue);
            } catch (NumberFormatException ex) {
                return null;
            }

            bridge.setSpeed(newIntValue);

            return oldValue;
        } else if (PROP_VOICE.equalsIgnoreCase(propertyName)) {
            final Object oldValue = bridge.getVoice();
            bridge.setVoice((String) newValue);
            return oldValue;
        } else if (PROP_XML_TAGS.equalsIgnoreCase(propertyName)) {
            final Object oldValue = bridge.getXmlTags();
            if ("true".equalsIgnoreCase((String) newValue)) {
                bridge.setXmlTags(true);
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                bridge.setXmlTags(false);
            }

            return oldValue;
        }

        return null;
    }

    /**
     * Defines input port
     */
    private class InputPort extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            String text = ConversionUtils.stringFromBytes(data);

            bridge.speechText(text);
        }
    }
}