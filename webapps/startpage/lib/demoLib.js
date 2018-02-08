<!-- Here the live chart scripting starts -->
			
	window.requestAnimFrame = (function(callback) {
		return window.requestAnimationFrame
				|| window.webkitRequestAnimationFrame
				|| window.mozRequestAnimationFrame
				|| window.oRequestAnimationFrame
				|| window.msRequestAnimationFrame ||

				function(callback) {
					window.setTimeout(callback, 1000 / 30); // 30 frames per second
				};
	})();

	var toggleVal1 = 0;
	var toggleVal2 = 0;
	var toggleVal3 = 0;
	var toggleVal4 = 0;
	var toggleVal5 = 0;
	var toggleVal6 = 0;
	var toggleVal7 = 0;
	var toggleVal8 = 0;

	var tempData = 0;
	var txData = '';

	var analogData1 = 0;
	var analogData2 = 0;
	var analogData3 = 0;
	var analogData4 = 0;
	var analogData5 = 0;
	var analogData6 = 0;
	var analogData7 = 0;
	var analogData8 = 0;

	var accDataX = 0;
	var accDataY = 0;
	var accDataZ = 0;

	var data1 = 0;
	var data2 = 0;
	var data3 = 0;
	
	var dataPacketCounter=0;

	var wsUri = "ws://localhost:8082/ws/astericsData";
	var output;
	function init() {
		output = document.getElementById("output");
		//testWebSocket();
	}

	function initSocket() {
		init();
		websocket = new WebSocket(wsUri);
		websocket.onopen = function(evt) {
			onOpen(evt)
		};
		websocket.onclose = function(evt) {
			onClose(evt)
		};
		websocket.onmessage = function(evt) {
			onMessage(evt)
		};
		websocket.onerror = function(evt) {
			onError(evt)
		};

	}
	function testWebSocket() {
		websocket = new WebSocket(wsUri);
		websocket.onopen = function(evt) {
			onOpen(evt)
		};
		websocket.onclose = function(evt) {
			onClose(evt)
		};
		websocket.onmessage = function(evt) {
			onMessage(evt)
		};
		websocket.onerror = function(evt) {
			onError(evt)
		};
	}
	function onOpen(evt) {
		writeToScreen("CONNECTED");
		doSend("WebSocket rocks");
	}
	function onClose(evt) {
		writeToScreen("DISCONNECTED");
	}
	function onMessage(evt) {

		accDataX = document.getElementById("AccX");
		accDataX.value = evt.data;

		var dataNum=Number(evt.data);
		/*
		console.log(dataPacketCounter);
		console.log(dataNum);
		
		if(dataPacketCounter != dataNum) {
			console.log("packetCounter differs");
		}
		*/
		dataPacketCounter = dataPacketCounter+1;
		window.data1 = dataNum; //AccX graphData
	}
	function onError(evt) {
		writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
	}
	function doSend(message) {
		<!-- writeToScreen("SENT: " + message);-->
		websocket.send(message);
	}
	function writeToScreen(message) {
		var pre = document.createElement("p");
		pre.style.wordWrap = "break-word";
		pre.innerHTML = message;
		output.appendChild(pre);
	}

	function initGraph1() //Initialize Graph 1
	{
		var smoothie1 = new SmoothieChart();
		var plot1 = new TimeSeries();

		smoothie1.streamTo(document.getElementById("myGraph1"));
		smoothie1.addTimeSeries(plot1, {
			strokeStyle : 'rgb(0, 255, 0)',
			lineWidth : 2
		});

		setInterval(function() {
			plot1.append(new Date().getTime(), data1);
		}, 100);
	}

	window.onload = function() {
		initSocket();
		initGraph1();
	};
	
	function connectToWebsocket()
	{
		initSocket();
		initGraph1();
	}
			
			
			
			
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
				relFilePath=location.origin+'/'+relFilePath;
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
						uploadModel(successCallback, UM_errorCallback, modelInXML);
					});
			}
			
			function loadModelFileFromWebServerAndUploadModelAndStartModel(relFilePath) {	
				loadModelFileFromWebServerAndUploadModel(relFilePath,
					function() {
						START();
					});
			}
		
			//downloadDeployedModel
			function DDM() {
				downloadDeployedModel(DDM_successCallback, DDM_errorCallback);
			}
			
			function DDM_successCallback(data, HTTPstatus) { alert(data); }
			function DDM_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//downloadModelFromFile
			function DMF() {
				var filename = document.getElementById("DMFfilename").value;
				downloadModelFromFile(DMF_successCallback, DMF_errorCallback, filename);
			}
			
			function DMF_successCallback(data, HTTPstatus) { alert(data); }
			function DMF_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//uploadModel
			function UM() {
				var modelInXML = document.getElementById("UMmodel").value;
				uploadModel(UM_successCallback, UM_errorCallback, modelInXML);
			}
						
			function UM_successCallback(data, HTTPstatus) { alert(data); }
			function UM_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//autorun
			function AUTO() {
				var filename = document.getElementById("AUTOfilename").value;
				autorun(AUTO_successCallback, AUTO_errorCallback, filename);
			}
			
			function AUTO_successCallback(data, HTTPstatus) { alert(data); }
			function AUTO_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//getModelState
			function MS() {
				getModelState(MS_successCallback, MS_errorCallback);
			}
			
			function MS_successCallback(data, HTTPstatus) { alert(data); }
			function MS_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//pauseModel
			function PAUSE() {
				pauseModel(PAUSE_successCallback, PAUSE_errorCallback);
			}
						
			function PAUSE_successCallback(data, HTTPstatus) { alert(data); }
			function PAUSE_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
						
			
			//startModel
			function START() {
				startModel(START_successCallback, START_errorCallback);
			}
						
			function START_successCallback(data, HTTPstatus) { alert(data); }
			function START_errorCallback(HTTPstatus, AREerrorMessage) { alert('Model could not be started.\n'+AREerrorMessage); }
						
			
			//stopModel
			function STOP() { 
				stopModel(STOP_successCallback, STOP_errorCallback); 
			}
						
			function STOP_successCallback(data, HTTPstatus) { alert(data); }
			function STOP_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }


			//storeModel
			function STORE() {
				var filename = document.getElementById("STOREfilename").value;
				var modelInXML = document.getElementById("STOREmodel").value;
				storeModel(STORE_successCallback, STORE_errorCallback, filename, modelInXML);
			}
									
			function STORE_successCallback(data, HTTPstatus) { alert(data); }
			function STORE_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//deployModelFromFile
			function DepMF() {
				var filename = document.getElementById("DepMFfilename").value;
				deployModelFromFile(DepMF_successCallback, DepMF_errorCallback, filename);
			}
			
			//deployModelFromFile
			function DepMFAndStart(filename) {
				//var filename = document.getElementById("DepMFfilename").value;
				deployModelFromFile(DepMF_StartModel_Callback, DepMF_errorCallback, filename);
				//stopModel(DepMF_Empty_successCallback, STOP_errorCallback); 
				
			}
			
			function DepMF_StartModel_Callback(data, HTTPstatus) { startModel(DepMF_Empty_successCallback, START_errorCallback); }
			
			function DepMFAndStartXFaceTrackerLK(filename) {
				deployModelFromFile(SelectCamera_Callback, DepMF_errorCallback, filename);
			}
			function SelectCamera_Callback(data, HTTPstatus) {
				var cameraIdx = document.getElementById("cameraIdx").value;
				setRuntimeComponentProperty(DepMF_StartModel_Callback, SCP_errorCallback, 'XFacetrackerLK.1', 'cameraSelection', cameraIdx);
			}
			
			function DepMF_Empty_successCallback(data, HTTPstatus) { }
			function DepMF_successCallback(data, HTTPstatus) { alert(data); }
			function DepMF_errorCallback(HTTPstatus, AREerrorMessage) { alert('Model could not be deployed\nHave you installed and started the asterics-prosperity4all-bb-demos package?\n'+AREerrorMessage); }
			

			//deleteModelFromFile
			function DELETE() {
				var filename = document.getElementById("DELETEfilename").value;
				deleteModelFromFile(DELETE_successCallback, DELETE_errorCallback, filename);
			}
									
			function DELETE_successCallback(data, HTTPstatus) { alert(data); }
			function DELETE_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//downloadComponentCollection
			function DCC() {
				downloadComponentCollection(DCC_successCallback, DCC_errorCallback);
			}
						
			function DCC_successCallback(data, HTTPstatus) { alert(data); }
			function DCC_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			

			//listStoredModels
			function LSM() {
				listStoredModels(LSM_successCallback, LSM_errorCallback);
			}
						
			function LSM_successCallback(data, HTTPstatus) { alert(data); }
			function LSM_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }

			
			//getInstalledComponents
			function INSTALLED_COMPONENTS() {
				getInstalledComponents(INSTALLED_COMPONENTS_successCallback, INSTALLED_COMPONENTS_errorCallback);
			}
									
			function INSTALLED_COMPONENTS_successCallback(data, HTTPstatus) {
				console.log(JSON.stringify(data[0]));
			}
			function INSTALLED_COMPONENTS_errorCallback(HTTPstatus, AREerrorMessage) {
				alert(AREerrorMessage);
			}
			
			
			//getInstalledComponentsDescriptor
			function INSTALLED_COMPONENTS_DSCR() {
				getInstalledComponentsDescriptor(INSTALLED_COMPONENTS_DSCR_successCallback, INSTALLED_COMPONENTS_DSCR_errorCallback);
			}
									
			function INSTALLED_COMPONENTS_DSCR_successCallback(data, HTTPstatus) {
				console.log(data);
			}
			function INSTALLED_COMPONENTS_DSCR_errorCallback(HTTPstatus, AREerrorMessage) {
				alert(AREerrorMessage);
			}
			
			
			//getCreatedComponentsDescriptor
			function CREATED_COMPONENTS_DSCR() {
				getCreatedComponentsDescriptor(CREATED_COMPONENTS_DSCR_successCallback, CREATED_COMPONENTS_DSCR_errorCallback);
			}
									
			function CREATED_COMPONENTS_DSCR_successCallback(data, HTTPstatus) {
				console.log(data);
			}
			function CREATED_COMPONENTS_DSCR_errorCallback(HTTPstatus, AREerrorMessage) {
				console.log(AREerrorMessage);
			}
	
			
			//getComponentPropertyKeys
			function GCPK() {
				var componentId = document.getElementById("GCPKid").value;
				getComponentPropertyKeys(GCPK_successCallback, GCPK_errorCallback, componentId);
			}
						
			function GCPK_successCallback(data, HTTPstatus) { alert(data[0]); }
			function GCPK_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			

			//getComponentProperty
			function GCP() {
				var componentId = document.getElementById("GCPid").value;
				var componentKey = document.getElementById("GCPkey").value;
				getComponentProperty(GCP_successCallback, GCP_errorCallback, componentId, componentKey);
			}
						
			function GCP_successCallback(data, HTTPstatus) { alert(data); }
			function GCP_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
			
			
			//setComponentProperty
			function SCP() {
				var componentId = document.getElementById("SCPid").value;
				var componentKey = document.getElementById("SCPkey").value;
				var componentValue = document.getElementById("SCPvalue").value;
				setComponentProperty(SCP_successCallback, SCP_errorCallback, componentId, componentKey, componentValue);
			}
						
			function SCP_successCallback(data, HTTPstatus) { alert(data); }
			function SCP_errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
				
			
			//getRestFunctions
			function FUNCTIONS() {
				getRestFunctions(FUNCTIONS_successCallback, FUNCTIONS_errorCallback);
			}
						
			function FUNCTIONS_successCallback(data, HTTPstatus) {
				console.log(JSON.stringify(data[0]));
			}
			function FUNCTIONS_errorCallback(HTTPstatus, AREerrorMessage) {
				alert(AREerrorMessage);
			}
							
			
			//subscribe to SSE
			function SUBSCRIBE_EVENTS() {
				//var eventType = document.getElementById("subid").value;
				var selects = document.getElementById("eTypesS");
				var eventType = selects.options[selects.selectedIndex].value;
				subscribe(SUBSCIBE_EVENTS_successCallback, SUBSCIBE_EVENTS_errorCallback, eventType);
			}
					
			function SUBSCIBE_EVENTS_successCallback(data, HTTPstatus) {
				var eventsBox = document.getElementById("eventsBox");
				eventsBox.innerHTML = "SSE event: " + data; 
				console.log("SSE event: " + data);
			}
			
			function SUBSCIBE_EVENTS_errorCallback(HTTPstatus, AREerrorMessage) {
				var eventsBox = document.getElementById("eventsBox");
				eventsBox.innerHTML = data;
				console.log(AREerrorMessage);
			}
			
			
			//unsubscribe from SSE
			function UNSUBSCRIBE_EVENTS() {
				//var eventType = document.getElementById("unsubid").value;
				var selects = document.getElementById("eTypesS");
				var eventType = selects.options[selects.selectedIndex].value;
				unsubscribe(eventType);
			}
			
			/*
			Camera mouse parametrization
			*/
			
			function applySettingsToModel() {
				var cameraSource = document.getElementById("cameraSource");
				var cameraSourceIdx = cameraSource.options[cameraSource.selectedIndex].value;
				setRuntimeComponentProperty(apply1_successCallback, errorCallback, 'XFacetrackerLK.1', 'cameraSelection', cameraSourceIdx);
			}
			
			function apply1_successCallback(data, HTTPstatus) {
				var speed = document.getElementById("speed").value;
				setRuntimeComponentProperty(apply4_successCallback, errorCallback, 'Slider.1', 'default', speed);				
			}

			function apply4_successCallback(data, HTTPstatus) {
				//left click: don't know how to set it by now
			
			
				//Finally start model! - Yippie!!
				startModel(Empty_successCallback, START_errorCallback);
			}			

			//-------------------------------------
			function applyAndStart() {
				deployModelFromFile(DepMF_XFacetrackerLK_successCallback, DepMF_errorCallback, 'CameraInput/XFacetrackerLK/XFaceTrackerMouse-no-osk(WLM).acs');
			}
			
			function DepMF_XFacetrackerLK_successCallback(data, HTTPstatus) {
				applySettingsToModel();
			}

			function Empty_successCallback(data, HTTPstatus) {}
			function errorCallback(HTTPstatus, AREerrorMessage) { alert(AREerrorMessage); }
