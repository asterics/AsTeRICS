QUnit.module( 'component' );

QUnit.test( "Component Initialization", function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	assert.strictEqual(comp.getId(),"comp1");
	assert.strictEqual(comp.getComponentTypeId(),"typeID");
	assert.strictEqual(comp.getDescription(),"desc");
	assert.strictEqual(comp.getSingleton(),true);
	assert.strictEqual(comp.getX(),1);
	assert.strictEqual(comp.getY(),2);
	assert.strictEqual(comp.getType(),"actuator");
	assert.strictEqual(comp.getIsSelected(),true);
	assert.strictEqual(comp.foundInComponentCollection,true);
});

QUnit.test( "Component SetID", function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	comp.setId("comp2");
	assert.strictEqual(comp.getId(),"comp2");
});

QUnit.test( "Component SetDescription", function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	comp.setDescription("desc 1");
	assert.strictEqual(comp.getDescription(),"desc 1");
});

QUnit.test( "Component setNewPosition 0", function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	comp.setNewPosition(2,3);
	assert.strictEqual(comp.getX(),2);
	assert.strictEqual(comp.getY(),3);
});

QUnit.test( "Component Set IsSelected", function( assert ) {
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	comp.setIsSelected(false);
	assert.strictEqual(comp.getIsSelected(),false);
});
