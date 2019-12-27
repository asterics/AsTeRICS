QUnit.module( 'menuView' );

QUnit.test( 'menuView setComponentMenu', function( assert ) {
	var modelList = ACS.modelList();
	var menuView = ACS.menuView(modelList);
	menuView.setComponentMenu();
	assert.strictEqual(document.getElementById('sensorsBtnList').getElementsByTagName('li').length, 50);
	assert.strictEqual(document.getElementById('processorsBtnList').getElementsByTagName('li').length, 73);
	assert.strictEqual(document.getElementById('actuatorsBtnList').getElementsByTagName('li').length, 54);
});

// componentCollectionChangedEventHandler and actModelChangedEventHandler simply call setComponentMenu - so nothing more to test here

// handleConnectARE: TODO

QUnit.test( 'menuView handleNewModel', function( assert ) {
	var modelList = ACS.modelList();
	var menuView = ACS.menuView(modelList);
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	var port = ACS.port('outP', comp, 1, 0, 0, false);
	comp.outputPortList.push(port);
	modelList.getActModel().addComponent(comp);
	$('#newModelBtn').trigger('click');
	assert.strictEqual(modelList.getLength(), 2);
});

// handleSelectedFile and handleOpenModel require user input to file dialog

QUnit.test( 'menuView handleCloseModel', function( assert ) {
	var modelList = ACS.modelList();
	var menuView = ACS.menuView(modelList);
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	var port = ACS.port('outP', comp, 1, 0, 0, false);
	comp.outputPortList.push(port);
	modelList.getActModel().addComponent(comp);
	$('#newModelBtn').trigger('click');
	assert.strictEqual(modelList.getLength(), 2);
	$('#closeModelBtn').trigger('click');
	assert.strictEqual(modelList.getLength(), 1);
});

// handleSaveModel requires user input to file dialog

QUnit.test( 'menuView handleCompMenu', function( assert ) {
	var modelList = ACS.modelList();
	var menuView = ACS.menuView(modelList);
	menuView.setComponentMenu();
	var evObjEnter = document.createEvent( 'Events' );
    evObjEnter.initEvent('mouseenter', true, false );
	var evObjLeave = document.createEvent( 'Events' );
    evObjLeave.initEvent('mouseleave', true, false );	
	document.getElementById('sensorsBtn').dispatchEvent( evObjEnter );
	assert.strictEqual(document.getElementById('sensorsBtnList').className, 'compMenuL1 compMenu');
	document.getElementById('sensorsBtn').dispatchEvent( evObjLeave );
	document.getElementById('sensorInertialMeasurement').dispatchEvent( evObjEnter );
	assert.strictEqual(document.getElementById('sensorInertialMeasurementList').className, 'compMenuL2 compMenu');
	document.getElementById('sensorInertialMeasurement').dispatchEvent( evObjLeave );
	document.getElementById('processorsBtn').dispatchEvent( evObjEnter );
	var done = assert.async();
	setTimeout(function() {
		assert.strictEqual(document.getElementById('sensorsBtnList').className, 'compMenuL1 compMenu hiddenMenu');
		assert.strictEqual(document.getElementById('sensorInertialMeasurementList').className, 'compMenuL2 compMenu hiddenMenu');
		done();
	}, 200);
});

// handleCut, handleCopy, handlePaste, handleDeleteSelection, handleUndo and handleRedo are implicitly tested in viewTest.js