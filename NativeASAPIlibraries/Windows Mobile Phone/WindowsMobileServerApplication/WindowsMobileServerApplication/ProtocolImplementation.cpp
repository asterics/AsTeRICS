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

#include "StdAfx.h"
#include "ProtocolImplementation.h"

CProtocolImplementation::CProtocolImplementation(ProtocolSite UserSite):m_ProtocolSite(UserSite)
{
	crc32_init();
}

CProtocolImplementation::~CProtocolImplementation(void)
{
}

unsigned int CProtocolImplementation::inceraseSerialNumber()
{
	m_unPacketSerialNumber=giveNextSerialNumber();
	return m_unPacketSerialNumber;
}

unsigned int CProtocolImplementation::giveNextSerialNumber()
{
	unsigned int l_unNextSerialNumber;

	l_unNextSerialNumber=m_unPacketSerialNumber;

	l_unNextSerialNumber=l_unNextSerialNumber+1;

	if(l_unNextSerialNumber>m_unMaxSerialNumber)
	{
		l_unNextSerialNumber=m_unMinSerialNumber;
	}

	return l_unNextSerialNumber;
}

unsigned int CProtocolImplementation::giveSerialNumber()
{
	return m_unPacketSerialNumber;
}

unsigned int CProtocolImplementation::inceraseEventSerialNumber()
{
	m_unEventPacketSerialNumber=giveNextEventSerialNumber();
	return m_unEventPacketSerialNumber;
}

unsigned int CProtocolImplementation::giveNextEventSerialNumber()
{
	unsigned int l_unNextSerialNumber;

	l_unNextSerialNumber=m_unEventPacketSerialNumber;

	l_unNextSerialNumber=l_unNextSerialNumber+1;

	if(l_unNextSerialNumber>m_unMaxEventSerialNumber)
	{
		l_unNextSerialNumber=m_unMinEventSerialNumber;
	}

	return l_unNextSerialNumber;
}

unsigned int CProtocolImplementation::giveEventSerialNumber()
{
	return m_unEventPacketSerialNumber;
}

void CProtocolImplementation::divide2ByteValue(uint unValue,char& cLowByte,char& cHighByte)
{
	unsigned int l_unTemp;
		
	l_unTemp= 0x000000FF & unValue;
	cLowByte=(char) l_unTemp;

	l_unTemp =  unValue>>8;
	l_unTemp=0x000000FF & l_unTemp;
	cHighByte=(char)l_unTemp;
}

void CProtocolImplementation::divide4ByteValue(uint unValue,char& cByte1,char& cByte2,char& cByte3,char& cByte4)
{
	unsigned int l_unTemp;
		
	l_unTemp= 0x000000FF & unValue;
	cByte1=(char) l_unTemp;

	l_unTemp =  unValue>>8;
	l_unTemp=0x000000FF & l_unTemp;
	cByte2=(char)l_unTemp;
	
	l_unTemp =  unValue>>16;
	l_unTemp=0x000000FF & l_unTemp;
	cByte3=(char)l_unTemp;

	l_unTemp =  unValue>>24;
	l_unTemp=0x000000FF & l_unTemp;
	cByte4=(char)l_unTemp;
}

int CProtocolImplementation::fillPacketHeader(char * pBuffer,uint pBufferLength,uint unDataSize, unsigned char unPacketSerialNumber, uint unCIMFeatureAddress, uint unRequestCode)
{
	if(pBuffer==NULL)
	{
		return -1;
	}

	if(pBufferLength<getHeaderSize())
	{
		return -1;
	}
	
	char l_cLowByte, l_cHighByte;

	pBuffer[m_unPacketID_Possition]=0x40;
	pBuffer[m_unPacketID_Possition+1]=0x54;
	
	divide2ByteValue(m_unVersion,l_cLowByte,l_cHighByte);
	pBuffer[m_unAREID_Possition]=l_cLowByte;
	pBuffer[m_unAREID_Possition+1]=l_cHighByte;

	divide2ByteValue(unDataSize,l_cLowByte,l_cHighByte);
	pBuffer[m_unDataSize_Possition]=l_cLowByte;
	pBuffer[m_unDataSize_Possition+1]=l_cHighByte;

	pBuffer[m_unPacketSerialNumber_Possition]=unPacketSerialNumber;
	
	divide2ByteValue(unCIMFeatureAddress,l_cLowByte,l_cHighByte);
	pBuffer[m_unCIMFeatureAddress_Possition]=l_cLowByte;
	pBuffer[m_unCIMFeatureAddress_Possition+1]=l_cHighByte;

	divide2ByteValue(unRequestCode,l_cLowByte,l_cHighByte);
	pBuffer[m_unRequestCode_Possition]=l_cLowByte;
	pBuffer[m_unRequestCode_Possition+1]=l_cHighByte;
	
	return 1;

}

int CProtocolImplementation::fillCRCchecksum(char *pBuffer, uint pBufferLength)
{
	if(pBuffer==NULL)
	{
		return -1;
	}

	if(pBufferLength<getHeaderSize()+ m_unOptionalCRCChecksum_Size)
	{
		return -1;
	}
	
	uint l_unCheckSum;
	l_unCheckSum= crc32(pBuffer,pBufferLength-4);

	char cByte1,cByte2,cByte3,cByte4;

	divide4ByteValue(l_unCheckSum,cByte1,cByte2,cByte3,cByte4);

	pBuffer[pBufferLength-4]=cByte1;
	pBuffer[pBufferLength-3]=cByte2;
	pBuffer[pBufferLength-2]=cByte3;
	pBuffer[pBufferLength-1]=cByte4;

	return 1;

}

char * CProtocolImplementation::buildRequestPacket(RequestType Request,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPacketSize=getHeaderSize()+m_unRequestData_Size;

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=inceraseSerialNumber();

	uint l_unCommandCode=0;
	uint l_unFeatureAddress=0;

	switch(Request)
	{
	case Init_Request:
		{
			l_unCommandCode=m_unInit_PhoneCommand;
			l_unFeatureAddress=m_unPhoneApplicationConfiguration_Init;
			break;
		}
	case Close_Request:
		{
			l_unCommandCode=m_unClose_PhoneCommand;
			l_unFeatureAddress=m_unPhoneApplicationConfiguration_Close;
			break;
		}
	case AcceptCall_Request:
		{
			l_unCommandCode=m_unAcceptCall_PhoneCommand;
			l_unFeatureAddress=m_unPhoneManager_AcceptCall;
			break;
		}
	case DropCall_Request:
		{
			l_unCommandCode=m_unDropCall_PhoneCommand;
			l_unFeatureAddress=m_unPhoneManager_DropCall;
			break;
		}
	case GetState_Request:
		{
			l_unCommandCode=m_unGetState_PhoneCommand;
			l_unFeatureAddress=m_unPhoneManager_GetState;
			break;
		}
	}

	uint l_ucRequestCode=m_unRequestCode;

	if(bAddCRC)
	{
		l_ucRequestCode=l_ucRequestCode|m_unEnableCRC;
	}

	fillPacketHeader(l_pBuffer,l_unPacketSize,m_unRequestData_Size,l_ucPacketSerialNumber,l_unFeatureAddress,l_ucRequestCode);
	
	char cByte1,cByte2,cByte3,cByte4;
	
	divide4ByteValue(l_unCommandCode,cByte1,cByte2,cByte3,cByte4);

	l_pBuffer[m_unOptionalData_Possition]=cByte1;
	l_pBuffer[m_unOptionalData_Possition+1]=cByte2;
	l_pBuffer[m_unOptionalData_Possition+2]=cByte3;
	l_pBuffer[m_unOptionalData_Possition+3]=cByte4;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}

	return l_pBuffer;
}

char * CProtocolImplementation::buildReplyPacket(ReplyType Reply,CIMProtocolError unCIMError,uint unErrorCode,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPacketSize=getHeaderSize()+m_unReplytData_Size;

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=giveSerialNumber();

	uint l_unFeatureAddress=0;

	switch(Reply)
	{
	case Init_Reply:
		{
			l_unFeatureAddress=m_unPhoneApplicationConfiguration_Init;
			break;
		}
	case Close_Reply:
		{
			l_unFeatureAddress=m_unPhoneApplicationConfiguration_Close;
			break;
		}
	case MakeCall_Reply:
		{
			l_unFeatureAddress=m_unPhoneManager_MakeCall;
			break;
		}
	case AcceptCall_Reply:
		{
			l_unFeatureAddress=m_unPhoneManager_AcceptCall;
			break;
		}
	case DropCall_Reply:
		{
			l_unFeatureAddress=m_unPhoneManager_DropCall;
			break;
		}
	case SendSMS_Reply:
		{
			l_unFeatureAddress=m_unMessageManager_sendSMS;
			break;
		}
	}
	
	uint l_unReplyCode=m_unReplyCode;
	uint l_unErrorCode;

	switch(unCIMError)
	{
	case None_Error:
		{
			l_unErrorCode=0;
			break;
		}
	case LostPackets_Error:
		{
			l_unErrorCode=m_unLostPackets_CIMError;
			break;
		}
	case CRC_Error:
		{
			l_unErrorCode=m_unCRC_CIMError;
			break;
		}
	case InvalidFeatureAddress_Error:
		{
			l_unErrorCode=m_unInvalidFeatureAddress_CIMError;
			break;
		}
	case InvalidCommandCombination_Error:
		{
			l_unErrorCode=m_unInvalidCommandCombination_CIMError;
			break;
		}
	case InvalidData_Error:
		{
			l_unErrorCode=m_unInvalidData_COMError;
			break;
		}
	case Other_Error:
		{
			l_unErrorCode=m_unOther_CIMError;
			break;
		}
	}

	uint l_unTmpError=l_unErrorCode<<8;
	l_unReplyCode=l_unReplyCode|l_unTmpError;
	
	fillPacketHeader(l_pBuffer,l_unPacketSize,m_unReplytData_Size,l_ucPacketSerialNumber,l_unFeatureAddress,l_unReplyCode);

	char cByte1,cByte2,cByte3,cByte4;
	
	divide4ByteValue(l_unErrorCode,cByte1,cByte2,cByte3,cByte4);

	l_pBuffer[m_unOptionalData_Possition]=cByte1;
	l_pBuffer[m_unOptionalData_Possition+1]=cByte2;
	l_pBuffer[m_unOptionalData_Possition+2]=cByte3;
	l_pBuffer[m_unOptionalData_Possition+3]=cByte4;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}

	return l_pBuffer;

}

char * CProtocolImplementation::buildStateReplyPacket(CIMProtocolError unCIMError,uint unStateCode,uint unErrorCode,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPacketSize=getHeaderSize()+m_unReplytData_Size+m_unPhoneState_Size;

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=giveSerialNumber();

	uint l_unFeatureAddress=m_unPhoneManager_GetState;
	
	uint l_unReplyCode=m_unReplyCode;
	uint l_unErrorCode;

	switch(unCIMError)
	{
	case None_Error:
		{
			l_unErrorCode=0;
			break;
		}
	case LostPackets_Error:
		{
			l_unErrorCode=m_unLostPackets_CIMError;
			break;
		}
	case CRC_Error:
		{
			l_unErrorCode=m_unCRC_CIMError;
			break;
		}
	case InvalidFeatureAddress_Error:
		{
			l_unErrorCode=m_unInvalidFeatureAddress_CIMError;
			break;
		}
	case InvalidCommandCombination_Error:
		{
			l_unErrorCode=m_unInvalidCommandCombination_CIMError;
			break;
		}
	case InvalidData_Error:
		{
			l_unErrorCode=m_unInvalidData_COMError;
			break;
		}
	case Other_Error:
		{
			l_unErrorCode=m_unOther_CIMError;
			break;
		}
	}

	uint l_unTmpError=l_unErrorCode<<8;
	l_unReplyCode=l_unReplyCode|l_unTmpError;
	
	fillPacketHeader(l_pBuffer,l_unPacketSize,m_unReplytData_Size+m_unPhoneState_Size,l_ucPacketSerialNumber,l_unFeatureAddress,l_unReplyCode);

	char cByte1,cByte2,cByte3,cByte4;
	
	divide4ByteValue(l_unErrorCode,cByte1,cByte2,cByte3,cByte4);

	l_pBuffer[m_unOptionalData_Possition]=cByte1;
	l_pBuffer[m_unOptionalData_Possition+1]=cByte2;
	l_pBuffer[m_unOptionalData_Possition+2]=cByte3;
	l_pBuffer[m_unOptionalData_Possition+3]=cByte4;
	
	l_pBuffer[m_unOptionalData_Possition+m_unReplytData_Size]=(char)unStateCode;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}

	return l_pBuffer;

}

char * CProtocolImplementation::buildMakeCallPacket(LPTSTR pPhoneID,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPhoneIDLength=(uint)wcslen(pPhoneID);
	uint l_unPhoneIDSize=(l_unPhoneIDLength+1)*2;
	
	uint l_unOptionalDataSize=m_unRequestData_Size + m_unPhoneID_Size + l_unPhoneIDSize;
	uint l_unPacketSize=getHeaderSize()+l_unOptionalDataSize;
	

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=inceraseSerialNumber();

	
	uint l_ucRequestCode=m_unRequestCode;

	if(bAddCRC)
	{
		l_ucRequestCode=l_ucRequestCode|m_unEnableCRC;
	}

	uint l_unFeatureAddress=m_unPhoneManager_MakeCall;

	fillPacketHeader(l_pBuffer,l_unPacketSize,l_unOptionalDataSize,l_ucPacketSerialNumber,l_unFeatureAddress,l_ucRequestCode);
	
	char cByte1,cByte2,cByte3,cByte4;
	
	divide4ByteValue(m_unMakeCall_PhoneCommand,cByte1,cByte2,cByte3,cByte4);

	uint l_unOptionalData_Index = m_unOptionalData_Possition;

	l_pBuffer[l_unOptionalData_Index]=cByte1;
	l_pBuffer[l_unOptionalData_Index+1]=cByte2;
	l_pBuffer[l_unOptionalData_Index+2]=cByte3;
	l_pBuffer[l_unOptionalData_Index+3]=cByte4;
	
	l_unOptionalData_Index=l_unOptionalData_Index+4;

	l_pBuffer[l_unOptionalData_Index]= (char)l_unPhoneIDSize;
	
	l_unOptionalData_Index=l_unOptionalData_Index+1;

	for(uint i=0;i<l_unPhoneIDLength;i++)
	{
		uint l_unStringChar=pPhoneID[i];
		divide2ByteValue(l_unStringChar,cByte1,cByte2);
		l_pBuffer[l_unOptionalData_Index]=cByte1;
		l_pBuffer[l_unOptionalData_Index+1]=cByte2;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}

	return l_pBuffer;
}

char * CProtocolImplementation::buildPhoneStateEventPacket(uint unState,LPTSTR pPhoneID,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPhoneIDLength=(uint)wcslen(pPhoneID);
	uint l_unPhoneIDSize=(l_unPhoneIDLength+1)*2;
	
	uint l_unOptionalDataSize=m_unPhoneState_Size + m_unPhoneID_Size + l_unPhoneIDSize;
	uint l_unPacketSize=getHeaderSize()+l_unOptionalDataSize;
	

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=inceraseEventSerialNumber();

	
	uint l_ucRequestCode=m_unEventCode;
	
	/*
	if(bAddCRC)
	{
		l_ucRequestCode=l_ucRequestCode|m_unEnableCRC;
	}*/

	uint l_unFeatureAddress=m_unPhoneManager_ChangeStateEvent;

	fillPacketHeader(l_pBuffer,l_unPacketSize,l_unOptionalDataSize,l_ucPacketSerialNumber,l_unFeatureAddress,l_ucRequestCode);
	
	char cByteLow,cByteHigh;
	

	uint l_unOptionalData_Index = m_unOptionalData_Possition;

	l_pBuffer[l_unOptionalData_Index]=(char) unState;
	l_unOptionalData_Index=l_unOptionalData_Index+1;

	l_pBuffer[l_unOptionalData_Index]= (char)l_unPhoneIDSize;
	l_unOptionalData_Index=l_unOptionalData_Index+1;

	for(uint i=0;i<l_unPhoneIDLength;i++)
	{
		uint l_unStringChar=pPhoneID[i];
		divide2ByteValue(l_unStringChar,cByteLow,cByteHigh);
		l_pBuffer[l_unOptionalData_Index]=cByteLow;
		l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}

	return l_pBuffer;
}

char * CProtocolImplementation::buildSendSMSPacket(LPTSTR pPhoneID,LPTSTR pSubject,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPhoneIDLength=(uint)wcslen(pPhoneID);
	uint l_unPhoneIDSize=(l_unPhoneIDLength+1)*2;

	uint l_unSubjectLength=(uint)wcslen(pSubject);
	uint l_unSubjectSize=(l_unSubjectLength+1)*2;

	uint l_unOptionalDataSize=m_unPhoneID_Size +m_unSubject_Size+ l_unPhoneIDSize+ l_unSubjectSize;

	uint l_unPacketSize=getHeaderSize()+l_unOptionalDataSize;
	
	if(l_unOptionalDataSize>m_unOptionalData_MaxSize)
	{
		unsigned int l_unOffset = l_unOptionalDataSize -m_unOptionalData_MaxSize;

		if(l_unOffset%2>0)
		{
			l_unOffset++;
		}

		l_unSubjectSize=l_unSubjectSize-l_unOffset;
		l_unSubjectLength=l_unSubjectSize/2-1;
	}

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=inceraseSerialNumber();

	
	uint l_ucRequestCode=m_unRequestCode;

	if(bAddCRC)
	{
		l_ucRequestCode=l_ucRequestCode|m_unEnableCRC;
	}

	uint l_unFeatureAddress=m_unMessageManager_sendSMS;

	fillPacketHeader(l_pBuffer,l_unPacketSize,l_unOptionalDataSize,l_ucPacketSerialNumber,l_unFeatureAddress,l_ucRequestCode);
	
	uint l_unOptionalData_Index = m_unOptionalData_Possition;
	
	l_pBuffer[l_unOptionalData_Index]= (char)l_unPhoneIDSize;
	l_unOptionalData_Index=l_unOptionalData_Index+1;
	
	char cByteLow,cByteHigh;

	for(uint i=0;i<l_unPhoneIDLength;i++)
	{
		uint l_unStringChar=pPhoneID[i];
		divide2ByteValue(l_unStringChar,cByteLow,cByteHigh);
		l_pBuffer[l_unOptionalData_Index]=cByteLow;
		l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;
	l_unOptionalData_Index=l_unOptionalData_Index+2;

	divide2ByteValue(l_unSubjectSize,cByteLow,cByteHigh);
	l_pBuffer[l_unOptionalData_Index]=cByteLow;
	l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
	l_unOptionalData_Index=l_unOptionalData_Index+2;
	
	for(uint i=0;i<l_unSubjectLength;i++)
	{
		uint l_unStringChar=pSubject[i];
		divide2ByteValue(l_unStringChar,cByteLow,cByteHigh);
		l_pBuffer[l_unOptionalData_Index]=cByteLow;
		l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;
	l_unOptionalData_Index=l_unOptionalData_Index+2;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}


	return l_pBuffer;
}

char * CProtocolImplementation::buildReceiveSMSEventPacket(LPTSTR pPhoneID,LPTSTR pSubject,uint& unBufferSize,bool bAddCRC)
{
	uint l_unPhoneIDLength=(uint)wcslen(pPhoneID);
	uint l_unPhoneIDSize=(l_unPhoneIDLength+1)*2;

	uint l_unSubjectLength=(uint)wcslen(pSubject);
	uint l_unSubjectSize=(l_unSubjectLength+1)*2;

	uint l_unOptionalDataSize=m_unPhoneID_Size +m_unSubject_Size+ l_unPhoneIDSize+l_unSubjectSize;

	uint l_unPacketSize=getHeaderSize()+l_unOptionalDataSize;
	
	if(l_unOptionalDataSize>m_unOptionalData_MaxSize)
	{
		unsigned int l_unOffset = l_unOptionalDataSize -m_unOptionalData_MaxSize;

		if(l_unOffset%2>0)
		{
			l_unOffset++;
		}

		l_unSubjectSize=l_unSubjectSize-l_unOffset;
		l_unSubjectLength=l_unSubjectSize/2-1;
	}

	if(bAddCRC)
	{
		l_unPacketSize=l_unPacketSize+m_unOptionalCRCChecksum_Size;
	}
	
	unBufferSize=l_unPacketSize;
	char*  l_pBuffer= new char[l_unPacketSize];

	for(unsigned int i=0;i<l_unPacketSize;i++)
	{
		l_pBuffer[i]=0;
	}
	
	unsigned char l_ucPacketSerialNumber=inceraseEventSerialNumber();

	
	uint l_ucRequestCode=m_unEventCode;

	/*
	if(bAddCRC)
	{
		l_ucRequestCode=l_ucRequestCode|m_unEnableCRC;
	}*/

	uint l_unFeatureAddress=m_unMessageManager_ReceiveSMSEvent;

	fillPacketHeader(l_pBuffer,l_unPacketSize,l_unOptionalDataSize,l_ucPacketSerialNumber,l_unFeatureAddress,l_ucRequestCode);
	
	uint l_unOptionalData_Index = m_unOptionalData_Possition;
	
	l_pBuffer[l_unOptionalData_Index]= (char)l_unPhoneIDSize;
	l_unOptionalData_Index=l_unOptionalData_Index+1;
	
	char cByteLow,cByteHigh;

	for(uint i=0;i<l_unPhoneIDLength;i++)
	{
		uint l_unStringChar=pPhoneID[i];
		divide2ByteValue(l_unStringChar,cByteLow,cByteHigh);
		l_pBuffer[l_unOptionalData_Index]=cByteLow;
		l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;
	l_unOptionalData_Index=l_unOptionalData_Index+2;

	divide2ByteValue(l_unSubjectSize,cByteLow,cByteHigh);
	l_pBuffer[l_unOptionalData_Index]=cByteLow;
	l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
	l_unOptionalData_Index=l_unOptionalData_Index+2;
	
	for(uint i=0;i<l_unSubjectLength;i++)
	{
		uint l_unStringChar=pSubject[i];
		divide2ByteValue(l_unStringChar,cByteLow,cByteHigh);
		l_pBuffer[l_unOptionalData_Index]=cByteLow;
		l_pBuffer[l_unOptionalData_Index+1]=cByteHigh;
		l_unOptionalData_Index=l_unOptionalData_Index+2;
	}

	l_pBuffer[l_unOptionalData_Index]=0;
	l_pBuffer[l_unOptionalData_Index+1]=0;
	l_unOptionalData_Index=l_unOptionalData_Index+2;

	if(bAddCRC)
	{
		fillCRCchecksum(l_pBuffer,l_unPacketSize);
	}


	return l_pBuffer;
}

int CProtocolImplementation::checkPacket(char * pBuffer,uint pBufferLength,CIMProtocolError& error,uint& unFeatureAddress)
{
	uint l_unAREID;
	getValueFromBuffer(pBuffer,pBufferLength,m_unAREID_Possition,m_unAREID_Size,l_unAREID);

	uint l_unOptionalDataSize;
	getValueFromBuffer(pBuffer,pBufferLength,m_unDataSize_Possition,m_unDataSize_Size,l_unOptionalDataSize);

	uint l_unPacketSerialNumber;
	getValueFromBuffer(pBuffer,pBufferLength,m_unPacketSerialNumber_Possition,m_unPacketSerialNumber_Size,l_unPacketSerialNumber);

	uint l_unCIMFeatureAddress;
	getValueFromBuffer(pBuffer,pBufferLength,m_unCIMFeatureAddress_Possition,m_unCIMFeatureAddress_Size,l_unCIMFeatureAddress);
	
	unFeatureAddress=l_unCIMFeatureAddress;

	uint l_unRequestCodeAll;
	getValueFromBuffer(pBuffer,pBufferLength,m_unRequestCode_Possition,m_unRequestCode_Size,l_unRequestCodeAll);
	uint l_unRequestCode = 0x00FF&l_unRequestCodeAll;

	uint l_unCheckSumRead;
	getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition+l_unOptionalDataSize,m_unOptionalCRCChecksum_Size,l_unCheckSumRead);
	
	uint l_unCheckSumCounted;
	l_unCheckSumCounted=crc32(pBuffer,m_unOptionalData_Possition+l_unOptionalDataSize);

	if(l_unCheckSumRead!=l_unCheckSumCounted)
	{
		error=CRC_Error;
		return -1;
	}
	
	if((l_unCIMFeatureAddress!=m_unUniqueSerialNumber)&&(l_unCIMFeatureAddress!=m_unPhoneApplicationConfiguration_Init)&&(l_unCIMFeatureAddress!=m_unPhoneApplicationConfiguration_Close)&&\
		(l_unCIMFeatureAddress!=m_unPhoneManager_MakeCall)&&(l_unCIMFeatureAddress!=m_unPhoneManager_AcceptCall)&&(l_unCIMFeatureAddress!=m_unPhoneManager_DropCall)&&(l_unCIMFeatureAddress!=m_unPhoneManager_GetState)&&\
		(l_unCIMFeatureAddress!=m_unPhoneManager_ChangeStateEvent)&&(l_unCIMFeatureAddress!=m_unMessageManager_sendSMS)&&(l_unCIMFeatureAddress!=m_unMessageManager_ReceiveSMSEvent))
	{
		error=InvalidFeatureAddress_Error;
		return -1;
	}

	bool l_bCorrectData=false;

	if(m_ProtocolSite==Client_Site)
	{
		if((l_unCIMFeatureAddress==m_unPhoneApplicationConfiguration_Init)||(l_unCIMFeatureAddress==m_unPhoneApplicationConfiguration_Close)||\
		(l_unCIMFeatureAddress==m_unPhoneManager_MakeCall)||(l_unCIMFeatureAddress==m_unPhoneManager_AcceptCall)||(l_unCIMFeatureAddress==m_unPhoneManager_DropCall)||\
		(l_unCIMFeatureAddress==m_unMessageManager_sendSMS))
		{
			if((l_unRequestCode==m_unReplyCode)&&(l_unOptionalDataSize==m_unReplytData_Size))
			{
				l_bCorrectData=true;
			}
		}
		else
		{
			if(l_unRequestCode==m_unEventCode)
			{
				if(l_unCIMFeatureAddress==m_unPhoneManager_ChangeStateEvent)
				{
					unsigned char l_ucPhoneIDLength=(unsigned char)pBuffer[m_unOptionalData_Possition+m_unPhoneState_Size];
					if(l_unOptionalDataSize==l_ucPhoneIDLength+m_unPhoneState_Size+m_unPhoneID_Size)
					{
						uint l_unPhoneState=pBuffer[m_unOptionalData_Possition];
						if((l_unPhoneState==m_unPhoneStateIdle)||(l_unPhoneState==m_unPhoneStateRing)||(l_unPhoneState==m_unPhoneStateConnected))
						{
							l_bCorrectData=true;
						}
					}
				}
				else
				{
					if(l_unCIMFeatureAddress==m_unMessageManager_ReceiveSMSEvent)
					{
						unsigned char l_ucPhoneIDLength=(unsigned char)pBuffer[m_unOptionalData_Possition];
						uint l_unSMSSize;
						int l_unResult=getSMSContentSize(pBuffer,pBufferLength,l_ucPhoneIDLength,l_unSMSSize);

						if((l_unResult>0)&&(l_ucPhoneIDLength+l_unSMSSize+m_unPhoneState_Size+m_unSubject_Size==l_unOptionalDataSize))
						{
							l_bCorrectData=true;
						}
					}
					else
					{
					
					}
				}
			}
			else
			{
				if(l_unCIMFeatureAddress==m_unPhoneManager_GetState)
				{
					if((l_unRequestCode==m_unReplyCode)&&(l_unOptionalDataSize==m_unReplytData_Size+m_unPhoneState_Size))
					{
						l_bCorrectData=true;
					}
				}
			}
		}
	
	}
	else
	{
		if(l_unRequestCode==m_unRequestCode)
		{
			switch(l_unCIMFeatureAddress)
			{
			case m_unPhoneApplicationConfiguration_Init:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unInit_PhoneCommand)&&(l_unOptionalDataSize==m_unRequestData_Size))
					{
						l_bCorrectData=true;
					}

					break;
				}
			case m_unPhoneApplicationConfiguration_Close:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unClose_PhoneCommand)&&(l_unOptionalDataSize==m_unRequestData_Size))
					{
						l_bCorrectData=true;
					}
					break;
				}
			case m_unPhoneManager_MakeCall:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unMakeCall_PhoneCommand))
					{
						unsigned char l_unPhoneIDSize=pBuffer[m_unOptionalData_Possition+m_unRequestData_Size];
						if(l_unOptionalDataSize==m_unRequestData_Size+l_unPhoneIDSize+m_unPhoneID_Size)
						{
							l_bCorrectData=true;
						}
						
					}

					break;
				}
			case m_unPhoneManager_AcceptCall:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unAcceptCall_PhoneCommand)&&(l_unOptionalDataSize==m_unRequestData_Size))
					{
						l_bCorrectData=true;
					}
					break;
				}
			case m_unPhoneManager_DropCall:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unDropCall_PhoneCommand)&&(l_unOptionalDataSize==m_unRequestData_Size))
					{
						l_bCorrectData=true;
					}
					break;
				}
			case m_unPhoneManager_GetState:
				{
					uint l_unCommand;
					uint l_unResult=getCommand(pBuffer,pBufferLength,l_unCommand);
					if((l_unResult>0)&&(l_unCommand==m_unGetState_PhoneCommand)&&(l_unOptionalDataSize==m_unRequestData_Size))
					{
						l_bCorrectData=true;
					}
					break;
				}
			case m_unMessageManager_sendSMS:
				{
					unsigned char l_ucPhoneIDLength=(unsigned char)pBuffer[m_unOptionalData_Possition];
					uint l_unSMSSize;
					int l_unResult=getSMSContentSize(pBuffer,pBufferLength,l_ucPhoneIDLength,l_unSMSSize);

					if((l_unResult>0)&&(l_ucPhoneIDLength+l_unSMSSize+m_unPhoneState_Size+m_unSubject_Size==l_unOptionalDataSize))
					{
							l_bCorrectData=true;
					}
				}

			}
		}
	}

	if(l_bCorrectData==false)
	{
		error=InvalidCommandCombination_Error;
		return -1;
	}
	
	return 1;

}

int CProtocolImplementation::getValueFromBuffer(char * pBuffer,uint pBufferLength, uint unStartIndex,uint unValueSize, uint& unValue)
{
	if(unStartIndex+unValueSize>pBufferLength)
	{
		return -1;
	}

	if(unValueSize>4)
	{
		return -1;
	}
	
	unValue=0;

	for(uint i=0;i<unValueSize;i++)
	{
		unsigned char l_cBufferChar=(unsigned char)pBuffer[unStartIndex+i];
		uint l_unTempValue=l_cBufferChar;
		l_unTempValue=l_unTempValue<<(i*8);
		unValue=unValue|l_unTempValue;
	}

	return 1;

}

int CProtocolImplementation::getCommand(char * pBuffer,uint pBufferLength,uint& unCommandCode)
{
	int l_nResult;
	
	l_nResult=getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition,m_unRequestData_Size,unCommandCode);

	return l_nResult;
}

int CProtocolImplementation::getErrorCode(char * pBuffer,uint pBufferLength,uint& unErrorCode)
{
	int l_nResult;
	
	l_nResult=getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition,m_unReplytData_Size,unErrorCode);

	return l_nResult;
}

int CProtocolImplementation::getPhoneStateCommand(char * pBuffer,uint pBufferLength,uint& unState,uint& unErrorCode)
{
	int l_nResult;
	
	l_nResult=getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition,m_unReplytData_Size,unErrorCode);
	
	if(l_nResult<0)
	{
		return l_nResult;
	}
	
	l_nResult=getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition+m_unReplytData_Size,m_unPhoneState_Size,unState);

	return l_nResult;
}

int CProtocolImplementation::getSMSContentSize(char *pBuffer, uint pBufferLength,uint unPhoneIDSize,uint& unSMSSize)
{
	int l_nResult;
	l_nResult=getValueFromBuffer(pBuffer,pBufferLength,m_unOptionalData_Possition+m_unPhoneID_Size+unPhoneIDSize,m_unSubject_Size,unSMSSize);

	return l_nResult;
}

int CProtocolImplementation::getSMSData(char *pBuffer, uint pBufferLength ,LPTSTR& pPhoneID,uint& unPhoneIDSize, LPTSTR& pSubject, uint& unSubjectSize)
{
	unsigned char l_cPhoneIDSize=(unsigned char)pBuffer[m_unOptionalData_Possition];
	uint l_unPhoneIDSize=l_cPhoneIDSize/2;
	unPhoneIDSize=l_unPhoneIDSize;


	pPhoneID=new wchar_t[l_unPhoneIDSize];
	uint l_unBufferIndex=m_unOptionalData_Possition+m_unPhoneID_Size;

	for(uint i=0;i<l_unPhoneIDSize;i++)
	{
		uint l_unChar;
		getValueFromBuffer(pBuffer,pBufferLength,l_unBufferIndex,2,l_unChar);
		pPhoneID[i]=(wchar_t)l_unChar;
		l_unBufferIndex=l_unBufferIndex+2;

	}

	pPhoneID[l_unPhoneIDSize-1]=0;

	uint l_unSubjectSize;
	getValueFromBuffer(pBuffer,pBufferLength,l_unBufferIndex,m_unSubject_Size,l_unSubjectSize);
	
	l_unSubjectSize=l_unSubjectSize/2;
	unSubjectSize=l_unSubjectSize;

	l_unBufferIndex=l_unBufferIndex+2;
	
	pSubject=new wchar_t[l_unSubjectSize];


	for(uint i=0;i<l_unSubjectSize;i++)
	{
		uint l_unChar;
		getValueFromBuffer(pBuffer,pBufferLength,l_unBufferIndex,2,l_unChar);
		pSubject[i]=(wchar_t)l_unChar;
		l_unBufferIndex=l_unBufferIndex+2;

	}
	
	pSubject[l_unSubjectSize-1]=0;

	return 1;
}

int CProtocolImplementation::getPhoneIDData(char *pBuffer, uint pBufferLength, LPTSTR& pPhoneID,uint& unPhoneIDSize,uint& unPhoneState)
{
	
	unsigned char l_cPhoneIDSize;
	
	if(m_ProtocolSite==Client_Site)
	{
		l_cPhoneIDSize=(unsigned char)pBuffer[m_unOptionalData_Possition+m_unPhoneState_Size];
	}
	else
	{
		l_cPhoneIDSize=(unsigned char)pBuffer[m_unOptionalData_Possition+m_unRequestData_Size];
	}
	
	uint l_unPhoneIDSize=l_cPhoneIDSize/2;
	unPhoneIDSize=l_unPhoneIDSize;


	pPhoneID=new wchar_t[l_unPhoneIDSize];
	uint l_unBufferIndex=m_unOptionalData_Possition+m_unPhoneID_Size;

	if(m_ProtocolSite==Client_Site)
	{
		l_unBufferIndex=l_unBufferIndex+m_unPhoneState_Size;
	}
	else
	{
		l_unBufferIndex=l_unBufferIndex+m_unRequestData_Size;
	}

	for(uint i=0;i<l_unPhoneIDSize;i++)
	{
		uint l_unChar;
		getValueFromBuffer(pBuffer,pBufferLength,l_unBufferIndex,2,l_unChar);
		pPhoneID[i]=(wchar_t)l_unChar;
		l_unBufferIndex=l_unBufferIndex+2;

	}

	pPhoneID[l_unPhoneIDSize-1]=0;
	
	unPhoneState=0;
	
	if(m_ProtocolSite==Client_Site)
	{
		unPhoneState=pBuffer[m_unOptionalData_Possition];
	}

	return 1;
}

int CProtocolImplementation::checkHeader(char* pBuffer,int nBufferLen,uint& unOptionaDataSize)
{
	if((pBuffer[0]!='@')||(pBuffer[1]!='T'))
	{
		return -1;
	}

	int l_unResult = getValueFromBuffer(pBuffer, nBufferLen,m_unDataSize_Possition ,m_unDataSize_Size,unOptionaDataSize);

	return l_unResult;
}

uint CProtocolImplementation::crc32_reflect(uint ref, int ch)
{
	unsigned int value=0;
    // Swap bit 0 for bit 7
    // bit 1 for bit 6, etc.
    for(int i = 1; i <= ch; i++)
    {
		if(ref & 1)
			value |= 1 << (ch - i);
        ref >>= 1;
	}
    return value;
}

void CProtocolImplementation::crc32_init(void)
{
	unsigned int crc,i;
    for(i = 0; i <= 0xFF; i++)
    {
		crc=crc32_reflect(i, 8) << 24;
        for (int j = 0; j < 8; j++)
			crc = (crc << 1) ^ (crc & 0x80000000 ? POLYNOMIAL : 0);
        crc32_table[i] = crc32_reflect(crc, 32);
	}
}

uint CProtocolImplementation::crc32(char *data, int len)
{
	unsigned int result = 0xffffffff;
	while(len--)
		result = (result >> 8) ^ crc32_table[(result & 0xFF) ^ *data++];
    // Exclusive OR the result with the beginning value.
    // In this case (0xffffffff) it's NEG operation
    return ~result;
}