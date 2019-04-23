---
title: AdjustmentCurve
---

# AdjustmentCurve

Component Type: Processor (Subcategory: Signal Shaping)

The AdjustmentCurve component allows transformation of an incoming signal to an outgoing signal. The signal mapping can be freely arranged in a drawing window (GUI) during runtime of the model. The resulting mapping can be saved as a curve file. The GUI is optional - an existing curve can be loaded to perform the signal mapping without the GUI.

![Screenshot: AdjustmentCurve plugin](./img/adjustmentcurve.jpg "Screenshot: AdjustmentCurve plugin")

AdjustmentCurve plugin

![Screenshot: AdjustmentCurve GUI during runtime](./img/adjustmentcurve1.jpg "Screenshot: AdjustmentCurve GUI during runtime")

AdjustmentCurve GUI during runtime

## Input Port Description

*   **in \[double\]:** This port receives the input values which will be mapped to output values.
*   **CurveName \[string\]:** When this port receives a string, the plugin tries to load a curve file of this name from the plugin's data subdirectory (ARE/data/processor.adjustmentcurve).

## Output Port Description

*   **out \[double\]:** This port provides the resulting output value.

## Event Listener Description

*   **displayGui:** An incoming event makes the GUI visible.
*   **hideGui:** An incoming event makes the GUI invisible.
*   **loadCurve:** An incoming event loads a curve of the current filename (as given in the plugin property or received from the input port "curveName").
*   **saveCurve:** An incoming event saves the current mapping curve under the given filename. This event has teh same function like the "save"-button which is available in the GUI window of the plugin.

## Properties

*   **filename \[string\]:** The filename of the curve file. If the curve file exists in the plugin's subdirectory (ARE/data/processor.adjustmentcurve), this curve is loaded. If the file does not exist, a new curve can be drawn in the GUI and saved under this name into the plugin's data subfolder (ARE/data/processor.adjustmentcurve).
*   **display GUI \[boolean\]:** If checked, the GUI of the adjustment curve plugin will be shown and the user can modify and save the curve in real time by dragging, creating or deleting curve points.
*   **intMin \[double\]:** Sets the minimum value of the input range.
*   **outMax \[double\]:** Sets the maximum value of the input range.
*   **outMin \[double\]:** Sets the minimum value of the output range.
*   **outMax \[double\]:** Sets the maximum value of the output range.
*   **mode \[combobox\]:** GUI mode: "autoupdate min and max" modifies the shown input range of the GUI if incoming values exceed the current minimum or maximum, "clip to min and max" which keeps the values as set by the min/max properties.
*   **fontSize \[integer\]:** The size of the font for dispaying text in the GUI.
*   **caption \[string\]:** The caption of the AdjustmentCurve GUI.
*   **operationMode \[integer\]:** Selects the mode of operation of the adjustment curve component, following modes are available:
    *   _"use file and GUI":_ Default mode. The x/y points of the adjustment curve are defined using the graphical user interface and are stored and loaded from a file, defined by property 'filename'.
    *   _"use property curvePoints (percent values)":_ No GUI is shown, x/y points of the adjustmentcurve are defined by property 'curvePoints' and interpreted as relative percentatge values of given in/out min/max values. In this mode no external file is needed or used.
    *   _"use property curvePoints (absolute values)":_ Same mode as before, using property 'curvePoints' with the difference that x/y values are defined as absolute values.
*   **curvePoints \[string\]:** For operation mode 'use property curvePoints' this property defines the x/y points of the adjustment curve. The expected format are comma-separated tuples of double or integer values like e.g. '(10.0,0.0),(10,10),(100,100)' defining 3 x/y points.