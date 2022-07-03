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
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */


#pragma once

typedef unsigned int uint;
typedef const unsigned int cuint;

enum ReplyType {Init_Reply=1,Close_Reply,MakeCall_Reply,AcceptCall_Reply,DropCall_Reply,SendSMS_Reply};
enum RequestType {Init_Request=1,Close_Request,AcceptCall_Request,DropCall_Request,GetState_Request};
enum CIMProtocolError{None_Error=0,LostPackets_Error=1,CRC_Error=2,InvalidFeatureAddress_Error=3,InvalidCommandCombination_Error=4,InvalidData_Error=5,Other_Error=255};
enum ProtocolSite {Server_Site=1,Client_Site};

/**
*	@brief CIM protocol implementation class
*
*	This class implements CIM protocol specification. It is not finished, needs some changes
*
*/

class CProtocolImplementation
{
public:
	CProtocolImplementation(ProtocolSite UserSite);
	~CProtocolImplementation(void);

	static cuint m_unPacketID_Size=2;
	static cuint m_unAREID_Size=2;
	static cuint m_unDataSize_Size=2;
	static cuint m_unPacketSerialNumber_Size=1;
	static cuint m_unCIMFeatureAddress_Size=2;
	static cuint m_unRequestCode_Size=2;
	static cuint m_unOptionalCRCChecksum_Size=4;

	static cuint m_unOptionalData_MaxSize=2048;

	static cuint m_unPacketID_Possition=0;
	static cuint m_unAREID_Possition=m_unPacketID_Possition+m_unPacketID_Size;
	static cuint m_unDataSize_Possition=m_unAREID_Possition+m_unAREID_Size;
	static cuint m_unPacketSerialNumber_Possition=m_unDataSize_Possition+m_unDataSize_Size;
	static cuint m_unCIMFeatureAddress_Possition=m_unPacketSerialNumber_Possition+m_unPacketSerialNumber_Size;
	static cuint m_unRequestCode_Possition=m_unCIMFeatureAddress_Possition+m_unCIMFeatureAddress_Size;
	static cuint m_unOptionalData_Possition=m_unRequestCode_Possition+m_unRequestCode_Size;
	
	static cuint m_unMinSerialNumber=0;
	static cuint m_unMaxSerialNumber=0x7F;
	static cuint m_unMinEventSerialNumber=0x80;
	static cuint m_unMaxEventSerialNumber=0xFF;

	static cuint m_unUniqueSerialNumber=0x0000;
	static cuint m_unPhoneApplicationConfiguration_Init=0x0001;
	static cuint m_unPhoneApplicationConfiguration_Close=0x0002;
	static cuint m_unPhoneManager_MakeCall=0x0010;
	static cuint m_unPhoneManager_AcceptCall=0x0011;
	static cuint m_unPhoneManager_DropCall=0x0012;
	static cuint m_unPhoneManager_ChangeStateEvent=0x0013;
	static cuint m_unPhoneManager_GetState=0x0014;
	static cuint m_unMessageManager_sendSMS=0x0020;
	static cuint m_unMessageManager_ReceiveSMSEvent=0x0021;

	static cuint m_unInit_PhoneCommand=0x0001;
	static cuint m_unClose_PhoneCommand=0x0002;
	static cuint m_unMakeCall_PhoneCommand=0x0003;
	static cuint m_unAcceptCall_PhoneCommand=0x0004;
	static cuint m_unDropCall_PhoneCommand=0x0005;
	static cuint m_unGetState_PhoneCommand=0x0006;
	
	static cuint m_unVersion = 0x0100; //1.0

	static cuint m_unRequestData_Size=4;
	static cuint m_unReplytData_Size=4;
	static cuint m_unPhoneID_Size=1;
	static cuint m_unSubject_Size=2;
	static cuint m_unPhoneState_Size=1;

	static cuint m_unRequestCode=0x10;
	static cuint m_unReplyCode=0x11;
	static cuint m_unEventCode=0x20;

	static cuint m_unEnableCRC=0x0100;

	static cuint m_unLostPackets_CIMError=1;
	static cuint m_unCRC_CIMError=2;
	static cuint m_unInvalidFeatureAddress_CIMError=3;
	static cuint m_unInvalidCommandCombination_CIMError=4;
	static cuint m_unInvalidData_COMError=5;
	static cuint m_unOther_CIMError=255;

	static cuint m_unPhoneStateIdle=1;
	static cuint m_unPhoneStateRing=2;
	static cuint m_unPhoneStateConnected=3;
	
	static cuint m_nMaxReadAttempt=100;

	static uint getHeaderSize()
	{
		return m_unPacketID_Size+m_unAREID_Size+m_unDataSize_Size+m_unPacketSerialNumber_Size+m_unCIMFeatureAddress_Size+m_unRequestCode_Size;
	}

	static uint getMaxPacketSize()
	{
		return getHeaderSize() + m_unOptionalData_MaxSize + m_unOptionalCRCChecksum_Size;
	}

	static uint getMinHeaderSize()
	{
		return m_unPacketID_Size+m_unAREID_Size+m_unDataSize_Size;
	}
	
	static cuint POLYNOMIAL=0x04c11db7;

	uint crc32_table[256];
	uint crc32_reflect(uint ref, int ch);
	void crc32_init(void);
	uint crc32(char *data, int len);


	uint inceraseSerialNumber();
	uint giveNextSerialNumber();
	uint giveSerialNumber();

	uint inceraseEventSerialNumber();
	uint giveNextEventSerialNumber();
	uint giveEventSerialNumber();

	char * buildRequestPacket(RequestType Request,uint& unBufferSize,bool bAddCRC=true);
	char * buildReplyPacket(ReplyType Reply,CIMProtocolError CIMError,uint unErrorCode,uint& unBufferSize,bool bAddCRC=true);
	char * buildStateReplyPacket(CIMProtocolError unCIMError,uint unStateCode,uint unErrorCode,uint& unBufferSize,bool bAddCRC=true);
	char * buildMakeCallPacket(LPWSTR pPhoneID,uint& unBufferSize,bool bAddCRC=true);
	char * buildPhoneStateEventPacket(uint unState, LPWSTR pPhoneID,uint& unBufferSize,bool bAddCRC=true);
	char * buildSendSMSPacket(LPWSTR pPhoneID,LPWSTR pSubject,uint& unBufferSize,bool bAddCRC=true);
	char * buildReceiveSMSEventPacket(LPWSTR pPhoneID,LPWSTR pSubject,uint& unBufferSize,bool bAddCRC=true);

	int checkPacket(char * pBuffer,uint pBufferLength,CIMProtocolError& error,uint& unFeatureAddress);
	
	int getCommand(char * pBuffer,uint pBufferLength,uint& unCommandCode);
	int getErrorCode(char * pBuffer,uint pBufferLength,uint& unErrorCode);

	int getSMSData(char * pBuffer, uint pBufferLength, LPWSTR& pPhoneID,uint& unPhoneIDSize, LPWSTR& pSubject, uint& unSubjectSize);
	int getPhoneIDData(char * pBuffer, uint pBufferLength, LPWSTR& pPhoneID,uint& unPhoneIDSize, uint& unPhoneState);

	int getPhoneStateCommand(char * pBuffer,uint pBufferLength,uint& unState,uint& unErrorCode);
	int checkHeader(char* pBuffer,int nBufferLen,uint& unOptionaDataSize);

private:
	uint m_unPacketSerialNumber;
	uint m_unEventPacketSerialNumber;
	ProtocolSite m_ProtocolSite;

	int fillPacketHeader(char * pBuffer,uint pBufferLength,uint unDataSize,unsigned char unPacketSerialNumber, uint unCIMFeatureAddress, uint unRequestCode);
	int fillCRCchecksum(char *pBuffer, uint pBufferLength);

	void divide2ByteValue(uint unValue,char& cLowByte,char& cHighByte);
	void divide4ByteValue(uint unValue,char& cByte1,char& cByte2,char& cByte3,char& cByte4);
	int getValueFromBuffer(char * pBuffer,uint pBufferLength, uint unStartIndex,uint unValueSize, uint& unValue);

	int getSMSContentSize(char *pBuffer, uint pBufferLength,uint unPhoneIDSize,uint& unSMSSize);

};
