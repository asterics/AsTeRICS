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
 
 ACS.canvasView = function(	modelList, // ACS.modelList
							clipBoard) { // ACS.clipBoard

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var canvasTabPanel = ACS.tabPanel(ACS.vConst.CANVASVIEW_MOTHERPANEL, ACS.vConst.CANVASVIEW_CLASSOFTAB, ACS.vConst.CANVASVIEW_CLASSOFPANEL);
	var modelViewList = []; // Array<modelView>
	var panelId = 0;
	var editorProperties = ACS.editorProperties();
	var blindMode = false;
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	// ********************************************** handlers ***********************************************************
	
	// filenameBeingChangedEventHandler is defined in function addActModelToView
	
	var newModelAddedEventHandler = function() {
		returnObj.addActModelToView();
	}
	
	var removingModelEventHandler = function() {
		var removePanel = ''; // for the id of the panel that needs to be removed
		// remove the modelView from the list:
		for (var i = 0; i < modelViewList.length; i++) {
			if (modelViewList[i] && (modelViewList[i].getModel() === modelList.getActModel())) {
				removePanel = modelViewList[i].getModelContainerId();
				modelViewList[i].destroy();
				modelViewList.splice(i, 1);
			}
		}
		
		// remove the tab from the DOM:
		document.getElementById(ACS.vConst.CANVASVIEW_TABLIST).removeChild(document.getElementById(removePanel.replace('Panel', 'Tab')));
		document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).removeChild(document.getElementById(removePanel));
		// update the tabPanel:
		canvasTabPanel.updatePanel();
	}
	
	var tabSwitchedEventHandler = function() {
		var elements = document.getElementById(ACS.vConst.CANVASVIEW_TABLIST).getElementsByClassName('tab');
		for (var i = 0; i < elements.length; i++) {
			if (elements[i].getAttribute('aria-selected') === 'true') {
				modelList.setActModel(i);
			}
		}
	}
	
	var actModelChangedEventHandler = function() {
		// seek the panel matching the actModel:
		for (var i = 0; i < modelViewList.length; i++) {
			if (modelViewList[i] && (modelViewList[i].getModel() === modelList.getActModel())) {
				// activate the tab
				var tabId = modelViewList[i].getModelContainerId().replace('Panel', 'Tab');
				document.getElementById(tabId).click();
			}
		}	
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.addActModelToView = function() {
		var actModel = modelList.getActModel();
		var li = document.createElement('li');
		li.setAttribute('id', 'canvasTab' + panelId);
		li.setAttribute('class', 'tab canvasTab');
		li.setAttribute('aria-controls', 'canvasPanel' + panelId);
		li.setAttribute('aria-selected', 'false');
		li.setAttribute('role', 'tab');
		li.setAttribute('tabindex', -1);
		li.textContent = actModel.getFilename();
		document.getElementById(ACS.vConst.CANVASVIEW_TABLIST).appendChild(li);
		var div = document.createElement('div');
		div.setAttribute('id', 'canvasPanel' + panelId);
		div.setAttribute('class', 'panel canvasPanel');
		div.setAttribute('aria-labelledby', 'tab' + panelId);
		div.setAttribute('role', 'tabpanel');
		document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).appendChild(div);
		modelViewList.push(ACS.modelView('canvasPanel' + panelId, actModel, clipBoard, editorProperties));
		panelId++;
		canvasTabPanel.updatePanel();
		// activate the tab (a simple li.click() will not work in safari)
		var click_ev = document.createEvent("MouseEvents");
		click_ev.initEvent("click", true, true);
		li.dispatchEvent(click_ev);
		// register the handler for changing the filename in the tablist
		actModel.events.registerHandler('filenameBeingChangedEvent', function() {
			li.textContent = actModel.getFilename();
			log.info('new filename: ' + actModel.getFilename());
		});
	}
	
	returnObj.getCanvasModelViewList = function(){
		return modelViewList;
	}
	
	returnObj.getEditorProperties = function(){
		return editorProperties;
	}
	
	returnObj.getActModelView = function() {
		for (var i = 0; i < modelViewList.length; i++) {
			if (modelViewList[i].getModel() === modelList.getActModel()) {
				return modelViewList[i];
			}
		}
		return null;
	}
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	returnObj.addActModelToView(); // add first model manually, because it has been created before registering the event handlers

	// register event handlers
	modelList.events.registerHandler('newModelAddedEvent', newModelAddedEventHandler);
	modelList.events.registerHandler('removingModelEvent', removingModelEventHandler);	
	canvasTabPanel.events.registerHandler('tabSwitchedEvent', tabSwitchedEventHandler);
	modelList.events.registerHandler('actModelChangedEvent', actModelChangedEventHandler);
	
	return returnObj;
}