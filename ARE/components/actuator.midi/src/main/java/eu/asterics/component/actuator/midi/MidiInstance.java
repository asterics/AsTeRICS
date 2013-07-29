

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
import java.util.List;
import java.util.Vector;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
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
	
	Scales scale = new Scales();	//Class for musical scales.
	int[] noteNumberArray = scale.noteNumberArray("alltones");	//Array of musical scale corresponding numbers.


    public Synthesizer synthesizer = null;
	public MidiDevice mididevice = null;
	public MidiChannel midiChannel = null;
    public Receiver receiver;    
    public Vector<Info> midiDeviceInfos;
    public Vector<String> instrumentNames;

	
    private boolean blow = true;
    private boolean edgeBlow = true;
    private boolean edgeBlowTrigger = false;
    public static int selectedNote;			//Selected Note
    private int oldNote;				//saves the old Note
    
    private int actInstrument = 0;	
    
    private int changeOctave = 0;
    private boolean changeOctaveDown = false;
    private boolean changeOctaveUp = false;
    private int thresholdStep = 0;
    public static int amountOfNotes = 8;
    private int velocity = 127;
    private int volume = 80;
    public boolean calibrateButtonPitch = false;
    public boolean calibrateButtonTrigger = false;
    public static int[] thresholdArray = new int[128];
    public static int range = 0;
    public boolean setMin = false;
    public boolean setMax = false;
    public boolean calibrationPitchSuccess = true;
    public boolean calibrationTriggerSuccess = true;

    int propPitchMin=0;
    int propPitchMax=1000;
    private int propTriggerThreshold=50;
    public int propTriggerMax = 100;
    String propToneScale="alltones";
    String propMidiDevice="Gervill";
    String propInstrument="Vibraphone";
    private boolean propDisplayGUI=true;

    public static int pitchInput=500;
    private int triggerInput=75;

    
   /**
    * The class constructor.
    */
    public MidiInstance()
    {
	    midiDeviceInfos= getActDeviceInfos();
		if (openMidiDevice("Gervill")==false)
			System.out.println("No Midi Device opened !");
		else instrumentNames=getInstrumentNames();
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
            openMidiDevice(propMidiDevice);
            return oldValue;
        }
        if("instrument".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propInstrument;
            propInstrument = (String) newValue;
            instrumentChange (propInstrument);
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
       
			noteNumberArray = scale.noteNumberArray(propToneScale);
			for (i=0; (i<256) && (noteNumberArray[i]>0); i++) ;
			amountOfNotes = i;
			thresholdArray= thresholdArray(propPitchMin, propPitchMax);
			
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
				for (Info dev : midiDeviceInfos)
				{
					res.add(dev.getName()); 
				}
			}
			if (key.equals("instrument"))
			{
				for (String inst : instrumentNames)
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

    
    
    
    
     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipTrigger  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			triggerInput = ConversionUtils.intFromBytes(data);
			
			if(calibrationTriggerSuccess)
			{
				if(triggerInput-propTriggerThreshold > propTriggerMax-propTriggerThreshold)
				{
					volume = 127;
				}
				else
				{
					double roundVolume = (127/(double)(propTriggerMax-propTriggerThreshold))*(triggerInput-propTriggerThreshold);
					volume = (int)roundVolume;
				}
			}	
			
			if(triggerInput >= propTriggerThreshold) 
			{
				blow = true;
			}
			else
			{
				blow = false;
				edgeBlow = false;
				midiAllSoundOff();
			}
    		
    		if(calibrateButtonTrigger)
    		{
    			propTriggerMax = triggerCalibration(triggerInput);
    		}
		}

	};
		
	private final IRuntimeInputPort ipPitch  = new DefaultRuntimeInputPort()
	{	
		public void receiveData(byte[] data)
		{
    		pitchInput = ConversionUtils.intFromBytes(data);
    		
    		if(calibrationPitchSuccess && calibrationTriggerSuccess)
    		{
    			for(int i = 0; i <= amountOfNotes; i++)
    			{
    				if(pitchInput < thresholdArray[0])
    				{
    					selectedNote = -1;
    					changeOctaveDown = true;
    					break;
    				}
    				else if(pitchInput > thresholdArray[amountOfNotes])
    				{
    					selectedNote = -1;
    					changeOctaveUp = true;
    					break;
    				}
    				else if(pitchInput > thresholdArray[i] && pitchInput < thresholdArray[i+1])
    				{
    					selectedNote = i;
    					changeOctaveUp = false;
    					changeOctaveDown = false;
    					break;
    				}
    			}
    			
    			gui.repaint();
    			
    			if(blow && !edgeBlow && changeOctaveDown)
    			{
    				edgeBlow = true;
    				changeOctaveDown = false;
    				if(changeOctave != -36)
    				{
        				changeOctave = changeOctave - 12;
    				}
    			}
    			else if(blow && !edgeBlow && changeOctaveUp)
    			{
    				edgeBlow = true;
    				changeOctaveUp = false;
    				if(changeOctave != 36)
    				{
        				changeOctave = changeOctave + 12;
    				}
    			}
    			else if(blow && (!edgeBlow || oldNote != selectedNote) && selectedNote != -1)
    			{
        			midiAllSoundOff();
    				midiNoteOn(noteNumberArray[selectedNote]+changeOctave, velocity);
    				edgeBlow = true;
    				oldNote = selectedNote;
    			}
    			
				midiChannel.controlChange(7,volume);
    		}
    		
    		if(calibrateButtonPitch && calibrationTriggerSuccess)
    		{
    			thresholdArray = pitchCalibration(pitchInput);
    		}

		}
		
	};


	
	 /**
     * Calibration of Trigger Input
     * @param data 	data received from the trigger input port
     * @return		thresholds that specifies the range of velocity
     */  
    public int triggerCalibration (int data)	//returns threshold for the pitch.
    {
		if(blow && !edgeBlow)
		{
			edgeBlowTrigger = true;
			if(data > propTriggerMax)
			{
				propTriggerMax = data;
			}
		}
		if(edgeBlowTrigger && !blow)
		{
			calibrationTriggerSuccess = true;	
			calibrateButtonTrigger = false;
			edgeBlowTrigger = false;
		}
	    return propTriggerMax;
    }
	
	 /**
     * Calibration of Pitch Input
     * @param data 	data received from the pitch input port
     * @return		thresholds that specify the range of each note
     */  
    public int[] pitchCalibration (int data)	//returns threshold for the pitch.
    {
	    if(blow && !edgeBlow && !setMin)
	    {
	        propPitchMin = data;
	        setMin = true;
		    edgeBlow = true;
	    }
	    
	    if(blow && !edgeBlow && !setMax)
	    {
	    	propPitchMax = data;
	    	setMax = true;
	    	edgeBlow = true;
	    }	    

	    if(setMin && setMax)
	    {
	    	thresholdArray = thresholdArray(propPitchMin, propPitchMax);
	    }
	    	
	    return thresholdArray;
    }
    
	 /**
     * Calibration of Pitch Input - Calculation of thresholdArrays
     * @param thresholdMin 	minimum pitch input
     * @param thresholdMax	maximum pitch input
     * @return		thresholds that specify the range of each note
     */
    public int[] thresholdArray(int thresholdMin, int thresholdMax)
    {
	    thresholdStep = (thresholdMax - thresholdMin)/(amountOfNotes-1); //defines the threshold_step
	    thresholdArray[0]=thresholdMin - (thresholdStep/2);
	    for(int i = 1; i <= amountOfNotes;i++)
	    {
	    	thresholdArray[i] = thresholdArray[i-1] + thresholdStep; 
	    }
	    calibrateButtonPitch = false;
	    calibrationPitchSuccess = true;
	    
	    return thresholdArray;
    }

    
    
    
	/**
	 * Returns a vector of all available synthesizers
	 * @return	Vector of all available synthesizers
	 */
	public Vector<Info> getActDeviceInfos()
	{
		Vector<Info> actDeviceInfos = new Vector<Info>(); 									//Vector<Info> = Generic
		MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo(); 	//gets a String of available MIDI-devices.
		for(int i = 0; i < deviceInfo.length; i++)
		{
			try
			{
				MidiDevice device = MidiSystem.getMidiDevice(deviceInfo[i]);
				System.out.println(device.getDeviceInfo().getName()+ " - Receivers: "+device.getMaxReceivers()+" Transmitters: "+device.getMaxTransmitters());
				if((device instanceof Synthesizer) || (device.getMaxTransmitters()>-1))
				{
					//System.out.println("SYNTHESIZER:"+device.getDeviceInfo().getName());
					actDeviceInfos.add(deviceInfo[i]);
				}
			}
			catch (MidiUnavailableException e)
			{
				e.printStackTrace();
			}
		}
		return actDeviceInfos;
	}	
	
	/**
	 * Returns a list of all available MIDI-instruments
	 * @param deviceInfo	information of type Info about the MidiDevice 
	 * @return				list of Instruments
	 */

	public Vector<String> getInstrumentNames()
	{
		Vector<String> list = new Vector<String>();		

		if (synthesizer != null)
		{
			Instrument[] instrumentNames = synthesizer.getAvailableInstruments();
			for (int i=0;(i<instrumentNames.length) && (i<128); i++ )
			{
				list.add(instrumentNames[i].getName());
			}
		}
		else
		{
			for (int i=0;i<128; i++ )
			{
				list.add("Instrument "+i);
			}
		}
		for (int i=0;i<128; i++ )
		{
			list.add("Controller "+i);
		}
		return  list;
	}
	
	public boolean openMidiDevice(String newDeviceName) // MidiDevice.Info deviceInfo)
	{	
		MidiDevice.Info deviceInfo =null;
		for (MidiDevice.Info di : midiDeviceInfos )
		{
			if (newDeviceName.equals(di.getName()))
				deviceInfo=di;
		}
		if (deviceInfo!=null)
		{
			try 
			{
				if (mididevice != null) mididevice.close();
				if (synthesizer!=null) synthesizer.close();
				
				mididevice= MidiSystem.getMidiDevice(deviceInfo);
				if ( mididevice instanceof Synthesizer)
				{
					synthesizer = (Synthesizer) mididevice;
					synthesizer.open();

					midiChannel= synthesizer.getChannels()[0];
					/*
					 MidiChannel[] availableMidiChannels = synthesizer.getChannels();
					 for(int i = 0; i < availableMidiChannels.length;i++)
					 {
						if(availableMidiChannels[i] != null)
						{
							return availableMidiChannels[i];
						}
					 }
					 */
					
					System.out.println("Synthesizer "+mididevice.getDeviceInfo().getName()+" opened.");
				}
				else
				{
					synthesizer=null;
					mididevice.open();
					receiver =  mididevice.getReceiver(); // MidiSystem.getReceiver();
					System.out.println("MidiDevice "+mididevice.getDeviceInfo().getName()+" opened.");					
				}
				return(true);
					
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return(false);
	}
	
	public void instrumentChange(String newInstrumentName)
	{
		int instrument;
		
		for (instrument=0;instrument<instrumentNames.size();instrument++)
		{
			if (instrumentNames.elementAt(instrument).equals(newInstrumentName))
				 break;
		}
		
		if (instrument<instrumentNames.size())
		{
			actInstrument=instrument;
			
			System.out.println("changing to instrument "+actInstrument);
	    	if (instrument < 128)
	    	{
				if (synthesizer != null) midiChannel.programChange(instrument);
				else if (receiver != null)
				{
					try 
					{
						ShortMessage myMsg = new ShortMessage();
						myMsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument,0 ); 
						receiver.send(myMsg, -1);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
	    	}	
		}
	}
	
	private void midiAllSoundOff()
	{
		if (synthesizer != null) midiChannel.allSoundOff();

	}

	private void midiNoteOn(int note, int velocity)
	{
		if (synthesizer != null) midiChannel.noteOn(note, velocity);
		else
		{
			try 
			{
				ShortMessage myMsg = new ShortMessage();
				if (actInstrument < 128)
				{
					myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, velocity);  // 60=note, 93=velocity
				}
				else
					myMsg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0, note); 
				receiver.send(myMsg, -1);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
			gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
		    thresholdArray = thresholdArray(propPitchMin, propPitchMax);

			if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
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
    	  if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, false);
          super.stop();
      }
}