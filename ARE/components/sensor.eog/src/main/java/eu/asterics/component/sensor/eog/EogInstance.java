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

package eu.asterics.component.sensor.eog; 


import java.util.logging.Logger;
import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
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
 * <Interpretate and process incoming packets sent by EOG CIM>
 * 
 * 
 *  
 * @author <Chris Veigl (FHTW), Benedikt Rossboth> [<benedikt@rossboth.at>]
 *         Date: 11/2011
 */
public class EogInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private CIMPortController port = null; 
 
	final IRuntimeOutputPort opHorizontal = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opVertical = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	public int 					propUpdatePeriod 					= 10;
	
	private final short 		EOG_CIM_ID 							= (short) 0xa101;
	
	private static final short 	EOG_FEATURE_SERIAL_NUMBER 			= 0x0000;
	private static final short 	EOG_FEATURE_ACTIVATE_PERIODIC_VALUE = 0x0001;
	private static final short 	EOG_FEATURE_CHANNEL_VALUE_REPORT 	= 0x0002;
	
	// declare member variables here

   
   /**
    * The class constructor.
    */
    public EogInstance()
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
		if ("horizontal".equalsIgnoreCase(portID))
		{
			return opHorizontal;
		}
		if ("vertical".equalsIgnoreCase(portID))
		{
			return opVertical;
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
			return propUpdatePeriod;
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
		if ("updatePeriod".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUpdatePeriod;
			propUpdatePeriod = Integer.parseInt(newValue.toString());
			
			if ( port != null)
			{
				CIMPortManager.getInstance().sendPacket(port, ConversionUtils.shortToBytesLittleEndian((short)propUpdatePeriod), EOG_FEATURE_ACTIVATE_PERIODIC_VALUE, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}			
			return oldValue;
		}

        return null;
    }


	/**
	 * Handles an input packet from the ADC CIM. Reads the values on all active 
	 * inputs and sends the data on the corresponding output ports 
	 * @param packet the incoming packet
	 */
	private void handleEogInputValuePacket(CIMProtocolPacket packet)
	{
		byte [] b = packet.getData();
		int horizontal = 0;
		int vertical = 0;

		horizontal =  ((int) b[1]) & 0xff;
		horizontal =  horizontal | ((((int) b[0]) & 0xff) << 8);
		
		
		vertical =  ((int) b[3]) & 0xff;
		vertical =  vertical | ((((int) b[2]) & 0xff) << 8);

		opHorizontal.sendData(ConversionUtils.intToBytes(horizontal));												
		opVertical.sendData(ConversionUtils.intToBytes(vertical));	
	}
	
	private void handleEogSerialNumber(CIMProtocolPacket packet)
	{
		byte []b=packet.getData();
		System.out.println(b);
	}
    
	private void handleEogStartNumber(CIMProtocolPacket packet)
	{
		System.out.println("started Plug-In...");
	}

	/**
	 * Called by port controller if new packet has been received
	 */
	public void handlePacketReceived(CIMEvent e)
	{
		CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
		CIMProtocolPacket packet = ev.packet;
		
		short featureAddress = 0;
		featureAddress=packet.getFeatureAddress();
		
		switch(packet.getRequestReplyCode())
		{
			case CIMProtocolPacket.COMMAND_REPLY_START_CIM:
				System.out.println ("Reply Start.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_STOP_CIM:
				System.out.println ("Reply Stop.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_RESET_CIM:
				System.out.println ("Reply Reset.");
				break;
			case CIMProtocolPacket.COMMAND_REPLY_READ_FEATURE:
				System.out.print ("Reply Read: ");
				if (featureAddress == EOG_FEATURE_SERIAL_NUMBER )
				{
					System.out.println ("UniqueNumber");
					handleEogSerialNumber(packet);
				}
				break;
			case CIMProtocolPacket.COMMAND_EVENT_REPLY:
				//System.out.print ("...");
				if (featureAddress == EOG_FEATURE_CHANNEL_VALUE_REPORT)
				{
					handleEogInputValuePacket(packet);
				}
				break;
			case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
				System.out.print ("Replay Write: ");
				if (featureAddress == EOG_FEATURE_ACTIVATE_PERIODIC_VALUE)
				{
					System.out.println ("Set PeriodicTimeValue.");
				}
				break;				
		}		
		//System.out.println("received packet.");
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
  		  port = CIMPortManager.getInstance().getConnection(EOG_CIM_ID);
		  if (port != null )
		  {
			port.addEventListener(this);
			CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
			CIMPortManager.getInstance().sendPacket(port, ConversionUtils.shortToBytesLittleEndian((short)propUpdatePeriod), EOG_FEATURE_ACTIVATE_PERIODIC_VALUE, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);			
		  }
		  else
		  {
	       		AstericsErrorHandling.instance.reportError(this, "Could not find EOG Module. Please verify that the Module is connected to an USB Port and that the driver is installed.");
		  }
          super.start();
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
    	  port.removeEventListener(this);
		  if ( port != null)
		  {
			  CIMPortManager.getInstance().sendPacket(port,null, (short)0, CIMProtocolPacket.COMMAND_REPLY_STOP_CIM, false);
		  }
          super.stop();
      }
}