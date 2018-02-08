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
 
 ACS.modelGui = function() { 


// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var decoration = ACS.mConst.MODELGUI_DECORATION;
	var fullScreen = ACS.mConst.MODELGUI_FULLSCREEN;
	var alwaysOnTop = ACS.mConst.MODELGUI_ALWAYSONTOP;
	var toSystemTray = ACS.mConst.MODELGUI_TOSYSTEMTRAY;
	var showControlPanel = ACS.mConst.MODELGUI_SHOWCONTROLPANEL;

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.events = ACS.eventManager();
	
	returnObj.areGuiWindow = ACS.gui(ACS.mConst.MODELGUI_AREGUIWINDOW_X, ACS.mConst.MODELGUI_AREGUIWINDOW_Y, ACS.mConst.MODELGUI_AREGUIWINDOW_WIDTH, ACS.mConst.MODELGUI_AREGUIWINDOW_HEIGHT, false);
	
	returnObj.setDecoration = function(dec) {
		decoration = dec;
		returnObj.events.fireEvent('decorationChangedEvent');
	}
	
	returnObj.getDecoration = function() {
		return decoration;
	}

	returnObj.setFullScreen = function(fs) {
		fullScreen = fs;
	}
	
	returnObj.getFullScreen = function() {
		return fullScreen;
	}

	returnObj.setAlwaysOnTop = function(aot) {
		alwaysOnTop = aot;
	}
	
	returnObj.getAlwaysOnTop = function() {
		return alwaysOnTop;
	}

	returnObj.setToSystemTray = function(tst) {
		toSystemTray = tst;
	}
	
	returnObj.getToSystemTray = function() {
		return toSystemTray;
	}

	returnObj.setShowControlPanel = function(scp) {
		showControlPanel = scp;
		returnObj.events.fireEvent('showControlPanelChangedEvent');
	}
	
	returnObj.getShowControlPanel = function() {
		return showControlPanel;
	}	
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	
	return returnObj;
}
