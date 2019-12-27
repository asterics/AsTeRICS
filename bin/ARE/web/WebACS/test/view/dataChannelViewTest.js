QUnit.module( 'dataChannelView' );

QUnit.test( 'dataChannelView initialisation', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	assert.strictEqual(dataChannelView.line.stroke(), ACS.vConst.DATACHANNELVIEW_STROKECOLOR);
	assert.strictEqual(dataChannelView.line.points()[0], 1 + ACS.vConst.DATACHANNELVIEW_OUTPUTPORTPOSITIONX);
	assert.strictEqual(dataChannelView.line.points()[1], 2 + ACS.vConst.DATACHANNELVIEW_FIRSTOUTPUTPORTDOCKINGPOINTY + ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP * port.getPosition());
	assert.strictEqual(dataChannelView.line.points()[2], 1 + ACS.vConst.DATACHANNELVIEW_OUTPUTPORTPOSITIONX);
	assert.strictEqual(dataChannelView.line.points()[3], 2 + ACS.vConst.DATACHANNELVIEW_FIRSTOUTPUTPORTDOCKINGPOINTY + ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP * port.getPosition());
});

QUnit.test( 'dataChannelView getChannel', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	assert.strictEqual(dataChannelView.getChannel(), dataChannel);
});

QUnit.test( 'dataChannelView destroy', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	dataChannelView.destroy();
	assert.strictEqual(dataChannelView.line, null);
});

QUnit.test( 'dataChannelView componentPositionChangedEventHandlerOutPort', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	dataChannel.getOutputPort().getParentComponent().setNewPosition(5,6);
	assert.strictEqual(dataChannelView.line.points()[0], 5 + ACS.vConst.DATACHANNELVIEW_OUTPUTPORTPOSITIONX);
	assert.strictEqual(dataChannelView.line.points()[1], 6 + ACS.vConst.DATACHANNELVIEW_FIRSTOUTPUTPORTDOCKINGPOINTY + ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP * port.getPosition());
});

QUnit.test( 'dataChannelView componentPositionChangedEventHandlerInPort', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp1 = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port1 = ACS.port('portId', comp1, 2, 3, 4, true);
	var comp2 = ACS.component("comp2","typeID","desc",true,3,4,"actuator",true,true);
	var port2 = ACS.port('portId2', comp2, 2, 3, 4, true);
	dataChannel.setOutputPort(port1);
	dataChannel.setInputPort(port2);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	dataChannel.getInputPort().getParentComponent().setNewPosition(5,6);
	assert.strictEqual(dataChannelView.line.points()[2], 5 - ACS.vConst.DATACHANNELVIEW_INPUTPORTLEFTOFCOMPONENT);
	assert.strictEqual(dataChannelView.line.points()[3], 6 + ACS.vConst.DATACHANNELVIEW_FIRSTINPUTPORTDOCKINGPOINTY + ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP * port2.getPosition());
});

QUnit.test( 'dataChannelView dataChannelCompletedEventHandler', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	var comp2 = ACS.component("comp2","typeID","desc",true,3,4,"actuator",true,true);
	var port2 = ACS.port('portId2', comp2, 2, 3, 4, true);
	dataChannel.setInputPort(port2);
	assert.strictEqual(dataChannelView.line.points()[2], 3 - ACS.vConst.DATACHANNELVIEW_INPUTPORTLEFTOFCOMPONENT);
	assert.strictEqual(dataChannelView.line.points()[3], 4 + ACS.vConst.DATACHANNELVIEW_FIRSTINPUTPORTDOCKINGPOINTY + ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP * port2.getPosition());
});

QUnit.test( 'dataChannelView selectedEventHandler', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	assert.strictEqual(dataChannelView.line.dashEnabled(), false); // checking default
	dataChannel.setIsSelected(true);
	assert.strictEqual(dataChannelView.line.dashEnabled(), true);
});

QUnit.test( 'dataChannelView deSelectedEventHandler', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var dataChannel = ACS.dataChannel('dc1');
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var port = ACS.port('portId', comp, 2, 3, 4, true);
	dataChannel.setOutputPort(port);
	var dataChannelView = ACS.dataChannelView(dataChannel, model, modelLayer);
	dataChannel.setIsSelected(true);
	assert.strictEqual(dataChannelView.line.dashEnabled(), true);
	dataChannel.setIsSelected(false);
	assert.strictEqual(dataChannelView.line.dashEnabled(), false);
});