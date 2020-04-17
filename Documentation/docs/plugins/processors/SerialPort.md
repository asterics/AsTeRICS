 

---
title: SerialPort
---

# SerialPort

Component Type: Processor (Subcategory: Communication)

The SerialPort plugin can be used to send and receive data from serial devices (e.g. microcontrollers/embedded devices/wireless links etc.) via a serial port (COM port) or virtual serial port. The plugin adds some convinience methods to append or wait for termination characters. Furthermore the sending and receiving of byte values is supported.

## Input Port Description

*   **send \[string\]:** string which will be sent to the microcontroller / embedded module
*   **sendBytes \[byte\]:** bytes to be sent to the serial port.

## Output Port Description

*   **received \[string\]:** string which has been received (was sent from the microcontroller / embedded module)
*   **receivedBytes \[byte\]:** received bytes from the serial port.

## Properties

*   **ComPort \[string\]:** Defines the COM Port of the target serial device. e.g. COM0 or /dev/ttyS0
*   **BaudRate \[integer\]:** Defines the Baudrate for the communication. It must match the baudrate of the target device
*   **sendStringTerminator \[combobox selection\]:**Append a character when sending the string, e.g. CR, LF, CR+LF, 0
*   **receiveStringTerminator \[combobox selection\]:**wait for a termination character when receiving characters (-> receive a string)
*   **sendBytesBufferSize \[integer\]:**Wait for the given number of bytes before sending them to the serial port.