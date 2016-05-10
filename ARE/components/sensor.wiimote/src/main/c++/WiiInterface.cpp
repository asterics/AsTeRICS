
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
#include "WiiYourself\wiimote.h"
#include "WiiInterface.h"
#include <jni.h>

HINSTANCE hInst = NULL;

JavaVM * jvm = NULL;
jobject hookObj_wiimote = NULL;

//jobject g_kl = NULL;


jmethodID processWiimoteResult = NULL;
jmethodID mID = NULL;
DWORD hookThreadId = 0;


const char * updatePeriodKey = "updatePeriod";
const char * updatePeriodValue = "5";

int updatePeriod=5;

DWORD dwHookStatId;
HANDLE HOOKTHREAD=0;
HANDLE HookExitEvent=0;
jint buttonstate=0;
void PrintError(void); 

struct {
	int Pitch;
	int Roll;
	int Point1X;
	int Point1Y;
	int Point2X;
	int Point2Y;
	int NunX;
	int NunY;
	int Battery;
	int Buttonstate;
} wm;


extern "C" 
BOOL APIENTRY DllMain(HINSTANCE _hInst, DWORD reason, LPVOID reserved) 
{
	switch (reason) 
	{
		case DLL_PROCESS_ATTACH:
		// printf(" WiiInterface - DLL_PROCESS_ATTACH.\n");
		hInst = _hInst;
		break;
		default:
		break;
	}

return TRUE;
}


// ------------------------------------------------------------------------------------
//  state-change callback example (we use polling for everything else):
// ------------------------------------------------------------------------------------
void on_state_change (wiimote			  &remote,
					  state_change_flags  changed,
					  const wiimote_state &new_state)
	{
	// we use this callback to set report types etc. to respond to key events
	//  (like the wiimote connecting or extensions (dis)connecting).
	
	// NOTE: don't access the public state from the 'remote' object here, as it will
	//		  be out-of-date (it's only updated via RefreshState() calls, and these
	//		  are reserved for the main application so it can be sure the values
	//		  stay consistent between calls).  Instead query 'new_state' only.

	// the wiimote just connected
	if(changed & CONNECTED)
		{
		// ask the wiimote to report everything (using the 'non-continous updates'
		//  default mode - updates will be frequent anyway due to the acceleration/IR
		//  values changing):

		// note1: you don't need to set a report type for Balance Boards - the
		//		   library does it automatically.
		
		// note2: for wiimotes, the report mode that includes the extension data
		//		   unfortunately only reports the 'BASIC' IR info (ie. no dot sizes),
		//		   so let's choose the best mode based on the extension status:
		if(new_state.ExtensionType != wiimote::BALANCE_BOARD)
			{
			//if(new_state.bExtension)
			//	remote.SetReportType(wiimote::IN_BUTTONS_ACCEL_IR_EXT); // no IR dots
			//else
				remote.SetReportType(wiimote::IN_BUTTONS_ACCEL_IR);		//    IR dots
			}
	
	}
	// a MotionPlus was detected
	if(changed & MOTIONPLUS_DETECTED)
		{
		// enable it if there isn't a normal extension plugged into it
		// (MotionPlus devices don't report like normal extensions until
		//  enabled - and then, other extensions attached to it will no longer be
		//  reported (so disable the M+ when you want to access them again).
		if(remote.ExtensionType == wiimote_state::NONE) {
			bool res = remote.EnableMotionPlus();
			_ASSERT(res);
			}
		}
	// an extension is connected to the MotionPlus
	else if(changed & MOTIONPLUS_EXTENSION_CONNECTED)
		{
		// We can't read it if the MotionPlus is currently enabled, so disable it:
		if(remote.MotionPlusEnabled())
			remote.DisableMotionPlus();
		}
	// an extension disconnected from the MotionPlus
	else if(changed & MOTIONPLUS_EXTENSION_DISCONNECTED)
		{
		// enable the MotionPlus data again:
		if(remote.MotionPlusConnected())
			remote.EnableMotionPlus();
		}
	// another extension was just connected:
	else if(changed & EXTENSION_CONNECTED)
		{
#ifdef USE_BEEPS_AND_DELAYS
		Beep(1000, 200);
#endif
		// switch to a report mode that includes the extension data (we will
		//  loose the IR dot sizes)
		// note: there is no need to set report types for a Balance Board.
		if(!remote.IsBalanceBoard())
			remote.SetReportType(wiimote::IN_BUTTONS_ACCEL_IR_EXT);
		}
	// extension was just disconnected:
	else if(changed & EXTENSION_DISCONNECTED)
		{
#ifdef USE_BEEPS_AND_DELAYS
		Beep(200, 300);
#endif
		// use a non-extension report mode (this gives us back the IR dot sizes)
		remote.SetReportType(wiimote::IN_BUTTONS_ACCEL_IR);
		}
	}


DWORD WINAPI HookProc(LPVOID lpv)
{
	MSG message;
	int quit=0;
	static const TCHAR* wait_str[] = { _T(".  "), _T(".. "), _T("...") };
	unsigned count = 0;

	hookThreadId = GetCurrentThreadId();


	// printf("Wiimote-Dll: query wiimote successful!\n");
	// perform JNI callback
	JNIEnv *env;
	if (jvm->AttachCurrentThread((void **)&env, NULL) != 0) printf("WiiMote-Dll:count not attach thread!\n");

	jclass cls = env->GetObjectClass(hookObj_wiimote);
	if (cls==0) printf("Wiimote-Dll:could not get class\n");

	jmethodID mid = env->GetMethodID(cls, "newValues_callback", "(IIIIIIIIII)V");
	
	// create a wiimote object
	wiimote remote;
	
	// in this demo we use a state-change callback to get notified of
	//  extension-related events, and polling for everything else
	// (note you don't have to use both, use whatever suits your app):
	remote.ChangedCallback		= on_state_change;
	//  notify us only when the wiimote connected sucessfully, or something
	//   related to extensions changes
	remote.CallbackTriggerFlags = (state_change_flags)(CONNECTED |  EXTENSION_CHANGED | MOTIONPLUS_CHANGED);

	printf("WiiMote-Dll: MiiMote Thread is running\n");


reconnect:
	while((!remote.Connect(wiimote::FIRST_AVAILABLE)) && (!quit)) {
		_tprintf(_T("\b\b\b\b%s "), wait_str[count%3]);
		count++;
		#ifdef USE_BEEPS_AND_DELAYS
			Beep(500, 30); Sleep(1000);
		#endif
		Sleep(500);
		while (PeekMessage( &message, NULL, 0, 0, PM_NOREMOVE ))
		{
		  if (!GetMessage(&message, NULL, 0, 0)) quit=1;
		  else 
		  {
			  TranslateMessage(&message);
		       DispatchMessage(&message);
		  }
		}
	}

	if (!quit) 
	{
		remote.SetLEDs(0x0f);
		printf("WiiMote-Dll: 1 Wiimote found.\n");
	}

	while (!quit)
	{	
  	    while(remote.RefreshState() == NO_CHANGE)
			Sleep(updatePeriod); // // don't hog the CPU if nothing changed


		if(remote.ConnectionLost())
		{
			Sleep(2000);
			printf("\nWiiMote Connection lost, reconnecting ...\n");
			goto reconnect;
		}

		//remote.SetRumble(remote.Button.B());

		wm.Buttonstate=remote.Button.Bits;
		wm.Battery=remote.BatteryPercent;

		wm.Pitch=(int)remote.Acceleration.Orientation.Pitch;
		wm.Roll=(int)remote.Acceleration.Orientation.Roll;

		wm.Point1X=remote.IR.Dot[0].RawX;
		wm.Point1Y=remote.IR.Dot[0].RawY;
		wm.Point2X=remote.IR.Dot[1].RawX;
		wm.Point2Y=remote.IR.Dot[1].RawY;

		if (remote.ExtensionType==wiimote_state::NUNCHUK)
		{
			wm.NunX=(int)(remote.Nunchuk.Joystick.X*1024);
			wm.NunY=(int)(remote.Nunchuk.Joystick.Y*1024);
			if (remote.Nunchuk.C) wm.Buttonstate |= 0x10000;
			if (remote.Nunchuk.Z) wm.Buttonstate |= 0x20000;
		}
		if (mid) env->CallVoidMethod(hookObj_wiimote, mid, (jint) (wm.Pitch), (jint) (wm.Roll), (jint) (wm.Point1X),
				(jint) (wm.Point1Y), (jint) (wm.Point2X), (jint) (wm.Point2Y), (jint) (wm.NunX), (jint) (wm.NunY), (jint) (wm.Battery), (jint) (wm.Buttonstate) );

		while (PeekMessage( &message, NULL, 0, 0, PM_NOREMOVE ))
		{
		  if (!GetMessage(&message, NULL, 0, 0)) quit=1;
		  else 
		  {
			  TranslateMessage(&message);
		       DispatchMessage(&message);
		  }
		}

	}
	remote.Disconnect();
	Sleep(10);
	hookThreadId = 0;
	printf("Wiimote-Dll: Wiimote Thread return\n");
	return(1);
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_wiimote_jni_Bridge_activate
  (JNIEnv * env, jobject obj)

{ 
	jint error_code = env->GetJavaVM(&jvm);
	if(error_code != 0)
	{
		printf("Wiimote-Dll: could not get JVM!\n");
		return error_code; 
	}

	wm.Pitch=0;
	wm.Roll=0;
	wm.Point1X=0;
	wm.Point1Y=0;
	wm.Point2X=0;
	wm.Point2Y=0;
	wm.NunX=0;
	wm.NunY=0;
	wm.Battery=0;
	wm.Buttonstate=0;

	HOOKTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) HookProc, (LPVOID) NULL, 0, &dwHookStatId);
	hookObj_wiimote = env->NewGlobalRef(obj);
	jclass cls_joystick = env->GetObjectClass(hookObj_wiimote);

	printf("WiiMote-Thread installed, trying to connect ...\n");
	return (jint)0;
}




//JNIEXPORT void JNICALL Java_SysHook_unRegisterHook(JNIEnv *env, jobject object) 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_wiimote_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{
	if (hookThreadId == 0)
	return (jint)1;

	// printf("Joyhook-Dll: Java_SysHook_unRegisterKeyboardHook - call PostThreadMessage.\n");
	PostThreadMessage(dwHookStatId, WM_QUIT, 0, 0L);
	while (hookThreadId !=0) Sleep(10);

	env->DeleteGlobalRef(hookObj_wiimote);	
	return (jint)0;
}




///////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_wiimote_jni_Bridge_getProperty
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


JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_wiimote_jni_Bridge_setProperty
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

