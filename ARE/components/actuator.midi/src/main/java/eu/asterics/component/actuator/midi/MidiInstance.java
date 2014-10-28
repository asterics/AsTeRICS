

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.midi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.MidiManager;

import javax.sound.midi.MidiDevice.Info;



/**
 * MidiInstance.java
 * Purpose of this module:
 * Implements the MIDI actuator plugin for the purpose of playing music via MIDI.
 * 
 * @Author Dominik Koller [dominik.koller@gmx.at]
 */
public class MidiInstance extends AbstractRuntimeComponentInstance
{
	private  GUI gui = null;
	
	Scales scale = new Scales();   	//  Class for musical scales.
	
	public  int selectedNote;			//Selected Note
    private int oldNote=-1;				//saves the old Note
    private int toneTriggerMode=0;		//if not zero, next note will definitely play
    
    public  int amountOfNotes = 8;
    private int velocity = 127;
    private int volume = 127;
    public  int range = 0;

    public int propPitchMin=0;
    public int propPitchMax=1000;
    public int propTriggerThreshold=50;
    public int propTriggerMax = 100;
    public int propChannel=1;
    String propToneScale="alltones.sc";
    String propMidiDevice="Gervill";
    String propInstrument="Vibraphone";
    private boolean propDisplayGUI=true;
    boolean propPlayOnlyChangingNotes=true;

    public int pitchInput=500;
    private int triggerInput=75;

    
   /**
    * The class constructor.
    */
    public MidiInstance() 
    {
    	scale.loadScale(propToneScale);
    	MidiManager.instance.openMidiDevice(propMidiDevice);
    }

    public int getAmountOfNotes()
    {
    	return (amountOfNotes);
    }
    /**
     * returns two Input Ports.
     * @param portID   the name of the port
     * @return         the input ports or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("trigger".equalsIgnoreCase(portID))
		{
			return ipTrigger;
		}
		if ("pitch".equalsIgnoreCase(portID))
		{
			return ipPitch;
		}
		if ("instrument".equalsIgnoreCase(portID))
		{
			return ipInstrument;
		}
		if ("scale".equalsIgnoreCase(portID))
		{
			return ipScale;
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
    	if("midiDevice".equalsIgnoreCase(propertyName))
        {
            return propMidiDevice;
        }
    	if("channel".equalsIgnoreCase(propertyName))
        {
            return propChannel;
        }
    	if("instrument".equalsIgnoreCase(propertyName))
        {
            return propInstrument;
        }
    	if("triggerThreshold".equalsIgnoreCase(propertyName))
        {
            return propTriggerThreshold;
        }
    	if("triggerMax".equalsIgnoreCase(propertyName))
        {
            return propTriggerMax;
        }
    	if("pitchMin".equalsIgnoreCase(propertyName))
        {
            return propPitchMin;
        }
    	if("pitchMax".equalsIgnoreCase(propertyName))
        {
            return propPitchMax;
        }
    	if("tonescale".equalsIgnoreCase(propertyName))
        {
            return propToneScale;
        }
    	if("playOnlyChangingNotes".equalsIgnoreCase(propertyName))
        {
            return propPlayOnlyChangingNotes;
        }
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            return propDisplayGUI;
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param[in] propertyName   the name of the property
     * @param[in] newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if("midiDevice".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propMidiDevice;
            propMidiDevice = (String) newValue;
            MidiManager.instance.openMidiDevice(propMidiDevice);
            return oldValue;
        }
        if("channel".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propChannel;
            propChannel = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("instrument".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propInstrument;
            propInstrument = (String) newValue;
            MidiManager.instance.instrumentChange (propMidiDevice, propChannel, propInstrument);
            return oldValue;
        }
        if("triggerThreshold".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propTriggerThreshold;
            propTriggerThreshold = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("triggerMax".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propTriggerMax;
            propTriggerMax = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("pitchMin".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propPitchMin;
            propPitchMin = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("pitchMax".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propPitchMax;
            propPitchMax = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("toneScale".equalsIgnoreCase(propertyName))
        {
        	int i;
            final Object oldValue = propToneScale;
            propToneScale = (String) newValue;

            scale.loadScale(propToneScale);
			amountOfNotes = scale.size;
            return oldValue;
        }
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propDisplayGUI;

            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = false;
            }
            return oldValue;
        }    	
    	if("playOnlyChangingNotes".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propPlayOnlyChangingNotes;

            if("true".equalsIgnoreCase((String)newValue))
            {
            	propPlayOnlyChangingNotes = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propPlayOnlyChangingNotes = false;
            }
            return oldValue;
        }    	

        return null;
    }

    
	
	 public static List<File> findFiles(File where, String extension, int maxDeep)
	 {
		 File[] files = where.listFiles();
		 ArrayList<File> result = new ArrayList<File>();
	
		 if(files != null)
		 {
			 for (File file : files) 
			 {
				 if (file.isFile() && file.getName().endsWith(extension))
					 result.add(file);
				 else if ( (file.isDirectory()) &&( maxDeep-1 > 0 ) )
				 {
					 // do the recursive crawling
			         List<File> temp = findFiles(file, extension, maxDeep-1);
		             for(File thisFile : temp)
		                   result.add(thisFile);
				 }
			 }
		 }
		 return result;
	 }
	
	/**
	 * Returns all the scale filenames inside the path folder data/midi
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>();
		try
		{
			if (key.equals("toneScale"))
			{
				List<File> files = findFiles(new File("data/actuator.midi"), ".sc", 200);
				for (File file : files)
				{
					res.add(file.getName()); //.getPath().substring(file.getPath().indexOf("set")));
				}
			}
			if (key.equals("midiDevice"))
			{
				for (Info dev : MidiManager.instance.midiDeviceInfos)
				{
					res.add(dev.getName()); 
				}
			}
			if (key.equals("instrument"))
			{
				for (String inst : MidiManager.instance.getInstrumentNames(propMidiDevice))
				{
					res.add(inst); 
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	} 

	private void lastNoteOff()
	{
		if (oldNote>0)
			MidiManager.instance.midiNoteOff(propMidiDevice, propChannel,scale.noteNumberArray[oldNote]);
		oldNote=-1;
	}

	private void noteOn()
	{
		if ((propPlayOnlyChangingNotes == false) || (oldNote != selectedNote) || (toneTriggerMode!=0))
		{ 	
			if (propInstrument.startsWith("Controller"))
				MidiManager.instance.midiControlChange(propMidiDevice, propChannel,0,scale.noteNumberArray[selectedNote]);
			else
			{
				lastNoteOff();
				MidiManager.instance.midiNoteOn(propMidiDevice, propChannel,scale.noteNumberArray[selectedNote], velocity);
			}
			oldNote = selectedNote;
			toneTriggerMode=0;
							
			//if (midiChannel != null)  midiChannel[propChannel].controlChange(7,volume);
		}
	}

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipTrigger  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			triggerInput = ConversionUtils.intFromBytes(data);
			

				if (triggerInput < propTriggerThreshold)
				{
					velocity=0;
					lastNoteOff();
				}
				else
				{
					if(triggerInput > propTriggerMax)
						velocity = 127;
					else
					{
						double roundVolume = (127/(double)(propTriggerMax-propTriggerThreshold))*(triggerInput-propTriggerThreshold);
						velocity = (int)roundVolume;
					}
					oldNote=-1;
					toneTriggerMode=1;
					noteOn();
				}
		}

	};
		
	private final IRuntimeInputPort ipPitch  = new DefaultRuntimeInputPort()
	{	
		public void receiveData(byte[] data)
		{
    		pitchInput = ConversionUtils.intFromBytes(data);
			if(pitchInput < propPitchMin)
			{
				selectedNote = 0;
			}
			else if(pitchInput > propPitchMax)
			{
				selectedNote = amountOfNotes-1;
			}
			else
			{
				selectedNote =  (int)((double)(pitchInput-propPitchMin) / (double)(propPitchMax-propPitchMin) * (double) amountOfNotes); 
			}
    			
			if (gui!=null && propDisplayGUI == true) gui.repaint();
			noteOn();

		}
		
	};

	private final IRuntimeInputPort ipScale  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
		    propToneScale = ConversionUtils.stringFromBytes(data);
            scale.loadScale(propToneScale);
			amountOfNotes = scale.size;
		}

	};
    
	private final IRuntimeInputPort ipInstrument  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
		    propInstrument = ConversionUtils.stringFromBytes(data);
            MidiManager.instance.instrumentChange (propMidiDevice, propChannel, propInstrument);
		}
	};

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  oldNote=-1;
    	  toneTriggerMode=0;
		  gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
		  if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  lastNoteOff();
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
    	  lastNoteOff();
    	  if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, false);
          super.stop();
      }
}