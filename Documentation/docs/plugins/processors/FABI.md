##

## FABI

# FABI

### Component Type: Processor (Subcategory: Microcontroller Interface)

This component provides an interface for a FABI controller and enables the programming process. It is possible to configure the buttons which are connected to the FABI controller and store the set to the built in EEPROM.

![Screenshot: FABI plugin](./img/FABI.png "Screenshot: FABI plugin")  
FABI plugin

## Requirements

The Plugin requires a FABI2.0 compatible Version as well as the right COM Port. The Baudrate is predefined at 9600 Baud.

## Input Port Description

- **ButtonMode \[integer\]:** Number of the button
- **SlotSaveName \[string\]:** Name of the slot to save the set of modes
- **SlotLoadName \[string\]:** Name of the slot to load the set of modes
- **MoveMouseX \[integer\]:** Number of pixels to move the mouse in X direction (right)
- **MoveMouseY \[integer\]:** Number of pixels to move the mouse in Y direction (down)
- **Text \[string\]:** Text for the KeyWrite command
- **key \[string\]:** Text for the KeyPress command  
  (e.g. AT KP KEY_UP presses the "Cursor-Up" key, AT KP KEY_CTRL KEY_ALT KEY_DELETE presses all three keys)  
  supported key identifiers for key press command (AT KP):  
  KEY_A KEY_B KEY_C KEY_D KEY_E KEY_F KEY_G KEY_H KEY_I KEY_J KEY_K KEY_L KEY_M KEY_N KEY_O KEY_P KEY_Q KEY_R KEY_S KEY_T KEY_U KEY_V KEY_W KEY_X KEY_Y KEY_Z KEY_1 KEY_2 KEY_3 KEY_4 KEY_5 KEY_6 KEY_7 KEY_8 KEY_9 KEY_0 KEY_F1 KEY_F2 KEY_F3 KEY_F4 KEY_F5 KEY_F6 KEY_F7 KEY_F8 KEY_F9 KEY_F10 KEY_F11 KEY_F12 KEY_RIGHT KEY_LEFT KEY_DOWN KEY_UP KEY_ENTER KEY_ESC KEY_BACKSPACE KEY_TAB KEY_HOME KEY_PAGE_UP KEY_PAGE_DOWN KEY_DELETE KEY_INSERT KEY_END KEY_NUM_LOCK KEY_SCROLL_LOCK KEY_SPACE KEY_CAPS_LOCK KEY_PAUSE KEY_SHIFT KEY_CTRL KEY_ALT KEY_RIGHT_ALT KEY_GUI KEY_RIGHT_GUI

## Output Port Description

- **List \[string\]:** Lists the slots stored in the EEPROM of the FABI controller
- **ID \[string\]:** Shows the ID of the FABI version

## Event Listener Description

- **ID :** identification string will be returned (e.g. "FABI Version 2.0")  
  Corresponding FABI command is : "AT ID"
- **ButtonMode :** button mode setting for a button (e.g. AT BM 2 -> next command defines the new function for button 2)  
  Corresponding FABI command is : "AT BM num"
- **ClickLeft :** click left mouse button  
  Corresponding FABI command is : "AT CL"
- **ClickRight :** click right mouse button  
  Corresponding FABI command is : "AT CR"
- **ClickDoubleLeft :** click double with left mouse button  
  Corresponding FABI command is : "AT CD"
- **ClickMiddle :** click middle mouse button  
  Corresponding FABI command is : "AT CM"
- **PressLeft :** press/hold the left mouse button  
  Corresponding FABI command is : "AT PL"
- **PressRight :** press/hold the right mouse button  
  Corresponding FABI command is : "AT PR"
- **PressMiddle :** press/hold the middle mouse button  
  Corresponding FABI command is : "AT PM"
- **ReleaseLeft :** release the left mouse button  
  Corresponding FABI command is : "AT RL"
- **ReleaseRight :** release the right mouse button  
  Corresponding FABI command is : "AT RR"
- **ReleaseMiddle :** release the middle mouse button  
  Corresponding FABI command is : "AT RM"
- **WheelUp :** move mouse wheel up  
  Corresponding FABI command is : "AT WU"
- **WheelDown :** move mouse wheel down  
  Corresponding FABI command is : "AT WD"
- **MoveMouseX :** move mouse in x direction (e.g. AT X 4 moves 4 pixels to the right)  
  Corresponding FABI command is : "AT MX num"
- **MoveMouseY :** move mouse in y direction (e.g. AT Y -10 moves 10 pixels up)  
  Corresponding FABI command is : "AT MY num"
- **KeyWrite :** keyboard write text (e.g. AT KW Hello! writes "Hello!")  
  Corresponding FABI command is : "AT KW text"
- **KeyPress :** key press: press/hold all keys identified in text (e.g. AT KP KEY_UP presses the "Cursor-Up" key, AT KP KEY_CTRL KEY_ALT KEY_DELETE presses all three keys) The possible KeyPress commands are described in detail at the input port "key"!  
  Corresponding FABI command is : "AT KP text"
- **KeyRelease :** key release: releases all keys identified in text  
  Corresponding FABI command is : "AT KR text"
- **KeyReleaseAll :** release all: releases all currently pressed keys and buttons  
  Corresponding FABI command is : "AT RA"
- **Save :** save settings and current button modes to next free eeprom slot under given name (e.g. AT SAVE mouse1)  
  Corresponding FABI command is : "AT SAVE text"
- **Load :** load button modes from eeprom slot (e.g. AT LOAD mouse1 -> loads profile named "mouse1")  
  Corresponding FABI command is : "AT LOAD text"
- **List :** list all saved mode names  
  Corresponding FABI command is : "AT LIST"
- **Next :** next mode will be loaded (wrap around after last slot)  
  Corresponding FABI command is : "AT NEXT"
- **Clear :** clear EEPROM content (delete all stored slots)  
  Corresponding FABI command is : "AT CLEAR"
- **Idle :** idle command (no operation)  
  Corresponding FABI command is : "AT IDLE"

## Properties

- **Stepsize \[integer\]:** set mouse wheel stepsize (e.g. AT WS 3 sets the wheel stepsize to 3 rows)  
  The stepsize is set when the plugin is started
- **COMPort \[integer\]:** COM Port of FABI.
