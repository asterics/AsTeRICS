# Welcome to AsTeRICS
-------------------

AsTeRICS is a free and Open-Source graphical construction set for assistive technologies (AT).
It allows the creation of flexible solutions for people with disabilities using a large set of sensors and actuators.


Possible applications are  
* Computer input (mouse, keyboard, joystick)
* Environmental Control (KNX, FS20, IR, EnOcean, openHAB)
* Toys and Games (Playstation 3, computer games, RC-toys ...)
* Brain/Neural computer interfaces (Enobio, OpenVIBE, OpenEEG)
* Android Phone support (SMS, calls)
* and many more!

For more information see our website http://www.asterics.eu.

## Demos (Solutions)
Check out some solutions [here](https://www.asterics.eu/solutions/)

## Quick Build Instructions
To clone and compile the AsTeRICS framework, please execute the following steps:

1. Clone Repository (submodules will be cloned during build)
  ```
  git clone https://github.com/asterics/AsTeRICS.git
  ```
2. Install the [**Java Development Kit (JDK, 32bit preferred) >= 8**](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * Ensure to set ```JAVA_HOME``` to the folder where you installed the Java JDK and add the JDK bin path to the  Environment Variable ```Path```
3. [apache ant build framework (version >= 1.9.1)](http://ant.apache.org/bindownload.cgi)
  * Ensure to set ```ANT_HOME``` to the folder where you installed ant and add the ant bin path to the Environment Variable ```Path```
  * The ```git``` command line tool must be available from within ant, ensure to set the Environment Variable ```Path``` to the respective ```bin``` folder.
4. Open a terminal and start the AsTeRICS Runtime Environment (ARE) by calling:

  ```
  ant run
  ```
  
 If you don't need commandline support, you can use an IDE for Java Developers, e.g. [Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/neon3), which already contains ant. There you can directly execute ant targets by selecting the build file ```build.xml``` and select the target of choice, e.g. ```run```.
 
## Documentation

* [AsTeRICS Tutorial with screencasts](https://ds.gpii.net/learn/tutorials/integration-alternative-input-modalities)
* [Getting Started](https://www.asterics.eu/get-started/)
* [Tutorials](https://www.asterics.eu/customize/)
* [Developer Manual](https://www.asterics.eu/develop/)
* [Plugin Hellp](https://www.asterics.eu/plugins/)


You can find more documentation and build plans for some hardware devices in [ModelGuides](https://github.com/asterics/AsTeRICS/tree/master/Documentation/ModelGuides) and  [DIYGuides](https://github.com/asterics/AsTeRICS/tree/master/Documentation/DIYGuides).

## Contact

If you want to contribute to the AsTeRICS project, have questions or just need help using it don't bother to create an [issue](https://github.com/asterics/AsTeRICS/issues), pull request or via the [contact page](https://www.asterics.eu/get-involved/Contact.html).  

If you want to support the development of AsTeRICS you're very welcome to donate to the AsTeRICS Foundation:

<a title="Support AsTeRICS Foundation on opencollective.com" href="https://opencollective.com/asterics-foundation" target="_blank">
  <img src="https://opencollective.com/webpack/donate/button@2x.png?color=blue" width=300 />
</a>


## License

This project has been partly funded by the European Commission,  Grant Agreement Number 247730 in the years 2010-2012.
Since 2013, further development of AsTeRICS software and hardware components is hosted at:  https://github.com/asterics/AsTeRICS

#### ACS and NativeASAPI libraries
Licensed under [LGPL](http://www.gnu.org/licenses/lgpl.html)

#### ARE-middleware, ARE-plugins, services and BNCI Suite
Licensed under a dual license [MIT or GPL with CLASSPATH exception](ARE-LICENSE_MITOrGPLv3WithException.txt)

##### How to apply the dual licensing

You may use these components under the terms of the MIT License, if no source code (plugins, services, libraries, ...) which is contained in your desired collection of ARE plugins and services is licensed under the GNU General Public License (GPL).
In order for that, you can remove unneeded plugins or services.

**Please note:** There are also some plugins (e.g. MathEvaluator, VLC) which are **GPL without CLASSPATH** exception, so in this case your license would have to be **GPL** as well.

Please have a look at the individual licenses of the AsTeRICS plugins and services. You find the license files in the subfolder LICENSE of every plugin.

## Disclaimer

This software is provided 'as-is', without any express or implied warranty. 
In no event will the authors be held liable for any damages arising from the use of this software. See [DISCLAIMER](DISCLAIMER.TXT)

## Releases

* See [CHANGELOG](./CHANGELOG.md) or old [Release Notes](RELEASE_NOTES.md) and the published [releases](https://github.com/asterics/AsTeRICS/releases)
