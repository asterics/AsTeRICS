

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

package eu.asterics.component.processor.comport;


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
public class ComPortInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opReceived = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propComPort = "COM4";
	int propBaudRate = 57600;
	int propReceivedDataType = 0;
	int propSendDataType = 1;

	// declare member variables here
	private InputStream in = null;
	private OutputStream out = null;

	private Thread readerThread = null;
	private boolean running = false;
	
	String receivedMessage = "";
	
	
	// declare member variables here
	CIMPortController portController = null;

  
    
   /**
    * The class constructor.
    */
    public ComPortInstance()
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
		if ("send".equalsIgnoreCase(portID))
		{
			return ipSend;
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
		if ("received".equalsIgnoreCase(portID))
		{
			return opReceived;
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
		if ("receivedDataType".equalsIgnoreCase(propertyName))
		{
			return propReceivedDataType;
		}
		if ("sendDataType".equalsIgnoreCase(propertyName))
		{
			return propSendDataType;
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
		if ("receivedDataType".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propReceivedDataType;
			propReceivedDataType = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("sendDataType".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSendDataType;
			propSendDataType = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipSend  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String str=ConversionUtils.stringFromBytes(data);
			System.out.print("Trying to send:"+ str);

			if (out!=null)
			{
				try {					
					switch (propSendDataType) {
					case 0:
						out.write(data);
						break;
					case 1:						
						out.write(data);
						out.write('\n');
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};


	/**
	 * Called by the raw port controller if data is available
	 * @param ev a CIMEvent which can be ignored as it is only needed due to the
	 * interface specification
	 */
	public void handlePacketReceived(byte data) {
		
		System.out.print((char) data);

		switch (propReceivedDataType) {
				case 0:   //string
					if ((char) data == '\n') {
						opReceived.sendData(ConversionUtils.stringToBytes(receivedMessage) );
							receivedMessage = "";
					} 
					else receivedMessage += (char) data;
					break;
		}
	}

	

    /**
     * called when model is started.
     */
     @Override
     public void start()
     {
   	  portController = CIMPortManager.getInstance().getRawConnection(propComPort,propBaudRate,true);
   	  
   	  if (portController == null) {
   		 AstericsErrorHandling.instance.reportError(this, 
   		 "ComPort-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
   	  } else {

   		  System.out.print("COM Port "+propComPort+" opened !");

   		  in = portController.getInputStream();
   		  out = portController.getOutputStream();
          receivedMessage="";
   		  readerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					running = true;
					while (running) {

						try { 
							if (in.available() > 0) {
								handlePacketReceived((byte) in.read());
							} else {
								Thread.sleep(5);
							}
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
       super.stop();
       if (portController != null) {
       	CIMPortManager.getInstance().closeRawConnection(propComPort);
 			portController = null;
 			in=null;
 			out=null;
 			AstericsErrorHandling.instance.reportInfo(this, "ComPort connection closed");
 			running = false;
       }
     }
  }