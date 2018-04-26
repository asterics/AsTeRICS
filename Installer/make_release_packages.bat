@ECHO off
ECHO To use this automatic package generator you need the following tools in you system path:
ECHO *) the linux shell tools (rm, mv, etc. e.g. included in the WinAVR package) 
ECHO *) the InnoSetup tool (iscc.exe, download from http://www.jrsoftware.org/isinfo.php)
ECHO *) Git Versioning tool (git.exe download from http://git-scm.com/, when installing choose the option: "Run git from Windows Command Prompt")
ECHO *) Apache Ant build system (http://ant.apache.org/)
ECHO *) Wget download utility (e.g. cygwin + wget package)
ECHO.
ECHO This scripts expects the following files in the folder from where it gets called
ECHO *) Folder called: "java" containing a jre 7 32 bit
ECHO *) File called: "vcredist_x86.exe"  (Visual C++ Redistributable)
ECHO *) File called: "dotNetFx40_setup.exe"  (.Net Framework 4.0)
ECHO.
ECHO Press any key to start !
pause
REM git clone -b v2.8RC1 https://github.com/asterics/AsTeRICS
REM clone Asterics including submodules and submodules of submodules
REM git clone --recurse-submodules https://github.com/asterics/AsTeRICS.git
git clone -b Branch_Bugfixes_v3.0.1 --recurse-submodules https://github.com/asterics/AsTeRICS.git
cd AsTeRICS/bin/ARE/web/webapps/WebACS/
git submodule update --init
cd ../../../../../../

rm -r AsTeRICS/NativeASAPIlibraries
rm -r AsTeRICS/BNCIevaluationSuite
rm -r AsTeRICS/Android/AsTeRICSPhoneServer
cd AsTeRICS
call ant buildAll-release
cd ..
rm AsTeRICS/ReadMe.md
rm -r AsTeRICS/ACS
rm -r AsTeRICS/ARE

rm -rf AsTeRICS/bin/ARE/javacv-*-linux.jar
rm -rf AsTeRICS/bin/ARE/javacv-*-macosx.jar

mv AsTeRICS/bin/ACS AsTeRICS/ACS
mv AsTeRICS/bin/ARE AsTeRICS/ARE
mv AsTeRICS/bin/APE AsTeRICS/APE
mv AsTeRICS/bin/OSKA AsTeRICS/OSKA
rm -rf AsTeRICS/bin
rm -rf AsTeRICS/.git
cp AsTeRICS/Documentation/ACS-Help/HTML/ACS_Help.chm AsTeRICS/ACS/
rm -rf AsTeRICS/Documentation/ACS-Help
rm -rf AsTeRICS/bin
rm AsTeRICS/*.xml
rm AsTeRICS/.*.yml
rm AsTeRICS/Documentation/ModelGuides/*.doc
rm AsTeRICS/Documentation/DIYGuides/*.doc
rm AsTeRICS/Documentation/*.doc
rm AsTeRICS/Documentation/*.docx
rm AsTeRICS/CIMs/Arduino/*.c
rm AsTeRICS/CIMs/Arduino/*.h
rm AsTeRICS/CIMs/Arduino/*.aps
cp AsTeRICS/CIMs/Arduino/driver/*.inf AsTeRICS/CIMs/Arduino
rm -r AsTeRICS/CIMs/Arduino/driver
rm AsTeRICS/CIMs/Arduino/build/Makefile
mv AsTeRICS/CIMs/Arduino/build/* AsTeRICS/CIMs/Arduino
rm -r AsTeRICS/CIMs/Arduino/build
mv AsTeRICS/CIMs/Teensy_RC_CIM/default/* AsTeRICS/CIMs/Teensy_RC_CIM/
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/default/
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.c
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.h
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.aps

mv AsTeRICS/CIMs/EOG_CIM/EOG_CIM/Debug/EOG_CIM.hex AsTeRICS/CIMs/EOG_CIM/
rm -rf  AsTeRICS/CIMs/EOG_CIM/EOG_CIM/
rm -rf AsTeRICS/CIMs/EOG_CIM/*.avrsln
rm -rf AsTeRICS/CIMs/EOG_CIM/*.avrsuo

rm -r AsTeRICS/CIMs/HID_actuator/Joystick_only
rm -r AsTeRICS/CIMs/HID_actuator/LUFA
rm -r AsTeRICS/CIMs/EOG_CIM
rm -r AsTeRICS/CIMs/Lipmouse_CIM
mv AsTeRICS/CIMs/HID_actuator/Mouse_Keyboard_Joystick/*.hex AsTeRICS/CIMs/HID_actuator
rm -r AsTeRICS/CIMs/HID_actuator/Mouse_Keyboard_Joystick
rm -r AsTeRICS/CIMs/HID_actuator/Tools
rm -r AsTeRICS/Tests
rm -r AsTeRICS/CIMs/HID_actuator/USB_Specifications
cd AsTeRICS\CIMs\HID_actuator
wget http://www.pjrc.com/teensy/teensy.exe
cd ..
cd ..
cd ..

rm -r AsTeRICS/CIMs/Proximity_CIM
rm -r AsTeRICS/CIMs/Razor_IMU

cp AsTeRICS/CIMs/Sensorboard/driver/serial_install.exe AsTeRICS/CIMs/Sensorboard
cp AsTeRICS/CIMs/Sensorboard/firmware/sensorboard.hex AsTeRICS/CIMs/Sensorboard
rm -r AsTeRICS/CIMs/Sensorboard/3d-files
rm -r AsTeRICS/CIMs/Sensorboard/driver
rm -r AsTeRICS/CIMs/Sensorboard/firmware
rm -r AsTeRICS/CIMs/Sensorboard/GL850PCB
rm -r AsTeRICS/CIMs/Sensorboard/WiiCamPCB

cp -r AsTeRICS/Installer Installer
rm -r AsTeRICS/Installer
cp ../../asterics_make_release/vcredist_x86.exe Installer/vcredist_x86.exe
cp -r  ../../asterics_make_release/java Installer/java 
cp ../../asterics_make_release/dotNetFx40_setup.exe Installer/dotNetFx40_setup.exe

REM copy icons
cp Installer/*.ico AsTeRICS

cd Installer
call iscc setup.iss
cd ..
mv Installer/setup.exe .
rm -rf Installer
rm -rf AsTeRICS
pause