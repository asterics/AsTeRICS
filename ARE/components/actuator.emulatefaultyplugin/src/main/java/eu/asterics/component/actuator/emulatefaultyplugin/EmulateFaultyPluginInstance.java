

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

package eu.asterics.component.actuator.emulatefaultyplugin;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
 * The idea of the module is to emulate failure situations during the lifecycle of a module. 
 * This includes start/pause/resume/stop, receiving data input and events or receiving/returning new property values.
 *  
 * @author Martin Deinhofer [martin.deinhofer@technikum-wien.at]
 *         Date: 23.02.2015
 *         Time: 09:30
 */
public class EmulateFaultyPluginInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	boolean propInstantiationException=false;
	boolean propStartException = false;
	boolean propPauseException = false;
	boolean propResumeException = false;
	boolean propStopException = false;
	boolean propGetInputPortException = false;
	boolean propGetOutputPortException = false;
	boolean propGetRuntimePropertyValueException = false;
	boolean propSetRuntimePropertyValueException = false;
	boolean propGetEventListenerPortException = false;
	boolean propGetInputPortNullValue = false;
	boolean propGetOutputPortNullValue = false;
	boolean propGetRuntimePropertyNullValue = false;
	boolean propGetEventListenerPortNullValue = false;
	int propStartDuration = 0;
	int propPauseDuration = 0;
	int propResumeDuration = 0;
	int propStopDuration = 0;
	int propGetRuntimePropertyDuration = 0;
	int propSetRuntimePropertyDuration = 0;
	int propInADuration = 0;
	int propInBDuration = 0;
	int propInCDuration = 0;
	int propInDDuration = 0;
	int propEventADuration = 0;
	int propEventBDuration = 0;
	int propEventCDuration = 0;

	// declare member variables here
	private  GUI gui = null;
  
	//Indicates whether the thread that emulates a hanging call should be stopped.
	boolean lockAcquired=false;
	ExecutorService executor=Executors.newSingleThreadExecutor();
    
   /**
    * The class constructor.
    */
    public EmulateFaultyPluginInstance()
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
    	/*
		handleCall(0,propGetInputPortException,"Exception in getInputPort");
		if(propGetInputPortNullValue) {
			return null;
		}*/

		if ("inA".equalsIgnoreCase(portID))
		{
			return ipInA;
		}
		if ("inB".equalsIgnoreCase(portID))
		{
			return ipInB;
		}
		if ("inC".equalsIgnoreCase(portID))
		{
			return ipInC;
		}
		if ("inD".equalsIgnoreCase(portID))
		{
			return ipInD;
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
    	
    	/*
		handleCall(0,propGetOutputPortException,"Exception in getOutputPort");
		if(propGetOutputPortNullValue) {
			return null;
		}*/

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
    	/*
		handleCall(0,propGetEventListenerPortException,"Exception in getEventListenerPort");
		if(propGetEventListenerPortNullValue) {
			return null;
		}*/

		if ("eventA".equalsIgnoreCase(eventPortID))
		{
			return elpEventA;
		}
		if ("eventB".equalsIgnoreCase(eventPortID))
		{
			return elpEventB;
		}
		if ("eventC".equalsIgnoreCase(eventPortID))
		{
			return elpEventC;
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
    	/*
    	System.out.println("Getting propertyName: "+propertyName);

		handleCall(propGetRuntimePropertyDuration,propGetRuntimePropertyValueException,"Exception in getRuntimePropertyValue");
		if(propGetRuntimePropertyNullValue) {
			return null;
		}*/

    	if("instantiationException".equalsIgnoreCase(propertyName))
		{
			return propInstantiationException;
		}
		if ("startException".equalsIgnoreCase(propertyName))
		{
			return propStartException;
		}
		if ("pauseException".equalsIgnoreCase(propertyName))
		{
			return propPauseException;
		}
		if ("resumeException".equalsIgnoreCase(propertyName))
		{
			return propResumeException;
		}
		if ("stopException".equalsIgnoreCase(propertyName))
		{
			return propStopException;
		}
		if ("getInputPortException".equalsIgnoreCase(propertyName))
		{
			return propGetInputPortException;
		}
		if ("getOutputPortException".equalsIgnoreCase(propertyName))
		{
			return propGetOutputPortException;
		}
		if ("getRuntimePropertyValueException".equalsIgnoreCase(propertyName))
		{
			return propGetRuntimePropertyValueException;
		}
		if ("setRuntimePropertyValueException".equalsIgnoreCase(propertyName))
		{
			return propSetRuntimePropertyValueException;
		}
		if ("getEventListenerPortException".equalsIgnoreCase(propertyName))
		{
			return propGetEventListenerPortException;
		}
		if ("getInputPortNullValue".equalsIgnoreCase(propertyName))
		{
			return propGetInputPortNullValue;
		}
		if ("getOutputPortNullValue".equalsIgnoreCase(propertyName))
		{
			return propGetOutputPortNullValue;
		}
		if ("getRuntimePropertyNullValue".equalsIgnoreCase(propertyName))
		{
			return propGetRuntimePropertyNullValue;
		}
		if ("getEventListenerPortNullValue".equalsIgnoreCase(propertyName))
		{
			return propGetEventListenerPortNullValue;
		}
		if ("startDuration".equalsIgnoreCase(propertyName))
		{
			return propStartDuration;
		}
		if ("pauseDuration".equalsIgnoreCase(propertyName))
		{
			return propPauseDuration;
		}
		if ("resumeDuration".equalsIgnoreCase(propertyName))
		{
			return propResumeDuration;
		}
		if ("stopDuration".equalsIgnoreCase(propertyName))
		{
			return propStopDuration;
		}
		if ("getRuntimePropertyDuration".equalsIgnoreCase(propertyName))
		{
			return propGetRuntimePropertyDuration;
		}
		if ("setRuntimePropertyDuration".equalsIgnoreCase(propertyName))
		{
			return propSetRuntimePropertyDuration;
		}
		if ("inADuration".equalsIgnoreCase(propertyName))
		{
			return propInADuration;
		}
		if ("inBDuration".equalsIgnoreCase(propertyName))
		{
			return propInBDuration;
		}
		if ("inCDuration".equalsIgnoreCase(propertyName))
		{
			return propInCDuration;
		}
		if ("inDDuration".equalsIgnoreCase(propertyName))
		{
			return propInDDuration;
		}
		if ("eventADuration".equalsIgnoreCase(propertyName))
		{
			return propEventADuration;
		}
		if ("eventBDuration".equalsIgnoreCase(propertyName))
		{
			return propEventBDuration;
		}
		if ("eventCDuration".equalsIgnoreCase(propertyName))
		{
			return propEventCDuration;
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
    	/*
    	System.out.println("Setting propertyName: "+propertyName+"="+newValue);
    	
		if(!"setRuntimePropertyValueException".equalsIgnoreCase(propertyName) && !"setRuntimePropertyDuration".equalsIgnoreCase(propertyName)) {
			handleCall(propSetRuntimePropertyDuration,propSetRuntimePropertyValueException,"Exception in setRuntimePropertyValue");
		}*/
    	
		if ("instantiationException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInstantiationException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propInstantiationException = true;
				throw new RuntimeException("Exception during instantiation/setRuntimePropertyValue");
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propInstantiationException = false;
			}			
			return oldValue;
		}    	
		if ("startException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStartException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propStartException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propStartException = false;
			}
			return oldValue;
		}
		if ("pauseException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPauseException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propPauseException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propPauseException = false;
			}
			return oldValue;
		}
		if ("resumeException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propResumeException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propResumeException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propResumeException = false;
			}
			return oldValue;
		}
		if ("stopException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStopException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propStopException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propStopException = false;
			}
			return oldValue;
		}
		if ("getInputPortException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetInputPortException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetInputPortException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetInputPortException = false;
			}
			return oldValue;
		}
		if ("getOutputPortException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetOutputPortException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetOutputPortException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetOutputPortException = false;
			}
			return oldValue;
		}
		if ("getRuntimePropertyValueException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetRuntimePropertyValueException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetRuntimePropertyValueException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetRuntimePropertyValueException = false;
			}
			return oldValue;
		}
		if ("setRuntimePropertyValueException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSetRuntimePropertyValueException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propSetRuntimePropertyValueException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propSetRuntimePropertyValueException = false;
			}
			return oldValue;
		}
		if ("getEventListenerPortException".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetEventListenerPortException;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetEventListenerPortException = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetEventListenerPortException = false;
			}
			return oldValue;
		}
		if ("getInputPortNullValue".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetInputPortNullValue;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetInputPortNullValue = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetInputPortNullValue = false;
			}
			return oldValue;
		}
		if ("getOutputPortNullValue".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetOutputPortNullValue;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetOutputPortNullValue = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetOutputPortNullValue = false;
			}
			return oldValue;
		}
		if ("getRuntimePropertyNullValue".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetRuntimePropertyNullValue;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetRuntimePropertyNullValue = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetRuntimePropertyNullValue = false;
			}
			return oldValue;
		}
		if ("getEventListenerPortNullValue".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetEventListenerPortNullValue;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGetEventListenerPortNullValue = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGetEventListenerPortNullValue = false;
			}
			return oldValue;
		}
		if ("startDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStartDuration;
			propStartDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("pauseDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPauseDuration;
			propPauseDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("resumeDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propResumeDuration;
			propResumeDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("stopDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStopDuration;
			propStopDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("getRuntimePropertyDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGetRuntimePropertyDuration;
			propGetRuntimePropertyDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("setRuntimePropertyDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSetRuntimePropertyDuration;
			propSetRuntimePropertyDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("inADuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInADuration;
			propInADuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("inBDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInBDuration;
			propInBDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("inCDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInCDuration;
			propInCDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("inDDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInDDuration;
			propInDDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("eventADuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propEventADuration;
			propEventADuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("eventBDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propEventBDuration;
			propEventBDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("eventCDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propEventCDuration;
			propEventCDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		
        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInA  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data);
			
			handleCall(propInADuration, false, "", false);
		}
	};
	private final IRuntimeInputPort ipInB  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
			handleCall(propInBDuration, false, "", false);
		}
	};
	private final IRuntimeInputPort ipInC  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
			handleCall(propInCDuration, false, "", false);
		}
	};
	private final IRuntimeInputPort ipInD  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
			handleCall(propInDDuration, false, "", false);
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpEventA = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here
			handleCall(propEventADuration, false, "", false);
		}
	};
	final IRuntimeEventListenerPort elpEventB = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
			handleCall(propEventBDuration, false, "", false);
		}
	};
	final IRuntimeEventListenerPort elpEventC = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here
			handleCall(propEventCDuration, false, "", false);
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  handleCall(propStartDuration, propStartException, "Error during Plugin.start",false);
			gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
			AREServices.instance.displayPanel(gui, this, true);
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  handleCall(propPauseDuration, propPauseException, "Error during Plugin.pause",false);
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  handleCall(propResumeDuration, propResumeException, "Error during Plugin.resume",false);    	  
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  handleCall(propStopDuration, propStopException, "Error during Plugin.stop",false);
			AREServices.instance.displayPanel(gui, this, false);
          super.stop();
      }
      
      private void handleCall(int duration, boolean throwExceptino, String exceptionMessage) {
    	  handleCall(duration, throwExceptino, exceptionMessage);
      }
      
      private Object handleCall(final int duration, boolean throwException, String exceptionMessage, boolean returnNullValue) {
    	  
    	  AstericsErrorHandling.instance.getLogger().fine("handleCall: duration: "+duration+", throw Exception: "+throwException+", exceptionMessage: "+exceptionMessage+", returnNullValue: "+returnNullValue);
    	  if(throwException) {
			  AstericsErrorHandling.instance.getLogger().warning("Before entering throwing exception with message: "+exceptionMessage);
    		  throw new RuntimeException(exceptionMessage);
    	  } else if(returnNullValue) {
			  AstericsErrorHandling.instance.getLogger().warning("Before returning null value");
    		  return null;
    	  } else {
    		  if(duration == -1) {
    			  AstericsErrorHandling.instance.getLogger().warning("Before entering endless thread dead lock.");
				  final Object lock=this;
				  lockAcquired=false;
				  
				  //Emulate a very heavy/non-responsive hang
				  Runnable hanging=new Runnable() {
					  @Override
					  public void run() {
						  synchronized(lock) {
							  AstericsErrorHandling.instance.getLogger().warning("Worker thread acquired lock.");
							  lockAcquired=true;
							  while(true);
						  }
					  }
				  };
				      //executor.submit(hanging).get(duration, TimeUnit.MILLISECONDS);
				  //executor.execute(hanging);

				  while(!lockAcquired);
				  AstericsErrorHandling.instance.getLogger().warning("Caller thread trying to acquire lock.");
				  synchronized (lock) {
					  //We should never reach this
				  }    			  
    		  } else {
    			  /*
    			  try {
					File tmpFile=File.createTempFile("AREhang", "tmp");
					FileInputStream fi=new FileInputStream(tmpFile);
					AstericsErrorHandling.instance.getLogger().warning("Before reading file");
					int nrBytes=fi.read(new byte[100],0,100);
					AstericsErrorHandling.instance.getLogger().warning("After reading file: nrBytes: "+nrBytes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/



    			  try {
    				  AstericsErrorHandling.instance.getLogger().warning("Before entering sleep of "+duration+"ms.");
    				  Thread.sleep(duration);
    				  AstericsErrorHandling.instance.getLogger().warning("After exiting sleep of "+duration+"ms.");    
    			  } catch (InterruptedException e) {
    				  // TODO Auto-generated catch block
    				  e.printStackTrace();
    			  }
    		  }
    	  }
    	  return null;
      }
}