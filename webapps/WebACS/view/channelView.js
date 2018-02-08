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
 
 ACS.channelView = function(model, // ACS.model
						    modelView, // ACS.modelView
						    modelLayer) { // Kinetic.Layer
						   
// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var visible = true;
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.line = {};
	
	returnObj.setStartPoint = function(x, y) {
		if (returnObj.line) {
			returnObj.line.points([x, y, returnObj.line.points()[2], returnObj.line.points()[3]]);
		}
	}
	
	returnObj.setEndPoint = function(x, y) {
		if (returnObj.line) {
			returnObj.line.points([returnObj.line.points()[0], returnObj.line.points()[1], x, y]);
		}
	}
	
	returnObj.setVisible = function(vis) {
		visible = vis;
		if (returnObj.line) {
			if (vis === true) {
				returnObj.line.show();
			} else {
				returnObj.line.hide();
			}
			visible = vis;
		}
	}
	
	returnObj.getVisible = function() {
		return visible;
	}
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	returnObj.line = new Kinetic.Line({
		points: [0, 0, 0, 0],
		stroke: ACS.vConst.CHANNELVIEW_STROKECOLOR,
		strokeWidth: ACS.vConst.CHANNELVIEW_STROKEWIDTH,
		dash: [5, 5],
		dashEnabled: false,
		// set custom hitRegion to be shorter than actual channel to avoid KinteicJs antiAliasing-bug
		hitFunc: function(context) {
			context.beginPath();
			context.moveTo(returnObj.line.points()[0], returnObj.line.points()[1]);
			var channelEnd = [];
			if (returnObj.line.points()[2] > returnObj.line.points()[0])
				channelEnd[0] = returnObj.line.points()[2]-2;
			else
				channelEnd[0] = returnObj.line.points()[2]+2;
			if (returnObj.line.points()[3] > returnObj.line.points()[1])
				channelEnd[1] = returnObj.line.points()[3]-2;
			else
				channelEnd[1] = returnObj.line.points()[3]+2;			
			context.lineTo(channelEnd[0], channelEnd[1]);
			this.setStrokeWidth(ACS.vConst.CHANNELVIEW_HITREGIONWIDTH);
			context.fillStrokeShape(this);
			this.setStrokeWidth(ACS.vConst.CHANNELVIEW_STROKEWIDTH);
		}
	});
	
	returnObj.line.on('mousedown', function(e) {
		e.cancelBubble = true; // prevents modelView from starting a focusRect; note that this is KineticJS' cancelBubble attribute, not the one IE uses
	});
	
	modelLayer.add(returnObj.line);
	
	return returnObj;
}