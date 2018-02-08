QUnit.module( 'channelView' );

// initialisation is implicitly tested through the tests of eventChannelView and dataChannelView

QUnit.test( 'channelView setStartPoint', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var channelView = ACS.channelView(model, modelLayer);
	channelView.setStartPoint(1, 2);
	assert.strictEqual(channelView.line.points()[0], 1);
	assert.strictEqual(channelView.line.points()[1], 2);
});

QUnit.test( 'channelView setEndPoint', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var channelView = ACS.channelView(model, modelLayer);
	channelView.setEndPoint(3, 4);
	assert.strictEqual(channelView.line.points()[2], 3);
	assert.strictEqual(channelView.line.points()[3], 4);
});

QUnit.test( 'channelView setVisible_getVisible', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var channelView = ACS.channelView(model, modelLayer);
	assert.strictEqual(channelView.line.isVisible(), true); // checking default
	assert.strictEqual(channelView.getVisible(), true); // checking default
	channelView.setVisible(false);
	assert.strictEqual(channelView.line.isVisible(), false);
	assert.strictEqual(channelView.getVisible(), false);
});

QUnit.test( 'channelView destroy', function( assert ) {
	resetDocument();
	var model = ACS.model('model1');
	modelLayer = new Kinetic.Layer({
		width: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEX,
		height: ACS.vConst.MODELVIEW_MODELDESIGNERSIZEY
	});
	var channelView = ACS.channelView(model, modelLayer);
	
	channelView.setEndPoint(3, 4);
	assert.strictEqual(channelView.line.points()[2], 3);
	assert.strictEqual(channelView.line.points()[3], 4);
});