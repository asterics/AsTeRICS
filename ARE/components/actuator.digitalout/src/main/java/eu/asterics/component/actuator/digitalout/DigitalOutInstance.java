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

package eu.asterics.component.actuator.digitalout;


import java.util.*;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

  
/**
 * DigitalOutInstance
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class DigitalOutInstance extends AbstractRuntimeComponentInstance 
{
	private CIMPortController port = null;
	
	private final String NAME_ELP_OUT1_H  	= "setOutput1";
	private final String NAME_ELP_OUT2_H  	= "setOutput2";
	private final String NAME_ELP_OUT3_H  	= "setOutput3";
	private final String NAME_ELP_OUT4_H  	= "setOutput4";
	private final String NAME_ELP_OUT5_H  	= "setOutput5";
	private final String NAME_ELP_OUT6_H  	= "setOutput6";
	private final String NAME_ELP_OUT7_H  	= "setOutput7";
	private final String NAME_ELP_OUT8_H  	= "setOutput8";
	private final String NAME_ELP_OUT1_L  	= "clearOutput1";
	private final String NAME_ELP_OUT2_L  	= "clearOutput2";
	private final String NAME_ELP_OUT3_L  	= "clearOutput3";
	private final String NAME_ELP_OUT4_L  	= "clearOutput4";
	private final String NAME_ELP_OUT5_L  	= "clearOutput5";
	private final String NAME_ELP_OUT6_L  	= "clearOutput6";
	private final String NAME_ELP_OUT7_L  	= "clearOutput7";
	private final String NAME_ELP_OUT8_L  	= "clearOutput8";
	
	private final int NUMBER_OF_OUTPUTS = 8;

    final IRuntimeEventListenerPort [] elpSetOutput    = new RuntimeEventListenerPortHigh[NUMBER_OF_OUTPUTS];    
    final IRuntimeEventListenerPort [] elpClearOutput  = new RuntimeEventListenerPortLow[NUMBER_OF_OUTPUTS];    
	
	private static final short GPIO_CIM_ID 			= 0x0801;
	private static final short LEGACY_GPIO_CIM_ID 			= 0x0201;
	
	private static final short GPIO_FEATURE_OUTPUT_STATE 	        = 0x10;
	private static final short GPIO_FEATURE_OUTPUT_PULLUP_STATE 	= 0x11;

	byte outputState = (byte) 0xff;
	byte propPullupState = 0;
	
    public DigitalOutInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    	port = CIMPortManager.getInstance().getConnection(GPIO_CIM_ID);
    	if (port == null)
    	{
    		port = CIMPortManager.getInstance().getConnection(LEGACY_GPIO_CIM_ID);
    	}
    	for (int i = 0; i < NUMBER_OF_OUTPUTS; i++)
		{
			elpSetOutput[i] = new RuntimeEventListenerPortHigh(this, i);
			elpClearOutput[i]  = new RuntimeEventListenerPortLow( this, i);
		}
    }
    public IRuntimeInputPort getInputPort(String portID)
    {
    	if("action".equalsIgnoreCase(portID))
        {
            return ipAction;
        }

        return null;
    }
    
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        if(NAME_ELP_OUT1_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[0];
        }
        else if(NAME_ELP_OUT2_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[1];
        }
        else if(NAME_ELP_OUT3_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[2];
        }
        else if(NAME_ELP_OUT4_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[3];
        }
        else if(NAME_ELP_OUT5_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[4];
        }
        else if(NAME_ELP_OUT6_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[5];
        }
        else if(NAME_ELP_OUT7_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[6];
        }
        else if(NAME_ELP_OUT8_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOutput[7];
        }
        else if(NAME_ELP_OUT1_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[0];
        }
        else if(NAME_ELP_OUT2_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[1];
        }
        else if(NAME_ELP_OUT3_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[2];
        }
        else if(NAME_ELP_OUT4_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[3];
        }
        else if(NAME_ELP_OUT5_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[4];
        }
        else if(NAME_ELP_OUT6_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[5];
        }
        else if(NAME_ELP_OUT7_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[6];
        }
        else if(NAME_ELP_OUT8_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOutput[7];
        }
        
        return null;
    }

    public Object getRuntimePropertyValue(String propertyName)
    {
    	if("pullupStateOut1".equalsIgnoreCase(propertyName))
        {
    		return ((propPullupState & 0x1) != 0);
        }
    	else if("pullupStateOut2".equalsIgnoreCase(propertyName))
        {
    		return ((propPullupState & 0x2) != 0);
        }    	
    	else if("pullupStateOut3".equalsIgnoreCase(propertyName))
        {
    		return ((propPullupState & 0x4) != 0);
        }    	
    	else if("pullupStateOut4".equalsIgnoreCase(propertyName))
        {
    		return ((propPullupState & 0x8) != 0);
        }
    	else if("uniqueID".equalsIgnoreCase(propertyName))
        {
            return propUniqueID;
        }
    	
    	
        return null;
    }

    public synchronized Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	if("pullupStateOut1".equalsIgnoreCase(propertyName))
        {
    		boolean value = ((String) newValue).equalsIgnoreCase("true") ? true: false;
    		if (value)
    			propPullupState |= 0x1;
    		else
    			propPullupState &= 0xe;
    		
    		byte [] pullup = { propPullupState };
    		if (port != null)
    		{
    			CIMPortManager.getInstance().sendPacket(port, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		}

        }
    	else if("pullupStateOut2".equalsIgnoreCase(propertyName))
        {
    		boolean value = ((String) newValue).equalsIgnoreCase("true") ? true: false;
    		if (value)
    			propPullupState |= 0x2;
    		else
    			propPullupState &= 0xd;
    		
    		byte [] pullup = { propPullupState };
    		if (port != null)
    		{
    			CIMPortManager.getInstance().sendPacket(port, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		}
        }    	
    	else if("pullupStateOut3".equalsIgnoreCase(propertyName))
        {
    		boolean value = ((String) newValue).equalsIgnoreCase("true") ? true: false;
    		if (value)
    			propPullupState |= 0x4;
    		else
    			propPullupState &= 0xb;
    		
    		byte [] pullup = { propPullupState };
    		if (port != null)
    		{
    			CIMPortManager.getInstance().sendPacket(port, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		}
        }    	
    	else if("pullupStateOut4".equalsIgnoreCase(propertyName))
        {
    		boolean value = ((String) newValue).equalsIgnoreCase("true") ? true: false;
    		if (value)
    			propPullupState |= 0x8;
    		else
    			propPullupState &= 0x7;

    		byte [] pullup = { propPullupState };
    		if (port != null)
    		{
    			CIMPortManager.getInstance().sendPacket(port, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		}
        }
    	else if("uniqueID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUniqueID;
			propUniqueID = (String)newValue;
			CIMPortController tempPort = openCIM(GPIO_CIM_ID, propUniqueID);
	    	if (tempPort == null) // legacy CIM available?
	    		tempPort = openCIM(LEGACY_GPIO_CIM_ID, propUniqueID);
			
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

        return null;
    }
    
    private String propUniqueID = "not used"; 
    
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
			Vector<Long> ids=CIMPortManager.getInstance()
				.getUniqueIdentifiersofCIMs(GPIO_CIM_ID);
			
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
     * Starts the module and sets up the outputs
     * 
     * This call will start the module and set to outputs to the value they 
     * have last been set to and deactivate the pull ups on all outputs
     */
    public void start()
    {
    	if (port == null)
    		port = openCIM(GPIO_CIM_ID, propUniqueID);
    	if (port == null) // legacy CIM available?
    		port = openCIM(LEGACY_GPIO_CIM_ID, propUniqueID);
    	super.start();
    	byte [] data = { outputState };
    	byte [] pullup = { propPullupState };
    	
    	if (port != null)
    	{
    		CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_OUTPUT_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		CIMPortManager.getInstance().sendPacket(port, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		
    	}
    	else
    	{
       		AstericsErrorHandling.instance.reportError(this, "Could not find DigitalOut CIM in PortManager. Please verify that the CIM Module is connected to an USB Port and that the driver is installed.");
    	}
    	AstericsErrorHandling.instance.reportInfo(this, "DigitalOutInstance started");
    }
    
    /**
     * Stops the component
     */
    @Override
    public void stop()
    {
        super.stop();
        port = null;
        AstericsErrorHandling.instance.reportInfo(this, "DigitalOutInstance stopped");
    }
    
    /**
     * Sets the level of the output port of (index + 1) to high
     * @param index the number of the output port - 1
     */
    public void setOutputBitHigh(int index)
    {
    	outputState |= 1 << index;
    	byte [] data = { outputState };
    	AstericsErrorHandling.instance.reportInfo(this, String.format("Sending byte %x to GPIO outputs", outputState));
    	if (port != null)
    	{
    		CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_OUTPUT_STATE, 
					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    	}
    }

    /**
     * Sets the level of the output port of (index + 1) to low
     * @param index the number of the output port - 1
     */
    public void setOutputBitLow(int index)
    {
    	outputState &= ~(1 << index);
    	byte [] data = { outputState };
    	AstericsErrorHandling.instance.reportInfo(this, String.format("Sending byte %x to GPIO outputs", outputState));
    	if (port != null)
    	{
    		CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_OUTPUT_STATE, 
					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    	}
    }
    
    /**
     * Inverts the level of the output port of (index + 1) 
     * @param index the number of the output port - 1
     */
    public void toggleOutputBit(int index)
    {
    	outputState ^= (1 << index);
    	byte [] data = { outputState };
    	AstericsErrorHandling.instance.reportInfo(this, String.format("Sending byte %x to GPIO outputs", outputState));
    	if (port != null)
    	{
    		CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_OUTPUT_STATE, 
					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    	}
    }
	
    /**
     * This class represents an event port which reacts to incoming events by
     * setting the level of their corresponding CIM output port to high
     *  
     * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
     */
	class RuntimeEventListenerPortHigh implements IRuntimeEventListenerPort
	{
		int index;
		DigitalOutInstance owner;
		
		/**
		 * The constructor
		 * 
		 * @param owner the DigitalOutInstance instance that owns this
		 * @param i the index of the CIM port
		 */
		public RuntimeEventListenerPortHigh(DigitalOutInstance owner, int i)
		{
			index = i;
			this.owner = owner;
		}
		
		/**
		 * Reacts to event and sets the output port
		 */
		public void receiveEvent(final String data)
	   	{
			AstericsErrorHandling.instance.reportInfo(owner, String.format("Received event on high listener port #" + index));
			owner.setOutputBitHigh(index);
	   	}
	}
	
    /**
     * This class represents an event port which reacts to incoming events by
     * setting the level of their corresponding CIM output port to low
     *  
     * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
     */
	class RuntimeEventListenerPortLow implements IRuntimeEventListenerPort
	{
		int index;
		DigitalOutInstance owner;
		
		/**
		 * The constructor
		 * 
		 * @param owner the DigitalOutInstance that owns this
		 * @param i the index of the CIM port
		 */
		public RuntimeEventListenerPortLow(DigitalOutInstance owner, int i)
		{
			index = i;
			this.owner = owner;
		}
		
		/**
		 * Reacts to event and clears the output port
		 */
		public void receiveEvent(final String data)
	   	{
			AstericsErrorHandling.instance.reportInfo(owner, String.format("Received event on low listener port #" + index));
			owner.setOutputBitLow(index);
	   	}
	}	
	
	
	/**
	 * The toggle thread is needed to make the press command work. It will run
	 * once and provide a press and release action within its runtime.
	 * 
	 * @author weissch
	 *
	 */
	class ToggleThread extends Thread {
		
		DigitalOutInstance owner;
		int portNum;
		
		/**
		 * The constructor
		 * @param portNum number of the port to perform the action on
		 */
		public ToggleThread(int portNum)
		{
			this.portNum = portNum;
		}
		
		/**
		 * Pulls the specified output port low and returns it to high after 
		 * 500ms 
		 */
		public void run()
		{
        	setOutputBitLow(portNum-1);

			try
			{
			   Thread.sleep(500);
			}
			catch (Exception e)
			{}
			setOutputBitHigh(portNum-1); 
		}
		
	}

	/**
	 * An input implementation handling the command strings "set", "clear",
	 * "toggle" and "press". Any command string has to be followed by a comma
	 * and the port number to perform the command on
	 * 
	 */
	private final IRuntimeInputPort ipAction
	    = new DefaultRuntimeInputPort()
		{
			public void receiveData(byte[] data)
			{
				String text = ConversionUtils.stringFromBytes(data);
				
	    		if (text.startsWith("@DIGITALOUT:")) 
	    		{  			
					try {	
						int portNum=-1;
						StringTokenizer st = new StringTokenizer(text.substring(12),",");

						String cmdType=st.nextToken();
						if (cmdType.equalsIgnoreCase("set"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<9))
		                        setOutputBitHigh(portNum-1); 
						}
						else if (cmdType.equalsIgnoreCase("clear"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<9))
		                        setOutputBitLow(portNum-1); 
						}
						else if (cmdType.equalsIgnoreCase("toggle"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<9))
		                        toggleOutputBit(portNum-1); 
						}	
						
						else if (cmdType.equalsIgnoreCase("press"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<9))
		                    {
		                    	AstericsThreadPool.instance.execute(new ToggleThread(portNum));
		                    }
						}
							
					} catch (Exception e) {
//TBD:	                	AstericsErrorHandling.instance.reportError(this, "Invalid CMD string: " + text );
						Logger.getAnonymousLogger().severe(e.toString());

					}
	    		}
			}

		};
}