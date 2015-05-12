

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

package eu.asterics.component.sensor.lipmouseir;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import eu.asterics.mw.cimcommunication.*;
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
/**
 * 
 * Interface for the Lipmouse including the infrared communication port
 * 
 * 
 *  
 * @author Alberto Ibanez [alberto_21_9@hotmail.com], extended by Christoph Ulbinger [christoph.ulbinger@technikum-wien.at]
 *         Date: 08.04.2015
 *         Time: 09:14
 */
public class LipmouseIRInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private final short LIPMOUSE_CIM_ID  			    	= (short) 0xa402;
	private static final short LIPMOUSE_CIM_FEATURE_UNIQUENUMBER    = 0x0000;
	private static final short LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD   = 0x0001;
	private static final short LIPMOUSE_CIM_FEATURE_ADCREPORT 	  	= 0x0002;
	private static final short LIPMOUSE_CIM_FEATURE_BUTTONREPORT 	= 0x0003;
	private static final short LIPMOUSE_CIM_FEATURE_SETLEDS 		= 0x0004;
	
	private static final short LIPMOUSE_CIM_FEATURE_SET_IR_MODE  = 0x0008;		// Set Lipmouse into sending/receiving mode
	private static final short LIPMOUSE_CIM_FEATURE_GET_IR_STATE = 0x0009;		// Get state of the Lipmouse
	private static final short LIPMOUSE_CIM_FEATURE_SET_IR_CODE  = 0x000A;		// Send IR code to Lipmouse
	private static final short LIPMOUSE_CIM_FEATURE_GET_IR_CODE	 = 0x000B;		// Receive IR code from Lipmouse
	
	final IRuntimeOutputPort opX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPressure = new DefaultRuntimeOutputPort();

	final IRuntimeEventTriggererPort etpSip = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpLongSip = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPuff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpLongPuff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpSipStart = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpSipEnd = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPuffStart = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPuffEnd = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton1Pressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton1Released = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton2Pressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton2Released = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton3Pressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpButton3Released = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStartRecord = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStopRecord = new DefaultRuntimeEventTriggererPort();
	
	int DATABUF_SIZE = 512;			// 512 byte size for IR code
	
	
	public int propPeriodicADCUpdate = 50;
	String propIRCodeFilePath = "IRCodes.csv";
	private String propUniqueID = "not used"; 
	public int propSipThreshold = 505;
	public int propSipTime = 700;
	public int propPuffThreshold = 520;
	public int propPuffTime = 700;
	
	String DeviceType = "";
	String DeviceName = "";
	String DeviceFunction = "";
	
	private int calibX = 0;
	private int calibY = 0;
	private int ledState = 1;
	private boolean calibNow = false;

	// declare member variables here

	private CIMPortController port = null; 
   /**
    * The class constructor.
    */
    public LipmouseIRInstance()
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
		if ("DeviceType".equalsIgnoreCase(portID))
		{
			return ipDeviceType;
		}
		if ("DeviceName".equalsIgnoreCase(portID))
		{
			return ipDeviceName;
		}
		if ("DeviceFunction".equalsIgnoreCase(portID))
		{
			return ipDeviceFunction;
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
		if ("sendIRCode".equalsIgnoreCase(eventPortID))
		{
			return elpSendIRCode;
		}
		if ("recordIRCode".equalsIgnoreCase(eventPortID))
		{
			return elpRecordIRCode;
		}
		if ("setLed1".equalsIgnoreCase(eventPortID))
		{
			return elpSetLed1;
		}
		if ("clearLed1".equalsIgnoreCase(eventPortID))
		{
			return elpClearLed1;
		}
		if ("setLed2".equalsIgnoreCase(eventPortID))
		{
			return elpSetLed2;
		}
		if ("clearLed2".equalsIgnoreCase(eventPortID))
		{
			return elpClearLed2;
		}
		if ("setLed3".equalsIgnoreCase(eventPortID))
		{
			return elpSetLed3;
		}
		if ("clearLed3".equalsIgnoreCase(eventPortID))
		{
			return elpClearLed3;
		}
		
        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("startRecord".equalsIgnoreCase(eventPortID))
		{
			return etpStartRecord;
		}
		if ("stopRecord".equalsIgnoreCase(eventPortID))
		{
			return etpStopRecord;
		}
		if ("sip".equalsIgnoreCase(eventPortID))
		{
			return etpSip;
		}
		if ("longSip".equalsIgnoreCase(eventPortID))
		{
			return etpLongSip;
		}
		if ("puff".equalsIgnoreCase(eventPortID))
		{
			return etpPuff;
		}
		if ("longPuff".equalsIgnoreCase(eventPortID))
		{
			return etpLongPuff;
		}
		if ("sipStart".equalsIgnoreCase(eventPortID))
		{
			return etpSipStart;
		}
		if ("sipEnd".equalsIgnoreCase(eventPortID))
		{
			return etpSipEnd;
		}
		if ("puffStart".equalsIgnoreCase(eventPortID))
		{
			return etpPuffStart;
		}
		if ("puffEnd".equalsIgnoreCase(eventPortID))
		{
			return etpPuffEnd;
		}
		if ("button1Pressed".equalsIgnoreCase(eventPortID))
		{
			return etpButton1Pressed;
		}
		if ("button1Released".equalsIgnoreCase(eventPortID))
		{
			return etpButton1Released;
		}
		if ("button2Pressed".equalsIgnoreCase(eventPortID))
		{
			return etpButton2Pressed;
		}
		if ("button2Released".equalsIgnoreCase(eventPortID))
		{
			return etpButton2Released;
		}
		if ("button3Pressed".equalsIgnoreCase(eventPortID))
		{
			return etpButton3Pressed;
		}
		if ("button3Released".equalsIgnoreCase(eventPortID))
		{
			return etpButton3Released;
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
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			return propPeriodicADCUpdate;
		}
		if ("iRCodeFilePath".equalsIgnoreCase(propertyName))
		{
			return propIRCodeFilePath;
		}
        if("uniqueID".equalsIgnoreCase(propertyName))
        {
            return propUniqueID;
        }
        if("sipThreshold".equalsIgnoreCase(propertyName))
        {
            return propSipThreshold;
        }
        if("sipTime".equalsIgnoreCase(propertyName))
        {
            return propSipTime;
        }
        if("puffThreshold".equalsIgnoreCase(propertyName))
        {
            return propPuffThreshold;
        }
        if("puffTime".equalsIgnoreCase(propertyName))
        {
            return propPuffTime;
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
		if ("iRCodeFilePath".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIRCodeFilePath;
			propIRCodeFilePath = (String)newValue;
			return oldValue;
		}
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPeriodicADCUpdate;
			propPeriodicADCUpdate = Integer.parseInt(newValue.toString());
			sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
			return oldValue;
		}
		if ("sipThreshold".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSipThreshold;
			propSipThreshold = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("sipTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSipTime;
			propSipTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("puffThreshold".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPuffThreshold;
			propPuffThreshold = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("puffTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPuffTime;
			propPuffTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if("uniqueID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUniqueID;
			propUniqueID = (String)newValue;
			CIMPortController tempPort = openCIM (LIPMOUSE_CIM_ID, propUniqueID);
			if (tempPort != null)
			{
				port=tempPort;
				if ((!propUniqueID.equals("")) && (!propUniqueID.equals("not used")))
				{
					for (int i=0;i<4;i++)
					{
					  CIMPortManager.getInstance().sendPacket  (port, null, 
							  CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER, 
							  CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
					  try { Thread.sleep (100); }  catch (InterruptedException e) {}
					}
				}
			} 
			return oldValue;
		}     

        return null;
    }
    
    public CIMPortController openCIM(short CIMID, String uniqueID)
	{
	   if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID==""))
	   {
		    return (CIMPortManager.getInstance().getConnection(LIPMOUSE_CIM_ID));
	   }
	   else
	   {
			Long id;
			try {
				id=Long.parseLong(propUniqueID);
				return (CIMPortManager.getInstance().getConnection(LIPMOUSE_CIM_ID, id));
			} catch (Exception e) {
				return(null);
			}	   
	   }
	}

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipDeviceType  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			DeviceType = ConversionUtils.stringFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipDeviceName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			DeviceName = ConversionUtils.stringFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipDeviceFunction  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			DeviceFunction = ConversionUtils.stringFromBytes(data);
		}
	};
	

     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSendIRCode = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			HideRecordMode();
			
			if((DeviceType != "") && (DeviceName != "") && (DeviceFunction != ""))
	  	  	{
				// open and read CSV file
				String IR_Code_Line = ReadFromCSV();
				
				if(IR_Code_Line == null)
				{
					System.out.println("#Error: no IR Code found!");
					return;
				}					
				
				// parsing IR code received from database
				byte IRCode[] = ParseIRCode(IR_Code_Line);
				
				if(IRCode == null)
				{
					System.out.println("#Error: no IR Code found or error at parsing IR Code!");
					return;
				}		
				
				// Send Lipmouse the IR code
				if (port != null)
				{
					CIMPortManager.getInstance().sendPacket(port,IRCode, LIPMOUSE_CIM_FEATURE_SET_IR_CODE, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		  		}
				
				System.out.println("Set Lipmouse into sending mode");				
	  	  	}
	  	  	else
	  	  	{
	  	  		System.out.println("#Error: Either 'DeviceType', 'DeviceName' or 'DeviceFunction' is not specified!");
	  	  	}							
		}
	};
	final IRuntimeEventListenerPort elpRecordIRCode = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if((DeviceType != "") && (DeviceName != "") && (DeviceFunction != ""))
	  	  	{
				// Set Lipmouse into recording mode			
				sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_IR_MODE, 2);  // 0 : idle
																	            // 1 : Sending
																				// 2 : Recording
				System.out.println("Set Lipmouse into recording mode");	
				
				ShowRecordMode();
	  	  	}
	  	  	else
	  	  	{
	  	  		System.out.println("#Error: Either 'DeviceType', 'DeviceName' or 'DeviceFunction' is not specified!");
	  	  	}
			
			
									
		}
	};
	
	final IRuntimeEventListenerPort elpCalibration = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{		    
			//sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_CALIBRATION,0);   // calib in firmware 
			calibNow=true;
			
		}
	};

	final IRuntimeEventListenerPort elpSetLed1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState|=1;
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};
	final IRuntimeEventListenerPort elpClearLed1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState&=(~1);
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};

	final IRuntimeEventListenerPort elpSetLed2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState|=2;
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};
	final IRuntimeEventListenerPort elpClearLed2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState&=(~2);
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};
	final IRuntimeEventListenerPort elpSetLed3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState|=4;
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};
	final IRuntimeEventListenerPort elpClearLed3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    ledState&=(~4);
			sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS,ledState);  
		}
	};
	
	
	private void ShowRecordMode()
	{
		etpStartRecord.raiseEvent();		// raise event to show that Lipmouse is recording
	}
	
	private void HideRecordMode()
	{
		etpStopRecord.raiseEvent();		// raise event to show that Lipmouse has finished recording
	}

	/**
	 * Handles an input packet from Lipmouse CIM. Reads the values 
	 * of all ADC channels and sends the data to the corresponding output ports 
	 * @param packet the incoming packet
	 */

	static int pressure=-1;
	static int oldPressure=-1;
	static long sipStartTime=0;
	static long sipTime=0;
	static long puffStartTime=0;
	static long puffTime=0;
	
	private void handleLipmouseAdcReport(CIMProtocolPacket packet)
	{
		// System.out.println("handleLipmouseAdcPacket");
		byte [] b = packet.getData();
		int x,y;

		x= ConversionUtils.intFromBytes(ADCDataToBytes(b[0],b[1]));
		y= ConversionUtils.intFromBytes(ADCDataToBytes(b[2],b[3]));

		if (calibNow==true)  {
			calibX=x;
			calibY=y;
			calibNow=false;
		}
		
		opX.sendData(ConversionUtils.intToBytes(x-calibX));
		opY.sendData(ConversionUtils.intToBytes(y-calibY));
				
		opPressure.sendData(ADCDataToBytes(b[4],b[5]));

		pressure= ConversionUtils.intFromBytes(ADCDataToBytes(b[4],b[5]));
		// System.out.println("pressure="+pressure);

		if (oldPressure!= -1)
		{
			if ((oldPressure > propSipThreshold) && (pressure <= propSipThreshold))
			{
				sipStartTime=System.currentTimeMillis();
				etpSipStart.raiseEvent();
			}
			else if ((oldPressure <= propSipThreshold) && (pressure > propSipThreshold))
			{
				sipTime=System.currentTimeMillis()-sipStartTime;
				etpSipEnd.raiseEvent();
				if (sipTime >= propSipTime)
					etpLongSip.raiseEvent();
				else
					etpSip.raiseEvent();
			}

			if ((oldPressure < propPuffThreshold) && (pressure >= propPuffThreshold))
			{
				puffStartTime=System.currentTimeMillis();
				etpPuffStart.raiseEvent();
			}
			else if ((oldPressure > propPuffThreshold) && (pressure <= propPuffThreshold))
			{
				puffTime=System.currentTimeMillis()-puffStartTime;
				etpPuffEnd.raiseEvent();
				if (puffTime >= propPuffTime)
					etpLongPuff.raiseEvent();
				else
					etpPuff.raiseEvent();
			}
		}
		oldPressure=pressure;
	}
	static int button1State = 0;
	static int button2State = 0;
	static int button3State = 0;
	
	private void handleLipmouseButtonReport(CIMProtocolPacket packet)
	{
		System.out.println("handleLipmouseButtonPacket");
		byte [] b = packet.getData();

		if ((button1State==0) && ((b[0]&1)!=0))
		{
			etpButton1Pressed.raiseEvent();
			button1State=1;
		}
		else if ((button1State==1) && ((b[0]&1)==0))
		{
			etpButton1Released.raiseEvent();
			button1State=0;
		}

		
		if ((button2State==0) && ((b[0]&2)!=0))
		{
			etpButton2Pressed.raiseEvent();
			button2State=1;
		}
		else if ((button2State==1) && ((b[0]&2)==0))
		{
			etpButton2Released.raiseEvent();
			button2State=0;
		}

		if ((button3State==0) && ((b[0]&4)!=0))
		{
			etpButton3Pressed.raiseEvent();
			button3State=1;
		}
		else if ((button3State==1) && ((b[0]&4)==0))
		{
			etpButton3Released.raiseEvent();
			button3State=0;
		}

	}

	//This is a function to convert the data sent by the microcontroller to the format that the 
	//output port send it through the channel
    private byte [] ADCDataToBytes(byte first,byte second)
    {
    	if ( (second & 80) == 0) //If the number is positive, i.e, if 8th bit is 0
    	{
    		return new byte [] {(byte)(0x00),(byte)(0x00),second,first};
    	}else{ //If the number is negative, i.e, if 8th bit is 1
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
				else if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT)
				{
					//System.out.println ("ADCReport.");
				 	handleLipmouseAdcReport(packet);
				}
				else if (featureAddress == LIPMOUSE_CIM_FEATURE_BUTTONREPORT)
				{
					 // System.out.println ("Incoming Event: ADCReport "+(128+(int)packet.getSerialNumber()));
					 handleLipmouseButtonReport(packet);
				}
				else if (featureAddress == LIPMOUSE_CIM_FEATURE_GET_IR_CODE)
				{
					HideRecordMode();
					handleReceivedIRCode(packet.getData());
				}
				else if (featureAddress == LIPMOUSE_CIM_FEATURE_GET_IR_STATE)
				{
					handleIRState(packet.getData());
				}
				break;

			case CIMProtocolPacket.COMMAND_EVENT_REPLY:
				if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT)
				{
					 // System.out.println ("Incoming Event: ADCReport "+(128+(int)packet.getSerialNumber()));
					 handleLipmouseAdcReport(packet);
				}
				else if (featureAddress == LIPMOUSE_CIM_FEATURE_BUTTONREPORT)
				{
					 // System.out.println ("Incoming Event: ADCReport "+(128+(int)packet.getSerialNumber()));
					 handleLipmouseButtonReport(packet);
				}
				break;
			case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
				if (featureAddress == LIPMOUSE_CIM_FEATURE_SET_IR_MODE)
				{
					handleIRState(packet.getData());
				}
				if (featureAddress == LIPMOUSE_CIM_FEATURE_GET_IR_STATE)
				{
					handleIRState(packet.getData());
				}
				break;
		}
	}
	
	
	private void handleIRState(byte[] packet)
	{
		byte[] IR_Data = packet;
		
		if(IR_Data == null)
		{
			System.out.println("Received null");
			return;
		}
		
		switch(IR_Data[0])
		{
			case 0:					// Idle
				HideRecordMode();
				System.out.println("Lipmouse ready");
				break;
			case 1:					// Sending
				System.out.println("Sending IR Code");	
				break;
			case 2:					// Recording
				System.out.println("Lipmouse is recording...");
				break;
			case 3:					// Record available
				System.out.println("Read IR Code");	
				sendLipmouseReadFeature(LIPMOUSE_CIM_FEATURE_GET_IR_CODE, 0);						
				break;
			case 4:					// Lipmouse received IR Code from ARE
				System.out.println("Lipmouse received IR Code from ARE");	
				sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_IR_MODE, 1);  // 0 : idle
	            																// 1 : Sending
																				// 2 : Recording
																				// 4 : Lipmouse Received IR Code from ARE	
				break;
			default:		
				System.out.println("Lipmouse sent unknown content!");
				break;
		}
	}
	
	/**
	 * Returns the unique ID
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("uniqueID")==0)
		{
			res.add("not used");
			Vector<Long> ids;
			ids=CIMPortManager.getInstance().getUniqueIdentifiersofCIMs(LIPMOUSE_CIM_ID);
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
	
	private void handleReceivedIRCode(byte[] packet)
	{
		byte[] IR_Data = packet;
  	  	
		if(IR_Data==null)
  	  		return;
  	  	
  	  	String IR_Code = "";
  	  	for (int i = 0; (i < IR_Data.length)&&(i<DATABUF_SIZE); i++) 	// create a CSV format
  	  	{
  	  		IR_Code += (short)(IR_Data[i] & 0xFF);
  	  		IR_Code += ",";
  		}
  	  	WriteToCSV(IR_Code);			// write IR code to database
  	   
	}

	/**
	 * Called upon faulty packet reception
	 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
	}
	
	private byte[] ParseIRCode(String IR_Code_Line)				// Parse IR code reveived from database
	{
		String tmpCode[] = IR_Code_Line.split(",");
		byte IRCode[] = new byte[DATABUF_SIZE];
		
		for(int counter = 0; (counter<(tmpCode.length-3))&&(counter < DATABUF_SIZE); counter++)			// -3 due to DeviceType, Devicename, DeviceFunction
		{
			IRCode[counter] = (byte)(Integer.parseInt(tmpCode[counter+3], 10) & 0xFF);
		}
		
		return IRCode;
	}
	
	
	 public String ReadFromCSV()		// Read IR code from database
     {
		checkPath();
		File csvfile = new File (propIRCodeFilePath);
		BufferedReader in;
		String currentLine = "";
		
   		try {
   			if(!csvfile.exists())
   			{
   				System.out.println("#Error: Could not find " + propIRCodeFilePath);
   				return null;   	
   			}

   			in  = new BufferedReader(new FileReader(csvfile));
   			
   			while((currentLine = in.readLine()) != null)
   			{
   				if(currentLine.contains(DeviceType + "," + DeviceName + "," + DeviceFunction))
   				{
   					System.out.println("IR Code found in " + propIRCodeFilePath);
   					break; 
   				}
   			}
   			if(currentLine == "")
   			{
   				System.out.println("#Error: Could not find IR Code for " + DeviceType + " " + DeviceName + " " + DeviceFunction);
   				return null;
   			}
	        in.close();	        
	     } catch (IOException e) {
   			AstericsErrorHandling.instance.reportInfo(this, "#Error: Cannot read from file!");
   			return null;
	     }
   		return currentLine;
     }
	
	
	 public void WriteToCSV(String IRCode)		// Write IR code to database
     {
		checkPath();
		File csvfile = new File (propIRCodeFilePath);
		File tmp_csvfile = new File ("tmp_IRCodes.csv");		// create a temporary file to reduce RAM usage 
		BufferedReader in;
		BufferedWriter out;
		
   		try {
   			if(!OpenFile(csvfile))
   				return;   			
   			out = new BufferedWriter(new FileWriter(tmp_csvfile, true));
   			in  = new BufferedReader(new FileReader(csvfile));
   			
   			String currentLine = "";
   			
   			while((currentLine = in.readLine()) != null)
   			{
   				if(currentLine.contains(DeviceType + "," + DeviceName + "," + DeviceFunction))
   				{
   					System.out.println("IR Code already exists and will be replaced");
   					continue; 
   				}
   				out.write(currentLine + System.getProperty("line.separator"));
   			}
   			out.write(DeviceType + "," + DeviceName + "," + DeviceFunction + "," + IRCode + System.getProperty("line.separator"));
	        out.close();
	        in.close();
	        
	        csvfile.delete();
	        if(tmp_csvfile.renameTo(csvfile))
	        {
	        	System.out.println("Write " + DeviceType + " " + DeviceName + " " + DeviceFunction + " to file " + propIRCodeFilePath);
	        }
	        else
	        {
	        	System.out.println("#Error: Could not rename temporary file tmp_IRCodes.csv to " + propIRCodeFilePath);
	        	System.out.println("#Error: You may need admin rights for this purpose.");
	        }
	        
	     } catch (IOException e) {
   			AstericsErrorHandling.instance.reportInfo(this, "#Error: Cannot write to file!");
	     }
     }
	 
	 private boolean OpenFile(File file)		// open IR code database
	 {
		
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} 
		return true;
	 }
	 
		
	 private void checkPath()					// maintain path for right access
     {
   	  if(propIRCodeFilePath.contains("/"))
   		  return;
   	  
   	  if(propIRCodeFilePath.contains("\\\\"))
   		  return;
   	  
   	  	propIRCodeFilePath.replace("\\", "\\\\");
     }

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  oldPressure=-1;
    	  if (port==null)
    	  {
  		     port = openCIM (LIPMOUSE_CIM_ID,propUniqueID);
    	  }
		  if (port != null )
		  {
			port.addEventListener(this);
 			sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
			CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
		  }
		  else
		  {
	       		AstericsErrorHandling.instance.reportError(this, "Could not find LipMouse Module with IR Module (ID "+propUniqueID+"). Please verify that the Module is connected to an USB Port and that the driver is installed.");
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
    	synchronized private final void sendLipmouseWriteFeatureByte (short feature, int value)
    	{
  		// send packet
  		byte [] b = new byte[1];
  		b[0] = (byte) (value & 0xff);
  		
  		if (port != null)
  		{
  			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
  		}
       }
      
      synchronized private final void sendLipmouseReadFeature (short feature, int value)
  	  {
		// send packet
		byte [] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) ((value >> 8) & 0xff);
		
		if (port != null)
		{
              // System.out.println("sending lipmouse-packet !");
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE,false);
		}
     }
}