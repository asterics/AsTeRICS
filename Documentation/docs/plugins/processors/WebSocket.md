---
title: WebSocket
subcategory: Web
---

# WebSocket

Component Type: Processor (Subcategory: Web)

The websocket component takes an input string message and forwards the data to a websocket ([http://localhost:8082/ws/astericsData][1]). Additionally, incoming messages at the websocket are sent out to the output port. A demo webpage that connects to the websocket and visualizes the data can be accessed at [http://localhost:8082/][2].

The websocket plugin can only be used if the ARE was started with the flag **\--webservice**.

## Input Port Description

- **InA \[string\]:** Incoming messages are sent to the websocket as string.

## Output Port Description

- **OutA \[string\]:** Incoming messages from the websocket are sent to this output port as string.

[1]: http://localhost:8082/ws/astericsData
[2]: http://localhost:8082/
