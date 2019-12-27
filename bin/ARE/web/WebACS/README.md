# WebACS

The WebACS is a platform-independent re-implementation of the AsTeRICS Configuration Suite using HTML5 and Javascript.
For more information on AsTeRICS, please refer to [AsTeRICS on Github](https://github.com/asterics/AsTeRICS) or the AsTeRICS website [http://www.asterics.eu](http://www.asterics.eu).

## Clone

To clone the repo ensure to also clone its submodules by

```git clone --recurse-submodules https://github.com/asterics/WebACS.git```

## Run

As the WebACS is based an Javascript totally, ```index.html``` can be opened as local file in Firefox (35.0.1 or higher).

It is recommended that the WebACS is hosted on a webserver and started via an http-URL, e.g.

```http://localhost:8081/webapps/WebACS/index.html```

In this case also Chrome and other browsers are supported.

### WebACS query string options

When starting the WebACS certain parameters can be specified in the query string of the URL:
* `openFile`: string containing the relative path to the model (must be the same webserver as the one which provides the WebACS) file that shall be opened directly on startup
* ``autoConnect``: boolean – if true: automatically connects to the ARE upon startup: default is false
* ``autoDownloadModel``: boolean – if true: automatically downloads the current model from the ARE; will only work if autoConnect is true and no openFile is specified; default is false
* ``areBaseURI``: string that specifies the URI for the ARE; if not specified, but the WebACS is hosted by an ARE-webservice, the hosting ARE will be used, else localhost will be assumed
* ``helpUrlPath``: Sets the URL to the online help which shall be used if no ARE connection is active. Example: ```helpUrlPath=http://localhost:8081/help/```

#### Examples

* Automatically connect to the specified ARE and download the model from there: \
http://localhost:8081/webapps/WebACS/?autoConnect=true&autoDownloadModel=true&areBaseURI=http://localhost:8081

* Automatically open a specified model file and automatically connect to the hosting ARE: \
http://localhost:8081/webapps/WebACS/?autoConnect=true&openFile=testmodels/test1.acs


## Docs

For further documentation please refer to [documentation](documentation) subfolder.

## Licence

Unless mentioned otherwise, the WebACS is licensed under the Apache License, Version 2.0. You may obtain a copy of the license at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0 "Apache Licence 2.0")
 
This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

## Acknowledgement
This project has received funding from the European Union’s Seventh Framework Programme for research, technological development and demonstration under grant agreement no 610510. Visit [developerspace.gpii.net](http://developerspace.gpii.net/) to find more useful resources.
