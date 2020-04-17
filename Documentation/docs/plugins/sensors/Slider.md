---
title: Slider
---

# Slider

### Component Type: Sensor (Subcategory: Graphical User Interface)

The Slider component generates a slider with adjustable range of values and size on the ARE desktop. This slider can be used to change important parameters of the model during runtime.

![Screenshot: Slider plugin](./img/Slider.jpg "Screenshot: Slider plugin")  
Slider plugin

## Output Port Description

- **value \[integer\]:** This port provides the currently selected value. Only integer values are possible. If fractional values are desired, these have to be generated from the integer values e.g. by using the math evaluator component.

## Properties

- **min \[integer\]:** The minimum value of the slider range
- **max \[integer\]:** The maximum value of the slider range
- **default \[integer\]:** The defualt position of the slider at model startup (this value is not automatically sent to the port at model startup.
- **caption:** A label for the slider
- **majorTickspacing:** Coarse sections for the slider value captions
- **minorTickspacing:** Fine sections for the slider value captions
- **alignment:** Slider orientation in the GUI, can be horizontal or vertical
- **fontSize:** Font size of the caption
