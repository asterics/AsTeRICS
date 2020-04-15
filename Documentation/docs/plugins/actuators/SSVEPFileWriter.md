##

## SSVEP File Writer

# SSVEP File Writer

### Component Type: Actuator (Subcategory: Brain Computer Interface)

This plugin writes to a text file the 4 EEG channels along with a software trigger received through the event listener ports. This file is lately analyzed by the [ProtocolSSVEPTrain](../processors/ProtocolSSVEPTrain.htm) plugin to obtain the optimus frequencies to be used on the SSVEP detection.

![Screenshot: SSVEPFileWriter plugin](./img/SSVEPFileWriter.jpg "Screenshot: SSVEPFileWriter plugin")  
SSVEPFileWriter plugin

## Input Port Description

- **filename \[string\]:** Name of the file to be saved.
- **channel1 to channel4 \[integer\]:** Input EEG signal from channels 1 to 4.
- **StimulationFrequency \[integer\]:** If a stimulation frequency value is received before the StarStimulation Event the stimulation freqeuncy is appended to the name of the output file to be saved.

## Event Listener Description

- **StartTrial:** An incoming event at this port starts the file writer process.
- **StopTrial:** An incoming event at this port stops the file writer process.
- **StartStimulation:** An incoming event at this port sets the trigger channel to the corresponding stimulation frequency value in Hz.
- **StopStimulation:** An incoming event at this port sets the trigger channel to zeros.

## Properties

- **DefaultFileName \[string\]:** The default file name.
