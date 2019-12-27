---
title: Control the ARE
---

# Control the ARE

In the system-tab, the group ARE handles the functionalities for connecting to and communicating with the ARE.

![Screenshot: System Tab, ARE and ARE Storage Group](./img/are_and_are_storage_group.png "Screenshot: System Tab, ARE and ARE Storage Group")

System Tab, ARE and ARE Storage Group

*   _Connect to ARE_ connects the ACS with the ARE. The location of the ARE is extracted from the URL of the WebACS (assuming it is hosted on the ARE's webservice) or - if the WebACS is not hosted - the ARE is assumed at "localhost:8081". The possibility to edit this value has yet to be implemented.
*   _Disconnect from ARE_ closes the connection to the ARE.
*   _Upload Model_ transmits the currently selected model from the ACS to the ARE. The model on the ARE will be overwritten. Uploading the model to the ARE does not start the model on the ARE.
*   _Download Model_ transmits the active model from the ARE to the ACS. A new model tab will be opened for this.
*   _Download Component Collection_ transmits the bundel description (describing the components) from the connected ARE to the ACS. These bundel descrptions will be set for the currently selected model only and will be available as components in the components-tab.

The group ARE Storage deals with the storage of models on the ARE. Once a model has been stored on the ARE it can be activated using the ARE interface.

*   _Store Model on ARE_ transmits the currently selected model from the ACS to the ARE storage. A dialog appears to set the filename.
*   _Load Model from Storage_ transmits a model from the ARE storage to the ACS (a new model tab will be opened for this). A dialog appears to choose which model to download.
*   _Activate a Stored Model_: A dialog appears to select the filename of a model in the ARE storage. This model will be set active in the ARE and also will be started.
*   _Delete a Stored Model_ deletes a model from the ARE storage using a file dialog.
*   _Set as Autorun_ sets the currently selected model as autorun model. This model will be started automatically when the ARE starts.

Starting and stopping a model can be done with the buttons in the group Model.

![Screenshot: The Model Group in the System Tab](./img/model_group_in_system_tab.png "Screenshot: The Model Group in the System Tab")

The Model Group in the System Tab

*   _Start Model_ starts the model on the ARE.
*   _Pause Model_ pauses the model on the ARE.
*   _Stop Model_ stops the model on the ARE.