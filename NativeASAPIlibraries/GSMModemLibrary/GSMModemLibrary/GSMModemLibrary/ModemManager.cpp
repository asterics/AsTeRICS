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
 * This class manages the modem which is used to send and receive SMS.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "StdAfx.h"
#include "ModemManager.h"
#include <time.h>
#include "Errors.h"


/**
 * Thread function which reads incoming messages.
 * @param lpParam pointer to the ModemManager class object
 * @return 0
 */
DWORD WINAPI readSMS(LPVOID lpParam)
{
	ModemManager *pManager=(ModemManager*)lpParam;
	bool finish=false;

	do
	{
		if(pManager->finishRead)
		{
			return 0;
		}
		DWORD result=WaitForSingleObject(pManager->portAccess,2*1000);  //2s
		
		if(result==WAIT_OBJECT_0)
		{
			if(pManager->finishRead)
			{
				finish=true;
				BOOL boolResult=ReleaseMutex(pManager->portAccess);
				/*
				if(boolResult!=TRUE)
				{
				
				}*/
			}
			else
			{
				int result= pManager->decodeSMS();
				if(result<0)
				{
					if(pManager->errorCallback!=NULL)
					{
						pManager->errorCallback(result,pManager->param);
					}
				}

				BOOL boolResult=ReleaseMutex(pManager->portAccess);
				/*
				if(boolResult!=TRUE)
				{
				
				}*/

				for(int i=0;i<40;i++)
				{
					Sleep(200);
					if(pManager->finishRead)
					{
						finish=true;
						break;
				
					}
				}

				
			}
		}
		else
		{
			if(pManager->finishRead)
			{
				finish=true;
			}
			else
			{
				switch(result)
				{
					case WAIT_TIMEOUT:
					{
						break;
					}
					case WAIT_ABANDONED:
						break;
					case WAIT_FAILED:
						break;
					default:
						;
				};
			}
		}
		
	}
	while(!finish);

	/*
	CloseHandle(pManager->readSMSThreadHandle);
	pManager->readSMSThreadHandle=NULL;
	*/

	return 0;
}


/**
 * Thread function which initializes the ModemManager object.
 * @param lpParam pointer to the ModemManager class object
 * @return 0
 */
DWORD WINAPI initialize (LPVOID lpParam)
{
	ModemManager *pManager=(ModemManager*)lpParam;

	int result=pManager->initialize();

	if(result<0)
	{
		if(result!=Abort)
		{
			if(pManager->errorCallback!=NULL)
			{
		
				pManager->errorCallback(result,pManager->param);
			}
			pManager->initialized=false;
		}
	}
	else
	{
		pManager->initialized=true;

	}

	if(!pManager->finishRead)
	{
		CloseHandle(pManager->initThreadHandle);
		pManager->initThreadHandle=NULL;
	}

	return 0;
}

/**
 * Thread function which sends the messages.
 * @param lpParam pointer to the ModemManager class object.
 * @return 0
 */
DWORD WINAPI sendSMSThread(LPVOID lpParam)
{
	ModemManager *pManager=(ModemManager*)lpParam;
	
	DWORD result;

	bool sent=false;

	for(int i=0;i<4;i++)
	{
		result=WaitForSingleObject(pManager->portAccess,1000);
		
		if(result==WAIT_OBJECT_0)
		{
			if(pManager->finishRead)
			{
				BOOL boolResult=ReleaseMutex(pManager->portAccess);

				break;
			}
			else
			{
				int result= pManager->sendSingleSMS();
				if(result<0)
				{
					if(pManager->errorCallback!=NULL)
					{
						pManager->errorCallback(result,pManager->param);
					}
				}

				sent=true;
				BOOL boolResult=ReleaseMutex(pManager->portAccess);
				/*
				if(boolResult!=TRUE)
				{
				
				}*/

				break;
			}
		}
		else
		{
			if(pManager->finishRead)
			{
				break;
			}
			else
			{
				switch(result)
				{
					case WAIT_TIMEOUT:
					{
						break;
					}
					case WAIT_ABANDONED:
						break;
					case WAIT_FAILED:
						break;
					default:
						;
				};
			}
		}
	}

	if(pManager->errorCallback!=NULL)
	{
		if(!sent)
		{
			pManager->errorCallback(SMS_not_sent,pManager->param);
		}
		else
		{
			pManager->errorCallback(1,pManager->param);
		}
	}
	
	CloseHandle(pManager->smsThreadHandle);
	pManager->smsThreadHandle=NULL;

	return 0;

}

/**
 * Thread function which sends the multipart messages.
 * @param lpParam pointer to the ModemManager class object.
 * @return 0
 */
DWORD WINAPI sendMultiPartSMSThread(LPVOID lpParam)
{
	
	ModemManager *pManager=(ModemManager*)lpParam;
	
	DWORD result;

	bool sent=false;

	for(int i=0;i<4;i++)
	{
		result=WaitForSingleObject(pManager->portAccess,1000);
		
		if(result==WAIT_OBJECT_0)
		{
			if(pManager->finishRead)
			{
				BOOL boolResult=ReleaseMutex(pManager->portAccess);
				/*
				if(boolResult!=TRUE)
				{
				
				}*/

				break;
			}
			else
			{
				int result= pManager->sendMultipartSMS();
				if(result<0)
				{
					if(pManager->errorCallback!=NULL)
					{
						pManager->errorCallback(result,pManager->param);
					}
				}
	
				sent=true;

				BOOL boolResult=ReleaseMutex(pManager->portAccess);
				/*
				if(boolResult!=TRUE)
				{
				
				}*/

				break;
			}
		}
		else
		{
			if(pManager->finishRead)
			{
				break;
			}
			else
			{
				switch(result)
				{
					case WAIT_TIMEOUT:
					{
						break;
					}
					case WAIT_ABANDONED:
						break;
					case WAIT_FAILED:
						break;
					default:
						;
				};
			}
		}
	}

	
	if(pManager->errorCallback!=NULL)
	{
		if(!sent)
		{
			pManager->errorCallback(SMS_not_sent,pManager->param);
		}
		else
		{	
			pManager->errorCallback(1,pManager->param);
		}
	}
	
	CloseHandle(pManager->smsThreadHandle);
	pManager->smsThreadHandle=NULL;

	return 0;
}

/**
 * The class constructor.
 * @param comPort serial port which is used by the modem
 */
ModemManager::ModemManager(LPWSTR comPort)
{
	com=comPort;
	initialized=false;
	finishRead=false;
	hCommDev=NULL;

	hasPin=false;
	hasCenterID=false;

	newSMSAvailable=NULL;
	errorCallback=NULL;
	readSMSThreadHandle=NULL;
	param=NULL;
}

/**
 * The class destructor.
 */
ModemManager::~ModemManager(void)
{
	
}

/**
 * This function sets the interface call-back functions.
 * @param newSMSAvailable pointer to the function which is called when the new SMS is available
 * @param errorCallback pointer to the function which is called when there is an error
 * @param param parameter defined by the user
 * @return 1
 */
int ModemManager::setCallbacks(NewSMSAvailable newSMSAvailable,ErrorCallback errorCallback,LPVOID param)
{
	this->newSMSAvailable=newSMSAvailable;
	this->errorCallback=errorCallback;
	this->param=param;

	return 1;
}

/**
 * Initializes the class object.
 * @return error number
 */
int ModemManager::initialize()
{
	portAccess=NULL;
	smsThreadHandle=NULL;
	readSMSThreadHandle=NULL;

	int result = initCom();

	if(result<0)
	{
		return result;
	}

	result = initModem();
	if(result<0)
	{
		return result;
	}
	
	
	portAccess=CreateMutex(NULL,FALSE,NULL);
	if(portAccess==NULL)
	{
		CloseHandle(hCommDev);
		hCommDev=NULL;
		return Library_initialize_error;
	}

	finishRead=false;

	readSMSThreadHandle=CreateThread(NULL,0,readSMS,this,0,NULL);
	if(readSMSThreadHandle==NULL)
	{
		CloseHandle(hCommDev);
		CloseHandle(portAccess);
		hCommDev=NULL;
		portAccess=NULL;
		return Library_initialize_error;
	}
	
	if(errorCallback!=NULL)
	{
		errorCallback(initialization_complete,param);
	}
	
	return 1;
}

/**
 * Prepares the initialization of the class object process.
 * @param pin the pin for the SIM card
 * @param smsCenter the id of the SMS center
 * @return error number
 */
int ModemManager::init(LPCWSTR pin, LPCWSTR smsCenter)
{
	initialized=false;


	hasPin=false;
	hasCenterID=false;

	if(wcslen(pin)>0)
	{
		atManager.setPin(pin);
		hasPin=true;
	}
	
	if(wcslen(smsCenter)>0)
	{
		atManager.setMessageCenterID(smsCenter);
		hasCenterID=true;
	}

	initThreadHandle=CreateThread(NULL,0,::initialize,this,0,NULL);
	if(initThreadHandle==NULL)
	{
		return Library_initialize_error;
	}

	return 1;

}

/**
 * Closes the object. 
 * @return 1
 */
int ModemManager::close()
{
	
	finishRead=true;
	if(readSMSThreadHandle!=NULL)
	{
		DWORD result=WaitForSingleObject(readSMSThreadHandle,10*1000);
		CloseHandle(readSMSThreadHandle);
	}
	else
	{
		Sleep(700);
	}
	//To do
	
	CloseHandle(portAccess);
	CloseHandle(hCommDev);
	initialized=false;

	if(initThreadHandle!=NULL)
	{
		CloseHandle(initThreadHandle);
		initThreadHandle=NULL;
	}

	return 1;
}

/**
 * Initializes the serial port.
 * @return error number
 */
int ModemManager::initCom()
{
	wstring comPort=L"\\\\.\\";
	comPort=comPort+com;

	hCommDev=CreateFile(comPort.c_str(),GENERIC_READ|GENERIC_WRITE,0,NULL,OPEN_EXISTING,NULL,NULL);

	if(hCommDev==INVALID_HANDLE_VALUE)
	{
		//int x= GetLastError();
		return COMM_initialize_false;
	}

	BOOL Result;

	Result=SetupComm(hCommDev,inputBufferSize,outputBufferSize);

	if(Result==FALSE)
	{
		return COMM_initialize_false;
	}

	DCB dcb;
	dcb.DCBlength=sizeof(dcb);

	Result=GetCommState(hCommDev,&dcb);
	if(Result==FALSE)
	{
		return COMM_initialize_false;
	}
	
	dcb.BaudRate=CBR_115200;
	dcb.Parity=FALSE;
	//dcb.StopBits=ONESTOPBIT;
	dcb.ByteSize=8;
	Result=SetCommState(hCommDev,&dcb);
	if(Result==FALSE)
	{
		return COMM_initialize_false;
	}

	Result=GetCommMask(hCommDev,&eventMask);
	if(Result==FALSE)
	{
		return COMM_initialize_false;
	}

	Result=SetCommMask(hCommDev,EV_TXEMPTY);
	if(Result==FALSE)
	{
		return COMM_initialize_false;
	}

	return 1;
}

/**
 * Initializes the GSM modem
 * @return error number
 */
int ModemManager::initModem()
{
	bool echoDetected=false;

	int result=processCommand(ATC_AT);

	if(result<0)
	{
		if(result==Abort)
		{
			return Abort;
		}

		if(result==Echo_detected)
		{
			echoDetected=true;			
		}
		else
		{
			return No_respond_on_AT_command;
		}
	}

	if(echoDetected)
	{
		int result=processCommand(ATC_ATE0);
		{
			if(result<0)
			{	
				if(result==Abort)
				{
					return Abort;
				}
		
				return Modem_initialize_false;
			}
		}

		result=processCommand(ATC_AT);
		if(result<0)
		{
			if(result==Abort)
			{
				return Abort;
			}
		
			return No_respond_on_AT_command;
		}
	}

	if(hasPin)
	{
		int result=processCommand(ATC_CPIN);
		if(result<0)
		{
			if(result==Abort)
			{
				return Abort;
			}

			//no need to stop here
		}
	}
	
	result=processCREGCommand();
	if(result<0)
	{
		if(result==Abort)
		{
			return result;
		}

		return Cannot_register_to_the_network;
	}
	
	result=processCommand(ATC_CMGF);
	if(result<0)
	{
		if(result==Abort)
		{
			return result;
		}

		return Modem_initialize_false;
	}

	if(hasCenterID)
	{
		int result=processCommand(ATC_CSCA);
		if(result<0)
		{
			if(result==Abort)
			{
				return result;
			}
			return Modem_initialize_false;
		}
	}

	result=processCommand(ATC_CNMI);
	if(result<0)
	{
		if(result==Abort)
		{
			return result;
		}
		return Modem_initialize_false;
	}

	return result;

}

/**
 * Writes data to the serial port.
 * @param numberOfBytesToWrite number of bytes to write
 * @param smsBuffer defines which buffer should be written 
 * @return error number
 */
int ModemManager::writeToComm(DWORD numberOfBytesToWrite,bool smsBuffer)
{
	DWORD numberOfBytesWriten;



	char *pBufferPointer;
	if(smsBuffer)
	{
		pBufferPointer=&outputSMSBuffer[0];
	}
	else
	{
		pBufferPointer=&outputBuffer[0];
	}

	
		if(WriteFile(hCommDev,pBufferPointer,numberOfBytesToWrite,&numberOfBytesWriten,NULL)==TRUE)
	{
		


		if(WaitCommEvent(hCommDev,&eventMask,NULL)==TRUE)
		{
			return 1;
		}
		else
		{
			long l=GetLastError();
			if (l==ERROR_IO_PENDING)
			{
				return 1;
			}
			else
			{
				return Write_Modem_Port_error;
			}
		}
	}
	else
	{
		return Write_Modem_Port_error;
	}
	return 1;
}

/**
 * Reads data from the serial port
 * @param numberOfBytesRead number of bytes read
 * @param flushIfFull defines if the buffer should be flushed
 * @return error number
 */
int ModemManager::readComm(DWORD& numberOfBytesRead,bool flushIfFull)
{
	numberOfBytesRead=0;
	COMSTAT stat;
	DWORD errors;

	ClearCommError(hCommDev,&errors,&stat);
	DWORD numberOfBytesToRead;
	bool bufferFull=false;

	if(stat.cbInQue>0)
	{
		if(stat.cbInQue>inputBufferSize)
		{
			numberOfBytesToRead=inputBufferSize;
			bufferFull=true;
		}
		else
		{
			numberOfBytesToRead=stat.cbInQue;
		}

		if(ReadFile(hCommDev,&inputBuffer[0],numberOfBytesToRead,&numberOfBytesRead,NULL)==TRUE)
		{
			if((flushIfFull)&&(bufferFull))
			{
				PurgeComm(hCommDev,PURGE_RXCLEAR);
			}
			return 1;
		}
		else
		{
			if((flushIfFull)&&(bufferFull))
			{
				PurgeComm(hCommDev,PURGE_RXCLEAR);
			}
			return Read_Modem_Port_error;
		}

	}
	else
	{
		numberOfBytesRead=0;
		return 1;
	}

}


/**
 * Writes data to the serial port and reads the answer
 * @param dataSize number of bytes to write
 * @param numberOfBytesRead number of bytes read
 * @return error number
 */
int ModemManager::sendBuffer(int dataSize,DWORD& numberOfBytesRead)
{
	int result;
	int bytesToSend=dataSize;

	
	if(dataSize+2>=outputBufferSize)
	{
		return Not_enought_space_in_buffer;
	}

	//outputBuffer[dataSize]=0x0D;
	//outputBuffer[dataSize+1]=0x0A;
	//bytesToSend=bytesToSend+2;
	

	result=writeToComm(bytesToSend);

	if(result<0)
	{
		return result;
	}

	int loops=0;
	bool finish=false;

	do
	{
		Sleep(500);
	
		numberOfBytesRead=0;
		result=readComm(numberOfBytesRead);

		if(result<0)
		{
			return result;
		}

		if(numberOfBytesRead>0)
		{
			finish=true;
		}
		else
		{
			if(loops>40)
			{
				return No_modem_answer;
			}
		}

		loops++;

	}while(finish==false);

	return 1;

}

/**
 * Sends command to the modem and reads the answer.
 * @param command command send to the modem
 * @param numberOfBytesRead number of bytes of the modem answer
 * @param commandData addictional data for the command
 * @return error number
 */
int ModemManager::sendCommand(ATCommand command,DWORD& numberOfBytesRead,int commandData)
{
	int bytesToSend=0;
	int result;
	if(command==ATC_CMGS)
	{
		atManager.prepareBufferSend(outputSMSBuffer,outputSMSBufferSize,command,bytesToSend, commandData);
		result=writeToComm(bytesToSend,true);
	}
	else
	{
		atManager.prepareBufferSend(outputBuffer,outputBufferSize,command,bytesToSend, commandData);
		result=writeToComm(bytesToSend);
	}
	

	if(result<0)
	{
		return result;
	}

	int startSleep=50;
	int readSleep=50;
	int iterations=5;

	if((command==ATC_CMGS)||(command==ATC_CMGL)||(command==ATC_CMGD))
	{
		startSleep=200;
	}

	if(command==ATC_CPIN)
	{
		startSleep=1000;
		readSleep=500;
		iterations=20;
	}
	
	
	numberOfBytesRead=0;

	bool flushBuffer=false;

	if(command==ATC_CMGL)
	{
		flushBuffer=true;
	}

	Sleep(startSleep);

	bool finish=false;
	int i=0;

	do
	{
		result=readComm(numberOfBytesRead,flushBuffer);

		if(result<0)
		{
			return result;

		}
		else
		{
			if(numberOfBytesRead==0)
			{
				if(finishRead)
				{
					finish=true;
					break;
				}
				else
				{
					if(i<iterations)
					{
						Sleep(readSleep);
					}
					else
					{
						finish=true;
					}
				}
			}
			else
			{
				finish=true;
			}
		}
		i++;
	}while(!finish);

	if(finishRead)
	{
		return Abort;
	}

	return 1;

}

/**
 * Process the CREG command.
 * @return error number
 */
int ModemManager::processCREGCommand()
{
	
	bool finish=false;

	ATCommandResult atResult=ATR_NONE;

	int i=0;

	do
	{
		DWORD numberOfBytesRead=0;
		int result = sendCommand(ATC_CREG,numberOfBytesRead);

		if(result<0)
		{
			return result;
		}

	
		atResult=ATR_NONE;
		atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CREG,atResult);

		if(atResult==ATR_REGISTERED)
		{
			finish=true;
		}
		else
		{
			if(finishRead)
			{
				finish=true;
			}
			else
			{
				if(i<120)
				{
					Sleep(600);
				}
				else
				{
					finish=true;
				}
			}
		}

		i++;
	}while(!finish);

	
	if(atResult==ATR_REGISTERED)
	{
		return 1;
	}
	else
	{
		if(finishRead)
		{
			return Abort;
		}
		return Cannot_register_to_the_network;
	}
}

/**
 * Process the command. Sends the command and verifies the answer.
 * @param command command send to the modem
 * @return error number
 */
int ModemManager::processCommand(ATCommand command)
{
	DWORD numberOfBytesRead=0;
	int result = sendCommand(command,numberOfBytesRead);

	if(result<0)
	{
		return result;
	}

	//inputBuffer[numberOfBytesRead]=0;
	ATCommandResult atResult=ATR_NONE;
	atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,command,atResult);

	if(atResult==ATR_OK)
	{
		return 1;
	}
	else
	{
		if(atResult==ATR_AT_OK)
		{
			return Echo_detected;
		}
		return Command_fail;
	}
}

/**
 * Decodes received SMS.
 * @return error number
 */
int ModemManager::decodeSMS()
{
	DWORD numberOfBytesRead=0;
	int result = sendCommand(ATC_CMGL,numberOfBytesRead);

	if(result<0)
	{
		return result;
	}

	vector<InputWord> readData;
	vector<InputSMSinformation> smsInfo;

	result=atManager.verifyReadSMS(inputBuffer,numberOfBytesRead,readData,smsInfo); 
	if(result<0)
	{
		return result;
	}

	int numberOfSMSs=smsInfo.size();

	if(numberOfSMSs>0)
	{
		int j=1;
		for(int i=0;i<numberOfSMSs;i++)
		{
			wstring phoneID;
			wstring message;
			bool moreMessages=false;
			bool multiPartMessage=false;
			unsigned int partNumber=0;
			unsigned int numberOfParts=0;
			unsigned long messageReference=0;
			int result=smsEncoder.decodeSMS(&inputBuffer[readData[j].worldBeginInBuffer],readData[j].worldSize(),smsInfo[i].length,message,phoneID,multiPartMessage,
				messageReference,numberOfParts,partNumber,moreMessages);
			if(result<0)
			{
				//return -1;  //too restrictid ??
				continue;
			}
			
			if(multiPartMessage)
			{
				smsInfo[i].multipart=true;
				//bool referenceExist=multiPartMessageManager.isReferenceExist(messageReference);
				int result = multiPartMessageManager.addPart(messageReference,numberOfParts,partNumber,!moreMessages,phoneID,message,smsInfo[i].possition);
			}
			else
			{
				smsInfo[i].multipart=false;
				if(newSMSAvailable!=NULL)
				{
					newSMSAvailable(phoneID.c_str(),message.c_str(),param);
				}
			}
			
			j=j+2;

		}

		for(int i=0;i<numberOfSMSs;i++)
		{
			if(smsInfo[i].multipart==false)
			{
				numberOfBytesRead=0;
				int result = sendCommand(ATC_CMGD,numberOfBytesRead,smsInfo[i].possition);

				if(result<0)
				{
					continue;
				}

				ATCommandResult atResult=ATR_NONE;
				atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGD,atResult);

				if(atResult!=ATR_OK)
				{
					continue;
				}
			}
		}

		vector<SMSData> multiPartSMS;
		multiPartMessageManager.getMessagesToSend(multiPartSMS);
		for(unsigned int i=0;i<multiPartSMS.size();i++)
		{
			if(newSMSAvailable!=NULL)
			{
				newSMSAvailable(multiPartSMS[i].phoneID.c_str() ,multiPartSMS[i].messageContent.c_str(),param);
			}
		}

		vector<int> messagesToRemove;
		multiPartMessageManager.getPositionsOfFinishedMessages(messagesToRemove);
		vector<int>::iterator it;

		if(messagesToRemove.size()>0)
		{	
			for(it=messagesToRemove.begin() ; it < messagesToRemove.end(); it++ )
			{
				numberOfBytesRead=0;
				int result = sendCommand(ATC_CMGD,numberOfBytesRead,*it);

				if(result<0)
				{
					continue;
				}

				ATCommandResult atResult=ATR_NONE;
				atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGD,atResult);

				/*
				if(atResult!=ATR_OK)
				{
				}*/
			}
		}

		multiPartMessageManager.removeFinishedMessages();
	}

	return 1;
}

/**
 * Sends single SMS.
 * @return error number
 */
int ModemManager::sendSingleSMS()
{
	int octets=0;

	smsBufferIndex=smsEncoder.prepareSMSSinglePacket(outputBuffer,0,outputBufferSize,phoneID.c_str(),messageContent.c_str(),smsCoding,octets);

	if(smsBufferIndex<0)
	{
		return Not_enought_space_in_buffer;
	}

	int octetsToSend=octets-1; //First octets shouldn't be count.

	atManager.setOctetCount(octetsToSend);
	
	DWORD numberOfBytesRead=0;
	int result = sendCommand(ATC_CMGS,numberOfBytesRead);

	if(result<0)
	{
		return result;
	}

	//inputBuffer[numberOfBytesRead]=0;
	ATCommandResult atResult=ATR_NONE;
	result =atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGS,atResult);

	if(atResult!=ATR_WAIT_FOR_MESSAGE)
	{
		return SMS_send_error;
	}
	numberOfBytesRead=0;

	outputBuffer[smsBufferIndex]=0x1a;
	smsBufferIndex++;
	sendBuffer(smsBufferIndex,numberOfBytesRead);

	atResult=ATR_NONE;
	result =atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGS_2,atResult);

	if(atResult!=ATR_OK)
	{
		return SMS_send_error;
	}

	return 1;
}

/**
 * Sends multi-part SMS.
 * @return error number
 */
int ModemManager::sendMultipartSMS()
{

	int contentSize=messageContent.size();

	int partNumber;
	
	vector<SubjectIndicator> subjectParts;

	if(smsCoding==C_UNICODE)
	{
		partNumber=contentSize/maxUnicodeSize;

		if(contentSize%maxUnicodeSize>0)
		{
			partNumber++;
		}
	}
	else
	{
		smsEncoder.prepare7BitsParts(messageContent.c_str(),subjectParts);
		partNumber=subjectParts.size();
	}

	int smsValue=rand() % 0xFE+1;

	for(int i=0;i<partNumber;i++)
	{
		int octets=0;
		int partStartIndex;
		int partContentSize;

		if(smsCoding==C_UNICODE)
		{
			partStartIndex=maxUnicodeSize*i;
			partContentSize=maxUnicodeSize;

			if(i+1==partNumber)
			{
				partContentSize=contentSize%maxUnicodeSize;
			}
		}
		else
		{
			partStartIndex=subjectParts[i].partBegin;
			partContentSize=subjectParts[i].partSize;
		}

		
		smsBufferIndex=smsEncoder.prepareSMSMultiPartPacket(outputBuffer,0,outputBufferSize,phoneID.c_str(),messageContent.c_str(),
			partStartIndex,partContentSize,smsCoding,octets,i+1,partNumber,smsValue);

		int octetsToSend=octets-1; //First octets shouldn't be count.

		atManager.setOctetCount(octetsToSend);
	
		DWORD numberOfBytesRead=0;
		int result = sendCommand(ATC_CMGS,numberOfBytesRead);

		if(result<0)
		{
			return result;
		}

		//inputBuffer[numberOfBytesRead]=0;
		ATCommandResult atResult=ATR_NONE;
		result =atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGS,atResult);

		if(atResult!=ATR_WAIT_FOR_MESSAGE)
		{
			return SMS_send_error;
		}
		numberOfBytesRead=0;

		outputBuffer[smsBufferIndex]=0x1a;
		smsBufferIndex++;
		sendBuffer(smsBufferIndex,numberOfBytesRead);

		atResult=ATR_NONE;
		result =atManager.verifyModemAnswer(inputBuffer,numberOfBytesRead,ATC_CMGS_2,atResult);

		if(atResult!=ATR_OK)
		{
			return SMS_send_error;
		}

		Sleep(50);
	}

	return 1;
}

/**
 * Sends the SMS
 * @param recipientID SMS recipient ID
 * @param subject SMS content
 * @return error number
 */
int ModemManager::sendSMS(LPWSTR recipientID, LPWSTR subject)
{
	if(!initialized)
	{
		return Library_is_not_ready;
	}

	if(smsThreadHandle!=NULL)
	{
		return Library_is_not_ready;
	}

	int subjectSize=wcslen(subject);
	int phoneIDSize=wcslen(recipientID);

	if(subjectSize<1)
	{
		return Message_content_empty;
	}

	if(phoneIDSize<1)
	{
		return Phone_id_empty;
	}


	messageContent=subject;
	phoneID=recipientID;

	int septetsNumber=smsEncoder.numberOfSeptets(subject);

	if(septetsNumber>0)
	{
		smsCoding=C_7BIT;
		int octetsNumber=smsEncoder.countOctetsNumber(septetsNumber);

		if(octetsNumber>max7BitOctedSize)
		{
			smsThreadHandle=CreateThread(NULL,0,sendMultiPartSMSThread,this,0,NULL);
			if(smsThreadHandle==NULL)
			{
				return SMS_send_error;
			}
		}
		else
		{
			smsThreadHandle=CreateThread(NULL,0,sendSMSThread,this,0,NULL);
			if(smsThreadHandle==NULL)
			{
				return SMS_send_error;
			}
		}

	}
	else
	{
	
		smsCoding=C_UNICODE;
		if(subjectSize>maxUnicodeSize)
		{
			smsThreadHandle=CreateThread(NULL,0,sendMultiPartSMSThread,this,0,NULL);
			if(smsThreadHandle==NULL)
			{
				return SMS_send_error;
			}
		}
		else
		{

			smsThreadHandle=CreateThread(NULL,0,sendSMSThread,this,0,NULL);
			if(smsThreadHandle==NULL)
			{
				return SMS_send_error;
			}
		}
	
	}

	return 1;
}