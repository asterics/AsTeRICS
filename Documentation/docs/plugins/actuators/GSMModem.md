##

## GSMModem

# GSM Modem

### Component Type: Actuator (Subcategory: Communication)

This component can perform send and receive SMS action through the GSM modem.

![Screenshot:
        GSMModem plugin](./img/GSMModem.jpg "Screenshot: GSMModem plugin")  
GSMModem plugin

## Requirements

A GSM modem with SMS option connected to the platform.

## Input Port Description

- **phoneID \[string\]:** Phone number which will be used for the send SMS action.
- **SMSContent \[string\]:** SMS content which will be used for the send SMS action.

## Output Port Description

- **remotePhoneID \[string\]:** This is a phone number of the SMS sender.
- **receivedSMS \[string\]:** This is the content of the incoming SMS.
- **errorNumber \[integer\]:** The number of the error.

## Event Listener Description

- **sendSMS:** Sends the SMS message.

## Event Trigger Description

- **newSMS:** There is a new message.
- **error:** An error occurred.

## Properties

- **serialPort \[string\]:** The modem COM port. If this parameter is empty, the component uses the port of the first modem found.

- **pin \[string\]:**The PIN code for the SIM card. If the PIN is not needed this property should be empty.
- **smsCenterID \[string\]:** SMS Center ID. If the Center ID is not needed this property should be empty.
- **defaultPhoneID \[string\]:** This is a default phone number, which will be used for the send SMS actions.
