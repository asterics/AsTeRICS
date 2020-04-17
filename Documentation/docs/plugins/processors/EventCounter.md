---
title: EventCounter
---

# Event Counter

### Component Type: Processor (Subcategory: Event and String Processing)

This component counts events. It can add and subtract incoming events.

![Screenshot:
        EventCounter plugin](./img/EventCounter.jpg "Screenshot: EventCounter plugin")  
EventCounter plugin

## Output Port Description

- **output \[integer\]:** Sends the number of events.

## Event Listener Description

- **increase:** Increases the number of events.
- **decrease:** Decreases the number of events.
- **resetToZero:** Sets the event number to zero.
- **resetToInitial:** Sets the event number to the initial value.

## Properties

- **mode \[integer\]:** Defines counting mode:

- _no limit:_ The component counts events without any limitation.
- _limit maximum:_ In this mode, the maximum value of the counter is limited by the maxValue property.
- _limit minimum:_ In this mode, the minimum value of the counter is limited by the minValue property.
- _limit minimum and maximum:_ In this mode, both maximum and minimum values of the counter are limited by the maxValue amd minValue properties.

- **minValue \[integer\]:** Defines the minimum value of the counter.
- **maxValue \[integer\]:** Defines the maximum value of the counter.
- **initialValue \[integer\]:** Defines the initial value of the counter.
- **wrapAround \[boolean\]:** if selected and the appropriate mode is set, exceeding the maximum value will wrap to the minimum value and vice versa.
- **initialValue \[boolean\]:** if selected, the initial value is sent at the startup.
