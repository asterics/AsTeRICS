

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

package eu.asterics.component.sensor.eshoe;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
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
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class ESHOEInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opAngle = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeel = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMeta1 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMeta5 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opToe = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyroX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyroY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyroZ = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpToePressureReached = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	final int CMD_START=1;
	final int CMD_STOP=2;
	final int CMD_CALIBRATE=3;
	final int TOE_EVENT_IDLE=10;
	
	int propSamplingRate = 0;
	int toeEventIdleTime=TOE_EVENT_IDLE;
	double propToePressureThreshold = 100;
	String propComPort = "COM4";


	// declare member variables here

	CIMPortController portController = null;
	private InputStream in = null;
	private OutputStream out = null;
	private Thread readerThread = null;
	private boolean running = false;
	int state=0;
	byte [] array = new byte[50];
	int [] value = new int[20];
	int i = 0;
		
  
    
   /**
    * The class constructor.
    */
    public ESHOEInstance()
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
		if ("angle".equalsIgnoreCase(portID))
		{
			return opAngle;
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
		if ("heel".equalsIgnoreCase(portID))
		{
			return opHeel;
		}
		if ("meta1".equalsIgnoreCase(portID))
		{
			return opMeta1;
		}
		if ("meta5".equalsIgnoreCase(portID))
		{
			return opMeta5;
		}
		if ("toe".equalsIgnoreCase(portID))
		{
			return opToe;
		}
		if ("gyroX".equalsIgnoreCase(portID))
		{
			return opGyroX;
		}
		if ("gyroY".equalsIgnoreCase(portID))
		{
			return opGyroY;
		}
		if ("gyroZ".equalsIgnoreCase(portID))
		{
			return opGyroZ;
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
		if ("calibrate".equalsIgnoreCase(eventPortID))
		{
			return elpCalibrate;
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
		if ("toePressureReached".equalsIgnoreCase(eventPortID))
		{
			return etpToePressureReached;
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
    	if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
		}
    	if ("samplingRate".equalsIgnoreCase(propertyName))
		{
			return propSamplingRate;
		}
		if ("toePressureThreshold".equalsIgnoreCase(propertyName))
		{
			return propToePressureThreshold;
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
			propComPort = newValue.toString();
			return oldValue;
		}
    	if ("samplingRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSamplingRate;
			propSamplingRate = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("toePressureThreshold".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propToePressureThreshold;
			propToePressureThreshold = Double.parseDouble((String)newValue);
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

    void eShoeCommand(int command)
    {
    	try	{
	    	switch (command) {
			    	case CMD_START:
			    	{
						byte [] rawbytes = {0x55,0x02,0x04,0x01,0x00,0x00,0x00};  // start command
						if (out!=null)	out.write(rawbytes);
						break;
			    	}
			    	case CMD_STOP:
			    	{
						byte [] rawbytes = {0x55,0x03,0x00}; 		 // stop command
						if (out!=null)	out.write(rawbytes);
						break;
			    	}
			    	case CMD_CALIBRATE:
			    	{
						byte [] rawbytes = {0x55,0x10,0x00};  // calibration command
						if (out!=null)	out.write(rawbytes);
						break;
			    	}
	    		}
		} catch (IOException io) {
			io.printStackTrace();
		}					
    }
    
    final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public synchronized void receiveEvent(final String data)
		{
			System.out.println("start");
			eShoeCommand(CMD_START);
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			System.out.println("stop");
			eShoeCommand(CMD_STOP);
		}
	};
	final IRuntimeEventListenerPort elpCalibrate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			eShoeCommand(CMD_CALIBRATE);
		}
	};

	
	public void handlePacketReceived(byte data)
	{
		//System.out.println("val = " + Integer.toHexString(0x000000ff & data) +" hex");
		// state machine
		
		switch (state) {
			case 0:
				if (data == 0x55)
					//System.out.println("Got start of sequence");
					state = 1;
				break;
			case 1:
				if (data  == (byte) 0xFF)  
					state = 2;	else state = 0;
				break;
			case 2:
				if (data == 24) {  
						state = 3;
						i = 0;
					}
					else state = 0;	
				break;
			default:
				array[i] = data;
				i++;
				if (i == 24) {
					state = 0;
					System.out.print("*");
					
					int x = 2;
					for (int k = 0 ; k < 11; k++)
					{
						value[k]=(((int) array[x]) & 0xff) | ((((int) array[x+1]) & 0xff) << 8);
						if ((array[x+1] & 128) !=0) value[k] |= 0xffff0000;
						
						// value[k] =(int)( (array[x] << 8) + array[x+1]);
			            x++;
			            x++;
					}

					if (value[7]>propToePressureThreshold)	{
						if (toeEventIdleTime==TOE_EVENT_IDLE) {
							etpToePressureReached.raiseEvent();
						}
						toeEventIdleTime=0;
					}
					else if (toeEventIdleTime<TOE_EVENT_IDLE) toeEventIdleTime++; 
					
					opAngle.sendData(ConversionUtils.doubleToBytes(value[0]));
					opAccX.sendData(ConversionUtils.doubleToBytes(value[1]));
					opAccY.sendData(ConversionUtils.doubleToBytes(value[2]));
					opAccZ.sendData(ConversionUtils.doubleToBytes(value[3]));
					opHeel.sendData(ConversionUtils.doubleToBytes(value[4]));
					opMeta1.sendData(ConversionUtils.doubleToBytes(value[5]));
					opMeta5.sendData(ConversionUtils.doubleToBytes(value[6]));
					opToe.sendData(ConversionUtils.doubleToBytes(value[7]));
					opGyroX.sendData(ConversionUtils.doubleToBytes(value[8]));
					opGyroY.sendData(ConversionUtils.doubleToBytes(value[9]));
					opGyroZ.sendData(ConversionUtils.doubleToBytes(value[10]));
					
				}			
		}
	}	
	
	
	void startReaderThread()
	{
  		  readerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				running = true;
				while (running) {

					try { 
						while (in.available() > 0) 
							handlePacketReceived((byte) in.read());
						Thread.sleep(5);
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
	
	
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
      	  portController = CIMPortManager.getInstance().getRawConnection(propComPort,115200,true);
    	  
      	  if (portController == null) {
      		 AstericsErrorHandling.instance.reportError(this, 
      		 "eSHOE-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
      	  } else {
      		  in = portController.getInputStream();
      		  out = portController.getOutputStream();
      		  startReaderThread();
      		  System.out.println("Reader Thread started");
      	  }
    	  super.start();
      }

      /**
       * called when model is paused.
       */
       @Override
       public void pause()
       {	 
    	 eShoeCommand(CMD_STOP);
     	 running = false;
         super.pause();
       }

      /**
       * called when model is resumed.
       */
       @Override
       public void resume()
       {
    	   if (in!=null)   startReaderThread();
    	   super.resume();
       }

      /**
       * called when model is stopped.
       */
       @Override
       public void stop()
       {
         if (portController != null) {
        	eShoeCommand(CMD_STOP); 
   			running = false;
         	CIMPortManager.getInstance().closeRawConnection(propComPort);
   			portController = null;
   			in=null;out=null;
   			AstericsErrorHandling.instance.reportInfo(this, "eSHOE connection closed");
         }
         super.stop();
       }
}