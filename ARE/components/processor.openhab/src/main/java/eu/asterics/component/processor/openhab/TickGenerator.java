package eu.asterics.component.processor.openhab;


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



import eu.asterics.mw.data.*;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the time generation thread for the timer plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:14:00 PM
 *   Changed for openHAB by 
 *    @author Benjamin Aigner [aignerb@technikum-wien.at]
 */
public class TickGenerator implements Runnable
{
	Thread t;	
	long startTime,currentTime; 
	boolean active=false;
	int count=0;
	long timecount;
	
	private Thread runThread = null;

	final openHABInstance owner;

	/**
	 * The class constructor.
	 */
	public TickGenerator(final openHABInstance owner)
	{
		this.owner = owner;
	}

	
	/**
	 * resets the time conter value.
	 */
	public synchronized void reset()	
	{	
		count=0;
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
					owner.fetchState();
					timecount+=owner.updateRate;
				}
				Thread.sleep(owner.updateRate / 10);	
			}
		} catch (InterruptedException e) {active =false;}

		runThread=null;
	}


	/**
	 * called when model is started or resumed.
	 */
	public synchronized void start()	
	{	
		if (runThread != null) return;
		
		startTime=System.currentTimeMillis();
		timecount=owner.updateRate;
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
		count=0;
	}

}
