#pragma once

#include <Windows.h>

enum PhoneState {PS_IDLE=1, PS_RING, PS_CONNECTED};

typedef void (__stdcall *DeviceFound) (unsigned _int64 deviceAddress, LPWSTR deviceName, LPVOID param);
typedef void (__stdcall *NewSMS) (LPWSTR PhoneID, LPWSTR subject, LPVOID param);
typedef void (__stdcall *PhoneStateChanged) (PhoneState phoneState, LPWSTR phoneID , LPVOID param);

typedef int (__stdcall *Init) (DeviceFound deviceFound, NewSMS newSMS, PhoneStateChanged phoneStateChanged, LPVOID param);
typedef int (__stdcall *SearchDevices) ();
typedef int (__stdcall *Close) ();
typedef int (__stdcall *ConnectToDevice) (unsigned _int64 deviceAddress, int port);
typedef int (__stdcall *Disconnect) ();
typedef int (__stdcall *SendSMS) (LPWSTR recipientID, LPWSTR subject);
typedef int (__stdcall *MakePhoneCall) (LPWSTR recipientID);
typedef int (__stdcall *AcceptCall) ();
typedef int (__stdcall *DropCall) ();
typedef int (__stdcall *GetPhoneState) (PhoneState &phoneState);