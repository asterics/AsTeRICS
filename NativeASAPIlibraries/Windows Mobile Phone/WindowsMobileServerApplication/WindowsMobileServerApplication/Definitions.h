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

#include "Error.h"

struct IncommingSMS
{
	CString m_sSender;
	CString m_sContent;
};

struct OutgoingSMS
{
	CString m_sRecipient;
	CString m_sContent;
};

struct NewCall
{
	CString m_sCallerID;
};

struct MakeCall
{
	CString m_sRecipientID;
};

struct DataToSend
{
	char *l_pPacket;
	unsigned int l_unPacketSize;
	DataToSend()
	{
		l_pPacket=NULL;
	}
	~DataToSend()
	{
		if(l_pPacket!=NULL)
		{
			delete  l_pPacket;
		}
	}
};

#define NEW_SMS (WM_USER+101)
#define SEND_SMS (WM_USER+102)
#define CALL_DISCONNECTED (WM_USER+104)
#define ANSWER_CALL (WM_USER+105)
#define DROP_CALL (WM_USER+106)
#define MAKE_CALL (WM_USER+107)
#define PHONE_STATE_CHANGE (WM_USER+108)
#define GET_PHONE_STATE (WM_USER+109)

#define SEND_DATA (WM_USER+110)
#define WAIT_FOR_CONNECTION (WM_USER+111)

#define BLUETOOTH_RESULT (WM_USER+151)
#define PHONE_RESULT (WM_USER+152)
#define MESSAGE_RESULT (WM_USER+153)
#define CONNECTED_RESULT (WM_USER+154)
#define PORT_RESULT (WM_USER+155)

#define SHOW_BLUETOOTH_RESULT (WM_USER+161)
#define SHOW_PHONE_RESULT (WM_USER+162)
#define SHOW_MESSAGE_RESULT (WM_USER+163)
#define SHOW_CONNECTED_STATE (WM_USER+164)
#define SHOW_PORT (WM_USER+165)
