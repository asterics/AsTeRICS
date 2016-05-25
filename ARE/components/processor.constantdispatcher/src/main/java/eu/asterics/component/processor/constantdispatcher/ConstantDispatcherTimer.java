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

package eu.asterics.component.processor.constantdispatcher;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the timer functionality for the ConstantDispatcherInstance class. 
 *   It is used in slot series Dispatching.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 15, 2011
 *         Time: 11:41:08 AM
 */

public class ConstantDispatcherTimer implements Runnable
{
  private final ConstantDispatcherInstance owner;
  private Thread t;
  private int numberOfSlotsToSend=0;
  private int delay;
  private boolean fastFinish=false;
	
  /**
   * The class constructor.
   */	
  public ConstantDispatcherTimer(final ConstantDispatcherInstance owner)
  {
    this.owner = owner;
  }
  
  /**
   * Sends slot series.
   * @param numberOfSlotsToSend   the number of slots to send
   * @param delay    the delay between slots send.
   */
  public void sendSeries(int numberOfSlotsToSend, int delay)
  {
    fastFinish=false;
	this.numberOfSlotsToSend=numberOfSlotsToSend;
	this.delay=delay;
	t=new Thread(this);
	t.start();
		
  }
  
  /**
   * Finish the sending series immediately.
   */
  public void finishNow()
  {
    fastFinish=true;
  }

  /**
   * Timer function.
   */
  public void run()
  {
    for(int i=0;i<numberOfSlotsToSend;i++)
	{
	  if(fastFinish==true)
      {
        fastFinish=false;
		break;
	  }
	  owner.sendNextSlotFromSeries();
	  try 
	  {
        t.sleep(delay);
	  } 
	  catch (InterruptedException e) {}
    }
    owner.resetBlock();
  }
}