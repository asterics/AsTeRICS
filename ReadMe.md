#Welcome to AsTeRICS
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

Check out our [**Demos**](http://asterics.github.io/AsTeRICS/demos.html)

For more information see our website http://www.asterics.eu.

## Quick Build Instructions for Windows
If you just want to use the current snapshot.

1. Clone Repository
  ```
  git clone https://github.com/asterics/AsTeRICS.git
  ```
2. Install the [**Java Development Kit (JDK, 32bit version) >= 7**] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * Ensure to set “JAVA_HOME” to the folder where you installed the Java JDK and add the JDK bin path to the  Environment Variable “Path”
3. Install the [**apache ant build framework (version >= 1.9.1)**] (http://ant.apache.org/bindownload.cgi)
  * Ensure to set “ANT_HOME” to the folder where you installed ant and add the ant bin path to the Environment Variable “Path”
4. Open a terminal and go to the ```ARE``` subfolder
5. Compile the ARE by calling
  ```
  ant
  ```
6. Go to the ```bin/ARE```folder and start the ARE by executing ```start.bat```

If you want to modify/add a plugin or generally want to contribute to the project, please consult the [Developer Manual] (https://github.com/asterics/AsTeRICS/blob/master/Documentation/DeveloperManual.pdf?raw=true])

##Documentation

* [Quickstart Guide] (https://github.com/asterics/AsTeRICS/blob/master/Documentation/QuickStart.pdf?raw=true)
* [User Manual] (https://github.com/asterics/AsTeRICS/blob/master/Documentation/UserManual.pdf?raw=true)
* [Developer Manual] (https://github.com/asterics/AsTeRICS/blob/master/Documentation/DeveloperManual.pdf?raw=true)


You can find more documentation and build plans for some hardware devices in [ModelGuides] (https://github.com/asterics/AsTeRICS/tree/master/Documentation/ModelGuides) and  [DIYGuides] (https://github.com/asterics/AsTeRICS/tree/master/Documentation/DIYGuides).

##Contact

If you want to contribute to the AsTeRICS project, have questions or just need help using it don't bother to create an [issue] (https://github.com/asterics/AsTeRICS/issues), pull request or contact us either on the [Forum] (http://www.asterics.eu/phpbb/index.php) or via [email](mailto:asterics_info@ki-i.at).


##License

This project has been partly funded by the European Commission,  Grant Agreement Number 247730 in the years 2010-2012.
Since 2013, further development of AsTeRICS software and hardware components is hosted at:  https://github.com/asterics/AsTeRICS

If not otherwise mentioned, the AsTeRICS source code is released under the following licenses:

  * ACS and NativeASAPI libraries under LGPL (http://www.gnu.org/licenses/lgpl.html)
  * ARE-middleware, ARE-plugins and BNCI Suite under GPL (http://www.gnu.org/licenses/gpl.html)

For a detailed description of the utilized 3rd party libraries and the implications of use,
please refer to the file "Licenses.pdf" in the folder "Documentation".

This software is provided 'as-is', without any express or implied warranty. 
In no event will the authors be held liable for any damages arising from the use of this software. See [DISCLAIMER] (DISCLAIMER.TXT)

##Releases
See [Release Notes] (RELEASE_NOTES.txt) 

