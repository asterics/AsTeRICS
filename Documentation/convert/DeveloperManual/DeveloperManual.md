# Developer Manual


## The AsTeRICS Runtime Environment
    

The AsTeRICS Runtime environment (ARE) is an OSGi-based middleware \[3\] which allows software plugins to run in parallel. The plugins usually represent a sensor or an actuator and are implemented as independent OSGi bundles. The runtime environment identifies AsTeRICS plugins from other OSGi bundles based on metadata defined inside the plugins.

The ARE expects from plugin-developers to define the structure of their plugins (properties, inputs, outputs and event ports) in XML files. Based on these XMLs, the middleware constructs a runtime representation of each installed AsTeRICS plugin.

Furthermore, the ARE expects a runtime model (system model) which usually comes from the AsTeRICS Configuration Suite (ACS). The ACS is running on a Windows Personal Computer (.net 4.0 required) and mainly used to graphically design the layout of the system as a network of interconnected components. The system model is another XML file that defines the components participating in a specific application, connections between them, events and other properties. Based on this file, ARE knows which plugins to activate and how to define the data flow between them. Since the system model represents the main communication means between the ACS and the ARE, it is expected to be a serialisable object, easy to transfer and translate. ARE and ACS communicate through an appropriate TCP/IP-based communication protocol named ASAPI.

  
![](./DeveloperManual_html_74b8c615b8455605.png)

  
  

The ARE also provides “services” to plugin developers (for example communication support for COM ports) and it allows reporting errors on the runtime environment, registering event listeners and interacting with its graphical user interface (ARE GUI).

The ARE GUI is a simple graphical environment developed to allow end-users to interact directly with the runtime environment. It may be used to modify runtime parameters of a model via buttons or sliders, and to monitor live signals and events of the running model.

  
  

## ARE Components
    

The ARE consist of the following main parts:

*   The ARE middleware
    
*   ARE plugins (also referred to as “components”) – sensor, processor and actuator modules which provide functional building blocks for assistive functionalities
    
*   A service layer which provides infrastructure to the ARE components,  
    for example COM port and communication management for connection of the Communication Interface Modules (CIMs)
    

  
The ARE is commonly deployed on an embedded device, running an appropriate operating system (OS), typically an embedded variant of Windows. On top of the OS, an appropriate Java Virtual Machine (JVM) is used to host the OSGi component framework which provides support for modularity and dynamic loading/unloading of components.

All the core components of the framework (described in detail later) are defined as OSGi modules. Certain components that need to access legacy code (e.g., written in C or C++) are also deployed on top of OSGi, and are interfaced to the native code using Java Native Interface (JNI) as needed. In this regard, and with the exception of the pluggable components that use native code interfaces with platform-specific JNI bindings, the ARE middleware is expected to be _platform independent_.

The implementation requires basically JAVA 1.7 (JDK/JRE 7) and an OSGi framework (which is part of the source code downloads).

## About OSGi
    

The Open Service Gateway initiative (OSGi) is an open specification that enables the modular assembly of software built with the Java technology \[3\]. The OSGi Service Platform facilitates the componentization of software modules and applications and assures interoperability of applications and services over a variety of networked devices.

OSGi technology is the dynamic module system for Java™. Java provides the portability that is required to support products on many different platforms. The OSGi technology provides the standardized primitives that allow applications to be constructed from small, reusable and collaborative components. These components can be composed into an application and deployed; The OSGi Service Platform provides a service-oriented architecture that enables these components to dynamically discover each other for collaboration, and thereby forms the optimal basis for the AsTeRICS middleware.

  
  

  
  

## Getting Started with AsTeRICS Development
    
    ## The AsTeRICS Source Code Repository
        

The AsTeRICS source code repository is hosted at github and located at

[**https://github.com/asterics/AsTeRICS**](https://github.com/asterics/AsTeRICS)

The source code contains open source software modules in JAVA, C++ and C, and proprietary modules by AsTeRICS partners which are available in binary from (.dll or .exe).

The licenses of the utilized software packages and 3rd party products can be viewed in the file _/documentation/licenses.doc_

Currently, the editor for OSKA (the on-screen keyboard application) is the only commercial software package within the AsTeRICS framework – and not included in the free downloads. The OSKA editor is only needed if you want to design custom on-screen keyboard layouts for OSKA (see AsTeRICS User Manual).

  
  

## Repository structure
    

The source code repository is organised in the following subfolders:

![](./DeveloperManual_html_27e824dab978d7f3.png)

The _**ACS**_ folder contains the AsTeRICS Configuration Suite source code.

The _**Android**_ folder contains a server application for Android phones which allows interfacing with the AsTeRICS Android plugin to use phone functions in AsTeRICS models.

The _**ARE**_ folder contains the middleware and service layers and ARE components.

The _**bin**_ folder contains subfolders where ARE and ACS executable files are placed during the build flow. These folders contain additional configuration files or dependencies, for example the config.ini and loader.ini files which specify the modules which are loaded by the ARE at startup.

Additionally, the bin folder contains several resources which are useful, e.g. a pre-built ACS with demo models (in the ACS\\models folder) and the OSKA application.

  
  

The _**BNCIevaluationSuite**_ is a collection of matlab files for analysis and comparison of algorithms for Brain Computer Interfaces (contributed by Starlab).

The _**CIM**_ folder contains firmware for the microcontroller modules used to interface the system to the environment (maintained by IMA and FHTW).

The _**Documentation**_ folder contains the User- and the Developer Manual, and OSKA manual and the licence information for the developed and all utilized source code and libraries.

The _**NativeASAPI**_ folder contains C++ libraries for mobile-phone and GSM modem access, 3d-mouse and tremor reduction from own C++ projects.

  
  

## Setting up the Eclipse IDE for ARE development
    

The ARE framework is not bound to a specific tool flow or IDE. For the convenience of the development process and ease-of-use for new developers, an Eclipse-based build is available and will be described in this section. If you prefer a different IDE you can skip this section. The described setup applies for Microsoft Windows operating systems. If Java and the Eclipse IDE are already installed, steps 1 - 4 can be omitted.

1.  Download and Install the Java Development Kit 7 (JDK 7), JDK 8 is supported as well.  
    from [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
    

![](./DeveloperManual_html_b0fc8d66a4384f23.png)

Choose the 32bit version for your operating system, because some necessary components for interfacing hardware are not supported by the 64bit version by now.

2.  C![](./DeveloperManual_html_e0e56374eade1421.png) reate a System Environment Variable “JAVA\_HOME” which points to the folder where you installed the Java JDK. The dialog for system environment variables can be found via System Properties -> Advanced -> Environment Variables
    

  
  

3.  A![](./DeveloperManual_html_b2e05658527ed1b7.png) dd the JDK bin path to the System Environment Variable “Path”
    

  

  
  

  

4.  Download and install Eclipse Luna from [http://www.eclipse.org/downloads/](http://www.eclipse.org/downloads/)
    

![](./DeveloperManual_html_377a3cdc7c8e5f15.png)

Note that the 32-bit version is also recommended for 64-bit machines e.g. running Windows-7 (as there have been reported problems with the 64-bit version)

5.  Download and install ant build framework, to build AsTeRICS from the command line (optional)
    
    1.  Download and install the apache ant build framework (version >= 1.9.1)  
        [http://ant.apache.org/bindownload.cgi](http://ant.apache.org/bindownload.cgi)
        
    2.  Create a System Environment Variable “ANT\_HOME” which points to the installation directory of ant.
        
    3.  Add the ant bin path to the System Environment Variable “Path”
        
6.  Download and extract the AsTeRICS source code
    
    1.  Use your favourite git client and clone the github repository  
          
        [https://github.com/asterics/AsTeRICS.git](https://github.com/asterics/AsTeRICS.git)
        
    2.  Or download and extract the .zip file into a desired location on your hard disk
        
7.  Start eclipse.exe (If starting the first time, create a workspace folder as suggested)
    
8.  Choose _File -> New -> JavaProject_ in the Eclipse main menu, disable the option _“Use default location”_ and browse to the _ARE_ subfolder:
    

![](./DeveloperManual_html_33ee9963708d1fff.png)

9.  Then you should see something like this:
    

![](./DeveloperManual_html_f830b9b181946728.png)

Congratulations ! – You have now a working AsTeRICS build environment !  
  
  

Eclipse provides different views (_Window -> Show View_), where the _Navigator_ and the _Package Explorer_ are most useful for Java source code development.

Note that the “_Refresh_” command (_F5_) synchronizes the Navigator view with changes in the local file system.

## Setting up build environment for JDK 8
    

If you follow the steps of 2.2 JDK 8 is supported without modifications. In case you have an older installation of Eclipse (version < Luna (4.4)), you can either

1.  upgrade to Eclipse Luna or
    
2.  upgrade the used ant version to at least 1.9.1. In this case you have to tell Eclipse where to find the new version of ant.
    
    1.  Start Eclipse and click on “Run/External Tools/External Tools Configuration”  
        ![](./DeveloperManual_html_88280be4a73a0ac5.png)
        
    2.  Click on tab “Classpath” and set the new Ant Home by clicking onto the respective button.  
        ![](./DeveloperManual_html_986d42a4442fb665.png)
        

## Building ARE Middleware, Services and Components
    

For building the ARE middleware and components (plugins), the supplied _ANT_ build scripts are recommended. _Apache ANT_ is a command-line based build tool for Java applications \[8\]. Eclipse provides an _ANT_ plugin which operates these build scripts (named “build.xml” in the AsTeRICS repository). You can either use the command line ant command in a windows shell or use the Eclipse plugin.

The middleware, the services and the components have separate build.xml files. The middleware and services are required for building the components. To build everything, a top-level build script is available in the ARE folder. To use this top-level build script, switch to the Java Project Perspective, right-click the “build.xml” file located in ARE-section of the Navigator window (as shown below) and select the second menu entry in the context menu: “2 RunAs -> Ant Build”:

![](./DeveloperManual_html_93c76b077daeb22a.png)

This opens the “Edit configuration and launch” window, where the build targets of the top-level build script can be selected. These build targets provide different “on-Click” builds for the AsTeRICS framework.

## Character encoding settings for successful builds
    

I![](./DeveloperManual_html_d5be58f6ea0f5f1.png) ncorrect character encoding settings can cause problems with ANT-based builds in eclipse, especially if special characters like ö, ä, etc. are used in the source code. Be sure to use ISO-8859-1 as character encoding in _Configuations and Lauch_ settings (in the _Common_ tab) as shown in the screenshot (note that this is not the default setting in some versions of eclipse):

  
  

  
  

## One Click Builds
    

The top-level build script allows building all components that exist in the source tree. It also defines several properties which are inherited to all component build scripts. An important example is the “debug” property which defines via compiler options if the code shall be instrumented with source code level debugging information (“true”) or not (“false”).

The top-level build script provides the following targets:

*   BuildARE: builds just the middleware
    
*   BuildServices: builds the middleware and all _services_ (eg. CIMCommunication etc.)
    
*   BuildAllNoClean: builds middleware, services and components without prior clean
    
*   BuildAll: cleans build targets, builds middleware, services and components
    
*   BuildAll-release: cleans build targets, builds middleware, services and components  
    without source-level debug information for the eclipse remote debugger
    
*   Clean: cleans build targets (removes all jar files and the out directory)
    

  
  

The source level debug information is enabled by all build targets of the top-level build script except “BuildAll-release”.

Alternatively, individual services or components can be built by selecting their associated “build.xml” script from the corresponding subfolders In these scripts, source level debugging information is per default disabled in the compilation step.

## Understanding the component build-scripts
    

A typical ANT build script for an ARE component looks like the following:
```xml
<project  name="asterics.${component.id}"  default="jar"  basedir="."\>

<property  name="component.id"  value="processor.MyComponent"/>

<!-- set global properties for this build -->

<property  name="build"  location="../out/production/${component.id}"/>

<property  name="src.java"  location="src/main/java"/>

<property  name="dist"  location=".."/>

<property  name="runtime"  location="../../../examples/ARE"/>

<property  name="osgi"  location="../../osgi"/>

<property  name="middleware"  location="../../middleware"/>

<property  name="services"  location="../../services"/>

<property  name="classpath"  location=".."/>

<path  id="asterics.classpath"\>

<pathelement  location="bin"/>

<pathelement  location=

"${osgi}/org.eclipse.osgi\_3.6.0.v20100517.jar"/>

<pathelement  location="${middleware}/asterics.ARE.jar"/>

</path>

<property  name="resources"  location="src/main/resources"/>

<target  name="init"\>

<!-- Create the time stamp -->

<tstamp/>

<!-- Create the build directory structure used by compile -->

<mkdir  dir="${build}"/>

</target>

<target  name="compile"  depends="init"  description="compile the source "\>

<javac  srcdir="${src.java}"  destdir="${build}"  verbose="true" debug="${debug}"

classpath="${classpath}"\>  <classpath  refid="asterics.classpath"/>

</javac>

</target>

  

  

<target  name="jar"  depends="compile"

description="generate the OSGi bundle"  \>

<jar  jarfile="${dist}/asterics.${component.id}.jar"  basedir="${build}"

manifest="${resources}/META-INF/MANIFEST.MF"\>

<fileset  dir="${resources}"/>

</jar>

<copy  file="${dist}/asterics.${component.id}.jar"

tofile="${runtime}/asterics.${component.id}.jar"/>

</target>

</project>
```
  
  

In the first section of the build script, folder locations for the build intermediates, the final build products (.jar file) and the classpath are defined. The classpath usually points to the “bin” folder, the middleware “asterics.ARE.jar” and the osgi distribution. If a component needs additional resources, their location has to be defined here.

Subsequently the build script defines two build targets: the compilation of the Java source code and the creation of the .jar file. If the .jar file shall contain additional .dlls with native code, they have to be specified in the Manifest file as shows in section 5.14.1.

After the .jar file has been created in the distribution folder, it is copied to the runtime folder (/bin/ARE).

## Starting the ARE middleware and component deployments
    

To test the ARE and component bundles, open the folder “/bin/ARE”, and use ARE.exe, start.bat or start\_debug.bat.

## Structure of the runtime folder “./bin/ARE”:
    

This folder contains dependencies for running the ARE middleware and the .jars resulting from ANT builds, it has the following structure:

```
/

+- bin/

+- ARE/

+- data/ folder for plugin working data

+- models/ stored models (configurations)

+- profile/

+- config.ini  system bundles to be started

+- services.ini  general service bundles to be started

+- services-windows.ini  windows-specific service bundles

+- services-linux.ini  linux-specific service bundles

+- services-macosx.ini  macosx-specific service bundles

+- org.eclipse.osgi/ osgi bundle cache folder

+- 1238790741.log system log messages, stack trace

+- tools/  plugin helper apps and dlls

+- .logger stores console logging settings

+- ARE.exe starts the ARE without console output

+- areProperties stores recent window/GUI properties

+- <my\_component.jar> component bundle(s)

+- asterics.ARE.jar ARE middleware

+- asterics.mw.services.cimcommunication.jar CIM port manager

+- grizzly-httpservice-bundle-2.3.23.jar http service for webservice feature

+- javacv-\*-linux|windows|macosx.jar platform specific javacv service

+- tmp/\*.log application log files

+- jtester.exe helper app for checking Java version

+- logging.properties  configuration of loglevel etc.

+- org.eclipse.osgi.\*.jar  osgi distribution

+- sleeper.exe helper app for launcher timing

+- start.bat  starts ARE with console output

+- findjava.bat  searches for the newest 32bit JRE

+- start\_debug.bat  starts ARE with Eclipse debug support

+- start.sh starts ARE without console on Linux

+- start\_debug.sh starts ARE with debugging on Linux

+- VCChecker.jar helper jar for checking VC redist dependency
```
  

  
Important Note: The osgi configuration folder “org.eclipse.osgi” in the “profile” subdirectory has to be deleted if .dlls in .jar bundles are updated or changed. (This folder is automatically created when starting the ARE and holds working data for the OSGI-bundles.) The One-Click build.xml script described in chapter 2.4.2 deletes the folder automatically.

## AsteRICS services
    

An AsteRICS service is a bundle that provides ARE-wide functionality usable by other services or plugins. The service can be optionally disabled which means that the service bundle is not installed and not activated. The file services.ini contains a list of general services to be loaded. Whereas the services-windows.ini, services-linux.ini and services-macosx.ini files contain platform dependent service names. You can also create your own use-case specific services ini file and edit the start script to load it.

## Running a deployment
    

The “ARE.exe” starter application launches the ARE without console output and without debugging instrumentation.

Alternatively, the commandline batch script “start\_debug.bat” which is provided in the folder “.bin/ARE” runs Java with additional configuration parameters including:

*   the location of the OSGi distribution
    
*   the profile subfolder which contains the config.ini file: “./bin/ARE/profile”
    
*   debugging instrumentation for the remote debugging server connection
    

After starting the ARE middleware, bundles are loaded and started on-demand if they are needed for the deployment of a model. If everything is properly configured, the ARE window comes up with a GUI and provides ASAPI server functionalities for connection of the ACS or other client applications.

## ARE webserver (including REST API, websocket)
    

The ARE contains a service that creates several web-based services. These include

*   a webserver with document root _ARE/web_ and URL: [http://localhost:8081/](http://localhost:8081/)
    

*   a websocket at URL [http://localhost:8082/ws/astericsData](http://localhost:8082/ws/astericsData)
    
*   a REST API at URL [http://localhost:8081/rest](http://localhost:8081/rest)
    
*   a javascript REST API client implementation example at [http://localhost:8081/](http://localhost:8081/)
    

## Define autostart model per command line
    

By starting the ARE with the name of a model as first command line parameter a model that should be started automatically can be defined. The model must exist in the sub-folder “models”.

ARE.exe CameraMouse.acs

or

start\_debug.bat CameraMouse.acs

## Change model task submit timeout
    

The file “areProperties” contains properties to configure ARE features and to configure the internal model execution behaviour. The following internal model execution properties exist:

*   ThreadPoolTasks.submitTimeout=20000
    
    *   When submitting a task to be executed in the ModelExecutor thread a submit timeout can be configured. After the time elapsed a TimeoutException is thrown. The timeout value must be specified in milliseconds.  
        The submit timeout is used for starting, stopping, pausing and resuming a model.
        

## Debugging the ARE
    

If the ARE is started using the “start\_debug.bat” script and source-level debug information was added during the compilation (see section 2.3), debugging with Eclipse is supported via a remote debugging connection. This is a convenient way for debugging an OSGI-based java framework with a lot of plugins. To enable the debugging support in Eclipse, a Debug Configuration is created via the dedicated menu entry:

  
  

![](./DeveloperManual_html_c0991ef32ae9b458.png)

  
  

Create a “Remote Java Application” Debug Configuration and assign a name for it, e.g. “ARE”. Then, specify the connection properties of the Debug Configuration to use the Host “localhost” and the Socket/Port “1044” (this port is given in the ARE build scripts for the remote debug server to listen for incoming client connections):

  
  

![](./DeveloperManual_html_4dfe72317ab9f571.jpg)

  
  

Now launch the ARE using “start\_debug.bat”. The messages in the console window should indicate the establishment of the listening socket 1044 for the debugging connection:  
  
  

![](./DeveloperManual_html_b6693a1cbcd9178a.png)

Now, the usual debugging support of Eclipse can be used, including breakpoints in middleware or components, variable and context watch windows, single stepping etc. All these operations are performed in the Eclipse “Debug” perspective.

The following screenshot shows a program execution of the ARE which ran into a breakpoint (here: the OSKA plugin was halted as a command was selected in the OSKA-application and transferred to the ARE plugin’s command handler:

![](./DeveloperManual_html_f1a0b4f36dd370ba.jpg)

  
  

I![](./DeveloperManual_html_fb7627584ba9d5d7.png) f the source-level debug information is missing (due to compilation without debugging support) an error message indicates a problem, e.g. the missing line number for breakpoint installation:

  
  

## A Quick Guide to AsTeRICS Plugin Development
    

This section describes the AsTeRICS Plugin-Creation tool and the plugin-activation process. These tools make it easy to create new plugins and make them available in ACS and ARE. They can be started manually from their location in the **AsTeRICS\_runtime.zip** package (folder: “ACS/tools”) – or they can be launched from the “Misc.” – Tab in the main menu of the ACS:

![](./DeveloperManual_html_c2213f881cbf5182.png)

  
  

The creation of a new AsTeRICS plugin for the runtime environment involves several steps:

*   creating the folder structure to store the plugin files
    
*   creating the ANT build script file
    
*   creating the manifest file
    
*   creating the bundle-descriptor, which specifies the ports and properties of the plugin
    
*   creating the source code file of the JavaInstance
    
    *   defining the ports and properties and implementing the get- and set-methods for input-, output-, eventListener- and evenTrigger ports
        
    *   implementing the get- and set- methods for property values and the input ports receive handlers
        
*   creating the license files for the plugin and third-party libraries in the **LICENSE** subfolder
    

This process is similar for each plugin, and involves much work and sources of errors, especially for people who work with the AsTeRICS framework for the first time.

Usually, you look for a plugin with similar specifications, copy its folder structure and then rename and change the files as desired. But also this process needs some effort and errors/typos can be introduced very easily.

The purpose of the AsTeRICS Plugin Creation Tools is to make it easy to create new plugins, by providing the necessary folder structure, the bundle descriptor and a template for the JAVA source code.

  
  

## The Plugin Creation Wizard
    

The plugin Creation wizard allows definition of characteristics of a new plugin and creates the needed folders and files for the Eclipse build flow, including the JAVA source code skeleton and the plugin’s bundle descriptor.

![](././DeveloperManual_html_ae369b509ff2c0e5.png)

  
  

As can be seen in the above figure, desired input- and output ports, data types, properties and plugin-features are simply selected and added to list boxes on the screen.

Important Notes:

*   the path to the target folder has to exist in the local file system, and must point to the ARE/components directory where all plugin source files are located, e.g.: “C:\\asterics\\bin\\components\\”.
    
*   The plugin name must be specified in CamelCase letters (capital first letter), e.g. “MyPlugin”. Type and Subcategory have to be specified - they define the location where the plugin will appear in the ACS Components menu.
    
*   It is possible to create a list of possible text-selections in a combo-box in the ACS property editor. The data type for this property must be integer, the property gets the number of the selected item. Text-captions for the combo-box entries must be separated with double slash, e.g: “Mode 1//Mode 2//Mode 3”.
    

## Created files and folders
    

After “Create Plugin!” has been pressed and the plugin creation was completed successfully, following sub-folders and files are begin created:

![](./DeveloperManual_html_dd4b70e6240040e3.png)

  
  

The root folder contains the build script, which can be executed inside Eclipse to compile and build the plugin (.jar) file:

![](./DeveloperManual_html_a82259165c76b9df.png)

  
  

The META-INF folder contains the manifest file

![](./DeveloperManual_html_3d4398abcb45cc73.png)

The “resources” folder contains the bundle descriptor (bundle\_descriptor.xml):

![](./DeveloperManual_html_fbbda5b6e8f42820.png)

  
  

The source code folder “src\\main\\java\\eu\\asterics\\component\\<pluginType>\\<pluginName>” contains a template for the plugin source code in JAVA, including the definitions of the selected ports and properties and the needed get- and set- methods for ports and property values. The code skeleton complies to the AsTeRICS coding guidelines and contains the AsTeRICS source file header (only a small portion is shown in the following screenshot).

![](./DeveloperManual_html_a0117682e4ae2ecc.png)

After the Eclipse IDE has been opened, Eclipse must be pushed to refresh the folder structure by pushing F5. Furthermore, the path “src\\main\\java” must be configured as source folder.

![](./DeveloperManual_html_4304f99776fb485f.png)

  
  

The plugin code can be built using the provided build script (right-click build.xml -> RunAs -> Ant Build in the plugin’s folder)

![](./DeveloperManual_html_f35fad4db20b5c1.png)

  
To see the plugin in the ACS editor window and/or start it inside the runtime environment, the Plugin Activation Tool can be used (see section 3).

  
  

## Plugin Activation in ACS and ARE
    

To use a new AsTeRICS plugin which has been built using the Eclipse build flow and exists as executable .jar file/OSGI bundle, one step is necessary:

1.  The Plugin has to be **announced to the ACS** – so that it gets visible in the graphical editor and can be used for the creation of deployment models. This is done by adding the bundle descriptor of the new plugin to a component-collection file (extension “.abd”) in the ACS-folder. These component collections contain all bundle-descriptors of components which can be used in the ACS. The new plugin section can be added either manually or can be downloaded from the running ARE via the ACS’ Component-Collection Manager (recommended, see 3.2.1). Using the Component Collection Manger, the downloaded collection can be stored as “default Component Collection” for the ACS, so that all components will be available when the ACS is started next time.
    

  
  

Subsequently, the plugin can be selected in the “components” menu of the ACS, and the ARE will activate the plugin at startup.

  
  

## Component-Collection Management in the ACS
    

The ACS provides a function for downloading the bundle descriptions of all active plugins directly from a running ARE and creating a component collection file from this information. (“System”- tab, Button “Download Component Collection”):

![](./DeveloperManual_html_2672e47f28834257.png)

The component collection will be stored as “.abd” – file in the ACS folder, subfolder “componentcollections”. The new component collection can be used right after download, but will not be available after an ACS restart.

Within the ACS Component-Collection Manager (in the “Miscellaneous” tab), component collections can be selected or set as default collection for the ACS startup. For details see the User Manual, ACS section.

Please note that the “loader.ini” – file has to be updated manually in the ARE’s “profile” subfolder, by addition of the new .jar filename. After restart of the ARE and connection to the ACS, the component collection can be downloaded.

## Plugin License Declaration
    

Since AsTeRICS version 2.8 the licenses (including licenses of third-party libraries) of a plugin must be provided as .txt files in the LICENSE subfolder. The filenames must follow a defined naming convention, which is:

A contributor must add the respective license file for the component (either **LICENSE\_MITOrGPLv3WithException.txt** or one of the two dual license options) to the component/LICENSE folder. Furthermore, for each thirdparty library used, the license file must be added with the following naming convention (Please use CamelCase notation for the library and license names):

**THIRDPARTY\_NameOfLibrary\_LicenseNameInclVersionInfo.txt**

  
  

## Writing AsTeRICS Plugin Code
    
    ## ARE Coding Guidelines
        

Coding guidelines are necessary to allow new developers to quickly find their through the code of the ARE. They are created in such a way to provide means for developers to understand code of each other but they also make sure that non-technical users can find their way through a model in ACS.

If you use Eclipse as IDE, **you can import predefined clean up, code template and formatting settings**, which cover some of the coding guidelines (see 4.1.1).

The basic coding guidelines are:

*   Plugins, ports and properties should be named intuitively in the bundle descriptor. Only if necessary, the corresponding variables in the plugin code should be named differently. However they should adhere to the naming conventions stated in section 4.1.3 and different names should be commented in the code sections which translate the name into the variable (getInputPort(), getRuntimeProperty() …)
    
*   Variable names should always use the Java naming conventions
    
*   Every method should be preceded by a JavaDoc compatible header in order to allow new developer to grasp what is going on in it
    
*   Where reasonable code comments should be added to improve understanding of code internals
    
*   Code should be indented by four spaces per indentations stage. Indentations should be done using space and **not tabs**. Tabs should be converted to spaces.
    
*   Opening parentheses should be placed in the same line
    

## Eclipse Code Style Settings
    

If you use Eclipse as IDE, you can import predefined clean up, code template and formatting settings. The files are located in the AsTeRICS/ARE folder of the checkout github repository.

*   **Eclipse - Code Style - Clean Up.xml**: Definitions for cleanup, e.g. remove unused imports and unused variables
    
*   **Eclipse - Code Style - Code Templates.xml**: Contains default file header including license information (see 4.1.5) and default class comment (see 4.1.6).
    
*   **Eclipse - Code Style - Formatter.xml**: Contains code formatting definitions e.g. 4 spaces instead of tabs.
    

You can import the settings by

1.  selecting the project folder and clicking right mouse button
    
2.  opening ‘Properties’ entry of popup menu
    
3.  Opening ‘Java Code Style’/Clean Up|Code Templates|Formatter respectively
    

## Port Naming Conventions
    

Variables of port instances should be named with a prefix indicating what kind of port it is. The rest of the port name should indicate the port’s use and adhere to the standard Java variable naming conventions. The available prefixes are:

*   ip: indicates that the port is an instance of IRuntimeInputPort
    
*   op: indicates that the port is an instance of IRuntimeOutputPort
    
*   elp: indicates that the port is an instance of IRuntimeEventListenerPort
    
*   etp: indicates that the port is an instance of IRuntimeEventTriggererPort
    

A variable holding an event listener port could therefore be named _elpKeyPressed_.

  
  

## Property Naming Conventions
    

Plugin properties should be directly mapped to a variable in the plugin code. The variable’s should be prepended with the prefix _prop_ and adhere to standard Java naming conventions. Thus a property could be named _InputGainValue_ and the corresponding variable should be named _propInputGainValue_.

## Bundle Descriptor Naming Conventions
    

The bundle descriptor should serve as an abstraction layer between the user who creates models in the ACS and the developer. Thus the names for plugins, ports and properties in the bundle descriptor should be as intuitive as possible. Names in the bundle descriptor should not include prefixes because the added information is also conveyed in the presentation of plugins in the ACS.

The bundle descriptor can translate intuitive names (e.g. input.switch) to the canonical names of plugins (e.g. GpioInputInstance) allowing coexistence of a user and a developer language. This method of name translation can be applied for plugin names, port names and property names.

## AsTeRICS ARE, service and plugin source file header
    

Each source file of ARE, of ARE services, ARE plugins and tools which will be released as open source should have the following header:

```java
/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */
```

## JavaDoc compatible comments
    

JavaDoc compatible comments should be used to indicate the author of a source file, and to describe the purpose of a function/method/class and the respective parameters and return values.

  

Example for a source file header info:
```java
/\*\*

\* Bardisplayinstance.java

\* Purpose of this module:

\* Implements the Bardisplay actuator plugin

\*

\* **@author** Chris Veigl \[veigl@technikum-wien.at\]

\* Date: Mar 7, 2011

\*/
```
  

Example for a method of a class:
```java
/\*\*

\* Returns the value of the given property

\* **@param** propertyName the name of the property

\* **@return** the property value

\*/

**public** Object getRuntimePropertyValue(String propertyName)
```

## Implementing AsTeRICS components
    

This section describes the basic steps required for implementing an AsTeRICS component including a brief introduction to OSGi. To illustrate the implementation steps, we take a walk-through with the implementation of a simple processor component.

The AsTeRICS schemata of the XML descriptors include two concepts: the _bundle descriptors_ and the _deployment descriptors_.

## The Bundle Descriptors
    

Bundle descriptors are used to describe the content of an individual bundle (typically encapsulating one or more components). As such, they contain information about the included _components_, their _ports_, their customizable _properties_ and optionally their GUI.

The following shows a bundle descriptor of a simple processor-plugin (subtype for the ACS components menu is “Basic Math”). The plugin provides an averaging function for n values (property “buffer-size”) and has one input port and one output port for integer values:
```
<?xml version="1.0"?>

<componentTypes xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

xsi:noNamespaceSchemaLocation="bundle\_model.xsd">

<componentType

id="asterics.averager"

canonical\_name="eu.asterics.component.processor.averager.AveragerComponent">

<type subtype="Basic Math">processor</type>

<description>Linked list-based averager</description>

<ports>

<inputPort id="in\_1">

<description>Input port of averager</description>

<multiplicity>one-to-one</multiplicity>

<mustBeConnected>true</mustBeConnected>

<dataType>integer</dataType>

</inputPort>

<outputPort id="out\_1">

<description>Output port of averager</description>

<dataType>integer</dataType>

</outputPort>

</ports>

<properties>

<property name="buffer-size"

type="integer"

value="50"

description="The size of the averager's buffer"/>

</properties>

</componentType>

</componentTypes>
```
## The Deployment Descriptor
    

Deployment descriptors instruct the ARE of the desired application deployment structure. The deployment descriptor is typically composed in the AsTeRICS Configuration Suite (ACS) but can also be written with a text editor (as the bundle descriptor). Basically the deployment descriptor contains several component descriptions (copied from the corresponding bundle descriptors), actual property values and the channel connection between input- and output ports of the components.

Please note that the _type\_id_ argument of the _component_ element in deployment descriptor must match the _id_ argument of the _componentType_ element on the bundle descriptor. This is how the ARE detects the referred plugin type in the deployment model.

The following demo deployment descriptor describes a simple model containing two plugins and one channel:

  
```
<?xml version="1.0" encoding="UTF-8"?>

<model xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

xsi:noNamespaceSchemaLocation="deployment\_model.xsd">

  

<components>

  

<component type\_id="sensor.SignalSource" id="sensor.SignalSource.1">

<description>A Source of two signal cahnnels </description>

<ports>

<outputPort portTypeID="outport1"/>

<outputPort portTypeID="outport2"/>

</ports>

<properties>

</properties>

</component>

  

<component type\_id="actuator.SignalTarget" id="actuator.SignalTarget.1">

<description>A Signal Target</description>

<ports>

<inputPort portTypeID="in\_x"/>

<inputPort portTypeID="in\_y"/>

</ports>

</component>

</components>

  

<channels>

<channel id="channel.1">

<description>Connects SignalSource.1 (outport 1)

to SignalTarget.1 (in\_x)</description>

<source>

<component id="sensor.SignalSource.1"/>

<port id="outport1"/>

</source>

<target>

<component id="actuator.SignalTarget.1"/>

<port id="in\_x"/>

</target>

</channel>

</channels>

</model>
```
## The Manifest file
    

The Manifest file tells the bundle name and other informations like import packages and .dlls to the OSGi. A typical Manifest looks as follows:

  
```
Manifest-Version: 1.0

Bundle-ManifestVersion: 2

Bundle-Name: asterics-processors.averager

Bundle-SymbolicName: eu.asterics.component.processor.averager

Bundle-Version: 0.1.0

DynamicImport-Package: \*
```
  

  
  

Please note the empty line at the end of the Manifest file. It seems that OSGi needs that empty line in order to work properly. An example of a Manifest file of a component containing native code .dlls can be found in section 5.14.

## Structure of OSGi bundles containing ARE components
    

As a common OSGi bundle, an AsTeRICS component must be packaged in a JAR file, containing the class files (object code) and the Manifest file. In addition to these, the AsTeRICS middleware expects the _bundle descriptor_. At this point, it should be noted that it is possible to include _multiple_ AsTeRICS components in a single OSGi bundle, as long as the bundle descriptor describes all of them.

Overall, the file structure in a typical AsTeRICS bundle looks as follows:
```
/

+- eu/

+- asterics/

+- component/

+- ...

+- lib/

+- native/

+- my\_library.dll

+- META-INF/

+- MANIFEST.MF

+- bundle\_descriptor.xml
```
  

  
  

The Java object code is included in the corresponding folders representing the package structure (e.g., “/eu/asterics/component/...” etc). Optionally, if libraries are needed - native or not-, then they are included in the “/lib” folder. The Manifest is included in the “META-INF” folder as per the standard Java/OSGi practice. Finally, the AsTeRICS bundle descriptor is included directly in the root of the JAR file (i.e. “/”).

  
  

  
  

  
  

## Component lifecyle
    

An ARE component implementation needs to realise the actual component with its lifecycle (i.e., ways to access its ports and properties, and methods realizing its lifecycle). This is illustrated in the following code:

  
```java
package eu.asterics.mw.model.runtime;

  

public interface IRuntimeComponentInstance

{

// ------------------ Lifecycle support methods ------------------------- //

  

public void start();

public void pause();

public void resume();

public void stop();

  

// ------------------ Component support methods ------------------------- //

  

public IRuntimeInputPort getInputPort(final String portID);

public IRuntimeOutputPort getOutputPort(final String portID);

public IRuntimeEventListenerPort getEventListenerPort(final String eventPortID);

public IRuntimeEventTriggererPort getEventTriggererPort(final String eventPortID);

  

public Object getRuntimePropertyValue(String propertyName);

public List<String> getRuntimePropertyList(String key);

  

public Object setRuntimePropertyValue(String propertyName, Object newValue);

public void syncedValuesReceived (HashMap <String, byte\[\]> dataRow);

}
```
  

  
  

![](./DeveloperManual_html_17298a48a6d250c5.png)  
  

The lifecycle support methods are used to intercept AsTeRICS events concerning the component’s lifecycle. In principle, a component can be any of the following:

*   READY,
    
*   ACTIVE,
    
*   SUSPENDED and
    
*   STOPPED
    

These states and their possible transitions are illustrated in the figure on the right:

The rest of the methods are used for supporting the component operations, namely accessing the input/output ports of the component, as well as getting/setting its supported properties.

  
  

## Step-by-Step implementation: Averager processor
    

In the following, the implementation on a simpe “averager” component is described. This component realizes some simple processing functionality: It collects its most recent input from one input port and produces its average at one output port. The number of samples to be stored and used for the computation of the average is controlled by a property.

The component shall have a single input port (named “in\_1”), a single output port (named “out\_1), and a single property (named “buffer-size”) which has the type “integer” and the default value “50”.

Using the PluginCreationWizard, the bundle descriptor, the Manifest file, the build script and the skeleton for the JAVA-code can be generated (see section3.1).

Then the actual Java-Code which implements the plugin’s functionality can be added.

The functionality of this component is quite simple: It takes as input integer values, which are queued in a buffer in a first in, first out order (FIFO). Whenever a new value is added, the average of the buffer value is computed and provided in the output. The size of the buffer is controlled by the “buffer-size” property. A possible implementation is shown below.

  
```java
public static final int DEFAULT\_BUFFER\_SIZE = 10;

private final LinkedList<Integer> buffer = new LinkedList<Integer>();

private int bufferSize = DEFAULT\_BUFFER\_SIZE;

  

  

public Object setRuntimePropertyValue(String propertyName, Object newValue)

{

if("buffer-size".equalsIgnoreCase(propertyName))

{

final Object oldValue = bufferSize;

  

if(newValue != null)

{

if(newValue instanceof Integer)

{

bufferSize = (Integer) newValue;

// truncate unnecessary tail elements

while(bufferSize < buffer.size())

{

buffer.removeLast();

}

}

else

{

AstericsErrorHandling.instance.reportError(this,  
"Invalid property value for "+propertyName+":"+newValue);

}

}

return oldValue;

}

return null;

}

  

  

  

private int addInt(final int in)

{

buffer.addFirst(in);

if(buffer.size() > bufferSize) buffer.removeLast();

  

float sum = 0f;

for(int item : buffer) sum += item;

  

return Math.round(sum / buffer.size());

}

  

private class InputPort1 implements IRuntimeInputPort

{

public void receiveData(byte\[\] data)

{

int in = ConversionUtils.byteArrayToInt(data);

outputPort1.sendData(ConversionUtils.intToByteArray(addInt(in)));

}

}

  

private class OutputPort1 extends DefaultRuntimeOutputPort

{

@Override

public void sendData(byte\[\] data)

{

super.sendData(data);

}

}

}
```
  
  

Note that the implementation details above build upon the code which is generated by the AsTeRICS PluginCreationWizard tool. Specifically, the above methods belong to the class of the desired “Averager” plugin, which extends and implements the abstract class “AbstractRuntimeComponentInstance”. This class provides some standard implementation of the lifecycle support methods.

The implementations of the input and output ports implement or override that of the “IRuntimeInputPort” and “DefaultRuntimeOutputPort” respectively. In the first case, the “receiveData” method is overridden so that the input bytes are converted to an integer, then processed using the local, private method “addInt”, and finally delegated to the output port. The latter has actually no implementation. A dummy implementation is used to illustrated overriding the “sendData” method, although this could be avoided altogether.

The private method “addInt” realized the core functionality of the averager component. Finally, the get/set property value methods are implemented to allow for getting/setting the value of the “buffer-size” property, in a straightforward manner.

## Threading
    

For detailed information about the threading concept see 9.

## Writing plugins using Swing
    

If a plugin provides a Swing GUI it should only use the asynchronous method

SwingUtilities.invokeLater(…)

(and not the synchronous one) to perform the GUI updates. This is to prevent a potential thread deadlock if an action was originally triggered by a Swing GUI event e.g. by a button click in the ARE GUI. For detailed information about the ARE threading concept see 9.

## Long lasting method calls
    

If a method call performs a long lasting task and there is no need to await the termination of it, the task should be handed over to a worker thread (see 5.9) to not block ModelExecutor thread. For detailed information about the threading concept see 9.

## Sensor callbacks
    

In case you write a plugin that uses a separate thread to generate data (e.g. FrameGrabber, Timer,…) you should explicitly use the method

AstericsModelExecutionThreadPool.instance.execute(…)

This is to ensure that corresponding data will be delivered within the same task execution. For detailed information about the threading concept see 9. Below is an example of the FacetrackerLK plugin in the callback method for new arriving coordinates:

  
```java
public void newCoordinates\_callback(final int point1\_x,

final int point1\_y, final int point2\_x, final int point2\_y)

{

AstericsModelExecutionThreadPool.instance.execute(new Runnable() {

@Override

public void run() {

opNoseX.sendData(ConversionUtils.intToBytes(point1\_x));

opNoseY.sendData(ConversionUtils.intToBytes(point1\_y));

opChinX.sendData(ConversionUtils.intToBytes(point2\_x));

opChinY.sendData(ConversionUtils.intToBytes(point2\_y));

}

  

});

}
```
## Contributing a developed plugin (git pull request)
    

The AsTeRICS platform is designed as an open and modular platform. The idea is to make it easy for others to develop assistive plugins any end-user in the world could benefit from. Hence, we would love to get your contribution back to the github repository to be able to ship the new plugin with future releases. For this purpose, please send a pull request.

[https://help.github.com/articles/using-pull-requests/](https://help.github.com/articles/using-pull-requests/)

## Services and Utils: Infrastructure for plugins
    

The ARE Services are a set of classes that enable the direct interaction between AsTeRICS plugins and other software to directly interact with the runtime environment. The most significant ARE Services are:

*   CIM Communication Service: the ARE CIM Communication service layer is a unified approach to allow plugins of the ARE to communicate with their associated hardware modules attached to the AsTeRICS platform via a COM port. A range of hardware modules are provided which implement the dedicated Communication Interface Module (CIM) protocol. Further details on this communication protocol and implementation details for the ARE CIM Communication Service can be found in chapter 5.15.
    
*   Remote Connection Service: the remote connection services allows external software that cannot be integrated into the standard plugin inter communication system used by the ARE, for example because of programming language incompatibilities, to work with the AsTeRICS system. For example, the interconnection of OSKA (the On-Screen Keyboard Application developed by AsTeRICS partner SENSORY) and the ARE uses the Remote Connection Service to send key selection information to the ARE. On the other hand, the ARE can reply with cell selection commands or other information. The actual communication is done via a protocol that can be understood by the Java ServerSocket implementation. The port number that the external software component connects to identifies the connecting component.
    
*   Local Storage Service: The Local Storage Service will allow plugins to store individual working data “per model” and “per plugin-instance”. This is necessary when plugins need to store own calibration data, pattern recognition samples or similar data. In course of the architectural refinements for the final prototype, a service class will be provided which generates an according folder and respective file read- and write methods.
    
*   Native Hook Services for systemwide keyboard and mouse capturing
    
*   Computer Vision services to support a unified way for frame grabbing, computer vision processing and video frame rendering.
    
*   Logging service
    
*   ConversionUtils: Helper class to convert port data streams into Java data types and vice versa.
    
*   Class **ResourceRegistry** to fetch resource URIs. If a plugin needs access to a config, data or a multimedia file, the class ResourceRegistry must be used.
    

## Communicating with peripherals: CIM Communication service
    

Communication between actuator and sensor components in the ARE and peripheral devices is currently defined to use a serial communication i.e. a COM port or a virtual COM port. Messaging via this interface can either adhere to the CIM protocol (see section 5.15) or use any other protocol using the raw port implementation of the CIM communication services.

All the communication with peripheral devices is done through a service in the ARE service layer called CIM Communication. The service is provided as a separate OSGi bundle which places its classes in the package _eu.asterics.mw.services.cimcommunication_. Access to the classes is done by exporting the entire package in the bundle.

  
  

Four classes of the CIM Communication service are important to the component programmer:

*   CIMPortManager
    
*   CIMController
    
*   CIMProtocolPacket
    
*   CIMEventHandler
    

## CIMPortController
    

CIMPortController is an abstract class which hides the actual implementation of the port controller. The port controller provides the same methods for sending packets using the CIM protocol, for raw port implementations and for future uses such as a port controller handling Zigbee connections.

## CIMPortManager
    

All CIM ports and other COM ports are access through the main class of the package CIMPortManager. This is implemented as a singleton with a public access method getInstance(). Thus all calls to the CIM communication service have to be done through:

CIMPortManager.getInstance()

Upon creation the CIMPortManager detects all the connected CIMs and registers them in a HashMap. CIMs are identified and stored by the combination of their CIM Id and their unique number. Therefore multiple CIMs of the same CIM Id can be used on the AsTeRICS platform.

On some computers there exist certain serial ports which do not work correctly and behave strangely. An example of such a port is a loopback port which echoes everything written to it or ports created by Bluetooth dongles. Since the CIMPortManager iterates through all serial ports, these ports can cause problems in the auto detection of attached CIMs and even lock up the runtime. Therefore a file _ignore\_ports.txt_ in the directory _data/cimcommunication_ is parsed upon start of the auto detection. This file should be filled with the name of the COM ports behaving erratically one name per line.

To be able to communicate with a CIM, the CIM port manager provides several methods:
```java
**public** CIMPortController getConnection(**short** cimId)

**public** CIMPortController getConnection(**short** cimId, **long** uniqueNumber)
```
  
  

These methods return a CIMPortController (read on for details) instance of the requested CIM. The method using two parameters will return the instance to the port controller which works with the CIM of the exact CIM ID and unique number. If the CIM cannot be found, null will be returned.

  
  

Requesting a connection without naming a unique number will return the first port controller connected to a CIM of the correct ID found in the HashMap holding all the port controllers.

Sending data to the connected peripheral can be done in several ways using the following methods of CIMPortManager:
```java
**public**  **int**  sendPacket(**short** cimId, **byte** \[\] data,

**short** featureAddress, **short** requestCode, **boolean** crc)

  
  

**public**  **int** sendPacket(CIMUniqueIdentifier cuid, **byte** \[\] data,

**short** featureAddress, **short** requestCode, **boolean** crc)

  
  

**public**  **int** sendPacket(CIMPortController ctrl, **byte** \[\] data,

**short** featureAddress, **short** requestCode, **boolean** crc)
```
  
  

Basically these three methods do the same thing, however they do it at different speeds as the first two methods will look up the port controller that the packet should be sent to. Again the method taking only the CIM ID as a parameter will look up the first correct port controller. The third method which is passed the CIMPortController instance returned on getConnection() is the fastest method and should be used whenever possible.

Sending a CIM packet is done by providing the feature address and request code for a certain packet. The feature addresses and request codes can be found in the CIM protocol specification and the basic addresses and requests are also provided as static fields in the CIMProtocolPacket class. If data has to be attached to a CIM protocol packet a byte array holding said data has to be passed to the method, otherwise the data parameter of the method has to be set to null. The caller can also decide whether a CRC checksum should be added to the packet although this is currently unimplemented.

## CIMEventHandler
    

Receiving a packet is done through use of the CIMEventHandler interface. This interface should be implemented by plugins that wish to communicate with CIMs (or raw ports). The interface contains two methods:
```java
**public**  **void** handlePacketReceived(CIMEvent e);

**public**  **void** handlePacketError(CIMEvent e);
```
These methods are called upon correct reception of a packet or upon discovery of an error (timeout of a reply, packet transmission errors, incorrect order of incoming packets …) respectively by the port controller.

Upon correct reception of a CIM protocol based packet the method handlePacketReceived() is called with an instance of CIMEventPacketReceived as parameter. After conversion of the CIMEvent to this class, the packet can be extracted from the event and processed further.

All detected errors lead to a call of handlePacketError() with an appropriate CIMEvent implementation. The possible implemenations are:

*   CIMEventErrorPacketFault: holds information to error in packet and the broken packet itself
    
*   CIMEventErrorPacketLost: holds information on serial number of lost packet
    

  
  

To register the event handler with a specific CIM port controller, the CIMPortController class exposes the following methods:

*   addEventHandler(CIMEventHandler hdlr)
    
*   removeEventHandler(CIMEventHandler hdlr)
    

A port controller can handle multiple attached event handlers and remove each one separately.

## CIMProtocolPacket
    

This class holds all the information given in a packet transferred to or from a CIM. There are two ways the developer has to use this class. Upon sending packets the sending component has to set the feature address and the request code. The CIMProtocolPacket class provides the constants as static field to facilitate setting commands.
```java
**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_FEATURE\_LIST = 0x00;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_FEATURE\_LIST = 0x01;

**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_WRITE\_FEATURE = 0x10;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_WRITE\_FEATURE = 0x10;

**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_READ\_FEATURE = 0x11;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_READ\_FEATURE = 0x11;

  

**public**  **final**  **static**  **byte** COMMAND\_EVENT\_REPLY = 0x20;

  

**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_RESET\_CIM = (**byte**) 0x80;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_RESET\_CIM = (**byte**) 0x80;

**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_START\_CIM = (**byte**) 0x81;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_START\_CIM = (**byte**) 0x81;

**public**  **final**  **static**  **byte** COMMAND\_REQUEST\_STOP\_CIM = (**byte**) 0x82;

**public**  **final**  **static**  **byte** COMMAND\_REPLY\_STOP\_CIM = (**byte**) 0x82;
```
  

Furthermore the class contains constants for the global features that every CIM has to provide.
```java
**public**  **static**  **final**  **short** FEATURE\_UNIQUE\_SERIAL\_NUMBER = 0;
```
Upon reception of an incoming packet the component associated with the CIM sending the packet is notified and a reference to the packet is passed as an instance of CIMProtocolPacket wrapped in a CIMEvent instance. The developer can access all the fields of the packet via the getter methods the class provides:
```java
**public**  **short** getAreCimID()

**public**  **byte** getSerialNumber()

**public**  **short** getFeatureAddress()

**public**  **short** getRequestReplyCode()

**public**  **byte**\[\] getData()

**public**  **int** getCrc()
```
  
  

## Serial ports not adhering to CIM Protocol (Raw Ports)
    

Some peripherals use a proprietary protocol to transfer their data. If this is the case the user can open a raw port through the CIMPortManager method:
```java
**public** CIMPortController getRawConnection(String portName, **int** baudRate)
```
This will open the port with the name specified in the parameter portName and set the communication to the specified Baud rate.

Data can be sent to peripheral using the sendPacket() method for the returned CIMPortController. The packet will simply transfer the byte array passed in the data parameter and ignore the values giving the in the other parameter fields.

Received data will be forwarded to the event handler through calls to handlePacketReceived() with a CIMEventRawPacket as parameter. This class holds a public member variable b which holds the value of the received byte. The event handler has to handle the reconstruction of the proprietary packet itself.

**HighSpeed Raw Ports:**
```java
**public** CIMPortController getRawConnection(String portName, **int** baudRate, **boolean** highSpeed)
```
  
  

A second variant opf the getRawConnection method allows specification of a “highSpeed” parameter. If highSpeed is true, the CIMPortController does not apply any connection handling or callbacks for received data to avoid performance problems in higher bandwidth streaming use cases. In this case, the CIMPortController can return the JAVA InputStream for the openend COM port connection and the plug developer can use it as desired:
```java
portController = CIMPortManager._getInstance_().getRawConnection(”COM12”,115200,**true**);

in = portController.getInputStream();

**if** (in.available() > 100) myHandlePacket ((**byte**) in.read());
```
  

## Communication through a socket interface: Remote Connection Manager
    

When using third party software that runs on the same platform (as for example the prominently used On Screen Keyboard Application OSKA), it becomes necessary to establish a communication between ARE and the third party application. This is managed by the RemoteConnectionManager found in the package eu.asterics.mw.services. The main interface to this manager are the classes RemoteConnectionManager and IRemoteConnectionListener.

## IRemoteConnectionListener
    

This interface is implemented by plugins that need to communicate via a socket communication. The interface contains the following methods:
```java
**void** connectionEstablished();

**void** dataReceived(**byte** \[\] data);

**void** connectionLost();

**void** connectionClosed();
```
  
connectionEstablished() is called whenever a plugin requests a connection and the connection has been established. This can either happen if a connection has already been established before or if the new connection has finished its setup and connection process.

dataReceived() is called whenever new data arrives from the other end of the connection. Data is transferred in a byte array and has to be processed by the event listener.

connectionLost() is called when the connection management cannot read from or write to the socket.

connectionClosed() is called after the connection has been closed.

## RemoteConnectionManager
    

The RemoteConnectionManager is implemented as a singleton and can be accessed via a public static member of the class. Thus access is always achieved through:
```java
RemoteConnectionManager._instance_
```
  

A connection is opened by a call the RemoteConnectionManager’s method:
```java
**boolean**  requestConnection (String port, IRemoteConnectionListener l)
```
  
This call will try to access a connection on the specified port. Although the port is actually an integer it is passed as a String here. The method will return true if a connection on this port has already been established and attach the remote connection listener passed in the second argument to the connection. If there is no active connection on the specified port, the requestConnection method will initiate the setup of the connection and return false. With this return value the user can decide whether he needs to perform setup actions or will be able to do this in the connectionEstablished() callback.

The socket connection handling is implemented using two threads, one for sending, one for receiving data. The receiver thread will continuously read data from the socket and forward it to the registered listener calling the dataReceived() method. Since incoming data is handled in another thread than the plugin which will use the socket connection, access to the methods handling this data or the way of passing data should be done in a synchronised code block.

Sending data is done calling the method sendData of RemoteConnectionManager:
```java
**public**  **boolean**  writeData(String port, **byte**\[\] data)
```
  
This method is called using a String holding the port number of the connection socket and an array of bytes to be sent. The call to this method will place the data in an outgoing queue and return true if this was successful. Thus it is not guaranteed that the data has already been sent when the method returns. The sender thread will grab data from the outgoing queue and transfer it via the socket or call the connectionLost() method of the registered listener if there are problems while sending.

Once the connection to a socket is not needed anymore, the user has to close the connection, calling the following method of RemoteConnectionManager:
```java
**public**  **void** closeConnection(String port)
```
  
  

This will close the socket connection, end all threads and return.

## Local Storage Service
    

If a model needs to save its own calibration data, training data or other private data that can be different in every model and every instance, the local storage service provides a method to save different data to the same file name on a per plugin instance per model basis.

The service uses a directory tree structure that is placed in the directory the OSGi is run from. Data is saved in a directory called “storage”. In this directory, directories for every model name of a model that uses at least one plugin that accesses local storage can be found. In the third directory layer, directories with the plugin instance name of every plugin that accesses local storage can be found. Thus if a model named “timertest” uses a plugin instance named “timer1” that saves local data this data can be found at the path location “storage/timertest/timer1”.

The service practically consists of only one method:
```java
**public** File getLocalStorageFile(IRuntimeComponentInstance component, String fileName)
```
  
  

Calling this method located in the AREServices class will return a File object pointing to the requested file name or null if the file could not be opened or the model name could not be retrieved. After opening the file the standard JAVA ways to manipulate files apply.

## Keyboard/Mouse Native Hook Services
    

The AsTeRICS service jnativehook (if enabled) provides access to the library [https://github.com/kwhat/jnativehook](https://github.com/kwhat/jnativehook).

Additionally, the service contains the singleton NativeHookServices that initializes the library to be usable in plugins. A plugin that wants to be a keaboard/mouse listener only has to add the listener to the GlobalScreen instance of the library.

## Computer Vision Services
    

The AsTeRICS services javacv and computervision (if enabled) provide access to the library [https://github.com/bytedeco/javacv](https://github.com/bytedeco/javacv), which again provides access to numerous libs in the field of computer vision (e.g. OpenCV), frame grabbing and frame rendering. Additionally, some helper classes are provided for face detection.

  
  

  
  

## Data Conversion Utilities
    

The middleware provides the class “ConversionUtils” that provide static helper methods to convert model data types to byte arrays and vice versa. The conversion is needed to convert incoming data of input ports or outgoing data to output ports. There are methods for each type of conversion. If two connected ports (output to input) have different data types the data is automatically converted to the data type of the input port.

Check the class ConversionUtils for a full list of supported methods: [https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/data/ConversionUtils.java](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/data/ConversionUtils.java)

## Fetching resource URIs with ResourceRegistry class
    

If a plugin needs resources like data files (images, keyboard files, haarcascade definitions,…), the class [ResourceRegistry](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java) must be used to fetch the resource URI.

This can be done by using the method:

public URI getResource(String resourcePath, RES\_TYPE type) throws URISyntaxException

  
  

Returns the URI according to the given resourcePath string and the given resource type RES\_TYPE.

Please refer to the Javadoc of the class and to the [wiki page](https://github.com/asterics/AsTeRICS/wiki/Fetching-resources-with-class-ResourceRegistry) for further examples.

## Logging
    

The Logging support provides a uniform way of error reporting in the runtime environment so we have utilized the Java logging libraries and the various severity levels supported. The AsTeRICS error handling mechanism is used extensively from the runtime core classes but also utilized by the AsTeRICS components via the AstericsErrorHandling interface.

Each component is allowed to report an error message, a debug information or a simple information to be displayed on the screen. The ARE maintains four separate log files and updates them whenever a new error occurs. In particular there are different loggers for reporting severe errors, warnings, fine errors and one logger that contains them all.

ARE also maintains a status object for the current status of the runtime environment. Whenever a fatal error occurs (either internally or caused by one of the deployed components) the status changes to fatal error. Other possible statuses are unknown, OK, deployed, running and paused.

The ACS can request the current status of the runtime environment and update its own state accordingly. For example the ACS user can be informed about the current ARE status while the ACS will terminate a connection (or refuse to establish a new one) with a non-working ARE.

Using a Logger is the recommended way to report notifications or error descriptions to the user. In the ARE framework, using the Java logging service is recommended. The Java logger can be configured using the file “logging.properties” (see section 2.3.3.1) and used as follows:

  
```java
**import** java.util.logging.Logger;

(…)

Logger.getAnonymousLogger().info("Component started ");
```
  

  

  
  

ARE provides a unified logging and error reporting mechanism. The AstericsErrorHandling class provides 4 types of loggers to be used by the ARE, deployed components and the ACS. It also provides methods for status checking which are responsible for monitoring the current status of the ARE and deployed Components.

The 4 different loggers correspond to different severity levels as follows:

Level _severe_: only severe errors are logged. Such errors cause an ARE failure and must be addressed immediately. Severe loggers should be used only by ARE. Errors of this type will be written in the “asterics\_logger\_severe.log” file.

Level _warning_: only warnings and upper level messages are logged. Warnings are important and must be addressed soon but not as fatal as the severe errors. Warnings can be logged by components using the following method call:

  
```java
public void reportError(IRuntimeComponentInstance component, String errorMsg)
```
  
The messages will be written in the “asterics\_logger\_warning.log” file.

Level _info_: only informative and upper level messages are logged. Use this logger when you normally wanted to print something on the screen.

  
```java
public void reportInfo(IRuntimeComponentInstance component, String info)
```
The messages will be written in the “asterics\_logger.log” file.

Level _fine_: only debug and upper level messages are logged. Usage of this logger is mainly for debugging or development time. Use the following command:

  
```java
public void reportInfo(IRuntimeComponentInstance component, String info)
```
  
  

The messages will be written in the “asterics\_logger\_fine.log” file.

Please note that each logger by default also logs all messages with severity level higher than its own as well. E.g. the warning logger logs warning and severe messages, the info logger logs informative, warning and severe messages etc.

## Status checking
    

The status checking mechanism is responsible for recording the current status of the ARE or the error state of a component. The status is recorded by creating and storing objects called _statusObjects_. A statusObject stores the status of its creator as a string, its creator (the ARE or the specific component) and the error message.

  

public static void setStatusObject(String status, String componentID,

String errorMsg)

  
  

The status of the ARE can be one of the following strings:

UNKNOWN: initial state for the ARE

OK: ARE is running and ready to deploy a model

DEPLOYED: A model has been deployed and the ARE is now ready to run the model

RUNNING: A model is running on the ARE

PAUSED: A model has been deployed and the ARE is in paused mode

ERROR: An error occurred

FATAL\_ERROR: A fatal error occurred, model or deployment aborted

The status of a component can only be the ERROR state because this is the only state of a component that we are interested in recording for later use. An ERROR statusObject is automatically created when a component calls the reportError method as described above.

For retrieving the statusObjects, the following method is used:

  
```java
public StatusObject\[\] queryStatus(boolean fullList)
```
  
  

This method is particularly useful for the ACS to determine the current status of the runtime environment and of the deployed components. If the ARE or one of the components are in a problematic state it can be reflected in the ACS.

The boolean _fullList_ argument  specifies whether the error list to be returned will include all statusObjects generated since the ARE startup or just those that have not been requested by the ACS before.

## The ARE Thread Pool
    

In order to avoid resource greedy threads and to achieve best thread handling, ARE uses one of the Thread Pool implementations provided by Java since JRE 1.5. In particular, we have utilized the java.util.concurrent.Executors library for creating a CachedThreadPool.

A cached thread pool will create threads as needed but will reuse previously instantiated threads when they are available and inactive. A cached thread pool is particularly useful for many short-lived asynchronous tasks and improves the performance of the runtime environment.

Developers are expected to use the ARE thread pool for executing their tasks that require a new Thread. You will need to import the middleware services package in order to get access to the **AstericsThreadPool** class.

## The ARE GUI support
    

The ARE provides a panel area (“ARE Desktop”) where plugins can display their graphical elements using the ARE GUI support classes. The ACS provides a dedicated canvas editor that allows end users positioning and resizing graphical elements of the plugins. Based on this information, the ARE displays plugins on the local device, maintaining the correct screen position and aspect ratio of graphical elements with respect to the screen resolution of the deployment device. (For more information about the usage of the ACS GUI editor and the ARE GUI control panel refer to the User Manual.)

![](./DeveloperManual_html_3e993efbc73f2910.png)

**GUI composition using the ACS GUI designer (right), resulting ARE GUI (left)**

  
  

In order to be recognized as GUI-plugin by ACS and ARE, the bundle descriptor of the plugin has to be extended with a dedicated <gui> entry, which specifies the default size in a virtual coordinate system of 10000/10000 pixels. In the deployment model, the ACS will create position and size information according to the area defined in the ACS GUI designer.

  
  
```xml
<gui>

<width>5000</width>

<height>3000</height>

</gui>
```
**The gui element addition to the bundle descriptor**

  
  

The AsTeRICS middleware provides some services to the plugin developers in order to allow them displaying their GUI element onto the ARE Desktop. The middleware services encapsulate the complexity of dealing with positioning and allow displaying all GUI elements onto the same container: the ARE Desktop.

All GUI services are defined in eu.asterics.mw.services.AREServices so developers need to import this class in order to get access to the following methods:

*   _void displayPanel (JPanel panel, IRuntimeComponentInstance componentInstance, boolean display)_
    

This method is used for displaying (or hiding) a plugin’s panel at/from the ARE desktop. Developers need to pass

*   the panel they want to be displayed (or removed)
    
*   the plugin object, in order to help the middleware finding the desired position and dimensions from the deployment model
    
*   a boolean argument specifying if they wish to hide or show the given panel.
    

*   _Dimension getAvailableSpace(IRuntimeComponentInstance componentInstance)_
    

The space that each plugin will occupy on the ARE desktop is defined by the designer on the ACS and passed to the ARE via ASAPI. Plugin developers can get the available space for their graphical elements by calling the getAvailableSpace method which will return the space occupied for the plugin object passed as argument.

*   _Point getComponentPosition (IRuntimeComponentInstance componentInstance)_
    

The positioning of plugin’s GUI elements is defined by the designer on the ACS and passed to the ARE via ASAPI. Plugin developers can get the position of their graphical elements by calling the getComponentPosition which will return the position on screen for the plugin object passed as argument.

*   _void adjustFonts(JPanel panel, int maxFontSize, int minFontSize, int offset)_
    

This service can be used by plugin developers interested in auto-adjusting the fonts of their GUI components depending on the space occupied for their plugins on the ARE desktop. They need to pass

*   a panel to which all the internal fonts will be auto-adjusted
    
*   the maximum font size (in case there is more space available than needed)
    
*   the minimum font size, in case there is too little space which causes the text to become non-readable. Finally, the offset argument is used in case we want to occupy a percentage of the available space.
    

A good approach to GUI plugin development is to analyse existing plugins which provide GUI elements, e.g. the BarDisplay or Oscilloscope actuators, or the Slider or Cellboard sensor components.

If the plugin uses **Swing to implement a GUI-widget** (e.g. Slider,…), please check the following guidelines as well 4.2.8

  
  

## ARE core events notification services
    

The ARE core events notification service allows plugins to register/unregister to the ARE middleware in order to receive notifications of ARE core events.

*   _void registerAREEventListener(IAREEventListener clazz)_
    

It is sometimes necessary that plugins can be notified of various ARE events so they can react as needed. This method can be called by component instances that wish to be notified of such ARE events. Currently, the core events supported are:

*   _preDeployModel:_ registered ARE event listeners will be notified just before the deployment of a model.
    
*   _postDeployModel:_ registered ARE event listeners will be notified immediately after the deployment of a model.
    
*   _preStartModel:_ registered ARE event listeners will be notified just before the currently deployed model is started.
    
*   _postStartModel:_ registered ARE event listeners will be notified immediately after the currently deployed model has been started.
    
*   _preStopModel:_ registered ARE event listeners will be notified just before the currently deployed model is stopped.
    
*   _postStopModel:_ registered ARE event listeners will be notified immediately after the deployed model has been stopped.
    
*   _prePauseModel:_ registered ARE event listeners will be notified just before the currently deployed model is paused.
    
*   _postPauseModel:_ registered ARE event listeners will be notified immediately after the deployed model has been paused.
    
*   _preResumeModel:_ registered ARE event listeners will be notified just before the currently deployed model is resumed.
    
*   _postResumeModel:_ registered ARE event listeners will be notified immediately after the deployed model has been resumed.
    
*   _preBundlesInstalled:_ registered ARE event listeners will be notified just before the bundles are is installed.
    
*   _postBundlesInstalled:_ registered ARE event listeners will be notified immediately after the bundles have been installed.
    

*   _void unregisterAREEventListener(IAREEventListener clazz)_
    

Plugins already registered for receiving ARE core events can un-register using this method.

## Dynamic Properties
    

In some applications, the ACS should be able to provide several options for property values which are not known in advance but depend on the current state of the ARE (see AsTeRUCS User Manual, section “Dynamic Properties”).. A typical example is the selection of a file which is available in the ARE file system (e.g. a .wav-file for the wave player plugin). This feature is particularly useful for plugins that are hardware dependent (selecting e.g. a soundcard or a midi player), or depend on the file system.

If a plugin is implementing a dynamic property, the values will be requested from the ARE, as soon as the ACS is synchronized with the ARE, via the ASAPI function:

_List<String>_  _getRuntimePropertyList(String componentID, String key)._

The ARE middleware will forward the request for valid property values to the component instance with the given ID. The _List<String> getRuntimePropertyList(String key)_ method has to be implemented in the _AbstractRuntimeComponentInstance_ class which every AsTeRICS component extends.

The method implementation creates the list of valid properties and returns it to the middleware and the latter forwards the string list to the ACS via ASAPI. The ACS will dynamically update the property list in the properties window.

For an example of the dynamic property implementation, see the WaveFilePlayer plugin.

## Data Synchronization
    

Some plugins need data of multiple input ports to be able to start processing. Without data synchronization it is possible that one input port of a plugin receives multiple values before another port gets one value, although both signal channels deliver values at the same sampling rate.

The synchronization service provides a buffering mechanism at the middleware level that can be utilized by plugin developers in order to make sure that incoming data of selected input ports arrives synchronized.

To use the synchronization service in the plugin code, plugin developers are expected to extend the _DefaultRuntimeInputPort_ instead of implementing the _IRuntimeInputPort._ Basically, _DefaultRuntimeInputPort_ provides a default implementation for the necessary buffering methods, as shown in the table below.
```java
**public**  **abstract**  **class**  DefaultRuntimeInputPort  **implements** IRuntimeInputPort {

  

**private**  **boolean**  buffering;

**public**  **void** receiveData(**final**  **byte** \[\] data) {

;

}

**public**  **void** startBuffering (AbstractRuntimeComponentInstance c,

String portID) {

**this**.buffering = **true**;

}

**public**  **void** stopBuffering (AbstractRuntimeComponentInstance c,

String portID) {

**this**.buffering = **false**;

}

**public**  **boolean** isBuffered () {**return**  **this**.buffering;}

}
```
  
  

The designer can define that a plugin's input port should be synchronized with some other input ports via the ACS. This will cause an argument change of the inputPort element on the deployment model file (e.g.,<inputPort portTypeID="inB" **sync="true"**\>).

As soon as a model is deployed on the ARE, the middleware collects per component every port noted as synchronized port. When the model is successfully deployed and started, the ARE will buffer data which enters synchronized input ports until data on all synchronized ports has arrived. At that point, the ARE will call a new _AbstractRuntimeComponentInstance_  callback method.

Developers that wish to support data synchronization need to implement the following method at their component instances.

_public void syncedValuesReceived(HashMap<String, byte\[\]> dataRow)_

Where dataRow is a HashMap between Input Port ID and byte\[\]. For synchronized input ports, instead of implementing the regular _void receiveData(byte\[\] data)_ method which delivers incoming data of a single port, developers need to implement the _syncedValuesReceived_  method which will be called from the ARE with synchronized data from all the input ports that have been selected.

  
  

## Interfacing Native C/C++ Code via JNI
    
    ## Specifying native libraries in the Manifest
        

The Manifest file of a bundle which includes native libraries has to specify these .dlls as shown in the following example:

  
```
Manifest-Version: 1.0

Bundle-ManifestVersion: 2

Bundle-Name: asterics\-sensors.mycomponent

Bundle-SymbolicName: org.asterics.mycomponent

Bundle-Version: 0.1.0

Bundle-NativeCode: lib/native/mylib1.dll;

lib/native/mylib2.dll;

lib/native/mylib3.dll;

osname\=win32;processor=x86;

osname\=win;processor=x86-64;

osname\=win8;processor=x86;

osname\=win8;processor=x86-64;

osname\=windows 8;processor=x86;

osname\=windows 8;processor=x86-64;

osname\=windows8;processor=x86;

osname\=windows8;processor=x86-64;

osname=Windows 8.1;processor=x86;

osname=Windows 8.1;processor=x86-64

DynamicImport-Package: \*
```
  

  
  

Components which interface native code via JNI and their respective manifest files can be found in the SVN, e.g. the “webcamera” component or the signal processing plugins by Starlab.  
  
Note that the .jar containing the .dlls can be built manually using the command:

  
```bash
jar -cvfm ../mybundle.jar META-INF/MANIFEST.MF .
```
  
  

## Java-Implementation: JNI-Bridge
    

The recommended way to interface Java code of an ARE component with native code in a .dll is a bridge class which encapsulates the JNI functions and callbacks and maps the functions of the ARE component’s lifecycle-, port- and property-management to the corresponding functions in the native code. Here is a simple example which comprises one component property and receives data callbacks from a thread implemented in C. The received values are transferred to the component’s output port:

  
```java
**package** org.asterics.jni;

**import** org.asterics.mycomponent.MyComponentInstance;

**import** java.util.logging.Logger;

  

**public**  **class** Bridge

{

/\* Statically load the native library \*/

**static**

{

System.loadLibrary("mylib1"); // loads mylib1.dll

System.loadLibrary("mylib2"); // loads mylib2.dll

System.loadLibrary("mylib3"); // loads mylib3.dll

}

  

**private**  **static**  **final** Logger logger = Logger.getAnonymousLogger();

**private**  **final** MyComponentInstance.OutputPort my\_outport;

  

**public** Bridge(**final** MyComponentInstance.OutputPort my\_outport)

{

**this**.my\_outport = my\_outport;

}

/\*\*

\* Activates the underlying native code/hardware.

\*

\* **@return** 0 if everything was OK, a negative number otherwise

\*/

**native**  **public**  **int** activate();

/\*\*

\* Deactivates the underlying native code/hardware.

\*

\* **@return** 0 if everything was OK, a negative number otherwise

\*/

**native**  **public**  **int** deactivate();

/\*\*

\* Gets the value of the named property.

\*

\* **@param** key the name of the property to be accessed

\* **@return** the value of the named property

\*/

**native**  **public** String getProperty(String key);

/\*\*

\* Sets the named property to the defined value.

\*

\* **@param** key the name of the property to be accessed

\* **@param** value the value to be assigned to the named property

\* **@return** the value previously assigned to the named property

\*/

**native**  **public** String setProperty(String key, **final** String value);

/\*\*

\* This method is called back from the native code on demand to signify

\* an internal error. The first argument corresponds to an error code

\* and the second argument corresponds to a textual description

\* of the error.

\* **@param** errorCode an error code

\* **@param** message a textual description of the error

\*/

**private**  **void** errorReport\_callback(

**final**  **int** errorCode,

**final** String message)

{

logger.severe(errorCode + ": " + message);

}

/\*\*

\* This method is called back from the native code to send data

\* to the component’s output port.

\*

\* **@param** data1 (range is \[0, Short.MAX\_VALUE\])

\*/

**private**  **void** newData\_callback(**final**  **int** data1)

{

my\_outport.sendData(data1);

}

}
```
## C-Implementation: Callbacks and JNI code
    

The native C-code needs to be compiled into a .dll and include the JNI header files and libraries. An example for the Microsoft Visual Studio compiler looks as follows:

The following C-example shows how to implement a JNI-callback from a C-thread and an ARE-compliant exchange of a component property:

  
```c
#include  <jni.h>

  

static JavaVM \* g\_jvm;

static jobject g\_obj = NULL;

  

const  char \* propertyKey = "myProperty";

const  char \* propertyValue = "20";

  

  

JNIEXPORT jint JNICALL Java\_org\_asterics\_jni\_Bridge\_activate

(JNIEnv \* env, jobject obj)

{

jint error\_code = 0;

error\_code = env->GetJavaVM(&g\_jvm);

if(error\_code != 0)

{

return error\_code;

}

jclass cls = env->GetObjectClass(obj);

jmethodID mid = env->GetMethodID(cls, "newData\_callback", "(IIII)V");

if (mid == NULL) return -1; /\* method not found \*/

// explicitly ask for a global reference

g\_obj = env->NewGlobalRef(obj);

  

my\_c\_thread\_init();

return error\_code;

}

  

JNIEXPORT jint JNICALL Java\_org\_asterics\_jni\_Bridge\_deactivate  
(JNIEnv \* env, jobject obj)

{

jint error\_code = 0;

my\_c\_thread\_exit();

env->DeleteGlobalRef(g\_obj);

return error\_code;

}

  

JNIEXPORT jstring JNICALL Java\_org\_asterics\_jni\_Bridge\_getProperty

(JNIEnv \*env, jobject obj, jstring key)

{

const  char \*strKey;

jstring result;

if (key == NULL) return NULL; /\* OutOfMemoryError already thrown\*/

strKey = env->GetStringUTFChars(key, NULL);

  

if(strcmp(propertyKey, strKey) == 0)

{

result = env->NewStringUTF(propertyValue);

}

else

{

result = NULL; /\* property was not found \*/

}

env->ReleaseStringUTFChars(key, strKey);

return result;

}

  

JNIEXPORT jstring JNICALL Java\_org\_asterics\_jni\_Bridge\_setProperty

(JNIEnv \*env, jobject obj, jstring key, jstring value)

{

const  char \*strKey;

const  char \*strValue;

jstring result;

if (key == NULL) return NULL; /\* OutOfMemoryError already thrown\*/

strKey = env->GetStringUTFChars(key, NULL);

  

if (value == NULL) return NULL; /\* OutOfMemoryError already thrown \*/

strValue = env->GetStringUTFChars(value, NULL);

  

if(strcmp(propertyKey, strKey) == 0)

{

result = env->NewStringUTF(propertyValue);

pollingIntervalValue = strValue;

}

else

{

result = NULL; /\* property was not found \*/

}

  

env->ReleaseStringUTFChars(key, strKey);

env->ReleaseStringUTFChars(value, strValue);

return result;

}

  

// prepare JNI callback

JNIEnv \*env;

g\_jvm->AttachCurrentThread((void \*\*)&env, NULL);

jclass cls = env->GetObjectClass(g\_obj);

jmethodID mid = env->GetMethodID(cls, "newCoordinates\_callback", "(IIII)V");

  

// perform JNI callback

env->CallVoidMethod((jint)my\_new\_data);
```
  

  
This native C-code needs to be compiled into a .dll, the JNI header files and libraries have to be specified to the compiler and linker respectively. An example for the Microsoft Visual Studio build tools looks as follows:

  
```
cl -c -I "C:\\Program Files (x86)\\java\\jdk1.6.0\_21\\include" -I "C:\\Program Files (x86)\\java\\jdk1.6.0\_21\\include\\win32" -I ".\\3rdparylib" my\_c\_file.cpp /ZI /nologo /W3 /WX- /Od /Oy- /D "WIN32" /D "\_DEBUG" /D "\_WINDOWS" /D "\_CRT\_SECURE\_NO\_WARNINGS" /D "\_VC80\_UPGRADE=0x0710" /D "\_MBCS" /Gm- /EHsc /RTC1 /MTd /GS /fp:precise /Zc:wchar\_t /Zc:forScope /Gd /analyze- /errorReport:queue

link my\_c\_file.obj /DLL /OUT:".\\my\_c\_file.dll" /INCREMENTAL:NO /NOLOGO /LIBPATH:"libmsvc" /LIBPATH:"3rdparylib" "odbc32.lib" "odbccp32.lib" "comctl32.lib" "winmm.lib" "opengl32.lib" "ole32.lib" "strmiids.lib" "uuid.lib" "kernel32.lib" "user32.lib" "gdi32.lib" "winspool.lib" "comdlg32.lib" "advapi32.lib" "shell32.lib" "oleaut32.lib" /NODEFAULTLIB:"libcd.lib" /NODEFAULTLIB:"atlthunk" /NODEFAULTLIB:"LIBCMT" /MANIFESTUAC:"level='asInvoker' uiAccess='false'" /DEBUG /SUBSYSTEM:WINDOWS /TLBID:1 /DYNAMICBASE:NO /MACHINE:X86 /ERRORREPORT:QUEUE
```
  
Note that the compiler and linker switches may differ depending on the nature of your dependency libraries and setup.

  
  

## External Helper Applications and Tools for Plugins
    

Some pluings make use of external helper applications which are self-contained binary executable files and communicate with the particular ARE plugin (usually via a socket interface). These helper applications are stored in the folder ARE/tools.

Currently, the following plugins use external tools:

  

**Plugin Name**

**Helper Application and Purpose**

**Interface**

SpeechProcessor

_Tools/SpeechProcessor.exe_

This application creates an instance of the Microsoft speech server for speech synthesis and recognition. It accepts a list of commands from the plugin and sends back recognized voice commands.

TCP/IP socket

OscGestureFollower

_Tools/GestureFollower/gfOSC\_v1.4.exe_

The GestureFollower algorithm by IRCAM. Allows training and recognition of several signal patterns (for example from multichannel sensor data).

TCP/IP socket, Open Sound control (OSC) protocol

FaceTrackerCLM

_Tools/EyesStateTrainer/EyesStateDetect.exe  
Tools/EyesStateTrainer/EyesStateRecord.exe  
Tools/EyesStateTrainer/EyesStateTrain.exe_

Training application for detection of open or closed eyes of a user for application with the FaceTrackerCLM plugin. The training process is exaplained in the Model Guide for the FaceTrackerCLM plugin.

Offline, interface via file

SSVEPStimulator

_Tools/blit.exe_

Creates a flickering images from a given bitmap file and it’s x/y coordinates on the desktop screen for Software-generated visual stimulation in SSVEP BCI tasks.

Commandline parameters to the application call

SSVEPFileWriter  
ProtocolSSVEPTrain SSVEPDetect

_Tools/SSVEPTrainFunction.exe_

Finds significant frequencies in an SSVEP training recording which has been generated by the SSVEPFileWriter plugin

Offline, interface via file:

  

  
  

  

## AsTeRICS Packaging Environment (APE)
    

The APE let's you select a set of AsTeRICS model files and create a downstripped (minimum size) version of the ARE including plugins, configuration files and data files to execute the models. Optionally, the APE allows the creation of native installers for Windows, Linux incl. Raspberry Pi and Mac OSX using [JavaFX packaging technology](http://docs.oracle.com/javase/8/docs/technotes/guides/deploy/self-contained-packaging.html#BCGIBBCI).

See [APE documentation](https://github.com/asterics/AsTeRICS/wiki/AsTeRICS-Packaging-Environment-(APE)).

