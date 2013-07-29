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

#include <iostream>
#include <sstream>
#include "SubjectIndicator.h"
#include <vector>

using namespace std;

enum SMSCoding
{
	C_7BIT,
	C_8BIT,
	C_UNICODE

};

class SMSEncoder
{
public:
	SMSEncoder(void);
	~SMSEncoder(void);
	int prepareSMSSinglePacket(char* buffer,int index,int bufferSize,const wchar_t* recipientID, const wchar_t* subject,SMSCoding coding,int &octecs,bool addHeaderOctet=true);
	int prepareSMSMultiPartPacket(char* buffer,int index,int bufferSize,const wchar_t* recipientID, const wchar_t* subject,int subjectStartIndex,int subjectSize,
		SMSCoding coding,int &octets,char packetNb,char packetCount,char packetValue,bool addHeaderOctet=true);
	int numberOfSeptets(const wchar_t* text);
	int countOctetsNumber(int septetsNumber);
	int prepare7BitsParts(const wchar_t* subject, vector<SubjectIndicator>& subjectParts);
	int decodeSMS(char* buffer,int bufferSize, int messageSize, wstring& message,wstring& phoneID,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber,bool& moreMessages);
protected:

	static const int septetTableSize=0x80;
	static const int extensionTableSize=10;
	static const int extensionValue=0x1b;
	static const int max7BitsSeptetPartSize=153;
	static const int septetTable[septetTableSize];
	static const int extensionTable[extensionTableSize][2];

	static const unsigned int tp_mms=0x04;
	static const unsigned int tp_udhi=0x40;
	static const unsigned int tp_dcs_begin=0xC0;
	static const unsigned int tp_dcs_7bit=0x00;
	static const unsigned int tp_dcs_8bit=0x04;
	static const unsigned int tp_dcs_16bit=0x08;
	static const unsigned int pt_dcs_reserved=0x0C;

	int prepareSMSPacketHeader(char* buffer,int index,int bufferSize,int& addedOctets,bool addHeaderOctet=true,bool multiPart=false);
	int prepareSMSPacketAddId(char* buffer,int index,int bufferSize,int& addedOctets,const char* recipientID);
	int prepareSMSPackedAddTP(char* buffer,int index,int bufferSize,int& addedOctets,SMSCoding coding);
	
	int AddMessageUnicode(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject);

	int Add7Bits(char* buffer,int index,int bufferSize,unsigned char octet,int& moveBitCount,int& lastAddedValue);
	int AddMessage7Bit(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject);

	int AddMessage7BitsMultiPart(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject,
		int subjectStartIndex,int subjectSize,char packetNb,char packetCount,char packetValue,int paddingBitsCount=0);
	int AddMessageUnicodeMultiPart(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject,
		int subjectStartIndex,int subjectSize,char packetNb,char packetCount,char packetValue);

	int add1ByteValueToBuffer(char* buffer,int index,int bufferSize,int& addedOctets,unsigned char value);

	int septetFromUnicode(int unicode);
	int septetFromExtenstionTable(int unicode);
	int unicodeFromExtensionTable(int septet);

	int numberOfSeptets(const wchar_t* text,int startIndex,int size);
	unsigned int octetToValue(char* octetBegin);
	int decodePhoneID(char* buffer,unsigned int numberOfDigits,wstring& phoneNumber);
	int hexCharToInt(unsigned char hex);

	int decodeHeader(char* buffer,int bufferSize,int index,unsigned int& headerSize,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber);

	int decode7BitMessage(char* buffer,int bufferSize,int contentLengthInSeptets, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber);
	int decode16BitMessage(char* buffer,int bufferSize,int contentLength, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber);
	int decode8BitMessage(char* buffer,int bufferSize,int contentLength, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber);

	string w2c(const wchar_t* wideText);
	wstring c2w(const char* text);
};

