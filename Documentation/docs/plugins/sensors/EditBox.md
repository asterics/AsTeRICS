---
title: EditBox
---

# Edit Box

### Component Type: Sensor (Subcategory: Graphical User Interface)

This component creates a GUI edit field which can send the text to other components. The text will be sent to the output port when enter is pressed or when the edit box looses the input focus (when the cursor is removed from the edit field).

![Screenshot: EditBox plugin](./img/EditBox.jpg "Screenshot: EditBox plugin")  
EditBox plugin

## Output Port Description

- **output \[string\]:** String output port.

## Event Listener Description

- **clear:** Removes the text from the component.
- **send:** Send the text value to the String output port.

## Properties

- **caption \[string\]:** Caption of the component.
- **default \[string\]:** The default text, which is set at startup.

- **textColor \[integer\]:** Defines color of the text.
- **backgroundColor \[integer\]:** Defines background color.
- **insertAction \[integer\]:** Defines behaviour of the component after the text has been sent to the output port. The text in the component can be selected or removed.
- **sendDefaultValue \[boolean\]:** When this checkbox is checked the default String value is sent to the String output port when the model gets started.
- **displayGUI \[boolean\]:** If selected, the GUI of this component will be displayed - if not, the GUI will be hidden and disabled.
