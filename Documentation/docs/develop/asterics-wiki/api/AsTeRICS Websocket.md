# Objective
In this tutorial you will learn how to use the AsTeRICS websocket functionality with Javascript from within a web client.

# Introduction
A web socket is defined as a two-way communication between the servers and the clients, which mean both the parties communicate and exchange data at the same time. The Websocket protocol is specified in the [RFC6455](https://tools.ietf.org/html/rfc6455) and the corresponding client-side [Websocket API](https://www.w3.org/TR/websockets/) is defined by the W3C.
For more information about web sockets see the following tutorials
* [Mozilla - web sockets tutorial](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications)
* [Tutorialspoint - web sockets tutorial](https://www.tutorialspoint.com/websockets/websockets_overview.htm)

The AsTeRICS Runtime Environment (ARE) provides a websocket at ```ws://localhost:8082/ws/astericsData``` which can be used to send data from a running AsTeRICS model to a web client or vice versa. 

# Example 1 - Web socket echo
Sends a text message to the ARE model and echoes the sent message back to the web client.

## Prerequisites
* [AsTeRICS 3.0 installed and ARE running](https://github.com/asterics/AsTeRICS/releases/tag/v3.0)
* [WebSocket plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&processors/WebSocket.htm)

## Create model with WebSocket plugin

1. Open the [WebACS](http://asterics.github.io/AsTeRICS/webapps/WebACS/?areBaseURI=http://localhost:8081)
2. Add a WebSocket plugin (Components tab, Processors/Web/WebSocket)
3. Connect the output port ```OutA``` to its input port ```InA```: This sends messages received from a websocket client back to the client.
4. Start the ARE (ARE.exe|start.bat|start.sh)
5. Deploy model to ARE by clicking ```Connect to ARE``` and ```Upload Model```
6. Start model by clicking ```Start Model```

![Websocket plugin with output port OutA connected to input port InA](developer_guide/api/images/Websocket-echo-connection.JPG)

## Create web page with web socket client
Using a text editor, copy the following code and save it as websocket.html somewhere on your hard drive. Then simply open it in a browser. The page will automatically connect, send a message, display the response, and close the connection. 

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title>WebSocket Test</title>
    <script language="javascript" type="text/javascript">

    //Set URI of AsTERICS websocket.
    var wsUri = "ws://localhost:8082/ws/astericsData";
    var output;

    /*
      This method is called on page load.
    */
    function init()
    {
    output = document.getElementById("output");
    testWebSocket();
    }

    /*
      Opens a connection to the specified web socket and defines callback functions.
    */
    function testWebSocket()
    {
    //Instantiates and opens  web socket. 
    websocket = new WebSocket(wsUri);
    websocket.onopen = function(evt) { onOpen(evt) };
    websocket.onclose = function(evt) { onClose(evt) };
    websocket.onmessage = function(evt) { onMessage(evt) };
    websocket.onerror = function(evt) { onError(evt) };
    }

    /*
      Called as soon as the web socket was opened successfully.
    */
    function onOpen(evt)
    {
    writeToScreen("CONNECTED");
    doSend("WebSocket rocks");
    }

    /*
      Called as soon as the web socket was closed.
    */
    function onClose(evt)
    {
    writeToScreen("DISCONNECTED");
    }

    /*
      Called in case of a received message from the web socket server.
    */ 
    function onMessage(evt)
    {
    writeToScreen('<span style="color: blue;">RESPONSE: ' + evt.data+'</span>');
    websocket.close();
    }

    /*
      Called in case of an error during connect or send.
    */
    function onError(evt)
    {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
    }

    function doSend(message)
    {
    writeToScreen("SENT: " + message);
    //Actually sends the message to the web socket.
    websocket.send(message);
    }

    function writeToScreen(message)
    {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
    }

    //Register function init to be called on page load.
    window.addEventListener("load", init, false);

    </script>
  </head>
  <body>
    <h2>WebSocket Test</h2>

    <div id="output"></div>
  </body>
</html>
```

## Resulting output
In case of success you should see something like this:
```
WebSocket Test

CONNECTED

SENT: WebSocket rocks

RESPONSE: WebSocket rocks

DISCONNECTED
```

In case of an error check if the ARE is running and the model with the WebSocket plugin is deployed and started.

# Example 2 - Web socket demo with signal data live chart
To try a more advanced web socket demo receiving signal data and visualizing it, please visit this [web socket demo](http://asterics.github.io/AsTeRICS/webapps/startpage/index.html#submenuSolutionDemos:asterics-networkio-websocket) to see how it works. 
You can also clone and edit the corresponding [git repository](https://github.com/asterics/asterics-networkio-websocket).

# References
* [WebSocket plugin](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&processors/WebSocket.htm)
* [Mozilla - web sockets tutorial](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications)
* [Tutorialspoint - web sockets tutorial](https://www.tutorialspoint.com/websockets/websockets_overview.htm)
* [RFC6455](https://tools.ietf.org/html/rfc6455)
* [Websocket API](https://www.w3.org/TR/websockets/)
