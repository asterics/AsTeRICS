
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

#include <windows.h>

#include "JoyHook.h"
#include <jni.h>

HINSTANCE hInst = NULL;

JavaVM * jvm = NULL;
jobject hookObj_joystick = NULL;

//jobject g_kl = NULL;


jmethodID processJoystickResult = NULL;
jmethodID mID = NULL;
DWORD hookThreadId = 0;

JOYINFOEX ji;
JOYCAPS jc;


const char * updatePeriodKey = "updatePeriod";
const char * updatePeriodValue = "100";

int updatePeriod=100;

DWORD dwHookStatId;
HANDLE HOOKTHREAD=0;
HANDLE HookExitEvent=0;
jint buttonstate=0;
void PrintError(void); 


extern "C" 
BOOL APIENTRY DllMain(HINSTANCE _hInst, DWORD reason, LPVOID reserved) 
{
	switch (reason) 
	{
		case DLL_PROCESS_ATTACH:
		// printf(" Joyhook-Dll: Keyboardhook - DLL_PROCESS_ATTACH.\n");
		hInst = _hInst;
		break;
		default:
		break;
	}

return TRUE;
}





DWORD WINAPI HookProc(LPVOID lpv)
{
	MSG message;
	int quit=0;

	hookThreadId = GetCurrentThreadId();

	printf("Joyhook-Dll: Joystickhook Thread is running\n");
	while (!quit)
	{	
		ji.dwSize=sizeof(ji);
		ji.dwFlags=JOY_RETURNALL;
		if (joyGetPosEx(0,&ji) == JOYERR_NOERROR)
		{
			// printf("Joyhook-Dll: query joystick successful!\n");
		    // perform JNI callback
			JNIEnv *env;
			if (jvm->AttachCurrentThread((void **)&env, NULL) != 0) printf("Joyhook-Dll:count not attach thread!\n");

			jclass cls = env->GetObjectClass(hookObj_joystick);
			if (cls==0) printf("Joyhook-Dll:could not get class\n");

			jmethodID mid = env->GetMethodID(cls, "newValues_callback", "(IIIIIIII)V");
			if (mid ==0) printf("Joyhook-Dll:could not find method\n");
			else env->CallVoidMethod(hookObj_joystick, mid, (jint) (ji.dwXpos), (jint) (ji.dwYpos), (jint) (ji.dwZpos),
				   (jint) (ji.dwRpos), (jint) (ji.dwUpos), (jint) (ji.dwVpos), (jint) (ji.dwButtons), (jint) (ji.dwPOV) );
		}
		else printf("Joyhook-Dll: Could not query joystick !\n");

		while (PeekMessage( &message, NULL, 0, 0, PM_NOREMOVE ))
		{
		  if (!GetMessage(&message, NULL, 0, 0)) quit=1;
		  else 
		  {
			  TranslateMessage(&message);
		       DispatchMessage(&message);
		  }
		}
		Sleep(updatePeriod);

	}
	hookThreadId = 0;
	printf("Joyhook-Dll: Joystick Thread return\n");
	return(1);
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_joystickcapture_jni_Bridge_activate
  (JNIEnv * env, jobject obj)

{ 
	jint error_code = env->GetJavaVM(&jvm);
	if(error_code != 0)
	{
		printf("Joyhook-Dll: could not get JVM!\n");
		return error_code; 
	}


	if (joyGetNumDevs()>0)
	{
		if (joyGetDevCaps(0,&jc,sizeof(jc)) == JOYERR_NOERROR)
		{

			printf("Joyhook-Dll: Joystick-Name: %s\n",jc.szPname);
			printf("Joyhook-Dll: min / max polling: %d / %d\n",jc.wPeriodMin,jc.wPeriodMax);
			printf("Joyhook-Dll: Number of Axis %d\n",jc.wNumAxes);
			printf("Joyhook-Dll: Number of Buttons %d\n",jc.wNumButtons);
			printf("Joyhook-Dll: has Point-of-Vision: %d\n",(jc.wCaps&JOYCAPS_HASPOV ? 1 : 0) );
			printf("Joyhook-Dll: XMin / Xmax = %d / %d\n",jc.wXmin,jc.wXmax);
			printf("Joyhook-Dll: YMin / Ymax = %d / %d\n",jc.wYmin,jc.wYmax);
			printf("Joyhook-Dll: ZMin / Zmax = %d / %d\n",jc.wZmin,jc.wZmax);
			printf("Joyhook-Dll: RMin / Rmax = %d / %d\n",jc.wRmin,jc.wRmax);
			printf("Joyhook-Dll: UMin / Umax = %d / %d\n",jc.wUmin,jc.wUmax);
			printf("Joyhook-Dll: VMin / Vmax = %d / %d\n",jc.wVmin,jc.wVmax);


			HOOKTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) HookProc, (LPVOID) NULL, 0, &dwHookStatId);

			hookObj_joystick = env->NewGlobalRef(obj);
			jclass cls_joystick = env->GetObjectClass(hookObj_joystick);

			processJoystickResult = env->GetMethodID(cls_joystick, "capabilities_callback", "(IIIIIIIIIIIIIII)V");
			if (processJoystickResult ==0)
			 printf("Joyhook-Dll: capabilities callback not found\n");
			else env->CallVoidMethod(obj, processJoystickResult, (jint) (jc.wNumAxes), (jint) (jc.wNumButtons), (jint) (jc.wCaps&JOYCAPS_HASPOV ? 1 : 0),
				(jint) (jc.wXmin), (jint) (jc.wXmax), (jint) (jc.wYmin), (jint) (jc.wYmax), (jint) (jc.wZmin), (jint) (jc.wZmax),
				(jint) (jc.wRmin), (jint) (jc.wRmax), (jint) (jc.wUmin), (jint) (jc.wUmax), (jint) (jc.wVmin), (jint) (jc.wVmax));

/*			mID = env->GetMethodID(cls_joystick, "newValues_callback", "(IIIIIIII)V");
			if (mID ==0)
			 printf("Joyhook-Dll: newValues callback not found\n");
			else env->CallVoidMethod(obj, mID, (jint) (1), (jint) (2), (jint) (3), 
				(jint) (4), (jint) (5), (jint) (6), (jint) (0), (jint) (9000));
*/
			printf("Joyhook-Dll: Joystickhook installed\n");
			return (jint)0;


		}
		else printf("Joyhook-Dll: Could not retrieve joystick capabilities\n");
	}
	else printf("Joyhook-Dll: No Joystick found\n");

	return (jint)1;
}




//JNIEXPORT void JNICALL Java_SysHook_unRegisterHook(JNIEnv *env, jobject object) 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_joystickcapture_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{
	if (hookThreadId == 0)
	return (jint)1;

	// printf("Joyhook-Dll: Java_SysHook_unRegisterKeyboardHook - call PostThreadMessage.\n");
	PostThreadMessage(dwHookStatId, WM_QUIT, 0, 0L);
	while (hookThreadId !=0) Sleep(10);

	env->DeleteGlobalRef(hookObj_joystick);
	
	return (jint)0;
}




///////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_joystickcapture_jni_Bridge_getProperty
  (JNIEnv *env, jobject obj, jstring key)
{
	const char *strKey;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

	/* printf("Getting property with key: %s\n", strKey); */
    if(strcmp(updatePeriodKey, strKey) == 0)
	{
		// printf("Joyhook-Dll, getproperty: Key \"%s\" was found", strKey);

		// printf("The value of key \"%s\" is: %s", strKey, block_eventsValue);
		result = env->NewStringUTF(updatePeriodValue);
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}

    return result;
}


JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_joystickcapture_jni_Bridge_setProperty
  (JNIEnv *env, jobject obj, jstring key, jstring value)
{
	const char *strKey;
	const char *strValue;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

    if (value == NULL) return NULL; /* OutOfMemoryError already thrown */
	strValue = env->GetStringUTFChars(value, NULL);

	//printf("Setting property %s to %s\n", strKey,strValue); 
    if(strcmp( updatePeriodKey, strKey) == 0)
	{
		result = env->NewStringUTF(updatePeriodValue);
		updatePeriodValue  = env->GetStringUTFChars(value, NULL);
		updatePeriod=atoi(updatePeriodValue);
		if (updatePeriod<10) updatePeriod=10;
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}
    return result;
}


void PrintError(void) 
{ 
    // Retrieve the system error message for the last-error code

    LPVOID lpMsgBuf;
    DWORD dw = GetLastError(); 

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER | 
        FORMAT_MESSAGE_FROM_SYSTEM |
        FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        dw,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (LPTSTR) &lpMsgBuf,
        0, NULL );


	// printf("\nERROR: %s\n", lpMsgBuf); 
    LocalFree(lpMsgBuf);
}

