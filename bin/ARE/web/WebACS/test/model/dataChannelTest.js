QUnit.module( 'dataChannel' );

QUnit.test( 'dataChannel initialization', function( assert ) {
	var ch = ACS.dataChannel('theID');
	assert.strictEqual(ch.getId(), 'theID');
});

QUnit.test( 'dataChannel setInputPort', function( assert ) {
	var ch = ACS.dataChannel('theID');
	var port = ACS.port('portId', {}, 2, 3, 4, true);
	ch.setInputPort(port);
	assert.strictEqual(ch.getInputPort(), port);
});

QUnit.test( 'dataChannel setOutputPort', function( assert ) {
	var ch = ACS.dataChannel('theID');
	var port = ACS.port('portId', {}, 2, 3, 4, true);
	ch.setOutputPort(port);
	assert.strictEqual(ch.getOutputPort(), port);
});