@ECHO OFF

REM This is a helper batch file to search for a 
REM 1) a 32-bit Java Runtime Environment
REM 2) if not found, any Java Runtime Environment
REM author: Martin Deinhofer, mailtto:martin.deinhofer@technikum-wien.at


setlocal
REM echo "params: %1, %2"
set JAVA_EXE=%2

REM uncomment for testing the single strategies
REM goto :seven

:one
REM 1) in case this is an installed AsTeRICS with an embedded java in the java subfolder
if exist "java\bin\%JAVA_EXE%" (
	set java_bin="java\bin\%JAVA_EXE%"
	echo Step One, found java path:
	goto foundjava
)

:two
REM 2) otherwise, first search for the current 32bit jre installed
set "branch=HKLM\Software\JavaSoft\Java Runtime Environment"
call :findjava32 jre

if exist "%jre%\bin\%JAVA_EXE%" (
	set java_bin="%jre%\bin\%JAVA_EXE%"
	echo Step Two, found java path:
	goto foundjava	
)

:three
REM 3) otherwise, search for the current 32bit jdk having an embedded jre installed
set "branch=HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit"
call :findjava32 jre
if exist "%jre%\jre\bin\%JAVA_EXE%" (
	set java_bin="%jre%\jre\bin\%JAVA_EXE%"
	echo Step Three, found java path:
	goto foundjava
)

:four
REM 4) next, try the programdata symbolic link
if exist "%ProgramData%\Oracle\Java\javapath32\%JAVA_EXE%" (
	set java_bin="%ProgramData%\Oracle\Java\javapath32\%JAVA_EXE%"
	echo Step Four, found java path:
	goto foundjava
)

:five
REM 5) next, search for an entry in the %ProgramFiles(x86)% subfolder, because this is the normal location for java 32-bit installations
FOR /D %%d IN ("%ProgramFiles(x86)%\Java\*") DO (
	if exist "%%d\bin\%JAVA_EXE%" (
		set java_bin="%%d\bin\%JAVA_EXE%"
		echo Step Five, found java path:
		goto foundjava
	)
)

:six
REM 6) Finally, if we could not find anything else, search in the 64-bit  standard installation folder
FOR /D %%d IN ("%ProgramFiles%\Java\*") DO (
	if exist "%%d\bin\%JAVA_EXE%" (
		set java_bin="%%d\bin\%JAVA_EXE%"
		echo Step Six, found java path:
		goto foundjava
	)
)

:seven
REM 7) Try if %JAVA_EXE% is in path, because this could be any other than the standard paths in theory.
set java_bin="%JAVA_EXE%"
echo Step Seven, found java path:
goto foundjava


:nojava
REM As I don't know of a possibility to popup a message, use "start" to popup a new batch command window. This is a trick to force the user recognizing the message.
start echo "No Java (>= 1.7, 32-bit) found, please install a correct Java Runtime Environment: http://www.java.com/de/download/manual.jsp"
exit /b 1

:foundjava
echo %java_bin%

REM test, if java is ok
REM ERRORLEVEL: 9009: command not found --> path, program not existent or executable
REM ERRORLEVEL: 1: jre version < 1.7
REM ERRORLEVEL: 0: JRE OK
%java_bin% -version 2>&1 | jtester.exe
REM echo java version "1.6.0_12" |jtester.exe
IF NOT ERRORLEVEL 0 goto :nojava

endlocal&set "%~1=%java_bin%"
exit /b 0


:findjava32
REM Search in given registry key for a JRE
REM Thanks to stackoverflow: http://stackoverflow.com/questions/28961386/how-to-determine-location-of-32bit-java
@ECHO OFF
setlocal

REM echo %branch%
if defined PROGRAMFILES(x86) (set "reg=%windir%\SysWOW64\reg") else set "reg=reg"

for /f "tokens=3" %%v in ('%reg% query "%branch%" /v "CurrentVersion" ^| find "REG_SZ"') do (
    for /f "tokens=2*" %%I in ('%reg% query "%branch%\%%v" /v "JavaHome" ^| find "REG_SZ"') do (
        set "$JAVA=%%J"
    )
)

endlocal&set "%~1=%$JAVA%"