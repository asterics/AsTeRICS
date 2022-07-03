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
#include "ProtocolImplementation.h"
#include "PhoneManager.h"
#include <ws2bth.h>
//#include <winbase.h>

DWORD WINAPI connectionFunction( LPVOID lpParam );



class CBTConnectionManager :
	public CWinThread
{
DECLARE_DYNCREATE(CBTConnectionManager)
public:
	CBTConnectionManager(void);
	~CBTConnectionManager(void);
	virtual BOOL InitInstance();
	virtual int ExitInstance();
	int init();
	void setTaskManager(CWinThread* pTaskManager);
	int sendRespond(ReplyType Reply,unsigned int nRespond);
	int sendStateRespond(PhoneState State,uint unRespond);
	int sendPhoneStateChange(unsigned int unPhoneState,CString sCallID);
	int sendNewSMSEvent(CString sSenderID,CString sSubject);
	void setDeviceStates(bool bPhoneState,bool bMessagerState);
protected:
	DECLARE_MESSAGE_MAP()

private:
	bool m_bPhoneState;
	bool m_bMessagerState;
	int m_nServerPort;
	SOCKET m_oServerSocket;
	SOCKET m_oConnectionSocket;
	HANDLE m_hConnectionHanlde;
	bool waitForConnection();
	CWinThread* m_pTaskManager;
	const long m_lIntervalInSeconds;
	const long m_lIntervalInUseconds;
	//static __int16 getFrameData(char* pBuffer,int nDataStartPosition, int nDataLen);

	//CCIMsProtocolManager m_oProtocolManager;
	CProtocolImplementation m_oProtocolClass;

	int decodePacket(char* pBuffer,int nBufferLen,int nOptionalDataSize);
	void sendData(WPARAM wParam, LPARAM lParam);
	void waitForConnection(WPARAM wParam, LPARAM lParam);
	bool m_bServerConnected;
	bool m_bClose;

	int bluetoothSetService();
	
	SOCKADDR_BTH l_oServerSocketData;

	CRITICAL_SECTION m_oReadFunctionCriticalSection;
friend DWORD WINAPI connectionFunction( LPVOID lpParam );
};
