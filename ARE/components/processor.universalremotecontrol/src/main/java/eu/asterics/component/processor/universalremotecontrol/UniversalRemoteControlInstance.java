

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

package eu.asterics.component.processor.universalremotecontrol;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Interface for the Universal Remote Control
 * 
 * 
 *  
 * @author Christoph Ulbinger [christoph.ulbinger@technikum-wien.at]
 *         Date: 08.04.2015
 *         Time: 09:41
 */
public class UniversalRemoteControlInstance extends AbstractRuntimeComponentInstance implements
CIMEventHandler 
{
	private final short UIRC_CIM_ID  = (short) 0xa031;
	private static final short UIRC_CIM_FEATURE_UNIQUENUMBER = 0x0000;
	
	private static final short UIRC_CIM_FEATURE_SET_IR_MODE  = 0x0008;
	private static final short UIRC_CIM_FEATURE_GET_IR_STATE = 0x0009;
	private static final short UIRC_CIM_FEATURE_SET_IR_CODE  = 0x000A;
	private static final short UIRC_CIM_FEATURE_GET_IR_CODE	 = 0x000B;
	
	final IRuntimeEventTriggererPort etpStartRecord = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStopRecord = new DefaultRuntimeEventTriggererPort();
	
	String propIRCodeFilePath = "./data/processor.UniversalRemoteControl/IRCodes.csv";
	
	String DeviceType = "";
	String DeviceName = "";
	String DeviceFunction = "";
	
	int DATABUF_SIZE = 512;						// Buffer size for IR code
	
	byte[] buffer = new byte[DATABUF_SIZE];		// Buffer for IR code
	int bufferIndex = 0;
	
	private CIMPortController port = null; 
	
   /**
    * The class constructor.
    */
    public UniversalRemoteControlInstance()
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
		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
    	if ("sendIRCode".equalsIgnoreCase(eventPortID))
		{
			return elpSendIRCode;
		}
		if ("recordIRCode".equalsIgnoreCase(eventPortID))
		{
			return elpRecordIRCode;
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
        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
    	if ("iRCodeFilePath".equalsIgnoreCase(propertyName))
		{
			return propIRCodeFilePath;
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
        return null;
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
	
	
	final IRuntimeEventListenerPort elpSendIRCode = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			HideRecordMode();
			
			if((DeviceType != "") && (DeviceName != "") && (DeviceFunction != ""))
	  	  	{
				//Open CSV file
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
				
				// Send universal RC the IR code
				if (port != null)
				{
					CIMPortManager.getInstance().sendPacket(port,IRCode, UIRC_CIM_FEATURE_SET_IR_CODE, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		  		}
				
				System.out.println("Set Sending Mode");				
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
				// Set universal RC into recording mode					
				sendUIRCWriteFeature(UIRC_CIM_FEATURE_SET_IR_MODE, 2);  
				// 0 : idle
				// 1 : Sending
				// 2 : Recording
				System.out.println("Set Record Mode");	
				
				ShowRecordMode();
	  	  	}
	  	  	else
	  	  	{
	  	  		System.out.println("#Error: Either 'DeviceType', 'DeviceName' or 'DeviceFunction' is not specified!");
	  	  	}
			
			
									
		}
	};
	
	// raise event to show that universal RC is recording
	private void ShowRecordMode()
	{
		etpStartRecord.raiseEvent();
	}
	// raise event to show that universal RC has finished recording
	private void HideRecordMode()
	{
		etpStopRecord.raiseEvent();
	}
	
	/**
	 * Called by port controller if new packet has been received
	 */
	public void handlePacketReceived(CIMEvent e)
	{
		
		short featureAddress=0;
		CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
		CIMProtocolPacket packet = ev.packet;
		featureAddress=packet.getFeatureAddress();
		
		switch (packet.getRequestReplyCode())
		{
			case CIMProtocolPacket.COMMAND_REPLY_START_CIM:
				break;
			case CIMProtocolPacket.COMMAND_REPLY_STOP_CIM:
				break;
			case CIMProtocolPacket.COMMAND_REPLY_RESET_CIM:
				break;
			case CIMProtocolPacket.COMMAND_REPLY_READ_FEATURE:
				if (featureAddress == UIRC_CIM_FEATURE_UNIQUENUMBER)
				{
					handleUIRCUniqueNumber(packet);
				}				
				if (featureAddress == UIRC_CIM_FEATURE_GET_IR_CODE)
				{
					HideRecordMode();
					handleReceivedIRCode(packet.getData());
				}
				if (featureAddress == UIRC_CIM_FEATURE_GET_IR_STATE)
				{
					handleIRState(packet.getData());
				}
				break;

			case CIMProtocolPacket.COMMAND_EVENT_REPLY:
				break;
			case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
				if (featureAddress == UIRC_CIM_FEATURE_SET_IR_MODE)
				{
					handleIRState(packet.getData());
				}
				if (featureAddress == UIRC_CIM_FEATURE_GET_IR_STATE)
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
				System.out.println("Universal Remote Control ready");
				break;
			case 1:					// Sending
				System.out.println("Sending IR Code");	
				break;
			case 2:					// Recording
				System.out.println("Universal Remote Control is recording...");
				break;
			case 3:					// Record available
				System.out.println("Read IR Code");	
				sendUIRCReadFeature(UIRC_CIM_FEATURE_GET_IR_CODE, 0);						
				break;
			case 4:					// UIRC received IR Code from ARE
				System.out.println("Universal Remote Control received IR Code from ARE");	
				sendUIRCWriteFeature(UIRC_CIM_FEATURE_SET_IR_MODE, 1);  
				// 0 : idle
	            // 1 : Sending
				// 2 : Recording
				// 4 : UIRC Received IR Code from ARE	
				break;
			default:		
				System.out.println("Universal Remote Control sent unknown content!");
				break;
		}
	}
	
	private void handleReceivedIRCode(byte[] packet)
	{
		byte[] IR_Data = packet;
  	  	
		if(IR_Data==null)
  	  		return;
  	  	
  	  	String IR_Code = "";
  	  	for (int i = 0; (i < IR_Data.length)&&(i<DATABUF_SIZE); i++)  	// create a CSV format
  	  	{
  	  		IR_Code += (short)(IR_Data[i] & 0xFF);
  	  		IR_Code += ",";
  		}
  	  	WriteToCSV(IR_Code);			// write IR code to database
  	   
	}
	
	private void handleUIRCUniqueNumber(CIMProtocolPacket packet)
	{
		byte []b=packet.getData();
	}

	/**
	 * Called upon faulty packet reception
	 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
	}
	
	private byte[] ParseIRCode(String IR_Code_Line)			// Parse IR code reveived from database
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
		// create a temporary file to reduce RAM usage 
		File tmp_csvfile = new File ("tmp_IRCodes.csv");
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
    	  if (port==null)
    	  {
  		     port = CIMPortManager.getInstance().getConnection(UIRC_CIM_ID );
    	  }
		  if (port != null )
		  {
			port.addEventListener(this);
			CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
		  }
		  else
		  {
	       		AstericsErrorHandling.instance.reportError(this, "Could not find UIRC Module. Please verify that the Module is connected to an USB Port and that the driver is installed.");
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
      
      synchronized private final void sendUIRCWriteFeature (short feature, int value)
  	{
		// send packet
		byte [] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) ((value >> 8) & 0xff);
		
		if (port != null)
		{
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		}
     }
    
    synchronized private final void sendUIRCReadFeature (short feature, int value)
	  {
		// send packet
		byte [] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) ((value >> 8) & 0xff);
		
		if (port != null)
		{
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE,false);
		}
   }
}