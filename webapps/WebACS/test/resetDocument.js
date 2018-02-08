var resetDocument = function() {
	document.getElementById('menuPanel').innerHTML = ''+
			'<h1 class="menu_header">AsTeRICS Configuration Suite</h1>'+
			'<div id="mainMenuPanel" class="tabpanel" role="menu">'+

			  '<ul id="mainMenuTablist" class="tablist" role="tablist">'+
				'<li id="tab1" class="tab menuTab" aria-controls="panel1" aria-selected="true" role="tab" tabindex="0">System</li>'+
				'<li id="tab2" class="tab menuTab" aria-controls="panel2" role="tab" aria-selected="false" tabindex="-1">Components</li>'+
				'<li id="tab3" class="tab menuTab" aria-controls="panel3" role="tab" aria-selected="false" tabindex="-1">Edit</li>'+
				'<li id="tab4" class="tab menuTab" aria-controls="panel4" role="tab" aria-selected="false" tabindex="-1">Misc.</li>'+
			  '</ul>'+

			  '<div id="panel1" class="panel menuPanel" aria-labelledby="tab1" role="tabpanel">'+
				'<section class="buttonGroup">'+
					'<h2>ARE</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="connectAREBtn"><div class="buttonContents"><img src="view/images/connect.png" alt="Connect to ARE" />Connect<br />to ARE</div></button></li>'+
						'<li><button type="button" class="menuButton" id="disconnectAREBtn"><div class="buttonContents"><img src="view/images/disconnect.png" alt="Disconnect from ARE" />Disconnect<br />from ARE</div></button></li>'+
						'<li><button type="button" class="menuButton" id="uploadModelBtn"><div class="buttonContents"><img src="view/images/Upload.png" alt="Upload model" />Upload<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="downloadModelBtn"><div class="buttonContents"><img src="view/images/Download.png" alt="Download model" />Download<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="downloadCompCollBtn"><div class="buttonContents"><img src="view/images/DownloadBundle.png" alt="Download component collection" />Download<br />Component Collection</div></button></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup">'+
					'<h2>ARE Storage</h2>'+		
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="storeModelAREBtn"><div class="buttonContents"><img src="view/images/storeup.png" alt="Store model on ARE" />Store<br />Model on ARE</div></button></li>'+
						'<li><button type="button" class="menuButton" id="loadModelAREBtn"><div class="buttonContents"><img src="view/images/storedown.png" alt="Load model from storage" />Load Model<br />from Storage</div></button></li>'+
						'<li><button type="button" class="menuButton" id="activateStoredModelBtn"><div class="buttonContents"><img src="view/images/storerun.png" alt="Activate a stored model" />Activate a<br />Stored Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="deleteStoredModelBtn"><div class="buttonContents"><img src="view/images/storedelete.png" alt="Delete a stored model" />Delete a<br />Stored Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="setAsAutorunBtn"><div class="buttonContents"><img src="view/images/autorun.png" alt="Set as autorun" />Set as<br />Autorun</div></button></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup">'+
					'<h2>Model</h2>'+	
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="startModelBtn"><div class="buttonContents"><img src="view/images/StartModel.png" alt="Start model" />Start<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="pauseModelBtn"><div class="buttonContents"><img src="view/images/PauseModel.png" alt="Pause model" />Pause<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="stopModelBtn"><div class="buttonContents"><img src="view/images/StopModel.png" alt="Stop model" />Stop<br />Model</div></button></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup bg_right">'+
					'<h2>Local</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="newModelBtn"><div class="buttonContents"><img src="view/images/new.png" alt="New model" />New<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="openModelBtn"><div class="buttonContents"><img src="view/images/open.png" alt="Open model" />Open<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="closeModelBtn"><div class="buttonContents"><img src="view/images/close.png" alt="Close model" />Close<br />Model</div></button></li>'+
						'<li><button type="button" class="menuButton" id="saveModelBtn"><div class="buttonContents"><img src="view/images/save.png" alt="Save model" />Save<br />Model</div></button></li>'+
					'</ul>'+
				'</section>'+
			  '</div>'+

			  '<div id="panel2" class="panel menuPanel" aria-labelledby="tab2" role="tabpanel">'+
				'<section class="buttonGroup">'+
					'<h2>Components</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="sensorsBtn"><div class="buttonContents"><img src="view/images/sensor.png" alt="Sensors" />Sensors</div></button><ul id="sensorsBtnList" class="compMenuL1 compMenu hiddenMenu"></ul></li>'+
						'<li><button type="button" class="menuButton" id="processorsBtn"><div class="buttonContents"><img src="view/images/processor.png" alt="Open model" />Processors</div></button><ul id="processorsBtnList" class="compMenuL1 compMenu hiddenMenu"></ul></li>'+
						'<li><button type="button" class="menuButton" id="actuatorsBtn"><div class="buttonContents"><img src="view/images/actuator.png" alt="Save model" />Actuators</div></button><ul id="actuatorsBtnList" class="compMenuL1 compMenu hiddenMenu"></ul></li>'+
						'<li><button type="button" class="menuButton" id="savedGroupsBtn"><div class="buttonContents"><img src="view/images/group_elem.png" alt="Save model as" />Saved<br />Groups</div></button><ul id="savedGroupsBtnList" class="compMenuL1 compMenu hiddenMenu"></ul></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup bg_right">'+
					'<label for="quickselect">Quickselect</label>'+
					'<input id="quickselect" list="componentsDataList">'+
					'<datalist id="componentsDataList">'+
					'</datalist>'+
					'<input id="insertButton" type="button" value="Insert"></input>'+
				'</section>'+
			  '</div>'+

			  '<div id="panel3" class="panel menuPanel" aria-labelledby="tab3" role="tabpanel">'+
				'<section class="buttonGroup">'+
					'<h2>Model Properties</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="editModelIDBtn"><div class="buttonContents"><img src="view/images/editModelName.png" alt="Edit Model ID" />Edit<br />Model ID</div></button></li>'+
						'<li><button type="button" class="menuButton" id="showModelDescBtn"><div class="buttonContents"><img src="view/images/editModelDescription.png" alt="Edit Model Description" />Show Model<br />Description</div></button></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup">'+
					'<h2>Edit Components</h2>'+			
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="moveComponentBtn"><div class="buttonContents"><img src="view/images/move.png" alt="Move Component" />Move<br />Component</div></button></li>'+
						'<li><button type="button" class="menuButton" id="componentPropertiesBtn"><div class="buttonContents"><img src="view/images/properties.png" alt="Component Properties" />Component<br />Properties</div></button></li>'+
					'</ul>'+
				'</section>'+
				'<section class="buttonGroup bg_right">'+
					'<h2>Edit</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="newChannelBtn"><div class="buttonContents"><img src="view/images/edit.png" alt="New Channel" />New<br />Channel</div></button></li>'+
						'<li><button type="button" class="menuButton" id="newEventchannelBtn"><div class="buttonContents"><img src="view/images/edit_event.png" alt="New Eventchannel" />New<br />Eventchannel</div></button></li>'+
						'<li><button type="button" class="menuButton" id="cutBtn"><div class="buttonContents"><img src="view/images/cut.png" alt="Cut" />Cut</div></button></li>'+
						'<li><button type="button" class="menuButton" id="copyBtn"><div class="buttonContents"><img src="view/images/copy.png" alt="Copy" />Copy</div></button></li>'+
						'<li><button type="button" class="menuButton" id="pasteBtn"><div class="buttonContents"><img src="view/images/paste.png" alt="Paste" />Paste</div></button></li>'+
						'<li><button type="button" class="menuButton" id="deleteSelectionBtn"><div class="buttonContents"><img src="view/images/delete.png" alt="Delete Selection" />Delete<br />Selection</div></button></li>'+
						'<li><button type="button" class="menuButton" id="undoBtn"><div class="buttonContents"><img src="view/images/undo.png" alt="Undo" />Undo</div></button></li>'+
						'<li><button type="button" class="menuButton" id="redoBtn"><div class="buttonContents"><img src="view/images/redo.png" alt="Redo" />Redo</div></button></li>'+
						'<li><button type="button" class="menuButton" id="groupBtn"><div class="buttonContents"><img src="view/images/group.png" alt="Group" />Group</div></button></li>'+
						'<li><button type="button" class="menuButton" id="ungroupBtn"><div class="buttonContents"><img src="view/images/ungroup.png" alt="Ungroup" />Ungroup</div></button></li>'+
						'<li><button type="button" class="menuButton" id="saveGroupBtn"><div class="buttonContents"><img src="view/images/group_save.png" alt="Save Group" />Save<br />Group</div></button></li>'+
					'</ul>'+
				'</section>'+
			  '</div>'+

			  '<div id="panel4" class="panel menuPanel" aria-labelledby="tab4" role="tabpanel">'+
				'<section class="buttonGroup bg_right">'+
					'<h2>Miscellaneous</h2>'+
					'<ul class="buttonList">'+
						'<li><button type="button" class="menuButton" id="getAREStatusBtn"><div class="buttonContents"><img src="view/images/status.png" alt="Get ARE Status" />Get ARE<br />Status</div></button></li>'+
						'<li><button type="button" class="menuButton" id="showLogfileAREBtn"><div class="buttonContents"><img src="view/images/logging.png" alt="Show Logfile from ARE" />Show Logfile<br />from ARE</div></button></li>'+
						'<li><button type="button" class="menuButton" id="componentCollMgrBtn"><div class="buttonContents"><img src="view/images/ConfigureBundle.png" alt="Component Collection Manager" />Component<br />Collection Manager</div></button></li>'+
						'<li><button type="button" class="menuButton" id="optionsBtn"><div class="buttonContents"><img src="view/images/Options.png" alt="Options" />Options</div></button></li>'+
						'<li><button type="button" class="menuButton" id="printBtn"><div class="buttonContents"><img src="view/images/Printer.png" alt="Print" />Print</div></button></li>'+
						'<li><button type="button" class="menuButton" id="aboutBtn"><div class="buttonContents"><img src="view/images/about.png" alt="About" />About</div></button></li>'+
					'</ul>'+
				'</section>'+
			  '</div>'+
			'</div>';
	document.getElementById('canvasMotherPanel').innerHTML = ''+
			'<ul id="canvasPanelTabList" class="tablist" role="tablist">'+
			'</ul>';
	document.getElementById('propertyEditorMotherPanel').innerHTML = ''+
			'<ul id="propertyEditorTabList" class="tablist" role="tablist">'+
			'</ul>';	
}