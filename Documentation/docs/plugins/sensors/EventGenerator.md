---
title: Event Generator
subcategory: Simulation
---

# {{$frontmatter.title}}

Component Type: Sensor (Subcategory: Simulation) The event generator plugin can be used to periodically send event triggers at a given time interval.

## Requirements

No special hardware or software required

## Port Description

No input or output ports available

## Event Trigger Description

- **event_out_1:** Events will be generated and sent to this port.

## Properties

- **generation_delay \[integer\]:** The time interval for generating events in milliseconds.
- **event_payload \[string\]:** A string value which will be sent with the event as a parameter (currently not used by other plugins).
