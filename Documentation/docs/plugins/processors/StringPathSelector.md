   
---
StringPathSelector
---

# String Path Selector

### Component Type: Processor (Subcategory: Signal Pathways)

The StringPathSelector component allows routing of incoming strings between up to 4 output ports. The desired output port can be directly selected by a dedicated event listener port, or the strings can be switched to the next or previous output port.  

![Screenshot: StringPathSelector plugin](img/StringPathSelector.jpg "Screenshot:
        StringPathSelector plugin")  
StringPathSelector plugin

## Input Port Description

*   **in \[string\]:** The incoming string port to be routed.

## Output Port Description

*   **out1 to out4:** Four output ports where the incoming strings can be routed to.

## Event Listener Description

*   **select1 to select4:** An incoming event at these ports directly activates the associated output path (e.g. as an event comes in at select 3, the input string port will be routed to out3.
*   **selectNext:** The next output port is selected for string output. The maximum number of active ports is set via the activePorts property. If the current number is already the maximum one, the select next event will wrap around the active port number and port 1 will be selected.
*   **selectPrevious:** The previous output port is selected for string output. If the current port is out1, the maximum port number (given by the activePorts property) will be selected.

## Properties

*   **activePorts \[integer\]:** The maximum port number in use (can be 1 to 4).