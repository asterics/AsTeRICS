#pragma once
#include <vector>
#include <iostream>
#include <sstream>
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include"NetConnectionLibrary.h"
#include "ProtocolService.h"
#include "ServerConnection.h"

using namespace std;

class CommunicationManager
{
public:
	CommunicationManager(ServerMode serverMode,LPWSTR IP,int port,NewEvent newEvent,NewIntegerValue newIntegerValue,NewDoubleValue newDoubleValue,NewStringValue newStringValue, LPVOID param);
	~CommunicationManager(void);
	void finishCommunication();
	bool sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData);
	int numberOfConnections();

private:

	wstring IP;
	int port;

	bool initError;

	std::vector<ServerConnection *> serverSessions;

	bool connected;
	bool finish;
	ServerMode serverMode;

	SOCKET clientSocket;
	SOCKET serverSocket;
	bool serverSocketCreated;

	ProtocolService protocolService;
	bool readData(ProtocolService* ps);

	ServerConnection* serverConnection;
	void closeServerSocket();
	int sessionID;
	void removeServerConnection(ServerConnection* serverConnection,int id);

	static DWORD WINAPI serverThread(LPVOID lpParam);
	static DWORD WINAPI clientThread(LPVOID lpParam);
	static bool connectClientSocket(CommunicationManager *_this);
	
	static const int maxServerSessions=10;
	static const int shortWait =5;
	static const int longWait=250;
	static const int dataWait=50;
	static const int errorWait=50;
	static const int serverWait=500;

	static const int numberOfEvents=10;
	static const int numberOfIntegers=5;
	static const int numberOfDoubles=5;
	static const int numberOfStrings=5;
	

	string w2c(const wchar_t* wideText);
	bool processCommand(ProtocolService* ps,Command command,int port, char* data,int size);

	bool sendClientCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData);

	bool CommunicationManager::createServerSocket(bool oneTrial);

	int timeCounter;
	HANDLE processCommandMutex;
	HANDLE sendCommandMutex;
	HANDLE sendClientCommandMutex;
	HANDLE removeServerMutex;
	HANDLE threadHandle;


	NewEvent newEvent;
	NewIntegerValue newIntegerValue;
	NewDoubleValue newDoubleValue;
	NewStringValue newStringValue;
	LPVOID param;

	bool writeError;
	friend class ServerConnection;
};

