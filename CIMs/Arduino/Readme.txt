
Arduino CIM firmware by FHTW,
for the Atmel AVR ATmega328 microcontroller mounted on the "Arduino UNO" or "Arduino Duemilanove" boards. 
(The Arduino is a low-cost microcontroller platform, see http://www.arduino.cc)

This firmware provides access to the digital I/O Pins, PWM functions and 6 ADC Channels via the AsTeRICS Arduino plugin.


Some issues have been reported for older Arduino boards which are not recognized by the ARE.
A solution can be to disable the "auto-reset" function of the Arduino board (which starts the bootloader when the COM port is opened)
by cutting the solder bridge between the two solder pads labelled "RESET-EN" with a knife. 
If needed, the auto-reset feature can be enabled again later by re-connecting the solder bridge.


Important files in the "build" subfolder:

  flash.bat:  this batch file transfers the Arduino.hex file into the Arduino Uno microcontroller
              It is started from a command shell and needs one parameter: the COM-port
              where the Arduino is connected. E.g.  "flash COM4"

  flash_duemilanove.bat:  this batch file transfers the Arduino.hex file into the (older) Arduino Duemilanove microcontroller
              It is started from a command shell and needs one parameter: the COM-port
              where the Arduino is connected. E.g.  "flash_duemilanove COM4"

  Arduino.hex: the binary executable file for the Arduino, which provides the basic 
              input/output functions via the CIM protocol, so that the Arduino plugin 
              for the ARE can be used.
          
  Avrdude.exe: the Avrdude firmware update tool which is used to transfer the hex file
               into the microcontroller via COM-Port and bootloader. 
               Avrdude is part of the WinAVR distribution, please have a look at+
               http://winavr.sourceforge.net/
               and http://www.nongnu.org/avrdude/ 