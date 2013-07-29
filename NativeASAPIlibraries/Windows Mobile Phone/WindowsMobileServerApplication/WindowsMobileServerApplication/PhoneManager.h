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

#include <tapi.h>

enum PhoneState {PS_IDLE=1, PS_RING, PS_CONNECTED};

VOID FAR PASCAL lineCallbackFunc(DWORD hDevice,DWORD dwMsg,DWORD dwCallbackInstance,DWORD dwParam1,DWORD dwParam2,DWORD dwParam3);

typedef struct tagLINEINFO
{
  HLINE hLine;              // Line handle returned by lineOpen
  BOOL  bVoiceLine;         // Indicates if the line is a voice line
  DWORD dwAPIVersion;       // API version that the line supports
  DWORD dwNumOfAddress;     // Number of available addresses on the line
  DWORD dwPermanentLineID;  // Permanent line identifier
  TCHAR szLineName[256];    // Name of the line
} LINEINFO, *LPLINEINFO;

/**
*	@brief Phone manager
*
*	This class manage phone functions.
*
*/

class CPhoneManager
{
public:
	CPhoneManager(CWinThread* pTaskManager);
	~CPhoneManager(void);
	bool init();
	void close();
	int call(CString recipientID);
	int drop();
	int answer();
	PhoneState getPhoneState();
	CString getCallID();
private:
	bool m_bState;
	HLINEAPP m_oTapiHandle;
	static CPhoneManager* m_pActivePhoneManager;
	bool getLineInfo(DWORD ulLineID, LPLINEINFO oLineInfo);
	DWORD m_ulNumberOfDevices;
	DWORD m_ulAPIVersion;
	DWORD m_ulLineID;
	HLINE m_oLineHandle;
	HCALL m_oCallhandle;
	CWinThread* m_pTaskManager;
	const DWORD TAPI_VERSION_1_0;
	PhoneState m_PhoneState;
	void phoneStateChanged(PhoneState enPhoneState,CString sCallID);
	CString m_sCallID;
	
friend VOID FAR PASCAL lineCallbackFunc(DWORD hDevice,DWORD dwMsg,DWORD dwCallbackInstance,DWORD dwParam1,DWORD dwParam2,DWORD dwParam3);
};
