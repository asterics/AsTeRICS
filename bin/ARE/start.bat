:Initialization

@REM VERY IMPORTANT: The two lines ensure that the script is executed in the current directy (ARE), also when called
@REM as administrator
@setlocal enableextensions
@cd /d "%~dp0"

CLS

@IF NOT DEFINED ARE_LOG_STRING SET ARE_LOG_STRING=error_level:WARNING

SET PROFILE_PATH=profile
SET ARE_OPTIONAL_SERVICES_INI=
set AUTOSTART_MODEL=

@REM Check commandline parameter
@IF "%1" == "--webservice" (
	@    echo "--webservice selected"
	@    SET AUTOSTART_MODEL=%2
	@    SET ARE_OPTIONAL_SERVICES_INI="services_websocketdemo.ini"
) ELSE (
	@     SET AUTOSTART_MODEL=%1
)

@echo "Using ARE_LOG_STRING path: %ARE_LOG_STRING%"
@echo "Using ARE_DEBUG_STRING path: %ARE_DEBUG_STRING%"
@echo "Using profile path: %PROFILE_PATH%"
@echo "Using autostart model: %AUTOSTART_MODEL%"
@echo "Using optional services: %ARE_OPTIONAL_SERVICES_INI%"

@set JAVA_BIN=java
@IF NOT DEFINED ARE_DEBUG_STRING SET JAVA_BIN=javaw

@if exist java\bin\java.exe (
	@set JAVA_BIN=java\bin\java.exe
	@IF NOT DEFINED ARE_DEBUG_STRING SET JAVA_BIN=java\bin\javaw
)

%JAVA_BIN% -version 2>&1  | jtester.exe

@ECHO OFF
IF ERRORLEVEL 1 GOTO QuitError

%JAVA_BIN% -jar VCChecker.jar
IF ERRORLEVEL 1 GOTO ContARE
echo NO C++ Redistributable Package found. 
echo It is highly recommended to download and install the Visual C++ Redistributable Package from Microsoft!
echo Download link for 32 bit systems: http://www.microsoft.com/en-us/download/details.aspx?id=5555
echo Several Plugins will not work without the package!
rem GOTO QuitError
Pause

:ContARE

IF EXIST asterics.ARE.jar GOTO Continue
ECHO ARE jar files not found, please unzip ARE.zip or copy jar files into this folder !
GOTO QuitError

:Continue

ECHO Deleting OSGi-Cache
rd /s/q %PROFILE_PATH%\org.eclipse.osgi

ECHO Starting AsTeRICS Runtime Environment...
ECHO %ARE_LOG_STRING%>.logger
REM ECHO error_level:FINE>.logger

set START_CMD=
set SPLASH_SWITCH=
@IF NOT DEFINED ARE_DEBUG_STRING set START_CMD=start
@IF NOT DEFINED ARE_DEBUG_STRING set SPLASH_SWITCH=-splash:images/asterics_startup.png

%START_CMD% %JAVA_BIN% %ARE_DEBUG_STRING% %SPLASH_SWITCH% -Dosgi.clean=true -Dorg.osgi.framework.bootdelegation=* -Dorg.osgi.framework.system.packages.extra=sun.misc -DAnsi=true -Djava.util.logging.config.file=logging.properties -Deu.asterics.ARE.startModel=%AUTOSTART_MODEL% -Deu.asterics.ARE.ServicesFiles="services.ini;services-windows.ini;%ARE_OPTIONAL_SERVICES_INI%" -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration %PROFILE_PATH% -console
set ARE_LOG_STRING=
set ARE_DEBUG_STRING=

GOTO Quit

:QuitError
pause
set ARE_LOG_STRING=
set ARE_DEBUG_STRING=

:Quit
set ARE_LOG_STRING=
set ARE_DEBUG_STRING=
