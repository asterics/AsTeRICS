# REST demos with Javascript client library

The ARE provides a REST API with several functionalities. The base REST URI is:
```
http://localhost:8081/rest/<restmethod>
```

In order to get a list of all available rest methods use:
<strong><code><a target="_blank" href="http://localhost:8081/rest/restfunctions">http://localhost:8081/rest/restfunctions</a></code></strong>

## Examples

One way to use the REST API ist to use the Javascript library <a target="_blank" href="./javascript/areCommunicator.js">areCommunicator.js</a>. Use the following links to navigate to examples using this library:

* <a href="demos/clientExample/are_repository.html"><strong>ARE Repository</strong></a>:
REST methods to interact with the ARE model repository (list, store, delete).
* <a href="demos/clientExample/runtime_model_channels.html"><strong>ARE Model Channels</strong></a>:
Interact with data channels and event channels of the ARE model.
* <a href="demos/clientExample/runtime_model_components.html"><strong>ARE Model Components</strong></a>:
Interact with components of the current ARE model.
* <a href="demos/clientExample/runtime_model_deployment.html"><strong>ARE Model Deployment</strong></a>:
Deploy and upload ARE models.
* <a href="demos/clientExample/runtime_model_state.html"><strong>ARE Model State</strong></a>:
Get information and change the state of the current ARE model.
* <a href="demos/clientExample/sse.html"><strong>Server Sent Events</strong></a>:
Subscribe to server sent events (SSE) of the current ARE model.
