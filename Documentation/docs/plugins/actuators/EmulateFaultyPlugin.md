 

---
title: EmulateFaultyPlugin
---

# EmulateFaultyPlugin

Component Type: Actuator (Subcategory: Test)

The component emulates a faulty plugin - which is a plugin that throws unexpected exceptions during start/pause/stop methods, or a plugin that has very long lasting method calls or even method calls hanging forever. The plugin is used to test the stability of the ARE in error situations. It supports the configuration of the duration of a method call including an endless method call.

## Requirements

No special hardware or software required.

## Input Port Description

*   **inA \[double\]:** Input data of type double.
*   **inB \[double\]:** Input data of type double.
*   **inC \[string\]:** Input data of type string.
*   **inA \[integer\]:** Input data of type integer.

## Event Listener Description

*   **eventA:** An incmonig event A.
*   **eventB:** An incoming event B.
*   **eventC:** An incoming event C.

## Properties

*   **startException \[boolean\]:** Throw an exception when the plugin start method is called.
*   **pauseException \[boolean\]:** Throw an exception when the plugin pause method is called.
*   **stopException \[boolean\]:** Throw an exception when the plugin stop method is called.
*   **resumeException \[boolean\]:** Throw an exception when the plugin resume method is called.
*   **startDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **pauseDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **resumeDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **stopDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **inADuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **inBDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **inCDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **inDDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **eventADuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **eventBDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.
*   **eventCDuration \[integer\]:** The duration in ms of the method call. If -1 is specified the method hangs forever and produces a thread dead lock.