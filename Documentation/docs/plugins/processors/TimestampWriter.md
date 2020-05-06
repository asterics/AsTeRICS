---
title: TimestampWriter
subcategory: Easy Reading
---

# TimestampWriter

## Component Type: Processor (Subcategory: Easy Reading)

This plugin stores the time in milliseconds (ms) that passed by since the last reset (or start).
Outputs are the (via date string) formatted and numerical milliseconds and also a formatted absolute UNIX epoch timestamp plus the absolute UNIX timestamp in ms.
The time zone of the local system is used!

## Input Port Description

None. Milliseconds are either absolute Unix epoch timestamps or relative intervals between events.

## Output Port Description

- **timePassedMs \[double\]**: Milliseconds (ms) since model start or last component reset.
- **timePassedFormatted \[string\]**: Milliseconds since model start or last component reset - formatted as date string.
- **timestampUnixMs \[double\]**: Unix timestamp in milliseconds (ms).
- **timestampUnixFormatted \[string\]**: Unix timestamp – absolute milliseconds formatted as date string.

## Event Listener Description

- **resetStartTimestamp**: Resets the start timestamp for calculating the time passed by (the first timestamp is set at model start).
- **sendOutputs**: Formats and sends outputs, triggers the event `timestampFormatted` when outputs are available at the ports.

## Event Trigger Description

- **timestampFormatted**: Triggers when output values (formatted and as milliseconds) are available at the output ports.

## Properties

- **timestampFormat \[string, default: “dd.MM.yyyy-HH:mm:ss.SSS”\]**: Format that must be valid according to `java.text.SimpleDateFormat`.
  It is used for formatting the UNIX timestamp as a date string.
  If it is invalid or null, the default date format is used.
- **diffTimeFormat \[string, default: “HH:mm:ss.SSS”\]**: Format that must be valid according to `java.text.SimpleDateFormat` but for meaningfulness with only time components.
  It is used for formatting passed milliseconds (since start/reset) as a date string.
  If invalid or null, the default date format is used.

_Important Usage Note_: Only the format for time components shall be set, as otherwise the Unix epoch start date components (01.01.1970 at 00:00:00.000 o’clock) are added.
E.g. if 5 seconds have passed by, using date components the output would look like 01.01.1970-00:00:05.000!

## Prospective use in Easy Reading

The plugin is needed for aligning data from different tracking sources (heart rate tracker, eye tracker or webcam) to each other (i.e., “synchronizing” them).
This is necessary as most of the sensor or communication plugins only provide raw data without a temporal reference, which the later implemented (AsTeRICS) reasoner could likely not make use of, once data was just sequentially written into text or csv files.

Usefulness and recoverability of earlier tracked data from user tests (during the early research phases on generally useful metrics) for later phases such as reasoner implementation would not be given without also storing the timestamps.

While mathematical methods and visualizations are likely to demand numerical milliseconds-based timestamps (maybe even relative ones, with regard to the model start), readability of text-based files will improve when (formatted) absolute date and time are printed.
