

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

package eu.asterics.component.processor.iirfilter;


import java.util.logging.Logger;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignFisher;
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
 * Filter implementations (iir bessel, butterworth and chebyshev filters) 
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class IIRFilterInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMagnitude = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propPasstype = 0;
	int propCharacteristictype = 0;
	int propOrder = 4;
	double propSamplingFrequency = 100;
	double propFc1 = 10;
	double propFc2 = 20;
	double propRipple = 0;

	// declare member variables here

	IirFilterCoefficients coeffs;
	IirFilter iirFilter;

    String passtypes[]={"lowpass","highpass","bandpass","bandstop"};
    String characteristictypes[]={"butterworth","bessel","chebyshev"};
    
   /**
    * The class constructor.
    */
    public IIRFilterInstance()
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
		if ("magnitude".equalsIgnoreCase(portID))
		{
			return opMagnitude;
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
		if ("passtype".equalsIgnoreCase(propertyName))
		{
			return propPasstype;
		}
		if ("characteristictype".equalsIgnoreCase(propertyName))
		{
			return propCharacteristictype;
		}
		if ("order".equalsIgnoreCase(propertyName))
		{
			return propOrder;
		}
		if ("samplingFrequency".equalsIgnoreCase(propertyName))
		{
			return propSamplingFrequency;
		}
		if ("fc1".equalsIgnoreCase(propertyName))
		{
			return propFc1;
		}
		if ("fc2".equalsIgnoreCase(propertyName))
		{
			return propFc2;
		}
		if ("ripple".equalsIgnoreCase(propertyName))
		{
			return propRipple;
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
		if ("passtype".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPasstype;
			propPasstype = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("characteristictype".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCharacteristictype;
			propCharacteristictype = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("order".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propOrder;
			propOrder = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("samplingFrequency".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSamplingFrequency;
			propSamplingFrequency = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("fc1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propFc1;
			propFc1 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("fc2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propFc2;
			propFc2 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("ripple".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propRipple;
			propRipple = Double.parseDouble((String)newValue);
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
			double result = iirFilter.step(ConversionUtils.doubleFromBytes(data));
			opOut.sendData(ConversionUtils.doubleToBytes(result));
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
    	   coeffs = IirFilterDesignFisher.design(FilterPassType.valueOf(passtypes[propPasstype]), 
    			   FilterCharacteristicsType.valueOf(characteristictypes[propCharacteristictype]), 
    			   propOrder, propRipple, propFc1/propSamplingFrequency, propFc2/propSamplingFrequency);
    	   iirFilter = new IirFilter(coeffs); 
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