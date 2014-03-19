

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

package eu.asterics.component.sensor.joystickcapture;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.component.sensor.joystickcapture.jni.Bridge;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
 

/**
 * JoystickCaptureInstance interfaces a standard joystick with up to 6 axis and 12 buttons
 * and provides these input values via ports 
 * 
 * @author Christoph Veigl [veigl@technikum-wien.at]
 *         Date: Oct 30, 2011
 *         Time: 02:10:00 PM
 */
public class JoystickCaptureInstance extends AbstractRuntimeComponentInstance
{
	public final int NUMBER_OF_BUTTONS = 20;
	private final String KEY_PROPERTY_EVENTPRESSED = "pressedButton";
	private final String KEY_PROPERTY_EVENTRELEASED = "releasedButton";

	public final IRuntimeOutputPort opX = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opY = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opZ = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opR = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opU = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opV = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opPov = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	public final IRuntimeEventTriggererPort [] etpPressedButton = new DefaultRuntimeEventTriggererPort[NUMBER_OF_BUTTONS];    
	public final IRuntimeEventTriggererPort [] etpReleasedButton = new DefaultRuntimeEventTriggererPort[NUMBER_OF_BUTTONS];    


	// declare member variables here

	private final Bridge bridge = new Bridge(this);
    public int axis=0;
    public int buttons=0;
    public int hasPov=0;
    
    
   /**
    * The class constructor.
    */
    public JoystickCaptureInstance()
    {
		for (int i = 0; i < NUMBER_OF_BUTTONS; i++)
		{
			etpPressedButton[i] = new DefaultRuntimeEventTriggererPort();
			etpReleasedButton[i] = new DefaultRuntimeEventTriggererPort();
		}
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("x".equalsIgnoreCase(portID))
		{
			return opX;
		}
		if ("y".equalsIgnoreCase(portID))
		{
			return opY;
		}
		if ("z".equalsIgnoreCase(portID))
		{
			return opZ;
		}
		if ("r".equalsIgnoreCase(portID))
		{
			return opR;
		}
		if ("u".equalsIgnoreCase(portID))
		{
			return opU;
		}
		if ("v".equalsIgnoreCase(portID))
		{
			return opV;
		}
		if ("pov".equalsIgnoreCase(portID))
		{
			return opPov;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	String s;
		for (int i = 0; i < NUMBER_OF_BUTTONS; i++)
		{
			s = KEY_PROPERTY_EVENTPRESSED + (i + 1);
			if (s.equalsIgnoreCase(eventPortID))
			{
				return etpPressedButton[i];
			}
			s = KEY_PROPERTY_EVENTRELEASED + (i + 1);
			if (s.equalsIgnoreCase(eventPortID))
			{
				return etpReleasedButton[i];
			}
		}
        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if("updatePeriod".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		} 

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param[in] propertyName   the name of the property
     * @param[in] newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if("updatePeriod".equalsIgnoreCase(propertyName))
		{
			final String oldValue = bridge.getProperty(propertyName);
			bridge.setProperty(propertyName, newValue.toString());

			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  if (bridge.activate() !=0)
         		AstericsErrorHandling.instance.reportError(this, "Could not find Joystick or Gamepad. Please verify that a Joystick or Gamepad is connected to a USB port !");
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
  		bridge.deactivate();
  		super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume() 
      {
    	  if (bridge.activate() !=0)
       		AstericsErrorHandling.instance.reportError(this, "Could not find Joystick or Gamepad. Please verify that a Joystick or Gamepad is connected to a USB port !");
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
  		  bridge.deactivate();
          super.stop();
      }
}