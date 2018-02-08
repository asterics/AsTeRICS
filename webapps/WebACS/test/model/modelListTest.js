QUnit.module( 'modelList' );

QUnit.test( 'modelList addNewModel', function( assert ) {
	var modelList = ACS.modelList();
	assert.strictEqual(modelList.getModelAtIndex(0).getFilename(), 'newFile1');
});

QUnit.test( 'modelList setActModel', function( assert ) {
	var modelList = ACS.modelList();
	modelList.addNewModel();
	modelList.addNewModel();
	modelList.setActModel(1);
	assert.strictEqual(modelList.actIndex, 1);
	assert.strictEqual(modelList.getActModel().getFilename(), 'newFile2');
});

QUnit.test( 'modelList removeModel', function( assert ) {
	var modelList = ACS.modelList();
	modelList.addNewModel();
	modelList.addNewModel();
	modelList.setActModel(1);
	modelList.removeModel();
	assert.strictEqual(modelList.getLength(), 2);
	assert.strictEqual(modelList.getActModel().getFilename(), 'newFile3');
});

QUnit.test( 'modelList handling of filenameBeingChangedEvent', function( assert ) {
	var modelList = ACS.modelList();
	modelList.getActModel().setFilename('anotherName');
	assert.strictEqual(modelList.getModelAtIndex(0).getFilename(), 'anotherName');
});