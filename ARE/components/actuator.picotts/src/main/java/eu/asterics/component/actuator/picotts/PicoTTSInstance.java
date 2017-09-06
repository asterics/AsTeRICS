

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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.picotts;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 */
public class PicoTTSInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propLanguage = 0;

	// declare member variables here

    String textToSpeak = "";
    Process process = null;
    boolean processStarted = false;
    private boolean endThread = false;
 
    
   /**
    * The class constructor.
    */
    public PicoTTSInstance()
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
		if ("input".equalsIgnoreCase(portID))
		{
			return ipInput;
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
		if ("language".equalsIgnoreCase(propertyName))
		{
			return propLanguage;
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
		if ("language".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propLanguage;
			propLanguage = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInput  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
            textToSpeak = ConversionUtils.stringFromBytes(data);
            launchNow();
		}
	};


    private final void launchNow() {
        if (processStarted == true) {
            closeNow();
        }
        try {
                List<String> command = new ArrayList<String>();
                command.add("tools/pico/tts");
                switch (propLanguage)  {
                	case 0: command.add("-l en"); break;
                	case 1: command.add("-l us"); break;
                	case 2: command.add("-l de"); break;
                	case 3: command.add("-l fr"); break;
                	case 4: command.add("-l es"); break;
                	case 5: command.add("-l it"); break;
                	default:  command.add("-l en"); 
                }

        	    command.add(textToSpeak);

                ProcessBuilder builder = new ProcessBuilder(command);
                builder.environment();
                // builder.directory(new File(propWorkingDirectory));
                builder.directory(new File("."));
                process = builder.start();
                processStarted = true;

                endThread = false;
                AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        String s;
                        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        try {
                            while (((s = in.readLine()) != null) && (endThread == false)) {
                                System.out.println(s);
                                Thread.sleep(5);

                            }
                        } catch (InterruptedException e) {
                        } catch (IOException e) {
                        }
                    }
                });
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this, "IOException: problem starting picoTTS");
        } catch (IllegalArgumentException e) {
            AstericsErrorHandling.instance.reportError(this,
                    "IllegalArgument: problem starting picoTTS");
        }
    }

    private final void closeNow() {
        if (process != null) {
            System.out.println("closing Process");
            endThread = true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            process.destroy();
            process = null;
        }
        processStarted = false;

    }

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

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

          super.stop();
      }
}