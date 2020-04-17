---
title: ButtonGrid
---

# Button Grid

### Component Type: Sensor (Subcategory: Graphical User Interface)

The Button Grid component is a simple GUI on-screen keyboard. It sends events after buttons have been pressed.

![Screenshot:
        ButtonGrid plugin](./img/ButtonGrid.jpg "Screenshot: ButtonGrid plugin")  
ButtonGrid plugin

## Event Trigger Description

- **button1...button20:** These events are fired as the corresponding buttons are pressed.

## Properties

- **caption \[string\]:** The text shown on the component caption.
- **horizontalOrientation \[boolean\]:** If selected, the keyboard will be placed horizontally, otherwise the keyboard will be placed vertically.
- **buttonCaption1...buttonCaption20 \[string\]:** The text shown on the buttons:1...20. If the text is empty, the button will not be displayed at all.
