package eu.asterics.mw.services;

import java.util.Vector;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;

public class MidiUnit {

	public MidiDevice midiDevice = null;
    public Synthesizer synthesizer = null;
	public MidiChannel midiChannels[] = null;
    public Receiver receiver=null;    
	public Vector<String> instrumentNames=null;
	
	public MidiUnit()
	{

	}
}
