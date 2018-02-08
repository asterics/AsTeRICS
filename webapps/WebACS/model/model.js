/*
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
 
 ACS.model = function(filename) { // String

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var componentCollection; // XML Document
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var loadDefaultComponentCollection = function() {
		var xmlObj = null;
		if (window.location.port === '8081') { // if WebACS is hosted by ARE webservice, use componentCollection from the ARE
			$.ajax({
				url: ACS.mConst.MODEL_DEFAULTCOMPONENTCOLLECTIONONARE,
				dataType: 'xml',
				success: function(data) {
							log.debug('success reading component collection from ARE');
							xmlObj = data;
						},
				error: function() {
							log.debug('failed to read component collection from ARE');
							$.ajax({
								url: ACS.mConst.MODEL_DEFAULTCOMPONENTCOLLECTION,
								dataType: 'xml',
								success: function(data) {
											log.debug('success reading default component collection as fallback');
											xmlObj = data;
										},
								error: function() {
											log.debug('failed to read default component collection as fallback');
										},
								async: false
							});
						},
				async: false
			});
		} else {
			$.ajax({
				url: ACS.mConst.MODEL_DEFAULTCOMPONENTCOLLECTION,
				dataType: 'xml',
				success: function(data) {
							log.debug('success reading default component collection');
							xmlObj = data;
						},
				error: function() {
							log.debug('failed to read default component collection');
						},
				async: false
			});
		}
		return xmlObj;
	}
	
	var findPortByNameInXML = function(portName, portsXML) {
		for (var i = 0; i < portsXML.length; i++) {
			if (portsXML.item(i).attributes.getNamedItem('portTypeID').textContent === portName) return portsXML.item(i);
		}
		return null;
	}
	
	var loadComponentList = function(modelXML) {
		var componentList = [];
		var components_section = modelXML.getElementsByTagName('components').item(0); // needed so that the component-tags of the channels-sections are not confused with the components of the model
		var components = components_section.getElementsByTagName('component');
		if (components) {
			for (var i = 0; i < components.length; i++) {
				// find the current component's componentTypeId
				compTypeId = components.item(i).attributes.getNamedItem('type_id').textContent;
				// check if the component has a description
				var description = null;
				if (components.item(i).getElementsByTagName('description')) description = components.item(i).getElementsByTagName('description').item(0).textContent;
				// seek the component in the componentCollection, since not all information is saved in the model
				var fullComponent = returnObj.findComponentInCollection(compTypeId, componentCollection);
				if (fullComponent === null) { // i.e. component was not found in componentCollection
					componentList[i] = ACS.component(	components.item(i).attributes.getNamedItem('id').textContent,
														compTypeId,
														'',
														false,
														0,
														0,
														0,
														false,
														false);
				} else {
					// find out the component's type:
					var compType = 0;
					switch (fullComponent.getElementsByTagName('type').item(0).textContent) {
						case 'actuator': 	compType = ACS.componentType.ACTUATOR;
											break;
						case 'processor':	compType = ACS.componentType.PROCESSOR;
											break;
						case 'sensor':		compType = ACS.componentType.SENSOR;
											break;					
					}
					
					// build the component-object:
					componentList[i] = ACS.component(	components.item(i).attributes.getNamedItem('id').textContent,
														compTypeId,
														description,
														fullComponent.getElementsByTagName('singleton').item(0).textContent,
														parseInt(components.item(i).getElementsByTagName('layout').item(0).getElementsByTagName('posX').item(0).textContent),
														parseInt(components.item(i).getElementsByTagName('layout').item(0).getElementsByTagName('posY').item(0).textContent),
														compType,
														false,
														true);
																
					// build inputPortList:
					var inputPortsFull = fullComponent.getElementsByTagName('inputPort');
					var inputPortsModel = components.item(i).getElementsByTagName('inputPort');
					if (inputPortsFull.length !== inputPortsModel.length) {
						componentList[i].matchesComponentCollection = false;
					}
					for (var j = 0; j < inputPortsFull.length; j++) {
						var portInModel = findPortByNameInXML(inputPortsFull.item(j).attributes.getNamedItem('id').textContent, inputPortsModel);
						if (portInModel) {
							//take portTypeId from model
							var portTypeId = portInModel.attributes.getNamedItem('portTypeID').textContent;
						} else {
							componentList[i].matchesComponentCollection = false;
							//take it from full
							var portTypeId = inputPortsFull.item(j).attributes.getNamedItem('id').textContent;
						}
						var portDataType = returnObj.getDataType(inputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent);
						// build the port object:
						componentList[i].inputPortList[j] = ACS.port(	portTypeId,
																		componentList[i],
																		ACS.portType.INPUT,
																		portDataType,
																		j,
																		inputPortsFull.item(j).getElementsByTagName('mustBeConnected').item(0).textContent);
						if (portInModel && (portInModel.attributes.getNamedItem('sync'))) {
							componentList[i].inputPortList[j].sync = portInModel.attributes.getNamedItem('sync').textContent;
						}
					}
					
					// build outputPortList:
					var outputPortsFull = fullComponent.getElementsByTagName('outputPort');
					var outputPortsModel = components.item(i).getElementsByTagName('outputPort');
					if (outputPortsFull.length !== outputPortsModel.length) {
						componentList[i].matchesComponentCollection = false;
					}					
					for (var j = 0; j < outputPortsFull.length; j++) {
						var portInModel = findPortByNameInXML(outputPortsFull.item(j).attributes.getNamedItem('id').textContent, outputPortsModel);
						if (portInModel) {
							//take portTypeId from model
							var portTypeId = portInModel.attributes.getNamedItem('portTypeID').textContent;
						} else {
							componentList[i].matchesComponentCollection = false;
							//take it from full
							var portTypeId = outputPortsFull.item(j).attributes.getNamedItem('id').textContent;
						}
						var portDataType = returnObj.getDataType(outputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent);
						// build the port object:
						componentList[i].outputPortList[j] = ACS.port(	portTypeId,
																		componentList[i],
																		ACS.portType.OUTPUT,
																		portDataType,
																		j,
																		false);
					}
					
					// build listenEventList:
					// Note: since the eventTriggers and eventListeners are not saved in deployment model, it is not possible at this point to determine whether they match the component collection;
					// instead this is checked in "loadEventChannelList" for all triggers and listeners that are in use (i.e. connected by a channel)
					var listenEventsFull = fullComponent.getElementsByTagName('eventListenerPort');
					for (var j = 0; j < listenEventsFull.length; j++) {
						// build the event object:
						componentList[i].listenEventList[j] = ACS.event(listenEventsFull.item(j).attributes.getNamedItem('id').textContent,
																		listenEventsFull.item(j).getElementsByTagName('description').item(0).textContent,
																		componentList[i]);
					}					
					
					// build triggerEventList:
					// Note: since the eventTriggers and eventListeners are not saved in deployment model, it is not possible at this point to determine whether they match the component collection;
					// instead this is checked in "loadEventChannelList" for all triggers and listeners that are in use (i.e. connected by a channel)					
					var triggerEventsFull = fullComponent.getElementsByTagName('eventTriggererPort');
					for (var j = 0; j < triggerEventsFull.length; j++) {
						// build the event object:
						componentList[i].triggerEventList[j] = ACS.event(	triggerEventsFull.item(j).attributes.getNamedItem('id').textContent,
																			triggerEventsFull.item(j).getElementsByTagName('description').item(0).textContent,
																			componentList[i]);
					}				

					// build propertyList:
					var propertiesFull = fullComponent.getElementsByTagName('property');
					var propertiesModel = components.item(i).getElementsByTagName('property');
					if (propertiesFull.length != propertiesModel.length) {
						componentList[i].matchesComponentCollection = false;
					}
					for (var j = 0; j < propertiesFull.length; j++) {
						var propIdx = indexOfPropertyInModel(propertiesFull.item(j).attributes.getNamedItem('name').textContent, propertiesModel);
						var propertyValue = '';
						if (propIdx > -1) { // -1 if property not yet existent in model
							propertyValue = propertiesModel.item(propIdx).attributes.getNamedItem('value').textContent;
						} else {
							propertyValue = propertiesFull.item(j).attributes.getNamedItem('value').textContent;
							componentList[i].matchesComponentCollection = false;
						}
						componentList[i].propertyList.push(ACS.property(propertiesFull.item(j).attributes.getNamedItem('name').textContent,
																		returnObj.getDataType(propertiesFull.item(j).attributes.getNamedItem('type').textContent),
																		propertyValue));
						if (propertiesFull.item(j).attributes.getNamedItem('description'))
							componentList[i].propertyList[componentList[i].propertyList.length - 1].description = propertiesFull.item(j).attributes.getNamedItem('description').textContent;
						if (propertiesFull.item(j).attributes.getNamedItem('combobox'))
							componentList[i].propertyList[componentList[i].propertyList.length - 1].combobox = propertiesFull.item(j).attributes.getNamedItem('combobox').textContent;
						if (propertiesFull.item(j).attributes.getNamedItem('getStringList'))
							componentList[i].propertyList[componentList[i].propertyList.length - 1].getStringList = propertiesFull.item(j).attributes.getNamedItem('getStringList').textContent;				
					}
					
					// build the gui object:
					if (components.item(i).getElementsByTagName('gui')) {
						var isExternal = false;
						if (fullComponent.getElementsByTagName('gui').item(0) && fullComponent.getElementsByTagName('gui').item(0).attributes.getNamedItem('IsExternalGUIElement')) {
							isExternal = fullComponent.getElementsByTagName('gui').item(0).attributes.getNamedItem('IsExternalGUIElement').textContent;
						}
						if (components.item(i).getElementsByTagName('gui').item(0) ) {
							componentList[i].gui = ACS.gui(	parseInt(components.item(i).getElementsByTagName('gui').item(0).getElementsByTagName('posX').item(0).textContent),
															parseInt(components.item(i).getElementsByTagName('gui').item(0).getElementsByTagName('posY').item(0).textContent),
															parseInt(components.item(i).getElementsByTagName('gui').item(0).getElementsByTagName('width').item(0).textContent),
															parseInt(components.item(i).getElementsByTagName('gui').item(0).getElementsByTagName('height').item(0).textContent),
															isExternal);
						}
					}
				}
			}
		}
		return componentList;
	}
	
	var findComponentById = function(compList, compId) {
		for (var i = 0; i < compList.length; i++) {
			if (compList[i].getId() === compId) return compList[i];
		}
		return null;
	}
	
	var findPortById = function(comp, portId, isInputPort) {
		if (isInputPort) {
			var portList = comp.inputPortList;
		} else {
			var portList = comp.outputPortList;
		}
		for (var i = 0; i < portList.length; i++) {
			if (portList[i].getId() === portId) return portList[i];
		}
		return null;
	}
	
	var findPropertyByKey = function(comp, propertyKey) {
		for (var i = 0; i < comp.propertyList.length; i++) {
			if (comp.propertyList[i].getKey() === propertyKey) return comp.propertyList[i];
		}
		return null;
	}	
	
	var loadDataChannelList = function(modelXML, componentList) {
		var dataChannelList = [];
		var channels_section = modelXML.getElementsByTagName('channels').item(0);
		if (channels_section) {
			var channels = channels_section.getElementsByTagName('channel');
			for (var i = 0; i < channels.length; i++) {
				var channelId = channels.item(i).attributes.getNamedItem('id').textContent;
				var channelDescription = '';
				if (channels.item(i).getElementsByTagName('description').item(0)) {
					channelDescription = channels.item(i).getElementsByTagName('description').item(0).textContent;
				}
				// get the source port:
				var sourceCompId = channels.item(i).getElementsByTagName('source').item(0).getElementsByTagName('component').item(0).attributes.getNamedItem('id').textContent;
				var sourcePortId = channels.item(i).getElementsByTagName('source').item(0).getElementsByTagName('port').item(0).attributes.getNamedItem('id').textContent;
				var sourceComponent = findComponentById(componentList, sourceCompId);
				if (sourceComponent && (sourceComponent.foundInComponentCollection)) {
					var outPort = findPortById(sourceComponent, sourcePortId, false);
					if (outPort) {
						// get the target port:
						var targetCompId = channels.item(i).getElementsByTagName('target').item(0).getElementsByTagName('component').item(0).attributes.getNamedItem('id').textContent;
						var targetPortId = channels.item(i).getElementsByTagName('target').item(0).getElementsByTagName('port').item(0).attributes.getNamedItem('id').textContent;
						var targetComponent = findComponentById(componentList, targetCompId);
						if (targetComponent && (targetComponent.foundInComponentCollection)) {
							var inPort = findPortById(targetComponent, targetPortId, true);
							if (inPort) {
								dataChannelList.push(ACS.dataChannel(channelId, outPort, inPort));
								dataChannelList[dataChannelList.length-1].description = channelDescription;
							}
						}
					}
				}
			}
		}
		return dataChannelList;
	}
	
	var findEventById = function(comp, eventId, isListener) {
		if (isListener) {
			var eventList = comp.listenEventList;
		} else {
			var eventList = comp.triggerEventList;
		}
		for (var i = 0; i < eventList.length; i++) {
			if (eventList[i].getId() === eventId) return eventList[i];
		}
		return null;
	}
	
	var seekChannelInList = function(channelList, channelId) {
		for (var i = 0; i < channelList.length; i++) {
			if (channelList[i].getId() === channelId) return channelList[i];
		}
		return null;
	}
	
	var loadEventChannelList = function(modelXML, componentList) {
		var eventChannelList = [];
		var eventChannels_section = modelXML.getElementsByTagName('eventChannels').item(0);
		if (eventChannels_section) {
			var eventChannels = eventChannels_section.getElementsByTagName('eventChannel');
			for (var i = 0; i < eventChannels.length; i++) {
				var startCompId = eventChannels.item(i).getElementsByTagName('source').item(0).getElementsByTagName('component').item(0).attributes.getNamedItem('id').textContent;
				var endCompId = eventChannels.item(i).getElementsByTagName('target').item(0).getElementsByTagName('component').item(0).attributes.getNamedItem('id').textContent;
				var startComp = findComponentById(componentList, startCompId);
				var endComp = findComponentById(componentList, endCompId);
				if ((startComp && startComp.foundInComponentCollection) && (endComp && endComp.foundInComponentCollection)) { // component might have been removed, because not in componentCollection
					var triggerEventId = eventChannels.item(i).getElementsByTagName('source').item(0).getElementsByTagName('eventPort').item(0).attributes.getNamedItem('id').textContent;
					var listenerEventId = eventChannels.item(i).getElementsByTagName('target').item(0).getElementsByTagName('eventPort').item(0).attributes.getNamedItem('id').textContent;				
					var triggerEvent = findEventById(startComp, triggerEventId, false);
					var listenerEvent = findEventById(endComp, listenerEventId, true);
					if (!triggerEvent) {
						startComp.matchesComponentCollection = false;
					} else if (!listenerEvent) {
						endComp.matchesComponentCollection = false;
					} else {
						var channelId = startCompId + '_' + endCompId;
						var actChannel = seekChannelInList(eventChannelList, channelId);
						if (!actChannel) {
							eventChannelList.push(ACS.eventChannel(channelId));
							eventChannelList[eventChannelList.length-1].startComponent = startComp;
							eventChannelList[eventChannelList.length-1].endComponent = endComp;
							actChannel = eventChannelList[eventChannelList.length-1];
						}
						var desc = '';
						if (eventChannels.item(i).getElementsByTagName('description').item(0)) desc = eventChannels.item(i).getElementsByTagName('description').item(0).textContent;
						actChannel.eventConnections.push({	trigger: triggerEvent,
															listener: listenerEvent,
															description: desc});
					}
				}
			}
		}
		return eventChannelList;
	}
	
	var loadModelGui = function(modelXML) {
		if (modelXML.getElementsByTagName('modelGUI').item(0)) {
			var modelGui = modelXML.getElementsByTagName('modelGUI').item(0);
			returnObj.modelGui.setDecoration(modelGui.getElementsByTagName('Decoration').item(0).textContent === 'true');
			returnObj.modelGui.setFullScreen(modelGui.getElementsByTagName('Fullscreen').item(0).textContent === 'true');
			returnObj.modelGui.setAlwaysOnTop(modelGui.getElementsByTagName('AlwaysOnTop').item(0).textContent === 'true');
			returnObj.modelGui.setToSystemTray(modelGui.getElementsByTagName('ToSystemTray').item(0).textContent === 'true');
			returnObj.modelGui.setShowControlPanel(modelGui.getElementsByTagName('ShopControlPanel').item(0).textContent === 'true'); // TODO: change to "ShowControlPanel", when changed in XML
			var guiWindow = modelGui.getElementsByTagName('AREGUIWindow').item(0);
			returnObj.modelGui.areGuiWindow.setNewPosition({x: guiWindow.getElementsByTagName('posX').item(0).textContent, y: guiWindow.getElementsByTagName('posY').item(0).textContent});
			returnObj.modelGui.areGuiWindow.setNewSize({width: guiWindow.getElementsByTagName('width').item(0).textContent, height: guiWindow.getElementsByTagName('height').item(0).textContent});
		}
	}
	
	var loadMetaDataList = function(modelXML) {
		var metaDataList = [];
		var descSection = modelXML.getElementsByTagName('modelDescription').item(0);
		if (descSection) {
			var i = 0;
			if (descSection.getElementsByTagName('shortDescription').item(0)) {
				metaDataList[i] = ACS.metaData('shortDescription', descSection.getElementsByTagName('shortDescription').item(0).textContent);
				i++;
			}
			if (descSection.getElementsByTagName('requirements').item(0)) {
				metaDataList[i] = ACS.metaData('requirements', descSection.getElementsByTagName('requirements').item(0).textContent);
				i++;
			}
			if (descSection.getElementsByTagName('description').item(0)) {
				metaDataList[i] = ACS.metaData('description', descSection.getElementsByTagName('description').item(0).textContent);
				i++;
			}			
		}
		return metaDataList;
	}
	
	var compNameFound = function(name) {
		for (var i = 0; i < returnObj.componentList.length; i++) {
			if (returnObj.componentList[i].getId() === name) return true;
		}
		return false;
	}
	
	var nextCompNameNumber = function(compName) {
		var num = 1;
		while (compNameFound(compName + '.' + num)) num++;
		return num;
	}
	
	var positionTaken = function(pos) {
		for (var i = 0; i < returnObj.componentList.length; i++) {
			if ((returnObj.componentList[i].getX() === pos[0]) && (returnObj.componentList[i].getY() === pos[1])) return true;
		}
		return false;
	}
	
	var generateModelName = function() {
		var time = new Date();
		var d = time.getDay();
		var m = time.getMonth();
		var y = time.getFullYear();
		var h = time.getHours();
		var min = time.getMinutes();
		if (d < 10) d = '0' + d;
		if (m < 10) m = '0' + m;
		if (h < 10) h = '0' + h;
		if (min < 10) min = '0' + min;
		return 'model_nr_' + Math.floor((Math.random() * 10000000000000) + 1) + '_created_at_' + y + '_' + m + '_' + d + '_' + h + '_' + min;
	}
	
	var metaDataIndexOfKey = function(key) {
		for (var i = 0; i < returnObj.metaDataList.length; i++) {
			if (returnObj.metaDataList[i].getKey() === key) return i;
		}
		return -1;
	}
	
	var indexOfPropertyInModel = function(name, propertiesModel) {
		for (var i = 0; i < propertiesModel.length; i++) {
			if (propertiesModel.item(i).attributes.getNamedItem('name').textContent === name) {
				return i;
			}
		}
		return -1;
	}
	
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.componentList = []; // Array<ACS.component>
	returnObj.dataChannelList = []; // Array<ACS.dataChannel>
	returnObj.eventChannelList = []; // Array<ACS.eventChannel>
	returnObj.modelGui = ACS.modelGui();
	returnObj.visualAreaMarkerList = []; // Array<ACS.visualAreaMarker>
	returnObj.undoStack = []; // Array<ACS.action>
	returnObj.redoStack = []; // Array<ACS.action>
	returnObj.guiUndoStack = []; // Array<ACS.action>
	returnObj.guiRedoStack = []; // Array<ACS.action>
	returnObj.metaDataList = []; // Array<ACS.metaData>
	returnObj.events = ACS.eventManager();
	returnObj.modelName = generateModelName();
	returnObj.acsVersion = ACS.mConst.MODELGUI_ACSVERSION;
	returnObj.selectedItemsList = []; // Array<Object>
	returnObj.hasBeenChanged = false;
	returnObj.recentlyRemovedChannel = null; // ACS.channel
	
	returnObj.getFilename = function() {
		return filename;
	}
	
	returnObj.setFilename = function(newName) {
		filename = newName;
		this.events.fireEvent('filenameBeingChangedEvent');
	}
	
	returnObj.findComponentInCollection = function(compTypeId, actCompColl) {
		var allComponents = actCompColl.getElementsByTagName('componentType');
		for (var j = 0; j < allComponents.length; j++) {
			if (allComponents.item(j).attributes.getNamedItem('id').textContent === compTypeId) return allComponents.item(j);
		}
		return null;
	}
	
	returnObj.getDataType = function(dataTypeAsString) {
		switch (dataTypeAsString) {
			case 'boolean':	return ACS.dataType.BOOLEAN;
			case 'byte':	return ACS.dataType.BYTE;
			case 'char':	return ACS.dataType.CHAR;
			case 'integer':	return ACS.dataType.INTEGER;
			case 'double':	return ACS.dataType.DOUBLE;
			case 'string':	return ACS.dataType.STRING;
		}
		return 0;
	}
	
	returnObj.getFreePosition = function(pos) {
		while (positionTaken(pos)) {
			pos[0] += ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETX;
			pos[1] += ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETY;
		}
		return pos;
	}	

	returnObj.loadModel = function(modelXML) {
		returnObj.modelName = modelXML.getElementsByTagName('model').item(0).attributes.getNamedItem('modelName').textContent;
		if (modelXML.getElementsByTagName('model').item(0).attributes.getNamedItem('version')) {
			returnObj.acsVersion = modelXML.getElementsByTagName('model').item(0).attributes.getNamedItem('version').textContent;
		}
		returnObj.componentList = loadComponentList(modelXML);
		returnObj.dataChannelList = loadDataChannelList(modelXML, returnObj.componentList);
		returnObj.eventChannelList = loadEventChannelList(modelXML, returnObj.componentList);
		loadModelGui(modelXML);
		//returnObj.visualAreaMarkerList = loadVisualAreaMarkerList(modelXML); // TODO: add visualAreaMarkers to XML first
		returnObj.metaDataList = loadMetaDataList(modelXML);
		returnObj.events.fireEvent('modelChangedEvent');
		returnObj.hasBeenChanged = false;
	}

	returnObj.loadModelFromFile = function(loadFile) {
		if (loadFile) {
			returnObj.setFilename(loadFile.name);
			var fr = new FileReader();
			fr.onload = function(e) {
				var modelXML = $.parseXML(e.target.result);
				
				// TODO: write a function that checks whether the given file is a valid ACS-file and alert the user if not
				
				returnObj.loadModel(modelXML);
			};
			fr.readAsText(loadFile);
		}
	}
	
	returnObj.saveModel = function() {
		var saveName;
		if (filename.indexOf('.') > -1) { // if file already contains an ending
			saveName = prompt('Save file as: ', filename);
		} else {
			saveName = prompt('Save file as: ', filename + '.acs');
		}
		if (saveName) {
			if (saveName.indexOf('.') === -1) saveName += '.acs'; // in case the user has entered no ending
			if (saveName !== returnObj.getFilename()) returnObj.setFilename(saveName);
			returnObj.hasBeenChanged = false;
			// actually save the model
			var blob = new Blob([returnObj.getModelXMLString()], {type: 'text/plain;charset=utf-8'});			
			saveAs(blob, saveName, true);
		}
	}
	
	returnObj.getModelXMLString = function() {
		var xmlString = '<?xml version="1.0"?>\r<model xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" modelName="' + returnObj.modelName + '" version="' + returnObj.acsVersion + '">\r';
		xmlString += '\t<modelDescription>\r';
		var mdi = metaDataIndexOfKey('shortDescription');
		if (mdi > -1) {
			xmlString += '\t\t<shortDescription>' + returnObj.metaDataList[mdi].value.replace(/"/g, '&quot;') + '</shortDescription>\r';
		} else {
			xmlString += '\t\t<shortDescription></shortDescription>\r';
		}
		var mdi = metaDataIndexOfKey('requirements');
		if (mdi > -1) {
			xmlString += '\t\t<requirements>' + returnObj.metaDataList[mdi].value.replace(/"/g, '&quot;') + '</requirements>\r';
		} else {
			xmlString += '\t\t<requirements></requirements>\r';
		}
		var mdi = metaDataIndexOfKey('description');
		if (mdi > -1) {
			xmlString += '\t\t<description>' + returnObj.metaDataList[mdi].value.replace(/"/g, '&quot;') + '</description>\r';
		} else {
			xmlString += '\t\t<description></description>\r';
		}
		xmlString += '\t</modelDescription>\r';
		// add the components
		xmlString += '\t<components>\r';
		for (var i = 0; i < returnObj.componentList.length; i++) {
			xmlString += '\t\t<component id="' + returnObj.componentList[i].getId() + '" type_id="' + returnObj.componentList[i].getComponentTypeId() + '">\r';
			if (returnObj.componentList[i].getDescription()) xmlString += '\t\t\t<description>' + returnObj.componentList[i].getDescription().replace(/"/g, '&quot;') + '</description>\r';
			// add the ports
			if ((returnObj.componentList[i].inputPortList.length > 0) || (returnObj.componentList[i].outputPortList.length > 0)) {
				xmlString += '\t\t\t<ports>\r';
				// add the inputPorts
				for (var j = 0; j < returnObj.componentList[i].inputPortList.length; j++) {
					xmlString += '\t\t\t\t<inputPort portTypeID="' + returnObj.componentList[i].inputPortList[j].getId() + '" sync="' + returnObj.componentList[i].inputPortList[j].sync + '">\r';
					if (returnObj.componentList[i].inputPortList[j].propertyList.length > 0) {
						xmlString += '\t\t\t\t\t<properties>\r';
						for (var k = 0; k < returnObj.componentList[i].inputPortList[j].propertyList.length; k++) {
							xmlString += '\t\t\t\t\t\t<property name="' + returnObj.componentList[i].inputPortList[j].propertyList[k].getKey() + '" value="' + returnObj.componentList[i].inputPortList[j].propertyList[k].value.replace(/"/g, '&quot;') + '" />\r';
						}
						xmlString += '\t\t\t\t\t</properties>\r';
					} else {
						xmlString += '\t\t\t\t\t<properties />\r';
					}
					xmlString += '\t\t\t\t</inputPort>\r';
				}
				// add the outputPorts
				for (var j = 0; j < returnObj.componentList[i].outputPortList.length; j++) {
					xmlString += '\t\t\t\t<outputPort portTypeID="' + returnObj.componentList[i].outputPortList[j].getId() + '">\r';
					if (returnObj.componentList[i].outputPortList[j].propertyList.length > 0) {
						xmlString += '\t\t\t\t\t<properties>\r';
						for (var k = 0; k < returnObj.componentList[i].outputPortList[j].propertyList.length; k++) {
							xmlString += '\t\t\t\t\t\t<property name="' + returnObj.componentList[i].outputPortList[j].propertyList[k].getKey() + '" value="' + returnObj.componentList[i].outputPortList[j].propertyList[k].value.replace(/"/g, '&quot;') + '" />\r';
						}
						xmlString += '\t\t\t\t\t</properties>\r';
					} else {
						xmlString += '\t\t\t\t\t<properties />\r';
					}
					xmlString += '\t\t\t\t</outputPort>\r';
				}				
				xmlString += '\t\t\t</ports>\r';
			} else {
				xmlString += '\t\t\t<ports />\r';
			}
			// add the properties
			if (returnObj.componentList[i].propertyList.length > 0) {
				xmlString += '\t\t\t<properties>\r';
				for (var j = 0; j < returnObj.componentList[i].propertyList.length; j++) {
					xmlString += '\t\t\t\t<property name="' + returnObj.componentList[i].propertyList[j].getKey() + '" value="' + returnObj.componentList[i].propertyList[j].value.replace(/"/g, '&quot;') + '" />\r';
				}
				xmlString += '\t\t\t</properties>\r';
			}
			// add the layout
			xmlString += '\t\t\t<layout>\r';
			xmlString += '\t\t\t\t<posX>' + returnObj.componentList[i].getX() + '</posX>\r';
			xmlString += '\t\t\t\t<posY>' + returnObj.componentList[i].getY() + '</posY>\r';
			xmlString += '\t\t\t</layout>\r';
			// add the GUI
			if (returnObj.componentList[i].gui) {
				xmlString += '\t\t\t<gui>\r';
				xmlString += '\t\t\t\t<posX>' + returnObj.componentList[i].gui.getX() + '</posX>\r';
				xmlString += '\t\t\t\t<posY>' + returnObj.componentList[i].gui.getY() + '</posY>\r';
				xmlString += '\t\t\t\t<width>' + returnObj.componentList[i].gui.getWidth() + '</width>\r';
				xmlString += '\t\t\t\t<height>' + returnObj.componentList[i].gui.getHeight() + '</height>\r';
				xmlString += '\t\t\t</gui>\r';
			}
			xmlString += '\t\t</component>\r';
		}
		xmlString += '\t</components>\r';
		// add the channels
		if (returnObj.dataChannelList.length > 0) {
			xmlString += '\t<channels>\r';
			for (var i = 0; i < returnObj.dataChannelList.length; i++) {
				if (returnObj.dataChannelList[i].getInputPort()) { // avoids adding unfinished channels
					xmlString += '\t\t<channel id="' + returnObj.dataChannelList[i].getId() + '">\r';
					if (returnObj.dataChannelList[i].description !== '') xmlString += '\t\t\t<description>' + returnObj.dataChannelList[i].description.replace(/"/g, '&quot;') + '</description>\r';
					xmlString += '\t\t\t<source>\r';
					xmlString += '\t\t\t\t<component id="' + returnObj.dataChannelList[i].getOutputPort().getParentComponent().getId() + '" />\r';
					xmlString += '\t\t\t\t<port id="' + returnObj.dataChannelList[i].getOutputPort().getId() + '" />\r';
					xmlString += '\t\t\t</source>\r';
					xmlString += '\t\t\t<target>\r';
					xmlString += '\t\t\t\t<component id="' + returnObj.dataChannelList[i].getInputPort().getParentComponent().getId() + '" />\r';
					xmlString += '\t\t\t\t<port id="' + returnObj.dataChannelList[i].getInputPort().getId() + '" />\r';
					xmlString += '\t\t\t</target>\r';
					xmlString += '\t\t</channel>\r';
				}
			}
			xmlString += '\t</channels>\r';
		}
		// add the eventChannels
		if (returnObj.eventChannelList.length > 0) {
			xmlString += '\t<eventChannels>\r';
			for (var i = 0; i < returnObj.eventChannelList.length; i++) {
				for (var j = 0; j < returnObj.eventChannelList[i].eventConnections.length; j++) {
					xmlString += '\t\t<eventChannel id="' + returnObj.eventChannelList[i].eventConnections[j].trigger.getId() + '_' + returnObj.eventChannelList[i].eventConnections[j].listener.getId() + '">\r';
					if (returnObj.eventChannelList[i].eventConnections[j].description !== '') xmlString += '\t\t\t<description>' + returnObj.eventChannelList[i].eventConnections[j].description.replace(/"/g, '&quot;') + '</description>\r';
					xmlString += '\t\t\t<sources>\r';
					xmlString += '\t\t\t\t<source>\r';
					xmlString += '\t\t\t\t\t<component id="' + returnObj.eventChannelList[i].startComponent.getId() + '" />\r';
					xmlString += '\t\t\t\t\t<eventPort id="' + returnObj.eventChannelList[i].eventConnections[j].trigger.getId() + '" />\r';
					xmlString += '\t\t\t\t</source>\r';
					xmlString += '\t\t\t</sources>\r';
					xmlString += '\t\t\t<targets>\r';
					xmlString += '\t\t\t\t<target>\r';
					xmlString += '\t\t\t\t\t<component id="' + returnObj.eventChannelList[i].endComponent.getId() + '" />\r';
					xmlString += '\t\t\t\t\t<eventPort id="' + returnObj.eventChannelList[i].eventConnections[j].listener.getId() + '" />\r';
					xmlString += '\t\t\t\t</target>\r';				
					xmlString += '\t\t\t</targets>\r';
					xmlString += '\t\t</eventChannel>\r';
				}
			}
			xmlString += '\t</eventChannels>\r';
		}
		// TODO: add groups
		// add the modelGUI
		xmlString += '\t<modelGUI>\r';
		xmlString += '\t\t<Decoration>' + returnObj.modelGui.getDecoration() + '</Decoration>\r';
		xmlString += '\t\t<Fullscreen>' + returnObj.modelGui.getFullScreen() + '</Fullscreen>\r';
		xmlString += '\t\t<AlwaysOnTop>' + returnObj.modelGui.getAlwaysOnTop() + '</AlwaysOnTop>\r';
		xmlString += '\t\t<ToSystemTray>' + returnObj.modelGui.getToSystemTray() + '</ToSystemTray>\r';
		xmlString += '\t\t<ShopControlPanel>' + returnObj.modelGui.getShowControlPanel() + '</ShopControlPanel>\r'; // TODO: correct to "ShowcontrolPanel", when corrected in XML
		xmlString += '\t\t<AREGUIWindow>\r';
		xmlString += '\t\t\t<posX>' + returnObj.modelGui.areGuiWindow.getX() + '</posX>\r';
		xmlString += '\t\t\t<posY>' + returnObj.modelGui.areGuiWindow.getY() + '</posY>\r';
		xmlString += '\t\t\t<width>' + returnObj.modelGui.areGuiWindow.getWidth() + '</width>\r';
		xmlString += '\t\t\t<height>' + returnObj.modelGui.areGuiWindow.getHeight() + '</height>\r';
		xmlString += '\t\t</AREGUIWindow>\r';
		xmlString += '\t</modelGUI>\r';
		xmlString += '</model>';
		
		return xmlString;
	}
	
	returnObj.initiateComponentByName = function(compName) {
		// returns null if compName does not exist
		// returns 'singleton", when the component is a singleton and there is already an instance in this model
		var compTypeId;
		if (compName.indexOf('Oska') > -1) { 
			compTypeId = compName;
		} else {
			compTypeId = 'asterics.' + compName;
		}
		var compXml = returnObj.findComponentInCollection(compTypeId, componentCollection);
		if (compXml) {
			// check whether the component is a singleton and if yes, avoid multiple instances by returning 'singleton' instead of a component object
			if (compXml.getElementsByTagName('singleton').item(0).textContent === 'true') {
				for (var i = 0; i < returnObj.componentList.length; i++) {
					if (returnObj.componentList[i].getComponentTypeId() === compTypeId) return 'singleton';
				}
			}
			// find out the component's type:
			var compType = 0;
			switch (compXml.getElementsByTagName('type').item(0).textContent) {
				case 'actuator': 	compType = ACS.componentType.ACTUATOR;
									break;
				case 'processor':	compType = ACS.componentType.PROCESSOR;
									break;
				case 'sensor':		compType = ACS.componentType.SENSOR;
									break;					
			}
			// find a free position for the new component
			var pos = returnObj.getFreePosition([ACS.mConst.MODEL_NEWCOMPONENTPOSITIONX, ACS.mConst.MODEL_NEWCOMPONENTPOSITIONX]);
			// build the component-object:
			var newComp = ACS.component(compName + '.' + nextCompNameNumber(compName),
										compTypeId,
										compXml.getElementsByTagName('description').item(0).textContent,
										compXml.getElementsByTagName('singleton')[0].textContent === 'true',
										pos[0],
										pos[1],
										compType,
										false,
										true);
			// build inputPortList:
			var inputPorts = compXml.getElementsByTagName('inputPort');
			for (var j = 0; j < inputPorts.length; j++) {
				var portDataType = returnObj.getDataType(inputPorts.item(j).getElementsByTagName('dataType').item(0).textContent);
				// build the port object:
				newComp.inputPortList[j] = ACS.port(inputPorts.item(j).attributes.getNamedItem('id').textContent,
													newComp,
													ACS.portType.INPUT,
													portDataType,
													j,
													inputPorts.item(j).getElementsByTagName('mustBeConnected').item(0).textContent);
				// TODO: add the port's properties
			}
			// build outputPortList:
			var outputPorts = compXml.getElementsByTagName('outputPort');
			for (var j = 0; j < outputPorts.length; j++) {
				var portDataType = returnObj.getDataType(outputPorts.item(j).getElementsByTagName('dataType').item(0).textContent);
				// build the port object:
				newComp.outputPortList[j] = ACS.port(	outputPorts.item(j).attributes.getNamedItem('id').textContent,
														newComp,
														ACS.portType.OUTPUT,
														portDataType,
														j,
														false);
				// TODO: add the port's properties
			}
			// build listenEventList:
			var listenEvents = compXml.getElementsByTagName('eventListenerPort');
			for (var j = 0; j < listenEvents.length; j++) {
				newComp.listenEventList[j] = ACS.event(	listenEvents.item(j).attributes.getNamedItem('id').textContent,
														listenEvents.item(j).getElementsByTagName('description').item(0).textContent,
														newComp);
			}	
			// build triggerEventList:
			var triggerEvents = compXml.getElementsByTagName('eventTriggererPort');
			for (var j = 0; j < triggerEvents.length; j++) {
				newComp.triggerEventList[j] = ACS.event(triggerEvents.item(j).attributes.getNamedItem('id').textContent,
														triggerEvents.item(j).getElementsByTagName('description').item(0).textContent,
														newComp);
			}
			// build propertyList:
			var properties = compXml.getElementsByTagName('property');
			for (var j = 0; j < properties.length; j++) {
				newComp.propertyList.push(ACS.property(properties.item(j).attributes.getNamedItem('name').textContent, 
													   returnObj.getDataType(properties.item(j).attributes.getNamedItem('type').textContent), 
													   properties.item(j).attributes.getNamedItem('value').textContent));
				if (properties.item(j).attributes.getNamedItem('description'))
					newComp.propertyList[newComp.propertyList.length - 1].description = properties.item(j).attributes.getNamedItem('description').textContent;
				if (properties.item(j).attributes.getNamedItem('combobox'))
					newComp.propertyList[newComp.propertyList.length - 1].combobox = properties.item(j).attributes.getNamedItem('combobox').textContent;
				if (properties.item(j).attributes.getNamedItem('getStringList'))
					newComp.propertyList[newComp.propertyList.length - 1].getStringList = properties.item(j).attributes.getNamedItem('getStringList').textContent;
			}
			// build the gui object:
			if (compXml.getElementsByTagName('gui').item(0)) {
				var isExternal = false;
				if (compXml.getElementsByTagName('gui').item(0).attributes.getNamedItem('IsExternalGUIElement')) {
					isExternal = compXml.getElementsByTagName('gui').item(0).attributes.getNamedItem('IsExternalGUIElement').textContent;
				}
				newComp.gui = ACS.gui(	0,
										0,
										parseInt(compXml.getElementsByTagName('gui').item(0).getElementsByTagName('width').item(0).textContent),
										parseInt(compXml.getElementsByTagName('gui').item(0).getElementsByTagName('height').item(0).textContent),
										isExternal);
			}
			return newComp;
		}
		return null;
	}
	
	returnObj.addComponent = function(comp) { // ACS.component
		returnObj.componentList.push(comp);
		if (comp.getIsSelected()) returnObj.selectedItemsList.push(comp);
		this.events.fireEvent('componentAddedEvent');
		returnObj.hasBeenChanged = true;
	}	
	
	returnObj.removeComponent = function(comp) { // ACS.component
		if (returnObj.componentList.indexOf(comp) > -1) returnObj.componentList.splice(returnObj.componentList.indexOf(comp), 1);
		if (returnObj.selectedItemsList.indexOf(comp) > -1) returnObj.selectedItemsList.splice(returnObj.selectedItemsList.indexOf(comp), 1);
		returnObj.hasBeenChanged = true;
		this.events.fireEvent('componentRemovedEvent');
	}
	
	returnObj.addDataChannel = function(ch) { // ACS.dataChannel
		returnObj.dataChannelList.push(ch);
		if (ch.getIsSelected()) returnObj.selectedItemsList.push(ch);
		returnObj.hasBeenChanged = true;
		this.events.fireEvent('dataChannelAddedEvent');
	}
	
	returnObj.removeDataChannel = function(ch) { // ACS.dataChannel
		if (returnObj.dataChannelList.indexOf(ch) > -1) returnObj.dataChannelList.splice(returnObj.dataChannelList.indexOf(ch), 1);
		if (returnObj.selectedItemsList.indexOf(ch) > -1) returnObj.selectedItemsList.splice(returnObj.selectedItemsList.indexOf(ch), 1);
		returnObj.recentlyRemovedChannel = ch;
		returnObj.hasBeenChanged = true;
		this.events.fireEvent('dataChannelRemovedEvent');
	}	

	returnObj.addEventChannel = function(ch) { // ACS.eventChannel
		returnObj.eventChannelList.push(ch);
		if (ch.getIsSelected()) returnObj.selectedItemsList.push(ch);
		returnObj.hasBeenChanged = true;
		this.events.fireEvent('eventChannelAddedEvent');
	}
	
	returnObj.removeEventChannel = function(ch) { // ACS.eventChannel
		if (returnObj.eventChannelList.indexOf(ch) > -1) returnObj.eventChannelList.splice(returnObj.eventChannelList.indexOf(ch), 1);
		if (returnObj.selectedItemsList.indexOf(ch) > -1) returnObj.selectedItemsList.splice(returnObj.selectedItemsList.indexOf(ch), 1);
		returnObj.recentlyRemovedChannel = ch;
		returnObj.hasBeenChanged = true;
		this.events.fireEvent('eventChannelRemovedEvent');
	}		
	
	returnObj.getComponentCollection = function() {
		return componentCollection; // (XML document)
	}
	
	returnObj.setComponentCollection = function(xmlDoc) {
		componentCollection = xmlDoc;
		this.events.fireEvent('componentCollectionChangedEvent');
	}
	
	returnObj.loadComponentCollection = function(loadFile) { // File
		// loads a component collection from a user-chosen file
		
		// componentCollection = ...
		// set menu to new collection...
		this.events.fireEvent('componentCollectionChangedEvent');
		return componentCollection; // (XML document)
	}
	
	returnObj.getDeleteListForNewComponentCollection = function(newColl) { // XML document
		var theList = [];
		for (var i = 0; i < returnObj.componentList.length; i++) {
			if (!returnObj.findComponentInCollection(returnObj.componentList[i].getComponentTypeId(), newColl)) theList.push(returnObj.componentList[i]);
		}
		return theList;
	}
	
	returnObj.getModifyListForNewComponentCollection = function(newColl) { // XML document
		var theList = [];
		for (var i = 0; i < returnObj.componentList.length; i++) {
			var needsModification = false;
			var fullComponent = returnObj.findComponentInCollection(returnObj.componentList[i].getComponentTypeId(), newColl);
			if (fullComponent) {
				// check the inputPorts
				var inputPortsFull = fullComponent.getElementsByTagName('inputPort');
				if (returnObj.componentList[i].inputPortList.length !== inputPortsFull.length) {
					needsModification = true;
				} else {
					var j = 0;
					while (!needsModification && j < inputPortsFull.length) {
						if (!findPortById(returnObj.componentList[i], inputPortsFull.item(j).attributes.getNamedItem('id').textContent, true)) {
							needsModification = true;
						}
						j++;
					}			
				}
				// check the outputPorts
				var outputPortsFull = fullComponent.getElementsByTagName('outputPort');
				if (returnObj.componentList[i].outputPortList.length !== outputPortsFull.length) {
					needsModification = true;
				} else {
					var j = 0;
					while (!needsModification && j < outputPortsFull.length) {
						if (!findPortById(returnObj.componentList[i], outputPortsFull.item(j).attributes.getNamedItem('id').textContent, false)) {
							needsModification = true;
						}
						j++;
					}			
				}
				// check the listener events
				var listenEventsFull = fullComponent.getElementsByTagName('eventListenerPort');
				if (returnObj.componentList[i].listenEventList.length !== listenEventsFull.length) {
					needsModification = true;
				} else {
					var j = 0;
					while (!needsModification && j < listenEventsFull.length) {
						if (!findEventById(returnObj.componentList[i], listenEventsFull.item(j).attributes.getNamedItem('id').textContent, true)) {
							needsModification = true;
						}
						j++;
					}			
				}
				// check the trigger events
				var triggerEventsFull = fullComponent.getElementsByTagName('eventTriggererPort');
				if (returnObj.componentList[i].triggerEventList.length !== triggerEventsFull.length) {
					needsModification = true;
				} else {
					var j = 0;
					while (!needsModification && j < triggerEventsFull.length) {
						if (!findEventById(returnObj.componentList[i], triggerEventsFull.item(j).attributes.getNamedItem('id').textContent, false)) {
							needsModification = true;
						}
						j++;
					}			
				}
				// check the properties
				var propertiesFull = fullComponent.getElementsByTagName('property');
				if (returnObj.componentList[i].propertyList.length !== propertiesFull.length) {
					needsModification = true;
				} else {
					var j = 0;
					while (!needsModification && j < propertiesFull.length) {
						if (!findPropertyByKey(returnObj.componentList[i], propertiesFull.item(j).attributes.getNamedItem('name').textContent)) {
							needsModification = true;
						}
						j++;
					}			
				}
				if (needsModification) {
					theList.push(returnObj.componentList[i]);
				}				
			}
		}
		return theList;
	}
	
	returnObj.modifyComponentsAccordingToComponentCollection = function(modifyList) {
		for (var i = 0; i < modifyList.length; i++) {
			var fullComponent = returnObj.findComponentInCollection(modifyList[i].getComponentTypeId(), componentCollection);
			modifyList[i].matchesComponentCollection = false;
			if (fullComponent) {
				// check the inputPorts
				var inputPortsFull = fullComponent.getElementsByTagName('inputPort');
				var inputPortsNew = [];
				for (var j = 0; j < inputPortsFull.length; j++) {
					var actPort = findPortById(modifyList[i], inputPortsFull.item(j).attributes.getNamedItem('id').textContent, true);
					if (actPort) {
						inputPortsNew.push(actPort);
					} else {
						inputPortsNew[inputPortsNew.length] = ACS.port(	inputPortsFull.item(j).attributes.getNamedItem('id').textContent,
																		modifyList[i],
																		ACS.portType.INPUT,
																		returnObj.getDataType(inputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent),
																		j,
																		inputPortsFull.item(j).getElementsByTagName('mustBeConnected').item(0).textContent);
					}
				}
				modifyList[i].inputPortList = inputPortsNew;
				// check the outputPorts
				var outputPortsFull = fullComponent.getElementsByTagName('outputPort');
				var outputPortsNew = [];
				for (var j = 0; j < outputPortsFull.length; j++) {
					var actPort = findPortById(modifyList[i], outputPortsFull.item(j).attributes.getNamedItem('id').textContent, false);
					if (actPort) {
						outputPortsNew.push(actPort);
					} else {
						outputPortsNew[outputPortsNew.length] = ACS.port(	outputPortsFull.item(j).attributes.getNamedItem('id').textContent,
																			modifyList[i],
																			ACS.portType.OUTPUT,
																			returnObj.getDataType(outputPortsFull.item(j).getElementsByTagName('dataType').item(0).textContent),
																			j,
																			false);
					}
				}
				modifyList[i].outputPortList = outputPortsNew;				
				// delete channels if their input- or output port has been deleted
				var j = 0;
				while (j < returnObj.dataChannelList.length) {
					if (returnObj.dataChannelList[j].getInputPort().getParentComponent() === modifyList[i]) {
						var found = false;
						var k = 0;
						while (!found && k < modifyList[i].inputPortList.length) {
							if (returnObj.dataChannelList[j].getInputPort() === modifyList[i].inputPortList[k]) {
								found = true;
							}
							k++;
						}
						if (!found) {
							removeDataChannel(returnObj.dataChannelList[j]);
						} else {
							j++;
						}
					} else if (returnObj.dataChannelList[j].getOutputPort().getParentComponent() === modifyList[i]) {
						var found = false;
						var k = 0;
						while (!found && k < modifyList[i].outputPortList.length) {
							if (returnObj.dataChannelList[j].getOutputPort() === modifyList[i].outputPortList[k]) {
								found = true;
							}
							k++;
						}
						if (!found) {
							removeDataChannel(returnObj.dataChannelList[j]);
						} else {
							j++;
						}
					} else {
						j++;
					}
				}
				// check eventListeners
				var listenEventsFull = fullComponent.getElementsByTagName('eventListenerPort');
				var listenEventsNew = [];
				for (var j = 0; j < listenEventsFull.length; j++) {
					var actEvent = findEventById(modifyList[i], listenEventsFull.item(j).attributes.getNamedItem('id').textContent, true);
					if (actEvent) {
						listenEventsNew.push(actEvent);
					} else {
						listenEventsNew[listenEventsNew.length] = ACS.event(listenEventsFull.item(j).attributes.getNamedItem('id').textContent,
																			listenEventsFull.item(j).getElementsByTagName('description').item(0).textContent,
																			modifyList[i]);
					}
				}
				modifyList[i].listenEventList = listenEventsNew;	
				// check eventTriggers
				var triggerEventsFull = fullComponent.getElementsByTagName('eventTriggererPort');
				var triggerEventsNew = [];
				for (var j = 0; j < triggerEventsFull.length; j++) {
					var actEvent = findEventById(modifyList[i], triggerEventsFull.item(j).attributes.getNamedItem('id').textContent, false);
					if (actEvent) {
						triggerEventsNew.push(actEvent);
					} else {
						triggerEventsNew[triggerEventsNew.length] = ACS.event(	triggerEventsFull.item(j).attributes.getNamedItem('id').textContent,
																				triggerEventsFull.item(j).getElementsByTagName('description').item(0).textContent,
																				modifyList[i]);
					}
				}
				modifyList[i].triggerEventList = triggerEventsNew;
				// delete eventConnections if their listener or trigger has been deleted
				var j = 0;
				while (j < returnObj.eventChannelList.length) {
					if (returnObj.eventChannelList[j].endComponent === modifyList[i]) {
						var l = 0;
						while (l < returnObj.eventChannelList[j].eventConnections.length) {
							var found = false;
							var k = 0;
							while (!found && k < modifyList[i].listenEventList.length) {
								if (returnObj.eventChannelList[j].eventConnections[l].listener === modifyList[i].listenEventList[k]) {
									found = true;
								}
								k++;
							}
							if (!found) {
								returnObj.eventChannelList[j].eventConnections.splice(l, 1);
							} else {
								l++;
							}
						}
						if (returnObj.eventChannelList[j].eventConnections.length === 0) {
							removeEventChannel(returnObj.eventChannelList[j]);
						} else {
							j++;
						}
					} else if (returnObj.eventChannelList[j].startComponent === modifyList[i]) {
						var l = 0;
						while (l < returnObj.eventChannelList[j].eventConnections.length) {
							var found = false;
							var k = 0;
							while (!found && k < modifyList[i].triggerEventList.length) {
								if (returnObj.eventChannelList[j].eventConnections[l].trigger === modifyList[i].triggerEventList[k]) {
									found = true;
								}
								k++;
							}
							if (!found) {
								returnObj.eventChannelList[j].eventConnections.splice(l, 1);
							} else {
								l++;
							}
						}
						if (returnObj.eventChannelList[j].eventConnections.length === 0) {
							removeEventChannel(returnObj.eventChannelList[j]);
						} else {
							j++;
						}
					} else {
						j++;
					}
				}
				// check the properties
				var propertiesFull = fullComponent.getElementsByTagName('property');
				var propertiesNew = [];
				for (var j = 0; j < propertiesFull.length; j++) {
					var actProperty = findPropertyByKey(modifyList[i], propertiesFull.item(j).attributes.getNamedItem('name').textContent);
					if (actProperty) {
						propertiesNew.push(actProperty);
					} else {
						propertiesNew[propertiesNew.length] = ACS.property(	propertiesFull.item(j).attributes.getNamedItem('name').textContent,
																			returnObj.getDataType(propertiesFull.item(j).attributes.getNamedItem('type').textContent),
																			propertiesFull.item(j).attributes.getNamedItem('value').textContent);
						if (propertiesFull.item(j).attributes.getNamedItem('description'))
							propertiesNew[propertiesNew.length - 1].description = propertiesFull.item(j).attributes.getNamedItem('description').textContent;
						if (propertiesFull.item(j).attributes.getNamedItem('combobox'))
							propertiesNew[propertiesNew.length - 1].combobox = propertiesFull.item(j).attributes.getNamedItem('combobox').textContent;
						if (propertiesFull.item(j).attributes.getNamedItem('getStringList'))
							propertiesNew[propertiesNew.length - 1].getStringList = propertiesFull.item(j).attributes.getNamedItem('getStringList').textContent;		
					}
				}
				modifyList[i].propertyList = propertiesNew;
			}
		}
		returnObj.events.fireEvent('modelChangedEvent');
	}
	
	returnObj.addItemToSelection = function(item) {
		returnObj.selectedItemsList.push(item);
		item.setIsSelected(true);
	}
	
	returnObj.removeItemFromSelection = function(item) {
		var itemIndex = returnObj.selectedItemsList.indexOf(item);
		if (itemIndex > -1) {
			returnObj.selectedItemsList.splice(itemIndex, 1);
			item.setIsSelected(false);
			return true;
		} else {
			return false;
		}
	}
	
	returnObj.deSelectAll = function() {
		var actItem;
		while (returnObj.selectedItemsList.length > 0) {
			actItem = returnObj.selectedItemsList.pop();
			actItem.setIsSelected(false);
		}
	}	
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	componentCollection = loadDefaultComponentCollection();
	if (componentCollection) {
		log.debug('the component-id of the first component in the collection is "' + componentCollection.getElementsByTagName('componentType').item(0).attributes.getNamedItem('id').nodeValue + '"');
		returnObj.events.fireEvent('componentCollectionChangedEvent');
	} else {	
		alert('No valid component collection found. Please make sure the file defaultComponentCollection.abd exists in the WebACS folder.');
	}
	
	return returnObj;
}