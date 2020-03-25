  
---
SignalGenerator
---

# Signal Generator

### Component Type: Sensor (Subcategory: Simulation)

The SignalGenerator component can generate several output waveforms for component tests or other purposes like timing or event generation. Available waveforms are sine, sawtooth, rectangle and random signal data.

![Screenshot: SignalGenerator plugin](img/SignalGenerator.jpg "Screenshot: SignalGenerator plugin")  
SignalGenerator plugin

## Output Port Description

*   **out \[double\]:** The generated waveform is provided at this port.

## Properties

*   **sendInterval \[integer\]:** This value specifies the output rate in milliseconds. Please note that the output rate has to be fast enough to assemble the selected output frequency. For example if a frequency of 2 Hz is set, the send\_interval should not be greater than 125 milliseconds according to the sample theorem.
*   **waveForm \[integer\]:** The waveform types random, sine, sawtooth and rectangle can be selected.
*   **frequency \[double\]:** The frequency of the output signal in Hertz.
*   **amplitude \[double\]:** The amplitude of the output signal.
*   **phaseShift \[double\]:** The output signal is phase-shifted by this value (in milliseconds).
*   **offset \[double\]:** Amplitude-offset of the output signal (this value is added to each generated waveform value).