# AsTeRICS Packaging Environment (APE)

The APE let's you select a set of AsTeRICS model files and create a downstripped (minimum size) version of the ARE including plugins, configuration files and data files to execute the models. Optionally, the APE allows the creation of native installers for Windows, Linux incl. Raspberry Pi and Mac OSX using [JavaFX packaging technology](http://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#BCGIBBCI).

## APE consists of two major elements
* [**APE-copy command line tool**](#ape-copy-commandline-tool): a commandine tool to create a downstripped version of the ARE based on provided model files
* [**Build infrastructure**](#build-infrastructure-and-native-installer-creation): a [template](template) project directory including an [ant](https://ant.apache.org/) build file and a property file [```APE.properties```](template/APE.properties). The build file contains the target ```ant APE-copy``` which provides the functionality of the APE-copy commandline tool and the target ```APE deploy``` to trigger the creation of **native installer** and  **native launcher** for a dedicated target platform.

## Important Terms
The term ```APE.baseURI``` refers to the location of ```APE.jar``` and the ```APE-copy``` command, which is in the folder ```APE``` parallel to the ```ARE``` folder of an AsTeRICS installation. The ```APE.baseURI``` folder also contains the build infrastructure to create the native installers and contains the default project directory (```APE.projectDir=<APE.baseURI>/defProjectDir```) and the default build directory (```APE.buildDir=<APE.baseURI>/defProjectDir/build```). ```APE.models``` refers to file and directory paths containing model files to use. Finally, ```ARE.baseURI``` refers to the location of the ARE that is used as the source for the extracted ARE versions.

## Dependencies
The commandline tool APE-copy only needs a Java Runtime Environment. In order to use the ant build targets and create native installers you also need **ant** and a **Java Development Kit 8**.

* Install the [**Java Development Kit 8 (32-bit)**] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  * Verify the java installation by opening a command shell and entering ```javac -version```. In case of trouble, ensure to set “JAVA_HOME” to the folder where you installed the Java JDK and add the JDK bin path to the Environment Variable “Path”
* Install the [**apache ant build framework (version >= 1.9.1)**] (http://ant.apache.org/bindownload.cgi)
  * Ensure to set “ANT_HOME” to the folder where you installed ant and add the ant bin path to the Environment Variable “Path”
* __Only for native installer creation__: Install installer-specific toolkits like [InnoSetup >= 5] (http://www.jrsoftware.org/isdl.php) (.exe), [WiX toolset >= 3.0](http://wixtoolset.org/) (.msi) or [debian packaging tools] (https://wiki.debian.org/PackageManagement) (.deb) depending on the required target platform. You must run the installer build process on the target platform of the installer. For more details, read the [JavaFX packaging tutorial] (https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#A1324980) 

## APE-copy commandline tool
Check the [dependencies](#dependencies) before you start.

To start APE-copy, call

```
APE-copy[.bat|.sh] -DAPE.models=<paths to model files or folder (seperated by ;)> [[-DAPE.buildDir=<Path to build folder>] [-DARE.baseURI=<Path to ARE installation>] [-DAPE.projectDir=<Path of project folder to use] [-DAPE.logLevel=[FINE|INFO|WARNING|SEVERE]]
```
Relative paths are resolved against the APE directory..
* **APE.models**: Provide a semicolon (;) seperated list of model files or folder. Relative and absolute paths can be mixed where relative paths are resolved against the APE directory. By default, the directroy ```<APE.projectDir>/custom/bin/ARE/models``` is automatically added to the parameter.
* **APE.buildDir**: The path to the build (output) folder. The downsized ARE is copied to that folder. The path can be relative or absolute where a relative path is resolved against the APE directory. By default, the ```build``` subfolder of the project directory (```APE.projectDir```) is used.
* **ARE.baseURI**: The path to the ARE installation. This can be the path to the ```bin/ARE``` folder of a development version (cloned git repository) or the ```ARE``` path of an installed AsTeRICS release (>= 2.8). The path can be relative or absolute where a relative path is resolved against the APE directory. By default, the parallel ```ARE`` folder is used.
* **APE.projectDir**: The path of the project directory to use. If the directory does not exist it is automatically created by copying the [```template```](#simplified-folder-structure) directory to the project directory. The given project directory must contain the ```APE.properties``` file, which contains default project-specific property values that can be overridden by the commandline switches of APE-copy. Additionally, the files and resources in the subfolder ```custom``` are finally copied to the target build directory (```APE.buildDir```). Read more about [customization of the ARE] here. By default, the project directory ```APE.projectDir=<APE.baseURI>/defProjectDir``` is used.
* **APE.logLevel**: You can specify the verbosity of the console output of the APE-copy command. Additionally, the log messages of the command execution are logged to the ```<APE.projectDir>/tmp```

### Example usages of the APE-copy commandline tool

You should be able to copy/paste the example commands below as long as you replace the placeholder with real values. The examples use windows path notations. 

**Note for Linux, Mac OSX**: On Linux and Mac OSX you must use ```APE-copy.sh``` and slashify ('/' instead of '\') the paths.

#### One model file
Create a downstripped ARE package of the model file ```ImageDemo.acs``` located in the ```ARE/models``` folder.
By default, the result is written to the folder ```<APE.buildDir>/merged```.

```
cd <APE.baseURI>
APE-copy -DAPE.models=../ARE/models/ImageDemo.acs
```

#### Several model files/folder
When specifying a folder, all contained model files (recursively) will be used. Several file or folder URIs can be seperated by a '**;**'. Relative and absolute URIs can be mixed. By default, the result is written to the folder ```<APE.buildDir>/merged```.

```
cd <APE.baseURI>
APE-copy -DAPE.models=../ARE/models/ImageDemo.acs;../ARE/models/eyetracking;D:/MyModelFiles/
```

#### One model file, custom build folder
You can specify a custom build folder with the property ```APE.buildDir```

```
cd <APE.baseURI>
APE-copy -DAPE.models=../ARE/models/ImageDemo.acs -DAPE.buildDir=C:\ImageDemo
```

## Build infrastructure and native installer creation

The tool APE-copy is only used to copy the required resources for a given model file to a certain location, but APE also provides a full build infrastructure (```APE.projectDir=<APE.baseURI>/defProjectDir```) that let's you

* configure the properties for APE-copy and the creation of native installers in a single file (```<APE.projectDir>/APE.properties```)
* replace single files/resources (images, model files, component and services jars and the respective configurations,...) of the ARE installation with custom files of the folder ```<APE.projectDir>/custom```
* customize the installer creation by providing drop-in resources and installer-specific files in the folder ```<APE.projectDir>/package/linux```, ```<APE.projectDir>/package/windows``` or ```<APE.projectDir>/package/macosx```
* trigger the creation of a native installer by using the provided ant build file at ```<APE.projectDir>/build.xml```

### Simplified folder structure
Subsequently you can see the simplified folder structure of an APE-based project, which contains an ant build file (**```build.xml```**), a property-based configuration file (**```APE.properties```**), the **```custom/bin/ARE```** folder to store the solution-specific files (e.g. model files), a **```build```** folder and a **```package```** folder for native installer customization. 

```
build
  |- merged
  |- deploy
custom
  |- bin/ARE
    |- data
    |- images
    |- LICENSE
    |- models
      |- <custom model file>.acs
      |- ...
    |-profile
package
  |- linux
  |- windows
  |- macosx
APE.properties
build.xml
```

### General workflow for using the build infrastructure

If you want to start a new project based on AsTeRICS functionality, you would normally

1. Use the subfolder ```defProjectDir``` directly or copy the [template](template) project directory to your project repository location.
2. Edit [```APE.properties```](template/APE.properties) and set the location of the AsTeRICS ARE (```ARE.baseURI```) to use for APE-copy. You only have to set ```ARE.baseURI``` if you use a project directory not within the AsTeRICS APE directory.
3. Create your AsTeRICS solution and save all needed resources (model files, images, configuration files,...) to the ```custom/bin/ARE``` folder.
4. Call ```ant APE-copy``` to create the extracted ARE solution or ```ant deploy``` to create a native installer for a certain target platform

Many Integrated Development Environments (IDE) like Eclipse support the ant build system. So you can use your favourite IDE to edit and build the project.

### Example usages of the build infrastructure

Check the [dependencies](#dependencies) before you start.

#### One model file, ant APE-copy

Copy the model file ```<ARE.baseURI>/models/ImageDemo.acs``` to the location ```<APE.projectDir>/custom/bin/ARE/models``` or edit the ```APE.models``` property in the file ```<APE.projectDir>/APE.properties```. Then execute the following commands:

```
cd <APE.projectDir>
ant APE-copy
```

The extracted ARE version can be found at ```<APE.buildDir>/build/merged/```, to test it, execute
```
cd build/merged/bin/ARE
start.bat
```

#### One model file, windows .exe installer

To create a native .exe installer [InnoSetup >= 5] (http://www.jrsoftware.org/isdl.php) must be installed and the build process must be run on a Windows system.

Copy the model file ```<ARE.baseURI>/models/ImageDemo.acs``` to the location ```<APE.projectDir>/custom/bin/ARE/models``` or edit the ```APE.models``` property in the file ```<APE.projectDir>/APE.properties```. Then execute the following commands:

```
cd <APE.projectDir>
ant deploy
```

By default, all supported installer types for the currently running platform are created and stored at ```<APE.buildDir>/deploy/bundles```
This can be changed by setting the property ```fx.deploy.nativeBundles``` to another value like ```msi```

#### One model file, Linux debian package

To create a debian installer the [debian packaging tools] (https://wiki.debian.org/PackageManagement) must be installed and the build process must be run on a debian-based Linux.

Copy the model file ```<ARE.baseURI>/models/ImageDemo.acs``` to the location ```<APE.projectDir>/custom/bin/ARE/models``` or edit the ```APE.models``` property in the file ```<APE.projectDir>/APE.properties```. Then execute the following commands:

```
cd <APE.projectDir>
ant deploy
```

By default, all supported installer types for the currently running platform are created and stored at ```<APE.buildDir>/deploy/bundles```
This can be changed by setting the property ```fx.deploy.nativeBundles``` to another value like ```deb```. On Linux, although also a .rpm package could be created by JavaFX packaging technology, APE only supports debian packages because the ARE needs some postinstall and prerm operations in order to run on Linux. 

#### One model file, Mac OSX dmg installer
Run one of the above examples on Mac OSX to create a .dmg installer. The .dmg packaging dependencies should already be contained in your Mac OSX version.

### Properties for copying/extraction behaviour of APE

To change the behaviour of the ARE extraction with APE-copy, edit the following properties of [APE.properties](template/APE.properties)

* ``APE.models```: Defines model files and folder containing model files to use
* ```APE.dataCopyMode```: Define if all the data files of the ```<ARE.baseURI>/data``` folder should be copied or just some.
* ```APE.servicesFiles```: Define if optional service configuration files should be used.

### Properties for installer creation

The provided build files only support a subset of the whole functionality of [JavaFX packaging](https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/javafx_ant_task_reference.html).
To change the behaviour of the installer creation, consider the following properties in [APE.properties](template/APE.properties)
* ```fx.deploy.nativeBundles```: To define installer type to create
* ```fx.application.*``` and ```fx.info.*```: To describe meta information of your application
* ```fx.preferences.*```: To define the desktop integration of the application
* ```APE.embedJava``` and ```fx.platform.basedir```: To embed a Java Runtime Environment into your native installer. The embedded java is linked to the native application launcher. If it is not embedded the default system java is used (only on Windows and Mac OSX).

### ARE customization
The ARE can be customized by replacing single files/resources (images, model files, component and services jars and the respective configurations,...) of the ARE installation with custom files of the folder ```<APE.projectDir>/custom/bin/ARE```. You only must ensure to use the same relative file paths.

#### Skipping non-used ARE services
If you want to exclude some of the ARE services (e.g. ```javacv-*.jar``` or ```cimcommunication*```.jar), simply copy the services configuration files of ```<ARE.baseURI>/profile/*.ini``` to the ```<APE.projectDir>/custom/bin/ARE/profile``` folder and edit them. You can exclude a service by commenting it out (prepending #) in the respective .ini file.

### Installer customization
If you want to customize installer-specific configuration files (.e.g .iss for .exe installer) or add your own application icon. Use the respective platform-specific subfolder in the [package](template/package) folder. There you can place replacement files for the default ones. To find out the supported files, that can be replaced, enable verbosity by setting ```fx.deploy.verbose=true``` in the [```APE.properties```](template/APE.properties) file.

Summurized, the following steps are necessary:
1. Enable verbose mode in [```APE.properties```](template/APE.properties): ```fx.deploy.verbose=true```
2. Run ```ant deploy```
3. Copy the resources (e.g. .iss or icon files) from the temp folder of the installer creation to the [package/<os>](template/package) folder
4. Edit the custom files
5. Rerun ```ant deploy```

If you want to know more, read [Customizing the Package Using Drop-In Resources](https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#BCGICFDB).

### ant build files
The ```template``` directory contains two ant build files: [build.xml](template/build.xml) and [imported.xml](template/imported.xml). The file ```build.xml``` contains some targets left for customization of the deployment build process. The targets ```before-deploy, before-deploy-windows, before-deploy-linux and before-deploy-macosx``` are called after the ```APE-copy``` target and before the ```deploy``` target. This way generic and platform-specific task can be added before the installer creation is triggered. You can use it to delete files which are not needed for a certain target platform (e.g. ARE.exe on Linux or javacv-*-macosx on Windows).
The second build-file ```imported.xml``` contains the internal targets and should not be modified except you really know, what you do.
