QUnit.module( 'canvasView' );

QUnit.test( 'canvasView addActModelToView', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	// addActModelToView is called by the canvasView constructor
	assert.strictEqual(document.getElementById('canvasTab0').textContent, 'newFile1');
});

QUnit.test( 'canvasView filenameBeingChangedEventHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	// addActModelToView is called by the canvasView constructor, a file called "newFile1" is therefore already there
	modelList.getActModel().setFilename('theChangedFilename');
	assert.strictEqual(document.getElementById('canvasTab0').textContent, 'theChangedFilename');
});

QUnit.test( 'canvasView newModelAddedEventHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	modelList.addNewModel();
	assert.strictEqual(document.getElementById('canvasTab1').textContent, 'newFile2');
});

QUnit.test( 'canvasView removingModelEventHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	modelList.addNewModel();
	modelList.removeModel();
	assert.strictEqual(document.getElementById('canvasTab1'), null);
});

QUnit.test( 'canvasView tabSwitchedEventHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	modelList.addNewModel();
	assert.strictEqual(modelList.getActModel().getFilename(), 'newFile2');
	$('#canvasTab0').trigger('click');
	assert.strictEqual(modelList.getActModel().getFilename(), 'newFile1');
});

QUnit.test( 'canvasView actModelChangedEventHandler', function( assert ) {
	resetDocument();
	var modelList = ACS.modelList();
	var clipBoard = ACS.clipBoard();
	var cv = ACS.canvasView(modelList, clipBoard);
	modelList.addNewModel();
	assert.strictEqual(document.getElementById('canvasTab0').getAttribute('aria-selected'), 'false');
	assert.strictEqual(document.getElementById('canvasTab1').getAttribute('aria-selected'), 'true');
	modelList.setActModel(0);
	assert.strictEqual(document.getElementById('canvasTab1').getAttribute('aria-selected'), 'false');
	assert.strictEqual(document.getElementById('canvasTab0').getAttribute('aria-selected'), 'true');
});