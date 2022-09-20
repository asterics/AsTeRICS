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

import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Interfaces the native SyntheticVoiceBridge.dll library for the Synthetic
 * Voice plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Aug 20, 2010 Time: 10:22:08
 *         AM
 */
public class SyntheticVoiceBridge {
    private final int voiceNotFoundWarning = -101;
    private final int libraryInitializeWarning = -102;
    private int propVolume = 0;
    private int propSpeed = 0;
    private String propVoice = "";
    private boolean propXmlTags = false;
    private final IRuntimeComponentInstance componentInstance;
    private boolean active = false;

    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("SyntheticVoiceBridge");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"SyntheticVoiceBridge.dll\" ... ok!");
    }

    /**
     * The class constructor.
     * 
     * @param componentInstance
     *            object of the SyntheticVoiceInstance class.
     */
    public SyntheticVoiceBridge(final IRuntimeComponentInstance componentInstance) {
        this.componentInstance = componentInstance;
    }

    /**
     * Gets the volume of the voice.
     * 
     * @return volume of the voice
     */
    int getVolume() {
        return propVolume;
    }

    /**
     * Sets the volume of the voice.
     * 
     * @param volume
     *            new voice volume
     */
    void setVolume(int volume) {
        propVolume = volume;
    }

    /**
     * Gets the speed of the voice.
     * 
     * @return speed of the voice
     */
    int getSpeed() {
        return propSpeed;
    }

    /**
     * Sets the speed of the voice.
     * 
     * @param speed
     *            new voice speed
     */
    void setSpeed(int speed) {
        propSpeed = speed;
    }

    /**
     * Gets the name of the voice.
     * 
     * @return name of the voice
     */
    String getVoice() {
        return propVoice;
    }

    /**
     * Sets the name of the voice.
     * 
     * @param voice
     *            new voice name
     */
    void setVoice(String voice) {
        propVoice = voice;
    }

    /**
     * Gets the support for XML tags
     * 
     * @return support for XML tags
     */
    boolean getXmlTags() {
        return propXmlTags;
    }

    /**
     * Sets the support for XML tags
     * 
     * @param xmlTags
     *            support for XML tags.
     */
    void setXmlTags(boolean xmlTags) {
        propXmlTags = xmlTags;
    }

    /**
     * Activates the library
     */
    public void start() {
        int result = activate(propVolume, propSpeed, propVoice, propXmlTags);

        if (result < 0) {
            if (result == voiceNotFoundWarning) {
                AstericsErrorHandling.instance.getLogger()
                        .warning("Synthetic Voice: user voice not found, the default voice will be used");
                active = true;
            } else if (result == libraryInitializeWarning) {
                AstericsErrorHandling.instance.reportInfo(componentInstance,
                        "Synthetic Voice start warning, the plugin continues");
                active = true;
            } else {
                AstericsErrorHandling.instance.reportError(componentInstance,
                        "The Synthetic Voice plugin coudl not start! Error code " + Integer.toString(result));
            }
        } else {
            active = true;
        }

    }

    /**
     * Deactivates the library
     */
    public void stop() {
        int result = deactivate();

        if (result < 0) {
            AstericsErrorHandling.instance.reportInfo(componentInstance,
                    "Synthetic Voice deactivation Error! " + Integer.toString(result));
        } else {
            active = false;
        }
    }

    /**
     * Sends text to speak
     * 
     * @param text
     *            text to speak
     */
    public void speechText(String text) {
        if (active) {
            int result = speech(text);

            if (result < 0) {
                AstericsErrorHandling.instance.reportInfo(componentInstance,
                        "Synthetic Voice speech Error! " + Integer.toString(result));
            }
        }
    }

    /**
     * Activates the underlying native code/hardware.
     * 
     * @param volume
     *            volume of the voice
     * @param speed
     *            speed of the voice
     * @param voice
     *            voice name
     * @param xmlTags
     *            support for the XML tags
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int activate(int volume, int speed, String voice, boolean xmlTags);

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int deactivate();

    /**
     * Speech the text.
     * 
     * @param text
     *            text to speak
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int speech(String text);

}