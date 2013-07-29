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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

package eu.asterics.component.actuator.wavefileplayer;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

import java.util.*;
import java.util.logging.*;

import java.io.File; 
import java.io.IOException; 
import javax.sound.sampled.AudioFormat; 
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.DataLine; 
import javax.sound.sampled.FloatControl; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.SourceDataLine; 
import javax.sound.sampled.UnsupportedAudioFileException; 

/**
 * WavefilePlayerInstance implements a way to play wavefiles by sending a string
 * with the filename to the input port of the component.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Jun 22, 2011
 *         Time: 10:22:08 AM
 */
public class WavefilePlayerInstance extends AbstractRuntimeComponentInstance
{
	private String propFilename="dummy.wav";
	SourceDataLine auline = null;
	Thread thread = new SimpleThread("dummy");
	boolean onRunning=false;
	double soundVolume=0.0;
	FloatControl volume;

	public WavefilePlayerInstance()
	{
		// empty constructor - needed for OSGi service factory operations
	}

	/**
	 * Returns a specified port for a port ID
	 * @param portID the requested port ID
	 * @return the requested instance of the port, null if non existant
	 */
	public IRuntimeInputPort getInputPort(String portID)
	{
		if("wavefileName".equalsIgnoreCase(portID))
		{
			return ipWavefileNamePort;
		}
		else
		{
			return null;
		}
	}
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
	{
		if ("Start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		if ("Stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		if ("volumeDown".equalsIgnoreCase(eventPortID))
		{
			return elpVolumeDown;
		}
		if ("VolumeUp".equalsIgnoreCase(eventPortID))
		{
			return elpVolumeUp;
		}
		return null;        
	}

	/**
	 * Standard method from framework
	 * @param portID
	 * @return
	 */
	public Object getRuntimePropertyValue(String propertyName)
	{
		if("filename".equalsIgnoreCase(propertyName))
		{
			return propFilename;
		}
		return null;
	}

	/**
	 * Standard method from framework
	 * @param portID
	 * @return
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue)
	{    	
		if("filename".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFilename;
			propFilename = (String)newValue;
			return oldValue;
		}
		return null;
	}


 	/**
 	 * Starts playing the current wavefile
 	 * @author Ibanez
 	 *
 	 */ 
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort() {
		@Override
		public void receiveEvent(String data)
		{
			  
			    //System.out.println("Start Button Pressed");
			    onRunning=true;			
				thread = new SimpleThread(propFilename);
				//System.out.println("Start File: " +propFilename);
				thread.start();

		}
	};
	
 	/**
 	 * Stops playing the current wavefile
 	 * @author Ibanez
 	 *
 	 */ 
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort() {
		@Override
		public void receiveEvent(String data)
		{
			    onRunning=false;
				while (thread.isAlive())
				{	
					
				}
			
		}
	};
	
 	/**
 	 * Increments the volume attenuation up to -40dB
 	 * @author Ibanez
 	 *
 	 */ 
	final IRuntimeEventListenerPort elpVolumeDown = new IRuntimeEventListenerPort() {
		@Override
		public void receiveEvent(String data)
		{
			soundVolume = (double)(soundVolume - 10.0);
			
		    if (soundVolume < -40.0)
		    {
		    	soundVolume = -40.0;
		    }
			
			//System.out.println("Volume: "+soundVolume);
			
		}
	};
	
	
 	/**
 	 * Decrements the volume attenuation up to 0dB
 	 * @author Ibanez
 	 *
 	 */ 
	final IRuntimeEventListenerPort elpVolumeUp = new IRuntimeEventListenerPort() {
		@Override
		public void receiveEvent(String data)
		{
			soundVolume = (double)(soundVolume + 10.0);
			
		    if (soundVolume > 0.0)
		    {
		    	soundVolume = 0.0;
		    }
		    
		    //System.out.println("Volume: "+soundVolume);
			
		}
	};



	private void playWavFile(String filename)
	{	 
		
		if (onRunning==true)
		{
		    onRunning=false;
			while (thread.isAlive())
			{
				
			}
		    onRunning=true;			
			thread = new SimpleThread(filename);
			thread.start();
		}

		else
		{		
		    onRunning=true;			
			thread = new SimpleThread(filename);
			thread.start();
		}
	}

	private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb 

	private IRuntimeInputPort ipWavefileNamePort = new InputPortWavefile(this);

	/**
	 * Input port implementation that results in the playback of a wave file
	 * upon reception of strings.
	 * @author weissch
	 *
	 */
	private class InputPortWavefile extends DefaultRuntimeInputPort
	{

		WavefilePlayerInstance owner;

		/**
		 * Constructs the port and stores the parent instance
		 * @param owner
		 */
		public InputPortWavefile(WavefilePlayerInstance owner)
		{
			this.owner = owner;
		}

		/**
		 * Receives a String and loads and plays the wave file of the 
		 * corresponding name if the file exists.
		 * @param data byte array holding the String that contains the filename
		 */
		public void receiveData(byte[] data)
		{
			String filename = new String(data);
			propFilename = filename;
			playWavFile(filename);        	

		}

	}

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
			nextDir.add("data/sounds");	
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
		return res;

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
    	 onRunning = false;
         super.stop();
     }
	
     
 	/**
 	 * Thread to play the wave file and control its volume
 	 * @author Ibanez
 	 *
 	 */ 
	class SimpleThread extends Thread 
	{
	    public SimpleThread(String str) 
	    {
	    	super(str);
	    }
	    
	    public void run() 
	    {
	    	int i=0;
	    	File soundFile = new File(getName());
			if (!soundFile.exists()) { 
				//AstericsErrorHandling.instance.reportError(this, "Wave file not found: " + getName());
				return;
			} 

			AudioInputStream audioInputStream = null;
			try { 
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (UnsupportedAudioFileException e1) { 
				e1.printStackTrace();
				return;
			} catch (IOException e1) { 
				e1.printStackTrace();
				return;
			} 

			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			try { 
				auline = (SourceDataLine) AudioSystem.getLine(info);
				auline.open(format);
			} catch (LineUnavailableException e) { 
				e.printStackTrace();
				return;
			} catch (Exception e) { 
				e.printStackTrace();
				return;
			} 

			auline.start();
			int nBytesRead = 0;
			byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
			
			if (auline.isControlSupported(FloatControl.Type.MASTER_GAIN)) 
			{ 
	            volume = (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);
	        }   
			

			try { 
				while (nBytesRead != -1 && onRunning == true) 
				{ 
					volume.setValue((float)(soundVolume));
					
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
						
					if (nBytesRead >= 0) 
						auline.write(abData, 0, nBytesRead);
				} 
			} catch (IOException e) { 
				e.printStackTrace();
				return;
			} finally 
			{
				auline.drain();
				auline.close();
				
				onRunning = false;
				// System.out.println("Acaba de sonar: " +getName());

			}

	    	
	    }
	}


}