
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


/*
 * This is an example that demonstrates how to connect to the EyeX Engine and subscribe to the lightly filtered gaze data stream.
 *
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 */

#include <Windows.h>
#include <stdio.h>
#include <conio.h>
#include <assert.h>
#include "include\eyex\EyeX.h"

#include "EyeX.h"
#include <jni.h>

#pragma comment (lib, "Tobii.EyeX.Client.lib")


HINSTANCE hInst = NULL;

JavaVM * jvm = NULL;
jobject hookObj = NULL;
//jobject g_kl = NULL;


jmethodID newEyeDataProc = NULL;
DWORD hookThreadId = 0;
HHOOK hookHandle;

const char * block_eventsKey = "blockEvents";
const char * block_eventsValue = "true";

int block_events=1;

DWORD dwHookStatId;
HANDLE HOOKTHREAD=0;
HANDLE HookExitEvent=0;
jint buttonstate=0;
void PrintError(void); 

float leftEyeX=0;
float leftEyeY=0;

// ID of the global interactors that provide our data streams; must be unique within the application.
static const TX_STRING Interactor1Id = "AsTeRICS eyeX gazePos";
static const TX_STRING Interactor2Id = "AsTeRICS eyeX eyePos";

// global variables
static TX_HANDLE g_hGlobalInteractor1Snapshot = TX_EMPTY_HANDLE;
static TX_HANDLE g_hGlobalInteractor2Snapshot = TX_EMPTY_HANDLE;

/*
 * Initializes g_hGlobalInteractorSnapshot with an interactor that has the Gaze Point behavior.
 */
BOOL InitializeGlobalInteractorSnapshot1(TX_CONTEXTHANDLE hContext)
{
	TX_HANDLE hInteractor = TX_EMPTY_HANDLE;
	TX_HANDLE hBehavior   = TX_EMPTY_HANDLE;
	TX_GAZEPOINTDATAPARAMS params = { TX_GAZEPOINTDATAMODE_LIGHTLYFILTERED };
	BOOL success;

	success = txCreateGlobalInteractorSnapshot(
		hContext,
		Interactor1Id,
		&g_hGlobalInteractor1Snapshot,
		&hInteractor) == TX_RESULT_OK;
//	success &= txCreateInteractorBehavior(hInteractor, &hBehavior, TX_INTERACTIONBEHAVIORTYPE_GAZEPOINTDATA) == TX_RESULT_OK;
//	success &= txSetGazePointDataBehaviorParams(hBehavior, &params) == TX_RESULT_OK;

		success &= txCreateGazePointDataBehavior(hInteractor, &params) == TX_RESULT_OK;
	txReleaseObject(&hBehavior);
	txReleaseObject(&hInteractor);

	return success;
}
BOOL InitializeGlobalInteractorSnapshot2(TX_CONTEXTHANDLE hContext)
{
	TX_HANDLE hInteractor = TX_EMPTY_HANDLE;
	TX_HANDLE hBehavior   = TX_EMPTY_HANDLE;
	BOOL success;

	success = txCreateGlobalInteractorSnapshot(
		hContext,
		Interactor2Id,
		&g_hGlobalInteractor2Snapshot,
		&hInteractor) == TX_RESULT_OK;
	    success &= txCreateInteractorBehavior(hInteractor, &hBehavior, TX_BEHAVIORTYPE_EYEPOSITIONDATA) == TX_RESULT_OK;

	txReleaseObject(&hBehavior);
	txReleaseObject(&hInteractor);

	return success;
}

/*
 * Callback function invoked when a snapshot has been committed.
 */
void TX_CALLCONVENTION OnSnapshotCommitted(TX_CONSTHANDLE hAsyncData, TX_USERPARAM param)
{
	// check the result code using an assertion.
	// this will catch validation errors and runtime errors in debug builds. in release builds it won't do anything.

	TX_RESULT result = TX_RESULT_UNKNOWN;
	txGetAsyncDataResultCode(hAsyncData, &result);
	assert(result == TX_RESULT_OK || result == TX_RESULT_CANCELLED);
}

/*
 * Callback function invoked when the status of the connection to the EyeX Engine has changed.
 */
void TX_CALLCONVENTION OnEngineConnectionStateChanged(TX_CONNECTIONSTATE connectionState, TX_USERPARAM userParam)
{
	switch (connectionState) {
	case TX_CONNECTIONSTATE_CONNECTED: {
			BOOL success;
			printf("The connection state is now CONNECTED (We are connected to the EyeX Engine)\n");
			// commit the snapshot with the global interactor as soon as the connection to the engine is established.
			// (it cannot be done earlier because committing means "send to the engine".)
			success = txCommitSnapshotAsync(g_hGlobalInteractor1Snapshot, OnSnapshotCommitted, NULL) == TX_RESULT_OK;
			success &= txCommitSnapshotAsync(g_hGlobalInteractor2Snapshot, OnSnapshotCommitted, NULL) == TX_RESULT_OK;
			if (!success) {
				printf("Failed to initialize the data stream.\n");
			}
			else
			{
				printf("Waiting for gaze data to start streaming...\n");
			}
		}
		break;

	case TX_CONNECTIONSTATE_DISCONNECTED:
		printf("The connection state is now DISCONNECTED (We are disconnected from the EyeX Engine)\n");
		break;

	case TX_CONNECTIONSTATE_TRYINGTOCONNECT:
		printf("The connection state is now TRYINGTOCONNECT (We are trying to connect to the EyeX Engine)\n");
		break;

	case TX_CONNECTIONSTATE_SERVERVERSIONTOOLOW:
		printf("The connection state is now SERVER_VERSION_TOO_LOW: this application requires a more recent version of the EyeX Engine to run.\n");
		break;

	case TX_CONNECTIONSTATE_SERVERVERSIONTOOHIGH:
		printf("The connection state is now SERVER_VERSION_TOO_HIGH: this application requires an older version of the EyeX Engine to run.\n");
		break;
	}
}



/*
 * Handles an event from the Gaze Point data stream.
 */
void OnGazeDataEvent(TX_HANDLE hGazeDataBehavior)
{
	JNIEnv * env;

	TX_GAZEPOINTDATAEVENTPARAMS eventParams;
	if (txGetGazePointDataEventParams(hGazeDataBehavior, &eventParams) == TX_RESULT_OK) {
		// printf("Gaze Data: (%.1f, %.1f) timestamp %.0f ms\n", eventParams.X, eventParams.Y, eventParams.Timestamp);
		if (jvm->AttachCurrentThread((void **)&env, NULL) >= 0) 
		{
     			env->CallVoidMethod(hookObj, newEyeDataProc, (jboolean)TRUE, (jint)eventParams.X,(jint)eventParams.Y,(jint)leftEyeX,(jint)leftEyeY);
		}
	} else {
		printf("Failed to interpret gaze data event packet.\n");
	}
}

/*
 * Handles an event from the Eye Position data stream.
 */
void OnEyePositionDataEvent(TX_HANDLE hGazeDataBehavior)
{
	TX_EYEPOSITIONDATAEVENTPARAMS eventParams;
	if (txGetEyePositionDataEventParams(hGazeDataBehavior, &eventParams) == TX_RESULT_OK) {
		//printf("Eye Position Data: (%.1f, %.1f) timestamp %.0f ms\n", eventParams.LeftEyeX, eventParams.LeftEyeY, eventParams.Timestamp);
		leftEyeX=eventParams.LeftEyeX*10;
		leftEyeY=eventParams.LeftEyeY*10;
	} else {
		printf("Failed to interpret eye postion data event packet.\n");
	}
}

/*
 * Callback function invoked when an event has been received from the EyeX Engine.
 */
void TX_CALLCONVENTION HandleEvent(TX_CONSTHANDLE hAsyncData, TX_USERPARAM userParam)
{
	TX_HANDLE hEvent = TX_EMPTY_HANDLE;
	TX_HANDLE hBehavior = TX_EMPTY_HANDLE;

    txGetAsyncDataContent(hAsyncData, &hEvent);

	// NOTE. Uncomment the following line of code to view the event object. The same function can be used with any interaction object.
	OutputDebugStringA(txDebugObject(hEvent));


	//if (txGetEventBehavior(hEvent, &hBehavior, TX_INTERACTIONBEHAVIORTYPE_GAZEPOINTDATA) == TX_RESULT_OK) {
	if (txGetEventBehavior(hEvent, &hBehavior, TX_BEHAVIORTYPE_GAZEPOINTDATA) == TX_RESULT_OK) {

		OnGazeDataEvent(hBehavior);
		txReleaseObject(&hBehavior);
	}
	else //if (txGetEventBehavior(hEvent, &hBehavior, TX_INTERACTIONBEHAVIORTYPE_EYEPOSITIONDATA) == TX_RESULT_OK) {
		if (txGetEventBehavior(hEvent, &hBehavior, TX_BEHAVIORTYPE_EYEPOSITIONDATA) == TX_RESULT_OK) {
		OnEyePositionDataEvent(hBehavior);
		txReleaseObject(&hBehavior);
	}

	// NOTE since this is a very simple application with a single interactor and a single data stream, 
	// our event handling code can be very simple too. A more complex application would typically have to 
	// check for multiple behaviors and route events based on interactor IDs.

	txReleaseObject(&hEvent);
}


	TX_CONTEXTHANDLE hContext = TX_EMPTY_HANDLE;
	TX_TICKET hConnectionStateChangedTicket = TX_INVALID_TICKET;
	TX_TICKET hEventHandlerTicket = TX_INVALID_TICKET;
	BOOL success;

/*
 * Application entry point.
 */
int startEyeX(void)
{
	// initialize and enable the context that is our link to the EyeX Engine.
	success = txInitializeEyeX(TX_EYEXCOMPONENTOVERRIDEFLAG_NONE, NULL, NULL, NULL, NULL) == TX_RESULT_OK;
	success &= txCreateContext(&hContext, TX_FALSE) == TX_RESULT_OK;
	success &= InitializeGlobalInteractorSnapshot1(hContext);
	success &= InitializeGlobalInteractorSnapshot2(hContext);
	success &= txRegisterConnectionStateChangedHandler(hContext, &hConnectionStateChangedTicket, OnEngineConnectionStateChanged, NULL) == TX_RESULT_OK;
	success &= txRegisterEventHandler(hContext, &hEventHandlerTicket, HandleEvent, NULL) == TX_RESULT_OK;
	success &= txEnableConnection(hContext) == TX_RESULT_OK;

	// let the events flow until a key is pressed.
	if (success) {
		printf("Initialization was successful.\n");
	} else {
		printf("Initialization failed.\n");
	}
	printf("running...\n");
//	_getch();
	return 0;
}

int stopEyeX(void)
{
	printf("Exiting.\n");

	// disable and delete the context.
	txDisableConnection(hContext);
	txReleaseObject(&g_hGlobalInteractor1Snapshot);
	txReleaseObject(&g_hGlobalInteractor2Snapshot);
	txShutdownContext(hContext, TX_CLEANUPTIMEOUT_DEFAULT, TX_FALSE);
	txReleaseContext(&hContext);

	return 0;
}



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


JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyex_jni_Bridge_activate
  (JNIEnv * env, jobject obj)

{
	hookObj = env->NewGlobalRef(obj);
	jclass cls = env->GetObjectClass(hookObj);
    newEyeDataProc = env->GetMethodID(cls, "newEyeData_callback", "(ZIIII)V");

	env->GetJavaVM(&jvm);

	startEyeX();

	printf("C++: EyeX activate called\n");
    return (jint)0;
}




JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyex_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{

	stopEyeX();

	if (hookThreadId == 0)
	return (jint)1;
	
	env->DeleteGlobalRef(hookObj);
	
	// printf("C++: Java_SysHook_unRegisterKeyboardHook - call PostThreadMessage.\n");
	PostThreadMessage(dwHookStatId, WM_QUIT, 0, 0L);
	return (jint)0;
}




///////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_eyex_jni_Bridge_getProperty
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

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_eyex_jni_Bridge_setProperty
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

