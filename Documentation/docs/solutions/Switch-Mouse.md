# Switch Mouse Solution

## Objective

Mouse control by switch input. This model demonstrates mouse cursor control via 2 switches or (alternatively) 2 keyboard keys. (There are other models for [single-switch](http://webacs.asterics.eu/?areBaseURI=https://127.0.0.1:8083&openFile=https://github.com/asterics/AsTeRICS/raw/master/bin/ARE/models/useCaseDemos/mouseControl/crosshairCursorControl_1key.acs) or [multi-switch](http://webacs.asterics.eu/?areBaseURI=https://127.0.0.1:8083&openFile=https://github.com/asterics/AsTeRICS/raw/master/bin/ARE/models/useCaseDemos/mouseControl/crosshairCursorControl_4keys%20_events.acs) cursor control available in the [`ARE/models`](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/models/useCaseDemos/mouseControl) folder.)
Instead of a mouse cursor, a crosshair indicator is displayed on the screen, scanning from the top to the bottom and from the left to the right. 

## Mouse movement

By pressing the `right` cursor key the crosshair should move accordingly from the left to the right as long as you keep the key pressed. Subsequently, press the `down` cursor key to move the cross from the top to the bottom. As soon as the keys are released, the left click is performed by dwelling (stopping movement and waiting for some time). When the crosshair exits the screen, it wraps around and enters the screen from the other side. Thus, every position on the screen can be reached.

![Cross marking the click position on the screen](./img/crosshair-cross.png)

*Fig. 1: Crosshair indicating the current mouse position. Red horizontal line indicating a movement from top to bottom.*

## Tooltips

In order to perform special clicks, so called "tooltips" are available: Press `right` and `down` cursor keys simultaneously in order to show the tooltips, which look like this:

![Cross marking the click position on the screen](./img/crosshair-tooltips.jpg)

*Fig. 2: Tooltip next to the crosshair, shows symbol for double click*

By pressing one of `right` or `down` cursor keys again it's possible to navigate through the tooltips where functions like double click, right click, dragging or keyboard shortcuts like `Ctrl + C` and `Ctrl + V` can be chosen.


## Requirements

* Switch
  * Keyboard key (```Cursor right```, ```Cursor down```) or
  * external Switch (e.g. [FABI](https://www.asterics-foundation.org/projects/fabi/)) attached and configured to emulate ```Cursor right```and ```Cursor down``` keys.
* AsTeRICS installed and ARE running
* OS: Windows, Linux (incl. RPi), Mac OSX

## Major Plugins

* [KeyCapture](/plugins/sensors/KeyCapture)
* [Mouse](/plugins/actuators/Mouse)
* [CrosshairCursorControl](/plugins/actuators/CrosshairCursorControl)
* [Tooltip](/plugins/actuators/Tooltip)
