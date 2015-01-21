echo AsTeRICS ARE Version 2.2
echo Starting AsTeRICS Runtime Environment ...
#-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 -Dorg.osgi.framework.bootdelegation=* -DAnsi=true
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044 -Dorg.osgi.framework.bootdelegation=* -DAnsi=true -Djava.util.logging.config.file=logging.properties -jar org.eclipse.osgi_3.6.0.v20100517.jar -configuration profile -console
