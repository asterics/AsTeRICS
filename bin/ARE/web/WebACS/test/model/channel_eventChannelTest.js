QUnit.module( 'channel_eventChannel' );

QUnit.test( 'channel_eventChannel initialization', function( assert ) {
	var ch = ACS.eventChannel('theID');
	assert.strictEqual(ch.getId(), 'theID');
});

QUnit.test( 'channel_eventChannel setId', function( assert ) {
	var ch = ACS.eventChannel('theID');
	ch.setId('anotherID');
	assert.strictEqual(ch.getId(), 'anotherID');
});

QUnit.test( 'channel_eventChannel setIsSelected', function( assert ) {
	var ch = ACS.eventChannel('theID');
	ch.setIsSelected(true);
	assert.strictEqual(ch.getIsSelected(), true);
});