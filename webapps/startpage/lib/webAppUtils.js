/**
 * @file
 * This Javascript library provides functions that simplify the creation of web-based user interfaces interacting with an AsTeRICS model.
 * The lib provides functions for downloading files from a webserver, widget <-> model synchronization, up/download/start models, setting/getting properties of deployed model.
 *   
 * @requires jquery-3.2.1.min.js (or maybe lower versions also)
 * @requires JSmap.js (AsTeRICS 3.0)
 * @requires areCommunicator.js (AsTeRICS 3.0)
 * 
 * @author Martin Deinhofer
 * @version 0.1
 */

/**
 * Loads a file hosted on the same webserver as this file and returns the contents as plain text.
 * @param {string} relFilePath - The path to the file relative to http://<location.origin>/subpath/.
 * @param {function(fileContentsAsString)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error.
 */
function loadFileFromWebServer(relFilePath, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);	
	
	var httpReq = new XMLHttpRequest();
	console.log("location.origin: "+location.origin);
	
	var pathFix='';
	if(Object.is(location.origin, 'https://asterics.github.io')||Object.is(location.origin, 'http://asterics.github.io')) {
		pathFix='/AsTeRICS';
		console.log("Adding pathFix: "+pathFix);
	}
	relFilePath=location.origin+pathFix+'/'+relFilePath;
	console.log('Fetching file from webserver: '+relFilePath);
	
	/*
	//With jQuery you could use something like this to fetch the file easily, nevertheless to be independent we use the hard approach.
	$.get(relFilePath).then(function(response) {
		successCallback(response);
	});*/

	httpReq.onreadystatechange = function() {
		if (httpReq.readyState === XMLHttpRequest.DONE && (httpReq.status === 404 || httpReq.status === 0)) {						
			alert('Could not find requested file: '+relFilePath);
			
		} else if (httpReq.readyState === XMLHttpRequest.DONE && httpReq.status === 200) {
			//success, so call success-callback
			console.log('File from Webserver successfully loaded: '+relFilePath);
			successCallback(httpReq.responseText);
		}
	}	
	httpReq.open('GET', relFilePath, true);
	httpReq.send();
}

/**
 * Deploys a model file hosted on the same webserver as this file to a running ARE instance e.g. on localhost.
 * @param {string} relFilePath - The path to the file relative to http://location.origin/subpath/.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error. 
 */
function deployModelFromWebserver(relFilePath, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);	
	
	loadFileFromWebServer(relFilePath, 
		function(modelInXML) {				
			uploadModel(successCallback, errorCallback, modelInXML);
		});
}

/**
 * Deploys a model file hosted on the same webserver as this file to a running ARE instance e.g. on localhost.
 * Additionally applies the property settings in the given propertyMap and if successful starts the model.
 * @param {string} relFilePath - The path to the file relative to http://location.origin/subpath/.
 * @param {string } propertyMap - A JSON string of property keys and values (see function setRuntimeComponentProperties) in the format: 
 
 {
   "Component_id_1":{
      "key_1_1":"val_1_1",
      "key_1_2":"val_1_2"
   },
   "Component_id_2":{
      "key_2_1":"val_2_1",
      "key_2_2":"val_2_2"
   }
}
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error. 
 */
function deployModelFromWebserverApplySettingsAndStartModel(relFilePath, propertyMap, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);	
	
	deployModelFromWebserver(relFilePath,
		function() {
			setRuntimeComponentProperties(			
				function (data, HTTPstatus){
					if(JSON.parse(data).length == 0) {
						var errorMsg="The property settings could not be applied.";
						alert(errorMsg);
					}
					console.log('The following properties could be set: '+data);
					
					startModel(successCallback,errorCallback);
				}, 
				errorCallback, propertyMap);
		});
}

/**
 * Stores the file hosted on the same webserver as this file at a running ARE instance e.g. on localhost and the given relFilePathARE.
 * @param {string} relFilePath - The path of the file relative to http://location.origin/subpath/.
 * @param {string} relFilePathARE - The store path of the file on the ARE relative to ARE/data.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error. 
 */
function storeFileFromWebserverOnARE(relFilePath, relFilePathARE, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);	
	
	loadFileFromWebServer(relFilePath, 
		function(fileContentsAsString) {
			storeData(successCallback, errorCallback, relFilePathARE, fileContentsAsString);
		});
}

/**
 * Download the model file (modelFilePathOnWebserver) hosted on a web server, apply all settings to the XML model, 
 * which have a defined binding (data-asterics-model-binding-1,...) to a model property and finally start the model.
 * @param {string} modelFilePathOnWebserver - The path of the file relative to http://location.origin/subpath/.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error.   
*/		
function applySettingsInXMLModelAndStart(modelFilePathOnWebserver, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);	
	
	loadFileFromWebServer(modelFilePathOnWebserver, function(modelInXML){
		modelInXML=updateModelPropertiesFromWidgets(modelInXML);
		
		//Finally upload and start modified model.
		uploadModel(function(data, HTTPstatus) {					
			startModel(function(data,HTTPStatus) {
				successCallback(data, HTTPStatus);
			},errorCallback);					
		}, errorCallback, modelInXML);
	});	
}

/**
 * Download the currently deployed model from the ARE and update all widgets with the property values in the XML model, 
 * which have a defined binding (data-asterics-model-binding-1,...) to a model property.
 * @param {string} modelFilePathOnWebserver - The path of the file relative to http://location.origin/subpath/.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error.   
*/		
function updateWidgetsFromDeployedModel(successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	successCallback=getSuccessCallback(successCallback);
	errorCallback=getErrorCallback(errorCallback);

	downloadDeployedModel(function(data, HTTPStatus) {
		//TODO: Check if the modelName is identical to the modelName of the template model, otherwise we should not
		//update the widgets from a wrong model.
		updateWidgetsFromModelProperties(data);
	},successCallback);
}

/**
 * Applies the settings to the model modelFilePathOnWebserver and saves it as autostart model on the ARE.
 * @param {string} modelFilePathOnWebserver - The path of the file relative to http://location.origin/subpath/.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be called with the file contents. 
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- Callback function in case of an error.   
*/		
function saveSettingsAsAutostartModel(modelFilePathOnWebserver, successCallback, errorCallback) {
	//assign default callback functions if none was provided.
	if(typeof successCallback !== 'function') {
		successCallback=function(data,HTTPStatus) {
			alert('Successfully saved settings as autostart!');
		}
	}
	errorCallback=getErrorCallback(errorCallback);
	
	loadFileFromWebServer(modelFilePathOnWebserver, function(modelInXML){
		modelInXML=updateModelPropertiesFromWidgets(modelInXML);
		storeModel(successCallback,errorCallback,'autostart.acs',modelInXML);
	});			
}

/** 
 * @const {string}
 * @description HTML5 data attribute to define binding to AsTeRICS model 
 */
var dataAttrAstericsModelBinding="data-asterics-model-binding-1";

/**
 * Updates the property values of the given modelInXML string with the currently set widget values.
 * @param {string} modelInXML - The AsTeRICS model as XML string.
 * @returns {string} The modified XML model as string.
 */
function updateModelPropertiesFromWidgets(modelInXML) {
	widgetIdToPropertyKeyMap=generateWidgetIdToPropertyKeyMap();
	console.log("Updating "+widgetIdToPropertyKeyMap.length+" widgets from model properties.");
	
	//parse template modelInXML to document object
	var xmlDoc = $.parseXML( modelInXML );
	
	//Update property values with values of input widgets.
	for(var i=0;i<widgetIdToPropertyKeyMap.length;i++) {
		var widgetVal=$(widgetIdToPropertyKeyMap[i]["widgetId"]).val();
		if (typeof widgetVal != 'undefined') {
			console.log("Updating modelProperty <"+widgetIdToPropertyKeyMap[i]["componentKey"]+"."+widgetIdToPropertyKeyMap[i]["propertyKey"]+"="+widgetVal+">");
			setPropertyValueInXMLModel(widgetIdToPropertyKeyMap[i]["componentKey"],widgetIdToPropertyKeyMap[i]["propertyKey"],widgetVal,xmlDoc);
		} else {
			console.log("widgetId <"+widgetIdToPropertyKeyMap[i]["widgetId"]+"=undefined>");
		}
	}
	
	//Convert back XML document to XML string.
	modelInXML=xmlToString(xmlDoc);
	return modelInXML;
}

/**
 * Updates the widgets (HTML input elements) with the property values of the given modelInXML string.
 * @param {string} modelInXML - The AsTeRICS model as XML string.
 */
function updateWidgetsFromModelProperties(modelInXML) {
	widgetIdToPropertyKeyMap=generateWidgetIdToPropertyKeyMap();
	console.log("Updating "+widgetIdToPropertyKeyMap.length+" widgets from model properties.");
	
	//parse template modelInXML to document object
	var xmlDoc = $.parseXML( modelInXML );
	
	//Update property values with values of input widgets.
	for(var i=0;i<widgetIdToPropertyKeyMap.length;i++) {
		var propVal=getPropertyValueFromXMLModel(widgetIdToPropertyKeyMap[i]["componentKey"],widgetIdToPropertyKeyMap[i]["propertyKey"],xmlDoc);
		if(typeof propVal != 'undefined') {
			console.log("Updating widget <"+widgetIdToPropertyKeyMap[i]["widgetId"]+"="+propVal+">");
			$(widgetIdToPropertyKeyMap[i]["widgetId"]).val(propVal);					
		}
	}	
}

/**
 * Generates an array describing the bindings between all widgetIds (id of HTML5 input tag) and their respective model properties.
 * @returns {Array} - Array with Javascript object elements. 
 */
function generateWidgetIdToPropertyKeyMap() {
	var widgetIdToPropertyKeyMap=[];
	var widgetList=$("["+dataAttrAstericsModelBinding+"]");
	for(var i=0;i<widgetList.length;i++) {
		var bindings=$(widgetList[i]).data();
		for(binding in bindings) {
			var bindingObj=	{
				widgetId:"#"+$(widgetList[i]).attr('id'),
				componentKey:bindings[binding]["componentKey"],
				propertyKey:bindings[binding]["propertyKey"]
			}
			widgetIdToPropertyKeyMap.push(bindingObj);
		}
	}
	return widgetIdToPropertyKeyMap;
}

/**
 * Returns a valid callback function - either successCallback if != undefined or {defaultSuccessCallback}.
 * @param {function(data, HTTPstatus)} [successCallback=defaultSuccessCallback] - The callback function to be used.
 * @returns {function(data, HTTPstatus)} - Either successCallback or defaultSuccessCallback.
*/		
function getSuccessCallback(successCallback) {
	if(typeof successCallback !== 'function') {
		return defaultSuccessCallback;
	}
	return successCallback;	
}

/**
 * Returns a valid callback function - either errorCallback if != undefined or {defaultErrorCallback}.
 * @param {function(HTTPstatus, errorMessage)} [errorCallback=defaultErrorCallback]- The callback function to be used.
 * @returns {function(HTTPstatus, errorMessage)} - Either errorCallback or defaultErrorCallback.
*/		
function getErrorCallback(errorCallback) {
	if(typeof errorCallback !== 'function') {
		return defaultErrorCallback;
	}
	return errorCallback;
}


/* generic callback handler */
/**
 * This is the default success callback. 
 * By default nothing is done.
 *
 * @callback defaultSuccessCallback
 * @param {data} - response text or message.
 * @param {HTTPstatus} - HTTP status code if applicable.
 */
function defaultSuccessCallback(data, HTTPstatus) {}
/**
 * This is the default error callback. 
 * By default an error dialog (alert) is opened.
 *
 * @callback defaultErrorCallback
 * @param {HTTPstatus} - HTTP status code if applicable.
 * @param {errorMessage} - The error message to be shown.

 */
function defaultErrorCallback(HTTPstatus, errorMessage) { alert("An error occured: "+errorMessage+"\nPlease ensure to install AsTeRICS and start the ARE!"); }

