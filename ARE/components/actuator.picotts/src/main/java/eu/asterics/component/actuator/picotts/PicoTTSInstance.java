

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
 *         The development of this plugin was partly funded by the municipal
 *         department 23 (MA23) of the City of Vienna within the Call 18
 *         project ToRaDes (grant number 18-04)
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 *         Author:  Chris Veigl <veigl@technikum-wien.at>
 *                  Benjamin Aigner <aignerb@technikum-wien.at>
 */

package eu.asterics.component.actuator.picotts;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.net.URL;
import javax.sound.sampled.*;

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
 * This module creates a speech output for a given text.
 * Input data is processed with the commandline tool "pico2wave",
 * temporarily saved in /tmp/pico.wav (because this tool is Linux only,
 * this path is set at compilation) and output via Java audio output.
 * 
 * @author Chris Veigl <veigl@technikum-wien.at>
 * @author Benjamin Aigner <aignerb@technikum-wien.at>
 *         Date: 28.09.2017
 */
public class PicoTTSInstance extends AbstractRuntimeComponentInstance
{
	int propLanguage = 0;

	//temp folder to put the speech wave to
    static final String TEMP_FOLDER = "/tmp";
    //binary to process text-to-speech
    static final String PICOBINARYNAME = "pico2wave";
    //binary to play the wave file
    static final String PLAYBACKBINARYNAME = "aplay";
    //output filename
    String outputfile = TEMP_FOLDER + "/pico.wav";

    String textToSpeak = "";
    Process p = null;
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
            //parse text to speak from input port
            textToSpeak = ConversionUtils.stringFromBytes(data);
            //launch text-to-speech
            launchNow();
		}
	};


    private final void launchNow() {
        
        if (processStarted == true) {
            closeNow();
        }
        try {
            //default language
            String language = "--lang=en-GB";
            
            //set language according to property
            switch (propLanguage)  {
                case 0: language = "--lang=en-GB"; break;
                case 1: language = "--lang=en-US"; break;
                case 2: language = "--lang=de-DE"; break;
                case 3: language = "--lang=fr-FR"; break;
                case 4: language = "--lang=es-ES"; break;
                case 5: language = "--lang=it-IT"; break;
                default:  language = "--lang=en-GB"; break;
            }

            //create a command for TTS, with given language and text
            String command[] = {PICOBINARYNAME,language,"--wave=" + TEMP_FOLDER + "/pico.wav",textToSpeak};

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.environment();
            builder.directory(new File("."));
            
            Logger.getAnonymousLogger().info("running command: " + builder.command());
            p = builder.start();
            processStarted = true;

            int returnValue = p.waitFor();
            
            //check if process finished with exit code 0 (success)
            if(returnValue == 0)
            {
                //if yes, output wave file
                String commandPlay[] = {PLAYBACKBINARYNAME,outputfile};
                
                builder = new ProcessBuilder(commandPlay);
                builder.environment();
                builder.directory(new File("."));
                
                Logger.getAnonymousLogger().info("running command-playback: " + builder.command());
                p = builder.start();
                
                returnValue = p.waitFor();
                
                if(returnValue != 0) AstericsErrorHandling.instance.reportError(this, "playback returned error " + returnValue);
            } else {
                //if no, report an error
                AstericsErrorHandling.instance.reportError(this, "picoTTS returned error " + returnValue);      
            }
            
            processStarted = false;
                
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this, "IOException: problem starting picoTTS");
        } catch (IllegalArgumentException e) {
            AstericsErrorHandling.instance.reportError(this,
                    "IllegalArgument: problem starting picoTTS");
        } catch (InterruptedException e) {
            AstericsErrorHandling.instance.reportError(this, "Execution interrupted!");
        }
    }

    private final void closeNow() {
        if (p != null) {
            System.out.println("closing Process");
            endThread = true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            p.destroy();
            p = null;
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
        //if yes, output wave file
        String commandrm[] = {"rm",outputfile};
        
        ProcessBuilder builder = new ProcessBuilder(commandrm);
        builder.environment();
        builder.directory(new File("."));
        
        Logger.getAnonymousLogger().info("deleting temp wave file: " + builder.command());
        try {
            p = builder.start();
        } catch (Exception e) {
            AstericsErrorHandling.instance.reportError(this, "Execution failed. Don't care anyway, because we are closing...");
        }
        super.stop();
      }
}
