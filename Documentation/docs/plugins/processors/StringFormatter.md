---
title: StringFormatter
---

# StringFormatter

Component Type: Processor (Subcategory: Event and String Processing)

The StringFormatter plugin can be used to create a printf-style formatted text with numbers or strings as input variables. The output string is formatted according to a given format string, see [Java class Formatter](https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html).

## Input Port Description

- **in1String \[string\]:** Input variable of type string, referenced with %1\$ in the format string.
- **in2String \[string\]:** Input variable of type string, referenced with %2\$ in the format string.
- **in3Double \[string\]:** Input variable of type double, referenced with %3\$ in the format string.
- **in4Integer \[string\]:** Input variable of type integer, referenced with %4\$ in the format string.
- **setFormatStr \[string\]:** Sets a new format string.

## Output Port Description

- **formattedStr \[string\]:** Formatted string according to input variables and format string.

## Properties

- **formatString \[string\]:** The format string to use, see Java class Formatter. The input ports in1XXX-in4XXX represent the formatable variables and can be referenced with the %n\$ syntax.
- **defaultIn1String \[string\]:** The default value of in1String. Used if there is no input port value.
- **defaultIn2String \[string\]:** The default value of in2String. Used if there is no input port value.
- **defaultIn3Double \[double\]:** The default value of in3Double. Used if there is no input port value.
- **defaultIn4Integer \[integer\]:** The default value of in4Integer. Used if there is no input port value.
- **sendOnlyByEvent \[boolean\]:** Only sends the value of the formatted string, if the event sendFormattedStr is received.
