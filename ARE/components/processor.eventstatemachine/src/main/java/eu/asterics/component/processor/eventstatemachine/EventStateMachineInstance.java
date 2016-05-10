

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

package eu.asterics.component.processor.eventstatemachine;


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
 * EventStateMachine plugin
 * compares incoming events with a given sequence
 * fires event when sequence is complete
 * optionally resets to inital state when non-matching event comes in
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 2013-05-26
 *         Time: 
 */
public class EventStateMachineInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpFinalStateReached = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStateError = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStateChanged = new DefaultRuntimeEventTriggererPort();

	String propStateSequence = "123123";
	String propStateTiming = "0/2000,x,x,1000/3000";
	boolean propResetOnIncorrectEvent = true;
	boolean propSkipFirstEvent = true;

	int currentState=0;
	
	long startTime=0,currentTime=0;
	
	String states[];
	String timings[];

	int mins[];
	int maxs[];
    
   /**
    * The class constructor.
    */
    public EventStateMachineInstance()
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
		if ("in1".equalsIgnoreCase(eventPortID))
		{
			return elpIn1;
		}
		if ("in2".equalsIgnoreCase(eventPortID))
		{
			return elpIn2;
		}
		if ("in3".equalsIgnoreCase(eventPortID))
		{
			return elpIn3;
		}
		if ("in4".equalsIgnoreCase(eventPortID))
		{
			return elpIn4;
		}
		if ("in5".equalsIgnoreCase(eventPortID))
		{
			return elpIn5;
		}
		if ("in6".equalsIgnoreCase(eventPortID))
		{
			return elpIn6;
		}
		if ("in7".equalsIgnoreCase(eventPortID))
		{
			return elpIn7;
		}
		if ("in8".equalsIgnoreCase(eventPortID))
		{
			return elpIn8;
		}
		if ("in9".equalsIgnoreCase(eventPortID))
		{
			return elpIn9;
		}
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return elpReset;
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
		if ("finalStateReached".equalsIgnoreCase(eventPortID))
		{  
			return etpFinalStateReached;
		}
		if ("stateError".equalsIgnoreCase(eventPortID))
		{
			return etpStateError;
		}
		if ("stateChanged".equalsIgnoreCase(eventPortID))
		{
			return etpStateChanged;
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
		if ("stateSequence".equalsIgnoreCase(propertyName))
		{
			return propStateSequence;
		}
		if ("propStateTiming".equalsIgnoreCase(propertyName))
		{
			return propStateTiming;
		}
		if ("resetOnIncorrectEvent".equalsIgnoreCase(propertyName))
		{
			return propResetOnIncorrectEvent;
		}
		if ("skipFirstEvent".equalsIgnoreCase(propertyName))
		{
			return propSkipFirstEvent;
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
		if ("stateSequence".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStateSequence;
			propStateSequence = (String)newValue;
			return oldValue;
		}
		if ("stateTiming".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStateTiming;
			propStateTiming = (String)newValue;
			return oldValue;
		}
		if ("resetOnIncorrectEvent".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propResetOnIncorrectEvent;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propResetOnIncorrectEvent = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propResetOnIncorrectEvent = false;
			}
			return oldValue;
		}
		if ("skipFirstEvent".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSkipFirstEvent;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propSkipFirstEvent = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propSkipFirstEvent = false;
			}
			return oldValue;
		}

        return null;
    }

    
    void initSequence()
    {
    	  if (propSkipFirstEvent==false)  currentState=0;
    	  else currentState=1;
	  	  	  	  
	  	  states=  propStateSequence.split(",");
		  timings= propStateTiming.split(",");           // "0/2000,x,x,1000/3000";
		  
		  mins= new int[states.length];
		  maxs= new int[states.length];
		  
		  for (int i=0; i<states.length; i++)
		  {
			  states[i].trim();
			  mins[i]=-1; maxs[i]=-1;
			  if (timings.length>i)
			  {
				  String minmax[] = timings[i].split("/");
				  if (minmax.length==1)
				  {
					  try {
						  maxs[i]=Integer.parseInt(minmax[0].trim());
					  }
					  catch (NumberFormatException e) {};
				  }
				  if (minmax.length==2)
				  {
					  try {
						  mins[i]=Integer.parseInt(minmax[0].trim());
						  maxs[i]=Integer.parseInt(minmax[1].trim());
					  }
					  catch (NumberFormatException e) {};
				  }
				  
			  }
			  // System.out.println("State "+(i+1)+": Event "+states[i]+", minTime="+mins[i]+", maxTime="+maxs[i]);
		  }
    }
     /**
      * Input Ports for receiving values.
      */

	final synchronized void checkEvent(String actEvent)
	{
		boolean stateError=false;	

		if (!(states[currentState].equals(actEvent)))
		{
			stateError=true;
		}
		else
		{
			if (((propSkipFirstEvent==false) && (currentState>0)) || (currentState>0))
			{
				currentTime=System.currentTimeMillis()-startTime;
				
				if ((mins[currentState-1] != -1) && (mins[currentState-1] > currentTime)) stateError=true;
				if ((maxs[currentState-1] != -1) && (maxs[currentState-1] < currentTime)) stateError=true;
			}
		}

		startTime=System.currentTimeMillis();

		if (stateError)
		{
			// System.out.println("State Error!");

			etpStateError.raiseEvent();
			if (propResetOnIncorrectEvent == true)
			{
				currentState=0;
				if 	(states[currentState].equals(actEvent))
					stateError=false;
			}
		}
		if (stateError==false)
		{
			currentState++;
			etpStateChanged.raiseEvent();
			// System.out.println("State "+currentState+" reached !");
			if (currentState==states.length)
			{
				etpFinalStateReached.raiseEvent();
		    	if (propSkipFirstEvent==false)  currentState=0;
		    	   else currentState=1;
			}
		}
	}

	
     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpIn1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("1");
		}
	};
	final IRuntimeEventListenerPort elpIn2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("2");
		}
	};
	final IRuntimeEventListenerPort elpIn3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("3");
		}
	};
	final IRuntimeEventListenerPort elpIn4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("4");
		}
	};
	final IRuntimeEventListenerPort elpIn5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("5");
		}
	};
	final IRuntimeEventListenerPort elpIn6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("6");
		}
	};
	final IRuntimeEventListenerPort elpIn7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("7");
		}
	};
	final IRuntimeEventListenerPort elpIn8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("8");
		}
	};
	final IRuntimeEventListenerPort elpIn9 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			checkEvent("9");
		}
	};
	final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			currentState=0;
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          initSequence();
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