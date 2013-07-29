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
 * This class prepares and verifies AT commands.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "StdAfx.h"
#include "ATCommandManager.h"

const char* AT_CMGF = "CMGF";
const char* AT_CNMI = "CNMI";
const char* AT_CMGS = "CMGS";
const char* AT_CMGL = "CMGL";
const char* AT_CMGD = "CMGD";
const char* AT_CPIN = "CPIN";
const char* AT_CREG = "CREG";
const char* AT_CSCA = "CSCA";

const char* ANS_OK = "OK";
const char* ANS_ERROR ="ERROR";
const char* ANS_ADD_SMS1="> ";
const char* ANS_ADD_SMS2=">";
const char* ANS_CMGS="+CMGS:";
const char* ANS_CMGL="+CMGL:";
const char* ANS_CREG="+CREG:";
const char* ANS_AT = "AT";

/**
 * The class constructor.
 */
ATCommandManager::ATCommandManager(void)
{
}

/**
 * The class destructor.
 */
ATCommandManager::~ATCommandManager(void)
{
}

/**
 * This function prepares command to be sent to modem.
 * @param buffer pointer to the buffer
 * @param bufferSize the buffer size
 * @param command command which will be send to the modem
 * @param commandSize size of prepared command
 * @param commandData additional command data
 * @return error number
 */
int ATCommandManager::prepareBufferSend(char* buffer,int bufferSize,ATCommand command,int& commandSize,int commandData)
{
	switch(command)
	{
	case ATC_AT:
		{
			int index=0;
			index=addAT(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CPIN:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CPIN);
			if(index<0)
			{
				return index;
			}

			string commandText="=\""+pin+"\"";

			index=addCommandText(buffer,index,bufferSize,commandText.c_str());
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;;
		}
	case ATC_CMGF:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CMGF);
			if(index<0)
			{
				return index;
			}
			index=addCommandValue(buffer,index,bufferSize,0);
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CNMI:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CNMI);
			if(index<0)
			{
				return index;
			}
			index=addCommandText(buffer,index,bufferSize,"=1,0,0,0,0");
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CMGS:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CMGS);
			if(index<0)
			{
				return index;
			}
			index=addCommandValue(buffer,index,bufferSize,octetCount);
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}

			commandSize=index;
			break;
		}
	case ATC_CMGL:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CMGL);
			if(index<0)
			{
				return index;
			}
			index=addCommandValue(buffer,index,bufferSize,0);
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CMGD:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CMGD);
			if(index<0)
			{
				return index;
			}
			index=addCommandValue(buffer,index,bufferSize,commandData);
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CREG:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CREG);
			if(index<0)
			{
				return index;
			}
			index=addCommandText(buffer,index,bufferSize,"?");
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_CSCA:
		{
			int index=0;
			index=addCommand(buffer,index,bufferSize,AT_CSCA);
			if(index<0)
			{
				return index;
			}

			string commandText="=\""+ messageCenterID+"\"";

			index=addCommandText(buffer,index,bufferSize,commandText.c_str());
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	case ATC_ATE0:
		{
			int index=0;
			index=addAT(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			index=addCommandText(buffer,index,bufferSize,"E0");
			if(index<0)
			{
				return index;
			}
			index=addEndLine(buffer,index,bufferSize);
			if(index<0)
			{
				return index;
			}
			commandSize=index;
			break;
		}
	};

	return 1;
}

/**
 * Prepares the verifying of the modem answer.
 * @param buffer pointer to the buffer where the answer is stored
 * @param answerSize size of the answer
 * @param readData vector of the answer words
 */
void ATCommandManager::prepareVeryfying(char* buffer,int answerSize,vector<InputWord>& readData)
{
	bool inWord=false;
	int beginOfWord=-1;

	readData.clear();

	for(int i=0;i<answerSize;i++)
	{
		if((buffer[i]>0x1F)&&(buffer[i]!=0x7F))
		{
			if(!inWord)
			{
				beginOfWord=i;
				inWord=true;
			}
		}
		else
		{
			if(inWord)
			{
				InputWord word;
				word.worldBeginInBuffer=beginOfWord;
				word.worldEndInBuffer=i-1;
				readData.push_back(word);
				beginOfWord=-1;
				inWord=false;
			}
		}
	}

	if(inWord)
	{
		InputWord word;
		word.worldBeginInBuffer=beginOfWord;
		word.worldEndInBuffer=answerSize-1;
		readData.push_back(word);
	}
}

/**
 * Verifies the incoming SMS.
 * @param buffer pointer to the buffer where the SMS is stored
 * @param answerSize size of the SMS frame
 * @param readData vector of the SMS frame words
 * @param smsInfo contains information about the SMS frame
 * @return error number
 */
int ATCommandManager::verifyReadSMS(char* buffer,int answerSize,vector<InputWord>& readData,vector<InputSMSinformation>& smsInfo)
{
	
	smsInfo.clear();
	prepareVeryfying(buffer,answerSize,readData);

	int size=readData.size();
	if((size>=2))
	{
		for(unsigned int i =0;i<readData.size()-1;i=i+2)
		{
			int smsIndex=0;
			int smsLength=0;
			int result=verifyCMGL(&buffer[readData[i].worldBeginInBuffer],readData[i].worldSize(),smsIndex,smsLength);
			
			if(result<0)
			{
				break;
			}

			InputSMSinformation input;
			input.length=smsLength;
			input.possition=smsIndex;
			smsInfo.push_back(input);
		}

		if(compareBuffer(&buffer[readData[readData.size()-1].worldBeginInBuffer],readData[readData.size()-1].worldSize(),ANS_OK))
		{
			return 1;
		}
		else
		{
			if(compareBuffer(&buffer[readData[readData.size()-1].worldBeginInBuffer],readData[readData.size()-1].worldSize(),ANS_ERROR))
			{
				return Error_respond_from_Modem;
			}
			else
			{
				return 1;
			}
		}
	}
	else
	{
		if(readData.size()==1)
		{
			if(compareBuffer(&buffer[readData[readData.size()-1].worldBeginInBuffer],readData[readData.size()-1].worldSize(),ANS_OK))
			{
				return 1;
			}
			else
			{
				if(compareBuffer(&buffer[readData[readData.size()-1].worldBeginInBuffer],readData[readData.size()-1].worldSize(),ANS_ERROR))
				{
					return Error_respond_from_Modem;
				}
				else
				{
					return default_error;
				}
			}
		}
		else
		{
		}
	}

	return 1;
}

/**
 * Verifies answer on the CMGL command
 * @param buffer pointer to the buffer
 * @param answerSize size of the answer
 * @param position position of the SMS in modem storage
 * @param size of the SMS
 * @return error number
 */
int ATCommandManager::verifyCMGL(char* buffer,int answerSize,int& position,int& size)
{
	
	int commandSize=strlen(ANS_CMGL);
	bool bResult=compareBuffer(buffer,commandSize,ANS_CMGL);
	
	if(bResult==false)
	{
		return Undefined_modem_answer;
	}

	int firstCommaPosition=0;
	bool found=false;

	for(int i=commandSize;i<answerSize;i++)
	{
		if(buffer[i]==',')
		{
			found =true;
			firstCommaPosition=i;
			break;
		}
	}

	if(found==false)
	{
		return Undefined_modem_answer;
	}

	int valueSize=firstCommaPosition-commandSize;

	int value=getValueFromBuffer(&buffer[commandSize],valueSize);

	if(value<0)
	{
		return value;
	}
	else
	{
		position=value;
	}

	found=false;
	int lastCommaPossition=0;
	for(int i=answerSize-1;i>commandSize;i--)
	{
		if(buffer[i]==',')
		{
			lastCommaPossition=i;
			break;
		}
	}

	valueSize=answerSize-lastCommaPossition-1;

	value=getValueFromBuffer(&buffer[lastCommaPossition+1],valueSize);

	if(value<0)
	{
		return value;
	}
	else
	{
		size=value;
	}

	return 1;
}

/**
 * Gets the value from the buffer 
 * @param buffer pointer to the buffer
 * @param size number of bytes which contains value
 * @return value or error number
 */

int ATCommandManager::getValueFromBuffer(char* buffer,int size)
{

	int value=0;
	bool foundDigit=false;

	for(int i=0;i<size;i++)
	{
		if(buffer[i]>='0' && buffer[i]<='9')
		{
			if(!foundDigit)
			{
				foundDigit=true;
			}

			value=value*10+(buffer[i]-'0');
		}
		else
		{
			if(foundDigit)
			{
				break;
			}
		}
	}

	if(foundDigit==false)
	{
		return Undefined_modem_answer;
	}
	else
	{
		return value;
	}


}

/**
 * Verifies modem answer.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param command command which modem answered
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyModemAnswer(char* buffer,int answerSize,ATCommand command,ATCommandResult& commandResult)
{
	
	prepareVeryfying(buffer,answerSize,inputData);


	switch(command)
	{
	case ATC_AT:
		{
			verifyOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CPIN:
		{
			verifyLastOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CMGF:
		{
			verifyOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CNMI:
		{
			verifyOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CMGS:
		{
			ATCommandManager::verifyCMGS(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CMGS_2:
		{
			ATCommandManager::verifyCMGS2(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CMGD:
		{
			verifyOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CREG:
		{
			verifyCREG(buffer,answerSize,commandResult);
			break;
		}
	case ATC_CSCA:
		{
			verifyOK(buffer,answerSize,commandResult);
			break;
		}
	case ATC_ATE0:
		{
			verifyLastOK(buffer,answerSize,commandResult);
			break;
		}

	};
	return 1;
}


/**
 * Verifies the modem answer on the CREG command.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyCREG(char* buffer,int answerSize,ATCommandResult& commandResult)
{
	if(inputData.size()<2)
	{
		commandResult=ATR_OTHER;
	}
	else
	{
		if(inputData.size()==2)
		{
			int size=inputData[0].worldEndInBuffer+1;
			if(compareBufferBegin(&buffer[inputData[0].worldBeginInBuffer],size,ANS_CREG))
			{
				int commaIndex=-1;

				for(int i=inputData[0].worldBeginInBuffer+strlen(ANS_CREG);i<size;i++)
				{
					if(buffer[i]==',')
					{
						commaIndex=i;
						break;
					}
				}

				if(commaIndex>-1)
				{
					unsigned char answerDigit=buffer[commaIndex+1];
					if(answerDigit>='0'&&answerDigit<='9')
					{
						unsigned int answer=answerDigit-'0';
						if(answer==1||answer==5)
						{
							commandResult=ATR_REGISTERED;
						}
						else
						{
							if(answer==2)
							{
								commandResult=ATR_REGISTERING;
							}
							else
							{
								commandResult=ATR_OTHER;
							}
						}
					}
				}
				else
				{
				
				}
				
				if(!compareBuffer(&buffer[inputData[1].worldBeginInBuffer],inputData[1].worldSize(),ANS_OK))
				{
					if(compareBuffer(&buffer[inputData[1].worldBeginInBuffer],inputData[1].worldSize(),ANS_ERROR))
					{
						commandResult=ATR_ERROR;
					}
					else
					{
						commandResult=ATR_OTHER;
					}
				}
			}
			else
			{
				if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ERROR))
				{
					commandResult=ATR_ERROR;
				}
				else
				{
					commandResult=ATR_OTHER;
				}
			}
		}
		else
		{
			commandResult=ATR_MORE_WORDS;
		}
	
	
	}
	return 1;
}

/**
 * Verifies the first step of the CMGS command.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyCMGS(char* buffer,int answerSize,ATCommandResult& commandResult)
{
	
	if(inputData.size()==0)
	{
		commandResult=ATR_OTHER;
	}
	else
	{
		if(inputData.size()==1)
		{
			if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ADD_SMS1)||compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ADD_SMS2))
			{
				commandResult=ATR_WAIT_FOR_MESSAGE;
			}
			else
			{
				if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ERROR))
				{
					commandResult=ATR_ERROR;
				}
				else
				{
					commandResult=ATR_OTHER;
				}
			}
		}
		else
		{
			commandResult=ATR_MORE_WORDS;
		}
	
	
	}
	return 1;
}

/**
 * Verifies the second step of the CMGS command.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyCMGS2(char* buffer,int answerSize,ATCommandResult& commandResult)
{
	
	if(inputData.size()<2)
	{
		commandResult=ATR_OTHER;
	}
	else
	{
		if(inputData.size()==2)
		{
			if(compareBufferBegin(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_CMGS))
			{
				//commandResult=ATR_WAIT_FOR_MESSAGE;
				if(compareBuffer(&buffer[inputData[1].worldBeginInBuffer],inputData[1].worldSize(),ANS_OK))
				{
					commandResult=ATR_OK;
				}
				else
				{
					if(compareBuffer(&buffer[inputData[1].worldBeginInBuffer],inputData[1].worldSize(),ANS_ERROR))
					{
						commandResult=ATR_ERROR;
					}
					else
					{
						commandResult=ATR_OTHER;
					}
				}
			}
			else
			{
				if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ERROR))
				{
					commandResult=ATR_ERROR;
				}
				else
				{
					commandResult=ATR_OTHER;
				}
			}
		}
		else
		{
			commandResult=ATR_MORE_WORDS;
		}
	
	
	}
	return 1;
}

/**
 * Checks if the answer is OK.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyOK(char* buffer,int answerSize,ATCommandResult& commandResult)
{

	if(inputData.size()==0)
	{
		commandResult=ATR_OTHER;
	}
	else
	{
		if(inputData.size()==1)
		{
			if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_OK))
			{
				commandResult=ATR_OK;
			}
			else
			{
				if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_ERROR))
				{
					commandResult=ATR_ERROR;
				}
				else
				{
					commandResult=ATR_OTHER;
				}
			}
		}
		else
		{
			if(inputData.size()==2)
			{
				if(compareBuffer(&buffer[inputData[0].worldBeginInBuffer],inputData[0].worldSize(),ANS_AT))
				{
					if(compareBuffer(&buffer[inputData[1].worldBeginInBuffer],inputData[1].worldSize(),ANS_OK))
					{
						commandResult=ATR_AT_OK;
						return 1;
					}
				}
			}
			commandResult=ATR_MORE_WORDS;
		}
	
	
	}

	return 1;
}

/**
 * Checks if the last word of the answer is OK.
 * @param buffer pointer to the buffer
 * @param answerSize size of the modem answer
 * @param commandResult result of the command
 * @return 1
 */
int ATCommandManager::verifyLastOK(char* buffer,int answerSize,ATCommandResult& commandResult)
{

	int size = inputData.size();
	if(size==0)
	{
		commandResult=ATR_OTHER;
	}
	else
	{
		
		if(compareBuffer(&buffer[inputData[size-1].worldBeginInBuffer],inputData[size-1].worldSize(),ANS_OK))
		{
			commandResult=ATR_OK;
		}
		else
		{
			if(compareBuffer(&buffer[inputData[size-1].worldBeginInBuffer],inputData[size-1].worldSize(),ANS_ERROR))
			{
				commandResult=ATR_ERROR;
			}
			else
			{
				commandResult=ATR_OTHER;
			}
		}
		
	}

	return 1;
}

/**
 * Adds the "AT" text to the command buffer
 * @param buffer pointer to the buffer
 * @param index current index of the buffer
 * @param bufferSize size of the buffer
 * @return next free position in the buffer
 */
int ATCommandManager::addAT(char* buffer,int index,int bufferSize)
{
	if(index+2>=bufferSize)
	{
		return Not_enought_space_in_buffer;
	}
	
	buffer[index]='A';
	buffer[index+1]='T';
	return index+2;
}

/**
 * Adds the command string to the command buffer
 * @param buffer pointer to the buffer
 * @param index current index of the buffer
 * @param bufferSize size of the buffer
 * @param command command to add
 * @return next free position in the buffer
 */
int ATCommandManager::addCommand(char* buffer,int index,int bufferSize,const char* command)
{
	int commandSize=strlen(command);

	if(index+commandSize+4>=bufferSize)
	{
		return Not_enought_space_in_buffer;
	}

	int currentIndex=addAT(buffer,index,bufferSize);

	if(currentIndex<0)
	{
		return currentIndex;
	}


	buffer[currentIndex]='+';

	currentIndex++;

	for(int i=0;i<commandSize;i++)
	{
		buffer[currentIndex+i]=command[i];
	}

	currentIndex=currentIndex+commandSize;

	return currentIndex;
}

/**
 * Adds the integer value to the command buffer
 * @param buffer pointer to the buffer
 * @param index current index of the buffer
 * @param bufferSize size of the buffer
 * @param value integer value
 * @return next free position in the buffer
 */
int ATCommandManager::addCommandValue(char* buffer,int index,int bufferSize,unsigned int value)
{
	char stringValue[10];

	int result = _itoa_s(value,stringValue,10);
	if(result!=0)
	{
		return String_is_not_a_number;
	}

	int valueSize = strlen(stringValue);

	if(index+valueSize+1>=bufferSize)
	{
		return Not_enought_space_in_buffer;
	}

	int currentIndex=index;

	buffer[currentIndex]='=';
	currentIndex++;

	for(int i=0;i<valueSize;i++)
	{
		buffer[currentIndex+i]=stringValue[i];
	}

	return currentIndex+valueSize;

}

/**
 * Adds the string value to the command buffer
 * @param buffer pointer to the buffer
 * @param index current index of the buffer
 * @param bufferSize size of the buffer
 * @param text string to add
 * @return next free position in the buffer
 */
int ATCommandManager::addCommandText(char* buffer,int index,int bufferSize,const char* text)
{
	int textSize = strlen(text);
	if(index+textSize>=bufferSize)
	{
		return Not_enought_space_in_buffer;
	}

	for(int i=0;i<textSize;i++)
	{
		buffer[index+i]=text[i];
	}

	return index+textSize;
}

/**
 * Adds the end line characters to the buffer
 * @param buffer pointer to the buffer
 * @param index current index of the buffer
 * @param bufferSize size of the buffer
 * @return next free position in the buffer
 */
int ATCommandManager::addEndLine(char* buffer,int index, int bufferSize)
{
	if(index+2>=bufferSize)
	{
		return Not_enought_space_in_buffer;
	}

	buffer[index]=0x0D;
	buffer[index+1]=0x0A;
	return index+2;
}

/**
 * Compares begin of the buffer with the text
 * @param buffer pointer to the buffer
 * @param size size of the buffer
 * @param compareText text to compare with buffer
 * @return true if the begin of the buffer and the text are the same (no case sensible), otherwise return false
 */
bool ATCommandManager::compareBufferBegin(char* buffer,int size,const char* compareText)
{
	int textSize = strlen(compareText);

	if(size<textSize)
	{
		return false;
	}

	for(int i=0;i<textSize;i++)
	{
		if(toupper(buffer[i])!=toupper(compareText[i]))
		{
			return false;
		}
	}

	return true;
}

/**
 * Compares buffer with the text
 * @param buffer pointer to the buffer
 * @param size size of the buffer
 * @param compareText text to compare with buffer
 * @return true if the buffer and the text are the same (no case sensible), otherwise return false
 */
bool ATCommandManager::compareBuffer(char* buffer,int size,const char* compareText)
{
	int textSize = strlen(compareText);

	if(size!=textSize)
	{
		return false;
	}

	for(int i=0;i<textSize;i++)
	{
		if(toupper(buffer[i])!=toupper(compareText[i]))
		{
			return false;
		}
	}

	return true;
}

/**
 * Sets the octets count
 * @param octetCount octet count
 */
void ATCommandManager::setOctetCount(int octetCount)
{
	this->octetCount=octetCount;
}

/**
 * Changes the wide charter text to the 8 bit character text.
 * @param wideText wide text to change
 * @return 8-bit character string
 */
string ATCommandManager::w2c(const wchar_t* wideText)
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

/**
 * Sets the PIN
 * @param pin PIN to set
 */
void ATCommandManager::setPin(wstring pin)
{
	this->pin=w2c(pin.c_str());
}

/**
 * Sets the Message Center ID
 * @param messageCenterID Message Center ID
 */
void ATCommandManager::setMessageCenterID(wstring messageCenterID)
{
	this->messageCenterID=w2c(messageCenterID.c_str());

}