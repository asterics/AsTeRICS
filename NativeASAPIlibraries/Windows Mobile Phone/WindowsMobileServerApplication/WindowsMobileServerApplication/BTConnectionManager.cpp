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

#include "StdAfx.h"
#include "BTConnectionManager.h"
#include "Definitions.h"
#include <bthapi.h>



IMPLEMENT_DYNCREATE(CBTConnectionManager, CWinThread)


CBTConnectionManager::CBTConnectionManager(void) :m_oProtocolClass(Server_Site), m_lIntervalInSeconds(0),m_lIntervalInUseconds(300000)
{
	m_bServerConnected=false;
	m_bClose=false;
	InitializeCriticalSection(&m_oReadFunctionCriticalSection);
}

CBTConnectionManager::~CBTConnectionManager(void)
{
	DeleteCriticalSection(&m_oReadFunctionCriticalSection);
}

BOOL CBTConnectionManager::InitInstance()
{
	int l_nResult;

	l_nResult=init();
	
	if(l_nResult<0)
	{
		m_pTaskManager->PostThreadMessage(BLUETOOTH_RESULT,l_nResult,0);
	}

	return TRUE;
}

int CBTConnectionManager::ExitInstance()
{
	
	
	EnterCriticalSection(&m_oReadFunctionCriticalSection);
	m_bClose=true;	
	LeaveCriticalSection(&m_oReadFunctionCriticalSection);

	return CWinThread::ExitInstance();
	

}

void CBTConnectionManager::setTaskManager(CWinThread* pTaskManager)
{
	m_pTaskManager=pTaskManager;
}

void CBTConnectionManager::setDeviceStates(bool bPhoneState,bool bMessagerState)
{
	m_bPhoneState=bPhoneState;
	m_bMessagerState=bMessagerState;
}

BEGIN_MESSAGE_MAP(CBTConnectionManager, CWinThread)
	ON_THREAD_MESSAGE(SEND_DATA,sendData)
	ON_THREAD_MESSAGE(WAIT_FOR_CONNECTION,waitForConnection)
END_MESSAGE_MAP()

void CBTConnectionManager::waitForConnection(WPARAM wParam, LPARAM lParam)
{
	if(!m_bClose)
	{
		m_bServerConnected=false;
		m_pTaskManager->PostThreadMessage(CONNECTED_RESULT,1,0);
		waitForConnection();
	}
}

void CBTConnectionManager::sendData(WPARAM wParam, LPARAM lParam)
{
	DataToSend* l_pDataToSend = (DataToSend*) wParam;
	
	int l_nDataSentLen;

	l_nDataSentLen=send(m_oConnectionSocket,l_pDataToSend->l_pPacket,l_pDataToSend->l_unPacketSize,0);
	

	if(l_nDataSentLen<0)
	{
	
	}
	else
	{
		/*
		if(l_nDataSentLen < l_pDataToSend->l_unPacketSize)
		{
		
		}*/
	}

	delete l_pDataToSend;

}

int CBTConnectionManager::bluetoothSetService()
{
   ULONG recordHandle = 0;	
   #define SDP_RECORD_SIZE  77
    
   // Please don't ask me about this record. I haven't got Platform Builder to create my own record. This record was found in Internet.
    BYTE rgbSdpRecord[] = {
	0x35, 0x4b, 0x09, 0x00, 0x01, 0x35, 0x19, 0x19, 
	0x11, 0x01, 0x1a, 0x66, 0x50, 0x00, 0x00, 0x1c, 
	0x00, 0x00, 0x11, 0x01, 0x00, 0x00, 0x10, 0x00, 
	0x80, 0x00, 0x00, 0x80, 0x5f, 0x9b, 0x34, 0xfb, 
	0x09, 0x00, 0x04, 0x35, 0x11, 0x35, 0x03, 0x19, 
	0x01, 0x00, 0x35, 0x03, 0x19, 0x00, 0x01, 0x35, 
	0x05, 0x19, 0x00, 0x03, 0x08, 
	0x01, //channel
	0x09, 0x01, 
	0x00, 0x25, 0x12, 0x53, 0x65, 0x72, 0x69, 0x61, 
	0x6c, 0x50, 0x6f, 0x72, 0x74, 0x5f, 0x62, 0x79, 
	0x5f, 0x6c, 0x76, 0x6c, 0x68
	};

	rgbSdpRecord[53]=m_nServerPort;
   
	int a=sizeof(BTHNS_SETBLOB);
	a++;
   struct 
   {
      BTHNS_SETBLOB   b;
      unsigned char   uca[SDP_RECORD_SIZE];
   } bigBlob;
   ULONG ulSdpVersion = BTH_SDP_VERSION;
   bigBlob.b.pRecordHandle   = &recordHandle;
   bigBlob.b.pSdpVersion     = &ulSdpVersion;
   bigBlob.b.fSecurity       = 0;
   bigBlob.b.fOptions        = 0;
   bigBlob.b.ulRecordLength  = SDP_RECORD_SIZE;
   memcpy (bigBlob.b.pRecord, rgbSdpRecord, SDP_RECORD_SIZE);
   
   BLOB blob;
   blob.cbSize    = sizeof(BTHNS_SETBLOB) + SDP_RECORD_SIZE - 1;
   blob.pBlobData = (PBYTE) &bigBlob;

   CSADDR_INFO l_oSockInfo;
	
   l_oSockInfo.iProtocol=BTHPROTO_RFCOMM;
   l_oSockInfo.iSocketType=SOCK_STREAM;
   l_oSockInfo.LocalAddr.lpSockaddr=(SOCKADDR *)&l_oServerSocketData;
   l_oSockInfo.LocalAddr.iSockaddrLength=sizeof(l_oServerSocketData);
   l_oSockInfo.RemoteAddr.lpSockaddr=(SOCKADDR *)&l_oServerSocketData;
   l_oSockInfo.RemoteAddr.iSockaddrLength=sizeof(l_oServerSocketData);
	
   WSAQUERYSET Service;
   memset (&Service, 0, sizeof(Service));
   Service.dwSize = sizeof(Service);
   Service.lpBlob = &blob;
   Service.dwNameSpace = NS_BTH;
   Service.lpcsaBuffer=&l_oSockInfo;

   if (WSASetService(&Service,RNRSERVICE_REGISTER,0) == SOCKET_ERROR)
   {
      int a=WSAGetLastError();
	  return Remote_default_error;
   }
   else
   {
      return 1;
   }
}

int CBTConnectionManager::init()
{
	
	WSADATA l_oSocketData;
	WSAStartup (MAKEWORD(1,0), &l_oSocketData);
	
	int l_nResult;

	m_oServerSocket = socket (AF_BT, SOCK_STREAM, BTHPROTO_RFCOMM);
	
	
	SOCKADDR_BTH l_oSocketAddres;
	
	memset (&l_oSocketAddres, 0, sizeof(l_oSocketAddres));
	l_oSocketAddres.addressFamily = AF_BT;
	l_oSocketAddres.port = 0;
	//l_oSocketAddres.serviceClassId=testGuid1;
	
	
	if (bind (m_oServerSocket, (SOCKADDR *)&l_oSocketAddres, sizeof(l_oSocketAddres))) 
	{
  
		int l_nError = WSAGetLastError();
		closesocket (m_oServerSocket);
		return Bluetooth_init_error;
	}
	
	
	//!SOCKADDR_BTH l_oServerSocketData;
	int l_nServerSocketDataSize=sizeof(l_oServerSocketData);

	if(getsockname (m_oServerSocket, (SOCKADDR *)&l_oServerSocketData, &l_nServerSocketDataSize))
	{
		closesocket (m_oServerSocket);
		return Bluetooth_init_error;
	}
	else
	{
		m_nServerPort=l_oServerSocketData.port;
	}
	
	m_pTaskManager->PostThreadMessage(PORT_RESULT,m_nServerPort,0);

	//int l_nResult;

	
	l_nResult=bluetoothSetService();

	if(l_nResult<0)
	{
		int l_nError=WSAGetLastError();
		return Bluetooth_init_error;
	}
	
	
	if (listen (m_oServerSocket, 2))
	{
	
		closesocket (m_oServerSocket);
		int l_nError=WSAGetLastError();
		return Bluetooth_init_error;
	}
	
	m_pTaskManager->PostThreadMessage(BLUETOOTH_RESULT,1,0);

	waitForConnection();

	return 1;

}

bool CBTConnectionManager::waitForConnection()
{

	

	SOCKADDR_BTH l_oConnectionSocketData;
	int l_nConnectionSocketDataSize = sizeof(l_oConnectionSocketData);

	m_oConnectionSocket = accept (m_oServerSocket, (SOCKADDR *)&l_oConnectionSocketData, &l_nConnectionSocketDataSize);
	m_hConnectionHanlde=::CreateThread(NULL,0,&connectionFunction,this,0,NULL);
	
	m_bServerConnected=true;
	m_pTaskManager->PostThreadMessage(CONNECTED_RESULT,0,0);

	return true;

}

/*
__int16 getFrameData(char* pBuffer,int nDataStartPosition, int nDataLen)
{
	__int16 l_rResult =0;

	if((nDataLen<1)||(nDataLen>8))
	{
		return -1;
	}

	for(int i=0;i<nDataLen;i++)
	{
		__int16 l_nTempData=0;
		l_rResult=l_rResult+(pBuffer[i+nDataStartPosition]<<(8*i));
	
	}

	return l_rResult;
}*/

int CBTConnectionManager::decodePacket(char* pBuffer,int nBufferLen,int nOptionalDataSize)
{
	
	CIMProtocolError l_Error;
	uint l_unFeatureAddress;
	int l_unResult;
	
	// Decode packet and make action

	l_unResult=m_oProtocolClass.checkPacket(pBuffer,nBufferLen,l_Error,l_unFeatureAddress);
	
	if(l_unResult<0)
	{
		return Packet_error;
	}
	
	switch(l_unFeatureAddress)
	{
		case m_oProtocolClass.m_unPhoneApplicationConfiguration_Init:
			{
				if(m_bPhoneState==false)
				{
					if(m_bMessagerState==false)
					{
						sendRespond(Init_Reply,(-1)*Phone_and_Messager_no_initialized);
					}
					else
					{
						sendRespond(Init_Reply,(-1)*Phone_no_initialized);
					}
				}
				else
				{
					if(m_bMessagerState==false)
					{
						sendRespond(Init_Reply,(-1)*Messager_no_initialized);
					}
					else
					{
						sendRespond(Init_Reply,0);
					}
				}

				
				break;
			}
		case m_oProtocolClass.m_unPhoneApplicationConfiguration_Close:
			{
				break;
			}
		case m_oProtocolClass.m_unPhoneManager_MakeCall:
			{
				LPTSTR l_sRecipientID;
				uint l_unIDSize, l_unState;
				m_oProtocolClass.getPhoneIDData(pBuffer,nBufferLen,l_sRecipientID,l_unIDSize,l_unState);
				MakeCall* l_pCallData = new MakeCall();
				l_pCallData->m_sRecipientID.Format(L"%s",l_sRecipientID);
				delete[] l_sRecipientID;
				m_pTaskManager->PostThreadMessageW(MAKE_CALL,(WPARAM)l_pCallData,NULL);
				break;
			}
		case m_oProtocolClass.m_unPhoneManager_AcceptCall:
			{
				m_pTaskManager->PostThreadMessageW(ANSWER_CALL,NULL,NULL);
				break;
			}
		case m_oProtocolClass.m_unPhoneManager_DropCall:
			{
				m_pTaskManager->PostThreadMessageW(DROP_CALL,NULL,NULL);
				break;
			}
		case m_oProtocolClass.m_unPhoneManager_GetState:
			{
				m_pTaskManager->PostThreadMessageW(GET_PHONE_STATE,NULL,NULL);
				break;
			}
		case m_oProtocolClass.m_unMessageManager_sendSMS:
			{
				LPTSTR l_sRecipientID;
				LPTSTR l_sSubject;
				uint l_unIDSize,l_unSubjectSize;
				m_oProtocolClass.getSMSData(pBuffer,nBufferLen,l_sRecipientID,l_unIDSize,l_sSubject,l_unSubjectSize);
				OutgoingSMS *l_pSMSData = new OutgoingSMS();
				l_pSMSData->m_sContent.Format(L"%s",l_sSubject);
				l_pSMSData->m_sRecipient.Format(L"%s",l_sRecipientID);
				delete[] l_sRecipientID;
				delete[] l_sSubject;
				m_pTaskManager->PostThreadMessageW(SEND_SMS,(WPARAM)l_pSMSData,NULL);
				break;
			}

	}

	

	return 1;
}


int CBTConnectionManager::sendStateRespond(PhoneState State,uint unRespond)
{
	DataToSend* l_pRespondPacket = new DataToSend();
	uint l_unBufferSize;
	uint l_unState;
	
	switch(State)
	{
	case PS_IDLE:
		l_unState=m_oProtocolClass.m_unPhoneStateIdle;
		break;
	case PS_RING:
		l_unState=m_oProtocolClass.m_unPhoneStateRing;
		break;
	case PS_CONNECTED:
		l_unState=m_oProtocolClass.m_unPhoneStateConnected;
		break;
	}

	l_pRespondPacket->l_pPacket =m_oProtocolClass.buildStateReplyPacket(None_Error,l_unState,unRespond,l_unBufferSize);
	l_pRespondPacket->l_unPacketSize=l_unBufferSize;
	this->PostThreadMessageW(SEND_DATA,(WPARAM)l_pRespondPacket,NULL);
	return 1;
}


int CBTConnectionManager::sendRespond(ReplyType Reply,unsigned int nRespond)
{
	DataToSend* l_pRespondPacket = new DataToSend();
	uint l_unBufferSize;
	l_pRespondPacket->l_pPacket =m_oProtocolClass.buildReplyPacket(Reply,None_Error,nRespond,l_unBufferSize);
	l_pRespondPacket->l_unPacketSize=l_unBufferSize;
	this->PostThreadMessageW(SEND_DATA,(WPARAM)l_pRespondPacket,NULL);
	return 1;
}

int CBTConnectionManager::sendPhoneStateChange(unsigned int unPhoneState,CString sCallID)
{
	DataToSend* l_pRespondPacket = new DataToSend();
	
	LPTSTR l_sCallID, l_sCallIDTemp;
	l_sCallIDTemp = sCallID.GetBuffer();
	l_sCallID = new TCHAR[sCallID.GetLength()+1];
	wcscpy_s(l_sCallID,sCallID.GetLength()+1,l_sCallIDTemp);
	sCallID.ReleaseBuffer();
	
	uint l_unPacketSize; 

	l_pRespondPacket->l_pPacket =m_oProtocolClass.buildPhoneStateEventPacket(unPhoneState,l_sCallID,l_unPacketSize);
	l_pRespondPacket->l_unPacketSize=l_unPacketSize;
	delete[] l_sCallID;

	this->PostThreadMessageW(SEND_DATA,(WPARAM)l_pRespondPacket,NULL);
	return 1;
}

int CBTConnectionManager::sendNewSMSEvent(CString sSenderID,CString sSubject)
{
	DataToSend* l_pRespondPacket = new DataToSend();

	LPTSTR l_sSenderID, l_sSenderIDTemp;
	LPTSTR l_sSubject, l_sSubjectTemp;

	l_sSenderIDTemp = sSenderID.GetBuffer();
	l_sSenderID = new TCHAR[sSenderID.GetLength()+1];
	wcscpy_s(l_sSenderID,sSenderID.GetLength()+1,l_sSenderIDTemp);
	sSenderID.ReleaseBuffer();
	
	l_sSubjectTemp = sSubject.GetBuffer();
	l_sSubject = new TCHAR[sSubject.GetLength()+1];
	wcscpy_s(l_sSubject,sSubject.GetLength()+1,l_sSubjectTemp);
	sSubject.ReleaseBuffer();

	uint l_unPacketSize;
	l_pRespondPacket->l_pPacket = m_oProtocolClass.buildReceiveSMSEventPacket(l_sSenderID,l_sSubject,l_unPacketSize);
	l_pRespondPacket->l_unPacketSize=l_unPacketSize;

	delete[] l_sSenderID;
	delete[] l_sSubject;

	this->PostThreadMessageW(SEND_DATA,(WPARAM)l_pRespondPacket,NULL);

	return 1;
}

// This function read the Bluetooth data
DWORD WINAPI connectionFunction( LPVOID lpParam )
{
	CBTConnectionManager* l_pBTConnectionManager = (CBTConnectionManager*)lpParam;
	
	unsigned int l_unBufferSize=l_pBTConnectionManager->m_oProtocolClass.getMaxPacketSize();
	char * l_sReadBuffer=new char [l_unBufferSize];


	fd_set l_oReadSocketData;
	timeval l_oTimeInterval;
	bool l_bFinishFunction=false;

	for(;;)
	{
		EnterCriticalSection(&l_pBTConnectionManager->m_oReadFunctionCriticalSection);

		bool l_bCloseFunction=l_pBTConnectionManager->m_bClose;
		LeaveCriticalSection(&l_pBTConnectionManager->m_oReadFunctionCriticalSection);

		LeaveCriticalSection(&l_pBTConnectionManager->m_oReadFunctionCriticalSection);

		if(l_bCloseFunction)
		{
			break;
		}
		
		bool l_bGetData=false; 

		FD_ZERO(&l_oReadSocketData);
		FD_SET(l_pBTConnectionManager->m_oConnectionSocket, &l_oReadSocketData);
		l_oTimeInterval.tv_sec = l_pBTConnectionManager->m_lIntervalInSeconds;
		l_oTimeInterval.tv_usec = l_pBTConnectionManager->m_lIntervalInUseconds;
		
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
				if(l_oReadSocketData.fd_count>=1) //CCIMsProtocolManager::getHeaderSize())
				{
					l_bGetData=true;
				}
			}
		}

		if(l_bGetData)
		{
			char *l_pBufferIndex = &l_sReadBuffer[0];
			int l_nBufferLen=l_unBufferSize;
			unsigned int l_nReceivedBytes=0;
			bool l_bFirstReading=true;
			bool l_bFinish =false;
			uint l_nDataSize=0;
			int l_nReadingCounter=0;
			bool l_bReadError=false;

			do
			{
				int l_nResult;
				l_nResult=recv(l_pBTConnectionManager->m_oConnectionSocket,l_pBufferIndex,l_nBufferLen,0);

				if(l_nResult<1)
				{
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
						int a=0;
						a++;
					}

				}
				else
				{
					l_nReceivedBytes=l_nReceivedBytes + l_nResult;

					if((l_bFirstReading)&&(l_nReceivedBytes>=CProtocolImplementation::getMinHeaderSize()))
					{
						l_bFirstReading =false;
						int l_unResult;
						l_unResult=l_pBTConnectionManager->m_oProtocolClass.checkHeader(l_sReadBuffer,l_nReceivedBytes,l_nDataSize);

						if(l_unResult<0)
						{
							/*
							if(l_nDataSize==CCIMsProtocolManager::m_nHeaderError)
							{
								l_nDataSize=(int)l_pBTConnectionManager->m_oProtocolManager.tryToFindHeaderBegin(l_pBufferIndex,l_nBufferLen,l_nReceivedBytes);
								
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
			
			if(!l_bReadError)
			{
				int l_nResult=0;
				l_nResult=l_pBTConnectionManager->decodePacket(l_sReadBuffer,l_nReceivedBytes,l_nDataSize);
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
		l_nResult=closesocket(l_pBTConnectionManager->m_oConnectionSocket);
		if(l_nResult==0)
		{
			break;
		}
		Sleep(1);
	}
	
	l_pBTConnectionManager->m_oConnectionSocket=NULL;
	CloseHandle(l_pBTConnectionManager->m_hConnectionHanlde);
	l_pBTConnectionManager->m_hConnectionHanlde=NULL;
	

	l_pBTConnectionManager->PostThreadMessageW(WAIT_FOR_CONNECTION,NULL,NULL);
	delete[] l_sReadBuffer;
	return 0;
}