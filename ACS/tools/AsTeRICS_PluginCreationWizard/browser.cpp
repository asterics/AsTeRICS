#include "AsTeRICS_PluginCreationWizard.h"

///////// AsTeRICS Plugin Creation Wizard


DWORD dwRead;
DWORD dwWritten;

IPStruct iplist[MAX_PORTS];
OPStruct oplist[MAX_PORTS];
ELPStruct elplist[MAX_PORTS];
ETPStruct etplist[MAX_PORTS];
PROPStruct proplist[MAX_PORTS];

SETUPStruct setup;

int ip_count=0,act_ip=-1;
int op_count=0,act_op=-1;
int elp_count=0,act_elp=-1;
int etp_count=0,act_etp=-1;
int prop_count=0,act_prop=-1;


void SD_OnSize(HWND hwnd, UINT state, int cx, int cy);
void SD_OnHScroll(HWND hwnd, HWND hwndCtl, UINT code, int pos);
void SD_OnVScroll(HWND hwnd, HWND hwndCtl, UINT code, int pos);
void SD_OnHVScroll(HWND hwnd, int bar, UINT code);
void SD_ScrollClient(HWND hwnd, int bar, int pos);
int SD_GetScrollPos(HWND hwnd, int bar, UINT code);


int read_file(char * templatefilepath, char* filename)
{
		char filepath[1024];

		strcpy(filepath,templatefilepath);
//		strcat(filepath,"templates\\");
		strcat(filepath,filename);

		rfile= CreateFile(filepath, GENERIC_READ, 0, NULL, OPEN_EXISTING, 0, NULL);
		if (rfile==INVALID_HANDLE_VALUE) 
		{   MessageBox(NULL, filepath, "Error opening template file", MB_OK);
			return FALSE;
		}
		ReadFile(rfile,buffer,sizeof(buffer), &dwRead,NULL);
		buffer[dwRead]=0;

		CloseHandle(rfile);
		return TRUE;
}
int replace_once ( char* oldstr, char* newstr)
{
	char * tmp,*sav;

	tmp = strstr(buffer,oldstr);
	sav=tmp;
	if (tmp) {
		memmove(tmp+strlen(newstr),tmp+strlen(oldstr),(buffer-tmp)+strlen(buffer)-strlen(oldstr)+1);
		while (*newstr) *tmp++=*newstr++;
	}
	return(true);
}

int write_file(char * targetfilepath,char * filename)
{
		char filepath[1024];

		strcpy(filepath,targetfilepath);
		strcat(filepath,"\\");
		strcat(filepath,filename);
		wfile= CreateFile(filepath, GENERIC_ALL, 0, NULL, CREATE_NEW, 0, NULL);
		if (wfile==INVALID_HANDLE_VALUE) 
		{   MessageBox(NULL, targetfilepath, "Error creating  file", MB_OK);
			return FALSE;
		}

		int l=strlen(buffer);
		for (int i=0; i<l;i++) {
			if (buffer[i] & (1<<7))
			{
				memmove (buffer+i+1,buffer+i,l+2-i);
				if (buffer[i] & (1<<6))
				{
					buffer[i] = (char)0xc3;
					buffer[i+1] &= ~(1<<6);
				} 
				else buffer[i] = (char)0xc2;
				i++; l++;
			}
		}

		WriteFile(wfile,buffer,strlen(buffer),&dwWritten,NULL);
		CloseHandle(wfile);
		return TRUE;
}

int check_name(char * name)
{
	while (*name)
	{
		if ( ((*name>='a') && (*name<='z')) ||
			((*name>='A') && (*name<='Z')) ||
			((*name>='0') && (*name<='9')) )
			name++;
		else return(false);
	}
	return (true);
}

int check_pluginname(char * name)
{
	if ((*name<'A') || (*name>'Z')) return(false);

	while (*name)
	{
		if ( ((*name>='a') && (*name<='z')) ||
			((*name>='A') && (*name<='Z')) ||
			((*name>='0') && (*name<='9')) )
			name++;
		else return(false);
	}
	return (true);
}

void uppercase(char * name)
{
	if ((*name>='a') && (*name<='z'))
	*name=*name-'a'+'A';
}
void lowercase(char * name)
{
	if ((*name>='A') && (*name<='Z'))
	*name=*name-'A'+'a';
}

void convert_to_lowercase(char * name)
{
	while (*name)
	{
		if ((*name>='A') && (*name<='Z'))
		*name=*name-'A'+'a';
		name++;
	}
}



static int CALLBACK BrowseCallbackProc(HWND hwnd,UINT uMsg, LPARAM lParam, LPARAM lpData)
{

    if(uMsg == BFFM_INITIALIZED)
    {
        std::string tmp = (const char *) lpData;
        std::cout << "path: " << tmp << std::endl;
        SendMessage(hwnd, BFFM_SETSELECTION, TRUE, lpData);
    }

    return 0;
}

std::string BrowseFolder(std::string saved_path)
{
    TCHAR path[MAX_PATH];

    const char * path_param = saved_path.c_str();

    BROWSEINFO bi = { 0 };
    bi.lpszTitle  = ("Browse for folder...");
    bi.ulFlags    = BIF_RETURNONLYFSDIRS | BIF_NEWDIALOGSTYLE;
    bi.lpfn       = BrowseCallbackProc;
    bi.lParam     = (LPARAM) path_param;

    LPITEMIDLIST pidl = SHBrowseForFolder ( &bi );

    if ( pidl != 0 )
    {
        //get the name of the folder and put it in path
        SHGetPathFromIDList ( pidl, path );

        //free memory used
        IMalloc * imalloc = 0;
        if ( SUCCEEDED( SHGetMalloc ( &imalloc )) )
        {
            imalloc->Free ( pidl );
            imalloc->Release ( );
        }

        return path;
    }

    return "";
}



INT_PTR CALLBACK BROWSEDlghandler( HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam )
{
	int i;
	int exists;
	char tmpstr[1024];
	HWND hwnd=hDlg;

	switch( message )
	{
		HANDLE_MSG(hwnd, WM_SIZE, SD_OnSize);
		HANDLE_MSG(hwnd, WM_HSCROLL, SD_OnHScroll);
		HANDLE_MSG(hwnd, WM_VSCROLL, SD_OnVScroll);

		case WM_INITDIALOG:
			{
					RECT rc = {};
					GetClientRect(hwnd, &rc);

					const SIZE sz = { rc.right - rc.left, rc.bottom - rc.top };

					SCROLLINFO si = {};
					si.cbSize = sizeof(SCROLLINFO);
					si.fMask = SIF_PAGE | SIF_POS | SIF_RANGE;
					si.nPos = si.nMin = 1;

					si.nMax = sz.cx;
					si.nPage = sz.cx;
					SetScrollInfo(hwnd, SB_HORZ, &si, FALSE);

					si.nMax = sz.cy;
					si.nPage = sz.cy;
					SetScrollInfo(hwnd, SB_VERT, &si, FALSE);

			    ghWndBrowser=hDlg;
				SendDlgItemMessage( hDlg, IDC_PLUGINTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "sensor" ) ;
				SendDlgItemMessage( hDlg, IDC_PLUGINTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "processor" ) ;
				SendDlgItemMessage( hDlg, IDC_PLUGINTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "actuator" ) ;
				SendDlgItemMessage( hDlg, IDC_PLUGINTYPECOMBO, CB_SETCURSEL, 1,0L ) ;

				SendDlgItemMessage( hDlg, IDC_IPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "integer" ) ;
				SendDlgItemMessage( hDlg, IDC_IPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "double" ) ;
				SendDlgItemMessage( hDlg, IDC_IPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "string" ) ;
				SendDlgItemMessage( hDlg, IDC_IPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "boolean" ) ;
				SendDlgItemMessage( hDlg, IDC_IPTYPECOMBO, CB_SETCURSEL, 1,0L ) ;

				SendDlgItemMessage( hDlg, IDC_OPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "integer" ) ;
				SendDlgItemMessage( hDlg, IDC_OPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "double" ) ;
				SendDlgItemMessage( hDlg, IDC_OPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "string" ) ;
				SendDlgItemMessage( hDlg, IDC_OPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "boolean" ) ;
				SendDlgItemMessage( hDlg, IDC_OPTYPECOMBO, CB_SETCURSEL, 1,0L ) ;

				SendDlgItemMessage( hDlg, IDC_PROPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "integer" ) ;
				SendDlgItemMessage( hDlg, IDC_PROPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "double" ) ;
				SendDlgItemMessage( hDlg, IDC_PROPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "string" ) ;
				SendDlgItemMessage( hDlg, IDC_PROPTYPECOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) "boolean" ) ;
				SendDlgItemMessage( hDlg, IDC_PROPTYPECOMBO, CB_SETCURSEL, 0,0L ) ;

				SetDlgItemText(hDlg,IDC_PLUGINNAME,"MyPlugin");
				SetDlgItemText(hDlg,IDC_SUBCATEGORY,"Others");
				SetDlgItemText(hDlg,IDC_IPNAME,"myInPort");
				SetDlgItemText(hDlg,IDC_IPDESC,"input port description");
				SetDlgItemText(hDlg,IDC_OPNAME,"myOutPort");
				SetDlgItemText(hDlg,IDC_OPDESC,"output port description");
				SetDlgItemText(hDlg,IDC_ELPNAME,"myElpPort");
				SetDlgItemText(hDlg,IDC_ELPDESC,"elp description");
				SetDlgItemText(hDlg,IDC_ETPNAME,"myEtpPort");
				SetDlgItemText(hDlg,IDC_ETPDESC,"etp description");
				SetDlgItemText(hDlg,IDC_PROPNAME,"myProperty");
				SetDlgItemText(hDlg,IDC_PROPDESC,"property description");

				SetDlgItemText(hDlg,IDC_PLUGINDESC,"My Plugin description");
				CheckDlgButton(hDlg,IDC_SINGLETON,false);
				CheckDlgButton(hDlg,IDC_HASGUI,false);

				setup.hasgui=false;

				SetDlgItemText(hDlg,IDC_GUI_XSIZE,"30");
				SetDlgItemText(hDlg,IDC_GUI_YSIZE,"20");

				SetDlgItemText(hDlg,IDC_AREPATH,"\\AsTeRICS\\ARE\\components\\");
      			return TRUE;
			}
		case WM_CLOSE:
				EndDialog(hDlg,LOWORD(wParam));
				return TRUE;

		case WM_COMMAND:
			switch (LOWORD(wParam)) {
				case IDC_CHOOSEDIR:
					 
					 SetDlgItemText(hDlg,IDC_AREPATH,BrowseFolder("").c_str());

					break;
			    case IDC_IPLIST:
					if (HIWORD(wParam)==LBN_SELCHANGE)
					{
						act_ip=SendDlgItemMessage(hDlg,IDC_IPLIST, LB_GETCURSEL, 0, 0L ) ;
						SetDlgItemText(hDlg,IDC_IPNAME,iplist[act_ip].name);
						SetDlgItemText(hDlg,IDC_IPDESC,iplist[act_ip].desc);
						SetDlgItemText(hDlg,IDC_IPTYPECOMBO,iplist[act_ip].type);
					}
					break;
				case IDC_ADD_IP:
						GetDlgItemText(hDlg, IDC_IPNAME, sztemp, sizeof(sztemp));
						if (!check_name(sztemp))
						{ 
							MessageBox(NULL,"Please use only Letters and numbers for the Portname, e.g: inValue3","Error", MB_OK); 
						    break; 
						}
						exists=0;
						for (i=0;i<ip_count;i++)
							if (!strcmp(iplist[i].name,sztemp)) exists=1;
						if (exists)
						{	MessageBox(NULL,"This Port name already exists","Error", MB_OK); break; }

						strcpy (iplist[ip_count].name,sztemp);
						GetDlgItemText(hDlg, IDC_IPTYPECOMBO, iplist[ip_count].type, sizeof(iplist[ip_count].type));
						GetDlgItemText(hDlg, IDC_IPDESC, iplist[ip_count].desc, sizeof(iplist[ip_count].desc));

						strcat(sztemp," (");
						strcat(sztemp,iplist[ip_count].type);
						strcat(sztemp,")");
						SendDlgItemMessage(hDlg,IDC_IPLIST, LB_ADDSTRING, 0, (LPARAM) sztemp);
						SendDlgItemMessage(hDlg,IDC_IPLIST, LB_SETCURSEL, (WPARAM) (ip_count), 0L ) ;
						act_ip=ip_count;
						ip_count++;
						break;
				case IDC_DEL_IP:
						if ((ip_count>0)&&(act_ip!=-1))
						{
							memmove(&(iplist[act_ip]),&(iplist[act_ip+1]),sizeof(iplist[0])*(ip_count-act_ip));
							//SetDlgItemText(hDlg,IDC_IPNAME,"");
							//SetDlgItemText(hDlg,IDC_IPDESC,"");
							//SetDlgItemText(hDlg,IDC_IPTYPECOMBO,"");
							SendDlgItemMessage(hDlg,IDC_IPLIST, LB_DELETESTRING, act_ip, 0);
							act_ip=-1;
							ip_count--;
						}
						break;

			    case IDC_OPLIST:
					if (HIWORD(wParam)==LBN_SELCHANGE)
					{
						act_op=SendDlgItemMessage(hDlg,IDC_OPLIST, LB_GETCURSEL, 0, 0L ) ;
						SetDlgItemText(hDlg,IDC_OPNAME,oplist[act_op].name);
						SetDlgItemText(hDlg,IDC_OPDESC,oplist[act_op].desc);
						SetDlgItemText(hDlg,IDC_OPTYPECOMBO,oplist[act_op].type);
					}
					break;
				case IDC_ADD_OP:
						GetDlgItemText(hDlg, IDC_OPNAME, sztemp, sizeof(sztemp));
						if (!check_name(sztemp))
						{ 
							MessageBox(NULL,"Please use only Letters and numbers for the Portname, e.g: outValue3","Error", MB_OK); 
						    break; 
						}
						exists=0;
						for (i=0;i<op_count;i++)
							if (!strcmp(oplist[i].name,sztemp)) exists=1;
						if (exists)
							{	MessageBox(NULL,"This Port name already exists","Error", MB_OK); break; }

						strcpy (oplist[op_count].name,sztemp);
						GetDlgItemText(hDlg, IDC_OPTYPECOMBO, oplist[op_count].type, sizeof(oplist[op_count].type));
						GetDlgItemText(hDlg, IDC_OPDESC, oplist[op_count].desc, sizeof(oplist[op_count].desc));

						strcat(sztemp," (");
						strcat(sztemp,oplist[op_count].type);
						strcat(sztemp,")");
						SendDlgItemMessage(hDlg,IDC_OPLIST, LB_ADDSTRING, 0, (LPARAM) sztemp);
						SendDlgItemMessage(hDlg,IDC_OPLIST, LB_SETCURSEL, (WPARAM) (op_count), 0L ) ;
						act_op=op_count;
						op_count++;
						break;
				case IDC_DEL_OP:
						if ((op_count>0)&&(act_op!=-1))
						{
							memmove(&(oplist[act_op]),&(oplist[act_op+1]),sizeof(oplist[0])*(op_count-act_op));
							//SetDlgItemText(hDlg,IDC_OPNAME,"");
							//SetDlgItemText(hDlg,IDC_OPDESC,"");
							//SetDlgItemText(hDlg,IDC_OPTYPECOMBO,"");
							SendDlgItemMessage(hDlg,IDC_OPLIST, LB_DELETESTRING, act_op, 0);
							act_op=-1;
							op_count--;
						}
						break;

			    case IDC_ELPLIST:
					if (HIWORD(wParam)==LBN_SELCHANGE)
					{
						act_elp=SendDlgItemMessage(hDlg,IDC_ELPLIST, LB_GETCURSEL, 0, 0L ) ;
						SetDlgItemText(hDlg,IDC_ELPNAME,elplist[act_elp].name);
						SetDlgItemText(hDlg,IDC_ELPDESC,elplist[act_elp].desc);
					}
					break;
				case IDC_ADD_ELP:
						GetDlgItemText(hDlg, IDC_ELPNAME, sztemp, sizeof(sztemp));
						if (check_name(sztemp))
						{
							exists=0;
							for (i=0;i<elp_count;i++)
								if (!strcmp(elplist[i].name,sztemp)) exists=1;

							if (!exists)
							{
								strcpy (elplist[elp_count].name,sztemp);
								GetDlgItemText(hDlg, IDC_ELPDESC, elplist[elp_count].desc, sizeof(elplist[elp_count].desc));
								SendDlgItemMessage(hDlg,IDC_ELPLIST, LB_ADDSTRING, 0, (LPARAM) sztemp);
								SendDlgItemMessage(hDlg,IDC_ELPLIST, LB_SETCURSEL, (WPARAM) (elp_count), 0L ) ;
								act_elp=elp_count;
								elp_count++;
							}
							else MessageBox(NULL,"This Port name already exists","Error", MB_OK);
						}
						else MessageBox(NULL,"Please use only Letters and numbers for the Portname, e.g: leftClick","Error", MB_OK);
						break;
				case IDC_DEL_ELP:
						if ((elp_count>0)&&(act_elp!=-1))
						{
							memmove(&(elplist[act_elp]),&(elplist[act_elp+1]),sizeof(elplist[0])*(elp_count-act_elp));
							//SetDlgItemText(hDlg,IDC_ELPNAME,"");
							//SetDlgItemText(hDlg,IDC_ELPDESC,"");
							SendDlgItemMessage(hDlg,IDC_ELPLIST, LB_DELETESTRING, act_elp, 0);
							act_elp=-1;
							elp_count--;
						}
						break;

			    case IDC_ETPLIST:
					if (HIWORD(wParam)==LBN_SELCHANGE)
					{
						act_etp=SendDlgItemMessage(hDlg,IDC_ETPLIST, LB_GETCURSEL, 0, 0L ) ;
						SetDlgItemText(hDlg,IDC_ETPNAME,etplist[act_etp].name);
						SetDlgItemText(hDlg,IDC_ETPDESC,etplist[act_etp].desc);
					}
					break;
				case IDC_ADD_ETP:
						GetDlgItemText(hDlg, IDC_ETPNAME, sztemp, sizeof(sztemp));
						if (check_name(sztemp))
						{
							exists=0;
							for (i=0;i<etp_count;i++)
								if (!strcmp(etplist[i].name,sztemp)) exists=1;

							if (!exists)
							{
								strcpy (etplist[etp_count].name,sztemp);
								GetDlgItemText(hDlg, IDC_ETPDESC, etplist[etp_count].desc, sizeof(etplist[etp_count].desc));
								SendDlgItemMessage(hDlg,IDC_ETPLIST, LB_ADDSTRING, 0, (LPARAM) sztemp);
								SendDlgItemMessage(hDlg,IDC_ETPLIST, LB_SETCURSEL, (WPARAM) (etp_count), 0L ) ;
								act_etp=etp_count;
								etp_count++;
							}
							else MessageBox(NULL,"This Port name already exists","Error", MB_OK);
						}
						else MessageBox(NULL,"Please use only Letters and numbers for the Portname, e.g: levelReached","Error", MB_OK);
						break;
				case IDC_DEL_ETP:
						if ((etp_count>0)&&(act_etp!=-1))
						{
							memmove(&(etplist[act_etp]),&(etplist[act_etp+1]),sizeof(etplist[0])*(etp_count-act_etp));
							//SetDlgItemText(hDlg,IDC_ETPNAME,"");
							//SetDlgItemText(hDlg,IDC_ETPDESC,"");
							SendDlgItemMessage(hDlg,IDC_ETPLIST, LB_DELETESTRING, act_etp, 0);
							act_etp=-1;
							etp_count--;
						}
						break;
	
			    case IDC_PROPLIST:
					if (HIWORD(wParam)==LBN_SELCHANGE)
					{
						act_prop=SendDlgItemMessage(hDlg,IDC_PROPLIST, LB_GETCURSEL, 0, 0L ) ;
						SetDlgItemText(hDlg,IDC_PROPNAME,proplist[act_prop].name);
						SetDlgItemText(hDlg,IDC_PROPDESC,proplist[act_prop].desc);
						SetDlgItemText(hDlg,IDC_PROPTYPECOMBO,proplist[act_prop].type);
						SetDlgItemText(hDlg,IDC_PROPVALUE,proplist[act_prop].value);
						SetDlgItemText(hDlg,IDC_PROPCOMBO_ENTRIES,proplist[act_prop].comboentries);
					}
					break;
				case IDC_ADD_PROP:
						GetDlgItemText(hDlg, IDC_PROPNAME, sztemp, sizeof(sztemp));
						if (!check_name(sztemp))
						{ 
							MessageBox(NULL,"Please use only Letters and numbers for the property names, e.g: inputGain","Error", MB_OK); 
						    break; 
						}
						exists=0;
						for (i=0;i<prop_count;i++)
							if (!strcmp(proplist[i].name,sztemp)) exists=1;
						if (exists)
							{	MessageBox(NULL,"This property name already exists","Error", MB_OK); break; }

						strcpy (proplist[prop_count].name,sztemp);
						GetDlgItemText(hDlg, IDC_PROPCOMBO_ENTRIES, proplist[prop_count].comboentries, sizeof(proplist[prop_count].comboentries));
						GetDlgItemText(hDlg, IDC_PROPTYPECOMBO, proplist[prop_count].type, sizeof(proplist[prop_count].type));
						if ((strcmp(proplist[prop_count].type,"integer") && (strlen(proplist[prop_count].comboentries)!=0)))
							{	MessageBox(NULL,"Combobox entries are only possible for properties with type integer.","Error", MB_OK); break; }

						GetDlgItemText(hDlg, IDC_PROPVALUE, proplist[prop_count].value, sizeof(proplist[prop_count].value));
						if (strlen(proplist[prop_count].value)==0)
							{	MessageBox(NULL,"Default value for property needed.","Error", MB_OK); break; }

						if ((!strcmp(proplist[prop_count].type,"boolean")) && ((strcmp(proplist[prop_count].value,"true"))&&((strcmp(proplist[prop_count].value,"false")))))
							{	MessageBox(NULL,"Property Value for boolean type needs to be true or false.","Error", MB_OK); break; }


						GetDlgItemText(hDlg, IDC_PROPDESC, proplist[prop_count].desc, sizeof(proplist[prop_count].desc));

						strcat(sztemp," (");
						strcat(sztemp,proplist[prop_count].type);
						strcat(sztemp,")");
						SendDlgItemMessage(hDlg,IDC_PROPLIST, LB_ADDSTRING, 0, (LPARAM) sztemp);
						SendDlgItemMessage(hDlg,IDC_PROPLIST, LB_SETCURSEL, (WPARAM) (prop_count), 0L ) ;
						act_prop=prop_count;
						prop_count++;
						break;
				case IDC_DEL_PROP:
						if ((prop_count>0)&&(act_prop!=-1))
						{
							memmove(&(proplist[act_prop]),&(proplist[act_prop+1]),sizeof(proplist[0])*(prop_count-act_prop));
							//SetDlgItemText(hDlg,IDC_PROPNAME,"");
							//SetDlgItemText(hDlg,IDC_PROPDESC,"");
							//SetDlgItemText(hDlg,IDC_PROPTYPECOMBO,"");
							//SetDlgItemText(hDlg,IDC_PROPVALUE,"");
							//SetDlgItemText(hDlg,IDC_PROPCOMBO_ENTRIES,"");
							SendDlgItemMessage(hDlg,IDC_PROPLIST, LB_DELETESTRING, act_prop, 0);
							act_prop=-1;
							prop_count--;
						}
						break;
				case IDC_HASGUI:
						   setup.hasgui=IsDlgButtonChecked(hDlg,IDC_HASGUI);
						   EnableWindow(GetDlgItem(hDlg, IDC_GUI_XSIZE), setup.hasgui);
						   EnableWindow(GetDlgItem(hDlg, IDC_GUI_YSIZE), setup.hasgui);
						break;
				case IDC_CREATE_PLUGIN:
					{
						char actdir[1024];
						char tmpdir[1024];
						char tmpstr[1024];

						buffer[0]=0;
						GetDlgItemText(hDlg, IDC_PLUGINNAME, setup.pluginname, sizeof(setup.pluginname));

						if (!check_pluginname(setup.pluginname))
						{ 
							MessageBox(NULL,"Please use only CamelCase letters and numbers for the PluginName, e.g: MyNewSensorPlugin","Error", MB_OK); 
						    break; 
						}

						GetDlgItemText(hDlg, IDC_PLUGINTYPECOMBO, setup.plugintype,sizeof(setup.plugintype));
						GetDlgItemText(hDlg, IDC_SUBCATEGORY, setup.pluginsubcategory,sizeof(setup.pluginsubcategory));
						GetDlgItemText(hDlg, IDC_AREPATH, setup.arepath,sizeof(setup.arepath));
						GetDlgItemText(hDlg, IDC_PLUGINDESC, setup.plugindesc,sizeof(setup.plugindesc));
						GetDlgItemText(hDlg, IDC_GUI_XSIZE,setup.x_size,sizeof(setup.x_size));
						GetDlgItemText(hDlg, IDC_GUI_YSIZE,setup.y_size,sizeof(setup.y_size));


						if (IsDlgButtonChecked(hDlg,IDC_SINGLETON)) strcpy(setup.singleton,"true"); else strcpy(setup.singleton,"false");

						if (strlen(setup.arepath)<3) 
						{
							MessageBox(NULL,"Please specify an absolute path to the ARE components folder, e.g. \\AsTeRICS\\ARE\\components\\","Error", MB_OK);
							break;
						}
						if (setup.arepath[strlen(setup.arepath)-1]!='\\') strcat(setup.arepath,"\\");

						strcpy(actdir,setup.arepath);
						strcpy (tmpstr,setup.plugintype);
						strcat(tmpstr,".");
						strcat(tmpstr,setup.pluginname);
						convert_to_lowercase (tmpstr);
						strcat(actdir,tmpstr);
						if (!CreateDirectory(actdir,NULL)) {
							if (GetLastError() == ERROR_ALREADY_EXISTS) 
								MessageBox(NULL,"The Plugin Directory already exisits - Please specify another name or delete the directory","Plugin already exists", MB_OK); 
							else MessageBox(NULL,"Please specify an absolute path to the ARE components folder, e.g. C:\\AsTeRICS\\ARE\\components\\ (Windows) or /home/user/AsTeRICS/ARE/components (Linux)","Could not create directory", MB_OK);
							break; 
						}
						// here goes the build script
						// @type: actuator
						// @name: bardisplay


						if (!read_file(path,"templates\\template_build.xml")) break;
						replace_once ("@type",setup.plugintype);
						replace_once ("@name",setup.pluginname);
						write_file(actdir,"build.xml");

						strcpy(tmpdir,actdir);
						strcat(actdir,"\\LICENSE");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }

						if (!read_file(path,"templates\\LICENSE\\LICENSE_MITOrGPLv3WithException.txt")) break;
						write_file(actdir,"LICENSE_MITOrGPLv3WithException.txt");
						if (!read_file(path,"templates\\LICENSE\\README.md")) break;
						write_file(actdir,"README.md");
						if (!read_file(path,"templates\\LICENSE\\THIRDPARTY_LibraryName_LicenseNameInclVersionInfo.txt")) break;
						write_file(actdir,"THIRDPARTY_LibraryName_LicenseNameInclVersionInfo.txt");

						strcpy (actdir,tmpdir);
						strcat(actdir,"\\src");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\main");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }

						strcpy(tmpdir,actdir);  // save the main folder loation

						strcat(actdir,"\\resources");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						// here goes the bundle descriptor

						if (!read_file(path,"templates\\template_bundle_descriptor.xml")) break;
						// @pluginid: asterics.BarDisplay
						strcpy(tmpstr,"asterics.");strcat(tmpstr,setup.pluginname);
						replace_once ("@pluginid",tmpstr);
						// @canonicalname: eu.asterics.component.actuator.bardisplay.BarDisplayInstance
						strcpy(tmpstr,"eu.asterics.component.");strcat(tmpstr,setup.plugintype);strcat(tmpstr,".");strcat(tmpstr,setup.pluginname);
						convert_to_lowercase(tmpstr);
						strcat(tmpstr,".");strcat(tmpstr,setup.pluginname);strcat(tmpstr,"Instance");
						replace_once ("@canonicalname",tmpstr);
						// @subcategory: Standard Input Devices
						replace_once ("@subcategory",setup.pluginsubcategory);
						// @type: actuator
						replace_once ("@type",setup.plugintype);
						// @singleton: false
						replace_once ("@singleton",setup.singleton);
						// @description: Bar-display
						replace_once ("@description",setup.plugindesc);

						// @inputports
						for (i=0;i<ip_count;i++)
						{
							replace_once("@inputports",
						    "\t\t\t<inputPort id=\"@ipname\">\n"
							"\t\t\t\t<description>@ipdesc</description>\n"
							"\t\t\t\t<mustBeConnected>false</mustBeConnected>\n"
							"\t\t\t\t<dataType>@iptype</dataType>\n"
							"\t\t\t</inputPort>\n"
							"@inputports");

							replace_once("@ipname",iplist[i].name);
							replace_once("@ipdesc",iplist[i].desc);
							replace_once("@iptype",iplist[i].type);

						}
						replace_once("@inputports","");

						// @outputports
						for (i=0;i<op_count;i++)
						{
							replace_once("@outputports",
						    "\t\t\t<outputPort id=\"@opname\">\n"
							"\t\t\t\t<description>@opdesc</description>\n"
							"\t\t\t\t<dataType>@optype</dataType>\n"
							"\t\t\t</outputPort>\n"
							"@outputports");

							replace_once("@opname",oplist[i].name);
							replace_once("@opdesc",oplist[i].desc);
							replace_once("@optype",oplist[i].type);

						}
						replace_once("@outputports","");

						// @elports
						for (i=0;i<elp_count;i++)
						{
							replace_once("@elports",
						    "\t\t\t<eventListenerPort id=\"@elpname\">\n"
							"\t\t\t\t<description>@elpdesc</description>\n"
							"\t\t\t</eventListenerPort>\n"
							"@elports");

							replace_once("@elpname",elplist[i].name);
							replace_once("@elpdesc",elplist[i].desc);
						}
						replace_once("@elports","");

						// @etports
						for (i=0;i<etp_count;i++)
						{
							replace_once("@etports",
						    "\t\t\t<eventTriggererPort id=\"@etpname\">\n"
							"\t\t\t\t<description>@etpdesc</description>\n"
							"\t\t\t</eventTriggererPort>\n"
							"@etports");

							replace_once("@etpname",etplist[i].name);
							replace_once("@etpdesc",etplist[i].desc);
						}
						replace_once("@etports","");

						//	@properties						   
						for (i=0;i<prop_count;i++)
						{
							if (strlen(proplist[i].comboentries)>0) 
							{
								replace_once("@properties",
								"\t\t\t<property name=\"@propname\"\n"
								"\t\t\t\ttype=\"@proptype\"\n"
								"\t\t\t\tvalue=\"@propvalue\"\n"
								"\t\t\t\tcombobox=\"@propcombo\"\n"  // "black//blue//cyan//darkgray..."
								"\t\t\t\tdescription=\"@propdesc\"/>\n"
								"@properties");

								replace_once("@propname",proplist[i].name);
								replace_once("@proptype",proplist[i].type);
								replace_once("@propvalue",proplist[i].value);
								replace_once("@propcombo",proplist[i].comboentries);
								replace_once("@propdesc",proplist[i].desc);
							} 
							else {
								replace_once("@properties",
								"\t\t\t<property name=\"@propname\"\n"
								"\t\t\t\ttype=\"@proptype\"\n"
								"\t\t\t\tvalue=\"@propvalue\"\n"
								"\t\t\t\tdescription=\"@propdesc\"/>\n"
								"@properties");

								replace_once("@propname",proplist[i].name);
								replace_once("@proptype",proplist[i].type);
								replace_once("@propvalue",proplist[i].value);
								replace_once("@propdesc",proplist[i].desc);
							}
						}
						replace_once("@properties","");

						// <gui>
						//   <width>40</width>
						//   <height>10</height>
						// </gui> 
						strcpy(sztemp,"");
						if (setup.hasgui) 
						{
							strcpy(sztemp,"\t\t<gui>\n"
								"\t\t\t<width>");
							strcat(sztemp,setup.x_size);
							strcat(sztemp,"</width>\n"
								"\t\t\t<height>");
							strcat(sztemp,setup.y_size);
							strcat(sztemp,"</height>\n"
								"\t\t</gui>\n");
						}
						replace_once("@gui",sztemp);

						write_file(actdir,"bundle_descriptor.xml");

						strcat(actdir,"\\META-INF");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						// here goes the manifest file
						// @bundlename: asterics-actuators.bardisplay
						// @symbolicname: eu.asterics.component.actuator.bardisplay
						if (!read_file(path,"templates\\template_MANIFEST.MF")) break;
						strcpy(tmpstr,"asterics-");strcat(tmpstr,setup.plugintype);strcat(tmpstr,"s.");strcat(tmpstr,setup.pluginname);
						convert_to_lowercase(tmpstr);
						replace_once ("@bundlename",tmpstr);
						strcpy(tmpstr,"eu.asterics.component.");strcat(tmpstr,setup.plugintype);strcat(tmpstr,".");strcat(tmpstr,setup.pluginname);
						convert_to_lowercase(tmpstr);
						replace_once ("@symbolicname",tmpstr);
						write_file(actdir,"MANIFEST.MF");

						strcpy(actdir,tmpdir);  // continue at main folder

						strcat(actdir,"\\java");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\eu");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\asterics");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\component");
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\");
						strcat(actdir,setup.plugintype);
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						strcat(actdir,"\\");
						strcat(actdir,setup.pluginname);
						convert_to_lowercase(actdir);
						if (!CreateDirectory(actdir,NULL)) { MessageBox(NULL,actdir,"Problem creating folder", MB_OK); break; }
						// here go the java source files

						if(!read_file(path,"templates\\template_javaInstance.java")) break;
						// @symbolicname:eu.asterics.component.actuator.bardisplay
						strcpy(tmpstr,"eu.asterics.component.");strcat(tmpstr,setup.plugintype);strcat(tmpstr,".");strcat(tmpstr,setup.pluginname);
						convert_to_lowercase(tmpstr);
						replace_once ("@symbolicname",tmpstr);
						// @instancename: BardisplayInstance
						strcpy(tmpstr,setup.pluginname);strcat(tmpstr,"Instance");
						replace_once ("@instancename",tmpstr);
						replace_once ("@instancename",tmpstr);  // for class constructor
						//   @defaultops:  final IRuntimeOutputPort op@Opname = new DefaultRuntimeOutputPort();
						for (i=0;i<op_count;i++)
						{
							replace_once("@defaultops",
						    "\tfinal IRuntimeOutputPort op@Opname = new DefaultRuntimeOutputPort();\n"
							"@defaultops");
							uppercase(oplist[i].name);
							replace_once("@Opname",oplist[i].name);
						}
						replace_once("@defaultops","\t// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); \n");

						//   @defaultetps: final IRuntimeEventTriggererPort etp@Etpname = new DefaultRuntimeEventTriggererPort();    
						for (i=0;i<etp_count;i++)
						{
							replace_once("@defaultetps",
						    "\tfinal IRuntimeEventTriggererPort etp@Etpname = new DefaultRuntimeEventTriggererPort();\n"
							"@defaultetps");
							uppercase(etplist[i].name);
							replace_once("@Etpname",etplist[i].name);
						}
						replace_once("@defaultetps","\t// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();\n");

						//   @properties: public int propertyname;
						for (i=0;i<prop_count;i++)
						{
							replace_once("@properties",
						    "\t@proptype prop@Propname = @propvalue;\n"
							"@properties");
							if (!strcmp(proplist[i].type,"string"))
							{
								replace_once("@proptype","String");
								strcpy(tmpstr,"\"");strcat(tmpstr,proplist[i].value);strcat(tmpstr,"\"");
								replace_once("@propvalue",tmpstr);
							}
							else if (!strcmp(proplist[i].type,"integer"))
							{
								replace_once("@proptype","int");
								replace_once("@propvalue",proplist[i].value);
							}
							else {
								replace_once("@proptype",proplist[i].type);
								replace_once("@propvalue",proplist[i].value);
							}
							uppercase(proplist[i].name);
							replace_once("@Propname",proplist[i].name);
						}
						replace_once("@properties","");

						if (setup.hasgui)
						{
							replace_once("@guiclass","\tprivate  GUI gui = null;");
							replace_once("@guistart","\t\t\tgui = new GUI(this,AREServices.instance.getAvailableSpace(this));\n"
													 "\t\t\tAREServices.instance.displayPanel(gui, this, true);");
							replace_once("@guistop","\t\t\tAREServices.instance.displayPanel(gui, this, false);");

						}
						else
						{
							replace_once("@guiclass","");
							replace_once("@guistart","");
							replace_once("@guistop","");
						}


						//   @getinputports:
						for (i=0;i<ip_count;i++)
						{
							replace_once("@getinputports",
						    "\t\tif (\"@ipname\".equalsIgnoreCase(portID))\n"
							"\t\t{\n"
							"\t\t\treturn ip@Ipname;\n"
							"\t\t}\n"
							"@getinputports");
							lowercase(iplist[i].name);
							replace_once("@ipname",iplist[i].name);
							uppercase(iplist[i].name);
							replace_once("@Ipname",iplist[i].name);
						}
						replace_once("@getinputports","");

						// @getouputports:
						for (i=0;i<op_count;i++)
						{
							replace_once("@getoutputports",
						    "\t\tif (\"@opname\".equalsIgnoreCase(portID))\n"
							"\t\t{\n"
							"\t\t\treturn op@Opname;\n"
							"\t\t}\n"
							"@getoutputports");
							lowercase(oplist[i].name);
							replace_once("@opname",oplist[i].name);
							uppercase(oplist[i].name);
							replace_once("@Opname",oplist[i].name);
						}
						replace_once("@getoutputports","");

						//  @getelports:
						for (i=0;i<elp_count;i++)
						{
							replace_once("@getelports",
						    "\t\tif (\"@elpname\".equalsIgnoreCase(eventPortID))\n"
							"\t\t{\n"
							"\t\t\treturn elp@Elpname;\n"
							"\t\t}\n"
							"@getelports");
							lowercase(elplist[i].name);
							replace_once("@elpname",elplist[i].name);
							uppercase(elplist[i].name);
							replace_once("@Elpname",elplist[i].name);
						}
						replace_once("@getelports","");

							
						//  @getetports:
						for (i=0;i<etp_count;i++)
						{
							replace_once("@getetports",
						    "\t\tif (\"@etpname\".equalsIgnoreCase(eventPortID))\n"
							"\t\t{\n"
							"\t\t\treturn etp@Etpname;\n"
							"\t\t}\n"
							"@getetports");
							lowercase(etplist[i].name);
							replace_once("@etpname",etplist[i].name);
							uppercase(etplist[i].name);
							replace_once("@Etpname",etplist[i].name);
						}
						replace_once("@getetports","");

						//  @getpropertyvalue:
						for (i=0;i<prop_count;i++)
						{
							replace_once("@getpropertyvalue",
						    "\t\tif (\"@propname\".equalsIgnoreCase(propertyName))\n"
							"\t\t{\n"
							"\t\t\treturn prop@Propname;\n"
							"\t\t}\n"
							"@getpropertyvalue");
							lowercase(proplist[i].name);
							replace_once("@propname",proplist[i].name);
							uppercase(proplist[i].name);
							replace_once("@Propname",proplist[i].name);
						}
						replace_once("@getpropertyvalue","");							
							
						//  @setpropertyvalue:
						for (i=0;i<prop_count;i++)
						{
							if (!strcmp(proplist[i].type,"integer"))
							{
								replace_once("@setpropertyvalue",
								"\t\tif (\"@propname\".equalsIgnoreCase(propertyName))\n"
								"\t\t{\n"
								"\t\t\tfinal Object oldValue = prop@Propname;\n"
								"\t\t\tprop@Propname = Integer.parseInt(newValue.toString());\n"
								"\t\t\treturn oldValue;\n"
								"\t\t}\n"
								"@setpropertyvalue");
								lowercase(proplist[i].name);
								replace_once("@propname",proplist[i].name);
								uppercase(proplist[i].name);
								replace_once("@Propname",proplist[i].name);
								replace_once("@Propname",proplist[i].name);
							}
							if (!strcmp(proplist[i].type,"double"))
							{
								replace_once("@setpropertyvalue",
								"\t\tif (\"@propname\".equalsIgnoreCase(propertyName))\n"
								"\t\t{\n"
								"\t\t\tfinal double oldValue = prop@Propname;\n"
								"\t\t\tprop@Propname = Double.parseDouble((String)newValue);\n"
								"\t\t\treturn oldValue;\n"
								"\t\t}\n"
								"@setpropertyvalue");
								lowercase(proplist[i].name);
								replace_once("@propname",proplist[i].name);
								uppercase(proplist[i].name);
								replace_once("@Propname",proplist[i].name);
								replace_once("@Propname",proplist[i].name);
							}
							if (!strcmp(proplist[i].type,"string"))
							{
								replace_once("@setpropertyvalue",
								"\t\tif (\"@propname\".equalsIgnoreCase(propertyName))\n"
								"\t\t{\n"
								"\t\t\tfinal Object oldValue = prop@Propname;\n"
								"\t\t\tprop@Propname = (String)newValue;\n"
								"\t\t\treturn oldValue;\n"
								"\t\t}\n"
								"@setpropertyvalue");
								lowercase(proplist[i].name);
								replace_once("@propname",proplist[i].name);
								uppercase(proplist[i].name);
								replace_once("@Propname",proplist[i].name);
								replace_once("@Propname",proplist[i].name);
							}
							if (!strcmp(proplist[i].type,"boolean"))
							{
								replace_once("@setpropertyvalue",
								"\t\tif (\"@propname\".equalsIgnoreCase(propertyName))\n"
								"\t\t{\n"
								"\t\t\tfinal Object oldValue = prop@Propname;\n"
								"\t\t\tif(\"true\".equalsIgnoreCase((String)newValue))\n"
								"\t\t\t{\n"
								"\t\t\t\tprop@Propname = true;\n"
								"\t\t\t}\n"
								"\t\t\telse if(\"false\".equalsIgnoreCase((String)newValue))\n"
								"\t\t\t{\n"
								"\t\t\t\tprop@Propname = false;\n"
								"\t\t\t}\n"
								"\t\t\treturn oldValue;\n"
								"\t\t}\n"
								"@setpropertyvalue");
								lowercase(proplist[i].name);
								replace_once("@propname",proplist[i].name);
								uppercase(proplist[i].name);
								replace_once("@Propname",proplist[i].name);
								replace_once("@Propname",proplist[i].name);
								replace_once("@Propname",proplist[i].name);
							}
						}
						replace_once("@setpropertyvalue","");

						  // @iports:
						for (i=0;i<ip_count;i++)
						{
							replace_once("@iports",
						    "\tprivate final IRuntimeInputPort ip@Ipname  = new DefaultRuntimeInputPort()\n"
							"\t{\n"
							"\t\tpublic void receiveData(byte[] data)\n"
							"\t\t{\n"
							"\t\t\t	 // insert data reception handling here, e.g.: \n"
							"\t\t\t	 // myVar = ConversionUtils.doubleFromBytes(data); \n"
							"\t\t\t	 // myVar = ConversionUtils.stringFromBytes(data); \n"
							"\t\t\t	 // myVar = ConversionUtils.intFromBytes(data); \n"
							"\t\t}\n"
							"\t};\n"
							"@iports");
							uppercase(iplist[i].name);
							replace_once("@Ipname",iplist[i].name);
						}
						replace_once("@iports","");							

						//	@elports:
						for (i=0;i<elp_count;i++)
						{
							replace_once("@elports",
						    "\tfinal IRuntimeEventListenerPort elp@Elpname = new IRuntimeEventListenerPort()\n"
							"\t{\n"
							"\t\tpublic void receiveEvent(final String data)\n"
							"\t\t{\n"
							"\t\t\t	 // insert event handling here \n"
							"\t\t}\n"
							"\t};\n"
							"@elports");
							uppercase(elplist[i].name);
							replace_once("@Elpname",elplist[i].name);
						}
						replace_once("@elports","");							

						strcpy(tmpstr,setup.pluginname);strcat(tmpstr,"Instance.java");
 						write_file(actdir,tmpstr);

						if (setup.hasgui)
						{
							if(!read_file(path,"templates\\template_GUI.java")) break;
							// @symbolicname:eu.asterics.component.actuator.bardisplay
							lowercase(setup.pluginname);
							strcpy(tmpstr,"eu.asterics.component.");strcat(tmpstr,setup.plugintype);strcat(tmpstr,".");strcat(tmpstr,setup.pluginname);
							replace_once ("@symbolicname",tmpstr);
							// @instancename: BardisplayInstance
							uppercase(setup.pluginname);strcpy(tmpstr,setup.pluginname);strcat(tmpstr,"Instance");
							replace_once ("@instancename",tmpstr);
							replace_once ("@instancename",tmpstr);  // for class constructor


							strcpy(tmpstr,"GUI.java");
	 						write_file(actdir,tmpstr);
						}



				        MessageBox(NULL,"Plugin creation successful.","Info", MB_OK);
					}
					break;
			}
//		case WM_PAINT:
//		{
//			return FALSE;
//		}

	}
    return FALSE;
}


void SD_OnSize(HWND hwnd, UINT state, int cx, int cy)
{
    if(state != SIZE_RESTORED && state != SIZE_MAXIMIZED)
        return;

    SCROLLINFO si = {};
    si.cbSize = sizeof(SCROLLINFO);

    const int bar[] = { SB_HORZ, SB_VERT };
    const int page[] = { cx, cy };

    for(size_t i = 0; i < ARRAYSIZE(bar); ++i)
    {
        si.fMask = SIF_PAGE;
        si.nPage = page[i];
        SetScrollInfo(hwnd, bar[i], &si, TRUE);

        si.fMask = SIF_RANGE | SIF_POS;
        GetScrollInfo(hwnd, bar[i], &si);

        const int maxScrollPos = si.nMax - (page[i] - 1);

        // Scroll client only if scroll bar is visible and window's
        // content is fully scrolled toward right and/or bottom side.
        // Also, update window's content on maximize.
        const bool needToScroll =
            (si.nPos != si.nMin && si.nPos == maxScrollPos) ||
            (state == SIZE_MAXIMIZED);

        if(needToScroll)
        {
            SD_ScrollClient(hwnd, bar[i], si.nPos);
        }
    }
}

void SD_OnHScroll(HWND hwnd, HWND /*hwndCtl*/, UINT code, int /*pos*/)
{
    SD_OnHVScroll(hwnd, SB_HORZ, code);
}

void SD_OnVScroll(HWND hwnd, HWND /*hwndCtl*/, UINT code, int /*pos*/)
{
    SD_OnHVScroll(hwnd, SB_VERT, code);
}

void SD_OnHVScroll(HWND hwnd, int bar, UINT code)
{
    const int scrollPos = SD_GetScrollPos(hwnd, bar, code);

    if(scrollPos == -1)
        return;

    SetScrollPos(hwnd, bar, scrollPos, TRUE);
    SD_ScrollClient(hwnd, bar, scrollPos);
}

void SD_ScrollClient(HWND hwnd, int bar, int pos)
{
    static int s_prevx = 1;
    static int s_prevy = 1;

    int cx = 0;
    int cy = 0;

    int& delta = (bar == SB_HORZ ? cx : cy);
    int& prev = (bar == SB_HORZ ? s_prevx : s_prevy);

    delta = prev - pos;
    prev = pos;

    if(cx || cy)
    {
        ScrollWindow(hwnd, cx, cy, NULL, NULL);
    }
}

int SD_GetScrollPos(HWND hwnd, int bar, UINT code)
{
    SCROLLINFO si = {};
    si.cbSize = sizeof(SCROLLINFO);
    si.fMask = SIF_PAGE | SIF_POS | SIF_RANGE | SIF_TRACKPOS;
    GetScrollInfo(hwnd, bar, &si);

    const int minPos = si.nMin;
    const int maxPos = si.nMax - (si.nPage - 1);

    int result = -1;

    switch(code)
    {
    case SB_LINEUP /*SB_LINELEFT*/:
        result = max(si.nPos - 1, minPos);
        break;

    case SB_LINEDOWN /*SB_LINERIGHT*/:
        result = min(si.nPos + 1, maxPos);
        break;

    case SB_PAGEUP /*SB_PAGELEFT*/:
        result = max(si.nPos - (int)si.nPage, minPos);
        break;

    case SB_PAGEDOWN /*SB_PAGERIGHT*/:
        result = min(si.nPos + (int)si.nPage, maxPos);
        break;

    case SB_THUMBPOSITION:
        // do nothing
        break;

    case SB_THUMBTRACK:
        result = si.nTrackPos;
        break;

    case SB_TOP /*SB_LEFT*/:
        result = minPos;
        break;

    case SB_BOTTOM /*SB_RIGHT*/:
        result = maxPos;
        break;

    case SB_ENDSCROLL:
        // do nothing
        break;
    }

    return result;
}