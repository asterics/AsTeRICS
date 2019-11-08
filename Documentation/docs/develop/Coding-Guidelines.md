# Coding Guidelines

Coding guidelines are necessary to allow new developers to quickly find their through the code of the ARE. They are created in such a way to provide means for developers to understand code of each other but they also make sure that non-technical users can find their way through a model in ACS.

If you use Eclipse as IDE, **you can import predefined clean up, code template and formatting settings**, which cover some of the coding guidelines (see 4.1.1).

The basic coding guidelines are:

*   Plugins, ports and properties should be named intuitively in the bundle descriptor. Only if necessary, the corresponding variables in the plugin code should be named differently. However they should adhere to the naming conventions stated in section 4.1.3 and different names should be commented in the code sections which translate the name into the variable (getInputPort(), getRuntimeProperty() …)
    
*   Variable names should always use the Java naming conventions
    
*   Every method should be preceded by a JavaDoc compatible header in order to allow new developer to grasp what is going on in it
    
*   Where reasonable code comments should be added to improve understanding of code internals
    
*   Code should be indented by four spaces per indentations stage. Indentations should be done using space and **not tabs**. Tabs should be converted to spaces.
    
*   Opening parentheses should be placed in the same line
    

## Eclipse Code Style Settings
    

If you use Eclipse as IDE, you can import predefined clean up, code template and formatting settings. The files are located in the AsTeRICS/ARE folder of the checkout github repository.

*   **Eclipse - Code Style - Clean Up.xml**: Definitions for cleanup, e.g. remove unused imports and unused variables
    
*   **Eclipse - Code Style - Code Templates.xml**: Contains default file header including license information (see 4.1.5) and default class comment (see 4.1.6).
    
*   **Eclipse - Code Style - Formatter.xml**: Contains code formatting definitions e.g. 4 spaces instead of tabs.
    

You can import the settings by

1.  selecting the project folder and clicking right mouse button
    
2.  opening ‘Properties’ entry of popup menu
    
3.  Opening ‘Java Code Style’/Clean Up|Code Templates|Formatter respectively
