QUnit.module( 'gui' );

QUnit.test( 'gui initialization', function( assert ) {
	var gui = ACS.gui(1, 2, 30, 40, true);
	assert.strictEqual(gui.x, 1);
	assert.strictEqual(gui.y, 2);
	assert.strictEqual(gui.width, 30);
	assert.strictEqual(gui.height, 40);
	assert.strictEqual(gui.getIsExternal(), true);
});