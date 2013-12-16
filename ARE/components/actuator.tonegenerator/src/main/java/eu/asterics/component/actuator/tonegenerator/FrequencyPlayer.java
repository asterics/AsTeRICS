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

 /* @author David Thaller [dt@ki-i.at]
 *         Date: 10.09.2013
 *         Time:  
 */
package eu.asterics.component.actuator.tonegenerator;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import com.jsyn.unitgen.UnitOscillator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrequencyPlayer
{
	private Synthesizer synth;
	private UnitOscillator osc,osc1;
	private LineOut lineOut;
	
	public void init()
	{
		synth = JSyn.createSynthesizer();
		setOscillator(1, true);
	}
        
  public void setFrequency(int channel, int f) 
	{
		switch (channel) {
			case 0:
				if (osc != null)
					osc.frequency.setup( 50.0, f, 10000.0 );
				break;
			case 1:
				if (osc1 != null)
					osc1.frequency.setup( 50.0, f, 10000.0 );
				break;
		}
  }
  
  public void setOscillator(int actOsc, boolean separateChannels)
  {
  	synth.remove( osc );
		synth.remove( osc1 );
  	switch(actOsc) {
  		case 1:
				synth.add( osc = new SineOscillatorPhaseModulated() );
				synth.add( osc1 = new SineOscillatorPhaseModulated() );  		
				break;
			case 2:
				synth.add( osc = new SquareOscillator() );
				synth.add( osc1 = new SquareOscillator() );		
				break;
			case 3:
				synth.add( osc = new SawtoothOscillator() );
				synth.add( osc1 = new SawtoothOscillator() );		
				break;
  	}
  	synth.add( lineOut = new LineOut() );
  	
		osc.output.connect( 0, lineOut.input, 0 );
		osc1.output.connect( 0, lineOut.input, 1 );
		if (!separateChannels) {
			osc.output.connect( 0, lineOut.input, 1 );
			osc1.output.connect( 0, lineOut.input, 0 );			
		}
  }

	public void start()
	{
		if (synth != null)
			synth.start();
		if (lineOut != null) 
			lineOut.start();
	}

	public void stop()
	{
		if (synth != null)
			synth.stop();
	}
}