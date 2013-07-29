

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

package eu.asterics.component.processor.relativemovesampler;

import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 * 
 * Implements the timer for the Relative Move Sampler component.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 10, 2012
 *         Time: 11:54:11 AM
 */


public class SamplingTimer implements Runnable
{
	private Thread t;
	private boolean active=false;
	private long sampingTime=-1;
	private final RelativeMoveSamplerInstance owner;
	
	/**
	 * The class constructor.
	 * @param owner the SamplerInstance
	 * @param opOutput the component output port
	 */	
	public SamplingTimer(RelativeMoveSamplerInstance owner)
	{
		this.owner=owner;
	}
	
	
	/**
	 * Sets the sampling time.
	 * @param samplingTime the sampling time
	 */
	public void setSamplingTime(long samplingTime)
	{
		this.sampingTime=samplingTime;
	}
	
	/**
	 * Starts the sampling
	 */
	public void startSampling()
	{
		if(active==false)
		{
			active=true;
			AstericsThreadPool.instance.execute(this);
		}
	}
	
	/**
	 * Stops the sampling
	 */
	public void stopSampling()
	{
		active=false;
	}
	
	
	/**
	 * The timer function.
	 */
	public void run()
	{
		while(active)
		{
			owner.sendValues();
			
			try
			{
				Thread.sleep(sampingTime);
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
}