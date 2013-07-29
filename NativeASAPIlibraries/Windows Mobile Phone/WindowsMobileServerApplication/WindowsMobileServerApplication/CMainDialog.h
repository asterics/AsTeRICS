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

#include "TaskManager.h"

#pragma once



// CMainDialog dialog
class CMainDialog : public CDialog
{
// Construction
public:
	CMainDialog(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_WINDOWSMOBILESERVERAPPLICATION_DIALOG };

#ifdef WIN32_PLATFORM_WFSP
protected:  // control bar embedded members
	CCommandBar m_dlgCommandBar;
#endif // WIN32_PLATFORM_WFSP

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support

// Implementation
protected:
	HICON m_hIcon;
	
	CTaskManager* m_pTaskManager;
	bool initTaskManager();
	
	

	LRESULT bluetoothResultShow(WPARAM wParam, LPARAM lParam);
	LRESULT phoneResultShow(WPARAM wParam, LPARAM lParam);
	LRESULT messageResultShow(WPARAM wParam, LPARAM lParam);
	LRESULT messageConnectedShow(WPARAM wParam, LPARAM lParam);
	LRESULT messagePortShow(WPARAM wParam, LPARAM lParam);


	// Generated message map functions
	virtual BOOL OnInitDialog();
#if defined(_DEVICE_RESOLUTION_AWARE) && !defined(WIN32_PLATFORM_WFSP)
	afx_msg void OnSize(UINT /*nType*/, int /*cx*/, int /*cy*/);
#endif
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnClose();
	CEdit m_CEBluetooth;
	CEdit m_CEPhone;
	CEdit m_CEMessage;
	CEdit m_CEConnected;
	CEdit m_CEPort;
	afx_msg void OnBnClickedButton1();
};
