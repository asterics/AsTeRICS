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

ACS.propertyEditor = function (modelList, // ACS.modelList
	modelViewListtemp, // Array<ACS.modelView>
	editorPropsTemp) { // ACS.editorProperties

	// ***********************************************************************************************************************
	// ************************************************** private variables **************************************************
	// ***********************************************************************************************************************
	var propertiesTabPanel = ACS.tabPanel(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL, ACS.vConst.PROPERTYEDITOR_CLASSOFTAB, ACS.vConst.PROPERTYEDITOR_CLASSOFPANEL);
	var actModel = modelList.getActModel();
	var actModelOld;
	var propertyTable = document.createElement('table');
	var internalPropertyTable = document.createElement('table');
	var inputPortTable = document.createElement('table');
	var outputPortTable = document.createElement('table');
	var propertiesGuiEditorTable = document.createElement('table');
	var propertiesGuiEditorTableEditorProperties = document.createElement('table');
	var eventTriggerTable = document.createElement('table');
	var eventListenerTable = document.createElement('table');
	var modelViewList = modelViewListtemp;
	var modelViewAct = modelViewList[0];
	var modelViewActTabPanel = modelViewList[0].getModelTabPanel();
	var editorProps = editorPropsTemp;
	var row = [];
	var cell = null;
	var dropdownList = document.createElement('select');
	var numberInput;
	var textInput;
	var selectedElement;
	var selectedElementOld;
	var eventChannelTable = document.createElement('table');
	var previousDropDownEntry = null; //stores the selected dropdownvalue before entry is changed
	var previousNumberEntry = null; //stored for an alert on wron input format
	var eventTableId = 0;
	var guiEditorOn = false;
	var propertyDefaultText = document.createElement('p');
	var propertyEdPanelCaptions = document.createElement('p');
	var inputPanelCaptions = document.createElement('p');
	var outputPanelCaptions = document.createElement('p');
	var triggerPanelCaptions = document.createElement('p');
	var listenerPanelCaptions = document.createElement('p');
	var eventPanelCaptions = document.createElement('p');
	var wrapperDivInput = document.createElement('div');
	var wrapperDivOutput = document.createElement('div');
	var wrapperDivProperty = document.createElement('div');
	var wrapperDivTrigger = document.createElement('div');
	var wrapperDivListener = document.createElement('div');
	var wrapperDivEvents = document.createElement('div');
	var wrapperDivGUI = document.createElement('div');
	var lastEditFieldId;
	var lastModiviedElement;
	var notSavedId = null; //workaround to save change also when no blur event is fired;
	//values are stored localy by ocurring input event
	//values are written to property in deselect methode
	var changeNotSaved = false; //workaround to save change also when no blur event is fired
	var notSavedValue = null; //workaround to save change also when no blur event is fired
	var selectedNumberOfComponents = 0;
	var selectedNumberOfEventChannels = 0;
	var selectedNumberOfDataChannels = 0;

	// ***********************************************************************************************************************
	// ************************************************** private methods ****************************************************
	// ***********************************************************************************************************************

	//methodes handling incoming events
	//=================================

	//generate view based on the type eventchannel or coponent and the selected tab
	var generateViews = function () {
		clearPropertyEditor();
		var selectedElementType = null;
		var containerId = modelViewAct.getModelContainerId();
		var modelPanelId = 'modelPanel' + containerId;
		var guiPanelId = 'guiPanel' + containerId;
		var listPanelId = 'listPanel' + containerId;

		//hide All Tabs and Panels => activation in the respective cases
		hideAllTabsAndPanels();
		if (document.getElementById(modelPanelId).getAttribute("aria-hidden") === 'false' || document.getElementById(listPanelId).getAttribute("aria-hidden") === 'false') {		
			selectedElementType = "none";
			if (selectedNumberOfComponents === 1 && selectedNumberOfEventChannels === 0) { //check if only one component is selected
				//get selected component
				for (var i = 0; i < actModel.componentList.length; i++) {
					if (actModel.componentList[i].getIsSelected()) {
						selectedElement = i;
						selectedElementType = "component";
					}
				}
			}
			if (selectedNumberOfComponents === 0 && selectedNumberOfEventChannels === 1) { //check if only one element is selected
				//get selected component
				for (var i = 0; i < actModel.eventChannelList.length; i++) {
					if (actModel.eventChannelList[i].getIsSelected()) {
						selectedElement = i;
						selectedElementType = "channel";
					}
				}
			}
			if (selectedNumberOfComponents === 1 && selectedNumberOfEventChannels === 1 && modelViewAct.getListChannelMode) { //check if channelMode is activated an two components are selected
				//get selected component
				for (var i = 0; i < actModel.eventChannelList.length; i++) {
					if (actModel.eventChannelList[i].getIsSelected()) {
						selectedElement = i;
						selectedElementType = "channel";
					}
				}
			}
			
				

			//Part for component
			if (selectedElementType === "component") {
				document.getElementById("propertyEditorTabList").children[0].setAttribute("class", "tab propEdTab");
				document.getElementById("propertyEditorTabList").children[1].setAttribute("class", "tab propEdTab");
				document.getElementById("propertyEditorTabList").children[2].setAttribute("class", "tab propEdTab");
				document.getElementById("propertyEditorTabList").children[3].setAttribute("class", "tab propEdTab");
				document.getElementById("propertyEditorTabList").children[4].setAttribute("class", "tab propEdTab");

				generatePropertiesForComponent();
				generateInputPortsForComponent();
				generateOuputPortsForComponent();
				generateEventTriggersForComponent();
				generateEventListenerForComponent();
				document.getElementById("propEdPanel").setAttribute("class", "panel propEdPanel");
				document.getElementById("propertyEditorTabList").children[0].setAttribute("aria-selected", "true");
				document.getElementById("propertyEditorTabList").children[0].tabIndex="0";
				document.getElementById("propEdPanel").setAttribute("aria-hidden", "false");
			}

			//Part for Events
			if (selectedElementType === "channel") {
				document.getElementById("propertyEditorTabList").children[5].setAttribute("class", "tab propEdTab");
				generateChannelEventsForChannel();
				document.getElementById("eventPanel").setAttribute("class", "panel propEdPanel");
				document.getElementById("propertyEditorTabList").children[5].setAttribute("aria-selected", "true");
				document.getElementById("propertyEditorTabList").children[5].tabIndex="0";
				document.getElementById("eventPanel").setAttribute("aria-hidden", "false");
			}

			//in case the selected item is either a component or a eventchannel
			if (selectedElementType === "none") {
				document.getElementById("propertyEditorTabList").children[0].setAttribute("class", "tab propEdTab");
				generateEmptyMessage();
				document.getElementById("propEdPanel").setAttribute("class", "panel propEdPanel");
				document.getElementById("propertyEditorTabList").children[0].setAttribute("aria-selected", "true");
				document.getElementById("propertyEditorTabList").children[0].tabIndex="0";
				document.getElementById("propEdPanel").setAttribute("aria-hidden", "false");
			}
		}
		
		if (document.getElementById(guiPanelId).getAttribute("aria-hidden") === 'false') {
			//Render Properties for Gui Editor
			document.getElementById("propertyEditorTabList").children[0].setAttribute("class", "tab propEdTab");
			generaterPropertiesForGUIEditor();
			document.getElementById("propEdPanel").setAttribute("class", "panel propEdPanel");
			document.getElementById("propertyEditorTabList").children[0].setAttribute("aria-selected", "true");
			document.getElementById("propertyEditorTabList").children[0].tabIndex="0";
			document.getElementById("propEdPanel").setAttribute("aria-hidden", "false");
		}
	}

	//generate the parts / fields for the properties for the selected component
	var generatePropertiesForComponent = function () {
		propertyEdPanelCaptions.innerHTML = actModel.componentList[selectedElement].getId();
		propertyEdPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		propertyTable.setAttribute("class", "propertyEditorT");
		wrapperDivProperty.setAttribute("class", "propertyEditorWrapper");
		//wrapperDivProperty.setAttribute("tabindex","-1");
		wrapperDivProperty.addEventListener("focusout", writeProperty);

		var header = propertyTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Property";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "Value";
		row[0].appendChild(headerCell2);

		var bodyT = propertyTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		//var tempString=actModel.componentList[selectedElement].getId();
		//Properties
		for (var h = 0; h < actModel.componentList[selectedElement].propertyList.length; h++) {
            var property = actModel.componentList[selectedElement].propertyList[h];
			var propName = property.getKey();
			row[h + 1] = bodyT.insertRow(-1);
			cell = row[h + 1].insertCell(0);

			var comboValues = property.combobox;
			var valtemp = property.value;
			var typetemp = property.getType();
			cell.innerHTML = propName;

			//generat dropdown list in case that combox includes values
			if (comboValues !== '') {
				var entries = comboValues.split('//');

				dropdownList = null;
				dropdownList = document.createElement('select');
				for (var l = 0; l < entries.length; l++) {
					var option = null;
					if(typetemp === ACS.dataType.STRING) {
                        option = new Option(entries[l], entries[l]);
					} else {
						option = new Option(entries[l], l);
					}
					if(valtemp == l || valtemp == entries[l]) {
						option.selected = true;
					}
					dropdownList.appendChild(option);
				}
				dropdownList.setAttribute("id", h + "/1/" + valtemp);
				dropdownList.addEventListener("change", writeProperty);
				cell = row[h + 1].insertCell(1);
				cell.appendChild(dropdownList);
			}

			//generate checkbox field for boolean
			if (comboValues === '' && typetemp === ACS.dataType.BOOLEAN) {
				cell = row[h + 1].insertCell(1);
				boolInput = null;
				boolInput = document.createElement("INPUT");
				boolInput.setAttribute("type", "checkbox");
				boolInput.setAttribute("value", valtemp);
				if ((valtemp === "true") || (valtemp === "True")) {
					boolInput.setAttribute("checked", true);
				}
				boolInput.setAttribute("id", h + "/1/" + valtemp);
				boolInput.addEventListener("change", writeProperty);
				cell.appendChild(boolInput);
			}
			//generate intage field
			if (comboValues === '' && typetemp === ACS.dataType.INTEGER) {
				cell = row[h + 1].insertCell(1);
				numberInput = null;
				numberInput = document.createElement("INPUT");
				numberInput.setAttribute("type", "number");
				numberInput.setAttribute("value", valtemp);
				numberInput.setAttribute("id", h + "/1/" + valtemp);
				numberInput.addEventListener("focus", setPreviousNumber);
				numberInput.addEventListener("change", writeProperty);
				numberInput.addEventListener("input", writePropertyChangLocal); //workaround when blur is not fired
				cell.appendChild(numberInput);
			}

			if (comboValues === '' && typetemp === ACS.dataType.DOUBLE) {
				cell = row[h + 1].insertCell(1);
				numberInput = null;
				numberInput = document.createElement("INPUT");
				numberInput.setAttribute("type", "text");
				//numberInput.setAttribute("step","0.0000000001");
				numberInput.setAttribute("value", valtemp);
				numberInput.setAttribute("pattern", "^[-]?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?");
				numberInput.setAttribute("id", h + "/1/" + valtemp);
				numberInput.addEventListener("change", writeProperty);
				numberInput.addEventListener("input", writePropertyChangLocal); //workaround when blur is not fired
				cell.appendChild(numberInput);
			}

			if (comboValues === '' && typetemp === ACS.dataType.STRING) {
				cell = row[h + 1].insertCell(1);
				textInput = null;
				textInput = document.createElement("INPUT");
				textInput.setAttribute("type", "text");
				textInput.setAttribute("name", propName);
				textInput.setAttribute("value", ACS.utils.decodeForXml(valtemp));
				textInput.setAttribute("id", h + "/1/" + valtemp);
				textInput.addEventListener("blur", writeProperty);
				textInput.addEventListener("input", writePropertyChangLocal); //workaround when blur is not fired
				cell.appendChild(textInput);
			}

			//element.setAttribute("type", "button");
			//element.setAttribute("value", tempString);
		}

		//internal Properties
		internalPropertyTable.setAttribute("class", "propertyEditorT");

		var headerInternalProoperty = internalPropertyTable.createTHead();
		headerInternalProoperty.setAttribute("class", "propertyEditorTh");
		row[0] = headerInternalProoperty.insertRow(0);
		var headerInternalProopertyCell1 = document.createElement("TH");
		headerInternalProopertyCell1.innerHTML = "Internal Property";
		row[0].appendChild(headerInternalProopertyCell1);
		var headerInternalProopertyCell2 = document.createElement("TH");
		headerInternalProopertyCell2.innerHTML = "Value";
		row[0].appendChild(headerInternalProopertyCell2);

		var bodyT2 = internalPropertyTable.createTBody();
		bodyT2.setAttribute("class", "propertyEditorTb");

		row[1] = bodyT2.insertRow(-1);
		cell = row[1].insertCell(0);
		cell.innerHTML = 'Name';
		cell = row[1].insertCell(1);
		valtemp = actModel.componentList[selectedElement].getId();
		textInput = null;
		textInput = document.createElement("INPUT");
		textInput.setAttribute("type", "text");
		textInput.setAttribute("name", "internalPropName");
		textInput.setAttribute("value", valtemp);
		textInput.setAttribute("id", "xx_name/1/" + valtemp);
		textInput.addEventListener("blur", writeProperty);
		textInput.addEventListener("input", writePropertyChangLocal);
		cell.appendChild(textInput);

		row[2] = bodyT2.insertRow(-1);
		cell = row[2].insertCell(0);
		cell.innerHTML = 'Component Type';
		cell = row[2].insertCell(1);
		cell.innerHTML = actModel.componentList[selectedElement].getComponentTypeId();

		row[3] = bodyT2.insertRow(-1);
		cell = row[3].insertCell(0);
		cell.innerHTML = 'Description';
		cell = row[3].insertCell(1);
		valtemp = actModel.componentList[selectedElement].getDescription();
		textInput = null;
		textInput = document.createElement("INPUT");
		textInput.setAttribute("type", "text");
		textInput.setAttribute("name", "internalPropDescription");
		textInput.setAttribute("value", valtemp);
		textInput.setAttribute("id", "xx_descr/1/" + valtemp);
		textInput.addEventListener("blur", writeProperty);
		textInput.addEventListener("input", writePropertyChangLocal);
		cell.appendChild(textInput);

		row[4] = bodyT2.insertRow(-1);
		cell = row[4].insertCell(0);
		cell.innerHTML = 'Component Class';
		cell = row[4].insertCell(1);
		var compClassId = actModel.componentList[selectedElement].getType();
		if (compClassId === 1) {
			cell.innerHTML = 'Sensor';
		} else if (compClassId === 2) {
			cell.innerHTML = 'Processor';
		} else if (compClassId === 3) {
			cell.innerHTML = 'Actuator';
		} else {
			cell.innerHTML = compClassId;
		}
		wrapperDivProperty.appendChild(propertyEdPanelCaptions);
		wrapperDivProperty.appendChild(propertyTable);
		wrapperDivProperty.appendChild(internalPropertyTable);
		document.getElementById('propEdPanel').appendChild(wrapperDivProperty);
	}

	//generate the parts / fields for the inputs of the selected property
	var generateInputPortsForComponent = function () {
		inputPanelCaptions.innerHTML = actModel.componentList[selectedElement].getId() + ':';
		inputPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		inputPortTable.setAttribute("class", "propertyEditorT");
		wrapperDivInput.setAttribute("class", "propertyEditorWrapper");

		var header = inputPortTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Port Label";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "PortDataType";
		row[0].appendChild(headerCell2);
		var headerCell3 = document.createElement("TH");
		headerCell3.innerHTML = "Synchronize";
		row[0].appendChild(headerCell3);
		var headerCell4 = document.createElement("TH");
		headerCell4.innerHTML = "MustBeConnected";
		row[0].appendChild(headerCell4);
		var headerCell5 = document.createElement("TH");
		headerCell5.innerHTML = "Description";
		row[0].appendChild(headerCell5);

		var bodyT = inputPortTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < actModel.componentList[selectedElement].inputPortList.length; h++) {
			var tempStringa = actModel.componentList[selectedElement].inputPortList[h].getId();
			row[h + 1] = bodyT.insertRow(-1);
			cell = row[h + 1].insertCell(0);
			cell.innerHTML = tempStringa;
			tempStringa = actModel.componentList[selectedElement].inputPortList[h].getDataType();
			cell = row[h + 1].insertCell(1);
			cell.innerHTML = stringOfEnum(ACS.dataType, tempStringa);
			tempStringa = actModel.componentList[selectedElement].inputPortList[h].sync;
			cell = row[h + 1].insertCell(2);
			boolInput = null;
			boolInput = document.createElement("INPUT");
			boolInput.setAttribute("type", "checkbox");
			boolInput.setAttribute("value", tempStringa);
			if (tempStringa === "true") {
				boolInput.setAttribute("checked", true);
			}
			boolInput.setAttribute("id", h + "/3/" + "sync");
			boolInput.addEventListener("change", writeInputPorts);
			cell.appendChild(boolInput);
			tempStringa = actModel.componentList[selectedElement].inputPortList[h].getMustBeConnected();
			cell = row[h + 1].insertCell(3);
			boolInput = null;
			boolInput = document.createElement("INPUT");
			boolInput.setAttribute("type", "checkbox");
			boolInput.setAttribute("value", tempStringa);
			if (tempStringa === "true") {
				boolInput.setAttribute("checked", true);
			}
			boolInput.setAttribute('disabled', 'disabled');
			cell.appendChild(boolInput);
			tempStringa = ''; //TODO get description
			cell = row[h + 1].insertCell(4);
			cell.innerHTML = tempStringa;
		}
		wrapperDivInput.appendChild(inputPanelCaptions);
		wrapperDivInput.appendChild(inputPortTable);
		document.getElementById('inputPanel').appendChild(wrapperDivInput);
	}

	//generate the parts / fields for the outputs of the selected component
	var generateOuputPortsForComponent = function () {
		outputPanelCaptions.innerHTML = actModel.componentList[selectedElement].getId() + ':';
		outputPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		outputPortTable.setAttribute("class", "propertyEditorT");
		wrapperDivInput.setAttribute("class", "propertyEditorWrapper");

		var header = outputPortTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Port Label";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "PortDataType";
		row[0].appendChild(headerCell2);
		var headerCell3 = document.createElement("TH");
		headerCell3.innerHTML = "Description";
		row[0].appendChild(headerCell3);

		var bodyT = outputPortTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < actModel.componentList[selectedElement].outputPortList.length; h++) {
			tempStringa = actModel.componentList[selectedElement].outputPortList[h].getId();
			row[h + 1] = bodyT.insertRow(-1);
			cell = row[h + 1].insertCell(0);
			cell.innerHTML = tempStringa;
			tempStringa = actModel.componentList[selectedElement].outputPortList[h].getDataType();
			cell = row[h + 1].insertCell(1);
			cell.innerHTML = stringOfEnum(ACS.dataType, tempStringa);
			tempStringa = ''; //TODO get description
			cell = row[h + 1].insertCell(2);
			cell.innerHTML = tempStringa;
		}

		wrapperDivOutput.appendChild(outputPanelCaptions);
		wrapperDivOutput.appendChild(outputPortTable);
		document.getElementById('outputPanel').appendChild(wrapperDivOutput);
	}

	var generateEventTriggersForComponent = function () {
		triggerPanelCaptions.innerHTML = actModel.componentList[selectedElement].getId() + ':';
		triggerPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		eventTriggerTable.setAttribute("class", "propertyEditorT");
		wrapperDivTrigger.setAttribute("class", "propertyEditorWrapper");

		var header = eventTriggerTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Trigger";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "Description";
		row[0].appendChild(headerCell2)

		var bodyT = eventTriggerTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < actModel.componentList[selectedElement].triggerEventList.length; h++) {
			var tempStringa = actModel.componentList[selectedElement].triggerEventList[h].getId();
			var tempDes = actModel.componentList[selectedElement].triggerEventList[h].getDescription();
			row[h] = bodyT.insertRow(-1);
			cell = row[h].insertCell(0);
			cell.innerHTML = tempStringa;
			cell = row[h].insertCell(1);
			cell.innerHTML = tempDes;
		}
		wrapperDivTrigger.appendChild(triggerPanelCaptions);
		wrapperDivTrigger.appendChild(eventTriggerTable);
		document.getElementById('triggerPanel').appendChild(wrapperDivTrigger);
	}

	var generateEventListenerForComponent = function () {
		listenerPanelCaptions.innerHTML = actModel.componentList[selectedElement].getId() + ':';
		listenerPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		eventListenerTable.setAttribute("class", "propertyEditorT");
		wrapperDivListener.setAttribute("class", "propertyEditorWrapper");

		var header = eventListenerTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Listener";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "Description";
		row[0].appendChild(headerCell2)

		var bodyT = eventListenerTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < actModel.componentList[selectedElement].listenEventList.length; h++) {
			var tempStringa = actModel.componentList[selectedElement].listenEventList[h].getId();
			var tempDes = actModel.componentList[selectedElement].listenEventList[h].getDescription();
			row[h] = bodyT.insertRow(-1);
			cell = row[h].insertCell(0);
			cell.innerHTML = tempStringa;
			cell = row[h].insertCell(1);
			cell.innerHTML = tempDes
		}
		wrapperDivListener.appendChild(listenerPanelCaptions);
		wrapperDivListener.appendChild(eventListenerTable);
		document.getElementById('listenerPanel').appendChild(wrapperDivListener);
	}

	//generate the event fields for the channel based on startcompoment and endcomponent
	var generateChannelEventsForChannel = function () {
		eventPanelCaptions.innerHTML = 'Channel';
		eventPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		eventChannelTable.setAttribute("class", "propertyEditorT");
		wrapperDivEvents.setAttribute("class", "propertyEditorWrapper");

		var chan = actModel.eventChannelList[selectedElement];
		var startcomp = chan.startComponent;
		var endcomp = chan.endComponent;

		var header = eventChannelTable.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		//row[0] = eventChannelTable.tHead.children[0];
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = endcomp.getId();
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = startcomp.getId();
		row[0].appendChild(headerCell2);
		var headerCell3 = document.createElement("TH");
		headerCell3.innerHTML = "Description";
		row[0].appendChild(headerCell3);

		var bodyT = eventChannelTable.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < endcomp.listenEventList.length; h++) {
			var eventName = endcomp.listenEventList[h].getId();
			row[h + 1] = bodyT.insertRow(-1);
			cell = row[h + 1].insertCell(0);
			cell.innerHTML = eventName;

			cell = row[h + 1].insertCell(1);
			dropdownList = null;
			dropdownList = document.createElement('select');
			for (var l = 0; l < startcomp.triggerEventList.length + 1; l++) {
				if (l === 0) {
					dropdownList.appendChild(new Option('---', l));
				} else {
					dropdownList.appendChild(new Option(startcomp.triggerEventList[l - 1].getId(), l));
				}
			}
			dropdownList.selectedIndex = '0';
			dropdownList.setAttribute("id", eventTableId + "/1/" + eventName);
			dropdownList.addEventListener("change", writeChannel);
			dropdownList.addEventListener("focus", setPreviousSelected);
			cell.appendChild(dropdownList);
			cell = row[h + 1].insertCell(2);
			textInput = document.createElement("INPUT");
			textInput.setAttribute("type", "text");
			textInput.setAttribute("id", eventTableId + "/2/" + eventName);
			eventTableId = eventTableId + 1;
			//textInput.addEventListener("input",writeChannelDescription);
			textInput.addEventListener("blur", writeChannelDescription);
			cell.appendChild(textInput);
		}

		var insertPosition = 1;
		for (var h = 0; h < endcomp.listenEventList.length; h++) {
			var eventName = endcomp.listenEventList[h].getId();

			for (var countx = 0; countx < chan.eventConnections.length; countx++) {
				var storedEventName = chan.eventConnections[countx].listener.getId();
				var storedTriggerEventName = chan.eventConnections[countx].trigger.getId();
				var eventDescription = chan.eventConnections[countx].description;
				if (eventName === storedEventName) {
					var rowToInsert = eventChannelTable.insertRow(insertPosition);
					cell = rowToInsert.insertCell(0);
					cell.innerHTML = eventName;

					cell = rowToInsert.insertCell(1);
					dropdownList = null;
					dropdownList = document.createElement('select');
					var selectedEventIndex = 0;
					for (var l = 0; l < startcomp.triggerEventList.length + 1; l++) {
						if (l === 0) {
							dropdownList.appendChild(new Option('---', l));
						} else {
							dropdownList.appendChild(new Option(startcomp.triggerEventList[l - 1].getId(), l));
							if (startcomp.triggerEventList[l - 1].getId() === storedTriggerEventName) {
								selectedEventIndex = l;
							}
						}
					}
					dropdownList.selectedIndex = selectedEventIndex;
					dropdownList.setAttribute("id", eventTableId + "/1/" + eventName);
					dropdownList.addEventListener("change", writeChannel);
					dropdownList.addEventListener("focus", setPreviousSelected);
					cell.appendChild(dropdownList);
					cell = rowToInsert.insertCell(2);
					textInput = document.createElement("INPUT");
					textInput.setAttribute("type", "text");
					textInput.value = eventDescription;
					if (eventDescription === "undefined") {
						textInput.value = "";
					}
					textInput.setAttribute("id", eventTableId + "/2/" + eventName);
					//textInput.addEventListener("input",writeChannelDescription);
					textInput.addEventListener("blur", writeChannelDescription);
					eventTableId = eventTableId + 1;
					cell.appendChild(textInput);

					insertPosition++;
				}
			}
			insertPosition++;
		}
		wrapperDivEvents.appendChild(eventPanelCaptions);
		wrapperDivEvents.appendChild(eventChannelTable);
		document.getElementById('eventPanel').appendChild(wrapperDivEvents);
	}

	//generate the property fields for the gui editor
	var generaterPropertiesForGUIEditor = function () {
		propertyEdPanelCaptions.innerHTML = 'GUI Editor Properties';
		propertyEdPanelCaptions.setAttribute("class", "propertyeditorCaptions");
		document.getElementById('propEdPanel').appendChild(propertyEdPanelCaptions);
		wrapperDivGUI.setAttribute("class", "propertyEditorWrapper");

		propertiesGuiEditorTableEditorProperties.setAttribute("class", "propertyEditorT");

		var header = propertiesGuiEditorTableEditorProperties.createTHead();
		header.setAttribute("class", "propertyEditorTh");
		row[0] = header.insertRow(0);
		var headerCell1 = document.createElement("TH");
		headerCell1.innerHTML = "Editor Properties";
		row[0].appendChild(headerCell1);
		var headerCell2 = document.createElement("TH");
		headerCell2.innerHTML = "Value";
		row[0].appendChild(headerCell2);

		var bodyT = propertiesGuiEditorTableEditorProperties.createTBody();
		bodyT.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < 4; h++) {
			row[h + 1] = bodyT.insertRow(-1);
			cell = row[h + 1].insertCell(0);
			if (h === 0) {
				cell.innerHTML = 'EnableGrid';
				tempStringa = editorProps.getEnableGrid();
			}
			if (h === 1) {
				cell.innerHTML = 'ShowGrid';
				tempStringa = editorProps.getShowGrid();
			}
			if (h === 2) {
				cell.innerHTML = 'GridSteps';
				tempStringa = editorProps.getGridSteps();
			}
			if (h === 3) {
				cell.innerHTML = 'ScreenRes';
				tempStringa = editorProps.getScreenRes();
			}
			cell = row[h + 1].insertCell(1);
			if (h === 0) {
				boolInput = null;
				boolInput = document.createElement("INPUT");
				boolInput.setAttribute("type", "checkbox");
				if (tempStringa) {
					boolInput.setAttribute("checked", true);
				}
				boolInput.setAttribute("value", tempStringa);
				boolInput.setAttribute("id", h + "/4/" + "enablegrid");
				boolInput.addEventListener("change", writeGuiEditorProperties);
				cell.appendChild(boolInput);
			}
			if (h === 1) {
				boolInput = null;
				boolInput = document.createElement("INPUT");
				boolInput.setAttribute("type", "checkbox");
				if (tempStringa) {
					boolInput.setAttribute("checked", true);
				}
				boolInput.setAttribute("value", tempStringa);
				boolInput.setAttribute("id", h + "/4/" + "showgrid");
				boolInput.addEventListener("change", writeGuiEditorProperties);
				cell.appendChild(boolInput);
			}
			if (h === 2) {
				dropdownList = null;
				dropdownList = document.createElement('select');
				for (l = 0; l < 4; l++) {
					if (l === 0) {
						dropdownList.appendChild(new Option('small', l));
					}
					if (l === 1) {
						dropdownList.appendChild(new Option('medium', l));
					}
					if (l === 2) {
						dropdownList.appendChild(new Option('large', l));
					}
					if (l === 3) {
						dropdownList.appendChild(new Option('huge', l));
					}
				}
				dropdownList.selectedIndex = tempStringa - 1;
				dropdownList.setAttribute("id", h + "/4/" + "gridsteps");
				dropdownList.addEventListener("change", writeGuiEditorProperties);
				cell.appendChild(dropdownList);
			}
			if (h === 3) {
				dropdownList = null;
				dropdownList = document.createElement('select');
				for (l = 0; l < 3; l++) {
					if (l === 0) {
						dropdownList.appendChild(new Option('FiveFour', l));
					}
					if (l === 1) {
						dropdownList.appendChild(new Option('SixteenNine', l));
					}
					if (l === 2) {
						dropdownList.appendChild(new Option('FourThree', l));
					}
				}
				dropdownList.selectedIndex = tempStringa - 1;
				dropdownList.setAttribute("id", h + "/4/" + "gridsteps");
				dropdownList.addEventListener("change", writeGuiEditorProperties);
				cell.appendChild(dropdownList);
			}
		}

		propertiesGuiEditorTable.setAttribute("class", "propertyEditorT");
		var header2 = propertiesGuiEditorTable.createTHead();
		header2.setAttribute("class", "propertyEditorTh");
		row[0] = header2.insertRow(0);
		var headerCell21 = document.createElement("TH");
		headerCell21.innerHTML = "ARE Properties";
		row[0].appendChild(headerCell21);
		var headerCell22 = document.createElement("TH");
		headerCell22.innerHTML = "Value";
		row[0].appendChild(headerCell22);

		var bodyT2 = propertiesGuiEditorTable.createTBody();
		bodyT2.setAttribute("class", "propertyEditorTb");

		for (var h = 0; h < 5; h++) {
			row[h] = bodyT2.insertRow(-1);
			cell = row[h].insertCell(0);
			if (h === 0) {
				cell.innerHTML = 'Decoration';
				tempStringa = actModel.modelGui.getDecoration();
			}
			if (h === 1) {
				cell.innerHTML = 'FullScreen';
				tempStringa = actModel.modelGui.getFullScreen();
			}
			if (h === 2) {
				cell.innerHTML = 'AlwaysOnTop';
				tempStringa = actModel.modelGui.getAlwaysOnTop();
			}
			if (h === 3) {
				cell.innerHTML = 'ToSystemTray';
				tempStringa = actModel.modelGui.getToSystemTray();
			}
			if (h === 4) {
				cell.innerHTML = 'ShowControlPanel';
				tempStringa = actModel.modelGui.getShowControlPanel();
			}
			cell = row[h].insertCell(1);
			boolInput = null;
			boolInput = document.createElement("INPUT");
			boolInput.setAttribute("type", "checkbox");
			boolInput.setAttribute("value", tempStringa);
			if (tempStringa) {
				boolInput.setAttribute("checked", true);
			}
			if (h === 0) {
				boolInput.setAttribute("id", h + "/5/" + "decoration");
			}
			if (h === 1) {
				boolInput.setAttribute("id", h + "/5/" + "fullscreen");
			}
			if (h === 2) {
				boolInput.setAttribute("id", h + "/5/" + "alwaysontop");
			}
			if (h === 3) {
				boolInput.setAttribute("id", h + "/5/" + "tosystemtray");
			}
			if (h === 4) {
				boolInput.setAttribute("id", h + "/5/" + "showcontrolpanel");
			}

			boolInput.addEventListener("change", writeGuiEditorProperties);
			cell.appendChild(boolInput);
		}

		wrapperDivGUI.appendChild(propertyEdPanelCaptions);
		wrapperDivGUI.appendChild(propertiesGuiEditorTable);
		wrapperDivGUI.appendChild(propertiesGuiEditorTableEditorProperties);
		document.getElementById('propEdPanel').appendChild(wrapperDivGUI);
	}

	//generates message to propPanel that number of selected items doesn't fit
	var generateEmptyMessage = function () {
		propertyDefaultText.innerHTML = 'Select one component to show properties';
		document.getElementById('propEdPanel').appendChild(propertyDefaultText);
	}

	//remove the content of the property editor
	var clearPropertyEditor = function () {
		if (inputPortTable.parentNode === wrapperDivInput) {
			inputPanelCaptions = null;
			inputPanelCaptions = document.createElement('p');
			wrapperDivInput.removeChild(inputPortTable);
			inputPortTable = null;
			inputPortTable = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('inputPanel').removeChild(wrapperDivInput);
			wrapperDivInput = null;
			wrapperDivInput = document.createElement('div');
		}
		if (outputPortTable.parentNode === wrapperDivOutput) {
			outputPanelCaptions = null;
			outputPanelCaptions = document.createElement('p');
			wrapperDivOutput.removeChild(outputPortTable);
			outputPortTable = null;
			outputPortTable = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('outputPanel').removeChild(wrapperDivOutput);
			wrapperDivOutput = null;
			wrapperDivOutput = document.createElement('div');
		}
		if (propertyTable.parentNode === wrapperDivProperty) {
			propertyEdPanelCaptions = null;
			propertyEdPanelCaptions = document.createElement('p');
			wrapperDivProperty.removeChild(propertyTable);
			propertyTable = null;
			propertyTable = document.createElement('table');
			wrapperDivProperty.removeChild(internalPropertyTable);
			internalPropertyTable = null;
			internalPropertyTable = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('propEdPanel').removeChild(wrapperDivProperty);
			wrapperDivProperty = null;
			wrapperDivProperty = document.createElement('div');
		}
		if (eventTriggerTable.parentNode === wrapperDivTrigger) {
			triggerPanelCaptions = null;
			triggerPanelCaptions = document.createElement('p');
			wrapperDivTrigger.removeChild(eventTriggerTable);
			eventTriggerTable = null;
			eventTriggerTable = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('triggerPanel').removeChild(wrapperDivTrigger);
			wrapperDivTrigger = null;
			wrapperDivTrigger = document.createElement('div');
		}
		if (eventListenerTable.parentNode === wrapperDivListener) {
			listenerPanelCaptions = null;
			listenerPanelCaptions = document.createElement('p');
			wrapperDivListener.removeChild(eventListenerTable);
			eventListenerTable = null;
			eventListenerTable = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('listenerPanel').removeChild(wrapperDivListener);
			wrapperDivListener = null;
			wrapperDivListener = document.createElement('div');
		}
		if (eventChannelTable.parentNode === wrapperDivEvents) {
			eventPanelCaptions = null;
			eventPanelCaptions = document.createElement('p');
			wrapperDivEvents.removeChild(eventChannelTable);
			eventChannelTable = null;
			eventChannelTable = document.createElement('table');
			row = [];
			cell = null;
			eventTableId = 0;
			document.getElementById('eventPanel').removeChild(wrapperDivEvents);
			wrapperDivEvents = null;
			wrapperDivEvents = document.createElement('div');
		}
		if (propertiesGuiEditorTable.parentNode === wrapperDivGUI) {
			propertyEdPanelCaptions = null;
			propertyEdPanelCaptions = document.createElement('p');
			wrapperDivGUI.removeChild(propertiesGuiEditorTable);
			wrapperDivGUI.removeChild(propertiesGuiEditorTableEditorProperties);
			propertiesGuiEditorTable = null;
			propertiesGuiEditorTable = document.createElement('table');
			propertiesGuiEditorTableEditorProperties = null;
			propertiesGuiEditorTableEditorProperties = document.createElement('table');
			row = [];
			cell = null;
			document.getElementById('propEdPanel').removeChild(wrapperDivGUI);
			wrapperDivGUI = null;
			wrapperDivGUI = document.createElement('div');
		}
		if (propertyDefaultText.parentNode === document.getElementById('propEdPanel')) {
			document.getElementById('propEdPanel').removeChild(propertyDefaultText);
			propertyDefaultText = null;
			propertyDefaultText = document.createElement('p');
		}

	}

	//methods handling outgoing events
	//================================

	//write the actual input modification to the property
	var writeProperty = function (evt) {
		changeNotSaved = false;
		lastModiviedElement = selectedElement;
		if (evt.target.type === 'checkbox') {
			var t = evt.target.checked.toString(); // a checkbox's "checked" field of seems to be more reliable than its "value"
		} else {
			var t = document.getElementById(evt.target.id).value;
		}
		var t_temp = document.getElementById(evt.target.id);
		var completeId = evt.target.id;
		lastEditFieldId = evt.target.id;
		var splitIda = completeId.split("/1/");
		var splitId = splitIda[0];

		//input validation for number and double fields
		var inputElement = document.getElementById(evt.target.id);
		var generateAlert = false;
		if (inputElement.type == 'number') {
			generateAlert = true;
			if (Boolean(t) && !isNaN(t) && t % 1 === 0) {
				generateAlert = false;
			}
		}
		if (inputElement.pattern === '^[-]?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?') {
			generateAlert = true;
			if (!isNaN(inputElement.value)) {
				if ((inputElement.value).match(/^[+-]?((\.\d+)|(\d+(\.\d+)?))$/) !== null) {
					generateAlert = false;
				}
			}
		}
		// write the values
		if (!generateAlert) {
			if (splitId !== 'xx_descr' && splitId !== 'xx_name') {
				actModel.componentList[selectedElement].propertyList[splitId].setValue(t);
			} else {
				if (splitId === 'xx_name') {
					actModel.componentList[selectedElement].setId(t);
				}
				if (splitId === 'xx_descr') {
					actModel.componentList[selectedElement].setDescription(t);
				}
			}
		}
		if (generateAlert) {
			$(function () {
				$("#alertPanel").dialog({
					modal : true,
					closeOnEscape : false,
					open : function (event, ui) {
						$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
					}
				});
			});
		}
		// if ARE is connected and synchronised, write value directly to ARE via REST
		if ((ACS.areStatus.getStatus != ACS.statusType.DISCONNECTED) &&
			(ACS.areStatus.getStatus != ACS.statusType.CONNECTING) &&
			(ACS.areStatus.getStatus != ACS.statusType.CONNECTIONLOST) &&
			ACS.areStatus.getSynchronised()) {
			setRuntimeComponentProperty(SRCP_successCallback, SRCP_errorCallback, actModel.componentList[selectedElement].getId(), actModel.componentList[selectedElement].propertyList[splitId].getKey(), t);

			function SRCP_successCallback(data, HTTPstatus) {
				log.debug('property successfully updated on ARE');
			}
			function SRCP_errorCallback(HTTPstatus, AREerrorMessage) {
				alert(AREerrorMessage);
			}
		}
	}

	//write the selected element of the Dropdown list to an eventchannel
	var writeChannel = function (evt) {
		var selectedChan = actModel.eventChannelList[selectedElement];
		var listenerComponent = selectedChan.startComponent;
		var triggerComponent = selectedChan.endComponent;

		var completeId = evt.target.id;
		var splitIda = completeId.split("/1/");
		var splitId = splitIda[0];
		var splitIdTriggerName = splitIda[1];
		var rowI = document.getElementById(completeId).parentNode.parentNode.rowIndex;
		var insertPosition = getPositionForChannelEven(rowI);
		var tableLenght = eventChannelTable.rows.length;

		var t_dropdown = document.getElementById(evt.target.id);
		var t = t_dropdown.options[t_dropdown.selectedIndex].text;
		var r_value = eventChannelTable.rows[rowI].cells[0].innerHTML;
		var eventConnectionDescription = eventChannelTable.rows[rowI].cells[2].firstChild.value;

		if (t !== '---' && previousDropDownEntry === '---') {
			//generate and insert eventconnection into selectedChannel
			var listener = ACS.event(r_value, '', listenerComponent.getId());
			var trigger = ACS.event(t, '', triggerComponent.getId());
			var description = eventConnectionDescription;
			var eventConnection = {
				listener,
				trigger,
				description
			};
			selectedChan.eventConnections.splice(insertPosition, 0, eventConnection);

			//generate View for further connections to the same listener
			//var eventName=triggerComponent;
			var rowToInsert = eventChannelTable.insertRow(rowI + 1);
			cell = rowToInsert.insertCell(0);
			cell.innerHTML = splitIdTriggerName;

			cell = rowToInsert.insertCell(1);
			dropdownList = null;
			dropdownList = document.createElement('select');
			for (l = 0; l < listenerComponent.triggerEventList.length + 1; l++) {
				if (l === 0) {
					dropdownList.appendChild(new Option('---', l));
				} else {
					dropdownList.appendChild(new Option(listenerComponent.triggerEventList[l - 1].getId(), l));
				}
			}
			dropdownList.selectedIndex = '0';
			dropdownList.setAttribute("id", eventTableId + "/1/" + splitIdTriggerName); //TODO lenght of list;
			dropdownList.addEventListener("change", writeChannel);
			dropdownList.addEventListener("focus", setPreviousSelected);
			cell.appendChild(dropdownList);
			/*cell = rowToInsert.insertCell(2);
			textInput = document.createElement("INPUT");
			textInput.setAttribute("type", "text");
			cell.appendChild(textInput);*/
			cell = rowToInsert.insertCell(2);
			textInput = document.createElement("INPUT");
			textInput.setAttribute("type", "text");
			textInput.setAttribute("id", eventTableId + "/2/" + splitIdTriggerName);
			eventTableId = eventTableId + 1;
			//textInput.addEventListener("input",writeChannelDescription);
			textInput.addEventListener("blur", writeChannelDescription);
			cell.appendChild(textInput);
		} else if (t !== '---' && previousDropDownEntry !== '---') {
			selectedChan.eventConnections.splice(insertPosition, 1);
			var listener = ACS.event(r_value, '', listenerComponent.getId());
			var trigger = ACS.event(t, '', triggerComponent.getId());
			var description = eventConnectionDescription;
			var eventConnection = {
				listener,
				trigger,
				description
			};
			selectedChan.eventConnections.splice(insertPosition, 0, eventConnection);
		} else {
			eventChannelTable.deleteRow(rowI);
			selectedChan.eventConnections.splice(insertPosition, 1);
		}
		previousDropDownEntry = splitIdTriggerName;
		ACS.areStatus.checkAndSetSynchronisation();
	}

	var writeChannelDescription = function (evt) {
		var selectedChan = actModel.eventChannelList[selectedElement];
		var listenerComponent = selectedChan.startComponent;
		var triggerComponent = selectedChan.endComponent;
		var completeId = evt.target.id;
		if (completeId) {
			var splitIda = completeId.split("/2/");
			var splitId = splitIda[0];
			var rowI = document.getElementById(completeId).parentNode.parentNode.rowIndex;
			var insertPosition = getPositionForChannelEven(rowI);
			var tableLenght = eventChannelTable.rows.length;
			var t_dropdown = eventChannelTable.rows[rowI].cells[1].firstChild;
			var t = t_dropdown.options[t_dropdown.selectedIndex].text;
			var r_value = eventChannelTable.rows[rowI].cells[0].innerHTML;
			var eventConnectionDescription = eventChannelTable.rows[rowI].cells[2].firstChild.value;

			if (t !== "---") {
				selectedChan.eventConnections.splice(insertPosition, 1);
				var listener = ACS.event(r_value, '', listenerComponent.getId());
				var trigger = ACS.event(t, '', triggerComponent.getId());
				var description = eventConnectionDescription;
				var eventConnection = {
					listener,
					trigger,
					description
				};
				selectedChan.eventConnections.splice(insertPosition, 0, eventConnection);
			}
		}
	}

	var writeInputPorts = function (evt) {
		var t_temp = document.getElementById(evt.target.id);
		var completeId = evt.target.id;
		var splitIda = completeId.split("/3/");
		var splitId = splitIda[0];
		var t = document.getElementById(evt.target.id).value;
		// toggle t in case of a boolean value
		if (t === 'false') {
			t = 'true';
			document.getElementById(evt.target.id).value = 'true';
		} else if (t === 'true') {
			t = 'false';
			document.getElementById(evt.target.id).value = 'false';
		}
		actModel.componentList[selectedElement].inputPortList[splitId].sync = t;
		ACS.areStatus.setSynchronised(false);
	}

	var writeGuiEditorProperties = function (evt) {
		var idString = evt.target.id;
		if (idString.indexOf('/4/') > -1) {
			var idStringSplita = idString.split('/4/');
			var idStringSplit = idStringSplita[0];
			if (idStringSplit === '0') {
				var tempbool = editorProps.getEnableGrid();
				editorProps.setEnableGrid(!tempbool);
			}
			if (idStringSplit === '1') {
				var tempbool = editorProps.getShowGrid();
				editorProps.setShowGrid(!tempbool);
			}
			if (idStringSplit === '2') {
				var t = document.getElementById(evt.target.id).selectedIndex + 1;
				editorProps.setGridSteps(t);
			}
			if (idStringSplit === '3') {
				var t = document.getElementById(evt.target.id).selectedIndex + 1;
				editorProps.setScreenRes(t);
			}
		}
		if (idString.indexOf('/5/') > -1) {
			var idStringSplita = idString.split('/5/');
			var idStringSplit = idStringSplita[0];
			if (idStringSplit === '0') {
				var tempbool = actModel.modelGui.getDecoration();
				actModel.modelGui.setDecoration(!tempbool);
				ACS.areStatus.checkAndSetSynchronisation();
			}
			if (idStringSplit === '1') {
				var tempbool = actModel.modelGui.getFullScreen();
				actModel.modelGui.setFullScreen(!tempbool);
				ACS.areStatus.checkAndSetSynchronisation();
			}
			if (idStringSplit === '2') {
				var tempbool = actModel.modelGui.getAlwaysOnTop();
				actModel.modelGui.setAlwaysOnTop(!tempbool);
				ACS.areStatus.checkAndSetSynchronisation();
			}
			if (idStringSplit === '3') {
				var tempbool = actModel.modelGui.getToSystemTray();
				actModel.modelGui.setToSystemTray(!tempbool);
				ACS.areStatus.checkAndSetSynchronisation();
			}
			if (idStringSplit === '4') {
				var tempbool = actModel.modelGui.getShowControlPanel();
				actModel.modelGui.setShowControlPanel(!tempbool);
				ACS.areStatus.checkAndSetSynchronisation();
			}
		}
	}

	var writeNotSavedChange = function (evt) {
		//workaround to save change also when no blur event is fired
		if (notSavedId === 'xx_name') {
			actModel.componentList[selectedElement].setId(notSavedValue);
		} else if (notSavedId === 'xx_descr') {
			actModel.componentList[selectedElement].setDescription(notSavedValue);
		} else {
			actModel.componentList[selectedElement].propertyList[notSavedId].setValue(notSavedValue);
		}
		changeNotSaved = false;
	}

	var writePropertyChangLocal = function (evt) {
		var completeId = evt.target.id;
		var splitIda = completeId.split("/1/");
		var splitId = splitIda[0];
		notSavedValue = document.getElementById(evt.target.id).value;
		notSavedId = splitId;
		changeNotSaved = true;
	}
	//class needed methodes helper functions
	//======================================

	//returns the insert position of an event into an event channel based on the row position
	var getPositionForChannelEven = function (rowInd) {
		var countera = 0;
		for (var i = 1; i < rowInd; i++) {
			var x = eventChannelTable.rows[i];
			var y = x.cells[1].childNodes[0].id;
			var t_dropdown = document.getElementById(y);
			var t = t_dropdown.options[t_dropdown.selectedIndex].text;
			if (t !== '---') {
				countera++;
			}
		}
		return countera;
	}

	var setPreviousSelected = function (evt) {
		var t_dropdown = document.getElementById(evt.target.id);
		previousDropDownEntry = t_dropdown.options[t_dropdown.selectedIndex].text;
	}

	var setPreviousNumber = function (evt) {
		var inputNumberField = document.getElementById(evt.target.id);
		previousNumberEntry = inputNumberField.value;
	}

	var stringOfEnum = function (enum1, value1) {
		for (var k in enum1)
			if (enum1[k] == value1)
				return k;
		return null;
	}

	var hideAllTabsAndPanels = function () {
		//hide all tabs
		document.getElementById("propertyEditorTabList").children[0].setAttribute("class", "tab propEdTab displayNone");
		document.getElementById("propertyEditorTabList").children[1].setAttribute("class", "tab propEdTab displayNone");
		document.getElementById("propertyEditorTabList").children[2].setAttribute("class", "tab propEdTab displayNone");
		document.getElementById("propertyEditorTabList").children[3].setAttribute("class", "tab propEdTab displayNone");
		document.getElementById("propertyEditorTabList").children[4].setAttribute("class", "tab propEdTab displayNone");
		document.getElementById("propertyEditorTabList").children[5].setAttribute("class", "tab propEdTab displayNone");
		
		document.getElementById("propertyEditorTabList").children[0].tabIndex="-1";
		document.getElementById("propertyEditorTabList").children[1].tabIndex="-1";
		document.getElementById("propertyEditorTabList").children[2].tabIndex="-1";
		document.getElementById("propertyEditorTabList").children[3].tabIndex="-1";
		document.getElementById("propertyEditorTabList").children[4].tabIndex="-1";
		document.getElementById("propertyEditorTabList").children[5].tabIndex="-1";
		
		document.getElementById("propertyEditorTabList").children[0].setAttribute("aria-selected", "false");
		document.getElementById("propertyEditorTabList").children[1].setAttribute("aria-selected", "false");
		document.getElementById("propertyEditorTabList").children[2].setAttribute("aria-selected", "false");
		document.getElementById("propertyEditorTabList").children[3].setAttribute("aria-selected", "false");
		document.getElementById("propertyEditorTabList").children[4].setAttribute("aria-selected", "false");
		document.getElementById("propertyEditorTabList").children[5].setAttribute("aria-selected", "false");

		//hide all panels
		document.getElementById("propEdPanel").setAttribute("class", "panel propEdPanel displayNone");
		document.getElementById("inputPanel").setAttribute("class", "panel propEdPanel displayNone");
		document.getElementById("outputPanel").setAttribute("class", "panel propEdPanel displayNone");
		document.getElementById("triggerPanel").setAttribute("class", "panel propEdPanel displayNone");
		document.getElementById("listenerPanel").setAttribute("class", "panel propEdPanel displayNone");
		document.getElementById("eventPanel").setAttribute("class", "panel propEdPanel displayNone");

		document.getElementById("propEdPanel").setAttribute("aria-hidden", "true");
		document.getElementById("inputPanel").setAttribute("aria-hidden", "true");
		document.getElementById("outputPanel").setAttribute("aria-hidden", "true");
		document.getElementById("triggerPanel").setAttribute("aria-hidden", "true");
		document.getElementById("listenerPanel").setAttribute("aria-hidden", "true");
		document.getElementById("eventPanel").setAttribute("aria-hidden", "true");

	}

	var alertReset = function () {
		//sets the value, which was stored before invlid input and focus the input
		var tempId = lastModiviedElement;
		if (actModel.componentList.length > 0) {
			actModelOld.deSelectAll();
		}
		$('#alertPanel').dialog('close');
		setTimeout(function () {
			actModelOld.addItemToSelection(actModelOld.componentList[tempId]);
			if (document.getElementById(lastEditFieldId) !== null) {
				document.getElementById(lastEditFieldId).select();
			}
		}, 50);
	}

	// ********************************************** handlers ***********************************************************

	var selectedComponentEventHandler = function () {
		selectedNumberOfComponents++;
		actModelOld = actModel;
		generateViews();
	}

	var selectedEventChannelEventHandler = function () {
		selectedNumberOfEventChannels++;
		actModelOld = actModel;
		generateViews();
	}

	var selectedDataChannelEventHandler = function () {
		selectedNumberOfDataChannels++;
		//generateViews();
	}

	var deSelectedDataChannelEventHandler = function () {
		if (selectedNumberOfDataChannels > 0) {
			// after remove a deselect event can happen => if statement necessary else -1 can happen
			selectedNumberOfDataChannels--;
		}
		//generateViews();
	}

	var deSelectedComponentEventHandler = function () {
		selectedElementOld = selectedElement;
		if (selectedNumberOfComponents > 0) {
			// after remove a deselect event can happen => if statement necessary else -1 can happen
			selectedNumberOfComponents--;
		}
		if (changeNotSaved) { //workaround to save change also when no blur event is fired
			writeNotSavedChange();
		}
		generateViews();
	}

	var deSelectedEventChannelEventHandler = function () {
		selectedElementOld = selectedElement;
		if (selectedNumberOfEventChannels > 0) {
			// after remove a deselect event can happen => if statement necessary else -1 can happen
			selectedNumberOfEventChannels--;
		}
		if (changeNotSaved) { //workaround to save change also when no blur event is fired
			writeNotSavedChange();
		}
		generateViews();
	}

	var componentAddedEventHandler = function () {
		actModel.componentList[actModel.componentList.length - 1].events.registerHandler('selectedEvent', selectedComponentEventHandler);
		actModel.componentList[actModel.componentList.length - 1].events.registerHandler('deSelectedEvent', deSelectedComponentEventHandler);
	}

	var removeComponentEventHandler = function () {
		selectedNumberOfComponents = 0;
		selectedNumberOfDataChannels = 0;
		selectedNumberOfEventChannels = 0;
		generateViews();
	}

	var eventChannelAddedEventHandler = function () {
		actModel.eventChannelList[actModel.eventChannelList.length - 1].events.registerHandler('selectedEvent', selectedEventChannelEventHandler);
		actModel.eventChannelList[actModel.eventChannelList.length - 1].events.registerHandler('deSelectedEvent', deSelectedEventChannelEventHandler);
	}

	var eventChannelRemovedEventHandler = function () {
		selectedNumberOfComponents = 0;
		selectedNumberOfDataChannels = 0;
		selectedNumberOfEventChannels = 0;
		generateViews();
	}

	var dataChannelAddedEventHandler = function () {
		actModel.dataChannelList[actModel.dataChannelList.length - 1].events.registerHandler('selectedEvent', selectedDataChannelEventHandler);
		actModel.dataChannelList[actModel.dataChannelList.length - 1].events.registerHandler('deSelectedEvent', deSelectedDataChannelEventHandler);
	}

	var dataChannelRemovedEventHandler = function () {
		selectedNumberOfComponents = 0;
		selectedNumberOfDataChannels = 0;
		selectedNumberOfEventChannels = 0;
		generateViews();
	}

	var actModelChangedEventHandler = function () {
		//deregister the old models events
		actModel.events.removeHandler('componentAddedEvent', componentAddedEventHandler);
		actModel.events.removeHandler('componentRemovedEvent', removeComponentEventHandler);
		actModel.events.removeHandler('eventChannelAddedEvent', eventChannelAddedEventHandler);
		actModel.events.removeHandler('eventChannelRemovedEvent', eventChannelRemovedEventHandler);
		actModel.events.removeHandler('dataChannelAddedEvent', dataChannelAddedEventHandler);
		actModel.events.removeHandler('dataChannelRemovedEvent', dataChannelRemovedEventHandler);
		actModel.events.removeHandler('modelChangedEvent', modelChangedEventHandler);

		//get the new model
		actModel = modelList.getActModel();
		//reset the counts of selections
		var tempCountAllSelected = actModel.selectedItemsList.length;

		selectedNumberOfComponents = 0;
		for (var i = 0; i < actModel.componentList.length; i++) {
			if (actModel.componentList[i].getIsSelected()) {
				selectedNumberOfComponents++;
			}
		}
		selectedNumberOfEventChannels = 0;
		for (var i = 0; i < actModel.eventChannelList.length; i++) {
			if (actModel.eventChannelList[i].getIsSelected()) {
				selectedNumberOfEventChannels++;
			}
		}

		actModel.events.registerHandler('componentAddedEvent', componentAddedEventHandler);
		actModel.events.registerHandler('componentRemovedEvent', removeComponentEventHandler);
		actModel.events.registerHandler('eventChannelAddedEvent', eventChannelAddedEventHandler);
		actModel.events.registerHandler('eventChannelRemovedEvent', eventChannelRemovedEventHandler);
		actModel.events.registerHandler('dataChannelAddedEvent', dataChannelAddedEventHandler);
		actModel.events.registerHandler('dataChannelRemovedEvent', dataChannelRemovedEventHandler);
		actModel.events.registerHandler('modelChangedEvent', modelChangedEventHandler);

		modelViewActTabPanel.events.removeHandler('tabSwitchedEvent', tabSwitchedEventHandler);
		for (var i = 0; i < modelViewList.length; i++) {
			if (modelViewList[i] && (modelViewList[i].getModel() === actModel)) {
				modelViewAct = modelViewList[i];
				modelViewActTabPanel = modelViewList[i].getModelTabPanel();
				modelViewActTabPanel.events.registerHandler('tabSwitchedEvent', tabSwitchedEventHandler);
			}
		}
		//clearPropertyEditor();
		var containerId = modelViewAct.getModelContainerId();
		var panelId = 'modelPanel' + containerId;
		generateViews();
	}

	var modelChangedEventHandler = function () {
		// derigister all event for the actual model
		for (var countera = 0; countera <= actModel.componentList.length - 1; countera++) {
			actModel.componentList[countera].events.removeHandler('selectedEvent', selectedComponentEventHandler);
			actModel.componentList[countera].events.removeHandler('deSelectedEvent', deSelectedComponentEventHandler);
		}
		for (var counterx = 0; counterx <= actModel.dataChannelList.length - 1; counterx++) {
			actModel.dataChannelList[counterx].events.removeHandler('selectedEvent', selectedDataChannelEventHandler);
			actModel.dataChannelList[counterx].events.removeHandler('deSelectedEvent', deSelectedDataChannelEventHandler);
		}
		for (var counterx = 0; counterx <= actModel.eventChannelList.length - 1; counterx++) {
			actModel.eventChannelList[counterx].events.removeHandler('selectedEvent', selectedEventChannelEventHandler);
			actModel.eventChannelList[counterx].events.removeHandler('deSelectedEvent', deSelectedEventChannelEventHandler);
		}
		actModel = modelList.getActModel();
		//in case that model was loaded select and deselect events must be registered
		for (var counterb = 0; counterb <= actModel.componentList.length - 1; counterb++) {
			actModel.componentList[counterb].events.registerHandler('selectedEvent', selectedComponentEventHandler);
			actModel.componentList[counterb].events.registerHandler('deSelectedEvent', deSelectedComponentEventHandler);
		}
		for (var counterb = 0; counterb <= actModel.dataChannelList.length - 1; counterb++) {
			actModel.dataChannelList[counterb].events.registerHandler('selectedEvent', selectedDataChannelEventHandler);
			actModel.dataChannelList[counterb].events.registerHandler('deSelectedEvent', deSelectedDataChannelEventHandler);
		}
		for (var countery = 0; countery <= actModel.eventChannelList.length - 1; countery++) {
			actModel.eventChannelList[countery].events.registerHandler('selectedEvent', selectedEventChannelEventHandler);
			actModel.eventChannelList[countery].events.registerHandler('deSelectedEvent', deSelectedEventChannelEventHandler);
		}
	}

	var tabSwitchedEventHandler = function () {
		generateViews();
	}

	// ***********************************************************************************************************************
	// ************************************************** public stuff *******************************************************
	// ***********************************************************************************************************************
	var returnObj = {};

	// ***********************************************************************************************************************
	// ************************************************** constructor code ***************************************************
	// ***********************************************************************************************************************

	// generate panel to represent the components Properties or the GUI Editor Properties
	var li1 = document.createElement('li');
	li1.setAttribute('id', 'propEdTab');
	li1.setAttribute('class', 'tab propEdTab');
	li1.setAttribute('aria-controls', 'propEdPanel');
	li1.setAttribute('aria-selected', 'false');
	li1.setAttribute('role', 'tab');
	li1.setAttribute('tabindex', -1);
	li1.textContent = ACS.vConst.PROPERTYEDITOR_PROPERTIESHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li1);
	var div = document.createElement('div');
	div.setAttribute('id', 'propEdPanel');
	div.setAttribute('class', 'panel propEdPanel');
	div.setAttribute('aria-labelledby', 'propEdTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	// generate panel to represent the components Input Ports
	var li2 = document.createElement('li');
	li2.setAttribute('id', 'propertiesInputTab');
	li2.setAttribute('class', 'tab propEdTab displayNone');
	li2.setAttribute('aria-controls', 'inputPanel');
	li2.setAttribute('aria-selected', 'false');
	li2.setAttribute('role', 'tab');
	li2.setAttribute('tabindex', -1);
	li2.textContent = ACS.vConst.PROPERTYEDITOR_INPUTHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li2);
	div = document.createElement('div');
	div.setAttribute('id', 'inputPanel');
	div.setAttribute('class', 'panel propEdPanel displayNone');
	div.setAttribute('aria-labelledby', 'inputTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	// generate panel to represent the components Output Ports
	var li3 = document.createElement('li');
	li3.setAttribute('id', 'propertiesOutputTab');
	li3.setAttribute('class', 'tab propEdTab displayNone');
	li3.setAttribute('aria-controls', 'outputPanel');
	li3.setAttribute('aria-selected', 'false');
	li3.setAttribute('role', 'tab');
	li3.setAttribute('tabindex', -1);
	li3.textContent = ACS.vConst.PROPERTYEDITOR_OUTPUTHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li3);
	div = document.createElement('div');
	div.setAttribute('id', 'outputPanel');
	div.setAttribute('class', 'panel propEdPanel');
	div.setAttribute('aria-labelledby', 'outputTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	// generate panel to represent the components Event Triggers
	var li4 = document.createElement('li');
	li4.setAttribute('id', 'propertiesTriggerTab');
	li4.setAttribute('class', 'tab propEdTab displayNone');
	li4.setAttribute('aria-controls', 'triggerPanel');
	li4.setAttribute('aria-selected', 'false');
	li4.setAttribute('role', 'tab');
	li4.setAttribute('tabindex', -1);
	li4.textContent = ACS.vConst.PROPERTYEDITOR_TRIGGERHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li4);
	div = document.createElement('div');
	div.setAttribute('id', 'triggerPanel');
	div.setAttribute('class', 'panel propEdPanel');
	div.setAttribute('aria-labelledby', 'triggerTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	// generate panel to represent the components Event Listener
	var li5 = document.createElement('li');
	li5.setAttribute('id', 'propertiesListenerTab');
	li5.setAttribute('class', 'tab propEdTab displayNone');
	li5.setAttribute('aria-controls', 'listenerPanel');
	li5.setAttribute('aria-selected', 'false');
	li5.setAttribute('role', 'tab');
	li5.setAttribute('tabindex', -1);
	li5.textContent = ACS.vConst.PROPERTYEDITOR_LISTENERHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li5);
	div = document.createElement('div');
	div.setAttribute('id', 'listenerPanel');
	div.setAttribute('class', 'panel propEdPanel');
	div.setAttribute('aria-labelledby', 'listenerTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	//Panel to generat event connections
	var li6 = document.createElement('li');
	li6.setAttribute('id', 'propertiesEventTab');
	li6.setAttribute('class', 'tab propEdTab displayNone');
	li6.setAttribute('aria-controls', 'eventPanel');
	li6.setAttribute('aria-selected', 'false');
	li6.setAttribute('role', 'tab');
	li6.setAttribute('tabindex', -1);
	li6.textContent = ACS.vConst.PROPERTYEDITOR_EVENTHEADER;
	document.getElementById(ACS.vConst.PROPERTYEDITOR_TABLIST).appendChild(li6);
	div = document.createElement('div');
	div.setAttribute('id', 'eventPanel');
	div.setAttribute('class', 'panel propEdPanel');
	div.setAttribute('aria-labelledby', 'eventTab');
	div.setAttribute('role', 'tabpanel');
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(div);

	//alert div
	var divAlert = document.createElement('div');
	divAlert.setAttribute('id', 'alertPanel');
	divAlert.setAttribute('title', 'Invalid Input');
	divAlert.setAttribute('class', 'alertDialog');
	divAlert.innerHTML = "Old value will be restored!<br>";
	var but1 = document.createElement('BUTTON'); //call the alert reset function to reset input value
	but1.innerHTML = "Ok";
	but1.addEventListener('click', alertReset);
	divAlert.appendChild(but1);
	document.getElementById(ACS.vConst.PROPERTYEDITOR_MOTHERPANEL).appendChild(divAlert);

	document.getElementById('propEdPanel').setAttribute("style", "overflow:auto;");
	document.getElementById('inputPanel').setAttribute("style", "overflow:auto;");
	document.getElementById('outputPanel').setAttribute("style", "overflow:auto;");
	document.getElementById('triggerPanel').setAttribute("style", "overflow:auto;");
	document.getElementById('listenerPanel').setAttribute("style", "overflow:auto;");
	document.getElementById('eventPanel').setAttribute("style", "overflow:auto;");

	document.getElementById("propertyEditorTabList").children[0].setAttribute("class", "tab propEdTab");
	generateEmptyMessage();
	document.getElementById("propEdPanel").setAttribute("class", "panel propEdPanel");
	document.getElementById("propertyEditorTabList").children[0].setAttribute("aria-selected", "true");
	document.getElementById("propEdPanel").setAttribute("aria-hidden", "false");
	
	propertiesTabPanel.updatePanel();

	modelList.events.registerHandler('actModelChangedEvent', actModelChangedEventHandler);
	actModel.events.registerHandler('modelChangedEvent', modelChangedEventHandler);
	actModel.events.registerHandler('componentAddedEvent', componentAddedEventHandler);
	actModel.events.registerHandler('componentRemovedEvent', removeComponentEventHandler);
	actModel.events.registerHandler('eventChannelAddedEvent', eventChannelAddedEventHandler);
	actModel.events.registerHandler('eventChannelRemovedEvent', eventChannelRemovedEventHandler);
	actModel.events.registerHandler('dataChannelAddedEvent', dataChannelAddedEventHandler);
	actModel.events.registerHandler('dataChannelRemovedEvent', dataChannelRemovedEventHandler);

	modelViewActTabPanel.events.registerHandler('tabSwitchedEvent', tabSwitchedEventHandler);

	return returnObj;
}
