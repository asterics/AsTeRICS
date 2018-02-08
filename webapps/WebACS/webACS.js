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
 
 ACS.webACS = function() {

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var modelList;
	var clipBoard;
	var view;

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	// global variables
	ACS.openFile = null; // path to model file that will be opened on startup
	ACS.autoConnect = false; // autoConnect to ARE using ACS.areBaseURI
	ACS.autoDownloadModel = false; // automatically download the current model from the auto-connected ARE; will only work if no openFile is specified
	ACS.areBaseURI = null; // specify URI for the ARE (if not specified, but the WebACS is hosted by ARE-webservice, that ARE will be used, else localhost will be assumed)
	

// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	log.setLevel(log.levels.TRACE); // loglevel usage log.trace(msg), log.debug(msg), log.info(msg), log.warn(msg), log.error(msg) (https://github.com/pimterry/loglevel)
	// extract values from query string
	var querystring = window.location.search;
	if (querystring) {
		querystring = querystring.substring(1); // eliminate the '?'
		var inStrings = querystring.split('&');
		for (var i = 0; i < inStrings.length; i++) {
			var actTuple = inStrings[i].split('=');
			switch (actTuple[0]) {
				case 'openFile':			if (actTuple[1] !== '') {
												ACS.openFile = actTuple[1];
											}
											break;
				case 'autoConnect':			if (actTuple[1] === 'true') {
												ACS.autoConnect = true;
											} else {
												ACS.autoConnect = false;
											}
											break;
				case 'autoDownloadModel':	if (actTuple[1] === 'true') {
												ACS.autoDownloadModel = true;
											} else {
												ACS.autoDownloadModel = false;
											}
											break;
				case 'areBaseURI':			if (actTuple[1] !== '') {
												ACS.areBaseURI = actTuple[1];
											}
											break;
			}
		}
	}
	// set areBaseURI, if not yet set
	if (!ACS.areBaseURI) {
		if (window.location.port === '8081') { // assuming that the WebACS is hosted by ARE-webservice
			ACS.areBaseURI = window.location.origin;
		} else {
			ACS.areBaseURI = 'http://localhost:8081';
		}
	}
	log.debug('openFile: ' + ACS.openFile + '\nautoConnect: ' + ACS.autoConnect + '\nautoDownloadModel: ' + ACS.autoDownloadModel + '\nareBaseURI: ' + ACS.areBaseURI);
	
	// setup the WebACS
	modelList = ACS.modelList();
	clipBoard = ACS.clipBoard();
	view = ACS.view(modelList, clipBoard);	
	ACS.areStatus.setModelList(modelList);
	
	return returnObj;
}