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
package eu.asterics.component.sensor.mousecapture.jni;

import java.lang.reflect.Field;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import eu.asterics.component.sensor.mousecapture.MouseCaptureInstance;
import eu.asterics.mw.jnativehook.NativeHookServices;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class provides a bridge for Mouse Capturing using the jnativehook library.
 * @see https://github.com/kwhat/jnativehook
 * 
 * @author mad <e-mail address>
 * @date Apr 19, 2019
 *
 */
public class JNativeHookBridge extends AbstractBridge implements NativeMouseInputListener, NativeMouseWheelListener {
    private boolean propBlockEvents = false;

    public JNativeHookBridge(final MouseCaptureInstance.OutputPort mouse_x, final MouseCaptureInstance.OutputPort mouse_y,
            final MouseCaptureInstance.EventTriggerPort eventLButtonPressed, final MouseCaptureInstance.EventTriggerPort eventLButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventRButtonPressed, final MouseCaptureInstance.EventTriggerPort eventRButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventMButtonPressed, final MouseCaptureInstance.EventTriggerPort eventMButtonReleased,
            final MouseCaptureInstance.EventTriggerPort eventWheelUp, final MouseCaptureInstance.EventTriggerPort eventWheelDown) {

        AstericsErrorHandling.instance.getLogger().fine("Initializing JNativeHookBridge for mouse capturing...");

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

        // Ensure that the NativeHookServices are initialized at least once.
        NativeHookServices.init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.component.sensor.mousecapture.jni.AbstractBridge#activate()
     */
    @Override
    public int activate() {
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeMouseWheelListener(this);
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.component.sensor.mousecapture.jni.AbstractBridge#deactivate()
     */
    @Override
    public int deactivate() {
        GlobalScreen.removeNativeMouseListener(this);
        GlobalScreen.removeNativeMouseMotionListener(this);
        GlobalScreen.removeNativeMouseWheelListener(this);
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.component.sensor.mousecapture.jni.AbstractBridge#getProperty(java .lang.String)
     */
    @Override
    public String getProperty(String key) {
        if ("blockEvents".equals(key)) {
            return Boolean.toString(propBlockEvents);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.component.sensor.mousecapture.jni.AbstractBridge#setProperty(java .lang.String, java.lang.String)
     */
    @Override
    public String setProperty(String key, String value) {
        if ("blockEvents".equals(key)) {
            propBlockEvents = Boolean.parseBoolean(value);
            return Boolean.toString(propBlockEvents);
        }

        return null;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent arg0) {
        if(blockedEvent(arg0)) {
            return;
        }
        
        switch (arg0.getButton()) {
        case NativeMouseEvent.BUTTON1:
            // left
            eventLButtonPressed.raiseEvent();
            break;
        case NativeMouseEvent.BUTTON2:
            // right
            eventRButtonPressed.raiseEvent();
            break;
        case NativeMouseEvent.BUTTON3:
            // middle
            eventMButtonPressed.raiseEvent();
            break;
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent arg0) {
        if(blockedEvent(arg0)) {
            return;
        }
        
        switch (arg0.getButton()) {
        case NativeMouseEvent.BUTTON1:
            // left
            eventLButtonReleased.raiseEvent();
            break;
        case NativeMouseEvent.BUTTON2:
            // right
            eventRButtonReleased.raiseEvent();
            break;
        case NativeMouseEvent.BUTTON3:
            // middle
            eventMButtonReleased.raiseEvent();
            break;
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent arg0) {
        if(blockedEvent(arg0)) {
            return;
        }
        
        newCoordinates_callback(arg0.getX(), arg0.getY());
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent arg0) {
        if(blockedEvent(arg0)) {
            return;
        }
        
        newWheel_callback(arg0.getWheelRotation());
    }
    
    /**
     * This method does the blocking trick. Unfortunately this is not supported on all platforms. On Windows it works :-)
     * @param arg0
     * @return
     */

    private boolean blockedEvent(NativeInputEvent arg0) {
        if (propBlockEvents) {
            try {
                //System.out.println("blocking event: "+arg0);
                Field f = NativeInputEvent.class.getDeclaredField("reserved");
                f.setAccessible(true);
                f.setShort(arg0, (short) 0x01);
                return true;
            } catch (NoSuchFieldException nsfe) {
                AstericsErrorHandling.instance.getLogger().warning("Error blocking keycode --> NativeInputField not found");
            } catch (IllegalAccessException iae) {
                AstericsErrorHandling.instance.getLogger().warning("Error blocking keycode --> IllegalAccess on NativeInputfield");
            }
        }
        return false;
    }
}
