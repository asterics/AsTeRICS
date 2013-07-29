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
#include "stdafx.h"
#include "WindowsMobileServerApplication.h"
#include "CMainDialog.h"
#include "Definitions.h" 

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

// CMainDialog dialog

CMainDialog::CMainDialog(CWnd* pParent /*=NULL*/)
	: CDialog(CMainDialog::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CMainDialog::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_EDIT2, m_CEBluetooth);
	DDX_Control(pDX, IDC_EDIT3, m_CEPhone);
	DDX_Control(pDX, IDC_EDIT4, m_CEMessage);
	DDX_Control(pDX, IDC_EDIT5, m_CEConnected);
	DDX_Control(pDX, IDC_EDIT6, m_CEPort);
}

BEGIN_MESSAGE_MAP(CMainDialog, CDialog)
#if defined(_DEVICE_RESOLUTION_AWARE) && !defined(WIN32_PLATFORM_WFSP)
	ON_WM_SIZE()
#endif
	//}}AFX_MSG_MAP
	ON_WM_CLOSE()
	//ON_BN_CLICKED(IDC_BUTTON1, &CMainDialog::OnBnClickedButton1)
	//ON_BN_CLICKED(IDC_BUTTON2, &CMainDialog::OnBnClickedButton2)
	ON_WM_TIMER()
	ON_MESSAGE(SHOW_BLUETOOTH_RESULT,bluetoothResultShow)
	ON_MESSAGE(SHOW_PHONE_RESULT,phoneResultShow)
	ON_MESSAGE(SHOW_MESSAGE_RESULT,messageResultShow)
	ON_MESSAGE(SHOW_CONNECTED_STATE,messageConnectedShow)
	ON_MESSAGE(SHOW_PORT,messagePortShow)
	ON_BN_CLICKED(IDC_BUTTON1, &CMainDialog::OnBnClickedButton1)
END_MESSAGE_MAP()


// CMainDialog message handlers

LRESULT CMainDialog::bluetoothResultShow(WPARAM wParam, LPARAM lParam)
{
	if(wParam==0)
	{
		m_CEBluetooth.SetWindowText(L"OK");
	}
	else
	{
		m_CEBluetooth.SetWindowText(L"ERROR");
	}

	return 0;
}

LRESULT CMainDialog::phoneResultShow(WPARAM wParam, LPARAM lParam)
{
	if(wParam==0)
	{
		m_CEPhone.SetWindowText(L"OK");
	}
	else
	{
		m_CEPhone.SetWindowText(L"ERROR");
	}

	return 0;
}

LRESULT CMainDialog::messageResultShow(WPARAM wParam, LPARAM lParam)
{
	if(wParam==0)
	{
		m_CEMessage.SetWindowText(L"OK");
	}
	else
	{
		m_CEMessage.SetWindowText(L"ERROR");
	}

	return 0;
}

LRESULT CMainDialog::messageConnectedShow(WPARAM wParam, LPARAM lParam)
{
	if(wParam==0)
	{
		m_CEConnected.SetWindowText(L"YES");
	}
	else
	{
		m_CEConnected.SetWindowText(L"NO");
	}

	return 0;
}

LRESULT CMainDialog::messagePortShow(WPARAM wParam, LPARAM lParam)
{
	
	int l_nPort=(int)wParam;

	if(l_nPort>-1)
	{
		
		CString s;
		s.Format(L"%d",l_nPort);
		m_CEPort.SetWindowText(s);
	}
	else
	{
		m_CEPort.SetWindowText(L"");
	}

	return 0;
}

BOOL CMainDialog::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

#ifdef WIN32_PLATFORM_WFSP
	if (!m_dlgCommandBar.Create(this) ||
	    !m_dlgCommandBar.InsertMenuBar(IDR_MAINFRAME))
	{
		TRACE0("Failed to create CommandBar\n");
		return FALSE;      // fail to create
	}
#endif // WIN32_PLATFORM_WFSP
	// TODO: Add extra initialization here
	
	m_CEBluetooth.SetWindowText(L"-");
	m_CEPhone.SetWindowText(L"-");
	m_CEMessage.SetWindowText(L"-");
	m_CEConnected.SetWindowText(L"NO");
	m_CEPort.SetWindowText(L"-");
	
	initTaskManager();


	return TRUE;  // return TRUE  unless you set the focus to a control
}

#if defined(_DEVICE_RESOLUTION_AWARE) && !defined(WIN32_PLATFORM_WFSP)
void CMainDialog::OnSize(UINT /*nType*/, int /*cx*/, int /*cy*/)
{
	/*k
	if (AfxIsDRAEnabled())
	{
		DRA::RelayoutDialog(
			AfxGetResourceHandle(), 
			this->m_hWnd, 
			DRA::GetDisplayMode() != DRA::Portrait ? 
			MAKEINTRESOURCE(IDD_WINDOWSMOBILESERVERAPPLICATION_DIALOG_WIDE) : 
			MAKEINTRESOURCE(IDD_WINDOWSMOBILESERVERAPPLICATION_DIALOG));
	}*/
}
#endif

bool CMainDialog::initTaskManager()
{
	
	m_pTaskManager = new CTaskManager();

	m_pTaskManager->m_bAutoDelete = FALSE;

	m_pTaskManager->setMainDialog(this);
	
	if(!m_pTaskManager->CreateThread())
	{
		return false;
	}



	return true;
}

void CMainDialog::OnClose()
{
	// TODO: Add your message handler code here and/or call default
	m_pTaskManager->PostThreadMessage(WM_QUIT, 0, 0);
	WaitForSingleObject(m_pTaskManager,INFINITE);
	
	delete m_pTaskManager;

	CDialog::OnClose();
}


void CMainDialog::OnBnClickedButton1()
{
	// TODO: Add your control notification handler code here
	AfxGetMainWnd()->PostMessage(WM_CLOSE);
}
