#include "stdafx.h"
#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include <conio.h>


int main ()
{
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	
	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );
	CreateProcess(TEXT("start.bat"),NULL,NULL,NULL,FALSE,CREATE_NO_WINDOW,NULL,NULL,&si,&pi);
}

