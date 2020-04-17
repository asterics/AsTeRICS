##

## DigitalIn

# Digital In

### Component Type: Sensor (Subcategory: Generic Control Input)

The DigitalIn component provides an interface to read the digital inputs of the GPIO CIM. On state changes of the connected signals (transitions to high or low level), the component generates corresponding trigger-events.

![Screenshot: DigitalIn plugin](./img/DigitalIn.jpg "Screenshot: DigitalIn plugin")  
DigitalIn plugin

## Requirements

This component requires the DigitalIn (GPO) CIM (CIM Id: 0x0701) connected to an USB port.

## Event Trigger Description

- **in1High to in6High:** Each of these event ports is linked to one input port, if the device connection to this input port delivers a signal which changes to high level, an event will be raised on the corresponding port.
- **in1Low to in6Low:** Each of these event ports is linked to one input port, if the device connection to this input port delivers a signal which changes to low level, an event will be raised on the corresponding port.

## Properties

- **activateEventIn1 to activateEventIn6 \[Boolean\]:** These properties declare for each port whether or not a signal transition on the actual input port should result in an event being triggered in the ARE. If a property is set true for one input, it will raise events on signal transitions, if it is set to false it will not.
- **periodicUpdate:** Period in milliseconds for update messages about state of device inputs. If the property is set to zero, the plugin will receive messages from the CIM on signal transitions, if the property is non-zero, the CIM will send status messages in the defined intervals without extra event messages on signal changes. Both modes will have the same effect on the software plugin, it will raise events on its trigger ports.
- **uniqueId:** unique number of the CIM - if more than one CIMs of the same type are used. The module flashes a LED for identification when the ID is selected. **Supports value suggestions from ARE (dynamic property)**
