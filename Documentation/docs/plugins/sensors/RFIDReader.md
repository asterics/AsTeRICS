  
---
RFID Reader
---

# RFID Reader

### Component Type: Sensor (Subcategory: Others)

The RFIDReader component provides an interface to the ID-Innovations RFID reader modules. These modules are available for example from Sparkfun electronics. The recognized ID-Tags are transferred from the module to a COM port, where the RFIDReader components reads the ID and puts it to the output port as an ASCII string. The RFIDReader can be useful to switch to a different a model (e.g. for another users or use cases) or to change parameters of a running model.

![Screenshot: RFIDReader plugin](img/RFIDReader.jpg "Screenshot: RFIDReader plugin")  
RFIDReader plugin

## Requirements

This software component requires an ID-Innovations RFID reader to be connected to a COM port via the Sparkfun USB breakout board or a UART/USB bridge or converter cable. Dedicated drivers have to be installed (e.g. the FTDI VCP drivers for the Sparkfun breakout board). All needed components are contained in the Sparkfun RFID Starter Kit:

![RFID Starter Kit](img/RFIDReader_kit.jpg "RFID Starter Kit")  
RFID Starter Kit

## Output Port Description

*   **tagID \[string\]:** A recognized TagID is put out on this port as a sequence of 12 hexadecimal numbers in an ASCII string.

## Properties

*   **comPort \[string\]:** The COM port where the RFID reader module is connected to (e.g "COM5")
*   **baudRate \[integer\]:** The baudrate for communication with the RFID reader module, should be 9600 for the ID Innovations modules