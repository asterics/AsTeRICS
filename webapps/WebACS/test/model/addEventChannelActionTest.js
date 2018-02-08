QUnit.module( 'addEventChannelAction' );

QUnit.test( 'addEventChannelAction initialization', function( assert ) {
	var model = ACS.model("test.acs");
	var ec = ACS.eventChannel('eventChannel1');
	var action = ACS.addEventChannelAction(model, ec);
	assert.strictEqual(action.getModel(), model);
});

QUnit.test( 'addEventChannelAction execute', function( assert ) {
	var model = ACS.model("test.acs");
	var ec = ACS.eventChannel('eventChannel1');
	var action = ACS.addEventChannelAction(model, ec);
	action.execute();
	assert.strictEqual(model.eventChannelList[0], ec);
	assert.strictEqual(model.undoStack[0], action);
});

QUnit.test( 'addEventChannelAction undo', function( assert ) {
	var model = ACS.model("test.acs");
	var ec = ACS.eventChannel('eventChannel1');
	var action = ACS.addEventChannelAction(model, ec);
	action.execute();
	action.undo();
	assert.strictEqual(model.eventChannelList.length, 0);
	assert.strictEqual(model.redoStack[0], action);
});