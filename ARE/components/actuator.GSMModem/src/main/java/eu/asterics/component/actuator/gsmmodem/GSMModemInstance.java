

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

package eu.asterics.component.actuator.gsmmodem;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * Implements GSM modem plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 11, 2011
 *         Time: 4:27:47 PM
 */

public class GSMModemInstance extends AbstractRuntimeComponentInstance
{
	
	private final String IP_PHONE_ID="phoneID";
	private final String IP_SMS_CONTENT ="SMSContent";
	private final String OP_REMOTE_PHONE_ID="remotePhoneID";
	private final String OP_RECEIVED_SMS="receivedSMS";
	private final String OP_ERROR_NUMBER="errorNumber";
	private final String ELP_SEND_SMS="sendSMS";
	private final String ETP_NEW_SMS ="newSMS";
	private final String ETP_ERROR ="error";
	
	private final String PROP_SERIAL_PORT="serialPort";
	private final String PROP_PIN ="pin";
	private final String PROP_SMS_CENTER_ID="smsCenterID";
	private final String PROP_DEFAULT_PHONE_ID="defaultPhoneID";
	
	final IRuntimeOutputPort opRemotePhoneID = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opReceivedSMS = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opErrorNumber = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpNewSMS = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpError = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here
	
	GSMModemBridge bridge=new GSMModemBridge(this,opRemotePhoneID,opReceivedSMS,opErrorNumber,etpNewSMS,etpError);

  
    
   /**
    * The class constructor.
    */
    public GSMModemInstance()
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
		if (IP_PHONE_ID.equalsIgnoreCase(portID))
		{
			return ipPhoneID;
		}
		if (IP_SMS_CONTENT.equalsIgnoreCase(portID))
		{
			return ipSMSContent;
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
		if (OP_REMOTE_PHONE_ID.equalsIgnoreCase(portID))
		{
			return opRemotePhoneID;
		}
		if (OP_RECEIVED_SMS.equalsIgnoreCase(portID))
		{
			return opReceivedSMS;
		}
		if (OP_ERROR_NUMBER.equalsIgnoreCase(portID))
		{
			return opErrorNumber;
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
		if (ELP_SEND_SMS.equalsIgnoreCase(eventPortID))
		{
			return elpSendSMS;
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
		if (ETP_NEW_SMS.equalsIgnoreCase(eventPortID))
		{
			return etpNewSMS;
		}
		if (ETP_ERROR.equalsIgnoreCase(eventPortID))
		{
			return etpError;
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
		if (PROP_SERIAL_PORT.equalsIgnoreCase(propertyName))
		{
			return bridge.getPropSerialPort();
		}
		if (PROP_PIN.equalsIgnoreCase(propertyName))
		{
			return bridge.getPropPin();
		}
		if (PROP_SMS_CENTER_ID.equalsIgnoreCase(propertyName))
		{
			return bridge.getPropSmsCenterID();
		}
		if (PROP_DEFAULT_PHONE_ID.equalsIgnoreCase(propertyName))
		{
			return bridge.getPhoneID();
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
		if (PROP_SERIAL_PORT.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = bridge.getPropSerialPort();
			bridge.setPropSerialPort((String)newValue);
			return oldValue;
		}
		if (PROP_PIN.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = bridge.getPropPin();
			bridge.setPropPin((String)newValue);
			return oldValue;
		}
		if (PROP_SMS_CENTER_ID.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = bridge.getPropSmsCenterID();
			bridge.setPropSmsCenterID((String)newValue);
			return oldValue;
		}
		if (PROP_DEFAULT_PHONE_ID.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = bridge.getPhoneID();
			bridge.setPhoneID((String)newValue);
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for the Phone ID.
      */
	private final IRuntimeInputPort ipPhoneID  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			bridge.setPhoneID(ConversionUtils.stringFromBytes(data));
		}

	};
	
	 /**
     * Input Ports for the Message content.
     */
	private final IRuntimeInputPort ipSMSContent  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			bridge.setMessageContent(ConversionUtils.stringFromBytes(data));
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSendSMS = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 bridge.sendSMS();
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          bridge.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
          bridge.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  super.resume();
    	  bridge.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          super.stop();
          bridge.stop();
      }
}