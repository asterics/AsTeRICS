
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

package eu.asterics.component.sensor.proximity;


import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
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

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author Franziska Horak [franziska.horak@gmail.com]
 *         Date: 07.March 2013 
 *         Time: 14:15
 */
public class ProximityInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private CIMPortController port = null;
	
	private final String NAME_ETP_LOW_TO_HIGH  	= "LowToHigh";
	private final String NAME_ETP_HIGH_TO_LOW  	= "HighToLow";
	private final String KEY_PROPERTY_THRESHOLD = "threshold";
	private final String KEY_PROPERTY_SENDING_MODE = "sendingMode";
	
	
	final IRuntimeOutputPort opDistance = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpLowToHigh = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpHighToLow = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	short propThreshold = 0;
	short propSendingMode = 0;

	// declare member variables here
	private static final short CIM_ID_TEENSY 			= (short) 0xa301;
	
	private static final short TEENSY_CIM_FEATURE_SET_THRESHOLD	 	= 0x02; //Schwellwert setzen
	private static final short TEENSY_CIM_FEATURE_MODE_SELECTION	= 0x06; //Auswahl des Sendemodus
	
	
   /**
    * The class constructor.
    */
    public ProximityInstance()
    {
    	System.out.println("** Hey I am Proximity !");

        // empty constructor - needed for OSGi service factory operations
    }

    
    /**
     * returns the output port
     * @param portID the ID of the requested port
     * @return the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("distance".equalsIgnoreCase(portID))
		{
			return opDistance;
		}

		return null;
	}

    
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("input".equalsIgnoreCase(portID))
		{
			return ipInput;
		}

		return null;
	}
 

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID the requested ID
     * @return the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if (NAME_ETP_LOW_TO_HIGH.equalsIgnoreCase(eventPortID))
		{
			return etpLowToHigh;
		}
		if (NAME_ETP_HIGH_TO_LOW.equalsIgnoreCase(eventPortID))
		{
			return etpHighToLow;
		}

        return null;
    }
		
    /**
     * Returns the value of a specified component property
     * @param propertyName the name of the requested property
     * @return the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if (KEY_PROPERTY_THRESHOLD.equalsIgnoreCase(propertyName))
		{
			return propThreshold;
		}

		if(KEY_PROPERTY_SENDING_MODE.equalsIgnoreCase(propertyName))
	    {
	        return propSendingMode;
	    }
		
        return null;
    }
    
    
    private final IRuntimeInputPort ipInput  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.intFromBytes(data);
			
			propThreshold = (short) (value);

	        if (port != null)
	        {
		       	byte [] leBytes = ConversionUtils.shortToBytesLittleEndian(propThreshold);
		       	CIMPortManager.getInstance().sendPacket(port, leBytes, TEENSY_CIM_FEATURE_SET_THRESHOLD, 
							      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
	       	}
		}
		
	};

    /**
     * Sets a new value for a specified property
     * @param propertyName the name of the requested property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {//Werte kommen von ACS in ARE und werden von dort an CIM gesendet

	        if(KEY_PROPERTY_THRESHOLD.equalsIgnoreCase(propertyName))
		    {
		        propThreshold = Short.parseShort(newValue.toString());
	         	AstericsErrorHandling.instance.reportInfo(this, "SetThreshold:"+propThreshold);

		        if (port != null)
		        {
			       	byte [] leBytes = ConversionUtils.shortToBytesLittleEndian(propThreshold);
			       	CIMPortManager.getInstance().sendPacket(port, leBytes, TEENSY_CIM_FEATURE_SET_THRESHOLD, 
								      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
		       	}
		    }
	        

	    	if(KEY_PROPERTY_SENDING_MODE.equalsIgnoreCase(propertyName))
	        { 
	            propSendingMode = Short.parseShort(newValue.toString());
	            if (port != null)
		        {
			       	byte[] send1 = ConversionUtils.shortToBytesLittleEndian(propSendingMode);
			       	CIMPortManager.getInstance().sendPacket(port, send1, TEENSY_CIM_FEATURE_MODE_SELECTION, 
								      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
		       	}
	            
	        }

        return null;
    }


     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	if (port == null)
      	{
      		port = (CIMPortManager.getInstance().getConnection(CIM_ID_TEENSY));
      	}
    	
    	if (port != null )
        {
    		port.addEventListener(this);

	       	byte[] send1 = ConversionUtils.shortToBytesLittleEndian(propSendingMode);
	       	CIMPortManager.getInstance().sendPacket(port, send1, TEENSY_CIM_FEATURE_MODE_SELECTION, 
						      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);

	       	byte [] leBytes = ConversionUtils.shortToBytesLittleEndian(propThreshold);
	       	CIMPortManager.getInstance().sendPacket(port, leBytes, TEENSY_CIM_FEATURE_SET_THRESHOLD, 
							      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);

         	CIMPortManager.getInstance().sendPacket(port, null, (short) 0, 
				      CIMProtocolPacket.COMMAND_REQUEST_START_CIM, false);

	       	
         	super.start();
         	// AstericsErrorHandling.instance.reportInfo(this, "ProximityInInstance started");
         }
    	
         else
         {
         	AstericsErrorHandling.instance.reportError(this, "Could not find Teensy controller, make sure the Proximity module is connected to an USB port and correctly installed.");
         }      	
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  port.removeEventListener(this);
          super.pause();
          AstericsErrorHandling.instance.reportInfo(this, "ProximityInInstance paused");
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  port.addEventListener(this);
          super.resume();
          // AstericsErrorHandling.instance.reportInfo(this, "ProximityInInstance resumed");
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	super.stop();
        if (port != null )
        {
         	CIMPortManager.getInstance().sendPacket(port, null, (short) 0, 
				      CIMProtocolPacket.COMMAND_REQUEST_STOP_CIM, false);

        	port.removeEventListener(this);
        	port = null;
        }
        // AstericsErrorHandling.instance.reportInfo(this, "ProximityInInstance stopped");
      }


public void handlePacketReceived(CIMEvent e)
{
	
	CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
	CIMProtocolPacket packet = ev.packet;
	
	if ( ((packet.getSerialNumber() & 0x80) != 0) && (packet.getFeatureAddress() == 0x03) ) //CIM_FEATURE_SEND_EVENT
	{
		byte [] b = packet.getData();		
		// AstericsErrorHandling.instance.reportInfo(this, "Got event !");
		
		if (b[0] == 1)
		{
			//AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
			etpLowToHigh.raiseEvent();
		}			
		else
		{
			//AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
			etpHighToLow.raiseEvent();
		}
	}
	
	if ( ((packet.getSerialNumber() & 0x80) != 0) && (packet.getFeatureAddress() == 0x05) ) //CIM_FEATURE_SEND_DATA
	{
		byte [] b = packet.getData();
		// AstericsErrorHandling.instance.reportInfo(this, "Got periodic update !");
		
		if (b.length == 2)
		{
			int output = 0;
			output =   (int) (b[0]&0xff);
			output += ((int) b[1])<<8;
			
			opDistance.sendData(ConversionUtils.intToBytes(output));
		}
	}	
}

/**
 * Called if a faulty packet is read from the CIM
 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportDebugInfo(this, "Faulty packet received");
	}
}