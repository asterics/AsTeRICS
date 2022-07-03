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

#include "StdAfx.h"
#include "BTConnectionManager.h"
#include "stdlib.h"

/**
 * Manages the Bluetooth connection
 *    
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 */


/**
 * The class constructor.
 */
CBTConnectionManager::CBTConnectionManager(void):m_oProtocolClass(Client_Site), m_lIntervalInSeconds(0),m_lIntervalInUseconds(300000),m_unMaxWaitTime(500)
{
	m_pDeviceFound=NULL;
	m_pNewSMS=NULL;
	m_pPhoneStateChanged=NULL;
	m_hRadio=NULL;
	m_hSearchThread=NULL;
	m_hReadThread=NULL;
	m_bInited=false;
	m_bClosed=false;
	m_bSearchingNow=false;
	m_uint64ConnectedDeviceAddress=0;
	m_bConnected=false;
	m_bCloseConnection=false;
	m_bReadThreadFinished=false;

	InitializeCriticalSection(&m_oProtocolRespondCriticalSection);
	InitializeCriticalSection(&m_oReadFunctionCriticalSection);
	InitializeCriticalSection(&m_oFinishBTConnectionManager);

}

/**
 * The class destructor.
 */
CBTConnectionManager::~CBTConnectionManager(void)
{

	if(m_bClosed==false)
	{
		close();
	}
	
	bool l_bFinish=false;
	int l_nCounter=0;
	

	DeleteCriticalSection(&m_oProtocolRespondCriticalSection);
	DeleteCriticalSection(&m_oReadFunctionCriticalSection);
	DeleteCriticalSection(&m_oFinishBTConnectionManager);

}

/**
 * Finds the port number in the connection data.
 * @param oTable pointer to the data.
 * @param nTableSize the data size.
 * @return port number or -1 if not found
 */
int CBTConnectionManager::findPort(BYTE *oTable, int nTableSize )
{
	for(int i=1;i<nTableSize-9;i++)
	{
		// Try to find the lpBlob data sequence, which should be put after the port number
		if((oTable[i+1]==0x09)&&(oTable[i+2]==0x01)&&(oTable[i+3]==0x00)&&(oTable[i+4]==0x25)&&(oTable[i+5]==0x12)&&(oTable[i+6]==0x53)&&(oTable[i+7]==0x65)&&(oTable[i+8]==0x72))
		{
			// Port number is befor this data sequence
			return oTable[i];
		}
	}

	return -1;
}

/**
 * Returns the remote device port.
 * @param address the device address
 * @return port number or -1 if not found
 */
int CBTConnectionManager::SDPGetPort(__int64 address)
{
	
	SOCKADDR_BTH l_oServerSocketData = {0};
	l_oServerSocketData.addressFamily = AF_BTH;
    l_oServerSocketData.btAddr = (BTH_ADDR) address;
    l_oServerSocketData.port = BT_PORT_ANY;

	wchar_t addressAsString[1000];
    DWORD addressSize = sizeof(addressAsString);
	
	int l_nResult;
	
	// Change get string vaule of the address
	l_nResult=WSAAddressToString((struct sockaddr *)&l_oServerSocketData,sizeof(l_oServerSocketData),&m_oProtocolInfo,addressAsString,&addressSize);
	
	if(l_nResult!=0)
	{
		int l_nError=WSAGetLastError();
		return -1;
	}

	// Serial Port Guid
	GUID SPPGuid=
	{0x000001101, 0x0000, 0x1000, {0x80, 0x00, 0x00, 0x80, 0x5F, 0x9B, 0x34, 0xFB}};

	int port = 0;
	HANDLE h;
	WSAQUERYSET *qs;
	//DWORD flags1 = 0;
	DWORD flags = 0;
	DWORD qs_len;
	bool done;
	qs_len = sizeof(WSAQUERYSET);
	//qs =new WSAQUERYSET;
	qs=(WSAQUERYSET*)malloc(qs_len);
	ZeroMemory( qs, qs_len );
	qs->dwSize = sizeof(WSAQUERYSET);
	qs->lpServiceClassId =&SPPGuid;
	qs->dwNameSpace = NS_BTH;
	qs->dwNumberOfCsAddrs = 0;
	qs->lpszContext = addressAsString; 
	//flags1 = LUP_FLUSHCACHE|LUP_RETURN_TYPE;
	flags = LUP_FLUSHCACHE|LUP_RETURN_ALL|LUP_RETURN_COMMENT;

	// Look for the Serial port service
	if( SOCKET_ERROR == WSALookupServiceBegin( qs, flags, &h )) {
		int l_nError=WSAGetLastError();
		return -1;
	}
	done = false;


	while ( ! done ) {
		if( SOCKET_ERROR == WSALookupServiceNext( h, flags, &qs_len, qs ) ) {
			int error = WSAGetLastError();
			if( error == WSAEFAULT ) {
				free(qs);
				qs=(WSAQUERYSET*)malloc(qs_len);
				ZeroMemory( qs, qs_len );
			} else if (error == WSA_E_NO_MORE ) {
				done = true;
			} else {
				//ExitProcess(2);
				int a=0;
				a++;
			}
		} else {
			if(qs->lpcsaBuffer!=0)
			{
				// if the lpcsaBuffer is not null get the port number
				port =((SOCKADDR_BTH*)qs->lpcsaBuffer->RemoteAddr.lpSockaddr)->port;
			}
			else
			{
				if(qs->lpBlob->pBlobData!=0)
				{
					if(qs->lpBlob->cbSize>10)
					{
						//port=qs->lpBlob->pBlobData[56];
						// try to find the port number in the lpBlob data
						port=findPort(qs->lpBlob->pBlobData,qs->lpBlob->cbSize);
					}
					else
					{
						port=-1;
					}
				}
				else
				{
					port=-1;
				}
			}
		}
	}
	WSALookupServiceEnd( h );
	free(qs);
	return port; 
}

/**
 * Initializes the class.
 * @param pDeviceFound pointer to callback function, which is called when the new device is found
 * @param pNewSMS pointer to callback function, which is called when the new SMS if available
 * @param pPhoneStateChanged pointer to callback function, which is called when the phone state is changed.
 * @param pUserPointer parameter given by user
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::init(DeviceFound pDeviceFound, NewSMS pNewSMS, PhoneStateChanged pPhoneStateChanged,LPVOID pUserPointer)
{
	if(m_bInited)
	{
		return Library_initialized;
	}
	
	// copy user callback function pointers
	m_pDeviceFound=pDeviceFound;
	m_pNewSMS=pNewSMS;
	m_pPhoneStateChanged=pPhoneStateChanged;
	m_pUserPointer=pUserPointer;
	

	int l_nResult;
	memset(&m_oSocketData,0,sizeof(m_oSocketData));

	// open client socket

	l_nResult=WSAStartup(MAKEWORD( 2, 2 ),&m_oSocketData);

	if(l_nResult==0)
	{
		m_bInited=true;
		return 1;
	}
	else
	{
		return Library_initialize_error;
	}


}

/**
 * Closes the class.
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::close()
{
	
	/*EnterCriticalSection(&m_oReadFunctionCriticalSection);
	m_bCloseConnection=true;
	LeaveCriticalSection(&m_oReadFunctionCriticalSection);*/
	
	disconnectDevice();


	m_bInited=false;
	
	Sleep(100);

	if(WSACleanup()==0)
	{	
		m_bClosed=true;
		return 1;
	}
	else
	{
		return -1;
	}
}

/**
 * Initializes device searching.
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::startSearchBTDevices()
{
	
	if(m_bSearchingNow)
	{
		return Devices_are_searching_now;
	}

	m_bSearchingNow=true;

	BLUETOOTH_FIND_RADIO_PARAMS l_oBTParams;
	l_oBTParams.dwSize=sizeof(l_oBTParams);

	HBLUETOOTH_RADIO_FIND l_oRadioFind;

	l_oRadioFind=BluetoothFindFirstRadio(&l_oBTParams,&m_hRadio);

	BLUETOOTH_RADIO_INFO l_oRadioInfo;

	memset(&l_oRadioInfo,0,sizeof(l_oRadioInfo));
	l_oRadioInfo.dwSize=sizeof(l_oRadioInfo);

	DWORD l_ulResult;

	l_ulResult=BluetoothGetRadioInfo(m_hRadio, &l_oRadioInfo);

	BluetoothFindRadioClose(l_oRadioFind);

	if(l_ulResult!=ERROR_SUCCESS) 
	{
		m_bSearchingNow=false;
		return Device_found_error;
	}

	// create device search thread
	m_hSearchThread=CreateThread(NULL,0,&searchFunction,this,0,NULL);

	Sleep(100);
	if(!m_hSearchThread) 
	{
		m_bSearchingNow=false;
		return Device_found_error;
	}
	
	return 1;

}

/**
 * Adds the new device if it is not on the list.
 * @param oDeviceInfo information about the device
 * @return true if the device was added
 */
bool CBTConnectionManager::AddIfAddressNotExist(BLUETOOTH_DEVICE_INFO& oDeviceInfo)
{
	
	if(m_nDeviceCount==0)
	{
		memcpy(&m_oFoundDevices[0],&oDeviceInfo,sizeof(oDeviceInfo));
		m_nDeviceCount++;
		return true;
	}
	else
	{
		bool l_bFound=false;
		for(int i=0;i<m_nDeviceCount;i++)
		{
			if(m_oFoundDevices[i].Address.ullLong==oDeviceInfo.Address.ullLong)
			{
				l_bFound=true;
			}
		}

		if(l_bFound==false)
		{
			memcpy(&m_oFoundDevices[m_nDeviceCount],&oDeviceInfo,sizeof(oDeviceInfo));
			m_nDeviceCount++;
			return true;
		}
	}
	
	return false;
}

/**
 * Sets the response from the server.
 * @param enRespondType response type
 * @param unRespond respose from the server
 * @return 1
 */
int CBTConnectionManager::setServerRespond(ResultType enRespondType,unsigned int unRespond)
{
	int l_nRespond=0;
	
	
	if(unRespond==0)
	{
		l_nRespond=1;
	}
	else
	{
			l_nRespond=(-1)*unRespond;
	}
	
	EnterCriticalSection(&m_oProtocolRespondCriticalSection);
	switch(enRespondType)
	{
		case RT_Connect:
			m_unConnectRespondCode=l_nRespond;
			break;
		case RT_Disconnect:
			m_unDisconnectRespondCode=l_nRespond;
			break;
		case RT_SendSMS:
			m_unSendSMSRespondCode=l_nRespond;
			break;
		case RT_MakeCall:
			m_unAcceptCallRespondCode=l_nRespond;
			break;
		case RT_AcceptCall:
			m_unAcceptCallRespondCode=l_nRespond;
			break;
		case RT_DropCall:
			m_unDropCallRespondCode=l_nRespond;
			break;
		case RT_GetState:
			m_unGetStateRespondCode=l_nRespond;
			break;
	}
	LeaveCriticalSection(&m_oProtocolRespondCriticalSection);

	return 1;
}

/**
 * Clears the response from the server.
 * @param enRespondType response type
 * @return 1
 */
int CBTConnectionManager::clearServerRespond(ResultType enRespondType)
{
	EnterCriticalSection(&m_oProtocolRespondCriticalSection);
	switch(enRespondType)
	{
		case RT_Connect:
			m_unConnectRespondCode=0;
			break;
		case RT_Disconnect:
			m_unDisconnectRespondCode=0;
			break;
		case RT_SendSMS:
			m_unSendSMSRespondCode=0;
			break;
		case RT_MakeCall:
			m_unAcceptCallRespondCode=0;
			break;
		case RT_AcceptCall:
			m_unAcceptCallRespondCode=0;
			break;
		case RT_DropCall:
			m_unDropCallRespondCode=0;
			break;
		case RT_GetState:
			m_unGetStateRespondCode=0;
			break;
	}
	m_bStopWait=false;
	LeaveCriticalSection(&m_oProtocolRespondCriticalSection);

	return 1;
}

/**
 * Waits for the server response.
 * @param enRespondType response type
 * @return response from the server
 */
int CBTConnectionManager::waitForServerRespond(ResultType enRespondType)
{
	bool l_bFinish=false;
	int l_nRespond;
	unsigned int l_unLoopConter=0;
	bool l_bStopWait=false;

	do
	{
		Sleep(10);
		l_unLoopConter++;
		EnterCriticalSection(&m_oProtocolRespondCriticalSection);
		l_nRespond=0;

		switch(enRespondType)
		{
			case RT_Connect:
				l_nRespond=m_unConnectRespondCode;
				break;
			case RT_Disconnect:
				l_nRespond=m_unDisconnectRespondCode;
				break;
			case RT_SendSMS:
				l_nRespond=m_unSendSMSRespondCode;
				break;
			case RT_MakeCall:
				l_nRespond=m_unAcceptCallRespondCode;
				break;
			case RT_AcceptCall:
				l_nRespond=m_unAcceptCallRespondCode;
				break;
			case RT_DropCall:
				l_nRespond=m_unDropCallRespondCode;
				break;
			case RT_GetState:
				l_nRespond=m_unGetStateRespondCode;
				break;
		}
		l_bStopWait=m_bStopWait;
		LeaveCriticalSection(&m_oProtocolRespondCriticalSection);

		if(l_nRespond!=0)
		{
			l_bFinish=true;
		}
		
		if(l_bStopWait)
		{
			l_nRespond=-1;
			l_bFinish=true;
		}
	
		if(l_unLoopConter>m_unMaxWaitTime)
		{
			l_nRespond=-1;
			l_bFinish=true;

		}

	}
	while(!l_bFinish);

	return l_nRespond;
}

/**
 * Stops waiting for the server response.
 */
void CBTConnectionManager::stopWaiting()
{
	EnterCriticalSection(&m_oProtocolRespondCriticalSection);
	m_bStopWait=true;
	LeaveCriticalSection(&m_oProtocolRespondCriticalSection);
}

/**
 * Connects to the device.
 * @param uint64DeviceAddress device Bluetooth address
 * @param nPort device port
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::connectDevice(unsigned _int64 uint64DeviceAddress,int nPort)
{
	if(m_bConnected)
	{
		return Device_is_connected;
	}
	
	m_bConnected=true;

	m_bCloseConnection=false;

	m_oLocalSocket= socket(AF_BTH, SOCK_STREAM, BTHPROTO_RFCOMM);
	if(m_oLocalSocket==INVALID_SOCKET)
	{
		m_bConnected=false;
		return Device_connect_error;
	}
	int l_nResult; 
	
	int l_nProtocolInfoSize=sizeof(m_oProtocolInfo);
	
	l_nResult =getsockopt(m_oLocalSocket, SOL_SOCKET, SO_PROTOCOL_INFO, (char*)&m_oProtocolInfo, &l_nProtocolInfoSize);
	
	if(l_nResult!=0)
	{
		int error = WSAGetLastError();
	}

	int l_nPort=0;

	if(nPort==Default_port)
	{
		l_nPort=SDPGetPort(uint64DeviceAddress);
		if(l_nPort<0)
		{
			return Device_connect_error;
		}

	}
	else
	{
		l_nPort=nPort;
	}

	SOCKADDR_BTH l_oServerSocketData = {0};
	l_oServerSocketData.addressFamily = AF_BTH;
    l_oServerSocketData.btAddr = (BTH_ADDR) uint64DeviceAddress;
    //l_oServerSocketData.port = 6; //BT_PORT_ANY;
	l_oServerSocketData.port = l_nPort;
	
	m_bReadThreadFinished=false;

	l_nResult=connect(m_oLocalSocket, (struct sockaddr *) &l_oServerSocketData, sizeof(SOCKADDR_BTH));
	if(l_nResult==SOCKET_ERROR)
	{
		int l_unResult=WSAGetLastError();
		closesocket(m_oLocalSocket);
		m_bConnected=false;
		return Device_connect_error;
	}
	
	m_uint64ConnectedDeviceAddress=uint64DeviceAddress;
		
	m_hReadThread=CreateThread(NULL,0,&readFunction,this,0,NULL);
	
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;

	l_pBuffer=m_oProtocolClass.buildRequestPacket(Init_Request,l_unBufferSize,true);
		//buildFrameWithoutOptionalData(CCIMsProtocolManager::m_unConnectCommand,l_unBufferSize,true);
	
	clearServerRespond(RT_Connect);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			return waitForServerRespond(RT_Connect);
		}
	}

		
	return No_respond_from_remote_device;

	return 1;
}

/**
 * Disconnects the device.
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::disconnectDevice()
{

	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}


	EnterCriticalSection(&m_oReadFunctionCriticalSection);
	m_bCloseConnection=true;		
	LeaveCriticalSection(&m_oReadFunctionCriticalSection);
	
	int l_nCounter=0;
	bool l_bFinish=false;

	do
	{
		
		bool l_bThreadFinished = false;
		EnterCriticalSection(&m_oFinishBTConnectionManager);
		l_bThreadFinished=m_bReadThreadFinished;
		LeaveCriticalSection(&m_oFinishBTConnectionManager);

		if(l_bThreadFinished==true)
		{
			l_bFinish=true;
		}
		else
		{
			Sleep(50);
			l_nCounter++;
			if(l_nCounter>40)
			{
				//l_bFinish=true;
			}
		}
		
	}
	while(!l_bFinish);

	//WaitForSingleObject(readFunction,INFINITE); 

	return 1;
}

/**
 * Sends the SMS.
 * @param recipientID phone ID
 * @param subject SMS content
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::sendSMS(LPWSTR recipientID, LPWSTR subject)
{
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;
	
	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}
	
	l_pBuffer=m_oProtocolClass.buildSendSMSPacket(recipientID,subject,l_unBufferSize);
	
	clearServerRespond(RT_SendSMS);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			return waitForServerRespond(RT_SendSMS);
		}
	}

		
	return No_respond_from_remote_device;
}

/**
 * Makes the phone call.
 * @param recipientID phone ID
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::makePhoneCall (LPWSTR recipientID)
{
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;
	
	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}

	l_pBuffer=m_oProtocolClass.buildMakeCallPacket(recipientID,l_unBufferSize);
	
	clearServerRespond(RT_MakeCall);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			return waitForServerRespond(RT_MakeCall);
		}
	}

		
	return No_respond_from_remote_device;
}

/**
 * Accepts the phone call.
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::acceptCall()
{
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;
	
	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}

	l_pBuffer=m_oProtocolClass.buildRequestPacket(AcceptCall_Request,l_unBufferSize);
	
	clearServerRespond(RT_AcceptCall);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			return waitForServerRespond(RT_AcceptCall);
		}
	}

		
	return No_respond_from_remote_device;
}

/**
 * Drops the phone call.
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::dropCall()
{
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;
	
	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}

	l_pBuffer=m_oProtocolClass.buildRequestPacket(DropCall_Request,l_unBufferSize);
	
	clearServerRespond(RT_DropCall);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			return waitForServerRespond(RT_DropCall);
		}
	}

		
	return No_respond_from_remote_device;
}

/**
 * Gets the phone state.
 * @phoneState state of the phone
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::getPhoneState(PhoneState &phoneState)
{
	char* l_pBuffer;
	unsigned int l_unBufferSize;
	int l_nDataSentLen;
	
	if(!m_bConnected)
	{
		return Device_is_not_connected;
	}

	l_pBuffer=m_oProtocolClass.buildRequestPacket(GetState_Request,l_unBufferSize);
	
	clearServerRespond(RT_GetState);

	l_nDataSentLen=send(m_oLocalSocket,l_pBuffer,l_unBufferSize,0);
	
	delete l_pBuffer;
	
	int l_nResult;

	if(l_nDataSentLen>0)
	{
		if(l_nDataSentLen==l_unBufferSize)
		{
			l_nResult= waitForServerRespond(RT_GetState);
			if(l_nResult<0)
			{
				return l_nResult;
			}
			else
			{
				phoneState=m_LastPhoneState;
				return 1;
			}
		}
	}
	
	return No_respond_from_remote_device;
}

/**
 * Decodes the packet.
 * @param pBuffer pointer to the buffer
 * @param nBufferLen size of the buffer
 * @param nOptionalDataSize size of the packet data
 * @return if the returned value is less then 0, the value is an error number.
 */
int CBTConnectionManager::decodePacket(char* pBuffer,int nBufferLen,int nOptionalDataSize)
{
	//Check the type of the packet and make appropriate action
	CIMProtocolError l_Error;
	uint l_unFeatureAddress;
	int l_unResult;
	l_unResult=m_oProtocolClass.checkPacket(pBuffer,nBufferLen,l_Error,l_unFeatureAddress);
	
	if(l_unResult<0)
	{
		return -1;
	}

	switch(l_unFeatureAddress)
	{
	case m_oProtocolClass.m_unUniqueSerialNumber:
		{
			break;
		}
	case m_oProtocolClass.m_unPhoneApplicationConfiguration_Init:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_Connect,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unPhoneApplicationConfiguration_Close:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_Disconnect,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unPhoneManager_MakeCall:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_MakeCall,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unPhoneManager_AcceptCall:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_AcceptCall,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unPhoneManager_DropCall:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_DropCall,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unPhoneManager_ChangeStateEvent:
		{
			PhoneState l_enPhoneState;
			uint l_unPhoneState;
			LPWSTR l_sCallID;
			uint l_unPhoneStateLen;

			m_oProtocolClass.getPhoneIDData(pBuffer,nBufferLen,l_sCallID,l_unPhoneStateLen,l_unPhoneState);
			
			switch(l_unPhoneState)
			{
			case m_oProtocolClass.m_unPhoneStateIdle:
				{
					l_enPhoneState=PS_IDLE;
					break;
				}
			case m_oProtocolClass.m_unPhoneStateRing:
				{
					l_enPhoneState=PS_RING;
					break;
				}
			case m_oProtocolClass.m_unPhoneStateConnected:
				{
					l_enPhoneState=PS_CONNECTED;
					break;
				}
			}
			
			if(m_pPhoneStateChanged!=NULL)
			{
				m_pPhoneStateChanged(l_enPhoneState,l_sCallID,m_pUserPointer);
			}

			delete[] l_sCallID;
			break;
		}
	case m_oProtocolClass.m_unPhoneManager_GetState:
		{
			uint l_unReplyCode;
			uint l_unPhoneState;
			m_oProtocolClass.getPhoneStateCommand(pBuffer,nBufferLen,l_unPhoneState,l_unReplyCode);

			switch(l_unPhoneState)
			{
			case m_oProtocolClass.m_unPhoneStateIdle:
				{
					m_LastPhoneState=PS_IDLE;
					break;
				}
			case m_oProtocolClass.m_unPhoneStateRing:
				{
					m_LastPhoneState=PS_RING;
					break;
				}
			case m_oProtocolClass.m_unPhoneStateConnected:
				{
					m_LastPhoneState=PS_CONNECTED;
					break;
				}
			}
			
			setServerRespond(RT_GetState,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unMessageManager_sendSMS:
		{
			uint l_unReplyCode;
			m_oProtocolClass.getErrorCode(pBuffer,nBufferLen,l_unReplyCode);
			setServerRespond(RT_SendSMS,l_unReplyCode);
			break;
		}
	case m_oProtocolClass.m_unMessageManager_ReceiveSMSEvent:
		{
			LPWSTR l_sSenderID;
			LPWSTR l_sSubject;
			uint l_unSenderLen,l_unSubjectLen;

			m_oProtocolClass.getSMSData(pBuffer,nBufferLen,l_sSenderID,l_unSenderLen,l_sSubject,l_unSubjectLen);
			if(m_pNewSMS!=NULL)
			{
				m_pNewSMS(l_sSenderID,l_sSubject,m_pUserPointer);
			}

			delete[] l_sSenderID;
			delete[] l_sSubject;
			
			break;
		}
	}

	

	return 1;
}

/**
 * Thread function, which reads incoming data.
 * @param lpParam pointer to the CBTConnectionManager class
 * @return 0
 */
DWORD WINAPI readFunction (LPVOID lpParam)
{

	CBTConnectionManager* l_pBTManager = (CBTConnectionManager*)lpParam;

	unsigned int l_unBufferSize=l_pBTManager->m_oProtocolClass.getMaxPacketSize();
	char * l_sReadBuffer=new char [l_unBufferSize];
	

	fd_set l_oReadSocketData;
	timeval l_oTimeInterval;
	bool l_bFinishFunction=false;

	bool l_bCloseFunction=false;

	for(;;)
	{
		if(l_bCloseFunction==false)
		{
			EnterCriticalSection(&l_pBTManager->m_oReadFunctionCriticalSection);
			l_bCloseFunction=l_pBTManager->m_bCloseConnection;
			LeaveCriticalSection(&l_pBTManager->m_oReadFunctionCriticalSection);
		}

		if(l_bCloseFunction)
		{
			break;
		}
		
		bool l_bGetData=false; 

		FD_ZERO(&l_oReadSocketData);
		FD_SET(l_pBTManager->m_oLocalSocket, &l_oReadSocketData);
		l_oTimeInterval.tv_sec = l_pBTManager->m_lIntervalInSeconds;
		l_oTimeInterval.tv_usec = l_pBTManager->m_lIntervalInUseconds;
		
		int l_nResult;
		
		int l_nIgnored=0;

		l_nResult=select(l_nIgnored,&l_oReadSocketData,NULL,NULL,&l_oTimeInterval);

		if(l_nResult==SOCKET_ERROR)
		{
			int l_nErrorCode;
			l_nErrorCode=WSAGetLastError();
		}
		else
		{
			if(l_nResult>0)
			{
				if(l_oReadSocketData.fd_count>=1)// CCIMsProtocolManager::getHeaderSize())
				{
					// there is data to get
					l_bGetData=true;
				}
			}
		}

		if(l_bGetData)
		{
			bool l_bFinish =false;
			char *l_pBufferIndex = &l_sReadBuffer[0];
			int l_nBufferLen=l_unBufferSize;
			unsigned int l_nReceivedBytes=0;
			bool l_bFirstReading=true;
			uint l_nDataSize=0;
			int l_nReadingCounter=0;
			bool l_bReadError=false;

			do
			{
				if(l_bCloseFunction==false)
				{
					EnterCriticalSection(&l_pBTManager->m_oReadFunctionCriticalSection);
					l_bCloseFunction=l_pBTManager->m_bCloseConnection;
					LeaveCriticalSection(&l_pBTManager->m_oReadFunctionCriticalSection);
				}

				if(l_bCloseFunction)
				{
					break;
				}

				int l_nResult;

				// get the data from socket
				l_nResult=recv(l_pBTManager->m_oLocalSocket,l_pBufferIndex,l_nBufferLen,0);

				if(l_nResult<1)
				{
					// no data or error
					if(l_nResult==0)
					{
						l_bFinishFunction=true;
						l_bFinish=true;
						l_bReadError=true;
					}
					else
					{
						int l_nErrorCode;
						l_nErrorCode=WSAGetLastError();
					}

				}
				else
				{
					l_nReceivedBytes=l_nReceivedBytes+l_nResult;

					//Check if there is enough data to read packed size
					if((l_bFirstReading)&&(l_nReceivedBytes>=CProtocolImplementation::getMinHeaderSize()))
					{
						l_bFirstReading=false;
						int l_unResult;
						l_unResult=l_pBTManager->m_oProtocolClass.checkHeader(l_sReadBuffer,l_nReceivedBytes,l_nDataSize);
						if(l_unResult<0)
						{
							/*
							if(l_nDataSize==CCIMsProtocolManager::m_nHeaderError)
							{
								l_nDataSize=(int)l_pBTManager->m_oProtocolManager.tryToFindHeaderBegin(l_pBufferIndex,l_nBufferLen,l_nReceivedBytes);
								
								if(l_nDataSize<-1)
								{
									l_bReadError=true;
									l_bFinish=true;
								}
								else
								{
									if(l_nDataSize==0)
									{
										if(l_nReceivedBytes<CCIMsProtocolManager::getMinHeaderSize())
										{
											l_bFirstReading =true;
										}
									}
								}
							}
							else
							{
								l_bReadError=true;
								l_bFinish=true;

							}*/

							l_bReadError=true;
							l_bFinish=true;


						}
					}


					// Check if you have all data
					if(l_nReceivedBytes<l_nDataSize+CProtocolImplementation::getHeaderSize()+CProtocolImplementation::m_unOptionalCRCChecksum_Size)
					{
						l_pBufferIndex=l_pBufferIndex+l_nReceivedBytes;
						l_nBufferLen=l_nBufferLen-l_nReceivedBytes;
					}
					else
					{
						l_bFinish=true;
					}

				}

				l_nReadingCounter++;
				if(l_nReadingCounter>CProtocolImplementation::m_nMaxReadAttempt)
				{
					l_bFinish=true;
					l_bReadError=true;
				}
			}
			while(!l_bFinish);

			int l_nResult=0;
			
			if(!l_bReadError)
			{
				l_nResult=l_pBTManager->decodePacket(l_sReadBuffer,l_nReceivedBytes,l_nDataSize);
			}

		}
		

		if(l_bFinishFunction)
		{
				break;
		}

	}


	int l_nResult;

	for(int i=0;i<100;i++)
	{
		l_nResult=closesocket(l_pBTManager->m_oLocalSocket);
		if(l_nResult==0)
		{
			break;
		}
		Sleep(1);
	}
	
	l_pBTManager->m_oLocalSocket=NULL;
	CloseHandle(l_pBTManager->m_hReadThread);
	l_pBTManager->m_hReadThread=NULL;
	l_pBTManager->stopWaiting();
	l_pBTManager->m_bConnected=false;
	
	delete[] l_sReadBuffer;

	EnterCriticalSection(&l_pBTManager->m_oFinishBTConnectionManager);
	l_pBTManager->m_bReadThreadFinished=true;
	LeaveCriticalSection(&l_pBTManager->m_oFinishBTConnectionManager);

	return 0;
}

/**
 * Thread function used in device search.
 * @param lpParam pointer to the CBTConnectionManager class
 * @return 1
 */
DWORD WINAPI searchFunction(LPVOID lpParam)
{
	//BT_PORT_ANY
	CBTConnectionManager* l_pBTManager = (CBTConnectionManager*)lpParam;
	int l_nInquiryTimeIndicator=5;
	
	l_pBTManager->m_nDeviceCount=0;
	for(int i=0;i<50;i++)
	{
		l_pBTManager->m_oFoundDevices[i].Address.ullLong=1;
	}

	memset(&l_pBTManager->m_oBTDeviceSearchParams,0,sizeof(l_pBTManager->m_oBTDeviceSearchParams));
	l_pBTManager->m_oBTDeviceSearchParams.cTimeoutMultiplier=l_nInquiryTimeIndicator;
	l_pBTManager->m_oBTDeviceSearchParams.dwSize=sizeof(l_pBTManager->m_oBTDeviceSearchParams);
	l_pBTManager->m_oBTDeviceSearchParams.fIssueInquiry=TRUE;
	l_pBTManager->m_oBTDeviceSearchParams.fReturnAuthenticated=TRUE;
	l_pBTManager->m_oBTDeviceSearchParams.fReturnConnected=TRUE;
	l_pBTManager->m_oBTDeviceSearchParams.fReturnRemembered=FALSE;
	l_pBTManager->m_oBTDeviceSearchParams.fReturnUnknown=TRUE;
	l_pBTManager->m_oBTDeviceSearchParams.hRadio=l_pBTManager->m_hRadio;

	BLUETOOTH_DEVICE_INFO l_oBTDeviceInfo;
	memset(&l_oBTDeviceInfo,0,sizeof(BLUETOOTH_DEVICE_INFO));

	l_oBTDeviceInfo.dwSize=sizeof(BLUETOOTH_DEVICE_INFO);

	l_pBTManager->m_hFoundDevice=BluetoothFindFirstDevice(&l_pBTManager->m_oBTDeviceSearchParams,&l_oBTDeviceInfo);
	
	Sleep(300);

	// search Bluetooth devices and add them to the list
	for(;;)
	{
		DWORD l_ulStartTick=GetTickCount();
		DWORD l_ulDelay=5000;

		for(;;)
		{
			DWORD l_ulResult;
			// get information about device
			l_ulResult=BluetoothGetDeviceInfo(l_pBTManager->m_hRadio, &l_oBTDeviceInfo);
			if(wcslen(l_oBTDeviceInfo.szName)>0)
			{
				break;
			}
			Sleep(1);
			if(GetTickCount()-l_ulStartTick>l_ulDelay)
			{
				break;
			}

		}

		// check if device with the device with found address already exist on the list, if not add the device
		l_pBTManager->AddIfAddressNotExist(l_oBTDeviceInfo);

		memset(&l_oBTDeviceInfo,0,sizeof(BLUETOOTH_DEVICE_INFO));
		l_oBTDeviceInfo.dwSize=sizeof(BLUETOOTH_DEVICE_INFO);
		BOOL l_bResult;

		// find the next device
		l_bResult=BluetoothFindNextDevice(l_pBTManager->m_hFoundDevice,&l_oBTDeviceInfo);

		//if no more devices stop searching
		if(!l_bResult) 
		{
			break;
		}

	}

	BluetoothFindDeviceClose(l_pBTManager->m_hFoundDevice);
	
	if(l_pBTManager->m_nDeviceCount>0)
	{
		for(int i=0;i<l_pBTManager->m_nDeviceCount;i++)
		{
			LPWSTR l_sDeviceName=new wchar_t [wcslen(l_pBTManager->m_oFoundDevices[i].szName)+1];
			wcscpy_s(l_sDeviceName,wcslen(l_pBTManager->m_oFoundDevices[i].szName)+1,l_pBTManager->m_oFoundDevices[i].szName);
			if(l_pBTManager->m_pDeviceFound!=NULL)
			{
				//call find device user callback
				l_pBTManager->m_pDeviceFound(l_pBTManager->m_oFoundDevices[i].Address.ullLong,l_sDeviceName,l_pBTManager->m_pUserPointer);
			}
			delete[]l_sDeviceName;

		}
	}

	if(l_pBTManager->m_pDeviceFound!=NULL)
	{
		// call find device callback with address==0 it means there is no more devices
		l_pBTManager->m_pDeviceFound(0,L"",l_pBTManager->m_pUserPointer);
	}
	
	CloseHandle(l_pBTManager->m_hSearchThread);
	l_pBTManager->m_hSearchThread=NULL;
	l_pBTManager->m_bSearchingNow=false;


	return 1;
}