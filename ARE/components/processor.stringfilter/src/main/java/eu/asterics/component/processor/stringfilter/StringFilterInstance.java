

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

package eu.asterics.component.processor.stringfilter;


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
public class StringFilterInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propFilterText = "dummy";
	boolean propPassOnlyIfContains = true;
	boolean propCropFilterText = true;

	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public StringFilterInstance()
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
		if ("in".equalsIgnoreCase(portID))
		{
			return ipIn;
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
		if ("out".equalsIgnoreCase(portID))
		{
			return opOut;
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
		if ("filterText".equalsIgnoreCase(propertyName))
		{
			return propFilterText;
		}
		if ("passOnlyIfContains".equalsIgnoreCase(propertyName))
		{
			return propPassOnlyIfContains;
		}
		if ("cropFilterText".equalsIgnoreCase(propertyName))
		{
			return propCropFilterText;
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
		if ("filterText".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFilterText;
			propFilterText = (String)newValue;
			return oldValue;
		}
		if ("passOnlyIfContains".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPassOnlyIfContains;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propPassOnlyIfContains = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propPassOnlyIfContains = false;
			}
			return oldValue;
		}
		if ("cropFilterText".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCropFilterText;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propCropFilterText = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propCropFilterText = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipIn  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				String in = ConversionUtils.stringFromBytes(data); 

				if (in.indexOf(propFilterText)>-1)
				{
					if (propCropFilterText)
					{
						in=in.replaceAll(propFilterText,"");
					}
					opOut.sendData(ConversionUtils.stringToBytes(in));
				}
				else
				{
					if (!propPassOnlyIfContains)
					{
						opOut.sendData(ConversionUtils.stringToBytes(in));
						
					}
				}

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