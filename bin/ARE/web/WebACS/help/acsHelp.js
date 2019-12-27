/*
 * AsTeRICS - Assistive Technology Rapid Integration and Construction Set (http://www.asterics.org)
 * 
 * 
 *        d8888  .d8888b.   .d8888b.          888   888          888
 *       d88888 d88P  Y88b d88P  Y88b         888   888          888
 *      d88P888 888    888 Y88b.              888   888          888
 *     d88P 888 888         "Y888b.   8888888 888888888  .d88b.  888       8888888b.
 *    d88P  888 888            "Y88b. 8888888 888888888 d8P  Y8b 888       888    Y8b
 *   d88P   888 888    888       "888         888   888 88888888 888       888    888
 *  d8888888888 Y88b  d88P Y88b  d88P         888   888 Y8b.     888888888 888    d8P
 * d88P     888  "Y8888P"   "Y8888P"          888   888  "Y8888  888888888 8888888P"
 *                                                                         888  
 *                                                                         888
 *                                                                         888
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
 
 var acsHelp = function() {
	 
// ***********************************************************************************************************************
// ************************************************** static methods *****************************************************
// ***********************************************************************************************************************
	acsHelp.resizeIframe = function(obj) {	
		try {
			var win = obj.contentWindow || obj.contentDocument;
			obj.style.height = win.document.body.offsetHeight + 50 + 'px';
		} catch(err) {
			console.log('An error occurred during optimising size of iFrame - using default settings instead.')
		}
	}
						
// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var jsonFileName = '';
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var removeWhiteSpace = function(str) {
		var arr = str.split(' ');
		str = '';
		for (var i = 0; i < arr.length; i++) {
			str = str + arr[i].trim();
		}
		return str;
	}
	
	var subtypeIsThere = function(element, subtype) {
		for (var i = 0; i < element.childNodes.length; i++) {
			if (element.childNodes[i].textContent.indexOf(subtype) === 0) return true;
		}
		return false;
	}

	var setSubtype = function(subtype, subtypeNoSpace, typeList, type) {
		var li = document.createElement('li');
		var div = document.createElement('div');
		div.setAttribute('id', type + subtypeNoSpace);
		var divText = document.createTextNode(subtype);
		div.appendChild(divText);
		li.appendChild(div);
		var ul = document.createElement('ul');
		ul.setAttribute('class', 'compMenuL2 compMenu');
		ul.setAttribute('id', type + subtypeNoSpace + 'List');
		li.appendChild(ul);
		document.getElementById(typeList).appendChild(li);
	}

	var setComponent = function(actCompName, subtypeNoSpace, type, pathPrefix) {
		// set the component in the menu:
		var comp = document.createElement('li');
		var compText = document.createTextNode(actCompName);
		comp.appendChild(compText);
		comp.setAttribute('data-filename', pathPrefix + type + 's/' + actCompName + '.htm');
		document.getElementById(type + subtypeNoSpace + 'List').appendChild(comp);
		// set the component in the datalist of the quickselect-field:
		var opt = document.createElement('option');
		opt.setAttribute('id', 'opt_' + actCompName);
		opt.setAttribute('value', actCompName);
		opt.setAttribute('data-filename', pathPrefix + type + 's/' + actCompName + '.htm');
		document.getElementById('componentsDataList').appendChild(opt);
	}	

	var buildComponentMenu = function(jsonData, componentCollection) {
		// first empty the menu...
		var sensorsList = document.getElementById('sensorsList');
		var processorsList = document.getElementById('processorsList');
		var actuatorsList = document.getElementById('actuatorsList');
		while (sensorsList.hasChildNodes()) sensorsList.removeChild(sensorsList.childNodes[0]);
		while (processorsList.hasChildNodes()) processorsList.removeChild(processorsList.childNodes[0]);
		while (actuatorsList.hasChildNodes()) actuatorsList.removeChild(actuatorsList.childNodes[0]);
		// ...and empty the dataList for the quickSelect
		var dataList = document.getElementById('componentsDataList');
		while (dataList.hasChildNodes()) dataList.removeChild(dataList.childNodes[0]);
		// fill the menu with the new content
		var components = componentCollection.getElementsByTagName('componentType');
		// set the subcategories:
		for (var i = 0; i < components.length; i++) {
			var actCompName = components.item(i).attributes.getNamedItem('id').textContent;
			if (actCompName.indexOf('Oska') === -1) actCompName = actCompName.slice(9); // the slice eliminates the "asterics."
			var type = components.item(i).getElementsByTagName('type').item(0).textContent;
			var subtype = components.item(i).getElementsByTagName('type').item(0).attributes.getNamedItem('subtype').textContent;
			var subtypeNoSpace = removeWhiteSpace(subtype);
			switch (type) {
				case 'sensor':
					// set new subcategory, if not yet done so:
					if (!subtypeIsThere(document.getElementById('sensorsList'), subtype)) {
						setSubtype(subtype, subtypeNoSpace, 'sensorsList', 'sensor');
					}
					// set the component:
					setComponent(actCompName, subtypeNoSpace, 'sensor', jsonData.plugins);
					break;
				case 'processor':
					// set new subcategory, if not yet done so:
					if (!subtypeIsThere(document.getElementById('processorsList'), subtype)) {
						setSubtype(subtype, subtypeNoSpace, 'processorsList', 'processor');
					}
					// set the component:
					setComponent(actCompName, subtypeNoSpace, 'processor', jsonData.plugins);
					break;
				case 'actuator':
					// set new subcategory, if not yet done so:
					if (!subtypeIsThere(document.getElementById('actuatorsList'), subtype)) {
						setSubtype(subtype, subtypeNoSpace, 'actuatorsList', 'actuator');
					}
					// set the component:
					setComponent(actCompName, subtypeNoSpace, 'actuator', jsonData.plugins);
					break;
			}
		}
	}

	var addToMenu = function (jsonData, htmlMenuSnippet, prefixPath) {
		var htmlObject = $.parseHTML(htmlMenuSnippet);
		var elements=$( htmlObject ).find("[data-filename]");
		for(var i=0; i<elements.length; i++) {
			var currentFilename = $( elements[i] ).attr("data-filename");
			$( elements[i] ).attr("data-filename", prefixPath + currentFilename);
		}
		$('#menu').append(htmlObject);
	}
	
	var setupMenu = function(jsonData) {
		if ($('#menu').menu('instance')) $('#menu').menu('destroy');
		$('#menu').menu({
			select: function(evt, ui) {
				if (ui.item[0].childElementCount === 0) {					
					var pagePath = ui.item.attr('data-filename');
					$('#mainContent').attr('src', pagePath);
				}
			}
		});
	}
	
	var loadPluginMenuFromComponentCollection = function(jsonData) {
		// load the component collection
		var httpReq = new XMLHttpRequest();
		httpReq.onreadystatechange = function() {
			if (httpReq.readyState === XMLHttpRequest.DONE && (httpReq.status === 404 || httpReq.status === 0)) {
				alert('Could not find component collection file. Please make sure the file "defaultComponentCollection.abd" exists in the folder specified in helpPaths_*.json.');
				setupMenu(jsonData);
			} else if (httpReq.readyState === XMLHttpRequest.DONE && httpReq.status === 200) {
				var componentCollection = $.parseXML(httpReq.responseText);
				// after having successfully loaded the componentCollection, build  and refresh the menu
				buildComponentMenu(jsonData, componentCollection);
				setupMenu(jsonData);
				// set the handlers for the quickselect field and the corresponding show-button
				document.getElementById('quickselect').addEventListener('change', function() {
					var compName = this.value;
					var file = document.getElementById('opt_' + compName).attributes.getNamedItem('data-filename').value;
					$('#mainContent').attr('src', file);
					this.value = '';				
				});
				document.getElementById('showButton').addEventListener('click', function() {
					var compName = document.getElementById('quickselect').value;
					var file = document.getElementById('opt_' + compName).attributes.getNamedItem('data-filename').value;
					$('#mainContent').attr('src', file);
					document.getElementById('quickselect').value = '';				
				});			
			}
		}
		// try to load component collection from the path specified in the json file
		if (jsonData.componentCollection) { 
			httpReq.open('GET', jsonData.componentCollection + 'defaultComponentCollection.abd', true);
			httpReq.send();	
		} else {
			alert('No component collection file specified. Please make sure to specify the path to "defaultComponentCollection.abd" in helpPaths_*.json.');
			setupMenu(jsonData);
		}
	}

	var loadPluginHelp = function(jsonData) {
		// load the code snippets for plugin-help, if necessary (i.e. if path is not null) - then load plugin-help according to componentCollection
		if (jsonData.plugins) {
			var httpReq = new XMLHttpRequest();
			httpReq.onreadystatechange = function() {
				if (httpReq.readyState === XMLHttpRequest.DONE && (httpReq.status === 404 || httpReq.status === 0)) {
					console.log('Error loading code snippets for help - maybe "help.htm" is missing at ' + jsonData.plugins + '?');
					setupMenu(jsonData);
				} else if (httpReq.readyState === XMLHttpRequest.DONE && httpReq.status === 200) {
					addToMenu(jsonData,httpReq.responseText.substring(0, httpReq.responseText.lastIndexOf('</li>') + 4),jsonData.plugins); // plugin help snippet
					$('#menuBlock').append(httpReq.responseText.substring(httpReq.responseText.lastIndexOf('</li>') + 5, httpReq.responseText.length)); // quickselect field
					loadPluginMenuFromComponentCollection(jsonData);
				}
			}
			httpReq.open('GET', jsonData.plugins + 'help.htm.menu', true);
			httpReq.send();
		} else {
			setupMenu(jsonData);
		}
	}
	
	var loadACSAndPluginHelp = function(jsonData) {
		// load misc. ACS-help, if necessary (i.e. if path is not null) - then (or otherwise) load plugin-help
		if (jsonData.ACS) {
			var httpReq = new XMLHttpRequest();
			httpReq.onreadystatechange = function() {
				if (httpReq.readyState === XMLHttpRequest.DONE && (httpReq.status === 404 || httpReq.status === 0)) {
					console.log('Error loading code snippet for ACS help - maybe "help.htm.menu" is missing at ' + jsonData.ACS + '?');
					loadPluginHelp(jsonData);
				} else if (httpReq.readyState === XMLHttpRequest.DONE && httpReq.status === 200) {
					addToMenu(jsonData,httpReq.responseText,jsonData.ACS);
					loadPluginHelp(jsonData);
				}
			}
			httpReq.open('GET', jsonData.ACS + 'help.htm.menu', true);
			httpReq.send();
		} else {
			loadPluginHelp(jsonData);
		}
	}
	
	var loadStartPage = function(jsonData) {		
		// make sure to load the correct file on startup, if a querystring has been given
		if (window.location.search !== '') {
			var qstr = window.location.search.substr(1, window.location.search.length-1).split('&');
			if (qstr[0] === 'are' && jsonData.ARE) {
				$('#mainContent').attr('src', jsonData.ARE + qstr[1]);
			} else if (qstr[0] === 'acs' && jsonData.ACS) {
				$('#mainContent').attr('src', jsonData.ACS + qstr[1]);
			} else if (qstr[0] === 'plugins' && jsonData.plugins) {
				$('#mainContent').attr('src', jsonData.plugins + qstr[1]);
			} else if (qstr.length === 1){
				$('#mainContent').attr('src', qstr[0]);
			} else {
				$('#mainContent').attr('src', 'startPage.htm');
			}
		} else {
			$('#mainContent').attr('src', 'startPage.htm');
		}
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	// find out if help is being used locally or hosted on a webserver
	if (window.location.href.includes('file:///')) {
		jsonFileName = 'help_files/helpPaths-local.json';
	} else {
		jsonFileName = 'help_files/helpPaths-hosted.json';
	}
	// load the paths to the help files from json
	$.getJSON(jsonFileName, function(jsonData) {
		// find the absolute URL to help.css (to inject into help files loaded into iFrame)
		$('#mainContent').load(function() {
			if (jsonData.stylesheet) {
				var href = window.location.href.split('?');
				var URLToStyle = href[0].substring(0, href[0].lastIndexOf('/') + 1) + jsonData.stylesheet + 'help.css';
				// insert link to stylesheet into page loaded in iFrame
				$(this).contents().find('head')[0].append($('<link rel="stylesheet" type="text/css" href="' + URLToStyle + '" />')[0]);				
			} else {
				console.log('Error loading URL to stylesheet - please make sure the relative URL to help.css is specified in helpPaths.json');
			}						
		});		
		// load misc. ARE-help, if necessary (i.e. if path is not null) - then (or otherwise) load ACS- and plugin-help
		if (jsonData.ARE) {
			var httpRequest = new XMLHttpRequest();
			httpRequest.onreadystatechange = function() {
				if (httpRequest.readyState === XMLHttpRequest.DONE && (httpRequest.status === 404 || httpRequest.status === 0)) {
					console.log('Error loading code snippet for ARE help - maybe "help.htm.menu" is missing at ' + jsonData.ARE + '?');
					loadACSAndPluginHelp(jsonData);
				} else if (httpRequest.readyState === XMLHttpRequest.DONE && httpRequest.status === 200) {
					addToMenu(jsonData,httpRequest.responseText,jsonData.ARE);
					loadACSAndPluginHelp(jsonData);
				}
			}
			httpRequest.open('GET', jsonData.ARE + 'help.htm.menu', true);
			httpRequest.send();
		} else {
			loadACSAndPluginHelp(jsonData);
		}
		loadStartPage(jsonData);
	}).fail(function() {alert('Error: Could not read path information - possible causes are:\n- a syntax error in helpPaths.json or\n- unsupported browser: AsTeRICS help is optimised for Mozilla Firefox 57 or higher.');});
	
	return returnObj;
}