#pragma once
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include "HeaderInfo.h"

class ProtocolService
{
public:
	ProtocolService(void);
	~ProtocolService(void);

private:
	SOCKET* pSocket;
	static const int headerSize=6;
	int offset;
	int offsetBytes[headerSize];
	Command getCommand(char commandByte);
	int getPort(char portByte);
	static const char firstByte=0x40;
	static const char secondByte=0x4e;
	bool headerReceived;
	
	static char* prepareActionFrame(int *pSize);
	static char* prepareEventFrame(int *pSize,int port);
	static char* prepareIntegerFrame(int *pSize,int port, int value);
	static char* prepareDoubleFrame(int *pSize,int port, double value);
	static char* prepareStringFrame(int *pSize,int port, wchar_t* text);

	static const int eventMaxPort=10;
	static const int integerMaxPort=5;
	static const int doubleMaxPort=5;
	static const int stringMaxPort=5;

public:
	bool getHeaderReceived();
	void init(SOCKET* pSocket);
	bool checkHeaderAvailable();
	HeaderInfo getHeader();
	bool checkDataAvailable(int size);
	char* getData(int size);
	wchar_t* getString(char *data,int size);

	int getInteger(char* data,int size);
	double getDouble(char* data,int size);
	SendError sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData);
};

