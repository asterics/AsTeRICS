---
title: Slider
---

# Slider

### Component Type: Sensor (Subcategory: Graphical User Interface)

The Slider component generates a slider with adjustable range of values and size on the ARE desktop. This slider can be used to change important parameters of the model during runtime. Furthermore, an incoming signal can be adjusted by the slider component, using a gain factor property.

![Screenshot: Slider plugin](./img/Slider.jpg "Screenshot: Slider plugin")  
Slider plugin

## Input Port Description

- **setValue \[integer\]:** Sets the slider position to the incoming value. Note that this value is not propagated to the output port (to avoid loops).
- **in \[double\]:** input port for an incoming signal which can be amplified by the slider component

## Output Port Description

- **value \[integer\]:** This port provides the currently selected slider value (position). Only integer values are possible.
- **out \[double\]:** The amplified (or attenuated) input signal (out = in \* gain \* slider value)

## Properties

- **min \[integer\]:** The minimum value of the slider range
- **max \[integer\]:** The maximum value of the slider range
- **default \[integer\]:** The defualt position of the slider at model startup (this value is not automatically sent to the port at model startup.
- **gain \[double\]:** The amplification value for an (optional) incoming signal (out = in \* gain \* slider value)
- **caption \[string\]:** A label for the slider
- **majorTickspacing \[integer\]:** Coarse sections for the slider value captions
- **minorTickspacing \[integer\]:** Fine sections for the slider value captions
- **alignment \[integer, combobox selection\]:** Slider orientation in the GUI, can be horizontal or vertical
- **fontSize \[integer\]:** Font size of the caption
- **storeValue \[boolean\]:** if the storeValue property is enabled, the current value of the slider position is stored and restored when the model is started next time. Note that this overrides the defaultValue property.
- **displayGUI \[boolean\]:** if selected, the GUI of this component will be displayed - if not, the GUI will be hidden and disabled.
