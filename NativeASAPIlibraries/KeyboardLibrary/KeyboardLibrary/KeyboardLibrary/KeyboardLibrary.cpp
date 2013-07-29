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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */

#include "stdafx.h"
#include "KeyboardLibrary.h"
#include <stdlib.h>
#include "KeyboardLibraryErrors.h"


HINSTANCE hInstance = NULL;
HHOOK hHook = NULL;
HookCallBack fUserHookCallBack;
BlockOptions UserBlockOptions;

int *pTestValue;

bool bHookActive;
bool bInitated=false;


/**
 * The LowLevelKeyboardProc callback function 
 * @param nCode defines how the callback function should process the message
 * @param wParam the identifier of the keyboard message
 * @param lParam a pointer to a KBDLLHOOKSTRUCT structure
 */
LRESULT CALLBACK LowLevelKeyboardProc(int nCode, WPARAM wParam, LPARAM lParam)
{
	if(nCode<0)
	{
		return ( CallNextHookEx(hHook,nCode,wParam,lParam) );
	}
	else
	{
		PKBDLLHOOKSTRUCT pHookStruct;
		pHookStruct=(PKBDLLHOOKSTRUCT)lParam;
		ULONG_PTR extraInfo = pHookStruct->dwExtraInfo;

		HookMessage hookMessage;

		switch(wParam)
		{
		case WM_KEYDOWN:
			hookMessage=HM_KEYDOWN;
			break;
		case WM_KEYUP:
			hookMessage=HM_KEYUP;
			break;
		case WM_SYSKEYDOWN:
			hookMessage=HM_SYSKEYDOWN;
			break;
		case WM_SYSKEYUP:
			hookMessage=HM_SYSKEYUP;
			break;
		default:
			hookMessage=HM_None;
		}
		
		int nHookFlags = HF_None;

		if(LLKHF_EXTENDED & pHookStruct->flags)
		{
			nHookFlags=nHookFlags|HF_ExtendedKey;
		}

		
		if(LLKHF_INJECTED & pHookStruct->flags)
		{
			nHookFlags=nHookFlags|HF_InjectedKey;
		}

		
		if(LLKHF_ALTDOWN & pHookStruct->flags)
		{
			nHookFlags=nHookFlags|HF_AltKeyPressed;
		}

		
		if(LLKHF_UP & pHookStruct->flags)
		{
			nHookFlags=nHookFlags|HF_KeyPress;
		}

		bool bSendFromLibrary=false;

		if(extraInfo==(ULONG_PTR)pTestValue)
		{
			nHookFlags=nHookFlags|HF_SentFromLibrary;
			bSendFromLibrary=true;
		}

		int nRes = fUserHookCallBack(pHookStruct->scanCode, pHookStruct->vkCode,hookMessage,(HookFlags)nHookFlags,0);

		if(nRes<0)
		{
			return 1;
		}

		if(nRes>0)
		{
			return ( CallNextHookEx(hHook,nCode,wParam,lParam) );
		}

		if(UserBlockOptions==BO_BlockAll)
		{
			return 1;
		}

		if(UserBlockOptions==BO_PassAll)
		{
			return ( CallNextHookEx(hHook,nCode,wParam,lParam) );
		}

		if(bSendFromLibrary)
		{
			return ( CallNextHookEx(hHook,nCode,wParam,lParam) );
		}
		else
		{
			return 1;
		}

		return ( CallNextHookEx(hHook,nCode,wParam,lParam) );
	}


}


/**
 * Initializes the library.
 * @param hookCallBack pointer to the user callback function
 * @param param parameter defined by user
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall init(HookCallBack hookCallBack, LPVOID param)
{
	if(bInitated==true)
	{
		return Library_initialized;
	}

	pTestValue = new int;
	*pTestValue=0;
	fUserHookCallBack=hookCallBack;
	bHookActive=false;
	
	UserBlockOptions=BO_PassAll;
	
	bInitated=true;

	return 1;
}

/**
 * Closes the library.
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall close()
{
	
	if(bInitated==false)
	{
		return Library_no_initialized;
	}

	delete pTestValue;
	
	if(bHookActive)
	{
		stopHook();
	}

	bInitated=false;

	return 1;
}

/**
 * Starts key events hooking.
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall startHook()
{
	if(bHookActive==true)
	{
		return Hook_initialized;
	}

	hHook=SetWindowsHookEx(WH_KEYBOARD_LL,LowLevelKeyboardProc,hInstance,NULL);

	if(hHook==NULL)
	{
		return Hook_initialize_error;
	}

	bHookActive=true;

	return 1;
}

/**
 * Stops key events hooking.
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall stopHook()
{
	if(bHookActive==false)
	{
		return Hook_no_initialized;
	}

	BOOL bRes=UnhookWindowsHookEx(hHook);

	if(!bRes)
	{
		return Hook_stopping_error;
	}

	bHookActive=false;

	return 1;
}

/**
 * Simulate the key send event defined by scan code.
 * @param scanCode scan code
 * @param flags send key flags, define the event type
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall sendKeyByScanCode(int scanCode, SendKeyFlags flags)
{
	int nInputs=1;

	if(flags&SKF_KeyPress)
	{
		nInputs=2;
	}

	int nExtendedKey=0;

	if((flags&SKF_KeyDown)&&(flags&SKF_KeyUP))
	{
		int nExtendedKey=KEYEVENTF_EXTENDEDKEY;
	}

	INPUT *pInput = new INPUT[nInputs];

	if((flags&SKF_KeyDown)&&(flags&SKF_KeyUP))
	{
		pInput[0].type=INPUT_KEYBOARD;
		pInput[0].ki.wVk=0;
		pInput[0].ki.wScan=scanCode;
		pInput[0].ki.dwFlags=KEYEVENTF_SCANCODE|nExtendedKey;
		pInput[0].ki.dwExtraInfo=(ULONG_PTR)pTestValue;

		pInput[1].type=INPUT_KEYBOARD;
		pInput[1].ki.wVk=0;
		pInput[1].ki.wScan=scanCode;
		pInput[1].ki.dwFlags=KEYEVENTF_SCANCODE|KEYEVENTF_KEYUP|nExtendedKey;
		pInput[1].ki.dwExtraInfo=(ULONG_PTR)pTestValue;
	}
	else
	{
		if((flags&SKF_KeyDown)||(flags&SKF_KeyUP))
		{
			int nKeyUp=0;
			
			if(flags&SKF_KeyUP)
			{
				nKeyUp=KEYEVENTF_KEYUP;
			}

			pInput[0].type=INPUT_KEYBOARD;
			pInput[0].ki.wVk=0;
			pInput[0].ki.wScan=scanCode;
			pInput[0].ki.dwFlags=KEYEVENTF_SCANCODE|nExtendedKey|nKeyUp;
			pInput[0].ki.dwExtraInfo=(ULONG_PTR)pTestValue;

		}
		else
		{
			delete[] pInput;
			return Send_keys_error;
		}
	}

	int nResult=SendInput(nInputs,pInput,sizeof(INPUT));

	delete[] pInput;

	if(nResult==0)
	{
		return Send_keys_error;
	}
	else
	{
		return 1;
	}
}

/**
 * Simulate the key send event defined by virtual key code.
 * @param virtualCode virtual key code
 * @param flags send key flags, define the event type
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall sendKeyByVirtualCode(int virtualCode, SendKeyFlags flags)
{
	int nInputs=1;

	if((flags&SKF_KeyDown)&&(flags&SKF_KeyUP))
	{
		nInputs=2;
	}

	INPUT *pInput = new INPUT[nInputs];

	if((flags&SKF_KeyDown)&&(flags&SKF_KeyUP))
	{
		pInput[0].type=INPUT_KEYBOARD;
		pInput[0].ki.wVk=virtualCode;
		pInput[0].ki.wScan=0;
		pInput[0].ki.dwFlags=0;
		pInput[0].ki.dwExtraInfo=(ULONG_PTR)pTestValue;

		pInput[1].type=INPUT_KEYBOARD;
		pInput[1].ki.wVk=virtualCode;
		pInput[1].ki.wScan=0;
		pInput[1].ki.dwFlags=KEYEVENTF_KEYUP;
		pInput[1].ki.dwExtraInfo=(ULONG_PTR)pTestValue;
	}
	else
	{
		if((flags&SKF_KeyDown)||(flags&SKF_KeyUP))
		{
			int nKeyUp=0;
			
			if(flags&SKF_KeyUP)
			{
				nKeyUp=KEYEVENTF_KEYUP;
			}

			pInput[0].type=INPUT_KEYBOARD;
			pInput[0].ki.wVk=virtualCode;
			pInput[0].ki.wScan=0;
			pInput[0].ki.dwFlags=nKeyUp;
			pInput[0].ki.dwExtraInfo=(ULONG_PTR)pTestValue;

		}
		else
		{
			delete[] pInput;
			return Send_keys_error;
		}
	}

	int nResult=SendInput(nInputs,pInput,sizeof(INPUT));

	delete[] pInput;


	if(nResult==0)
	{
		return Send_keys_error;
	}
	else
	{
		return 1;
	}
}

/**
 * Simulates text being typed in
 * @param text text to send
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall sendText(LPWSTR text)
{
	int nStringSize=wcslen(text);
	int nInputs=nStringSize*2;

	INPUT *pInput = new INPUT[nInputs];

	for(int i=0;i<nStringSize;i++)
	{
		pInput[2*i].type=INPUT_KEYBOARD;
		pInput[2*i].ki.wVk=0;
		pInput[2*i].ki.wScan=text[i];
		pInput[2*i].ki.dwFlags=KEYEVENTF_UNICODE;
		pInput[2*i].ki.dwExtraInfo=(ULONG_PTR)pTestValue;
		pInput[2*i].ki.time=0;

		pInput[2*i+1].type=INPUT_KEYBOARD;
		pInput[2*i+1].ki.wVk=0;
		pInput[2*i+1].ki.wScan=text[i];
		pInput[2*i+1].ki.dwFlags=KEYEVENTF_UNICODE|KEYEVENTF_KEYUP;
		pInput[2*i+1].ki.dwExtraInfo=(ULONG_PTR)pTestValue;
		pInput[2*i+1].ki.time=0;
	}
	
	
	int nResult=SendInput(nInputs,pInput,sizeof(INPUT));

	delete[] pInput;

	if(nResult==0)
	{
		return Send_keys_error;
	}
	else
	{
		return 1;
	}


	return 1;
}

/**
 * Blocks or Passes key events.
 * @param blockOptions defines the function's behaviour.
 * @return error number
 */
KEYBOARDLIBRARY_API int __stdcall blockKeys(BlockOptions blockOptions)
{
	UserBlockOptions=blockOptions;
	return 1;
}