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
 
 ACS.component = function(id, // String; can be changed, but must be unique
						 componentTypeId, // String
						 description, // String
						 singleton, // bool
						 x, // int
						 y, // int
						 type, // ACS.componentType
						 isSelected, // bool
						 foundInComponentCollection) { // bool (if false, the component will be a skeleton only and removed by the view)

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************

// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.inputPortList = [];
	returnObj.outputPortList = [];
	returnObj.listenEventList = [];
	returnObj.triggerEventList = [];
	returnObj.propertyList = [];
	returnObj.gui = null;
	returnObj.events = ACS.eventManager();
	returnObj.matchesComponentCollection = true;
	returnObj.foundInComponentCollection = foundInComponentCollection;
	
	returnObj.getId = function() {
		return id;
	}
	
	returnObj.setId = function(newId) {
		id = ACS.utils.encodeForXml(newId);
		returnObj.events.fireEvent('componentIdChangedEvent');
	}

	returnObj.getComponentTypeId = function() {
		return componentTypeId;
	}	
	
	returnObj.getDescription = function() {
		return description;
	}
	
	returnObj.setDescription = function(newDescription) {
		description = ACS.utils.encodeForXml(newDescription);
	}

	returnObj.getSingleton = function() {
		return singleton;
	}	

	returnObj.getX = function() {
		return x;
	}
	
	returnObj.getY = function() {
		return y;
	}	
	
	returnObj.setNewPosition = function(newX, newY) {
		x = newX;
		y = newY;
		returnObj.events.fireEvent('componentPositionChangedEvent');
	}

	returnObj.getType = function() {
		return type;
	}	
	
	returnObj.getIsSelected = function() {
		return isSelected;
	}	
	
	returnObj.setIsSelected = function(newIsSelected) {
		isSelected = newIsSelected;
		if (isSelected) {
			returnObj.events.fireEvent('selectedEvent');
		} else {
			returnObj.events.fireEvent('deSelectedEvent');
		}
	}			

// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
		
	return returnObj;
}