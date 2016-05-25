
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

package eu.asterics.component.processor.benchmark;
import eu.asterics.mw.data.*;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import java.util.logging.Logger;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 *   Implements the Benchmark plugin, which measures activity on
 *   input and events ports and outputs values per time
 *  
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Apr 06, 2011
 *         Time: 12:48:18 AM
 *         
 *         extended by Chris Veigl Apr 23, 2011
 */
public class BenchmarkInstance extends AbstractRuntimeComponentInstance
{
	public final OutputPort opDataCountPort = new OutputPort();
	public final OutputPort opEventCountPort = new OutputPort();
    private IRuntimeInputPort ipInputPort = new InputPort1();

	public int propTime=1000;

	private final TimeGenerator tg = new TimeGenerator(this);
	public int eventCounter=0;
	public int dataCounter=0;
	private long lastUpdate=0;

	
    /**
     * The class constructor.
     */
    public BenchmarkInstance()
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
    	 if("in".equalsIgnoreCase(portID))
         {
             return ipInputPort;
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
    	if("dataCount".equalsIgnoreCase(portID))
        {
            return opDataCountPort;
        }
    	else if("eventCount".equalsIgnoreCase(portID))
        {
            return opEventCountPort;
        }
    	return null;
    }
    
    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the event listener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        if("eventIncrease".equalsIgnoreCase(eventPortID))
        {
            return elpIncreaseEventCounter;
        }
        else if("resetCounter".equalsIgnoreCase(eventPortID))
        {
            return elpResetCounter;
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
        if("time".equalsIgnoreCase(propertyName))
        {
            return propTime;
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
         if("time".equalsIgnoreCase(propertyName))
         {
             final Object oldValue = propTime;

             if(newValue != null)
             {
                 try
                 {
                     propTime = Integer.parseInt(newValue.toString());
                     if (propTime<1) propTime=1;
                 }
                 catch (NumberFormatException nfe)
                 {
                     throw new RuntimeException("Invalid property value for " + propertyName + ": " + newValue);
                 }
             }
             return oldValue;
         }
         return null;
    }
    

    /**
     * Input Port for receiving signal.
     */    
    private class InputPort1 extends DefaultRuntimeInputPort
    {
        public synchronized void receiveData(byte[] data)
        {
            dataCounter=dataCounter+1;
        }

    }
 
    /**
     * Output Port for sending benchmark data.
     */
    public class OutputPort extends DefaultRuntimeOutputPort
    {
        public synchronized void sendData(int data)
        {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }
    
    /**
     * Event Listener Port for increasing the event counter
     */
    final IRuntimeEventListenerPort elpIncreaseEventCounter 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public synchronized void receiveEvent(String data)
    	 {
    		eventCounter=eventCounter+1;
    	 }
    };
        
    /**
     * Event Listener Port for resetting the event counter
     */
    final IRuntimeEventListenerPort elpResetCounter 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public synchronized void receiveEvent(String data)
    	 {
    		eventCounter=0;
    		dataCounter=0;
  		    lastUpdate=System.currentTimeMillis();
    	 }
    };
   
    /**
     * called when model is started.
     */
    @Override
    public void start()
    {
        super.start();
    	lastUpdate=System.currentTimeMillis();
    	eventCounter=0;
    	dataCounter=0;
        tg.start();

    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause()
    {
        super.pause();
        tg.stop();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume()
    {
    	lastUpdate=System.currentTimeMillis();
        super.resume();
        tg.start();

    }
  
    /**
     * called when model is stopped.
     */
    @Override
    public void stop()
    {
        super.stop();
        tg.stop();
    }
}