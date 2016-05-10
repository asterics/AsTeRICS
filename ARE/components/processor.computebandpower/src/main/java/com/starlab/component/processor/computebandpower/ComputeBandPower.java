
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


package com.starlab.component.processor.computebandpower;

import com.starlab.component.processor.jni.JNIcomputebandpower;

/**
 *   Implements the bridge to the JNI functions that execute the compute band
 *   power algorithm
 * 
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: May 5, 2011
 *         Time 01:06:51 PM
 */
public class ComputeBandPower {

    private JNIcomputebandpower jni = new JNIcomputebandpower();

    public static final int DEFAULT_DATALEN_VALUE = 1024;
    public static final int DEFAULT_SAMPLERATE_VALUE = 250;
    public static final int DEFAULT_STARTBAND_VALUE = 0;
    public static final int DEFAULT_ENDBAND_VALUE = 125;
    public static final int DEFAULT_PSDCOMPUTINGRATE = DEFAULT_DATALEN_VALUE;
    private int dataLen = DEFAULT_DATALEN_VALUE;
    private int sampleRate = DEFAULT_SAMPLERATE_VALUE;
    private int startBand = DEFAULT_STARTBAND_VALUE;
    private int endBand = DEFAULT_ENDBAND_VALUE;
    private int psdComputingRate = DEFAULT_PSDCOMPUTINGRATE;

    /**
     * The class constructor.
     */
    public ComputeBandPower ()
    {
        jni.CBPInitialization();
    }
    
    /**
     * It Sets the psd computing rate (as a % of the dataLen)
     * @return             psd computing rate
     */
    public void setPsdComputingRate(int psdComputingRate)
    {
    	int val = (int)(psdComputingRate*this.dataLen/100);
    	
    	if (val < 1)
    		this.psdComputingRate = 1;
    	else
    		this.psdComputingRate = val;         
    }

    /**
     * It sets the length of the input data
     * @param dataLen       Lenght of the input data
     */
    public void setDataLen (int dataLen)
    {
        this.dataLen = dataLen;
    }

    /**
     * It sets the sample rate of the input signal
     * @param sampleRate    Sample rate of the input signal
     */
    void setSampleRate(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    /**
     * It set the start frequency of the band where the computations is
     * performed
     * @param starBand      Start frequency of the band under analysis
     */
    void setStartBand(int startBand)
    {
        this.startBand = startBand;
    }

    /**
     * It set the end frequency of the band where the computations is
     * performed
     * @param endBand      End frequency of the band under analysis
     */
    void setEndBand(int endBand)
    {
        this.endBand = endBand;
    }
    
    /**
     * It gets the configured psd computing rate (in samlpes)
     * @return             psd computing rate
     */
    int getPsdComputingRate()
    {
        return this.psdComputingRate;
    }

    /**
     * It gets the configured length of the input data
     * @return             Lenght of the input data
     */
    int getDataLen()
    {
        return this.dataLen;
    }

    /**
     * It gets the configured sample rate of the input signal
     * @return             Sample rate of the input signal
     */
    int getSampleRate()
    {
        return this.sampleRate;
    }

    /**
     * It gets the configured start frequency of the band where the computation
     * is performed
     * @return             Start frequency of the band under analysis
     */
    int getStartBand()
    {
        return this.startBand;
    }

    /**
     * It gets the configured end frequency of the band where the computation
     * is performed
     * @return             End frequency of the band under analysis
     */
    int getEndBand()
    {
        return this.endBand;
    }

    /**
     * It computes the power that the input signal has in the configured band
     * @param in           Vector of input samples in the time domain
     * @return             Power in V^2 (assuming the input signal is given in
     *                     Volts) that is present in input signal in the given
     *                     band 
     */
    public double compute(double[] in)
    {
    	//int len = in.length;
        if (in.length != dataLen)
           return -1;

        double[] out = new double[dataLen/2];

        jni.CBP(in, out);

		//Calculation of the PSD of the band
		int psdLen = dataLen / 2;
		double hzperbin = sampleRate / (double)dataLen;
		double currentF = 0;
		double psd = 0;
		for (int i = 0; i < psdLen; i++)
		{
			if (currentF >= (double)startBand)
			{
				if (currentF > (double)endBand)
					break;
				psd += 2* ((out[i] / (double)dataLen) * (out[i] / (double)dataLen));
			}
			currentF += hzperbin;
		}
		
        return psd;
    }

}