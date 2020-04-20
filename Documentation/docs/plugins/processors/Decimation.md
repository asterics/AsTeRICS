---
title: Decimation
---

# Decimation

Component Type: Processor (Subcategory: Basic Math)

The decimation operation performed by this component consists in an anti-aliasing low band pass filter plus a down-sampling. The component outputs a computed sample after receiving a certain number of input samples according to the down-sampling-ratio property value. So the resultant signal is like the original signal, but sampled to a lower ratio determined by the mentioned property.

![Screenshot: Decimation plugin](./img/Decimation.jpg "Screenshot: Decimation plugin")  
Decimation plugin

## Input Port Description

- **input \[double\]:** Input port for the signal to be decimated.

## Output Port Description

- **output \[double\]:** Output port of the decimated signal.

## Properties

- **DownSamplingRatio \[integer\]:** Defines the ratio between the number of samples in the input and output ports.
