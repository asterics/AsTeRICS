#include <Windows.h>




typedef void (__stdcall *NewSMSAvailable) (LPCWSTR phoneID, LPCWSTR subject, LPVOID param);
typedef void (__stdcall *ErrorCallback) (int result, LPVOID param);
typedef void (__stdcall *ModemSearchResult) (LPCWSTR port,LPCWSTR modemName, LPVOID param);


typedef int (__stdcall *Init)(LPWSTR com, NewSMSAvailable newSMSAvailable,ErrorCallback errorCallback, LPWSTR pin, LPWSTR smsCenterNumber ,LPVOID param);
typedef int (__stdcall *Close)();
typedef int (__stdcall *SendSMS)(LPWSTR recipientID, LPWSTR subject);
typedef int (__stdcall *GetModemPortNumber)(ModemSearchResult modemSearchResult,LPVOID param);
