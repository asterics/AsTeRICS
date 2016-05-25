

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

package eu.asterics.component.processor.stringsplitter;


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
public class StringSplitterInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOut1 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut2 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut3 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut4 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut5 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut6 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut7 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut8 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut9 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut10 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut11 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut12 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut13 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut14 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut15 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOut16 = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propSeperator = ";";

	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public StringSplitterInstance()
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
		if ("input".equalsIgnoreCase(portID))
		{
			return ipInput;
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
		if ("out1".equalsIgnoreCase(portID))
		{
			return opOut1;
		}
		if ("out2".equalsIgnoreCase(portID))
		{
			return opOut2;
		}
		if ("out3".equalsIgnoreCase(portID))
		{
			return opOut3;
		}
		if ("out4".equalsIgnoreCase(portID))
		{
			return opOut4;
		}
		if ("out5".equalsIgnoreCase(portID))
		{
			return opOut5;
		}
		if ("out6".equalsIgnoreCase(portID))
		{
			return opOut6;
		}
		if ("out7".equalsIgnoreCase(portID))
		{
			return opOut7;
		}
		if ("out8".equalsIgnoreCase(portID))
		{
			return opOut8;
		}
		if ("out9".equalsIgnoreCase(portID))
		{
			return opOut9;
		}
		if ("out10".equalsIgnoreCase(portID))
		{
			return opOut10;
		}
		if ("out11".equalsIgnoreCase(portID))
		{
			return opOut11;
		}
		if ("out12".equalsIgnoreCase(portID))
		{
			return opOut12;
		}
		if ("out13".equalsIgnoreCase(portID))
		{
			return opOut13;
		}
		if ("out14".equalsIgnoreCase(portID))
		{
			return opOut14;
		}
		if ("out15".equalsIgnoreCase(portID))
		{
			return opOut15;
		}
		if ("out16".equalsIgnoreCase(portID))
		{
			return opOut16;
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
		if ("seperator".equalsIgnoreCase(propertyName))
		{
			return propSeperator;
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
		if ("seperator".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSeperator;
			propSeperator = (String)newValue;
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInput  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String line = ConversionUtils.stringFromBytes(data);
			String [] stringparts = line.split(propSeperator);
			
			for (int i=0; i<stringparts.length; i++)
			{
				switch (i)
				{
				case 0:
					opOut1.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 1:
					opOut2.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 2:
					opOut3.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 3:
					opOut4.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 4:
					opOut5.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 5:
					opOut6.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 6:
					opOut7.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 7:
					opOut8.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 8:
					opOut9.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 9:
					opOut10.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 10:
					opOut11.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 11:
					opOut12.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 12:
					opOut13.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 13:
					opOut14.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 14:
					opOut15.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				case 15:
					opOut16.sendData(ConversionUtils.stringToBytes(stringparts[i]));
					break;
				}
				
			}
				
			
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
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