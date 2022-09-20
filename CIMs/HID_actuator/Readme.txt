

AsTeRICS Universal HID actuator CIM, FHTW

The firmware fits for the Atmel AVR AT90USB1286 microcontroller 
(e.g. mounted on the "teensy++" board, see http://www.pjrc.com/store/teensypp.html)

The HID-actuator provides Mouse, AbsoluteMouse, Keyboard and Joystick functionality in a "quadruple-role" USB HID device.
Input data is sent from the AsTeRICS ARE plugins RemoteMouse, RemoteKeyboard, RemoteJoystick or RemoteTablet, via a Virtual COM Port / UART.
The Communication Protocol layout can be found in the AsTeRICS developer manual, section "CIM Protocol".

The source code for USB HID interaction is based upon the open source LUFA library, 
the Lightweight USB framework for AVRs, which is permissive also for commercial use, see:
http://www.fourwalledcubicle.com/LUFA.php
License and copyright notice: LUFA/README.txt

Furthermore, some code of the PS3 Teensy HID Gamepad, Copyright (C) 2010 Josh Kropf <josh@slashdev.ca>
was useful to get the PS3-button working, many thanks for that !
(released under the GPL license, http://git.slashdev.ca/ps3-teensy-hid )

Important files / file extensions:
  HID_actuator.aps:  project file can be opened/built with AVRStudio 4 + WinAVR, 
  HID_actuator.hex:  binary/executable, can be downloaded via Atmel FLIP into the microcontroller

Tools folder contains:
    the Flip software for firmware download via USB and the signed driver for the Atmel microcontroller 
    a joystick-tester-tool to verify joystick operation of the HID actuator
    an initialisation firmware for the BTM-222 bluetooth module (if a wireless operation of the HID-actuator is desired)

provided versions:
   Mouse_Keyboard_Joystick: Mouse (relative and absolute), Keyboard and Joystick functionality available (quadruple role USB HID device)
   Joystick_only:  First draft version of a single joystick emulator.



