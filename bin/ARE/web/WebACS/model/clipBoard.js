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
 
ACS.clipBoard = function() {

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var components = []; // Array<ACS.component>
	var dataChannels = []; // Array<ACS.dataChannel>
	var eventChannels = []; // Array<ACS.eventChannel>
	var removedComponentsList = []; // list of all components that were not found in the componentCollection
	var changedComponentsList = []; // list of all components that did not match the componentCollection
	var removedSingletonComponentsList = []; // list of all singleton-components that were already in the model and have therefore not been pasted
	var portList = []; // array of all copied ports
	var eventList = []; // array of all copied events

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var copyProperty = function(oldProp) {
		var newProp = ACS.property(	oldProp.getKey().valueOf(),
									oldProp.getType(),
									oldProp.value.valueOf());
		newProp.description = oldProp.description.valueOf();
		newProp.combobox = oldProp.combobox.valueOf();
		newProp.getStringList = oldProp.getStringList;	
	
		return newProp;
	}
	
	var copyPort = function(oldPort, newParentComponent) {
		var newPort = ACS.port(	oldPort.getId().valueOf(),
								newParentComponent,
								oldPort.getType(),
								oldPort.getDataType(),
								oldPort.getPosition(),
								oldPort.getMustBeConnected());
		for (var i = 0; i < oldPort.propertyList.length; i++) {
			newPort.propertyList[i] = copyProperty(oldPort.propertyList[i]);
		}
		newPort.sync = oldPort.sync;
					
		return newPort;
	}
	
	var copyEvent = function(oldEvent, newParentComponent) {
		var newEvent = ACS.event(oldEvent.getId().valueOf(),
								 oldEvent.getDescription().valueOf(),
								 newParentComponent)
	
		return newEvent;
	}
	
	var countExtensions = function(id) {
		var count = 0;
		var extLength = ACS.mConst.CLIPBOARD_IDEXTENSION.length;
		while (id.substr(-extLength, extLength) === ACS.mConst.CLIPBOARD_IDEXTENSION) {
			id = id.slice(0, id.length - extLength);
			count++
		}
		
		return count;
	}
	
	var findPort = function(oldPort, extendId) {
		var newPort = null; // will remain null, if not found in list
		var i = 0;
		while (!newPort && (i < portList.length)) {
			var ext = '';
			if (extendId) {
				ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
			}
			if ((portList[i].getId() === oldPort.getId()) && (portList[i].getParentComponent().getId() === oldPort.getParentComponent().getId() + ext)) {
				newPort = portList[i];
			}
			i++;
		}
		
		return newPort;
	}
	
	var findEvent = function(oldEvent, extendId) {
		var newEvent = null; // will remain null, if not found in list
		var i = 0;
		while (!newEvent && (i < eventList.length)) {
			var ext = '';
			if (extendId) {
				ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
			}
			if ((eventList[i].getId() === oldEvent.getId()) && (eventList[i].getParentComponent().getId() === oldEvent.getParentComponent().getId() + ext)) {
				newEvent = eventList[i];
			}
			i++;
		}
		
		return newEvent;
	}

	var findComponent = function(compList, oldComp, extendId) {
		var newComp = null; // will remain null, if not found in list
		var i = 0;
		while (!newComp && (i < compList.length)) {
			var ext = '';
			if (extendId) {
				ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
			}
			if (compList[i].getId() === oldComp.getId() + ext) {
				newComp = compList[i];
			}
			i++;
		}
		
		return newComp;
	}
	
	var deepCopyComponentList = function(oldList, extendId) {
		var newList = [];
		// clear port- and event-list before new copy
		portList = [];
		eventList = [];
		// decide on extension for id
		var ext = '';
		if (extendId) {
			ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
		}
		// do the copying
		for (var i = 0; i < oldList.length; i++) {
			var comp = ACS.component(oldList[i].getId().valueOf() + ext,
									 oldList[i].getComponentTypeId().valueOf(),
									 oldList[i].getDescription().valueOf(),
									 oldList[i].getSingleton(),
									 oldList[i].getX(),
									 oldList[i].getY(),
									 oldList[i].getType(),
									 true, // always true, so that pasted content is always selected
									 false); // component is checked for existence in component-collection before pasting
			for (var j = 0; j < oldList[i].inputPortList.length; j++) {
				comp.inputPortList[j] = copyPort(oldList[i].inputPortList[j], comp);
				portList.push(comp.inputPortList[j]);
			}
			for (var j = 0; j < oldList[i].outputPortList.length; j++) {
				comp.outputPortList[j] = copyPort(oldList[i].outputPortList[j], comp);
				portList.push(comp.outputPortList[j]);
			}
			for (var j = 0; j < oldList[i].listenEventList.length; j++) {
				comp.listenEventList[j] = copyEvent(oldList[i].listenEventList[j], comp);
				eventList.push(comp.listenEventList[j]);
			}
			for (var j = 0; j < oldList[i].triggerEventList.length; j++) {
				comp.triggerEventList[j] = copyEvent(oldList[i].triggerEventList[j], comp);
				eventList.push(comp.triggerEventList[j]);
			}
			for (var j = 0; j < oldList[i].propertyList.length; j++) {
				comp.propertyList[j] = copyProperty(oldList[i].propertyList[j]);
			}
			if (oldList[i].gui) {
				comp.gui = ACS.gui(oldList[i].gui.getX(),
								   oldList[i].gui.getY(),
								   oldList[i].gui.getWidth(),
								   oldList[i].gui.getHeight(),
								   oldList[i].gui.getIsExternal())
			}
			newList.push(comp);
		}		
	
		return newList;
	}
	
	var deepCopyDataChannels = function(oldList, extendId) {
		var newList = [];
		var ext = '';
		if (extendId) {
			ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
		}		
		for (var i = 0; i < oldList.length; i++) {
			var channel = ACS.dataChannel(oldList[i].getId().valueOf() + ext, findPort(oldList[i].getOutputPort(), extendId),findPort(oldList[i].getInputPort(), extendId));
			channel.setIsSelected(true); // elements shall be selected, when pasted
			channel.description = oldList[i].description.valueOf();
			if ((channel.getInputPort() !== null) && (channel.getOutputPort() !== null)) { // only keep the channel, if it has a source and a target within the copied components
				newList.push(channel);
			}
		}
		
		return newList;
	}
	
	var deepCopyEventChannels = function(compList, oldList, extendId) {
		var newList = [];
		var ext = '';
		if (extendId) {
			ext = ACS.mConst.CLIPBOARD_IDEXTENSION;
		}	
		for (var i = 0; i < oldList.length; i++) {
			var channel = ACS.eventChannel(oldList[i].getId().valueOf() + ext);
			channel.setIsSelected(true); // elements shall be selected, when pasted
			channel.startComponent = findComponent(compList, oldList[i].startComponent,extendId);
			channel.endComponent = findComponent(compList, oldList[i].endComponent,extendId);
			for (var j = 0; j < oldList[i].eventConnections.length; j++) {
				channel.eventConnections[j] = {trigger: findEvent(oldList[i].eventConnections[j].trigger, extendId), listener: findEvent(oldList[i].eventConnections[j].listener, extendId), description: oldList[i].eventConnections[j].description.valueOf()};
			}
			if ((channel.startComponent !== null) && (channel.endComponent !== null)) { // only keep the channel, if it has a source and a target within the copied components
				newList.push(channel);
			}
		}
		
		return newList;
	}
	
	var isInList = function(comp, pasteComponents) {
		for (var i = 0; i < pasteComponents.length; i++) {
			if (pasteComponents[i] === comp) return true;
		}
		return false;
	}
	
	var singletonAndAlreadyInModel = function(model, fullComponent, pasteComponent) {
		var singleton = fullComponent.getElementsByTagName('singleton');
		if (singleton[0].textContent === 'true') {
			for (var i = 0; i < model.componentList.length; i++) {
				if (model.componentList[i].getComponentTypeId() === pasteComponent.getComponentTypeId()) return true;
			}
		}
		return false;
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};

	returnObj.cut = function(model) {
		returnObj.copy(model);
		var remAct = ACS.removeItemListAction(model, model.selectedItemsList);
		remAct.execute();
	}

	returnObj.copy = function(model) { // deep-copy of all elements in model.selectedItemsList
		var tempCompList = [];
		var tempDataChannelList = [];
		var tempEventChannelList = [];
		
		// sort all elements that need to be copied
		for (var i = 0; i < model.selectedItemsList.length; i++) {
			if (typeof model.selectedItemsList[i].getComponentTypeId === 'function') { // must be a component
				tempCompList.push(model.selectedItemsList[i]);
			} else {
				if (typeof model.selectedItemsList[i].startComponent === 'undefined') { // must be a dataChannel
					tempDataChannelList.push(model.selectedItemsList[i]);
				} else { // must be an eventChannel
					tempEventChannelList.push(model.selectedItemsList[i]);
				}
			}
		
		}
		if (tempCompList.length > 0) { // if at least one component is to be copied (channels cannot be copied without corresponding components)
			// deep-copy them to the clipboard
			components = deepCopyComponentList(tempCompList, true);
			dataChannels = deepCopyDataChannels(tempDataChannelList, true);
			eventChannels = deepCopyEventChannels(components, tempEventChannelList, true);
		}
	}

	returnObj.paste = function(model) {
		if (components.length > 0) { // if at least one component is to be pasted (channels cannot be copied without corresponding components)
			// clear selection in the model
			model.deSelectAll();
		
			// deep-copy all elements
			var pasteComponents = deepCopyComponentList(components, false);
			var pasteDataChannels = deepCopyDataChannels(dataChannels, false);
			var pasteEventChannels = deepCopyEventChannels(pasteComponents, eventChannels, false);
			
			// reset removed- and mismatch-lists
			removedComponentsList = [];
			changedComponentsList = [];
			removedSingletonComponentsList = [];
			
			// check components
			var i = 0;
			while (i < pasteComponents.length) {
				var fullComponent = model.findComponentInCollection(pasteComponents[i].getComponentTypeId(), model.getComponentCollection());
				if (fullComponent) {
					// check if component is singleton and if yes, if it can be pasted or if it would be the second of its kind
					if (!singletonAndAlreadyInModel(model, fullComponent, pasteComponents[i])) {
						// mark the component as existent
						pasteComponents[i].foundInComponentCollection = true;
						// check, if the component's id already exists in the model and, if so, add another extension
						var j = 0;
						while (j < model.componentList.length) {
							if (model.componentList[j].getId() === pasteComponents[i].getId()) {
								pasteComponents[i].setId(pasteComponents[i].getId() + ACS.mConst.CLIPBOARD_IDEXTENSION);
								j = 0; // must start checking from the beginning, since the ID has changed and all components have to be checked for the new ID
							} else {
								j++;
							}
						}
						// avoid pasting components with the same id
						var j = 0;
						while (j < pasteComponents.length) {
							if (j !== i) { // avoid comparing with self
								if (pasteComponents[j].getId() === pasteComponents[i].getId()) {
									pasteComponents[i].setId(pasteComponents[i].getId() + ACS.mConst.CLIPBOARD_IDEXTENSION);
									j = 0; // must start checking from the beginning, since the ID has changed and all components have to be checked for the new ID
								} else {
									j++;
								}
							} else {
								j++;
							}
						}
						// check if the component's position in the model-graph is free
						var newPos = model.getFreePosition([pasteComponents[i].getX(), pasteComponents[i].getY()]);
						pasteComponents[i].setNewPosition(newPos[0], newPos[1]);
						// avoid pasting several components at the same position
						var j = 0;
						while (j < pasteComponents.length) {
							if (j !== i) { // avoid comparing with self
								if ((pasteComponents[i].getX() === pasteComponents[j].getX()) && (pasteComponents[i].getY() === pasteComponents[j].getY())) {
									pasteComponents[i].setNewPosition(pasteComponents[i].getX() + ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETX, pasteComponents[i].getY() + ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETY);
									j = 0; // must start checking from the beginning, since the position has changed and all components have to be checked for the new position
								} else {
									j++;
								}
							} else {
								j++;
							}
						}
						// check if the input ports match the component collection
						var inputPortsFull = fullComponent.getElementsByTagName('inputPort');
						if (inputPortsFull.length < pasteComponents[i].inputPortList.length) {
							pasteComponents[i].inputPortList.splice(inputPortsFull.length, pasteComponents[i].inputPortList.length - inputPortsFull.length);
							pasteComponents[i].matchesComponentCollection = false;
						} else if (inputPortsFull.length > pasteComponents[i].inputPortList.length) {
							for (var j = pasteComponents[i].inputPortList.length; j < inputPortsFull.length; j++) {
								pasteComponents[i].inputPortList.push(ACS.port(	inputPortsFull.item(j).attributes.getNamedItem('id').textContent,
																				pasteComponents[i],
																				ACS.portType.INPUT,
																				model.getDataType(inputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent),
																				j,
																				inputPortsFull.item(j).getElementsByTagName('mustBeConnected').item(0).textContent));
								// TODO: add the port's properties
							}
							pasteComponents[i].matchesComponentCollection = false;
						}
						// check if the output ports match the component collection
						var outputPortsFull = fullComponent.getElementsByTagName('outputPort');
						if (outputPortsFull.length < pasteComponents[i].outputPortList.length) {
							pasteComponents[i].outputPortList.splice(outputPortsFull.length, pasteComponents[i].outputPortList.length - outputPortsFull.length);
							pasteComponents[i].matchesComponentCollection = false;
						} else if (outputPortsFull.length > pasteComponents[i].outputPortList.length) {
							for (var j = pasteComponents[i].outputPortList.length; j < outputPortsFull.length; j++) {
								pasteComponents[i].outputPortList.push(ACS.port(	outputPortsFull.item(j).attributes.getNamedItem('id').textContent,
																					pasteComponents[i],
																					ACS.portType.OUTPUT,
																					model.getDataType(outputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent),
																					j,
																					false));
								// TODO: add the port's properties
							}
							pasteComponents[i].matchesComponentCollection = false;
						}
						// check if the listener events match the component collection
						var listenEvents = fullComponent.getElementsByTagName('eventListenerPort');
						if (listenEvents.length < pasteComponents[i].listenEventList.length) {
							pasteComponents[i].listenEventList.splice(listenEvents.length, pasteComponents[i].listenEventList.length - listenEvents.length);
							pasteComponents[i].matchesComponentCollection = false;
						} else if (listenEvents.length > pasteComponents[i].listenEventList.length) {
							for (var j = pasteComponents[i].listenEventList.length; j < listenEvents.length; j++) {
								pasteComponents[i].listenEventList.push(ACS.event(	listenEvents.item(j).attributes.getNamedItem('id').textContent,
																				listenEvents.item(j).getElementsByTagName('description').item(0).textContent,
																				pasteComponents[i]));
							}
							pasteComponents[i].matchesComponentCollection = false;
						}
						// check if the trigger events match the component collection
						var triggerEvents = fullComponent.getElementsByTagName('eventTriggererPort');
						if (triggerEvents.length < pasteComponents[i].triggerEventList.length) {
							pasteComponents[i].triggerEventList.splice(triggerEvents.length, pasteComponents[i].triggerEventList.length - triggerEvents.length);
							pasteComponents[i].matchesComponentCollection = false;
						} else if (triggerEvents.length > pasteComponents[i].triggerEventList.length) {
							for (var j = pasteComponents[i].triggerEventList.length; j < triggerEvents.length; j++) {
								pasteComponents[i].triggerEventList.push(ACS.event(	triggerEvents.item(j).attributes.getNamedItem('id').textContent,
																				triggerEvents.item(j).getElementsByTagName('description').item(0).textContent,
																				pasteComponents[i]));
							}
							pasteComponents[i].matchesComponentCollection = false;
						}				
						// check if the properties match the component collection
						var propertiesFull = fullComponent.getElementsByTagName('property');
						if (propertiesFull.length < pasteComponents[i].propertyList.length) {
							pasteComponents[i].propertyList.splice(propertiesFull.length, pasteComponents[i].propertyList.length - propertiesFull.length);
							pasteComponents[i].matchesComponentCollection = false;
						} else if (propertiesFull.length > pasteComponents[i].propertyList.length) {
							for (var j = pasteComponents[i].propertyList.length; j < propertiesFull.length; j++) {
								pasteComponents[i].propertyList.push(ACS.property(	propertiesFull.item(j).attributes.getNamedItem('name').textContent, 
																				model.getDataType(propertiesFull.item(j).attributes.getNamedItem('type').textContent), 
																				propertiesFull.item(j).attributes.getNamedItem('value').textContent));
								if (propertiesFull.item(j).attributes.getNamedItem('description'))
									pasteComponents[i].propertyList[pasteComponents[i].propertyList.length - 1].description = propertiesFull.item(j).attributes.getNamedItem('description').textContent;
								if (propertiesFull.item(j).attributes.getNamedItem('combobox'))
									pasteComponents[i].propertyList[pasteComponents[i].propertyList.length - 1].combobox = propertiesFull.item(j).attributes.getNamedItem('combobox').textContent;
								if (propertiesFull.item(j).attributes.getNamedItem('getStringList'))
									pasteComponents[i].propertyList[pasteComponents[i].propertyList.length - 1].getStringList = propertiesFull.item(j).attributes.getNamedItem('getStringList').textContent;					
							}
							pasteComponents[i].matchesComponentCollection = false;
						}
						if ((pasteComponents[i]) && (!pasteComponents[i].matchesComponentCollection)) {
							changedComponentsList.push(pasteComponents[i]);
						}					
						i++;
					} else {
						removedSingletonComponentsList.push(pasteComponents[i]);
						pasteComponents.splice(i, 1);
					}
				} else {
					removedComponentsList.push(pasteComponents[i]);
					pasteComponents.splice(i, 1);
				}
			}

			// check dataChannels
			var i = 0;
			while (i < pasteDataChannels.length) {
				if (!isInList(pasteDataChannels[i].getInputPort().getParentComponent(), pasteComponents) || !isInList(pasteDataChannels[i].getOutputPort().getParentComponent(), pasteComponents)) {
					pasteDataChannels.splice(i, 1); // delete channel if either end-component is not in the list of components to be pasted
				} else {
					i++;
				}
			}		
			
			// check eventChannels
			var i = 0;
			while (i < pasteEventChannels.length) {
				if (!isInList(pasteEventChannels[i].startComponent, pasteComponents) || !isInList(pasteEventChannels[i].endComponent, pasteComponents)) {
					pasteEventChannels.splice(i, 1); // delete channel if either end-component is not in the list of components to be pasted
				} else {
					i++;
				}
			}	
			
			var addAct = ACS.addItemsAction(model, pasteComponents, pasteDataChannels, pasteEventChannels);
			addAct.execute();
			model.events.fireEvent('alertUserOfComponentCollectionMismatchEvent'); // needed in case the pasted parts do not match the component collection (alerts the user)
			if (removedSingletonComponentsList.length > 0) model.events.fireEvent('alertUserOfRemovedSingletonComponentsEvent');
		}
	}

	returnObj.getRemovedComponentsList = function() {
		return removedComponentsList;
	}
	
	returnObj.getChangedComponentsList = function() {
		return changedComponentsList;
	}
	
	returnObj.getRemovedSingletonComponentsList = function() {
		return removedSingletonComponentsList;
	}	
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
		
	return returnObj;
}