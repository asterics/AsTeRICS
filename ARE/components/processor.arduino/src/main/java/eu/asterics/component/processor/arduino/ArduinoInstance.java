

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

package eu.asterics.component.processor.arduino;

 
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;
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
 * 
 * interfaces to the Arduino Microcontroller (running the CIM firmware)
 * 
 * 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 11.11.2011
 *         Time: 00:07 AM
 */
public class ArduinoInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private final short ARDUINO_CIM_ID 			= (short) 0xa001;
	private static final short ARDUINO_CIM_FEATURE_UNIQUENUMBER = 0x0000;
	private static final short ARDUINO_CIM_FEATURE_SET_PINDIRECTION	= 0x0001;
	private static final short ARDUINO_CIM_FEATURE_SET_PINSTATE   	= 0x0002;
	private static final short ARDUINO_CIM_FEATURE_GET_PINVALUE   	= 0x0003;
	private static final short ARDUINO_CIM_FEATURE_SET_ADCPERIOD   	= 0x0004;
	private static final short ARDUINO_CIM_FEATURE_ADCREPORT 	  	= 0x0005;
	private static final short ARDUINO_CIM_FEATURE_SET_PINMASK 	  	= 0x0006;
	private static final short ARDUINO_CIM_FEATURE_SET_PWM 	      	= 0x0007;

	private static final int PINMODE_NOT_USED = 0;
	private static final int PINMODE_INPUT_WITHOUT_PULLUP = 1;
	private static final int PINMODE_INPUT_WITH_PULLUP = 2;
	private static final int PINMODE_OUTPUT_DEFAULT_LOW = 3;
	private static final int PINMODE_OUTPUT_DEFAULT_HIGH = 4;
	private static final int PINMODE_PWM_SERVO = 5;
	private static final int PINMODE_PWM_500Hz = 6;
	//private static final int PINMODE_PWM_10kHz = 7;
	//private static final int PINMODE_PWM_28kHz = 8;

	public final int NUMBER_OF_ADCPORTS = 6;
	public final int NUMBER_OF_PINS = 12;  // pins 2 to 13 available
	public final int STARTPIN   = 2; 

	private final String PIN_PREFIX = "pin";
	private final String OP_ADC_PREFIX = "A";
	private final String ELP_SETPIN_PREFIX = "setPin";
	private final String ELP_CLEARPIN_PREFIX = "clearPin";
	private final String ETP_LOWHIGH_POSTFIX = "ChangedLowToHigh";
	private final String ETP_HIGHLOW_POSTFIX = "ChangedHighToLow";
	private final String PROP_MODE_POSTFIX = "Mode";

//	final IRuntimeOutputPort opA0 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opA1 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opA2 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opA3 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opA4 = new DefaultRuntimeOutputPort();

	public final SetPinListener [] elpSetPin = new SetPinListener[NUMBER_OF_PINS];    
	public final ClearPinListener [] elpClearPin = new ClearPinListener[NUMBER_OF_PINS];    
	public final IRuntimeOutputPort [] opAdc = new DefaultRuntimeOutputPort[NUMBER_OF_ADCPORTS];
	public final IRuntimeEventTriggererPort [] etpChangedLowToHigh = new DefaultRuntimeEventTriggererPort[NUMBER_OF_PINS];    
	public final IRuntimeEventTriggererPort [] etpChangedHighToLow = new DefaultRuntimeEventTriggererPort[NUMBER_OF_PINS];    

	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	//use default value 200
	public int propPeriodicADCUpdate = 200;
	public int[] propPinMode= new int[NUMBER_OF_PINS];
	
	// declare member variables here

	private CIMPortController port = null; 
	private int pinMask = 0;
	private int pinState = 0;
	private int pinDirection = 0;
	
	private int currentPinValue = 0;
	private boolean firstPinReport = true;
	
	private int pin3Mode = 0; //mode of pin 3 (0 -> disabled, 1 -> servo, 2-> 1khz PWM, 3-> 10khz PWM, 4-> 28khz PWM)
	private int pin5Mode = 0; //mode of pin 5 (0 -> disabled, 1 -> servo, 2-> 1khz PWM, 3-> 10khz PWM, 4-> 28khz PWM)
	private int pin6Mode = 0; //mode of pin 6 (0 -> disabled, 1 -> servo, 2-> 1khz PWM, 3-> 10khz PWM, 4-> 28khz PWM)

	
   /**
    * The class constructor.
    */
    public ArduinoInstance()
    {
		for (int i = 0; i < NUMBER_OF_PINS; i++)
		{
			elpSetPin[i] = new SetPinListener(i);
			elpClearPin[i] = new ClearPinListener(i);
			etpChangedLowToHigh[i] = new DefaultRuntimeEventTriggererPort();
			etpChangedHighToLow[i] = new DefaultRuntimeEventTriggererPort();
			propPinMode[i]=0;
		}    
		for (int i = 0; i < NUMBER_OF_ADCPORTS; i++)
		{
			opAdc[i]=new DefaultRuntimeOutputPort();
		}
    }

    /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("pwm3".equalsIgnoreCase(portID))
		{
			return ipPwm3;
		}
		if ("pwm5".equalsIgnoreCase(portID))
		{
			return ipPwm5;
		}
		if ("pwm6".equalsIgnoreCase(portID))
		{
			return ipPwm6;
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
    	String s;
		for (int i = 0; i < NUMBER_OF_ADCPORTS; i++)
		{
			s = OP_ADC_PREFIX + i;
			if (s.equalsIgnoreCase(portID))
			{
				return opAdc[i];
			}
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
    	String s;
		for (int i = 0; i < NUMBER_OF_PINS; i++)
		{
			s = ELP_SETPIN_PREFIX + (i + STARTPIN);
			if (s.equalsIgnoreCase(eventPortID))
			{
				return elpSetPin[i];
			}
			s = ELP_CLEARPIN_PREFIX + (i + STARTPIN);
			if (s.equalsIgnoreCase(eventPortID))
			{
				return elpClearPin[i];
			}
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
    	String s;
		for (int i = 0; i < NUMBER_OF_PINS; i++)
		{
			s = PIN_PREFIX + (i+STARTPIN) + ETP_LOWHIGH_POSTFIX;
			if (s.equalsIgnoreCase(eventPortID))
			{
				return etpChangedLowToHigh[i];
			}
			s = PIN_PREFIX + (i+STARTPIN) + ETP_HIGHLOW_POSTFIX;
			if (s.equalsIgnoreCase(eventPortID))
			{
				return etpChangedHighToLow[i];
			}
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
    	String s;
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			return propPeriodicADCUpdate;
		}
		for (int i = 0; i < NUMBER_OF_PINS; i++)
		{
			s = PIN_PREFIX + (i+STARTPIN) + PROP_MODE_POSTFIX;
			if (s.equalsIgnoreCase(propertyName))
			{
				return propPinMode[i];
			}
		}
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param[in] propertyName   the name of the property
     * @param[in] newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
	   	String s;
		if ("periodicADCUpdate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPeriodicADCUpdate;
			propPeriodicADCUpdate = Integer.parseInt(newValue.toString());
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
			return oldValue;
		}
		
		for (int i = 0; i < NUMBER_OF_PINS; i++)
		{
			s = PIN_PREFIX + (i+STARTPIN) + PROP_MODE_POSTFIX;
			if (s.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propPinMode[i];
				propPinMode[i] = Integer.parseInt(newValue.toString());
				switch (propPinMode[i])
				{
					case PINMODE_NOT_USED:
						 if((i+STARTPIN) == 3) pin3Mode=0;
						 if((i+STARTPIN) == 5) pin5Mode=0;
						 if((i+STARTPIN) == 6) pin6Mode=0;
			  			 pinState &= ~(1<<(i+STARTPIN));
			  			 pinDirection &= ~(1<<(i+STARTPIN));
			  			 pinMask &= ~(1<<(i+STARTPIN));
			  			break;
					case PINMODE_INPUT_WITHOUT_PULLUP:
						 if((i+STARTPIN) == 3) pin3Mode=0;
						 if((i+STARTPIN) == 5) pin5Mode=0;
						 if((i+STARTPIN) == 6) pin6Mode=0;
			  			 pinState &= ~(1<<(i+STARTPIN));
			  			 pinDirection &= ~(1<<(i+STARTPIN));
			  			 pinMask |= (1<<(i+STARTPIN));
						break;
					case PINMODE_INPUT_WITH_PULLUP:
						 if((i+STARTPIN) == 3) pin3Mode=0;
						 if((i+STARTPIN) == 5) pin5Mode=0;
						 if((i+STARTPIN) == 6) pin6Mode=0;
			  			 pinState |= (1<<(i+STARTPIN));
			  			 pinDirection &= ~(1<<(i+STARTPIN));
			  			 pinMask |= (1<<(i+STARTPIN));
						break;
					case PINMODE_OUTPUT_DEFAULT_LOW:
						 if((i+STARTPIN) == 3) pin3Mode=0;
						 if((i+STARTPIN) == 5) pin5Mode=0;
						 if((i+STARTPIN) == 6) pin6Mode=0;
			  			 pinState &= ~(1<<(i+STARTPIN));
			  			 pinDirection |= (1<<(i+STARTPIN));
			  			 pinMask &= ~(1<<(i+STARTPIN));
						break;
					case PINMODE_OUTPUT_DEFAULT_HIGH:
						 if((i+STARTPIN) == 3) pin3Mode=0;
						 if((i+STARTPIN) == 5) pin5Mode=0;
						 if((i+STARTPIN) == 6) pin6Mode=0;
			  			 pinState |= (1<<(i+STARTPIN));
			  			 pinDirection |= (1<<(i+STARTPIN));
			  			 pinMask &= ~(1<<(i+STARTPIN));
						break;
					case PINMODE_PWM_SERVO:
						if((i+STARTPIN) == 3) pin3Mode=1;
						if((i+STARTPIN) == 5) pin5Mode=1;
						if((i+STARTPIN) == 6) pin6Mode=1;
						pinMask &= ~(1<<(i+STARTPIN));
						break;
					case PINMODE_PWM_500Hz:
						if((i+STARTPIN) == 3) pin3Mode=2;
						if((i+STARTPIN) == 5) pin5Mode=2;
						if((i+STARTPIN) == 6) pin6Mode=2;
						pinMask &= ~(1<<(i+STARTPIN));
						break;
					/*case PINMODE_PWM_10kHz:
						if((i+STARTPIN) == 3) pin3Mode=3;
						if((i+STARTPIN) == 5) pin5Mode=3;
						if((i+STARTPIN) == 6) pin6Mode=3;
						pinMask &= ~(1<<(i+STARTPIN));
						break;
					case PINMODE_PWM_28kHz:
						if((i+STARTPIN) == 3) pin3Mode=4;
						if((i+STARTPIN) == 5) pin5Mode=4;
						if((i+STARTPIN) == 6) pin6Mode=4;
						pinMask &= ~(1<<(i+STARTPIN));
						break;*/
				}
				sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,3+(pin3Mode<<4));
				sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,5+(pin5Mode<<4));
				sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,6+(pin6Mode<<4));
	 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINSTATE,pinState);
	 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINDIRECTION,pinDirection);
	 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINMASK,pinMask);
				return oldValue;
			}
		}
        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipPwm3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.intFromBytes(data);
			AstericsErrorHandling.instance.reportDebugInfo(null, String.valueOf(3+(value<<8)+(pin3Mode<<4)) + "  Value 3");
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,3+(value<<8)+(pin3Mode<<4));
		}

	};
	private final IRuntimeInputPort ipPwm5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.intFromBytes(data); 
			AstericsErrorHandling.instance.reportDebugInfo(null, String.valueOf(5+(value<<8)+(pin5Mode<<4)) + "  Value 5");
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,5+(value<<8)+(pin5Mode<<4));//hier PWM
		}

	};
	private final IRuntimeInputPort ipPwm6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.intFromBytes(data); 
			AstericsErrorHandling.instance.reportDebugInfo(null, String.valueOf(6+(value<<8)+(pin6Mode<<4)) + "  Value 6");
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,6+(value<<8)+(pin6Mode<<4));//hier PWM
		}
	};


	/**
	 * Handles an input packet from Arduino CIM. Reads the values 
	 * of all ADC channels and sends the data to the corresponding output ports 
	 * @param packet the incoming packet
	 */
	private void handleArduinoAdcReport(CIMProtocolPacket packet)
	{
		// System.out.println("handleArduinoAdcPacket");
		byte [] b = packet.getData();
		int channelValue;

		for (int i=0;i<NUMBER_OF_ADCPORTS;i++)
		{
			channelValue =  ((int) b[i*2]) & 0xff;
			channelValue |= ((((int) b[i*2+1]) & 0xff) << 8);
			opAdc[i].sendData(ConversionUtils.intToBytes(channelValue));
		}
	}
	private void handleArduinoPinReport(CIMProtocolPacket packet)
	{
		int newPinValue;
		// System.out.println("handleArduinoPinPacket");
		byte [] b = packet.getData();
		newPinValue =  ((int) b[0]) & 0xff;
		newPinValue |= ((((int) b[1]) & 0xff) << 8);
		
		if (firstPinReport == false)
			for (int i=0;i<NUMBER_OF_PINS;i++)
			{
				if ( ((newPinValue & (1<<(i+STARTPIN) )) != (currentPinValue & (1<<(i+STARTPIN) ))) 
					&& ((pinMask & (1<<(i+STARTPIN))) != 0))
				{
					if ((newPinValue & (1<<(i+STARTPIN) )) != 0)
					    etpChangedLowToHigh[i].raiseEvent();
					else etpChangedHighToLow[i].raiseEvent();
				}
					
			}

		firstPinReport = false;
		currentPinValue=newPinValue;
	}
	private void handleArduinoUniqueNumber(CIMProtocolPacket packet)
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
		// System.out.println ("ArduinoCIM handlePacketReceived:");
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
				if (featureAddress == ARDUINO_CIM_FEATURE_UNIQUENUMBER)
				{
					//System.out.println ("UniqueNumber");
					handleArduinoUniqueNumber(packet);
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_ADCREPORT)
				{
					//System.out.println ("ADCReport.");
				 	handleArduinoAdcReport(packet);
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_GET_PINVALUE)
				{
					//System.out.println ("PINReport.");
					 handleArduinoPinReport(packet);
				}
				break;

			case CIMProtocolPacket.COMMAND_EVENT_REPLY:
				if (featureAddress == ARDUINO_CIM_FEATURE_ADCREPORT)
				{
					 // System.out.println ("Incoming Event: ADCReport "+(128+(int)packet.getSerialNumber()));
					 handleArduinoAdcReport(packet);
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_GET_PINVALUE)
				{
					//System.out.println ("Incoming Event: PINReport "+(128+(int)packet.getSerialNumber()));
					 handleArduinoPinReport(packet);
				}
				break;
			case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
				/*
				System.out.print ("Reply Write: ");
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINDIRECTION)
				{
					 System.out.println ("Set PinDirection.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINSTATE)
				{
					 System.out.println ("Set PinState.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_PINMASK)
				{
					 System.out.println ("Set PinMask.");
				}
				if (featureAddress == ARDUINO_CIM_FEATURE_SET_ADCPERIOD)
				{
					 System.out.println ("Set AdcPeriod.");
				}
				*/
				break;
		}
	}

	/**
	 * Called upon faulty packet reception
	 */
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
	}


	
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  if (port==null)
    	  {
  		     port = CIMPortManager.getInstance().getConnection(ARDUINO_CIM_ID);
    	  }
		  if (port != null )
		  {
			port.addEventListener(this);
			// port.sendPacket(null, ARDUINO_CIM_FEATURE_UNIQUENUMBER, CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE,false);
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINSTATE,pinState);
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINDIRECTION,pinDirection);
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINMASK,pinMask);
 			sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_ADCPERIOD,propPeriodicADCUpdate);
 			if(pin3Mode != 0) sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,3+(pin3Mode<<4));
 			if(pin5Mode != 0) sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,5+(pin5Mode<<4));
 			if(pin6Mode != 0) sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PWM,6+(pin6Mode<<4));
 			firstPinReport = true;
			CIMPortManager.getInstance().sendPacket(port, null, ARDUINO_CIM_FEATURE_GET_PINVALUE, CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
			CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
		  }
		  else
		  {
	       		AstericsErrorHandling.instance.reportError(this, "Could not find Arduino. Please verify that the Arduino Module is connected to an USB Port and that the correct firmware is installed.");
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
      
      class SetPinListener implements IRuntimeEventListenerPort
      {
      	 private int index; 
      	 SetPinListener(int index)
      	 {
      		 this.index=index;
      	 }
      	 
      	 public void receiveEvent(final String data)
      	 {
            // Logger.getAnonymousLogger().info("Set Pin event received");
  			 pinState |= (1<<(index+STARTPIN));
  			 sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINSTATE,pinState);
      	 }
      } 
      
      class ClearPinListener implements IRuntimeEventListenerPort
      {
      	 private int index; 
      	 ClearPinListener(int index)
      	 {
      		 this.index=index;
      	 }
      	 
      	 public void receiveEvent(final String data)
      	 {
             // Logger.getAnonymousLogger().info("Clear Pin event received");
  			 pinState &= ~(1<<(index+STARTPIN));
  			 sendArduinoWriteFeature(ARDUINO_CIM_FEATURE_SET_PINSTATE,pinState);
      	 }
      } 
      
      
  	synchronized private final void sendArduinoWriteFeature (short feature, int value)
  	{
		// send packet
		byte [] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) ((value >> 8) & 0xff);
		
		if (port != null)
		{
              // System.out.println("sending arduino-packet !");
			CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,false);
		}
     }
}