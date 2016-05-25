

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

package eu.asterics.component.actuator.tonegenerator;


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
 * This plugin allows to play a stereo tone with a specified frequency.
 * The frequency of the left and right channel can be set separately
 * 
 *  
 * @author David Thaller [dt@ki-i.at]
 *         Date: 10.09.2013
 *         Time: 
 */
public class ToneGeneratorInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propSamplerate = 44100;
	boolean propSeparatechannels = true;
	int propWaveform = 0;
	private FrequencyPlayer player;
	private Thread toneGenThread;
	// declare member variables here
	private int frequency = 510;
	private double duration = 0.1;
	private boolean play = false;
  
    
   /**
    * The class constructor.
    */
    public ToneGeneratorInstance()
    {
        player = new FrequencyPlayer();
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("frequencyLeft".equalsIgnoreCase(portID))
		{
			return ipFrequencyLeft;
		}
		if ("frequencyRight".equalsIgnoreCase(portID))
		{
			return ipFrequencyRight;
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
		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("play".equalsIgnoreCase(eventPortID))
		{
			return elpPlay;
		}
		if ("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
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
			if ("samplerate".equalsIgnoreCase(propertyName))
			{
				return propSamplerate;
			}
			if ("separate channels".equalsIgnoreCase(propertyName))
			{
				return propSeparatechannels;
			}
			if ("waveform".equalsIgnoreCase(propertyName))
			{
				return propWaveform;
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
		if ("samplerate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSamplerate;
			propSamplerate = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("separate channels".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSeparatechannels;
			propSeparatechannels = Boolean.parseBoolean(newValue.toString());
			return oldValue;
		}
		if ("waveform".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propWaveform;
			propWaveform = Integer.parseInt(newValue.toString());
			return oldValue;
		}		
        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipFrequencyLeft  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 frequency = (int)ConversionUtils.doubleFromBytes(data);
				 player.setFrequency(0,frequency);
		}
	};
	
	private final IRuntimeInputPort ipFrequencyRight  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 frequency = (int)ConversionUtils.doubleFromBytes(data);
				 player.setFrequency(1,frequency);
		}
	};

	

     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpPlay = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (player != null)
				player.start();
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (player != null)
				player.stop();
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		player = new FrequencyPlayer();
		player.init();
		player.setOscillator(propWaveform, propSeparatechannels);
		
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
		player.stop();
        super.stop();
      }
}