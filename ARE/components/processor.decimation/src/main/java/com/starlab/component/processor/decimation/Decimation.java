
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

package com.starlab.component.processor.decimation;

import com.starlab.component.processor.jni.JNIdecimation;

/**
 * Implements the decimation of an input signal
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Apr 29, 2011 Time
 *         04:51:02 PM
 */
public class Decimation {

    private JNIdecimation jni = new JNIdecimation();

    private int downSamplingRatio = 2;
    private double[] outValue = new double[1];

    /**
     * The class constructor
     */
    public Decimation() {
        outValue[0] = 0;
    }

    /**
     * Set the down sampling ratio
     * 
     * @param downSamplingRatio
     *            The new down sampling ratio
     */
    public void setDownSampling(int downSamplingRatio) {
        this.downSamplingRatio = downSamplingRatio;
    }

    /**
     * It perform the decimation algorithm
     * 
     * @param in
     *            New incoming sample to be decimated
     * @return True if the incoming sample produces a new decimated value
     */
    public boolean decimate(double in) {
        return jni.Decimation(in, downSamplingRatio, outValue);
    }

    /**
     * Gets the last decimated value
     * 
     * @return Last decimated value
     */
    public double getLastDecimated() {
        return outValue[0];
    }
}