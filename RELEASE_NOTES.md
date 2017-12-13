
AsTeRICS - Assistive Technology Rapid Integration and Construction Set
homepage: http://www.asterics.org 

This project has been partly funded by the European Commission,  Grant Agreement Number 247730 in the years 2010-2012.
Since 2013, further development of AsTeRICS software and hardware components is hosted at:  https://github.com/asterics/AsTeRICS

Since 2014, further development of AsTeRICS have been partly funded in course of the Prosperity4All project (http://www.prosperity4all.eu/) by the European Union’s Seventh Framework Programme for research, technological development and demonstration under grant agreement no 610510

In case you would like to contribute to the repository, please send a request to: asterics-info@ki-i.at
  
This software is provided 'as-is', without any express or implied warranty. 
In no event will the authors be held liable for any damages arising from the use of this software.

Version history:

V1.2 beta (2012-02-22):
first public release; ACS, ARE and 85 plugins with demo models released to the open source community !

V2.0 beta (2012-12-14):
public release of the final AsTeRICS prototype; 
ACS, ARE and 125 plugins with demo models.

V2.2 (2013-07-30):
major GUI improvents and bug corrections

V2.3 (2014-04-25):
Midi-Plugin: Bug fixes/Improvements
Mediaplayer-Plugin: added
Ponggame-Plugin: added
Skywatchermount-Plugin: added
Peakdetector-Plugin: added
Tonegenerator-Plugin: added
EnOcean-Plugin: added
fixed issue #3 and updated project and images for the 2.3 release
added IMA CIM drivers
added IMA CIM drivers
added .hex and Makefile to Arduino CIM folder
fixed issue #8
Speechprocessor-Plugin: improved error handling in case no speechprocessor is installed
Updated help
Proximitysensor-Plugin: added
EOG-Plugin: added
FS20Sender-Plugin: Fixed ARE-crash due to thread-synchronisation problems
Fixed Issue #1, #3, #4, #5, #6, #7, #8, #9, #10
Averager-Plugin: fixed bug
Fixed ACS <-> ARE synchronisation issues
added teensy RC CIM
ARE: Improved Error handling
MicGPI-Plugin: added

v2.5 (2014-12-10)
added Keycapture plugin
fixed Issue #59
fixed Issue #54
fixed Issue #53
fixed Issue #52
fixed Issue #50
fixed Issue #49
fixed Issue #48
fixed Issue #42
fixed Issue #41
fixed Issue #39 and many more
TimerPlugin: cured timing issues
fixed Issue #21
added ReadEDF/WriteEDF, TeensyRCPrototype plugins
fixed Issue #36
fixed Issue #24
improved Issue #30
improved start.bat file
added better version handling to ARE and ACS
added/adapted models for LipMouse plugin
added java 8 support
added Tooltips to all visible components in the ACS
added search functionality for components in the ACS
it is now possible to copy parts of a model to another one
added lots of event listeners to plugins
improved cellboard plugin
fixed arduino plugin to enable the gpio functionality on pwm pins
irtrans can now also be used as IR receiver
events can now have a description
the click range for input/output/event ports was increased
the thread 	synchronizing in the ARE got improved
OSKA now includes german word prediction
added plugins for the eyeX and eyetribe low cost eye trackers
added websocket support to the ARE

v2.6 (2015-03-27)
added/updated the following plugins
LipMouse
CellBoard (incl. CellBoardGUIEditor)
EyeX
Fabi
FabiCronusMax
EasyHomeControl
EmulateFaultyPlugin
XFaceTracker
update Arduino: uniqueId
WriteCSV
ReadCSV
StringSplitter
KinectJ4K
StringExpander plugin
eShoe
HRVAnalysis
ECMAScriptInterpreter
LineWriter
Update Slider
MicGPI
EditBox
CellBoard
ButtonGrid
EventRouter
EventFlipFlop
MediaPlayer
Keyboard
TextSender
StringDispatcher
LineReader
StringDelay
Delay
RemoteWindow
AREWindow

ARE now uses single threaded execution approach
Basic support for Linux OS for ARE
Introduced x-platform service for computervision (javacv and computervision)
Introduced x-platform service for native mouse and keyboard hooks
added global hotkeys for ARE
Numerous bug fixes
Numerous usability improvements for the ACS
added support for RESTful API when started with --webservice

v2.7 (2015-07-02)
added/updated the following plugins
LipMouse
XFacetrackerLK
UniversalRemoteControl
p2_parser
FABI
IIRFilter
bug fix local storage of slider value
Server Sent Events for RESTful API
start.bat, start_debug.bat, start.sh and start_debug.sh now all support autostart of commandline model and --webservice flag
TuioReactivision
MotionAnalysis
ApplicationLauncher
Skype

Basic ARE with cimcommunication and computer vision (based on JavaCV e.g. XFaceTrackerLK) should now run on Windows, Linux and Mac OSX
Numerous bug fixes
Numerous bug fixes/usability improvements for the ACS
moved CIM/StandAloneModules/Fabi2 and CIM/StandAloneModules/LipMouse to seperate github repositories
Fixed issue #85

v2.8 (2016-07-07)
Release with major changes under the hood regarding ARE licensing and customized packaging with APE:
	Licensing: Changed licenses of ARE, services and plugins to a dual license MIT or GPLv3 witch CLASSPATH exception. The individual licenses can be found in the bin/ARE/LICENSE folder
	
	Plugin development made easier: 
    No no need for files bin/ARE/profile/loader.ini, bin/ARE/profile/loader_mini.ini and bin/ARE/profile/loader_componentlist.ini
    Improved loading of plugin osgi bundles and fixed exception handling
    Simplified fetching resources like data files utilizing newly introduced class ResourceRegistry: https://github.com/asterics/AsTeRICS/blob/master/ARE/middleware/src/main/java/eu/asterics/mw/services/ResourceRegistry.java
	
	moved log files to tmp subfolder
	Added webservice homepage to document root of ARE: data/webservice: including improved websocket demo and javascript REST API demo. Start ARE --webservice and open http://localhost:8081/
	Added Asterics Packaging Environment (APE) which let's you create customized and downstripped standalone packages (including native installer and launcher) for a given model file: https://github.com/asterics/AsTeRICS/tree/master/bin/APE
	
	Simplified Java Dependencies: 
    Mixed installations of JREs, JDKs (32bit, 64bit, Java 7 or 8) are supported now. bin/ARE/findjava.bat searches for the best 32bit JRE found on the system. Execution with 64bit is allowed now, only warning is given
    Iprovement for development: All code is compiled for target level java 7, several JDKs can coexist.
	
	Improved build system: Added build file to AsTeRICS root directory.
	Added ARE REST API libraries for Java and javascript including example implementation: https://github.com/asterics/AsTeRICS/tree/master/ARE_RestAPIlibraries
	Removed dependency for external Visual Studio 2010 Redistributable installation: They .dlls are included in the bin/ARE folder
	Added official support for Mac OS X
	Added ARE installer for Linux and Mac OS X
	Models can send ARE to system tray including startup models --> Invisible for user
	ACS to ARE connectivity can be disabled, registering of UDP port is skipped in such a case --> no popup of firewall: see bin/ARE/areProperties
	ACS to ARE connection port can be configured, see bin/ARE/areProperties
	Improved ARE error messages at startup and improved exception handling for deploying and starting models.
	In case of just one model file, there is no need for autostart.acs model.

	Many, many bug fixes including Java8/Windows 10 support: https://github.com/asterics/AsTeRICS/milestone/4?closed=1 
	
	New Plugins:
		RandomNumber
		GMailShortcuts: Emulate shortcuts to operate GMail Web client
		IntelRealSense: Intel Real Sense 3D camera support on Windows 8.1 64 bit machines.
		ComPort, SeriaPort: Generic serial port interfacing with support for binary and string data.
		OpenHAB: Interfacing a OpenHAB server: Controlling SmartHome attached to an OpenHAB server and reading sensor values from there
		StringFilter: Filter incoming string and decide and extract parts. Forward residual string to output port or trigger events: --> very useful in conjunction with CellBoard.
		HoverPanel: Single button that can be positioned anywhere on the screen, can be triggered by hovering (staying still on button, no mouse click): very useful for eye tracking.
		OpenBCI: Plugin for interfacing EEG devices which are compatible to the openBCI packet fromat
	
	Major Changes/Improvements in Plugins
		FS20Sender, FS20Receiver, EasyHomeControl: ported to all platforms (Win, Linux incl. Raspi, Mac OSX) using javahidapi
		KNX: Supported dynamic data points and provide compobox selection for datatypes. Also read values from datapoints
		enOcean: Improved, bug fixed lib
		WebSocket: Added Output Port: The plugin can now receive data through the websocket.
		CellBoard: Many improvements in CellBoard editor: Support for several levels for keyboard files and 'Back' action --> Only one model for AAC with submenus necessary.
		Timer: Bug fixes when starting/restarting

		javacv: Reorganized into platform specific bundles to get smaller packages; Removed ffmpeg lib in javacv-*-basic-*.jar. Optionally a bigger javacv-*-full-*.jar including ffmpeg can be created.
		JNativeHook service: Upgraded to version 2.0.3
		grizzly-http-service: upgraded to version 2.3.23
