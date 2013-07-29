

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

package eu.asterics.component.processor.multisource;


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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 
 * This plugin passes signal from the four input ports to the one output port.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 27, 2011
 *         Time: 11:27:12 AM
 */
public class MultiSourceInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here

	private Lock lock = new ReentrantLock();
    
   /**
    * The class constructor.
    */
    public MultiSourceInstance()
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
		if ("input1".equalsIgnoreCase(portID))
		{
			return ipInput1;
		}
		if ("input2".equalsIgnoreCase(portID))
		{
			return ipInput2;
		}
		if ("input3".equalsIgnoreCase(portID))
		{
			return ipInput3;
		}
		if ("input4".equalsIgnoreCase(portID))
		{
			return ipInput4;
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
		if ("output".equalsIgnoreCase(portID))
		{
			return opOutput;
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
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInput1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			double inputData= ConversionUtils.doubleFromBytes(data); 
			try
			{
				lock.lock();
				opOutput.sendData(ConversionUtils.doubleToBytes(inputData));
			}
			finally
			{
				lock.unlock();
			}
				 
		}
	
	};
	private final IRuntimeInputPort ipInput2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
		
			double inputData= ConversionUtils.doubleFromBytes(data); 
			try
			{
				lock.lock();
				opOutput.sendData(ConversionUtils.doubleToBytes(inputData));
			}
			finally
			{
				lock.unlock();
			}
		}
		
	};
	private final IRuntimeInputPort ipInput3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			double inputData= ConversionUtils.doubleFromBytes(data); 
			try
			{
				lock.lock();
				opOutput.sendData(ConversionUtils.doubleToBytes(inputData));
			}
			finally
			{
				lock.unlock();
			}
		}
		
	};
	private final IRuntimeInputPort ipInput4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
		
			double inputData= ConversionUtils.doubleFromBytes(data); 
			try
			{
				lock.lock();
				opOutput.sendData(ConversionUtils.doubleToBytes(inputData));
			}
			finally
			{
				lock.unlock();
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