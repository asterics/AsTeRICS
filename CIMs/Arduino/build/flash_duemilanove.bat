@echo off
IF "%1"=="" GOTO Usage
avrdude.exe -pm328p -P %1 -c stk500v1 -b 57600 -U flash:w:Arduino.hex -F
pause
goto end
:Usage
echo Usage: flash COMPortName
echo for example flash COM4
:end