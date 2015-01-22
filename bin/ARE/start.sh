echo Starting AsTeRICS Runtime Environment ...

ARE_AUTOSTART_MODEL=autostart.acs
ARE_PROFILE_PATH=profile

#Should be done by -Dosgi.clean flag
#echo "Deleting OSGi-Cache"
#rm -Rf $PROFILE_PATH/org.eclipse.osgi

if [ -v $ARE_LOG_STRING  ]
then
	export ARE_LOG_STRING="error_level:WARNING"
fi

echo "ARE_PROFILE_PATH=$ARE_PROFILE_PATH"
echo "ARE_AUTOSTART_MODEL=$ARE_AUTOSTART_MODEL"
echo "ARE_LOG_STRING=$ARE_LOG_STRING"
echo "ARE_DEBUG_STRING=$ARE_DEBUG_STRING"

echo $ARE_LOG_STRING >.logger
	
java $ARE_DEBUG_STRING -Dosgi.clean=true  -Dorg.osgi.framework.bootdelegation=* -Dorg.osgi.framework.system.packages.extra=sun.misc -DAnsi=true -Djava.util.logging.config.file=logging.properties -Deu.asterics.ARE.startModel=$ARE_AUTOSTART_MODEL -Deu.asterics.ARE.ServicesFiles="services.ini;services-linux.ini" -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration $ARE_PROFILE_PATH -console
