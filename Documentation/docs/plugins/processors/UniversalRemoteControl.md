---
title: Universal Remote Control
---

# Universal Remote Control

Component Type: Processor (Subcategory:Microcontroller Interfaces)

The Universal Remote Control (RC) enables the possibility to control all electronic devices in a household which are controlled remotely through infrared (IR) commands. THe necessary IR codes can be recorded with the RC itself and stored on a database on the computer. By choosing the manufacturer, name and function the IR code is deposed in this database and can be selected in order to send the code to the universal RC and therefore control electronic devices. The universal RC can also be used as a handheld gadget due to its built in battery and user interface. THe IR code database can be stored on an SD card.

![Screenshot: UniversalRemoteControl plugin](./img/UniversalRemoteControl.png "Screenshot: Universal Remote Control plugin")

The Universal Remote Control sensor plugin

## Requirements

For the use of the universal RC with a computer, it has to be be connected to a USB port. The firmware of the universal RC can be found in the AsTeRICS folder under /CIMs/UniversalInfraredRemoteControl/. A database will be automatically generated if there is not found one. For the usage of the universal RC as a handheld gadget, the SD card has to be formatted as a FAT16 volume and contain a database as well as the configuration file.

## Input Port Description

- **DeviceType \[string\]:** Type of the device to be controlled via IR (e.g.: TV)
- **DeviceName \[string\]:** Name of the device to be controlled via IR (e.g.: Sony, Samsung, etc.)
- **DeviceFunction \[string\]:** Name of function of the device (e.g.: On, Off, VolumeUp, etc.)

## Event Listener Description

- **SendIRCode:** Send an IR Code to the Universal Remote Control
- **RecordIRCode:** Record an IR Code through the Universal Remote Control

## Event Triggerer Description

- **StartRecord:** will be triggered when Universal Remote Control is recording
- **StopRecord:** will be triggered when Universal Remote Control has finished recording

## Properties

- **IRCodeFilePath \[string\]:** Filepath to the file, where the IR Codes are stored.

## IR Code Database

The database which contains the IR codes as well as the information about the type and name of the device and the specific function is a comma separated value file. The first value is the type, the second one is the name and the third one is the function. The following 512 values are the IR code. This database is automatically generated and maintained if new IR codes are recorded with the Universal Remote Control with IR functions.

## Configuration File

The settings of the universal RC can be stored in the config.csv file. This is a comma separated value file and contains configurations such as the speed and sensibility of the rotary encoder and external buttons as well as the name of the file that contains the IR codes. The content of the default config.csv file is:

    		JoystickSpeed,120
    		JoystickSensibility,3
    		File,IRCODES.CSV
    		Sort,2
