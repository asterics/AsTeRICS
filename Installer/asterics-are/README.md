# Build instructions for Linux and Mac

## Prerequisites

* [Oracle JDK 8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html): It must be Oracle JDK 8 because it contains the javafxpackager which is used for creating an Installer packager.
* ant: see [README of root page](/README.md)
* APE: Is included in the repository

## Linux Build

```bash
ant deploy -Dfx.deploy.nativeBundles=deb -Dfx.application.version=<version-string>
```

## Mac OSX Build

```bash
ant deploy -Dfx.deploy.nativeBundles=dmg -Dfx.application.version=<version-string>
```


