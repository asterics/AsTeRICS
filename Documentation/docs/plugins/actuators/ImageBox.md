---
title: ImageBox
---

# Image Box

### Component Type: Actuator (Subcategory: Graphical User Interface)

The Image Box is a GUI component which displays images loaded from image files.

![Screenshot:
        ImgeBox plugin](./img/ImageBox.jpg "Screenshot: ImageBox plugin")  
ImageBox plugin

## Input Port Description

- **input \[string\]:** The path of the image file, which will be displayed.

## Event Listener Description

- **clear:** Removes the image from the component.

## Event Trigger Description

- **clicked:** The event is triggered when the user clicks on the component.

## Properties

- **caption \[string\]:** Caption of the component.
- **default \[string\]:** The path of the image file, which is displayed after start.
- **backgroundColor \[integer\]:** Defines background color.
- **displayGUI \[boolean\]:** If selected, the GUI of this component will be displayed - if not, the GUI will be hidden and disabled.
