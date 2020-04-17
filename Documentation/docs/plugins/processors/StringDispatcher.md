---
title: String Dispatcher
---

# String Dispatcher

### Component Type: Processor (Subcategory: Event and String Processing)

This component has twenty slots for text strings. After the appropriate command, the text is sent through the output port.

![Screenshot: StringDispatcher plugin](./img/StringDispatcher.jpg "Screenshot:
        StringDispatcher plugin")  
StringDispatcher plugin

## Input Port Description

- **slotDispatch \[integer\]:** Sends the string from the slot defined by number.

## Output Port Description

- **output \[string\]:** String output port.

## Event Listener Description

- **dispatchSlot1...dispatchSlot20:** This event causes text from the slot: 1..20 to be sent.
- **dispatchNextSlot:** This event causes text from the next not empty slot to be sent.
- **dispatchPreviousSlot:** This event causes text from the previous not empty slot to be sent.
- **dispatchSlotSeries:** This event causes text in sequence from all not empty slots to be sent, with a delay defined by the delay property.

## Properties

- **delay \[integer\]:** The interval (ms) which will be used for sending strings from all slots.
- **slot1...slot20 \[string\]:** Contains the text for the slot: 1...20.
