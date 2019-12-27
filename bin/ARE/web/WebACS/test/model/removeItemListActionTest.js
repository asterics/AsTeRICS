QUnit.module( 'removeItemListAction' );

QUnit.test( 'removeItemListAction initialization', function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	
	var itemList = [comp];
	var action = ACS.removeItemListAction(model, itemList);
	assert.strictEqual(action.getModel(), model);
});

QUnit.test( 'removeItemListAction execute', function( assert ) {
	var model = ACS.model("test.acs");
	var comp1 = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var comp2 = ACS.component("comp2","typeID","desc",true,1,2,"actuator",true,true);
	var dc1 = ACS.dataChannel('dataChannel1');
	var dc2 = ACS.dataChannel('dataChannel2');
	var ec1 = ACS.eventChannel('eventChannel1');
	var ec2 = ACS.eventChannel('eventChannel2');
	model.addComponent(comp1);
	model.addComponent(comp2);
	model.addDataChannel(dc1);
	model.addDataChannel(dc2);
	model.addEventChannel(ec1);
	model.addEventChannel(ec2);
	var itemList = [];
	itemList.push(comp1);
	itemList.push(dc1);
	itemList.push(ec1);
	var action = ACS.removeItemListAction(model, itemList);
	action.execute();
	assert.strictEqual(model.componentList.length, 1);
	assert.strictEqual(model.componentList[0], comp2);
	assert.strictEqual(model.dataChannelList.length, 1);
	assert.strictEqual(model.dataChannelList[0], dc2);
	assert.strictEqual(model.eventChannelList.length, 1);
	assert.strictEqual(model.eventChannelList[0], ec2);
	assert.strictEqual(model.undoStack[0], action);
});

QUnit.test( 'removeItemListAction undo', function( assert ) {
	var model = ACS.model("test.acs");
	var comp1 = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var comp2 = ACS.component("comp2","typeID","desc",true,1,2,"actuator",true,true);
	var dc1 = ACS.dataChannel('dataChannel1');
	var dc2 = ACS.dataChannel('dataChannel2');
	var ec1 = ACS.eventChannel('eventChannel1');
	var ec2 = ACS.eventChannel('eventChannel2');
	model.addComponent(comp1);
	model.addComponent(comp2);
	model.addDataChannel(dc1);
	model.addDataChannel(dc2);
	model.addEventChannel(ec1);
	model.addEventChannel(ec2);
	var itemList = [];
	itemList.push(comp1);
	itemList.push(dc1);
	itemList.push(ec1);
	var action = ACS.removeItemListAction(model, itemList);
	action.execute();
	action.undo();
	assert.strictEqual(model.componentList.length, 2);
	assert.strictEqual(model.dataChannelList.length, 2);
	assert.strictEqual(model.eventChannelList.length, 2);
	assert.strictEqual(model.undoStack[0], action);
});