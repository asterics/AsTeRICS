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
#include "PhoneManager.h"
#include "Definitions.h"

CPhoneManager* CPhoneManager::m_pActivePhoneManager=NULL;

CPhoneManager::CPhoneManager(CWinThread* pTaskManager):TAPI_VERSION_1_0(0x00010003)
{
	m_pActivePhoneManager=this;
	m_oTapiHandle=NULL;
	m_ulNumberOfDevices=0;
	m_pTaskManager=pTaskManager;
	m_PhoneState=PS_IDLE;
	m_bState=false;
}

CPhoneManager::~CPhoneManager(void)
{
	m_pActivePhoneManager=NULL;
}

bool CPhoneManager::init()
{
	LONG l_lResult;
	l_lResult = lineInitialize(&m_oTapiHandle,AfxGetApp()->m_hInstance,(LINECALLBACK) lineCallbackFunc,NULL,&m_ulNumberOfDevices);

	if(l_lResult)
	{
		return false;
	}

	if(m_ulNumberOfDevices<0)
	{
		return false;
	}

	LINEINFO l_oLineInfo;
	m_ulAPIVersion=0;
	m_ulLineID=-1;

	bool l_bLineFound=false;

	for(DWORD l_ulLine=0;l_ulLine<m_ulNumberOfDevices;l_ulLine++)
	{
		if(getLineInfo(l_ulLine,&l_oLineInfo))
		{
			if (_tcscmp(l_oLineInfo.szLineName,L"Cellular Line")==0)
			{
				m_ulAPIVersion=l_oLineInfo.dwAPIVersion;
				m_ulLineID = l_oLineInfo.dwPermanentLineID;
				l_bLineFound=true;
				break;
			}
		}
	}

	if(!l_bLineFound)
	{
		return false;
	}

	l_lResult=lineOpen (m_oTapiHandle,m_ulLineID,&m_oLineHandle,m_ulAPIVersion, 0, 0, LINECALLPRIVILEGE_OWNER, LINEMEDIAMODE_INTERACTIVEVOICE, NULL);
	
	if(l_lResult)
	{
		return false;
	}
	

	m_bState=true;
	return true;
}

void CPhoneManager::close()
{
	m_bState=false;
	if(lineClose(m_oLineHandle))
	{
		//return false;
	}
	
	if (lineShutdown(m_oTapiHandle))
	{
		//return false;
	}
}

bool CPhoneManager::getLineInfo(DWORD ulLineID, LPLINEINFO oLineInfo)
{
	LONG l_lResult;
	l_lResult=lineNegotiateAPIVersion(m_oTapiHandle,ulLineID,TAPI_VERSION_1_0,TAPI_CURRENT_VERSION,&(oLineInfo->dwAPIVersion),NULL);
	
	if(l_lResult)
	{
		return false;
	}
	
	DWORD l_ulStructureSize=sizeof(LINEDEVCAPS);
	LPLINEDEVCAPS l_oLineDeviceCapabilities = NULL;
	int l_nNumberOfLoops=0;

	bool l_bFinish=false;

	do
	{
		l_nNumberOfLoops++;
		if(l_nNumberOfLoops>20)
		{
			if(l_oLineDeviceCapabilities)
			{
				LocalFree(l_oLineDeviceCapabilities);
			}

			return false;
		}

		if (!(l_oLineDeviceCapabilities = (LPLINEDEVCAPS) LocalAlloc (LPTR, l_ulStructureSize)))
        {
            return false;
        }

		l_oLineDeviceCapabilities->dwTotalSize = l_ulStructureSize;

		l_lResult = lineGetDevCaps (m_oTapiHandle,ulLineID, oLineInfo->dwAPIVersion,0,l_oLineDeviceCapabilities);
		
		if(l_lResult)
		{
			
			if(l_oLineDeviceCapabilities)
			{
				LocalFree(l_oLineDeviceCapabilities);
			}

			return false;
		}

		if (l_oLineDeviceCapabilities->dwNeededSize <= l_oLineDeviceCapabilities->dwTotalSize)
		{
			l_bFinish=true;
		}
		else
		{
			l_ulStructureSize = l_oLineDeviceCapabilities->dwNeededSize;
			LocalFree (l_oLineDeviceCapabilities);
			l_oLineDeviceCapabilities = NULL;
		}
	}
	while(!l_bFinish);
	
	oLineInfo->dwPermanentLineID = l_oLineDeviceCapabilities->dwPermanentLineID;
    oLineInfo->dwNumOfAddress = l_oLineDeviceCapabilities->dwNumAddresses;
    oLineInfo->bVoiceLine =(l_oLineDeviceCapabilities->dwMediaModes & LINEMEDIAMODE_INTERACTIVEVOICE);
   
	LPTSTR l_sLineName = NULL; 
   
    if (!(l_sLineName = (LPTSTR) LocalAlloc (LPTR, 512)))
    {
		if(l_oLineDeviceCapabilities)
		{
			LocalFree(l_oLineDeviceCapabilities);
		}
		return false;
    }

	if (l_oLineDeviceCapabilities->dwLineNameSize >= 512)
    {
        wcsncpy (l_sLineName, (LPTSTR)((LPSTR)l_oLineDeviceCapabilities + l_oLineDeviceCapabilities->dwLineNameOffset),512);
    }
    else if (l_oLineDeviceCapabilities->dwLineNameSize > 0)
    {
        wcsncpy (l_sLineName, (LPTSTR)((LPSTR)l_oLineDeviceCapabilities + l_oLineDeviceCapabilities->dwLineNameOffset),l_oLineDeviceCapabilities->dwLineNameSize);
    }
    else 
	{
        wsprintf (l_sLineName, TEXT("Line %d"), ulLineID);
	}
    
    lstrcpy (oLineInfo->szLineName, l_sLineName);

	if (l_oLineDeviceCapabilities)
	{
        LocalFree (l_oLineDeviceCapabilities);
	}
    
    if (l_sLineName)
	{
        LocalFree (l_sLineName);
	}

	return true;

}

int CPhoneManager::drop()
{
	if(m_bState==false)
	{
		return Phone_no_initialized;
	}
	
	LONG l_lResult;
	l_lResult=lineDrop(m_oCallhandle,NULL,0);
	if(l_lResult>0)
	{
		return 1;
	}
	else
	{
		return Phone_drop_error;
	}
}

int CPhoneManager::answer()
{
	
	if(m_bState==false)
	{
		return Phone_no_initialized;
	}
	
	LONG l_lResult;
	l_lResult=lineAnswer(m_oCallhandle,NULL,0);

	if(l_lResult>0)
	{
		return 1;
	}
	else
	{
		return Phone_accept_error;
	}
}

int CPhoneManager::call(CString recipientID)
{
	if(m_bState==false)
	{
		return Phone_no_initialized;
	}
	
	LPLINETRANSLATEOUTPUT l_oTranslateOutput = NULL;
	LPLINECALLPARAMS l_oCallParams = NULL;

	DWORD l_ulStructureSize = sizeof (LINETRANSLATEOUTPUT);
	
	LONG l_lResult;
	bool l_bFinish=false;

	do
	{
        if (!(l_oTranslateOutput = (LPLINETRANSLATEOUTPUT) LocalAlloc (LPTR,l_ulStructureSize)))
        {
            return Phone_call_error;
        }
		

		l_oTranslateOutput->dwTotalSize = l_ulStructureSize;
		
		l_lResult=lineTranslateAddress (m_oTapiHandle, m_ulLineID, m_ulAPIVersion, recipientID,0,0,l_oTranslateOutput);
		if (l_lResult)
        {
            if(l_oTranslateOutput)
			{
				LocalFree(l_oTranslateOutput);
				return Phone_call_error;
			}
        }

		if (l_oTranslateOutput->dwNeededSize <= l_oTranslateOutput->dwTotalSize)
		{
			break; 
		}
        else
        {
            l_ulStructureSize = l_oTranslateOutput->dwNeededSize;
            LocalFree (l_oTranslateOutput);
            l_oTranslateOutput = NULL;
        }


	}while(!l_bFinish);
	
	l_ulStructureSize = sizeof (LINECALLPARAMS) +  l_oTranslateOutput->dwDisplayableStringSize;
	
	if (!(l_oCallParams = (LPLINECALLPARAMS) LocalAlloc (LPTR, l_ulStructureSize)))
    {
		LocalFree(l_oTranslateOutput);
		return Phone_call_error;
    }

	ZeroMemory(l_oCallParams, l_ulStructureSize);

    l_oCallParams->dwTotalSize      = l_ulStructureSize;
    l_oCallParams->dwBearerMode     = LINEBEARERMODE_VOICE;
    l_oCallParams->dwMediaMode      = LINEMEDIAMODE_INTERACTIVEVOICE;
    l_oCallParams->dwCallParamFlags = LINECALLPARAMFLAGS_IDLE;
    l_oCallParams->dwAddressMode    = LINEADDRESSMODE_ADDRESSID;
    l_oCallParams->dwAddressID      = 0;
    l_oCallParams->dwDisplayableAddressSize = l_oTranslateOutput->dwDisplayableStringSize;
    l_oCallParams->dwDisplayableAddressOffset = sizeof (LINECALLPARAMS);
	
	TCHAR l_sDialablePhoneNum[TAPIMAXDESTADDRESSSIZE + 1] = {'\0'};

	lstrcpy (l_sDialablePhoneNum, recipientID);
    memcpy((LPBYTE) l_oCallParams + l_oCallParams->dwDisplayableAddressOffset,(LPBYTE) l_oTranslateOutput + l_oTranslateOutput->dwDisplayableStringOffset,l_oTranslateOutput->dwDisplayableStringSize);
	
	l_lResult = lineMakeCall (m_oLineHandle, &m_oCallhandle, l_sDialablePhoneNum, 0, l_oCallParams);   
	
	if(l_oTranslateOutput)
	{
		LocalFree(l_oTranslateOutput);
	}

	if(l_oCallParams)
	{
		LocalFree(l_oCallParams);
	}

	if(l_lResult)
	{
		return 1;
	}
	else
	{
		return Phone_call_error;
	}
}


PhoneState CPhoneManager::getPhoneState()
{
	return m_PhoneState;
}

void CPhoneManager::phoneStateChanged(PhoneState enPhoneState,CString sCallID)
{
	if(sCallID.GetLength()>0)
	{
		m_sCallID=sCallID;
	}

	if(enPhoneState==PS_IDLE)
	{
		m_sCallID=L"";
	}

	if(enPhoneState!=m_PhoneState)
	{
		m_pTaskManager->PostThreadMessageW(PHONE_STATE_CHANGE,(WPARAM)enPhoneState,0);
	}
	m_PhoneState=enPhoneState;
}

CString CPhoneManager::getCallID()
{
	return m_sCallID;
}

VOID FAR PASCAL lineCallbackFunc(DWORD hDevice,DWORD dwMsg,DWORD dwCallbackInstance,DWORD dwParam1,DWORD dwParam2,DWORD dwParam3)
{
	switch (dwMsg)
	{
	case LINE_APPNEWCALL:
	  {
		  
		  CPhoneManager *l_pPhoneManager = CPhoneManager::m_pActivePhoneManager;
		  
		  l_pPhoneManager->m_oCallhandle = (HCALL)dwParam2;
			
		  DWORD l_ulStructureSize = sizeof(LINECALLINFO)+1000;
		  
		  LPLINECALLINFO l_pCallInfo = (LPLINECALLINFO)new BYTE[l_ulStructureSize];
		  
		  l_pCallInfo ->dwTotalSize = l_ulStructureSize;
		  LONG l_lResult;
          l_lResult = lineGetCallInfo(l_pPhoneManager->m_oCallhandle,l_pCallInfo);

		  TCHAR l_sCallerID[256] = {0};
		  memcpy(l_sCallerID,((BYTE*)l_pCallInfo)+l_pCallInfo->dwCallerIDOffset,l_pCallInfo->dwCallerIDSize);
		  
		  
		  //NewCall* l_pNewCall= new NewCall();

		  //l_pNewCall->m_sCallerID.Format(L"%s",l_sCallerID);
		
		  //l_pPhoneManager->m_pTaskManager->PostThreadMessageW(NEW_CALL,(WPARAM)l_pNewCall,0);
		  CString l_sCallID;
		  l_sCallID.Format(L"%s",l_sCallerID);
		  l_pPhoneManager->phoneStateChanged(PS_RING,l_sCallID);

		  delete l_pCallInfo;
	  }
	  break;	
	case LINE_CALLSTATE:
		{
			switch(dwParam1)
			{
			case LINECALLSTATE_ACCEPTED:
				{
					
				}
				break;
			case LINECALLSTATE_BUSY:
				{
					
				}
				break;
			case LINECALLSTATE_CONFERENCED:
				{
					
				}
				break;
			case LINECALLSTATE_CONNECTED:
				{
					CPhoneManager *l_pPhoneManager = CPhoneManager::m_pActivePhoneManager;
					l_pPhoneManager->phoneStateChanged(PS_CONNECTED,L"");

					
				}
				break;
			case LINECALLSTATE_DIALING:
				{
					CPhoneManager *l_pPhoneManager = CPhoneManager::m_pActivePhoneManager;
					l_pPhoneManager->phoneStateChanged(PS_RING,L"");
					
				}
				break;
			case LINECALLSTATE_DIALTONE:
				{
					int a=0;
					a++;
				}
				break;
			case LINECALLSTATE_DISCONNECTED:
				{
					
				}
				break;
			case LINECALLSTATE_IDLE:
				{
					CPhoneManager *l_pPhoneManager = CPhoneManager::m_pActivePhoneManager;
					l_pPhoneManager->phoneStateChanged(PS_IDLE,L"");

					
				}
				break;
			case LINECALLSTATE_OFFERING:
				{
					
					
				}
				break;
			case LINECALLSTATE_ONHOLD:
				{
					
				}
				break;
			case LINECALLSTATE_ONHOLDPENDCONF:
				{
					
				}
				break;
			case LINECALLSTATE_ONHOLDPENDTRANSFER:
				{
					
				}
				break;
			case LINECALLSTATE_PROCEEDING:
				{
					
				}
				break;
			case LINECALLSTATE_RINGBACK:
				{
					
				}
				break;
			case LINECALLSTATE_SPECIALINFO:
				{
					
				}
				break;
			case LINECALLSTATE_UNKNOWN:
				{
					
				}
				break;

			}
				
		}
		break;
	case LINE_CALLINFO:
		{
			int a=0;
			a++;
		}
		
		break;
	case LINE_REPLY:
		{
			
		}
		
		break;
	default:
		break;
	}
}