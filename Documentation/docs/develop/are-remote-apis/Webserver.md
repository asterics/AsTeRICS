# ARE Webserver

The ARE provides a webserver with the document root at 
```
ARE/web/
``` 

If you start the ARE, you can open the start page of the webserver by

```
http://localhost:8081/
```

or

```
https://localhost:8083/
```

## Document root subpath definitions

* **/ or /index.html**: Landing page of the webserver. This URL provides some general informations about AsTeRICS and should link to the WebACS, REST demos and model demos. Can be overridden in case of an APE project. (*readonly*)
* **rest/**: This is the base path of the [REST interface](./REST-API.md).
(*read, write*)
* **ws/**: This is the base path of the websocket functionality. *(read, write)*
* **data/**: Reserved for providing access to the [ARE/data](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/data/) folder containing model data files, plugin config files, plugin images,... (*read, write*)
* **models/**: Reserved for providing access to [ARE/models](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/models/). (*read, write*)
* **modelSettings/**: Reserved for static or dynamically generated settings dialogs of a model targeted for end users (users with disabilities or care takers, personal assistents, occupational therapists,...) (*readonly*)
* **componentCollections/**: Reserved for providing various component collections describing the set of plugins available on an ARE installation. (*readonly*)
* **webapps**/: Reserved for web applications. Could be used to extend web functionality by (optional) web applications. (*read, write*)
* **WebACS/**: Base URL of the webbased AsTeRICS Configuration Suite [(WebACS)](https://github.com/asterics/WebACS) including help of WebACS **without plugin help files**. (*read, write*)

## Physical folder structure

The virtual paths are represented by the following physical folder structure on the ARE:

```
ARE
 |-data/
 |-models/
 |-web/
  |index.html
  |-modelSettings/
  |-componentCollections/
  |-WebACS/
  |-webapps/
```

The subpaths _rest/_ and _ws/_ are only virtual paths needed for the REST API and websocket functionalities.
