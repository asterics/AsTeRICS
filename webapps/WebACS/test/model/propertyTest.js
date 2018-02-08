QUnit.module( 'property' );

QUnit.test( 'property initialization', function( assert ) {
	var ppt = ACS.property('keyString', 3, '2.5');
	assert.strictEqual(ppt.getKey(), 'keyString');
	assert.strictEqual(ppt.getType(), 3);
	assert.strictEqual(ppt.value, '2.5');
});