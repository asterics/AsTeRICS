---
title: StringExtractor
subcategory: Easy Reading
---

# StringExtractor

## Component Type: Processor (Subcategory: Easy Reading)

Extracts a subtext from a given input text and forwards it to the output port as soon as extraction was done based on start and end delimiter strings.

Delimiters must be in the correct order and non-overlapping.
In case of several occurrences, always the first occurrence of the start and the end delimiter are used.
The event trigger also fires during successful extraction, which is also the case for an empty extracted text (also handed to the output port).

## Input Port Description

- **inText \[string\]**: Provides the input text, from which a subtext shall be extracted and sent to the output port, based on start and end delimiter specifications.

## Output Port Description

- **extractedText \[string\]**: Represents the subtext extracted from the input text based on delimiter specifications, i.e. the text between start and end delimiter (can also be an empty string).

## Event Listener Description

None. (Each new `inText` is checked for the existence of start and end delimiter.)

## Event Trigger Description

- **textExtracted**: Triggers when the end delimiter was detected after the start delimiter (not, if the order is wrong or one is missing or overlapping, i.e. within the other) and thus a subtext was extracted and sent to the output port.
  (An empty string is a valid output that also triggers!)

## Properties

- **startDelimiter \[string, default: START\]**: Defines the start string between which and the end string the text is extracted (no regular expression!)
- **endDelimiter \[string, default: END\]**: Defines the end string between which and the start string the text is extracted (no regular expression!)
