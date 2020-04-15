##

## Sampler

# Sampler

### Component Type: Processor (Subcategory: Basic Math)

This plugin generates a constant rate of sampling for the input port signal.

![Screenshot:
        Sampler plugin](./img/Sampler.jpg "Screenshot: Sampler plugin")  
Sampler plugin

## Input Port Description

- **input \[double\]:** Input port for the signal.

## Output Port Description

- **output \[\*\***double\***\*\]:** Output port for signal with constant rate of the sampling.

## Properties

- **samplingRate \[double\]:** The rate of sampling, which defines the data generation frequency at the output port.
- **responseTime \[integer\]:** Response time in milliseconds. If the time from arrival of the last input data exceeds the response time, the plugin stops sending the output data. If the responseTime is set to 0, it is not used.
- **sendNullSamples \[boolean\]:** If this property is set to true and there is no input data or the response time is exceeded the plugin sends samples equal to zero.
