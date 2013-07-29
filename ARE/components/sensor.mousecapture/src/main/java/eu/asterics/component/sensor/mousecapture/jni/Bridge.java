
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.mousecapture.jni;

import eu.asterics.component.sensor.mousecapture.MouseCaptureInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Java JNI brdige for interfacing C++ code for the mousecapture plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 1, 2011
 *         Time: 3:35:00 PM
 */
public class Bridge
{ 
    /** 
     * Statically load the native library
     */
    static   
    {  
        System.loadLibrary("syshook");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"syshook.dll\" ... ok!");
    }
 
    private int oldButtonstate=0;

    private final MouseCaptureInstance.OutputPort mouseX;
    private final MouseCaptureInstance.OutputPort mouseY;
    private final MouseCaptureInstance.EventTriggerPort eventLButtonPressed;    
    private final MouseCaptureInstance.EventTriggerPort eventLButtonReleased;    
    private final MouseCaptureInstance.EventTriggerPort eventRButtonPressed;    
    private final MouseCaptureInstance.EventTriggerPort eventRButtonReleased;    
    private final MouseCaptureInstance.EventTriggerPort eventMButtonPressed;    
    private final MouseCaptureInstance.EventTriggerPort eventMButtonReleased;    
    private final MouseCaptureInstance.EventTriggerPort eventWheelUp;    
    private final MouseCaptureInstance.EventTriggerPort eventWheelDown;    

    public Bridge(final MouseCaptureInstance.OutputPort mouse_x,
                  final MouseCaptureInstance.OutputPort mouse_y,
                  final MouseCaptureInstance.EventTriggerPort eventLButtonPressed,
                  final MouseCaptureInstance.EventTriggerPort eventLButtonReleased,
                  final MouseCaptureInstance.EventTriggerPort eventRButtonPressed,
                  final MouseCaptureInstance.EventTriggerPort eventRButtonReleased,
                  final MouseCaptureInstance.EventTriggerPort eventMButtonPressed,
                  final MouseCaptureInstance.EventTriggerPort eventMButtonReleased,
                  final MouseCaptureInstance.EventTriggerPort eventWheelUp,
                  final MouseCaptureInstance.EventTriggerPort eventWheelDown)
    {
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
     * error code otherwise
     */
    native public int activate();
  
    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     * error code otherwise
     */
    native public int deactivate();
        
    /**
     * Gets the value of the named property.
     *
     * @param key the name of the property to be accessed
     * @return the value of the named property
     */
    native public String getProperty(String key);
 
    /**
     * Sets the named property to the defined value.
     *
     * @param key the name of the property to be accessed
     * @param value the value to be assigned to the named property
     * @return the value previously assigned to the named property
     */
    native public String setProperty(String key, final String value);
 
    /** 
     * This method is called back from the native code on demand to signify an
     * internal error. The first argument corresponds to an error code and the
     * second argument corresponds to a textual description of the error.
     *
     * @param errorCode an error code
     * @param message a textual description of the error
     */
    private void errorReport_callback( 
            final int errorCode,
             final String message) 
    {
    	AstericsErrorHandling.instance.getLogger().fine(errorCode + ": " + message);
    } 
     
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the x and y movement of the mouse
     *
     * @param x_value the x movement (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     * @param a_value the a movement (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     */
    private void newCoordinates_callback(final int x_value,
        final int y_value)
    { 
        mouseX.sendData(x_value);  
        mouseY.sendData(y_value);  
           
    }

    /**
     * This method is called back from the native code on demand. The passed
     * argument holds the left mouse button state in bit 0, the right
     * mouse button state in bit 1 and the middle mouse button state in bit 2
     *
     * @param buttonstate left/right/middle mouse button in bit 0/1/2  
     * (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     */
    private void newButtons_callback(final int buttonstate)
    { 
//        System.out.println("Java: Callback buttonstate = " + buttonstate);

    	if (((oldButtonstate & 1) == 0) && ((buttonstate & 1) != 0))
    		eventLButtonPressed.raiseEvent();
    	else if (((oldButtonstate & 1) != 0) && ((buttonstate & 1) == 0 ))
    		eventLButtonReleased.raiseEvent();

    	if (((oldButtonstate & 2) == 0) && ((buttonstate & 2) != 0))
    		eventRButtonPressed.raiseEvent();
    	else if (((oldButtonstate & 2) != 0) && ((buttonstate & 2) == 0 ))
    		eventRButtonReleased.raiseEvent();
      
    	if (((oldButtonstate & 4) == 0) && ((buttonstate & 4) != 0))
    		eventMButtonPressed.raiseEvent();
    	else if (((oldButtonstate & 4) != 0) && ((buttonstate & 4) == 0 ))
    		eventMButtonReleased.raiseEvent();

    	oldButtonstate=buttonstate;  
         
    }  
 
    /**
     * This method is called back from the native code on demand. The passed
     * argument holds the mouse wheel movement (1 = wheel up, -1 = wheel down)
     *
     * @param wheel int 1/-1  (range is [-1, 1])
     */
    private void newWheel_callback(final int wheel)
    { 
    	if (wheel>0)
    		eventWheelUp.raiseEvent();
    	else eventWheelDown.raiseEvent();
    }
 
}