# Objective
In this tutorial you will learn how to use the [AsTeRICS Solution Template Repository](https://github.com/asterics/asterics-solution-template) to create your own AT-related software project.

# Introduction
The AsTeRICS Packaging Environment (APE) provides the possibility to maintain an AsTeRICS solution as a dedicated software project, that can be hosted anywhere and also versioned as a git repository. 
As an AsTeRICS solution not always consists of just one model file but also of config files, images, or web applications, APE provides a template repository, which acts as a starting point for a solution. The repository can be downloaded, cloned or forked as any other git repository. There are also other [AsTeRICS solution examples](https://github.com/asterics?utf8=%E2%9C%93&q=topic%3Aexample&type=&language=) derived from that repository.

Before starting the tutorial, have a look at the [folder structure](https://github.com/asterics/asterics-solution-template/blob/master/README.md#folder-structure) of the template repository.

# Prerequisites
You need at least
1. [Java Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). 
  * Ensure to set ```JAVA_HOME``` to the folder where you installed the Java JDK and add the JDK bin path to the  Environment Variable ```Path```
2. [apache ant build framework (version >= 1.9.1)](http://ant.apache.org/bindownload.cgi)
  * Ensure to set ```ANT_HOME``` to the folder where you installed ant and add the ant bin path to the Environment Variable ```Path```
3. [AsTeRICS 3.0.1](https://github.com/asterics/AsTeRICS/releases/download/v3.0/Setup_AsTeRICS_3_0_1.exe) installed **or** a snapshot of [AsTeRICS](https://github.com/asterics/AsTeRICS) cloned to a parallel folder of the project.
4. A copy of the template repository (see [Download / Clone template repository](#download--clone-template-repository)

You can use an IDE for Java and Web Developers, e.g. [Eclipse for Java EE developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/oxygen3a). See [Eclipse installation and setup](#eclipse-installation-and-setup) for details.

# Download / Clone template repository
You can either clone the [template repository](https://github.com/asterics/asterics-solution-template/) or download it as a zipped file.

You might also want to start based on an existing [AsTeRICS solution example](https://github.com/asterics?utf8=%E2%9C%93&q=topic%3Aexample&type=&language=) by downloading or forking it, if your application is similar to one of them.

# Example 1 - Run project
The template repository already contains a default autostart model at ```custom/bin/ARE/models/autostart.acs```, which opens the ARE webserver startpage with the system default browser.

```cmd
ant run
```

For subsequent runs use the targets ```APE-copy``` and  ```run-quick``` to speed up the starting.

If you want use ```Eclipse```, please read the [Eclipse installation and setup](#eclipse-installation-and-setup) instructions.

# Example 2 - Edit and test model
To edit and test the modifications immediately 

1. Run project  (see [Example 1](#example-1---run-project))
3. Double click onto model file. The ACS program should be started with the model file opened.
4. ```Connect to ARE```
5. Modify model
 1. Click onto ```TextArea.1``` and set ```caption``` to ```Hello World```
6. Upload and run model
7. To permanently save the modification, you **must save the file with the ACS** again, otherwise the modifications will be lost with the next run of the project.

# AsTeRICS model + web application
In many cases you want to provide a better user interface or allow the user to parametrize your asterics solution without the need to edit the model in the ACS program. As the end user or relatives of end users might not be very technically skilled, you should provide easy configuration pages with high usability. This can be very easily achieved with web technologies.

The [AsTeRICS webserver document root specification](https://github.com/asterics/AsTeRICS/wiki/AsTeRICS-webserver-document-root-specification) defines the folder structure and paths on the webserver.
The document root is set to the ```ARE/web``` sub folder and the default start page is expected at ```ARE/web/webapps/startpage```. 

The default start page will be opened, if you start the ARE and open the URL ```http://localhost:8081/```. The file ```ARE/web/webapps/startpage/index.html``` contains the top and side menus and opens ```ARE/web/webapps/startpage/start.html``` as default iframe in the contens area. 

You can override these pages by simply providing your own versions in the ```custom/bin/ARE/web/webapps/startpage/``` folder of your project repository.
It is recommended to provide the actual web application in a parallel folder (```ARE/web/webapps/<asterics-solution-name>```) named as your project folder and redirect from within ```start.html``` to ```../<asterics-solution-name>/index.html```.

## Example 3 - Model parametrization through web page
As mentioned above, a model should be made configurable in an intuitive way, not forcing the user to edit the model in the ACS program. To achieve this you can use HTML input widgets and bind them to associated model properties. This way the value between both will be automatically synchronized during page load and model start.

The folder ```ARE/web/webapps/<asterics-solution-name>/``` should be used to store all web application specific files like .html files and associated model files. This way the web application folder can also be put onto a webserver online and the web application be deployed to a running ARE instance from there. 

1. Save this [ButtonGrid model file](https://raw.githubusercontent.com/asterics/AsTeRICS/master/bin/ARE/models/componentTests/sensors/ButtonGrid_test.acs) to ```ARE/web/webapps/<asterics-solution-name>/models/```.
2. Edit ```ARE/web/webapps/<asterics-solution-name>/index.html``` and add/edit the following lines in the ```head``` section of the file:

```html
<!-- provided from AsTeRICS 3.0 -->
<script type="text/javascript" src="../startpage/lib/jquery-3.2.1.min.js"></script>
<script src="../startpage/clientExample/javascript/JSmap.js"></script>
<script src="../startpage/clientExample/javascript/areCommunicator.js"></script>
<!-- provided by this repository, should be part of the framework later -->
<script src="../startpage/lib/webAppUtils.js"></script>
<script src="../startpage/lib/modelManipulation.js"></script>

<script type="text/javascript">
	//Set the base URI of the running ARE instance.     
	//You could also make this configurable by the user.
	setBaseURI("http://localhost:8081/rest/");

	//Define path of model file on the webserver.
	var modelFilePathOnWebserver='webapps/asterics-solution-template/models/ButtonGrid_test.acs';
	//Init window.onload function to automatically update all widgets with a model binding with the values of the currently deployed model.
	window.onload=updateWidgetsFromDeployedModel;		
</script>
```
You must include ```jquery``` as the only third party dependency, then ```JSmap.js``` and ```areCommunicator.js``` [(the REST API lib)](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/clientExample/javascript). Additionally, you need ```webAppUtils.js``` and ```modelManipulation.js``` ([see API documentation](http://asterics.github.io/AsTeRICS/webapps/startpage/doc/lib-js-api/index.html)) for the automatic model binding and synchronization functionality.

In the script section you only need 3 lines for initialization. The first one (```setAREBaseURI```) defines the address of the ARE. The second one defines the path to the model file which should be deployed when clicking onto the ```Start model``` button. Finally, the third line sets the ```window.onload``` function to a function which automatically loads all property values which are defined with a model binding and updates the corresponding HTML widgets automatically.

3. Add two input widgets to parametrize the text of button1 and the background color of the Buttongrid:
```html
<!-- provide your input widgets here -->
<h3>ButtonGrid parametrization</h3>
<label for="background-color">Select background color: </label>
<select id="background-color" data-asterics-model-binding-1='{"componentKey": "ButtonGrid.1","propertyKey": "backgroundColor"}'>
  <option value="0">black</option>
  <option selected="selected" value="1">blue</option>
  <option value="2">cyan</option>
  <option value="3">darkgrey</option>
</select>
<p>
<label for="button1-text">Set text of button 1: </label>
<input title="Button1 text" type="text" id="button1-text" placeholder="Text Button1" value="Hello World" data-asterics-model-binding-1='{"componentKey": "ButtonGrid.1","propertyKey": "buttonCaption1"}'>
```
Note the **data-asterics-model-binding-1** attribute, it defines the binding to the plugin property of the corresponding model. So the combobox with id ```background-color``` will change the background color of the plugin instance ```ButtonGrid.1```.

Finally, there is a ```Start Model``` button which calls the javascript function ```applySettingsInXMLModelAndStart(modelFilePathOnWebserver)``` with a variable as parameter holding the path to the model file on the webserver hosting this page. This automatically downloads the model file from the webserver, update the plugin properties within the model file with the current values of the HTML widgets and deploys and starts the modified model.

```html
<button onclick="applySettingsInXMLModelAndStart(modelFilePathOnWebserver)" title="Description: Applies all settings and starts the model" class="button"> Start Model </button>
```


## Example 4 - Rename web application folder and title
The template repository already contains a template folder for a web application. You should rename it and change the redirection to the new folder.

1. Rename template folder (```ARE/web/webapps/asterics-solution-template```  ) to the name of your project folder.
2. Open ```ARE/web/webapps/startpage/start.html``` and change path to redirection to the name of your project folder.
```html
	<meta charset='utf-8' http-equiv="refresh" content="0; URL=../<asterics-solution-name>/"/>
```
3. Edit ```ARE/web/webapps/<asterics-solution-name>/index.html``` and change the title and first heading (h1) to
```html
    <title>AsTeRICS Solution Hello World</title>
    ...
    <header>
      <div class="inner">
        <h1>AsTeRICS Solution Hello World</h1>
      </div>
    </header>
``` 
4. Save files and run project

# Eclipse installation and setup
As APE uses the ant build framework, the repository can also be used with your preferred IDE. Subsequently some recommendations of how to install and setup Eclipse for an asterics solution repository.

1. Install [Eclipse for Java EE developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/oxygen3a) (Select **same bitness** as your JDK!! (x86 <-> x86 or x86_64 <-> x86_64))

## Eclipse project setup

1. Click on ```File/New/Project```
2. Then click on ```Next```, uncheck ```Use default location``` and browse to your asterics solution folder.
3. Set a ```Project name```
4. Click onto ```Finish```

Now you have an eclipse project with ant support.
## Configure editor for model files (.acs)
1. Right click on model file, select ```Open with/Other```
2. Check ```External programs``` and browse to ```C:\Program Files (x86)\AsTeRICS\ACS\ACS.bat``` (Note: **ACS.bat**)
3. Check ```Use it for all `*.acs file```
4. Click onto ```Ok```

## Using ant within eclipse

### Run default target 
This is synonymous to ```ant``` on the command line:
1. Right click onto file ```build.xml```
2. Select ```Run As/Ant Build``` (first entry)

### Run selected targets
1. Right click onto file ```build.xml```
2. Select ```Run As/Ant Build``` (second entry)
3. Click onto targets to run
4. Select order of execution at the bottom
5. Click onto ```Apply``` and ```Run```

From now on, the selected targets are the default ones for subsequent calls.

# References
* [AsTeRICS Solution Template Repository](https://github.com/asterics/asterics-solution-template)
* [AsTeRICS solution examples](https://github.com/asterics?utf8=%E2%9C%93&q=topic%3Aexample&type=&language=)
* [webAppUtils.js and modelManipulation.js API documentation](http://asterics.github.io/AsTeRICS/webapps/startpage/doc/lib-js-api/index.html)
* [the REST API lib](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE_RestAPIlibraries/clientExample/javascript)
* [HTML, CSS, Javascript documentation](https://www.w3schools.com)