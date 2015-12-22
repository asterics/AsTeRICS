## AsTeRICS Packaging Environment (APE)

To start APE call

```
java -jar APE.jar [-DAPE.models=<URI to models dirs or files>] [[-Dfx.deploy.nativeBundles=exe|msi|deb|all|none] [-Dfx.application.name=<name of application>] [-Dfx.application.version=<xx.xx.xxx>]]
```

### Example usages

#### One model file, no installer
Create a downstripped ARE package of model file _CameraMouse.acs_. Model file names are first looked up in the ARE/models folder of the AsTeRICS installation, which APE belongs to. By default, the result is written to the subfolder _defaultProjectDir/build/merged_ (The destination directory can be changed with the properties _APE.targetProjectDir_ and _APE.targetBuildDir_).

```
java -jar APE.jar -DAPE.models=CameraMouse.acs
```

#### Several model files/folder, no installer
When specifying a folder all contained model files (recursively) will be used. Several file or folder URIs can be seperated by a _;_. Relative and absolute URIs can be mixed.

```
java -jar APE.jar -DAPE.models=CameraMouse.acs;ImageDemo.acs;eyetracking;D:/MyModelFiles/
```

#### Model file, windows .msi installer
After copying the needed AsTeRICS jars and data files to _APE.targetBuildDir_ a windows .msi installer is created and copied to ```_APE.targetBuildDir_/deploy/bundles```. 
APE uses [JavaFX packaging](http://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#A1324980) which also has prerequisites for the installer type chosen.

**Prerequisites**:
* [Java Development Kit 8] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Ant build system >= 1.9.2] (http://ant.apache.org/bindownload.cgi)
* [WiX >= 3.0 for .msi installer] (http://wixtoolset.org/)

```
java -jar APE.jar -DAPE.models=CameraMouse.acs -Dfx.deploy.nativeBundles=msi -Dfx.application.name=<name of application> -Dfx.application.version=xx.xx.xxx
```



### Changing base URI and properties file
By default the properties in the property file _APE.properties_ at _APE.baseURI_ will be used. Both properties can be customized and set to another path.

* APE.baseURI: URI where APE looks for template data and config files per default. The default _APE.baseURI_ is the location of the APE.jar file.
* APE.propertiesFile: Property file to use instead of default one at _APE.baseURI_

```
[-DAPE.propertiesFile=<URI to APE.properties file>]
```

### Properties for copying/packaging behaviour of APE (file: APE.properties)
Can be overridden with -Dkey=value command line switch). Relative paths are resolved against the location of the properties file.

ARE.baseURI: The base URI of the ARE binaries (can be the binaries of a development snapshot (AsTeRICS/bin/ARE) or an installed version (AsTeRICS/ARE))
APE.targetProjectDir: The target folder for the created project (Copies the template folder to it and then all the relevant files)
APE.targetBuildDir: Default: APE.targetProjectDir/build: Used for building the application. In the subfolder merged the downstripped version of the ARE is located.

APE.clean.targetProjectDir: Cleans the targetProjectDir before copying
APE.overwrite.targetProjectDir: Overwrites the contents of the targetProjectDir

APE.copyDataFiles: [true|false]
APE.copyServices: [true|false]

APE.models

### Properties for deployment (creating installer)
Generelly they represent a subset of [javafx packaging ant-tasks](https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/javafx_ant_task_reference.html)

fx.deploy.nativeBundles

fx.application.name
fx.application.version

fx.info.title
fx.info.vendor
fx.info.description
fx.info.license
fx.info.category

fx.preferences.shortcut
fx.preferences.install
fx.preferences.menu

fx.platform.embedjava
fx.platform.basedir

### Properties set during startup of deployed application (app.properties)
fx.platform.property.<name>
