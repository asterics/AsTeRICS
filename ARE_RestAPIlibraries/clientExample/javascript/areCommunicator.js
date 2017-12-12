
//The base URI that ARE runs at
var _baseURI;

//A map holding the opened connection with ARE for SSE
var _eventSourceMap = new Map();

//delimiter used for encoding
var delimiter = "-";

//enumeration for server event types
var ServerEventTypes = {
		MODEL_CHANGED: "model_changed",
		MODEL_STATE_CHANGED: "model_state_changed",
		EVENT_CHANNEL_TRANSMISSION: "event_channel_transmission",
		DATA_CHANNEL_TRANSMISSION: "data_channel_transmission",
		PROPERTY_CHANGED: "property_changed"
};

//Port datatypes
var PortDatatype = {
		UNKNOWN: "unknown",
		BOOLEAN: "boolean",
		BYTE: "byte",
		CHAR: "char",
		INTEGER: "integer",
		DOUBLE: "double",
		STRING: "string"
};

//set the base uri (usually where ARE runs at)
function setBaseURI(uri) {
	_baseURI = uri;
}

//encodes PathParametes
function encodeParam(text) {
	encoded = "";
	for (i=0; i<text.length; i++) {
		encoded += text.charCodeAt(i) + delimiter;
	}
	
	return encoded;
}


//replaces all occurrences of a 'oldString' with 'newString' in 'text'
function replaceAll(text, oldString, newString) {
	return text.split(oldString).join(newString);
}

/**********************
 *	Runtime resources 
 **********************/

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


function autorun(successCallback, errorCallback, filepath) {
	
	if (filepath == "") return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/autorun/" + encodeParam(filepath),
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

function getModelName(successCallback, errorCallback) {
    $.ajax({
        type: "GET",
        url: _baseURI + "runtime/model/name",
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

function deployModelFromFile(successCallback, errorCallback, filepath) {
	
	if ( filepath == "") return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/" + encodeParam(filepath),
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


function getRuntimeComponentIds(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/ids",
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


function getRuntimeComponentPropertyKeys(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+ encodeParam(componentId),
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


function getRuntimeComponentProperty(successCallback, errorCallback, componentId, componentKey) {
	
	if ( (componentId == "") || (componentKey == "")) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+encodeParam(componentId)+"/"+encodeParam(componentKey),
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


function setRuntimeComponentProperties(successCallback, errorCallback, propertyMap) {
	
	if ( propertyMap == "" ) return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/components/properties",
		contentType: "application/json",
		data: propertyMap,
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



function setRuntimeComponentProperty(successCallback, errorCallback, componentId, componentKey, componentValue) {
	
	if ( (componentId == "") || (componentKey == "") || (componentValue == "") ) return;
	
	$.ajax({
		type: "PUT",
		url: _baseURI + "runtime/model/components/"+encodeParam(componentId)+"/"+encodeParam(componentKey),
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


function getEventChannelsIds(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/event/ids",
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


function getEventChannelSource(successCallback, errorCallback, channelId) {
	
	if ( channelId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/event/"+encodeParam(channelId) + "/source",
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


function getEventChannelTarget(successCallback, errorCallback, channelId) {
	
	if ( channelId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/event/"+encodeParam(channelId) + "/target",
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


function getComponentEventChannelsIds(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+encodeParam(componentId) + "/channels/event/ids",
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


function getDataChannelsIds(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/data/ids",
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


function getDataChannelSource(successCallback, errorCallback, channelId) {
	
	if ( channelId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/data/"+encodeParam(channelId) + "/source",
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


function getDataChannelTarget(successCallback, errorCallback, channelId) {
	
	if ( channelId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/channels/data/"+encodeParam(channelId) + "/target",
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


function getComponentDataChannelsIds(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+encodeParam(componentId) + "/channels/data/ids",
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


function getComponentInputPortIds(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/"+encodeParam(componentId) + "/ports/input/ids",
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


function getComponentOutputPortIds(successCallback, errorCallback, componentId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/" + encodeParam(componentId) + "/ports/output/ids",
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(jsonString, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getPortDatatype(successCallback, errorCallback, componentId, portId) {
	
	if ( componentId == "" ) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "runtime/model/components/" + encodeParam(componentId) + "/ports/" + encodeParam(portId) + "/datatype",
		datatype: "text",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonString = jqXHR.responseText;
					successCallback(jsonString, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}

function sendDataToInputPort(successCallback, errorCallback, componentId, portId, value) {
    if ( !componentId || !portId || !value) return;

    $.ajax({
        type: "PUT",
        beforeSend: function(request) {
            request.setRequestHeader("Content-Type", "text/plain");
        },
        url: _baseURI + "runtime/model/components/" + encodeParam(componentId) + "/ports/" + encodeParam(portId) + "/data",
        datatype: "text",
        crossDomain: true,
		data: value,
        success:
            function (data, textStatus, jqXHR){
                jsonString = jqXHR.responseText;
                successCallback(jsonString, textStatus);
            },
        error:
            function (jqXHR, textStatus, errorThrown) {
                errorCallback(errorThrown,jqXHR.responseText);
            }
    });
}

function triggerEvent(successCallback, errorCallback, componentId, eventPortId) {
    if (!componentId || !eventPortId) return;

    $.ajax({
        type: "PUT",
        url: _baseURI + "runtime/model/components/" + encodeParam(componentId) + "/events/" + encodeParam(eventPortId),
        datatype: "text",
        crossDomain: true,
        success:
            function (data, textStatus, jqXHR){
                jsonString = jqXHR.responseText;
                successCallback(jsonString, textStatus);
            },
        error:
            function (jqXHR, textStatus, errorThrown) {
                errorCallback(errorThrown,jqXHR.responseText);
            }
    });
}


/*************************************
 *	Storage/ARE-repository resources
 *************************************/

function downloadModelFromFile(successCallback, errorCallback, filepath) {
	
	if (filepath == "") return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/models/" + encodeParam(filepath),
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


function storeModel(successCallback, errorCallback, filepath, modelInXML) {
	
	if ( (filepath == "") || (modelInXML == "")) return;
	
	$.ajax({
		type: "POST",
		url: _baseURI + "storage/models/" + encodeParam(filepath),
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

function storeData(successCallback, errorCallback, filepath, data) {
	
	if (!filepath || !data) return;
	
	$.ajax({
		type: "POST",
		url: _baseURI + "storage/data/" + encodeParam(filepath),
		contentType: "text/plain",
		data: data,	
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

function storeWebappData(successCallback, errorCallback, webappId, filepath, data) {
	
	if (!webappId || !filepath || !data) return;
	
	$.ajax({
		type: "POST",
		url: _baseURI + "storage/webapps/" + encodeParam(webappId) + "/"  + encodeParam(filepath),
		contentType: "text/plain",
		data: data,	
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

function getWebappData(successCallback, errorCallback, webappId, filepath) {
	
	if (!webappId || !filepath) return;
	
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/webapps/" + encodeParam(webappId) + "/"  + encodeParam(filepath),
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


function deleteModelFromFile(successCallback, errorCallback, filepath) {
	
	if ( filepath == "" ) return;
	
	$.ajax({
		type: "DELETE",
		url: _baseURI + "storage/models/" + encodeParam(filepath),
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


function listStoredModels(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/models/names",
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


function getComponentDescriptorsAsXml(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/components/descriptors/xml",
		datatype: "text/xml",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					successCallback(data, textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


function getComponentDescriptorsAsJSON(successCallback, errorCallback) {
	$.ajax({
		type: "GET",
		url: _baseURI + "storage/components/descriptors/json",
		datatype: "application/json",
		crossDomain: true,
		success:
				function (data, textStatus, jqXHR){
					jsonObject = JSON.parse(jqXHR.responseText);
					successCallback(jsonObject[0], textStatus);
				},
		error: 
				function (jqXHR, textStatus, errorThrown) {
					errorCallback(errorThrown,jqXHR.responseText);
				}
	});
}


/**********************
 *	Other Functions
 **********************/

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




/**********************************
 *	Subscription to SSE events
 **********************************/

function subscribe(successCallback, errorCallback, eventType, channelId) {
	
	// Browser does not support SSE
	if( (typeof EventSource)==="undefined" ) { 
	   alert("SSE not supported by browser");
	   return;
	}

	var eventSource = _eventSourceMap.get(eventType);
	if (eventSource != null) {
		eventSource.close();
	}

	switch (eventType) {
	    case ServerEventTypes.MODEL_CHANGED:
	        resource = "runtime/deployment/listener";
	        break;
	    case ServerEventTypes.MODEL_STATE_CHANGED:
	        resource = "runtime/model/state/listener";
	        break;
	    case ServerEventTypes.EVENT_CHANNEL_TRANSMISSION:
	        resource = "runtime/model/channels/event/listener";
	        break;
	    case ServerEventTypes.DATA_CHANNEL_TRANSMISSION:
	        resource = "runtime/model/channels/data/" + encodeParam(channelId) + "/listener";
	        break;
	    case ServerEventTypes.PROPERTY_CHANGED:
	        resource = "runtime/model/components/properties/listener";
	        break;
        default:
        	console.error("ERROR: Unknown event type given as a parameter '" + eventType + "'");
			return;
	}

	eventSource = new EventSource(_baseURI + resource); // Connecting to SSE service
	_eventSourceMap.add(eventType, eventSource);
	
	//adding listener for specific events
	eventSource.addEventListener("event", function(e) {
		successCallback(e.data, 200);
	}, false);
	
	// After SSE handshake constructed
	eventSource.onopen = function (e) {
		console.log("Waiting message...");
	};

	// Error handler	
	eventSource.onerror = function (e) {
		switch(e.target.readyState) {
			case EventSource.CONNECTING:	
				console.log(400, 'reconnecting');
				break;
			case EventSource.CLOSED:		
				console.log(400, 'connectionLost');
				break;
			default:
				errorCallback(400, 'someErrorOccurred');
				console.log("Error occured");
		}
	};
}


function unsubscribe(eventType, channelId) {
	closeEventSource(eventType);
}


function closeEventSource(eventType) {
	var eventSource = _eventSourceMap.remove(eventType);

	if (eventSource == null) {
		return false;
	}
	else {
		eventSource.close();
		return true;
	}
}

