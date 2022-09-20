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
 * This class decodes and prepares messages.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "StdAfx.h"
#include "SMSEncoder.h"

const int SMSEncoder::extensionTable[extensionTableSize][2]={
//{7bit,unicode}
{10,0x0C},
{20,0x5E},
{40,0x7B},
{41,0x7D},
{47,0x5C},
{60,0x5B},
{61,0x7E},
{62,0x5D},
{64,0x7C},
{101,0x20AC}
};

const int SMSEncoder::septetTable[septetTableSize]={
 0x40,
 0xA3,
 0x24,
 0xA5,
 0xE8,
 0xE9,
 0xF9,
 0xEC,
 0xF2,
 0xC7,
 0x0A,
 0xD8,
 0xF8,
 0x0D,
 0xC5,
 0xE5,
 0x394,
 0x5F,
 0x3A6,
 0x393,
 0x39B,
 0x3A9,
 0x3A0,
 0x3A8,
 0x3A3,
 0x398,
 0x39E,
 0x00,  //Extension
 0xC6,
 0xE6,
 0xDF,
 0xC9,
 0x20,
 0x21,
 0x22,
 0x23,
 0xA4,
 0x25,
 0x26,
 0x27,
 0x28,
 0x29,
 0x2A,
 0x2B,
 0x2C,
 0x2D,
 0x2E,
 0x2F,
 0x30,
 0x31,
 0x32,
 0x33,
 0x34,
 0x35,
 0x36,
 0x37,
 0x38,
 0x39,
 0x3A,
 0x3B,
 0x3C,
 0x3D,
 0x3E,
 0x3F,
 0xA1,
 0x41,
 0x42,
 0x43,
 0x44,
 0x45,
 0x46,
 0x47,
 0x48,
 0x49,
 0x4A,
 0x4B,
 0x4C,
 0x4D,
 0x4E,
 0x4F,
 0x50,
 0x51,
 0x52,
 0x53,
 0x54,
 0x55,
 0x56,
 0x57,
 0x58,
 0x59,
 0x5A,
 0xC4,
 0xD6,
 0xD1,
 0xDC,
 0xA7,
 0xBF,
 0x61,
 0x62,
 0x63,
 0x64,
 0x65,
 0x66,
 0x67,
 0x68,
 0x69,
 0x6A,
 0x6B,
 0x6C,
 0x6D,
 0x6E,
 0x6F,
 0x70,
 0x71,
 0x72,
 0x73,
 0x74,
 0x75,
 0x76,
 0x77,
 0x78,
 0x79,
 0x7A,
 0xE4,
 0xF6,
 0xF1,
 0xFC,
 0xE0
};

/**
 * The class constructor.
 */
SMSEncoder::SMSEncoder(void)
{
}

/**
 * The class destructor.
 */
SMSEncoder::~SMSEncoder(void)
{
}

/**
 * This function returns septet code of the given wide character from Extension table.
 * @param unicode wide character
 * @return septet code or -1 if the character is not in the extension table
 */
int SMSEncoder::septetFromExtenstionTable(int unicode)
{
	for(int i=0;i<extensionTableSize;i++)
	{
		if(extensionTable[i][1]==unicode)
		{
			return extensionTable[i][0];
		}
	}

	return -1;
}

/**
 * This function returns wide character of the given septet code from Extension table.
 * @param septet septet code
 * @return unicode or -1 if the character is not in the extension table
 */
int SMSEncoder::unicodeFromExtensionTable(int septet)
{
	for(int i=0;i<extensionTableSize;i++)
	{
		if(extensionTable[i][0]==septet)
		{
			return extensionTable[i][1];
		}
	}

	return -1;
}

/**
 * This function returns septet code of the given wide character from septet table.
 * @param unicode wide character
 * @return unicode or -1 if the character is not in the septet table
 */
int SMSEncoder::septetFromUnicode(int unicode)
{
	for(int i =0;i< septetTableSize;i++)
	{
		if((septetTable[i]==unicode)&&(i!=extensionValue))
		{
			return i;
		}

	}

	return -1;
}

/**
 * Returns number of septets from part of the text
 * @param text text which will be used to count septets
 * @param startIndex begin of the part of the text
 * @param size size of part of the text
 * @return number of septets or -1 if there is an error or part of the text contains character not transferable to the septet
 */
int SMSEncoder::numberOfSeptets(const wchar_t* text,int startIndex,int size)
{
	int septets=0;
	int textSize=wcslen(text);
	if(startIndex+size>textSize)
	{
		return -1;
	}

	for(int i=startIndex;i<startIndex+size;i++)
	{
		if(septetFromUnicode(text[i])<0)
		{
			if(septetFromExtenstionTable(text[i])<0)
			{
				return -1;
				break;
			}
			else
			{
				septets=septets+2;
			}
		}
		else
		{
			septets++;
		}
	}

	return septets;
}

/**
 * Returns number of septets from the text
 * @param text text which will be used to count septets
 * @return number of septets or -1 if the text contains character not transferable to the septet
 */
int SMSEncoder::numberOfSeptets(const wchar_t* text)
{
	int septets=0;
	int textSize=wcslen(text);
	for(int i=0;i<textSize;i++)
	{
		if(septetFromUnicode(text[i])<0)
		{
			if(septetFromExtenstionTable(text[i])<0)
			{
				return -1;
				break;
			}
			else
			{
				septets=septets+2;
			}
		}
		else
		{
			septets++;
		}
	}

	return septets;
}

/**
 * Gives octet number from the septet number
 * @param septetsNumber septet number
 * @return octet number
 */
int SMSEncoder::countOctetsNumber(int septetsNumber)
{
	int bitsNumber= septetsNumber*7;
	int octetsNumber=bitsNumber/8;
	if(bitsNumber%8!=0)
	{
		octetsNumber++;
	}

	return octetsNumber;
}

/**
 * Prepares SMS Header
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets returns number of octets added
 * @param addHeaderOctet defines if the header octet should be added
 * @param multiPart definies if the message is multi-part.
 * @return current position in the buffer or the error number
 */
int SMSEncoder::prepareSMSPacketHeader(char* buffer,int index,int bufferSize,int& addedOctets,bool addHeaderOctet,bool multiPart)
{
	int currentIndex=index;
	int octets=0;

	if(addHeaderOctet)
	{
		if(bufferSize<3)
		{
			return Not_enought_space_in_buffer;
		}

		buffer[currentIndex]='0';
		buffer[currentIndex+1]='0';
		currentIndex=currentIndex+2;
		octets=octets+1;
	}

	if(bufferSize<2)
	{
		return Not_enought_space_in_buffer;
	}

	if(multiPart)
	{
		buffer[currentIndex]='5';
		buffer[currentIndex+1]='1';
	}
	else
	{
		buffer[currentIndex]='1';
		buffer[currentIndex+1]='1';
	}

	buffer[currentIndex+2]='0';
	buffer[currentIndex+3]='0';
	

	currentIndex=currentIndex+4;
	octets=octets+2;

	addedOctets=octets;
	return currentIndex;
}

/**
 * Prepares single SMS packet.
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param recipientID recipient phone ID
 * @param subject content of the message
 * @param coding defines message coding
 * @param octets returns number of octets added
 * @param addHeaderOctet defines if the header octet should be added
 * @return current position in the buffer or the error number
 */
int SMSEncoder::prepareSMSSinglePacket(char* buffer,int index,int bufferSize,const wchar_t* recipientID, const wchar_t* subject,SMSCoding coding,int &octets,bool addHeaderOctet)
{
	int currentIndex=index;
	octets=0;

	currentIndex=prepareSMSPacketHeader(buffer,currentIndex,bufferSize,octets);

	if(currentIndex<0)
	{
		return currentIndex;
	}

	int idSize=wcslen(recipientID);
	string recipientIDnarrow=w2c(recipientID);

	currentIndex=prepareSMSPacketAddId(buffer,currentIndex,bufferSize,octets,recipientIDnarrow.c_str());
	if(currentIndex<0)
	{
		return currentIndex;
	}

	currentIndex=prepareSMSPackedAddTP(buffer,currentIndex,bufferSize,octets,coding);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	if(coding==C_UNICODE)
	{
		currentIndex=AddMessageUnicode(buffer,currentIndex,bufferSize,octets,subject);
	}
	else
	{
		currentIndex=AddMessage7Bit(buffer,currentIndex,bufferSize,octets,subject);
	}
	
	if(currentIndex<0)
	{
		return currentIndex;
	}
	return currentIndex;

}

/**
 * Prepares packet of multi-part SMS
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param recipientID recipient phone ID
 * @param subject content of the message
 * @param subjectStartIndex begin of the part of the message
 * @param subjectSize size of the part of the message
 * @param coding defines message coding
 * @param octets returns number of octets added
 * @param packetNB number of the current part of the message
 * @param packetCount number of the parts.
 * @param packetValue multi-part message value
 * @param addHeaderOctet defines if the header octet should be added
 * @return current position in the buffer or the error number
 */
int SMSEncoder::prepareSMSMultiPartPacket(char* buffer,int index,int bufferSize,const wchar_t* recipientID, const wchar_t* subject,int subjectStartIndex,int subjectSize,
		SMSCoding coding,int &octets,char packetNb,char packetCount,char packetValue,bool addHeaderOctet)
{
	int currentIndex=index;
	octets=0;

	currentIndex=prepareSMSPacketHeader(buffer,currentIndex,bufferSize,octets,true,true);
	
	if(currentIndex<0)
	{
		return currentIndex;
	}

	int idSize=wcslen(recipientID);
	string recipientIDnarrow=w2c(recipientID);

	currentIndex=prepareSMSPacketAddId(buffer,currentIndex,bufferSize,octets,recipientIDnarrow.c_str());
	if(currentIndex<0)
	{
		return currentIndex;
	}

	currentIndex=prepareSMSPackedAddTP(buffer,currentIndex,bufferSize,octets,coding);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	if(coding==C_UNICODE)
	{
		currentIndex=AddMessageUnicodeMultiPart(buffer,currentIndex,bufferSize,octets,subject,subjectStartIndex,subjectSize,packetNb,packetCount,packetValue);
	}
	else
	{
		currentIndex=AddMessage7BitsMultiPart(buffer,currentIndex,bufferSize,octets,subject,subjectStartIndex,subjectSize,packetNb,packetCount,packetValue,1);
	}

	return currentIndex;
}

/**
 * Adds the recipient phone ID to the SMS packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets returns number of octets added
 * @param recipientID recipient phone ID
 * @return current position in the buffer or the error number
 */
int SMSEncoder::prepareSMSPacketAddId(char* buffer,int index,int bufferSize,int& addedOctets,const char * recipientID)
{
	int IDSize = strlen(recipientID);
	int digitCount=0;
	int currentIndex=index;
	

	for(int i=0;i<IDSize;i++)
	{
		if(iswdigit(recipientID[i]))
		{
			digitCount++;
		}
	}

	bool addComplement;

	if(digitCount%2==1)
	{
		if(currentIndex+((digitCount+1)/2)+2>=bufferSize)
		{
			return -1;
		}
		addComplement=true;
	}
	else
	{
		if(currentIndex+(digitCount/2)+2>=bufferSize)
		{
			return -1;
		}
		addComplement=false;
	}

	char hexValue[5];

	int result = _itoa_s(digitCount,hexValue,16);

	if(result!=0)
	{
		return -1;
	}

	int idSize=strlen(hexValue);



	if(idSize==1)
	{
		buffer[currentIndex]='0';
		buffer[currentIndex+1]=toupper(hexValue[0]);
		currentIndex=currentIndex+2;
		addedOctets++;
	}
	else
	{
		if(idSize==2)
		{
			buffer[currentIndex]=toupper(hexValue[0]);
			buffer[currentIndex+1]=toupper(hexValue[1]);
			currentIndex=currentIndex+2;
			addedOctets++;
		}
		else
		{
			return -1;
		}
	}
	


	buffer[currentIndex]='9';
	buffer[currentIndex+1]='1';
	currentIndex=currentIndex+2;
	addedOctets++;

	bool first=true;

	for(int i=0;i<IDSize;i++)
	{
		if(iswdigit(recipientID[i]))
		{
			if(first)
			{
				buffer[currentIndex+1]=recipientID[i];
				first=false;
			}
			else
			{
				buffer[currentIndex]=recipientID[i];
				currentIndex=currentIndex+2;
				addedOctets++;
				first=true;
			}
		}
	}

	if(addComplement)
	{
		buffer[currentIndex]='F';
		currentIndex=currentIndex+2;
		addedOctets++;
	}

	
	return currentIndex;



}

/**
 * Adds the TP section to the SMS packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets returns number of octets added
 * @param coding coding of the packet
 * @return current position in the buffer or the error number
 */
int SMSEncoder::prepareSMSPackedAddTP(char* buffer,int index,int bufferSize,int& addedOctets,SMSCoding coding)
{
	int currentIndex=index;
	if(currentIndex+6>=bufferSize)
	{
		return -1;
	}

	buffer[currentIndex]='0';
	currentIndex++;
	buffer[currentIndex]='0';
	currentIndex++;

	switch(coding)
	{
	case C_7BIT:
		buffer[currentIndex]='0';
		currentIndex++;
		buffer[currentIndex]='0';
		currentIndex++;
		break;
	case C_UNICODE:
		buffer[currentIndex]='0';
		currentIndex++;
		buffer[currentIndex]='8';
		currentIndex++;
		break;
	};

	buffer[currentIndex]='A';
	currentIndex++;
	buffer[currentIndex]='A';
	currentIndex++;

	addedOctets=addedOctets+3;
	return currentIndex;
}

/**
 * Adds the septet value to the SMS packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param sepetet septet value to add to the packet
 * @param moveBitCount number of bits to shift
 * @param lastAddedValue returns added value with the bit shift
 * @return current position in the buffer or the error number
 */
int SMSEncoder::Add7Bits(char* buffer,int index,int bufferSize,unsigned char septet,int& moveBitCount,int& lastAddedValue)
{
	int currentIndex=index;
	int octets=0;
	if(moveBitCount>0)
	{
		unsigned char septetValue=(unsigned char)septet;
		septetValue=septetValue>>moveBitCount;
		unsigned char previousOctet=(unsigned char)lastAddedValue;
		unsigned char restOfBits=septet<<(8-moveBitCount);
		previousOctet=previousOctet|restOfBits;
		
		currentIndex=add1ByteValueToBuffer(buffer,currentIndex-2,bufferSize,octets,previousOctet);
		if(currentIndex<0)
		{
			return currentIndex;
		}
		currentIndex=add1ByteValueToBuffer(buffer,currentIndex,bufferSize,octets,septetValue);
		if(currentIndex<0)
		{
			return currentIndex;
		}

		lastAddedValue=septetValue;

		moveBitCount++;
		if(moveBitCount==8)
		{
			moveBitCount=0;
			currentIndex=currentIndex-2;
		}
		else
		{
			//currentIndex++;
		}
	}
	else
	{		
		lastAddedValue=septet;
		currentIndex=add1ByteValueToBuffer(buffer,currentIndex,bufferSize,octets,septet);
		moveBitCount++;
	}

	return currentIndex;
}

/**
 * Adds the 7 bit message
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets number of octets added
 * @param subject message to add
 * @return current position in the buffer or the error number
 */
int SMSEncoder::AddMessage7Bit(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject)
{
	int messageSize=wcslen(subject);
	int currentIndex=index;

	int septetsNumber=numberOfSeptets(subject);

	if(septetsNumber<0)
	{
		return -1;
	}

	int octetsNumber=countOctetsNumber(septetsNumber);
	
	if(currentIndex+octetsNumber+2>=bufferSize)
	{
		return -1;
	}

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, addedOctets,septetsNumber);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	int bitsToMove=0;
	int lastAddedValue=0;
	int startPosition=0;
	//unsigned char restOfBits=0;
	

	for(int i=startPosition;i<messageSize;i++)
	{
		int septet=septetFromUnicode(subject[i]);

		if(septet<0)
		{
			int extensionSeptet = septetFromExtenstionTable(subject[i]);
			if(extensionSeptet>0)
			{
				currentIndex=Add7Bits(buffer,currentIndex, bufferSize,extensionValue,bitsToMove,lastAddedValue);
				if(currentIndex<0)
				{	
					return currentIndex;
				}
				currentIndex=Add7Bits(buffer,currentIndex, bufferSize,extensionSeptet,bitsToMove,lastAddedValue);
				if(currentIndex<0)
				{
					return currentIndex;
				}
			}
			else
			{
				return -1;
			}
		}
		else
		{
			currentIndex=Add7Bits(buffer,currentIndex, bufferSize,septet,bitsToMove,lastAddedValue);
			if(currentIndex<0)
			{
				return currentIndex;
			}

		}
	}

	addedOctets=addedOctets+octetsNumber;
	
	if((octetsNumber+1)!=(currentIndex-index)/2)
	{
		return -1;
	}
	

	return currentIndex;
}

/**
 * Adds the unicode message
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets number of octets added
 * @param subject message to add
 * @return current position in the buffer or the error number
 */
int SMSEncoder::AddMessageUnicode(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject)
{
	int messageSize=wcslen(subject);
	int currentIndex=index;

	if(currentIndex+messageSize+2>=bufferSize)
	{
		return -1;
	}

	
	char hexValue[6];

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, addedOctets,messageSize*2);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	for(int i=0;i<messageSize;i++)
	{
		int result = _itoa_s(subject[i],hexValue,16);

		if(result!=0)
		{
			return -1;
		}
		
		int hexSize=strlen(hexValue);

		buffer[currentIndex]='0';
		buffer[currentIndex+1]='0';
		buffer[currentIndex+2]='0';
		buffer[currentIndex+3]='0';

		for(int j=0;j<hexSize;j++)
		{
			buffer[currentIndex+4-hexSize+j]=toupper(hexValue[j]);
		}

		
		currentIndex=currentIndex+4;
		addedOctets=addedOctets+2;
	}

	return currentIndex;

}

/**
 * Adds the 7 bit message to the multi-part packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets number of octets added
 * @param subject message to add
 * @param subjectStartIndex begin of the part of the message to add.
 * @param subjectSize size of the part of the message
 * @param packetNb number of current packet
 * @param packetCount number of packets
 * @param packetValue multi-part message value
 * @param paddingBitsCount number of paddings bits
 * @return current position in the buffer or the error number
 */

int SMSEncoder::AddMessage7BitsMultiPart(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject,int subjectStartIndex,
	int subjectSize,char packetNb,char packetCount,char packetValue,int paddingBitsCount)
{
	int messageSize=wcslen(subject);
	if(messageSize<subjectSize+subjectStartIndex)
	{
		return -1;
	}

	int currentIndex=index;

	int septetsNumber=numberOfSeptets(subject,subjectStartIndex,subjectSize);

	if(septetsNumber<0)
	{
		return -1;
	}

	int octetsNumber;

	if(paddingBitsCount>0)
	{
		int bitsNumber= 1*8+ 6*8+septetsNumber*7+paddingBitsCount;   //header: 6 bytes = 6*8
		octetsNumber=bitsNumber/8; 
		if(bitsNumber%8!=0)
		{
			octetsNumber++;
		}
	}
	else
	{
		octetsNumber=countOctetsNumber(septetsNumber);
	}



	if(currentIndex+octetsNumber+2>=bufferSize)
	{
		return -1;
	}

	char hexValue[6];

	int result = _itoa_s(septetsNumber+7,hexValue,16); //septets here header + padding bit has 7 septets!

	if(result!=0)
	{
		return -1;
	}

	int messageHexSize=strlen(hexValue);

	int octetToAdd=0;

	if(messageHexSize==1)
	{
		buffer[currentIndex]='0';
		buffer[currentIndex+1]=toupper(hexValue[0]);
		currentIndex=currentIndex+2;
		octetToAdd++;
	}
	else
	{
		if(messageHexSize==2)
		{
			buffer[currentIndex]=toupper(hexValue[0]);
			buffer[currentIndex+1]=toupper(hexValue[1]);
			currentIndex=currentIndex+2;
			octetToAdd++;
		}
		else
		{
			return -1;
		}
	}

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='5';
	currentIndex=currentIndex+2;
	octetToAdd++;

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='0';
	currentIndex=currentIndex+2;
	octetToAdd++;

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='3';
	currentIndex=currentIndex+2;
	octetToAdd++;

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, octetToAdd,packetValue);
	if(currentIndex<0)
	{
		return currentIndex;
	}


	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, octetToAdd,packetCount);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, octetToAdd,packetNb);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	int bitsToMove=0;
	int lastAddedValue=0;
	int startPosition=subjectStartIndex;
	//unsigned char restOfBits=0;
	
	

	if(paddingBitsCount!=0)
	{
		if((paddingBitsCount>0)&&(paddingBitsCount<7))
		{
			currentIndex=currentIndex+2;
			bitsToMove=8-paddingBitsCount;
		}
	}

	for(int i=startPosition;i<startPosition+subjectSize;i++)
	{
		int septet=septetFromUnicode(subject[i]);

		if(septet<0)
		{
			int extensionSeptet = septetFromExtenstionTable(subject[i]);
			if(extensionSeptet>0)
			{
				currentIndex=Add7Bits(buffer,currentIndex, bufferSize,extensionValue,bitsToMove,lastAddedValue);
				if(currentIndex<0)
				{	
					return currentIndex;
				}
				currentIndex=Add7Bits(buffer,currentIndex, bufferSize,extensionSeptet,bitsToMove,lastAddedValue);
				if(currentIndex<0)
				{
					return currentIndex;
				}
			}
			else
			{
				return -1;
			}
		}
		else
		{
			currentIndex=Add7Bits(buffer,currentIndex, bufferSize,septet,bitsToMove,lastAddedValue);
			if(currentIndex<0)
			{
				return currentIndex;
			}

		}
	}

	addedOctets=addedOctets+octetsNumber;
	
	if((octetsNumber)!=(currentIndex-index)/2)
	{
		return -1;
	}
	

	return currentIndex;
}


/**
 * Adds the unicode message to the multi-part packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets number of octets added
 * @param subject message to add
 * @param subjectStartIndex begin of the part of the message to add.
 * @param subjectSize size of the part of the message
 * @param packetNb number of current packet
 * @param packetCount number of packets
 * @param packetValue multi-part message value
 * @return current position in the buffer or the error number
 */
int SMSEncoder::AddMessageUnicodeMultiPart(char* buffer,int index,int bufferSize,int& addedOctets,const wchar_t* subject,int subjectStartIndex,
	int subjectSize,char packetNb,char packetCount,char packetValue)
{
	int messageSize=wcslen(subject);
	if(messageSize<subjectSize+subjectStartIndex)
	{
		return -1;
	}

	int currentIndex=index;

	if(currentIndex+subjectSize+7>=bufferSize)
	{
		return -1;
	}

	char hexValue[6];

	int result = _itoa_s(subjectSize*2+6,hexValue,16);

	if(result!=0)
	{
		return -1;
	}

	int messageHexSize=strlen(hexValue);


	if(messageHexSize==1)
	{
		buffer[currentIndex]='0';
		buffer[currentIndex+1]=toupper(hexValue[0]);
		currentIndex=currentIndex+2;
		addedOctets++;
	}
	else
	{
		if(messageHexSize==2)
		{
			buffer[currentIndex]=toupper(hexValue[0]);
			buffer[currentIndex+1]=toupper(hexValue[1]);
			currentIndex=currentIndex+2;
			addedOctets++;
		}
		else
		{
			return -1;
		}
	}

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='5';
	currentIndex=currentIndex+2;
	addedOctets++;

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='0';
	currentIndex=currentIndex+2;
	addedOctets++;

	buffer[currentIndex]='0';
	buffer[currentIndex+1]='3';
	currentIndex=currentIndex+2;
	addedOctets++;

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, addedOctets,packetValue);
	if(currentIndex<0)
	{
		return currentIndex;
	}


	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, addedOctets,packetCount);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	currentIndex=add1ByteValueToBuffer(buffer, currentIndex, bufferSize, addedOctets,packetNb);
	if(currentIndex<0)
	{
		return currentIndex;
	}

	for(int i=subjectStartIndex;i<subjectStartIndex+subjectSize;i++)
	{
		result = _itoa_s(subject[i],hexValue,16);

		if(result!=0)
		{
			return -1;
		}
		
		int hexSize=strlen(hexValue);

		buffer[currentIndex]='0';
		buffer[currentIndex+1]='0';
		buffer[currentIndex+2]='0';
		buffer[currentIndex+3]='0';

		for(int j=0;j<hexSize;j++)
		{
			buffer[currentIndex+4-hexSize+j]=toupper(hexValue[j]);
		}

		
		currentIndex=currentIndex+4;
		addedOctets=addedOctets+2;
	}

	return currentIndex;
}

/**
 * Adds the one byte value to the SMS packet
 * @param buffer pointer to the buffer
 * @param index current position in the buffer
 * @param bufferSize size of the buffer
 * @param addedOctets number of octets added
 * @param value value to add
 * @return current position in the buffer or the error number
 */
int SMSEncoder::add1ByteValueToBuffer(char* buffer,int index,int bufferSize,int& addedOctets,unsigned char value)
{
	int currentIndex=index;
	if(currentIndex+2>=bufferSize)
	{
		return -1;
	}

	char hexValue[6];
	unsigned long l = (unsigned long)value;
	int result = _ultoa_s(l,hexValue,6,16);

	if(result!=0)
	{
		return -1;
	}

	int messageHexSize=strlen(hexValue);


	if(messageHexSize==1)
	{
		buffer[currentIndex]='0';
		buffer[currentIndex+1]=toupper(hexValue[0]);
		currentIndex=currentIndex+2;
		addedOctets++;
	}
	else
	{
		if(messageHexSize==2)
		{
			buffer[currentIndex]=toupper(hexValue[0]);
			buffer[currentIndex+1]=toupper(hexValue[1]);
			currentIndex=currentIndex+2;
			addedOctets++;
		}
		else
		{
			return -1;
		}
	}

	return currentIndex;
}

/**
 * Changes the wide charter text to the 8 bit character text.
 * @param wideText wide text to change
 * @return 8-bit character string
 */
string SMSEncoder::w2c(const wchar_t* wideText)
{
	ostringstream stm;
	wstring wideString=wideText;
	const ctype<char>& ctfacet = std::use_facet< ctype<char> >( stm.getloc() );
	
	for(size_t i=0; i<wideString.size(); i++)
	{
		stm << ctfacet.narrow( wideString[i], 0 );
	}
	
	string str=stm.str();

	return str;
}

/**
 * Changes the 8-bit character text to the unicode text.
 * @param wideText 8 bit text to change
 * @return unicode string
 */
wstring SMSEncoder::c2w(const char* text)
{
	wostringstream wstm;
	string narrowString = text;
    const ctype<wchar_t>& ctfacet =use_facet< ctype<wchar_t> >( wstm.getloc() );
 
	for(size_t i=0; i<narrowString.size(); i++)
	{
		wstm << ctfacet.widen( narrowString[i] );
	}
 
 
	wstring wstr = wstm.str();

	return wstr;
}

/**
 * Prepares information about SMS content for multi-part message
 * @param subject subject of the message
 * @param subjectParts information about message
 * @return error code
 */
int SMSEncoder::prepare7BitsParts(const wchar_t* subject, vector<SubjectIndicator>& subjectParts)
{
	int subjectSize=wcslen(subject);

	int septetCount=0;
	int startIndex=0;

	for(int i=0;i<subjectSize;i++)
	{
		int septet=septetFromUnicode(subject[i]);
		int numberOfSeptets=0;

		if(septet<0)
		{
			septet=septetFromExtenstionTable(subject[i]);

			if(septet<0)
			{
				return -1;
			}
			else
			{
				numberOfSeptets=2;
			}
		}
		else
		{
			numberOfSeptets=1;
		}


		if(septetCount+numberOfSeptets>max7BitsSeptetPartSize)
		{
			SubjectIndicator subjectIndicator;
			subjectIndicator.partBegin=startIndex;
			subjectIndicator.partSize=i-startIndex;
			startIndex=i;
			septetCount=numberOfSeptets;
			subjectParts.push_back(subjectIndicator);
		}
		else
		{
			septetCount=septetCount+numberOfSeptets;
		}
	}

	SubjectIndicator subjectIndicator;
	subjectIndicator.partBegin=startIndex;
	subjectIndicator.partSize=subjectSize-startIndex;
	subjectParts.push_back(subjectIndicator);
	
	return 1;

}

/**
 * Decodes incoming SMS
 * @param buffer pointer to the buffer
 * @param bufferSize size of the buffer
 * @param messageSize size of the message
 * @param message decoded message content
 * @param phoneID decoded message phone id
 * @param multiPartMessage if is set to true the message is multi-part
 * @param messageReference message reference for multi-part messages
 * @param numberOfParts number of parts
 * @param partNumber number of current part
 * @param moreMessages if is set to true, there will be more parts
 * @return error code
 */
int SMSEncoder::decodeSMS(char* buffer,int bufferSize, int messageSize, wstring& message,wstring& phoneID,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber,bool& moreMessages)
{
	unsigned int headerSize=octetToValue(&buffer[0]);

	if(bufferSize!=2*(1+headerSize+messageSize))
	{
		return -1;
	}
	
	int index=(headerSize+1)*2;

	unsigned int firstOctet=octetToValue(&buffer[index]);

	bool hasHeader=false;
	if(firstOctet&tp_udhi)
	{
		hasHeader=true;
	}
	
	if(firstOctet&tp_mms)
	{
		moreMessages=false;
	}
	else
	{
		moreMessages=true;
	}

	index=index+2;
	unsigned int SenderIDLength=octetToValue(&buffer[index]);
	index=index+2;
	unsigned int SenderIDType=octetToValue(&buffer[index]);
	index=index+2;

	wstring phoneNumber;

	int result=decodePhoneID(&buffer[index],SenderIDLength,phoneNumber);

	phoneID=phoneNumber;

	if(result<0)
	{
		return -1;
	}

	if((SenderIDLength % 2)>0)
	{
		index=index+SenderIDLength+1;
	}
	else
	{
		index=index+SenderIDLength;
	}



	unsigned int ProtocolIdentifier=octetToValue(&buffer[index]);
	index=index+2;

	unsigned int dataCoding=octetToValue(&buffer[index]);
	index=index+2;

	SMSCoding coding;

	if(dataCoding & tp_dcs_begin)
	{
		return -1;
	}
	else
	{
		if((dataCoding & pt_dcs_reserved)==pt_dcs_reserved)
		{
			return -1;
		}
		else
		{
			if(dataCoding & tp_dcs_8bit)
			{
				coding=C_8BIT;
			}
			else
			{
				if(dataCoding & tp_dcs_16bit)
				{
					coding=C_UNICODE;
				}
				else
				{
					coding=C_7BIT;
				}
			}
		}
	}

	index=index+2*7;

	unsigned int contentLength=octetToValue(&buffer[index]);
	index=index+2;

	int contentSize=bufferSize-index;


	switch(coding)
	{
	case C_7BIT:
		result=decode7BitMessage(&buffer[index], contentSize,contentLength,message,hasHeader,multiPartMessage,messageReference, numberOfParts,partNumber);
		if(result<0)
		{
			return -1;
		}
		break;
	case C_8BIT:
		result=decode8BitMessage(&buffer[index], contentSize,contentLength,message,hasHeader,multiPartMessage,messageReference, numberOfParts,partNumber);
		if(result<0)
		{
			return -1;
		}
		break;
	case C_UNICODE:
		result=decode16BitMessage(&buffer[index], contentSize,contentLength,message,hasHeader,multiPartMessage,messageReference, numberOfParts,partNumber);
		if(result<0)
		{
			return -1;
		}
		break;
	};

	return 1;

}

/**
 * Returns integer value form the octet
 * @param octetBegin pointer to the octet
 * @return value from the octet or error code
 */
unsigned int SMSEncoder::octetToValue(char* octetBegin)
{
	unsigned int high = hexCharToInt(octetBegin[0]);
	if(high<0)
	{
		return -1;
	}
	unsigned int low = hexCharToInt(octetBegin[1]);
	if(low<0)
	{
		return -1;
	}

	return (high<<4) + low;
}

/**
 * Returns integer value from the hex character.
 * @param hex character
 * @return value from the hex character
 */
int SMSEncoder::hexCharToInt(unsigned char hex)
{
	if(hex>='0'&&hex<='9')
	{
		return hex-'0';
	}
	else
	{
		if(hex>='A'&& hex<='F')
		{
			return 10+ hex-'A';
		}
		else
		{
			if(hex>='a'&& hex<='f')
			{
				return 10+ hex-'a';
			}
			else
			{
				return -1;
			}
		}
	}
}


/**
 * Decodes phone ID
 * @param buffer pointer to the buffer
 * @param numberOfDigits number of digits in phone ID
 * @param phoneNumber decoded phone ID
 * @return error code
 */
int SMSEncoder::decodePhoneID(char* buffer,unsigned int numberOfDigits,wstring& phoneNumber)
{
	bool fAdded=false;

	string phoneID;

	bool odd=false;
	for(int i=0;i<numberOfDigits;i++)
	{
		int j=0;
		if(odd)
		{
			j=i-1;
			odd=false;
		}
		else
		{
			j=i+1;
			odd=true;
		}

		if((buffer[j]<'0')||(buffer[j]>'9'))
		{
			return -1;  //remove this
		}
		else
		{
			phoneID=phoneID+buffer[j];
		}
	}



	phoneNumber.clear();
	phoneNumber=c2w(phoneID.c_str());

	return 1;


}

/**
 * Decodes header of the SMS
 * @param buffer pointer to the buffer
 * @param bufferSize size of the buffer
 * @param index pointer to the header begin
 * @param headerSize size of the header
 * @param multiPartMessage if is set to true the message is multi-part
 * @param messageReference message reference for multi-part messages
 * @param numberOfParts number of parts
 * @param partNumber number of current part
 * @return error code
 */
int SMSEncoder::decodeHeader(char* buffer,int bufferSize,int index,unsigned int& headerSize,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber)
{


	if(index>=bufferSize)
	{
		return -1;
	}

	headerSize=octetToValue(&buffer[index]);
	index=index+2;

	if(index+1+headerSize>=bufferSize)
	{
		return -1;
	}

	if(headerSize==5)
	{
		unsigned int informationElement=octetToValue(&buffer[index]);
		index=index+2;

		if(informationElement==0)
		{
			unsigned int lengthOfHeader=octetToValue(&buffer[index]);
			index=index+2;
			if(lengthOfHeader==3)
			{
				messageReference=octetToValue(&buffer[index]);
				index=index+2;

				numberOfParts=octetToValue(&buffer[index]);
				index=index+2;

				partNumber=octetToValue(&buffer[index]);
				index=index+2;

				multiPartMessage=true;
			}
			else
			{
				//unknow
			}
		}
		else
		{
			//unknow
		}
	}
	else
	{
		if(headerSize==6)
		{
			unsigned int informationElement=octetToValue(&buffer[index]);
			index=index+2;

			if(informationElement==8)
			{
				unsigned int lengthOfHeader=octetToValue(&buffer[index]);
				index=index+2;
				if(lengthOfHeader==8)
				{
					unsigned long referenceNumber=octetToValue(&buffer[index]);
					index=index+2;

					unsigned long referenceNumber2=octetToValue(&buffer[index]);
					index=index+2;

					referenceNumber=referenceNumber<<8;
					referenceNumber=referenceNumber+referenceNumber2;
					messageReference=referenceNumber;

					numberOfParts=octetToValue(&buffer[index]);
					index=index+2;

					partNumber=octetToValue(&buffer[index]);
					index=index+2;

					multiPartMessage=true;
				}
				else
				{
						//unknow
				}
			}
			else
			{
				//unknow
			}
		}
		else
		{
			//unknow header
		}
	}

	return index;
}

/**
 * Decodes the 7 bit message
 * @param buffer pointer to the buffer
 * @param bufferSize size of the buffer
 * @param contentLengthInSeptets length of message content is septets
 * @param wstring message decoded message
 * @param header defines if the message content has header
 * @param multiPartMessage if is set to true the message is multi-part
 * @param messageReference message reference for multi-part messages
 * @param numberOfParts number of parts
 * @param partNumber number of current part
 * @return error code
 */
int SMSEncoder::decode7BitMessage(char* buffer,int bufferSize,int contentLengthInSeptets, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber)
{
	int index=0;
	
	unsigned int headerSize=0;
	unsigned int septetProcessed=0;
	int paddingBits=0;

	message.clear();

	if(header)
	{
		index=decodeHeader(buffer,bufferSize,index,headerSize,multiPartMessage,messageReference,numberOfParts,partNumber);

		if(index<0)
		{
			return -1;
		}

		int restBits=(headerSize+1)%7;
		if(restBits>0)
		{
			 paddingBits=7-restBits;
		}
	}

	int headerSizeInSeptets=0;

	if(header)
	{
		headerSizeInSeptets=((headerSize+1)*8+paddingBits)/7;
	}
	else
	{
		headerSizeInSeptets=0;
	}
	
	septetProcessed=headerSizeInSeptets;

	if(headerSize>0)
	{
		if((((headerSize+1)*8+paddingBits)%7)>0)
		{
			//return -1; //this should happed
		}
	}

	int bufferSizeinOctets=bufferSize/2;
	if(bufferSize%2>0)
	{
		return -1;
	}

	int messageSizeInSeptets=((bufferSizeinOctets-headerSize)*8-paddingBits)/7;
	/*
	if((((bufferSizeinOctets-headerSize)*8-paddingBits)%7)>0)
	{
		return -1;
	}*/

	if(messageSizeInSeptets<contentLengthInSeptets-headerSizeInSeptets)
	{
		return -1;
	}

	int movedBits=0;
	unsigned int bitsToAdd=0;

	if(paddingBits>0)
	{
		movedBits=8-paddingBits;
		unsigned char octet=octetToValue(&buffer[index]);
		index=index+2;

		bitsToAdd=octet;
		bitsToAdd=bitsToAdd>>paddingBits;
	}
	
	int end=0;
	bool nextCharIsExtended=false;

	for(int i=index;i<bufferSize;i=i+2)
	{
		movedBits++;
		if(movedBits>7)
		{
			if(bitsToAdd==extensionValue)
			{
				nextCharIsExtended=true;
				septetProcessed++;
			}
			else
			{
				wchar_t unicodeChar;
				if(nextCharIsExtended)
				{
					nextCharIsExtended=false;
					int wChar=unicodeFromExtensionTable(bitsToAdd);
					if(wChar<0)
					{
						wChar=septetTable[0x20]; //space
					}
					else
					{
						unicodeChar=wChar;
					}
				}
				else
				{
					unicodeChar=septetTable[bitsToAdd];
				}
				message=message+unicodeChar;
				septetProcessed++;

			}
			movedBits=1;
			bitsToAdd=0;
		}

		unsigned int octet=octetToValue(&buffer[i]);
		unsigned int newMovedBits=octetToValue(&buffer[i]);

		unsigned int valueMask=0xff>>movedBits;

		octet=octet & valueMask;

		octet=octet<<(movedBits-1);
		octet=octet|bitsToAdd;

		newMovedBits=newMovedBits>>(8-movedBits);
		bitsToAdd= newMovedBits;

		if(octet==extensionValue)
		{
			nextCharIsExtended=true;
			septetProcessed++;
		}
		else
		{
			wchar_t unicodeChar;
			if(nextCharIsExtended)
			{
				nextCharIsExtended=false;
				int wChar=unicodeFromExtensionTable(octet);
				if(wChar<0)
				{
					unicodeChar=septetTable[0x20]; //space
				}
				else
				{
					unicodeChar=wChar;
				}
			}
			else
			{
				unicodeChar=septetTable[octet];
			}
			message=message+unicodeChar;
			septetProcessed++;
		}

		if(i>=bufferSize-2)
		{
			if(movedBits==7)
			{
				if(septetProcessed<contentLengthInSeptets)
				{
					if(bitsToAdd!=extensionValue)
					{
						wchar_t unicodeChar;
						if(nextCharIsExtended)
						{
							nextCharIsExtended=false;
							int wChar=unicodeFromExtensionTable(bitsToAdd);
							if(wChar<0)
							{
								wChar=septetTable[0x20]; //space
							}
							else
							{
								unicodeChar=wChar;
							}
						}
						else
						{
							unicodeChar=septetTable[bitsToAdd];
						}
						message=message+unicodeChar;
					}
				}
			}
		}

	}

	return 1;

}

/**
 * Decodes the unicode message
 * @param buffer pointer to the buffer
 * @param bufferSize size of the buffer
 * @param contentLength length of message content.
 * @param wstring message decoded message
 * @param header defines if the message content has header
 * @param multiPartMessage if is set to true the message is multi-part
 * @param messageReference message reference for multi-part messages
 * @param numberOfParts number of parts
 * @param partNumber number of current part
 * @return error code
 */
int SMSEncoder::decode16BitMessage(char* buffer,int bufferSize,int contentLength, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber)
{
	int index=0;
	
	unsigned int headerSize=0;

	message.clear();

	if(header)
	{
		index=decodeHeader(buffer,bufferSize,index,headerSize,multiPartMessage,messageReference,numberOfParts,partNumber);

		if(index<0)
		{
			return -1;
		}

	}

	for(int i=index;i<bufferSize;i=i+4)
	{
		wchar_t wchar=0;

		wchar=octetToValue(&buffer[i]);
		wchar=wchar<<8;
		wchar=wchar|octetToValue(&buffer[i+2]);
		message=message+wchar;
	}

	return 1;
}

/**
 * Decodes the 8 bit message
 * @param buffer pointer to the buffer
 * @param bufferSize size of the buffer
 * @param contentLength length of message content.
 * @param wstring message decoded message
 * @param header defines if the message content has header
 * @param multiPartMessage if is set to true the message is multi-part
 * @param messageReference message reference for multi-part messages
 * @param numberOfParts number of parts
 * @param partNumber number of current part
 * @return error code
 */
int SMSEncoder::decode8BitMessage(char* buffer,int bufferSize,int contentLength, wstring& message,bool header,bool& multiPartMessage,unsigned long& messageReference,unsigned int& numberOfParts,unsigned int& partNumber)
{
	int index=0;
	
	unsigned int headerSize=0;

	message.clear();

	if(header)
	{
		index=decodeHeader(buffer,bufferSize,index,headerSize,multiPartMessage,messageReference,numberOfParts,partNumber);

		if(index<0)
		{
			return -1;
		}

	}

	string sMessage;

	for(int i=index;i<bufferSize;i=i+1)
	{
		char schar=0;

		schar=octetToValue(&buffer[i]);
	
		sMessage=sMessage+schar;
	}

	message=c2w(sMessage.c_str());

	return 1;
}