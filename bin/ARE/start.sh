#!/bin/bash

SCRIPTDIR="$(dirname "$0")"

echo Starting AsTeRICS Runtime Environment in $SCRIPTDIR...
cd $SCRIPTDIR


ARE_AUTOSTART_MODEL=""
ARE_OPTIONAL_SERVICES_INI=""

echo "Params: $@"
MODEL_PARAM="$1"

#Check --webservice flag
if [ "x$1" == "x--webservice" ]
then
	echo "Ignoring --webservice flag, will be started automatically"
	MODEL_PARAM="$2"
fi

#Check second parameter with model name
if [ "x$MODEL_PARAM" != "x" ]
then
	ARE_AUTOSTART_MODEL="$MODEL_PARAM"
fi
ARE_PROFILE_PATH=profile

#Should be done by -Dosgi.clean flag
#echo "Deleting OSGi-Cache"
#rm -Rf $PROFILE_PATH/org.eclipse.osgi

if [ -v $ARE_CONSOLE_LOGLEVEL  ]
then
	export ARE_CONSOLE_LOGLEVEL="WARNING"
fi

echo "ARE_PROFILE_PATH=$ARE_PROFILE_PATH"
echo "ARE_AUTOSTART_MODEL=$ARE_AUTOSTART_MODEL"
echo "ARE_CONSOLE_LOGLEVEL=$ARE_CONSOLE_LOGLEVEL"
echo "ARE_DEBUG_STRING=$ARE_DEBUG_STRING"
echo "ARE_OPTIONAL_SERVICES_INI=$ARE_OPTIONAL_SERVICES_INI"

	
java $ARE_DEBUG_STRING -Deu.asterics.mw.services.AstericsErrorHandling.consoleLogLevel=$ARE_CONSOLE_LOGLEVEL -Dosgi.clean=true  -Dorg.osgi.framework.bootdelegation=* -Dorg.osgi.framework.system.packages.extra=sun.misc -DAnsi=true -Djava.util.logging.config.file=logging.properties -Deu.asterics.ARE.startModel="$ARE_AUTOSTART_MODEL" -Deu.asterics.ARE.ServicesFiles="$ARE_OPTIONAL_SERVICES_INI" -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration $ARE_PROFILE_PATH -console "$@"


# workaround for muliple user support
# delete files with user ownership, because another user would not be allowed to overwrite it
echo "Deleting osgi cache..."
rm -Rf $ARE_PROFILE_PATH/org.eclipse.osgi

echo "Deleting JNativeHook temp files..."
rm -f /tmp/*JNativeHook*
