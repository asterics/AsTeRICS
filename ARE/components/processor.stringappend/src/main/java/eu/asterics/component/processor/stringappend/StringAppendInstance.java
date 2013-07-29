

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

package eu.asterics.component.processor.stringappend;


import java.util.logging.Logger;
import java.lang.StringBuffer;
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
 * This plugin allows assembly of a string from single characters or substrings. 
 * The current and final results are sent to output ports.
 * 
 * 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 11-07-2013
 */

public class StringAppendInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opActResult = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFinalResult = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	boolean propAutoSendAtEnter = false;
	String propDefaultValue = "";

	// declare member variables here

   StringBuffer stringBuffer = new StringBuffer();
    
   /**
    * The class constructor.
    */
    public StringAppendInstance()
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
		if ("inStr".equalsIgnoreCase(portID))
		{
			return ipInStr;
		}
		if ("inChar".equalsIgnoreCase(portID))
		{
			return ipInChar;
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
		if ("actResult".equalsIgnoreCase(portID))
		{
			return opActResult;
		}
		if ("finalResult".equalsIgnoreCase(portID))
		{
			return opFinalResult;
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
		if ("sendNow".equalsIgnoreCase(eventPortID))
		{
			return elpSendNow;
		}
		if ("sendNowAndClear".equalsIgnoreCase(eventPortID))
		{
			return elpSendNowAndClear;
		}
		if ("deleteCharacter".equalsIgnoreCase(eventPortID))
		{
			return elpDeleteCharacter;
		}
		if ("clear".equalsIgnoreCase(eventPortID))
		{
			return elpClear;
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
		if ("autoSendAtEnter".equalsIgnoreCase(propertyName))
		{
			return propAutoSendAtEnter;
		}
		if ("defaultValue".equalsIgnoreCase(propertyName))
		{
			return propDefaultValue;
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
		if ("autoSendAtEnter".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutoSendAtEnter;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutoSendAtEnter = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutoSendAtEnter = false;
			}
			return oldValue;
		}
	    if("defaultValue".equalsIgnoreCase(propertyName))
	    {
	      final Object oldValue = propDefaultValue;
	      propDefaultValue=(String)newValue;
	      return oldValue;
	    }

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInStr  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			stringBuffer.append(new String(data));
			opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString())); 

		}
	};
	private final IRuntimeInputPort ipInChar  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.intFromBytes(data);
			
			if ((propAutoSendAtEnter == true) && (value == 13)) // check for enter 
			{
				opFinalResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
				stringBuffer=new StringBuffer();
				stringBuffer.append(propDefaultValue);
				opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString())); 
			}
			else
			{
				stringBuffer.append((char)value);
				opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
			}
 
		
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSendNow = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				opFinalResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
		}
	};
	final IRuntimeEventListenerPort elpSendNowAndClear = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				opFinalResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
				stringBuffer=new StringBuffer();
				stringBuffer.append(propDefaultValue);
				opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString())); 
		}
	};
	final IRuntimeEventListenerPort elpDeleteCharacter = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{	
			if (stringBuffer.length()>0)
			{
				stringBuffer.deleteCharAt(stringBuffer.length()-1);
				opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
			}
		}
	};
	
	final IRuntimeEventListenerPort elpClear = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			stringBuffer=new StringBuffer();
			stringBuffer.append(propDefaultValue);
			opActResult.sendData (ConversionUtils.stringToBytes(stringBuffer.toString()));
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  stringBuffer=new StringBuffer();
    	  stringBuffer.append(propDefaultValue);
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
      }
}