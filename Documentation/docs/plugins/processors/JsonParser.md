---
title: JsonParser
subcategory: Easy Reading
---

*[JSON]: JavaScript Object Notation

# JsonReader

## Component Type: Processor (Subcategory: Easy Reading)

Receives a JSON string and JSON field name as input and parses it into a
JSON object to send one or more field values (several ones, only if the ``is true and
other `jsonFieldName` input values follow).

## Input Port Description

- **jsonInputString \[string\]**: The JSON-formatted string to be converted into a JSON object.
- **jsonFieldName \[string\]**: The key/name of the JSON object's field, of which the value shall be sent to the output port (most recent name from this port is used, “” causes an error at `readNextField` event).
- ** \[oolean, default: true\]**: Optional.
  If connected, it overwrites the `keepParsedObject` property.

## Output Port Description

- **jsonFieldValue \[string\]**: The value of the JSON field, identified by `jsonFieldName`, converted to a string (also `""` is valid; an explicit `null` is converted to `"null"`).
  In case of failure, it will be `""` and the `error` port provides details.
- **latestReadFieldName \[string\]**: The name of the field whenever an attempt of reading a field value (from the parsed JSON object) has been completed.
  This output can only be `""` if the field name was `""|null|unset`, in which case the `error` output port provides details.
- **error \[string\]**: Receives a value (different from `""`) whenever an attempt of input parsing or reading the next field fails.
  No matter whether it is because of a missing JSON object (as needed for reading a field but not previously parsed) or missing or invalid input port values.
  It receives `""` (= no error) at the next successful parse or read.
  Thus it can be used for showing error messages.

## Event Listener Description

For details on which errors are handled and error messages are provided for, see description of the `error` output port and of the event triggers that cause an `error` port output.

- **parseInput**: Parses the most recently received value from the input port `jsonInputString` into a JSON object - from which later the desired JSON field (identified by the `jsonFieldName` input) value is read and sent to the `jsonFieldValue` output port.
- **readNextField**: Triggers the next attempt of reading the value of the currently desired JSON field (identified by `jsonFieldName`) from an already parsed JSON object.  
  This event must be used after `parseInput` (which must be repeated if `keepParsedObject` is set to false).
  `parseInput`, in combination with a well-matched `keepParsedObject` property/port, has to assure that there is already a JSON object from which values can be read when firing `readNextField`.
- **rejectParsedObject**: Unsets the parsed JSON object for the case that `keepParsedObject` is true (via property or overwritten by input port value) or if it is false but no successful reading of any JSON field has happened (see property description).

## Event Trigger Description

- **inputParsed**: Triggers to indicate that the plugin is done with parsing the JSON input (string) into a JSON object.
- **fieldValueAvailable**: Triggers when the value of the desired JSON field (specified by the input `jsonFieldName` and now identified by the output `latestReadFieldName`) is available at the `jsonFieldValue` output port.
  ::: tip Note
  Event Triggers that cause an `error` port output
  :::
- **fieldValueNotFound**: Triggers when trying to read a field but when there is no field with the name given at the input port within the parsed JSON object (string).
- **fieldValueInvalidFormat**: Triggers when trying to read a field but when there has been an error returning the value of the desired JSON field.
  The value has a wrong/an invalid format but is found (i.e. the key is existent).
  This is the case whenever a found value cannot be parsed to a string (which is the output format at the output port `jsonFieldValue`).
- **invalidInputFormat**: Triggers when trying to parse the JSON input string but when it cannot be parsed into a JSON object (to later read values from).
- **missingJsonInput**: When trying to parse the JSON input string but when there has been no value at the input port `jsonInputString`.
- **missingFieldName**: Triggers when trying to read from an already parsed JSON object but when there has been no value at the input port `jsonFieldName`.

## Properties

- **keepParsedObject \[boolean, default: true\]**: Specifies, whether to keep the parsed JSON object (the one resulting from the latest parsing of `jsonInputString`) until the next value appears at the `jsonInputString` port – to read further fields in the meanwhile.

_Usage Note_: If a field reading attempt fails, a JSON object that has potentially been stored will not be unset, independently from the value of this property.
This is to assure that each JSON input delivers at least one field value or otherwise is replaced by the next JSON input.
In order to force the parsed JSON object's rejection, `rejectParsedObject` must be triggered.
