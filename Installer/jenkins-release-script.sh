# --------------------------------------------------------
# bash script for building an AsTeRICS release with a
# jenkins builder on a windows machine.
# --------------------------------------------------------
# The following is needed on PATH on the windows machine:
# *) the InnoSetup tool (iscc.exe, download from http://www.jrsoftware.org/isinfo.php)
# *) Git Versioning tool and git bash
#    (git.exe, bash.exe download from http://git-scm.com/, add e.g. "C:\Program Files\Git\bin" to PATH)
# *) Apache Ant build system (http://ant.apache.org/)
# --------------------------------------------------------
# run in jenkins with command:
# bash -ex jenkins-release-script.sh
# --------------------------------------------------------

# build release with ant
ant buildAll-release

# copy everything except AsTeRICS folder to AsTeRICS subfolder
rm -rf AsTeRICS
mkdir AsTeRICS
shopt -s extglob
cp -r !(AsTeRICS) AsTeRICS

# remove folders that will be overwritten by mv
rm -r AsTeRICS/ACS
rm -r AsTeRICS/ARE

# move files
mv AsTeRICS/bin/ACS AsTeRICS/ACS
mv AsTeRICS/bin/ARE AsTeRICS/ARE
mv AsTeRICS/bin/APE AsTeRICS/APE
mv AsTeRICS/bin/OSKA AsTeRICS/OSKA
mv AsTeRICS/CIMs/Arduino/build/* AsTeRICS/CIMs/Arduino
mv AsTeRICS/CIMs/Teensy_RC_CIM/default/* AsTeRICS/CIMs/Teensy_RC_CIM/
mv AsTeRICS/CIMs/EOG_CIM/EOG_CIM/Debug/EOG_CIM.hex AsTeRICS/CIMs/EOG_CIM/
mv AsTeRICS/CIMs/HID_actuator/Mouse_Keyboard_Joystick/*.hex AsTeRICS/CIMs/HID_actuator

# copy files
cp AsTeRICS/CIMs/Arduino/driver/*.inf AsTeRICS/CIMs/Arduino
cp AsTeRICS/CIMs/Sensorboard/driver/serial_install.exe AsTeRICS/CIMs/Sensorboard
cp AsTeRICS/CIMs/Sensorboard/firmware/sensorboard.hex AsTeRICS/CIMs/Sensorboard
cp Installer/*.ico AsTeRICS

# remove files
rm -r AsTeRICS/Installer
rm -rf AsTeRICS/bin
rm -rf AsTeRICS/Documentation/ACS-Help
rm -r AsTeRICS/CIMs/Arduino/driver
rm -r AsTeRICS/CIMs/Arduino/build
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/default/
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.c
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.h
rm -rf AsTeRICS/CIMs/Teensy_RC_CIM/*.aps
rm -rf  AsTeRICS/CIMs/EOG_CIM/EOG_CIM/
rm -rf AsTeRICS/CIMs/EOG_CIM/*.avrsln
rm -rf AsTeRICS/CIMs/EOG_CIM/*.avrsuo
rm -r AsTeRICS/CIMs/HID_actuator/Joystick_only
rm -r AsTeRICS/CIMs/HID_actuator/LUFA
rm -r AsTeRICS/CIMs/EOG_CIM
rm -r AsTeRICS/CIMs/Lipmouse_CIM
rm -r AsTeRICS/CIMs/HID_actuator/Mouse_Keyboard_Joystick
rm -r AsTeRICS/CIMs/HID_actuator/Tools
rm -r AsTeRICS/Tests
rm -r AsTeRICS/CIMs/HID_actuator/USB_Specifications

rm AsTeRICS/CIMs/Arduino/build/Makefile
rm -r AsTeRICS/NativeASAPIlibraries
rm -r AsTeRICS/BNCIevaluationSuite
rm -r AsTeRICS/Android/AsTeRICSPhoneServer
rm AsTeRICS/ReadMe.md
rm -rf AsTeRICS/bin/ARE/javacv-*-linux.jar
rm -rf AsTeRICS/bin/ARE/javacv-*-macosx.jar
rm AsTeRICS/*.xml
rm AsTeRICS/.*.yml
rm AsTeRICS/Documentation/ModelGuides/*.doc
rm AsTeRICS/Documentation/DIYGuides/*.doc
rm AsTeRICS/Documentation/*.doc
rm AsTeRICS/Documentation/*.docx
rm AsTeRICS/CIMs/Arduino/*.c
rm AsTeRICS/CIMs/Arduino/*.h
rm AsTeRICS/CIMs/Arduino/*.aps
rm -r AsTeRICS/CIMs/Proximity_CIM
rm -r AsTeRICS/CIMs/Razor_IMU
rm -r AsTeRICS/CIMs/Sensorboard/3d-files
rm -r AsTeRICS/CIMs/Sensorboard/driver
rm -r AsTeRICS/CIMs/Sensorboard/firmware
rm -r AsTeRICS/CIMs/Sensorboard/GL850PCB
rm -r AsTeRICS/CIMs/Sensorboard/WiiCamPCB

# copy dependencies
if [ -d  ./AsTeRICS-release-dependencies ]; then 
    cd ./AsTeRICS-release-dependencies
	git pull
	cd ..
else
    git clone https://github.com/asterics/AsTeRICS-release-dependencies.git
fi

cp AsTeRICS-release-dependencies/teensy.exe AsTeRICS\CIMs\HID_actuator
cp AsTeRICS-release-dependencies/vcredist_x86.exe Installer
cp AsTeRICS-release-dependencies/dotNetFx40_setup.exe Installer
cp -r AsTeRICS-release-dependencies/java Installer/java

# make installer
cd Installer
iscc setup.iss
cd ..
mv Installer/setup.exe .

# cleanup
rm -rf AsTeRICS
