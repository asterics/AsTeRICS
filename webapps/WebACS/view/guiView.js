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
 
 ACS.guiView = function(model, // ACS.model
						modelContainerId, // String
						editorProperties) { // ACS.editorProperties
						
// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var guiStage; // Kinetic.Stage
	var guiLayer; // Kinetic.Layer
	var areGUI; // guiViewElement
	var componentGUIs = []; // Array<Object {component, guiViewElement}>
	var gridLines = []; // Array<Kinetic.Line>
	var img = null; // Image
	var guiDesignerFrame; // Kinetic.Rect
	var guiKeyboardMode = false; // boolean
	
// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var addComponent = function(comp, isNewComponent) {
		if (comp.gui) {
			if (isNewComponent && model.modelGui.getDecoration()) { // if decoration enabled, position componentGUI below
				comp.gui.setNewPosition({x: comp.gui.getX(), y: comp.gui.getY() + ACS.vConst.GUIVIEW_DECORATIONHEIGHT});
			}
			if (comp.gui.getIsExternal()) { // external components can be dragged and resized on the whole canvas
				componentGUIs.push({component: comp,
									guiViewElement: ACS.guiViewElement(	model,
																		comp.gui,
																		guiLayer,
																		{width: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X - comp.gui.getX(), height: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y - comp.gui.getY()},
																		{left: 0, right: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X, upper: 0, lower: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y},
																		comp.getId(),
																		ACS.vConst.GUIVIEW_EXTERNALCOMPONENTGUIBACKGROUND,
																		editorProperties)});			
			} else { // internal components can be dragged and resized only within the areGUI
				componentGUIs.push({component: comp,
									guiViewElement: ACS.guiViewElement(	model,
																		comp.gui,
																		guiLayer,
																		{width: areGUI.getWidth() - (comp.gui.getX() - areGUI.getX()), height: areGUI.getHeight() - (comp.gui.getY() - areGUI.getY())},
																		{left: areGUI.getX(), right: areGUI.getX() + areGUI.getWidth(), upper: areGUI.getY(), lower: areGUI.getY() + areGUI.getHeight()},
																		comp.getId(),
																		ACS.vConst.GUIVIEW_COMPONENTGUIBACKGROUND,
																		editorProperties)});
				componentGUIs[componentGUIs.length - 1].guiViewElement.setParent(areGUI);
				areGUI.addChildElement(componentGUIs[componentGUIs.length - 1].guiViewElement);
				// add handler for changing componentID
				componentGUIs[componentGUIs.length - 1].component.events.registerHandler('componentIdChangedEvent', function(compGui) {
					return function() {
						compGui.guiViewElement.setName(compGui.component.getId());
					};
				}(componentGUIs[componentGUIs.length - 1]));
			}
		}
	}

	var makeGrid = function() {
		var steps;
		switch (editorProperties.getGridSteps()) {
			case ACS.gridStepType.SMALL: steps = ACS.vConst.GUIVIEW_GRIDSTEPS_SMALL;
										 break;
			case ACS.gridStepType.MEDIUM: steps = ACS.vConst.GUIVIEW_GRIDSTEPS_MEDIUM;
										  break;
			case ACS.gridStepType.LARGE: steps = ACS.vConst.GUIVIEW_GRIDSTEPS_LARGE;
										 break;
			case ACS.gridStepType.HUGE: steps = ACS.vConst.GUIVIEW_GRIDSTEPS_HUGE;
										break;											 
		}
		// draw vertical lines
		var linePos = steps;
		while (linePos < editorProperties.getGuiDesignerSize().width) {
			gridLines.push(new Kinetic.Line({
				x: linePos,
				y: 0,
				points: [0, 1, 0, editorProperties.getGuiDesignerSize().height - 2],
				stroke: ACS.vConst.GUIVIEW_GRIDLINECOLOR,
				strokeWidth: 1
			}));
			linePos = linePos + steps;
		}
		// draw horizontal lines
		var linePos = steps;
		while (linePos < editorProperties.getGuiDesignerSize().height) {
			gridLines.push(new Kinetic.Line({
				x: 0,
				y: linePos,
				points: [1, 0, editorProperties.getGuiDesignerSize().width - 2, 0],
				stroke: ACS.vConst.GUIVIEW_GRIDLINECOLOR,
				strokeWidth: 1
			}));
			linePos = linePos + steps;
		}
		// add the lines to the layer
		for (var i = 0; i < gridLines.length; i++) {
			guiLayer.add(gridLines[i]);
			gridLines[i].moveToBottom();
		}
	}
	
	var showGrid = function(show) {
		for (var i = 0; i < gridLines.length; i++) {
			gridLines[i].visible(show);
		}
	}
	
	var killGrid = function() {
		for (var i = 0; i < gridLines.length; i++) {
			gridLines[i].destroy();
		}
		gridLines = [];
	}
	
	var switchGrid = function(on) {
		if (on) {
			if (gridLines.length > 0) {
				showGrid(true);
			} else {
				makeGrid();
			}
			guiLayer.draw();
		} else {
			showGrid(false);
			guiLayer.draw();
		}
	}
	
	var getFocussedGUI = function() {
		if (areGUI.getFocus()) {
			return areGUI;
		} else {
			for (var i = 0; i < componentGUIs.length; i++) {
				if (componentGUIs[i].guiViewElement.getFocus()) return componentGUIs[i].guiViewElement;
			}
		}
		return null;
	}
	
	var getCloserGUI = function(closestGUI, otherGUI, centerOfFocussedGUI, centerOfOtherGUI) {
		var centerOfClosestGUI = {x: closestGUI.getX() + (closestGUI.getWidth() / 2),
								  y: closestGUI.getY() + (closestGUI.getHeight() / 2)};
		var distToClosest = Math.sqrt(Math.pow(centerOfClosestGUI.x - centerOfFocussedGUI.x, 2) + Math.pow(centerOfClosestGUI.y - centerOfFocussedGUI.y, 2));								
		var distToOther = Math.sqrt(Math.pow(centerOfOtherGUI.x - centerOfFocussedGUI.x, 2) + Math.pow(centerOfOtherGUI.y - centerOfFocussedGUI.y, 2));
		if ((distToClosest === 0) || (distToOther < distToClosest)) {
			return otherGUI;
		} else {
			return closestGUI;
		}
	}	
	
	// ********************************************** handlers ***********************************************************
	var componentAddedEventHandler = function() {
		if (model.componentList.length > 0) {
			addComponent(model.componentList[model.componentList.length - 1], true);
			guiLayer.draw();
		}
	}
	
	var componentRemovedEventHandler = function() {
		var i = 0;
		for (var i = 0; i < componentGUIs.length; i++) {
			var found = false;
			for (var j = 0; j < model.componentList.length; j++) {
				if (componentGUIs[i].component === model.componentList[j]) {
					found = true;
				}
			}
			if (!found) {
				componentGUIs[i].guiViewElement.destroy();
				componentGUIs.splice(i, 1);
				guiLayer.draw();
			}
		}
	}
	
	var modelChangedEventHandler = function() {
		// destroy all old components
		while (componentGUIs.length > 0) {
			componentGUIs.pop().guiViewElement.destroy();
		}
		// set decoration and controls
		areGUI.setDecoration(model.modelGui.getDecoration());
		areGUI.setAREControls(model.modelGui.getShowControlPanel());
		areGUI.reCalcSizeBoundsMin();
		// add new components
		for (var i = 0; i < model.componentList.length; i++) {
			addComponent(model.componentList[i], false);
		}
		guiLayer.draw();
	}
	
	var showGridChangedEventHandler = function() {
		switchGrid(editorProperties.getShowGrid());
	}
	
	var gridStepsChangedEventHandler = function() {
		killGrid();
		makeGrid();
		switchGrid(editorProperties.getShowGrid());
		guiLayer.draw();
	}
	
	var screenResChangedEventHandler = function() {
		guiStage.width(editorProperties.getGuiDesignerSize().width);
		guiStage.height(editorProperties.getGuiDesignerSize().height);
		guiLayer.width(editorProperties.getGuiDesignerSize().width);
		guiLayer.height(editorProperties.getGuiDesignerSize().height);
		guiDesignerFrame.width(editorProperties.getGuiDesignerSize().width);
		guiDesignerFrame.height(editorProperties.getGuiDesignerSize().height);
		killGrid();
		makeGrid();
		switchGrid(editorProperties.getShowGrid());
		guiLayer.draw();		
		areGUI.setSizeBoundsMax({width: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X - model.modelGui.areGuiWindow.getX(), height: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y - model.modelGui.areGuiWindow.getY()});
		areGUI.setDragBounds({left: 0, right: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X, upper: 0, lower: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y});
		for (var i = 0; i < componentGUIs.length; i++) {
			if (componentGUIs[i].guiViewElement.getGui().getIsExternal()) {
				componentGUIs[i].guiViewElement.setSizeBoundsMax({width: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X - componentGUIs[i].guiViewElement.getX(), height: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y - componentGUIs[i].guiViewElement.getY()});
				componentGUIs[i].guiViewElement.setDragBounds({left: 0, right: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X, upper: 0, lower: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y});
			}
		}
	}
	
	var decorationChangedEventHandler = function() {
		areGUI.setDecoration(model.modelGui.getDecoration());
	}

	var showControlPanelChangedEventHandler = function() {
		areGUI.setAREControls(model.modelGui.getShowControlPanel());
		areGUI.reCalcSizeBoundsMin();
	}	
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.setGuiKeyboardMode = function(newMode) {
		if (newMode) {
			// first set focus on areGUI
			areGUI.setFocus(true);
		} else {
			// unfocus all
			if (areGUI.getFocus()) areGUI.setFocus(false);
			for (var i = 0; i < componentGUIs.length; i++) {
				if (componentGUIs[i].guiViewElement.getFocus()) componentGUIs[i].guiViewElement.setFocus(false);
			}
		}
		guiKeyboardMode = newMode;
	}
	
	returnObj.focusNextGuiElement = function(direction) {
		log.debug('focussing next guiView: ' + direction);
		var focussedGUI = getFocussedGUI();
		if ((componentGUIs.length > 0) && focussedGUI) {
			var centerOfFocussedGUI = {x: focussedGUI.getX() + (focussedGUI.getWidth() / 2),
									   y: focussedGUI.getY() + (focussedGUI.getHeight() / 2)};
			var n1 = centerOfFocussedGUI.y - centerOfFocussedGUI.x;
			var n2 = centerOfFocussedGUI.y + centerOfFocussedGUI.x;
			var closestGUI = focussedGUI;
			var otherGUI = areGUI;
			var centerOfOtherGUI = {x: areGUI.getX() + (areGUI.getWidth() / 2),
									y: areGUI.getY() + (areGUI.getHeight() / 2)};
			for (var i = 0; i < componentGUIs.length + 1; i++) {
				if ((direction === 'up' && centerOfOtherGUI.y < centerOfOtherGUI.x + n1 && centerOfOtherGUI.y < -centerOfOtherGUI.x + n2) ||
					(direction === 'right' && centerOfOtherGUI.y < centerOfOtherGUI.x + n1 && centerOfOtherGUI.y > -centerOfOtherGUI.x + n2) ||
					(direction === 'down' && centerOfOtherGUI.y > centerOfOtherGUI.x + n1 && centerOfOtherGUI.y > -centerOfOtherGUI.x + n2) ||
					(direction === 'left' && centerOfOtherGUI.y > centerOfOtherGUI.x + n1 && centerOfOtherGUI.y < -centerOfOtherGUI.x + n2)) {
					closestGUI = getCloserGUI(closestGUI, otherGUI, centerOfFocussedGUI, centerOfOtherGUI);
				}
				if (i < componentGUIs.length) {
					otherGUI = componentGUIs[i].guiViewElement;
					centerOfOtherGUI = {x: otherGUI.getX() + (otherGUI.getWidth() / 2),
										y: otherGUI.getY() + (otherGUI.getHeight() / 2)};				
				}
			}
			focussedGUI.setFocus(false);
			closestGUI.setFocus(true);
		}
	}
	
	returnObj.resizeFocussedGuiElement = function(direction) {
		log.debug('resizing focussed GuiElement: ' + direction);
		var focussedGUI = getFocussedGUI();
		if (focussedGUI) focussedGUI.resize(direction);
	}
	
	returnObj.moveFocussedElement = function(direction) {
		log.debug('repositioning focussed GuiElement: ' + direction);
		var focussedGUI = getFocussedGUI();
		if (focussedGUI) focussedGUI.reposition(direction);
	}
	
	returnObj.destroy = function() {
		while (componentGUIs.length > 0) {
			componentGUIs.pop().guiViewElement.destroy();
		}
		areGUI.destroy();
		areGUI = null;
		guiLayer.destroyChildren();
		guiLayer.destroy();
		guiLayer = null;
		guiStage.destroyChildren();
		guiStage.destroy();
		guiStage = null;
	}	
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	guiStage = new Kinetic.Stage({
		container: modelContainerId,
		width: editorProperties.getGuiDesignerSize().width,
		height: editorProperties.getGuiDesignerSize().height
	});
	guiLayer = new Kinetic.Layer({
		width: editorProperties.getGuiDesignerSize().width,
		height: editorProperties.getGuiDesignerSize().height
	});
	guiStage.add(guiLayer);
	
	guiDesignerFrame = new Kinetic.Rect({x: 0,
										 y: 0, 
										 width: editorProperties.getGuiDesignerSize().width,
										 height: editorProperties.getGuiDesignerSize().height,
										 stroke: 'black'});
	guiLayer.add(guiDesignerFrame);
	
	switchGrid(editorProperties.getShowGrid());
	
	areGUI = ACS.guiViewElement(model,
								model.modelGui.areGuiWindow,
								guiLayer, 
								{width: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X - model.modelGui.areGuiWindow.getX(), height: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y - model.modelGui.areGuiWindow.getY()},
								{left: 0, right: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X, upper: 0, lower: ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y},
								ACS.vConst.GUIVIEW_AREWINDOWNAME,
								ACS.vConst.GUIVIEW_AREWINDOWBACKGROUND,
								editorProperties);
	areGUI.setDecoration(model.modelGui.getDecoration());
	if (model.modelGui.getShowControlPanel()) areGUI.setAREControls(true);
	for (var i = 0; i < model.componentList.length; i++) {
		addComponent(model.componentList[i], false);
	}
	guiLayer.draw();
	
	model.events.registerHandler('componentAddedEvent', componentAddedEventHandler);
	model.events.registerHandler('componentRemovedEvent', componentRemovedEventHandler);
	model.events.registerHandler('modelChangedEvent', modelChangedEventHandler);
	editorProperties.events.registerHandler('showGridChangedEvent', showGridChangedEventHandler);
	editorProperties.events.registerHandler('gridStepsChangedEvent', gridStepsChangedEventHandler);
	editorProperties.events.registerHandler('screenResChangedEvent', screenResChangedEventHandler);
	model.modelGui.events.registerHandler('decorationChangedEvent', decorationChangedEventHandler);
	model.modelGui.events.registerHandler('showControlPanelChangedEvent', showControlPanelChangedEventHandler);
	
	return returnObj;
}