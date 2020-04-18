---
title: ApplicationLauncher
---

# ApplicationLauncher

### Component Type: Actuator (Subcategory: File System)

The ApplicationLauncher component can be used to run an external executable application or to open a URL in the platform default browser.
The command and arguments values can either be set as property or dynamically configured through the respective input ports.
The launch of the application or the opening of the URL can also be triggered by an incoming event (launchNow).
The property executeOnPlatform selects the platforms on which the configured application or URL will be launched.

Together with the Keyboard- or RemoteKeyboard components, the ApplicationLauncher plugin can perform complex automation tasks, for example open Skype, choose a contact and make a call.

![Screenshot: ApplicationLauncher plugin](./img/ApplicationLauncher.jpg "Screenshot: ApplicationLauncher plugin")  
ApplicationLauncher plugin

## Input Port Description

- **filename \[string\]:** File name of executable file which shall be started without arguments.
  The application is started automatically, if onlyByEvent is false.
- **arguments \[string\]:** Sets the arguments of the command.
  The application is started automatically, if onlyByEvent is false.
- **stdIn \[string\]:** Sends the incoming string to the standard input stream of the started process.

## Output Port Description

- **stdOut \[string\]:** The standard output stream of the started process.
- **stdErr \[string\]:** The standard error stream of the started process.
- **exitValue \[integer\]:** The exit value of the started process.

## Event Listener Description

- **launchNow:** An incoming event on this port will start the (default or lastest received) application
- **closeNow:** An incoming event on this port will close the current application

## Event Triggerer Description

- **startedSuccessfully:** The process started successfully.
- **startedWithError:** The process started with error.

## Properties

- **executeOnPlatform \[combobox (ALL, WINDOWS, LINUX, MACOSX)\]:** If != ALL, the application will only be launched if the ARE runs on the defined platform.
- **executionMode \[combobox (START_APPLICATION, OPEN_URL)\]:** If START_APPLICATION, the command defined in defaultApplication will be launched with the given arguments.
  If OPEN_URL, the URL defined in arguments will be launched with the platform default browser.
  The platform-specific browser launch commands are configurable in the file areProperties.
- **defaultApplication \[string\]:** Full path and filename of the default application
- **arguments \[string\]:** the commandline arguments for the application
- **workingDirectory \[string\]:** the working directory for the application (`.` is used for home directory of the application)
- **closeCmd \[string\]:** Optional close cmd, e.g. if started cmd has forked processes (e.g. OSKA) use: taskkill.exe /IM "OSKA Keyboard.exe" /T
- **autoLaunch \[boolean\]:** Defines if the default application is automatically launched at startup
- **autoClose \[boolean\]:** Defines if the current application is closed when the model is stopped
- **onlyByEvent \[boolean\]:** If this property is set to true, incoming application files names will not be started immediately (only the launchNow event will start the application)
