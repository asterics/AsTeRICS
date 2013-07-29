
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
jobject hookObj_ms = NULL;

jmethodID processMouseMove = NULL;
jmethodID processMouseButtons = NULL;
jmethodID processMouseWheel = NULL;
DWORD hookThreadId = 0;
HHOOK hookHandle_ms;

const char * block_eventsKey = "blockEvents";
const char * block_eventsValue = "true";

int block_events=1;

LONG	g_mouseLocX = -1;	// x-location of mouse position
LONG	g_mouseLocY = -1;	// y-location of mouse position

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
//		printf(" C++: Mousehook - DLL_PROCESS_ATTACH.\n");
		hInst = _hInst;
		break;
		default:
		break;
	}

return TRUE;
}




LRESULT CALLBACK MouseTracker(int nCode, WPARAM wParam, LPARAM lParam)
{
	JNIEnv * env;
	int injected=0;

	if (jvm->AttachCurrentThread((void **)&env, NULL) >= 0) 
	{
		
		if (nCode==HC_ACTION) 
		{
//			MOUSEHOOKSTRUCT* pStruct = (MOUSEHOOKSTRUCT*)lParam;
			MSLLHOOKSTRUCT* pStruct = (MSLLHOOKSTRUCT*)lParam;
//			injected=(pStruct->wHitTestCode)&1;
			injected=(pStruct->flags) & LLMHF_INJECTED;

			switch (wParam) {
				case WM_LBUTTONDOWN:
					if (!injected) {
					 buttonstate|=1;
					 env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_LBUTTONUP:
					if (!injected) {
						buttonstate&=~1;
						env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_RBUTTONDOWN:
					if (!injected) {
						buttonstate|=2;
						env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_RBUTTONUP:
					if (!injected) {
						buttonstate&=~2;
						env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_MBUTTONDOWN:
					if (!injected) {
						buttonstate|=4;
						env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_MBUTTONUP:
					if (!injected) {
						buttonstate&=~4;
						env->CallVoidMethod(hookObj_ms, processMouseButtons, (jint)buttonstate);
					}
					break;
				case WM_MOUSEMOVE:
						if (!injected)
						{
							if (block_events) 
								env->CallVoidMethod(hookObj_ms, processMouseMove, 
								      (jint)(pStruct->pt.x-g_mouseLocX),(jint)(pStruct->pt.y-g_mouseLocY));
							else env->CallVoidMethod(hookObj_ms, processMouseMove, 
								      (jint)(pStruct->pt.x), (jint)(pStruct->pt.y));

						}
						else
						{
							g_mouseLocX = pStruct->pt.x;
							g_mouseLocY = pStruct->pt.y;
						}
					break;
				case WM_MOUSEWHEEL:
						if (!injected)
						{
							if ((short) HIWORD(pStruct->mouseData) > 0)
							   env->CallVoidMethod(hookObj_ms, processMouseWheel, (jint)(1));
							else
							   env->CallVoidMethod(hookObj_ms, processMouseWheel, (jint)(-1));
						}
					break;
			}
		}
	}
	else 
	{
		// printf("C++: LowLevelMouseProc - Error on the attach current thread.\n");
	}

	if ((block_events) && (!injected)) return (1);
	return CallNextHookEx(NULL, nCode, wParam, lParam);
}

void get_firstxy(void)
{
	POINT pt;
	GetCursorPos(&pt);

	g_mouseLocX=pt.x;
	g_mouseLocY=pt.y;
}


DWORD WINAPI HookProc(LPVOID lpv)
{
	MSG message;

	hookThreadId = GetCurrentThreadId();

 	hookHandle_ms = SetWindowsHookEx(WH_MOUSE_LL, MouseTracker, hInst, 0);
	if (hookHandle_ms == NULL) 
	{
		// printf("C++: Java_SysHook_registerMouseHook - Hook failed!\n");
		// PrintError();
		return (jint)1;
	}
	else 
	{
		// printf("C++: Java_SysHook_registerMouseHook - Hook successful\n");
	}



	// printf("C++: Mousehook Thread is running\n");

	while (GetMessage(&message, NULL, 0, 0)) 
	{
    	//printf("event!\n");

		TranslateMessage(&message);
		DispatchMessage(&message);
	}
	// printf("C++: Mousehook Thread return\n");
	return(1);
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_mousecapture_jni_Bridge_activate
  (JNIEnv * env, jobject obj)

{
	get_firstxy();
    HOOKTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) HookProc, (LPVOID) NULL, 0, &dwHookStatId);
	hookObj_ms = env->NewGlobalRef(obj);
	jclass cls_ms = env->GetObjectClass(hookObj_ms);
    processMouseMove = env->GetMethodID(cls_ms, "newCoordinates_callback", "(II)V");
    processMouseButtons = env->GetMethodID(cls_ms, "newButtons_callback", "(I)V");
    processMouseWheel = env->GetMethodID(cls_ms, "newWheel_callback", "(I)V");

	env->GetJavaVM(&jvm);
	// printf("C++: Mousehook installed\n");

	//g_kl = kl;
    return (jint)0;
}




//JNIEXPORT void JNICALL Java_SysHook_unRegisterHook(JNIEnv *env, jobject object) 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_mousecapture_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{
	if (hookThreadId == 0)
	return (jint)1;

	if (!UnhookWindowsHookEx(hookHandle_ms))
	{
		// printf("C++: Java_SysHook_registerMouseHook - Unhook failed\n");
	}
	else
	{
		// printf("C++: Java_SysHook_registerMouseHook - Unhook successful\n");
	}

	env->DeleteGlobalRef(hookObj_ms);	
	// printf("C++: Java_SysHook_unRegisterMouseHook - call PostThreadMessage.\n");
	PostThreadMessage(dwHookStatId, WM_QUIT, 0, 0L);
	return (jint)0;
}




///////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_mousecapture_jni_Bridge_getProperty
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
		get_firstxy();
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}

    return result;
}

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_mousecapture_jni_Bridge_setProperty
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

