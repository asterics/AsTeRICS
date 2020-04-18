---
title: SSVEP Stimulator
---

# SSVEP Stimulator

Component Type: Actuator (Subcategory: Brain Computer Interface)

This plug-in allows the user to interact with the SW-generated flickering surfaces (panels) for SSVEP stimulation. The stimulation frequency of the panels can be modified before the stimulation is started.

![Screenshot: SSVEPStimulator application](./img/SSVEPStimulator.jpg "Screenshot: SSVEPStimulator application")

SSVEPStimulator application

## Requirements

A recent version of DirectX has to be installed.

## Input Port Description

- **frequency \[integer\]:** the stimulation frequency the SW-generated panel in Hertz. The value ranges from 1 to 20 Hertz.

## Event Listener Description

- **startStim:** starts the stimulation of the SW-generated flickering panels when receiving an event.
- **stopStim:** stops the stimulation of the SW-generated flickering panels when receiving an event.

## Event Trigger Description

- **stimPeriodFinished:** an event is emitted through this port when the stimulation period of N miliseconds has finished.

## Properties

- **onBitmapFile \[string\]:** filename of a bitmap file which is used in the on-phase of the stimulation. The file is expected in the data/SSVEPStimulator subfolder of the ARE. The filename is given without extenstion (e.g. "arrow_up" for the file "ARE/data/SSVEPStimulator/arrow_up.bmp")
- **offBitmapFile \[string\]:** filename of a bitmap file which is used in the off-phase of the stimulation (same filename as above).
- **xPosition \[integer\]:** x-position of the flickering surface on the desktop/screen.
- **yPosition \[integer\]:** y-position of the flickering surface on the desktop/screen.
- **frequency \[integer\]:** default stimulation frequency of in Hertz. This property ranges from 1 to 20 Hertz.
- **msec \[integer\]:** duration in miliseconds of the stimulation started by the startStimPeriod event.
