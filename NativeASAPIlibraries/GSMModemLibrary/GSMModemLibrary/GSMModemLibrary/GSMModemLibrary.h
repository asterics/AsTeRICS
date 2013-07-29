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



#ifdef GSMMODEMLIBRARY_EXPORTS
#define GSMMODEMLIBRARY_API __declspec(dllexport)
#else
#define GSMMODEMLIBRARY_API __declspec(dllimport)
#endif


typedef void (__stdcall *NewSMSAvailable) (LPCWSTR phoneID, LPCWSTR subject, LPVOID param);
typedef void (__stdcall *ErrorCallback) (int result, LPVOID param);
typedef void (__stdcall *ModemSearchResult) (LPCWSTR port,LPCWSTR modemName, LPVOID param);

extern "C"
{
	GSMMODEMLIBRARY_API int __stdcall init(LPWSTR com, NewSMSAvailable newSMSAvailable,ErrorCallback errorCallback, LPWSTR pin, LPWSTR smsCenterNumber ,LPVOID param);
	GSMMODEMLIBRARY_API int __stdcall close();
	GSMMODEMLIBRARY_API int __stdcall sendSMS(LPWSTR recipientID, LPWSTR subject);
	GSMMODEMLIBRARY_API int __stdcall getModemPortNumber(ModemSearchResult modemSearchResult,LPVOID param);
}