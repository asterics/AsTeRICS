QUnit.module( 'model' );

QUnit.test( 'Model Initialization', function( assert ) {
	var model = ACS.model("test.acs");
	assert.strictEqual(model.getFilename(), "test.acs");
});

QUnit.test( 'Model setFileName', function( assert ) {
	var model = ACS.model("test.acs");
	model.setFilename("anotherTest.acs");
	assert.strictEqual(model.getFilename(), "anotherTest.acs");
});

QUnit.test( "Model findComponentInCollection", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = model.findComponentInCollection("asterics.AnalogOut");
	assert.strictEqual(comp.getAttribute('id'), "asterics.AnalogOut");
});

QUnit.test( "Model getDataType", function( assert ) {
	var model = ACS.model("test.acs");
	assert.strictEqual(model.getDataType('boolean'), 1);
	assert.strictEqual(model.getDataType('byte'), 2);
	assert.strictEqual(model.getDataType('char'), 3);
	assert.strictEqual(model.getDataType('integer'), 4);
	assert.strictEqual(model.getDataType('double'), 5);
	assert.strictEqual(model.getDataType('string'), 6);
});

QUnit.test( "Model getFreePosition", function( assert ) {
	var model = ACS.model("test.acs");
	assert.strictEqual(model.getFreePosition([1, 2])[0], 1);
	assert.strictEqual(model.getFreePosition([1, 2])[1], 2);
	model.addComponent(ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true));
	assert.strictEqual(model.getFreePosition([1, 2])[0], 1 + ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETX);
	assert.strictEqual(model.getFreePosition([1, 2])[1], 2 + ACS.mConst.MODEL_COMPONENTPOSITIONOFFSETY);
});

QUnit.test( "Model loadModelFromFile", function( assert ) {
	var model = ACS.model("test.acs");
	var file = new File(['<?xml version="1.0"?>\r<model xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" modelName="29.07.2014_1130" version="20130320">\r	<modelDescription>\r		<shortDescription>das ist die short description</shortDescription>\r		<requirements>das sind die model requirements</requirements>\r		<description>das ist eine detaillierte description</description>\r	</modelDescription>\r	<components>\r		<component id="ButtonGrid.1" type_id="asterics.ButtonGrid">\r			<description>Keyboard which sends event after button press</description>\r			<properties>\r				<property name="caption" value="Keyboard" />\r				<property name="horizontalOrientation" value="false" />\r				<property name="buttonCaption1" value="Button 1" />\r				<property name="buttonCaption2" value="Button 2" />\r				<property name="buttonCaption3" value="Button 3" />\r				<property name="buttonCaption4" value="Button 4" />\r				<property name="buttonCaption5" value="Button 5" />\r				<property name="buttonCaption6" value="" />\r				<property name="buttonCaption7" value="" />\r				<property name="buttonCaption8" value="" />\r				<property name="buttonCaption9" value="" />\r				<property name="buttonCaption10" value="" />\r				<property name="buttonCaption11" value="" />\r				<property name="buttonCaption12" value="" />\r				<property name="buttonCaption13" value="" />\r				<property name="buttonCaption14" value="" />\r				<property name="buttonCaption15" value="" />\r				<property name="buttonCaption16" value="" />\r				<property name="buttonCaption17" value="" />\r				<property name="buttonCaption18" value="" />\r				<property name="buttonCaption19" value="" />\r				<property name="buttonCaption20" value="" />\r			</properties>\r			<layout>\r				<posX>226</posX>\r				<posY>293</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>444</posY>\r				<width>500</width>\r				<height>2000</height>\r			</gui>\r		</component>\r		<component id="Arduino.1" type_id="asterics.Arduino">\r			<description>Arduino Microcontroller CIM</description>\r			<ports>\r				<inputPort portTypeID="pwm3" sync="false">\r				</inputPort>\r				<inputPort portTypeID="pwm5" sync="false">\r				</inputPort>\r				<inputPort portTypeID="pwm6" sync="false">\r				</inputPort>\r				<outputPort portTypeID="A0">\r				</outputPort>\r				<outputPort portTypeID="A1">\r				</outputPort>\r				<outputPort portTypeID="A2">\r				</outputPort>\r				<outputPort portTypeID="A3">\r				</outputPort>\r				<outputPort portTypeID="A4">\r				</outputPort>\r				<outputPort portTypeID="A5">\r				</outputPort>\r			</ports>\r			<properties>\r				<property name="periodicADCUpdate" value="0" />\r				<property name="pin2Mode" value="0" />\r				<property name="pin3Mode" value="0" />\r				<property name="pin4Mode" value="0" />\r				<property name="pin5Mode" value="0" />\r				<property name="pin6Mode" value="0" />\r				<property name="pin7Mode" value="0" />\r				<property name="pin8Mode" value="0" />\r				<property name="pin9Mode" value="0" />\r				<property name="pin10Mode" value="0" />\r				<property name="pin11Mode" value="0" />\r				<property name="pin12Mode" value="0" />\r				<property name="pin13Mode" value="0" />\r			</properties>\r			<layout>\r				<posX>80</posX>\r				<posY>80</posY>\r			</layout>\r		</component>\r		<component id="PlatformDigitalOut.1" type_id="asterics.PlatformDigitalOut">\r			<description>Control for the digital outputs of the Personal Platform</description>\r			<ports>\r				<inputPort portTypeID="command" sync="false">\r				</inputPort>\r			</ports>\r			<properties>\r				<property name="pullupOutput1" value="false" />\r				<property name="pullupOutput2" value="false" />\r			</properties>\r			<layout>\r				<posX>353</posX>\r				<posY>109</posY>\r			</layout>\r		</component>\r		<component id="DoubleToString.1" type_id="asterics.DoubleToString">\r			<description>Converts double to string</description>\r			<ports>\r				<inputPort portTypeID="input" sync="false">\r				</inputPort>\r				<outputPort portTypeID="output">\r				</outputPort>\r			</ports>\r			<layout>\r				<posX>228</posX>\r				<posY>27</posY>\r			</layout>\r		</component>\r		<component id="FacetrackerLK.1" type_id="asterics.FacetrackerLK">\r			<description>Webcamera-based face tracking sensor</description>\r			<ports>\r				<outputPort portTypeID="noseX">\r				</outputPort>\r				<outputPort portTypeID="noseY">\r				</outputPort>\r				<outputPort portTypeID="chinX">\r				</outputPort>\r				<outputPort portTypeID="chinY">\r				</outputPort>\r			</ports>\r			<properties>\r				<property name="cameraSelection" value="0" />\r				<property name="cameraResolution" value="1" />\r				<property name="cameraDisplayUpdate" value="100" />\r				<property name="cameraProfile" value="" />\r			</properties>\r			<layout>\r				<posX>558</posX>\r				<posY>187</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>0</posY>\r				<width>2000</width>\r				<height>1500</height>\r			</gui>\r		</component>\r	</components>\r	<channels>\r		<channel id="binding.0">\r			<source>\r				<component id="Arduino.1" />\r				<port id="A0" />\r			</source>\r			<target>\r				<component id="DoubleToString.1" />\r				<port id="input" />\r			</target>\r		</channel>\r	</channels>\r	<eventChannels>\r		<eventChannel id="pin2ChangedLowToHigh_setOut1">\r			<sources>\r				<source>\r					<component id="Arduino.1" />\r					<eventPort id="pin2ChangedLowToHigh" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id="PlatformDigitalOut.1" />\r					<eventPort id="setOut1" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id="pin4ChangedHighToLow_setOut2">\r			<sources>\r				<source>\r					<component id="Arduino.1" />\r					<eventPort id="pin4ChangedHighToLow" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id="PlatformDigitalOut.1" />\r					<eventPort id="setOut2" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id="pin8ChangedHighToLow_clearOut1">\r			<sources>\r				<source>\r					<component id="Arduino.1" />\r					<eventPort id="pin8ChangedHighToLow" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id="PlatformDigitalOut.1" />\r					<eventPort id="clearOut1" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id="pin11ChangedHighToLow_clearOut2">\r			<sources>\r				<source>\r					<component id="Arduino.1" />\r					<eventPort id="pin11ChangedHighToLow" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id="PlatformDigitalOut.1" />\r					<eventPort id="clearOut2" />\r				</target>\r			</targets>\r		</eventChannel>\r	</eventChannels>\r	<modelGUI>\r		<Decoration>true</Decoration>\r		<Fullscreen>false</Fullscreen>\r		<AlwaysOnTop>false</AlwaysOnTop>\r		<ToSystemTray>false</ToSystemTray>\r		<ShopControlPanel>true</ShopControlPanel>\r		<AREGUIWindow>\r			<posX>0</posX>\r			<posY>0</posY>\r			<width>9000</width>\r			<height>5000</height>\r		</AREGUIWindow>\r	</modelGUI>\r</model>'], "test1.acs");
	model.loadModelFromFile(file);
	var done = assert.async();
	setTimeout(function() {
		assert.strictEqual(model.getFilename(), "test1.acs");
		assert.strictEqual(model.componentList.length, 5);
		assert.strictEqual(model.componentList[1].getId(), 'Arduino.1');
		assert.strictEqual(model.dataChannelList.length, 1);
		assert.strictEqual(model.dataChannelList[0].getId(), 'binding.0');
		assert.strictEqual(model.eventChannelList.length, 1);
		assert.strictEqual(model.eventChannelList[0].getId(), 'Arduino.1_PlatformDigitalOut.1');
		assert.strictEqual(model.modelGui.getDecoration(), true);
		// TODO: add assertion for visualAreaMarker
		assert.strictEqual(model.metaDataList.length, 3);
		assert.strictEqual(model.metaDataList[0].value, 'das ist die short description');
		assert.strictEqual(model.modelName, '29.07.2014_1130');
		assert.strictEqual(model.acsVersion, '20130320');
		done();
	});
});

QUnit.test( "Model getModelXMLString", function( assert ) {
	var model = ACS.model("test.acs");
	var file = new File(['<?xml version=\"1.0\"?>\r<model xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" modelName=\"29.07.2014_1130\" version=\"20130320\">\r	<modelDescription>\r		<shortDescription>das ist die short description</shortDescription>\r		<requirements>das sind die model requirements</requirements>\r		<description>das ist eine detaillierte description</description>\r	</modelDescription>\r	<components>\r		<component id=\"ButtonGrid.1\" type_id=\"asterics.ButtonGrid\">\r			<description>Keyboard which sends event after button press</description>\r		<ports />\r		<properties>\r				<property name=\"caption\" value=\"Keyboard\" />\r				<property name=\"horizontalOrientation\" value=\"false\" />\r				<property name=\"buttonCaption1\" value=\"Button 1\" />\r				<property name=\"buttonCaption2\" value=\"Button 2\" />\r				<property name=\"buttonCaption3\" value=\"Button 3\" />\r				<property name=\"buttonCaption4\" value=\"Button 4\" />\r				<property name=\"buttonCaption5\" value=\"Button 5\" />\r				<property name=\"buttonCaption6\" value=\"\" />\r				<property name=\"buttonCaption7\" value=\"\" />\r				<property name=\"buttonCaption8\" value=\"\" />\r				<property name=\"buttonCaption9\" value=\"\" />\r				<property name=\"buttonCaption10\" value=\"\" />\r				<property name=\"buttonCaption11\" value=\"\" />\r				<property name=\"buttonCaption12\" value=\"\" />\r				<property name=\"buttonCaption13\" value=\"\" />\r				<property name=\"buttonCaption14\" value=\"\" />\r				<property name=\"buttonCaption15\" value=\"\" />\r				<property name=\"buttonCaption16\" value=\"\" />\r				<property name=\"buttonCaption17\" value=\"\" />\r				<property name=\"buttonCaption18\" value=\"\" />\r				<property name=\"buttonCaption19\" value=\"\" />\r				<property name=\"buttonCaption20\" value=\"\" />\r			</properties>\r			<layout>\r				<posX>226</posX>\r				<posY>293</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>444</posY>\r				<width>500</width>\r				<height>2000</height>\r			</gui>\r		</component>\r		<component id=\"Arduino.1\" type_id=\"asterics.Arduino\">\r			<description>Arduino Microcontroller CIM</description>\r			<ports>\r				<inputPort portTypeID=\"pwm3\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<inputPort portTypeID=\"pwm5\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<inputPort portTypeID=\"pwm6\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<outputPort portTypeID=\"A0\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A1\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A2\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A3\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A4\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A5\">\r					<properties />\r				</outputPort>\r			</ports>\r			<properties>\r				<property name=\"periodicADCUpdate\" value=\"0\" />\r				<property name=\"pin2Mode\" value=\"0\" />\r				<property name=\"pin3Mode\" value=\"0\" />\r				<property name=\"pin4Mode\" value=\"0\" />\r				<property name=\"pin5Mode\" value=\"0\" />\r				<property name=\"pin6Mode\" value=\"0\" />\r				<property name=\"pin7Mode\" value=\"0\" />\r				<property name=\"pin8Mode\" value=\"0\" />\r				<property name=\"pin9Mode\" value=\"0\" />\r				<property name=\"pin10Mode\" value=\"0\" />\r				<property name=\"pin11Mode\" value=\"0\" />\r				<property name=\"pin12Mode\" value=\"0\" />\r				<property name=\"pin13Mode\" value=\"0\" />\r			</properties>\r			<layout>\r				<posX>80</posX>\r				<posY>80</posY>\r			</layout>\r		</component>\r		<component id=\"PlatformDigitalOut.1\" type_id=\"asterics.PlatformDigitalOut\">\r			<description>Control for the digital outputs of the Personal Platform</description>\r			<ports>\r				<inputPort portTypeID=\"command\" sync=\"false\">\r					<properties />\r				</inputPort>\r			</ports>\r			<properties>\r				<property name=\"pullupOutput1\" value=\"false\" />\r				<property name=\"pullupOutput2\" value=\"false\" />\r			</properties>\r			<layout>\r				<posX>353</posX>\r				<posY>109</posY>\r			</layout>\r		</component>\r		<component id=\"DoubleToString.1\" type_id=\"asterics.DoubleToString\">\r			<description>Converts double to string</description>\r			<ports>\r				<inputPort portTypeID=\"input\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<outputPort portTypeID=\"output\">\r					<properties />\r				</outputPort>\r			</ports>\r			<layout>\r				<posX>228</posX>\r				<posY>27</posY>\r			</layout>\r		</component>\r		<component id=\"FacetrackerLK.1\" type_id=\"asterics.FacetrackerLK\">\r			<description>Webcamera-based face tracking sensor</description>\r			<ports>\r				<outputPort portTypeID=\"noseX\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"noseY\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"chinX\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"chinY\">\r					<properties />\r				</outputPort>\r			</ports>\r			<properties>\r				<property name=\"cameraSelection\" value=\"0\" />\r				<property name=\"cameraResolution\" value=\"1\" />\r				<property name=\"cameraDisplayUpdate\" value=\"100\" />\r				<property name=\"cameraProfile\" value=\"\" />\r			</properties>\r			<layout>\r				<posX>558</posX>\r				<posY>187</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>0</posY>\r				<width>2000</width>\r				<height>1500</height>\r			</gui>\r		</component>\r	</components>\r	<channels>\r		<channel id=\"binding.0\">\r			<source>\r				<component id=\"Arduino.1\" />\r				<port id=\"A0\" />\r			</source>\r			<target>\r				<component id=\"DoubleToString.1\" />\r				<port id=\"input\" />\r			</target>\r		</channel>\r	</channels>\r	<eventChannels>\r		<eventChannel id=\"pin2ChangedLowToHigh_setOut1\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin2ChangedLowToHigh\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"setOut1\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin4ChangedHighToLow_setOut2\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin4ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"setOut2\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin8ChangedHighToLow_clearOut1\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin8ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"clearOut1\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin11ChangedHighToLow_clearOut2\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin11ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"clearOut2\" />\r				</target>\r			</targets>\r		</eventChannel>\r	</eventChannels>\r	<modelGUI>\r		<Decoration>true</Decoration>\r		<Fullscreen>false</Fullscreen>\r		<AlwaysOnTop>false</AlwaysOnTop>\r		<ToSystemTray>false</ToSystemTray>\r		<ShopControlPanel>true</ShopControlPanel>\r		<AREGUIWindow>\r			<posX>0</posX>\r			<posY>0</posY>\r			<width>9000</width>\r			<height>5000</height>\r		</AREGUIWindow>\r	</modelGUI>\r</model>'], "test1.acs");
	model.loadModelFromFile(file);
	var done = assert.async();
	setTimeout(function() {
		assert.strictEqual(model.getModelXMLString(), '<?xml version=\"1.0\"?>\r<model xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" modelName=\"29.07.2014_1130\" version=\"20130320\">\r	<modelDescription>\r		<shortDescription>das ist die short description</shortDescription>\r		<requirements>das sind die model requirements</requirements>\r		<description>das ist eine detaillierte description</description>\r	</modelDescription>\r	<components>\r		<component id=\"ButtonGrid.1\" type_id=\"asterics.ButtonGrid\">\r			<description>Keyboard which sends event after button press</description>\r			<ports />\r			<properties>\r				<property name=\"caption\" value=\"Keyboard\" />\r				<property name=\"horizontalOrientation\" value=\"false\" />\r				<property name=\"buttonCaption1\" value=\"Button 1\" />\r				<property name=\"buttonCaption2\" value=\"Button 2\" />\r				<property name=\"buttonCaption3\" value=\"Button 3\" />\r				<property name=\"buttonCaption4\" value=\"Button 4\" />\r				<property name=\"buttonCaption5\" value=\"Button 5\" />\r				<property name=\"buttonCaption6\" value=\"\" />\r				<property name=\"buttonCaption7\" value=\"\" />\r				<property name=\"buttonCaption8\" value=\"\" />\r				<property name=\"buttonCaption9\" value=\"\" />\r				<property name=\"buttonCaption10\" value=\"\" />\r				<property name=\"buttonCaption11\" value=\"\" />\r				<property name=\"buttonCaption12\" value=\"\" />\r				<property name=\"buttonCaption13\" value=\"\" />\r				<property name=\"buttonCaption14\" value=\"\" />\r				<property name=\"buttonCaption15\" value=\"\" />\r				<property name=\"buttonCaption16\" value=\"\" />\r				<property name=\"buttonCaption17\" value=\"\" />\r				<property name=\"buttonCaption18\" value=\"\" />\r				<property name=\"buttonCaption19\" value=\"\" />\r				<property name=\"buttonCaption20\" value=\"\" />\r			</properties>\r			<layout>\r				<posX>226</posX>\r				<posY>293</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>444</posY>\r				<width>500</width>\r				<height>2000</height>\r			</gui>\r		</component>\r		<component id=\"Arduino.1\" type_id=\"asterics.Arduino\">\r			<description>Arduino Microcontroller CIM</description>\r			<ports>\r				<inputPort portTypeID=\"pwm3\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<inputPort portTypeID=\"pwm5\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<inputPort portTypeID=\"pwm6\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<outputPort portTypeID=\"A0\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A1\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A2\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A3\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A4\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"A5\">\r					<properties />\r				</outputPort>\r			</ports>\r			<properties>\r				<property name=\"periodicADCUpdate\" value=\"0\" />\r				<property name=\"pin2Mode\" value=\"0\" />\r				<property name=\"pin3Mode\" value=\"0\" />\r				<property name=\"pin4Mode\" value=\"0\" />\r				<property name=\"pin5Mode\" value=\"0\" />\r				<property name=\"pin6Mode\" value=\"0\" />\r				<property name=\"pin7Mode\" value=\"0\" />\r				<property name=\"pin8Mode\" value=\"0\" />\r				<property name=\"pin9Mode\" value=\"0\" />\r				<property name=\"pin10Mode\" value=\"0\" />\r				<property name=\"pin11Mode\" value=\"0\" />\r				<property name=\"pin12Mode\" value=\"0\" />\r				<property name=\"pin13Mode\" value=\"0\" />\r			</properties>\r			<layout>\r				<posX>80</posX>\r				<posY>80</posY>\r			</layout>\r		</component>\r		<component id=\"PlatformDigitalOut.1\" type_id=\"asterics.PlatformDigitalOut\">\r			<description>Control for the digital outputs of the Personal Platform</description>\r			<ports>\r				<inputPort portTypeID=\"command\" sync=\"false\">\r					<properties />\r				</inputPort>\r			</ports>\r			<properties>\r				<property name=\"pullupOutput1\" value=\"false\" />\r				<property name=\"pullupOutput2\" value=\"false\" />\r			</properties>\r			<layout>\r				<posX>353</posX>\r				<posY>109</posY>\r			</layout>\r		</component>\r		<component id=\"DoubleToString.1\" type_id=\"asterics.DoubleToString\">\r			<description>Converts double to string</description>\r			<ports>\r				<inputPort portTypeID=\"input\" sync=\"false\">\r					<properties />\r				</inputPort>\r				<outputPort portTypeID=\"output\">\r					<properties />\r				</outputPort>\r			</ports>\r			<layout>\r				<posX>228</posX>\r				<posY>27</posY>\r			</layout>\r		</component>\r		<component id=\"FacetrackerLK.1\" type_id=\"asterics.FacetrackerLK\">\r			<description>Webcamera-based face tracking sensor</description>\r			<ports>\r				<outputPort portTypeID=\"noseX\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"noseY\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"chinX\">\r					<properties />\r				</outputPort>\r				<outputPort portTypeID=\"chinY\">\r					<properties />\r				</outputPort>\r			</ports>\r			<properties>\r				<property name=\"cameraSelection\" value=\"0\" />\r				<property name=\"cameraResolution\" value=\"1\" />\r				<property name=\"cameraDisplayUpdate\" value=\"100\" />\r				<property name=\"cameraProfile\" value=\"\" />\r			</properties>\r			<layout>\r				<posX>558</posX>\r				<posY>187</posY>\r			</layout>\r			<gui>\r				<posX>0</posX>\r				<posY>0</posY>\r				<width>2000</width>\r				<height>1500</height>\r			</gui>\r		</component>\r	</components>\r	<channels>\r		<channel id=\"binding.0\">\r			<source>\r				<component id=\"Arduino.1\" />\r				<port id=\"A0\" />\r			</source>\r			<target>\r				<component id=\"DoubleToString.1\" />\r				<port id=\"input\" />\r			</target>\r		</channel>\r	</channels>\r	<eventChannels>\r		<eventChannel id=\"pin2ChangedLowToHigh_setOut1\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin2ChangedLowToHigh\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"setOut1\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin4ChangedHighToLow_setOut2\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin4ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"setOut2\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin8ChangedHighToLow_clearOut1\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin8ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"clearOut1\" />\r				</target>\r			</targets>\r		</eventChannel>\r		<eventChannel id=\"pin11ChangedHighToLow_clearOut2\">\r			<sources>\r				<source>\r					<component id=\"Arduino.1\" />\r					<eventPort id=\"pin11ChangedHighToLow\" />\r				</source>\r			</sources>\r			<targets>\r				<target>\r					<component id=\"PlatformDigitalOut.1\" />\r					<eventPort id=\"clearOut2\" />\r				</target>\r			</targets>\r		</eventChannel>\r	</eventChannels>\r	<modelGUI>\r		<Decoration>true</Decoration>\r		<Fullscreen>false</Fullscreen>\r		<AlwaysOnTop>false</AlwaysOnTop>\r		<ToSystemTray>false</ToSystemTray>\r		<ShopControlPanel>true</ShopControlPanel>\r		<AREGUIWindow>\r			<posX>0</posX>\r			<posY>0</posY>\r			<width>9000</width>\r			<height>5000</height>\r		</AREGUIWindow>\r	</modelGUI>\r</model>');
		done();
	});
});

QUnit.test( "Model initiateComponentByName", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = model.initiateComponentByName('Deadzone');
	assert.strictEqual(comp.getId(), 'Deadzone.1');
	assert.strictEqual(comp.getComponentTypeId(), 'asterics.Deadzone');
	assert.strictEqual(comp.getDescription(), 'Defines active/passive Zone for x/y values');
	assert.strictEqual(comp.getSingleton(), false);
	assert.strictEqual(comp.getX(), ACS.mConst.MODEL_NEWCOMPONENTPOSITIONX);
	assert.strictEqual(comp.getY(), ACS.mConst.MODEL_NEWCOMPONENTPOSITIONY);
	assert.strictEqual(comp.getType(), 2);
	assert.strictEqual(comp.getIsSelected(), false);
	assert.strictEqual(comp.inputPortList.length, 3);
	assert.strictEqual(comp.inputPortList[2].getId(), 'radius');
	assert.strictEqual(comp.outputPortList.length, 2);
	assert.strictEqual(comp.outputPortList[0].getId(), 'outX');
	assert.strictEqual(comp.listenEventList.length, 1);
	assert.strictEqual(comp.listenEventList[0].getId(), 'setCenter');
	assert.strictEqual(comp.triggerEventList.length, 2);
	assert.strictEqual(comp.triggerEventList[1].getId(), 'exitZone');
	assert.strictEqual(comp.propertyList.length, 4);
	assert.strictEqual(comp.propertyList[2].getKey(), 'radius');
	assert.strictEqual(comp.gui, null);
	assert.strictEqual(comp.matchesComponentCollection, true);
	assert.strictEqual(comp.foundInComponentCollection, true);
});

QUnit.test( "Model addComponent", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	model.addComponent(comp);
	assert.strictEqual(model.componentList[0], comp);
});

QUnit.test( "Model removeComponent", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",true,true);
	model.addComponent(comp);
	model.removeComponent(comp);
	assert.strictEqual(model.componentList.length, 0);
});

QUnit.test( "Model addDataChannel", function( assert ) {
	var model = ACS.model("test.acs");
	var ch = ACS.dataChannel('channel1');
	model.addDataChannel(ch);
	assert.strictEqual(model.dataChannelList[0], ch);
});

QUnit.test( "Model removeDataChannel", function( assert ) {
	var model = ACS.model("test.acs");
	var ch = ACS.dataChannel('channel1');
	model.addDataChannel(ch);
	model.removeDataChannel(ch);
	assert.strictEqual(model.dataChannelList.length, 0);
});

QUnit.test( "Model addEventChannel", function( assert ) {
	var model = ACS.model("test.acs");
	var ch = ACS.eventChannel('channel1');
	model.addEventChannel(ch);
	assert.strictEqual(model.eventChannelList[0], ch);
});

QUnit.test( "Model removeEventChannel", function( assert ) {
	var model = ACS.model("test.acs");
	var ch = ACS.eventChannel('channel1');
	model.addEventChannel(ch);
	model.removeEventChannel(ch);
	assert.strictEqual(model.eventChannelList.length, 0);
});

QUnit.test( "Model getComponentCollection", function( assert ) {
	var model = ACS.model("test.acs");
	var coll = model.getComponentCollection();
	assert.strictEqual(coll.getElementsByTagName('componentType')[0].getAttribute('id'), 'asterics.AnalogOut');
});

// loadComponentCollection requires user inputs

QUnit.test( "Model addItemToSelection", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",false,true);
	model.addItemToSelection(comp);
	assert.strictEqual(model.selectedItemsList[0], comp);
});

QUnit.test( "Model removeItemFromSelection", function( assert ) {
	var model = ACS.model("test.acs");
	var comp = ACS.component("comp1","typeID","desc",true,1,2,"actuator",false,true);
	model.addItemToSelection(comp);
	model.removeItemFromSelection(comp);
	assert.strictEqual(model.selectedItemsList.length, 0);
})

QUnit.test( "Model deSelectAll", function( assert ) {
	var model = ACS.model("test.acs");
	var comp1 = ACS.component("comp1","typeID","desc",true,1,2,"actuator",false,true);
	var comp2 = ACS.component("comp2","typeID","desc",true,1,2,"actuator",false,true);
	model.addItemToSelection(comp1);
	model.addItemToSelection(comp2);
	model.deSelectAll();
	assert.strictEqual(model.selectedItemsList.length, 0);
})
