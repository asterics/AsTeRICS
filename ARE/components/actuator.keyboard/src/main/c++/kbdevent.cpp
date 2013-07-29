
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
#include "kbdevent.h"
#include <jni.h>

jobject jniObj = NULL;
JavaVM * jvm = NULL;
jmethodID processKey = NULL;


JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_keyboard_KeyboardInstance_keyPress
  (JNIEnv * env, jobject obj, jint vk_key)

{
	//	keybd_event(vk_key,MapVirtualKey(vk_key,0) , KEYEVENTF_KEYUP|KEYEVENTF_EXTENDEDKEY,0); 
	//	keybd_event(vk_key,0 , 2,0); 

	HWND hWnd = GetForegroundWindow();
	HWND fWnd = 0;

	BYTE ks[1]={0};
	DWORD tid=::GetWindowThreadProcessId(hWnd,NULL);

	::SetKeyboardState(ks);
	::AttachThreadInput(::GetCurrentThreadId(),tid,TRUE);
	// ::BringWindowToTop(hWnd);
	fWnd=GetFocus();
	if (fWnd==0) fWnd=hWnd;
	// printf("sending keypress to wnd %ld \n\r",fWnd);
	::PostMessage(fWnd,WM_KEYDOWN,vk_key,(MapVirtualKey(vk_key,0)<<16) | 0x00000001);
	::AttachThreadInput(::GetCurrentThreadId(),tid,FALSE);

    return (jint)0;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_keyboard_KeyboardInstance_keyPressSi
  (JNIEnv * env, jobject obj, jint vk_key)

{
	INPUT    Input={0};

	Input.type      = INPUT_KEYBOARD;

	Input.ki.wVk=vk_key;
	Input.ki.wScan=MapVirtualKey(vk_key,0);
	Input.ki.dwFlags=KEYEVENTF_EXTENDEDKEY;
	Input.ki.time=0;
	Input.ki.dwExtraInfo=0;

	::SendInput(1,&Input,sizeof(Input));
    return (jint)0;
}


JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_keyboard_KeyboardInstance_keyRelease
	(JNIEnv * env, jobject obj, jint vk_key)

{

	HWND hWnd = GetForegroundWindow();
	HWND fWnd = 0;

	BYTE ks[1]={0};
	DWORD tid=::GetWindowThreadProcessId(hWnd,NULL);

	::SetKeyboardState(ks);
	::AttachThreadInput(::GetCurrentThreadId(),tid,TRUE);
	//::BringWindowToTop(hWnd);
	fWnd=GetFocus();
	if (fWnd==0) fWnd=hWnd;

	::PostMessage(fWnd,WM_KEYUP,vk_key,(MapVirtualKey(vk_key,0)<<16) | 0xC0000001);
	::AttachThreadInput(::GetCurrentThreadId(),tid,FALSE);
    return (jint)0;
}



JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_keyboard_KeyboardInstance_keyReleaseSi
  (JNIEnv * env, jobject obj, jint vk_key)

{

	INPUT    Input={0};

	Input.type      = INPUT_KEYBOARD;
	Input.ki.wVk=vk_key;
	Input.ki.wScan=MapVirtualKey(vk_key,0);
	Input.ki.dwFlags=KEYEVENTF_KEYUP|KEYEVENTF_EXTENDEDKEY;
	Input.ki.time=0;
	Input.ki.dwExtraInfo=0;

	::SendInput(1,&Input,sizeof(Input));
    return (jint)0;
}
