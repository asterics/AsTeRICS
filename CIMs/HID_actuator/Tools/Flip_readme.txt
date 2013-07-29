
This information is for developers who want to manufacture and program their own 
HID actuator dongle with wireless support (BTM-222 module) using the FLIP programmer by Atmel.


"Flip" is a tool to download firmware into the flash memory of microcontrollers.
It can be used to update the firmware of the AsTeRICS HID actuator (.hex file for AT90USB1286).

When the HID actuator hardware is connected for the first time, 
the bluetooth module has to be initialised by loading the bt_init.hex firmware into the microcontroller.

1) Install Atmel Flip
2) Connect the HID actuator dongle
   If the Atmel device is not recognized in the Windows device manager, install the driver from the
   "usb" subfolder of Atmel Flip (e.g. C:\Programs (x86)\Atmel\Flip 3.4.5\usb"
3) Start Atmel Flip and select MCU type (AT90USB1286) and connect via USB
4) select the bt_init.hex file and Run the programming process
5) detach the HID actuator dongle after the programming sequence has been completed
6) start the hid_listen.exe program to see the output of the initialisation sequence
7) attach the HID actuator dongle and watch the output in the hid-listen window.


Now, the BT module should be correctly configured and the HID firmware can be loaded into the dongle

1) disconnect / reconnect the HID actuator dongle or press its reset button
2) Start Atmel Flip and select MCU type (AT90USB1286) and connect via USB
3) select the hid_actuator.hex file and Run the programming process
4) detach the HID actuator dongle after the programming sequence has been completed


Now the dongle should be ready for use with the AsTeRICS platform

