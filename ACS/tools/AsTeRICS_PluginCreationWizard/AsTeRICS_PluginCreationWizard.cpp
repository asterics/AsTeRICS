
#include "AsTeRICS_PluginCreationWizard.h"
 
///////// AsTeRICS Plugin Creation Wizard


////////////////// Program startup

int APIENTRY WinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPSTR     lpCmdLine,
                     int       nCmdShow )
{
	int z;

	LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);

	GetModuleFileName(NULL,path,sizeof(path));
	for (z=strlen(path);(z>0)&&path[z]!='\\';z--) ;
	path[z+1]=0;
	GetSystemTime(&st);              // gets current time
	strcpy(pic,"d.M.yyyy");
	GetDateFormat(NULL, 0, &st, pic , actdate, sizeof(sztemp));

	DialogBox(
        hInstance,
        MAKEINTRESOURCE(IDD_BROWSERDIALOG),
        GetDesktopWindow(),
        BROWSEDlghandler);

	return 0;
}
