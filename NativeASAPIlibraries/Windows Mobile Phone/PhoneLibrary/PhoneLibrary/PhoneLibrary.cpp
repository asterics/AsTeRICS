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

#include "stdafx.h"
#include "PhoneLibrary.h"
#include "BTConnectionManager.h"

/**
 * Provides library interface
 *    
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Oct 14, 2011
 *         Time: 2:24:23 PM
 */

CBTConnectionManager *pBTConnectionManager=NULL;

/**
* Initializes the library.
*
* @param deviceFound  callback function which will be called when the new device is found
* @param newSMS callback function which will be called when the new SMS is available
* @param phoneStateChanged function which will be called when phone state is changed
* @param param param given by user
* @return if the returned value is less then 0, the value is an error number.
*/
extern "C" int __stdcall init(DeviceFound deviceFound, NewSMS newSMS, PhoneStateChanged phoneStateChanged, LPVOID param)
{
	if (pBTConnectionManager!=NULL)
	{
		return Library_initialized;
	}

	pBTConnectionManager = new CBTConnectionManager();
	

	return pBTConnectionManager->init(deviceFound,newSMS,phoneStateChanged,param);
	
}

/**
* Search for the BT devices.
*
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall searchDevices()
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	return pBTConnectionManager->startSearchBTDevices();
}

/**
* Closes the library
*
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall close()
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}
	
	pBTConnectionManager->close();
	delete pBTConnectionManager;
	pBTConnectionManager=NULL;

	return 1;
}

/**
* Connects to the phone
*
* @param deviceAddress address of the phone
* @param port phone server port
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall connectToDevice(unsigned _int64 deviceAddress, int port)
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}
	
	int l_nResult;

	l_nResult=pBTConnectionManager->connectDevice(deviceAddress,port);

	return l_nResult;
}


/**
* Disconnects the device
*
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall disconnect()
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	return pBTConnectionManager->disconnectDevice();
}


/**
* Sends the SMS.
*
* @param recipientID  recipient phone id
* @param subject  content of the SMS
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall sendSMS(LPWSTR recipientID, LPWSTR subject)
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	int l_nResult;

	if(wcslen(recipientID)==0 || wcslen(subject)==0)
	{
		return Data_empty;
	}
	else
	{
		l_nResult=pBTConnectionManager->sendSMS(recipientID,subject);
	}

	return l_nResult;
}

/**
* Makes phone call.
*
* @param recipientID  recipient phone id
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall makePhoneCall (LPWSTR recipientID)
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	int l_nResult;

	if(wcslen(recipientID)==0)
	{
		return Data_empty;
	}
	else
	{
		l_nResult=pBTConnectionManager->makePhoneCall(recipientID);
	}

	return l_nResult;

}

/**
* Accepts incoming phone call.
*
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall acceptCall()
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	int l_nResult;

	l_nResult=pBTConnectionManager->acceptCall();

	return l_nResult;
}

/**
* Drops phone call
*
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall dropCall()
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	int l_nResult;

	l_nResult=pBTConnectionManager->dropCall();

	return l_nResult;
}

/**
* Gets phone state.
*
* @param phoneState  state of the phone
* @return if the returned value is less then 0, the value is an error number
*/
extern "C" int __stdcall getPhoneState(PhoneState &phoneState)
{
	if (pBTConnectionManager==NULL)
	{
		return Library_no_initialized;
	}

	int l_nResult;

	l_nResult=pBTConnectionManager->getPhoneState(phoneState);

	return l_nResult;
}