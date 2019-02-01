# Objective
This tutorial demonstrates how to use the AsTeRICS REST API with the Javascript library provided with AsTeRICS 3.0.

# Introduction

To allow remote communication with the AsTeRICS Runtime Environment (ARE), the ARE REST API was developed. It allows manipulation of resources through a set of HTTP methods such as GET, POST, PUT and DELETE.
Apart from the regular REST functions, an event mechanism through [Server Sent Events (SSE)](https://www.w3schools.com/html/html5_serversentevents.asp) is provided. With this mechanism, ARE can broadcast messages to anyone who subscribes and inform when an event occurs.
The API uses HTTP status codes to declare an error in a call. Specifically, when an error occurs, the response will contain a 500 HTTP status code (Internal Server Error) with an ARE-produced error message inside the HTTP response body.

The base URI for REST operations is:

```http://<hostname>:8081/rest/<restmethod>```

_The default port number is 8081, but can be overridden in the file ```ARE/areProperties```_

In order to get a list of all available REST methods use the REST method ```http://localhost:8081/rest/restfunctions```

There are client libraries facilitating the function calls for [Javascript](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/clientExample/javascript) and [Java](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/JavaLibrary).

# Prerequisites
* [AsTeRICS 3.0 installed and ARE running](https://github.com/asterics/AsTeRICS/releases/tag/v3.0)
* [areCommunicator.js, JSMap.js and  jquery-3.2.1.min.js libraries](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/clientExample/javascript)


# Example 1 - Model start/stop
In this example you will learn how to stop and start the currently deployed ARE model.

1. Start the ARE (ARE.exe|start.bat|start.sh)
2. Copy and paste the following HTML/Javascript snippet into a text editor, save it as ```restapi-start_stop-model.html``` somewhere on your hard drive.
3. Open the file in a browser.

```html
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!-- Include areCommunicator.js library and it's dependencies, you could also include jquery directly from their page. -->
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/areCommunicator.js"></script>
    <script type="text/javascript" src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/jquery-3.2.1.min.js"></script>
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/JSmap.js"></script> 
     
    <title>REST client - Model stop/start</title>
    <script type="text/javascript">
      /* This is an example of how to use the ARE Javascipt framework for the communication
        with the ARE Restful Services.
        
        The location of the server should be defined with the 'setBaseURI(<url>)' method.
        
        A success-callback function and an error-callback function should be passed as an argument
        for every function.
      */
      setBaseURI("http://localhost:8081/rest/");
      
      function START_MODEL() {
        startModel(defaultSuccessCallback, defaultErrorCallback);
      }
      
      //stopModel
      function STOP_MODEL() { 
        stopModel(defaultSuccessCallback, defaultErrorCallback); 
      }
      //Callback functions to be called in case of success or error.
      function defaultSuccessCallback(data, HTTPstatus) { alert("Success message: "+data); }
      function defaultErrorCallback(HTTPstatus, AREerrorMessage) { alert("Error message: "+AREerrorMessage); }
    </script>
  </head>
  <body>
    <div id="content">
      <h1>REST client - Model stop/start</h1>
      <button onclick="START_MODEL()" title="Description: Changes the state of the deployed model to STARTED &#013;Ouput: alert"> Start model </button>
      <br/>      
      <button onclick="STOP_MODEL()" title="Description: Changes the state of the deployed model to STOPPED &#013;Ouput: alert"> Stop model </button>
      <br/>
    </div>
  </body>
</html>
```


# Example 2 - Model upload
This examples shows how a model (XML string) can be uploaded to the ARE.

1. Start the ARE (ARE.exe|start.bat|start.sh)
2. Copy and paste the following HTML/Javascript snippet into a text editor, save it as ```restapi-upload-model.html``` somewhere on your hard drive.

```html
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!-- Include areCommunicator.js library and it's dependencies, you could also include jquery directly from their page. -->
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/areCommunicator.js"></script>
    <script type="text/javascript" src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/jquery-3.2.1.min.js"></script>
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/JSmap.js"></script> 
     
    <title>REST client - Model upload</title>
    <script type="text/javascript">
      /* This is an example of how to use the ARE Javascipt framework for the communication
        with the ARE Restful Services.
        
        The location of the server should be defined with the 'setBaseURI(<url>)' method.
        
        A success-callback function and an error-callback function should be passed as an argument
        for every function.
      */
      setBaseURI("http://localhost:8081/rest/");
      
      //uploadModel
      function UPLOAD_MODEL() {
        var modelInXML = document.getElementById("UMmodel").value;
        uploadModel(defaultSuccessCallback, defaultErrorCallback, modelInXML);
      }
      
      //Callback functions to be called in case of success or error.
      function defaultSuccessCallback(data, HTTPstatus) { alert("Success message: "+data); }
      function defaultErrorCallback(HTTPstatus, AREerrorMessage) { alert("Error message: "+AREerrorMessage); }
    </script>
  </head>
  <body>
    <div id="content">
      <h1>REST client - Model upload</h1>
      
      <button onclick="UPLOAD_MODEL()" title="Description: Deploys the model given as a parameter &#013;Ouput: alert"> Upload model </button>
      <input placeholder="modelInXML" type="text" id="UMmodel"/>
      <br/>
    </div>
  </body>
</html>
```

3. Open the file in a browser.
4. Open the following [model file](https://raw.githubusercontent.com/asterics/AsTeRICS/v3.0/bin/ARE/models/ImageDemo.acs) with a text editor and copy and paste the model xml string into the given field.
5. Click onto ```Upload Model```

# Example 3 - Change plugin property values
This example show how to parametrize a model by overriding default property values of plugins in a model. We use the default autostart model (ARE/models/autostart.acs) of the ARE, which is deployed and started automatically upon startup. The model contains a [TextDisplay plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&actuators/TextDisplay.htm) with id ```TextDisplay.1``` and a [CellBoard plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&sensors/CellBoard.htm) with id ```CellBoard.1```. With the function ```setRuntimeComponentProperties``` you can provide a JSON string with plugin property key/value pairs.

1. Start the ARE (ARE.exe|start.bat|start.sh)
2. Copy and paste the following HTML/Javascript snippet into a text editor, save it as ```restapi-change-property-values.html``` somewhere on your hard drive.

```html
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!-- Include areCommunicator.js library and it's dependencies, you could also include jquery directly from their page. -->
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/areCommunicator.js"></script>
    <script type="text/javascript" src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/jquery-3.2.1.min.js"></script>
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/JSmap.js"></script> 
     
    <title>REST client - Change property values</title>
    <script type="text/javascript">
      /* This is an example of how to use the ARE Javascipt framework for the communication
        with the ARE Restful Services.
        
        The location of the server should be defined with the 'setBaseURI(<url>)' method.
        
        A success-callback function and an error-callback function should be passed as an argument
        for every function.
      */
      setBaseURI("http://localhost:8081/rest/");
      
      function SET_RUNTIME_COMPONENT_PROPERTIES() {
        //The JSON object must be sent as JSON string, the keys and values must be Strings as well.
        var propertyMap=JSON.stringify(
        {
          //Set the default property of the plugin with id TextDisplay.1
          "TextDisplay.1":{
            "default":String(document.getElementById("title").value)
          },
          //Change the cellText1 and cellText2 properties of the plugin with id CellBoard.1
          "CellBoard.1":{
            "cellText1":String(document.getElementById("cellText1").value),
            "cellText2":String(document.getElementById("cellText2").value)
          }
        });      
    
        setRuntimeComponentProperties(
          function (data, HTTPstatus){
            //If the ARE could be reached and the method call was successful, the success callback is called.
            //The variable data contains an array with key/value pairs of properties which could be set successfully.
            //If the length of the array == 0, no property could be set successfully.            
            if(JSON.parse(data).length == 0) {
              var errorMsg="The property settings could not be applied.";
              alert(errorMsg);
            }
            console.log('The following properties could be set: '+data);
          }, 
          defaultErrorCallback, propertyMap);
      }
      
      //Callback functions to be called in case of success or error.
      function defaultSuccessCallback(data, HTTPstatus) { alert("Success message: "+data); }
      function defaultErrorCallback(HTTPstatus, AREerrorMessage) { alert("Error message: "+AREerrorMessage); }
    </script>
  </head>
  <body>
    <div id="content">
      <h1>REST client - Change property values</h1>
      
      <button onclick="SET_RUNTIME_COMPONENT_PROPERTIES()" title=""> Apply Settings </button>
      <input placeholder="Enter Title" type="text" id="title"/>
      <input placeholder="Enter Cell Text 1" type="text" id="cellText1"/>
      <input placeholder="Enter Cell Text 2" type="text" id="cellText2"/>
      <br/>
    </div>
  </body>
</html>
```
3. Open the file in a browser.
4. Change parameter values for title, cellText1 and cellText2 and click onto ```Apply Settings```
5. Not all plugin properties can be changed live, so to ensure that the changes are active stop and start the model.

# Example 4 - Send data to input port
In this example you will learn how to send data to an input port of a plugin. We will use the [Mouse plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&actuators/Mouse.htm) and send absolute coordinates to the input ports (mouseX, mouseY) to change the absolute mouse position.

1. Start the ARE (ARE.exe|start.bat|start.sh)
2. Open the [WebACS](http://asterics.github.io/AsTeRICS/webapps/WebACS/?areBaseURI=http://localhost:8081) and create a model with a [Mouse plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&actuators/Mouse.htm) and an [EditBox plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&sensors/EditBox.htm), where the output port is connected to ```mouseX``` and ```mouseIn``` of the Mouse plugin (see pic below). The EditBox plugin is a workaround for the known [issue #230](https://github.com/asterics/AsTeRICS/issues/230).

![Mouse plugin](developer_guide/api/images/Mouse.JPG)

3. Deploy and start the model
4. Copy and paste the following HTML/Javascript snippet into a text editor, save it as ```restapi-send-data-to-input-port.html``` somewhere on your hard drive.

```html
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!-- Include areCommunicator.js library and it's dependencies, you could also include jquery directly from their page. -->
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/areCommunicator.js"></script>
    <script type="text/javascript" src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/jquery-3.2.1.min.js"></script>
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/JSmap.js"></script> 
     
    <title>REST client - Send Data to Input Ports</title>
    <script type="text/javascript">
      /* This is an example of how to use the ARE Javascipt framework for the communication
        with the ARE Restful Services.
        
        The location of the server should be defined with the 'setBaseURI(<url>)' method.
        
        A success-callback function and an error-callback function should be passed as an argument
        for every function.
      */
      setBaseURI("http://localhost:8081/rest/");
      
      function SEND_DATA_TO_INPUT_PORT() {
        //Fetch values of input fields and send them to the input ports mouseX and mouseY of the Mouse plugin instance with id Mouse.1
        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Mouse.1', 'mouseX', document.getElementById("x-coordinate").value);
        sendDataToInputPort(defaultSuccessCallback, defaultErrorCallback, 'Mouse.1', 'mouseY', document.getElementById("y-coordinate").value);
      }
      
      //Callback functions to be called in case of success or error.
      function defaultSuccessCallback(data, HTTPstatus) { alert("Success message: "+data); }
      function defaultErrorCallback(HTTPstatus, AREerrorMessage) { alert("Error message: "+AREerrorMessage); }
    </script>
  </head>
  <body>
    <div id="content">
      <h1>REST client - Send Data to Input Ports</h1>
      
      <button onclick="SEND_DATA_TO_INPUT_PORT()" title=""> Apply Settings </button>
      <input placeholder="Enter X coordinate" type="text" id="x-coordinate"/>
      <input placeholder="Enter Y coordinate" type="text" id="y-coordinate"/>
      <br/>
    </div>
  </body>
</html>
```

4. Open the file in a browser.
5. Enter values for X and Y coordinate of the Mouse and press ```Apply Settings```.

# Example 5 - Trigger event listener
In this example you will learn how to trigger an event listener of a plugin. The [Mouse plugin]((http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&actuators/Mouse.htm)) has event listener for triggering a mouse click (leftClick, middleClick, rightClick). The example triggers the ```rightClick``` event listener. 
1. Start the ARE (ARE.exe|start.bat|start.sh)
2. Open the [WebACS](http://asterics.github.io/AsTeRICS/webapps/WebACS/?areBaseURI=http://localhost:8081) and create a model with a [Mouse plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&actuators/Mouse.htm).
3. Deploy and start the model
4. Copy and paste the following HTML/Javascript snippet into a text editor, save it as ```restapi-trigger-event-listener.html``` somewhere on your hard drive.

```html
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!-- Include areCommunicator.js library and it's dependencies, you could also include jquery directly from their page. -->
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/areCommunicator.js"></script>
    <script type="text/javascript" src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/jquery-3.2.1.min.js"></script>
    <script src="https://cdn.rawgit.com/asterics/AsTeRICS/v3.0/ARE_RestAPIlibraries/clientExample/javascript/JSmap.js"></script> 
     
    <title>REST client - Trigger event listener</title>
    <script type="text/javascript">
      /* This is an example of how to use the ARE Javascipt framework for the communication
        with the ARE Restful Services.
        
        The location of the server should be defined with the 'setBaseURI(<url>)' method.
        
        A success-callback function and an error-callback function should be passed as an argument
        for every function.
      */
      setBaseURI("http://localhost:8081/rest/");
      
      function TRIGGER_EVENT() {
        triggerEvent(defaultSuccessCallback, defaultErrorCallback, 'Mouse.1', 'rightClick');
      }
      
      //Callback functions to be called in case of success or error.
      function defaultSuccessCallback(data, HTTPstatus) { alert("Success message: "+data); }
      function defaultErrorCallback(HTTPstatus, AREerrorMessage) { alert("Error message: "+AREerrorMessage); }
    </script>
  </head>
  <body>
    <div id="content">
      <h1>REST client - Trigger event listener</h1>
      
      <button onclick="TRIGGER_EVENT()" title=""> Generate right click </button>
      <br/>
    </div>
  </body>
</html>
```

5. Open the file in a browser.
6. Click on the button ```Generate right click```

# References
* [Complete REST API documentation](https://github.com/asterics/AsTeRICS/blob/master/Documentation/REST_API.pdf)
* [REST API demo page](http://asterics.github.io/AsTeRICS/webapps/startpage/index.html#submenuRest)
* [REST API client libraries](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/)
