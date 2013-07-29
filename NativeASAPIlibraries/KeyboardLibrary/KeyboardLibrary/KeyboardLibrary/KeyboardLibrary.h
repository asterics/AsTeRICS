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

#ifdef KEYBOARDLIBRARY_EXPORTS
#define KEYBOARDLIBRARY_API __declspec(dllexport)
#else
#define KEYBOARDLIBRARY_API __declspec(dllimport)
#endif


enum HookFlags
{
	HF_None=0,
	HF_ExtendedKey=1,
	HF_InjectedKey=2,
	HF_AltKeyPressed=4,
	HF_KeyPress=8,
	HF_SentFromLibrary =0x10
};

enum HookMessage
{
	HM_None=0,
	HM_KEYDOWN=1, 
	HM_KEYUP, 
	HM_SYSKEYDOWN,
	HM_SYSKEYUP
};

enum SendKeyFlags
{
	SKF_KeyDown=1,
	SKF_KeyUP=2,
	SKF_KeyPress=3, // SKF_KeyPress=SKF_KeyDown|SKF_KeyUP
	SKF_KeyExtended=4,
};

enum BlockOptions
{
	BO_BlockAll=1,
	BO_PassSentFromLibrary=2,
	BO_PassAll=3
};

enum LibraryAction
{
	LA_Block = -1,
	LA_Default = 0,
	LA_Pass = 1
};

typedef int (__stdcall *HookCallBack) (int scanCode, int virtualCode,HookMessage message, HookFlags flags, LPVOID param);

extern "C"
{
KEYBOARDLIBRARY_API int __stdcall init(HookCallBack hookCallBack, LPVOID param);
KEYBOARDLIBRARY_API int __stdcall close();
KEYBOARDLIBRARY_API int __stdcall startHook();
KEYBOARDLIBRARY_API int __stdcall stopHook();
KEYBOARDLIBRARY_API int __stdcall sendKeyByScanCode(int scanCode, SendKeyFlags flags);
KEYBOARDLIBRARY_API int __stdcall sendKeyByVirtualCode(int virtualCode, SendKeyFlags flags);
KEYBOARDLIBRARY_API int __stdcall sendText(LPWSTR text);
KEYBOARDLIBRARY_API int __stdcall blockKeys(BlockOptions blockOptions);
}
