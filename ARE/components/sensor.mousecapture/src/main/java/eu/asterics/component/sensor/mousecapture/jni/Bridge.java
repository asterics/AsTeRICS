
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

package eu.asterics.component.sensor.mousecapture.jni;

import eu.asterics.component.sensor.mousecapture.MouseCaptureInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Java JNI brdige for interfacing C++ code for the mousecapture plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 1, 2011 Time: 3:35:00
 *         PM
 */
public class Bridge extends AbstractBridge {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("syshook");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"syshook.dll\" ... ok!");
    }

    public Bridge(final MouseCaptureInstance.OutputPort mouse_x, final MouseCaptureInstance.OutputPort mouse_y,
            final MouseCaptureInstance.EventTriggerPort eventLButtonPressed,
            final MouseCaptureInstance.EventTriggerPort eventLButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventRButtonPressed,
            final MouseCaptureInstance.EventTriggerPort eventRButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventMButtonPressed,
            final MouseCaptureInstance.EventTriggerPort eventMButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventWheelUp,
            final MouseCaptureInstance.EventTriggerPort eventWheelDown) {
        this.mouseX = mouse_x;
        this.mouseY = mouse_y;
        this.eventLButtonPressed = eventLButtonPressed;
        this.eventLButtonReleased = eventLButtonReleased;
        this.eventRButtonPressed = eventRButtonPressed;
        this.eventRButtonReleased = eventRButtonReleased;
        this.eventMButtonPressed = eventMButtonPressed;
        this.eventMButtonReleased = eventMButtonReleased;
        this.eventWheelUp = eventWheelUp;
        this.eventWheelDown = eventWheelDown;
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

}