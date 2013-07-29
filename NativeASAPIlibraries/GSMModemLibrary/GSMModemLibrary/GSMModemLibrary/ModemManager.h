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


#pragma once

#include <iostream>
#include <tchar.h>
#include <locale>
#include <string>
#include <sstream>
#include <Windows.h>
#include "ATCommandManager.h"
#include "SMSEncoder.h"
#include "MultiPartMessageManager.h"
#include "GSMModemLibrary.h"

using namespace std;

DWORD WINAPI sendSMSThread(LPVOID lpParam);
DWORD WINAPI readSMS(LPVOID lpParam);
DWORD WINAPI initialize (LPVOID lpParam);

class ModemManager
{
public:
	ModemManager(LPWSTR comPort);
	~ModemManager(void);
	int init(LPCWSTR pin, LPCWSTR smsCenter);
	int sendSMS(LPWSTR recipientID, LPWSTR subject);
	int setCallbacks(NewSMSAvailable newSMSAvailable,ErrorCallback errorCallback,LPVOID param);
	int close();
protected:
	bool initialized;
	NewSMSAvailable newSMSAvailable;
	ErrorCallback errorCallback;
	LPVOID param;

	wstring com;
	string pin;
	string smsCenter;
	HANDLE hCommDev;
	DWORD eventMask;
	int smsBufferIndex;
	HANDLE smsThreadHandle;
	HANDLE portAccess;
	HANDLE readSMSThreadHandle;
	HANDLE initThreadHandle;
	wstring messageContent;
	wstring phoneID;
	bool finishRead;
	bool hasPin;
	bool hasCenterID;

	ATCommandManager atManager;
	SMSEncoder smsEncoder;
	SMSCoding smsCoding;
	MultiPartMessageManager multiPartMessageManager;

	static const int inputBufferSize=8192;
	static const int outputBufferSize=1024;
	static const int outputSMSBufferSize=16;

	static const int maxUnicodeSize=67;  //64
	static const int max7BitOctedSize=140; 

	char inputBuffer[inputBufferSize];
	char outputBuffer[outputBufferSize];
	char outputSMSBuffer[outputSMSBufferSize];

	int initialize();
	int initCom();
	int initModem();
	int writeToComm(DWORD numberOfBytesToWrite,bool smsBuffer=false);
	int readComm(DWORD& numberOfBytesRead,bool flushIfFull=false);
	int sendCommand(ATCommand command,DWORD& numberOfBytesRead,int commandData=0);
	int sendBuffer(int dataSize,DWORD& numberOfBytesRead);
	int processCommand(ATCommand command);
	int processCREGCommand();
	int decodeSMS();
	int sendSingleSMS();
	int sendMultipartSMS();

	friend DWORD WINAPI sendSMSThread(LPVOID lpParam);
	friend DWORD WINAPI sendMultiPartSMSThread(LPVOID lpParam);
	friend DWORD WINAPI readSMS(LPVOID lpParam);
	friend DWORD WINAPI initialize (LPVOID lpParam);
	
	
};

