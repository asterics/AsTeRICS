---
title: IntelRealSense
---

# IntelRealSense

### Component Type: Sensor (Subcategory: Computer Vision)

**OS: >= Windows 8.1, 64 bit**

This component interfaces the Intel Real Sense F200 (SR300) 3D camera and provides head tracking and facial gesture recognition functionality. The head tracking could be used for camera mouse applications and provides 3D absolute coordinates. The facial gestures could be used for simple actions like mouse click or key emulation. Each facial gesture has a recognition score. The user can define the threshold that triggers an event when the score exceeds the threshold.

## Requirements

You need an [Intel Real Sense F200 (SR300)](https://software.intel.com/de-de/realsense/sr300camera) camera plugged in and the SDK installed and running. The camera only supports Windows (>= 8.1, 64 Bit).

![Intel Real Sense 3D camera](./img/realsense_f200_camera.jpg "Intel Real Sense F200 (SR300) camera")  
Intel Real Sense F200 (SR300) camera

## Output Port Description

- **h \[integer\]:** The face rectangle height in pixels.
- **w \[integer\]:** The face rectangle width in pixels.
- **x \[integer\]:** The x coordinate of the top left corner of the face rectangle.
- **y \[integer\]:** The y coordinate of the top left corner of the face rectangle.
- **roll \[integer\]:** Degree of head roll.
- **yaw \[integer\]:** Degree of head yaw (left - right).
- **pitch \[integer\]:** Degree of head pitch (up - down).

## Event Trigger Description

- **browRaiserLeft:** browRaiserLeft expression happened
- **browRaiserRight:** browRaiserRight expression happened
- **browLowererLeft:** browLowererLeft expression happened
- **browLowererRight:** browLowererRight expression happened
- **smile:** smile expression happened
- **kiss:** kiss expression happened
- **mouthOpen:** mouthOpen expression happened
- **tongueOut:** tongueOut expression happened
- **eyesClosedLeft:** eyesClosedLeft expression happened
- **eyesClosedRight:** eyesClosedRight expression happened
- **eyesTurnLeft:** eyesTurnLeft expression happened
- **eyesTurnRight:** eyesTurnRight expression happened
- **eyesUp:** eyesUp expression happened
- **eyesDown:** eyesDown expression happened
- **puffLeft:** puffLeft expression happened
- **puffRight:** puffRight expression happened

## Properties

- **deviceModel \[combobox selection\]:** Camera model, one of: F200, R200, R200_Enhanced, SR300
- **enableExpressions:** Enable,Disable facial expressions
- **displayGUI:** Enable, Disable camera gui.
- **scoreBrowRaiserLeft:** Set score threshold value of expression to trigger event.
- **scoreBrowRaiserRight:** Set score threshold value of expression to trigger event.
- **scoreBrowLowererLeft:** Set score threshold value of expression to trigger event.
- **scoreBrowLowererRight:** Set score threshold value of expression to trigger event.
- **scoreSmile:** Set score threshold value of expression to trigger event.
- **scoreKiss:** Set score threshold value of expression to trigger event.
- **scoreMouthOpen:** Set score threshold value of expression to trigger event.
- **scoreThongueOut:** Set score threshold value of expression to trigger event.
- **scoreEyesClosedLeft:** Set score threshold value of expression to trigger event.
- **scoreEyesClosedRight:** Set score threshold value of expression to trigger event.
- **scoreEyesTurnLeft:** Set score threshold value of expression to trigger event.
- **scoreEyesTurnRight:** Set score threshold value of expression to trigger event.
- **scoreEyesUp:** Set score threshold value of expression to trigger event.
- **scoreEyesDown:** Set score threshold value of expression to trigger event.
- **scorePuffLeft:** Set score threshold value of expression to trigger event.
- **scorePuffRight:** Set score threshold value of expression to trigger event.
