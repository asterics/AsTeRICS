

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

package eu.asterics.component.actuator.applicationlauncher;


import java.util.*;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

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
 * ApplicationLauncherInstance can external software applications
 * via full path and filename. A default application is given as property,
 * which can be replace by an incoming application name at the input port.
 * The Launch can be performed automatically at startup, at incoming filename or
 * only via incoming event trigger.  
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 * 
 */
public class ApplicationLauncherInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propDefaultApplication = "c:\\windows\\notepad.exe";
	String propArguments = "";
	String propWorkingDirectory = ".";
	boolean propAutoLaunch = false;
	boolean propAutoClose = true;
	boolean propOnlyByEvent = false;

	// declare member variables here
	Process process = null;
    boolean processStarted=false;
  
    private boolean endThread=false;
    
   /**
    * The class constructor.
    */
    public ApplicationLauncherInstance()
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
		if ("filename".equalsIgnoreCase(portID))
		{
			return ipFilename;
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
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("launchNow".equalsIgnoreCase(eventPortID))
		{
			return elpLaunchNow;
		}
		if ("closeNow".equalsIgnoreCase(eventPortID))
		{
			return elpCloseNow;
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
		if ("defaultApplication".equalsIgnoreCase(propertyName))
		{
			return propDefaultApplication;
		}
		if ("arguments".equalsIgnoreCase(propertyName))
		{
			return propArguments;
		}
		if ("workingDirectory".equalsIgnoreCase(propertyName))
		{
			return propWorkingDirectory;
		}
		if ("autoLaunch".equalsIgnoreCase(propertyName))
		{
			return propAutoLaunch;
		}
		if ("autoClose".equalsIgnoreCase(propertyName))
		{
			return propAutoClose;
		}
		if ("onlyByEvent".equalsIgnoreCase(propertyName))
		{
			return propOnlyByEvent;
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
		if ("defaultApplication".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDefaultApplication;
			propDefaultApplication = (String)newValue;
			return oldValue;
		}
		if ("arguments".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArguments;
			propArguments = (String)newValue;
			return oldValue;
		}
		if ("workingDirectory".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propWorkingDirectory;
			propWorkingDirectory = (String)newValue;
			if (propWorkingDirectory=="") propWorkingDirectory=".";
			return oldValue;
		}
		if ("autoLaunch".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutoLaunch;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutoLaunch = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutoLaunch = false;
			}
			return oldValue;
		}
		if ("autoClose".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutoClose;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutoClose = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutoClose = false;
			}
			return oldValue;
		}
		if ("onlyByEvent".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propOnlyByEvent;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propOnlyByEvent = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propOnlyByEvent = false;
			}
			return oldValue;
		}

        return null;
    }

    
    private final void launchNow()
    {
    	if (processStarted == true) closeNow();
    	try
    	{
//    		String[] args={"cmd","/c","start","\"C:/Program Files (x86)/Vocal Joystick/vjapp/bin/win/run\"","vj"}; 
//    			process = Runtime.getRuntime().exec(propDefaultApplication);
//    		System.out.println("starting :" + args[0]);
    		
//    		File dirpath = new File("\"C:/Program Files (x86)/Vocal Joystick/vjapp/bin/win/\"");
//    		process = Runtime.getRuntime().exec(args); //,null,dirpath);

    		{
    		    List<String> command =new ArrayList<String>();
//    		    command.add("\"C:/Program Files (x86)/Vocal Joystick/vjapp/bin/win/run.bat\"");
//    		    command.add("vj");
//    		    command.add("--controlType");
//    		    command.add("key");

    		    command.add(propDefaultApplication);
    			StringTokenizer st = new StringTokenizer(propArguments);
    			while (st.hasMoreTokens()) {
    				String act=st.nextToken();
    				command.add(act);
    	    		System.out.println("adding argument :" + act);
    			}

    		    
    		    ProcessBuilder builder = new ProcessBuilder(command);
    		    
    		    Map<String, String> env = builder.environment();
    		    //env.put( "VJ_LIB_DIR", "." );
    		    
//    		    builder.directory(new File("C:/Program Files (x86)/Vocal Joystick/vjapp/bin/win/"));
    		    builder.directory(new File(propWorkingDirectory));
    		    
    		    process = builder.start();    		    
    	    	processStarted = true;

    	    	endThread=false;
    			  AstericsThreadPool.instance.execute(new Runnable() {
    				  public void run()
    				  {
    					    String s;
    					    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

    	    				try
    	    				{
    						    while(((s = in.readLine())!= null) && (endThread==false)) {
    							   System.out.println(s);
    							   Thread.sleep(5);
    							   
    						    }
    	    				}
    	    				catch (InterruptedException e) {}
    	    				catch (IOException e) {}
    		    		}
    		    	  }
    		    	  );
   		  }    		
    	}
    	catch (IOException e)
    	{
    		AstericsErrorHandling.instance.reportError(this, 
    				"IOException: problem starting "+ propDefaultApplication);
    	}
    	catch (IllegalArgumentException e)
    	{
    		AstericsErrorHandling.instance.reportError(this, 
    				"IllegalArgument: problem starting "+ propDefaultApplication);
    	}
    }
    
    private final void closeNow()
    { 
        if (process != null)
        {
    		System.out.println("closing Process");
    		endThread=true;
			try
			{
				   Thread.sleep(500);				   
			}
			catch (InterruptedException e) {}

        	process.destroy();
        	process = null;
        }
    	processStarted=false;

    	
    }
     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipFilename  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propDefaultApplication = ConversionUtils.stringFromBytes(data);
			if (propOnlyByEvent == false)
				launchNow();
		}

	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpLaunchNow = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			launchNow(); 
		}
	};
	final IRuntimeEventListenerPort elpCloseNow = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			closeNow();
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  if (propAutoLaunch==true)
    		  launchNow();
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
    	  if (propAutoClose==true)
    		  closeNow();

          super.stop();
      }
}