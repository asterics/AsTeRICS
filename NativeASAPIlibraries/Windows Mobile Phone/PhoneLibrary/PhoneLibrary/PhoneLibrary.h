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


#pragma once


#ifdef PHONELIBRARY_EXPORTS
#define PHONELIBRARY_API __declspec(dllexport)
#else
#define PHONELIBRARY_API __declspec(dllimport)
#endif



enum PhoneState {PS_IDLE=1, PS_RING, PS_CONNECTED};
typedef void (__stdcall *DeviceFound) (unsigned _int64 deviceAddress, LPWSTR deviceName, LPVOID param);
typedef void (__stdcall *NewSMS) (LPWSTR PhoneID, LPWSTR subject, LPVOID param);
typedef void (__stdcall *PhoneStateChanged) (PhoneState phoneState, LPWSTR phoneID , LPVOID param);

#define Default_port -1

extern "C"
{
	PHONELIBRARY_API int __stdcall init(DeviceFound deviceFound, NewSMS newSMS, PhoneStateChanged phoneStateChanged, LPVOID param);
	PHONELIBRARY_API int __stdcall searchDevices();
	PHONELIBRARY_API int __stdcall close();
	PHONELIBRARY_API int __stdcall connectToDevice(unsigned _int64 deviceAddress, int port);
	PHONELIBRARY_API int __stdcall disconnect();
	PHONELIBRARY_API int __stdcall sendSMS(LPWSTR recipientID, LPWSTR subject);
	PHONELIBRARY_API int __stdcall makePhoneCall (LPWSTR recipientID);
	PHONELIBRARY_API int __stdcall acceptCall();
	PHONELIBRARY_API int __stdcall dropCall();
	PHONELIBRARY_API int __stdcall getPhoneState(PhoneState &phoneState);
}