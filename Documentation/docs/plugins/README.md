---
title: AsTeRICS Plugins
---

# Plugins

<!-- ::: tip Note
In the following sections, all the plugins available in the AsTeRICS framework will be presented. The plugins can be divided into 3 groups: sensor modules, processing modules and actuator modules.

- **Sensors** include all plugins which make physical or simulated data available to a model and other plugins of it. Examples include a digital switch interface, a bioelectrical signal sensor (e.g. EMG) a webcam or an eye tracker.
- **Processors** include mathematical transformations, feature detectors, threshold level monitors etc. which are necessary to scale or combine signals or detect interesting events in the data streams acquired from the sensors.
- **Actuators** enable assistive functionalities like mouse- or keyboard replacement, visual or acoustic feedback or direct interaction with the environment, e.g. infrared remote control, home automation or physical manipulation.
  ::: -->

The plugins represent the functional blocks of AsTeRICS. They can be combined to tailored Assistive Technology setups via the graphical AsTeRICS Configuration Suite (ACS) software, where plugins can be selected, connected via channels, and parameterized. Thus, the ACS can be considered as a user friendly environment to arrange the plugins. The result of this configuration process is an .xml file containing the deployment model for the AsTeRICS Runtime Environment (ARE).

<ClientOnly>
<Plugins-Search/>

<Plugins-Sensor/>
<Plugins-Processor/>
<Plugins-Actuator/>
</ClientOnly>
