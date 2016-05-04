

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

package eu.asterics.component.processor.sampler;


import java.util.logging.Logger;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//import javax.swing.Timer;

import sun.misc.Timer;
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
 * Implements the sampler component.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 10, 2012
 *         Time: 11:54:11 AM
 */
public class SamplerInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();

	public double propSamplingRate = 10;
	public int propResponseTime = 0;
	public boolean propSendNullSamples = false;
	
	SamplingTimer samplingTimer = new SamplingTimer(this,opOutput);
	
	javax.swing.Timer responseTimer;
   
    
   /**
    * The class constructor.
    */
    public SamplerInstance()
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
		if ("samplingRate".equalsIgnoreCase(propertyName))
		{
			return propSamplingRate;
		}
		if ("responseTime".equalsIgnoreCase(propertyName))
		{
			return propResponseTime;
		}
		if ("sendNullSamples".equalsIgnoreCase(propertyName))
		{
			return propSendNullSamples;
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
		if ("samplingRate".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSamplingRate;
			propSamplingRate = Double.parseDouble((String)newValue);
			if(propSamplingRate==0)
			{
				propSamplingRate=10;
				AstericsErrorHandling.instance.getLogger().warning("Sampling rate can not equal 0. Sampling rate is set to 10");
			}
			
			if(propSamplingRate<0)
			{
				propSamplingRate=propSamplingRate*(-1);
				AstericsErrorHandling.instance.getLogger().warning("Sampling rate can not be negative. Sampling rate is set to " + Double.toString(propSamplingRate));
			}
			
			double sampleTime=1000.0/propSamplingRate;
			
			if(sampleTime<1)
			{
				sampleTime=100;
			}
			
			long sampleTimelong=(long)sampleTime;
			
			samplingTimer.setSamplingTime(sampleTimelong);
			
			return oldValue;
		}
		if ("responseTime".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propResponseTime;
			propResponseTime = Integer.parseInt((String)newValue);
			if(propResponseTime<0)
			{
				propResponseTime=propResponseTime*(-1);
				AstericsErrorHandling.instance.getLogger().warning("Response time can not be negative. Response time is set to " + Double.toString(propResponseTime));
			}
			
			if(propResponseTime>0)
			{
				responseTimer=new javax.swing.Timer(propResponseTime,taskPerformer);
				//responseTimer = new Timer()
			}
			return oldValue;
		}
		if ("sendNullSamples".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSendNullSamples;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propSendNullSamples = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propSendNullSamples = false;
			}
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
			double value = ConversionUtils.doubleFromBytes(data);  
			samplingTimer.setValue(value);
			samplingTimer.startSampling();
			if(propResponseTime>0)
			{
				responseTimer.restart();
			}
		}
		
	};

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  super.start();
    	  if(propSendNullSamples)
    	  {
    		  samplingTimer.setValue(0);
    		  samplingTimer.startSampling();
    	  }
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  super.pause();
    	  samplingTimer.stopSampling();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          if(propSendNullSamples)
    	  {
    		  samplingTimer.setValue(0);
    		  samplingTimer.startSampling();
    	  }
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {

    	  super.stop();
    	  samplingTimer.stopSampling();
      }
      
      
      /**
       * The Action Listener for the response time timer.
       */
      ActionListener taskPerformer = new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
        	  if(propSendNullSamples)
        	  {
        		  samplingTimer.setValue(0);
        	  }
        	  else
        	  {
        		  samplingTimer.stopSampling();
        	  }
          }
      };
}