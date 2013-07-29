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
#include "TaskManager.h"
#include "PhoneManager.h"
#include "Definitions.h"

IMPLEMENT_DYNCREATE(CTaskManager, CWinThread)

CTaskManager::CTaskManager()
{
	m_pMessageManager = new CMessageManager (this);
	m_pPhoneManager = new CPhoneManager(this);
	m_pBTConnectionManager = new CBTConnectionManager();
	m_pBTConnectionManager->setTaskManager(this);
}

CTaskManager::~CTaskManager(void)
{
	delete m_pBTConnectionManager;
	delete m_pMessageManager;
	delete m_pPhoneManager;
}

BOOL CTaskManager::InitInstance()
{
	bool PhoneState=false;
	bool MessagerState=false;

	m_pBTConnectionManager->m_bAutoDelete = FALSE;
	
	int l_nResult;

	l_nResult=m_pMessageManager->init();

	if(l_nResult>0)
	{
		PhoneState=true;
		m_pMainDialog->SendMessage(SHOW_MESSAGE_RESULT,0,0);
	}
	else
	{
		m_pMainDialog->SendMessage(SHOW_MESSAGE_RESULT,1,0);
	}
	
	l_nResult=m_pPhoneManager->init();
	
	if(l_nResult>0)
	{
		MessagerState=true;
		m_pMainDialog->SendMessage(SHOW_PHONE_RESULT,0,0);
	}
	else
	{
		m_pMainDialog->SendMessage(SHOW_PHONE_RESULT,1,0);
	}
	
	if(!m_pBTConnectionManager->CreateThread())
	{
		m_pMainDialog->SendMessage(SHOW_BLUETOOTH_RESULT,1,0);  //return -1;
	}

	return TRUE;
}

int CTaskManager::ExitInstance()
{
	m_pMessageManager->close();
	m_pPhoneManager->close();
	
	m_pBTConnectionManager->PostThreadMessage(WM_QUIT, 0, 0);
	WaitForSingleObject(m_pBTConnectionManager,INFINITE);

	return CWinThread::ExitInstance();
}

BEGIN_MESSAGE_MAP(CTaskManager, CWinThread)
	ON_THREAD_MESSAGE(NEW_SMS,newSmsIncomming)
	ON_THREAD_MESSAGE(SEND_SMS,sendSMS)
	ON_THREAD_MESSAGE(ANSWER_CALL,answerCall)
	ON_THREAD_MESSAGE(DROP_CALL,dropCall)
	ON_THREAD_MESSAGE(MAKE_CALL,makeCall)
	ON_THREAD_MESSAGE(PHONE_STATE_CHANGE,phoneStateChanged)
	ON_THREAD_MESSAGE(GET_PHONE_STATE,getPhoneState)
	ON_THREAD_MESSAGE(BLUETOOTH_RESULT,bluetoothResult)
	ON_THREAD_MESSAGE(PHONE_RESULT,phoneResult)
	ON_THREAD_MESSAGE(MESSAGE_RESULT,messageResult)
	ON_THREAD_MESSAGE(CONNECTED_RESULT,connectedResult)
	ON_THREAD_MESSAGE(PORT_RESULT,portResult)
END_MESSAGE_MAP()

void CTaskManager::connectedResult(WPARAM wParam, LPARAM lParam)
{
	m_pMainDialog->SendMessage(SHOW_CONNECTED_STATE,wParam,0);
}

void CTaskManager::portResult(WPARAM wParam, LPARAM lParam)
{
	m_pMainDialog->SendMessage(SHOW_PORT,wParam,0);
}


void CTaskManager::bluetoothResult(WPARAM wParam, LPARAM lParam)
{
	int l_nResult=(int)wParam;
	if(l_nResult>0)
	{
		m_pMainDialog->SendMessage(SHOW_BLUETOOTH_RESULT,0,0);
	}
	else
	{
		m_pMainDialog->SendMessage(SHOW_BLUETOOTH_RESULT,1,0);
	}
}

void CTaskManager::phoneResult(WPARAM wParam, LPARAM lParam)
{
	int l_nResult=(int)wParam;
	if(l_nResult>0)
	{
		m_pMainDialog->SendMessage(SHOW_PHONE_RESULT,0,0);
	}
	else
	{
		m_pMainDialog->SendMessage(SHOW_PHONE_RESULT,1,0);
	}
}

void CTaskManager::messageResult(WPARAM wParam, LPARAM lParam)
{
	int l_nResult=(int)wParam;
	if(l_nResult>0)
	{
		m_pMainDialog->SendMessage(SHOW_MESSAGE_RESULT,0,0);
	}
	else
	{
		m_pMainDialog->SendMessage(SHOW_MESSAGE_RESULT,1,0);
	}
}

void CTaskManager::newSmsIncomming(WPARAM wParam, LPARAM lParam)
{
	IncommingSMS* l_pNewSMS = (IncommingSMS*) wParam;

	CString l_sSender = l_pNewSMS->m_sSender;
	CString l_sContent = l_pNewSMS->m_sContent;
	
	m_pBTConnectionManager->sendNewSMSEvent(l_sSender,l_sContent);
	



	delete l_pNewSMS;
}

void CTaskManager::makeCall(WPARAM wParam, LPARAM lParam)
{
	MakeCall* l_pMakeCall=(MakeCall *)wParam;
	int l_nResult;
	l_nResult=m_pPhoneManager->call(l_pMakeCall->m_sRecipientID);
	if(l_nResult<0)
	{
		int l_nErrorCode=(-1)*l_nResult;
		m_pBTConnectionManager->sendRespond(MakeCall_Reply,(-1)*l_nErrorCode);
	}
	else
	{
		m_pBTConnectionManager->sendRespond(MakeCall_Reply,0);
	}
	delete l_pMakeCall;
}

void CTaskManager::answerCall(WPARAM wParam, LPARAM lParam)
{
	int l_nResult;
	
	l_nResult=m_pPhoneManager->answer();

	if(l_nResult<0)
	{
		int l_nErrorCode=(-1)*l_nResult;
		m_pBTConnectionManager->sendRespond(AcceptCall_Reply,(-1)*l_nErrorCode);
	}
	else
	{
		m_pBTConnectionManager->sendRespond(AcceptCall_Reply,0);
	}
}

void CTaskManager::dropCall(WPARAM wParam, LPARAM lParam)
{
	int l_nResult;
	
	l_nResult=m_pPhoneManager->drop();

	if(l_nResult<0)
	{
		int l_nErrorCode=(-1)*l_nResult;
		m_pBTConnectionManager->sendRespond(DropCall_Reply,(-1)*l_nErrorCode);
	}
	else
	{
		m_pBTConnectionManager->sendRespond(DropCall_Reply,0);
	}
}

void CTaskManager::sendSMS(WPARAM wParam, LPARAM lParam)
{
	OutgoingSMS* l_pSendingSMS = (OutgoingSMS*) wParam;

	int l_nResult;
	l_nResult=m_pMessageManager->sendMessage(l_pSendingSMS->m_sRecipient,l_pSendingSMS->m_sContent);
	
	if(l_nResult<0)
	{
		int l_nErrorCode=(-1)*l_nResult;
		m_pBTConnectionManager->sendRespond(SendSMS_Reply,(-1)*l_nErrorCode);
	}
	else
	{
		m_pBTConnectionManager->sendRespond(SendSMS_Reply,0);
	}
	
	delete l_pSendingSMS;
}


void CTaskManager::phoneStateChanged(WPARAM wParam, LPARAM lParam)
{
	PhoneState l_enPhoneState=(PhoneState)wParam;
	CString l_sCallID = m_pPhoneManager->getCallID();
	m_pBTConnectionManager->sendPhoneStateChange(l_enPhoneState,l_sCallID);

}

void CTaskManager::getPhoneState(WPARAM wParam, LPARAM lParam)
{
	PhoneState l_oPhoneState;
	l_oPhoneState = m_pPhoneManager->getPhoneState();

	m_pBTConnectionManager->sendStateRespond(l_oPhoneState,0);
}

void CTaskManager::setMainDialog(CDialog* pMainDialog)
{
	m_pMainDialog=pMainDialog;
}