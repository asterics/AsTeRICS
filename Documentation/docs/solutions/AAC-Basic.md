# Basic AAC Grid Solution

## Objective

Basic grid for Alternative and Augmentative Communication including a simple on-screen keyboard with speech synthesis.

## Description

The grid provides cells with basic communication topics (e.g. Help, Eating, Environment) and also provides a basic on-screen keyboard. A cell can be triggered by several modalities depending on the capabilities of the user. Currently the input options clicking, scanning (for switch-based control) or hovering (for eye-tracking based control) are supported. If clicking is enabled, the solution can also be operated by the [Camera Mouse Solution](/solutions/#computer-control) or [Eye Tracking Solution](/solutions/#computer-control). Additionally, the grid text and images can be edited. Each grid has a unique link and is automatically synchronized when using the same link on different end devices.

![Symbol based grid for communication](/img/AsTeRICS-Ergo_Grid_en-1-768x592.jpg)

## Requirements

* Web browser (Chrome and Firefox preferred)

### Optional Requirements

* Scanning
  * Keyboard key (```Space```) or
  * external Switch (e.g. [FABI](https://www.asterics-foundation.org/projects/fabi/)) attached and configured to emulate the ```Space```key.
* Environmental Control
    * AsTeRICS installed and ARE running
    * [IrTrans device](http://www.irtrans.de/en/shop/usb.php)
    * OS: Windows, Linux (incl. RPi), Mac OSX

## Major Plugins

* [IrTrans (optional)](/plugins/sensors/IrTrans)
