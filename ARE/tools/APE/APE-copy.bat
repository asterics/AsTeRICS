@ECHO OFF
@REM VERY IMPORTANT: The two lines ensure that the script is executed in the current directy (ARE), also when called
@REM as administrator
@setlocal enableextensions
REM @cd /d "%~dp0"
set APE_DIR=%~dp0

java %* -jar %APE_DIR%\APE.jar 