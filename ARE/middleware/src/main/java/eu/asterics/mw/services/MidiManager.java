package eu.asterics.mw.services;

import java.util.HashMap;
import java.util.Vector;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

public class MidiManager {

	public static MidiManager instance = 
			new MidiManager();


	final static boolean debugOutput = false;
	
    Synthesizer synthesizer = null;
	MidiDevice midiDevice = null;
    Receiver receiver =null;    
    public Vector<Info> midiDeviceInfos=null;
    
    private HashMap<String, MidiUnit> midiUnitsByName = new HashMap<String, MidiUnit>(); 


	private MidiManager()
	{
		if (debugOutput) System.out.println("MidiManger Started");
		if (midiDeviceInfos == null)
		{
			if (debugOutput) System.out.println("MidiManger: first start - getting  Midi Deviceinfos!");
		    midiDeviceInfos= getActDeviceInfos();
		}
	}

	 
		/**
		 * Returns a vector of all playable Midi Devices (Synthesizers or Transmitters)
		 * @return	a vector of all playable Midi Devices (Synthesizers or Transmitters)
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
					if((device instanceof Synthesizer) || (device.getMaxTransmitters()>-1))
					{
						if (debugOutput) System.out.println("MidiManager: Playable device found: " +device.getDeviceInfo().getName());
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
		 * Returns a list of all available MIDI-instruments for a device
		 * @param deviceInfo	information of type Info about the MidiDevice 
		 * @return				list of Instruments
		 */

		public Vector<String> getInstrumentNames(String deviceName)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) { return(null); }
			return (midiUnit.instrumentNames);
		}
		
		public boolean openMidiDevice(String newDeviceName) // MidiDevice.Info deviceInfo)
		{	
			if (debugOutput) System.out.println("Midimanager: open called.");
			if (midiUnitsByName.get(newDeviceName) != null)
			{
				if (debugOutput) System.out.println("Midimanager: Device " + newDeviceName +" found in map returning !");
				return true;
			}

			if (debugOutput) System.out.println("Midimanager: Device " + newDeviceName +" not fount in map, trying to add / open the device.");
			MidiDevice.Info deviceInfo=null;
			boolean found=false;

			for (int i=0; i< midiDeviceInfos.size(); i++)
			{
				deviceInfo=midiDeviceInfos.elementAt(i);
				if (newDeviceName.equals(deviceInfo.getName()))
				{
					found=true;
					break;
				}
			}
			if (!found)	return(false);

			if (debugOutput) System.out.println("Midimanager: "+deviceInfo.getName()+" found - trying to open.");
			try 
			{
				// if (mididevice != null) mididevice.close();
				// if (synthesizer!=null) synthesizer.close();
				
				midiDevice= MidiSystem.getMidiDevice(deviceInfo);
				if ( midiDevice instanceof Synthesizer)
				{
					synthesizer = (Synthesizer) midiDevice;
					if (synthesizer.isOpen()==false)
					{
						synthesizer.open();
						if (debugOutput) System.out.println("Midimanager: Synthesizer "+midiDevice.getDeviceInfo().getName()+" opened.");
					}
					else if (debugOutput) System.out.println("Midimanager: Synthesizer "+midiDevice.getDeviceInfo().getName()+" reopened.");

					MidiUnit midiUnit = new MidiUnit();
					midiUnit.midiDevice=midiDevice;
					midiUnit.synthesizer = synthesizer;
					midiUnit.midiChannels =synthesizer.getChannels();
					midiUnit.receiver= null;
					Instrument[] instrumentNames = synthesizer.getAvailableInstruments();
					Vector<String> list = new Vector<String>();		
					for (int i=0;(i<instrumentNames.length); i++ )
						list.add(instrumentNames[i].getName());
					midiUnit.instrumentNames=list;
					midiUnitsByName.put(newDeviceName, midiUnit);
				}
				else
				{
					synthesizer=null;
					if (midiDevice.isOpen()==false)
					{
						midiDevice.open();
						if (debugOutput) System.out.println("Midimanager: MidiDevice "+midiDevice.getDeviceInfo().getName()+" opened.");
					}
					else
					{
						if (debugOutput) System.out.println("Midimanager: MidiDevice "+midiDevice.getDeviceInfo().getName()+" reopened.");
					}
					receiver =  midiDevice.getReceiver(); // MidiSystem.getReceiver();

					MidiUnit midiUnit = new MidiUnit();
					midiUnit.midiDevice=midiDevice;
					midiUnit.synthesizer = null;
					midiUnit.midiChannels = null;
					midiUnit.receiver= receiver;
					Vector<String> list = new Vector<String>();
					String[] names = getGeneralMidiInstrumentNames();
					for (int i=0;i<128; i++ ) list.add(names[i]);
					for (int i=0;i<128; i++ ) list.add("Controller "+i);
					midiUnit.instrumentNames=list;
					midiUnitsByName.put(newDeviceName, midiUnit);
				}
				return(true);
					
			} catch (Exception e1) 
			{
				e1.printStackTrace();
			}
			return(false);
		}
		
		
		public void instrumentChange(String deviceName, int channel, String newInstrumentName)
		{
			
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			
			int instrument;	
			for (instrument=0;instrument<midiUnit.instrumentNames.size();instrument++)
			{
				if (midiUnit.instrumentNames.elementAt(instrument).startsWith(newInstrumentName))
					 break;
			}
			
			if (instrument>=midiUnit.instrumentNames.size())
				instrument=0;
			
			if (midiUnit.synthesizer != null) 
			{
				if (debugOutput) System.out.println("MidiManager: changing Synth channel "+channel+" to instrument "+instrument);
				midiUnit.midiChannels[channel].programChange(instrument);
			}
			else if ((midiUnit.receiver != null) && (instrument < 128))
	    	{
				if (debugOutput) System.out.println("MidiManager: changing Mididevice channel "+channel+" to instrument "+instrument);

				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.PROGRAM_CHANGE, channel, instrument,0 ); 
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    	}	
		}
		
		public void midiAllSoundOff(String deviceName, int channel)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;

			if (midiUnit.synthesizer != null) midiUnit.midiChannels[channel].allSoundOff();
		}

		public void midiNoteOn(String deviceName, int channel, int note, int velocity)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null) midiUnit.midiChannels[channel].noteOn(note, velocity);
			else if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.NOTE_ON, channel, note, velocity); 
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		public void midiNoteOff(String deviceName, int channel, int note)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null) 
				midiUnit.midiChannels[channel].noteOff(note);
			if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.NOTE_OFF, channel, note);  
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		public void midiChannelPressure(String deviceName, int channel, int pressure)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null)
				midiUnit.midiChannels[channel].setChannelPressure(pressure);
			if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.CHANNEL_PRESSURE, channel, pressure);  
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		public void midiChannelPolyPressure(String deviceName, int channel, int note, int pressure)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null)
				midiUnit.midiChannels[channel].setPolyPressure(note,pressure);
			if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.POLY_PRESSURE, channel, note, pressure);  
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		public void midiPitchBend(String deviceName, int channel, int bend)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null)
				midiUnit.midiChannels[channel].setPitchBend(bend);
			else if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.PITCH_BEND, 0, bend);  
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		public void midiControlChange(String deviceName, int channel, int data1, int data2)
		{
			MidiUnit midiUnit=midiUnitsByName.get(deviceName);
			if (midiUnit==null) return;
			if (midiUnit.synthesizer != null)
				midiUnit.midiChannels[channel].controlChange(data1,data2);
			if (midiUnit.receiver!=null)
			{
				try 
				{
					ShortMessage myMsg = new ShortMessage();
					myMsg.setMessage(ShortMessage.CONTROL_CHANGE, channel, data1, data2); 
					midiUnit.receiver.send(myMsg, -1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		
		public String[] getGeneralMidiInstrumentNames()
		{
		   String[] names = { "1 Acoustic Grand Piano", "2 Bright Acoustic Piano", "3 Electric Grand Piano", "4 Honky-tonk Piano",
				   "5 Electric Piano 1", "6 Electric Piano 2", "7 Harpsichord", "8 Clavinet", "9 Celesta", "10 Glockenspiel",
				   "11 Music Box", "12 Vibraphone", "13 Marimba", "14 Xylophone", "15 Tubular Bells", "16 Dulcimer",
				   "17 Drawbar Organ", "18 Percussive Organ", "19 Rock Organ", "20 Church Organ", "21 Reed Organ",
				   "22 Accordion", "23 Harmonica", "24 Tango Accordion", "25 Acoustic Guitar (nylon)", "26 Acoustic Guitar (steel)",
				   "27 Electric Guitar (jazz)", "28 Electric Guitar (clean)", "29 Electric Guitar (muted)", "30 Overdriven Guitar",
				   "31 Distortion Guitar", "32 Guitar harmonics", "33 Acoustic Bass", "34 Electric Bass (finger)",
				   "35 Electric Bass (pick)", "36 Fretless Bass", "37 Slap Bass 1", "38 Slap Bass 2", "39 Synth Bass 1",
				   "40 Synth Bass 2", "41 Violin", "42 Viola", "43 Cello", "44 Contrabass", "45 Tremolo Strings",
				   "46 Pizzicato Strings", "47 Orchestral Harp", "48 Timpani", "49 String Ensemble 1", "50 String Ensemble 2",
				   "51 Synth Strings 1", "52 Synth Strings 2", "53 Choir Aahs", "54 Voice Oohs", "55 Synth Voice",
				   "56 Orchestra Hit", "57 Trumpet", "58 Trombone", "59 Tuba", "60 Muted Trumpet", "61 French Horn",
				   "62 Brass Section", "63 Synth Brass 1", "64 Synth Brass 2", "65 Soprano Sax", "66 Alto Sax", "67 Tenor Sax",
				   "68 Baritone Sax", "69 Oboe", "70 English Horn", "71 Bassoon", "72 Clarinet", "73 Piccolo",
				   "74 Flute", "75 Recorder", "76 Pan Flute", "77 Blown Bottle", "78 Shakuhachi", "79 Whistle",
				   "80 Ocarina", "81 Lead 1 (square)", "82 Lead 2 (sawtooth)", "83 Lead 3 (calliope)", "84 Lead 4 (chiff)",
				   "85 Lead 5 (charang)", "86 Lead 6 (voice)", "87 Lead 7 (fifths)", "88 Lead 8 (bass + lead)",
				   "89 Pad 1 (new age)", "90 Pad 2 (warm)", "91 Pad 3 (polysynth)", "92 Pad 4 (choir)",
				   "93 Pad 5 (bowed)", "94 Pad 6 (metallic)", "95 Pad 7 (halo)", "96 Pad 8 (sweep)", "97 FX 1 (rain)",
				   "98 FX 2 (soundtrack)", "99 FX 3 (crystal)", "100 FX 4 (atmosphere)", "101 FX 5 (brightness)",
				   "102 FX 6 (goblins)", "103 FX 7 (echoes)", "104 FX 8 (sci-fi)", "105 Sitar", "106 Banjo", "107 Shamisen",
				   "108 Koto", "109 Kalimba", "110 Bag pipe", "111 Fiddle", "112 Shanai", "113 Tinkle Bell", "114 Agogo",
				   "115 Steel Drums", "116 Woodblock", "117 Taiko Drum", "118 Melodic Tom", "119 Synth Drum", "120 Reverse Cymbal",
				   "121 Guitar Fret Noise", "122 Breath Noise", "123 Seashore", "124 Bird Tweet", "125 Telephone Ring", "126 Helicopter",
				   "127 Applause", "128 Gunshot" };
		   	return(names);
		}
	
}