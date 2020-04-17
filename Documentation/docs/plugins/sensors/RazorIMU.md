---
title: RazorIMU
---

# Razor IMU

Component Type: Sensor (Subcategory: Inertial Measurement)

The RazorIMU plugin provides the serial output of the 9DOF Razor Inertial Measurement Unit at three output ports: pitch, yaw and roll. These three values represent the orientation in terms of rotation along the three axes of the coordinate system.

![Screenshot: RazorIMU plugin](./img/RazorIMU.jpg "Screenshot: RazorIMU plugin")

RazorIMU plugin

## Requirements

This plugin requires a 9DOF Razor IMU module connected to the AsTeRICS platform via a UART/USB converter cable (e.g. an FTDI cable) which creates a COM port. The Razor IMU module (and also the converter cable) is available at SparkFun electronics. It has to be updated with the Sparkfun 9DOF Razor IMU AHRS firmware. (The COM port must be determined by looking in the device manager window and cannot be automatically detected like with dedicated AsTeRICS CIMs.) The required baud rate is 57600.

![RazorIMU](./img/RazorIMU_picture.jpg "RazorIMU")

RazorIMU module

## Output Port Description

- **pitch \[double\]:** The value for the current pitch.
- **yaw \[double\]:** The value for the current yaw.
- **roll \[double\]:** The value for the current roll.

## Properties

- **comPort \[String\]:** The name of the COM port the IMU is connected to.
- **baudRate \[integer\]:** The baud rate the IMU is transferring its data at.
- **operationMode \[String\]:** Designates the operation mode (currently only "PitchYawRoll" is available).
