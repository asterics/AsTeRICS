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
#include "ProtocolService.h"


/**
 * 
 * This class implements the communication protocol for the NetConnection plugin.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Sep 14, 2012
 *         Time: 13:24:48 AM
 */

/**
* The class constructor.
*/
ProtocolService::ProtocolService(void)
{

}

/**
* The class destructor.
*/
ProtocolService::~ProtocolService(void)
{
}

/**
* Initialize object
* @param pSocket socket of the connection
*/
void ProtocolService::init(SOCKET* pSocket)
{
	offset=0;
	this->pSocket=pSocket;
}

/**
* Decodes the integer value form the data.
* @param data data array
* @param size size of the data
* @return the integer value
*/
int ProtocolService::getInteger(char* data,int size)
{
	int value=0;
	for(int i=0;i<size;i++)
	{
		value=value<<8;
		value=value|(data[i] & 0xff);
	}
	return value;
}

/**
* Decodes the double value form the data.
* @param data data array
* @param size size of the data
* @return the double value
*/
double ProtocolService::getDouble(char* data,int size)
{
		
	char *tmpData=new char[size];

	for(int i=0;i<size;i++)
	{
		tmpData[size-i-1]=data[i];
	}

	double *tmpDouble = (double*) tmpData;

	double d=*tmpDouble;
	
	delete [] tmpData;

	return d;
}
	
/**
* Decodes the string value form the data.
* @param data data array
* @param size size of the data
* @return the string value
*/
wchar_t* ProtocolService::getString(char *data,int size)
{
	int stringSize=size/2;

	wchar_t* string = new wchar_t[stringSize+1];

	for(int i=0;i<size;i=i+2)
	{
		unsigned int tmp=0;
		tmp=data[i+1];
		tmp<<8;
		tmp=tmp|(data[i+1] & 0xff );
		string[i/2]=(wchar_t)tmp;
	}

	string[stringSize]=0;

	return string;
}

/**
* Checks if the packet header is available.
* @return true if header is available
*/
bool ProtocolService::checkHeaderAvailable()
{
	headerReceived=false;
	u_long dataSize=0;
	int result = ioctlsocket(*pSocket, FIONREAD, &dataSize);				
	if (result != NO_ERROR)
	{
		//TODO
	}

	if(dataSize<6)
	{
		return false;
	}
	return true;
}

/**
* Checks if data of the command is available.
* @param size of the expected data
* @return true if data is ready
*/
bool ProtocolService::checkDataAvailable(int size)
{
	u_long dataSize=0;
	int result = ioctlsocket(*pSocket, FIONREAD, &dataSize);				
	if (result != NO_ERROR)
	{
		return false;
	}

	if(dataSize<size)
	{
		return false;
	}
	return true;
}

/**
* Gets package data.
* @param size size of the data
* @return data byte array
*/
char* ProtocolService::getData(int size)
{
	if(size==0)
	{
		return NULL;
	}

	char *data = new char[size];

	int result=recv(*pSocket,data,size,0);

	if(result==SOCKET_ERROR)
	{
		return NULL;
	}

	if(result<size)
	{
		//TODO
	}

	return data;
}

/**
* Decodes the command from the packet header
* @param commandByte the command byte 
* @return command of the packet
*/
Command ProtocolService::getCommand(char commandByte)
{
	if((commandByte==0x41)||(commandByte==0x61))
	{
		return Action;
	}
		
	if((commandByte==0x4E)||(commandByte==0x6E))
	{
		return None;
	}
		
	if((commandByte==0x45)||(commandByte==0x65))
	{
		return Event;
	}
		
	if((commandByte==0x49)||(commandByte==0x69))
	{
		return Integer;
	}
		
	if((commandByte==0x44)||(commandByte==0x64))
	{
		return Double;
	}
		
	if((commandByte==0x53)||(commandByte==0x73))
	{
		return String;
	}
		
	return None;
}

/**
* Decodes the port number from the packet header
* @param portByte the port byte 
* @return port number
*/
int ProtocolService::getPort(char portByte)
{
		int port=0;
		return port|portByte;
}

/**
* Decodes the size of the data
* @param first high bite of the size
* @param second low byte of the size
* @return data size
*/
int getDataSize(char first,char second)
{
		int dataSize=0;
		int secondByte=0;
		int firstByte=0;
		
		firstByte=0xFF & first;
		secondByte=0xFF & second;

		dataSize=dataSize|firstByte;
		dataSize=dataSize<<8;
		dataSize=dataSize|secondByte;
		return dataSize;
}

/**
* Returns true if the header was received. Last getHeader call was successful.
* @return true if the header was received.
*/
bool ProtocolService::getHeaderReceived()
{
	return headerReceived;
}

/**
* Gets data of the packet header.
* @return header data
*/
HeaderInfo ProtocolService::getHeader()
{
	char header[headerSize];

	for(int i=0;i<offset;i++)
	{
		header[i]=offsetBytes[i];
	}

	int result=recv(*pSocket,header,headerSize-offset,0);

	if(result==SOCKET_ERROR)
	{
		return HeaderInfo(None,-1,-1);
	}
	
	if((result>=0)&&(result<headerSize-offset))
	{
		offset = result+offset;
		for(int i=0;i<offset;i++)
		{
			offsetBytes[i]=header[i];
		}
		
		return HeaderInfo(None,0,0);
	}

	if((header[0]==firstByte)&&(header[1]==secondByte))
	{
		Command command = getCommand(header[2]);
		int port =getPort(header[3]);
		int size=getDataSize(header[4],header[5]);
				
		///headerReceived=true;
		headerReceived=true;		
		return HeaderInfo(command,port,size);
				
				//short dataSize=inputStream.readShort();
				/*
				if(command==Command.None)
				{
					//TODO 
				}
				
				if(checkCommandData(command)==false && dataSize>0)
				{
					//TODO
				}*/
				
				
		}
		else
		{
			bool found=false;
			for(int i=1;i<headerSize-1;i++)
			{
				if((header[i]==firstByte)&&(header[i+1]==secondByte))
				{
					offset=headerSize-i;
					for(int j=0;j<offset;j++)
					{
						offsetBytes[j]=header[j+i];
					}
						
					found=true; 
					break;
				}
			}
				
			if(!found)
			{
				if(header[headerSize-1]==firstByte)
				{
					offset=1;
					offsetBytes[0]=firstByte;
				}
			}
				
		}

	
		return HeaderInfo(None,0,0);


	//ret
}

/**
* This method sends command through the network. 
* @param command defines value type
* @param port defines the port of the remote receiver.
* @param doubleData double value
* @param integerData integer value
* @param stringData string value
* @param send result
*/
SendError ProtocolService::sendCommand(Command command,int port,double doubleData,int integerData,wchar_t* stringData)
{
	char * frame=NULL;
	int frameSize=0;

	switch (command)
	{
		case Action:
		{
			frame=prepareActionFrame(&frameSize);
			break;
		}
		case Event:
		{
			frame = prepareEventFrame(&frameSize,port);
			break;
		}
		case Integer:
		{
			frame=prepareIntegerFrame(&frameSize,port,integerData);
			break;
		}
		case Double:
		{
			frame=prepareDoubleFrame(&frameSize,port,doubleData);
			break;
		}
		case String:
		{
			frame=prepareStringFrame(&frameSize,port, stringData);
			break;
		}
		default:
		{
			///AstericsErrorHandling.instance.getLogger().warning("Command not found");
			return CommandNotRecognized;
		}
	}
		
	if(frame==NULL)
	{
		//AstericsErrorHandling.instance.getLogger().warning("Wrong data");
		return WrongData;
	}
	

	int sendResult=send(*pSocket,frame,frameSize,0);
	
	delete[] frame;

	if(sendResult==SOCKET_ERROR)
	{
		return SendErrorOccur;
	}
		
	return 	OK;


}

/**
* Prepares the action packet frame.
* @param pSize size of the frame
* @return Bytes array of the action packet
*/
char* ProtocolService::prepareActionFrame(int *pSize)
{
			
	char* frame=new char[6];
	*pSize=6;
	frame[0]=firstByte;
	frame[1]=secondByte;
	frame[2]=0x41;
	frame[3]=0;
	frame[4]=0;
	frame[5]=0;
		
	return frame;
}

/**
* Prepares the event packet frame.
* @param pSize size of the frame
* @param port the event port
* @return Bytes array of the event packet
*/
char* ProtocolService::prepareEventFrame(int *pSize,int port)
{
		
	if((port<0)||(port>eventMaxPort))
	{
		return NULL;
	}
		
	char* frame=new char[6];
	*pSize=6;
	frame[0]=firstByte;
	frame[1]=secondByte;
	frame[2]=0x45;
	frame[3]=(char)port;
	frame[4]=0;
	frame[5]=0;
		
	return frame;
}

/**
* Prepares the integer packet frame.
* @param pSize size of the frame
* @param port the integer port
* @param value integer value
* @return Bytes array of the integer packet
*/
char* ProtocolService::prepareIntegerFrame(int *pSize,int port, int value)
{
			
	if((port<0)||(port>integerMaxPort))
	{
		return NULL;
	}
		
		 
		
		char* frame=new char[10];
		*pSize=10;
		frame[0]=firstByte;
		frame[1]=secondByte;
		frame[2]=0x49;
		frame[3]=(char)port;
		frame[4]=0;
		frame[5]=0x04;
		frame[6]=(char)(value >> 24);
		frame[7]=(char)(value >> 16);
		frame[8]=(char)(value>> 8);
		frame[9]= (char) value;
		
		return frame;
}

/**
* Prepares the double packet frame.
* @param pSize size of the frame
* @param port the double port
* @param value double value
* @return Bytes array of the double packet
*/
char* ProtocolService::prepareDoubleFrame(int *pSize,int port, double value)
{
			
	if((port<0)||(port>doubleMaxPort))
	{
		return NULL;
	}
		
	//long doubleValue = Double.doubleToRawLongBits(value); 
	char tmpDouble[8];
	char* pDouble = (char*)&value;

	for(int i=0;i<8;i++)
	{
		
		tmpDouble[i]=*(pDouble+7-i);
	}
		
	char* frame=new char[14];
	*pSize=14;

	frame[0]=firstByte;
	frame[1]=secondByte;
	frame[2]=0x44;
	frame[3]=(char)port;
	frame[4]=0;
	frame[5]=0x08;
	for(int i=0;i<8;i++)
	{
		frame[6+i]=tmpDouble[i];
	}
			
	return frame;
}

/**
* Prepares the string packet frame.
* @param pSize size of the frame
* @param port the string port
* @param text string value
* @return Bytes array of the string packet
*/
char* ProtocolService::prepareStringFrame(int *pSize,int port, wchar_t* text)
{
	if((port<0)||(port>stringMaxPort))
	{
		return NULL;
	}

	int stringLength=wcslen(text);

	wchar_t * stringValue=NULL;
	int strlength=0;

	if(stringLength>0x12c)
	{
		stringValue=new wchar_t[0x12c+1];
		for(int i=0;i<0x12c;i++)
		{
			stringValue[i]=text[i];
		}

		stringValue[0x12c]=0;
		strlength=0x12c;
	}
	else
	{
		stringValue=new wchar_t[stringLength+1];
		wcscpy_s(stringValue,stringLength+1,text);
		strlength=stringLength;
	}

	int frameData=2*strlength;

	char *frame=new char[frameData+6];
	*pSize=frameData+6;

	frame[0]=firstByte;
	frame[1]=secondByte;
	frame[2]=0x53;
	frame[3]=(char)port;
	frame[4]=(char)(frameData>> 8);
	frame[5]= (char) frameData;
			
	for(int i=0;i<strlength;i++)
	{
		frame[6+2*i]=stringValue[i]>>8;
		frame[6+2*i+1]=stringValue[i];
	}

	delete[] stringValue;
	return frame;
}