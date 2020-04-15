##

## SerialSender

# SerialSender

### Component Type: Actuator (Subcategory: Serial Communication)

The Serialsender can be used to send structured data to Serial devices. It has 16 data slots. Whenever a send event occurs the plugin sends every slot which is active to the device, beginning with slot0

![Screenshot: SerialSender plugin](./img/SerialSender.png "Screenshot:
        SerialSender plugin")  
SerialSender plugin

## Input Port Description

- **slot0 - slot15 \[int\]:** Input data for each slot. The lower 8 Bit of the input will be sent when the slot is set Active and a send event occurs

## Properties

- **COMPort \[string\]:** Defines the COM Port of the target serial device. e.g. COM0
- **BaudRate \[integer\]:** Defines the Baudrate for the communication. It must match the baudrate of the target device
- **Slot\[0-15\] \[int\]:**Defines the default value of a slot. This value will be overridden if there is data available at the corresponding input slot
- **Slot\[0-15\]Active \[boolean\]:**Activate a Slot. Whenever a send event occurs the SerialSender will iterate over all Slots beginning with slot 0 and send the data of every Active Slot
- **Slot\[0-15\]Delay \[int\]:**Defines the delay the plugin should wait before sending data to a slot.
