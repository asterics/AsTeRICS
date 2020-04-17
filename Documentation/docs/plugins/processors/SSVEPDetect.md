---
title: SSVEP Detect
---

# SSVEP Detect

This component is in charge of evaluating the SSVEP response (up to 4 different frequencies) among the frequencies defined by the user. It also calculates the config file based on previously recorded training files that will be used to evaluate the detection, and is also in charge of updating its parameters according to the config file

### Component Type: Processor (Subcategory: DSP and Feauture extraction)

![Screenshot: SSVEPDetect plugin](./img/SSVEPDetect.jpg "Screenshot: SSVEPDetect plugin")  
SSVEPDetect plugin

## Requirements

SSVEPTrainFunction.exe and Matlab 2008B runtime engine

## Input Port Description

- **O1 \[double\]:** Input port for the EEG channel O1. **This input port supports synchronization**
- **Oz \[double\]:** Input port for the EEG channel Oz. **This input port supports synchronization**
- **O2 \[double\]:** Input port for the EEG channel O2. **This input port supports synchronization**
- **UserName \[string\]:** Name Identifying current the user.
- **NumberOfPanels \[string\]:** Number of stimulation panels.
- **FreqP1 \[string\]:** Stimulation frequency of panel 1.
- **FreqP2 \[string\]:** Stimulation frequency of panel 2.
- **FreqP3 \[string\]:** Stimulation frequency of panel 3.
- **FreqP4 \[string\]:** Stimulation frequency of panel 4.

## Output Port Description

- **FreqP1 \[integer\]:** Stimulation frequency of panel 1.
- **FreqP2 \[integer\]:** Stimulation frequency of panel 2.
- **FreqP3 \[integer\]:** Stimulation frequency of panel 3.
- **FreqP4 \[integer\]:** Stimulation frequency of panel 4.

## Event Listener Description

- **StartStim:** Event Informing that the stimulation period has started.
- **StopStim:** Event Informing that the stimulation period has finished.
- **CalculateConfigFile:** Event requesting the calculation of the configuration file
- **UpdateFromConfigFile:** Event requested an update of the properties according to the configuration file.
- **UpdatePanelsConfig:** Event reporting the stimulation panels plugin to update the stimulation frequencies.

## Event Trigger Description

- **UpdatePanelsConfig:** Event reporting the stimulation panels plugin to update the stimulation frequencies.
- **NonStimFreqD:** Event reporting that none stimulation frequency could be detected.
- **StimFreq1D:** Event reporting that stimulation frequency number 1 was detected.
- **StimFreq2D:** Event reporting that stimulation frequency number 2 was detected.
- **StimFreq3D:** Event reporting that stimulation frequency number 3 was detected.
- **StimFreq4D:** Event reporting that stimulation frequency number 4 was detected.

## Properties

- **SF1GO1 \[double\]:** Spatial filter coefficient for stimulation frequency 1 and electrode O1.
- **SF1GOz \[double\]:** Spatial filter coefficient for stimulation frequency 1 and electrode Oz.
- **SF1GO2 \[double\]:** Spatial filter coefficient for stimulation frequency 1 and electrode O2.
- **SF2GO1 \[double\]:** Spatial filter coefficient for stimulation frequency 2 and electrode O1.
- **SF2GOz \[double\]:** Spatial filter coefficient for stimulation frequency 2 and electrode Oz.
- **SF2GO2 \[double\]:** Spatial filter coefficient for stimulation frequency 2 and electrode O2.
- **SF3GO1 \[double\]:** Spatial filter coefficient for stimulation frequency 3 and electrode O1.
- **SF3GOz \[double\]:** Spatial filter coefficient for stimulation frequency 3 and electrode Oz.
- **SF3GO2 \[double\]:** Spatial filter coefficient for stimulation frequency 3 and electrode O2.
- **SF4GO1 \[double\]:** Spatial filter coefficient for stimulation frequency 4 and electrode O1.
- **SF4GOz \[double\]:** Spatial filter coefficient for stimulation frequency 4 and electrode Oz.
- **SF4GO2 \[double\]:** Spatial filter coefficient for stimulation frequency 4 and electrode O2.
- **StimFreq1 \[integer\]:** Stimulation frequency number 2 in Hz. If its value is 0 it will not be evaluated in the SSVEP detection..
- **StimFreq2 \[integer\]:** Stimulation frequency number 2 in Hz. If its value is 0 it will not be evaluated in the SSVEP detection.
- **StimFreq3 \[integer\]:** Stimulation frequency number 3 in Hz. If its value is 0 it will not be evaluated in the SSVEP detection.
- **StimFreq4 \[integer\]:** Stimulation frequency number 4 in Hz. If its value is 0 it will not be evaluated in the SSVEP detection.
- **BestHarm1 \[integer\]:** Best harmonic to detect stimulation frequency 1.
- **BestHarm2 \[integer\]:** Best harmonic to detect stimulation frequency 2.
- **BestHarm3 \[integer\]:** Best harmonic to detect stimulation frequency 3.
- **BestHarm4 \[integer\]:** Best harmonic to detect stimulation frequency 4.
