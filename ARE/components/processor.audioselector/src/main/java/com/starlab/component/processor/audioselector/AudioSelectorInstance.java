 

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

package com.starlab.component.processor.audioselector;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
 *This plug-in manages the audio tracks present in the data/music folder 
 *and different external request working as an interface with the wavefileplayer plug-in
 *  
 * @author <David Ibanez Soria> [david.ibanez@starlab.es]
 *         Date: 30/08/2012
 *         Time: 16:22
 */
public class AudioSelectorInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opTrackName = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpPlay = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPause = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpVolumeUp = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpVolumeDown = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpNextTrack = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here
    boolean onPlay = false;
    int currentTrack = 0;
    int numTracks = 0;
    List<String> trackFilenames = new ArrayList<String>(); 
	
    
   /**
    * The class constructor.
    */
    public AudioSelectorInstance()
    {
    	/*String track=" ";
    	track= trackFilenames.get(0);
    	opTrackName.sendData(ConversionUtils.stringToBytes(track));*/
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
		if ("trackName".equalsIgnoreCase(portID))
		{
			return opTrackName;
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
		if ("startStop".equalsIgnoreCase(eventPortID))
		{
			return elpStartStop;
		}
		if ("nextTrack".equalsIgnoreCase(eventPortID))
		{
			return elpNextTrack;
		}
		if ("volumeUp".equalsIgnoreCase(eventPortID))
		{
			return elpVolumeUp;
		}
		if ("volumeDown".equalsIgnoreCase(eventPortID))
		{
			return elpVolumeDown;
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
		if ("play".equalsIgnoreCase(eventPortID))
		{
			return etpPlay;
		}
		if ("pause".equalsIgnoreCase(eventPortID))
		{
			return etpPause;
		}
		if ("volumeUp".equalsIgnoreCase(eventPortID))
		{
			return etpVolumeUp;
		}
		if ("volumeDown".equalsIgnoreCase(eventPortID))
		{
			return etpVolumeDown;
		}
		if ("nextTrack".equalsIgnoreCase(eventPortID))
		{
			return etpNextTrack;
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

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStartStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{

			if (onPlay==true)
			{
				onPlay=false;	
				etpPause.raiseEvent();
			}
			else
			{
				onPlay=true;
				etpPlay.raiseEvent();
			}
			
		}
	};
	final IRuntimeEventListenerPort elpNextTrack = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			
			if (currentTrack >= (numTracks-1))
			{
				currentTrack=0;
			}
			else
			{
				currentTrack++;
			}
			
			String track=" ";
			track= trackFilenames.get(currentTrack);
			opTrackName.sendData(ConversionUtils.stringToBytes(track));
			etpNextTrack.raiseEvent();
			onPlay = true ;
		}
	};
	final IRuntimeEventListenerPort elpVolumeUp = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			etpVolumeUp.raiseEvent();
		}
	};
	final IRuntimeEventListenerPort elpVolumeDown = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			etpVolumeDown.raiseEvent();
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  //As default it is off
    	  onPlay=false;
    	  //List all the tracks from the folder
      	  trackFilenames = getRuntimePropertyList() ;
      	  numTracks = trackFilenames.size();
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
      
      
      /**
       * Lists filenames inside the track folder placed in data/music
       * @return         List of the tracknames within the music folder
       */
	  	public List<String> getRuntimePropertyList()
	  	{
	
	  		List<String> res = new ArrayList<String>(); 
	  		List<String> nextDir = new ArrayList<String>();
	  		nextDir.add("data/music/");	
	  		
	  		while(nextDir.size() > 0) 
	  		{
	  			File pathName = new File(nextDir.get(0)); 
	  			String[] fileNames = pathName.list(); 
	
	  			for(int i = 0; i < fileNames.length; i++) 
	  			{ 
	  				File f = new File(pathName.getPath(), fileNames[i]);  
	  				if (f.isDirectory()) 
	  				{  
	  					nextDir.add(f.getPath()); 
	  				} 
	  				else 
	  				{
	  					res.add(f.getPath());
	  				}
	  			} 
	  			nextDir.remove(0); 
	  		}  
	  			
	  		return res;
	
	  	} 

      
}