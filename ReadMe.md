#Welcome to AsTeRICS
-------------------

AsTeRICS is a free and Open-Source graphical construction set for assistive technologies (AT).
It allows the creation of flexible solutions for people with disabilities using a large set of sensors and actuators.


Possible applications are  
* Computer input (mouse, keyboard, joystick)
* Environmental Control (KNX, FS20, IR)
* Toys and Games (Playstation 3, computer games, RC-toys ...)
* Brain/Neural computer interfaces (Enobio, OpenVIBE, OpenEEG)
* Android Phone support (SMS, calls)
* and many more!

For more information see our website http://www.asterics.eu.

## Quick Build Instructions for Windows
* Clone repository
```
git clone https://github.com/asterics/AsTeRICS.git
```
* Download and Install the Java Development Kit (JDK) >= 7 from http://www.oracle.com/technetwork/java/javase/downloads/index.html
  * (Choose the 32bit version for your operating system, because some necessary components for interfacing hardware are not supported by the 64bit version by now)
  * Create a System Environment Variable “JAVA_HOME” which points to the folder where you installed the Java JDK.
  * Add the JDK bin path to the System Environment Variable “Path”
* Download and install the apache ant build framework (version >= 1.9.1) http://ant.apache.org/bindownload.cgi
  * Create a System Environment Variable “ANT_HOME” which points to the installation directory of ant.
  * Add the ant bin path to the System Environment Variable “Path”
* Open a terminal and go to the ARE subfolder
* Start ant
```
ant
```

##Documentation

* [Quickstart Guide] (http://www.asterics.eu/download/DeveloperManual.pdf)
* [User Manual] (http://www.asterics.eu/download/UserManual.pdf)
* [Developer Manual] (http://www.asterics.eu/download/DeveloperManual.pdf)


You can find more documentation and build plans for some hardware devices [here] (http://www.asterics.eu/index.php?id=26).

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

