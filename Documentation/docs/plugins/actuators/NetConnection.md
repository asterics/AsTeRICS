   
---
Net Connection
---

# Net Connection

### Component Type: Actuator (Subcategory: Communication)

This component provides interface to pass the data through the network. It allows to pass data to the another NetConnection component or to the application which uses the NetConnection Native ASAPI library.  

![Screenshot:
        NetConnection plugin](img/NetConnection.jpg "Screenshot: NetConnection plugin")  
NetConnection plugin

## Input Port Description

*   **integerInputPort1...integerInputPort5 \[integer\]:** The integer values which are passed to these ports are sent to the remote receiver.  
    
*   **doubleInputPort1...doubleInputPort5 \[double\]:** The double values which are passed to these ports are sent to the remote receiver.
*   **stringInputPort1...stringInputPort5 \[string\]:** The text values which are passed to these ports are sent to the remote receiver.

## Output Port Description

*   **integerOutputPort1...integerOutputPort5 \[integer\]:** The output ports for the integer values received from the remote sender.  
    
*   **doubleOutputPort1...doubleOutputPort5 \[double\]:** The output ports for the double values received from the remote sender.  
    
*   **stringOutputPort1...stringOutputPort5 \[string\]:** The output ports for the text values received from the remote sender.  
    

## Event Listener Description

*   **inputEvent1...inputEvent10:** The events which are sent to the remote receiver.  
    

## Event Trigger Description

*   **outputEvent1...outputEvent10:** The events received from the remote sender.  
    

## Properties

*   **connectionType \[integer\]:** Describes connection mode: client or server.  
    
*   **IP \[string\]:** The IP address of the remote server.  
    
*   **port \[integer\]:** Port used in IP/TCP connection.
*   **multisession \[boolean\]:** If the plugin is set to work as the server and this property is set, the plugin can connect to more than one client.