  
---
DialogBox
---

# DialogBox

### Component Type: Actuator (Subcategory: Graphical User Interface)

Creates a popup dialog box with configurable text message and button texts. Can be used to inform the user or get user input.

## Requirements

No special requirements.

## Input Port Description

*   **setText \[string\]:** Updates the text to be displayed.

## Properties

*   **caption \[string\]:** The caption of the window.
*   **text \[string\]:** the text message of the dialog box
*   **alwaysOnTop \[boolean\]:** if true, dialog stays on top, no matter if other windows are put to front.
*   **messageType \[integer\]:** the message type of the dialog box, one of: plain,information,question,warning,error
*   **buttonText\[1-5\] \[string\]:** The button text

## Event Listener Description

*   **displayBox**: Displays the dialog box.
*   **hideBox**: Disposes the dialog box.

## Event Triggerer Description

*   **button\[1-5\]**: Button\[1-5\] pressed