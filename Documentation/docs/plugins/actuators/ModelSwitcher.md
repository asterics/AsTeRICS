---
title: ModelSwitcher
---

# Model Switcher

### Component Type: Actuator (Subcategory: File System)

The ModelSwitcher component allows to switch from the running model to another model which will be deployed and started. This makes it possible to build menus for different use-cases or switch from one use-case to another.

![Screenshot: ModelSwitcher plugin](./img/ModelSwitcher.jpg "Screenshot: ModelSwitcher plugin")  
ModelSwitcher plugin

## Input Port Description

- **modelName \[string\]:** The name of the model (including extension, for example "CameraMouse_sensitive.acs". The switch is performed as soon as the model name is received. The model must exist in the ARE/models folder of the runtime environment.

## Event Listener Description

- **switchModel:** An incoming event on this port will switch to the default model.

## Properties

- **model\[string\]:** A fixed model name can be give here. This model must exist in the ARE/models folder of the runtime environment. The model switch is performed when the switchModel event is received.
