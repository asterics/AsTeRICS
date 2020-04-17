##

## Flickering Light Stimulator

# Flickering Light Stimulator

### Component Type: Actuator (Subcategory: Brain Computer Interface)

This plug-in allows the user to interact with the [Flickering Light Stimulator](../actuators/img/Manual_FlickeringLightStimulator.pdf) panels by configuring their properties (stimulation frequency, duty cycle, intensity and colour) and start/stop the light stimulation. The stimulation frequency of the panels can be modified while the stimulation is running.

![Screenshot: Flickering Light Stimulator plugin](./img/FlickeringLightStimulator.jpg "Screenshot: Flickering Light Stimulator plugin")  
Flickering Light Stimulator plugin

## Requirements

This software component requires at least one of the four Flickering Light Stimulator panels to be connected to any available COM port. The COM port number to which the panels are connected must be configured in the properties before starting the plugin.

![Screenshot: Flickering Panels setup](./img/FlickeringPanels.jpg "Screenshot: Flickering Panels setup")  
Flickering Panels setup

## Input Port Description

- **panel1 to panel4 \[integer\]:** input ports for the stimulation frequency of each panel in Hertz. Their value ranges from 1 to 50 Hertz.

## Event Listener Description

- **startStim:** starts the stimulation of the flickering light panels when receiving an event.
- **stopStim:** stops the stimulation of the flickering light panels when receiving an event.
- **updateConfiguration:** updates the configuration of the panels (stimulation frequency, duty cycle, intensity and colour) when receiving an event.
- **startStimPeriod:** starts a stimulation of N miliseconds.

## Event Trigger Description

- **stimPeriodFinished:** an event is emitted through this port when the stimulation period of N miliseconds has finished.

## Properties

- **frequencyPanel1 \[integer\]:** stimulation frequency of panel 1 in Hertz. This property ranges from 1 to 50 Hertz.
- **frequencyPanel2 \[integer\]:** stimulation frequency of panel 2 in Hertz. This property ranges from 1 to 50 Hertz.
- **frequencyPanel3 \[integer\]:** stimulation frequency of panel 3 in Hertz. This property ranges from 1 to 50 Hertz.
- **frequencyPanel4 \[integer\]:** stimulation frequency of panel 4 in Hertz. This property ranges from 1 to 50 Hertz.
- **dcPanel1 \[integer\]:** duty cycle of panel 1. This property ranges from 1 (short cycle) to 100 (large cycle).
- **dcPanel2 \[integer\]:** duty cycle of panel 2. This property ranges from 1 (short cycle) to 100 (large cycle).
- **dcPanel3 \[integer\]:** duty cycle of panel 3. This property ranges from 1 (short cycle) to 100 (large cycle).
- **dcPanel4 \[integer\]:** duty cycle of panel 4. This property ranges from 1 (short cycle) to 100 (large cycle).
- **intPanel1 \[integer\]:** light intensity of panel 1. This property ranges from 1 (low intensity) to 100 (high intensity).
- **intPanel2 \[integer\]:** light intensity of panel 2. This property ranges from 1 (low intensity) to 100 (high intensity).
- **intPanel3 \[integer\]:** light intensity of panel 3. This property ranges from 1 (low intensity) to 100 (high intensity).
- **intPanel4 \[integer\]:** light intensity of panel 4. This property ranges from 1 (low intensity) to 100 (high intensity).
- **red \[boolean\]:** presence of red colour in all the panels.
- **blue \[boolean\]:** presence of blue colour in all the panels.
- **green \[boolean\]:** presence of green colour in all the panels.
- **comPort \[string\]:** COM port number to which the panels are connected. The string must be expressed as "COMx", where x is the COM port number.
- **N \[integer\]:** duration in miliseconds of the stimulation started by the startStimPeriod event.
