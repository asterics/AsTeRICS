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

package eu.asterics.component.sensor.analogin;


import java.util.*;

import eu.asterics.mw.cimcommunication.*; 
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * AnalogInInstance is an AsTeRICS component handling inputs from the ADC inputs
 * of the ADC CIM (ID: 0x0401). It will provide the values sampled on the 
 * analog inputs of the CIM on its outputs 
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class AnalogInInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler, Runnable
{
	private CIMPortController port = null;

	private final String KEY_PROPERTY_INPUTACTIVE 	    = "activateInput";
	private final String KEY_PROPERTY_INPUTACTIVE_1 	= "activateInput1";
	private final String KEY_PROPERTY_INPUTACTIVE_2 	= "activateInput2";
	private final String KEY_PROPERTY_INPUTACTIVE_3 	= "activateInput3";
	private final String KEY_PROPERTY_INPUTACTIVE_4 	= "activateInput4";
	private final String KEY_PROPERTY_INPUTACTIVE_5 	= "activateInput5";
	private final String KEY_PROPERTY_INPUTACTIVE_6 	= "activateInput6";
	private final String KEY_PROPERTY_INPUTACTIVE_7 	= "activateInput7";
	private final String KEY_PROPERTY_INPUTACTIVE_8 	= "activateInput8";
	private final String KEY_PROPERTY_PERIODIC_UPDATE   = "periodicUpdate";

	private final int NUMBER_OF_INPUTS      = 8;
	private final short ADC_CIM_ID 			= 0x0901; 
	private boolean legacyAdcCim = false;

	final OutputPort [] opIn = new OutputPort[NUMBER_OF_INPUTS];    

	private boolean [] propActivateInput = new boolean[NUMBER_OF_INPUTS];
	private short propPeriodicUpdate = 0;
    private String propUniqueID = "not used"; 

	private static final short ADC_FEATURE_INPUT_VALUE   	= 0x40;

	boolean threadActive = false;
	boolean conversionActive = false;

	/**
	 * Constructs component and initiates output port arrays
	 */
	public AnalogInInstance()
	{
		// empty constructor - needed for OSGi service factory operations
		for (int i = 0; i < NUMBER_OF_INPUTS; i++)
		{
			opIn[i] = new OutputPort();
			propActivateInput[i] = false;
		}
	}

	/**
	 * Returns the requested output port instance
	 * @param portID the ID of the requested port
	 * @return the the output port instance, null if non existant 
	 */
	public IRuntimeOutputPort getOutputPort(String portID)
	{
		for (int i = 0; i < NUMBER_OF_INPUTS; i++)
		{
			String s = "in" + ( i + 1 );
			if(s.equalsIgnoreCase(portID))
			{
				return opIn[i];
			}
		}
		return null;
	}

	/**
	 * Returns the requested event port
	 * @param eventPortID the ID of the requested event listener port
	 * @return the requested port, null if non existant
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
	{
		if("adcSampleTrigger".equalsIgnoreCase(eventPortID))
		{
			return elpAdcSampleTrigger;
		}
		return null;
	}


	/**
	 * Returns the value of a requested property
	 * @param propertyName the name of the requested property
	 * @return the value of the property as an Object
	 */
	public Object getRuntimePropertyValue(String propertyName)
	{
		if(KEY_PROPERTY_INPUTACTIVE_1.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[0];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_2.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[1];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_3.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[2];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_4.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[3];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_5.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[4];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_6.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[5];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_7.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[6];
		}
		else if(KEY_PROPERTY_INPUTACTIVE_8.equalsIgnoreCase(propertyName))
		{
			return propActivateInput[7];
		}
		else if(KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName))
		{
			return propPeriodicUpdate;
		}
		else if("uniqueID".equalsIgnoreCase(propertyName))
        {
            return propUniqueID;
        }

		return null;
	}

	/**
	 * Sets the value of a requested property
	 * @param propertyName the name of the requested property
	 * @param newValue the new value for the property
	 * @return null
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue)
	{
		if(KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName))
		{
			try
			{
				propPeriodicUpdate = Short.parseShort(newValue.toString());
				AstericsErrorHandling.instance.reportInfo(this, String.format("Setting periodic updates to %d ms", propPeriodicUpdate));
			}
			catch (NumberFormatException nfe)
			{
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
			}        
		}
		else if("uniqueID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUniqueID;
			propUniqueID = (String)newValue;
			CIMPortController tempPort = openCIM (ADC_CIM_ID, propUniqueID);
			if (tempPort != null)
			{
				port=tempPort;
				if ((!propUniqueID.equals("")) && (!propUniqueID.equals("not used")))
				{
					byte [] leBytes = { (byte) 0x20 };
					CIMPortManager.getInstance().sendPacket  (port, leBytes, 
						  (short) 0x77, 
						  CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
					try { Thread.sleep (1000); }  catch (InterruptedException e) {}
					byte [] lebytes = { (byte) 0 };
					CIMPortManager.getInstance().sendPacket  (port, lebytes, 
						  (short) 0x77, 
						  CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);

				}
			} 
			return oldValue;
		}
		else 
		{
			// only the input active properties available anymore
			boolean propertyValue = false;
			if("true".equalsIgnoreCase((String)newValue))
				propertyValue = true;
			else if("false".equalsIgnoreCase((String)newValue))
				propertyValue = false;
			else
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);

			for (int i = 0; i < NUMBER_OF_INPUTS; i++)
			{
				String s = KEY_PROPERTY_INPUTACTIVE + (i + 1);
				if (s.equalsIgnoreCase(propertyName))
				{
					propActivateInput[i] = propertyValue;
					AstericsErrorHandling.instance.reportInfo(this, String.format("Setting input %d to %s", i, newValue));
				}
			}
		}

		return null;
	}
	
    /**
     * Opens a CIM controller for a certain unique ID 
     * @param cimId the CIM type ID of the CIM 
     * @param uniqueID the unique ID of the CIM as a string
     * @return the CIM controller for the corresponding CIM, null if not present
     */
	private CIMPortController openCIM(short cimId, String uniqueID)
	{
		if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID==""))
		{
		    return (CIMPortManager.getInstance().getConnection(cimId));
		}
		else
		{
			Long id;
			try {
				id=Long.parseLong(propUniqueID);
				return (CIMPortManager.getInstance().getConnection(cimId, id));
			} catch (Exception e) {
				return null;
			}	   
		}
	}
 
	/**
	 * Returns a List of available CIM unique IDs
	 * @return list of string with CIM IDs
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("uniqueID")==0)
		{
			res.add("not used");
			Vector<Long> ids = CIMPortManager.getInstance()
				.getUniqueIdentifiersofCIMs(ADC_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					res.add(l.toString());
					// System.out.println(" found unique ID: "+l.toString());
				}
			}
		}
		return res;
	} 
    
	

	/**
	 * Starts the component, retrieves ADC CIM connection and adds a listener to 
	 * the connection. Starts thread for periodic updates.
	 */
	@Override
	public void start()
	{
		port = openCIM(ADC_CIM_ID, propUniqueID);
		if (port != null )
		{
			port.addEventListener(this);
			legacyAdcCim = false;				
		}
		else
		{
			port = openCIM((short) 0x401, propUniqueID);
			if (port == null)
			{
	       		AstericsErrorHandling.instance.reportError(this, "Could not find AnalogIn CIM in PortManager. Please verify that the Module is connected to an USB Port and that the driver is installed.");
				return;
			}
			else
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Legacy ADC CIM connected, switchting to legacy mode");
				port.addEventListener(this);
				legacyAdcCim = true;				
			}
		}
		threadActive = true;
		AstericsThreadPool.instance.execute(this);
		super.start();
		// AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance started");
	}

	/**
	 * Stops the component, removes the listener, halts the thread
	 */
	@Override
	public void stop()
	{
		super.stop();
		threadActive = false;
		if (port != null )
		{
			port.removeEventListener(this);
			port = null;
		}
		// AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance stopped");
	}

	/**
	 * Pauses the component, removes the listener, halts the thread
	 */
	@Override
	public void pause()
	{
		super.pause();
		threadActive = false;
		if (port != null )
		{
			port.removeEventListener(this);
			port=null;
		}
		// AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance paused");
	}

	/**
	 * Resumes the component, retrieves ADC CIM connection and adds a listener to 
	 * the connection. Starts thread for periodic updates.
	 */
	@Override
	public void resume()
	{
	
		port = CIMPortManager.getInstance().getConnection(ADC_CIM_ID);
		if (port != null )
		{
			port.addEventListener(this);
			legacyAdcCim = false;				
		}
		else
		{
			port = CIMPortManager.getInstance().getConnection((short) 0x401);
			if (port == null)
			{
	       		AstericsErrorHandling.instance.reportError(this, "Could not find AnalogIn CIM in PortManager. Please verify that the Module is connected to an USB Port and that the driver is installed.");
				return;
			}
			else
			{
				port.addEventListener(this);
				legacyAdcCim = true;				
			}
		}
		threadActive = true;
		AstericsThreadPool.instance.execute(this);
		super.start();
	}

	
	/**
	 * Runs a loop which periodically sends a sample request to the ADC CIM if
	 * periodic update is activated
	 */
	public void run()
	{
		while (threadActive)
		{
			if ( (propPeriodicUpdate != 0) && (port != null) && (!conversionActive))
			{
				CIMPortManager.getInstance().sendPacket(port, null, ADC_FEATURE_INPUT_VALUE, 
						CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
				//		        Logger.getAnonymousLogger().info("Sent ADC input value read message");
				conversionActive = true;
			}

			try {
				Thread.sleep(propPeriodicUpdate != 0 ? propPeriodicUpdate : 500);
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * Handles an input packet from the ADC CIM. Reads the values on all active 
	 * inputs and sends the data on the corresponding output ports 
	 * @param packet the incoming packet
	 */
	private void handleAdcInputValuePacket(CIMProtocolPacket packet)
	{
		byte [] b = packet.getData();
		
		if (!legacyAdcCim)
		{
			if (b.length == 6)
			{
				for (int i = 0; i < 2; i++)
				{
					if (propActivateInput[i])
					{
						int output = 0;
						output =  ((int) b[i*3]) & 0xff;
						output =  output | ((((int) b[i*3 +1]) & 0xff) << 8);
						output =  output | ((((int) b[i*3 +2]) & 0xff) << 16);
						opIn[i].sendData(output);												
						//		        Logger.getAnonymousLogger().fine(String.format("Sending value: %x on output %x", output, i));
					}
				}
			}
			else
			{
				System.out.println("data not 6 long");
			}
		}
		else
		{
			// Logger.getAnonymousLogger().info("handleAdcInputValuePacket start");
			for (int i = 0; i < 4; i++)
			{
				if (propActivateInput[i])
				{
					int output = 0;
					output =  ((int) b[i*2]) & 0xff;
					output =  output | ((((int) b[i*2 +1]) & 0xff) << 8);
					opIn[i].sendData(output);												
					//		        Logger.getAnonymousLogger().fine(String.format("Sending value: %x on output %x", output, i));
				}
			}
	
			for (int i = 4, k = 0; i < 6; i++)
			{
				if (propActivateInput[i])
				{
					int output = 0;
					for (int j = 0 ; j < 3; j++)
					{
						output |= (((int) b[8 + j + k * 3]) & 0xff) << 8*j;
					}
					k++;
					opIn[i].sendData(output);
				}
			}
	
			for (int i = 6, j = 0; i < 8; i++, j++)
			{
				if (propActivateInput[i])
				{
					int output = 0;
					output =  ((int) b[j*2+14]) & 0xff;
					output =  output | ((((int) b[j*2 +15]) & 0xff) << 8);
					opIn[i].sendData(output);												
				}
			}
		}
	}

	/**
	 * Called by port controller if new packet has been received
	 */
	public void handlePacketReceived(CIMEvent e)
	{
		//        Logger.getAnonymousLogger().info("handlePacketReceived start");

		CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
		CIMProtocolPacket packet = ev.packet;

		if (packet.getFeatureAddress() == ADC_FEATURE_INPUT_VALUE)
		{
			handleAdcInputValuePacket(packet);
		}
		conversionActive = false;
	}

	/**
	 * Called upon faulty packet reception
	 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, 
			"Faulty packet received");
		conversionActive = false;
	}

	/**
	 * An output port implementation that allows sending of integers	
	 * @author weissch
	 *
	 */
	public class OutputPort extends DefaultRuntimeOutputPort
	{
		public void sendData(int data)
		{      	
			super.sendData(ConversionUtils.intToByteArray(data));
		}
	}

	/**
	 * An event listener port implementation which will cause ADC sampling upon
	 * an incoming event.
	 */
	final IRuntimeEventListenerPort elpAdcSampleTrigger = 
		new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if ( (propPeriodicUpdate == 0) && (port != null) && (!conversionActive))
			{
				conversionActive = true;
				CIMPortManager.getInstance().sendPacket(port, null, ADC_FEATURE_INPUT_VALUE, 
						CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
			}
			else
			{
				AstericsErrorHandling.instance.getLogger()
				.fine("Trigger event received while periodic update was" + 
				" set, ignoring event");
			}
		}
	};


}