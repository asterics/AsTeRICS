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
#include "MessageManager.h"
#include "Error.h"
#include <atlconv.h>

CMessageManager::CMessageManager(CWinThread* pTaskManager)
{
	m_pTaskManager=pTaskManager;
	m_bState=false;
	m_oAdviseSink.m_bMessageState=m_bState;
}

CMessageManager::~CMessageManager(void)
{
}

int CMessageManager::sendMessage(CString sRecipientID, CString sMessageContent)
{
	if(m_bState==false)
	{
		return Messager_no_initialized;
	}
	
	HRESULT l_Result;
	
	CComPtr<IMessage> l_pMessage;
	
	l_Result=m_pSmsOutputFolder->CreateMessage(NULL,0,&l_pMessage);

	if(FAILED(l_Result))
	{
		return Messager_send_message_error;
	}

	SPropValue l_oRecipient[3];

	l_oRecipient[0].ulPropTag=PR_RECIPIENT_TYPE;
	l_oRecipient[0].Value.l=MAPI_TO;

	l_oRecipient[1].ulPropTag = PR_ADDRTYPE;
	l_oRecipient[1].Value.lpszW = _T("SMS");
	
	l_oRecipient[2].ulPropTag = PR_EMAIL_ADDRESS;
	
	LPWSTR l_psRecipientID = T2W(sRecipientID.GetBuffer(0));
	sRecipientID.ReleaseBuffer();

	l_oRecipient[2].Value.lpszW = (LPWSTR)l_psRecipientID;

	ADRLIST l_oAddressList;
	l_oAddressList.cEntries = 1;
	l_oAddressList.aEntries[0].cValues = 3;
	l_oAddressList.aEntries[0].rgPropVals = (LPSPropValue)(&l_oRecipient);

	l_Result=l_pMessage->ModifyRecipients(MODRECIP_ADD,&l_oAddressList);
	
	if(FAILED(l_Result))
	{
		return Messager_send_message_error;
	}

	SPropValue l_oSmsPropertyValues[3];
	ZeroMemory(&l_oSmsPropertyValues, sizeof(SPropValue));

	l_oSmsPropertyValues[0].ulPropTag = PR_MESSAGE_FLAGS;
    l_oSmsPropertyValues[0].Value.ul = MSGFLAG_FROMME | MSGFLAG_UNSENT;

	l_oSmsPropertyValues[1].ulPropTag = PR_MSG_STATUS;
	l_oSmsPropertyValues[1].Value.ul = MSGSTATUS_RECTYPE_SMS;

	l_oSmsPropertyValues[2].ulPropTag = PR_SUBJECT;


	LPWSTR l_psMessage = T2W(sMessageContent.GetBuffer(0));
	sMessageContent.ReleaseBuffer();
	
	l_oSmsPropertyValues[2].Value.lpszW = (LPWSTR)l_psMessage;

	l_Result = l_pMessage->SetProps(sizeof(l_oSmsPropertyValues) / sizeof(l_oSmsPropertyValues[0]), (LPSPropValue)&l_oSmsPropertyValues, NULL);
	
	if(FAILED(l_Result))
	{
		return Messager_send_message_error;
	}
	

	l_Result = l_pMessage->SubmitMessage(0);
	

	if(FAILED(l_Result))
	{
		return Messager_send_message_error;
	}

	return 1;
}

int CMessageManager::init()
{
	HRESULT l_Result;

	l_Result=MAPIInitialize(NULL);

	if(FAILED(l_Result))
	{
		return Messager_init_error;
	}

	l_Result=MAPILogonEx(0 ,NULL, NULL, 0, &m_pMapiSession);
	
	if(FAILED(l_Result))
	{
		return Messager_init_error;	
	}

	m_pMessageStore=NULL;
	
	l_Result=getSmsMessageStore(m_pMapiSession,m_pMessageStore);
	
	if(FAILED(l_Result)||m_pMessageStore==NULL)
	{
		return Messager_init_error;
	}

	ULONG l_oPropertyTagArray[] = {1,PR_IPM_OUTBOX_ENTRYID};
	ULONG l_ulValueCount;
	LPSPropValue l_oPropertyValues;

	l_Result=m_pMessageStore->GetProps((LPSPropTagArray)l_oPropertyTagArray,MAPI_UNICODE,&l_ulValueCount,&l_oPropertyValues);

	if(FAILED(l_Result))
	{
		return Messager_init_error;
	}
	
	l_Result=m_pMapiSession->OpenEntry(l_oPropertyValues[0].Value.bin.cb,(LPENTRYID)l_oPropertyValues[0].Value.bin.lpb,NULL,0,NULL,(LPUNKNOWN*)&m_pSmsOutputFolder);
    
	if(FAILED(l_Result))
	{
		return Messager_init_error;
	}

	MAPIFreeBuffer(l_oPropertyValues);
	
	ULONG l_ulEntryIdByteCount = 0;
	LPENTRYID l_pEntryId = NULL;

	l_Result=m_pMessageStore->GetReceiveFolder(NULL, MAPI_UNICODE, &l_ulEntryIdByteCount, &l_pEntryId, NULL);
	if(FAILED(l_Result))
	{
		return Messager_init_error;
	}


	ULONG l_ulObjectType = 0;
	
	l_Result=m_pMessageStore->OpenEntry(l_ulEntryIdByteCount, l_pEntryId, NULL, 0, &l_ulObjectType, (LPUNKNOWN*)&m_pSmsReceiveFolder);
	if(FAILED(l_Result))
	{
		return Messager_init_error;
	}
	
	m_oAdviseSink.init(m_pMapiSession,m_pTaskManager);
	
	l_Result=m_pMessageStore->Advise(0, NULL,fnevObjectCreated,&m_oAdviseSink, &m_ulConnection);
	
	if(l_Result!=S_OK)
	{
		return Messager_init_error;
	}

	m_bState=true;
	m_oAdviseSink.m_bMessageState=m_bState;

	return 1;


}

void CMessageManager::close()
{
	m_bState=false;
	m_oAdviseSink.m_bMessageState=m_bState;

	m_pMessageStore->Unadvise(m_ulConnection);
	//m_pMessageStore->Release();
	//m_pSmsOutputFolder->Release();
	//m_pSmsReceiveFolder->Release();
	m_pMapiSession->Logoff(0,0,0);
	m_pMapiSession.Release();
	MAPIUninitialize();
}

// This function looking for SMS inbox
HRESULT CMessageManager::getSmsMessageStore(const ATL::CComPtr<IMAPISession> &pMapiSession, ATL::CComPtr<IMsgStore> &pMessageStore)
{
	CComPtr<IMAPITable> l_pImapiTable;
	HRESULT l_Result = pMapiSession->GetMsgStoresTable(MAPI_UNICODE,&l_pImapiTable);

	if (FAILED(l_Result))
	{
		return FALSE;
	}
	
	bool l_bFinish=false;

	do
	{
		SRowSet* pRowSet = NULL;
		l_Result=l_pImapiTable->QueryRows(1, 0, &pRowSet);
		
		if (FAILED(l_Result))
		{
			l_bFinish=true;
		}
		else
		{
			if(pRowSet->cRows==1)
			{
				SBinary& rBinaryValue = pRowSet->aRow[0].lpProps->Value.bin;
				l_Result=pMapiSession->OpenMsgStore(NULL, rBinaryValue.cb, (LPENTRYID)rBinaryValue.lpb, NULL, 0, &pMessageStore);
			}
			else
			{
				l_Result=HRESULT_FROM_WIN32(ERROR_NOT_FOUND);
			}

			FreeProws(pRowSet);

			if (FAILED(l_Result))
			{
				l_bFinish=true;
			}
			else
			{
				SPropTagArray l_oPropertyTags;
				l_oPropertyTags.cValues =1;
				l_oPropertyTags.aulPropTag[0]=PR_DISPLAY_NAME;
				ULONG l_ulValueCount;
				SPropValue * l_oPropertyValues=NULL;
				l_Result = pMessageStore->GetProps(&l_oPropertyTags, MAPI_UNICODE, &l_ulValueCount, &l_oPropertyValues);
				if (FAILED(l_Result) || l_ulValueCount != 1)
				{
					l_bFinish=true;
				}
				else
				{
					if (_tcsicmp(l_oPropertyValues[0].Value.lpszW, _T("SMS")) == 0)
					{
						l_bFinish=true;
					}
					
				}

				if(FAILED(l_Result))
				{
					pMessageStore.Release();
				}
			
			}

			

		}


		
	}
	while(l_bFinish==false);
	
	if(FAILED(l_Result))
	{
		pMessageStore.Release();
	}

	return l_Result;

}