
#include "AsTeRICS_PluginCreationWizard.h"
 

///////// AsTeRICS Plugin Creation Wizard


LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	int wmId, wmEvent, t;
	
	switch( message ) 
	{
		case WM_CREATE:
			  ghWndMain=hWnd;
				GetModuleFileName(NULL,path,sizeof(path));
				for (t=strlen(path);(t>0)&&path[t]!='\\';t--) ;
				path[t+1]=0;
				GetSystemTime(&st);              // gets current time
				strcpy(pic,"d.M.yyyy");
				GetDateFormat(NULL, 0, &st, pic , actdate, sizeof(sztemp));
				ghWndBrowser=CreateDialog(hInst, (LPCTSTR)IDD_BROWSERDIALOG,ghWndMain, (DLGPROC)BROWSEDlghandler);

			return DefWindowProc( hWnd, message, wParam, lParam );

		case WM_COMMAND:
			wmId    = LOWORD(wParam); 
			wmEvent = HIWORD(wParam); 

		switch( wmId ) 
		 {
			case IDM_EXIT:
				SendMessage(ghWndMain,WM_DESTROY,0,0);
				break;
		 default:
		   return DefWindowProc( hWnd, message, wParam, lParam );
		 }
		 break;

		case WM_DESTROY:
			  PostQuitMessage( 0 );
			break;
		default:
			return DefWindowProc( hWnd, message, wParam, lParam );
   }
   return 0;
}


////////////////// Program startup


int APIENTRY WinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPSTR     lpCmdLine,
                     int       nCmdShow )
{
	MSG msg;
	HACCEL hAccelTable;

	LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
	LoadString(hInstance, IDC_ITMES, szWindowClass, MAX_LOADSTRING);
	MyRegisterClass(hInstance);

	if( !InitInstance( hInstance, nCmdShow ) ) 
	{
		return FALSE;
	}

	hAccelTable = LoadAccelerators(hInstance, (LPCTSTR)IDC_ITMES);

	while (GetMessage(&msg, NULL, 0, 0) ) 
	{
		if( !TranslateAccelerator (msg.hwnd, hAccelTable, &msg) ) 
		{
			TranslateMessage( &msg );
			DispatchMessage( &msg );
		}

	}
	return msg.wParam;
}


ATOM MyRegisterClass( HINSTANCE hInstance )
{
	WNDCLASSEX wcex;

	wcex.cbSize = sizeof(WNDCLASSEX); 

	wcex.style			= CS_HREDRAW | CS_VREDRAW;
	wcex.lpfnWndProc	= (WNDPROC)WndProc;
	wcex.cbClsExtra		= 0;
	wcex.cbWndExtra		= 0;
	wcex.hInstance		= hInstance;
	wcex.hIcon			= LoadIcon(hInstance, (LPCTSTR)IDI_ITMES);
	wcex.hCursor		= LoadCursor(NULL, IDC_ARROW);
	wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW+1);
	wcex.lpszMenuName	= (LPCSTR)IDC_ITMES;
	wcex.lpszClassName	= szWindowClass;
	wcex.hIconSm		= LoadIcon(wcex.hInstance, (LPCTSTR)IDI_SMALL);

	return RegisterClassEx(&wcex);
}

BOOL InitInstance( HINSTANCE hInstance, int nCmdShow )
{
   HWND hWnd;

   hInst = hInstance; 

   hWnd = CreateWindow(szWindowClass, szTitle, WS_SYSMENU|WS_MINIMIZEBOX|WS_OVERLAPPEDWINDOW,
      14, 30, 1130, 790, NULL, NULL, hInstance, NULL);

   if( !hWnd ) 
   {
      return FALSE;
   }
   ghWndMain=hWnd;
   ShowWindow( hWnd, nCmdShow );

   UpdateWindow( hWnd );
   
   return TRUE;
}

