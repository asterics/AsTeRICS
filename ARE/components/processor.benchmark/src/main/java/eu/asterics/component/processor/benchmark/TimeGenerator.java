
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
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the timing thread for the Benchmark plugin
 *   
 *   @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Apr 06, 2011
 *         Time: 12:48:18 AM
 *         
 *         extended by Chris Veigl Apr 23, 2011
 */
public class TimeGenerator implements Runnable
{

	boolean active=false;	
	private Thread runThread = null;
	
	final BenchmarkInstance owner;

	public TimeGenerator(final BenchmarkInstance owner)
	{
		    this.owner = owner;
	}
	public synchronized void start()	
	{	
		if (runThread != null) return;
		active=true;
		AstericsThreadPool.instance.execute(this);
	}
	public void stop()	
	{	
		if (runThread!=null)
			runThread.interrupt();
		active=false;
	}
	
	
	public void run()
	{
        runThread = Thread.currentThread();

		while(active)
		{
				try {
				  Thread.sleep(owner.propTime);
				} catch (InterruptedException e) { active=false;}
				
				if (active ==true)
				{
		    		owner.opDataCountPort.sendData(ConversionUtils.intToByteArray(owner.dataCounter));
		    		owner.opEventCountPort.sendData(ConversionUtils.intToByteArray(owner.eventCounter));
		    		owner.dataCounter=0;
		    		owner.eventCounter=0;
				}
		}
		runThread=null;
	}
}
