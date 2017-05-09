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

package eu.asterics.component.processor.stringformatter;


import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * This plugin provides the functionality of the Java @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter class</a>. 
 *  
 * @author Martin Deinhofer [deinhofe@technikum-wien.at]
 *         Date: 20170502 
 */

public class StringFormatterInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opFormattedStr = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propFormatString = "%1$s, %2$s, %3$4.2f, %4$d";
	boolean propSendOnlyByEvent=false;
	
	// declare member variables here
	Object[] inputVariables=new Object[4];
	Object[] defaultVariableValues=new Object[4];
	

    
   /**
    * The class constructor.
    */
    public StringFormatterInstance()
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
		if ("in1String".equalsIgnoreCase(portID))
		{
			return ipIn1String;
		}
		if ("in2String".equalsIgnoreCase(portID))
		{
			return ipIn2String;
		}
		if ("in3Double".equalsIgnoreCase(portID))
		{
			return ipIn3Double;
		}
		if ("in4Integer".equalsIgnoreCase(portID))
		{
			return ipIn4Integer;
		}
		if ("setFormatStr".equalsIgnoreCase(portID))
		{
			return ipSetFormatStr;
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
		if ("formattedStr".equalsIgnoreCase(portID))
		{
			return opFormattedStr;
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
		if ("sendFormattedStr".equalsIgnoreCase(eventPortID))
		{
			return elpSendFormattedStr;
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
		if ("formatString".equalsIgnoreCase(propertyName))
		{
			return propFormatString;
		} else if("defaultIn1String".equalsIgnoreCase(propertyName)) {
		    return defaultVariableValues[0];
		} else if("defaultIn2String".equalsIgnoreCase(propertyName)) {
            return defaultVariableValues[1];
        } else if("defaultIn3Double".equalsIgnoreCase(propertyName)) {
            return defaultVariableValues[2];
        } else if("defaultIn4Integer".equalsIgnoreCase(propertyName)) {
            return defaultVariableValues[3];
        } else if("sendOnlyByEvent".equalsIgnoreCase(propertyName)) {
            return propSendOnlyByEvent;
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
		if ("formatString".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFormatString;
			propFormatString = (String)newValue;
			return oldValue;
		} else if("defaultIn1String".equalsIgnoreCase(propertyName)) {
		    if(newValue!=null) {
	            final Object oldValue = defaultVariableValues[0];
	            defaultVariableValues[0] = (String)newValue;
	            return oldValue;		        
		    }
		} else if("defaultIn2String".equalsIgnoreCase(propertyName)) {
            if(newValue!=null) {
                final Object oldValue = defaultVariableValues[1];
                defaultVariableValues[1] = (String)newValue;
                return oldValue;                
            }
        } else if("defaultIn3Double".equalsIgnoreCase(propertyName)) {
            if(newValue!=null) {
                final Object oldValue = defaultVariableValues[2];
                defaultVariableValues[2] = Double.parseDouble((String) newValue);
                return oldValue;                
            }
        } else if("defaultIn4Integer".equalsIgnoreCase(propertyName)) {
            if(newValue!=null) {
                final Object oldValue = defaultVariableValues[3];
                defaultVariableValues[3] = Integer.parseInt((String)newValue);
                return oldValue;                
            }
        } else if("sendOnlyByEvent".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendOnlyByEvent;
            propSendOnlyByEvent = Boolean.parseBoolean((String)newValue);
            return oldValue;
        }

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipIn1String  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data);
		    inputVariables[0]=ConversionUtils.stringFromBytes(data);
		    formatAndSendString(false);
		}
	};
	private final IRuntimeInputPort ipIn2String  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		    inputVariables[1]=ConversionUtils.stringFromBytes(data);
		    formatAndSendString(false);
		}
	};
	private final IRuntimeInputPort ipIn3Double  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data);
		    inputVariables[2]=ConversionUtils.doubleFromBytes(data);
		    formatAndSendString(false);
		}
	};
	private final IRuntimeInputPort ipIn4Integer  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		    inputVariables[3]=ConversionUtils.intFromBytes(data);
		    formatAndSendString(false);
		}
	};
	private final IRuntimeInputPort ipSetFormatStr  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data);
		    setRuntimePropertyValue("formatString", ConversionUtils.stringFromBytes(data));
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSendFormattedStr = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here
		    formatAndSendString(true);
		}
	};

	

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
            
      /**
       * Formats and sends the resulting foramtted string to the output port.
     * @param triggeredByEvent TODO
       */
      private void formatAndSendString(boolean triggeredByEvent) {
          if(propSendOnlyByEvent && !triggeredByEvent) {
              return;
          }
          
          //get current format string
          String curFormatString=(String)getRuntimePropertyValue("formatString");
       
          //override input variables with defaults, if the value on the input port is null.
          for(int i=0;i<inputVariables.length;i++) {
              if(inputVariables[i]==null) {                  
                  inputVariables[i]=defaultVariableValues[i];
              }
          }

        // Execute actual formatting of string
        String formattedString = String.format(curFormatString, inputVariables);
        // Convert formatted string to byte[] and send it to the output port
        opFormattedStr.sendData(ConversionUtils.stringToBytes(formattedString));
      }
}