
///////// AsTeRICS Plugin Creation Wizard


#define _CRT_SECURE_NO_WARNINGS
#include <stdlib.h>
#include <windows.h>
#include <WindowsX.h>

#include "resource.h"
#include <io.h>
#include <stdio.h>
#include <malloc.h>
#include <memory.h>


#include <string>
#include <shlobj.h>
#include <iostream>
#include <sstream>

#define MAX_LOADSTRING 256
#define MAX_BUFFER_LEN 100000
#define MAX_TEMPSTR_LEN 20000

#define MAX_PORTS 50


#define TAB 9
#define LF 10
#define CR 13


/////---------------------------  Data structures

typedef struct IPStruct
{
	char   name[100];
	char   desc[1024];
	char   type[20];
} IPStruct;

typedef struct OPStruct
{
	char   name[100];
	char   desc[1024];
	char   type[20];
} OPStruct;

typedef struct ELPStruct
{
	char   name[100];
	char   desc[1024];
} ELPStruct;

typedef struct ETPStruct
{
	char   name[100];
	char   desc[1024];
} ETPStruct;

typedef struct PROPStruct
{
	char   name[100];
	char   desc[1024];
	char   type[20];
	char   value[100];
	char   comboentries[1024];
} PROPStruct;

typedef struct SETUPStruct
{
	char   pluginname[100];
	char   plugintype[100];
	char   pluginsubcategory[100];
	char   plugindesc[1024];
	char   singleton[10];
	int    hasgui;
	char   x_size[10];
	char   y_size[10];
	char   arepath[1024];
} SETUPStruct;



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

extern char pic[30];
extern char actdate[30];
extern char path[500];
extern char buffer[MAX_BUFFER_LEN];
extern char actpath[500];
extern char sztemp[MAX_TEMPSTR_LEN];



ATOM				MyRegisterClass( HINSTANCE hInstance );
BOOL				InitInstance( HINSTANCE, int );
LRESULT CALLBACK	WndProc( HWND, UINT, WPARAM, LPARAM );
LRESULT CALLBACK	About( HWND, UINT, WPARAM, LPARAM );
LRESULT CALLBACK    ITEMDlghandler( HWND , UINT, WPARAM , LPARAM );
INT_PTR CALLBACK	BROWSEDlghandler( HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam );

