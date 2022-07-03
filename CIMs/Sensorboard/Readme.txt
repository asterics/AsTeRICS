

AsTeRICS sensorboard CIM, FHTW

The firmware fits for the Atmel AVR AT90USB1286 microcontroller 
(e.g. mounted on the "teensy++" board, see http://www.pjrc.com/store/teensypp.html)

The sensorboard CIM provides a wide range of sensor values from different sources to the AsTeRICS sensorboard plugin.
Depending on the connected sensors, the sensorboard PCB can deliver the following values:
  *) 9 Degree-Of-Freedom IMU data (if the 9-DOF Sensor stick is connected, see: https://www.sparkfun.com/products/10724)
  *) up to 4 infrared led tracking coordinates (x/y each) (if the IR-tracking sensor from a WiiMote device is connected to the I2C bus, see:http://www.instructables.com/id/Wii-Remote-IR-Camera-Hack/?lang=ja)
  *) a pressure sensor value e.g. for a sip/puff sensor (if the MP§V7007GP pressure sensor module is connected)

The sensorboard  can be used for eyetracking, IMU controlled assistive input devices and various other applications.
Please refer to the AsTeRICS user manual and demo models for more information.

Important files/folders:
  firmware/sensorboard.aps:  project file, can be opened/built with AVRStudio 4 + WinAVR
  firmware/sensorboard.hex:  binary/executable, can be downloaded via Atmel FLIP into the microcontroller

  subfolder "WiiCamPCB": schematics/layout files for the GL850 PCB,  featuring:
       the AT90USB1286 microcontroller, 
       mounting options for the Sparkfun SensorsStick + pressure sensor
       I2C connector for the WiiMote IR camera

  subfolder "GL850PCB":  schematics/layout files for the GL850 PCB,  featuring: 
       the AT90USB1286 microcontroller, 
       mounting options for the Sparkfun SensorsStick + pressure sensor
       UART IR-sensor interface (for PAC7001 IR-Cam)
       a USB hub so that a modified webcam for eye-tracking can be connected to the PCB without adding a second USB cable

