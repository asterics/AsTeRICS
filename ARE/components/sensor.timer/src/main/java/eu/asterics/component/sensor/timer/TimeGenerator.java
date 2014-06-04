
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.timer;

import eu.asterics.mw.data.*;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the time generation thread for the timer plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:14:00 PM
 */
public class TimeGenerator implements Runnable
{
	final int MODE_ONE_SHOT=0;
	final int MODE_N_TIMES=1;
	final int MODE_LOOP=2;
	final int MODE_ONCE_STAY_ACTIVE=3;
	final int MEASURE_TIME=4;

	Thread t;	
	long startTime,currentTime; 
	boolean active=false;
	int count=0;
	long timecount;
	
	private Thread runThread = null;

	final TimerInstance owner;

	/**
	 * The class constructor.
	 */
	public TimeGenerator(final TimerInstance owner)
	{
		this.owner = owner;
	}

	
	/**
	 * resets the time conter value.
	 */
	public synchronized void reset()	
	{	
		count=0;
		if (owner.propMode != MEASURE_TIME)
		   owner.opTime.sendData(ConversionUtils.intToBytes(0));
	}

//	static int tcount=0;

	/**
	 * the time generation thread.
	 */
	public void run()
	{
        runThread = Thread.currentThread();
		//System.out.println ("\n\n *** TimeGenThread "+ (++tcount) + " started.\n");
		
		try {
		
		while(active==true)
		{
			currentTime=System.currentTimeMillis()-startTime;
			while ((currentTime>timecount) && (active==true))
			{
					owner.etpPeriodFinished.raiseEvent();

					switch (owner.propMode)
					{
						case MODE_N_TIMES:
							count++;
							if (count>=owner.propRepeatCounter)
							{ count=0; active=false; }
							break;
	
						case MODE_LOOP:
							break;
	
						case MODE_ONE_SHOT:
							active=false; 
							break;
	
						case MODE_ONCE_STAY_ACTIVE:
							owner.opTime.sendData(ConversionUtils.intToBytes(owner.propTimePeriod));
							break;
					}
					timecount+=owner.propTimePeriod;
				}
				Thread.sleep(owner.propResolution);	
				if ((timecount>owner.propWaitPeriod) &&  (owner.propMode != MEASURE_TIME)
						&& (active==true))
				owner.opTime.sendData(ConversionUtils.intToBytes((int)(currentTime-owner.propWaitPeriod)));
			}
		} catch (InterruptedException e) {active =false;}

		runThread=null;
		//	System.out.println ("\n\n *** TimeGenThread "+ (tcount) + " stopped.\n");
	}


	/**
	 * called when model is started or resumed.
	 */
	public synchronized void start()	
	{	
		if (runThread != null) return;
		
		startTime=System.currentTimeMillis();
		timecount=owner.propTimePeriod;
		active=true;
		AstericsThreadPool.instance.execute(this);
	}

	/**
	 * called when model is stopped or paused.
	 */
	public synchronized void stop()	
	{	
		if (runThread!=null)
			runThread.interrupt();

		active=false;
		if (owner.propMode == MEASURE_TIME)
		{
			owner.opTime.sendData(ConversionUtils.intToBytes((int)(currentTime-owner.propWaitPeriod)));
		}
		count=0;
	}

}
