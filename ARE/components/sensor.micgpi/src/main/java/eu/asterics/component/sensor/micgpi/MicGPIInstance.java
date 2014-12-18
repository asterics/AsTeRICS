

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

package eu.asterics.component.sensor.micgpi;

import java.util.*;

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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * This pluign delivers data from a sound input device (e.g. standard microphone)
 * It can be used to attach a standard momentary switch to the microphone input
 * and detect button press / release actions  or to detect sound events
 * 
 *  
 * @author David Thaller [dt@ki-i.at]
 */
public class MicGPIInstance extends AbstractRuntimeComponentInstance implements Runnable
{
	final IRuntimeOutputPort opPressure = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFrequency = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpInLow = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpInHigh = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	
	int [] sampleSizeList = {32,64,128,256,512,1024,2048};
 	
	int propSampleSize = 2;    //  32//64//128//256//512//1024//2048  default = 128 
	int propThresholdHigh = 30;
	int propThresholdLow = -30;
	double propNoiseLevel = 1.5;
	
	boolean propCalculateFrequency = true;
	boolean propPrintSpectrum = false;	
	String propCaptureDevice = "System Default";
	
	// declare member variables here
	private RecordDevice recDev;
    private TargetDataLine targetDataLine;
    private boolean running = true;
	private boolean highState, lowState;
		
   /**
    * The class constructor.
    */
    public MicGPIInstance()
    {
        recDev = new RecordDevice();
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("thresholdLow".equalsIgnoreCase(portID))
		{
			return ipThresholdLow;
		}
		if ("thresholdHigh".equalsIgnoreCase(portID))
		{
			return ipThresholdHigh;
		}
		return null;
	}

	private final IRuntimeInputPort ipThresholdLow  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			propThresholdLow = ConversionUtils.intFromBytes(data);
		}
		
	};

	private final IRuntimeInputPort ipThresholdHigh = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			propThresholdHigh = ConversionUtils.intFromBytes(data);
		}
		
	};
	
    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("pressure".equalsIgnoreCase(portID))
		{
			return opPressure;
		}
		if ("frequency".equalsIgnoreCase(portID))
		{
			return opFrequency;
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

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("inLow".equalsIgnoreCase(eventPortID))
		{
			return etpInLow;
		}
		if ("inHigh".equalsIgnoreCase(eventPortID))
		{
			return etpInHigh;
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
		if ("sampleSize".equalsIgnoreCase(propertyName))
		{
			return propSampleSize;
		}
	
		if ("captureDevice".equalsIgnoreCase(propertyName))
		{
			return propCaptureDevice;
		}
		if ("thresholdLow".equalsIgnoreCase(propertyName))
		{
			return propThresholdLow;
		}
		if ("thresholdHigh".equalsIgnoreCase(propertyName))
		{
			return propThresholdHigh;
		}
		if ("noiseLevel".equalsIgnoreCase(propertyName))
		{
			return propNoiseLevel;
		}
		if ("calculateFrequency".equalsIgnoreCase(propertyName))
		{
			return propCaptureDevice;
		}
		if ("printSpectrum".equalsIgnoreCase(propertyName))
		{
			return propCaptureDevice;
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
		if ("sampleSize".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSampleSize;
			propSampleSize = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("captureDevice".equalsIgnoreCase(propertyName)) {
			final String oldValue = propCaptureDevice;
			propCaptureDevice = (String) newValue;
			return oldValue;
		}
		if ("thresholdLow".equalsIgnoreCase(propertyName)) {
			final int oldValue = propThresholdLow;
			propThresholdLow = Integer.parseInt((String) newValue);
			return oldValue;
		}
		if ("thresholdHigh".equalsIgnoreCase(propertyName)) {
			final int oldValue = propThresholdHigh;
			propThresholdHigh = Integer.parseInt((String) newValue);
			return oldValue;
		}
		if ("noiseLevel".equalsIgnoreCase(propertyName)) {
			final double oldValue = propNoiseLevel;
			propNoiseLevel = Double.parseDouble((String) newValue);
			return oldValue;
		}
	    if("calculateFrequency".equalsIgnoreCase(propertyName))
	    {
	    	final boolean oldValue = propCalculateFrequency;
	        if("true".equalsIgnoreCase((String)newValue))
	        	propCalculateFrequency = true;
	        else propCalculateFrequency = false;
	        return oldValue;
	    }
	    if("printSpectrum".equalsIgnoreCase(propertyName))
	    {
	    	final boolean oldValue = propPrintSpectrum;
	        if("true".equalsIgnoreCase((String)newValue))
	        	propPrintSpectrum = true;
	        else propPrintSpectrum = false;
	        return oldValue;
	    }

        return null;
    }
	
	/**
	 * Returns a List of available CIM unique IDs
	 * @return list of string with CIM IDs
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("captureDevice")==0)
		{
			res.add("System Default");
			List<String> list = recDev.getCaptureDeviceNames();
			for (String s : list)
				System.out.println(s);
			res.addAll(recDev.getCaptureDeviceNames());
		}
		return res;
	} 
    

	@Override
    public void run() {
            if (this.targetDataLine == null) {
				AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
				return;
            }

			try {
				this.targetDataLine.open();
				this.targetDataLine.start();
			} catch (LineUnavailableException lue) {
				AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
				return;
			}

			int sampleCount = sampleSizeList[propSampleSize];
			byte [] data = new byte[sampleCount];
			double [] fftMagnitudes = new double[sampleCount/2];
            running = true;

			while (running == true) {
				int readBytes = targetDataLine.read(data, 0, sampleCount);
				if (readBytes <= 0)
					continue;
				
				int sum=0;
				for (int i = 0; i< readBytes; i++) {
					int value = data[i];

					if (Math.abs(value)<propNoiseLevel)
					{
						highState = false;
						lowState = false;
						value=0;
					}					
					else
					{
						if (value < propThresholdLow) {
							if (lowState == false) {
								etpInLow.raiseEvent();
								lowState = true;
							}
						} 
						if (value > propThresholdHigh) {
							if (highState == false) {
								etpInHigh.raiseEvent();
								highState = true;
							}
						}
					}
					
					if (propThresholdLow!=-1000) sum+=Math.abs(value);
					else sum+=value;
				}
				opPressure.sendData(ConversionUtils.doubleToBytes((double)sum/readBytes));

				if (propCalculateFrequency == true)
				{
					calculateFFT (data,fftMagnitudes,sampleCount);
					String spectrum = "";
					double max=-1;
					int maxIndex=-1;
					for (int i=0;i<sampleCount/2;i++)
					{
						double r=fftMagnitudes[i]*10;
						if (r<propNoiseLevel) r=0;
						if (r>max) {max=r; maxIndex=i;}
						if (propPrintSpectrum == true) {
							if (r<2) spectrum+=".";
							else if (r<4) spectrum+=":";
							else if (r<6) spectrum+="o";
							else if (r<8) spectrum+="x";
							else spectrum+="*";
						}
					}
					int dominantFrequency = maxIndex*8000/sampleCount;
					if (propPrintSpectrum == true)
					{
						spectrum+=(" DomF="+dominantFrequency);
						System.out.println(spectrum);
					}
					opFrequency.sendData(ConversionUtils.intToBytes(dominantFrequency));					
				}
        }
		this.targetDataLine.stop();
		this.targetDataLine.close();
    }
	 
	private TargetDataLine getTargetDataLine() {
	
		if (propCaptureDevice.equals("System Default")) {
			System.out.println("Getting Default Device");
			return recDev.getDefaultTargetDataLine();
		} else {
			System.out.println("Getting Device: " + propCaptureDevice);
			return recDev.getTargetDataLine(propCaptureDevice);
		}
	}
	  
	  
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		   
          super.start();
		  lowState = false;
		  highState = false;
		  targetDataLine = getTargetDataLine();
		  if (targetDataLine == null) {
			AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
			return;
		  }
		  AstericsThreadPool.instance.execute(this);
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
		running = false;
        super.stop();
      }
      
      
      
      
      boolean IsPowerOfTwo ( int x )
      {
          if ( x < 2 )    return (false);
          if ( ( x & (x-1)) != 0 )        // Thanks to 'byang' for this cute trick!
              return (false);
          return (true);
      }

      int NumberOfBitsNeeded ( int PowerOfTwo )
      {
          int i;
          for ( i=0; ; i++ )
            if ( (PowerOfTwo & (1 << i)) != 0 )   return i;
      }

      int ReverseBits ( int index, int NumBits )
      {
          int i, rev;
          for ( i=rev=0; i < NumBits; i++ )
          {
              rev = (rev << 1) | (index & 1);
              index >>= 1;
          }
          return rev;
      }

      public void calculateFFT (byte[] buffer, double[] fftbands, int numSamples)
      {
          int NumBits;					// Number of bits needed to store indices 
          int i, j, k, n, x;
          int BlockSize, BlockEnd;
          int bins;

          double angle_numerator = 2.0 * Math.PI;
      	double tr, ti;						// temp real, temp imaginary 
      	
      	if (!IsPowerOfTwo(numSamples)) return;

      	bins=numSamples/2;
          NumBits = NumberOfBitsNeeded ( numSamples );

      	double [] RealOut = new double[numSamples];
      	double [] ImagOut = new double[numSamples];
      
          //  data copy and bit-reversal ordering 
          for ( i=0; i < numSamples; i++ )
          {		
              j = ReverseBits ( i, NumBits );
              ImagOut[j] = 0.0;
      		RealOut[j] = buffer[i];
      		// apply hanning window !
      		RealOut[j] *= (0.5 + 0.5*Math.cos(2*Math.PI*(i-numSamples/2)/numSamples));
          }

          //   the FFT itself
          BlockEnd = 1;
          for ( BlockSize = 2; BlockSize <= numSamples; BlockSize <<= 1 )
          {
              double delta_angle = angle_numerator / (double)BlockSize;
              double sm2 = Math.sin ( -2 * delta_angle );
              double sm1 = Math.sin ( -delta_angle );
              double cm2 = Math.cos ( -2 * delta_angle );
              double cm1 = Math.cos ( -delta_angle );
              double w = 2 * cm1;
              double [] ar =new double[3];
              double [] ai =new double[3];

              for ( i=0; i < numSamples; i += BlockSize )
              {
                  ar[2] = cm2;
                  ar[1] = cm1;

                  ai[2] = sm2;
                  ai[1] = sm1;

                  for ( j=i, n=0; n < BlockEnd; j++, n++ )
                  {
                      ar[0] = w*ar[1] - ar[2];
                      ar[2] = ar[1];
                      ar[1] = ar[0];

                      ai[0] = w*ai[1] - ai[2];
                      ai[2] = ai[1];
                      ai[1] = ai[0];

                      k = j + BlockEnd;
                      tr = ar[0]*RealOut[k] - ai[0]*ImagOut[k];
                      ti = ar[0]*ImagOut[k] + ai[0]*RealOut[k];

                      RealOut[k] = RealOut[j] - tr;
                      ImagOut[k] = ImagOut[j] - ti;

                      RealOut[j] += tr;
                      ImagOut[j] += ti;
                  }
              }

              BlockEnd = BlockSize;
          }

         for (i=0;i<bins;i++)
         {
            fftbands[i] = Math.sqrt(ImagOut[i]*ImagOut[i]+RealOut[i]*RealOut[i])/bins;
         }

      }          
      
}