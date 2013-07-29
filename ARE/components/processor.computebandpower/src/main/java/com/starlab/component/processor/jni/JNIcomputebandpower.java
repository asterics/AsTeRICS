
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

package com.starlab.component.processor.jni;

/**
 *   Implements the access to the native functions for computing the power in
 *   band algorithm
 * 
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: Feb 11, 2011
 *         Time 01:11:01 PM
 */
public class JNIcomputebandpower {

	public native void nativeCBPInitialization();
	public native void nativeCBPImplementation (double [] samples, int len, double [] output);
	
	static {
		System.loadLibrary("computebandpower");
    }

	/**
     * The class constructor.
     */
	public JNIcomputebandpower ()
	{
	}
	
	/**
     * Initialization of the compute band power algorithm
     */
	public void CBPInitialization()
	{
		nativeCBPInitialization();
	}
	
	/**
     * Computation of the frequency transformation from the time domain samples
     * @param samples       Vector with the input samples in the time domain
     *                      to be processed
     * @param output        Vector with the output samples corresponding to
     *                      the frequency domain
     */
	public void CBP (double [] samples, double [] output)
	{
		nativeCBPImplementation(samples, samples.length, output);
	}
}