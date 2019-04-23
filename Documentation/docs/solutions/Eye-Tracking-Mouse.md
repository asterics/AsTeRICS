# Eye Tracking Mouse Solution

## Objective

Mouse control (moving mouse cursor, clicking and dragging) by eye movements.

## Description

By moving the eyes up/down or left/right the mouse cursor should move accordingly. A left click is performed by dwelling (stopping movement and waiting for some time). To do a right, double or drag click select the respective button in the ARE GUI and move the cursor to the location where the click should be performed at. The camera device, the mouse speed and other settings can be changed by clicking onto the ```Settings``` button. Additionally, external switches can be configured for clicking and an on-screen keyboard of choice can be defined.

## Requirements

* [Tobii Eye Tracker 4C](https://tobiigaming.com/eye-tracker-4c/) attached
* [Tobii Eye Tracking Core Software](https://tobiigaming.com/getstarted/) installed and running.
* AsTeRICS installed and ARE running
* OS: Windows

## Major Plugins

* [EyeX](/plugins/sensors/EyeX)
* [Mouse](/plugins/actuators/Mouse)