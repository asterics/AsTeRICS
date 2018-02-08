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
 
 ACS.removeItemListAction = function(parentModel, inList) { // ACS.model

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var itemList = inList.slice(); // copy the original array to be independent from changes in the original array
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var updateItemList = function() {
		// for every component in the list: add to the list all channels that lead to or from the component
		for (var i = 0; i < itemList.length; i++) {
			if (typeof itemList[i].matchesComponentCollection !== 'undefined') { // only components have a parameter "matchesComponentCollection" and it is defined in every component-object
				for (var j = 0; j < parentModel.dataChannelList.length; j++) {
					if (((parentModel.dataChannelList[j].getInputPort()) && (parentModel.dataChannelList[j].getOutputPort())) 
						&& ((parentModel.dataChannelList[j].getInputPort().getParentComponent() === itemList[i]) || (parentModel.dataChannelList[j].getOutputPort().getParentComponent() === itemList[i]))) {
						if (itemList.indexOf(parentModel.dataChannelList[j]) === -1) itemList.push(parentModel.dataChannelList[j]);
					}
				}
				for (var j = 0; j < parentModel.eventChannelList.length; j++) {
					if ((parentModel.eventChannelList[j].startComponent === itemList[i]) || (parentModel.eventChannelList[j].endComponent === itemList[i])) {
						if (itemList.indexOf(parentModel.eventChannelList[j]) === -1) itemList.push(parentModel.eventChannelList[j]);
					}
				}
			}
		}
	}

// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = ACS.action(parentModel);
	
	returnObj.execute = function() {
		for (var i = 0; i < itemList.length; i++) {
			if (typeof itemList[i].matchesComponentCollection !== 'undefined') { // only components have an attribute "matchesComponentCollection" and it is defined in every component-object
				parentModel.removeComponent(itemList[i]);
			} else {
				if (typeof itemList[i].startComponent !== 'undefined') { // only eventChannels have an attribute "startComponent" and it is defined in every eventChannel-object
					parentModel.removeEventChannel(itemList[i]);
				} else {
					parentModel.removeDataChannel(itemList[i]);
				}
			}
		}
		ACS.areStatus.checkAndSetSynchronisation();
		parentModel.undoStack.push(returnObj);
	}
	
	returnObj.undo = function() {
		for (var i = 0; i < itemList.length; i++) {
			if (typeof itemList[i].matchesComponentCollection !== 'undefined') { // only components have an attribute "matchesComponentCollection" and it is defined in every component-object
				parentModel.addComponent(itemList[i]);
			} else {
				if (typeof itemList[i].startComponent !== 'undefined') { // only eventChannels have an attribute "startComponent" and it is defined in every eventChannel-object
					parentModel.addEventChannel(itemList[i]);
				} else {
					parentModel.addDataChannel(itemList[i]);
				}
			}
		}
		ACS.areStatus.checkAndSetSynchronisation();
		parentModel.redoStack.push(returnObj);
	}

// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	parentModel.redoStack = [];
	updateItemList();
	
	return returnObj;
}