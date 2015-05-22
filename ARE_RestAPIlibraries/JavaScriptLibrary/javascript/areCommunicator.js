

//Base URI that ARE runs at
var _baseURI;

//A map holding the opened connection with ARE for SSE
var _eventSourceMap = new Map();

//enumeration for server event types
var ServerEvents = {
		  MODEL_STATE_CHANGED: "ModelStateChanged",
		  MODEL_CHANGED: "ModelChanged",
		  REPOSITORY_CHANGED: "RepositoryChanged"
};

function setBaseURI(uri) {
	_baseURI = uri;
}

function downloadDeployedModel(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model",
		datatype: "text/xml",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function downloadModelFromFile(successCallback, errorCallback, filename) {
	
	if (filename == "") return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/models/" + filename,
		datatype: "text/xml",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function uploadModel(successCallback, errorCallback, modelInXML) {
	
	if (modelInXML == "") return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model",
		contentType: "text/xml",									//content-type of the request
		data: modelInXML,	
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function autorun(successCallback, errorCallback, filename) {
	
	if (filename == "") return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/autorun/" + filename,
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function pauseModel(successCallback, errorCallback) {
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/state/pause",
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function startModel(successCallback, errorCallback) {
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/state/start",
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function stopModel(successCallback, errorCallback) {
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/state/stop",
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function storeModel(successCallback, errorCallback, filename, modelInXML) {
	
	if ( (filename == "") || (modelInXML == "")) return;
	
	$.ajax({
		type: "POST",
		url: _baseURI + "storage/models/" + filename,
		contentType: "text/xml",									//content-type of the request
		data: modelInXML,	
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getModelState(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/state",	
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function deployModelFromFile(successCallback, errorCallback, filename) {
	
	if ( filename == "") return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/" + filename,
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function deleteModelFromFile(successCallback, errorCallback, filename) {
	
	if ( filename == "" ) return;
	
	$.ajax({
		type: "DELETE",
		url: _baseURI + "storage/models/" + filename,
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function downloadComponentCollection(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components",
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(JSON.parse(jsonString), textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function listStoredModels(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/models",
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(JSON.parse(jsonString), textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getComponentPropertyKeys(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+componentId,
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(JSON.parse(jsonString), textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getComponentProperty(successCallback, errorCallback, componentId, componentKey) {
	
	if ( (componentId == "") || (componentKey == "")) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+componentId+"/"+componentKey,
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function setComponentProperty(successCallback, errorCallback, componentId, componentKey, componentValue) {
	
	if ( (componentId == "") || (componentKey == "") || (componentValue == "") ) return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/components/"+componentId+"/"+componentKey,
		contentType: "text/plain",
		data: componentValue,
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(jqXHR.responseText, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getRestFunctions(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "restfunctions",
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(JSON.parse(jsonString), textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function subscribe(successCallback, errorCallback, eventsType) {
	
	// Browser does not support SSE
	if((typeof EventSource)==="undefined") { 
	   alert("SSE not supported by browser");
	   return;
	}

	var eventSource = _eventSourceMap.get(eventsType);
	if (eventSource != null) {
		eventSource.close();
	}
	eventSource = new EventSource(_baseURI + "events/subscribe"); // Connecting to SSE service
	_eventSourceMap.add(eventsType, eventSource);

	//adding listener for specific events
	eventSource.addEventListener(eventsType, function(e) {
		successCallback(e.data, 200);
	}, false);
	
	// After SSE handshake constructed
	eventSource.onopen = function (e) {
		console.log("Waiting message...");
	};

	// Error handler	
	eventSource.onerror = function (e) {
		errorCallback(400, e.data);
		console.log("Error occured");
	};
}


function unsubscribe(eventsType) {
	var eventSource = _eventSourceMap.remove(eventsType);
	
	if (eventSource == null) {
		return false;
	}
	else {
		eventSource.close();
		return true;
	}
}

