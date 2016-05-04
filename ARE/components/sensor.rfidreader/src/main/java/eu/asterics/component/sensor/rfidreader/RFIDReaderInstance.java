

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

package eu.asterics.component.sensor.rfidreader;


import java.util.ArrayDeque;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * RFIDReaderInstance interfaces to the IDinnovations RFID-Reader
 * via a raw com port. A recgnized tag-ID is sent to the output port as a string.
 * 
 *  
 * @author <Chris Veigl> [veigl@technikum-wien.at]
 * 
 */
public class RFIDReaderInstance extends AbstractRuntimeComponentInstance
		implements CIMEventHandler
{
	final IRuntimeOutputPort opTagID = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propComPort = "COM8";
	int propBaudRate = 9600;
	// declare member variables here

	CIMPortController portController = null;
	ArrayDeque<Byte> inputQueue = new ArrayDeque<Byte>();
	static int state =0;
	static int readpos =0;
    static final int ID_LENGTH = 12;
	static byte [] array = new byte[ID_LENGTH];


    
   /**
    * The class constructor.
    */
    public RFIDReaderInstance()
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
		if ("tagID".equalsIgnoreCase(portID))
		{
			return opTagID;
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
			CIMPortManager.getInstance().closeRawConnection(propComPort);
			propComPort = (String) newValue;
			AstericsErrorHandling.instance.reportInfo(this, "Set COM port attribute to:" + propComPort);            
			return oldValue;
		}
		if ("baudRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBaudRate;
			propBaudRate = Integer.parseInt((String) newValue);
			CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate);
			AstericsErrorHandling.instance.reportInfo(this, "Set COM port baudrate to:" + propBaudRate);            
			return oldValue;
		}

        return null;
    }

	/**
	 * Called by the raw port controller if data is available
	 * @param ev a CIMEvent which can be ignored as it is only needed due to the
	 * interface specification
	 */
	public void handlePacketReceived(CIMEvent ev)
	{
		byte data;

		CIMEventRawPacket rp = (CIMEventRawPacket ) ev;
		data = rp.b;
		
		switch (state) {
			case 0: if (data==0x02) {state++; readpos=0;}
					break;
			case 1: if (( (data>='0') && (data<='9')) ||
						  ((data>='A') && (data<='F')))
					  {
						  array[readpos++]=data;
					  }
					  else state=0;
					  if (readpos==ID_LENGTH) state++;
					break;
			case 2: if (data==0x0d) state++; else state=0;
					break;
			case 3: if (data==0x0a) state++; else state=0;
					break;
			case 4: if (data==0x03) 
					{
						//array[readpos]=0;
				 		opTagID.sendData(ConversionUtils.stringToBytes(new String(array)));
					}
					state=0;
					break;						  
		}
	}

	/**
	 * Method stub, needed by CIM listener interface 
	 */
	public void handlePacketError(CIMEvent e)
	{

	}

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
	  		portController = CIMPortManager.getInstance()
			.getRawConnection(propComPort, propBaudRate);
			if (portController == null)
			{
				AstericsErrorHandling.instance.reportError(this, 
						"Could not construct raw port controller for RFID reader, please verify that the reader module is conencted to an USB port and correctly installed.");
			}
			else portController.addEventListener(this);
            super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
	  		if (portController != null)
			{
				portController.removeEventListener(this);
				CIMPortManager.getInstance().closeRawConnection(propComPort);
				portController = null;
				AstericsErrorHandling.instance.reportInfo(this, "Raw port controller closed");
			}
	        super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
	  		portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate);
			if (portController == null)
			{
				AstericsErrorHandling.instance.reportError(this, 
						"Could not construct raw port controller for RFID reader, please verify that the reader module is conencted to an USB port and correctly installed.");

			}
			else portController.addEventListener(this);
	
			super.resume();	    	
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
            super.stop();
	  		if (portController != null)
			{
				portController.removeEventListener(this);
				CIMPortManager.getInstance().closeRawConnection(propComPort);
				portController = null;
				AstericsErrorHandling.instance.reportInfo(this, "Connection closed");
			}

      }
}