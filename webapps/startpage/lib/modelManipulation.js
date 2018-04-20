/**
 * @file
 * This Javascript library provides functions that simplify manipulation of an AsTeRICS model in XML representation.
 * The lib provides functions for setting and getting a property value.
 * 
 * @author Martin Deinhofer
 * @version 0.1
 */

/**
 * Sets the property componentKey.propertyKey to the given propertyValue.
 * @param {string} componentKey - The component instance id.
 * @param {string} propertyKey - The property key of the component.
 * @param {string} propertyValue - The new value.
 * @param {string} xmlDoc - The XML model as string to be updated. 
 */
function setPropertyValueInXMLModel(componentKey, propertyKey, propertyValue, xmlDoc) {
	var commandPanel=xmlDoc.getElementsByTagName('component');

	var found=false;
	for(var i=0;i<commandPanel.length; i++) {
		var currentValue=commandPanel.item(i);
		if(currentValue.attributes.getNamedItem('id').textContent == componentKey) {
			
			var commandPanelProperties=currentValue.getElementsByTagName('property');
			
			for(var j=0;j<commandPanelProperties.length; j++) {
				var curProperty=commandPanelProperties.item(j);
				if(curProperty.getAttribute("name")==propertyKey) {
					curProperty.setAttribute("value",propertyValue);
					//console.log("Property ["+componentKey+"."+propertyKey+"="+propertyValue+"] set");											
					found=true;
				}
			}					
		} 
	}
	if(!found) {
		console.log("Property ["+componentKey+"."+propertyKey+"="+propertyValue+"] not set");				
	}
}

/**
 * Gets the property value of property componentKey.propertyKey.
 * @param {string} componentKey - The component instance id.
 * @param {string} propertyKey - The property key of the component.
 * @param {string} xmlDoc - The XML model as string to be updated. 
 * @returns {string} The property value.
 */
function getPropertyValueFromXMLModel(componentKey, propertyKey, xmlDoc) {
	var commandPanel=xmlDoc.getElementsByTagName('component');

	for(var i=0;i<commandPanel.length; i++) {
		var currentValue=commandPanel.item(i);
		if(currentValue.attributes.getNamedItem('id').textContent == componentKey) {
			
			var commandPanelProperties=currentValue.getElementsByTagName('property');
			
			for(var j=0;j<commandPanelProperties.length; j++) {
				var curProperty=commandPanelProperties.item(j);
				if(curProperty.getAttribute("name")==propertyKey) {
					var propVal=curProperty.getAttribute("value");
					//console.log("Property ["+componentKey+"."+propertyKey+"="+propVal+"]");
					return propVal;
				}
			}					
		} 
	}
	console.log("Property ["+componentKey+"."+propertyKey+"] not found");
	return undefined;
}

/**
 * Converts the given XML document object to an XML string.
 * @param {Document} xmlData - The XML document object. 
 * @returns {string} The XML document as string.
 */
function xmlToString(xmlData) { 
    var xmlString;
    //IE
    if (window.ActiveXObject){
        xmlString = xmlData.xml;
    }
    // code for Mozilla, Firefox, Opera, etc.
    else{
        xmlString = (new XMLSerializer()).serializeToString(xmlData);
    }
    return xmlString;
}