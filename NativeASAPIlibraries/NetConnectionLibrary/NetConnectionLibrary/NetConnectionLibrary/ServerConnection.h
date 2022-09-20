#pragma once

#include <winsock2.h>
#include "ProtocolService.h"
//#include "CommunicationManager.h"

class CommunicationManager;

class ServerConnection
{
public:
	ServerConnection(SOCKET socket, int id,CommunicationManager* owner);
	~ServerConnection(void);
	bool sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData);
	bool isConnected();
	void stopNow(bool noWait);
	void start();
	static void init();
	static void end();
	int getID();

private:
	HANDLE  threadHandle;
	CommunicationManager* owner;
	ProtocolService ps;
	SOCKET socket;
	int id;
	bool connected;
	bool writeError;
	int timeCounter;
	bool endNow;
	bool noWait;
	static HANDLE sendServerCommandMutex;

	static DWORD WINAPI run(LPVOID lpParam);
};

