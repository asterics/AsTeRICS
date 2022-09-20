

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
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 *
 */

/**
 *Implements the dll windows library which interfaces the 3D Mouse device.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jul 05, 2011
 *         Time: 11:51:00 AM
 */

#include "stdafx.h"
#include "Mouse3Dlibrary.h"
#include <boost/scoped_array.hpp>
#include "Mouse3DlibraryErrors.h"

extern HINSTANCE hinstance;
#define LOGITECH_VENDOR_ID 0x46d

HANDLE windowThread=NULL;
CRITICAL_SECTION StartSection;
CRITICAL_SECTION Mouse3DData;

bool startResult=false;
bool resultReady=false;
bool finish=false;

long Mouse3dX=0;
long Mouse3dY=0;
long Mouse3dZ=0;

long Mouse3dRX=0;
long Mouse3dRY=0;
long Mouse3dRZ=0;

long Mouse3dbuttons=0;

HWND WindowHandle=NULL;

bool bInitialized=false;

/**
 * Registers Raw Input for the 3D mouse device
 * @param hwnd   the handle of the window which will be getting messages from the 3D mouse device
 * @return       if true the registration is successful
 */
bool registerRawInput(HWND hwnd)
{
	RAWINPUTDEVICE sRawInputDevices[] = {
	{0x01, 0x08, 0x00, 0x00}
	};

	

	UINT uiNumDevices = sizeof(sRawInputDevices)/sizeof(sRawInputDevices[0]);
	UINT cbSize = sizeof (sRawInputDevices[0]);
	for (size_t i=0; i<uiNumDevices; i++)
	{
		sRawInputDevices[i].hwndTarget = hwnd;
		sRawInputDevices[i].dwFlags=sRawInputDevices[i].dwFlags|RIDEV_INPUTSINK;
	}
	BOOL Result =RegisterRawInputDevices(sRawInputDevices, uiNumDevices, cbSize);
	if(Result==TRUE)
	{
		return true;
	}
	else
	{
		return false;
	}
	
}

/**
 * Reads the data from the message
 * @param wParam   message parameter
 * @param lParam   message parameter
 * @return 0
 */
LRESULT read3DMouseData (WPARAM wParam, LPARAM lParam)
{
	boost::scoped_array<BYTE> saRawInput;
	
	HRAWINPUT hRawInput = reinterpret_cast<HRAWINPUT>(lParam);
	UINT dwSize=0;

	if (::GetRawInputData(hRawInput, RID_INPUT, NULL, &dwSize, sizeof(RAWINPUTHEADER))== 0)
	saRawInput.reset(new BYTE[dwSize]);
	PRAWINPUT pRawInput = reinterpret_cast<PRAWINPUT>(saRawInput.get());
	
	if (!pRawInput)
		return 0;

	if (::GetRawInputData(hRawInput, RID_INPUT, pRawInput, &dwSize,sizeof(RAWINPUTHEADER)) != dwSize)
		return 0;

	if (pRawInput->header.dwType != RIM_TYPEHID)
		return 0;

	RID_DEVICE_INFO sRidDeviceInfo;
	sRidDeviceInfo.cbSize = sizeof(RID_DEVICE_INFO);
	dwSize = sizeof(RID_DEVICE_INFO);
	if (GetRawInputDeviceInfo(pRawInput->header.hDevice,RIDI_DEVICEINFO,&sRidDeviceInfo,&dwSize) == dwSize)
	{
		if (sRidDeviceInfo.hid.dwVendorId == LOGITECH_VENDOR_ID)
		{

			if (pRawInput->data.hid.bRawData[0] == 0x01)
			{
				short* pnData = reinterpret_cast<short*>(&pRawInput->
				data.hid.bRawData[1]);
				short X = pnData[0];
				short Y = pnData[1];
				short Z = pnData[2];

				EnterCriticalSection(&Mouse3DData);
				Mouse3dX=X;
				Mouse3dY=Y;
				Mouse3dZ=Z;
				LeaveCriticalSection(&Mouse3DData);
				
			}
			else if (pRawInput->data.hid.bRawData[0] == 0x02)
			{ 
				short* pnData = reinterpret_cast<short*>(&pRawInput->
				data.hid.bRawData[1]);
				short rX = pnData[0];
				short rY = pnData[1];
				short rZ = pnData[2];

				EnterCriticalSection(&Mouse3DData);
				Mouse3dRX=rX;
				Mouse3dRY=rY;
				Mouse3dRZ=rZ;
				LeaveCriticalSection(&Mouse3DData);
				
			}
			else if (pRawInput->data.hid.bRawData[0] == 0x03)
			{ 
				unsigned long dwKeystate = *reinterpret_cast<unsigned long *>(&pRawInput->data.hid.bRawData[1]);
				EnterCriticalSection(&Mouse3DData);
				Mouse3dbuttons=0xFFFF & dwKeystate;
				LeaveCriticalSection(&Mouse3DData);
			}
		}
	}
}

/**
 * Checks if the 3D Mouse is connected
 * @return  returns 1 if the device is connected, 0 if the device is not connected, -1 if there is an error.
 */
int isDeviceConnected()
{
	UINT nDevices;
	PRAWINPUTDEVICELIST pRawInputDeviceList;

	if (GetRawInputDeviceList(NULL, &nDevices, sizeof(RAWINPUTDEVICELIST)) != 0) 
	{ 
		return -1;
	}

	if(nDevices<1)
	{
		return 0;
	}

	pRawInputDeviceList = new RAWINPUTDEVICELIST[nDevices];
	
	if (GetRawInputDeviceList(pRawInputDeviceList, &nDevices, sizeof(RAWINPUTDEVICELIST)) == (UINT) -1)
	{
		return -1;
	}

	bool found=false;

	for(int i=0;i<nDevices;i++)
	{
		RID_DEVICE_INFO rdi;
		rdi.cbSize= sizeof(rdi);
        UINT cbSize = sizeof(rdi);
		if(GetRawInputDeviceInfo(pRawInputDeviceList[i].hDevice, RIDI_DEVICEINFO, &rdi, &cbSize) > 0)
		{
			if (rdi.dwType == RIM_TYPEHID || rdi.hid.dwVendorId == LOGITECH_VENDOR_ID)
			{
				found=true;
				break;
			}
		}
	}


	delete[] pRawInputDeviceList;

	if(found)
	{
		return 1;
	}
	else
	{
		return 0;
	}

}

/**
 * Hiden Window callback function.
 * @param hwnd  window handle
 * @param UINT  message id
 * @param wParam  message parameter
 * @param lParam  message parameter
 * @return 0
 */

LRESULT CALLBACK windowCallback(HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	
	switch(message)
	{
		case WM_DESTROY:
			 PostQuitMessage(WM_QUIT);
			 break;
		case WM_CLOSE:
			 DestroyWindow(WindowHandle);
			 break;
		case WM_INPUT:
			 read3DMouseData(wParam,lParam);
			break;
		default:
			return DefWindowProc(hwnd,message,wParam,lParam);
	}
	return 0;
}


/**
 * Hiden Window function running in the thread.
 * @param lpParam  thread parameter
 * @return 1
 */
DWORD WINAPI windowThreadFunction(LPVOID lpParam) 
{
	WNDCLASSEX wndclassex;

	wndclassex.cbSize=sizeof(WNDCLASSEX);
	wndclassex.style=CS_NOCLOSE;
	wndclassex.lpfnWndProc=windowCallback;
	wndclassex.cbClsExtra=0;
	wndclassex.cbWndExtra=0;
	wndclassex.hInstance=hinstance;
	wndclassex.hIcon=NULL;
	wndclassex.hCursor=LoadCursor(0,(LPCWSTR)IDC_ARROW);
	wndclassex.hbrBackground=(HBRUSH)GetStockObject(WHITE_BRUSH);
	wndclassex.lpszMenuName=NULL;
	wndclassex.lpszClassName=L"Mouse 3D Window";
	wndclassex.hIconSm=LoadIcon(0,(LPCWSTR)IDI_APPLICATION);

	if(RegisterClassEx(&wndclassex)==0)
	{
		EnterCriticalSection(&StartSection);
		startResult=false;
		resultReady=true;
		LeaveCriticalSection(&StartSection);

		return 1;
	}

	WindowHandle=NULL;

	/* Creates hidden window which will be getting messages*/
	WindowHandle=CreateWindowEx(0,L"Mouse 3D Window",L"3D Mouse Window",WS_OVERLAPPEDWINDOW,100,100,100,100,0,0,hinstance,0);

	if(WindowHandle==0)
	{
		EnterCriticalSection(&StartSection);
		startResult=false;
		resultReady=true;
		LeaveCriticalSection(&StartSection);

		UnregisterClass(L"Mouse 3D Window",hinstance);
		return 1;
	}

	if(registerRawInput(WindowHandle)==false)
	{
		EnterCriticalSection(&StartSection);
		startResult=false;
		resultReady=true;
		LeaveCriticalSection(&StartSection);

		SendMessage(WindowHandle,WM_CLOSE,0,0);
	}
	else
	{
		EnterCriticalSection(&StartSection);
		startResult=true;
		resultReady=true;
		LeaveCriticalSection(&StartSection);
	}

	MSG msg;
	for(;;)
	{

		if(0!=GetMessage(&msg,NULL,0,0))
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
		if(msg.message==WM_QUIT)
		{
			break;
		}
	}

	if(UnregisterClass(L"Mouse 3D Window",hinstance)==0)
	{
		//error		
	}

	return 1;
}

/**
 * Library interface function which initialize the library
 * @return  if the returned value is less then 0, the value is an error number.
 */
MOUSE3DLIBRARY_API int __stdcall init ()
{
	
	if(bInitialized)
	{
		return library_initialized;
	}

	InitializeCriticalSection(&StartSection);
	InitializeCriticalSection(&Mouse3DData);
	finish=false;
	startResult=false;
	resultReady=false;

	if(isDeviceConnected()<1)
	{
		DeleteCriticalSection(&StartSection);
		DeleteCriticalSection(&Mouse3DData);
		return no_3D_mouse_device_found;
	}

	windowThread=CreateThread(NULL,0,&windowThreadFunction,0,0,NULL);
	Sleep(50);

	bool result;

	if(!windowThread)
	{
		DeleteCriticalSection(&StartSection);
		DeleteCriticalSection(&Mouse3DData);
		return library_initialize_error;
	}

	bool exitFor=false;

	/*waits for response from the thread*/
	for(;;)
	{
		EnterCriticalSection(&StartSection);
		if(resultReady==true)
		{
			if(startResult==true)
			{
				result=true;
			}
			else
			{
				result=false;
			}

			resultReady=false;
			startResult=false;

			exitFor=true;
		}
		
		LeaveCriticalSection(&StartSection);
		if(exitFor)
		{
			break;
		}

		Sleep(10);
	}

	if(result)
	{
		bInitialized=true;
		return 1;
	}
	else
	{
		DeleteCriticalSection(&StartSection);
		DeleteCriticalSection(&Mouse3DData);
		CloseHandle(windowThread);
		return library_initialize_error;
	}
	
}

/**
 * Library interface function which closes the library
 * @return  if the returned value is less then 0, the value is an error number.
 */
MOUSE3DLIBRARY_API int __stdcall close ()
{
	
	if(!bInitialized)
	{
		return library_no_initialized;
	}
	
	SendMessage(WindowHandle,WM_CLOSE,0,0);

	int result =WaitForSingleObject(windowThread,300);

	switch(result)
	{
	case WAIT_TIMEOUT:
		break;
	case WAIT_FAILED:
		break;
	}

	bInitialized=false;

	DeleteCriticalSection(&StartSection);
	DeleteCriticalSection(&Mouse3DData);
	CloseHandle(windowThread);
	return 1;
}

/**
 * Library interface function which gets the 3D mouse data
 * @param x  pointer to value which holds the x 3D mouse data
 * @param y  pointer to value which holds the y 3D mouse data
 * @param z  pointer to value which holds the z 3D mouse data
 * @param Rx  pointer to value which holds the rx 3D mouse data
 * @param Ry  pointer to value which holds the ry 3D mouse data
 * @param Rz  pointer to value which holds the rz 3D mouse data
 * @param buttons  pointer to value which holds the state of the 3D mouse buttons
 * @return  if the returned value is less then 0, the value is an error number.
 */
MOUSE3DLIBRARY_API int __stdcall get3DMouseState(long *x, long *y, long *z, long *Rx, long *Ry, long *Rz, long* buttons)
{
	
	if(!bInitialized)
	{
		return library_no_initialized;
	}

	EnterCriticalSection(&Mouse3DData);
	*buttons=Mouse3dbuttons;
	*x=Mouse3dX;
	*y=Mouse3dY;
	*z=Mouse3dZ;
	*Rx=Mouse3dRX;
	*Ry=Mouse3dRY;
	*Rz=Mouse3dRZ;
	LeaveCriticalSection(&Mouse3DData);
	return 1;
}


