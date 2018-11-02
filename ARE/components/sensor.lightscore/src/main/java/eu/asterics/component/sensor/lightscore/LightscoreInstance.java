

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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.lightscore;


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
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.AREServices;

import java.io.IOException;
import java.net.*;






/**
 * 
 * This module interfaces with the wearable Lightscore sensor (9DOF IMU + RGB light intensity)
 * Note: the Lightscore node.js BLE interface application must be running 
 *       - it continously sends ASCII string data to UPD port 3005
 * 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 10/2018
 */
public class LightscoreInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opRed = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGreen = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opBlue = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opWhite = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAccZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMagX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMagY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMagZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyrX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyrY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opGyrZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opYaw = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPitch = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opRoll = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int LIGHTSCORE_PORT = 3005;
	boolean propAutostart = true;
    boolean readerThreadRunning=false;
	String propId = "none";

	// declare member variables here

   
    
   /**
    * The class constructor.
    */
    public LightscoreInstance()
    {
        System.out.println("Hello Lightscores");
    	
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("led1".equalsIgnoreCase(portID))
		{
			return ipLed1;
		}
		if ("led2".equalsIgnoreCase(portID))
		{
			return ipLed2;
		}
		if ("led3".equalsIgnoreCase(portID))
		{
			return ipLed3;
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
		if ("red".equalsIgnoreCase(portID))
		{
			return opRed;
		}
		if ("green".equalsIgnoreCase(portID))
		{
			return opGreen;
		}
		if ("blue".equalsIgnoreCase(portID))
		{
			return opBlue;
		}
		if ("white".equalsIgnoreCase(portID))
		{
			return opWhite;
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
		if ("magX".equalsIgnoreCase(portID))
		{
			return opMagX;
		}
		if ("magY".equalsIgnoreCase(portID))
		{
			return opMagY;
		}
		if ("magZ".equalsIgnoreCase(portID))
		{
			return opMagZ;
		}
		if ("gyrX".equalsIgnoreCase(portID))
		{
			return opGyrX;
		}
		if ("gyrY".equalsIgnoreCase(portID))
		{
			return opGyrY;
		}
		if ("gyrZ".equalsIgnoreCase(portID))
		{
			return opGyrZ;
		}
		if ("yaw".equalsIgnoreCase(portID))
		{
			return opYaw;
		}
		if ("pitch".equalsIgnoreCase(portID))
		{
			return opPitch;
		}
		if ("roll".equalsIgnoreCase(portID))
		{
			return opRoll;
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
		if ("autostart".equalsIgnoreCase(propertyName))
		{
			return propAutostart;
		}
		if ("id".equalsIgnoreCase(propertyName))
		{
			return propId;
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
		if ("autostart".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutostart;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutostart = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutostart = false;
			}
			return oldValue;
		}
		if ("id".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propId;
			propId = (String)newValue;
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipLed1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipLed2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipLed3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};


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

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

          super.start();
          System.out.println("Starting Lightscores component");
          ReaderThread readerT = new ReaderThread(LIGHTSCORE_PORT);
          AstericsThreadPool.instance.execute(readerT);
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
          System.out.println("Stopping Lightscores component");
    	  readerThreadRunning=false;
          super.stop();
      }
      
      
	  private class ReaderThread implements Runnable {   
	    			  
	    DatagramSocket serverSocket=null;
	    private int port;
        byte[] receiveData = new byte[4096];

	    public ReaderThread(int port) {
            this.port = port;
        }
 
        @Override
        public void run() {
 
	      try {
            System.out.println("Starting Lightscores reader thread");
	        serverSocket = new DatagramSocket(port);
	        System.out.printf("Listening on udp:%s:%d%n",
	                InetAddress.getLocalHost().getHostAddress(), port);     

	        DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
	        readerThreadRunning=true;
	        //String sendString = "test";
	        //byte[] sendData = sendString.getBytes("UTF-8");

	        while(readerThreadRunning)
	        {
	        	  double d=0,actvalue=0;
	              serverSocket.receive(receivePacket);
	              String sentence = new String( receivePacket.getData(), 0,
	                                 receivePacket.getLength() );
	              // System.out.println("RECEIVED: " + sentence);

	              String[] parts = sentence.split("[,;\\s\\?]");
	              for (String acttoken: parts) {
	            	  
	            	  try
	            	  {
	            	      d = Double.valueOf(acttoken.trim()).doubleValue();
	            	      //System.out.println("number found: " + d);
	            	      actvalue=d;
	            	  }
	            	  catch (NumberFormatException nfe)
	            	  {
	            	      // System.out.println("not a number: " + acttoken);
		            	  switch (acttoken.trim()) {
		            	  	case "s1r": opRed.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "s1g": opGreen.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "s1b": opBlue.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "s1w": opWhite.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "acc1x": opAccX.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "acc1y": opAccY.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "acc1z": opAccZ.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "mag1x": opMagX.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "mag1y": opMagY.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "mag1z": opMagZ.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "gyr1x": opGyrX.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "gyr1y": opGyrY.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "gyr1z": opGyrZ.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "eul1x": opRoll.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "eul1y": opPitch.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  	case "eul1z": opYaw.sendData(ConversionUtils.doubleToBytes(actvalue)); break;
		            	  }
	            	  }
	              }
	              
	              // now send acknowledgement packet back to sender     
	              //InetAddress IPAddress = receivePacket.getAddress();
	              //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
	              //     IPAddress, receivePacket.getPort());
	              //serverSocket.send(sendPacket);
	        }
	      } catch (IOException e) {
	              System.out.println(e);
	      }
	      finally {
	    	  serverSocket.close();
	          System.out.println("Lightscores Reader Thread stopped.");
	      }
	    }
	  }
	  
}