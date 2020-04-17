---
title: FacetrackerLK
---

# XFacetrackerLK

### Component Type: Sensor (Subcategory: Computer Vision)

This component provides a cross-platform face tracking computer vision algorithm which puts out estimated movement of a users� nose and chin (not supported yet) in x and y coordinates. The underlying mechanism builds upon the JavaCV (OpenCV) library (in particular a trained cascade of haar-like features is used to track a face and its movement). The x- and y- coordinates can be used in camera-mouse configurations or to enable selection or control tasks. The values are only provided if a face can be tracked by the algorithm and are updated with the achievable frame rate. Note that the x- and y- values represent relative movement in pixels and have to be accumulated (e.g. via the integrate component) to generate e.g. absolute mouse positions.

**The plugin is still experimental and should not be used for end user AT solutions at the moment.**

## Requirements

A camera has to be available (this can be any webcam or a camera which is available as image acquisition device via the operating system). The picture below shows the Logitech Webcam 9000 Pro.

![Logitech Webcam 9000 Pro](./img/webcam.jpg "Logitech Webcam 9000 Pro")  
Logitech Webcam 9000 Pro

## Output Port Description

- **noseX \[integer\]:** This value specifies the relative change in the x coordinate of the user's nose with respect to the previous image frame.
- **noseY \[integer\]:** This value specifies the relative change in the y coordinate of the user's nose with respect to the previous image frame.
- **chinX \[integer\]:** Not supported yet
- **chinY \[integer\]:** Not supported yet

## Event Listener Description

No event listeners supported.

## Properties

The property settings are not supported yet.
