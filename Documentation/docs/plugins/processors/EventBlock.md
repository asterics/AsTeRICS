##

## EventBlock

# Event Block

### Component Type: Processor (Subcategory: Event and String Processing)

This plugin, depending on its state, can pass or block events from the input port.

![Screenshot:
        EventBlock plugin](./img/EventBlock.jpg "Screenshot: EventBlock plugin")  
EventBlock plugin

## Event Listener Description

- **input:** Input port for the events.

- **pass:** Pass the events.

- **block:** Block the events.

- **change:** Change the state of component: pass/block to the opposite.

## Event Trigger Description

- **output** **:** Output port for events.

## Properties

- **block \[boolean\]:** If is set to true, the component will block the events after start.

- **blockAfterEvent \[boolean\]:** If is set to true, the component will block the events after passing one event.
