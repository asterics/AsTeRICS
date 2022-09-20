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

#include <initguid.h>
#include <winsock2.h>
#include <ws2bth.h>
#include <BluetoothAPIs.h>
#include <bthdef.h>
#include "PhoneLibrary.h"
//#include "CIMsProtocolManager.h"
#include "ProtocolImplementation.h"
#include "PhoneLibraryErrors.h"

// Search Bluetooth device thread function
DWORD WINAPI searchFunction(LPVOID lpParam);
// read Bluetooth data thread function
DWORD WINAPI readFunction (LPVOID lpParam);

enum ResultType {RT_Connect=1,RT_Disconnect,RT_SendSMS,RT_MakeCall, RT_AcceptCall, RT_DropCall, RT_GetState};

/**
*	@brief Bluetooth manager class
*
*	This class manage the Bluetooth connection.
*
*/

class CBTConnectionManager
{
public:
	CBTConnectionManager(void);
	~CBTConnectionManager(void);
	int init(DeviceFound pDeviceFound, NewSMS pNewSMS, PhoneStateChanged pPhoneStateChanged,LPVOID pUserPointer);
	int startSearchBTDevices();
	int connectDevice(unsigned _int64 uint64DeviceAddress,int nPort);
	int disconnectDevice();
	int close();
	int sendSMS(LPWSTR recipientID, LPWSTR subject);
	int makePhoneCall (LPWSTR recipientID);
	int acceptCall();
	int dropCall();
	int getPhoneState(PhoneState &phoneState);
private:
	unsigned _int64 m_uint64ConnectedDeviceAddress;
	SOCKET m_oLocalSocket;
	bool m_bConnected;
	bool m_bInited;
	bool m_bClosed;
	DeviceFound m_pDeviceFound;
	NewSMS m_pNewSMS;
	PhoneStateChanged m_pPhoneStateChanged;
	WSADATA m_oSocketData;
	bool m_bSearchingNow;
	HANDLE m_hRadio;
	HANDLE m_hSearchThread;
	HANDLE m_hReadThread;
	BLUETOOTH_DEVICE_SEARCH_PARAMS m_oBTDeviceSearchParams;
	HBLUETOOTH_DEVICE_FIND m_hFoundDevice;
	BLUETOOTH_DEVICE_INFO m_oFoundDevices[50];
	int m_nDeviceCount;
	bool AddIfAddressNotExist(BLUETOOTH_DEVICE_INFO& pDeviceInfo);
	LPVOID m_pUserPointer;
	bool m_bCloseConnection;
	bool m_bStopWait;
	//int SDPGetPort(unsigned _int64 uint64DeviceAddress);
	//int SDPGetPort();
	//int PerformServiceSearch () ;
	//CCIMsProtocolManager m_oProtocolManager;
	int SDPGetPort(__int64 address);
	int findPort(BYTE *oTable, int nTableSize );
	CProtocolImplementation m_oProtocolClass;
	
	const long m_lIntervalInSeconds;
	const long m_lIntervalInUseconds;

	const unsigned int m_unMaxWaitTime;

	int decodePacket(char* pBuffer,int nBufferLen,int nOptionalDataSize);
	
	CRITICAL_SECTION m_oProtocolRespondCriticalSection;
	CRITICAL_SECTION m_oReadFunctionCriticalSection;
	CRITICAL_SECTION m_oFinishBTConnectionManager;
	
	bool m_bReadThreadFinished;

	int waitForServerRespond(ResultType enResultType);
	int clearServerRespond(ResultType enRespondType);
	int setServerRespond(ResultType enRespondType,unsigned int unRespond);
	void stopWaiting();

	int m_unConnectRespondCode;
	int m_unDisconnectRespondCode;
	int m_unSendSMSRespondCode;
	int m_unMakeCallRespondCode;
	int m_unAcceptCallRespondCode;
	int m_unDropCallRespondCode;
	int m_unGetStateRespondCode;
	PhoneState m_LastPhoneState;

	WSAPROTOCOL_INFO m_oProtocolInfo;


friend DWORD WINAPI searchFunction(LPVOID lpParam);
friend DWORD WINAPI readFunction (LPVOID lpParam);
};
