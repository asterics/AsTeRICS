## AsTeRICS Packaging Environment (APE)

To start APE call

java -jar APE.jar -DAPE.models=<URI to models dirs or files>] [[-Dfx.deploy.nativeBundles=exe|msi|deb|all|none] [-Dfx.application.name=<name of application>] [-Dfx.application.version=<xx.xx.xxx>]]

If you call it with no arguments the properties file at the location of the APE.jar file will be used.

[-DAPE.propertiesFile=<URI to APE.properties file>]

General properties that define startup behaviour:
ARE.baseURI: URI where APE looks for the template folder and config files per default.
APE.propertiesFile


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
They represent some of the [javafx packaging ant-tasks](https://docs.oracle.com/javase/8/docs/technotes/guides/deploy/javafx_ant_task_reference.html)

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
