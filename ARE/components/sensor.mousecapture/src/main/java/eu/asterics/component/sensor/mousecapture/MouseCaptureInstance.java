
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

package eu.asterics.component.sensor.mousecapture;

import java.lang.reflect.Field;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.jnativehook.NativeHookServices;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.utils.OSUtils;

/**
 * MouseCaptureInstance intercepts local mouse input and routes the mouse
 * actions to output ports of the plugin.
 * Improved version, using the jnativehook service
 * 
 * @author Benjamin Aigner <aignerb@technikum-wien.at>, October 2017
 */
public class MouseCaptureInstance extends AbstractRuntimeComponentInstance implements NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {
    private final OutputPort opMouseX = new OutputPort();
    private final OutputPort opMouseY = new OutputPort();
    private final EventTriggerPort etpLeftButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpLeftButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpRightButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpRightButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpMiddleButtonPressed = new EventTriggerPort();
    private final EventTriggerPort etpMiddleButtonReleased = new EventTriggerPort();
    private final EventTriggerPort etpWheelUp = new EventTriggerPort();
    private final EventTriggerPort etpWheelDown = new EventTriggerPort();

    boolean propBlock = false;
    boolean enabled = true;
    int Xprev = 0;
    int Yprev = 0;
    boolean isMousePrevValid = true;
    /**
     * The class constructor.
     */
    public MouseCaptureInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("mouseX".equalsIgnoreCase(portID)) {
            return opMouseX;
        } else if ("mouseY".equalsIgnoreCase(portID)) {
            return opMouseY;
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
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            return propBlock;
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
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            final String oldValue = String.valueOf(propBlock);
            if ("true".equalsIgnoreCase((String) newValue)) {
                propBlock = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propBlock = false;
            }
            return oldValue;
        }
        return null;
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpBlockEvents = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            propBlock = true;
        }
    };

    final IRuntimeEventListenerPort elpForwardEvents = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            propBlock = false;
        }
    };

    final IRuntimeEventListenerPort elpToggleBlock = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            propBlock = !propBlock;
        }
    };

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("blockEvents".equalsIgnoreCase(eventPortID)) {
            return elpBlockEvents;
        } else if ("forwardEvents".equalsIgnoreCase(eventPortID)) {
            return elpForwardEvents;
        } else if ("toggleBlock".equalsIgnoreCase(eventPortID)) {
            return elpToggleBlock;
        }
        return null;
    }

    /**
     * returns an Event Triggerer Port
     * 
     * @param enventPortID
     *            the name of the event trigger port
     * @return the event trigger port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("leftButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpLeftButtonPressed;
        } else if ("leftButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpLeftButtonReleased;
        } else if ("rightButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpRightButtonPressed;
        } else if ("rightButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpRightButtonReleased;
        } else if ("middleButtonPressed".equalsIgnoreCase(eventPortID)) {
            return etpMiddleButtonPressed;
        } else if ("middleButtonReleased".equalsIgnoreCase(eventPortID)) {
            return etpMiddleButtonReleased;
        } else if ("wheelUp".equalsIgnoreCase(eventPortID)) {
            return etpWheelUp;
        } else if ("wheelDown".equalsIgnoreCase(eventPortID)) {
            return etpWheelDown;
        }
        return null;
    }

    /**
     * Output Port for mouse coordinate values.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToBytes(data));
        }
    }

    /**
     * Event Triggerer Port for mouse actions.
     */
    public class EventTriggerPort extends DefaultRuntimeEventTriggererPort {
        @Override
        public void raiseEvent() {
            super.raiseEvent();
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        NativeHookServices.init();
        GlobalScreen.addNativeMouseWheelListener(this);
        GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
        enabled = true;
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        enabled = false;
        isMousePrevValid = false;
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        enabled = true;
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        enabled = false;
        isMousePrevValid = false;
        GlobalScreen.removeNativeMouseWheelListener(this);
        GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
        super.stop();
    }

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
		if (enabled == false) {
            return;
        }
		int rotation = e.getWheelRotation();
		
		//raise wheel events, according to jnative declaration:
		//negative rotation -> wheel up
		//positive rotation -> wheel down
		if(rotation < 0)
		{
			etpWheelUp.raiseEvent();
		} else {
			etpWheelDown.raiseEvent();
		}
		//block events, if requested to
		if(propBlock == true)
		{
			blockEvent(e);

		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		return;
		//not used in this plugin, clicks & move is already forwarde
		//possible access:
		//e.getX();
		//e.getY();
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		if (enabled == false) {
            return;
        }
		
		//send either absolute positioning values (not blocking)
		//or relative values (blocking)
		
		//block events, if requested to
		if(propBlock == true)
		{
			blockEvent(e);
			if(isMousePrevValid)
			{
				//send relative coordinates
				opMouseX.sendData(ConversionUtils.intToBytes(e.getX()-Xprev+1));
				opMouseY.sendData(ConversionUtils.intToBytes(e.getY()-Yprev+1));
				//save current position for next call
				Xprev = e.getX();
				Yprev = e.getY();
			} else {
				//if no valid previous value is available:
				//save current values & send data next time
				Xprev = e.getX();
				Yprev = e.getY();
				isMousePrevValid = true;
			}
		} else {
			opMouseX.sendData(ConversionUtils.intToBytes(e.getX()));
			opMouseY.sendData(ConversionUtils.intToBytes(e.getY()));
		}
	}
	
	private void blockEvent(NativeMouseEvent e)
	{
		try {
            Field f = NativeInputEvent.class.getDeclaredField("reserved");
            f.setAccessible(true);
            f.setShort(e, (short) 0x01);
        } catch (NoSuchFieldException nsfe) {
            AstericsErrorHandling.instance.reportError(this,
                    "Error blocking keycode --> NativeInputField not found, blocking not supported in Linux yet!");
        } catch (IllegalAccessException iae) {
            AstericsErrorHandling.instance.reportError(this,
                    "Error blocking keycode --> IllegalAccess on NativeInputfield, blocking not supported in Linux yet!");
        }
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		return; // just do nothing
		//e.getClickCount(); //could be used, not in this plugin
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		if (enabled == false) {
            return;
        }
		switch(e.getButton())
		{
			//left mouse button
			case NativeMouseEvent.BUTTON1:
				etpLeftButtonPressed.raiseEvent();
				break;
			//Linux: middle mouse button (mostly scroll wheel click)
			//Windows: right mouse button
			case NativeMouseEvent.BUTTON2:
				switch(OSUtils.getOsName())
				{
				case "windows":
					etpRightButtonPressed.raiseEvent();
					break;
				default:
					etpMiddleButtonPressed.raiseEvent();
					break;
				}
				
				break;
			//Linux: right mouse button
			//Windows: middle mouse button (mostly scroll wheel click)
			case NativeMouseEvent.BUTTON3:
				switch(OSUtils.getOsName())
				{
				case "windows":
					etpMiddleButtonPressed.raiseEvent();
					break;
				default:
					etpRightButtonPressed.raiseEvent();
					break;
				}
			default:
				//no button event available, might be used in future version
				break;
		}
		
		if(propBlock == true)
		{
			blockEvent(e);

		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		if (enabled == false) {
            return;
        }
		switch(e.getButton())
		{
			//left mouse button
			case NativeMouseEvent.BUTTON1:
				etpLeftButtonReleased.raiseEvent();
				break;
			//Linux: middle mouse button (mostly scroll wheel click)
			//Windows: right mouse button
			case NativeMouseEvent.BUTTON2:
				switch(OSUtils.getOsName())
				{
				case "windows":
					etpRightButtonReleased.raiseEvent();
					break;
				default:
					etpMiddleButtonReleased.raiseEvent();
					break;
				}
				
				break;
			//Linux: right mouse button
			//Windows: middle mouse button (mostly scroll wheel click)
			case NativeMouseEvent.BUTTON3:
				switch(OSUtils.getOsName())
				{
				case "windows":
					etpMiddleButtonReleased.raiseEvent();
					break;
				default:
					etpRightButtonReleased.raiseEvent();
					break;
				}
			default:
				//no button event available, might be used in future version
				break;
		}
		
		if(propBlock == true)
		{
			blockEvent(e);

		}
	}

}
