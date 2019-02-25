# AsTeRICS Overview

This pages explains the most important terms and elements of the AsTeRICS framework.

## Elements

The framework consists of several elements you should know about.

### Model

A model describes the logic of an Assistive Technology. It is used to define how plugins (components) are connected together and which input modalities to use.

### Plugin (Component)

A plugin represents a modular component that can be used within a model.
It can be a sensor (incorporating data into the system), a processor (processing, modifying data) or an actuator (generating actions in the environment).

### AsTeRICS Grid

The AsTeRICS grid is a web-based user interface which can be used for Augmentative and Alternative Communication (AAC). It can be connected to a model and be used to trigger actions.

### AT Solution

An AT solution is a more complex Assistive Technology and may be comprised of several models, grids, images or web user interfaces.

## Executables

AsTeRICS consists of several executable programs.

### ACS (WebACS)

AsTeRICS Configuration Suite is a graphical editor for easy building and adapting
"Assistive Technologies".
For this purpose you use sensors, actuators and processors, which are included in
the software. You can connect these elements and build your own individual setup
(also called a "model"), which is specially adapted for your needs, easily and fast.

### ARE

The AsTeRICS Runtime Environment is the basic software framework for all
AsTeRICS applications and use-cases. The models built in the ACS will run in the
ARE. The ACS can be seen as a configuration program for the ARE.
