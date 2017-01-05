
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
 * Implements the access to the native functions for computing the decimation
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Apr 29, 2011 Time
 *         04:51:02 PM
 */
public class JNIdecimation {

    public native boolean nativeDecimation(double sample, int downs_ratio, double[] output);

    static {
        System.loadLibrary("decimation");
    }

    /**
     * The class constructor.
     */
    public JNIdecimation() {
    }

    /**
     * Deciamtion calculation
     * 
     * @param samples
     *            Input sample
     * @param downs_ration
     *            Factor of decimation
     * @param ouput
     *            Output decimated value
     * @return True when the output value has a new decimated signal, false
     *         otherwise
     */
    public boolean Decimation(double sample, int downs_ratio, double[] output) {
        return nativeDecimation(sample, downs_ratio, output);
    }
}