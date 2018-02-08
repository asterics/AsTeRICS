/*
 * AsTeRICS - Assistive Technology Rapid Integration and Construction Set (http://www.asterics.org)
 * 
 * 
 * Y88b                     d88P      888               d8888  .d8888b.   .d8888b. 
 *  Y88b                   d88P       888              d88888 d88P  Y88b d88P  Y88b
 *   Y88b                 d88P        888             d88P888 888    888 Y88b.
 *    Y88b     d888b     d88P .d88b.  8888888b.      d88P 888 888         "Y888b.  
 *     Y88b   d88888b   d88P d8P  Y8b 888   Y88b    d88P  888 888            "Y88b.
 *      Y88b d88P Y88b d88P  88888888 888    888   d88P   888 888    888       "888
 *       Y88888P   Y88888P   Y8b.     888   d88P  d8888888888 Y88b  d88P Y88b  d88P
 *        Y888P     Y888P     "Y8888  8888888P"  d88P     888  "Y8888P"   "Y8888P"
 * 
 * Copyright 2015 Kompetenznetzwerk KI-I (http://ki-i.at)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
ACS.editorProperties = function() {

// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var enableGrid = ACS.vConst.EDITORPROPERTIES_ENABLEGRID;
	var showGrid = ACS.vConst.EDITORPROPERTIES_SHOWGRID;
	var gridSteps = ACS.vConst.EDITORPROPERTIES_GRIDSTEPS;
	var screenRes = ACS.vConst.EDITORPROPERTIES_SCREENRES;
	var guiDesignerSize; // Object {width, height}

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var setGuiDesignerSize = function() {
		switch (screenRes) {
			case ACS.screenResType.FIVEFOUR:	guiDesignerSize = {width: ACS.vConst.EDITORPROPERTIES_SCREENRESFIVEFOUR_X, height: ACS.vConst.EDITORPROPERTIES_SCREENRESFIVEFOUR_Y};
												break;
			case ACS.screenResType.SIXTEENNINE: guiDesignerSize = {width: ACS.vConst.EDITORPROPERTIES_SCREENRESSIXTEENNINE_X, height: ACS.vConst.EDITORPROPERTIES_SCREENRESSIXTEENNINE_Y};
												break;
			case ACS.screenResType.FOURTHREE:	guiDesignerSize = {width: ACS.vConst.EDITORPROPERTIES_SCREENRESFOURTHREE_X, height: ACS.vConst.EDITORPROPERTIES_SCREENRESFOURTHREE_Y};
												break;
		}
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.events = ACS.eventManager();
	
	returnObj.getEnableGrid = function() {
		return enableGrid;
	}
	
	returnObj.setEnableGrid = function(enabled) { // bool
		enableGrid = enabled;
	}
	
	returnObj.getShowGrid = function() {
		return showGrid;
	}

	returnObj.setShowGrid = function(show) { // bool
		showGrid = show;
		returnObj.events.fireEvent('showGridChangedEvent');
	}	

	returnObj.getGridSteps = function() {
		return gridSteps;
	}
	
	returnObj.setGridSteps = function(newSteps) { // ACS.gridStepType
		gridSteps = newSteps;
		returnObj.events.fireEvent('gridStepsChangedEvent');
	}	

	returnObj.getScreenRes = function() {
		return screenRes;
	}

	returnObj.setScreenRes = function(newScreenRes) { // ACS.screenResType
		screenRes = newScreenRes;
		setGuiDesignerSize();
		returnObj.events.fireEvent('screenResChangedEvent');
	}
	
	returnObj.getGuiDesignerSize = function() {
		return guiDesignerSize;
	}

// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	setGuiDesignerSize();
	
	return returnObj;
}