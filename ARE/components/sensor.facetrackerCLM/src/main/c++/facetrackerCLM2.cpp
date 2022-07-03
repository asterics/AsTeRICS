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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#include "facetrackerCLM2.h"
////////////////////////////////////////////////////////////////////////////
#include "facetracker_clm_wrapper_t.h"
////////////////////////////////////////////////////////////////////////////
#include "3rdparty\videoInput\include\videoInput.h"
////////////////////////////////////////////////////////////////////////////
//GLOBALS
/////////////////  JNI / Thread interfacing ////////////////////////////////
namespace {
JavaVM * g_jvm;
jobject g_obj = NULL;//it's a pointer
boost::shared_ptr<upmc::facetracker_clm_wrapper_t> 
	gCLMptr;
}
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
#define jOK static_cast<jint>(1)
#define jFAIL static_cast<jint>(0)
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    activate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_activate
  (JNIEnv * jenv, jobject jobj)
{
	//printf("FacetrackerCLM2Bridge_activate\n");
		//create instance to ASM Wrapper
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();

	jint error_code = jenv->GetJavaVM(&::g_jvm);

	if(error_code != 0) return error_code;

	// explicitly ask for a global reference
	::g_obj = jenv->NewGlobalRef(jobj);

	//set pointer to JVM so that we can attache the thread and invoke the callback
	//from within the running thread.
	::gCLMptr->set_jvm(::g_jvm);
	//Same for the jobject
	::gCLMptr->set_jobj(::g_obj);
	
	return ( (::gCLMptr->activate())? (jOK):(jFAIL) );
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    suspend
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_suspend
  (JNIEnv *, jobject)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	return ( (::gCLMptr->suspend())? (jOK):(jFAIL) );
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    resume
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_resume
  (JNIEnv *, jobject)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	return ( (::gCLMptr->resume())? (jOK):(jFAIL) );
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    deactivate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_deactivate
  (JNIEnv * jenv, jobject )
{
	//printf("facetrackerCLM2_jni_FacetrackerCLM2Bridge_deactivate\n");
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	gCLMptr->deactivate();

	jenv->DeleteGlobalRef(g_obj);
	
	return jOK;
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    showCameraSettings
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_showCameraSettings
  (JNIEnv *, jobject)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	gCLMptr->showCameraSettings();
	return jOK;
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    setReferencePose
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_setReferencePose
  (JNIEnv *, jobject)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	gCLMptr->setReferencePose();
	return jOK;
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    setDisplayPosition
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_setDisplayPosition
  (JNIEnv *, jobject, jint x, jint y, jint width , jint height)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	gCLMptr->adjust_window(cv::Rect(x,y, width, height));
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    getProperty
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_getProperty
  (JNIEnv * env, jobject obj, jstring key)
{
	//printf("facetrackerCLM2_jni_FacetrackerCLM2Bridge_getProperty\n");
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();

	const char *strKey;
	jstring result = NULL;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

	std::ostringstream out;
	//printf("Property Query:\"%s\" \n", strKey); 

	if(strcmp(gCLMptr->modelName.first.c_str(), strKey) == 0)
	{///User Name
		printf("Getting modelName\n"); 
		out << gCLMptr->modelName.second;
		result = env->NewStringUTF(out.str().c_str());
	}
	else if(strcmp(gCLMptr->cameraIndex.first.c_str(), strKey) == 0) //camera index
	{///cameraIndex
		printf("Getting Camera index\n"); 
		out << gCLMptr->cameraIndex.second;
		result = env->NewStringUTF(out.str().c_str());
	}
	else if(strcmp(gCLMptr->cameraResolution.first.c_str(), strKey) == 0) //camera resolution
	{
		printf("Getting Camera resolution\n"); 
		out << (int)gCLMptr->cameraResolution.second;
		result = env->NewStringUTF(out.str().c_str());
	}
	else if(strcmp(gCLMptr->cameraDisplayUpdate.first.c_str(), strKey) == 0) //camera resolution
	{
		printf("Getting Camera Display Update\n"); 
		out << (int)gCLMptr->cameraDisplayUpdate.second;
		result = env->NewStringUTF(out.str().c_str());
	}
	else
	{
		printf("Key \"%s\" was not found\n", strKey); 
	}

    return result;
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    setProperty
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_setProperty
  (JNIEnv * env, jobject, jstring key, jstring value)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();

	const char *strKey;
	const char *strValue;
	jstring result =NULL;
	//
	if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

	if (value == NULL) return NULL; /* OutOfMemoryError already thrown */
	strValue = env->GetStringUTFChars(value, NULL);

	std::ostringstream out;

	//printf("Property Query:\"%s\" \n", strKey); 

	if(strcmp(gCLMptr->modelName.first.c_str(), strKey) == 0)
	{
		out << gCLMptr->modelName.second;
		result = env->NewStringUTF(out.str().c_str());
		gCLMptr->modelName.second.assign(strValue);
		printf("Set value of \"%s\" to: %s\n", strKey, strValue); 
	}
	else if(strcmp(gCLMptr->cameraIndex.first.c_str(), strKey) == 0)
	{
		out << gCLMptr->cameraIndex.second;
		result = env->NewStringUTF(out.str().c_str());
		gCLMptr->cameraIndex.second=atoi(strValue);
		printf("Set value of \"%s\" to: %d\n", strKey, atoi(strValue)); 
	}
	else if(strcmp(gCLMptr->cameraResolution.first.c_str(), strKey) == 0) //camera resolution
	{
		out << (int) gCLMptr->cameraResolution.second;
		result = env->NewStringUTF(out.str().c_str());
		gCLMptr->setResolution(static_cast<upmc::camRes>(atoi(strValue)));
		printf("Set value of \"%s\" to: %d\n", strKey, atoi(strValue)); 
	}
	else if(strcmp(gCLMptr->cameraDisplayUpdate.first.c_str(), strKey) == 0) //camera resolution
	{
		out << (int) gCLMptr->cameraDisplayUpdate.second;
		result = env->NewStringUTF(out.str().c_str());
		gCLMptr->setResolution(static_cast<upmc::camRes>(atoi(strValue)));
		printf("Set value of \"%s\" to: %d\n", strKey, atoi(strValue)); 
	}
 //   //env->ReleaseStringUTFChars(key, strKey);
 //   //env->ReleaseStringUTFChars(value, strValue);
	return result;
}
////////////////////////////////////////////////////////////////////////////
/*
 * Class:     eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerCLM2_jni_FacetrackerCLM2Bridge_reset
  (JNIEnv *, jobject)
{
	gCLMptr=upmc::facetracker_clm_wrapper_t::get();
	
	gCLMptr->initFace();

	gCLMptr->setReferencePose();
}
////////////////////////////////////////////////////////////////////////////