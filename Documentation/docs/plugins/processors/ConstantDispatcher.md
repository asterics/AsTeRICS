   
---
ConstantDispatcher
---

# Constant Dispatcher

### Component Type: Processor (Subcategory: Basic Math)

This component sends double values from the chosen slot.

![Screenshot: ConstantDispatcher plugin](img/ConstantDispatcher.jpg "Screenshot:
        ConstantDispatcher plugin")  
ConstantDispatcher plugin

## Input Port Description

*   **slotDispatch \[integer\]:** Sends the value from the slot defined by number.

## Output Port Description

*   **output \[double\]:** The port for the output value.

## Event Listener Description

*   **dispatchSlot1...dispatchSlot20 :** Sends the double value from the slot: 1...20.
*   **dispatchNextSlot:** Sends double value from the next slot.
*   **dispatchPreviousSlot:** Sends double value from the previous slot.
*   **dispatchSlotSeries:** Sends slots values in sequence from slot 1 to slot defined by the Number property with the delay defined by the Delay property.

## Properties

*   **number \[integer\]:** Number of used slots.
*   **delay \[integer\]:** Delay in ms used in sequence slot dispatch.
*   **slot1...slot20 \[double\]:** The slot for the value: 1...20.
*   **autosendSlot \[integer\]:** Number of slot which is automatically sent at start (0=disable).