#pragma once

enum Command{
	None=1,Action,Event,Integer,Double,String 
};

enum SendError{
	OK=1,WrongData,SendErrorOccur,CommandNotRecognized
};

class HeaderInfo
{
public:
	HeaderInfo(void);
	HeaderInfo(Command command,int port, int dataSize);
	~HeaderInfo(void);

	Command getCommand();
	int getDataSize();
	int getPort();
	void setDataSize(int dataSize);
	void setCommand(Command command);	
	void setPort(int port);

private:
	Command command;
	int dataSize;
	int port;
};

