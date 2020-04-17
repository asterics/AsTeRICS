---
title: Quantizer
---

# Quantizer

Component Type: Processor (Subcategory: Basic Math)

This plugin performs a quantization of the input signal. The value of the output signal is the input value rounded to the nearest multiple of the quantizationStep property value.

![Screenshot: Quantizer plugin](./img/Quantizer.jpg "Screenshot: Quantizer plugin")

Quantizer plugin

## Input Port Description

- **input \[double\]:** Input port for the values to be quantized.

## Output Port Description

- **output \[\*\***double\***\*\]:** Output port for the quantized values.

## Properties

- **quantizationStep \[double\]:** The quantization step.
