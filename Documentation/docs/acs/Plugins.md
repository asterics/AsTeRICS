---
title: Plugins
---

# Plugins

In the following sections, all the plugins available in the AsTeRICS framework will be presented.
The plugins can be divided in 3 groups: sensor modules, processing modules and actuator modules.

- _Sensors_ include all software modules which make physical or simulated data available to other AsTeRICS plugins.
  Examples include a digital switch interface, an analogue sip/puff sensor, a webcam or a signal generator.
- _Processors_ include mathematical transformations, feature detectors, threshold level monitors etc. which are necessary to scale or combine signals or detect interesting events in the data streams acquired from the sensors.
- _Actuators_ enable assistive functionalities like mouse- or keyboard replacement, visual or acoustic feedback or direct interaction with the environment, e.g. infrared remote control, home automation or physical manipulation.

The plugins represent the functional blocks of AsTeRICS.
They can be combined to tailored Assistive Technology setups via the graphical AsTeRICS Configuration Suite software application (ACS), where plugins can be selected, connected via channels, and parameterized.
Thus, the ACS can be considered as a user friendly environment to arrange the plugins.
The result of this configuration process is an .xml file containing the deployment model for the AsTeRICS Runtime Environment (ARE).
