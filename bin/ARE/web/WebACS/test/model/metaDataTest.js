QUnit.module( 'metaData' );

QUnit.test( 'metaData initialization', function( assert ) {
	var md = ACS.metaData('keyString', 'some value');
	assert.strictEqual(md.getKey(), 'keyString');
	assert.strictEqual(md.value, 'some value');
});