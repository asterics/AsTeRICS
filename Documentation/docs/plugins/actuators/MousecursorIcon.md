---
title: MousecursorIcon
---

# MousecursorIcon

### Component Type: Actuator (Subcategory: Input Device Emulation)

The MousecursorIcon plugin can be used to modifiy the current default system mouse cursor icon. A number of icon file names can be given as properties and activated by corresponding event listener ports. Currently, only the default system cursor can be modified. The cursor is restored to the default arrow when the model is stopped.

![Screenshot: MousecursorIcon plugin](./img/MousecursorIcon.jpg "Screenshot: MousecursorIcon plugin")  
MousecursorIcon plugin

## Input Port Description

- **iconName \[strig\]:** This input port accepts a filename, the plugin tries to load a cursor file with this name from the local plugin working directory.

## Event Listener Description:

- **setIcon1 - setIcon9:** an incoming events sets the given cursor as new system cursor.

## Properties

- **iconName1 - iconName9 \[string\]:** 9 slots for cursor file names.
