
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

package com.starlab.component.processor.jni;

/**
 * Implements the access to the native functions for computing the derivative
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: May 1, 2011 Time
 *         08:28:57 PM
 */
public class JNIderivative {

    public native int nativeDerivativeNew();

    public native int nativeDerivativeDelete(int id);

    public native int nativeDerivativeReset(int id);

    public native double nativeDerivative(int id, double sample);

    public native int nativeGetSampleFrequency(int id);

    public native int nativeSetSampleFrequency(int id, int sampleFrequency);

    static {
        System.loadLibrary("derivative");
    }

    /**
     * The class constructor.
     */
    public JNIderivative() {
    }

    /**
     * It creates a new workspace for the derivative calculation
     * 
     * @return id of the new derivative workspace
     */
    public int DerivativeNew() {
        return nativeDerivativeNew();
    }

    /**
     * It deletes a workspace for the derivative calculation
     * 
     * @param id
     *            id of the derivative workspace to delete
     * @return 1 if is successes, 0 otherwise
     */
    public int DerivativeDelete(int id) {
        return nativeDerivativeDelete(id);
    }

    /**
     * It resets the given derivative workspace
     * 
     * @param id
     *            id of the workspace
     */
    public void DerivativeReset(int id) {
        nativeDerivativeReset(id);
    }

    /**
     * It performs the calculation of the derivative
     * 
     * @param id
     *            Id of the workspace
     * @param sample
     *            New sample of the signal to calculate its derivative
     * @return value of the derivative
     */
    public double Derivative(int id, double sample) {
        return nativeDerivative(id, sample);
    }

    /**
     * Gets the configured sample frequency of the signal
     * 
     * @param id
     *            Id of the workspace
     * @return Configured sample frequency of the signal
     */
    public int getSampleFrequency(int id) {
        return nativeGetSampleFrequency(id);
    }

    /**
     * Sets the sample frequency of the input signal
     * 
     * @param id
     *            Id of the workspace
     * @param samplefrequency
     *            Sample frequency of the input signal
     */
    public void setSampleFrequency(int id, int sampleFrequency) {
        nativeSetSampleFrequency(id, sampleFrequency);
    }
}