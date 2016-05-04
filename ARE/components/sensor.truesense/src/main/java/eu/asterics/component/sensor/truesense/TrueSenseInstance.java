

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

package eu.asterics.component.sensor.truesense;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;

import java.util.logging.Logger;

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







import java.util.*;
import java.util.logging.*;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class TrueSenseInstance extends AbstractRuntimeComponentInstance
{
	 final IRuntimeOutputPort opSignal = new DefaultRuntimeOutputPort();
	 final IRuntimeOutputPort opTemp = new DefaultRuntimeOutputPort();
	 final IRuntimeOutputPort opAccX = new DefaultRuntimeOutputPort();
	 final IRuntimeOutputPort opAccY = new DefaultRuntimeOutputPort();
	 final IRuntimeOutputPort opAccZ = new DefaultRuntimeOutputPort();
	
	

	
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	private Thread readerThread = null;
	private InputStream in = null;
	private boolean running = false;
	String propComPort = "COM5";
	int propBaudRate = 57600;
	
//For Parsing Beginn:
	int state=0;
	byte [] data_buff = new byte[256];
	byte [] turn_on = {0x33,0x33,0x00,0x02,0x20,0x23,0x00,0x43};/* Sync 2Byte; PayloadLength 2Byte; Payload 2Byte; CheckSum 2Byte;*/ // alt;{0x33,0x33,0x03,0x00,0x10,0x00,0x01,0x00,0x11 }
	byte [] request_data = {0x33,0x33,0x00,0x02,0x10,0x00,0x00,0x10};/* Sync 2Byte; PayloadLength 2Byte; Payload 2Byte; CheckSum 2Byte;*/ // alt;{0x33,0x33,0x03,0x00,0x10,0x00,0x01,0x00,0x11 }
	//|Sync 2Byte|PL_Length 2byte||Datacode 2Bytee|SubDdataCode 1Byte|PayloadLength 1Byte|Checksum 2Byte 0x10+0x01 = 0x11
	short payloadlength = 0;
	int datacode = 0;
	byte subdatacode = 0;
	int pdn = 0;
	byte misc = 0;
	int  runningsum = 0;
	int actpos = 0;
	int checksum = 0;
	byte [] timestamp = new byte[6];
	int i = 0;	
	int d = 0;
	int e = 0;
	int [] send_data = new int[128];
	long a = 0;
	int k = 0;
//	double time = 0;
	//For Parsing End
	
	byte [] turn_off = new byte[15];
	int [] value = new int[6];

	// declare member variables here
	CIMPortController portController = null;
  
    
   /**
    * The class constructor.
    */
    public TrueSenseInstance()
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

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("signal".equalsIgnoreCase(portID))
		{
			return opSignal;
		}
		if ("temp".equalsIgnoreCase(portID))
		{
			return opTemp;
		}
		if ("accX".equalsIgnoreCase(portID))
		{
			return opAccX;
		}
		if ("accY".equalsIgnoreCase(portID))
		{
			return opAccY;
		}
		if ("accZ".equalsIgnoreCase(portID))
		{
			return opAccZ;
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
		if ("start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		if ("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
		}
		if ("baudRate".equalsIgnoreCase(propertyName))
		{
			return propBaudRate;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComPort;
			propComPort = (String)newValue;
			return oldValue;
		}
		if ("baudRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBaudRate;
			propBaudRate = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};

	public class OutputPort extends DefaultRuntimeOutputPort
   	{
   		/**
   		 * Sends data to the connected input port 
   		 * @param data a double value to be sent
   		 * 
   		 */
   		public void sendData(double data)
   		{	
   			//System.out.println("SendData :" +data);
   			super.sendData(ConversionUtils.doubleToBytes(data));
   			
   		}
   	}
	
public void handlePacketReceived(byte data)
	{
		//System.out.println("val = " + Integer.toHexString(0x000000ff & data) +" hex");
		
		//	Sub-DataCode | TimeStamp | PDN | Misc. |    ADC       | Temp. | AccX | AccY | AccZ   |   ED
		//		(1)      | (6)       |(1)  | (1)   |   (128/124)  |  (1)  |  (1) |  (1) |   (4)  |   (1) 
		
		switch (state) 
		{

			case 0:
				
			//	System.out.println("Got start of start sequence" +a);
				
				if (data == 0x33)
				{	//System.out.println("Got 0x33 " +data);
				 	state = 1;
				}
				break;
				
			case 1:
				if (data == 0x33) 
				{ 
					state = 2;
				//	System.out.println("Got 0x33 " +data);
				}
				else state = 0;
				
				break;
				
			case 2:	// payloadlength

				{	
					payloadlength = data;
				
					
					state = 3;
					break;

				}	
			case 3:	// payloadlength
				
				{	
				
					payloadlength += (data&0xFF);// 145 -> -110// -110 + 255 = 146 ->0x92
					payloadlength--;
					state=4; 
					break;
				
				}
				
			case 4:// datacode
				
			//	System.out.println("datacode == "+ data);
					
				datacode = data;
				
				if (datacode == 0x01) // Datenuebertragung 
				{	
				//	System.out.println("Datenuebertragung ");
					i = 0;
					state = 5;
					actpos=0; runningsum=0;
					break;
				}
				
				
				if (datacode == 0x40) // Status OK
				{	
				//	System.out.println("Status OK ");
					state = 0;
					break;
				}
				
				if (datacode == 0x41) // Status NOK
				{	
					System.out.println("Status NOK ");
					state = 0;
					break;
				}
				
				else
				{
					System.out.println("unknown datacode :" + datacode);
					state = 0;
					break;
				}
				

			case 5: 
				
				
				data_buff[actpos++]=data;
				
				if (actpos > 255)
				{actpos = 0;}
				//System.out.println("runningsum: "+runningsum+" Databuff: "+data_buff[actpos] +" actpos: " +actpos);
				//System.out.println("Databuff "+data_buff[actpos] + actpos);
				runningsum+=(data&0xff);  //ueberpruefen
				
			//	runningsum+=data;
				
				//System.out.println("runningsum: "+runningsum+"Databuff: "+data_buff[actpos] +"actpos: " +actpos);
				if (actpos == payloadlength) 
					{
					state=6;
				//	System.out.println("actpos: " +actpos + "payloadlength: "+payloadlength + "runningsum_final: "+runningsum);
					}
				
				break;
				
			case 6: 
				//  System.out.println("State 6: checksum1: " +checksum+ " data1: " +data);
				  checksum=(data & 0xff);
				 // checksum=(data);
				//  System.out.println("State 6: checksum2: " +checksum+ " data2: " +data);
				  checksum=checksum <<8;
				//  System.out.println("State 6: checksum3: " +checksum+ " data3: " +data);
                  state=7;
                  break;
				  
			case 7: 
				
				//System.out.println("State 7: checksum3: " +checksum+ " data3: " +data+ " runningsum3: "+runningsum );	
				checksum+=(data&0xff);
				checksum--;
              
					subdatacode = data_buff[0];
					pdn = data_buff[7];
					//System.out.println("pdn before 0xff: "+ pdn );
					pdn = pdn&0xFF;
				//	System.out.println("pdn after 0xff: "+ pdn );
					misc = data_buff[8];
					state = 0;
			

						for ( i=0; i< 64; i++)	
						{
							if (((data_buff[9+i*2]&128) == 128) )		
							{   d = ((int)data_buff[9+i*2] & 0xff);
								d = d << 8;
								e = (int)data_buff[10+i*2] & 0xfc;
								d = d  | e;
								d = d| 0xffff0000;
							}
							else
							{
								d= ((((int)data_buff[9+i*2]) & 0xff) << 8)
									| (((int)data_buff[10+i*2]) & 0xfc);
							}				
							
							if (d == -20480) //Ist Sensor in der Saettigung?
				    		  { 
				    			  d *= -1;
				    		  }
							opSignal.sendData(ConversionUtils.doubleToBytes(d));
						}
			
				checksum = 0;
		}
	}
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  
    	//System.out.println("truesense start");
    	
		  portController = CIMPortManager.getInstance().getRawConnection(propComPort,propBaudRate,true);
    	  
    	  if (portController == null) 
    	  {
    		 AstericsErrorHandling.instance.reportError(this, 
    		 "Truesense-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
    	  } 
    	  else 
    	  { 
 
    		  //System.out.println("truesense send start");
    		  CIMPortManager.getInstance().sendPacket(portController,turn_on, (short) 0,(short) 0, false); 
			  in = portController.getInputStream();
    		  readerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					running = true;
				  	// System.out.println("truesense in read thread");

					while (running) {

						try { 
							while (in.available() > 0) {

								handlePacketReceived((byte) in.read());
								//System.out.println("in.read() "+in.read());
							} 
					//		System.out.println("truesense request data");
							//Thread.sleep(100);
							CIMPortManager.getInstance().sendPacket(portController,request_data, (short) 0,(short) 0, false); 
							Thread.sleep(30);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						} catch (IOException io) {
							io.printStackTrace();
						}
					
					}
				}
    			  
    		  });
    		  readerThread.start();
    	  }
    
    	  super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {	  
    	if (portController != null) {
  			CIMPortManager.getInstance().closeRawConnection(propComPort);
  			portController = null;
  			AstericsErrorHandling.instance.reportInfo(this, "Truesense raw port controller closed");
  		}
    	running = false;
        super.pause();
        
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate,true);
  		if (portController == null) {
  			AstericsErrorHandling.instance.reportError(this, "Could not construct Truesense raw port controller, please make sure that the COM port is valid.");
  		}
  		readerThread.start();
  		super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
	  if (portController != null) {
        	CIMPortManager.getInstance().closeRawConnection(propComPort);
  			portController = null;
  			AstericsErrorHandling.instance.reportInfo(this, "Truesense connection closed");
  			running = false;
        }

          super.stop();

      }
}