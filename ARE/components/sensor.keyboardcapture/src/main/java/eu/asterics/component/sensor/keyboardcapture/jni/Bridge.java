
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

package eu.asterics.component.sensor.keyboardcapture.jni;

import eu.asterics.component.sensor.keyboardcapture.KeyboardCaptureInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Java JNI bridge for interfacing C++ code for the keyboardcapture plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 1, 2011 Time: 3:35:00
 *         PM
 */
public class Bridge {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("syshook");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"syshook.dll\" for KeyboardCapture... ok!");

    }

    private final KeyboardCaptureInstance owner;

    public Bridge(final KeyboardCaptureInstance owner) {
        this.owner = owner;
    }

    /**
     * Activates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int activate();

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int deactivate();

    /**
     * Gets the value of the named property.
     *
     * @param key
     *            the name of the property to be accessed
     * @return the value of the named property
     */
    native public String getProperty(String key);

    /**
     * Sets the named property to the defined value.
     *
     * @param key
     *            the name of the property to be accessed
     * @param value
     *            the value to be assigned to the named property
     * @return the value previously assigned to the named property
     */
    native public String setProperty(String key, final String value);

    /**
     * This method is called back from the native code on demand to signify an
     * internal error. The first argument corresponds to an error code and the
     * second argument corresponds to a textual description of the error.
     *
     * @param errorCode
     *            an error code
     * @param message
     *            a textual description of the error
     */
    private void errorReport_callback(final int errorCode, final String message) {
        AstericsErrorHandling.instance.getLogger().fine(errorCode + ": " + message);
    }

    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the key press state and the keycode
     *
     * @param pressState
     *            (boolean)
     * @param keyCode
     *            - virtual key code (range is [0, Int.MAX_VALUE])
     */
    private void newKey_callback(final boolean pressState, final int keyCode) {
        // AstericsErrorHandling.instance.getLogger().fine("Java: Callback [(" +
        // pressState + ", " + keyCode + ")");
        owner.processKeyboardInput(pressState, keyCode);
    }

}