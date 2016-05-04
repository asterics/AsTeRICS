
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

package eu.asterics.component.actuator.ssvepstimulator;
import java.util.*;
import java.io.*;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the SSVEPSTimulator plugin, which creates a direct 2d blitting 
 *   surface for SW-based flickering 
 *   
 * @author Christoph Veigl [christoph.veigl@technikum-wien.at]
 *         Date: Sept 16, 2012
 *         Time: 04:45:08 PM
 */
public class SSVEPStimulatorInstance extends AbstractRuntimeComponentInstance
{  
    private String propOnBitmapFile = "";
    private String propOffBitmapFile = "";
    private int propXPosition = 100;
    private int propYPosition = 100;
    private int propMsec = 3000;
    private int propFrequency = 10;
    
    private int toggleTime = 50;
  
	final IRuntimeEventTriggererPort etpStimPeriodFinished = new DefaultRuntimeEventTriggererPort();

    
	// declare member variables here
	Process process = null;
    boolean processStarted=false;  
    private boolean endThread=false;

	public void startBlitting()
    {
    	if (processStarted == false) 
    	try
    	{
    		
    		toggleTime= (int)((500/propFrequency)+0.5);
    		
    	    String onBitmap  = "data\\actuator.ssvepstimulator\\"+propOnBitmapFile+".bmp";
    		
		    List<String> command =new ArrayList<String>();
		    command.add("tools\\blit.exe");
			command.add(Integer.toString(propXPosition));
			command.add(Integer.toString(propYPosition));
			command.add(Integer.toString(toggleTime));
 			command.add(onBitmap);
    	    if (propOffBitmapFile!="")
    	    {
    	    	String offBitmap = "data\\actuator.ssvepstimulator\\"+propOffBitmapFile+".bmp";
    	    	command.add(offBitmap);
    	    }
 

		    ProcessBuilder builder = new ProcessBuilder(command);
		    
		    Map<String, String> env = builder.environment();
		    builder.directory(new File("."));   		    
		    process = builder.start();    		    
	    	processStarted = true;
	    	endThread=false;
			  AstericsThreadPool.instance.execute(new Runnable() {
				  public void run()
				  {
	    				try
	    				{
							   Thread.sleep(propMsec);
							   stopBlitting();
	    				}
	    				catch (InterruptedException e) {}
		    		}
		    	  }
		    	  );
    	}
    	catch (IOException e)
    	{
    		AstericsErrorHandling.instance.reportError(this, 
    				"IOException: problem starting Blitting application !");
    	}
    	catch (IllegalArgumentException e)
    	{
    		AstericsErrorHandling.instance.reportError(this, 
    				"IllegalArgument: problem starting tarting Blitting application !");
    	}
    }
    
	public void stopBlitting()
    { 
        if (process != null)
        {
        	/*
    		System.out.println("closing Process");
    		endThread=true;
			try
			{
				   Thread.sleep(500);				   
			}
			catch (InterruptedException e) {}
        	 */
        	process.destroy();
        	process = null;
        	etpStimPeriodFinished.raiseEvent();
        }
    	processStarted=false;
    	
    }
	
	
    /**
     * The class constructor
     */	
   public SSVEPStimulatorInstance()
    {

    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
   public IRuntimeInputPort getInputPort(String portID)
    {
		if ("frequency".equalsIgnoreCase(portID))
		{
			return ipFrequency;
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
        return null;
    }
    
   /**
    * returns an Event Listener Port.
    * @param portID   the name of the port
    * @return         the event listener port or null if not found
    */
   public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        if("startStim".equalsIgnoreCase(eventPortID))
        {
            return elpStartStim;
        }
        else if("stopStim".equalsIgnoreCase(eventPortID))
        {
            return elpStopStim;
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

		if ("stimPeriodFinished".equalsIgnoreCase(eventPortID))
		{
			return etpStimPeriodFinished;
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
        if("onBitmapFile".equalsIgnoreCase(propertyName))
        {
            return propOnBitmapFile;
        }
        if("offBitmapFile".equalsIgnoreCase(propertyName))
        {
            return propOffBitmapFile;
        }
        if("xPosition".equalsIgnoreCase(propertyName))
        { 
            return propXPosition;
        }
        if("yPosition".equalsIgnoreCase(propertyName))
        { 
            return propYPosition;
        }
        if("msec".equalsIgnoreCase(propertyName))
        { 
            return propMsec;
        }
        if("frequency".equalsIgnoreCase(propertyName))
        { 
            return propFrequency;
        }
        return null;
    }

   /**
    * sets a new value for the given property.
    * @param propertyName   the name of the property
    * @param newValue       the desired property value
    */
   public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if("onBitmapFile".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propOnBitmapFile;
            propOnBitmapFile=(String)newValue;
            return oldValue;
        }
        else if("offBitmapFile".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propOffBitmapFile;
            propOffBitmapFile=(String)newValue;
            return oldValue;
        }
		else if("xPosition".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = propXPosition;
			propXPosition = Integer.parseInt((String) newValue);
			return oldValue;
		}
		else if("yPosition".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = propYPosition;
			propYPosition = Integer.parseInt((String) newValue);
			return oldValue;
		}
		else if("msec".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = propMsec;
			propMsec = Integer.parseInt((String) newValue);
			return oldValue;
		}
		else if("frequency".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = propFrequency;
			propFrequency = Integer.parseInt((String) newValue);
			return oldValue;
		}

        return null;
    }
 
   
   /**
    * Input Ports for receiving values.
    */
	private final IRuntimeInputPort ipFrequency  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			
			propFrequency = ConversionUtils.intFromBytes(data);
		}
	};

 
   /**
    * Event Listener Port for start.
    */
   final IRuntimeEventListenerPort elpStartStim 	= new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
   	          startBlitting();
    	 }
    };    
    
    
    /**
     * Event Listener Port for stop.
     */
    final IRuntimeEventListenerPort elpStopStim 	= new IRuntimeEventListenerPort()
    {
   	 public void receiveEvent(final String data)
   	 {
   		 stopBlitting();
  	 }
    };  

   
      
   /** 
    * called when model is started.
    */
  @Override    
   public void start()
   {
       super.start();
       startBlitting();
   }
  
   /**
    * called when model is paused.
    */    
   @Override
   public void pause()  
   {
      super.pause(); 
      stopBlitting(); 
   }   
    
  /**
   * called when model is resumed.
   */
   @Override 
   public void resume() 
   {
      super.resume(); 
      startBlitting();
   }
  
  /**
   * called when model is stopped.
   */
  @Override
    public void stop()
    {
        super.stop();
        stopBlitting();
    }
}