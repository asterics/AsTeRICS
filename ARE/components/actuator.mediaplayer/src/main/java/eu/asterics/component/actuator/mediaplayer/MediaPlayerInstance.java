

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
 *         License: GPL v3.0
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.mediaplayer;


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
 * Mediaplayer component - interfaces with the VLCJ library for playback of multimedia files
 * 
 * 
 *  
 * @author Chris Veigl [ veigl@technikum-wien.at}
 *         Date: 30.7.2013
 */
public class MediaPlayerInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opActPos = new DefaultRuntimeOutputPort();

	private String propFilename = "movie.avi";
	private double propPosition = 0;
	private double propRate = 100;
	boolean propAutoplay = false;
	boolean propDisplayGui = true;
	String propPathToVLC = "C:\\Program Files (x86)\\VideoLAN\\VLC";

	// declare member variables here
	private  GUI gui = null;
 
	public boolean playerActive =false;
    
   /**
    * The class constructor.
    */
    public MediaPlayerInstance()
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
		if ("position".equalsIgnoreCase(portID))
		{
			return ipPosition;
		}
		if ("rate".equalsIgnoreCase(portID))
		{
			return ipRate;
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
		if ("actPos".equalsIgnoreCase(portID))
		{
			return opActPos;
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
		if ("play".equalsIgnoreCase(eventPortID))
		{
			return elpPlay;
		}
		if ("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		if ("pause".equalsIgnoreCase(eventPortID))
		{
			return elpPause;
		}
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return elpReset;
		}
		if ("next".equalsIgnoreCase(eventPortID))
		{
			return elpNext;
		}
		if ("previous".equalsIgnoreCase(eventPortID))
		{
			return elpPrevious;
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
		if ("filename".equalsIgnoreCase(propertyName))
		{
			return propFilename;
		}
		if ("autoplay".equalsIgnoreCase(propertyName))
		{
			return propAutoplay;
		}
		if ("displayGui".equalsIgnoreCase(propertyName))
		{
			return propDisplayGui;
		}
		if ("pathToVLC".equalsIgnoreCase(propertyName))
		{
			return propPathToVLC;
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
		if ("filename".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFilename;
			propFilename = (String)newValue;
			return oldValue;
		}
		if ("pathToVLC".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPathToVLC;
			propPathToVLC = (String)newValue;
			return oldValue;
		}
		if ("autoplay".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutoplay;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutoplay = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutoplay = false;
			}
			return oldValue;
		}
		if ("displayGui".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDisplayGui;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propDisplayGui = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propDisplayGui = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipFilename  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String filename = new String(data);
			propFilename = filename;
            if (propAutoplay==true)
            {
    	    	System.out.println("play via inputport");
	        	 if (playerActive) gui.play(propFilename);   
            }
		}
	};

	private final IRuntimeInputPort ipPosition  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propPosition=  ConversionUtils.doubleFromBytes(data);
			if (playerActive) gui.setPosition(propPosition);
		}
	};

	private final IRuntimeInputPort ipRate  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propRate=  ConversionUtils.doubleFromBytes(data);
			if (playerActive) gui.setRate(propRate);
		}
	};
	

     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpPlay = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
	    	System.out.println("play via eventlistener");
	    	if (playerActive) gui.play(propFilename);   		
		}
	};
	final IRuntimeEventListenerPort elpPause = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (playerActive) gui.pause();   		
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (playerActive) gui.stop();   		
		}
	};
	final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (playerActive) gui.setPosition(0);    
		}
	};
	final IRuntimeEventListenerPort elpNext = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (playerActive) gui.playNext();    
		}
	};
	final IRuntimeEventListenerPort elpPrevious = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (playerActive) gui.playPrevious();    
		}
	};



	/**
	 * Returns all the filenames inside the path folder data/music
	 * and data/sounds
	 */
	public List<String> getRuntimePropertyList(String key) 
	{

		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("filename")==0)
		{
			List<String> nextDir = new ArrayList<String>(); //Directories
			nextDir.add("data/music");	
			//nextDir.add("data/sounds");	
			nextDir.add("data/videos");	
			
			try 
			{
				while(nextDir.size() > 0) 
				{
					File pathName = new File(nextDir.get(0)); 
					String[] fileNames = pathName.list();  // lists all files in the directory
	
					for(int i = 0; i < fileNames.length; i++) 
					{ 
						File f = new File(pathName.getPath(), fileNames[i]); // getPath converts abstract path to path in String, 
						// constructor creates new File object with fileName name   
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
			}
			catch (Exception e) {System.out.println ("could not find directories for media files !");}
		}
		return res;

	} 
		

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		  gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
		  if (propDisplayGui) AREServices.instance.displayPanel(gui, this, true);
		  else AREServices.instance.displayPanel(gui, this, false);
          super.start();
          if (propAutoplay==true)
          {
     		  System.out.println("play via Start method");
     		  if (playerActive) gui.play(propFilename);   
          }
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
          if (playerActive) gui.pause();   	

      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
 		  System.out.println("play via resume method");
 		  if (playerActive) gui.play(propFilename);   	
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          super.stop();
 		  System.out.println("Stop method");

		  AREServices.instance.displayPanel(gui, this, false);
    	  gui.stop();
    	  gui.disposePlayer();
  		  System.out.println("Stop method done");
 
      }
}