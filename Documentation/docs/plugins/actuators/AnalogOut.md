---
title: AnalogOut
---

# Analog Out

Component Type: Actuator (Subcategory: Generic Control Output)

This plugin communicates with the DAC CIM and operates the analog outputs of the module.
The plugin provides four input ports which correspond to the four DAC outputs of the CIM.

![Screenshot: AnalogOut plugin](./img/AnalogOut.jpg "Screenshot: AnalogOut plugin")

AnalogOut plugin

## Requirements

This software component requires an DAC CIM (CIM ID: 0x0401) connected to an USB port.

![ADC/DAC CIM](./img/AnalogOut_CIM.jpg "ADC/DAC CIM")

ADC/DAC CIM

## Input port Description

- **out1 to out4 \[integer\]:** these input ports correspond to the DAC output of the same number on the CIM.
  The input is an integer and has a valid range between 0 and 240.
  The values represent the output voltage in 100mv steps, e.g. a value of 10 represents 1.0V, 143 represents 14.3V.
- **uniqueId:** unique number of the CIM - if more than one CIMs of the same type are used.
  The module flashes a LED for identification when the ID is selected.
