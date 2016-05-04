

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

package eu.asterics.component.processor.oneeventmanyactions;


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
 * This component is controlled by one or two input events. It can activate up to 10 output actions.
 * 
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Apr 06, 2012
 *         Time: 14:50:37 AM 
 */
public class OneEventManyActionsInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	
	final int MAX_ACTIONS=10;
	private final String ETP_ACTION="action";
	private final String ETP_ACTION_SHOWN="actionShown";
	
	final IRuntimeEventTriggererPort[] etpActionArray = new DefaultRuntimeEventTriggererPort[MAX_ACTIONS];
	final IRuntimeEventTriggererPort[] etpActionShownArray = new DefaultRuntimeEventTriggererPort[MAX_ACTIONS];
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propActionsNumber = 3;
	int propDelay = 2000;
	int propMode = 0;
	
	int activeActions=propActionsNumber+2;
	int actionIndex=0;

  
    
   /**
    * The class constructor.
    */
    public OneEventManyActionsInstance()
    {
        for(int i=0;i<MAX_ACTIONS;i++)
        {
        	etpActionArray[i]=new DefaultRuntimeEventTriggererPort();
        	etpActionShownArray[i]=new  DefaultRuntimeEventTriggererPort();
        }
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

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
		if ("input".equalsIgnoreCase(eventPortID))
		{
			return elpInput;
		}
		if ("inputOff".equalsIgnoreCase(eventPortID))
		{
			return elpInputOff;
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
    	int etpActionSize=ETP_ACTION.length();
	    if(eventPortID.length()>etpActionSize)
	    {
	    	String testName=eventPortID.substring(0,etpActionSize);
	    	if(testName.equalsIgnoreCase(ETP_ACTION))
	    	{
	    	  String actionNumberText=eventPortID.substring(etpActionSize);
	    	  int actionNumberValue=0;
	    	  boolean finish=false;
	    	  
	    	  try
	    	  {
	    	    actionNumberValue = Integer.parseInt(actionNumberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	    			
	    	  if(finish==false)
	    	  {
	    		  if(actionNumberValue>0 && actionNumberValue<=MAX_ACTIONS)
	    		  {
	    			  return etpActionArray[actionNumberValue-1];
	    		  }
	    	  }
	    	  
	        }
	    }
	    
	    int etpActionShownSize=ETP_ACTION_SHOWN.length();
	    if(eventPortID.length()>etpActionShownSize)
	    {
	    	String testName=eventPortID.substring(0,etpActionShownSize);
	    	if(testName.equalsIgnoreCase(ETP_ACTION_SHOWN))
	    	{
	    	  String actionNumberText=eventPortID.substring(etpActionShownSize);
	    	  int actionNumberValue=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  actionNumberValue = Integer.parseInt(actionNumberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(actionNumberValue>0 && actionNumberValue<=MAX_ACTIONS)
	    		  {
	    			  return etpActionShownArray[actionNumberValue-1];
	    		  }
	    	  }
	       }
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
		if ("actionsNumber".equalsIgnoreCase(propertyName))
		{
			return propActionsNumber;
		}
		if ("delay".equalsIgnoreCase(propertyName))
		{
			return propDelay;
		}
		if ("mode".equalsIgnoreCase(propertyName))
		{
			return propMode;
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
		if ("actionsNumber".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propActionsNumber;
			propActionsNumber = Integer.parseInt(newValue.toString());
			activeActions=propActionsNumber+2;
			return oldValue;
		}
		if ("delay".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDelay;
			propDelay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("mode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMode;
			propMode = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpInput = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			action(false);
		}
	};
	final IRuntimeEventListenerPort elpInputOff = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			action(true);
		}
	};
	
	
	boolean selectMode=false;
	private Object lock1 = new Object();
	boolean finish=false;
	boolean makeAction=false;
	
	/**
	 *  Selects action according to the active mode
	 *  @param offEvent defines input event
	 */
	private void action(boolean offEvent)
	{
		if(propMode==0)
		{
			mode0Action(offEvent);
		}else if(propMode==1){
			if(!offEvent)
			{
				mode1Action();
			}
		}else if(propMode==2){
			if(!offEvent)
			{
				mode2Action();
			}
		}
	}
	
	/**
	 * Raises the action event.
	 * @param index index of the evet
	 */
	private void makeAction(int index)
	{
		etpActionArray[index].raiseEvent();
	}
	
	/**
	 * Raises the show event
	 * @param index index of the event
	 */
	private void showAction(int index)
	{
		etpActionShownArray[index].raiseEvent();
	}
	
	
	/**
	 * Thread of the mode1 and mode2.
	 */
	private final Runnable mode1Timer = new Runnable(){
		
		/**
		 * Thread method.
		 */
		@Override
		public void run() {
			do{
				
				synchronized(lock1) {
					try{
						lock1.wait(propDelay);
					}catch(InterruptedException e){
						if(!finish){
							
						}
					}
					
					if(!finish){
						if(makeAction){
							makeAction(actionIndex);
							finish=true;
						}
						else{
							actionIndex=actionIndex+1;
							if(actionIndex>=activeActions){
								actionIndex=0;
							}
					
							showAction(actionIndex);
						}
					
					}
					
				}
				
			}while(!finish);
			selectMode=false;
		}
	};
	
	private boolean startBlock=true;
	
	/**
	 * Performs actions of the mode1
	 * @param offEvent event type
	 */
	private void mode0Action(boolean offEvent)
	{
		synchronized(lock1) {
			if(selectMode){
				if(offEvent)
				{
					if(!startBlock)
					{
						makeAction=true;
						selectMode=false;
						lock1.notify( );
					}
				}
			}else{
				if(!offEvent)
				{
					startBlock=false;
					finish=false;
					makeAction=false;
					selectMode=true;
					actionIndex=0;
					showAction(actionIndex);
					AstericsThreadPool.instance.execute(mode1Timer);
				}
			}
		}
	}
	
	/**
	 * performs actions of the mode2
	 */
	private void mode1Action()
	{
		synchronized(lock1) {
			if(selectMode){
				makeAction=true;
				selectMode=false;
				lock1.notify( );
			}else{
				
				finish=false;
				makeAction=false;
				selectMode=true;
				actionIndex=0;
				showAction(actionIndex);
				AstericsThreadPool.instance.execute(mode1Timer);
				
			}
		}
	}
	
	/**
	 * Thread of the mode3.
	 */
	private final Runnable mode2Timer = new Runnable(){
		/**
		 * Thread method.
		 */
		@Override
		public void run() {
			do{
				
				synchronized(lock1) {
					try{
						lock1.wait(propDelay);
					}catch(InterruptedException e){
					
					}
					if(!finish)
					{
						if(!newAction)
						{
							makeAction(actionIndex);
							finish=true;
							newAction=false;
							selectMode=false;
						}
						else
						{
							actionIndex=actionIndex+1;
							if(actionIndex>=activeActions){
								actionIndex=0;
							}
						
							showAction(actionIndex);
							newAction=false;
						}
					}
				}
				
			}while(!finish);
		}
	};
	
	boolean newAction=false;
	
	/**
	 * performs actions of the mode3
	 */
	private void mode2Action(){
		synchronized(lock1) {
			if(!selectMode){
				selectMode=true;
				actionIndex=0;
				showAction(actionIndex);
				finish=false;
				newAction=false;
				AstericsThreadPool.instance.execute(mode2Timer);
			}
			else{
				newAction=true;
				lock1.notify( );
			}
			
			
		}
	}
	
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  startBlock=true;
    	  selectMode=false;
    	  newAction=false;
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  synchronized(lock1) {
    		  lock1.notify( );  
    	  }
    	  
    	  finish=true;
    	  super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  startBlock=true;
    	  selectMode=false;
    	  newAction=false;
    	  super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  synchronized(lock1) {
    		  lock1.notify( );  
    	  }
    	  finish=true;
          super.stop();
      }
}