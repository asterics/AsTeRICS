# REST demos with Javascript client library

The ARE provides a REST API with several functionalities. The base REST URI is:
```
http://localhost:8081/rest/<restmethod>
```

In order to get a list of all available rest methods use: <StaticLink href="http://localhost:8081/rest/restfunctions" target="_blank" text="http://localhost:8081/rest/restfunctions"/>

## Examples

One way to use the REST API ist to use the Javascript library <a target="_blank" href="./javascript/areCommunicator.js">areCommunicator.js</a>. Use the following links to navigate to examples using this library:

* <StaticLink href="demos/clientExample/are_repository.html" text="ARE Repository"/>:
REST methods to interact with the ARE model repository (list, store, delete).
* <StaticLink href="demos/clientExample/runtime_model_channels.html text="ARE Model Channels"/>:
Interact with data channels and event channels of the ARE model.
* <StaticLink href="demos/clientExample/runtime_model_components.html" text="ARE Model Components"/>:
Interact with components of the current ARE model.
* <StaticLink href="demos/clientExample/runtime_model_deployment.html" text="ARE Model Deployment"/>:
Deploy and upload ARE models.
* <StaticLink href="demos/clientExample/runtime_model_state.html" text="ARE Model State"/>:
Get information and change the state of the current ARE model.
* <StaticLink href="demos/clientExample/sse.html" text="Server Sent Events"/>:
Subscribe to server sent events (SSE) of the current ARE model.
