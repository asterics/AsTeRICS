  

---
title: WebSocket
---

# WebSocket

### Component Type: Processor (Subcategory: Web)

#### This is just a demo plugin and is not yet fully functional.

The websocket component takes an input stream and forwards the data to a websocket ([http://localhost:8082/ws/astericsData](http://localhost:8082/ws/astericsData)). A demo webpage that connects to the websocket and visualizes the data can be accessed at [http://localhost:8082/](http://localhost:8082/).

The websocket plugin can only be used if the the ARE was started with the following command:

**start\_debug.bat --webservice**

## Input Port Description

*   **InA \[double\]:** This port reads the input to be forwarded.
*   **InB \[double\]:** Not yet supported
*   **InC \[double\]:** Not yet supported
*   **InD \[double\]:** Not yet supported
*   **InE \[double\]:** Not yet supported
*   **InF \[double\]:** Not yet supported

## Output Port Description

*   **OutA \[double\]:** Not yet supported
*   **OutB \[double\]:** Not yet supported
*   **OutC \[double\]:** Not yet supported
*   **OutD \[double\]:** Not yet supported
*   **OutE \[double\]:** Not yet supported
*   **OutF \[double\]:** Not yet supported

## Properties

*   **host \[string\]:** Not yet supported
*   **port \[integer\]:** Not yet supported