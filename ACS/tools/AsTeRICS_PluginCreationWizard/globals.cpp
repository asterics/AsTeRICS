#include "AsTeRICS_PluginCreationWizard.h"

///////// AsTeRICS Plugin Creation Wizard

SYSTEMTIME st;
char pic[30];
char actdate[30];

int actfile;
char szfilename[500];
char sztemp[MAX_TEMPSTR_LEN];
char buffer[MAX_BUFFER_LEN];
char * actpos;
char path[500];
char actpath[500];


DWORD dwread,dwwritten;

HWND ghWndMain;
HWND ghWndBrowser=0;
HANDLE rfile;
HANDLE wfile;
HINSTANCE hInst;
TCHAR szTitle[MAX_LOADSTRING];
TCHAR szWindowClass[MAX_LOADSTRING];    

char * strup ( char * str)
{
	char * sav;

	if (!str) return (NULL);

	sav=str;
	while (*sav)
	{
		if ((*sav>='a')&&(*sav<='z')) *sav=*sav-('a'-'A');
		if (*sav=='ö') *sav='Ö';
		if (*sav=='ä') *sav='Ä';
		if (*sav=='ü') *sav='Ü';
		sav++;
	}
	return str;
}
/*
void sort_artikel (void)
{
	int i,t;
	ARTIKELStruct sav;

	for (i=0; i<num_artikel; i++)
	{
		for (t=i+1;t<num_artikel; t++)
		{
			if (compare(artikel[i].artikel,artikel[t].artikel)==1)
			{
				memcpy(&sav,&(artikel[i]),sizeof(ARTIKELStruct));
				memcpy(&(artikel[i]),&(artikel[t]),sizeof(ARTIKELStruct));
				memcpy(&(artikel[t]),&sav,sizeof(ARTIKELStruct));
			}
		}
	}
}
*/
