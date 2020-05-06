---
title: BlinkChangeDetector
subcategory: Easy Reading
---

# BlinkChangeDetector

## Component Type: Processor (Subcategory: Easy Reading)

The plugin detects blinks, based on the eyes’ open and closed states which are indicated by events, which it listens to, and then calculates and outputs blink-related metrics.
This plugin does not detect the eyes’ states itself! Though, in order not to bother the plugin user (the AsTeRICS model developer) with blink detection, it is sufficient to externally fire events of both eyes’ state currently being open or closed (no matter whether the state has changed or stayed the same for a while).
By the plugin listening to the events `eyesAreOrBecomeOpen` and `eyesAreOrBecomeClosed` is also how the model developer can individually specify a temporal granularity for blink metrics output, e.g. by external timers and thresholds, even if the eyes’ state has not changed.

Eyes state: open or closed, always with reference to both eyes simultaneously.

**Basic Terminology for Blink States**: A blink’s start is defined as both eyes’ state having simultaneously changed from open to closed, the blink’s end is indicated by both eyes’ being reopened again.
There is an ongoing (a “current”) blink as soon as a blink’s start is detected.

**Provided Metrics**: The respective “forwarding” event listeners allow recalculating and forwarding blink rate or blink duration to the output ports on demand.
There is a recalculation before every output.

Further, blink rate and blink duration are recalculated and sent to the output ports when the eyes’ state changes, and, if specified via properties, in a certain interval.
If the eyes’ state events are detected but the state has not changed, there is only a recalculation and output when property `OutputIntervalMs` is set and has passed by since the last state-based output.

**Implications**: Note, that the duration changes while the eyes are closed and is set to 0 milliseconds when the eyes’ state changes to closed.
It is longest at the first `eyesAreOrBecomeOpen` event in a row and then set back to 0 milliseconds again till the next `eyesAreOrBecomeClosed` is detected.

In contrast, the rate changes steadily and is unlikely to ever be 0 Hz again, independently from eyes being closed or open.

A blink is included in the rate calculation as soon as it has started (i.e. the eyes changed to closed).

**Trend Detection**: Changes in blink rate and blink duration are analysed at certain eyes’ states respectively state changes.
Then the plugin fires events, if certain thresholds are exceeded respectively undercut.

The detectable trends (rate increase/decrease and duration prolongation/shortening) work with the same basic detection algorithm, described below, but using trend-specific properties (see Properties Section).

**Trend Detection Triggers in detail (also see Event Listeners Section)**: The rate trend is analysed, when a blink starts or the output interval leads to recalculation.
The duration trend is only analysed, when a blink ends (as then the final duration is clear and as there cannot be a duration analysis while no blink is going on, either).
Trend detection is never done when there is a rate or duration recalculation and output due to forwarding events.

## Basic Trend Detection Algorithm

Blink duration trend detection as well as blink rate trend detection allow certain thresholds.
All of them are optional and specify whether only an increasing, only a decreasing, both or no trends are detectable.

Each calculated value not undercutting the lower and not exceeding the upper threshold is "within range".

If the most recently calculated value is passed on to trend detection, three cases are possible:

- The value exceeds the upper threshold which starts the detection algorithm for a currently ongoing increasing trend.
- The value undercuts the lower threshold which starts the detection algorithm for a currently ongoing decreasing trend.
- The value is within range which means an ongoing trend is not interrupted but no new trend can be detected either.

If one of the thresholds is not set, the corresponding case cannot appear but does not influence the others.

If trend detection into one direction is initiated, an opposite trend is always interrupted.

Whenever starting trend detection into one direction, the corresponding internal counter (above upper threshold or below lower threshold) is set to 1, then the buffer of earlier calculated values is walked through backwards, i.e. the more recent values first.
Each value within the range does not affect trend detection, whereas each crossing a threshold in the same direction increases the counter and in the opposite direction interrupts this turn of trend detection.

The algorithm terminates with firing the corresponding trend detection event, as soon as the counter exceeds `howManyDurationOutliers` (for a duration trend) or `howManyRateOutliers` (for a rate trend), respectively.

The algorithm terminates with not firing any detection event, if the start of the buffer was reached or if it was interrupted by a value crossing the opposite threshold.

## Input Port Description

None.

## Output Port Description

- **blinkDurationMs**: Outputs the currently recalculated duration in milliseconds (ms) since blink start, whenever the event `forwardCurrentBlinkDuration` is detected, eyes’ state changed to closed, or `eyesAreOrBecomeOpen` or `eyesAreOrBecomeClosed` are detected and property `OutputIntervalMs` passed by.
  Outputs 0, if currently the eyes are open (no current blink – indicated by the event `eyesAreOrBecomeOpened`).
- **blinkRateHz**: Outputs the current recalculated blink rate (in Hertz) since the plugin’s start, whenever the event `forwardCurrentBlinkRate` is detected, or when `eyesAreOrBecomeOpen` or `eyesAreOrBecomeClosed` are detected and property `OutputIntervalMs` passed by.

It is possible to make the plugin calculate it for a specified observation period, rather than since model start, this can be set by the property `rateObservationPeriodMinutes`.

## Event Listener Description

- **eyesAreOrBecomeClosed**: The event represents the closed-state of the eyes and the listener handles, whether it indicates that a new blink has just started or eyes have not even been opened in the meantime (no previous `eyesAreOrBecomeOpen` since last firing this event).
  This listener is responsible for triggering `blinkStarts`.  
  Considering property `OutputIntervalMs` and the current blink state, it recalculates blink duration, blink rate and initiates trend detection for the blink rate.
- **eyesAreOrBecomeOpen**: The event represents the open-state of the eyes and the listener handles, whether it indicates that a blink has just ended or eyes have not even been closed before (no previous `eyesAreOrBecomeClosed` since last firing this event).
  This listener is responsible for triggering `blinkEnds`.  
  Considering property `OutputIntervalMs` and the current blink state, it recalculates blink duration, blink rate and initiates trend detection for the blink rate and blink duration.
- **forwardCurrentBlinkDuration**: Sends the time that has passed by since blink start to the output port.
  0, if currently not blinking (i.e. if eyes are open).
  It does not do any threshold evaluations (trend detection) or trigger events!
- **forwardCurrentBlinkRate**: Sends the recalculated blink rate, no matter whether there is currently a blink (i.e. eyes closed) going on.
  A possible ongoing blink is already included in the rate calculation.
  It does not do any threshold evaluations or trigger events!

## Event Trigger Description

- **blinkRateIncreased**: Triggers when the detection of an increasing blink rate trend was positive.  
  Also, the properties `rateObservationPeriodMinutes` and `trendsBufferSize` are considered and must therefore be set meaningfully (or be disabled).
- **blinkRateDecreased**: Triggers when the detection of a decreasing blink rate trend was positive.  
  Also, the properties `rateObservationPeriodMinutes` and `trendsBufferSize` are considered and must therefore be set meaningfully (or be disabled).
- **blinkDurationLonger**: Triggers when the detection of an increasing blink duration trend was positive.  
  Also, the property `trendsBufferSize` is considered and must therefore be set meaningfully (or be disabled).
- **blinkDurationShorter**: Triggers when the detection of a decreasing blink duration trend was positive.  
  Also, the property `trendsBufferSize` is considered and must therefore be set meaningfully (or be disabled).
- **blinkStarts**: Triggers when a blink’s start is detected during the handling of the `eyesAreOrBecomeClosed` event.
- **blinkEnds**: Triggers when a blink’s end is detected during the handling of the `eyesAreOrBecomeOpen` event.

## Properties

- **outputIntervalMs \[integer, default: -1\]**: Defines how often the blink rate and duration (also 0, if currently not blinking) are sent to the output ports without the forwarding events being fired.  
  This is only relevant, if the eyes' state has not changed as at blink end or blink start the output ports always receive data.
  This property was introduced in order not to send values too often (e.g. in a 1ms-interval, if the source for eyes state detection such as a camera plugin would fire events that often) but to adapt the outputs for visualization in an oscilloscope etc.
  The unit for the interval is milliseconds (ms).  
  Rate is always recalculated and sent to the output port when a blink starts, the duration, if a blink starts (0 ms) or ends.
  If those outputs, together with output (recalculation and) forwarding events on demand is sufficient, and no continuous output is desired, it can be disabled by setting 0 or a negative value.  
  It is not possible to turn off the outputs, just described for blink starts or ends.  
  ::: tip Note
  Used for blink rate trend and blink duration trend detection.
  :::
- **trendsBufferSize \[integer, default: 10\]**: Defines how many blink rates or blink durations can be looked back for detecting blink rate and blink duration outliers in trend detection.  
  This buffer must not be lower than `howManyRateOutliers` and `howManyDurationOutliers` properties, as then the conditions to fire the corresponding events can never be fulfilled! Value 0 disables the buffer (only meaningful if the needed number of outliers is 1 or disabled).
  Negative values are invalid and the default value is used.
  This must not be confused with `rateObservationPeriodMinutes` which restricts the period for which blinks are considered when calculating the current blink rate.
  ::: tip Note
  Used for blink duration trend detection only.
  :::
- **threshDurationHighMs \[integer, default: 500\]**: Defines the blink duration in milliseconds (ms) above which a blink is detected as an outlier, representing a prolonged blink duration.
  This upper threshold can be disabled by setting it to 0 or a negative value.
- **threshDurationLowMs \[integer, default: -1\]**: Defines the blink duration in milliseconds below which a blink is detected as an outlier, representing a shortened blink duration.
  This lower threshold can be disabled by setting it to 0 or a negative value.
- **howManyDurationOutliers \[integer, default: 3\]**: Defines how many blink duration outliers must occur in one trend direction within the buffered durations for a duration trend to be detected.
  If a trend shall be detected, as soon as there is one outlier, it must be set to 1, duration trend detection can be disabled by setting it to 0 or a negative value.
  The 1-outlier option especially makes sense when no buffer is used.
  ::: tip Note
  Used for blink rate trend detection only.
  :::
- **threshRateLowHz \[double, default: -1\]**: Defines below which rate in Hertz (Hz) a blink rate must be, in order to count towards a decreasing blink rate trend.
  This lower threshold can be disabled by setting it to 0 or a negative value.
- **threshRateHighHz \[double, default: 1\]**: Defines above which rate in Hertz (Hz) a blink rate must be, in order to count towards an increasing blink rate trend.
  This higher threshold can be disabled by setting it to 0 or a negative value.
- **howManyRateOutliers \[integer, default: 3\]**: Defines how many blink rate outliers must occur in one trend direction within the buffered rates for a rate trend to be detected.
  If a trend shall be detected, as soon as there is one outlier, it must be set to 1, rate trend detection can be disabled by setting it to 0 or a negative value.
  The 1-outlier option especially makes sense when no buffer is used.
- **rateObservationPeriodMinutes \[integer, default: 2\]**: Defines the length of the observation period for the blink rate in minutes, e.g. if 3, the number of blinks during the last 3 minutes (sliding window) is considered when calculating the blink rate for this 3-minutes-period.  
  If this property is used, no memory control is done, thus it is recommended not to use a period longer than 1 day (1440 minutes).
  If all blinks and the whole time period since model start (memory control restricts it to several hours though) shall be considered, the observation period can be disabled by setting it to 0 or a negative number.
  Note, that the rate for a natural blinking behavior gets steadily flatter then, as time passes by and might not be representative for trend detection.

## Prospective use in Easy Reading

This plugin already does a little preparatory work for stress detection (in terms of cognitive load analysis) to help the reasoner, later being implemented with AsTeRICS.

It was found that changes in blink rate and blink duration are significant indicators for the demand of cognitive load and thus mental stress level.
The plugin can get information on eyes’ states (open, closed) from different sources, such as a cheap standard webcam via the AsTeRICS [FacetrackerCLM](/plugins/sensors/FacetrackerCLM.html) plugin.
The fact that it only needs the eyes’ state and no previous blink detection or metrics such as timing measurements but does it itself, makes it highly reusable for different eye tracker sources, i.e. sensor and other processor plugins.

The options of providing the calculated metrics at specified output ports on demand but also in regular intervals enables visualization, e.g. with the [Oscilloscope](/plugins/actuators/Oscilloscope.html) plugin, which helps with interpretation.

It will be a main part of the AsTeRICS reasoner to find and utilize suitable threshold rates and durations that indicate cognitive load or stress level and further to combine them.
This research part and the later implementation can be perfectly done by the huge amount of properties, interacting with each other.
