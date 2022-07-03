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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

// NetConnectionLibrary.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "NetConnectionLibrary.h"
#include "CommunicationManager.h"


CommunicationManager * cm=NULL;

/**
* Initializes the library.
* @param serverMode defines connection mode
* @param IP IP of the remote server
* @param port TCP port of the connection
* @param newEvent event value callback
* @param newIntegerValue integer value callback
* @param newDoubleValue double value callback
* @param newStringValue string value callback
* @param param parameter defined by the user
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall init(ServerMode serverMode,LPWSTR IP,int port,NewEvent newEvent,NewIntegerValue newIntegerValue,NewDoubleValue newDoubleValue,NewStringValue newStringValue, LPVOID param)
{
	cm=new CommunicationManager(serverMode,IP,port,newEvent,newIntegerValue,newDoubleValue,newStringValue,param);
	return 1;
}

/**
* Closes the library
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall close()
{
	cm->finishCommunication();
	delete cm;
	cm=NULL;

	return 1;
}

/**
* Sends the event/
* @param port the event port
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall sendEvent(int port)
{
	if(cm!=NULL)
	{
		cm->sendCommand(Event,port,0,0,L"");
	}

	return 1;
}

/**
* Sends the integer value.
* @param port the integer port
* @param value the integer value
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall sendInteger(int port,int value)
{
	if(cm!=NULL)
	{
		cm->sendCommand(Integer,port,0,value,L"");
	}
	return 1;
}

/**
* Sends the double value.
* @param port the double port
* @param value the double value
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall sendDouble(int port,double value)
{
	if(cm!=NULL)
	{
		cm->sendCommand(Double,port,value,0,L"");
	}
	return 1;
}

/**
* Sends the text value.
* @param port the text port
* @param value the text value
* @return error number
*/
NETCONNECTIONLIBRARY_API int __stdcall sendText(int port,wchar_t* text)
{
	if(cm!=NULL)
	{
		cm->sendCommand(String,port,0,0,text);
	}
	return 1;
}


/**
* If the library works in the server mode, this method returns number of connected clients.
* If the library works in the client mode, this method returns 0, if the library is not
* connected to the server or 1, if the library is connected to the server. The method may returns negative value if en error is found.
* @return number of active connections.
*/
NETCONNECTIONLIBRARY_API int __stdcall numberOfConnections()
{
	if(cm!=NULL)
	{
		return cm->numberOfConnections();
	}
	return -1;
}