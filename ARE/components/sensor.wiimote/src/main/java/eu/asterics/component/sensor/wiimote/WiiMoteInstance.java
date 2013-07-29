

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.wiimote;
 

import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.component.sensor.wiimote.jni.Bridge;
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
 * 
 * WiiMoteInstance interfaces to a WiiMote controller and possibly a Nunchuck Extesion
 * via JNI and the WiiYourself! Library. Provided sensor information includes:
 * Wiimote pitch and roll, up/down/left/A/B/+/-/1/2/Home button events,
 * Nunchuck Joystick x/Y position and Battery Level. 
 * 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 */
public class WiiMoteInstance extends AbstractRuntimeComponentInstance
{
	public final IRuntimeOutputPort opPitch = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opRoll = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opPoint1X = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opPoint1Y = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opPoint2X = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opPoint2Y = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opNunX = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opNunY = new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opBattery = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	public final IRuntimeEventTriggererPort etpPressedUp = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedUp = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedDown = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedDown = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedLeft = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedLeft = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedRight = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedRight = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedA = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedA = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedB = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedB = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressed1 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleased1 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressed2 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleased2 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedPlus = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedPlus = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedMinus = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedMinus = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedHome = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedHome = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedNunchuckC = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedNunchuckC = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpPressedNunchuckZ = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpReleasedNunchuckZ = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	// declare member variables here

	private final Bridge bridge = new Bridge(this);

    
   /**
    * The class constructor.
    */
    public WiiMoteInstance()
    {
        // empty constructor
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
		if ("pitch".equalsIgnoreCase(portID))
		{
			return opPitch;
		}
		if ("roll".equalsIgnoreCase(portID))
		{
			return opRoll;
		}
		if ("point1X".equalsIgnoreCase(portID))
		{
			return opPoint1X;
		}
		if ("point1Y".equalsIgnoreCase(portID))
		{
			return opPoint1Y;
		}
		if ("point2X".equalsIgnoreCase(portID))
		{
			return opPoint2X;
		}
		if ("point2Y".equalsIgnoreCase(portID))
		{
			return opPoint2Y;
		}
		if ("nunX".equalsIgnoreCase(portID))
		{
			return opNunX;
		}
		if ("nunY".equalsIgnoreCase(portID))
		{
			return opNunY;
		}
		if ("battery".equalsIgnoreCase(portID))
		{
			return opBattery;
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
		if ("pressedUp".equalsIgnoreCase(eventPortID))
		{
			return etpPressedUp;
		}
		if ("releasedUp".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedUp;
		}
		if ("pressedDown".equalsIgnoreCase(eventPortID))
		{
			return etpPressedDown;
		}
		if ("releasedDown".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedDown;
		}
		if ("pressedLeft".equalsIgnoreCase(eventPortID))
		{
			return etpPressedLeft;
		}
		if ("releasedLeft".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedLeft;
		}
		if ("pressedRight".equalsIgnoreCase(eventPortID))
		{
			return etpPressedRight;
		}
		if ("releasedRight".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedRight;
		}
		if ("pressedA".equalsIgnoreCase(eventPortID))
		{
			return etpPressedA;
		}
		if ("releasedA".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedA;
		}
		if ("pressedB".equalsIgnoreCase(eventPortID))
		{
			return etpPressedB;
		}
		if ("releasedB".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedB;
		}
		if ("pressed1".equalsIgnoreCase(eventPortID))
		{
			return etpPressed1;
		}
		if ("released1".equalsIgnoreCase(eventPortID))
		{
			return etpReleased1;
		}
		if ("pressed2".equalsIgnoreCase(eventPortID))
		{
			return etpPressed2;
		}
		if ("released2".equalsIgnoreCase(eventPortID))
		{
			return etpReleased2;
		}
		if ("pressedPlus".equalsIgnoreCase(eventPortID))
		{
			return etpPressedPlus;
		}
		if ("releasedPlus".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedPlus;
		}
		if ("pressedMinus".equalsIgnoreCase(eventPortID))
		{
			return etpPressedMinus;
		}
		if ("releasedMinus".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedMinus;
		}
		if ("pressedHome".equalsIgnoreCase(eventPortID))
		{
			return etpPressedHome;
		}
		if ("releasedHome".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedHome;
		}
		if ("pressedNunchuckC".equalsIgnoreCase(eventPortID))
		{
			return etpPressedNunchuckC;
		}
		if ("releasedNunchuckC".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedNunchuckC;
		}
		if ("pressedNunchuckZ".equalsIgnoreCase(eventPortID))
		{
			return etpPressedNunchuckZ;
		}
		if ("releasedNunchuckZ".equalsIgnoreCase(eventPortID))
		{
			return etpReleasedNunchuckZ;
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
		if ("updatePeriod".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("updatePeriod".equalsIgnoreCase(propertyName))
		{
			final String oldValue = bridge.getProperty(propertyName);
			bridge.setProperty(propertyName, newValue.toString());

			return oldValue;
		}

        return null;
    }
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  bridge.activate();
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
    	  bridge.activate();
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