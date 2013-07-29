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

#include "StdAfx.h"
#include "ServerConnection.h"
#include "CommunicationManager.h"


HANDLE ServerConnection::sendServerCommandMutex=NULL;

/**
 * 
 * This class stores server connections.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Sep 09, 2012
 *         Time: 13:24:48 AM
 */

/**
* The class constructor.
* @param socket the socket of the connection
* @param id the connection id
* @param owner pointer to the CommunicationManager object
*/
ServerConnection::ServerConnection(SOCKET socket, int id,CommunicationManager* owner)
{
	noWait=false;
	this->socket=socket;
	this->id=id;
	this->owner=owner;
	connected=false;
	writeError=false;
	timeCounter=0;
	endNow=false;
	ps.init(&this->socket);
	//sendServerCommandMutex=CreateMutex(NULL,FALSE,NULL);
	threadHandle=NULL;
}

/**
* The class destructor.
*/
ServerConnection::~ServerConnection(void)
{
	//CloseHandle(sendServerCommandMutex);
	stopNow(noWait);
	if(threadHandle!=NULL)
	{
		if(noWait==false)
		{
			WaitForSingleObject(threadHandle,5000);
		}
	}
}

/**
* Initializes the class
*/
void ServerConnection::init()
{
	sendServerCommandMutex=CreateMutex(NULL,FALSE,NULL);
}

/**
* Cleans the class
*/
void ServerConnection::end()
{
	CloseHandle(sendServerCommandMutex);
}

/**
* This method sends the command to the remote receiver
* @param command defines value type
* @param port defines the port of the remote receiver.
* @param doubleData double value
* @param integerData integer value
* @param stringData string value
* @return result
*/
bool ServerConnection::sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData)
{
	bool sendResult=false;
	DWORD result=WaitForSingleObject(sendServerCommandMutex,INFINITE);

	if(result==WAIT_OBJECT_0)
	{
		__try {
			if(connected)
			{
				SendError result =ps.sendCommand(command,port,doubleData,integerData,stringData);
				if(result==SendErrorOccur)
				{
					writeError=true;
				}
			
			
				if(result==OK)
				{
					timeCounter=0;
					sendResult=true;
				}else{
					//return false;
				}
			}
		}
		 __finally{
			BOOL boolResult=ReleaseMutex(sendServerCommandMutex);
			/*if(boolResult!=TRUE)
			{
				
			}*/
		}
		
	}
	else
	{
		//TODO
	}	

	return sendResult;
}

/**
* Returns true if the object is connected to the remote receiver. 
* @return true if the object is in the connected state
*/
bool ServerConnection::isConnected()
{
	return connected;
}
		
/**
* Stops the connection.
* @param noWait do not wait for the thread
*/
void ServerConnection::stopNow(bool noWait)
{
	this->noWait=noWait;
	endNow=true;
}

/**
* Starts the connection.
*/
void ServerConnection::start()
{
	threadHandle=::CreateThread(0,0,(LPTHREAD_START_ROUTINE)run,(LPVOID)this,0,0);
}

/**
* Gets the connection ID.
* @return connection ID
*/
int ServerConnection::getID()
{
	return id;
}

/**
* Connection main thread
*/
DWORD WINAPI ServerConnection::run(LPVOID lpParam)
{
	ServerConnection *_this = (ServerConnection*) lpParam;

	bool exit=false;
	_this->writeError=false;
	_this->connected=true;
	_this->endNow=false;
	do{
		exit=_this->owner->readData(&_this->ps);

		if(_this->ps.getHeaderReceived())
		{
			_this->timeCounter=0;
			Sleep(_this->owner->shortWait);
		}
		else
		{
			_this->timeCounter=_this->timeCounter+1;
			Sleep(_this->owner->longWait);
		}

		if(_this->timeCounter>35)
		{
			_this->timeCounter=0;
			_this->sendCommand(Action,0,0,0,L"");
		}
	}while((_this->owner->finish==false)&&(!exit)&&(_this->writeError==false)&&(!_this->endNow));
	_this->connected=false;

	closesocket(_this->socket);

	if(!_this->endNow)
	{
		try
		{
			_this->owner->removeServerConnection(_this,_this->id);
		}
		catch(...)
		{
		}
	}

	return 0;
}

