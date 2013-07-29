
///////// AsTeRICS Plugin Creation Wizard


#define _CRT_SECURE_NO_WARNINGS
#include <stdlib.h>
#include <windows.h>
#include "resource.h"
#include <io.h>
#include <stdio.h>
#include <malloc.h>
#include <memory.h>


#define MAX_LOADSTRING 256
#define MAX_BUFFER_LEN 100000
#define MAX_TEMPSTR_LEN 20000

#define TAB 9
#define LF 10
#define CR 13



///////////---------------------        global variables

extern HWND ghWndMain;
extern HWND ghWndBrowser;

extern HANDLE rfile;
extern HANDLE wfile;

extern HINSTANCE hInst;
extern TCHAR szTitle[MAX_LOADSTRING];
extern TCHAR szWindowClass[MAX_LOADSTRING];    
//extern HBITMAP g_hbmBG2;
extern HBITMAP g_hbmBG2;
extern SYSTEMTIME st;



ATOM				MyRegisterClass( HINSTANCE hInstance );
BOOL				InitInstance( HINSTANCE, int );
LRESULT CALLBACK	WndProc( HWND, UINT, WPARAM, LPARAM );
LRESULT CALLBACK	About( HWND, UINT, WPARAM, LPARAM );
LRESULT CALLBACK    ITEMDlghandler( HWND , UINT, WPARAM , LPARAM );
LRESULT CALLBACK	BROWSEDlghandler( HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam );

