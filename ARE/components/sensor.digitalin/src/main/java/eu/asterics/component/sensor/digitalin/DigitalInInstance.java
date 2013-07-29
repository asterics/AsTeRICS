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

package eu.asterics.component.sensor.digitalin;

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
 * DigitalInInstance works with the inputs of the GPIO CIM (ID 0x0201). It 
 * provides event triggers for state transitions (low to high and high to low) 
 * on each port of the CIM. 
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class DigitalInInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private CIMPortController port = null;
	
	private final String KEY_PROPERTY_CHANGE_EVENT_1 	= "activateEventIn1";
	private final String KEY_PROPERTY_CHANGE_EVENT_2 	= "activateEventIn2";
	private final String KEY_PROPERTY_CHANGE_EVENT_3 	= "activateEventIn3";
	private final String KEY_PROPERTY_CHANGE_EVENT_4 	= "activateEventIn4";
	private final String KEY_PROPERTY_CHANGE_EVENT_5 	= "activateEventIn5";
	private final String KEY_PROPERTY_CHANGE_EVENT_6 	= "activateEventIn6";
	private final String KEY_PROPERTY_CHANGE_EVENT_7 	= "activateEventIn7";
	private final String KEY_PROPERTY_CHANGE_EVENT_8 	= "activateEventIn8";
	private final String KEY_PROPERTY_PERIODIC_UPDATE   = "periodicUpdate";

	private final String NAME_ETP_IN1_H  	= "in1High";
	private final String NAME_ETP_IN2_H  	= "in2High";
	private final String NAME_ETP_IN3_H  	= "in3High";
	private final String NAME_ETP_IN4_H  	= "in4High";
	private final String NAME_ETP_IN5_H  	= "in5High";
	private final String NAME_ETP_IN6_H  	= "in6High";
	private final String NAME_ETP_IN7_H  	= "in7High";
	private final String NAME_ETP_IN8_H  	= "in8High";
	private final String NAME_ETP_IN1_L  	= "in1Low";
	private final String NAME_ETP_IN2_L  	= "in2Low";
	private final String NAME_ETP_IN3_L  	= "in3Low";
	private final String NAME_ETP_IN4_L  	= "in4Low";
	private final String NAME_ETP_IN5_L  	= "in5Low";
	private final String NAME_ETP_IN6_L  	= "in6Low";
	private final String NAME_ETP_IN7_L  	= "in7Low";
	private final String NAME_ETP_IN8_L  	= "in8Low";
	
	private final int NUMBER_OF_INPUTS = 8;
	private final boolean throwRuntimeExcetions = false;

    final IRuntimeEventTriggererPort [] etpInHigh = new DefaultRuntimeEventTriggererPort[NUMBER_OF_INPUTS];    
    final IRuntimeEventTriggererPort [] etpInLow  = new DefaultRuntimeEventTriggererPort[NUMBER_OF_INPUTS];    
	
	private static final short GPIO_CIM_ID 			= 0x0701;
	private static final short GPIO_LEGACY_CIM_ID	= 0x0201;
	private static final short WGPIO_CIM_ID 		= 0x0B01;
	
	private static final short GPIO_FEATURE_INPUT_CHANGE_EVENT 	= 0x04;
	private static final short GPIO_FEATURE_PERIODIC_UPDATE 	= 0x05;

    private byte 	propChangeEventRegister = 0;
    private short 	propPeriodicUpdate = 0;
    private short 	propDefaultCIMID = 0x0701;
    private String 	propUniqueID = "not used";

    private boolean wirelessCIM; 
    private byte 	inputValues = (byte) 0xff;
    
	/**
	 * Constructs the component and its event triggerer port arrays
	 */
    public DigitalInInstance()
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
        else if(NAME_ETP_IN5_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[4];
        }
        else if(NAME_ETP_IN6_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[5];
        }
        else if(NAME_ETP_IN7_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[6];
        }
        else if(NAME_ETP_IN8_H.equalsIgnoreCase(eventPortID))
        {
            return etpInHigh[7];
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
        else if(NAME_ETP_IN5_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[4];
        }
        else if(NAME_ETP_IN6_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[5];
        }
        else if(NAME_ETP_IN7_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[6];
        }
        else if(NAME_ETP_IN8_L.equalsIgnoreCase(eventPortID))
        {
            return etpInLow[7];
        }
        
        return null;
    }
 
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
        else if(KEY_PROPERTY_CHANGE_EVENT_5.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x10) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_6.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x20) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_7.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x40) == 0) ? false : true) ;
        }
        else if(KEY_PROPERTY_CHANGE_EVENT_8.equalsIgnoreCase(propertyName))
        {
            return ( ((propChangeEventRegister & 0x80) == 0) ? false : true) ;
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
	    	else if("uniqueID".equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propUniqueID;
				propUniqueID = (String)newValue;
				
				CIMPortController tempPort = openCIM(propDefaultCIMID, propUniqueID);

				if (tempPort != null)
				{
					port=tempPort;
					if (    (!wirelessCIM) &&
							(!propUniqueID.equals("")) && 
							(!propUniqueID.equals("not used")))
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
	            	AstericsErrorHandling.instance.reportError(this, "Invalid property value for " + propertyName + ": " + newValue);
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
	            else if(KEY_PROPERTY_CHANGE_EVENT_5.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 4;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_6.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 5;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_7.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 6;
	            }
	            else if(KEY_PROPERTY_CHANGE_EVENT_8.equalsIgnoreCase(propertyName))
	            {
	            	shiftValue = 7;
	            }
	        	
	        	if (maskValue == 1)
	        	{
	            	propChangeEventRegister |=   maskValue << shiftValue; 
	        	}
	        	else
	        	{
	        		propChangeEventRegister &= ~(maskValue << shiftValue);
	        	}
	        	
	        	// AstericsErrorHandling.instance.reportInfo(this, String.format("Setting input change event register to %x", propChangeEventRegister));
	        	
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
        	AstericsErrorHandling.instance.reportError(this, "Invalid property value for " + propertyName + ": " + newValue);
        }
        catch (NullPointerException npe)
        {
       		AstericsErrorHandling.instance.reportError(this, String.format("Could not find CIM 0x%x in PortManager", GPIO_CIM_ID ));
        }    	
        return null;
    }
    
    
    /**
     * Opens a CIM controller for a certain unique ID 
     * @param cimId the CIM type ID of the CIM 
     * @param uniqueID the unique ID of the CIM as a string
     * @return the CIM controller for the corresponding CIM, null if not present
     */
	private CIMPortController openCIM(short cimID, String uniqueID)
	{
		if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID==""))
		{
		    return (CIMPortManager.getInstance().getConnection(cimID));
		}
		else
		{
			try {
				short id = Short.decode(propUniqueID.substring(0, propUniqueID.indexOf('-')));
				long  uid = Long.decode(propUniqueID.substring(propUniqueID.indexOf('-') + 1));
				System.out.println(String.format("Trying to get: id %x uid %x", id, uid));
				if (id == (short) 0x0b01)
				{
					wirelessCIM = true;
					return (CIMPortManager.getInstance().getWirelessConnection(id, uid));
				}
				wirelessCIM = false;
				return (CIMPortManager.getInstance().getConnection(id, uid));
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
		System.out.println("DigitalInInstance.getRuntimePropertyList");

		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("uniqueID")==0)
		{
			res.add("not used");
			String s;
			// get wired digital in CIMs (0x0701)
			Vector<Long> ids=CIMPortManager.getInstance()
				.getUniqueIdentifiersofCIMs(GPIO_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					s = String.format("0x%x-0x%x", GPIO_CIM_ID, l);
					res.add(s);
					System.out.println(" 0x0701 found unique ID: "+s);
				}
			}
			
			// get wired legacy digital in CIMs (0x201)
			ids=CIMPortManager.getInstance()
			.getUniqueIdentifiersofCIMs(GPIO_LEGACY_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					s = String.format("0x%x-0x%x", GPIO_LEGACY_CIM_ID, l);
					res.add(s);
					System.out.println(" 0x0201 found unique ID: "+s);
				}
			}
					
			// get wireless digital in CIMs
			ids=CIMPortManager.getInstance()
				.getUniqueIdentifiersofWirelessCIMs(WGPIO_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					s = String.format("0x%x-0x%x", WGPIO_CIM_ID, l);
					res.add(s);
					System.out.println(" 0x0b01 found wireless unique ID: "+s);
				}
			}
		}
		return res;
	} 
    
    
    /**
     * Starts the component, gets the port controller and reports error on fail
     */
    public void start()
    {
    	if (port == null)
    	{
    		port = openCIM(propDefaultCIMID, propUniqueID);
    	}
    	
        if (port != null )
        {
        	port.addEventListener(this);
        	byte [] data = { propChangeEventRegister };
        	if (!wirelessCIM)
        	{
	        	CIMPortManager.getInstance().sendPacket(port, data, GPIO_FEATURE_INPUT_CHANGE_EVENT, 
	  					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
	        	byte [] leBytes = ConversionUtils.shortToBytesLittleEndian(propPeriodicUpdate);
	        	CIMPortManager.getInstance().sendPacket(port, leBytes, GPIO_FEATURE_PERIODIC_UPDATE, 
						      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
        	}
        	super.start();
        	AstericsErrorHandling.instance.reportInfo(this, "DigitalInInstance started");
        }
        else
        {
        	AstericsErrorHandling.instance.reportError(this, "Could not find port controller for digital In CIM or legacy GPIO ");
        }    	
    }
    
    /**
     * Stops the component and releases the port controller
     */
    @Override
    public void stop()
    {
        super.stop();
//        CIMPortController port = CIMPortManager.getInstance().getConnection(GPIO_CIM_ID);
        if (port != null )
        {
        	port.removeEventListener(this);
        	port = null;
        }
        AstericsErrorHandling.instance.reportInfo(this, "DigitalInInstance stopped");
    }
    
    /**
     * Handles incoming packets and raises events if necessary. Called when new 
     * packets arrive from the GPIO CIM.  
     */
	public void handlePacketReceived(CIMEvent e)
	{
		if (wirelessCIM)
		{
			CIMWirelessDataEvent ev = (CIMWirelessDataEvent) e;
			byte [] b = ev.data;
			for (int i = 0; i < NUMBER_OF_INPUTS; i++)
			{
				byte was = (byte) ((inputValues >> i) & (byte) 0x1);
				byte is  = (byte) ((b[0]        >> i) & (byte) 0x1);
				
				if (is > was)
				{
					//AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
					etpInHigh[i].raiseEvent();
					inputValues |= (1 << i);
				}			
				if (is < was)
				{
					//AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
					etpInLow[i].raiseEvent();
					inputValues &= ~( ((byte) 1) << i);
				}
			}
		}
		else
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
							//AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
							etpInHigh[i].raiseEvent();
							inputValues |= (1 << i);
						}			
						if (is < was)
						{
							//AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
							etpInLow[i].raiseEvent();
							inputValues &= ~( ((byte) 1) << i);
						}
					}
					else
					{
						if (is==0)
						{
							//AstericsErrorHandling.instance.reportInfo(this, "Raising low trigger on " + i);
							etpInLow[i].raiseEvent();
							inputValues &= ~( ((byte) 1) << i);
						}
						else
						{
				    		//AstericsErrorHandling.instance.reportInfo(this, "Raising high trigger on " + i);
							etpInHigh[i].raiseEvent();
							inputValues |= (1 << i);
						}			
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