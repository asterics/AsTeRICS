
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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

/**
 * @file derivative.h
 *
 * @author Javier Acedo
 * @date 20/08/2010
 * @version 1.0
 *
 * @brief Derivative functions
 *
 * This file contains the definition of the simple signal processing functions
 * used in the AsTeRICS project
 *
 **/
#ifndef DERIVATIVE_H_
#define DERIVATIVE_H_

#ifndef BOOL
typedef unsigned char BOOL;
#endif
#ifndef NULL
#define NULL	0x00000000
#endif

/*
 * Derivative functions
 */

/**
* Create a new derivative workspace
*/
int Derivative_new ();

/**
* Delete an existing derivative workspace
**/
int Derivative_delete(int id);

/**
 * Reset the internal buffer for the derivative calculation
 **/
void Derivative_reset (int id);

/**
* It returns the current sample frequency used in the calculation of the
* derivative.
**/
int getSampleFrequency (int id);

/**
* Set the sample frequency. It allows to know the distance between samples used
* in the calculation of the derivative
* \param [in] sampleFrequency
*		The new sample frequency
**/
int setSampleFrequency (int id, int sampleFrequency);

/**
 * It implements the five points method derivative
 * \param [in] sample
 *		The sample value to be derivated
 **/
double Derivative (int id, double sample);

#endif /*DERIVATIVE_H_*/
