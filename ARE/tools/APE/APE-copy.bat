@ECHO OFF
@REM VERY IMPORTANT: The two lines ensure that the script is executed in the current directy (ARE), also when called
@REM as administrator
@setlocal enableextensions
REM @cd /d "%~dp0"
set APE_DIR=%~dp0

REM searches for a 32-bit java or any java if no 32-bit found. 
REM JAVA_BIN is the full path including the .exe command
REM Return ERRORLEVEL 1, if no java was found
cd "%APE_DIR%\..\ARE\"
call ..\ARE\findjava.bat JAVA_BIN java.exe
cd "%APE_DIR%"
REM if no java could be found, exit
IF ERRORLEVEL 1 GOTO QuitError

echo Using JAVA_BIN: %JAVA_BIN% 
%JAVA_BIN% -version

%JAVA_BIN% -jar "%APE_DIR%\APE.jar" %*
 
:QuitError