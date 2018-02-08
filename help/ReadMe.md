AsTeRICS Help - Setup Instructions

"helpPaths.json" contains the paths to ARE-, ACS- and plugins-help files, as well as the path to the component collection that is to be used to build the plugins help menu.

The path to "ARE" must refer to a folder containing a file "are_help.htm" (containing a HTML code snippet for the ARE help menu entry) and a folder "are/", containing the actual help files.
The path to "ACS" must refer to a folder containing a file "acs_help.htm" (containing a HTML code snippet for the ACS help menu entry) and a folder "acs/", containing the actual help files.
The path to "plugins" must refer to a folder containing a file "plugins_help.htm" (containing the HTML code snippets for the plugin help menu entries and for the quickselect field) and the folders "actuators/", "processors/" and "sensors/" containing the actual help files.
The path to "componentCollection" must refer to a folder containing a file "defaultComponentCollection.abd". The plugins help menu will be built based on the information in that file. If the file cannot be found, the standard "defaultComponentCollection_help.abd" will be used instead.

Start AsTeRICS Help by opening "index.htm". The first file opened will be "startPage.htm". When called with a specific query string, AsTeRICS Help can be started showing a specific help file instead. The query string must comprise two parts:
- The information which part of the help it is referring to - can be either "are", "acs" or "plugins".
- The relative path (below the path specified in "helpPaths.json") and the complete filename of the file that should be opened.
Example: "?acs&acs/ACS_Basic_Functions.htm"