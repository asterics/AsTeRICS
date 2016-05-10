
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
#define OEMRESOURCE
#include <windows.h>
#include "mouseicon.h"
#include <jni.h>

jobject jniObj = NULL;
JavaVM * jvm = NULL;

HINSTANCE hinst;                 // handle to current instance  
HCURSOR hCurs; //, hCurs2,hCurs3;  // cursor handles 


JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_mousecursoricon_MouseCursorIconInstance_initCursor
  (JNIEnv * env, jobject obj)

{
	 jint error_code = 0;
	 error_code = env->GetJavaVM(&jvm);

	  if(error_code != 0)
	  {
		   printf("GetJavaVM failed\n"); return(0);
	  }
	  jniObj = env->NewGlobalRef(obj);

	  printf("MouseCursor Init\n");
   	  hCurs=CopyCursor(LoadCursor(NULL,( LPCTSTR) MAKEINTRESOURCE(IDC_ARROW)));   // GetCursor());

	  return (jint)1;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_mousecursoricon_MouseCursorIconInstance_setCursor
 (JNIEnv *env, jobject obj, jstring filename)
  
{
//	static int i=0;
	const char *strFilename;
    if (filename == NULL) return (jint)1; /* OutOfMemoryError already thrown*/
	strFilename = env->GetStringUTFChars(filename, NULL);


	//  SetSystemCursor(LoadCursor(hInst,( LPCTSTR) MAKEINTRESOURCE(IDC_CURSOR1+cpos)), OCR_NORMAL);
	
	// printf("Setting CursorIcon to %s\n",strFilename);

	SetSystemCursor(LoadCursorFromFile(strFilename),OCR_NORMAL);
	
	// SetSystemCursor(LoadCursor(NULL,( LPCTSTR) MAKEINTRESOURCE(IDC_ARROW)), OCR_NORMAL);	
	// SetSystemCursor(hCurs,OCR_NORMAL);
	return (jint)0;
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_mousecursoricon_MouseCursorIconInstance_exitCursor
  (JNIEnv * env, jobject obj)
{


	  SetSystemCursor(hCurs,OCR_NORMAL);
	  env->DeleteGlobalRef(jniObj);
	  return (jint)0;
}



