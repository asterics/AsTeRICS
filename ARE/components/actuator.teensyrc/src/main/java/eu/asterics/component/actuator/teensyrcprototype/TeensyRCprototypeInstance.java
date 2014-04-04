

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

package eu.asterics.component.actuator.teensyrcprototype;


import java.util.HashMap;
import java.util.logging.Logger;

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
 * This module allows control of radio-controlled toy models via the Walkera MTC-01 (Magic Cube)
 * which is connected to a teensy micorcontroller running the Teensy_RC_CIM firmware
 * 
 *  
 * @author Alexander Frimmel
 *         Date: 02/2014
 *         Time: 
 */
public class TeensyRCprototypeInstance extends AbstractRuntimeComponentInstance
{
	private final short TEENSY_CIM_ID 			= (short) 0xa002;
	private static final short TEENSY_CIM_FEATURE_UNIQUENUMBER = 0x0000;
	private static final short TEENSY_CIM_FEATURE_SET_PPM_VALUES = 0x0001;
	
	private CIMPortController port = null;
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here
	private int channel1;
	private int channel2;
	private int channel3;
	private int channel4;
	private int channel5;
	private int channel6;
	private int channel7;
	private int channel8;
  
    
   /**
    * The class constructor.
    */
    public TeensyRCprototypeInstance()
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
		if ("channel1".equalsIgnoreCase(portID))
		{
			return ipChannel1;
		}
		if ("channel2".equalsIgnoreCase(portID))
		{
			return ipChannel2;
		}
		if ("channel3".equalsIgnoreCase(portID))
		{
			return ipChannel3;
		}
		if ("channel4".equalsIgnoreCase(portID))
		{
			return ipChannel4;
		}
		if ("channel5".equalsIgnoreCase(portID))
		{
			return ipChannel5;
		}
		if ("channel6".equalsIgnoreCase(portID))
		{
			return ipChannel6;
		}
		if ("channel7".equalsIgnoreCase(portID))
		{
			return ipChannel7;
		}
		if ("channel8".equalsIgnoreCase(portID))
		{
			return ipChannel8;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{

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

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {

        return null;
    }


    
    
     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipChannel1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel1=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel2=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel3=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel4=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel5=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel6=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel7  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel7=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};
	private final IRuntimeInputPort ipChannel8  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			channel8=ConversionUtils.intFromBytes(data);
	    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
		}
	};


    @Override
    public void syncedValuesReceived(HashMap<String, byte[]> dataRow)
    {
    	
    	for (String s: dataRow.keySet())
		{
			
			byte [] data = dataRow.get(s);
			if (s.equals("channel1"))
			{
				channel1=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel2"))
			{
				channel2=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel3"))
			{
				channel3=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel4"))
			{
				channel4=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel5"))
			{
				channel5=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel6"))
			{
				channel6=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel7"))
			{
				channel7=ConversionUtils.intFromBytes(data);
			}
			if (s.equals("channel8"))
			{
				channel8=ConversionUtils.intFromBytes(data);
			}
			
		}
    	
    	sendTeensyWriteFeature(TEENSY_CIM_FEATURE_SET_PPM_VALUES);
    }
    
    synchronized private void sendTeensyWriteFeature(short feature)
	{
		
    	//System.out.println(channel1+" "+channel2+" "+channel3+" "+channel4);		
		
		//byte [] b = new byte[8];
		byte [] b = new byte[16];	
		b[0] = (byte) (channel1 & 0xff);
		b[1] = (byte) ((channel1 >> 8) & 0xff);
		b[2] = (byte) (channel2 & 0xff);
		b[3] = (byte) ((channel2 >> 8) & 0xff);
		b[4] = (byte) (channel3 & 0xff);
		b[5] = (byte) ((channel3 >> 8) & 0xff);
		b[6] = (byte) (channel4 & 0xff);
		b[7] = (byte) ((channel4 >> 8) & 0xff);
		b[8] = (byte) (channel5 & 0xff);
		b[9] = (byte) ((channel5 >> 8) & 0xff);
		b[10] = (byte) (channel6 & 0xff);
		b[11] = (byte) ((channel6 >> 8) & 0xff);
		b[12] = (byte) (channel7 & 0xff);
		b[13] = (byte) ((channel7 >> 8) & 0xff);
		b[14] = (byte) (channel8 & 0xff);
		b[15] = (byte) ((channel8 >> 8) & 0xff);		
		
		if (port != null)
		{
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		}
		
		
		
	} 
    
    
    
     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  
    	  if(port==null) 
    	  {
  		     port = CIMPortManager.getInstance().getConnection(TEENSY_CIM_ID);
    	  }
    	  if(port != null)
		  {
    		  
		  }
    	  else
		  {
			AstericsErrorHandling.instance.reportError(this, String.format("Could not get port controller for TeensyRC CIM, please make sure the TeensyRC module is connected to a USB port. "));
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

          super.stop();
      }
}