
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
#include "TrackIRBridge.h"
#include <jni.h>

#if _WIN64
#define ENV64BIT
#else
#define ENV32BIT
#endif

JavaVM * jvm = NULL;
jobject trackObj_ms = NULL;
HINSTANCE hLib;
jmethodID processData = NULL;


extern "C" 
BOOL APIENTRY DllMain(HINSTANCE _hInst, DWORD reason, LPVOID reserved) 
{
	switch (reason) 
	{
		case DLL_PROCESS_ATTACH:
		printf(" C++: TrackIR - DLL_PROCESS_ATTACH.\n");
		break;
		default:
		break;
	}

return TRUE;
}


typedef struct tagTrackIRSignature
{
	char DllSignature[200];	///< Signature set by DLL once initialized.
	char AppSignature[200];	///< Signature set by TrackIR application once initialized.
} SIGNATUREDATA, * LPTRACKIRSIGNATURE;

typedef struct tagTrackIRData
{
	unsigned short Status;	///< Tells us whether or not the TrackIR camera is in mouse-emulation mode or not
	unsigned short FrameSignature; ///< Incrementing frame number coming from the TrackIR app used to verify if the incoming frame is new. Ranges from 1 to 32766 (will loop)
	unsigned long  IOData;	///< only used for hash key encryption on a few games. It is likely you won't need to use this.
	float Roll;		///< Roll component of head pose in TIR rotation units.
	float Pitch;	///< Pitch component of head pose in TIR rotation units.
	float Yaw;		///< Yaw component of head pose in TIR rotation units	
	float X; ///< X component of head position in TIR units
	float Y; ///< Y component of head position in TIR units	
	float Z; ///< Z component of head position in TIR units
	float reserved1; ///< Unused field 
	float reserved2; ///< Unused field 
	float reserved3; ///< Unused field 
	float reserved4; ///< Unused field
	float reserved5; ///< Unused field
	float reserved6; ///< Unused field
	float reserved7; ///< Unused field
	float reserved8; ///< Unused field
	float reserved9; ///< Unused field

} TRACKIRDATA, * LPTRACKIRDATA;


SIGNATUREDATA pSignature;
TRACKIRDATA pTrackIRData;

typedef int(__stdcall* NP_GETSIGNATURE)(LPTRACKIRSIGNATURE pSignature);
typedef int(__stdcall* NP_REGISTERWINDOWHANDLE)(HWND);
typedef int(__stdcall* NP_UNREGISTERWINDOWHANDLE)(void);
typedef int(__stdcall* NP_REGISTERPROGRAMPROFILEID)(unsigned short);
typedef int(__stdcall* NP_QUERYVERSION)(unsigned short*);
typedef int(__stdcall* NP_REQUESTDATA)(unsigned short);
typedef int(__stdcall* NP_GETSIGNATURE)(LPTRACKIRSIGNATURE);
typedef int(__stdcall* NP_GETDATA)(LPTRACKIRDATA);
typedef int(__stdcall* NP_STARTCURSOR)(void);
typedef int(__stdcall* NP_STOPCURSOR)(void);
typedef int(__stdcall* NP_RECENTER)(void);
typedef int(__stdcall* NP_STARTDATATRANSMISSION)(void);
typedef int(__stdcall* NP_STOPDATATRANSMISSION)(void);

typedef int(__stdcall* Close)();

NP_GETSIGNATURE getSignature = NULL;
NP_REGISTERWINDOWHANDLE registerWindowHandle = NULL;
NP_UNREGISTERWINDOWHANDLE unregisterWindowHandle = NULL;
NP_REGISTERPROGRAMPROFILEID registerProgramProfileID = NULL;
NP_QUERYVERSION queryVersion = NULL;
NP_REQUESTDATA requestData = NULL;
NP_GETDATA getData = NULL;
NP_STARTCURSOR startCursor = NULL;
NP_STOPCURSOR stopCursor = NULL;
NP_RECENTER reCenter = NULL;
NP_STARTDATATRANSMISSION startDataTransmission = NULL;
NP_STOPDATATRANSMISSION stopDataTransmission = NULL;

HWND displayWnd;


LRESULT CALLBACK wndHandler(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	return DefWindowProc(hWnd, message, wParam, lParam);
}


int start_TrackIR() {
	static int needInit = 1;

	if (needInit) {
		needInit = 0;

		HKEY pKey = NULL;
		if (RegOpenKeyEx(HKEY_CURRENT_USER, "Software\\NaturalPoint\\NATURALPOINT\\NPClient Location\\", 0, KEY_READ, &pKey) != ERROR_SUCCESS)
		{
			printf("C++: could not find TrackIR DLL Location\n");
			return(0);
		}

		//get the value from the key
		char libraryPath[300];
		DWORD dwSize;

		//first discover the size of the value
		if (RegQueryValueEx(pKey, "Path", NULL, NULL, NULL, &dwSize) == ERROR_SUCCESS)
		{
			//now get the value
			if (RegQueryValueEx(pKey, "Path", NULL, NULL, reinterpret_cast<LPBYTE>(libraryPath), &dwSize) != ERROR_SUCCESS)
			{
				RegCloseKey(pKey);
				printf("C++: could not read Registry key\n");
				return (0);
			}
		}
		//everything worked
		RegCloseKey(pKey);

#if defined ENV32BIT
		strcat(libraryPath, "NPClient.dll");	// 32bit dll

#endif
#if defined ENV64BIT
		strcat(libraryPath, "NPClient64.dll");	// 64bit dll
#endif

		printf("C++: Found the TrackIR library: %s\n", libraryPath);

		hLib = LoadLibrary(libraryPath);
		if (hLib == NULL)
		{
			printf("C++: Could not load NPClient library\n");
			return (0);
		}

		getSignature = (NP_GETSIGNATURE)GetProcAddress((HMODULE)hLib, "NP_GetSignature");
		if (getSignature == NULL)
		{
			printf("C++: Could not load get TrackIR DLL signature\n");
			return (0);
		}
		int result = (*getSignature)(&pSignature);
		printf("C++: TrackIR DLL Signature found: %s\n", pSignature.DllSignature);

		registerWindowHandle = (NP_REGISTERWINDOWHANDLE)GetProcAddress((HMODULE)hLib, "NP_RegisterWindowHandle");
		unregisterWindowHandle = (NP_UNREGISTERWINDOWHANDLE)GetProcAddress((HMODULE)hLib, "NP_UnregisterWindowHandle");
		registerProgramProfileID = (NP_REGISTERPROGRAMPROFILEID)GetProcAddress((HMODULE)hLib, "NP_RegisterProgramProfileID");
		queryVersion = (NP_QUERYVERSION)GetProcAddress((HMODULE)hLib, "NP_QueryVersion");
		requestData = (NP_REQUESTDATA)GetProcAddress((HMODULE)hLib, "NP_RequestData");
		getData = (NP_GETDATA)GetProcAddress((HMODULE)hLib, "NP_GetData");
		startCursor = (NP_STARTCURSOR)GetProcAddress((HMODULE)hLib, "NP_StartCursor");
		stopCursor = (NP_STOPCURSOR)GetProcAddress((HMODULE)hLib, "NP_StopCursor");
		reCenter = (NP_RECENTER)GetProcAddress((HMODULE)hLib, "NP_ReCenter");
		startDataTransmission = (NP_STARTDATATRANSMISSION)GetProcAddress((HMODULE)hLib, "NP_StartDataTransmission");
		stopDataTransmission = (NP_STOPDATATRANSMISSION)GetProcAddress((HMODULE)hLib, "NP_StopDataTransmission");

		// Register the window class.
		const char CLASS_NAME[] = "DummyWindowClass";
		WNDCLASS wc = { };
		HINSTANCE  hInst = GetModuleHandle(NULL);
		wc.lpfnWndProc = (WNDPROC)wndHandler;
		wc.hInstance = hInst;
		wc.lpszClassName = CLASS_NAME;
		RegisterClass(&wc);

		if (!(displayWnd = CreateWindow(CLASS_NAME, "TrackIRGetData", WS_OVERLAPPEDWINDOW, 0, 0, 0, 0, NULL, NULL, hInst, NULL))) {
			printf("C++: can't create TrackIR Dummy Window\n");
			return(0);
		}
	}

	printf("C++: TrackIR Brigde Calling NP_RegisterWindowHandle\n");
	int result = (*registerWindowHandle)(displayWnd);
	printf("C++: TrackIR Brigde Calling NP_RequestData\n");
	result = (*requestData)(119); //  Request roll, pitch, yawand x, y, z
	printf("C++: TrackIR Brigde Calling NP_RegisterProgramProfileID\n");
	result = (*registerProgramProfileID)(13302);  //  # Note that FreePIE uses id 13302
	// see also: https://github.com/ExtReMLapin/TrackIR_Research/blob/master/decrypted%20sgl.dat
	printf("C++: TrackIR Brigde Calling NP_StopCursor\n");
	result = (*stopCursor)();
	printf("C++: TrackIR Brigde Calling NP_StartDataTransmission\n");
	result = (*startDataTransmission)();

	return (1);
}

int stop_TrackIR() {
	printf("Calling NP_StopDataTransmission\n");
	int result = (*stopDataTransmission)();
	printf("Calling NP_StartCursor\n");
	result = (*startCursor)();
	printf("Calling NP_UnregisterWindowHandle\n");
	result = (*unregisterWindowHandle)();
	return(1);
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_trackir_jni_Bridge_activate
(JNIEnv* env, jobject obj)

{
	start_TrackIR();

	trackObj_ms = env->NewGlobalRef(obj);
	jclass cls = env->GetObjectClass(obj);

    processData = env->GetMethodID(cls, "newCoordinates_callback", "(IIIIII)V");
	if (processData == NULL) printf("C++: callback method not found\n");
	env->GetJavaVM(&jvm);
	printf("C++: TrackIR bridge installed\n");

    return (jint)0;
}

 


JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_trackir_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)

{
	stop_TrackIR();
	return (jint)1;
}


JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_trackir_jni_Bridge_getUpdate
(JNIEnv* env, jobject obj)
{

	memset(&pTrackIRData, 0, sizeof(TRACKIRDATA));
	(*getData)(&pTrackIRData);
	//printf("C++: send test callback\n");
	env->CallVoidMethod(trackObj_ms, processData, (jint)(pTrackIRData.Yaw), (jint)(pTrackIRData.Pitch), (jint)(pTrackIRData.Roll),
												  (jint)(pTrackIRData.X), (jint)(pTrackIRData.Y), (jint)(pTrackIRData.Z));
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_trackir_jni_Bridge_centerCoordinates
(JNIEnv* env, jobject obj)
{
	(*reCenter)();
	return (jint)0;
}


