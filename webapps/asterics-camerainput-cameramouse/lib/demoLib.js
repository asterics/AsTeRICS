<!-- Here AsTERICS part starts -->
/* This is an example of how to use the ARE Javascipt framework for the communication
	with the ARE Restfull Services.
	
	The location of the server should be defined with the 'setBaseURI(<url>)' method.
	
	A success-callback function and an error-callback function should be passed as an argument
	for every function.
*/

//Set the base URI of the running ARE instance.
setBaseURI("http://localhost:8081/rest/");

/**
 * Loads a file hosted on the same webserver as this file and returns the contents as plain text.
 * @param {string} relFilePath - The path to the file relative to http://<location.origin>/subpath/.
 * @param {function(fileContentsAsString)} successCallback - The callback function to be called with the file contents.
 */
function loadFileFromWebServer(relFilePath, successCallback) {
	var httpReq = new XMLHttpRequest();
	console.log("location.origin: "+location.origin);
	
	var pathFix='';
	if(Object.is(location.origin, 'https://asterics.github.io')) {
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
 * @param {string} relFilePath - The path to the file relative to http://<location.origin>/subpath/.
 * @param {function(data, HTTPstatus)} successCallback - The success callback function.
 */
function deployModelFromWebserver(relFilePath, successCallback) {
	loadFileFromWebServer(relFilePath, 
		function(modelInXML) {				
			uploadModel(successCallback, errorCallback, modelInXML);
		});
}

/**
 * Deploys a model file hosted on the same webserver as this file to a running ARE instance e.g. on localhost.
 * Additionally applies the property settings in the given propertyMap and if successful starts the model.
 * @param {string} relFilePath - The path to the file relative to http://<location.origin>/subpath/.
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
 */
function deployModelFromWebserverApplySettingsAndStartModel(relFilePath, propertyMap) {
	deployModelFromWebserver(relFilePath,
		function() {
			setRuntimeComponentProperties(			
				function (data, HTTPstatus){
					if(JSON.parse(data).length == 0) {
						var errorMsg="The property settings could not be applied.";
						alert(errorMsg);
					}
					console.log('The following properties could be set: '+data);
					
					startModel(Empty_successCallback,errorCallback);
				}, 
				errorCallback, propertyMap);
		});
}

/**
 * Stores the file hosted on the same webserver as this file at a running ARE instance e.g. on localhost and the given relFilePathARE.
 * @param {string} relFilePath - The path of the file relative to http://<location.origin>/subpath/.
 * @param {string} relFilePathARE - The store path of the file on the ARE relative to ARE/data.
 * @param {function(data, HTTPstatus)} successCallback - The success callback function.
 */
function storeFileFromWebserverOnARE(relFilePath, relFilePathARE, successCallback) {
	loadFileFromWebServer(relFilePath, 
		function(fileContentsAsString) {
			storeData(successCallback, errorCallback, relFilePathARE, fileContentsAsString);
		});
}

/* generic callback handler */
function Empty_successCallback(data, HTTPstatus) {}
function errorCallback(HTTPstatus, AREerrorMessage) { alert("An error occured: "+AREerrorMessage+"\nPlease ensure to install AsTeRICS and start the ARE!"); }

