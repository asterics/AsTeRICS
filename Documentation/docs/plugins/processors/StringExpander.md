---
title: StringExpander
---

# String Expander

### Component Type: Processor (Subcategory: Event and String Processing)

Adds the preString and postString strings to the incoming string and sends the new string to the output port.

![Screenshot: StringExpander plugin](./img/StringExpander.jpg "Screenshot: StringExpander plugin")  
StringExpander plugin

## Input Port Description

- **input \[string\]:** String input port.
- **preString \[string\]:** String which will be placed before the input string (as leading string).
- **postString \[string\]:** String which will be placed after the input string (as trailing string).

## Output Port Description

- **output \[string\]:** String output port.

## Properties

- **preString \[string\]:** default leading String.

- **postString \[string\]:** default trailing String.

- **trim \[boolean\]:** if selected, all leading and trailing white-space characters will be removed from the input string.
