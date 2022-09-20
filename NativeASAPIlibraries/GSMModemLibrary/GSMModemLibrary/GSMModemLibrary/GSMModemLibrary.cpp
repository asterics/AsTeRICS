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


/**
 * This file contains interface functions of the library.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "stdafx.h"
#include "GSMModemLibrary.h"
#include "ModemManager.h"

#include <comdef.h>
#include <Wbemidl.h>
#pragma comment(lib, "wbemuuid.lib")
#include "ModemData.h"

ModemManager *pModem=NULL;

bool initialized=false;


/**
 * Initializes the library.
 * @param com defines the modem serial port
 * @param newSMSAvailable call-back functions, the function is called if the new SMS is available
 * @param errorCallback call-back functions, the function is called if the error is found
 * @param pin the PIN for the SIM card, if it is not needed is should be empty
 * @param smsCenterNumber the SMS content number, if if is not needed is should be empty
 * @param param parameter defined by user and passed to the newSMSAvailable and errorCallback call-back functions
 * @return error number
 */
GSMMODEMLIBRARY_API int __stdcall init(LPWSTR com, NewSMSAvailable newSMSAvailable,ErrorCallback errorCallback, LPWSTR pin, LPWSTR smsCenterNumber ,LPVOID param)
{
	
	if(initialized)
	{
		return Library_initialized;
	}

	pModem=new ModemManager(com);
	pModem->setCallbacks(newSMSAvailable, errorCallback,param);
	int result=pModem->init(pin,smsCenterNumber);

	if(result>0)
	{
		initialized=true;
	}

	return result;

}


/**
 * Closes the library.
 * @return error number
 */
GSMMODEMLIBRARY_API int __stdcall close()
{
	
	if(!initialized)
	{
		return Library_no_initialized;
	}

	pModem->close();
	delete pModem;
	pModem=NULL;

	initialized=false;

	return 1;
}

/**
 * Sends SMS.
 * @param recipientID the recipient phone ID
 * @param subject the content of the message
 * @return error number
 */
GSMMODEMLIBRARY_API int __stdcall sendSMS(LPWSTR recipientID, LPWSTR subject)
{
	
	if(!initialized)
	{
		return Library_no_initialized;
	}

	int result=pModem->sendSMS(recipientID,subject);
	return result;
}

wstring getModemPortFromRegistry()
{
	HKEY hKey;
	LONG result = RegOpenKeyExW(HKEY_LOCAL_MACHINE, L"SYSTEM\\CurrentControlSet\\services\\Modem\\Enum", 0, KEY_READ, &hKey);

	if (result != ERROR_SUCCESS) 
    {
        if (result == ERROR_FILE_NOT_FOUND) {
            return L"";
        } 
        else {
            return L"";
        }
    }

	DWORD bufferSize=1024;
	wchar_t* buffer=new wchar_t[bufferSize];

	result= RegQueryValueEx(hKey,L"0",0,NULL,(LPBYTE)buffer,&bufferSize);

	wstring comName=L"";

	if(result==ERROR_SUCCESS)
	{
		wstring device = L"SYSTEM\\CurrentControlSet\\Enum\\";
		device.append(buffer);
		device.append(L"\\Device Parameters");
		
		HKEY modemKey;

		result = RegOpenKeyExW(HKEY_LOCAL_MACHINE, device.c_str(), 0, KEY_READ, &modemKey);
		if (result == ERROR_SUCCESS) 
		{
			
				result= RegQueryValueEx(modemKey,L"PortName",0,NULL,(LPBYTE)buffer,&bufferSize);
				if(result==ERROR_SUCCESS)
				{
					comName=buffer;
					
				}
				RegCloseKey (modemKey);
			
		}

		RegCloseKey (hKey);
	

	}

	delete[] buffer;

	return comName;
}

ModemSearchResult modemResult=NULL;
int modemSearchError=1;
bool modemFound=false;

LPVOID userParam;

/**
 * The thread function which search for the modems. 
 * @param lpParam function parameter
 * @return error number
 */
DWORD WINAPI portSearchFunction(LPVOID lpParam)
{
	HRESULT hres;

	wstring defaultModem;
	defaultModem=getModemPortFromRegistry();

	if(defaultModem.size()>0)
	{
		modemResult(defaultModem.c_str(),L"default",userParam);
	}

    hres =  CoInitializeEx(0, COINIT_MULTITHREADED); 
    if (FAILED(hres))
    {
        return COMM_initialize_false; //Failed to initialize COM library
		modemSearchError=-1;
    }
	

	hres =  CoInitializeSecurity(
		NULL, 
        -1,                          // COM authentication
        NULL,                        // Authentication services
        NULL,                        // Reserved
        RPC_C_AUTHN_LEVEL_DEFAULT,   // Default authentication 
        RPC_C_IMP_LEVEL_IMPERSONATE, // Default Impersonation  
        NULL,                        // Authentication info
        EOAC_NONE,                   // Additional capabilities 
        NULL                         // Reserved
	);

                      
    if (FAILED(hres))
    {
        if(RPC_E_TOO_LATE!=hres)
		{
			CoUninitialize();
			return COMM_initialize_false; //Failed to initialize security
			modemSearchError=-1;
		}
    }

	IWbemLocator *pLoc = NULL;

    hres = CoCreateInstance(
        CLSID_WbemLocator,             
        0, 
        CLSCTX_INPROC_SERVER, 
        IID_IWbemLocator, (LPVOID *) &pLoc);
 
    if (FAILED(hres))
    {
        CoUninitialize();
        return COMM_initialize_false;                 // Failed to create IWbemLocator object
		modemSearchError=-1;
    }

	IWbemServices *pSvc = NULL;
	
    hres = pLoc->ConnectServer(
         _bstr_t(L"ROOT\\CIMV2"), // Object path of WMI namespace
         NULL,                    // User name. NULL = current user
         NULL,                    // User password. NULL = current
         0,                       // Locale. NULL indicates current
         NULL,                    // Security flags.
         0,                       // Authority (e.g. Kerberos)
         0,                       // Context object 
         &pSvc                    // pointer to IWbemServices proxy
         );
    
    if (FAILED(hres))
    {
        pLoc->Release();     
        CoUninitialize();
        return COMM_initialize_false; //"Could not connect
		modemSearchError=-1;//
    }

	hres = CoSetProxyBlanket(
       pSvc,                        // Indicates the proxy to set
       RPC_C_AUTHN_WINNT,           // RPC_C_AUTHN_xxx
       RPC_C_AUTHZ_NONE,            // RPC_C_AUTHZ_xxx
       NULL,                        // Server principal name 
       RPC_C_AUTHN_LEVEL_CALL,      // RPC_C_AUTHN_LEVEL_xxx 
       RPC_C_IMP_LEVEL_IMPERSONATE, // RPC_C_IMP_LEVEL_xxx
       NULL,                        // client identity
       EOAC_NONE                    // proxy capabilities 
    );

    if (FAILED(hres))
    {
        pSvc->Release();
        pLoc->Release();     
        CoUninitialize();
        return COMM_initialize_false;  //Could not set proxy blanket. 
		modemSearchError=-1;//
    }


	IEnumWbemClassObject* pEnumerator = NULL;
    hres = pSvc->ExecQuery(
        bstr_t("WQL"), 
        //bstr_t("SELECT * FROM Win32_POTSModemToSerialPort"),
		bstr_t("SELECT * FROM Win32_POTSModem"),
        WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, 
        NULL,
        &pEnumerator);
    
    if (FAILED(hres))
    {
        pSvc->Release();
        pLoc->Release();
        CoUninitialize();
        return COMM_initialize_false;  //Query for operating system name failed.
		modemSearchError=-1;//
    }

	IWbemClassObject *pclsObj;
    ULONG uReturn = 0;
   
    while (pEnumerator)
    {
        HRESULT hr = pEnumerator->Next(WBEM_INFINITE, 1, &pclsObj, &uReturn);

        if(0 == uReturn)
        {
            break;
        }

        VARIANT vtSerialPort;
		VARIANT vtModem;
        
		hr = pclsObj->Get(L"AttachedTo", 0, &vtSerialPort, 0, 0); //AttachedTo
		hr = pclsObj->Get(L"SystemName", 0, &vtModem, 0, 0); //Name
	

		_bstr_t portBString;
		_bstr_t modemNameBString;

		portBString.Assign(vtSerialPort.bstrVal);
		modemNameBString.Assign(vtModem.bstrVal);

		wstring port = portBString;
		wstring modemName=modemNameBString;

        
		VariantClear(&vtSerialPort);
		VariantClear(&vtModem);

		if(modemResult!=NULL)
		{
			if(port.size()!=0 && modemName.size()!=0)
			{
				modemResult(port.c_str(),modemName.c_str(),userParam);
			}
		}

		modemFound=true;

        pclsObj->Release();
    }

	modemResult(L"",L"",userParam);
    
    pSvc->Release();
    pLoc->Release();
    pEnumerator->Release();
    //pclsObj->Release();
    CoUninitialize();
	return 0;
}

/**
 * Prepares the initialization of the modem search.
 * @param  modemSearchResult user call-back function which is called when the new modem is found
 * @param param parameter defined by user and passed to the modemSearchResult call-back function
 * @return error number
 */
GSMMODEMLIBRARY_API int __stdcall getModemPortNumber(ModemSearchResult modemSearchResult,LPVOID param)
{
	
	ModemData modemManager;
	modemResult=modemSearchResult;

	modemSearchError=-1;
	modemFound=false;

	userParam=param;

	HANDLE threadHandle=CreateThread(NULL,0,portSearchFunction,&modemManager,0,NULL);
	if(threadHandle==NULL)
	{
		return COMM_initialize_false;
	}

	WaitForSingleObject(threadHandle,300);
	CloseHandle(threadHandle);
	threadHandle=NULL;
	return 1;
}