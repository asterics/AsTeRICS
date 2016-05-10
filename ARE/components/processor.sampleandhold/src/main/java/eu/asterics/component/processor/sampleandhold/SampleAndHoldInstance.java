
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.sampleandhold;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import java.util.*;

/**
 *  SampleAndHoldInstance performs an event-triggered sample-and-hold function
 *  on up to 4 channels
 * 
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Apr 18, 2011
 *         Time: 08:15:00 PM
 */
public class SampleAndHoldInstance extends AbstractRuntimeComponentInstance
{
	
    private double value1=0;
    private double value2=0;
    private double value3=0;
    private double value4=0;

    private IRuntimeInputPort ipIn1 = new InputPort1();
    private IRuntimeInputPort ipIn2 = new InputPort2();
    private IRuntimeInputPort ipIn3 = new InputPort3();
    private IRuntimeInputPort ipIn4 = new InputPort4();

    private IRuntimeOutputPort opOut1 = new OutputPort();
    private IRuntimeOutputPort opOut2 = new OutputPort();
    private IRuntimeOutputPort opOut3 = new OutputPort();
    private IRuntimeOutputPort opOut4 = new OutputPort();

	IRuntimeEventListenerPort elpSampleNow = new RuntimeEventListenerPortSampleNow();

    
    /**
     * The class constructor.
     */
	public SampleAndHoldInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }


   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("in1".equalsIgnoreCase(portID))
        {
            return ipIn1;
        }
        else if("in2".equalsIgnoreCase(portID))
        {
            return ipIn2;
        }
        else if("in3".equalsIgnoreCase(portID))
        {
            return ipIn3;
        }
        else if("in4".equalsIgnoreCase(portID))
        {
            return ipIn4;
        }
        else
        {
            return null;
        }
    }

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        if("out1".equalsIgnoreCase(portID))
        {
            return opOut1;
        }
        else if("out2".equalsIgnoreCase(portID))
        {
            return opOut2;
        }
        else if("out3".equalsIgnoreCase(portID))
        {
            return opOut3;
        }
        else if("out4".equalsIgnoreCase(portID))
        {
            return opOut4;
        }
        else
        {
            return null;
        }
    }

    /**
     * returns an Event Listener Port.
     * @param portID   the name of the port
     * @return         the event listener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
    	if (eventPortID.equalsIgnoreCase("sampleNow"))
    	{
    		return elpSampleNow;    		
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
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        return null;
    }
 
    
    /**
     * Event Listener Port for sampling the values.
     */
	class RuntimeEventListenerPortSampleNow implements IRuntimeEventListenerPort
	{
		public void receiveEvent(final String data)
	   	{
		    opOut1.sendData(ConversionUtils.doubleToBytes(value1));
		    opOut2.sendData(ConversionUtils.doubleToBytes(value2));
		    opOut3.sendData(ConversionUtils.doubleToBytes(value3));
		    opOut4.sendData(ConversionUtils.doubleToBytes(value4));
	   	}
	}
   
   /**
    * Input Ports for receiving values.
    */
    private class InputPort1 extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            value1 = ConversionUtils.doubleFromBytes(data);
        }

		
    }
    private class InputPort2 extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            value2 = ConversionUtils.doubleFromBytes(data);
        }

		
    }
    private class InputPort3 extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            value3 = ConversionUtils.doubleFromBytes(data);
        }

		
    }
    private class InputPort4 extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            value4 = ConversionUtils.doubleFromBytes(data);
        }
    }

    /**
     * Output Ports for sending result.
     */
    private class OutputPort extends DefaultRuntimeOutputPort
    {   }  // empty
 
    /**
     * called when model is started.
     */
     public void start()
      {
          super.start();
      }

     /**
      * called when model is paused.
      */
      public void pause()
      {
          super.pause();
      }

    /**
     * called when model is resumed.
     */
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      public void stop()
      {
          super.stop();
      }

}