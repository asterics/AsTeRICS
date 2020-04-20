---
title: IIRFilter
---

# IIRFilter

Component Type: Processor (Subcategory: DSP and Feature Detection)

This plugin provides adjustable Infinite Impulse Response Filters, based on the Java DSP Library: [http://www.source-code.biz/dsp/java](http://www.source-code.biz/dsp/java/)

![Screenshot: IIRFilter plugin](./img/IIRFilter.jpg "Screenshot: IIRFilter plugin")  
IIRFilter plugin

## Input Port Description

- **in \[double\]:** The signal to be filtered

## Input Port Description

- **out \[double\]:** The filtered signal
- **magnitude \[double\]:** the magnitude of the filtered signal in the selected passband (only calculated if the passType = bandpass !)

## Properties

- **passType \[integer\] (combobox selection):** can be lowpass, highpass, bandpass or bandstop

- **characteristicType \[integer\] (combobox selection):** can be butterworth, bessel or chebyshev

- **order \[integer\]:** the filter order (values from 1 to 12 recommended)

- **samplingFrequency \[integer\]:** the sampling rate of the input signal

- **fc1 \[double\]:** corner frequency (lower corner frequency for bandpass filter)

- **fc2 \[double\]:** higher corner frequency (ignored in case of highpass or lowpass types)

- **ripple \[double\]:** the passband ripple supression, must be a negative value (only for chebyshev types, ignored for other types)
