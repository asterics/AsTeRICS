; -- AsTeRICS Installer --
[Setup]
AppName=AsTeRICS
AppVersion=2.8
DefaultDirName={pf}\AsTeRICS
DefaultGroupName=AsTeRICS
UninstallDisplayIcon=
Compression=lzma2
SolidCompression=yes
OutputDir=.
SetupIconFile=asterics_icon.ico
PrivilegesRequired=admin

[Icons]
Name: "{commondesktop}\AsTeRICS Configuration Suite"; Filename: {app}\ACS/ACS.exe; WorkingDir: {app}/ACS
Name: "{commondesktop}\AsTeRICS Runtime Environment"; Filename: {app}\ARE/ARE.exe; WorkingDir: {app}/ARE
Name: "{group}\ACS"; Filename: "{app}\ACS\ACS.exe"; WorkingDir: "{app}/ACS"
Name: "{group}\ARE"; Filename: "{app}\ARE\ARE.exe"; WorkingDir: "{app}/ARE"
Name: {group}\{cm:UninstallProgram,AsTeRICS}; Filename: {uninstallexe}

           
[Files]
Source: "..\AsTeRICS\*"; DestDir: "{app}"; Flags: recursesubdirs createallsubdirs; Components: "AsTeRICS";Permissions: everyone-full;
Source: "java\*"; DestDir: "{app}\ARE/java"; Flags: recursesubdirs; Components: "jre";
Source: "splash.bmp"; DestName: "Splash.bmp"; Flags: dontcopy nocompression
Source: "dotNetFx40_setup.exe"; DestDir: {tmp}; Flags: deleteafterinstall; Check: CheckForFramework
Source: "vcredist_x86.exe"; DestDir: {tmp}; Flags: deleteafterinstall; Check: VCSetupNeeded

[DIRS]
Name: "{app}\ACS"; Permissions: users-full;
Name: "{app}\ARE"; Permissions: users-full;
Name: "{app}\APE"; Permissions: users-full;
Name: "{app}\OSKA"; Permissions: users-full;
Name: "{app}\CIMs"; Permissions: users-full;

[Components]
Name: "AsTeRICS"; Description: "AsTeRICS Core Files"; Types: full compact custom; Flags: fixed
Name: "jre"; Description: "Java Runtime Environment version 7."; Types: full

[Run]
; install if frameworks needed - Check: CheckForFramework;
Filename: {tmp}\dotNetFx40_setup.exe; Parameters: "/q:a /c:""install /l /q"""; Check: CheckForFramework; StatusMsg: Microsoft Framework 4.0 is beïng installed. Please wait...
Filename: {tmp}\vcredist_x86.exe; Parameters: "/q:a /c:""install /l /q"""; Check: VCSetupNeeded; StatusMsg: Microsoft Visual C++ 2010 is beïng installed. Please wait...

[UninstallDelete]
Type: filesandordirs; Name: "{app}\ACS"
Type: filesandordirs; Name: "{app}\ARE"
Type: filesandordirs; Name: "{app}\APE"
Type: filesandordirs; Name: "{app}\OSKA"

[Code]           
#IFDEF UNICODE
  #DEFINE AW "W"
#ELSE
  #DEFINE AW "A"
#ENDIF


// the following function checks if Visual C++ Redistributable needs to be installed
function VCSetupNeeded(): Boolean;
var
  RedistInstalled : Boolean;
begin
  RedistInstalled := RegKeyExists(HKLM,'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{196BB40D-1578-3D01-B289-BEFC77A11A1E}');
  RedistInstalled := RedistInstalled or RegKeyExists(HKLM,'SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{F0C3E5D1-1ADE-321E-8167-68EF0DE699A5}');
  RedistInstalled := RedistInstalled or RegKeyExists(HKLM,'SOFTWARE\Microsoft\DevDiv\VC');
  Result := not RedistInstalled;
end;

                                                                                                                            
var Splash  : TSetupForm;

// Add Splash Image 
function InitializeSetup(): Boolean;
var
  BitmapImage1 : TBitmapImage;
  UninstallPath : String;
  RegPath : String;
  ResultCode: Integer;
begin
  Splash := CreateCustomForm;
  Splash.BorderStyle := bsNone;

  BitmapImage1 := TBitmapImage.Create(Splash);
  BitmapImage1.AutoSize := True;
  BitmapImage1.Align := alClient;
  BitmapImage1.Left := 0;
  BitmapImage1.Top := 0;
  BitmapImage1.stretch := True;
  BitmapImage1.Parent := Splash;

  ExtractTemporaryFile('Splash.bmp');
  BitmapImage1.Bitmap.LoadFromFile(ExpandConstant('{tmp}') + '\Splash.bmp');

  Splash.Width := BitmapImage1.Width;
  Splash.Height := BitmapImage1.Height;
  Splash.Center;
  Splash.Show;

  BitmapImage1.Refresh;
  
  Sleep(2000)

  RegPath := 'Software\Microsoft\Windows\CurrentVersion\Uninstall\AsTeRICS_is1';
  if RegValueExists(HKEY_LOCAL_MACHINE, RegPath, 'UninstallString') 
  then  //Your App GUID/ID
  begin
    RegQueryStringValue(HKEY_LOCAL_MACHINE, RegPath, 'UninstallString', UninstallPath);
  end;

  RegPath := 'Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\AsTeRICS_is1';
  if RegValueExists(HKEY_LOCAL_MACHINE,RegPath, 'UninstallString') 
  then  //Your App GUID/ID
  begin
    RegQueryStringValue(HKEY_LOCAL_MACHINE, RegPath, 'UninstallString', UninstallPath);
  end;

  if Length(UninstallPath) > 0 then
  begin
  MsgBox(ExpandConstant('An old version of AsTeRCIS was detected, which will be uninstalled before the Installation?'), mbInformation, MB_OK);
  Exec(RemoveQuotes(ExpandConstant(UninstallPath)),'', '', SW_SHOW,ewWaitUntilTerminated, ResultCode);
  end;

  Result := True;
end;
        
//  check for .Net Framework 4.0
Function CheckForFramework : boolean;
Var
regresult : cardinal;
Begin
RegQueryDWordValue(HKLM, 'Software\Microsoft\NET Framework Setup\NDP\v4\Full', 'Install', regresult);
If regresult = 0 Then
Begin
Result := true;
End
Else
Result := false;
End;



procedure InitializeWizard();
begin
  Splash.Close;
end;

procedure CurPageChanged(CurPageID: Integer);
var
  Index: Integer;
begin
  if CurPageID = wpSelectComponents then
    begin
      Index := WizardForm.ComponentsList.Items.IndexOf('Component 2');
      if Index <> -1 then
      begin
        WizardForm.ComponentsList.Checked[Index] := False;
        WizardForm.ComponentsList.ItemEnabled[Index] := False;
      end;
      Index := WizardForm.ComponentsList.Items.IndexOf('Component 3');
      if Index <> -1 then
      begin
        WizardForm.ComponentsList.Checked[Index] := False;
        WizardForm.ComponentsList.ItemEnabled[Index] := False;
      end;
   end;
end;