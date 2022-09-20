
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIderivative.h"
#include "derivative.h"


JNIEXPORT jint JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeDerivativeNew
  (JNIEnv *env, jobject obj)
{
	return Derivative_new();
}

JNIEXPORT jint JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeDerivativeDelete
  (JNIEnv *env, jobject obj, jint id)
{
	return Derivative_delete(id);
}

JNIEXPORT void JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeDerivativeReset
  (JNIEnv *env, jobject obj, jint id)
{
	Derivative_reset(id);
}

JNIEXPORT jint JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeGetSampleFrequency
  (JNIEnv *env, jobject obj, jint id)
{
	return getSampleFrequency(id);
}

JNIEXPORT jint JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeSetSampleFrequency
  (JNIEnv *env, jobject obj, jint id, jint sampleFrequency)
{
	return setSampleFrequency(id, sampleFrequency);
}

JNIEXPORT jdouble JNICALL Java_com_starlab_component_processor_jni_JNIderivative_nativeDerivative
  (JNIEnv *env, jobject obj, jint id, jdouble sample)
{
	return Derivative(id, sample);
}
