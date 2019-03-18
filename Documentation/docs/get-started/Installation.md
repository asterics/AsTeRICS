# AsTeRICS Installation

The AsTeRICS provides installer for Windows, Linux and Mac OSX, but only on
Windows the full AsTeRICS suite is supported. On the other platforms the installer
only contains the ARE.

## Windows

The AsTeRICS installer on Windows contains the following executables
* ACS
* WebACS
* ARE
* APE

### Installation Steps

Start the setup file.
You should see the welcome window. It informs you which version of the software
you are installing. Click the button **Next**.

![AsTeRICS Setup Wizard](./img/quickstart01.png)

<!-- <p align="center">
  <img src="./img/quickstart01.png" alt="AsTeRICS Setup Wizard"/>
</p> -->

#### Installation Path

![Installation Path](./img/quickstart02.png)

In this dialogue window you can define the installation path that means you can
choose a folder, where Asterics should be installed. We suggest using the standard
installation path. Then Click the button **Next**.

#### Select Components

You can see the component selection window:

![Installation Java Runtime Environment](./img/quickstart03.png)

AsTeRICS needs the Java Runtime Environment (`>= Version 8`). If
you have already installed the Java Runtime Environment on your computer, please
deselect this option and install only AsTeRICS.

Then, click the button **Next**.

#### Start Menu Folder

Define Start Menu folder entry
In the next dialogue window you can define the name of the start menu entry.You
can see the following window:

![Start Menu Entry](./img/quickstart04.png)

Then, click the button **Next**.

#### Installation Progress

In the next dialogue window you can see the progress of the installation. It can take a
few minutes until the process is finished:

![Installation Progress](./img/quickstart05.png)

#### Installation Finished

As soon as the installation is finisehd, you can see the following window:

![Installation Finish](./img/quickstart06.png)

Finally, click the button **Finish**.

### Desktop Short Cuts

On your desktop you will find the following links (short cuts):

![ACS Startmenu Entry](./img/quickstart07.png)
![ARE Startmenu Entry](./img/quickstart08.png)

These short cuts can be used to start ACS and ARE. Alternaively, you can start these
programs also from the Windows start menu, where additional liks to the Debug
version of the ARE (which displays debugging information in a console window) and
the AsTeRICS unistaller have been added:

### Start Menu

Under Windows-start **All Programs** you will find the start folder **AsTeRICS** and
the start-files.

![AsTeRICS Startmenu Folder](./img/quickstart09.png)

## Mac OS X

On Mac OS X you can use the ARE-only `.dmg` installer with Java embedded. It was tested on Mac OS X 10.9.1 (Mavericks).

### Installation Steps

1. Download the **[Mac OSX installer](https://github.com/asterics/AsTeRICS/releases/download/v3.0/asterics-are-javaembedded-3.0.dmg)**
2. Double-click onto the `.dmg` file and follow the instructions.

## Linux

On Linux you can use the ARE-only [.deb installer](https://github.com/asterics/AsTeRICS/releases/download/v3.0/asterics-are-3.0.deb) for Ubuntu, Debian and ARM/Raspberry Pi.
The installer was tested on Debian 16.04 LTS and Raspberry Pi 3 with Raspbian Jessie.

### Installation Steps

#### Install Java

You must install a Java Runtime Environment first.

The ARE runs with OpenJDK or Oracle Java, but Oracle is recommended due to better execution performance.

``` bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

Another possibility is to download the java package directly from oracle and install it: [Oracle Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

#### Install the ARE

```
sudo dpkg -i asterics-are-3.0.deb
```
