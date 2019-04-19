package eu.asterics.component.sensor.mousecapture.jni;

import eu.asterics.component.sensor.mousecapture.MouseCaptureInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

public abstract class AbstractBridge {

    private int oldButtonstate = 0;
    protected MouseCaptureInstance.OutputPort mouseX;
    protected MouseCaptureInstance.OutputPort mouseY;
    protected MouseCaptureInstance.EventTriggerPort eventLButtonPressed;
    protected MouseCaptureInstance.EventTriggerPort eventLButtonReleased;
    protected MouseCaptureInstance.EventTriggerPort eventRButtonPressed;
    protected MouseCaptureInstance.EventTriggerPort eventRButtonReleased;
    protected MouseCaptureInstance.EventTriggerPort eventMButtonPressed;
    protected MouseCaptureInstance.EventTriggerPort eventMButtonReleased;
    protected MouseCaptureInstance.EventTriggerPort eventWheelUp;
    protected MouseCaptureInstance.EventTriggerPort eventWheelDown;

    public AbstractBridge() {
        super();
    }
    
    /**
     * Activates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    abstract public int activate();

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    abstract public int deactivate();

    /**
     * Gets the value of the named property.
     *
     * @param key
     *            the name of the property to be accessed
     * @return the value of the named property
     */
    abstract public String getProperty(String key);

    /**
     * Sets the named property to the defined value.
     *
     * @param key
     *            the name of the property to be accessed
     * @param value
     *            the value to be assigned to the named property
     * @return the value previously assigned to the named property
     */
    abstract public String setProperty(String key, final String value);    

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
    protected void errorReport_callback(final int errorCode, final String message) {
        AstericsErrorHandling.instance.getLogger().fine(errorCode + ": " + message);
    }

    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the x and y movement of the mouse
     *
     * @param x_value
     *            the x movement (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     * @param a_value
     *            the a movement (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     */
    protected void newCoordinates_callback(final int x_value, final int y_value) {
        mouseX.sendData(x_value);
        mouseY.sendData(y_value);
    
    }

    /**
     * This method is called back from the native code on demand. The passed
     * argument holds the left mouse button state in bit 0, the right mouse
     * button state in bit 1 and the middle mouse button state in bit 2
     *
     * @param buttonstate
     *            left/right/middle mouse button in bit 0/1/2 (range is
     *            [Int.MIN_VALUE, Int.MAX_VALUE])
     */
    protected void newButtons_callback(final int buttonstate) {
        System.out.println("Java: Callback buttonstate = " + Integer.toBinaryString(buttonstate));
    
        if (((oldButtonstate & 1) == 0) && ((buttonstate & 1) != 0)) {
            eventLButtonPressed.raiseEvent();
        } else if (((oldButtonstate & 1) != 0) && ((buttonstate & 1) == 0)) {
            eventLButtonReleased.raiseEvent();
        }
    
        if (((oldButtonstate & 2) == 0) && ((buttonstate & 2) != 0)) {
            eventRButtonPressed.raiseEvent();
        } else if (((oldButtonstate & 2) != 0) && ((buttonstate & 2) == 0)) {
            eventRButtonReleased.raiseEvent();
        }
    
        if (((oldButtonstate & 4) == 0) && ((buttonstate & 4) != 0)) {
            eventMButtonPressed.raiseEvent();
        } else if (((oldButtonstate & 4) != 0) && ((buttonstate & 4) == 0)) {
            eventMButtonReleased.raiseEvent();
        }
    
        oldButtonstate = buttonstate;
    
    }

    /**
     * This method is called back from the native code on demand. The passed
     * argument holds the mouse wheel movement (1 = wheel up, -1 = wheel down)
     *
     * @param wheel
     *            int 1/-1 (range is [-1, 1])
     */
    protected void newWheel_callback(final int wheel) {
        if (wheel > 0) {
            eventWheelUp.raiseEvent();
        } else {
            eventWheelDown.raiseEvent();
        }
    }

}