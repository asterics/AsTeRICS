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

package eu.asterics.component.actuator.platformdigitalout;
 
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

/**
 * PlatformDigitalOutInstance is a class which represents the general purpose 
 * output ports of the core CIM on the platform. It is implemented as an 
 * actuator and uses event listener inputs to set or clear each output.
 * 
 * The class can work with incoming commands prepended with "@GPIO:".
 * The available commands are "set" for setting, "clear" for 
 * clearing and "toggle" for inverting an output. The example syntax of a
 * command is "@GPIO:set,1". 
 *  
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 */
public class PlatformDigitalOutInstance extends AbstractRuntimeComponentInstance 
{
	private CIMPortController serialPortController = null;
	
	private final String NAME_ELP_OUT1_H  	= "setOut1";
	private final String NAME_ELP_OUT2_H  	= "setOut2";
	private final String NAME_ELP_OUT1_L  	= "clearOut1";
	private final String NAME_ELP_OUT2_L  	= "clearOut2";
	
	private final int NUMBER_OF_OUTPUTS = 2;

    final IRuntimeEventListenerPort [] elpSetOut    = new RuntimeEventListenerPortHigh[NUMBER_OF_OUTPUTS];    
    final IRuntimeEventListenerPort [] elpClearOut  = new RuntimeEventListenerPortLow[NUMBER_OF_OUTPUTS];    
	
	private static final short PLATFORM_CORE_CIM_V2_ID 			= 0x0602;
	
	private static final short GPIO_FEATURE_OUTPUT_STATE 	        = 0x10;
	private static final short GPIO_FEATURE_OUTPUT_PULLUP_STATE 	= 0x11;

	byte outputState = (byte) 0xff;
	byte pullupState = 0;
	
	/**
	 * The base constructor opens the connection to the CIM and creates all
	 * port instances.
	 * 
	 */
    public PlatformDigitalOutInstance()
    {
    	serialPortController = CIMPortManager.getInstance().getConnection(PLATFORM_CORE_CIM_V2_ID);
		for (int i = 0; i < NUMBER_OF_OUTPUTS; i++)
		{
			elpSetOut[i] = new RuntimeEventListenerPortHigh(this, i);
			elpClearOut[i]  = new RuntimeEventListenerPortLow( this, i);
		}
    }
    
    /**
     * Returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
    	if("command".equalsIgnoreCase(portID))
        {
            return ipCommandPort;
        }

        return null;
    }

    /**
     * Returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        return null;
    }
    
    /**
     * Returns an event listener port
     * @param eventPortID the name of the event port    
     * @return the instance of the event port or null if non existant
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        if(NAME_ELP_OUT1_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOut[0];
        }
        else if(NAME_ELP_OUT2_H.equalsIgnoreCase(eventPortID))
        {
            return elpSetOut[1];
        }
        else if(NAME_ELP_OUT1_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOut[0];
        }
        else if(NAME_ELP_OUT2_L.equalsIgnoreCase(eventPortID))
        {
            return elpClearOut[1];
        }
        return null;
    }

    /**
     * Returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if("pullupOutput1".equalsIgnoreCase(propertyName))
		{
			if ((pullupState & 1) != 0)
				return true;
			return false;
		} 
		else if("pullupOutput2".equalsIgnoreCase(propertyName))
		{
			if ((pullupState & 2) != 0)
				return true;
			return false;
		}
        return null;
    }

    /**
     * Sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if("pullupOutput1".equalsIgnoreCase(propertyName))
		{
			if("true".equalsIgnoreCase((String)newValue))
				pullupState |= 1;
			else 
				pullupState &= 0xfe;
		} 
		else if("pullupOutput2".equalsIgnoreCase(propertyName))
		{
			if("true".equalsIgnoreCase((String)newValue))
				pullupState |= 2;
			else 
				pullupState &= 0xfd;
		}
        return null;
    }
    
    /**
     * Starts the module and sets up the outputs
     * 
     * This call will start the module and set to outputs to the value they 
     * have last been set to and deactivate the pull ups on all outputs
     */
    public void start()
    {
    	super.start();
    	byte [] data = { outputState };
    	byte [] pullup = {  pullupState };
    	if (serialPortController != null)
    	{
    		CIMPortManager.getInstance().sendPacket(serialPortController, data, GPIO_FEATURE_OUTPUT_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    		CIMPortManager.getInstance().sendPacket(serialPortController, pullup, GPIO_FEATURE_OUTPUT_PULLUP_STATE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    	}
    	else
    	{
    		AstericsErrorHandling.instance.reportError(this, 
    			"The AsTeRICS platform could not be found - it is necessary for the PlatformDigitalOut plugin.");
    	}
    	AstericsErrorHandling.instance.reportInfo(this, 
    			"PlatformDigitalOut started");
    }
    
    /**
     * Stops the module
     */
    @Override
    public void stop()
    {
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, 
        		"PlatformDigitalOut stopped");
    }
    
    /**
     * Sets the level of the output port of (index + 1) to high
     * @param index the number of the output port - 1
     */
    public void setOutputBitHigh(int index)
    {
    	outputState |= 1 << index;
    	byte [] data = { outputState };
    	AstericsErrorHandling.instance.reportInfo(this, 
    			String.format("Sending byte %x to CORE CIM GP outputs", outputState));
    	if (serialPortController != null)
    	{
    		CIMPortManager.getInstance().sendPacket(serialPortController, data, GPIO_FEATURE_OUTPUT_STATE, 
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
    	AstericsErrorHandling.instance.reportInfo(this, 
    			String.format("Sending byte %x to CORE CIM GP outputs", outputState));
    	if (serialPortController != null)
    	{
    		CIMPortManager.getInstance().sendPacket(serialPortController, data, GPIO_FEATURE_OUTPUT_STATE, 
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
    	AstericsErrorHandling.instance.reportInfo(this, 
    		String.format("Sending byte %x to CORE CIM GP outputs", outputState));
    	if (serialPortController != null)
    	{
    		CIMPortManager.getInstance().sendPacket(serialPortController, data, GPIO_FEATURE_OUTPUT_STATE, 
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
		PlatformDigitalOutInstance owner;
		
		/**
		 * The constructor
		 * 
		 * @param owner the CoreCimGpioOutputInstance instance that owns this
		 * @param i the index of the CIM port
		 */
		public RuntimeEventListenerPortHigh(PlatformDigitalOutInstance owner, int i)
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
		PlatformDigitalOutInstance owner;
		
		/**
		 * The constructor
		 * 
		 * @param owner the CoreCimGpioOutputInstance instance that owns this
		 * @param i the index of the CIM port
		 */
		public RuntimeEventListenerPortLow(PlatformDigitalOutInstance owner, int i)
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
	

	  private final IRuntimeInputPort ipCommandPort
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
		                    if ((portNum>0) && (portNum<2))
		                        setOutputBitHigh(portNum-1); 
						}
						else if (cmdType.equalsIgnoreCase("clear"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<2))
		                        setOutputBitLow(portNum-1); 
						}
						else if (cmdType.equalsIgnoreCase("toggle"))
						{
							String portToken=st.nextToken();
		                    portNum = Integer.parseInt(portToken);
		                    if ((portNum>0) && (portNum<2))
		                        toggleOutputBit(portNum-1); 
						}	
					} catch (Exception e) {
//TBD:	                	AstericsErrorHandling.instance.reportError(this, "Invalid CMD string: " + text );
						Logger.getAnonymousLogger().severe(e.toString());

					}
	    		}
			}

		};
}