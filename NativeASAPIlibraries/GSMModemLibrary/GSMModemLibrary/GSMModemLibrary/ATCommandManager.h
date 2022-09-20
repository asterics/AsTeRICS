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


#pragma once

#include "InputWord.h"
#include "InputSMSinformation.h"
#include <vector>
#include <iostream>
#include <sstream>

using namespace std;

enum ATCommand
{
	ATC_AT,
	ATC_CPIN,
	ATC_CMGF,
	ATC_CNMI,
	ATC_CMGS,
	ATC_CMGS_2,
	ATC_CMGL,
	ATC_CMGD,
	ATC_CREG,
	ATC_CSCA,
	ATC_ATE0,
};

enum ATCommandResult
{
	ATR_OK,
	ATR_ERROR,
	ATR_OTHER,
	ATR_MORE_WORDS,
	ATR_NONE,
	ATR_WAIT_FOR_MESSAGE,
	ATR_REGISTERED,
	ATR_REGISTERING,
	ATR_AT_OK
};

/*
extern const char* AT_CMGF;
extern const char* AT_CNMI;
extern const char* AT_CMGS;
extern const char* AT_CMGL;
extern const char* AT_CMGD;
extern const char* AT_CPIN

extern const char* ANS_OK;
extern const char* ANS_ERROR;
extern const char* ANS_ADD_SMS1;
extern const char* ANS_ADD_SMS2;
extern const char* ANS_CMGL;
*/

class ATCommandManager
{
public:
	
	ATCommandManager(void);
	~ATCommandManager(void);

	int prepareBufferSend(char* buffer,int bufferSize,ATCommand command,int& commandSize,int commandData=0);
	int verifyModemAnswer(char* buffer,int answerSize,ATCommand command,ATCommandResult& commandResult); 
	int verifyReadSMS(char* buffer,int answerSize,vector<InputWord>& readData,vector<InputSMSinformation>& smsInfo); 
	void setOctetCount(int octetCount);
	void setPin(wstring pin);
	void setMessageCenterID(wstring messageCenterID);
private:
	int octetCount;
	vector<InputWord> inputData;
	string pin;
	string messageCenterID;

	int addAT(char* buffer,int index,int bufferSize);
	int addCommand(char* buffer,int index,int bufferSize,const char* command);
	int addCommandValue(char* buffer,int index,int bufferSize,unsigned int value);
	int addCommandText(char* buffer,int index,int bufferSize,const char* text);
	int addEndLine(char* buffer,int index, int bufferSize);
	void prepareVeryfying(char* buffer,int answerSize,vector<InputWord>& readData);

	int verifyOK(char* buffer,int answerSize,ATCommandResult& commandResult);
	int verifyLastOK(char* buffer,int answerSize,ATCommandResult& commandResult);
	int verifyCMGS(char* buffer,int answerSize,ATCommandResult& commandResult);
	int verifyCMGS2(char* buffer,int answerSize,ATCommandResult& commandResult);
	int verifyCMGL(char* buffer,int answerSize,int& position,int& size);
	int verifyCREG(char* buffer,int answerSize,ATCommandResult& commandResult);
	bool compareBuffer(char* buffer,int size,const char* compareText);
	bool compareBufferBegin(char* buffer,int size,const char* compareText);
	int getValueFromBuffer(char* buffer,int size);

	string w2c(const wchar_t* wideText);

};

