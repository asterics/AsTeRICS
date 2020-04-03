# Installer Build instructions (Linux and Mac)

## Prerequisites

* [APE dependencies](../../bin/APE/#dependencies): You need the dependencies for building AsTeRICS and the tools for the native installer creation on your platform.

### APE native installer creation

The [native installer creation](../../bin/APE#build-infrastructure-and-native-installer-creation) is done using APE and it's bindings to the Oracle JDK 8 javafxpackager tool.

### APE.properties

The installer properties and the behvaviour during the build can be defined in the file [APE.properties](./APE.properties). The properties can also be overridden on the command line using JAVA property syntax:

```bash
ant -Dfx.application.version=<xxx> ...
``` 

## Linux

The Linux installer has some custom debian-specific files in [packages/linux](./packages/linux/). The file [packages/linux/control](./packages/linux/control) contains the meta-information for the package e.g. version string or package dependencies.

If you freshly checkout AsTeRICS, the version string in ```APE.properties``` and the ``` control``` file will automatically be replaced when calling ```ant deploy```. For subsequent calls you would have to update the files manually or perform ```git checkout .``` in the AsTeRICS root directory if you want to change the version number again.

```bash
VERSION=<version string> ant deploy -Dfx.deploy.nativeBundles=deb -Dfx.application.version=$VERSION
```

## Mac OSX

```bash
VERSION=<version string> ant deploy -Dfx.deploy.nativeBundles=dmg -DAPE.embedJava=true -Dfx.application.version=$VERSION
```

or if you want to specify the Java version that should be used for embedding add the ```-Dfx.application.version``` parameter:

```bash
VERSION=<version string> ant deploy -Dfx.deploy.nativeBundles=dmg -DAPE.embedJava=true -Dfx.application.version=$VERSION -Dfx.platform.basedir=<path to JRE Home> 
```


