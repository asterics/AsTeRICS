
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.processor.minmax;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;

import java.util.*;

/**
 *   Implements the minmax plugin, which computes minimum and maximum 
 *   of a connected signal and provides the results at output ports
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Apr 18, 2011
 *         Time: 06:30:00 PM
 */
public class MinmaxInstance extends AbstractRuntimeComponentInstance
{
	public static final double DEFAULT_MIN = 0;
	public static final double DEFAULT_MAX = 10;
	private double propDefaultMin = DEFAULT_MIN;
	private double propDefaultMax = DEFAULT_MAX;
	private double min = DEFAULT_MIN;
	private double max = DEFAULT_MAX;

	private IRuntimeInputPort ipIn = new InputPort1();
	private IRuntimeOutputPort opMax = new OutputPort();
	private IRuntimeOutputPort opMin = new OutputPort();
	IRuntimeEventListenerPort elpReset = new RuntimeEventListenerPortReset();

	/**
	 * The class constructor.
	 */
	public MinmaxInstance()
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
			return ipIn;
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
		if("max".equalsIgnoreCase(portID))
		{
			return opMax;
		}
		else  if("min".equalsIgnoreCase(portID))
		{
			return opMin;
		}
		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * @param portID   the name of the port
	 * @return         the event listener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
	{
		if (eventPortID.equalsIgnoreCase("reset"))
		{
			return elpReset;    		
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
		if("defaultMin".equalsIgnoreCase(propertyName))
		{
			return propDefaultMin;
		}
		else if("defaultMax".equalsIgnoreCase(propertyName))
		{
			return propDefaultMax;
		}
		else
		{
			return null;
		}
	}

	/**
	 * sets a new value for the given property.
	 * @param propertyName   the name of the property
	 * @param newValue       the desired property value or null if not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue)
	{
		if("defaultMin".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDefaultMin;

			if(newValue != null)
			{
				try
				{
					propDefaultMin = Double.parseDouble((String) newValue);
				}
				catch (NumberFormatException nfe)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
				}
			}
			return oldValue;
		}
		else  if("defaultMax".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDefaultMax;

			if(newValue != null)
			{
				try
				{
					propDefaultMax = Double.parseDouble((String) newValue);
				}
				catch (NumberFormatException nfe)
				{
					AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
				}
			}
			return oldValue;
		}
		return null;
	}


	/**
	 * Event Listener Port for reset integration value.
	 */
	class RuntimeEventListenerPortReset implements IRuntimeEventListenerPort
	{
		public void receiveEvent(final String data)
		{
			min=propDefaultMin;
			max=propDefaultMax;
		}
	}


	/**
	 * Input Port for receiving values.
	 */
	private class InputPort1 extends DefaultRuntimeInputPort
	{
		public void receiveData(byte[] data)
		{
			// convert input to int
			double in = ConversionUtils.doubleFromBytes(data);
			if (in < min)
			{ 
				min= in;
				opMin.sendData(ConversionUtils.doubleToBytes(min));
			}
			if (in > max)
			{ 
				max= in;
				opMax.sendData(ConversionUtils.doubleToBytes(max));
			}
		}

		
	}

	/**
	 * Output Port for sending values.
	 */
	private class OutputPort extends DefaultRuntimeOutputPort
	{
		// empty
	}
	
	
	/**
	 * called when model is started.
	 */
	@Override
	public void start()
	{
		min=propDefaultMin;
		max=propDefaultMax;
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