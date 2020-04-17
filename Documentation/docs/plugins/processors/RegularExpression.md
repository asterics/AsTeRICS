---
title: RegularExpression
---

# Regular Expression

### Component Type: Processor (Subcategory: Event and String Processing)

This component processes strings with regular expressions. It can work in two modes: match strings with the pattern or replace string parts which match the pattern with another string.

![Screenshot: RegularExpression plugin](./img/RegularExpression.jpg "Screenshot:
        RegularExpression plugin")  
RegularExpression plugin

## Input Port Description

- **input \[string\]:** Input port for strings.

## Output Port Description

- **output \[string\]:** The port for strings which match the pattern or which were changed.

## Event Trigger Description

- **match** **:** This event is sent if the string matches the pattern.

- **notMatch** **:** This event is sent if the string doesn't match the pattern.

- **replace** **:** This event is sent if parts of the string where replaced by the replaceString.

- **notReplace** **:** This event is sent if no replacement occurred.

## Properties

- **pattern \[string\]:** Regular expression pattern.
- **replace \[boolean\]:** If the property is set to true, the component will search parts of the string which match the pattern and replace these parts with the replaceString, otherwise the component will match the whole string with the pattern.
- **replaceString \[string\]:** The string which replaces expressions which matching the pattern.
