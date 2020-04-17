---
title: SpaceNavigtor3DMouse
---

# Space Navigtor 3D Mouse

### Component Type: Sensor (Subcategory: Standard Input Devices)

This component interfaces the 3Dconnexion 3D Mouse device.

![Screenshot:
        SpaceNavigator3DMouse plugin](./img/SpaceNavigator3DMouse.jpg "Screenshot:
        SpaceNavigator3DMouse plugin")  
SpaceNavigator3DMouse plugin

## Requirements

The 3D Mouse device connected to the platform

![SpaceNavigator 3DMouse](./img/SpaceNavigator.jpg "SpaceNavigator 3DMouse")  
SpaceNavigator 3DMouse

## Output Port Description

- **mouseX \[integer\]:** Data of axis X.
- **mouseY \[integer\]:** Data of axis Y.
- **mouseZ \[integer\]:** Data of axis Z.
- **mouseRx \[integer\]:** Data of rotation of axis X.
- **mouseRy \[integer\]:** Data of rotation of axis Y.
- **mouseRz \[integer\]:** Data of rotation of axis Z.
- **buttons \[integer\]:** Data of selected buttons combination.

## Properties

- **interval \[integer\]:** The interval of capturing 3D mouse state (ms).
