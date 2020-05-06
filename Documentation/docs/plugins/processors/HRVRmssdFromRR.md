---
title: HRVRmssdFromRR
subcategory: Easy Reading
---

*[HRV]: Heart Rate Variability  
*[RMS]: Root Mean Square  
*[RMSSD]: Root Mean Square of Successive Differences  
*[ECG]: Electrocardiogram

# HRVRmssdFromRR

## Component Type: Processor (Subcategory: Easy Reading)

Calculates and outputs the HRV in terms of RMSSD in milliseconds (ms) based on incoming R-R intervals (also in ms).

A sliding window of the x most recent R-R intervals, x being the property `rmssdWindowSize`, is used for the calculation, which is first started as soon as there have been x samples since the last reset (or model start).

Calculation and collection of samples can be paused and continued via event listener ports.
The output port rmssd only delivers a value, if there is an actual calculation (i.e. currently no pause and enough samples).

## Input Port Description

- **rrInterval \[double\]**: This port must deliver the R-R interval in ms, i.e. the time interval between the most significant, the highest, peaks (the R-peaks) of two consecutive QRS' of an ECG.

## Output Port Description

- **rmssd \[double\]**: Provides the calculated RMS of successive differences in ms, calculated from the collected input port `rrInterval’s` signals.

## Event Listener Description

- **resetCalculation**: When the event is detected, the plugin discards previous R-R intervals and resets the counter of intervals used for the calculation.
  This does not influence running or paused states, i.e. if the plugin is currently paused, it will stay paused, otherwise there is a recalculation as soon as there are (again) enough samples.
- **pauseCalculation**: When the event is detected, from now on RMSSD is no longer recalculated and no value is sent to the output port, but R-R values from the input port continue to be stored (more recent ones overwrite existing ones due to the sliding window).
  This can be used in order to wait for more meaningful R-R samples before the next calculation, respectively further outputs.
- **continueCalculation**: The event must be fired after each `pauseCalculation` in order to continue RMSSD calculation and sending the result to the output port again.
- **pauseComponent**: Completely pauses the component's activity, i.e. from now on no RMSSD is recalculated and sent to the output port.
  In addition to a `pauseCalculation`, no more R-R values from the input port are collected, either.
- **continueComponent**: To be used after `pauseComponent` in order to collect R-R values from the input port again (values aren’t reset but progressively overwritten due to the sliding window) and to continue RMSSD calculation and sending the result to the output port.

## Event Trigger Description

- **rmssdRecalculated**: Triggers whenever an RMSSD calculation has been finished and the current RMSSD value is available at the output port, i.e. if `rmssdWindowSize` (property) is 100, it will trigger first when 100 intervals were received and calculation has finished, then after each further interval and calculation completion.

## Properties

- **rmssdWindowSize \[integer, default: 100\]**: The number of R-R intervals that are used for each RMSSD calculation, thus the sliding window size.
  Example: If this is set to 100, the 100 most recent R-R intervals are taken into consideration and calculation is not started before at least 100 values have been received at the input port (since the start or a possible reset).
  A valid value must be > 1, otherwise it is replaced by the default value.

## Prospective use in Easy Reading

RMSSD was found to be a suitable indicator for a user being stressed, in terms of heart rate measurement and its analysis in the time domain.
Thus this is one of the plugins that shall provide pre-processed input and clues for the reasoner implemented with AsTeRICS.
