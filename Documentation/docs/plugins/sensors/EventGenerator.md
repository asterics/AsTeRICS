  
---
Event Generator
---

# Event Generator

The event generator plugin can be used to periodically send event triggers at a given time interval.

## Requirements

No special hardware or software required

## Port Description

No input or output ports available

## Event Trigger Description

*   **event\_out\_1:** Events will be generated and sent to this port.

## Properties

*   **generation\_delay \[integer\]:** The time interval for generating events in milliseconds.
*   **event\_payload \[string\]:** A string value which will be sent with the event as a parameter (currently not used by other plugins).