@ECHO OFF
:Initialization

@REM VERY IMPORTANT: The two lines ensure that the script is executed in the current directy (ARE), also when called
@REM as administrator
@setlocal enableextensions
@cd /d "%~dp0"

CLS

@IF NOT DEFINED ARE_CONSOLE_LOGLEVEL SET ARE_CONSOLE_LOGLEVEL=WARNING 

SET PROFILE_PATH=profile
SET ARE_OPTIONAL_SERVICES_INI=
set AUTOSTART_MODEL=

@REM Check commandline parameter
@IF "%1" == "--webservice" (
	@    echo "Ignoring --webservice flag, will be started automatically!"
	@    SET AUTOSTART_MODEL=%2
) ELSE (
	@     SET AUTOSTART_MODEL=%1
)

@echo "Using ARE_CONSOLE_LOGLEVEL: %ARE_CONSOLE_LOGLEVEL%"
@echo "Using ARE_DEBUG_STRING path: %ARE_DEBUG_STRING%"
@echo "Using profile path: %PROFILE_PATH%"
@echo "Using autostart model: %AUTOSTART_MODEL%"
@echo "Using optional services: %ARE_OPTIONAL_SERVICES_INI%"

@set JAVA_EXE=java.exe
@IF NOT DEFINED ARE_DEBUG_STRING SET JAVA_EXE=javaw.exe

REM searches for a 32-bit java or any java if no 32-bit found. 
REM JAVA_BIN is the full path including the .exe command
REM Return ERRORLEVEL 1, if no java was found
call findjava.bat JAVA_BIN %JAVA_EXE%
REM if no java could be found, exit
IF ERRORLEVEL 1 GOTO QuitError

echo Using JAVA_BIN: %JAVA_BIN% 
%JAVA_BIN% -version

REM Disable Visual C++ Redistributable check, because the dlls are now contained in the application folder
REM %JAVA_BIN% -jar VCChecker.jar
REM IF ERRORLEVEL 1 GOTO ContARE
REM echo NO C++ Redistributable Package found. 
REM echo It is highly recommended to download and install the Visual C++ Redistributable Package from Microsoft!
REM echo Download link for 32 bit systems: http://www.microsoft.com/en-us/download/details.aspx?id=5555
REM echo Several Plugins will not work without the package!
REM rem GOTO QuitError
REM Pause

:ContARE

IF EXIST asterics.ARE.jar GOTO Continue
ECHO ARE jar files not found, please unzip ARE.zip or copy jar files into this folder !
GOTO QuitError

:Continue

ECHO Deleting OSGi-Cache
rd /s/q %PROFILE_PATH%\org.eclipse.osgi

ECHO Starting AsTeRICS Runtime Environment...

set START_CMD=
set SPLASH_SWITCH=
REM The start command must have empty quotes as first parameter, so that a path with spaces that is also encoded with quotes is not mistaken as the first parameter title
@IF NOT DEFINED ARE_DEBUG_STRING set START_CMD=start ""
@IF NOT DEFINED ARE_DEBUG_STRING set SPLASH_SWITCH=-splash:images/asterics_startup.png

%START_CMD% %JAVA_BIN% %ARE_DEBUG_STRING% %SPLASH_SWITCH% -Deu.asterics.mw.services.AstericsErrorHandling.consoleLogLevel=%ARE_CONSOLE_LOGLEVEL% -Dsun.java2d.d3d=true -Dorg.osgi.framework.os.name=win32 -Dosgi.clean=true -Dorg.osgi.framework.bootdelegation=* -Dorg.osgi.framework.system.packages.extra=sun.misc -DAnsi=true -Djava.util.logging.config.file=logging.properties -Deu.asterics.ARE.startModel=%AUTOSTART_MODEL% -Deu.asterics.ARE.ServicesFiles="%ARE_OPTIONAL_SERVICES_INI%" -jar org.eclipse.osgi_3.14.0.v20190517-1309.jar -configuration %PROFILE_PATH% -console %*
set ARE_CONSOLE_LOGLEVEL=
set ARE_DEBUG_STRING=

GOTO Quit

:QuitError
pause
set ARE_CONSOLE_LOGLEVEL=
set ARE_DEBUG_STRING=

:Quit
set ARE_CONSOLE_LOGLEVEL=
set ARE_DEBUG_STRING=
