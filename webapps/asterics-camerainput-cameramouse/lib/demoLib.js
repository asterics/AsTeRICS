<!-- Here AsTERICS part starts -->
/* This is an example of how to use the ARE Javascipt framework for the communication
	with the ARE Restfull Services.
	
	The location of the server should be defined with the 'setBaseURI(<url>)' method.
	
	A success-callback function and an error-callback function should be passed as an argument
	for every function.
*/

setBaseURI("http://localhost:8081/rest/");


//helper functions for loading xml model file
function loadModelFileFromWebServer(relFilePath, successCallback) {
	var httpReq = new XMLHttpRequest();
	console.log("location.origin: "+location.origin);
	
	var pathFix='';
	if(Object.is(location.origin, 'https://asterics.github.io')) {
		pathFix='/AsTeRICS';
		console.log("Adding pathFix: "+pathFix);
	}
	relFilePath=location.origin+pathFix+'/'+relFilePath;
	console.log('Fetching model file from webserver: '+relFilePath);
	httpReq.onreadystatechange = function() {
		if (httpReq.readyState === XMLHttpRequest.DONE && (httpReq.status === 404 || httpReq.status === 0)) {						
			alert('Could not find requested model file: '+relFilePath);
			
		} else if (httpReq.readyState === XMLHttpRequest.DONE && httpReq.status === 200) {
			//success, so call success-callback
			console.log('Modelfile from Webserver successfully loaded: '+relFilePath);
			successCallback(httpReq.responseText);
		}
	}	
	httpReq.open('GET', relFilePath, true);
	httpReq.send();
}

function loadModelFileFromWebServerAndUploadModel(relFilePath, successCallback) {
	loadModelFileFromWebServer(relFilePath, 
		function(modelInXML) {				
			uploadModel(successCallback, errorCallback, modelInXML);
		});
}

function loadModelFileFromWebServerAndUploadModelAndStartModel(relFilePath) {	
	loadModelFileFromWebServerAndUploadModel(relFilePath,
		function() {
			startModel(EMPTY_successCallback, errorCallback);
		});
}

/* generic callback handler */
function Empty_successCallback(data, HTTPstatus) {}
function errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }

