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
 
 ACS.eventChannelView = function(ec, // ACS.eventChannel (must have at least the startComponent already set)
								model, // ACS.model
								modelView, // ACS.modelView
								modelLayer) { // Kinetic.Layer
								
// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	// ********************************************** private helper methods *********************************************
	var getComponentHeight = function(component) {
		// determine height of the component, depending on the amount of input- and/or output-ports
		var elementHeight = ACS.vConst.COMPONENTVIEW_ELEMENTHEIGHT;
		if ((component.outputPortList.length > 3) || (component.inputPortList.length > 3)) {
			if (component.outputPortList.length > component.inputPortList.length) {
				elementHeight = elementHeight + (component.outputPortList.length-3) * ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP;
			} else {
				elementHeight = elementHeight + (component.inputPortList.length-3) * ACS.vConst.COMPONENTVIEW_PORTHEIGHTPLUSGAP;
			}
		}
		return elementHeight;
	}
	
	var selectLine = function() {
		returnObj.line.stroke(ACS.vConst.EVENTCHANNELVIEW_SELECTEDSTROKECOLOR);
		returnObj.line.dashEnabled(true);
	}
	
	// ********************************************** handlers ***********************************************************
	var componentPositionChangedEventHandlerStartComponent = function() {
			returnObj.line.points([	ec.startComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX,
									ec.startComponent.getY() + getComponentHeight(ec.startComponent) + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT, 
									returnObj.line.points()[2], 
									returnObj.line.points()[3]]);
		if (!modelView.isDragging()) {
			modelLayer.draw();
		}									
	}

	var componentPositionChangedEventHandlerEndComponent = function() {
			returnObj.line.points([	returnObj.line.points()[0], 
									returnObj.line.points()[1], 
									ec.endComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_LISTENERPOSX,
									ec.endComponent.getY() + getComponentHeight(ec.endComponent) + ACS.vConst.EVENTCHANNELVIEW_LISTENERBELOWCOMPONENT]);
		if (!modelView.isDragging()) {
			modelLayer.draw();
		}									
	}
	
	var eventChannelCompletedEventHandler = function() {
		// set endpoint and and handler for endComponent and redraw
		returnObj.line.points([	returnObj.line.points()[0], 
								returnObj.line.points()[1], 
								ec.endComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_LISTENERPOSX,
								ec.endComponent.getY() + getComponentHeight(ec.endComponent) + ACS.vConst.EVENTCHANNELVIEW_LISTENERBELOWCOMPONENT]);
		ec.endComponent.events.registerHandler('componentPositionChangedEvent', componentPositionChangedEventHandlerEndComponent);
		modelLayer.draw();
	}
	
	var selectedEventHandler = function() {
		selectLine();
		modelLayer.draw();
	}

	var deSelectedEventHandler = function() {
		returnObj.line.stroke(ACS.vConst.EVENTCHANNELVIEW_STROKECOLOR);
		returnObj.line.dashEnabled(false);
		modelLayer.draw();
	}

// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = ACS.channelView(model, modelView, modelLayer);
	
	returnObj.getChannel = function() {
		return ec;
	}

	returnObj.destroy = function() {
		// remove all event handlers
		ec.startComponent.events.removeHandler('componentPositionChangedEvent', componentPositionChangedEventHandlerStartComponent);
		if (ec.endComponent) ec.endComponent.events.removeHandler('componentPositionChangedEvent', componentPositionChangedEventHandlerEndComponent);
		ec.events.removeHandler('eventChannelCompletedEvent', eventChannelCompletedEventHandler);
		ec.events.removeHandler('selectedEvent', selectedEventHandler);
		ec.events.removeHandler('deSelectedEvent', deSelectedEventHandler);
		// destroy the line
		if (returnObj.line) {
			returnObj.line.destroy();
			returnObj.line = null;
		}
	}

// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	returnObj.line.stroke(ACS.vConst.EVENTCHANNELVIEW_STROKECOLOR);
	if (ec.endComponent) { // i.e. channel is already complete
		returnObj.line.points([	ec.startComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX,
								ec.startComponent.getY() + getComponentHeight(ec.startComponent) + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT,
								ec.endComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_LISTENERPOSX,
								ec.endComponent.getY() + getComponentHeight(ec.endComponent) + ACS.vConst.EVENTCHANNELVIEW_LISTENERBELOWCOMPONENT]);
		ec.endComponent.events.registerHandler('componentPositionChangedEvent', componentPositionChangedEventHandlerEndComponent);
		// check if channel is already selected on insert
		if (ec.getIsSelected()) selectLine();
	} else {
		// draw a line with length == 0 - target coordinates will be set on mouse move
		returnObj.line.points([	ec.startComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX,
								ec.startComponent.getY() + getComponentHeight(ec.startComponent) + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT,
								ec.startComponent.getX() + ACS.vConst.EVENTCHANNELVIEW_TRIGGERPOSX,
								ec.startComponent.getY() + getComponentHeight(ec.startComponent) + ACS.vConst.EVENTCHANNELVIEW_TRIGGERBELOWCOMPONENT]);
	}
	
	// highlight channel when mouse is over hitGraph
	returnObj.line.on('mouseover', function(e) {
		returnObj.line.strokeWidth(ACS.vConst.CHANNELVIEW_STROKEWIDTH+2);
		modelLayer.draw();
	});
	returnObj.line.on('mouseout', function(e) {
		if (returnObj.line) { // mouseout will fire if channel has already been deleted, when mouse was over the channel at the time of deleting it
			returnObj.line.strokeWidth(ACS.vConst.CHANNELVIEW_STROKEWIDTH);
			modelLayer.draw();
		}
	});	
	
	// do the selecting
	returnObj.line.on('click', function(e) {
		if (e.evt.ctrlKey) {
			// invert selection status
			var newStatus = !ec.getIsSelected();
			if (newStatus) {
				model.addItemToSelection(ec);
			} else {
				model.removeItemFromSelection(ec);
			}
		} else {
			// select only this channel
			model.deSelectAll();
			model.addItemToSelection(ec);
		}
		e.cancelBubble = true; // note that this is KineticJS' cancelBubble attribute, not the one IE uses		
	});
	
	// register event handlers
	ec.startComponent.events.registerHandler('componentPositionChangedEvent', componentPositionChangedEventHandlerStartComponent);
	ec.events.registerHandler('eventChannelCompletedEvent', eventChannelCompletedEventHandler);
	ec.events.registerHandler('selectedEvent', selectedEventHandler);
	ec.events.registerHandler('deSelectedEvent', deSelectedEventHandler);

	return returnObj;
}