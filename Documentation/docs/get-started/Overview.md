# AsTeRICS Overview

This pages explains the most important terms and elements of the AsTeRICS framework.

::: warning TODO
Add illustrative diagram!!
:::

## Elements

The framework consists of several elements you should know about.

### Model

A model describes the logic of an Assistive Technology (AT). It is used to define how plugins (components) are connected together and which input modalities to use.

### Plugin (Component)

A plugin represents a modular component that can be used within a model.
It can be a sensor (incorporating data into the system), a processor (processing, modifying data) or an actuator (generating actions in the environment).

### Grid

The grid is a web-based user interface which can be used for Augmentative and Alternative Communication (AAC). It can be connected to a model and be used to trigger actions.

### AT Solution

An AT solution is a more complex Assistive Technology and may be comprised of several models, grids, images or web user interfaces.

## Executables

AsTeRICS consists of several executable programs.

### ACS

The AsTeRICS Configuration Suite (ACS) is a graphical editor for easy designing and testing model files.

### WebACS

The WebACS is a new web-based version of the [ACS](#acs) program for designing and testing model files.

### ARE

The AsTeRICS Runtime Environment (ARE) executes a given model or AT solution.

### APE

The AsTeRICS Packaging Environment (APE) is needed for the development of an AT Solution. Fore more details, read the [developer docs](../develop/AT_solution_development.html).