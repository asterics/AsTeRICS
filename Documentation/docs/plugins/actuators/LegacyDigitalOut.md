---
title: Legacy Digital Out
---

# Legacy Digital Out

## Component Type: Actuator (Subcategory: Generic Control Output)

The DigitalOut plugin operates the output ports of the legacy GPIO CIM (CIM Id: 0x0201). The output ports 1-4 are open-collector outputs, where a pull-up resistor can be activated or deactivated using the plugin's properties. The output ports 5-8 are relais outputs where loads can be connected via a galvanic isolation barrier. The plugin provides event listener ports which serve the activation or deactivation of an output channel, and a command port which accepts string parameters to set, clear and toggle particular output channels.

![Screenshot: Legacy DigitalOut plugin](./img/LegacyDigitalOut.jpg "Screenshot: LegacyDigitalOut plugin")

Legacy DigitalOut plugin

## Requirements

This component requires the GPIO CIM (CIM Id: 0x0201) to be connected to an USB port.

![GPIO CIM](./img/DigitalOut_CIM.jpg "GPIO CIM")

Legacy GPIO CIM

## Input port Description

- **action \[string\]:** The plugin reacts to incoming action strings starting with "@GPIO:" and a command. Valid commands are "set", "clear", "toggle" and "press". The command has to be followed by a comma and the port number, for example: "@GPIO:set,1" or "@GPIO:toggle,2". The "press"-command toggles the given output port two times with a delay of 500 milliseconds. The following examples illustrate the available action strings:
  - _"@DIGITALOUT:set,1":_ Pin 1 of the GPIO CIM will be set
  - \_"@DIGITALOUT:clear,2":\_Pin 2 of the GPIO CIM will be cleared
  - _"@DIGITALOUT:toggle,1":_ Pin 1 of the GPIO CIM will be changed
  - _"@DIGITALOUT:press,4":_ Pin 4 of the GPIO CIM will be cleared and after 500ms it will be set again

## Event Listener Description

- **setOutput1 to setOutput8:** an incoming event on these ports will cause the corresponding output port on the CIM to go to the high level.
- **clearOutput1 to clearOutput8:** an incoming event on these ports will cause the corresponding output port on the CIM to go to the low level.

## Properties

- **pullupStateOut1 to pullupStateOut4 \[boolean\]:** These properties specify if the internal pullup resistor shall be activated on the respective open collector output channels.
