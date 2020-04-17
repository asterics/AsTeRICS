---
title: Event Flip Flop
---

# Event Flip Flop

This component stores the state, driven by an event. When the event-in event is received and the internal status is 0, event-out1 is fired and the internal status set to 1. When the event-in event is received and the internal status is 1, event-out2 is fired and the internal status set to 0.

### Component Type: Processor (Subcategory: Event and Signal Processing)

![Screenshot: Event Flip Flop plugin](./img/EventFlipFlop.png "Screenshot: Event Flip Flop plugin")  
Event Flip Flop plugin

## Requirements

No requirements.

## Inport Description

No Inports.

## Outport Description

No Outports.

## Event Listener Description

- **Event-in:** Event input to change the state of the flip-flop and fire an out-event.

## Event Trigger Description

- **Event-out1:** Event fired, if event-in received and stored status is 0.
- **Event-out2:** Event fired, if event-in received and stored status is 1.

## Properties

No Properties.
