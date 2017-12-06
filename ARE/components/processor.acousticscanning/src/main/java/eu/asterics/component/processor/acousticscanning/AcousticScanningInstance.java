

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

package eu.asterics.component.processor.acousticscanning;


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

import javax.sound.sampled.AudioInputStream;

import marytts.MaryInterface;
import marytts.LocalMaryInterface;
import marytts.util.data.audio.MaryAudioUtils;
import marytts.util.data.audio.AudioPlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

  
/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 */
public class AcousticScanningInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opSelectionPreview = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSelection = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpEvent1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent6 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent7 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent8 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent9 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent10 = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propLanguage = "de";
	String propVoice = "voice-cmu-slt-hsmm-5.2";
	int propDelayTime = 800;
	String propSelectionSet1 = "a,b,c,d,e,f,g,h";
	String propSelectionSet2 = "i,j,k,l,m,n,o,p";
	String propSelectionSet3 = "q,r,s,t,u,v,w,x,y,z";
	String propSelectionSet4 = "1,2,3,4,5,6,7,8,9,0";
	String propSelectionSet5 = "backspace#del,space key#space,enter key#enter,speak preview#speak,commit text#commit,clear all#clear";
	String propSelectionSet6 = "event1#event1,event2#event2,speak different text#this text is different";

	// declare member variables here

    ProcessSelection pk = null;

    MaryInterface marytts = null;
    // declare member variables here
    AcousticScanningInstance me;
    String previewText = "";
    BlockingQueue queue; 
  
    
   /**
    * The class constructor.
    */
    public AcousticScanningInstance()
    {
        me=this;
        queue = new ArrayBlockingQueue<>(1024);
        pk=new ProcessSelection(me,queue);
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("loadSetFile".equalsIgnoreCase(portID))
		{
			return ipLoadSetFile;
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
		if ("selectionPreview".equalsIgnoreCase(portID))
		{
			return opSelectionPreview;
		}
		if ("selection".equalsIgnoreCase(portID))
		{
			return opSelection;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("selectionSet1".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet1;
		}
		if ("selectionSet2".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet2;
		}
		if ("selectionSet3".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet3;
		}
		if ("selectionSet4".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet4;
		}
		if ("selectionSet5".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet5;
		}
		if ("selectionSet6".equalsIgnoreCase(eventPortID))
		{
			return elpSelectionSet6;
		}
		if ("select".equalsIgnoreCase(eventPortID))
		{
			return elpSelect;
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
		if ("event1".equalsIgnoreCase(eventPortID))
		{
			return etpEvent1;
		}
		if ("event2".equalsIgnoreCase(eventPortID))
		{
			return etpEvent2;
		}
		if ("event3".equalsIgnoreCase(eventPortID))
		{
			return etpEvent3;
		}
		if ("event4".equalsIgnoreCase(eventPortID))
		{
			return etpEvent4;
		}
		if ("event5".equalsIgnoreCase(eventPortID))
		{
			return etpEvent5;
		}
		if ("event6".equalsIgnoreCase(eventPortID))
		{
			return etpEvent6;
		}
		if ("event7".equalsIgnoreCase(eventPortID))
		{
			return etpEvent7;
		}
		if ("event8".equalsIgnoreCase(eventPortID))
		{
			return etpEvent8;
		}
		if ("event9".equalsIgnoreCase(eventPortID))
		{
			return etpEvent9;
		}
		if ("event10".equalsIgnoreCase(eventPortID))
		{
			return etpEvent10;
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
		if ("language".equalsIgnoreCase(propertyName))
		{
			return propLanguage;
		}
		if ("voice".equalsIgnoreCase(propertyName))
		{
			return propVoice;
		}
		if ("delayTime".equalsIgnoreCase(propertyName))
		{
			return propDelayTime;
		}
		if ("selectionSet1".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet1;
		}
		if ("selectionSet2".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet2;
		}
		if ("selectionSet3".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet3;
		}
		if ("selectionSet4".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet4;
		}
		if ("selectionSet5".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet5;
		}
		if ("selectionSet6".equalsIgnoreCase(propertyName))
		{
			return propSelectionSet6;
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
			propLanguage = (String)newValue;
			return oldValue;
		}
		if ("voice".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propVoice;
			propVoice = (String)newValue;
			return oldValue;
		}
		if ("delayTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDelayTime;
			propDelayTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("selectionSet1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet1;
			propSelectionSet1 = (String)newValue;
			return oldValue;
		}
		if ("selectionSet2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet2;
			propSelectionSet2 = (String)newValue;
			return oldValue;
		}
		if ("selectionSet3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet3;
			propSelectionSet3 = (String)newValue;
			return oldValue;
		}
		if ("selectionSet4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet4;
			propSelectionSet4 = (String)newValue;
			return oldValue;
		}
		if ("selectionSet5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet5;
			propSelectionSet5 = (String)newValue;
			return oldValue;
		}
		if ("selectionSet6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSelectionSet6;
			propSelectionSet6 = (String)newValue;
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipLoadSetFile  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSelectionSet1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet1);
		}
	};
	final IRuntimeEventListenerPort elpSelectionSet2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet2);
		}
	};
	final IRuntimeEventListenerPort elpSelectionSet3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet3);
		}
	};
	final IRuntimeEventListenerPort elpSelectionSet4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet4);
		}
	};
	final IRuntimeEventListenerPort elpSelectionSet5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet5);
		}
	};
	final IRuntimeEventListenerPort elpSelectionSet6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.addSelections(propSelectionSet6);

		}
	};
	final IRuntimeEventListenerPort elpSelect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            pk.select();
		}
	};

	
	
    public void addKey(String currentToken)
    {
        String command="";

        if (!(currentToken.contains("#")))
        {
            marySay(currentToken);
            previewText=previewText+currentToken;
        }
        else {
            marySay(currentToken.substring(0,currentToken.indexOf("#")));
            command = currentToken.substring(currentToken.indexOf("#"));
        
            if (command.equals("#commit")) {
                opSelection.sendData(ConversionUtils.stringToBytes(previewText));
                previewText="";
            } else if (command.equals("#space")) {
                previewText=previewText+" ";            
            } else if (command.equals("#enter")) {
                previewText=previewText+"{ENTER}";          
            } else if (command.equals("#del")) {
                 if (previewText != null && previewText.length() > 0) {
                     previewText = previewText.substring(0,previewText.length() - 1);
                    }       
            } else if (command.equals("#clear")) {
                previewText="";
            } else if (command.equals("#speak")) {
                marySay(previewText);
            } else if (command.equals("#event1")) {
                etpEvent1.raiseEvent();
            } else if (command.equals("#event2")) {
                etpEvent2.raiseEvent();
            } else if (command.equals("#event3")) {
                etpEvent3.raiseEvent();
            } else if (command.equals("#event4")) {
                etpEvent4.raiseEvent();
            } else if (command.equals("#event5")) {
                etpEvent5.raiseEvent();
            } else if (command.equals("#event6")) {
                etpEvent6.raiseEvent();
            } else if (command.equals("#event7")) {
                etpEvent7.raiseEvent();
            } else if (command.equals("#event8")) {
                etpEvent8.raiseEvent();
            } else if (command.equals("#event9")) {
                etpEvent9.raiseEvent();
            } else if (command.equals("#event10")) {
                etpEvent10.raiseEvent();
            } else   {
                marySay(command.substring(1));
                previewText=previewText+command.substring(1);
            }
        }

        opSelectionPreview.sendData(ConversionUtils.stringToBytes(previewText));
        
    }
    public void marySay(String text)
    {
        try {
            System.out.println("MaryTTS: generating audio ...");
            AudioInputStream audio = marytts.generateAudio(text);
            System.out.println("MaryTTS: audio generated - now playing ...");
            
           // MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), "tmp/maryspeech.wav", audio.getFormat());
           // MaryAudioUtils.playWavFile("tmp/maryspeech.wav", 0, true);
            
            /*
            AudioPlayer player = new AudioPlayer(audio) {
                public void run() {
                    super.run();
                    System.out.println("playing finished");
                }
            };
            player.start();
            */

            AudioPlayer player = new AudioPlayer(audio);
            player.run();
            
            System.out.println("MaryTTS: audio playback done.");
        } catch (Exception e) {e.printStackTrace(); }
    }

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          try {
              if (marytts==null) {
                  System.out.println("Creating Mary interface for Speech synthesis ...");
                  marytts = new LocalMaryInterface();
                  System.out.println("I currently have " + marytts.getAvailableVoices() + " voices");
                  
                  if (propLanguage.equals("de"))
                       marytts.setVoice("dfki-pavoque-neutral-hsmm");  // a german voice
                  else  marytts.setVoice("cmu-slt-hsmm");                 // an english  voice                 
                  System.out.println("Mary interface created!");
                  
                  System.out.println("Starting Play Thread");
                  pk.start();

              }
          } catch (Exception e) {e.printStackTrace();}          
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
          pk.stop();
          super.stop();
      }
}