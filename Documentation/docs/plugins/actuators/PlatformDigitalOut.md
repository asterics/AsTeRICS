---
title: PlatformDigitalOut
---

# Platform Digital Out

### Component Type: Actuator (Subcategory: Personal Platform)

The PlatformDigitalOut plugin operates the output ports of the AsTeRICS Personal Platform.
The output ports 1-2 are open-collector outputs with a deactivated pull-up resistor.

![Screenshot: PlatformDigitalOut plugin](./img/PlatformDigitalOut.jpg "Screenshot: PlatformDigitalOut plugin")  
PlatformDigitalOut plugin

## Requirements

This component requires the Core CIM (CIM Id: 0x0602) of the AsTeRICS Personal Platform.

![The AsteRICS Personal Platform (preliminary version)](./img/PersonalPlatform.jpg "The AsteRICS Personal Platform (preliminary version)")  
The AsteRICS Personal Platform

## Input Port Description

- **command \[string\]:** The plugin reacts to incoming action strings starting with "@GPIO:" and a command.
  Valid commands are "set", "clear", "toggle" and "press".
  The command has to be followed by a comma and the port number, for example: "@GPIO:set,1" or "@GPIO:toggle,2".
  The following examples illustrate the available action strings:
  - \_"@DIGITALOUT:set,1":\_Output port 1 of the Personal Platform will be set (5 Volt)
  - \_"@DIGITALOUT:clear,2":\_Output port 2 of the Personal Platform will be cleared (0 Volt)
  - \_"@DIGITALOUT:toggle,1":\_Output port 1 of the Personal Platform will be changed

## Event Listener Description

- **setOutput1 to setOutput2:** an incoming event on these ports will cause the corresponding output port to go to the high level.
- **clearOutput1 to clearOutput2:** an incoming event on these ports will cause the corresponding output port to go to the low level.
