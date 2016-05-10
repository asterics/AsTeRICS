

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

package eu.asterics.component.actuator.enocean;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;
import java.util.logging.Logger;


import java.net.UnknownHostException;

import at.technikum_wien.embsys.aat.PriscillaCore.enums.BinaryState;
import at.technikum_wien.embsys.aat.PriscillaCore.enums.ErrorCodeSend;
import at.technikum_wien.embsys.aat.PriscillaCore.enums.EventSend;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEvent;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventBinary;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventError;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventFan;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventGetGatewayID;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventHumidity;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventIllumination;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventLearnTelegram;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventSetPoint;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventSupplyVoltage;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventTemperature;
import at.technikum_wien.embsys.aat.PriscillaCore.event.FrameEventWindowHandle;
import at.technikum_wien.embsys.aat.PriscillaCore.link.EnOceanLinkImpl;
import at.technikum_wien.embsys.aat.PriscillaCore.link.event.ILinkListener;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

/* 
 * This is a plugin for interfacing with an EnOcean network.
 * The communication is based on the Priscilla library, developed
 * by the UAS FH Technikum Wien (Benjamin Aigner and Richard Wagner)
 * 
 *  
 * @author Benjamin Aigner [aignerb@technikum-wien.at]
 *         Date: 26.12.2013
 *         Time: 
 */
public class EnoceanInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	EnOceanLinkImpl enoceanLink = null;

	// Properties
	private boolean propUSB = true;
	private String propLocalIP = new String();
	private String propGatewayIP = new String();
	
	
	private String propSendID1 = new String();
	private String propDataType1 = new String();
	private String propDataValue1 = new String();
	private String propSendID2 = new String();
	private String propDataType2 = new String();
	private String propDataValue2 = new String();
	private String propSendID3 = new String();
	private String propDataType3 = new String();
	private String propDataValue3 = new String();
	private String propSendID4 = new String();
	private String propDataType4 = new String();
	private String propDataValue4 = new String();
	private String propSendID5 = new String();
	private String propDataType5 = new String();
	private String propDataValue5 = new String();
	private String propSendID6 = new String();
	private String propDataType6 = new String();
	private String propDataValue6 = new String();
	
	private String propIDSlider1 = new String();
	private String propIDSlider2 = new String();
	private String propIDSlider3 = new String();
	private String propIDSlider4 = new String();
	private String propIDSlider5 = new String();
	private String propIDSlider6 = new String();
	
	private String propTypeSlider1 = new String();
	private String propTypeSlider2 = new String();
	private String propTypeSlider3 = new String();
	private String propTypeSlider4 = new String();
	private String propTypeSlider5 = new String();
	private String propTypeSlider6 = new String();
	// declare member variables here

	// Event Listener Ports
	private final String ELP_SEND1 	= "send1";
	private final String ELP_SEND2 	= "send2";
	private final String ELP_SEND3 	= "send3";
	private final String ELP_SEND4 	= "send4";
	private final String ELP_SEND5 	= "send5";		
	private final String ELP_SEND6 	= "send6";
	
	private String propIDTrigger1 = new String();
	private String propIDTrigger2 = new String();
	private String propIDTrigger3 = new String();
	private String propIDTrigger4 = new String();
	private String propIDTrigger5 = new String();
	private String propIDTrigger6 = new String();
	private String propTypeTrigger1 = new String();
	private String propTypeTrigger2 = new String();
	private String propTypeTrigger3 = new String();
	private String propTypeTrigger4 = new String();
	private String propTypeTrigger5 = new String();
	private String propTypeTrigger6 = new String();
	
	final IRuntimeEventTriggererPort runtimeEventTriggererPort1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort6 = new DefaultRuntimeEventTriggererPort();
	
	final IRuntimeOutputPort opValue1 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opValue2 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opValue3 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opValue4 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opValue5 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opValue6 = new DefaultRuntimeOutputPort();
	
    
	
	private double in1=0,in2=0,in3=0,in4=0,in5=0,in6=0;
   /**
    * The class constructor.
    */
    public EnoceanInstance()
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
		if ("command".equalsIgnoreCase(portID))
		{
			return ipCommand;
		}
		if ("slider1".equalsIgnoreCase(portID))
		{
			return ipSlider1;
		}
		if ("slider2".equalsIgnoreCase(portID))
		{
			return ipSlider2;
		}
		if ("slider3".equalsIgnoreCase(portID))
		{
			return ipSlider3;
		}
		if ("slider4".equalsIgnoreCase(portID))
		{
			return ipSlider4;
		}
		if ("slider5".equalsIgnoreCase(portID))
		{
			return ipSlider5;
		}
		if ("slider6".equalsIgnoreCase(portID))
		{
			return ipSlider6;
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
		if ("output1".equalsIgnoreCase(portID))
		{
			return opValue1;
		}
		
		if ("output2".equalsIgnoreCase(portID))
		{
			return opValue2;
		}
		
		if ("output3".equalsIgnoreCase(portID))
		{
			return opValue3;
		}
		
		if ("output4".equalsIgnoreCase(portID))
		{
			return opValue4;
		}
		
		if ("output5".equalsIgnoreCase(portID))
		{
			return opValue5;
		}
		
		if ("output6".equalsIgnoreCase(portID))
		{
			return opValue6;
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
		if ("send1".equalsIgnoreCase(eventPortID))
		{
			return elpSend1;
		}
		if ("send2".equalsIgnoreCase(eventPortID))
		{
			return elpSend2;
		}
		if ("send3".equalsIgnoreCase(eventPortID))
		{
			return elpSend3;
		}
		if ("send4".equalsIgnoreCase(eventPortID))
		{
			return elpSend4;
		}
		if ("send5".equalsIgnoreCase(eventPortID))
		{
			return elpSend5;
		}
		if ("send6".equalsIgnoreCase(eventPortID))
		{
			return elpSend6;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    
    

    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        if("event_out_1".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort1;
        }
        else if("event_out_2".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort2;
        }
        else if("event_out_3".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort3;
        }
        else if("event_out_4".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort4;
        }
        else if("event_out_5".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort5;
        }
        else if("event_out_6".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort6;
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
    	if("localIP".equalsIgnoreCase(propertyName))
        {
            return propLocalIP;
        }
        else if("gatewayIP".equalsIgnoreCase(propertyName))
        {
        	return propGatewayIP;
        }
        else if("USB".equalsIgnoreCase(propertyName))
        {
        	return propUSB;
        }
        else if("id1".equalsIgnoreCase(propertyName))
        {
        	return propSendID1;
        }
        else if("dataType1".equalsIgnoreCase(propertyName))
        {
        	return propDataType1;
        }
        else if("dataValue1".equalsIgnoreCase(propertyName))
        {
        	return propDataValue1;
        }
        else if("id2".equalsIgnoreCase(propertyName))
        {
        	return propSendID2;
        }
        else if("dataType2".equalsIgnoreCase(propertyName))
        {
        	return propDataType2;
        }
        else if("dataValue2".equalsIgnoreCase(propertyName))
        {
        	return propDataValue2;
        }
        else if("id3".equalsIgnoreCase(propertyName))
        {
        	return propSendID3;
        }
        else if("dataType3".equalsIgnoreCase(propertyName))
        {
        	return propDataType3;
        }
        else if("dataValue3".equalsIgnoreCase(propertyName))
        {
        	return propDataValue3;
        }
        else if("id4".equalsIgnoreCase(propertyName))
        {
        	return propSendID4;
        }
        else if("dataType4".equalsIgnoreCase(propertyName))
        {
        	return propDataType4;
        }
        else if("dataValue4".equalsIgnoreCase(propertyName))
        {
        	return propDataValue4;
        }
        else if("id5".equalsIgnoreCase(propertyName))
        {
        	return propSendID5;
        }
        else if("dataType5".equalsIgnoreCase(propertyName))
        {
        	return propDataType5;
        }
        else if("dataValue5".equalsIgnoreCase(propertyName))
        {
        	return propDataValue5;
        }
        else if("id6".equalsIgnoreCase(propertyName))
        {
        	return propSendID6;
        }
        else if("dataType6".equalsIgnoreCase(propertyName))
        {
        	return propDataType6;
        }
        else if("dataValue6".equalsIgnoreCase(propertyName))
        {
        	return propDataValue6;
        }
        else if("IDSlider1".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider1;
        }
        else if("sendTypeSlider1".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider1;
        }
        else if("IDSlider2".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider2;
        }
        else if("sendTypeSlider2".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider2;
        }
        else if("IDSlider3".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider3;
        }
        else if("sendTypeSlider3".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider3;
        }
        else if("IDSlider4".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider4;
        }
        else if("sendTypeSlider4".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider4;
        }
        else if("IDSlider5".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider5;
        }
        else if("sendTypeSlider5".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider5;
        }
        else if("IDSlider6".equalsIgnoreCase(propertyName))
        {
        	return propIDSlider6;
        }
        else if("sendTypeSlider6".equalsIgnoreCase(propertyName))
        {
        	return propTypeSlider6;
        }
        else if("IDTrigger1".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger1;
        }
        else if("IDTrigger2".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger2;
        }
        else if("IDTrigger3".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger3;
        }
        else if("IDTrigger4".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger4;
        }
        else if("IDTrigger5".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger5;
        }
        else if("IDTrigger6".equalsIgnoreCase(propertyName))
        {
        	return propIDTrigger6;
        }
        else if("TypeTrigger1".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger1;
        }
        else if("TypeTrigger2".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger2;
        }
        else if("TypeTrigger3".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger3;
        }
        else if("TypeTrigger4".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger4;
        }
        else if("TypeTrigger5".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger5;
        }
        else if("TypeTrigger6".equalsIgnoreCase(propertyName))
        {
        	return propTypeTrigger6;
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
    	Object oldValue = null;
        if("localIP".equalsIgnoreCase(propertyName)) {
            oldValue = propLocalIP;
            propLocalIP = newValue.toString();
        }
        else if("gatewayIP".equalsIgnoreCase(propertyName)) {
        	oldValue = propGatewayIP;
        	propGatewayIP = newValue.toString();
        }
        else if("USB".equalsIgnoreCase(propertyName)) {
        	oldValue = propUSB;
        	if("true".equalsIgnoreCase((String)newValue)) {
        		propUSB = true;
            }
            else if("false".equalsIgnoreCase((String)newValue)) {
            	propUSB = false;
            }
        }
        else if("id1".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID1;
        	propSendID1 = newValue.toString();
        }
        else if("sendType1".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType1;
        	propDataType1 = newValue.toString();
        }
        else if("dataValue1".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue1;
        	propDataValue1 = newValue.toString();
        }
        else if("id2".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID2;
        	propSendID2 = newValue.toString();
        }
        else if("sendType2".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType2;
        	propDataType2 = newValue.toString();
        }
        else if("dataValue2".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue2;
        	propDataValue2 = newValue.toString();
        }
        else if("id3".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID3;
        	propSendID3 = newValue.toString();
        }
        else if("sendType3".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType3;
        	propDataType3 = newValue.toString();
        }
        else if("dataValue3".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue3;
        	propDataValue3 = newValue.toString();
        }
        else if("id4".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID4;
        	propSendID4 = newValue.toString();
        }
        else if("sendType4".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType4;
        	propDataType4 = newValue.toString();
        }
        else if("dataValue4".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue4;
        	propDataValue4 = newValue.toString();
        }
        else if("id5".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID5;
        	propSendID5 = newValue.toString();
        }
        else if("sendType5".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType5;
        	propDataType5 = newValue.toString();
        }
        else if("dataValue5".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue5;
        	propDataValue5 = newValue.toString();
        }
        else if("id6".equalsIgnoreCase(propertyName)) {
        	oldValue = propSendID6;
        	propSendID6 = newValue.toString();
        }
        else if("sendType6".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType6;
        	propDataType6 = newValue.toString();
        }
        else if("dataValue6".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue6;
        	propDataValue6 = newValue.toString();
        }
        else if("IDSlider1".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider1;
        	propIDSlider1 = newValue.toString();
        }
        else if("sendTypeSlider1".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider1;
        	propTypeSlider1 = newValue.toString();
        }
        else if("IDSlider2".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider2;
        	propIDSlider2 = newValue.toString();
        }
        else if("sendTypeSlider2".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider2;
        	propTypeSlider2 = newValue.toString();
        }
        else if("IDSlider3".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider3;
        	propIDSlider3 = newValue.toString();
        }
        else if("sendTypeSlider3".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider3;
        	propTypeSlider3 = newValue.toString();
        }
        else if("IDSlider4".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider4;
        	propIDSlider4 = newValue.toString();
        }
        else if("sendTypeSlider4".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider4;
        	propTypeSlider4 = newValue.toString();
        }
        else if("IDSlider5".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider5;
        	propIDSlider5 = newValue.toString();
        }
        else if("sendTypeSlider5".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider5;
        	propTypeSlider5 = newValue.toString();
        }
        else if("IDSlider6".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDSlider6;
        	propIDSlider6 = newValue.toString();
        }
        else if("sendTypeSlider6".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeSlider6;
        	propTypeSlider6 = newValue.toString();
        }
        else if("IDTrigger1".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger1;
        	propIDTrigger1 = newValue.toString();
        }
        else if("IDTrigger2".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger2;
        	propIDTrigger2 = newValue.toString();
        }
        else if("IDTrigger3".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger3;
        	propIDTrigger3 = newValue.toString();
        }
        else if("IDTrigger4".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger4;
        	propIDTrigger4 = newValue.toString();
        }
        else if("IDTrigger5".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger5;
        	propIDTrigger5 = newValue.toString();
        }
        else if("IDTrigger6".equalsIgnoreCase(propertyName)) {
        	oldValue = propIDTrigger6;
        	propIDTrigger6 = newValue.toString();
        }
        else if("TypeTrigger1".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger1;
        	propTypeTrigger1 = newValue.toString();
        }
        else if("TypeTrigger2".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger2;
        	propTypeTrigger2 = newValue.toString();
        }
        else if("TypeTrigger3".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger3;
        	propTypeTrigger3 = newValue.toString();
        }
        else if("TypeTrigger4".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger4;
        	propTypeTrigger4 = newValue.toString();
        }
        else if("TypeTrigger5".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger5;
        	propTypeTrigger5 = newValue.toString();
        }
        else if("TypeTrigger6".equalsIgnoreCase(propertyName)) {
        	oldValue = propTypeTrigger6;
        	propTypeTrigger6 = newValue.toString();
        }
        
        return oldValue;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipCommand  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String text = ConversionUtils.stringFromBytes(data);
	    	// Logger.getAnonymousLogger().info("KNX received: " + text);
			//TODO: den sendstring noch einbauen
    		if (text.startsWith("@ENOCEAN:")) {  			
				try {
					
					//StringTokenizer st = new StringTokenizer(text.substring(5),"#");
			    	Logger.getAnonymousLogger().info("Tokenizing: " + text.substring(5));
					//sendKNX(st.nextToken(),st.nextToken(),st.nextToken());
				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(e.toString());
				}
    		}
		}
	};
	private final IRuntimeInputPort ipSlider1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in1=ConversionUtils.doubleFromBytes(data);
            sendEnOcean(propIDSlider1,propTypeSlider1,Integer.toString((int)(in1)));
		}
	};
	private final IRuntimeInputPort ipSlider2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in2=ConversionUtils.doubleFromBytes(data);
            sendEnOcean(propIDSlider2,propTypeSlider2,Integer.toString((int)(in2)));
		}
	};
	private final IRuntimeInputPort ipSlider3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in3=ConversionUtils.doubleFromBytes(data);
			sendEnOcean(propIDSlider3,propTypeSlider3,Integer.toString((int)(in3)));
		}
	};
	private final IRuntimeInputPort ipSlider4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in4=ConversionUtils.doubleFromBytes(data);
			sendEnOcean(propIDSlider4,propTypeSlider4,Integer.toString((int)(in4)));
		}
	};
	private final IRuntimeInputPort ipSlider5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in5=ConversionUtils.doubleFromBytes(data);
			sendEnOcean(propIDSlider5,propTypeSlider5,Integer.toString((int)(in5)));
		}
	};
	private final IRuntimeInputPort ipSlider6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in6=ConversionUtils.doubleFromBytes(data);
			sendEnOcean(propIDSlider6,propTypeSlider6,Integer.toString((int)(in6)));
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSend1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID1,propDataType1,propDataValue1);
		}
	};
	final IRuntimeEventListenerPort elpSend2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID2,propDataType2,propDataValue3);
		}
	};
	final IRuntimeEventListenerPort elpSend3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID3,propDataType3,propDataValue3);
		}
	};
	final IRuntimeEventListenerPort elpSend4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID4,propDataType4,propDataValue4);
		}
	};
	final IRuntimeEventListenerPort elpSend5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID5,propDataType5,propDataValue5);
		}
	};
	final IRuntimeEventListenerPort elpSend6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendEnOcean(propSendID6,propDataType6,propDataValue6);
		}
	};

	
	
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  openConnection();
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
    	  closeConnection();
          super.stop();
      }

      /**
       * opens a connection to a EnOcean router via the Priscilla library.
       */
      private void openConnection() {
	   		try {
	   			System.out.println("PORT: " + propGatewayIP);
	   			System.out.println("POPUSB: " + propUSB);
	   			if(propUSB == false)
	   			{
	   				InetSocketAddress adress = new InetSocketAddress(propGatewayIP,5000);
	   				enoceanLink = new EnOceanLinkImpl(adress);
	   			} else {
	   				CIMPortController portController = CIMPortManager.getInstance().getRawConnection(propGatewayIP,57600,true);
	   				if(portController == null) System.out.println("Port controller ist null....");
	   				if(portController.getInputStream() == null) System.out.println("Port controller InputStream ist null....");
	   				if(portController.getOutputStream() == null) System.out.println("Port controller OutputStream ist null....");
	   				enoceanLink = new EnOceanLinkImpl(portController.getInputStream(),portController.getOutputStream());
	   			}
	   			
	   			enoceanLink.addLinkListener(new ILinkListener() {

					@Override
					public void frameReceived(FrameEvent arg0) {
					}

					@Override
					public void frameReceived_Binary(FrameEventBinary arg0) {
						String temp = "";
						
						if(arg0.getDeviceA() == BinaryState.FALSE)
						{
							temp = temp + "false";
						}
						if(arg0.getDeviceA() == BinaryState.TRUE)
						{
							temp = temp + "true";
						}
						
						if(arg0.getDeviceB() == BinaryState.FALSE)
						{
							temp = temp + " false";
						}
						if(arg0.getDeviceB() == BinaryState.TRUE)
						{
							temp = temp + "true";
						}
						
						if(arg0.getDeviceC() == BinaryState.FALSE)
						{
							temp = temp + " false";
						}
						if(arg0.getDeviceC() == BinaryState.TRUE)
						{
							temp = temp + "true";
						}
						
						if(arg0.getDeviceD() == BinaryState.FALSE)
						{
							temp = temp + " false";
						}
						if(arg0.getDeviceD() == BinaryState.TRUE)
						{
							temp = temp + "true";
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "binary".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger2) && "binary".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger3) && "binary".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger4) && "binary".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger5) && "binary".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger6) && "binary".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
					}

					@Override
					public void frameReceived_DayNight(FrameEvent arg0) {
						//not implemented
						
					}

					@Override
					public void frameReceived_Error(FrameEventError arg0) {
						//not implemented
						
					}

					@Override
					public void frameReceived_Fan(FrameEventFan arg0) {
						String temp = "";
						
						switch(arg0.getFanState())
						{
						case OFF:
							temp = "OFF";
							break;
						case AUTO:
							temp = "AUTO";
							break;
						case SPEED0:
							temp = "0";
							break;
						case SPEED1:
							temp = "1";
							break;
						case SPEED2:
							temp = "2";
							break;
						case SPEED3:
							temp = "3";
							break;
						case SPEED4:
							temp = "4";
							break;
						case SPEED5:
							temp = "5";
							break;
						default:
							break;
						}
						
						switch(arg0.getFanType())
						{
						case TYPE3:
							temp = temp + "/3";
							break;
						case TYPE5:
							temp = temp + "/5";
							break;
						default:
							break;
						}
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "fan".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger2) && "fan".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger3) && "fan".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger4) && "fan".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger5) && "fan".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger6) && "fan".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
					}

					@Override
					public void frameReceived_Gas(FrameEvent arg0) {
						//not implemented
						
					}

					@Override
					public void frameReceived_GatewayID(
							FrameEventGetGatewayID arg0) {
						//not necessary...
						
					}

					@Override
					public void frameReceived_Humidity(FrameEventHumidity arg0) {
						String temp = String.valueOf(arg0.getHumidityValue());
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "humidity".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger2) && "humidity".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger3) && "humidity".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger4) && "humidity".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger5) && "humidity".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger6) && "humidity".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
					}

					@Override
					public void frameReceived_Illumination(
							FrameEventIllumination arg0) {
						String temp = String.valueOf(arg0.getIlluminationValue());
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "illumination".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger2) && "illumination".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger3) && "illumination".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger4) && "illumination".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger5) && "illumination".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger6) && "illumination".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
					}

					@Override
					public void frameReceived_LearnTelegram(
							FrameEventLearnTelegram arg0) {
						//not necessary...
						
					}

					@Override
					public void frameReceived_RainSensor(FrameEvent arg0) {
						//not implemented
						
					}

					@Override
					public void frameReceived_SetPoint(FrameEventSetPoint arg0) {
						String temp = String.valueOf(arg0.getSetPointValue());
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "setpoint".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger2) && "setpoint".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger3) && "setpoint".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger4) && "setpoint".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger5) && "setpoint".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger6) && "setpoint".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
						
					}

					@Override
					public void frameReceived_SupplyVoltage(
							FrameEventSupplyVoltage arg0) {
						//not implemented
						
					}

					@Override
					public void frameReceived_Temperature(
							FrameEventTemperature arg0) {
						String temp = String.valueOf(arg0.getTemperatureValue());
						
						if(arg0.getDeviceID().equals(propIDTrigger1) && "temperature".equalsIgnoreCase(propTypeTrigger1))
						{
							opValue1.sendData(temp.getBytes());
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger2) && "temperature".equalsIgnoreCase(propTypeTrigger2))
						{
							opValue2.sendData(temp.getBytes());
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger3) && "temperature".equalsIgnoreCase(propTypeTrigger3))
						{
							opValue3.sendData(temp.getBytes());
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger4) && "temperature".equalsIgnoreCase(propTypeTrigger4))
						{
							opValue4.sendData(temp.getBytes());
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger5) && "temperature".equalsIgnoreCase(propTypeTrigger5))
						{
							opValue5.sendData(temp.getBytes());
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(arg0.getDeviceID().equals(propIDTrigger6) && "temperature".equalsIgnoreCase(propTypeTrigger6))
						{
							opValue6.sendData(temp.getBytes());
							runtimeEventTriggererPort6.raiseEvent();
						}
					}

					@Override
					public void frameReceived_Time(FrameEvent arg0) {
						//not implemented
					}

					@Override
					public void frameReceived_WeatherStation(FrameEvent arg0) {
						//not implemented
					}

					@Override
					public void frameReceived_WindowHandle(
							FrameEventWindowHandle arg0) {
						// TODO to be done...
						
					}
		
					}

				);

	   		} catch (Exception e) {
	   			//netLinkIp = null;
	   			System.out.println(e.getMessage());
	   		}
       }
      
      /**
       * closes a connection to an EnOcean gateway
       */
       private void closeConnection () {
       }
       
       /**
        * sends a id/type/value command  to the connected EnOcean gateway 
        */
       private void sendEnOcean(String id, String type, String value) {
    	   /*System.out.println(id);
    	   System.out.println(type);
    	   System.out.println(value);*/
    	   ErrorCodeSend returnval = null;
    	   if(enoceanLink != null)
    	   {
    		   switch(type)
    		   {
    		   		case "binary":
    		   			if(value.equalsIgnoreCase("true")) {
    		   				returnval = enoceanLink.sendEvent(EventSend.BINARY_EVENT, Integer.parseInt(id), BinaryState.TRUE , BinaryState.UNKNOWN,  BinaryState.UNKNOWN,  BinaryState.UNKNOWN);
    		   			} else {
    		   				returnval = enoceanLink.sendEvent(EventSend.BINARY_EVENT, Integer.parseInt(id), BinaryState.FALSE , BinaryState.UNKNOWN,  BinaryState.UNKNOWN,  BinaryState.UNKNOWN);
    		   			}
    		   			break;
    		   		case "temperature":
    		   			returnval = enoceanLink.sendEvent(EventSend.TEMPERATURE_EVENT, Integer.parseInt(id), Float.parseFloat(value));
    		   			break;
    		   		case "illumination":
    		   			returnval = enoceanLink.sendEvent(EventSend.ILLUMINATION_EVENT, Integer.parseInt(id), Float.parseFloat(value));
    		   			break;
    		   		case "fan":
    		   			//TODO
    		   			break;
    		   		case "humidity":
    		   			returnval = enoceanLink.sendEvent(EventSend.HUMIDITY_EVENT, Integer.parseInt(id), Float.parseFloat(value));
    		   			break;
    		   		case "setpoint":
    		   			returnval = enoceanLink.sendEvent(EventSend.SETPOINT_EVENT, Integer.parseInt(id), Float.parseFloat(value));
    		   			break;
    		   		default:
    		   			break;
    		   }
    		   //System.out.println("Return value send: " + returnval);
    	   }
       }
      
      
}