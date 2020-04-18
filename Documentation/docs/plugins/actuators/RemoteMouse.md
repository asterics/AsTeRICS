---
title: RemoteMouse
---

# Remote Mouse

### Component Type: Actuator (Subcategory: Input Device Emulation)

The RemoteMouse component interfaces the AsTeRICS Personal Platform to a second computer via the HID actuator CIM (USB dongle, plugged into the target computer).
The HID actuator emulates a standard USB mouse on the target computer (no special driver software is needed).
The mouse x-position, y-position, press/release actions of three mouse buttons and mouse-wheel movements can be controlled via desired input values and event triggers.
Note that multiple instances of the Remote components (RemoteJoystick, RemoteKeyboard and RemoteMouse) can be used concurrently with one HID actuator USB dongle, e.g. to provide different key actions for up to three different input devices on the target computer.

![Screenshot: RemoteMouse plugin](./img/RemoteMouse.jpg "Screenshot: RemoteMouse plugin")  
RemoteMouse plugin

## Requirements

The HID Actuator CIM (CIM ID 0x0101) has to be plugged into a free USB port of the target computer and the cable has to be connected to the AsTeRICS platform.

![HID Actuator CIM](./img/HID_CIM.jpg "HID Actuator CIM")  
the HID Actuator plugs into the target computer and connects via Bluetooth wirelessly to the ARE

## Input Port Description

- **mouseX \[integer\]:** The desired X-Position of the mouse.
  **This input port supports synchronization**
- **mouseY \[integer\]:** The desired Y-Position of the mouse.
  **This input port supports synchronization**
- **action \[string\]:** Input port for a command string.
  This command string allows to modify the action of the next left mouse click �V it can be set to trigger other types of mouse clicks.
  Following command strings are accepted:
  - _"@MOUSE: nextclick, right":_ next left click event will create a right mouse button click.
  - _"@MOUSE: nextclick, double":_ next left click event will create a double click.
  - _"@MOUSE: nextclick, middle":_ next left click event will create a middle button click.
  - _"@MOUSE: nextclick, drag":_ next left click event will hold the left mouse button.
  - _"@MOUSE: nextclick, release":_ next left click event will release the left mouse button.
  - _"@MOUSE: action, enable":_ enables all mouse actions.
  - _"@MOUSE: action, disable":_ disables all mouse actions.
  - _"@MOUSE: action, toggle":_ enables / disables all mouse actions.

## Event Listener Description

- **leftClick:** An incoming event at this port creates a mouse button click.
  A left click will be generated, unless a valid "nextclick �" command has been received at the cmd input which changed the type of the click (see above).
- **middleClick:** A click with the middle mouse button is generated.
- **rightClick:** A click with the right mouse button is generated.
- **doubleClick:** A double click with the left mouse button is generated.
- **dragPress:** The left mouse button is pressed (but not released again).
- **dragRelease:** The left mouse button is released.
- **wheelUp:** The mouse wheel is turned one position from the user.
- **wheelDown:** The mouse wheel is turned one position to the user.

## Properties

- **absolutePosition \[boolean\]:** Currently not supported.
  All mouse position input values are interpreted as relative changes of the position (X- and Y-movement).
- **uniqueId:** unique number of the CIM - if more than one CIMs of the same type are used.
  The module flashes a LED for identification when the ID is selected.
  **Supports value suggestions from ARE (dynamic property)**
