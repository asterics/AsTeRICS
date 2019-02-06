## Communication Interface Modules and Protocol
    

This section describes the communication protocol between the AsTeRICS Runtime Environment and the Communication Interface Modules (CIMs) via the USB standard interface.

The CIM – protocol is a bi-directional communication standard between ARE and the external modules. As described in chapter 5.1, the ARE provides services for connection and communication with external hardware modules, if these modules support the CIM communication protocol in their firmware. The CIM protocol defines a unique ID for the CIM type, and commands for reading and writing so-called “features” from/to the CIM.

All USB CIMs will be identified and will communicate via USB CDC virtual serial ports. (If desired, a CIM could be connected also via a real serial port and use the same protocol.)

Usually, no system driver development is needed to obtain a virtual COM port when a CIM is plugged into the computer’s USB port: An appropriate .inf-file is sufficient to create the COM port in the Windows operating system. This .inf file has to be specified only at the first connection.

Currently, two different .inf files are supplied with the AsTeRICS Runtime distribution:

*   the Arduino.inf (for the Arduino UNO microcontroller which is used for general purpose digital I/O and analog input via the Arduino processor plugin
    
*   the .inf file for all IMA CIMs (Analog In, Digital In/Out, Acceleration CIM, ..)
    

  
  

In the communication process, the ARE acts as master and the CIM acts as slave: The CIM will usually answer only on a request from the ARE.

Additionally, CIMs could send data without being requested from the ARE – for example periodic updates of a value. These periodic updates use a reserved

The CIMs provide a full list of supported features upon an identification request. This offers flexibility if a new module type is manufactured, where already known features with known commands are integrated but the number and combination of features is different from the previous module types.

To provide the possibility to identify CIMs of the same type in the ARE (e.g. when two GPIO CIMS are connected), a unique serial number is hardcoded in firmware and can be queried via a feature request.

  
  

  
  

  
  

  
  

## Communication Mechanism and Packet Format
    

The following table shows the CIM protocol structure:

  
  

  

**Data field**

**Size (bytes)**

**Range of values**

**Description**

  

Packet ID

2

“@T” (0x4054 )

In case of the lost packet synchronization the 2-bytes packet ID helps to identify the beginning of a packet, so that a lost communication with the CIM will be resynchronized

  

ARE ID

  

( _CIM ID )_

2

  

If the packet is sent from ARE to CIM, the ARE-ID identifies the ARE software version (e.g. “0x010E” means 1.14).

If the packet is sent from CIM to ARE, the CIM-ID identifies the CIM type in the MSB and the CIM version in the LSB (e.g. “0x0105” means CIM type 0x01 = HID actuator, version 5).

The CIM version informs the ARE about specific feature deviations due to hardware and/or firmware revisions.  
The CIM may refuse to execute or respond to certain or all commands from ARE, if the ARE version value is below the minimum compatible version required

  

Data Size

2

0x0000-0x0800

Some of the commands or the answers from CIM may require optional data like the ADC/DAC values. The size says how many data is attached to the command or answer. The maximum data size is limited to 2048 bytes. If ARE sends a higher size value, the CIM will handle it as incorrect packet and it will not respond to it, but try to resynchronize the packet reception. In case there is no data attached to the packet this value will be 0x0000.

  

Serial packet  
number

1

0x00-0x7f

(_0x80-0xff for event-replies from CIM_ )

The serial number in a packet which is sent from ARE to CIM is incremented by the ARE every packet, with values ranging from 0x00 to 0x7f. The CIM sends the same value in the response packet. This helps to identify what reply belongs to which request.  
A packet which is sent from CIM to ARE without request (e.g. in reaction to an event or periodically) will have different serial numbers with the highest bit (0x80) set, incremented by the CIM

  

CIM-Feature address

2

  

This value from 0x0000 to 0xffff defines the addressed CIM-feature. The feature address 0x0000 holds a serial number which is unique for all manufactured CIMs of a specific type. All other features (and the associated addresses) will be defined for a particular AsTeRICS CIM-Type. A feature definition includes the amount of data which is expected in the optional data field. If a command is not associated with a specific feature (e.g. the request “get feature list”) the feature address can have any value and will have no effect. For a specification of currently defined features please refer to section 7.3.

  

Request Code

  

( _Reply_ code )

2

  

The LSB of this value represents a command code which is globally valid for all CIM-Types.  
If sent from ARE to CIM, the MSB specifies the transmission mode.  
If sent from CIM to ARE, the MSB holds an error/status code related to the transmission. For a detailed description of Request/Reply codes please refer to section 7.2.

Optional data

0-2048

  

The packet can contain up to 2048 bytes of additional data. The actual length is given in the “Data Size” field.

Optional CRC checksum

0 or 4

CRC32 checksum

(if CRC-Bit in “Command”-field is set)  
CRC32 with 0x04c11db7 polynomial used also in e.g. ZIP or Ethernet protocol.

  
  

  
  

  
  

**CIM Protocol Important notes:**

*   _Italic descriptions_ refer to communication from CIM to ARE
    
*   All integer values (version, data size, serial number, feature address, command, checksum) in the packet are stored in little-endian format in the packet.
    
*   The minimum packet size (without optional data, without CRC) is 11 byes,  
    the maximum packet size (2048 bytes data, CRC) is 2063 bytes.
    

## Request / _Reply_ \- Code
    

The request-/_reply_ codes have to be supported by all CIMs and specify a generic way to read/write features etc. Requests are sent from ARE to CIM, replies are sent from CIM to ARE – and are usually a direct acknowledgement to a request. The only exception is when a CIM replies data periodically or on occurrence of an event.  
All CIMs have to implement command with codes < 0x80, others can be implemented optionally (e.g. the command 0x80-“reset CIM” could be useful to re-initialise CIM-functions, 0x82-“stop CIM” could establish a failsafe state if necessary.)

A request/_reply_ consists of a high-byte (MSB) and a low byte (LSB). The LSB specifies the actual command-ID. In case the packet is sent from ARE to CIM the MSB specifies the transmission mode (e.g. with/without CRC). In case the packet is sent from CIM to ARE, the MSB holds an error / status information

  

**MSB (8-bit)**

**LSB (8-bit)**

Mode / _Status_ code

Request/_Reply_ code

  
Every request from ARE to CIM will be acknowledged by a corresponding _reply_ packet. A _reply_ packet may contain feature-associated data .

## Request/_Reply_ Code in LSB
    

  

**Request / _Reply_ code**

**Direction**

**Description**

**Expected Data**

0x00

ARE→CIM

request feature list

  

\-

_0x00_

_CIM__→__ARE_

_reply feature list_

  

_list of supported features  
(eg. 8 bytes for 4 feature addresses)_

  

  

  

  

0x10

ARE→CIM

request write feature

bytes according to feature

_0x10_

_CIM__→__ARE_

_reply write feature_

_bytes according to feature_

  

  

  

  

0x11

ARE→CIM

request read feature

bytes according to feature

_0x11_

_CIM__→__ARE_

_reply read feature_

_bytes according to feature_

  

  

  

  

_0x20_

_CIM__→__ARE_

_event reply_

_bytes according to feature_

  

  

  

  

0x80

ARE→CIM

request reset CIM

\-

_0x80_

_CIM__→ARE_

_reply reset CIM_

_\-_

  

  

  

  

0x81

ARE→CIM

request start CIM

\-

_0x81_

_CIM__→ARE_

_reply start CIM_

\-

  

  

  

  

0x82

ARE→CIM

request stop CIM

\-

_0x82_

_CIM__→ARE_

_reply stop CIM_

\-

  
  

## Mode / _Status_ code in MSB
    

  

**Mode /**

**_Status_ code**

**Direction**

**Description**

Bit 0

ARE→CIM

CRC-mode:

Bit value ==0 :CRC is not appended to packet and not checked on receiving side

Bit value ==1: CRC is checked on receiving side,  
packet is dropped if CRC wrong;

Bits 1-7

ARE→CIM

Currently not used

  

  

  

_Bit 0_

_CIM__→__ARE_

_CRC-mode, as in received packet from ARE_

_Bit 1_

_CIM__→ARE_

_Error 1: Lost packets  
(serial number mismatch)_

_Bit 2_

_CIM__→ARE_

_Error 2: CRC mismatch_

_Bit 3_

_CIM__→ARE_

_Error 3: Invalid or unsupported feature_

_Bit 4_

_CIM__→ARE_

_Error 4: Invalid ARE version_

_Bit 5_

_CIM__→ARE_

_Error 5: CIM not ready_

_Bit 6_

_CIM__→ARE_

_Currently not used_

_Bit 7_

_CIM__→ARE_

_Other Error, description available in data field_

  
  

  
  

## Feature Lists and CIM-IDs of all AsTeRICS CIMs
    

The following section defines the CIM-ID’s, the feature addresses and the expected data for a particular feature request/_reply_ for all AsTeRICS CIMs.

## HID-CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

  

  

  

  

  

**0x01**01:

HID

actuator

version 1

0x0000

r

Unique serial number

4 bytes

0x0001

w

MOUSE x/y pos  
(relative change)

4 bytes: xxyy

0x0002

w

MOUSE buttonstate

1 byte:

Bit 0=left click, Bit 1=right click, Bit3=middle click

0x0003

W

MOUSE wheel

1 byte: wheel displacement

0x0010

w

KEYBOARD keypress

2 bytes: keycode, modifier

0x0011

w

KEYBOARD keyhold

2 bytes: keycode, modifier

0x0012

w

KEYBOARD keyrelease

\----------

0x0020

w

JOYSTICK joy1pos-analog

4 bytes: xxyy

0x0021

w

JOYSTICK joy2pos-analog

4 bytes: xxyy

0x0022

w

JOYSTICK joy3pos-digital

1 byte:

Bits 0-3: left/right/up/dwn

0x0023

w

JOYSTICK joybuttonstate

2 bytes:

Bits 0-9: button pressed 0/1

  
  

  
  

  
  

  
  

  
  

## PT-1 GPIO – CIM (Legacy GPIO)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0x0201:

GPIO version 1 sensor/ actuator,

0x0000

r

Unique serial number

4 bytes

0x0001

r

GPIO Input State

1 byte:

Bit 0 = Input 1; Bit 7 = Input 8

0x0002

r/w

GPIO Input Threshold Voltage

4 bytes:

bytes 0,1: threshold voltage value for inputs 1-4 (0 to 25000 mV)

bytes 2,3: threshold voltage value for inputs 5-8 (0 to 25000 mV)

0x0003

r/w

GPIO Input Pullup State

1 byte:

Bit 0 = Input 1; Bit 7 = Input 8

Value: 0 = off; 1 = on, 33K resisor connected to 3.3 V

0x0004

r/w

GPIO Input Value Change Event

1 byte:

Bit 0 = Input 1; Bit 7 = Input 8

Value: 0 = off; 1 = on

0x0005

r/w

GPIO Periodic Input Value Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0010

r/w

GPIO Output State

1 byte:

Bit 0 = Output 1; Bit 7 = Output 8

0x0011

r/w

GPIO Output Pullup State

1 byte:

Bit 0 = Output 1; Bit 3 = Output 4

Value: 0 = off; 1 = on

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0030

r/w

5-24 V power output

2 bytes:

bytes 0,1: 0 (bypass to USB 5V), 5000-25000 mV

  

  

## Phone-CIM (Windows Phone OS)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x0301**: Phone actuator, version 1

0x0000

r

Unique serial number

4 bytes

0x0001

w

Phone Application Configuration:  
init

request:  
4 bytes (init)

reply:  
4 bytes (error\_code)

0x0002

w

Phone Application Configuration:  
close

request:  
4 bytes (close)

reply:  
4 bytes (error\_code)

0x0010

w

Phone Manager:  
make call

request:

4 bytes: command (make call)

1 byte: phone\_id\_len

X bytes: phone\_id

reply:

4 bytes (error\_code)

0x0011

w

Phone Manager:  
accept call

request:  
4 bytes (accept)

reply:  
4 bytes (error\_code)

0x0012

w

Phone Manager:  
drop call

request:  
4 bytes (drop)

reply:  
4 bytes (error\_code)

0x0013

r

Phone Manager:  
receive call event

1 byte: phone\_id\_len

X bytes: phone\_id

0x0014

w

Phone Manager: get phone state

request:

4 bytes (get phone state)

reply:

4 bytes (error\_code)

1 byte (state\_code)

  

0x0020

w

Message Manager:  
send SMS

request:

1 byte: phone\_id\_len

X bytes: phone\_id

2 bytes: message\_len

Y bytes: message

reply:  
4 bytes (error\_code)

0x0021

r

Message Manager:  
receive SMS event

1 byte: phone\_id\_len

X bytes: phone\_id

2 bytes: message\_len

Y bytes: message

  

  
  

## PT-1 ADC – CIM (Legacy ADC/DAC)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0x0401:

ADC version 1 sensor/ actuator,

0x0000

r

Unique serial number

4 bytes

0x0001

r

GPIO Input State

1 byte:

Bit 0 = Input 1; Bit 1 = Input 2

0x0003

r/w

GPIO Input Pullup State

1 byte:

Bit 0 = Input 1; Bit 1 = Input 2

Value: 0 = off; 1 = on, 33K resisor connected to 3.3 V

0x0004

r/w

GPIO Input Value Change Event

1 byte:

Bit 0 = Input 1; Bit 2 = Input 2

Value: 0 = off; 1 = on

0x0005

r/w

GPIO Periodic Input Value Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0010

r/w

GPIO Output State

1 byte:

Bit 0 = Output 1; Bit 1 = Output 2

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0040

r

ADC Input Value

18 bytes:

bytes 0-1: ADC01 input value...

byte 6-7: ADC04 input value

0-24000 mV

  

bytes 8-10: ADC05 input value

bytes 11-13: ADC06 input value

in Ohms, 1.5E+06 is maximum

0xFFFFFF means anything above 1.5 MOhm

  

byte 14-15: ADC07 input value

byte 16-17: ADC08 input value

0-24000 mV

0x0050

r/w

DAC Output Value

4 bytes:

byte 0: DAC01 0..24.0 V

...

byte 3: DAC04 0..24.0 V

e.g. 240 is 24.0V

  

  
  

## BMA180 Accelerometer Sensor
    

  

**CIM-ID**

**Feature-address**

**Access**

**Description**

**Data**

0x0501

BMA180 accelerometer sensor version 1

0x0000

r

Unique serial number

4 bytes

0x0020

w

Store current state to EEPROM as default power-on state

none

0x0060

r/w

BMA180 direct register access

**READ**

request byte 0: address 00-5B

reply byte 0: value

**WRITE**

request byte 0: address 00-5B

request byte 1: value

reply has no data

Not all registers or their bits can be written, please see the BMA180 reference manual

**NOTE: This is for PT1 HW testing purposes only and shall be never used for normal operation as there can occur collision with new data reading in a high priority interrupt function. If you still want to use it, disable all BMA180 interrupts in ctrl\_reg3 first. As a consequence, the feature 0x0063 will have no new data until the new\_data\_int in ctrl\_reg3 is re-enabled.**

0x0061

r/w

BMA180 bandwidth (data sample frequency)

1 byte: bandwidth

0x00 ... 10 Hz

0x01 ... 20 Hz

0x02 ... 40 Hz

0x03 ... 75 Hz

0x04 ... 150 Hz

0x05 ... 300 Hz

0x06 ... 600 Hz

0x07 ... 1200 Hz

other values are not allowed and will result in an error reply

0x0062

r/w

BMA180 range

1 byte: range

0x00 ... 1 g

0x01 ... 1.5 g

0x02 ... 2 g

0x03 ... 3 g

0x04 ... 4 g

0x05 ... 8 g

0x06 ... 16 g

other values are not allowed and will result in an error reply

0x0063

r

BMA180 X/Y/Z data

7 bytes

byte 0: TRUE if new data are acquired since last read, otherwise false

bytes 1-2: acc\_x 14-bit value

bytes 3-4: acc\_y 14-bit value

bytes 5-6: acc\_z 14-bit value

0x0064

r/w

Accelerometer Data Event

1 byte

0x00 ... disabled

0x01-0xFF ... enabled, feature 0x0063 X/Y/Z data is sent automatically every time when new data are acquired.

The period is set by feature 0x0061

  
  

## PT-1 Core – CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0x0601:

Core CIM version 1

0x0000

r

Unique serial number

4 bytes

0x0001

r

GPIO Input State

1 byte:

Bit 0 = Input 1; Bit 3 = Input 4

0x0002

r/w

GPIO Input Threshold Voltage

2 bytes:

bytes 0,1: threshold voltage value for inputs 1-4 (0 to 25000 mV)

0x0003

r/w

GPIO Input Pullup State

1 byte:

Bit 0 = Input 1; Bit 3 = Input 4

Value: 0 = off; 1 = on, 33K resisor connected to 3.3 V

0x0004

r/w

GPIO Input Value Change Event

1 byte:

Bit 0 = Input 1; Bit 3 = Input 4

Value: 0 = off; 1 = on

0x0005

r/w

GPIO Periodic Input Value Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0010

r/w

GPIO Output State

1 byte:

Bit 0 = Output 1; Bit 3 = Output 4

0x0011

r/w

GPIO Output Pullup State

1 byte:

Bit 0 = Output 1; Bit 3 = Output 4

Value: 0 = off; 1 = on

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0070

w

clear status LCDisplay

none

0x0071

w

clear window on status LCDisplay

8 bytes

bytes 0,1: top left X

bytes 2,3: top left Y

bytes 4,5: width

bytes 6,7: height

\* byte per value would be now sufficient but in case of larger display in future word size is used

0x0072

r/w

set text window on status LCDisplay

8 bytes

bytes 0,1: top left X

bytes 2,3: top left Y

bytes 4,5: width

bytes 6,7: height

\* window must fit on the display otherwise error is returned

0x0073

r/w

set text font

1 byte:

0 ... Terminal 6 – 6x8 pixels

1 ... Terminal 9 – 6x12 pixels

2 ... Terminal 18 – 12x24 pixels

0x0074

w

print

1 to 2048 bytes

null-terminated string, prints only part which fits in the text window set by feature 0x0072

Special characters:

\\n - goes to next line but keeps the column.

  \\r - clears the line inside the window from the current position to the end of the line and then it goes to the beginning of the line. (So \\r\\r clears the full line.)

  \\b - goes one character back and clears it.

  \\t - TAB function, the step is 4 columns, clears the text from the current position to the new one (so 1-4 characters depending on the position)

  \\f - clears the whole text window and sets the position to the top left corner of the window.

0x0075

w

draw bitmap

9 to 2048 bytes

bytes 0-1: top left X

bytes 2-3: top left Y

bytes 4-5: width

bytes 6-7: height

bytes 8-2047: bitmap stream, standard Windows 2-color BMP order

\* only part which fits the display is drawn

\* first byte bit 0 is (0,0), bit 7 is (7,0)

\* if the bitmap width is e.g. 10 pixels, stream has 2 bytes per row and bits 2 to 7 of the second byte are ignored

0x0076

r/w

status LCDisplay backlight

1 byte - backlight 0-100%

0x0080

r

read front panel buttons state

1 byte

bit 0 ... left

bit 1 ... right

bit 2 ... down

bit 3 ... up

bit 4 ... OK

1 – pressed, 0 – not pressed

0x0081

r/w

front panel buttons change event mask

1 byte

bit 0 ... left

bit 1 ... right

bit 2 ... down

bit 3 ... up

bit 4 ... OK

1 – enabled, 0 – disabled

  

  

  

## EOG-CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0xa101:

EOG version 1 sensor/ actuator

0x0000

r

Unique serial number

4 bytes

0x0001

w

Activate Periodic Value Reports

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0002

r

Channel Value Report

4 bytes: 2 channels of ADC values

Byte 1: chn1 low byte

Byte 2: chn1 high byte

Byte 3: chn2 low byte

Byte 4: chn2 high byte

  

  

  

  

  

  

## Sensorboard – CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0xa201:

Sensor-board for low-cost eye tracker

0x0000

r

Unique serial number

4 bytes

0x0001

w

Activate Periodic Value Reports

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0002

r

Sensor Value Report

35 bytes of sensor values:

1:accelerometer X MSB

2:accelerometer X LSB

3:accelerometer Y MSB

4:accelerometer Y LSB

5:accelerometer Z MSB

6:accelerometer Z LSB

7:gyro X MSB

8:gyro X LSB

9:gyro Y MSB

10:gyro Y LSB

11:gyro Z MSB

12:gyro Z LSB

13:compass X MSB

14:compass X LSB

15:compass Y MSB

16:compass Y LSB

17:compass Z MSB

18:compass Z LSB

19:IR-Cam, point 1 X MSB

20:IR-Cam, point 1 X LSB

21:IR-Cam, point 1 Y MSB

22:IR-Cam, point 1 Y LSB

23:IR-Cam, point 2 X MSB

24:IR-Cam, point 2 X LSB

25:IR-Cam, point 2 Y MSB

26:IR-Cam, point 2 Y LSB

27:IR-Cam, point 3 X MSB

28:IR-Cam, point 3 X LSB

29:IR-Cam, point 3 Y MSB

30:IR-Cam, point 3 Y LSB

31:IR-Cam, point 4 X MSB

32:IR-Cam, point 4 X LSB

33:IR-Cam, point 4 Y MSB

34:IR-Cam, point 4 Y LSB

35:pressure sensor

  

  

  

  

  

  
  

  
  

  
  

  
  

  

  

  

## Arduino – CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

0xa001:

Arduino version 1 sensor/ actuator

0x0000

r

Unique serial number

4 bytes

0x0001

r/w

Set Pin Directions (input or output)

2 bytes: Data Direction State of Port B (DDRB) and Port D (DDRD)

Bit 0 : Pin = Input

Bit 1 : Pin = Output

0x0002

w

Set Output Pin States  
or Input Pin Pullup State

2 bytes:

Byte 1: Output Pin values of PORT B

Byte 2: Output Pin values of PORTD

  

For Input Pins: activate pullup:

Value: 0 = off; 1 = on

0x0003

r

Get Input PIN Change

2 bytes:

Byte 1: input PIN values of Port B

Byte 2: input PIN values of Port D

  

0x0004

w

Activate ADC Periodic Value Reports

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0005

r

ADC Value Report

12 bytes: 6 channels of ADC values

Byte 1: chn1 low byte

Byte 2: chn1 high byte

Byte 3: chn2 low byte

Byte 4: chn2 high byte

Byte 5: chn3 low byte

Byte 6: chn3 high byte

Byte 7: chn4 low byte

Byte 8: chn4 high byte

Byte 9: chn5 low byte

Byte 10: chn5 high byte

Byte 11: chn6 low byte

Byte 12: chn7 high byte

  

0x0006

w

Set PIN Mask for auto send back Input PIN Change events

2 bytes:

Byte 1: input pins of Port B

Byte 2: input pins of Port D

  

  

0x0007

w

Set PWM channel value

2 bytes

Byte 1: channel number (0-5) + operation mode (0x1x: servo, 0x2x: PWM)

Byte 2: channel value (0-255)

  

  
  

  
  

  
  

## PT2 Core - CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x06**02:

Core CIM version 2

0x0000

r

Unique serial number

4 bytes

0x0001

r

DigitalInput State

1 byte:

Bit 0 = Input 1; Bit 2 = Input 3

0x0003

r/w

DigitalInput Pullup State

1 byte:

Bit 0 = Input 1; Bit 2 = Input 3

Value: 0 = off; 1 = on, 33K resisor connected to 3.3 V

0x0004

r/w

DigitalInput State Change Event

1 byte:

Bit 0 = Input 1; Bit 2 = Input 3

Value: 0 = off; 1 = on

0x0005

r/w

Periodic DigitalInput State Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0010

r/w

DigitalOutput State

1 byte:

Bit 0 = Output 1; Bit 1 = Output 2

0x0011

r/w

DigitalOutput Pullup State

1 byte:

Bit 0 = Output 1; Bit 1 = Output 2

Value: 0 = off; 1 = on

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0031

r/w

12 V GPO power output

1 byte:

0 disable, 1-255 enable

0x0040

r

AnalogInput Value

6 bytes

bytes 0-2: Input 1 value

bytes 3-5: Input 2 value

\* the values are in mV or miliohms according to the sensor type connected

0x0070

w

clear status LCDisplay

none

0x0071

w

clear window on status LCDisplay

8 bytes

bytes 0,1: top left X

bytes 2,3: top left Y

bytes 4,5: width

bytes 6,7: height

\* byte per value would be now sufficient but in case of larger display in future word size is used

\* the current accessible display area is 114 x 64 pixels

0x0072

r/w

set text window on status LCDisplay

8 bytes

bytes 0,1: top left X

bytes 2,3: top left Y

bytes 4,5: width

bytes 6,7: height

\* window must fit on the display otherwise error is returned

\* the current accessible display area is 114 x 64 pixels

0x0073

r/w

set text font

1 byte:

0 ... Terminal 6 – 6x8 pixels

1 ... Terminal 9 – 6x12 pixels

2 ... Terminal 18 – 12x24 pixels

0x0074

w

print

1 to 2048 bytes

null-terminated string, prints only part which fits in the text window set by feature 0x0072

**Special characters:**

**\\n** - goes to next line but keeps the column.

  **\\r** - clears the line inside the window from the current position to the end of the line and then it goes to the beginning of the line. (So \\r\\r clears the full line.)

  **\\b** - goes one character back and clears it.

  **\\t** - TAB function, the step is 4 columns, clears the text from the current position to the new one (so 1-4 characters depending on the position)

  **\\f** - clears the whole text window and sets the position to the top left corner of the window.

  **0x1f** – the letters after this special character are inverted.

  **0x1e** – the letters after this special character are not inverted.

0x0075

w

draw bitmap

9 to 2048 bytes

bytes 0-1: top left X

bytes 2-3: top left Y

bytes 4-5: width

bytes 6-7: height

bytes 8-2047: bitmap stream, standard 16-level grayscale

\* only part which fits the display is drawn

\* first byte bit 0-3 is (0,0), bit 4-7 is (1,0)

\* if the bitmap width is e.g. 11 pixels, 6 bytes per row and bits 4 to 7 of the last byte are ignored

0x0076

r/w

status LCDisplay brightness

1 byte - brightness 0-100%

\* for backward compatibility, the brightness can be set in the CIM’s internal menu

0x0078

w

draw 16x16 predefined icon

6 bytes

bytes 0-1: top left X

bytes 2-3: top left Y

bytes 4-5: icon index

0 .. minus, 1 .. plus, 2 .. up,

3 .. down, 4 .. left, 5 .. right,

6 .. play, 7 .. pause

0x0082

r

read touch panel state

4 bytes

bytes 0-1 ... display coordinate X

bytes 2-3 .. display coordinate Y

value -1 means not touched

0x0083

r/w

touch panel event enable

1 byte

1 – enabled, 0 – disabled

when enabled, CIM send the X/Y coordinates every time the display is touched

0x0090

r

battery level

1 byte

not accessible on request, every time the battery charge level changes, the CIM sends the level automatically

\* 1-100 % when discharging,

\* 101-200 % when charging.

\* 254 – battery missing or dead

\* 255 battery status is uknown (e.g. during startup)

Level <15 means hibernate system immediately.

  
  

  
  

  
  

## PT2 GPI – CIM (DigitalIn)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x07**01:

GPI version 1

0x0000

r

Unique serial number

4 bytes

0x0001

r

GPIO Input State

1 byte:

Bit 0 = Input 1; Bit 5 = Input 6

0x0003

r/w

GPIO Input Pullup State

1 byte:

Bit 0 = Input 1; Bit 7 = Input 8

Value: 0 = off; 1 = on, 33K resisor connected to 3.3 V

0x0004

r/w

GPIO Input Value Change Event

1 byte:

Bit 0 = Input 1; Bit 7 = Input 8

Value: 0 = off; 1 = on

0x0005

r/w

GPIO Periodic Input Value Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0077

r/w

RGB status LED override

1 byte:

bit 0-1: red

bit 2-3: green

bit 4-5: blue

00 – override off

01 – always off

10 – always on

11 – blinking

  

## PT2 GPO – CIM (DigitalOut)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x08**01:

GPO version 1

0x0000

r

Unique serial number

4 bytes

0x0010

r/w

GPIO Output State

1 byte:

Bit 0 = Output 1; Bit 4 = Output 5

outputs 1-2 are relays, 3-5 are OC

0x0011

r/w

GPIO Output Pullup State

1 byte:

Bit 2 = Output 3; Bit 4 = Output 5

Value: 0 = off; 1 = on

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0031

r/w

12 V power output

1 byte:

0 disable, 1-255 enable

0x0077

r/w

RGB status LED override

1 byte:

bit 0-1: red

bit 2-3: green

bit 4-5: blue

00 – override off

01 – always off

10 – always on

11 – blinking

  

  

  

  

  

  

  

  

## PT2 ADC – CIM (AnalogIN)
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x09**01:

ADC version 1

0x0000

r

Unique serial number

4 bytes

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0040

r

AnalogInput Value

6 bytes

bytes 0-2: Input 1 value

bytes 3-5: Input 2 value

\* the values are in mV or miliohms according to the sensor type connected

0x0041

r/w

ADC Periodic Input Value Event

2 bytes:

bytes 0,1: period time 0 (off) to 65535 milliseconds

value lower than ~50 ms results in period of 20 to 50 ms

0x0077

r/w

RGB status LED override

1 byte:

bit 0-1: red

bit 2-3: green

bit 4-5: blue

00 – override off

01 – always off

10 – always on

11 – blinking

  

## PT2 ZigBee – CIM
    

  

**CIM-ID**

**Feature-address**

**Access**

**Descritption**

**Data**

**0x0a**01:

ZigBee version 1

0x0000

r

Unique serial number

4 bytes

0x0020

w

Store current CIM state to EEPROM as default power-on state

none

0x0077

r/w

RGB status LED override

1 byte:

bit 0-1: red

bit 2-3: green

bit 4-5: blue

00 – override off

01 – always off

10 – always on

11 – blinking

  

0x0090

w

Init ZigBee pairing mode for 60 seconds

none

  

0x0091

w

End ZigBee pairing mode imediately

none

  

0x0092

r

Get full paired wireless CIMs list

2+6xN bytes

byte 0-1: N...number of CIMs

followed by N-times

byte 0-3: unique serial number

byte 4-5: CIM ID

where CIM ID value means:

0x0b01 ... GPI v. 1

0x0c01 ... GPO v. 1

0x0d01 ... Accelerometer v. 1

  

0x0093

r

Get active paired wireless CIMs list

same as above but the list is limited to CIMs which sent at least 1 event since the last ZigBee-CIM start

  

0x0094

w

Erase CIM from paired list

6 bytes

byte 0-3: unique serial number

byte 4-5: CIM ID

_Note: returned error when the specified CIM not paired_

  

0x0095

r/w

Send and receive remote wireless CIM features

8+N bytes

byte 0-3: unique serial number

byte 4-5: CIM ID

byte 6-7: data length N

byte 8-(7+N): feature data

  

\* when CIM ID 0x0b01, sent as event by the CIM only, N=1, byte 8: GPI input state bit 0-5 ... input 1-6 state

\* when CIM ID 0x0c01, write-only, N=1, byte 8: bit 0-1 ... relay output 1-2 state

\* when CIM ID 0x0d01, sent as event by the CIM only, N=6, byte 8-13: accelerometer data ax,ay,az

_Note: When the connection to the destination is lost, the response can take up to ~5 seconds._

  
  

  
  

## Demo Implementations of the CIM protocol
    

In the AsTeRICS Source Code package, the following microcontroller firmware implementations of the CIM protocol can be found:

*   Folder /CIMs/Arduino: an implementation for the 8-bit Atmel ATmega328 AVR microcontroller architecture, with features for reading / writing GPIO and ADC
    
*   Folder /CIMs/HID\_actuator: an implementation for the 8-bit Atmel AT90USB1286, with features for mouse/keyboard/joystick emulation
    
*   Upon special request, CIM firmware for the Arm Cortex M3 or other architectures can be delivered by AsTeRICS partners IMA of FHTW
    

The corresponding JAVA implementations on the ARE-side can be found in the respective plugins (Arduino and RemoteMouse, RemoteKeyboard, RemoteJoystick)
