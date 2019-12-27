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
 
var ACS = {};

ACS.portType = {
	INPUT: 1,
	OUTPUT: 2
};

ACS.dataType = {
	BOOLEAN: 1,
	BYTE: 2,
	CHAR: 3,
	INTEGER: 4,
	DOUBLE: 5,
	STRING: 6
};

ACS.componentType = {
	SENSOR: 1,
	PROCESSOR: 2,
	ACTUATOR: 3
};

ACS.statusType = {
	DISCONNECTED: 1,
	CONNECTING: 2,
	CONNECTED: 3,
	STARTING: 4,
	STARTED: 5,
	PAUSING: 6,
	PAUSED: 7,
	RESUMING: 8,
	STOPPING: 9,
	STOPPED: 10,
	CONNECTIONLOST: 11
};

ACS.gridStepType = {
	SMALL: 1,
	MEDIUM: 2,
	LARGE: 3,
	HUGE: 4
};

ACS.screenResType = {
	FIVEFOUR: 1,
	SIXTEENNINE: 2,
	FOURTHREE: 3
};

 ACS.tabPanelKeyCodes = {
  TAB: 9,
  ENTER: 13,
  ESC: 27,
  SPACE: 32,
  PGUP: 33,
  PGDOWN: 34,
  END: 35,
  POS1: 36,
  LEFT: 37,
  UP: 38,
  RIGHT: 39,
  DOWN: 40
};