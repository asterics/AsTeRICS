# AsTeRICS Packaging Environment (APE)

The APE let's you select a set of AsTeRICS model files and create a downstripped (minimum size) version of the ARE including plugins, configuration files and data files to execute the models. Optionally, the APE allows the creation of native installers for Windows and Linux using [JavaFX packaging technology](http://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#BCGIBBCI).

APE consists of two major elements
* **APE-copy**: a commandine tool to create a downstripped version of the ARE based on provided model files
* **Build infrastructure for native installers**: a template project directory including an [ant](https://ant.apache.org/) build file and a property file (```APE.properties```) to easily configure the installer creation with JavaFX.

## APE-copy

To start APE-copy, call

```
APE-copy[.bat|.sh] -DAPE.models=<; seperated paths to model files or folder> [[-DAPE.buildDir=<Path to output folder>] [-DARE.baseURI=<Path to ARE installation>] [-DAPE.projectDir=<Path of project folder to use] [-DAPE.logLevel=[FINE|INFO|WARNING|SEVERE]]
```
Relative paths are resolved against the current working directoy (CWD).
* **APE.models**: Provide a semicolon (;) seperated list of model files or folder. Relative and absolute paths can be mixed where relative paths are resolved against the CWD.
* **APE.buildDir**: The path to the build (output) folder. The downsized ARE is copied to that folder. The path can be relative or absolute where a relative path is resolved against the CWD.
* **ARE.baseURI**: The path to the ARE installation. This can be the path to the ```bin/ARE``` folder of a development version (cloned git repository) or the ```ARE``` path of an installed AsTeRICS release (>= 2.8). The path can be relative or absolute where a relative path is resolved against the CWD.

##### APE.baseURI, APE.projectDir and APE.buildDir
The term _APE.baseURI_ refers to the location of the APE-copy command, which is in the folder ```APE``` parallel to the ```ARE``` folder of an AsTeRICS installation. The _APE.baseURI_ folder also contains the build infrastructure to create the native installers and contains the default project directory (```APE.projectDir=<APE.baseURI>/defProjectDir```) and the default build directory (```APE.buildDir=<APE.baseURI>/defProjectDir/build```).

### Example usages of APE-copy

You should be able to copy/paste the example commands below as long as you replace the placeholder with real values. The examples use windows path notations. 

**Note for Linux**: On Linux you must use ```APE-copy.sh``` and slashify ('/' instead of '\') the paths.

#### One model file
Create a downstripped ARE package of the model file ```ImageDemo.acs``` located in the ```ARE/models``` folder.
By default, the result is written to the folder ```<APE.buildDir>/merged```.

```
cd <ARE.baseURI>
..\APE\APE-copy -DAPE.models=models/ImageDemo.acs
```

#### Several model files/folder
When specifying a folder, all contained model files (recursively) will be used. Several file or folder URIs can be seperated by a '**;**'. Relative and absolute URIs can be mixed. By default, the result is written to the folder ```<APE.buildDir>/merged```.

```
cd <ARE.baseURI>
..\APE\APE-copy -DAPE.models=models/ImageDemo.acs;models/eyetracking;D:/MyModelFiles/
```

#### One model file, custom build folder
You can specify a custom build folder with the property _APE.buildDir_

```
cd <ARE.baseURI>
..\APE\APE-copy -DAPE.models=models/ImageDemo.acs -DAPE.buildDir=C:\ImageDemo
```

## Build infrastructure for native installers

The tool APE-copy is only used to copy the required resources for a given model file to a certain location, but APE also provides a full build infrastructure (```APE.projectDir=<APE.baseURI>/defProjectDir```) that let's you

* configure the properties for APE-copy and the creation of native installers in a single file (```<APE.projectDir>/APE.properties```)
* replace single files/resources (images, model files, component and services jars and the respective configurations,...) of the ARE installation with custom files of the folder ```<APE.projectDir>/custom```
* customize the installer creation by providing drop-in resources and installer-specific files in the folder ```<APE.projectDir>/package/linux``` and ```<APE.projectDir>/package/windows```
* trigger the creation of a native installer by using the provided ant build file at ```<APE.projectDir>/build.xml```

### Example usages of the build infrastructure

The build infrastructure has the following prerequisites:
* [Java Development Kit 8] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Ant build system >= 1.9.2] (http://ant.apache.org/bindownload.cgi)
* Installer specific toolkits like Inno Setup, WiX toolset or debian packaging tools

#### One model file, windows .exe installer

**Prerequisites**:
* [Java Development Kit 8] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Ant build system >= 1.9.2] (http://ant.apache.org/bindownload.cgi)
* [InnoSetup >= 5] (http://www.jrsoftware.org/isdl.php)

To create an .exe installer of the model file ```<ARE.baseURI>/models/ImageDemo.acs``` copy the model file to the location ```<APE.projectDir>/custom/bin/ARE/models``` or edit the _APE.models_ property in the file ```<APE.projectDir>/APE.properties```. Then execute the following commands:

```
cd <APE.projectDir>
ant
```

By default all supported installer types for the currently running platform are created and stored at ```<APE.buildDir>/deploy/bundles```

#### One model file, Linux debian package

### Properties for copying/packaging behaviour of APE (file: APE.properties)

**TODO**
