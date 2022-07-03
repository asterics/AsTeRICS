# Objective
In this tutorial you will learn how to use the class ResourceRegistry, which is a central repository that must be used to compose resource URIs and fetch and store resource contents from within plugins and the whole ARE.

# Introduction
The idea is to generically implement the fetching of resources to enable the same approach for the whole AsTeRICS framework. This way all plugins, services and other classes will be able to also support several URI schemes (e.g. file, http, jar,...). Furthermore base URIs can be reconfigured depending on platform specific or usecase specific requirements (e.g. readonly plugin respository hosted on a webserver). Currently only one file based repository URI (```ARE baseURI```) is supported. Later maybe the repository URIs could also be an http-URL and the plugin resources directly fetched from there.

The ```ARE baseURI``` is set to the location of the ARE.jar file by default, but can be set to another location by the method [```public void setAREBaseURI(URI areBaseURI)```](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java#L852) programmatically.

## Main benefits of class ResourceRegistry
* Abstraction of ARE folder structure: So if folder structure changes models are not affected.
* Centralized dealing with platform specific problems (```\```, ```/```, conversion between URI encoded path and file path,...)
* Dealing with relative and absolute file paths.
* By using URI syntax for describing the path of a resource, resources can be of different protocol types (file, http, ...)
* Many utility methods for checking resource existence and conversion between several types (File, String, URI,...)
* Convinience methods for fetching resource contents or storing contents, ensuring proper encoding (UTF-8) and exception handling.
* Centralized implementation of searching strategies for resources, e.g. Search in user home directory first and if not found search in ARE installation directory. (not supported yet)
* Configurable base URIs depending on resource type (not supported yet)


## Resource types
As part of the abstraction, class ResourceRegistry provides several resource types ([RES_TYPE enum](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java#L482)), which are then mapped to real folders (or maybe later to http-URLs depending on the supported ARE baseURI protocols).

As of AsTeRICS 3.0, these are:
* **MODEL**: Mapped to ```<ARE baseURI>/models/```
* **DATA**: Four step approach to search for a data file in either ```<ARE baseURI>/models/``` or ```<ARE baseURI>/data/``` or a subfolder of it.
* **JAR**: Mapped to ```<ARE baseURI>/```
* **PROFILE**: Mapped to ```<ARE baseURI>/profile/```
* **STORAGE**: Mapped to ```<ARE baseURI>/storage/```
* **LICENSE**: Mapped to ```<ARE baseURI>/LICENSE/```
* **IMAGE**: Mapped to ```<ARE baseURI>/images/```
* **TMP**: Mapped to ```<ARE baseURI>/tmp/```
* **WEB_DOCUMENT_ROOT**: Mapped to ```<ARE baseURI>/web/```
* **ANY**: Mapped to ```<ARE baseURI>/```

# Prerequisites
* [AsTeRICS 3.0 installed](https://github.com/asterics/AsTeRICS/releases/tag/v3.0)
* Java IDE ([Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/neon3) recommended)
* [Java Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

# Preparation
This tutorial demonstrates how to use the API of class ResourceRegistry in general. The tutorial does not show how to create an [AsTeRICS plugin](https://github.com/asterics/AsTeRICS/wiki/Plugin-Development) where the API would be used normally.

1. Start Eclipse
2. Create a new Java project (```File/New/Java Project```)
3. Add the following libraries to the build configuration (```Project/Properties/Java Build Path/Libraries```)

  * ```<ARE baseURI>/asterics.ARE.jar```
  * ```<ARE baseURI>/../APE/lib/commons-io-2.4.jar```
  * ```<ARE baseURI>/../APE/lib/commons-codec-1.11.jar```

![Build path dialog with external libraries](developer_guide/api/images/BuildPath.JPG)

4. Create a main class ```ResourceRegistryExamples``` and copy and paste the following template code into it

```java
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.net.URISyntaxException;

import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;

public class ResourceRegistryExamples {

  public static void main(String[] args) {
    // Don't call these two lines if you are using the class ResourceRegistry from within the ARE (plugin).
    // setOSGIMode(false) defines that we are using it as a library.
    ResourceRegistry.getInstance().setOSGIMode(false);
    // setAREBaseURI(URI ...) sets the location of the ARE.jar file, which will be set automatically when used within
    // the ARE. If you are on Linux use the respective path of <AsTeRICS snapshot>/bin/ARE
    ResourceRegistry.getInstance().setAREBaseURI(new File("C:\\Program Files (x86)\\AsTeRICS\\ARE").toURI());

    // Define variables for our examples.
    URI myURI = null;
    String contents = "";
  }
}
```

# Example 1 - Getting resource URI
## Get model URI
To get the URI of a model file normally located at ```<ARE baseURI>/models/``` or a subpath of it, use

```java
try {
  myURI = ResourceRegistry.getInstance().getResource("CameraMouse.acs", RES_TYPE.MODEL);
  System.out.println("myURI: " + myURI);

  myURI = ResourceRegistry.getInstance().getResource("grids\\eyeX_Environment\\eyeX_Environment.acs", RES_TYPE.MODEL);  
  System.out.println("myURI: " + myURI);
} catch (URISyntaxException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
}
```

This returns a valid URI if one can be constructed. The method does not check for resource existence.

## Get URI of a data file
To get the URIs of files with resource type data, use

```java
try {
  myURI = ResourceRegistry.getInstance().getResource("pictures/slide7.jpg", RES_TYPE.DATA);  
  System.out.println("myURI: " + myURI);
  
  //slashes may be \\ or / and even mixed up. The paths may contain spaces
  myURI = ResourceRegistry.getInstance().getResource("pictures\\symbols//walk the dog.png", RES_TYPE.DATA);  
  System.out.println("myURI: " + myURI);
  
  //To indicate that a data file is in a plugin-specific subpath of the data folder, use the overridden getResource method 
  //and provide the componentTypeId
  myURI = ResourceRegistry.getInstance().getResource("haarcascade_frontalface_alt.xml", RES_TYPE.DATA,"facetrackerLK",null);  
  System.out.println("myURI: " + myURI);

  //If you provide an absolute URI/URL it is returned as is, withou resolving it against the ARE baseURI.
  myURI = ResourceRegistry.getInstance().getResource("https://raw.githubusercontent.com/wiki/asterics/AsTeRICS/Fetching-resources-with-class-ResourceRegistry.md", RES_TYPE.DATA);  
  System.out.println("myURI: " + myURI);  
} catch (URISyntaxException e) {
  e.printStackTrace();
}
```
# Example 2 - Getting resource content
To get the contents of a resource as a String, use

```Java
try {
  contents = ResourceRegistry.getInstance().getResourceContentAsString("CameraMouse.acs", RES_TYPE.MODEL);
  System.out.println(contents);

  contents = ResourceRegistry.getInstance().getResourceContentAsString("https://raw.githubusercontent.com/wiki/asterics/AsTeRICS/Fetching-resources-with-class-ResourceRegistry.md", RES_TYPE.ANY);
  System.out.println(contents);
} catch (IOException | URISyntaxException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
}
```

When reading the resource content, character encoding is guessed best effort using the class [BOMInputStream](https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/input/BOMInputStream.html), with or without ```ByteOrderMark```

# Example 3 - Storing resource content
To store contents to a resource location, use

```Java
try {
  contents = "My new test data to save.";
  ResourceRegistry.getInstance().storeResource(contents, "saveddata/testFile.txt", RES_TYPE.DATA);
} catch (IOException | URISyntaxException e) {
  e.printStackTrace();
}
```

The method automatically creates missing directories in the path (if supported by the used protocol, e.g. file://) and ensures proper [UTF8 character encoding](https://github.com/asterics/AsTeRICS/blob/v3.0/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java#L94).

# Example 4 - Getting a resource list
To get a list of models or data files, use

```java
//Returns the URIs of all model resources
List<URI> modelList=ResourceRegistry.getInstance().getModelList(false);

//Returns the URIs of all data resources
List<URI> dataList=ResourceRegistry.getInstance().getDataList(false);

//If the element's type must be of String, you can convert it
List<String> modelListAsStrings=ResourceRegistry.getInstance().toStringList(modelList);
```

The parameter of ```getModelList``` or ```getDataList``` defines, if the paths should be relative or absolute.

# References
* [Source code of class ResourceRegistry](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java#L53)
* [Unit tests with usage examples](https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/test/java/eu/asterics/mw/services/TestResourceRegistry.java)
