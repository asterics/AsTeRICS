

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

package eu.asterics.component.sensor.lipmouse;


import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * Interface to the Lipmouse module. Based on ArduinoInstance.java developed by Chris Veigl
 * 
 * 
 *   
 * @author Alberto Ibanez [alberto_21_9@hotmail.com]
 *         Date: 07.01.2014
 *         Time: 17:50
 */
public class LipmouseInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private final short LIPMOUSE_CIM_ID  			= (short) 0xa401;
	private static final short LIPMOUSE_CIM_FEATURE_UNIQUENUMBER = 0x0000;
	private static final short LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD   	= 0x0001;
	private static final short LIPMOUSE_CIM_FEATURE_ADCREPORT 	  	= 0x0002;
	
	final IRuntimeOutputPort opX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPressure = new DefaultRuntimeOutputPort();

	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	public int propPeriodicADCUpdate = 0;
	
	// declare member variables here

	private CIMPortController port = null; 
	
   /**
    * The class constructor.
    */
    public LipmouseInstance()
    {
    	//System.out.println("Lipmouse firmware");
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID) //It doesn't have input ports, it can be deleted
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
		if ("pressure".equalsIgnoreCase(portID))
		{
			return opPressure;
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
		if ("calibration".equalsIgnoreCase(eventPortID))
		{
			return elpCalibration;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) //It doesn't have event trigger ports, it can be deleted
    {

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			return propPeriodicADCUpdate;
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
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPeriodicADCUpdate;
			propPeriodicADCUpdate = Integer.parseInt(newValue.toString());
			sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
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
	final IRuntimeEventListenerPort elpCalibration = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // It is not developed currently
			//sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_CALLIBRATION,calibrationData);
		}
	};

	

	/**
	 * Handles an input packet from Lipmouse CIM. Reads the values 
	 * of all ADC channels and sends the data to the corresponding output ports 
	 * @param packet the incoming packet
	 */
	private void handleLipmouseAdcReport(CIMProtocolPacket packet)
	{
		// System.out.println("handleLipmouseAdcPacket");
		byte [] b = packet.getData();
		int channelValue;

		opX.sendData(ADCDataToBytes(b[0],b[1]));
		opY.sendData(ADCDataToBytes(b[2],b[3]));
		opPressure.sendData(ADCDataToBytes(b[4],b[5]));
	}
	
	//This is a function to convert the data sent by the microcontroller to the format that the 
	//output port send it through the channel
    private byte [] ADCDataToBytes(byte first,byte second)
    {
    	if ( (second & 80) == 0) //If the number is positive, i.e, if 8th bit is 0
    	{
    		return new byte [] {(byte)(0x00),(byte)(0x00),second,first};
    	}else{ //If the numbe is negative, i.e, if 8th bit is 1
    		return new byte [] {(byte)(0xff),(byte)(0xff),second,first};
    	}
    	
    }
	private void handleLipmouseUniqueNumber(CIMProtocolPacket packet) //The Lipmouse will never calls this function, so it can be deleted
	{
		byte []b=packet.getData();
		// System.out.println(b);
	}
    
    
	/**
	 * Called by port controller if new packet has been received
	 */
	public void handlePacketReceived(CIMEvent e)
	{
		short featureAddress=0;
		// System.out.println ("LipmouseCIM handlePacketReceived:");
		CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
		CIMProtocolPacket packet = ev.packet;
		featureAddress=packet.getFeatureAddress();
		switch (packet.getRequestReplyCode())
		{
			case CIMProtocolPacket.COMMAND_REPLY_START_CIM:
				//System.out.println ("Reply Start.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_STOP_CIM:
				//System.out.println ("Reply Stop.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_RESET_CIM:
				//System.out.println ("Reply Reset.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_READ_FEATURE:
				//System.out.print ("Reply Read: ");
				if (featureAddress == LIPMOUSE_CIM_FEATURE_UNIQUENUMBER)
				{
					//System.out.println ("UniqueNumber");
					handleLipmouseUniqueNumber(packet);
				}
				if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT)
				{
					//System.out.println ("ADCReport.");
				 	handleLipmouseAdcReport(packet);
				}
				
				break;

			case CIMProtocolPacket.COMMAND_EVENT_REPLY:
				if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT)
				{
					 // System.out.println ("Incoming Event: ADCReport "+(128+(int)packet.getSerialNumber()));
					 handleLipmouseAdcReport(packet);
				}
				
				break;
			case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
				/*
				System.out.print ("Reply Write: ");
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINDIRECTION)
				{
					 System.out.println ("Set PinDirection.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINSTATE)
				{
					 System.out.println ("Set PinState.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINMASK)
				{
					 System.out.println ("Set PinMask.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_ADCPERIOD)
				{
					 System.out.println ("Set AdcPeriod.");
				}
				*/
				break;
		}
	}

	/**
	 * Called upon faulty packet reception
	 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
	}


	
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  if (port==null)
    	  {
  		     port = CIMPortManager.getInstance().getConnection(LIPMOUSE_CIM_ID );
    	  }
		  if (port != null )
		  {
			port.addEventListener(this);
 			sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
			CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
		  }
		  else
		  {
	       		AstericsErrorHandling.instance.reportError(this, "Could not find LipMouse Module. Please verify that the Module is connected to an USB Port and that the driver is installed.");
		  }

          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
		  if ( port != null)
		  {
			  CIMPortManager.getInstance().sendPacket(port, null, (short)0, CIMProtocolPacket.COMMAND_REPLY_STOP_CIM, false);
		  }
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
		  if ( port != null)
		  {
			  CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
		  }
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
		  if ( port != null)
		  {
			  CIMPortManager.getInstance().sendPacket(port, null, (short)0, CIMProtocolPacket.COMMAND_REPLY_STOP_CIM, false);
			  port.removeEventListener(this);
			  port=null;
		  }
          super.stop();
      }
      
      
  	synchronized private final void sendLipmouseWriteFeature (short feature, int value)
  	{
		// send packet
		byte [] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) ((value >> 8) & 0xff);
		
		if (port != null)
		{
              // System.out.println("sending lipmouse-packet !");
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		}
     }
}