@REM VERY IMPORTANT: The two lines ensure that the script is executed in the current directy (ARE), also when called
@REM as administrator
@setlocal enableextensions
@cd /d "%~dp0"

set ARE_CONSOLE_LOGLEVEL=FINE
set ARE_DEBUG_STRING="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044"

start.bat %*

set ARE_CONSOLE_LOGLEVEL=
set ARE_DEBUG_STRING=