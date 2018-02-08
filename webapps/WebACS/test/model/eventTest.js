QUnit.module( 'event' );

QUnit.test( 'event initialization', function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	var evt = ACS.event('evtId', 'description', comp);
	assert.strictEqual(evt.getId(), 'evtId');
	assert.strictEqual(evt.getDescription(), 'description');
	assert.strictEqual(evt.getParentComponent(), comp);
});

