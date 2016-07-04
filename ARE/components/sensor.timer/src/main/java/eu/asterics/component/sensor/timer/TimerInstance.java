
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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.timer;

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


/**
 *   Implements the timer plugin, which uses a thread to measure time
 *   and triggers events when time period has passed. The timer can be 
 *   configured for periodic of one-shot operation.
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:14:00 PM
 */
public class TimerInstance extends AbstractRuntimeComponentInstance
{

	final private String OUT_PORT1_KEY 	= "time"; 
	final private String IN_PORT1_KEY 	= "period";
	final private String EVENT_LISTENER_START_KEY = "start";
	final private String EVENT_LISTENER_STOP_KEY = "stop";
	final private String EVENT_LISTENER_RESET_KEY = "reset";
	final private String EVENT_TRIGGER_PERIOD_FINISHED_KEY = "periodFinished";

	final private String MODE_PROPERTY_KEY 	= "mode";
	final private String PERIOD_PROPERTY_KEY = "timePeriod";
	final private String RESOLUTION_PROPERTY_KEY = "resolution";
	final private String WAIT_PROPERTY_KEY 	= "waitPeriod";
	final private String REPEAT_PROPERTY_KEY = "repeatCounter";
	final private String AUTOSTART_PROPERTY_KEY = "autostart";

	IRuntimeOutputPort opTime = new DefaultRuntimeOutputPort();    
	IRuntimeEventTriggererPort etpPeriodFinished = new DefaultRuntimeEventTriggererPort();    

	private final TimeGenerator tg = new TimeGenerator(this);

	int propMode = 0;
	int propTimePeriod = 2000;
	int propResolution= 50;
	int propWaitPeriod = 0;
	int propRepeatCounter = 0;
	boolean propAutostart=false;

	/**
	 * The class constructor.
	 */
	public TimerInstance()
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
		if(IN_PORT1_KEY.equalsIgnoreCase(portID))
		{
			return ipPeriod;
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
		if(OUT_PORT1_KEY.equalsIgnoreCase(portID))
		{
			return opTime;
		}        return null;
	}

	/**
	 * returns an Event Listener Port 
	 * @param enventPortID   the name of the event listener port
	 * @return       the event listener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
	{
		if(EVENT_LISTENER_START_KEY.equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		else if(EVENT_LISTENER_STOP_KEY.equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		else if(EVENT_LISTENER_RESET_KEY.equalsIgnoreCase(eventPortID))
		{
			return elpReset;
		}
		return null;
	}

	/**
	 * returns an Event Triggerer Port 
	 * @param enventPortID   the name of the event trigger port
	 * @return       the event trigger port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
	{
		if(EVENT_TRIGGER_PERIOD_FINISHED_KEY.equalsIgnoreCase(eventPortID))
		{
			return etpPeriodFinished;
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
		if(MODE_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propMode;
		}
		else if(PERIOD_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propTimePeriod;
		}
		else if(RESOLUTION_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propResolution;
		}
		else if(WAIT_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propWaitPeriod;
		}
		else if(REPEAT_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propRepeatCounter;
		}
		else if(AUTOSTART_PROPERTY_KEY.equalsIgnoreCase(propertyName))
		{
			return propAutostart;
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
		try
		{

			if(MODE_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propMode;
				propMode = Integer.parseInt(newValue.toString());
				return oldValue;            
			}
			else if(PERIOD_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propTimePeriod;
				int value  = Integer.parseInt(newValue.toString());
				if (value < 1)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Property value out of range for " + propertyName + ": " + newValue);
				}
				else propTimePeriod = value;
				return oldValue;
			}
			else if(RESOLUTION_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propResolution;
				int value  = Integer.parseInt(newValue.toString());
				if (value < 1)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Property value out of range for " + propertyName + ": " + newValue);
				}
				else propResolution = value;
				return oldValue;
			}
			else if(WAIT_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propWaitPeriod;
				int value  = Integer.parseInt(newValue.toString());
				if (value < 0)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Property value out of range for " + propertyName + ": " + newValue);
				}
				else propWaitPeriod = value;
				return oldValue;
			}
			else if(REPEAT_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propRepeatCounter;
				int value  = Integer.parseInt(newValue.toString());
				if (value < 0)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Property value out of range for " + propertyName + ": " + newValue);
				}
				else propRepeatCounter = value;
				return oldValue;
			}
			else if(AUTOSTART_PROPERTY_KEY.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propAutostart;
				if("true".equalsIgnoreCase((String)newValue))
				{
					propAutostart = true;
				}
				else if("false".equalsIgnoreCase((String)newValue))
				{
					propAutostart = false;
				}
				return oldValue;
			}	            
		}
		catch (NumberFormatException nfe)
		{
			AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
		}
		return null;
	}


	/**
	 * Event Listener Port for start event 
	 */   
	final IRuntimeEventListenerPort elpStart 	= new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			tg.start();
		}
	}; 

	/**
	 * Event Listener Port for stop event 
	 */   
	final IRuntimeEventListenerPort elpStop 	= new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			tg.stopAndSendData();
		}
	}; 

	/**
	 * Event Listener Port for reset event 
	 */   
	final IRuntimeEventListenerPort elpReset 	= new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			tg.reset();
		}
	}; 

	
	private final IRuntimeInputPort ipPeriod  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int value  = ConversionUtils.intFromBytes(data);
			if (value > 0)
			    propTimePeriod = value;
   		}
		
	};

	
	
	/**
	 * called when model is started.
	 */
	public void start()
	{
		super.start();
		if (propAutostart==true) tg.start();
	}

	/**
	 * called when model is stopped.
	 */
	public void pause()
	{
		super.pause();
		tg.stop();
	}

	/**
	 * called when model is resumed.
	 */
	public void resume()
	{
		super.resume();
		tg.start();
	}

	/**
	 * called when model is stopped.
	 */
	public void stop()
	{
		super.stop();
		tg.stop();
	}

}