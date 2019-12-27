QUnit.module( 'modelView' );

// initialisation is tested implicitly by the following two tests

QUnit.test( 'modelView getModel', function( assert ) {
	resetDocument();
	// create a panel to hold the modelView
	var div = document.createElement('div');
	div.setAttribute('id', 'canvasPanelTest');
	document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).appendChild(div);
	//
	var model = ACS.model('model1');
	var clipBoard = ACS.clipBoard();
	var modelView = ACS.modelView('canvasPanelTest', model, clipBoard);
	assert.strictEqual(modelView.getModel(), model);
});

QUnit.test( 'modelView getModelContainerId', function( assert ) {
	resetDocument();
	// create a panel to hold the modelView
	var div = document.createElement('div');
	div.setAttribute('id', 'canvasPanelTest');
	document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).appendChild(div);
	//
	var model = ACS.model('model1');
	var clipBoard = ACS.clipBoard();
	var modelView = ACS.modelView('canvasPanelTest', model, clipBoard);
	assert.strictEqual(modelView.getModelContainerId(), 'canvasPanelTest');
});

QUnit.test( 'modelView modelChangedEventHandler', function( assert ) {
	resetDocument();
	// create a panel to hold the modelView
	var div = document.createElement('div');
	div.setAttribute('id', 'canvasPanelTest');
	document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).appendChild(div);
	//
	var model = ACS.model('model1');
	var clipBoard = ACS.clipBoard();
	var modelView = ACS.modelView('canvasPanelTest', model, clipBoard);
	// a model has to be loaded from a file in order to invoke the modelChangedEventHandler
	// this model deliberately contains a component that will not be found in componentCollection and therefore an alert will be fired, which is what we can check
	var file = new File(['<?xml version="1.0"?>\r<model xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" modelName="29.07.2014_1130" version="20130320">\r	<modelDescription>\r		<shortDescription>das ist die short description</shortDescription>\r		<requirements>das sind die model requirements</requirements>\r		<description>das ist eine detaillierte description</description>\r	</modelDescription>\r	<components>\r		<component id="ButtonGrid.1" type_id="asterics.ButtonGrid">\r			<description>Keyboard which sends event after button press</description>\r			<properties>\r	</properties>\r			<layout>\r				<posX>226</posX>\r				<posY>293</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>444</posY>\r				<width>500</width>\r				<height>2000</height>\r			</gui>\r		</component>\r		</components>\r	<channels>\r		</channels>\r	<eventChannels>\r		</eventChannels>\r	<modelGUI>\r		<Decoration>true</Decoration>\r		<Fullscreen>false</Fullscreen>\r		<AlwaysOnTop>false</AlwaysOnTop>\r		<ToSystemTray>false</ToSystemTray>\r		<ShopControlPanel>true</ShopControlPanel>\r		<AREGUIWindow>\r			<posX>0</posX>\r			<posY>0</posY>\r			<width>9000</width>\r			<height>5000</height>\r		</AREGUIWindow>\r	</modelGUI>\r</model>'], "test1.acs");
	var alertStub = sinon.stub(window, 'alert');
	model.loadModelFromFile(file);
	var done = assert.async();
	setTimeout(function() {
		assert.strictEqual(alertStub.callCount, 1);
		window.alert.restore();
		done();
	}, 100);	
});

QUnit.test( 'modelView alertUserOfComponentCollectionMismatchEventHandler', function( assert ) {
	resetDocument();
	// create a panel to hold the modelView
	var div = document.createElement('div');
	div.setAttribute('id', 'canvasPanelTest');
	document.getElementById(ACS.vConst.CANVASVIEW_MOTHERPANEL).appendChild(div);
	//
	var model = ACS.model('model1');
	var clipBoard = ACS.clipBoard();
	var comp2 = ACS.component("comp2","asterics.Proximity","desc",true,1,2,"actuator",false,true);
	model.addComponent(comp2);
	var comp3 = ACS.component("comp3","someIdNotInCollection","desc",true,1,2,"actuator",false,false);
	model.addComponent(comp3);
	model.addItemToSelection(comp2);
	model.addItemToSelection(comp3);
	clipBoard.cut(model);
	var newModel = ACS.model('newTestModel.acs');
	var modelView = ACS.modelView('canvasPanelTest', newModel, clipBoard);
	var alertStub = sinon.stub(window, 'alert');
	clipBoard.paste(newModel);
	assert.strictEqual(alertStub.callCount, 2);
	window.alert.restore();
});

// the other handlers in modelView must be tested manually