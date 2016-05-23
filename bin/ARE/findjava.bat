@ECHO OFF

REM This is a helper batch file to search for a 
REM 1) a 32-bit Java Runtime Environment
REM 2) if not found, any Java Runtime Environment
REM author: Martin Deinhofer, mailtto:martin.deinhofer@technikum-wien.at


setlocal

REM uncomment for testing the single strategies
REM goto :one

:one
REM 1) in case this is an installed AsTeRICS with an embedded java in the java subfolder
if exist "java\bin\java.exe" (
	set "java_bin=java\bin"
	call :foundjava "Step One"
	goto:eof
)

:two
REM 2) otherwise, first search for the current 32bit jre installed
set "branch=HKLM\Software\JavaSoft\Java Runtime Environment"
call :findjava32 jre

if exist "%jre%\bin\java.exe" (
	set "java_bin=%jre%\bin"
	call :foundjava "Step Two"
	goto:eof
)

:three
REM 3) otherwise, search for the current 32bit jdk having an embedded jre installed
set "branch=HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit"
call :findjava32 jre
if exist "%jre%\jre\bin\java.exe" (
	set "java_bin=%jre%\jre\bin"
	call :foundjava "Step Three"
	goto:eof
)

:four
REM 4) next, try the programdata symbolic link
if exist "%ProgramData%\Oracle\Java\javapath32\java.exe" (
	set "java_bin=%ProgramData%\Oracle\Java\javapath32"
	call :foundjava "Step Four"
	goto:eof
)

:five
REM 5) next, search for an entry in the %ProgramFiles(x86)% subfolder, because this is the normal location for java 32-bit installations
FOR /D %%d IN ("%ProgramFiles(x86)%\Java\*") DO (
	if exist "%%d\bin\java.exe" (
		set "java_bin=%%d\bin"
		call :foundjava "Step Five"
		goto:eof
	)
)

:six
REM 6) Finally, if we could not find anything else, search in the 64-bit  standard installation folder
FOR /D %%d IN ("%ProgramFiles%\Java\*") DO (
	if exist "%%d\bin\java.exe" (
		set "java_bin=%%d\bin"
		call :foundjava "Step Six"
		goto:eof
	)
)

:nojava
REM As I don't know of a possibility to popup a message, use "start" to popup a new batch command window. This is a trick to force the user recognizing the message.
start echo "No Java found, please install a 32-bit Java Runtime Environment: http://www.java.com/de/download/manual.jsp"
exit /b 1

:foundjava
echo Found java path through %1: "%java_bin%"
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