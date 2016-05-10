

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

package eu.asterics.component.processor.datatype;


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
public class DataTypeInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutByte = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutChar = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutInteger = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutDouble = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutString = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutBoolean = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	double propOutValue = 1.1;

	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public DataTypeInstance()
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
		if ("inByte".equalsIgnoreCase(portID))
		{
			return ipInByte;
		}
		if ("inChar".equalsIgnoreCase(portID))
		{
			return ipInChar;
		}
		if ("inInteger".equalsIgnoreCase(portID))
		{
			return ipInInteger;
		}
		if ("inDouble".equalsIgnoreCase(portID))
		{
			return ipInDouble;
		}
		if ("inString".equalsIgnoreCase(portID))
		{
			return ipInString;
		}
		if ("inBoolean".equalsIgnoreCase(portID))
		{
			return ipInBoolean;
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
		if ("outByte".equalsIgnoreCase(portID))
		{
			return opOutByte;
		}
		if ("outChar".equalsIgnoreCase(portID))
		{
			return opOutChar;
		}
		if ("outInteger".equalsIgnoreCase(portID))
		{
			return opOutInteger;
		}
		if ("outDouble".equalsIgnoreCase(portID))
		{
			return opOutDouble;
		}
		if ("outString".equalsIgnoreCase(portID))
		{
			return opOutString;
		}
		if ("outBoolean".equalsIgnoreCase(portID))
		{
			return opOutBoolean;
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
		if ("outValue".equalsIgnoreCase(propertyName))
		{
			return propOutValue;
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
		if ("outValue".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propOutValue;
			propOutValue = Double.parseDouble((String)newValue);
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInByte  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			System.out.println(this+".ipInByte: "+ConversionUtils.byteFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipInChar  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			
			System.out.println(this+".ipInChar: "+ConversionUtils.charFromBytes(data));		}
	};
	private final IRuntimeInputPort ipInInteger  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			System.out.println(this+".ipInInteger: "+ConversionUtils.intFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipInDouble  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			System.out.println(this+".ipInDouble: "+ConversionUtils.doubleFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipInString  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			System.out.println(this+".ipInString: "+ConversionUtils.stringFromBytes(data));
		}
	};
	private final IRuntimeInputPort ipInBoolean  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			System.out.println(this+".ipInBoolean: "+ConversionUtils.booleanFromBytes(data));
		}
	};


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  opOutBoolean.sendData(ConversionUtils.convertData(ConversionUtils.doubleToBytes(propOutValue), ConversionUtils.DOUBLE_TO_BOOLEAN));
    	  opOutByte.sendData(ConversionUtils.convertData(ConversionUtils.doubleToBytes(propOutValue), ConversionUtils.DOUBLE_TO_BYTE));
    	  opOutChar.sendData(ConversionUtils.convertData(ConversionUtils.doubleToBytes(propOutValue), ConversionUtils.DOUBLE_TO_CHAR));
    	  opOutDouble.sendData(ConversionUtils.doubleToBytes(propOutValue));
    	  opOutInteger.sendData(ConversionUtils.convertData(ConversionUtils.doubleToBytes(propOutValue), ConversionUtils.DOUBLE_TO_INTEGER));
    	  opOutString.sendData(ConversionUtils.convertData(ConversionUtils.doubleToBytes(propOutValue), ConversionUtils.DOUBLE_TO_STRING));
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