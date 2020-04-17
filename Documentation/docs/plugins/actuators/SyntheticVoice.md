---
title: SyntheticVoice
---

# Synthetic Voice

Component Type: Actuator (Subcategory: Audio and Voice)

The Synthetic Voice component uses the SAPI 5 technology to generate synthetic voice.

![Screenshot: SyntheticVoice plugin](./img/SyntheticVoice.jpg "Screenshot: SyntheticVoice plugin")

SyntheticVoice plugin

## Requirements

The appropriate voice should be installed on the platform.

## Input Port Description

- **input \[string\]:** The text sentence, which will be converted into speech.

## Properties

- **volume \[integer\]:** Defines the volume of the voice. The volume propety values should be in range from 0 to 100.
- **speed \[integer\]:** Defines the speed of the voice. The speed propety values should be in range from -10 to 10.
- **voice \[string\]:** Specifies the voice used for the speech synthesis.
- **xmlTags \[boolean\]:** Defines if the XML tags in the input text will be suported.
