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
