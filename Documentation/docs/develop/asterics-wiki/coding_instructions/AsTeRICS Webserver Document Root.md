**Note:** Will be implemented with https://github.com/asterics/AsTeRICS/issues/163

# Specification of the AsTeRICS webserver document root

This specification defines the paths/subpaths of the webserver document root, which was previously located at the [ARE/data/webservice/](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/data/webservice) subfolder of an AsTeRICS installation.

The new location of the document root will be at ```ARE/web```.

The goal is to prevent current or future namespace collisions between AsTeRICS web functionalities. Not all of the documented subpaths are already implemented.

## Security considerations
Currently the AsTeRICS web functionality does not provide authentication and authorization functionality. There is no support for SSL encryption through https either. These requirements are planned for future releases. The parentheses in the [Subpath definitions](https://github.com/asterics/AsTeRICS/wiki/AsTeRICS-webserver-document-root-specification/#subpath-definitions) describe permission requirements for the respective path.

## Virtual Subpath definitions

* **/ or /index.html**: Landing page of the webserver. This URL provides some general informations about AsTeRICS and should link to the WebACS, REST demos and model demos. Can be overridden in case of an APE project. (*readonly*)
* **rest/**: This is the base path of the [REST interface](https://github.com/asterics/AsTeRICS/blob/master/Documentation/REST_API.pdf).
(*read, write*)
* **ws/**: This is the base path of the websocket functionality. *(read, write)*
* **data/**: Reserved for providing access to the [ARE/data](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/data/) folder containing model data files, plugin config files, plugin images,... (*read, write*)
* **models/**: Reserved for providing access to [ARE/models](https://github.com/asterics/AsTeRICS/tree/master/bin/ARE/models/). (*read, write*)
* **modelSettings/**: Reserved for static or dynamically generated settings dialogs of a model targeted for end users (users with disabilities or care takers, personal assistents, occupational therapists,...) (*readonly*)
* **help/**: Base URL of the help system. Only the generic help system without contents. The content is provided through the parallel path ```../help_files/``` (*readonly*)
* **help/help_files/**: Definition of content paths with files ```helpPaths-hosted.json``` and ```helpPaths-local.json``` and ARE, C#-ACS and Plugin help files, e.g. Plugins/actuators/FS20Sender.htm or ARE/Introduction.htm, see [#45](https://github.com/asterics/AsTeRICS/issues/45)
* **componentCollections/**: Reserved for providing various component collections describing the set of plugins available on an ARE installation. (*readonly*)
* **webapps**/: Reserved for web applications. Could be used to extend web functionality by (optional) web applications. (*read, write*)
* **webapps/WebACS/**: Base URL of the webbased AsTeRICS Configuration Suite [(WebACS)](https://github.com/asterics/WebACS) including help of WebACS **without plugin help files**. (*read, write*)
* **webapps/WebACS/help/help_files/**: Definition of content paths with files ```helpPaths-hosted.json``` and ```helpPaths-local.json``` and ARE, WebACS and Plugin help files, e.g. Plugins/actuators/FS20Sender.htm or ARE/Introduction.htm, see [#45](https://github.com/asterics/AsTeRICS/issues/45)
* **webapps/AsTeRICS-Ergo**/: Reserved for the [AsTeRICS Ergo](https://github.com/asterics/AsTeRICS-Ergo) wizard based web application. (*read, write*)
* **webapps/CCCSD**/: Reserved for the Clinician/Consumer Custom Solution Development environment [(CCCSD)](https://github.com/asterics/CCCSD) (*read, write*)

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
  |-help/
  |-help_files/
    |-helpPaths-hosted.json
    |-helpPaths-local.json
  |-webapps/
   |-WebACS/
   |-AsTeRICS-Ergo/
```
The subpaths _rest/_ and _ws/_ are only virtual paths needed for the REST API and websocket functionalities.