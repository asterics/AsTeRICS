---
title: RemoteWindow
---

# RemoteWindow

### Component Type: Actuator (Subcategory: Graphical User Interface)

This component allows moving or setting the state of a specific window (which is active and can be identified via the window title) to desired locations on the screen. Several default locations can be selected via incoming events (top, left, bottom, right or center of the screen). X- and Y- offset values can be defined - thus it becomes possible to move the remote window e.g to a second screen.

![Screenshot:
        RemoteWindow plugin](./img/RemoteWindow.jpg "Screenshot: RemoteWindow plugin")  
RemoteWindow plugin

## Input Port Description

- **xPos \[integer\]:** The x offest value for positioning the window
- **yPos \[integer\]:** The y offest value for positioning the window

## Event Listener Description

- **moveToTop:** moves the window to the top of the screen (y offset will be applied). The x position will not be changed.
- **moveToBottom:** moves the window to the bottom of the screen (y offset will be applied). The x position will not be changed.
- **moveToLeft:** moves the window to the left side of the screen (x offset will be applied). The y position will not be changed.
- **moveToRight:** moves the window to the right side of the screen (x offset will be applied). The y position will not be changed.
- **moveToCenter:** moves the window to the center of the screen (x and y offsets will be applied).
- **minimize:** minimizes the window to the taskbar
- **restore:** restores the window from the taskbar
- **bringToFront:** places the window on top of other windows
- **moveNow:** moves the window to the selected xPos and yPos positions

## Properties

- **windowName \[string\]:** the window title / name
- **mode \[integer, combobox selection\]:** the search mode for finding the window. If "exact match" is selected, the window title must be exactly the same as the given name. If "contains text" is selected, the any window title which contains the given text will be found. Note that both modes are case-sensitive !
- **xPos \[integer\]:** default value for the x offset
- **yPos \[integer\]:** default value for the y offset
- **autoSetPosition \[boolean\]:** if selected, the position of the window will be set to xPos/yPos at startup
