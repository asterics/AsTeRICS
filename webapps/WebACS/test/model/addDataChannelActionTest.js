QUnit.module( 'addDataChannelAction' );

QUnit.test( 'addDataChannelAction initialization', function( assert ) {
	var model = ACS.model("test.acs");
	var dc = ACS.dataChannel('dataChannel1');
	var action = ACS.addDataChannelAction(model, dc);
	assert.strictEqual(action.getModel(), model);
});

QUnit.test( 'addDataChannelAction execute', function( assert ) {
	var model = ACS.model("test.acs");
	var dc = ACS.dataChannel('dataChannel1');
	var action = ACS.addDataChannelAction(model, dc);
	action.execute();
	assert.strictEqual(model.dataChannelList[0], dc);
	assert.strictEqual(model.undoStack[0], action);
});

QUnit.test( 'addDataChannelAction undo', function( assert ) {
	var model = ACS.model("test.acs");
	var dc = ACS.dataChannel('dataChannel1');
	var action = ACS.addDataChannelAction(model, dc);
	action.execute();
	action.undo();
	assert.strictEqual(model.dataChannelList.length, 0);
	assert.strictEqual(model.redoStack[0], action);
});