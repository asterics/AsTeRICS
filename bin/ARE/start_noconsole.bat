
:Initialization
CLS
@ECHO OFF
@ECHO AsTeRICS ARE Version 2.2
set JAVA_BIN="java"

if exist java\bin\java.exe (
	set JAVA_BIN=java\bin\java.exe
	set JAVAW_BIN=java\bin\javaw.exe
)

%JAVA_BIN% -version 2>&1  | jtester.exe

IF ERRORLEVEL 1 GOTO QuitError

IF EXIST asterics.ARE.jar GOTO Continue
ECHO ARE jar files not found, please unzip ARE.zip or copy jar files into this folder !
GOTO QuitError

:Continue

ECHO Starting AsTeRICS Runtime Environment ...
ECHO error_level:WARNING>.logger

REM use next two lines for starting without the shell window
REM start javaw -splash:images/asterics_startup.png -Djava.util.logging.config.file=logging.properties -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration profile -console
REM sleeper 5


REM use the next line instead for starting with the shell window
if exist java/bin/javaw.exe (
	start java/bin/javaw.exe -splash:images/asterics_startup.png -Djava.util.logging.config.file=logging.properties -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration profile -console
) else (
	start javaw -splash:images/asterics_startup.png -Djava.util.logging.config.file=logging.properties -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration profile -console
)


CHOICE /C:AB /D:A /T:8 > NUL
goto end

:QuitError
pause

:end
