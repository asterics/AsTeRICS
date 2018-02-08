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
 
 ACS.modelList = function() {

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var list = []; // ACS.model
	var filenameCounter = 1;
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var removeSubstituteFilename = function() { // is called when actModel is removed or loaded from a file
		var actNumber = list[returnObj.actIndex].getFilename().slice(7); // removing the 7-letter-word "newfile" leaves the number
		if ((filenameCounter - 1) + '' === actNumber) filenameCounter--;
	}
	
	var filenameBeingChangedEventHandler = function() {
		removeSubstituteFilename();
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.actIndex = 0;
	returnObj.events = ACS.eventManager();
	
	returnObj.addNewModel = function() {
		this.actIndex = (list.push(ACS.model('newFile' + filenameCounter))) - 1;
		filenameCounter++;
		
		this.events.fireEvent('newModelAddedEvent');		
		this.events.fireEvent('actModelChangedEvent');
		list[this.actIndex].events.registerHandler('filenameBeingChangedEvent', filenameBeingChangedEventHandler);
	}
	
	returnObj.getActModel = function() {
		return list[this.actIndex];
	}
	
	returnObj.setActModel = function(actIndex) {
		if ((actIndex > -1) && (actIndex < list.length)) {
			this.actIndex = actIndex;
			this.events.fireEvent('actModelChangedEvent');
			return true;
		}  else {
			return false;
		}
	}
	
	returnObj.getModelAtIndex = function(index) {
		return list[index];
	}
	
	returnObj.removeModel = function() { // removes the actModel
		this.events.fireEvent('removingModelEvent');
		removeSubstituteFilename();
		list.splice(this.actIndex, 1);
		if (this.actIndex > (list.length - 1)) this.actIndex--; // if no more models to the right, go to the left
		if (this.actIndex === -1) { // if list is empty, add a new empty model again
			returnObj.addNewModel();
		}
		this.events.fireEvent('actModelChangedEvent');
	}
	
	returnObj.getLength = function() {
		return list.length;
	}
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	returnObj.addNewModel();
	
	// check if an openFile was provided in the querystring and if yes, try to load a model from that file
	if (ACS.openFile) {
		var xmlObj;
		var httpRequest = new XMLHttpRequest();
		httpRequest.onreadystatechange = function() {
			if (httpRequest.readyState === XMLHttpRequest.DONE && httpRequest.status === 200) {
				list[0].setFilename(ACS.openFile.substring(ACS.openFile.lastIndexOf('/') + 1));
				xmlObj = $.parseXML(httpRequest.responseText);
				list[0].loadModel(xmlObj);
			}
		}
		try {
			httpRequest.open('GET', ACS.openFile, false);
			httpRequest.send();
		} catch (e) {
			// Note: If an invalid URL is passed to the WebACS, it will start normally, showing an empty model.
			// Since URLs will usually be passed by some software and not by the enduser directly, no error-popup 
			// has been installed in order not to confuse the enduser, who often will have no knowledge of URLs and querystrings.
		}
	}
	return returnObj;
}