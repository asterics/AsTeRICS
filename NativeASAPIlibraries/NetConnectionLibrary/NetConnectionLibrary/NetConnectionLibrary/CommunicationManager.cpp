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
#include "CommunicationManager.h"
#pragma comment(lib,"ws2_32.lib")


/**
 * 
 * This class manages the Internet connection. It can run the client or server connection depend on the settings.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Sep 09, 2012
 *         Time: 13:24:48 AM
 */

/**
* The class constructor.
* @param serverMode defines connection mode
* @param IP IP of the remote server
* @param port TCP port of the connection
* @param newEvent event value callback
* @param newIntegerValue integer value callback
* @param newDoubleValue double value callback
* @param newStringValue string value callback
* @param param parameter defined by the user
*/
CommunicationManager::CommunicationManager(ServerMode serverMode,LPWSTR IP,int port,NewEvent newEvent,NewIntegerValue newIntegerValue,NewDoubleValue newDoubleValue,NewStringValue newStringValue, LPVOID param)
{
	connected=false;
	finish=false;
	initError=false;

	wstring localhost = L"localhost";

	if(localhost.compare(IP)==0)
	{
		this->IP=L"127.0.0.1";
	}
	else
	{
		this->IP=IP;
	}

	this->port=port;
	

	WSADATA wsaData;

	timeCounter=0;
	bool writeError=false;
	sessionID=0;

	serverSocketCreated=false;
	threadHandle=NULL;

	processCommandMutex=CreateMutex(NULL,FALSE,NULL);
	sendCommandMutex=CreateMutex(NULL,FALSE,NULL);
	sendClientCommandMutex=CreateMutex(NULL,FALSE,NULL);
	removeServerMutex=CreateMutex(NULL,FALSE,NULL);

	serverConnection=NULL;
	ServerConnection::init();

	if(WSAStartup(MAKEWORD(2,2),&wsaData)!=0)
	{
		WSACleanup();
		initError=true;
		return;
	}

	this->newEvent=newEvent;
	this->newIntegerValue=newIntegerValue;
	this->newDoubleValue=newDoubleValue;
	this->newStringValue=newStringValue;
	this->param=param;
	this->serverMode=serverMode;


	

	if(serverMode==SM_CLIENT)
	{
      threadHandle=::CreateThread(0,0,(LPTHREAD_START_ROUTINE)clientThread,(LPVOID)this,0,0);
	}
	else
	{
		threadHandle=::CreateThread(0,0,(LPTHREAD_START_ROUTINE)serverThread,(LPVOID)this,0,0);
	}

	
}

/**
* This method decodes the command data and executes commands.
* @param ps ProtocolService object pointer
* @param command the command type
* @param port command port number
* @param data the command data
* @param size size of the data
* @return result
*/
bool CommunicationManager::processCommand(ProtocolService* ps,Command command,int port, char* data,int size)
{
	DWORD result=WaitForSingleObject(processCommandMutex,INFINITE);

	if(result==WAIT_OBJECT_0)
	{
		__try
		{
			switch(command)
			{
				case Event:
				{
					if((port<1)||(port>numberOfEvents)){
						
					}else{
						if(newEvent!=NULL)
						{
							newEvent(port,param);
						}
					}
							
					break;
				}
				case Integer:
				{
					int integerValue = ps->getInteger(data,size);
			
					if((port<1)||(port>numberOfIntegers)){
						
					}else{
						if(newIntegerValue!=NULL)
						{
							newIntegerValue(port,integerValue,param);
						}
					}
			
					break;
				}
				case Double:
				{
					double doubleValue=ps->getDouble(data,size);
			
					if((port<1)||(port>numberOfDoubles)){
						
					}else{
						if(newDoubleValue!=NULL)
						{
							newDoubleValue(port,doubleValue,param);
						}
					}
			
					break;
				}
				case String:
				{
					wchar_t* string=ps->getString(data,size);
			
					if((port<1)||(port>numberOfStrings)){
						
					}else{
						if(newStringValue!=NULL)
						{
							newStringValue(port,string,param);
						}
					}

					delete[] string;
			
					break;
			
			
				}
			}
		}
		__finally
		{
			BOOL boolResult=ReleaseMutex(processCommandMutex);
		}
		
		/*if(boolResult!=TRUE)
		{
				
		}*/
	}
	else
	{
		//TODO
	}

	return true;
		

}

/**
* The class destructor.
*/
CommunicationManager::~CommunicationManager(void)
{
	
	if(serverConnection!=NULL)
	{
		delete serverConnection;
		serverConnection=NULL;
	}

	if(serverMode==SM_SERVER_MULTISESSION)
	{
		std::vector<ServerConnection *>::iterator connection;
		for(connection=serverSessions.begin();connection<serverSessions.end();connection++)
		{
			delete (*connection);
		}

		serverSessions.clear();

	}

	if(threadHandle!=NULL)
	{
		WaitForSingleObject(threadHandle,5000);
	}

	ServerConnection::end();

	CloseHandle(processCommandMutex);
	CloseHandle(sendCommandMutex);
	CloseHandle(sendClientCommandMutex);
	CloseHandle(removeServerMutex);

	if(initError==false)
	{
		WSACleanup();
	}
}

/**
* If the library works in the server mode, this method returns number of connected clients.
* If the library works in the client mode, this method returns 0, if the library is not
* connected to the server or 1, if the library is connected to the server. The method may returns negative value if en error is found.
* @return number of active connections.
*/

int CommunicationManager::numberOfConnections()
{
	
	if(initError)
	{
		return -1;
	}

	int result=0;
	if(serverMode==SM_CLIENT)
	{
		if(connected)
		{	
			result=1;
		}
	}
	else
	{
		if(serverMode==SM_SERVER_MULTISESSION)
		{
			
			std::vector<ServerConnection *>::iterator connection;
			for(connection=serverSessions.begin();connection<serverSessions.end();connection++)
			{
				if((*connection)->isConnected())
				{
					result++;
				}
			}

		}
		else
		{
			if(serverConnection!=NULL)
			{
				if(serverConnection->isConnected())
				{
					result=1;
				}
					
			}
		}
	}
	return result;
}

/**
* Closes the connections.
*/
void CommunicationManager::finishCommunication()
{
	finish=true;
}

/**
* Connetcs the client socket to the remote server.
* @param _this pointer to the CommunicationManager instance
* @return result
*/
bool CommunicationManager::connectClientSocket(CommunicationManager *_this)
{
		bool connectionError=false;
		do
		{
			connectionError=false;
			_this->clientSocket=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
			if(_this->clientSocket==INVALID_SOCKET)
			{
				connectionError=true;
			}
			else
			{
				u_long mode=1;
				int result = ioctlsocket(_this->clientSocket, FIONBIO, &mode);
				
				if (result != NO_ERROR)
				{
					connectionError=true;
				}
				else
				{
					SOCKADDR_IN address; 

					address.sin_family = AF_INET; 
					address.sin_port = htons (_this->port); 
					address.sin_addr.s_addr = inet_addr (_this->w2c(_this->IP.c_str()).c_str()); 	

					if (connect(_this->clientSocket, (SOCKADDR *)&address, sizeof(address)) == SOCKET_ERROR)
					{
						int error = WSAGetLastError();
						if(error == WSAEWOULDBLOCK)
						{
							
							fd_set Write, Err;
							TIMEVAL Timeout;
							int TimeoutSec = 5; 

							FD_ZERO(&Write);
							FD_ZERO(&Err);
							FD_SET(_this->clientSocket, &Write);
							FD_SET(_this->clientSocket, &Err);

							Timeout.tv_sec = TimeoutSec;
							Timeout.tv_usec = 0;

							result = select(0,NULL,&Write,&Err,&Timeout);
							if(result == 0)
							{
								connectionError=true;
							}
							else
							{
								if(FD_ISSET(_this->clientSocket, &Write))
								{
									//connected;
								}
								if(FD_ISSET(_this->clientSocket, &Err))
								{
									connectionError=true;
								}
							}
						}
						else 
						{
							connectionError=true;
							Sleep(100);
						}
					}
					else
					{
						//connected;
					}
				}
				
				
			}
			
		}
		while((_this->finish==false)&&(connectionError==true));

		if(_this->finish==true)
		{
			return false;
		}

		return true;
}

/**
* This method reads data from the remote sender.
* @param ps ProtocolService pointer
* @return error
*/
bool CommunicationManager::readData(ProtocolService* ps)
{
	if(ps->checkHeaderAvailable())
	{
		HeaderInfo header = ps->getHeader();

		int dataSize = header.getDataSize();
		int port = header.getPort();
		Command command =header.getCommand();

		if(dataSize<0||port<0)
		{
			return true;
		}

		bool dataReady=false;
			
		if(dataSize==0)
		{
			dataReady=true;
		}
		else
		{
			int repeats=6;
				
			do{
				dataReady=ps->checkDataAvailable(dataSize);
				repeats=repeats-1;
				if(finish)
				{
					repeats=0;
				}
					
				if(repeats<5)
				{
					Sleep(dataWait);
				}
			}while((dataReady==false)&&(repeats>0));
		}

		if(dataReady)
		{
			char *data=NULL;
			if(dataSize>0)
			{
				//char *data;//=new char[dataSize];
				data=ps->getData(dataSize);
			}
			
			processCommand(ps,command,port,data,dataSize);
			
			if(dataSize>0)
			{
				delete[] data;
			}
		}
	}

	return false;
}

/**
* This method sends the command, if the plugin works as the client. 
* @param command defines value type
* @param port defines the port of the remote receiver.
* @param doubleData double value
* @param integerData integer value
* @param stringData string value
* @return result
*/
bool CommunicationManager::sendClientCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData)
{

	bool sendResult=false;
	DWORD result=WaitForSingleObject(sendClientCommandMutex,INFINITE);


	if(result==WAIT_OBJECT_0)
	{
		__try
		{
			SendError result =protocolService.sendCommand(command,port,doubleData,integerData,stringData);
			if(result==SendErrorOccur)
			{
				writeError=true;
			}
			
			
			if(result==OK)
			{
				timeCounter=0;
				sendResult=true;
			}else{
				sendResult=false;
			}
		}
		__finally
		{
			BOOL boolResult=ReleaseMutex(sendClientCommandMutex);
		}
		
		/*if(boolResult!=TRUE)
		{
				
		}*/
	}
	else
	{
		//TODO
	}

	return sendResult;
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
bool CommunicationManager::sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData)
{
	if(initError==true)
	{
		return false;
	}
	
	DWORD mutexResult=WaitForSingleObject(sendCommandMutex,INFINITE);
	bool result =false;
	bool connectedToRemote=false;

	if(mutexResult==WAIT_OBJECT_0)
	{
		__try
		{
			if(serverMode==SM_CLIENT)
			{
				if(connected)
				{	
					connectedToRemote=true;
					result=sendClientCommand(command, port, doubleData, integerData, stringData);
				}
			}
			else
			{
				if(serverMode==SM_SERVER_MULTISESSION)
				{
					result=true;
					bool sent=false;
					std::vector<ServerConnection *>::iterator connection;
					for(connection=serverSessions.begin();connection<serverSessions.end();connection++)
					{
						if((*connection)->isConnected())
						{
							sent=true;
							connectedToRemote=true;
							result = result && (*connection)->sendCommand(command, port, doubleData, integerData, stringData);
						}
					}

				}
				else
				{
					if(serverConnection!=NULL)
					{
						if(serverConnection->isConnected())
						{
							connectedToRemote=true;
							result=serverConnection->sendCommand(command, port, doubleData, integerData, stringData);
						}
					
					}
				}
			}
		}
		__finally
		{
			BOOL boolResult=ReleaseMutex(sendCommandMutex);
			/*if(boolResult!=TRUE)
			{
				
			}*/
		}
		
		
		
	}
	else
	{
		//TODO
	}

	/*
	if(!result)
	{
		if(connectedToRemote)
		{

		}
	}*/

	return result;
}

/**
* Removes the server connection with client.
* @param connection server connection to remove
* @param id of the server connection
*/
void CommunicationManager::removeServerConnection(ServerConnection* serverConnection,int id)
{
	DWORD result=WaitForSingleObject(removeServerMutex,INFINITE);

	if(result==WAIT_OBJECT_0)
	{
		__try
		{
			if(serverMode==SM_SERVER_MULTISESSION)
			{
				std::vector<ServerConnection *>::iterator connection;
				for(connection=serverSessions.begin();connection<serverSessions.end();connection++)
				{
					if((*connection)->getID()==id)
					{
						ServerConnection* sc = *connection;
						serverSessions.erase(connection);
					
						sc->stopNow(true);
						delete sc;

						break;
					}
				}
			
			}
			else
			{
				if(serverMode==SM_SERVER_SINGLE_SESSION)
				{
					this->serverConnection->stopNow(true);
					delete this->serverConnection;
					this->serverConnection=NULL;
				}
			}
		}
		__finally
		{
			BOOL boolResult=ReleaseMutex(removeServerMutex);	
		}
	}

	
}

/**
* This method creates the server socket.
* @param oneTrial if is set there will be only one trial of creating socket.
* @return result
*/
bool CommunicationManager::createServerSocket(bool oneTrial)
{
	bool connectionError=false;
	do
	{
		serverSocket=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
		if(serverSocket==INVALID_SOCKET)
		{
			connectionError=true;


		}
		else
		{
			u_long mode=1;
			int result = ioctlsocket(serverSocket, FIONBIO, &mode);
				
			if (result != NO_ERROR)
			{
				connectionError=true;
				closesocket(serverSocket);
			}
			else
			{
				SOCKADDR_IN addr; // The address structure for a TCP socket

				addr.sin_family = AF_INET;      // Address family
				addr.sin_port = htons (port);
				addr.sin_addr.s_addr = htonl (INADDR_ANY);  

				if (bind(serverSocket, (LPSOCKADDR)&addr, sizeof(addr)) == SOCKET_ERROR)
				{
					connectionError=true;
					closesocket(serverSocket);
				}
				else
				{
					serverSocketCreated=true;
				}

			}
		}

	}while((!finish)&&(connectionError==true)&&(!oneTrial));

	if(finish==true)
	{
		return false;
	}

	if(connectionError)
	{
		return false;
	}


	return true;
}

/**
 * Implements the client thread.
*/
DWORD WINAPI CommunicationManager::clientThread(LPVOID lpParam)
{
	CommunicationManager *_this = (CommunicationManager*)lpParam;

	

	do
	{
		_this->connected=false;
		connectClientSocket(_this);

		if(_this->finish==false)
		{
			_this->protocolService.init(&_this->clientSocket);
			bool exit=false;
			_this->connected=true;
			_this->writeError=false;
			do
			{
				exit=_this->readData(&_this->protocolService);

				if(_this->protocolService.getHeaderReceived())
				{
					_this->timeCounter=0;
					Sleep(shortWait);
				}
				else
				{
					_this->timeCounter=_this->timeCounter+1;
					Sleep(longWait);
				}

				if(_this->timeCounter>35)
				{
					_this->timeCounter=0;
					_this->sendClientCommand(Action,0,0,0,L"");
				}
			}
			while((_this->finish==false)&&(!exit)&&(_this->writeError==false));
			_this->connected=false;
		}


	}
	while(_this->finish==false);

	closesocket(_this->clientSocket);
	return 0;
}

/**
* Closes server socket
*/
void CommunicationManager::closeServerSocket()
{
	closesocket(serverSocket);
	serverSocketCreated=false;
}

/**
* Implements the main server thread.
*/
DWORD WINAPI CommunicationManager::serverThread(LPVOID lpParam)
{
	CommunicationManager *_this = (CommunicationManager*)lpParam;

	bool connectionError=false;

	_this->createServerSocket(false);

	if(_this->finish)
	{
		closesocket(_this->serverSocket);
		return 0;
	}

	bool timeOut=false;
	bool connected=false;

	if(_this->serverMode==SM_SERVER_MULTISESSION) //multisession
	{
		do
		{
			timeOut=false;
			int numberOfSessions=_this->serverSessions.size();
			if(numberOfSessions>=maxServerSessions)
			{
				Sleep(serverWait);
				if(_this->serverSocketCreated)
				{
					_this->closeServerSocket();
				}
			}
			else
			{
				if(_this->serverSocketCreated)
				{
					if(listen(_this->serverSocket,1)!=0)
					{
						connectionError=true;
					}
					else
					{

						sockaddr addr;
						int addrSize=sizeof(addr);

						SOCKET socket = accept(_this->serverSocket,(struct sockaddr *)&addr,&addrSize);
						if(socket==INVALID_SOCKET)
						{
							int error = WSAGetLastError();
							if(error == WSAEWOULDBLOCK)
							{
								Sleep(5000);
							}
							else
							{
								connectionError=true;
							}
						}
						else
						{
							ServerConnection* pServerConnection=new ServerConnection(socket,_this->sessionID++,_this);
							pServerConnection->start();
							_this->serverSessions.push_back(pServerConnection);
						}
					}


					if(connectionError)
					{
						Sleep(_this->shortWait);
					}

				}
				else
				{
					bool result =_this->createServerSocket(true);
						
					if(!result)
					{
						Sleep(_this->serverSocket);
					}
				}
			}
		}
		while(_this->finish==false);
	
	}
	else
	{
		if(_this->serverMode==SM_SERVER_SINGLE_SESSION)
		{
			do{
				timeOut=false;
				if(_this->serverConnection!=NULL)
				{
					Sleep(serverWait);
					if(_this->serverSocketCreated)
					{
						_this->closeServerSocket();
					}

				}
				else
				{
					if(!_this->serverSocketCreated)
					{
						bool result =_this->createServerSocket(true);
						if(!result)
						{
							Sleep(_this->serverSocket);
						}
					}
					else
					{
						
						
						if(listen(_this->serverSocket,1)!=0)
						{
							connectionError=true;
						}
						else
						{

							sockaddr addr;
							int addrSize=sizeof(addr);

							SOCKET socket = accept(_this->serverSocket,(struct sockaddr *)&addr,&addrSize);
							if(socket==INVALID_SOCKET)
							{
								int error = WSAGetLastError();
								if(error == WSAEWOULDBLOCK)
								{
									Sleep(5000);
								}
								else
								{
									connectionError=true;
								}
							}
							else
							{
								_this->serverConnection=new ServerConnection(socket,_this->sessionID++,_this);
								_this->serverConnection->start();
							}
						}


						if(connectionError)
						{
							Sleep(_this->shortWait);
						}



					}
				}



			}while(_this->finish==false);
		}
	}

	return 0;
}

/**
 * Changes the wide charter text to the 8 bit character text.
 * @param wideText wide text to change
 * @return 8-bit character string
 */
string CommunicationManager::w2c(const wchar_t* wideText)
{
	ostringstream stm;
	wstring wideString=wideText;
	const ctype<char>& ctfacet = use_facet< ctype<char> >( stm.getloc() );
	
	for(size_t i=0; i<wideString.size(); i++)
	{
		stm << ctfacet.narrow( wideString[i], 0 );
	}
	
	string str=stm.str();

	return str;
}