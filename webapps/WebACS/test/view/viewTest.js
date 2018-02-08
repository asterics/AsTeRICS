QUnit.module( 'view' );

// handleKeyDown and handleKeyPress require user inputs

QUnit.test( 'view cutBtnPressedHandler_pasteBtnPressedHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	var port = ACS.port('outP', comp, 1, 0, 0, false);
	comp.outputPortList.push(port);
	modelList.getActModel().addComponent(comp);
	modelList.getActModel().addItemToSelection(comp);
	var clipBoard = ACS.clipBoard();
	var v = ACS.view(modelList, clipBoard);
	$('#cutBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 0);
	modelList.addNewModel();
	var alertStub = sinon.stub(window, 'alert');
	$('#pasteBtn').trigger('click');
	assert.strictEqual(alertStub.callCount, 1);
	assert.strictEqual(alertStub.getCall(0).args[0], ACS.vConst.MODELVIEW_ALERTSTRINGCHANGEDCOMPONENTS + 'comp1_c (FS20Receiver)\n');
	assert.strictEqual(modelList.getActModel().componentList.length, 1);
	assert.strictEqual(modelList.getActModel().componentList[0].getId(), 'comp1_c');
	// to prevent browser from presenting a popup on unload of the page:
	modelList.getModelAtIndex(0).hasBeenChanged = false;
	modelList.getModelAtIndex(1).hasBeenChanged = false;
	window.alert.restore();
});

QUnit.test( 'view copyBtnPressedHandler_pasteBtnPressedHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	modelList.getActModel().addComponent(comp);
	modelList.getActModel().addItemToSelection(comp);
	var clipBoard = ACS.clipBoard();
	var v = ACS.view(modelList, clipBoard);
	$('#copyBtn').trigger('click');
	modelList.addNewModel();
	var alertStub = sinon.stub(window, 'alert');
	$('#pasteBtn').trigger('click');
	assert.strictEqual(alertStub.callCount, 1);
	assert.strictEqual(alertStub.getCall(0).args[0], ACS.vConst.MODELVIEW_ALERTSTRINGCHANGEDCOMPONENTS + 'comp1_c (FS20Receiver)\n');	
	assert.strictEqual(modelList.getActModel().componentList.length, 1);
	assert.strictEqual(modelList.getActModel().componentList[0].getId(), 'comp1_c');
	// to prevent browser from presenting a popup on unload of the page:
	modelList.getModelAtIndex(0).hasBeenChanged = false;
	modelList.getModelAtIndex(1).hasBeenChanged = false;
	window.alert.restore();
});

QUnit.test( 'view deleteBtnPressedHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	modelList.getActModel().addComponent(comp);
	modelList.getActModel().addItemToSelection(comp);
	var clipBoard = ACS.clipBoard();
	var v = ACS.view(modelList, clipBoard);
	assert.strictEqual(modelList.getActModel().componentList.length, 1);
	$('#deleteSelectionBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 0);
	// to prevent browser from presenting a popup on unload of the page:
	modelList.getModelAtIndex(0).hasBeenChanged = false;	
});

QUnit.test( 'view undoBtnPressedHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	modelList.getActModel().addComponent(comp);
	modelList.getActModel().addItemToSelection(comp);
	var clipBoard = ACS.clipBoard();
	var v = ACS.view(modelList, clipBoard);
	$('#deleteSelectionBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 0);
	$('#undoBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 1);
	// to prevent browser from presenting a popup on unload of the page:
	modelList.getModelAtIndex(0).hasBeenChanged = false;	
});

QUnit.test( 'view redoBtnPressedHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var comp = ACS.component("comp1","asterics.FS20Receiver","desc",true,1,2,"actuator",false,true);
	modelList.getActModel().addComponent(comp);
	modelList.getActModel().addItemToSelection(comp);
	var clipBoard = ACS.clipBoard();
	var v = ACS.view(modelList, clipBoard);
	$('#deleteSelectionBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 0);
	$('#undoBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 1);
	$('#redoBtn').trigger('click');
	assert.strictEqual(modelList.getActModel().componentList.length, 0);
	// to prevent browser from presenting a popup on unload of the page:
	modelList.getModelAtIndex(0).hasBeenChanged = false;	
});