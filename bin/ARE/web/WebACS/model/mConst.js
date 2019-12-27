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
 
// ACS Datamodel Constants
ACS.mConst = {
	// model
	MODEL_DEFAULTCOMPONENTCOLLECTION: 'defaultComponentCollection.abd',
	MODEL_DEFAULTCOMPONENTCOLLECTIONONARE: '/componentCollections/defaultComponentCollection.abd',
	MODEL_COMPONENTPOSITIONOFFSETX: 10, // if the position of a component is already taken, the new one is inserted this much off to the right
	MODEL_COMPONENTPOSITIONOFFSETY: 10, // if the position of a component is already taken, the new one is inserted this much off to the bottom
	MODEL_NEWCOMPONENTPOSITIONX: 15,
	MODEL_NEWCOMPONENTPOSITIONY: 15,
	// visualAreaMarker
	VISUALAREAMARKER_BGCOLOR: 'rgba(0, 0, 255, 0.9)',
	VISUALAREAMARKER_BORDERCOLOR: 'rgba(0, 0, 255, 0)',
	// modelGUI
	MODELGUI_DECORATION: true,
	MODELGUI_FULLSCREEN: false,
	MODELGUI_ALWAYSONTOP: false,
	MODELGUI_TOSYSTEMTRAY: false,
	MODELGUI_SHOWCONTROLPANEL: true,
	MODELGUI_AREGUIWINDOW_X: 0,
	MODELGUI_AREGUIWINDOW_Y: 0,
	MODELGUI_AREGUIWINDOW_WIDTH: 10000,
	MODELGUI_AREGUIWINDOW_HEIGHT: 6000,
	MODELGUI_ACSVERSION: 'webACS_v0.1_to_be_replaced_by_proper_version_number',
	// clipBoard
	CLIPBOARD_IDEXTENSION: '_c'
}