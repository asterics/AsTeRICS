

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.fourdjoystick;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventRawPacket;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.cimcommunication.CIMPortController;

/**
 * 
 * Plugin to gather data from the 4D-Joystick
 * 
 * 
 *  
 * @author <David Thaller> [dt@ki-i.at]
 *         Date: 14.01.2015
 *         Time: 19:03
 */
public class FourDJoystickInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	final IRuntimeOutputPort opJoyX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opJoyY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opJoyZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSipPuff = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpCH5Pressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpCH5Released = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpCH6Pressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpCH6Released = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propComPort = "COM0";
	private int state = 0;
	private int recvValue;
	private int receivedCounter = 0;
	private CIMPortController portController = null;
	private int ch5Value = -1;
	private int ch6Value = -1;
	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public FourDJoystickInstance()
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
		if ("joyX".equalsIgnoreCase(portID))
		{
			return opJoyX;
		}
		if ("joyY".equalsIgnoreCase(portID))
		{
			return opJoyY;
		}
		if ("joyZ".equalsIgnoreCase(portID))
		{
			return opJoyZ;
		}
		if ("sipPuff".equalsIgnoreCase(portID))
		{
			return opSipPuff;
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
		if ("cH5Pressed".equalsIgnoreCase(eventPortID))
		{
			return etpCH5Pressed;
		}
		if ("cH5Released".equalsIgnoreCase(eventPortID))
		{
			return etpCH5Released;
		}
		if ("cH6Pressed".equalsIgnoreCase(eventPortID))
		{
			return etpCH6Pressed;
		}
		if ("cH6Released".equalsIgnoreCase(eventPortID))
		{
			return etpCH6Released;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComPort;
			propComPort = (String)newValue;
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
          super.start();
		  state = 0;
		  ch5Value = -1;
		  ch6Value = -1;
		  portController = CIMPortManager.getInstance().getRawConnection(propComPort, 9600);
		  if (portController == null) {
			AstericsErrorHandling.instance.reportError(this,"Could not construct raw port controller, please verify that the COM port number is valid.");
		  } else {
				portController.addEventListener(this);
		}
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          super.stop();
		  if (portController != null) {
			CIMPortManager.getInstance().closeRawConnection(propComPort);
			portController = null;
		  }
      }
	  
	  @Override
      public void handlePacketReceived(CIMEvent e) 
	  {
		  CIMEventRawPacket rp = (CIMEventRawPacket ) e;
		  int data = rp.b & 0x000000FF;
		  switch (state) 
		  {
			case 0:
				if (data == 255) 
					state = 1;
				receivedCounter = 0;
				break;
			case 1:
				if (data == 255) 
					state = 2;
				else
					state = 0;
				break;
			case 2:
				// read MSB of channel 1;
				recvValue = (data << 8);
				state = 3;
				break;
			case 3:
				recvValue |= data;
				state = 2;
				switch (receivedCounter) {
					case 0:
						 opJoyY.sendData(ConversionUtils.intToBytes(recvValue));
						break;
					case 1:
						opJoyX.sendData(ConversionUtils.intToBytes(recvValue));
						break;
					case 2:
						opJoyZ.sendData(ConversionUtils.intToBytes(recvValue));
						break;
					case 3:
						opSipPuff.sendData(ConversionUtils.intToBytes(recvValue));
						break; 
					case 4:
						if (ch5Value == -1) {
							ch5Value = recvValue;
						} else {
							if (recvValue != ch5Value) {
								if (recvValue < ch5Value) {
									etpCH5Pressed.raiseEvent();
								} else {
									etpCH5Released.raiseEvent();
								}
								ch5Value = recvValue;
							}
						}
						break;
					case 5:
						if (ch6Value == -1) {
							ch6Value = recvValue;
						} else {
							if (recvValue != ch6Value) {
								if (recvValue < ch6Value) {
									etpCH6Pressed.raiseEvent();
								} else {
									etpCH6Released.raiseEvent();
								}
								ch6Value = recvValue;
							}
						}
						break;
				}
				receivedCounter++;
				if (receivedCounter == 6) 
					state = 0;
				break;
		  }
	  }
      
	  @Override
	  public void handlePacketError(CIMEvent e) {
	  	
	  }
}