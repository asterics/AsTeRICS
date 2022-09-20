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
#include "afxwin.h"
#include "MessageManager.h"
#include "PhoneManager.h"
#include "BTConnectionManager.h"

/**
*	@brief Task manager
*
*	Main Windows Mobile AsTeRICS Server task manager
*
*/

class CTaskManager :
	public CWinThread
{
DECLARE_DYNCREATE(CTaskManager)
public:
	CTaskManager();
	~CTaskManager(void);
	virtual BOOL InitInstance();
	virtual int ExitInstance();
	void setMainDialog(CDialog* pMainDialog);

protected:
	DECLARE_MESSAGE_MAP()

private:
	CDialog* m_pMainDialog;
	CMessageManager* m_pMessageManager;
	CPhoneManager* m_pPhoneManager;
	CBTConnectionManager *m_pBTConnectionManager;
	void newSmsIncomming(WPARAM wParam, LPARAM lParam);
	void sendSMS(WPARAM wParam, LPARAM lParam);
	void newCall(WPARAM wParam, LPARAM lParam);
	void answerCall(WPARAM wParam, LPARAM lParam);
	void dropCall(WPARAM wParam, LPARAM lParam);
	void makeCall(WPARAM wParam, LPARAM lParam);
	void phoneStateChanged(WPARAM wParam, LPARAM lParam);
	void getPhoneState(WPARAM wParam, LPARAM lParam);
	void bluetoothResult(WPARAM wParam, LPARAM lParam);
	void phoneResult(WPARAM wParam, LPARAM lParam);
	void messageResult(WPARAM wParam, LPARAM lParam);
	void connectedResult(WPARAM wParam, LPARAM lParam);
	void portResult(WPARAM wParam, LPARAM lParam);
};
