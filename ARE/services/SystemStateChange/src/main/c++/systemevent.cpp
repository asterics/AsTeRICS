
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
#include "systemevent.h"
#include <jni.h>
#include <Dbt.h>

jobject jniObj = NULL;
JavaVM * jvm = NULL;

int usb_events=0;

DWORD dwSystemStatId;
HANDLE SYSTEMTHREAD=0;
HANDLE SystemExitEvent=0;

#define EVENT_REQUEST_SLEEP 0
#define EVENT_SLEEP 1
#define EVENT_WAKE 2
#define EVENT_USB_ATTACH 10
#define EVENT_USB_DETACH 11

void Callback (int eventType)
{
			JNIEnv *env;
			jvm->AttachCurrentThread((void **)&env, NULL);
			jclass cls = env->GetObjectClass(jniObj);
			jmethodID mid = env->GetMethodID(cls, "systemEventCallback", "(I)V");
			if (mid!=NULL)	env->CallVoidMethod(jniObj, mid, (jint) eventType);
			else printf("systemEventCallback method not found\n");
}

	
LRESULT CALLBACK WndHandler(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
    
	switch( message ) 
	{
		case WM_POWERBROADCAST:
			if (wParam == PBT_APMSUSPEND) {

				// printf("\nDetect Sleep mode at second: %ld\n",GetTickCount()/1000);
				Callback(EVENT_SLEEP);
			}

			if (wParam == PBT_APMRESUMESUSPEND) {
				// printf("\nResume Event Occured at second %ld\n",GetTickCount()/1000);
				Callback(EVENT_WAKE);
			}
			break;

		case WM_DEVICECHANGE:
		{
			PDEV_BROADCAST_HDR pHdr = (PDEV_BROADCAST_HDR) lParam;
			switch (wParam)
			{
				case DBT_DEVICEARRIVAL:
					Callback(EVENT_USB_ATTACH);
					usb_events++;
					// printf("A device has been inserted.\n");
				break;
				case DBT_DEVICEREMOVECOMPLETE:
					Callback(EVENT_USB_DETACH);
					usb_events+=256;
					// printf("A device has been removed.\n");
				break;
			}
		}
		break;

		case WM_DESTROY:
			PostQuitMessage( 0 );
			break;

		default:
			return DefWindowProc( hWnd, message, wParam, lParam );
   }
   return 0;
}




DWORD WINAPI systemThread(LPVOID p)
{
    HANDLE     hArray[1];
	BOOL SystemThreadDone=FALSE;
	hArray[0] = SystemExitEvent;

	DWORD err = 0, dwRes = 0;
    MSG  msg;
    HWND hWnd;
    HINSTANCE hInst = (HINSTANCE) p;

    WNDCLASS w;
    memset(&w,0,sizeof(WNDCLASS));
    w.style = CS_HREDRAW | CS_VREDRAW;
    w.lpfnWndProc = WndHandler;
    w.hInstance = hInst;
    w.hbrBackground = (HBRUSH) (COLOR_WINDOW + 1);
    w.lpszClassName = TEXT("C Windows");
    RegisterClass(&w);
    err = GetLastError();


	// printf("Creating Window\n");
	hWnd = CreateWindow( TEXT("C Windows"), TEXT("C Windows"), WS_OVERLAPPEDWINDOW,
        10,10,20,20,NULL,NULL,NULL,NULL);
    
    if (!hWnd) 
    {
		printf("Window creation failed\n");
		return(0);
    }
	// printf("Window created\n");
//	ShowWindow(hWnd,SW_SHOWNORMAL);
//	UpdateWindow(hWnd);    

    DEV_BROADCAST_DEVICEINTERFACE NotificationFilter;
    HDEVNOTIFY hDeviceNotify = NULL;
    static const GUID GuidDevInterfaceList[] =
    {
        { 0xa5dcbf10, 0x6530, 0x11d2, { 0x90, 0x1f, 0x00, 0xc0, 0x4f, 0xb9, 0x51, 0xed } },
        // { 0x53f56307, 0xb6bf, 0x11d0, { 0x94, 0xf2, 0x00, 0xa0, 0xc9, 0x1e, 0xfb, 0x8b } },
        //{ 0x4d1e55b2, 0xf16f, 0x11Cf, { 0x88, 0xcb, 0x00, 0x11, 0x11, 0x00, 0x00, 0x30 } },
        //{ 0xad498944, 0x762f, 0x11d0, { 0x8d, 0xcb, 0x00, 0xc0, 0x4f, 0xc3, 0x35, 0x8c } }
    };
    ZeroMemory(&NotificationFilter, sizeof(NotificationFilter));
    NotificationFilter.dbcc_size = sizeof(DEV_BROADCAST_DEVICEINTERFACE);
    NotificationFilter.dbcc_devicetype = DBT_DEVTYP_DEVICEINTERFACE;
    for (int i = 0; i < sizeof(GuidDevInterfaceList); i++)
    {
        NotificationFilter.dbcc_classguid = GuidDevInterfaceList[i];
        hDeviceNotify = RegisterDeviceNotification(hWnd, &NotificationFilter, DEVICE_NOTIFY_WINDOW_HANDLE);
        if (hDeviceNotify == NULL)
          printf("Could not register device notification\n");
    }

	while (!SystemThreadDone)  
	{
 		    while (PeekMessage(&msg, hWnd, 0, 0, PM_REMOVE))
		       DispatchMessage(&msg);

//			while(GetMessage(&msg, NULL, 0, 0))
//			{
//				TranslateMessage(&msg);
//				DispatchMessage(&msg);
//			}

			dwRes = WaitForMultipleObjects(1, hArray, FALSE, 250);
			switch(dwRes)  {
				case WAIT_OBJECT_0: 
					 SystemThreadDone = TRUE;
					 // printf("usbthread exit event received\n");
					 break;
				case WAIT_TIMEOUT:
					 // printf("usbthread timed out\n");
					 break;                       
				default: break;
			}
	}

    return 0;
}


JNIEXPORT jint JNICALL Java_eu_asterics_mw_systemstatechange_SystemChangeNotifier_systemEventInit
  (JNIEnv * env, jobject obj)

{
	 jint error_code = 0;
	 error_code = env->GetJavaVM(&jvm);

	  if(error_code != 0)
	  {
		   printf("GetJavaVM failed\n"); return(0);
	  }
	  jniObj = env->NewGlobalRef(obj);

      SystemExitEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
      if (SystemExitEvent == NULL)	  { printf("CreateEvent failed (SystemThread exit event)\n"); return(0); }
      SYSTEMTHREAD = CreateThread(NULL,0,systemThread,GetModuleHandle(NULL),0,&dwSystemStatId);    
	  if (SYSTEMTHREAD == NULL) { printf("CreateThread failed\n"); return(0); }

	  return (jint)1;
}

JNIEXPORT jint JNICALL Java_eu_asterics_mw_systemstatechange_SystemChangeNotifier_systemEventExit
  (JNIEnv * env, jobject obj)

{
	HANDLE hThreads[1];
    DWORD  dwRes;

	if (SYSTEMTHREAD)	{

      hThreads[0] = SYSTEMTHREAD;
      SetEvent(SystemExitEvent);
      dwRes = WaitForMultipleObjects(1, hThreads, FALSE, 10000);

      switch(dwRes)       {

		case WAIT_OBJECT_0:
				// printf("Thread returned.\n");
			break;
		case WAIT_TIMEOUT:
				// printf("Thread timed out.\n");
    	     break;
	    default:
             printf("Systemthread - unknown exit error\n");
             break;
      }

	  // reset thread exit event here
      ResetEvent(SystemExitEvent);
  	  CloseHandle(SystemExitEvent);
	  CloseHandle(SYSTEMTHREAD);
	  SYSTEMTHREAD=0;

	  env->DeleteGlobalRef(jniObj);
	  return (jint)0;
	}
}


