
Plug in Razor IMU board via FTDI cable, make sure windows finds the serial to USB device 
Check com port assigned in Device Manager 


for 9DOF AHRS operation (outputs Attitude and Heading):
--------------------------------------------------------
Download arduino programming environment from www.arduino.cc 
Use Arduino serial monitor to check device is working (push reset button on Razor!) 
Set correct board (Pro or Pro mini, ATmega168 or ATmega328 - see chip description) 
Set correct port 
Upload Razor-AHRS firmware to board 
baud rate in operation is 57600

for 9DOF raw firmware (outputs 3 axis of acceleration, angle velocity and compass):
-----------------------------------------------------------------------------------
compile C-code using WinAVR GCC (+ AVRStudio if preferred)
upload firmware using avrdude
baud rate in operation is 38400


important Notes: 
----------------
 - the AsTeRICS IMU plugin currently only works with the AHRS firmware. 
 - the license compatibility of the AHRS code (creative commons) has to be checked