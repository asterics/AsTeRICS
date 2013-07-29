
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

#include <windows.h>
#include "SysHook.h"
#include <jni.h>

HINSTANCE hInst = NULL;

JavaVM * jvm = NULL;
jobject hookObj_kb = NULL;
//jobject g_kl = NULL;


jmethodID processKey = NULL;
DWORD hookThreadId = 0;
HHOOK hookHandle_kb;

const char * block_eventsKey = "blockEvents";
const char * block_eventsValue = "true";

int block_events=1;

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
		// printf(" C++: Keyboardhook - DLL_PROCESS_ATTACH.\n");
		hInst = _hInst;
		break;
		default:
		break;
	}

return TRUE;
}



LRESULT CALLBACK KeyTracker(int nCode, WPARAM wParam, LPARAM lParam) 
{
	JNIEnv * env;
	KBDLLHOOKSTRUCT * p = (KBDLLHOOKSTRUCT *)lParam;
	int injected=0;
	
	if (jvm->AttachCurrentThread((void **)&env, NULL) >= 0) 
	{
		injected=(p->flags)&1;

		switch (wParam) 
		{
			case WM_KEYDOWN:
			case WM_SYSKEYDOWN:
     		env->CallVoidMethod(hookObj_kb, processKey, (jboolean)TRUE, p->vkCode);
			break;
			case WM_KEYUP:
			case WM_SYSKEYUP:
            env->CallVoidMethod(hookObj_kb, processKey, (jboolean)FALSE, p->vkCode);
			break;
			default:
			break;
		}
	}
	else 
	{
		// printf("C++: LowLevelKeyboardProc - Error on the attach current thread.\n");
	}
	
	if ((block_events) && (!injected)) return (1);
	return CallNextHookEx(NULL, nCode, wParam, lParam);
}




DWORD WINAPI HookProc(LPVOID lpv)
{
	MSG message;

	hookThreadId = GetCurrentThreadId();

 	hookHandle_kb = SetWindowsHookEx(WH_KEYBOARD_LL, KeyTracker, hInst, 0);
	if (hookHandle_kb == NULL) 
	{
		// printf("C++: Java_SysHook_registerKeyboardHook - Hook failed!\n");
		// PrintError();
		return (jint)1;
	}
	else 
	{
		// printf("C++: Java_SysHook_registerKeyboardHook - Hook successful\n");
	}

	// printf("C++: Keyboardhook Thread is running\n");
	while (GetMessage(&message, NULL, 0, 0)) 
	{
    	// printf("event!\n");

		TranslateMessage(&message);
		DispatchMessage(&message);
	}
	// printf("C++: Keyboardhook Thread return\n");
	return(1);
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_keyboardcapture_jni_Bridge_activate
  (JNIEnv * env, jobject obj)

{
    HOOKTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) HookProc, (LPVOID) NULL, 0, &dwHookStatId);

	hookObj_kb = env->NewGlobalRef(obj);
	jclass cls_kb = env->GetObjectClass(hookObj_kb);
    processKey = env->GetMethodID(cls_kb, "newKey_callback", "(ZI)V");

	env->GetJavaVM(&jvm);
	// printf("C++: Keyboardhook installed\n");
    return (jint)0;
}




//JNIEXPORT void JNICALL Java_SysHook_unRegisterHook(JNIEnv *env, jobject object) 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_keyboardcapture_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{
	if (hookThreadId == 0)
	return (jint)1;

	if (!UnhookWindowsHookEx(hookHandle_kb))
	{
		// printf("C++: Java_SysHook_registerKeyboardHook - Unhook failed\n");
	}
	else
	{
		// printf("C++: Java_SysHook_registerKeyboardHook - Unhook successful\n");
	}

	

	env->DeleteGlobalRef(hookObj_kb);
	
	// printf("C++: Java_SysHook_unRegisterKeyboardHook - call PostThreadMessage.\n");
	PostThreadMessage(dwHookStatId, WM_QUIT, 0, 0L);
	return (jint)0;
}




///////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_keyboardcapture_jni_Bridge_getProperty
  (JNIEnv *env, jobject obj, jstring key)
{
	const char *strKey;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

	/* printf("Getting property with key: %s\n", strKey); */
    if(strcmp(block_eventsKey, strKey) == 0)
	{
		/* printf("The value of key \"%s\" is: %s", strKey, block_eventsValue); */
		result = env->NewStringUTF(block_eventsValue);
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}

    return result;
}

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_keyboardcapture_jni_Bridge_setProperty
  (JNIEnv *env, jobject obj, jstring key, jstring value)
{
	const char *strKey;
	const char *strValue;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

    if (value == NULL) return NULL; /* OutOfMemoryError already thrown */
	strValue = env->GetStringUTFChars(value, NULL);

	/* printf("Getting property with key: %s\n", strKey); */
    if(strcmp( block_eventsKey, strKey) == 0)
	{
		result = env->NewStringUTF(block_eventsValue);
//		strcpy (block_eventsValue,strValue);
		block_eventsValue=strValue;
		// printf("The value of key \"%s\" was set to: %s\n", strKey,  block_eventsValue); 
		if ((strcmp(block_eventsValue, "True") == 0)||(strcmp(block_eventsValue, "true") == 0))
			block_events=1; else block_events=0;
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}

    return result;

    //env->ReleaseStringUTFChars(key, strKey);
    //env->ReleaseStringUTFChars(value, strValue);
	//return result;
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

