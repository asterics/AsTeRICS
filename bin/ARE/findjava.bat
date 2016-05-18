@ECHO OFF
setlocal

REM in case this is an installed AsTeRICS with an embedded java in the java subfolder
if exist "java" (
	set "java_bin=java\bin"
	goto foundjava
)

REM otherwise, first search for the current 32bit jre installed
set "branch=HKLM\Software\JavaSoft\Java Runtime Environment"
call :findjava32 jre


if exist "%jre%\bin" (
	set "java_bin=%jre%\bin"
	goto foundjava
)

REM otherwise, search for the current 32bit jdk installed
set "branch=HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\JavaSoft\Java Development Kit"
call :findjava32 jre
if exist "%jre%\jre\bin" (
	set "java_bin=%jre%\jre\bin"
	goto foundjava
)

:nojava
REM As I don't know of a possibility to popup a message, use "start" to popup a new batch command window. This is a trick to force the user recognizing the message.
start echo "No Java found, please install a 32-bit Java Runtime Environment: http://www.java.com/de/download/manual.jsp"
exit /b 1

:foundjava
echo "Found 32-bit java path: %java_bin%"
endlocal&set "%~1=%java_bin%"
exit /b 0


:findjava32
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
