---
title: Sensorboard
---

# Sensorboard

### Component Type: Sensor (Subcategory:Sensor Modules)

This component provides the sensor signals from the Sensorboard CIM module, which has been developed in course of the Master Thesis of Yat-Sin Yeung at the UAS Technikum Wien. The Sensorboard is part of the head-mounted Smart Vision Module setup but can also be used as a stand-alone unit for movement analysis. The Sensorboard contains a 3-axis accelerometer, a 3-axis gyroscope, a 3-axis compass module, one analogue pressure sensor (a sip/puff sensor) and a connection to an optical IR-object tracking sensor which can track the position of up to 4 infrared LEDs in the field of view of the sensor. Furthermore, the Sensorboard contains a USB hub so that a USB camera can be connected.

The sensorboard is necessary for the headpose-compensated eye gaze tracking applications of the SVM. It can be used also for other applications like remote IR-led tracking or as inertial measurement unit. For a detailled description of the Sensorboard and its application for eye-tracking please refer to the files Documentation/DIYGuides/SVM_Eyetracking_Yeung.pdf and Documentation/DIYGuides/SmartVisionModule.pdf

![Screenshot: Sensorboard plugin](./img/Sensorboard_plugin.jpg "Screenshot: Sensorboard plugin")  
the Sensorboard plugin

## Requirements

The Sensorboard has to be connected to an USB port. Design files and firmware of the Sensorboard can be found in the folder CIMs/Sensorboard.

![Screenshot: the Sensorboard PCB](./img/Sensorboard.jpg "Screenshot: Sensorboard PCB")  
The Sensorboard PCB

## Output Port Description

- **accX/Y/Z \[integer\]:** the three axis output of the acceleration sensor
- **gyroX/Y/Z \[integer\]:** the three axis output of the gyroscope sensor
- **compassX/Y/Z \[integer\]:** the three axis output of the compass sensor
- **pt1x - pt4x \[integer\]:**
- **pt1y - pt4y \[integer\]:** The x/y corrdinates of the IR-LED tracking camera (0-1022, 1023 if no LED detected)
- **pressure \[integer\]:** the output of the pressure (sip/puff) sensor

## Properties

- **refreshInterval \[integer\]:** the refresh interval for sensor values in milliseconds (should not be less than 20).
