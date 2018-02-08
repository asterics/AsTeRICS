/*
 * AsTeRICS - Assistive Technology Rapid Integration and Construction Set (http://www.asterics.org)
 * 
 * 
 * Y88b                     d88P      888               d8888  .d8888b.   .d8888b. 
 *  Y88b                   d88P       888              d88888 d88P  Y88b d88P  Y88b
 *   Y88b                 d88P        888             d88P888 888    888 Y88b.
 *    Y88b     d888b     d88P .d88b.  8888888b.      d88P 888 888         "Y888b.  
 *     Y88b   d88888b   d88P d8P  Y8b 888   Y88b    d88P  888 888            "Y88b.
 *      Y88b d88P Y88b d88P  88888888 888    888   d88P   888 888    888       "888
 *       Y88888P   Y88888P   Y8b.     888   d88P  d8888888888 Y88b  d88P Y88b  d88P
 *        Y888P     Y888P     "Y8888  8888888P"  d88P     888  "Y8888P"   "Y8888P"
 * 
 * Copyright 2015 Kompetenznetzwerk KI-I (http://ki-i.at)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 ACS.areStatus = function() {

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var status = ACS.statusType.DISCONNECTED;
	var synchronised = undefined;
	var modelList;

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var actModelChangedEventHandler = function() {
		returnObj.checkAndSetSynchronisation();
	}
	
	var isSameModel = function(actModel, deployedModelXML) {
		var $depMod = $(deployedModelXML);
		var isSame = true;
		// count event connections in current model
		eventConnectionsInCurrentModel = 0;
		for (var i = 0; i < actModel.eventChannelList.length; i++) {
			for (var j = 0; j < actModel.eventChannelList[i].eventConnections.length; j++) {
				eventConnectionsInCurrentModel++;
			}
		}
		// compare model name, amount of components, amount of dataChannels and amount of event connections
		if (actModel.modelName !== $depMod.find('model').attr('modelName') ||
			actModel.componentList.length !== $depMod.find('components').children().length ||
			actModel.dataChannelList.length !== $depMod.find('channel').length ||
			eventConnectionsInCurrentModel !== $depMod.find('eventChannel').length){
			isSame = false;
		} else {
			// compare components, their input ports and their GUI (if they have one)
			$depMod.find('components').each(function() {
				$(this).children().each(function() {
					var found = false;
					var i = 0;
					while (!found && i<actModel.componentList.length) {
						if ($(this).attr('id') === actModel.componentList[i].getId()) {
							if (actModel.componentList[i].gui) {  // since not all components have a gui
								if (actModel.componentList[i].gui.getX() === Number($(this).find('gui').find('posX').text()) && actModel.componentList[i].gui.getY() === Number($(this).find('gui').find('posY').text()) &&
									actModel.componentList[i].gui.getWidth() === Number($(this).find('width').text()) && actModel.componentList[i].gui.getHeight() === Number($(this).find('height').text())) {
										found = true;
									}
							} else {
								found = true;
							}
						}
						i++;
					}
					if (!found) isSame = false;
				});
			});
			// compare dataChannels
			if (isSame) {
				$depMod.find('channel').each(function() {
					var found = false;
					var i = 0;
					while (!found && i<actModel.dataChannelList.length) {
						if (actModel.dataChannelList[i].getId() === $(this).attr('id')) {
							found = true;
						}
						i++
					}
					if (!found) isSame = false;
				});
			}
			// compare event connections
			if (isSame) {
				$depMod.find('eventChannel').each(function() {
					var found = false;
					var i = 0;
					while (!found && i<actModel.eventChannelList.length) {
						if (actModel.eventChannelList[i].startComponent.getId() === $(this).find('sources').find('component').attr('id') && 
							actModel.eventChannelList[i].endComponent.getId() === $(this).find('targets').find('component').attr('id')) {
							for (var j = 0; j < actModel.eventChannelList[i].eventConnections.length; j++) {
								if (actModel.eventChannelList[i].eventConnections[j].trigger.getId() === $(this).find('sources').find('eventPort').attr('id') &&
									actModel.eventChannelList[i].eventConnections[j].listener.getId() === $(this).find('targets').find('eventPort').attr('id')) {
									found = true;
								}
							}
						}
						i++
					}
					if (!found) isSame = false;
				});
			}
			// compare model GUI
			if (isSame) {
				if (actModel.modelGui.getDecoration().toString() !== $depMod.find('Decoration').text() ||
					actModel.modelGui.getFullScreen().toString() !== $depMod.find('Fullscreen').text() ||
					actModel.modelGui.getAlwaysOnTop().toString() !== $depMod.find('AlwaysOnTop').text() ||
					actModel.modelGui.getToSystemTray().toString() !== $depMod.find('ToSystemTray').text() ||
					actModel.modelGui.getShowControlPanel().toString() !== $depMod.find('ShopControlPanel').text() ||
					// (accepting +-1 in the following lines, because there might be rounding errors)
					(Math.round(actModel.modelGui.areGuiWindow.getX()) < Number($depMod.find('AREGUIWindow').find('posX').text()) - 1 && Math.round(actModel.modelGui.areGuiWindow.getX()) > Number($depMod.find('AREGUIWindow').find('posX').text()) + 1) ||
					(Math.round(actModel.modelGui.areGuiWindow.getY()) < Number($depMod.find('AREGUIWindow').find('posY').text()) - 1 && Math.round(actModel.modelGui.areGuiWindow.getY()) > Number($depMod.find('AREGUIWindow').find('posY').text()) + 1) ||
					(Math.round(actModel.modelGui.areGuiWindow.getWidth()) < Number($depMod.find('AREGUIWindow').find('width').text()) - 1 && Math.round(actModel.modelGui.areGuiWindow.getWidth()) > Number($depMod.find('AREGUIWindow').find('width').text()) + 1) ||
					(Math.round(actModel.modelGui.areGuiWindow.getHeight()) < Number($depMod.find('AREGUIWindow').find('height').text()) - 1 && Math.round(actModel.modelGui.areGuiWindow.getHeight()) > Number($depMod.find('AREGUIWindow').find('height').text()) + 1)) {
					isSame = false;
				}
			}			
		}
		return isSame;
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.events = ACS.eventManager();
	
	returnObj.setModelList = function(mList) {
		modelList = mList;
		modelList.events.registerHandler('actModelChangedEvent', actModelChangedEventHandler);
	}
	
	returnObj.checkAndSetSynchronisation = function() {
		if ((typeof modelList != 'undefined') && (status != ACS.statusType.DISCONNECTED) && (status != ACS.statusType.CONNECTIONLOST) && (status != ACS.statusType.CONNECTING)) {
						
			function DDM_successCallback(data, HTTPstatus) {
				var newSync;
				var deployedModelXML = $.parseXML(data);
				// compare actModel with deployed model to determine synchronisation status
				newSync = isSameModel(modelList.getActModel(), deployedModelXML);
				if (synchronised != newSync) {
					synchronised = newSync;
					returnObj.events.fireEvent('ARESynchronisationChangedEvent');
				}
			}
			
			function DDM_errorCallback(HTTPstatus, AREerrorMessage) {
				if (AREerrorMessage != '') log.debug('Unable to determine synchronisation status - ARE says: "' + AREerrorMessage + '"');
				synchronised = undefined;
				returnObj.events.fireEvent('ARESynchronisationChangedEvent');
			}
			
			// get currently deployed model from the ARE
			downloadDeployedModel(DDM_successCallback, DDM_errorCallback);			
		}
	}
	
	returnObj.setSynchronised = function(newSync) {
		synchronised = newSync;
		log.debug('setting sync: ' + synchronised);
		returnObj.events.fireEvent('ARESynchronisationChangedEvent');
	}
	
	returnObj.getSynchronised = function() {
		return synchronised;
	}
	
	returnObj.setStatus = function(newStatus) { // ACS.statusType
		status = newStatus;
		returnObj.events.fireEvent('AREStatusChangedEvent');
	}
	
	returnObj.getStatus = function() {
		return status;
	}
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	
	return returnObj;
}();