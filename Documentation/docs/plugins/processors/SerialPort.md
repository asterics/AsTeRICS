---
title: SerialPort
---

# SerialPort

Component Type: Processor (Subcategory: Communication)

The SerialPort plugin can be used to send and receive data from serial devices (e.g. microcontrollers/embedded devices/wireless links etc.) via a serial port (COM port) or virtual serial port. The plugin adds some convinience methods to append or wait for termination characters. Furthermore the sending and receiving of byte values is supported.

## Input Port Description

*   **send \[string\]:** string which will be sent to the microcontroller / embedded module
*   **sendBytes \[byte\]:** bytes to be sent to the serial port.

_Note:_ Any data sent to one of the input ports tries to open the given COM port or serial device with given cimId, if the port is not currently open. Therefore sending a byte/command to an input port can be used to trigger a CIM-rescan or retry to open a device with specified cimId. The current status can be monitored by output values of "opPortStatus".

## Output Port Description

*   **received \[string\]:** string which has been received (was sent from the microcontroller / embedded module)
*   **receivedBytes \[byte\]:** received bytes from the serial port.
*   **opPortStatus \[string\]:** status for mode with specified property "cimId". The following outputs are possible:
    *   "IN\_PORT\_RESCAN": sent if CIM scanning is currently in progress and was not just triggered
    *   "NEW\_PORT\_RESCAN": sent if a new CIM scanning was just triggered and CIM scanning is now in progress

## Properties

*   **ComPort \[string\]:** Defines the COM Port of the target serial device. e.g. COM0 or /dev/ttyS0
*   **BaudRate \[integer\]:** Defines the Baudrate for the communication. It must match the baudrate of the target device
*   **sendStringTerminator \[combobox selection\]:** Append a character when sending the string, e.g. CR, LF, CR+LF, 0
*   **receiveStringTerminator \[combobox selection\]:** wait for a termination character when receiving characters (-> receive a string)
*   **sendBytesBufferSize \[integer\]:** Wait for the given number of bytes before sending them to the serial port.
*   **cimId \[string\]:** If specified it is tried to open a raw COM port for a device with the given CIM-ID. If no device with the given CIM-ID is found a CIM rescan is triggered. The accepted format is a hex-value with prefix "0x", e.g. "0xa401" for FLipMouse. Status of CIM scan/rescan is sent to "opPortStatus" - see port description for details.