:Initialization
CLS
@ECHO AsTeRICS ARE Version 2.3
@ECHO.
set JAVA_BIN="java"

if exist java\bin\java.exe (
	set JAVA_BIN=java\bin\java.exe
)

%JAVA_BIN% -version 2>&1  | jtester.exe

@ECHO OFF
IF ERRORLEVEL 1 GOTO QuitError

%JAVA_BIN% -jar VCChecker.jar
IF ERRORLEVEL 1 GOTO ContARE
echo NO C++ Redistributable Package found. 
echo It is highly recommended to download and install the Visual C++ Redistributable Package from Microsoft!
echo Download link for 32 bit systems: http://www.microsoft.com/en-us/download/details.aspx?id=5555
echo Download link for 64 bit systems: http://www.microsoft.com/en-us/download/details.aspx?id=14632
echo Several Plugins will not work without the package!
rem GOTO QuitError
Pause

:ContARE

IF EXIST asterics.ARE.jar GOTO Continue
ECHO ARE jar files not found, please unzip ARE.zip or copy jar files into this folder !
GOTO QuitError

:Continue

ECHO Deleting OSGi-Cache
rd /s/q profile\org.eclipse.osgi

ECHO Starting AsTeRICS Runtime Environment with Debug output ...
ECHO error_level:FINE>.logger
%JAVA_BIN% -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 -Dorg.osgi.framework.bootdelegation=* -DAnsi=true -Djava.util.logging.config.file=logging.properties  -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration profile -console

GOTO Quit

:QuitError
pause

:Quit