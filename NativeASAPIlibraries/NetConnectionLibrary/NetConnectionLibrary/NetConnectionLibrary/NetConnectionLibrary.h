// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the NETCONNECTIONLIBRARY_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// NETCONNECTIONLIBRARY_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.l

#pragma once

#ifdef NETCONNECTIONLIBRARY_EXPORTS
#define NETCONNECTIONLIBRARY_API __declspec(dllexport)
#else
#define NETCONNECTIONLIBRARY_API __declspec(dllimport)
#endif

enum ServerMode{SM_CLIENT=1, SM_SERVER_SINGLE_SESSION, SM_SERVER_MULTISESSION};

typedef void (__stdcall *NewEvent) (int port, LPVOID param);
typedef void (__stdcall *NewIntegerValue) (int port,int value, LPVOID param);
typedef void (__stdcall *NewDoubleValue) (int port,double value, LPVOID param);
typedef void (__stdcall *NewStringValue) (int port,LPWSTR value, LPVOID param);

extern "C"
{
	NETCONNECTIONLIBRARY_API int __stdcall init(ServerMode serverMode,LPWSTR IP,int port,NewEvent newEvent,NewIntegerValue newIntegerValue,NewDoubleValue newDoubleValue,NewStringValue newStringValue, LPVOID param);
	NETCONNECTIONLIBRARY_API int __stdcall close();
	NETCONNECTIONLIBRARY_API int __stdcall sendEvent(int port);
	NETCONNECTIONLIBRARY_API int __stdcall sendInteger(int port,int value);
	NETCONNECTIONLIBRARY_API int __stdcall sendDouble(int port,double value);
	NETCONNECTIONLIBRARY_API int __stdcall sendText(int port,wchar_t* text);
	NETCONNECTIONLIBRARY_API int __stdcall numberOfConnections();
}