AsTeRICS Plugins
===============

This is my collection of plugins for the [AsTeRICS](http://http://www.asterics.eu/) project. 
See the project's GitHub repository [here](http://github.com/asterics/AsTeRICS).


Generally the build.xml of the plugins has been extended with the property **arecodelocation**. This property should hold the path to the ARE folder of the AsTeRICS source tree.


ECMA Script Interpreter
===============

 This component is a general purpose processor that can relays the input and
 incoming events to a script compatible to the ECMA script specification (e.g.
 JavaScript). The script is specified by the property scriptname. If the property
 is left empty, the component will load the file "script.js" from local storage.
 If this file does not exist, the component will generate the file in local storage
 and fill it with a default "pass-through" script. 
 
 There are certain constraints for the script:
  - the script has to contain an object named scriptclass.
  - the object has to implement a method dataInput(input_index, input_data)
  - the object has to implement a method eventInput(event_index)
  
 
The script is provided with the following external variables:
  - output:   an array of size 8 representing 8 IRuntimeOutputPorts
  - eventout: an array of size 8 representing 8 IRuntimeEventTriggererPorts
  - property: an array of size 8 holding strings with the property inputs from the components property fields 
 

The sendData method of the output variables has to be called with a string.
If necessary this needs to be converted into a Java string, this can be done like this:
  
     str = new java.lang.String(in_data);
     output[in_nb].sendData(str.getBytes());
  

