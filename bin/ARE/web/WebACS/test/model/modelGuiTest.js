QUnit.module( 'modelGui' );

QUnit.test( 'modelGui setDecoration', function( assert ) {
	var mGui = ACS.modelGui();
	mGui.setDecoration(false);
	assert.strictEqual(mGui.getDecoration(), false);
});

QUnit.test( 'modelGui setFullScreen', function( assert ) {
	var mGui = ACS.modelGui();
	mGui.setFullScreen(true);
	assert.strictEqual(mGui.getFullScreen(), true);
});

QUnit.test( 'modelGui setAlwaysOnTop', function( assert ) {
	var mGui = ACS.modelGui();
	mGui.setAlwaysOnTop(true);
	assert.strictEqual(mGui.getAlwaysOnTop(), true);
});

QUnit.test( 'modelGui setToSystemTray', function( assert ) {
	var mGui = ACS.modelGui();
	mGui.setToSystemTray(true);
	assert.strictEqual(mGui.getToSystemTray(), true);
});

QUnit.test( 'modelGui setShowControlPanel', function( assert ) {
	var mGui = ACS.modelGui();
	mGui.setShowControlPanel(false);
	assert.strictEqual(mGui.getShowControlPanel(), false);
});