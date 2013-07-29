

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

package com.starlab.component.processor.blinkdetector;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * This module implements the detection of the artefact a blink produces in
 * the EEG signal
 *  
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: 08/06/2012
 *         Time: 12:48:32
 */
public class BlinkDetectorInstance extends AbstractRuntimeComponentInstance
{
	public native long nativeCreateBlinkDetector(int sampleRate, int maxThreshold,
												 int minThreshold, int interval1, int interval2);
	public native boolean nativeRemoveBlinkDetector (long handle);
	public native int nativeComputeSampleBlinkDetector (long handle, double sample);
	public native double nativeGetFilteredSample(long handle);
	public native double nativeGetDerivatedSample(long handle);
	
	static {
    	System.loadLibrary("blinkdetector");
    }

	final IRuntimeOutputPort sampleOutput = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort diffOutput = new DefaultRuntimeOutputPort();
	
	final IRuntimeEventTriggererPort etpBlinkDetected = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDoubleblinkDetected = new DefaultRuntimeEventTriggererPort();

	final Object lock = new Object();

	int propSampleRate = 250;
	int propMaxThreshold = 10000;
	int propMinThreshold = -10000;
	int propBlinkLength = 300; //In miliseconds
	int propDoubleBlinkSeparation = 500; //In miliseconds
	long blinkDetectorHandle = 0;
  
    
   /**
    * The class constructor.
    */
    public BlinkDetectorInstance()
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
    	if ("Filtered Sample".equalsIgnoreCase(portID)) {
    		return sampleOutput;
		}
    	if ("Differential".equalsIgnoreCase(portID)) {
    		return diffOutput;
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
		if ("blinkDetected".equalsIgnoreCase(eventPortID))
		{
			return etpBlinkDetected;
		}
		if ("doubleblinkDetected".equalsIgnoreCase(eventPortID))
		{
			return etpDoubleblinkDetected;
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
		if ("sampleRate".equalsIgnoreCase(propertyName))
		{
			return propSampleRate;
		}
		if ("maxThreshold".equalsIgnoreCase(propertyName))
		{
			return propMaxThreshold;
		}
		if ("minThreshold".equalsIgnoreCase(propertyName))
		{
			return propMinThreshold;
		}
		if ("blinkLength".equalsIgnoreCase(propertyName))
		{
			return propBlinkLength;
		}
		if ("doubleBlinkSeparation".equalsIgnoreCase(propertyName))
		{
			return propDoubleBlinkSeparation;
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
		if ("sampleRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSampleRate;
			int oldPropSampleRate = propSampleRate;
			propSampleRate = Integer.parseInt(newValue.toString());
			if (propSampleRate != oldPropSampleRate) {
				reconfigure();
			}
			return oldValue;
		}
		if ("maxThreshold".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMaxThreshold;
			int oldPropMaxThreshold = propMaxThreshold;
			propMaxThreshold = Integer.parseInt(newValue.toString());
			if (propMaxThreshold != oldPropMaxThreshold) {
				reconfigure();
			}
			return oldValue;
		}
		if ("minThreshold".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMinThreshold;
			int oldPropMinThreshold = propMinThreshold;
			propMinThreshold = Integer.parseInt(newValue.toString());
			if (propMinThreshold != oldPropMinThreshold) {
				reconfigure();
			}
			return oldValue;
		}
		if ("blinkLength".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBlinkLength;
			int oldPropBlinkLength = propBlinkLength;
			propBlinkLength = Integer.parseInt(newValue.toString());
			if (propBlinkLength != oldPropBlinkLength) {
				reconfigure();
			}
			return oldValue;
		}
		if ("doubleBlinkSeparation".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDoubleBlinkSeparation;
			int oldPropDoubleBlinkSeparation = propDoubleBlinkSeparation;
			propDoubleBlinkSeparation = Integer.parseInt(newValue.toString());
			if (propDoubleBlinkSeparation != oldPropDoubleBlinkSeparation) {
				reconfigure();
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
			// convert input to int
            double in = ConversionUtils.doubleFromBytes(data);

            int isBlinkDetected;
            double outSample1;
            double outSample2;
            synchronized (lock) {
            	isBlinkDetected = nativeComputeSampleBlinkDetector(blinkDetectorHandle, in);
            	outSample1 = nativeGetFilteredSample(blinkDetectorHandle);
            	outSample2 = nativeGetDerivatedSample(blinkDetectorHandle);
            }
            //System.out.println(isBlinkDetected);
            if (isBlinkDetected >= 0)
            {
            	sampleOutput.sendData(ConversionUtils.doubleToBytes(outSample1));
            	diffOutput.sendData(ConversionUtils.doubleToBytes(outSample2));
            }
            
            if (isBlinkDetected == 1)
            {
            	//System.out.println("blink detected!");
            	etpBlinkDetected.raiseEvent();
            }
            else if (isBlinkDetected == 2)
            {
            	//System.out.println("double blink detected!");
            	etpDoubleblinkDetected.raiseEvent();
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
    	  if (blinkDetectorHandle == 0)
    	  {
    		  blinkDetectorHandle = nativeCreateBlinkDetector(propSampleRate, propMaxThreshold,
    				  						propMinThreshold, propBlinkLength, propDoubleBlinkSeparation);
    	  }
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
    	  if (blinkDetectorHandle != 0)
    	  {
    		  nativeRemoveBlinkDetector(blinkDetectorHandle);
    		  blinkDetectorHandle = 0;
    	  }
          super.stop();
      }
      
      private void reconfigure() {
    	  synchronized (lock) {
    		  if (blinkDetectorHandle != 0) {
    			  nativeRemoveBlinkDetector(blinkDetectorHandle);
			  }
    		  blinkDetectorHandle = nativeCreateBlinkDetector(propSampleRate, propMaxThreshold, 
    				  propMinThreshold,	propBlinkLength, propDoubleBlinkSeparation);
		  }
	  }

      
      protected void finalize() throws Throwable
      {
    	  if (blinkDetectorHandle != 0)
    	  {
    		  nativeRemoveBlinkDetector(blinkDetectorHandle);
    	  }
    	  super.finalize();
      }
}