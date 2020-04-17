---
title: PhoneControl
---

# Phone Control

Component Type: Actuator (Subcategory: Phone Interface)

This component controls a mobile phone with Windows Mobile operating system (v. 5.0 and above) through a Bluetooth connection. Currently supported functions are: Calling a phone number and accepting an incoming call, sending and receiving SMS messages. These functions can either be triggered by incoming events or by sending string commands to the phone plugin.

![Screenshot: PhoneControl plugin](./img/PhoneControl.jpg "Screenshot: PhoneControl plugin")

PhoneControl plugin

## Requirements

A Phone running Windows Mobile, a Bluetooth dongle or Bluetooth HW support, Microsoft Bluetooth stack active. AsTeRICS Phone server application running on the mobile phone.

## Input Port Description

- **phoneID \[string\]:** This is the phone number which will be used for actions like: send SMS, make phone call.
- **SMSContent \[string\]:** This is the SMS content which will be used for sending SMS action.
- **command \[string\]:** String command that can be sent to this component from other plugins to trigger phone actions. Currently supported commands are:
  - @PHONE: SMS:Phone_ID, "Message_content"
  - @PHONE: CALL: Phone_ID
  - @PHONE: ACCEPT
  - @PHONE: DROP

## Output Port Description

- **remotePhoneID \[string\]:** This is a phone number of the caller or SMS sender.
- **receivedSMS \[string\]:** This is the content of the incoming SMS.
- **errorNumber \[integer\]:** The number of the error.

## Event Listener Description

- **sendSMS:** Sends SMS.
- **makePhoneCall:** Makes the phone call.
- **acceptPhoneCall:** Accepts the incoming phone call.
- **dropPhoneCall:** Drops the phone call.
- **reconnect:** Reconnects the phone.

## Event Trigger Description

- **idleState:** Phone is in the idle state.
- **ringState:** Phone is in the ring state.
- **connectedState:** Phone is connected with the remote phone.
- **newSMS:** There is a new SMS.
- **error:** The error occurs.

## Properties

- **defaultPhoneID \[string\]:** This is a default phone number, which will be used for send SMS and make phone call actions.
- **bluetoothPhoneName \[string\]:** This is a Bluetooth name of the phone which the component will connect.
- **port \[integer\]:** This is a Bluetooth port number.
