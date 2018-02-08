QUnit.module( 'eventChannelView' );

QUnit.test( 'eventChannelView initialisation', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	assert.strictEqual(eventChannelView.line.stroke(), ACS.vConst.EVENTCHANNELVIEW_STROKECOLOR);
	assert.strictEqual(eventChannelView.line.points()[0], 1 + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX);
	assert.strictEqual(eventChannelView.line.points()[1], 2 + ACS.vConst.COMPONENTVIEW_ELEMENTHEIGHT + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT);
	assert.strictEqual(eventChannelView.line.points()[2], 1 + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX);
	assert.strictEqual(eventChannelView.line.points()[3], 2 + ACS.vConst.COMPONENTVIEW_ELEMENTHEIGHT + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT);
});

QUnit.test( 'eventChannelView getChannel', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	assert.strictEqual(eventChannelView.getChannel(), eventChannel);
});

QUnit.test( 'eventChannelView destroy', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	eventChannelView.destroy();
	assert.strictEqual(eventChannelView.line, null);
});

QUnit.test( 'eventChannelView componentPositionChangedEventHandlerStartComponent', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	eventChannel.startComponent.setNewPosition(5,6);
	assert.strictEqual(eventChannelView.line.points()[0], 5 + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX);
	assert.strictEqual(eventChannelView.line.points()[1], 6 + ACS.vConst.COMPONENTVIEW_ELEMENTHEIGHT + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT);
});

QUnit.test( 'eventChannelView componentPositionChangedEventHandlerEndComponent', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	eventChannel.endComponent = ACS.component("comp2","typeID","desc",true,3,4,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	eventChannel.endComponent.setNewPosition(5,6);
	assert.strictEqual(eventChannelView.line.points()[2], 5 + ACS.vConst.EVENTCHANNELVIEW_LISTENERPOSX);
	assert.strictEqual(eventChannelView.line.points()[3], 6 + ACS.vConst.COMPONENTVIEW_ELEMENTHEIGHT + ACS.vConst.EVENTCHANNELVIEW_LISTENERBELOWCOMPONENT);
});

// eventChannelCompletedEventHandler: the corresponding event is sent on user input

QUnit.test( 'eventChannelView selectedEventHandler', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	assert.strictEqual(eventChannelView.line.dashEnabled(), false); // checking default
	eventChannel.setIsSelected(true);
	assert.strictEqual(eventChannelView.line.dashEnabled(), true);
});

QUnit.test( 'eventChannelView deSelectedEventHandler', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var eventChannel = ACS.eventChannel('ec1');
	eventChannel.startComponent = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var eventChannelView = ACS.eventChannelView(eventChannel, model, modelLayer);
	eventChannel.setIsSelected(true);
	assert.strictEqual(eventChannelView.line.dashEnabled(), true);
	eventChannel.setIsSelected(false);
	assert.strictEqual(eventChannelView.line.dashEnabled(), false);
});