---
title: DateToDouble
subcategory: Easy Reading
---

# DateToDouble

## Component Type: Processor (Subcategory: Easy Reading)

Converts a date string to a Unix epoch timestamp in milliseconds, using the default time zone and a property-defined date format.

## Input Ports

- **dateString \[string\]**: Date string input to be converted using the date format given by the property.
  The event `formatInput` is also fired internally whenever this input port receives data.

## Output Ports

- **timestampUnixMs \[double\]**: The converted Unix epoch timestamp in milliseconds.

## Event Listeners

- **formatInput**: Initiates conversion for the current input date string, triggers `dateFormatted` when done.

## Event Triggers

- **dateFormatted**: Triggers when an output value is available at the output port.
- **conversionFailed**: Triggers when a conversion attempt failed (might happen if inputs are missing or do not fit the specified date format).

## Properties

- **dateFormat \[string, default: “dd.MM.yyyy-HH:mm:ss.SSS”\]**: The format of the incoming date string.
  It is used for conversion into a UNIX timestamp in milliseconds.
  If invalid (according to `java.text.SimpleDateFormat`) or null, the default date format is tried.
  If conversion fails because of missing or invalid input `conversionFailed` is triggered.
