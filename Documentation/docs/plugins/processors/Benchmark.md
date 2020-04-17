---
title: Benchmark
---

# Benchmark

Component Type: Processor (Subcategory: Others)

This component may be used to perform benchmark of data throughput at a particular location of the model / design. It counts port activity of data and event ports per given time.

![Screenshot: Benchmark plugin](./img/Benchmark.jpg "Screenshot: Benchmark plugin")

Benchmark plugin

## Input Port Description

- **in \[double\]:** Input port for numeric values. Incoming activity increases the data counter.

## Output Port Description

- **dataCount \[integer\]:** The current value of the data counter.
- **eventCount \[integer\]:** The current value of the event counter.

## Event Listener Description

- **eventIncrease:** Incoming events increase the event counter.
- **resetCounter:** An incoming event resets data counter and event counter to 0.

## Properties

- **time \[integer\]:** The time period in milliseconds for measuring data activity and events. After the time has passed, the current values of data counter and event counter are provided at the output port, and the counters are reset to zero.
