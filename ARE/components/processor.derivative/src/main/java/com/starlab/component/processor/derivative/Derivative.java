
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package com.starlab.component.processor.derivative;

import com.starlab.component.processor.jni.JNIderivative;

/**
 *   Implements the derivative of the input signal
 * 
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: May 1, 2011
 *         Time 08:28:57 PM
 */
public class Derivative {

    private JNIderivative jni = new JNIderivative();
	private int id;

	/**
     * The class constructor.
     */
    public Derivative()
    {
		id = jni.DerivativeNew();
        reset();
    }
    
    /**
     * It performs the operations for cleaning up the plugin
     */
	protected void finalize()
	{
		jni.DerivativeDelete(id);
	}

	/**
	 * It resets derivative algorithm for starting a calculation of a new
	 * signal
	 */
    public void reset ()
    {
        jni.DerivativeReset(id);
    }

    /**
     * It performs the derivative calculation
     * @param in         Input sample
     * @return           derivative result
     */
    public double doDerivative (double in)
    {
        return jni.Derivative(id, in);
    }

    /**
	 * Gets the configured sample frequency of the signal
	 * @return           Configured sample frequency of the signal
	 */
    public int getSampleFrequency ()
    {
        return jni.getSampleFrequency(id);
    }

    /**
	 * Sets the sample frequency of the input signal
	 * @param samplefrequency  Sample frequency of the input signal
	 */
    public void setSampleFrequency (int sampleFrequency)
    {
        jni.setSampleFrequency (id, sampleFrequency);
    }
}