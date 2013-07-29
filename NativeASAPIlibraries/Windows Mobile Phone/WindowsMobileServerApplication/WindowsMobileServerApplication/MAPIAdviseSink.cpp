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
#include "MAPIAdviseSink.h"
#include "Definitions.h"

CMAPIAdviseSink::CMAPIAdviseSink(void)
{
	m_ulReferenceCounter=0;
}

CMAPIAdviseSink::~CMAPIAdviseSink(void)
{
}

HRESULT CMAPIAdviseSink::QueryInterface (REFIID riid, LPVOID FAR * ppvObj)
{
	return 0;
}

ULONG CMAPIAdviseSink::AddRef()
{
	m_ulReferenceCounter++;
	return m_ulReferenceCounter;
}

ULONG CMAPIAdviseSink::Release()
{
	m_ulReferenceCounter--;
	return m_ulReferenceCounter;
}

void CMAPIAdviseSink::init (IMAPISession* pSession,CWinThread* pTaskManager)
{
	m_pSession=pSession;
	m_pTaskManager=pTaskManager;

}

ULONG CMAPIAdviseSink::OnNotify(ULONG cNotif,LPNOTIFICATION lpNotifications)
{
	if(lpNotifications->ulEventType ==fnevObjectCreated)
	{
		if(lpNotifications->info.obj.ulObjType==MAPI_MESSAGE)
		{
			if(checkIfMessageInSmsInputFolder(&lpNotifications->info.obj))
			{
				IMessage* l_pMessage=NULL;
				ULONG l_ulObjectType;
				HRESULT l_Result;
				l_Result = m_pSession->OpenEntry(lpNotifications->info.obj.cbEntryID,lpNotifications->info.obj.lpEntryID,0,0,&l_ulObjectType,(LPUNKNOWN*)&l_pMessage);
	
				if((FAILED(l_Result))||(l_pMessage==NULL))
				{
					return false;
				}
				
				IncommingSMS  *l_pNewSMS = new IncommingSMS();
				

				l_pNewSMS->m_sSender = GetMapiStringProperty(l_pMessage,PR_SENDER_EMAIL_ADDRESS);
				l_pNewSMS->m_sContent = GetMapiStringProperty(l_pMessage,PR_SUBJECT);
				l_pMessage->Release();
				
				if(m_bMessageState==true)
				{
					m_pTaskManager->PostThreadMessageW(NEW_SMS,(WPARAM)l_pNewSMS,0);
				}

				
			}
		}

	}

	return 0;
}

bool CMAPIAdviseSink::checkIfMessageInSmsInputFolder(OBJECT_NOTIFICATION* pNotificationObject)
{
	HRESULT l_Result;
	IMessage* l_pMessage=NULL;
	ULONG l_ulObjectType;

	l_Result=m_pSession->OpenEntry(pNotificationObject->cbEntryID,pNotificationObject->lpEntryID,0,0,&l_ulObjectType,(LPUNKNOWN*)&l_pMessage);
	if(FAILED(l_Result)||l_pMessage==NULL)
	{
		return false;
	}

	CByteArray l_StoreID;

	if (!GetMapiBinaryProperty(l_pMessage,PR_STORE_ENTRYID,l_StoreID))
	{
		l_pMessage->Release();
		return false;
	}

	IMsgStore* l_pStore=NULL;
	
	l_Result=m_pSession->OpenEntry(l_StoreID.GetSize(),(LPENTRYID)l_StoreID.GetData(),0,0,&l_ulObjectType,(LPUNKNOWN*)&l_pStore);
	if(FAILED(l_Result)||l_pStore==NULL)
	{
		l_pMessage->Release();
		return false;
	}

	CByteArray l_InboxID;

	if (!GetMapiBinaryProperty(l_pStore,PR_CE_IPM_INBOX_ENTRYID,l_InboxID))
	{
		l_pMessage->Release();
		l_pStore->Release();
		return false;
	}
	l_pStore->Release();
	
	CByteArray l_FolderID;

	if (GetMapiBinaryProperty(l_pMessage,PR_PARENT_ENTRYID,l_FolderID))
	{
		ULONG l_ulResult;
		l_Result = m_pSession->CompareEntryIDs(l_FolderID.GetSize(),
									(LPENTRYID)l_FolderID.GetData(),
									l_InboxID.GetSize(),
									(LPENTRYID)l_InboxID.GetData(),
									0,
									&l_ulResult);
		if ((!FAILED(l_Result)) && l_ulResult)
		{
			l_pMessage->Release();
			return true;
		}
		else
		{
			l_pMessage->Release();
			return false;
		}
	}
	else
	{
		l_pMessage->Release();
		return false;
	}
	
	return false;
}

bool CMAPIAdviseSink::GetMapiBinaryProperty(IMAPIProp* pPropertyObject, ULONG ulPropertyTag, CByteArray& PropertyValue)
{
	HRESULT l_Result;
	
	SPropTagArray l_PropertyTagArray;
	l_PropertyTagArray.cValues = 1;
	l_PropertyTagArray.aulPropTag[0] = ulPropertyTag;
	ULONG l_ulValuesCount;
	LPSPropValue l_pPropertyValue;

	l_Result= pPropertyObject->GetProps(&l_PropertyTagArray,MAPI_UNICODE,&l_ulValuesCount,&l_pPropertyValue);
	if((!FAILED(l_Result))&&(l_pPropertyValue[0].ulPropTag!=PT_ERROR))
	{
		PropertyValue.SetSize(l_pPropertyValue[0].Value.bin.cb);
		memcpy(PropertyValue.GetData(),l_pPropertyValue[0].Value.bin.lpb,l_pPropertyValue[0].Value.bin.cb);
		MAPIFreeBuffer(l_pPropertyValue);
		return true;
	}

	return false;
}

CString CMAPIAdviseSink::GetMapiStringProperty(IMAPIProp* pPropertyObject, ULONG ulPropertyTag)
{
	CString l_rsResult;
	SPropTagArray l_PropertyTagArray;
	l_PropertyTagArray.cValues = 1;
	l_PropertyTagArray.aulPropTag[0] = ulPropertyTag;

	ULONG l_ulValuesCount;
	LPSPropValue l_pPropertyValue;
	
	HRESULT l_Result;

	l_Result = pPropertyObject->GetProps(&l_PropertyTagArray,MAPI_UNICODE,&l_ulValuesCount,&l_pPropertyValue);
	if((!FAILED(l_Result))&&(l_pPropertyValue[0].ulPropTag!=PT_ERROR))
	{
		l_rsResult = l_pPropertyValue[0].Value.lpszW;
		MAPIFreeBuffer(l_pPropertyValue);
	}
	return l_rsResult;
}