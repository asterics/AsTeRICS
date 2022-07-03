
This folder contains firmware for the Sparkfun "Razor IMU" board, 
(a 9 degrees of freedom Inertial Measurement Unit, see:https://www.sparkfun.com/products/10736)


Instructions for operation or the "RazorIMU" with the corresponding AsTeRICS plugin:

*) plug in Razor IMU board via FTDI cable, make sure windows finds the serial to USB device, 
*) note the assigned Com port in Device Manager 


*) for 9DOF AHRS operation (outputs Attitude and Heading):
  download arduino programming environment from www.arduino.cc 
  use Arduino serial monitor to check device is working (push reset button on Razor!) 
  set correct board (Pro or Pro mini, ATmega168 or ATmega328 - see chip description) 
  set correct port 
  upload Razor-AHRS firmware to board 
  baud rate in operation is 57600
  the license compatibility of the AHRS code (creative commons) has to be checked for your application


*) for 9DOF raw firmware (outputs 3 axis of acceleration, angle velocity and compass, currently not supported by ARE plugin):
  compile C-code using WinAVR GCC (+ AVRStudio if preferred)
  upload firmware using avrdude
  baud rate in operation is 38400

thanks to all contributors of this frimware !!
see also:

http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Sensors/IMU/SF9DOF_AHRS.zip
https://github.com/ptrbrtz/razor-9dof-ahrs/wiki/Tutorial
https://github.com/a1ronzo/SparkFun-9DOF-Razor-IMU-Test-Firmware