
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

package com.starlab.component.processor.dissimilarity;

import com.starlab.component.processor.jni.JNIdissimilarity;

/**
 * Implements the dissimilarity between two signals
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Apr 29, 2011 Time
 *         04:51:02 PM
 */ 
public class Dissimilarity {
    private JNIdissimilarity jni = new JNIdissimilarity();

    /**
     * The class constructor
     */
    Dissimilarity() {
    }

    /**
     * Dissimilarity calculation
     * 
     * @param in1
     *            First vector of samples
     * @param in2
     *            Second vector of samples
     * @return Dissimilarity between the two input vectors
     */
    double process(double[] in1, double[] in2) {
        return jni.Dissimilarity(in1, in2);
    }

}