QUnit.module( 'addComponentAction' );

QUnit.test( 'addComponentAction initialization', function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var action = ACS.addComponentAction(model, comp);
	assert.strictEqual(action.getModel(), model);
});

QUnit.test( 'addComponentAction execute', function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var action = ACS.addComponentAction(model, comp);
	action.execute();
	assert.strictEqual(model.componentList[0], comp);
	assert.strictEqual(model.undoStack[0], action);
});

QUnit.test( 'addComponentAction undo', function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var action = ACS.addComponentAction(model, comp);
	action.execute();
	action.undo();
	assert.strictEqual(model.componentList.length, 0);
	assert.strictEqual(model.redoStack[0], action);
});