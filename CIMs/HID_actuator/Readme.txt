

First version of the AsTeRICS Universal HID actuator, FHTW

The firmware fits for the Atmel AVR AT90USB1286 microcontroller mounted on the "teensy++" PCB. 
The teensy is a low-cost evaluation platform (22 Euros), see http://www.pjrc.com/store/teensypp.html

The HID-actuator provides Mouse, Keyboard and Joystick functionality in a triple role USB HID device.
Input data is sent from the AsTeRICS ARE via Virtual COM Port / UART.
The Communication Protocol layout can be found at AsTeRICS BSCW, section WP3/firmware:
https://bscw.integriert-studieren.jku.at/bscw/bscw.cgi/41214

The source code for USB HID interaction is based upon the open source LUFA library, 
the Lightweight USB framework for AVRs, which is permissive also for commercial use, see:
http://www.fourwalledcubicle.com/LUFA.php
License and copyright notice: LUFA/README.txt

Furthermore, some code of the PS3 Teensy HID Gamepad, Copyright (C) 2010 Josh Kropf <josh@slashdev.ca>
was useful to get the PS3-button working, many thanks for that !
(released under the GPL license, http://git.slashdev.ca/ps3-teensy-hid )

Important files / file extensions:
  .aps project file can be opened/built with AVRStudio 4 + WinAVR, 
       e.g.  ./Mouse_Keyboard_Joystick/HID_actuator.aps 
  .hex binary/executable, can be downloaded via Atmel FLIP into the microcontroller, 
       e.g. ./Mouse_Keyboard_Joystick/HID_actuator.hex .
       the Flip software for firmware download via USB and the signed driver for 
       the Atmel microcontroller are available in the ./Tools folder


There are following versions available:

-Mouse_Keyboard_Joystick:
  Mouse, Keyboard and Joystick functionality available (triple role USB HID device)

deprecated:
-Mouse_only:
  First draft version of a mouse emulator which is connected to an ARE demo bundle.

-Mouse_Keyboard:
  Mouse and Keyboard functionality available (dual role USB HID device)



