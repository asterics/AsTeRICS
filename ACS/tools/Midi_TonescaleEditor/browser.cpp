#include "Midi_TonescaleEditor.h"
#include <mmsystem.h>


#define DEF_MIDIPORT 0
#define OPEN_SAVE 1
#define OPEN_LOAD 0
#define MAX_HARMONICTONES 200

typedef struct SCALEStruct
{
	int  len;
	char name[100];
	int  tones[256];
} SCLAEStruct;

typedef struct MIDIPORTStruct
{
	HMIDIOUT midiout;
	char portname[50];
} MIDIPORTStruct;


struct MIDIPORTStruct       MIDIPORTS[10];
struct SCALEStruct          LOADSCALE;

int midiports=0;


char DEFPATH[]="..\\..\\data";

char noteNames[12][10]= {"A","A#","B","C","C#","D","D#","E","F","F#","G","G#"};


char midi_instnames[256][30]= {
{"Acoustic Grand Piano"},
{"Bright Acoustic Piano"},
{"Electric Grand Piano"},
{"Honky-tonk Piano"},
{"Electric Piano 1"},
{"Electric Piano 2"},
{"Harpsichord"},
{"Clavi"},
{"Celesta"},
{"Glockenspiel"},
{"Music Box"},
{"Vibraphone"},
{"Marimba"},
{"Xylophone"},
{"Tubular Bells"},
{"Dulcimer"},
{"Drawbar Organ"},
{"Percussive Organ"},
{"Rock Organ"},
{"Church Organ"},
{"Reed Organ"},
{"Accordion"},
{"Harmonica"},
{"Tango Accordion"},
{"Acoustic Guitar (Nylon)"},
{"Acoustic Guitar (Steel)"},
{"Electric Guitar (Jazz)"},
{"Electric Guitar (Clean)"},
{"Electric Guitar (Muted)"},
{"Overdriven Guitar"},
{"Distortion Guitar"},
{"Guitar Harmonics"},
{"Acoustic bass"},
{"Electric Bass (Finger)"},
{"Electric Bass (Pick)"},
{"Fretless Bass"},
{"Slap Bass 1"},
{"Slap Bass 2"},
{"Synth Bass 1"},
{"Synth Bass 2"},
{"Violin"},
{"Viola"},
{"Cello"},
{"Contrabass"},
{"Tremolo Strings"},
{"Pizzicato Strings"},
{"Orchestral Harp"},
{"Timpani"},
{"String Ensemble 1"},
{"String Ensemble 2"},
{"Synth Strings 1"},
{"Synth Strings 2"},
{"Choir Aahs"},
{"Voice Oohs"},
{"Synth Voice"},
{"Orchestra Hit"},
{"Trumpet"},
{"Trumbone"},
{"Tuba"},
{"Muted Trumpet"},
{"French Horn"},
{"Brass Section"},
{"Synth Brass 1"},
{"Synth Brass 2"},
{"Soprano Sax"},
{"Alto Sax"},
{"Tenor Sax"},
{"Baritone Sax"},
{"Oboe"},
{"English Horn"},
{"Bassoon"},
{"Clarinet"},
{"Piccolo"},
{"Flute"},
{"Recorder"},
{"Pan Flute"},
{"Blown Bottle"},
{"Shakuhachi"},
{"Whistle"},
{"Ocarina"},
{"Lead 1 (Square)"},
{"Lead 2 (Sawtooth)"},
{"Lead 3 (Calliope)"},
{"Lead 4 (Chiff)"},
{"Lead 5 (Charang)"},
{"Lead 6 (Voice)"},
{"Lead 7 (Fifths)"},
{"Lead 8 (Bass + Lead)"},
{"Pad 1 (New Age)"},
{"Pad 2 (Warm)"},
{"Pad 3 (Polysynth)"},
{"Pad 4 (Choir)"},
{"Pad 5 (Bowed)"},
{"Pad 6 (Metallic)"},
{"Pad 7 (Halo)"},
{"Pad 8 (Sweep)"},
{"FX 1 (Rain)"},
{"FX 2 (Soundtrack)"},
{"FX 3 (Crystal)"},
{"FX 4 (Atmosphere)"},
{"FX 5 (Brightness)"},
{"FX 6 (Goblins)"},
{"FX  7 (Echoes)"},
{"FX 8 (Sci-fi)"},
{"Sitar"},
{"Banjo"},
{"Shamisen"},
{"Koto"},
{"Kalimba"},
{"Bagpipes"},
{"Fiddle"},
{"Shanai"},
{"Tinkle Bell"},
{"Agogo"},
{"Steel Drums"},
{"Wood Block"},
{"Taiko Drum"},
{"Melodic Drum"},
{"Synth Drum"},
{"Reverse Cymbal"},
{"Guitar Fret Noise"},
{"Breath Noise"},
{"Seashore"},
{"Bird Tweet"},
{"Telephone Ring"},
{"Helicopter"},
{"Applause"},
{"Gunshot"}
};


void report(char * Message)
{
    MessageBox(0, Message, "Info", MB_OK|MB_SYSTEMMODAL|MB_TOPMOST|MB_SETFOREGROUND|MB_ICONINFORMATION);
}

int open_file_dlg(HWND hDlg, char * szFileName,  int flag_save)
{
    OPENFILENAME ofn;
	int i=0;
    
    ZeroMemory(&ofn, sizeof(ofn));

	while (szFileName[i]!='\0') 
	{	switch (szFileName[i]) {
		   case '/':
		   case '\"':
		   case ',':
		   case '?':
		   case '|':
		   case '>':
		   case '<': szFileName[i]='_'; break;
								}
		i++;
	}
    ofn.lStructSize = sizeof(ofn); 
    ofn.hwndOwner = hDlg;
	ofn.lpstrFilter = "Scale Files (*.sc)\0*.sc\0All Files (*.*)\0*.*\0";
    ofn.lpstrDefExt = "sc";
    ofn.lpstrFile = szFileName;
    ofn.nMaxFile = MAX_PATH;

    if (flag_save==OPEN_SAVE) 
	{  ofn.Flags =OFN_EXPLORER | OFN_PATHMUSTEXIST | OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT;
		if(GetSaveFileName(&ofn)) 
		{
		   strcpy(szFileName,ofn.lpstrFile);
 		   return TRUE; 
		}
		else return FALSE;
	}
	else  
	{
		ofn.Flags= OFN_EXPLORER | OFN_FILEMUSTEXIST | OFN_HIDEREADONLY;
        if(GetOpenFileName(&ofn))
		{
//		   strcpy(szFileName,lpstrInitialDir);
		   strcpy(szFileName,ofn.lpstrFile);
           return TRUE;
		}
		else return FALSE;
	}

}



BOOL load_from_file(LPCTSTR pszFileName, void * buffer, int size)
{
    
    HANDLE hFile;
    BOOL bSuccess = FALSE;

    hFile = CreateFile(pszFileName, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);
    if(hFile != INVALID_HANDLE_VALUE)
    {
         DWORD dwRead;

         if(!ReadFile(hFile, (unsigned char *) buffer , size, &dwRead, NULL)) return FALSE;
		 CloseHandle(hFile);
		 return TRUE;
     }
	 return FALSE;    
}

BOOL save_to_file(LPCTSTR pszFileName, void * buffer, int size)
{
    
    HANDLE hFile;
    BOOL bSuccess = FALSE;

    hFile = CreateFile(pszFileName, GENERIC_WRITE, 0, NULL,
        CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
    if(hFile != INVALID_HANDLE_VALUE)
    {
         DWORD dwWritten;
         if(!WriteFile(hFile, (unsigned char * )buffer, size, &dwWritten, NULL)) return FALSE;
		 CloseHandle(hFile);
		 return TRUE;
    }
	return FALSE;    
}




int get_scrollpos(WPARAM wParam, LPARAM lParam)
{
	int nScrollCode = (int)LOWORD(wParam);
	int nPos = (short int)HIWORD(wParam);
	int nNewPos; 	
	SCROLLINFO si = {sizeof(SCROLLINFO), SIF_PAGE|SIF_POS|SIF_RANGE|SIF_TRACKPOS, 0, 0, 0, 0,0};

	GetScrollInfo ((HWND)lParam,SB_CTL, &si);
	nNewPos = si.nPos;
	switch (nScrollCode)
	{
	  case 0:	nNewPos = nNewPos - 1;break;
	  case 1:	nNewPos = nNewPos + 1;break;
	  case 2:	nNewPos = nNewPos - 5;break;
	  case 3:	nNewPos = nNewPos + 5;break;
	  case SB_THUMBTRACK:
	  case SB_THUMBPOSITION:
				nNewPos = nPos;	break;
	}
	if (nNewPos<si.nMin) nNewPos=si.nMin;
	if (nNewPos>si.nMax) nNewPos=si.nMax;
	si.fMask = SIF_POS;  si.nPos = nNewPos;
	SetScrollInfo ((HWND)lParam,SB_CTL, &si, TRUE);
	return(nNewPos);
}


int midi_open_port(HMIDIOUT * midiout, int portnum)
{
	if (midiout) midiOutClose(* midiout);
	if (midiOutOpen(midiout,portnum-1,0,0,CALLBACK_NULL)==MMSYSERR_NOERROR) 
	   return TRUE;
	return FALSE;
}


//			  midi_ControlChange(&(MIDIPORTS[st->port].midiout), st->midichn,123, 0);
//			  midi_ControlChange(&(MIDIPORTS[st->port].midiout), st->midichn,120, 0);

void init_midi (void)
{
	char sztemp[50];
    MIDIOUTCAPS ocaps;

	for (int t=128; t<255; t++)
	{
 		wsprintf(sztemp,"Controller Nr.%d",t-127);
  		strcpy(midi_instnames[t],sztemp);
	}

	midiports=0;

	for (int t = -1;  t < (int) midiOutGetNumDevs(); t++) 
//	for (unsigned int t = 0;  t < midiOutGetNumDevs(); t++) 
	{
	  midiOutGetDevCaps(t, &(ocaps), sizeof (MIDIOUTCAPS));
	  strcpy(MIDIPORTS[midiports].portname, ocaps.szPname ) ;
	  midi_open_port(&(MIDIPORTS[midiports].midiout), t+2);
	  midiports++;
	} 
}

int get_listed_midiport(int num)
{
	int i,c;
	c=-1;
	for (i=0;i<midiports;i++)
	{
		if (MIDIPORTS[i].midiout) c++;
		if (c==num)	return(i);
	}
	return(midiports);
}

int get_opened_midiport(int num)
{
	int i,c;
	c=-1;
	for (i=0;i<=num;i++)
		if (MIDIPORTS[i].midiout) c++;
	
	return(c);
}


void midi_Instrument(HMIDIOUT * midiout, int chn, int inst)
{
		if (inst<128) midiOutShortMsg(*midiout, 256*inst+191+chn);
		// 1-127: Instruments for noteOn,  128 - 255: Controller numbers
}

void midi_ControlChange(HMIDIOUT * midiout, int chn, int cont, int val)
{
		midiOutShortMsg(*midiout,65536*val+256*cont+175+chn);
}

void midi_NoteOn(HMIDIOUT * midiout, int chn, int note, int vol)
{
		midiOutShortMsg(*midiout,65536*vol+256*note+143+chn);
}

void midi_NoteOff(HMIDIOUT * midiout, int chn, int note)
{
		//midiOutShortMsg(*midiout,256*note+143+chn);
			midiOutShortMsg(*midiout,256*note+127+chn);
}

void midi_PitchRange(HMIDIOUT * midiout, int chn, int pitchrange)
{
/*	 'B0 65 00  Controller/chan 0, RPN coarse (101), Pitch Bend Range
	 'B0 64 00  Controller/chan 0, RPN fine (100), Pitch Bend Range
	 'B0 06 02  Controller/chan 0, Data Entry coarse, +/- 2 semitones
	 'B0 26 04  Controller/chan 0, Data Entry fine, +/- 4 cents
			  */
	 midi_ControlChange(midiout, chn,101, 0);
	 midi_ControlChange(midiout, chn,100, 0);
     
	 midi_ControlChange(midiout, chn,6, pitchrange);
     midi_ControlChange(midiout, chn,38, 0);

}

void midi_Vol(HMIDIOUT * midiout, int chn, int vol)
{
	 midi_ControlChange(midiout, chn,7, vol);

}

void midi_Pitch(HMIDIOUT * midiout, int chn, int wheel)
{
	int wl,wh;

	wh=(wheel>>8)&255;
	wl=wheel&255;
	//SetDlgItemInt(ghWndStatusbox,IDC_STATUS,wl,0);
	midiOutShortMsg(*midiout,65536*wh+256*wl+223+chn); //
	
}

void midi_Message(HMIDIOUT * midiout, int status)
{
	midiOutShortMsg(*midiout, status*255);
}



void update_harmonic( HWND hCtrlWnd)
{
	int t,index;
    char szdata[5];

	SendMessage(hCtrlWnd, LB_RESETCONTENT, 0, 0);
	for (t=0;t<LOADSCALE.len;t++) 
	{
		int n=LOADSCALE.tones[t];
		wsprintf(szdata, "%d", n);

		int x=n % 12;
		int y=(int)(n / 12)+1;
		char str[20];
		char numstr[10];
		itoa(y,numstr,10);
		strcpy (str,noteNames[x]);
		strcat(str,numstr);

		index=SendMessage(hCtrlWnd, LB_ADDSTRING, 0,(LPARAM) str);
		SendMessage(hCtrlWnd, LB_SETITEMDATA, (WPARAM)index, (LPARAM)LOADSCALE.tones[t]);
	}

}

void apply_harmonic(HWND hDlg)
{ 
  int t;

  for (t=0;t<LOADSCALE.len;t++)
	  LOADSCALE.tones[t]=SendDlgItemMessage(hDlg, IDC_HARMONICLIST, LB_GETITEMDATA, (WPARAM)t, 0);
}


LRESULT CALLBACK BROWSEDlghandler( HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam )
{
    static int t,port=DEF_MIDIPORT,midichn=1,instrument=11;
	static char szFileName[MAX_PATH];
	static int oldtone=0;


	switch( message )
	{
		case WM_INITDIALOG:
			{
				SCROLLINFO lpsi;

				init_midi();
				strcpy(LOADSCALE.name,"Chromatic1-127");
				for (t=1;t<128;t++)  LOADSCALE.tones[t-1]=t; 
				LOADSCALE.len=127;
				SetDlgItemText(hDlg, IDC_HARMONICNAME, LOADSCALE.name);

				for (t=0;t<255;t++) 
					SendDlgItemMessage(hDlg, IDC_MIDIINSTCOMBO, CB_ADDSTRING, 0,(LPARAM) (LPSTR) midi_instnames[t]) ;
				SendDlgItemMessage( hDlg, IDC_MIDIINSTCOMBO, CB_SETCURSEL, instrument, 0L ) ;

 			    for (t = 0; t < midiports; t++) 
				 if(MIDIPORTS[t].midiout)
				   SendDlgItemMessage(hDlg, IDC_MIDIPORTCOMBO, CB_ADDSTRING, 0, (LPARAM) (LPSTR) MIDIPORTS[t].portname ) ;
				SetDlgItemText(hDlg, IDC_MIDIPORTCOMBO, MIDIPORTS[port].portname);

				lpsi.cbSize=sizeof(SCROLLINFO);
				lpsi.fMask=SIF_RANGE|SIF_POS;
				lpsi.nMin=0; lpsi.nMax=127;
				SetScrollInfo(GetDlgItem(hDlg,IDC_TONEBAR),SB_CTL,&lpsi,TRUE);
 			    SetScrollPos(GetDlgItem(hDlg, IDC_TONEBAR), SB_CTL, 64, 1);
			    SetDlgItemInt(hDlg, IDC_ACTTONE, 64,0);
			    SetDlgItemInt(hDlg, IDC_MIDICHN, midichn,0);
				
				midi_Instrument(&(MIDIPORTS[port].midiout),midichn,instrument);
				update_harmonic(GetDlgItem(hDlg, IDC_HARMONICLIST));

			}
			return TRUE;
	
		case WM_CLOSE:
				midi_NoteOff(&(MIDIPORTS[port].midiout), midichn,oldtone);
			    EndDialog(hDlg, LOWORD(wParam));
				return TRUE;
			break;
		case WM_COMMAND:
			switch (LOWORD(wParam)) 
			{

			case IDC_MIDIPORTCOMBO:
 				 if (HIWORD(wParam)==CBN_SELCHANGE)
				    port=get_listed_midiport(SendMessage(GetDlgItem(hDlg, IDC_MIDIPORTCOMBO), CB_GETCURSEL , 0, 0));
					midi_Instrument(&(MIDIPORTS[port].midiout),midichn,instrument); 
				 break;
			case IDC_MIDIINSTCOMBO:
					instrument=SendMessage(GetDlgItem(hDlg, IDC_MIDIINSTCOMBO), CB_GETCURSEL , 0, 0);
					midi_Instrument(&(MIDIPORTS[port].midiout),midichn,instrument); 
				break;
			case IDC_MIDICHN:
					midichn=GetDlgItemInt(hDlg, IDC_MIDICHN, NULL, 0);
					midi_Instrument(&(MIDIPORTS[port].midiout),midichn,instrument);
				break;
			case IDC_DELTONE:
				{   int select;
					select=SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_GETCURSEL , 0, 0L ) ;
					if ((select>=0)&&(select<LOADSCALE.len))
					{
					  SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_DELETESTRING , (WPARAM) select, 0L ) ;
					  LOADSCALE.len--;
					  SetFocus(GetDlgItem(hDlg,IDC_HARMONICLIST));
					  SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_SETCURSEL , (WPARAM) select, 0L ) ;
					  apply_harmonic(hDlg);
					}
//					else  report("Nothing selected");
				}
				break;
			case IDC_HARMONICNAME:
				  GetDlgItemText(hDlg, IDC_HARMONICNAME, LOADSCALE.name, 256);
				  
				break;
			case IDC_ADDTONE:
				{  int index,dataint,select;
				   char szdata[5];

					if (LOADSCALE.len<MAX_HARMONICTONES-1)
					{
					  select=SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_GETCURSEL , 0, 0L )+1 ;
					  if ((select<1)||(select>LOADSCALE.len)) select=LOADSCALE.len;
					  GetDlgItemText(hDlg, IDC_ACTTONETEXT, szdata, 4);
					  dataint=GetDlgItemInt(hDlg, IDC_ACTTONE, 0,0);
					  index=SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_INSERTSTRING , (WPARAM) select, (LPARAM) szdata) ;
					  SendDlgItemMessage(hDlg, IDC_HARMONICLIST, LB_SETITEMDATA, (WPARAM)index, (LPARAM)dataint);
					  LOADSCALE.len++;
					  SetFocus(GetDlgItem(hDlg,IDC_HARMONICLIST));
					  apply_harmonic(hDlg);
					} ;
				}
				break;
			case IDC_HARMONICCLEAR:
					SendDlgItemMessage(hDlg, IDC_HARMONICLIST, LB_RESETCONTENT, 0, 0);
					LOADSCALE.len=0;
					apply_harmonic(hDlg);
					break;
			case IDC_LOADHARMONIC:
				{
				strcpy(szFileName,DEFPATH);
 			    strcat(szFileName,"TONESCALES\\default.sc");
				 if (open_file_dlg(hDlg, szFileName, OPEN_LOAD)) 
				 {
					 /*
				 char tmp[256],*p1,*p2,diff=0;
				  strcpy(tmp,DEFPATH);
				  strcat(tmp,"TONESCALES\\"); 
				  for (p1=tmp,p2=szFileName;(*p1) && (*p2) && (!diff);p1++,p2++) 
					  if (tolower(*p1)!=tolower(*p2)) diff=1;
				  if (diff||(strlen(tmp)>strlen(szFileName)))
					report("Please use Tonescales-subfolder of brainbay application to load/store palette files");
				  else*/
				  {

					if (!load_from_file(szFileName, &LOADSCALE, sizeof(struct SCALEStruct)))
						report("Could not load Harmonic Scale");
					else
					{
					    update_harmonic(GetDlgItem(hDlg, IDC_HARMONICLIST));
						SetDlgItemText(hDlg, IDC_HARMONICNAME, LOADSCALE.name);

					}
				  }
				 } else report("Could not load Harmonic Scale");

				}
				break;
			case IDC_SAVEHARMONIC:
				{
					char temp[100];
					strcpy(szFileName,DEFPATH);
					strcat(szFileName,"TONESCALES\\");
					GetDlgItemText(hDlg, IDC_HARMONICNAME, temp, MAX_PATH);
					strcat (szFileName,temp);
					strcat (szFileName,".sc");

					if (open_file_dlg(hDlg, szFileName, OPEN_SAVE))
					{
/*	 				  char tmp[256],*p1,*p2,diff=0;
					  strcpy(tmp,DEFPATH);
					  strcat(tmp,"TONESCALES\\"); 
					  for (p1=tmp,p2=szFileName;(*p1) && (*p2) && (!diff);p1++,p2++) 
						  if (tolower(*p1)!=tolower(*p2)) diff=1;
					  if (diff||(strlen(tmp)>strlen(szFileName)))
						report("Please use Tonescales-subfolder of brainbay application to load/store palette files");
					  else*/
					  {

						 if (!save_to_file(szFileName, &LOADSCALE, sizeof(struct SCALEStruct)))
							report("Could not save Scale");
					  }
					}
				}
				break;

            case IDC_HARMONICLIST:
                if (HIWORD(wParam)==LBN_SELCHANGE)
                {
					int dataint,sel;
					sel=SendDlgItemMessage( hDlg, IDC_HARMONICLIST, LB_GETCURSEL , 0, 0L ) ;
					dataint=SendDlgItemMessage(hDlg, IDC_HARMONICLIST, LB_GETITEMDATA, (WPARAM)sel, 0);
					midi_NoteOff(&(MIDIPORTS[port].midiout), midichn,oldtone);
					oldtone=dataint;
					midi_NoteOn(&(MIDIPORTS[port].midiout), midichn, dataint,127);
					
                }
				break;

			}
			return TRUE;
		case WM_HSCROLL:
		{
			int nNewPos; 
			if ((nNewPos=get_scrollpos(wParam,lParam))>=0)
			{   
			  if (lParam == (long) GetDlgItem(hDlg,IDC_TONEBAR)) 
			  { 
				int x=nNewPos % 12;
				int y=(int)(nNewPos / 12)+1;
				char str[20];
				char numstr[10];
				itoa(y,numstr,10);
				strcpy (str,noteNames[x]);
				strcat(str,numstr);
				SetDlgItemInt(hDlg, IDC_ACTTONE,nNewPos,0);
				SetDlgItemText(hDlg, IDC_ACTTONETEXT, str);
				midi_NoteOff(&(MIDIPORTS[port].midiout), midichn,oldtone);
				oldtone=nNewPos;
				midi_NoteOn(&(MIDIPORTS[port].midiout), midichn,nNewPos,127);
				apply_harmonic(hDlg);
				}
			}
		
		}
		break;
		case WM_SIZE:
		case WM_MOVE: ;
		break;
		return TRUE;
	}
    return FALSE;
}
