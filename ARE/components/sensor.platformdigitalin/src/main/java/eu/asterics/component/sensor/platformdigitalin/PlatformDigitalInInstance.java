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

package eu.asterics.component.sensor.platformdigitalin;


import java.util.*;

import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;



/**
 * PlatformDigitalIn works with the inputs of the Core CIM (ID 0x0601). 
 * It provides event triggers for state transitions (low to high and high to 
 * low) on each port of the CIM. 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class PlatformDigitalInInstance extends AbstractRuntimeComponentInstance
	implements CIMEventHandler
{
	
	// Constants
	private final String KEY_PROPERTY_CHANGE_EVENT_1 	= "activateEventIn1";
	private final String KEY_PROPERTY_CHANGE_EVENT_2 	= "activateEventIn2";
	private final String KEY_PROPERTY_CHANGE_EVENT_3 	= "activateEventIn3";
	private final String KEY_PROPERTY_CHANGE_EVENT_4 	= "activateEventIn4";
	private final String KEY_PROPERTY_PERIODIC_UPDATE   = "periodicUpdate";

	private final String NAME_ETP_IN1_H  	= "in1High";
	private final String NAME_ETP_IN2_H  	= "in2High";
	private final String NAME_ETP_IN3_H  	= "in3High";
	private final String NAME_ETP_IN4_H  	= "in4High";
	private final String NAME_ETP_IN1_L  	= "in1Low";
	private final String NAME_ETP_IN2_L  	= "in2Low";
	private final String NAME_ETP_IN3_L  	= "in3Low";
	private final String NAME_ETP_IN4_L  	= "in4Low";
	
	private int NUMBER_OF_INPUTS = 4;
	private static final long REFRESH_INTERVAL 	= 13;
	
	private static final short PLATFORM_GPIO_CIM_ID 			= 0x0601;
	private static final short PLATFORM_GPIO_CIM_V2_ID 			= 0x0602;
	private static final short GPIO_FEATURE_INPUT_CHANGE_EVENT 	= 0x04;
	private static final short GPIO_FEATURE_PERIODIC_UPDATE 	= 0x05;
	
	// internals
	private final boolean throwRuntimeExcetions = false;
	private CIMPortController port = null;
	private int diff_x = 0;
	private int diff_y = 0;
	private long last_update = 0;
	
	// event ports
    final IRuntimeEventTriggererPort [] etpInHigh = new DefaultRuntimeEventTriggererPort[NUMBER_OF_INPUTS];    
    final IRuntimeEventTriggererPort [] etpInLow  = new DefaultRuntimeEventTriggererPort[NUMBER_OF_INPUTS];    
	
	
	/**
	 * Constructs the component and its event triggerer port arrays
	 */
    public PlatformDigitalInInstance()
    {
        // empty constructor - needed for OSGi service factory operations

    	for (int i = 0; i < NUMBER_OF_INPUTS; i++)
		{
			etpInHigh[i] = new DefaultRuntimeEventTriggererPort();
			etpInLow[i]  = new DefaultRuntimeEventTriggererPort();
		}
    }

    /**
     * Returns the event triggerer port instances for a requested port ID
     * @param eventPortID the requested ID
     * @return the requested port instance
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        if(NAME_ETP_IN1_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[0];
        }
        else if(NAME_ETP_IN2_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[1];
        }
        else if(NAME_ETP_IN3_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[2];
        }
        else if(NAME_ETP_IN4_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[3];
        }
        else if(NAME_ETP_IN1_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[0];
        }
        else if(NAME_ETP_IN2_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[1];
        }
        else if(NAME_ETP_IN3_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[2];
        }
        else if(NAME_ETP_IN4_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[3];
        }
        
        return null;
    }

    private byte propChangeEventRegister = 0;

    private short propPeriodicUpdate = 0;
    private byte inputValues = (byte) 0xff;
    
    /**
     * Returns the value of a specified component property
     * @param propertyName the name of the requested property
     * @return the value of the property as an Object
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
        if(KEY_PROPERTY_CHANGE_EVENT_1.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x1) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_2.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x2) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_3.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x4) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_4.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x8) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName))
        {
        	return propPeriodicUpdate;
        }
        return null;
    }

    /**
     * Sets a new value for a specifed property
     * @param propertyName the name of the requested property
     * @param newValue the new value for the property
     * @return the old value of the property as an Object
    */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	try
    	{
	    	if(KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName))
	        {
	        	propPeriodicUpdate = Short.parseShort(newValue.toString());
	        	
	        	if (port != null)
	        	{
		        	byte [] leBytes = ConversionUtils.shortToBytesLittleEndian(propPeriodicUpdate);
		        	
		        	CIMPortManager.getInstance().sendPacket(port, leBytes, GPIO_FEATURE_PERIODIC_UPDATE, 
							      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
	        	}
	        }
	    	else 
	        {
	    		int maskValue = -1;
	    		int shiftValue = -1;
	    		
	            if("true".equalsIgnoreCase((String)newValue))
	            {
	            	maskValue = 1;
	            }
	            else if("false".equalsIgnoreCase((String)newValue))
	            {
	            	maskValue = 0;
	            }
	            else
	            {
	            	AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
	            }
	        	
	        	if(KEY_PROPERTY_CHANGE_EVENT_1.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 0;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_2.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 1;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_3.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 2;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_4.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 3;
	            }
	        	
	        	if (maskValue == 1)
	        	{
	            	propChangeEventRegister |=   maskValue << shiftValue; 
	        	}
	        	else
	        	{
	        		propChangeEventRegister &= ~(maskValue << shiftValue);
	        	}
	        	
	        	AstericsErrorHandling.instance.reportInfo(this, String.format("Setting input change event register to %x", propChangeEventRegister));
	        	
	        	if (port != null)
	        	{
		        	byte [] data = { propChangeEventRegister };
		        	CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_INPUT_CHANGE_EVENT, 
		  					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
	        	}
	        	return propChangeEventRegister;
	        }
    	}
        catch (NumberFormatException nfe)
        {
        	AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
        }
        catch (NullPointerException npe)
        {
       		AstericsErrorHandling.instance.reportError(this, 
       				"Could not find AsTeRICS Personal platform - needed by the PlatformDigitalIn plugin");

        }    	
        return null;
    }
    
    /**
     * Starts the component, gets the port controller and reports error on fail
     */
    public void start()
    {
    	System.out.println("PlatformDigitalInInstance.PlatformDigitalInInstance()");
    	port = CIMPortManager.getInstance().getConnection(PLATFORM_GPIO_CIM_V2_ID);
    	if (port == null)
    	{
    		port = CIMPortManager.getInstance().getConnection(PLATFORM_GPIO_CIM_ID);
    	}
        if (port != null )
        {
        	port.addEventListener(this);
        	byte [] data = { propChangeEventRegister };
        	CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_INPUT_CHANGE_EVENT, 
  					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
        	
        	super.start();
//       	AstericsErrorHandling.instance.reportInfo(this, "PlatformDigitalIn started");
        }
        else
        {
        	AstericsErrorHandling.instance.reportError(this,
       				"Could not find AsTeRICS Personal platform - needed by the PlatformDigitalIn plugin");

        }    	
    	
    	System.out.println("PlatformDigitalIn.start()");
    	super.start();
    }
    
    /**
     * Stops the component and releases the port controller
     */
    @Override
    public void stop()
    {
    	System.out.println("PlatformDigitalIn.stop()");
        super.stop();
        if (port != null )
        {
        	port.removeEventListener(this);
        }
        AstericsErrorHandling.instance.reportInfo(this, "PlatformDigitalIn stopped");
    }
    
    
    /**
     * Handles incoming packets and raises events if necessary. Called when new 
     * packets arrive from the GPIO CIM.  
     */
	public void handlePacketReceived(CIMEvent e)
	{
		CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
		CIMProtocolPacket packet = ev.packet;
		if ( ((packet.getSerialNumber() & 0x80) != 0) && (packet.getFeatureAddress() == 0x1) )
		{
			byte [] b = packet.getData();		
			for (int i = 0; i < NUMBER_OF_INPUTS; i++)
			{
				byte was = (byte) ((inputValues >> i) & (byte) 0x1);
				byte is  = (byte) ((b[0]        >> i) & (byte) 0x1);
				
				if (propPeriodicUpdate == 0)
				{
					if (is > was)
					{
			    		//System.out.println("RAISING HIGH on port "+i);
//						AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
						etpInHigh[i].raiseEvent();
						inputValues |= (1 << i);
					}			
					if (is < was)
					{
			    		//System.out.println("RAISING LOW on port "+i);
//						AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
						etpInLow[i].raiseEvent();
						inputValues &= ~( ((byte) 1) << i);
					}
				}
				else
				{
					if (is==0)
					{
			    		//System.out.println("PERIODIC RAISING LOW on port "+i);
//						AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
						etpInLow[i].raiseEvent();
						inputValues &= ~( ((byte) 1) << i);
					}
					else
					{
			    		//System.out.println("PERIODIC RAISING HIGH on port "+i);
//			    		AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
						etpInHigh[i].raiseEvent();
						inputValues |= (1 << i);
					}			
					
				}
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