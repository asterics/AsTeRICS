
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

package eu.asterics.component.sensor.joystickcapture.jni;

import eu.asterics.component.sensor.joystickcapture.JoystickCaptureInstance;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Java JNI bridge for interfacing C++ code for the joystickcapture plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Oct 25, 2011 Time:
 *         8:35:00 PM
 */
public class Bridge {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("joyhook");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"joyhook.dll\" for JoystickCapture... ok!");

    }

    private final JoystickCaptureInstance owner;
    private int buttonstate = 0;

    public Bridge(final JoystickCaptureInstance owner) {
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
     * This method is called back from the native code on activation of the
     * bridge. arguments specify the joystick cabilities
     *
     * @param axis
     *            (integer)
     * @param buttons
     *            (integer)
     * @param pov
     *            (integer)
     * @param min-x
     *            axis (integer)
     * @param max-x
     *            axis (integer)
     * @param min-y
     *            axis (integer)
     * @param max-y
     *            axis (integer)
     * @param min-z
     *            axis (integer)
     * @param max-z
     *            axis (integer)
     * @param min-r
     *            axis (integer)
     * @param max-r
     *            axis (integer)
     * @param min-u
     *            axis (integer)
     * @param max-u
     *            axis (integer)
     * @param min-v
     *            axis (integer)
     * @param max-v
     *            axis (integer)
     */
    private void capabilities_callback(final int axis, final int buttons, final int pov, final int xmin, final int xmax,
            final int ymin, final int ymax, final int zmin, final int zmax, final int rmin, final int rmax,
            final int umin, final int umax, final int vmin, final int vmax) {
        // System.out.println("C++ capabilities callback: Axis="+axis+",
        // Buttons="+buttons+",xmin="+xmin+",xmax="+xmax);
        owner.axis = axis;
        owner.buttons = buttons;
        owner.hasPov = pov;

    }

    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the current joystick values
     *
     * @param x
     *            axis (integer)
     * @param y
     *            axis (integer)
     * @param z
     *            axis (integer)
     * @param r
     *            axis (integer)
     * @param u
     *            axis (integer)
     * @param v
     *            axis (integer)
     * @param buttons
     *            (integer)
     * @param pov
     *            value (integer)
     */
    private void newValues_callback(int x, int y, int z, int r, int u, int v, int buttons, int pov) {
        // AstericsErrorHandling.instance.getLogger().fine("Java: Callback [(" +
        // pressState + ", " + keyCode + ")");
        // System.out.println("C++ values: x="+x+" y="+y+" z="+z+" r="+r+"
        // u="+u+" v="+v);

        x >>= 8;
        y >>= 8;
        z >>= 8;
        r >>= 8;
        u >>= 8;
        v >>= 8;
        switch (pov) {
        case 0:
            break;
        case 4500:
            pov = 1;
            break;
        case 9000:
            pov = 2;
            break;
        case 13500:
            pov = 3;
            break;
        case 18000:
            pov = 4;
            break;
        case 22500:
            pov = 5;
            break;
        case 27000:
            pov = 6;
            break;
        case 31500:
            pov = 7;
            break;
        default:
            pov = 8;
            break;
        }
        if (owner.axis > 0) {
            owner.opX.sendData(ConversionUtils.intToBytes(x));
        }
        if (owner.axis > 1) {
            owner.opY.sendData(ConversionUtils.intToBytes(y));
        }
        if (owner.axis > 2) {
            owner.opZ.sendData(ConversionUtils.intToBytes(z));
        }
        if (owner.axis > 3) {
            owner.opR.sendData(ConversionUtils.intToBytes(r));
        }
        if (owner.axis > 4) {
            owner.opU.sendData(ConversionUtils.intToBytes(u));
        }
        if (owner.axis > 5) {
            owner.opV.sendData(ConversionUtils.intToBytes(v));
        }
        if (owner.hasPov != 0) {
            owner.opPov.sendData(ConversionUtils.intToBytes(pov));
        }

        if (buttonstate != buttons) {
            int i;

            for (i = 0; i < owner.NUMBER_OF_BUTTONS; i++) {
                if (((buttonstate & (1 << i)) == 0) && ((buttons & (1 << i)) != 0)) {
                    owner.etpPressedButton[i].raiseEvent();
                }
                if (((buttonstate & (1 << i)) != 0) && ((buttons & (1 << i)) == 0)) {
                    owner.etpReleasedButton[i].raiseEvent();
                }
            }
            buttonstate = buttons;
        }

    }

}