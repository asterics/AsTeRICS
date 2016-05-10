
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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.signalgenerator;

import java.util.Random;

import eu.asterics.component.sensor.signalgenerator.SignalGeneratorInstance.OutputPort;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the signal generation thread for the generator plugin
 *
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 10:22:08 AM
 */
public class CoordinatesGenerator implements Runnable{

	private Thread runThread = null;
	long count; 
	private OutputPort out;
	private Random r;
	private int sendInterval;
	private int waveForm;
	private double frequency;
	private double amplitude;
	private double phaseShift;	
	private double offset;	
	private int active;
	private int rstep=0;
	long starttime, resumetime=0;

    final int WAVEFORM_RANDOM = 0;
    final int WAVEFORM_SINE = 1;
    final int WAVEFORM_SAWTOOTH = 2;
    final int WAVEFORM_RECTANGLE = 3;

    
	/**
	 * The class constructor, get parameters from plugin properties
	 */
	public CoordinatesGenerator(final SignalGeneratorInstance.OutputPort out,
			final int sendInterval,
			final int waveForm,
			final double frequency,
			final double amplitude,
			final double phaseShift,
			final double offset){

		this.out = out;
		this.sendInterval = sendInterval;
		this.waveForm = waveForm;
		this.frequency = frequency;
		this.amplitude = amplitude;
		this.phaseShift = phaseShift;
		this.offset = offset;
		this.active=0;
		this.r =  new Random();
		//	        init();

	}
	
	/**
	 * initialize the signal generation, start the thread
	 */
	public void init()	
	{	
		count=0;
		rstep=0;
		AstericsThreadPool.instance.execute(this);
	}
	
	/**
	 * set send interval
	 */
	public void setSendInterval (int newValue)
	{
		this.sendInterval=newValue;
	}

	
	/**
	 * set waveform type
	 */
	public void setWaveForm(int newValue)
	{
		this.waveForm=newValue;
		if (newValue==WAVEFORM_RECTANGLE) 
			rstep=(int)(count*frequency/500);
	}

	
	/**
	 * set signal frequency
	 */
	public void setFrequency (double newValue)
	{
		this.frequency=newValue;
		if (newValue==WAVEFORM_RECTANGLE) 
			rstep=(int)(count*frequency/500);
	}

	/**
	 * set signal amplitude
	 */
	public void setAmplitude (double newValue)
	{
		this.amplitude=newValue;
	}

	
	/**
	 * set phase shift
	 */
	public void setPhaseShift (double newValue)
	{
		this.phaseShift=newValue;
	}

	/**
	 * set signal offset
	 */
	public void setOffset (double newValue)
	{
		this.offset=newValue;
	}

	/**
	 * called when model is started
	 */
	public void start()
	{
		if (runThread != null) return;

		count=0;
		rstep=0;
		active=1; 	
		AstericsThreadPool.instance.execute(this);
	}

	/**
	 * called when model is paused
	 */
	public void pause()
	{
		resumetime=System.currentTimeMillis();
		active=2;
	}

	/**
	 * called when model is resumed
	 */
	public void resume()
	{
		if (resumetime!=0)
			starttime+=(System.currentTimeMillis()-resumetime);
		resumetime=0;
		//count=0;rstep=0;
		active=1;
	}

	/**
	 * called when model is stopped
	 */
	public void stop ()
	{
		if (runThread!=null)
			runThread.interrupt();
		active=0;
	}

	/**
	 *  the signal generation thread
	 */
	 public void run()
	{
	    runThread = Thread.currentThread();
		starttime=System.currentTimeMillis();
		while (active != 0)
		{
			if (active == 1)
			{
				while (System.currentTimeMillis()-starttime>count)
				{
					switch (waveForm) {
					case WAVEFORM_RANDOM: out.sendData(offset+r.nextDouble()*amplitude);				 		
						break;
					case WAVEFORM_SINE: out.sendData(offset+amplitude*Math.sin(((double)count+phaseShift)/1000*frequency*2*Math.PI));
						break;
					case WAVEFORM_SAWTOOTH: out.sendData(offset+((int)((count+phaseShift)*frequency) % 1000)*amplitude/1000) ;
						break;	
					case WAVEFORM_RECTANGLE:if (count>=(int)(500*rstep/frequency)) rstep++; 
							out.sendData(offset+ (rstep %2 ==0 ? 0:amplitude));
						break;				
				   }
				   count+=sendInterval;
				}
			}
			try {
				Thread.sleep(10); 
			} catch (InterruptedException e) {active=0;}
		}
		runThread=null;
	}


}
