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
 
 ACS.guiViewElement = function(	model, // ACS.model
								gui, // ACS.gui
								guiLayer, // Kinetic.Layer
								sizeBoundsMaxIn, // Object {width, height}
								dragBoundsIn, // Object {left, right, upper, lower}
								name, // String
								backgroundColor, // String
								editorProperties) { // ACS.editorProperties
						
// ***********************************************************************************************************************
// ************************************************** private variables **************************************************
// ***********************************************************************************************************************
	var sizeBoundsMin = {width: 30, height: 30};
	var sizeBoundsMax;
	var dragBounds;
	var children = []; // Array<guiViewElement>
	var parent = null; // guiViewElement
	var mainRect = null;
	var nameText = null;
	var anchor = null;
	var group = null;
	var controls = null;
	var decoration = null;
	var decorHeight;
	var controlWidth;
	var resizeAct = null; // ACS.guiResizeAction
	var dragAct = null; // ACS.guiDragDropAction
	var dragOffsetX = null;
	var dragOffsetY = null;
	var hasFocus = false;

// ***********************************************************************************************************************
// ************************************************** private methods ****************************************************
// ***********************************************************************************************************************
	var stopEvent = function(e) {
		if (e.stopPropagation) {
			e.stopPropagation();
		} else {
			e.cancelBubble = true;
		}
		if (e.preventDefault) e.preventDefault();
	}
	
	var xToScreenRes = function(x) { // takes a normalised x-coordinate and returns that coordinate mapped to the currently set resolution of the gui designer
		return x / ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X * editorProperties.getGuiDesignerSize().width;
	}
	
	var yToScreenRes = function(y) { // takes a normalised y-coordinate and returns that coordinate mapped to the currently set resolution of the gui designer
		return y / ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y * editorProperties.getGuiDesignerSize().height;
	}
	
	var xToNormRes = function(x) { // takes an x-coordinate and returns that coordinate mapped to the normalised resolution that is used for storing coordinates
		return x / editorProperties.getGuiDesignerSize().width * ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_X;
	}
	
	var yToNormRes = function(y) { // takes an y-coordinate and returns that coordinate mapped to the normalised resolution that is used for storing coordinates
		return y / editorProperties.getGuiDesignerSize().height * ACS.vConst.GUIVIEWELEMENT_NORMSCREENRES_Y;
	}	

	var setNewSize = function(newWidth, newHeight) {
		mainRect.size({width: newWidth, height: newHeight});
		nameText.width(newWidth);
		nameText.setAbsolutePosition({x: nameText.getAbsolutePosition().x,
									  y: mainRect.getAbsolutePosition().y + newHeight/2 - 5});
		anchor.setAbsolutePosition({x: mainRect.getAbsolutePosition().x + newWidth,
									y: mainRect.getAbsolutePosition().y + newHeight});	
		if (decoration) decoration.width(newWidth - 2);
		if (controls) {
			var childNodes = controls.getChildren();
			childNodes[0].height(newHeight);
			childNodes[0].setAbsolutePosition({x: mainRect.getAbsolutePosition().x + newWidth - controlWidth,
											   y: childNodes[0].getAbsolutePosition().y});
			childNodes[1].setAbsolutePosition({x: mainRect.getAbsolutePosition().x + newWidth - controlWidth,
											   y: mainRect.getAbsolutePosition().y + newHeight/2 - 5});
		}
		// set new dragBounds and new sizeBoundsMax for all children
		for (var i = 0; i < children.length; i++) {
			children[i].setDragBounds({left: xToNormRes(mainRect.getAbsolutePosition().x),
									   upper: yToNormRes(mainRect.getAbsolutePosition().y),
									   right: xToNormRes(mainRect.getAbsolutePosition().x + newWidth),
									   lower: yToNormRes(mainRect.getAbsolutePosition().y + newHeight)});
			children[i].setSizeBoundsMax({width: xToNormRes(newWidth - (xToScreenRes(children[i].getX()) - mainRect.getAbsolutePosition().x)),
										  height: yToNormRes(newHeight - (yToScreenRes(children[i].getY()) - mainRect.getAbsolutePosition().y))});
		}
		// recalc bounds for parent (if there is one) and own sizeBoundsMax
		if (parent) {
			parent.reCalcSizeBoundsMin();
			returnObj.setSizeBoundsMax({width: xToNormRes(xToScreenRes(parent.getWidth()) - (mainRect.getAbsolutePosition().x - xToScreenRes(parent.getX()))),
										height: yToNormRes(yToScreenRes(parent.getHeight()) - (mainRect.getAbsolutePosition().y - yToScreenRes(parent.getY())))});
		} else {
			returnObj.setSizeBoundsMax({width: xToNormRes(editorProperties.getGuiDesignerSize().width - mainRect.getAbsolutePosition().x), 
										height: yToNormRes(editorProperties.getGuiDesignerSize().height - mainRect.getAbsolutePosition().y)});
		}
		guiLayer.draw();
	}

	var updateSize = function() {
		var newWidth = anchor.getAbsolutePosition().x - mainRect.getAbsolutePosition().x;
		var newHeight = anchor.getAbsolutePosition().y - mainRect.getAbsolutePosition().y;
		if ((newWidth < sizeBoundsMin.width) || (newHeight < sizeBoundsMin.height) || (newWidth > sizeBoundsMax.width) || (newHeight > sizeBoundsMax.height)) {
			if (newWidth < sizeBoundsMin.width) newWidth = sizeBoundsMin.width;
			if (newHeight < sizeBoundsMin.height) newHeight = sizeBoundsMin.height;
			if (newWidth > sizeBoundsMax.width) newWidth = sizeBoundsMax.width;
			if (newHeight > sizeBoundsMax.height) newHeight = sizeBoundsMax.height;
			anchor.setAbsolutePosition({x: mainRect.getAbsolutePosition().x + newWidth,
									    y: mainRect.getAbsolutePosition().y + newHeight});
		}
		gui.setNewSize({width: Math.round(xToNormRes(newWidth)),
						height: Math.round(yToNormRes(newHeight))});
	};
	
	var getGridStep = function() {
		switch (editorProperties.getGridSteps()) {
			case ACS.gridStepType.SMALL: return ACS.vConst.GUIVIEW_GRIDSTEPS_SMALL;
										 break;
			case ACS.gridStepType.MEDIUM: return ACS.vConst.GUIVIEW_GRIDSTEPS_MEDIUM;
										  break;
			case ACS.gridStepType.LARGE: return ACS.vConst.GUIVIEW_GRIDSTEPS_LARGE;
										 break;
			case ACS.gridStepType.HUGE: return ACS.vConst.GUIVIEW_GRIDSTEPS_HUGE;
										break;											 
		}		
	}

	var checkMinBoundsInRelationToChild = function(childElem) { // guiViewElement
		if (controls && controls.isVisible()) {
			if (sizeBoundsMin.width < xToScreenRes(childElem.getX()) - mainRect.getAbsolutePosition().x + xToScreenRes(childElem.getWidth()) + controlWidth) sizeBoundsMin.width = xToScreenRes(childElem.getX()) - mainRect.getAbsolutePosition().x + xToScreenRes(childElem.getWidth()) + controlWidth;
		} else {
			if (sizeBoundsMin.width < xToScreenRes(childElem.getX()) - mainRect.getAbsolutePosition().x + xToScreenRes(childElem.getWidth())) sizeBoundsMin.width = xToScreenRes(childElem.getX()) - mainRect.getAbsolutePosition().x + xToScreenRes(childElem.getWidth());
		}
		if (sizeBoundsMin.height < yToScreenRes(childElem.getY()) - mainRect.getAbsolutePosition().y + yToScreenRes(childElem.getHeight())) sizeBoundsMin.height = yToScreenRes(childElem.getY()) - mainRect.getAbsolutePosition().y + yToScreenRes(childElem.getHeight());
	}

	var updatePosAccordingToGrid = function(newPos) {
		// if grid enabled, make newPos stick to grid
		if (editorProperties.getEnableGrid()) {
			var step = getGridStep();
			if (newPos.x % step < step/2) {
				newPos.x = newPos.x - (newPos.x % step);
			} else {
				newPos.x = newPos.x - (newPos.x % step) + step;
			}
			if (newPos.y % step < step/2) {
				newPos.y = newPos.y - (newPos.y % step);
			} else {
				newPos.y = newPos.y - (newPos.y % step) + step;
			}
		}		
		return newPos;
	}
	
	var updateChildrensPosition = function() {
		for (var i = 0; i < children.length; i++) {
			children[i].updatePosition();
			children[i].setDragBounds({left: xToNormRes(mainRect.getAbsolutePosition().x),
									   upper: yToNormRes(mainRect.getAbsolutePosition().y),
									   right: xToNormRes(mainRect.getAbsolutePosition().x + mainRect.width()),
									   lower: yToNormRes(mainRect.getAbsolutePosition().y + mainRect.height())});		
		}		
	}
	
	var updateSizeBounds = function() {
		if (parent) {
			parent.reCalcSizeBoundsMin();
			returnObj.setSizeBoundsMax({width: xToNormRes(xToScreenRes(parent.getWidth()) - (mainRect.getAbsolutePosition().x - xToScreenRes(parent.getX()))),
										height: yToNormRes(yToScreenRes(parent.getHeight()) - (mainRect.getAbsolutePosition().y - yToScreenRes(parent.getY())))});
		} else {
			returnObj.setSizeBoundsMax({width: xToNormRes(editorProperties.getGuiDesignerSize().width - mainRect.getAbsolutePosition().x), 
										height: yToNormRes(editorProperties.getGuiDesignerSize().height - mainRect.getAbsolutePosition().y)});
		}		
	}
	
	// ********************************************** handlers ***********************************************************
	var guiPositionChangedEventHandler = function() {
		mainRect.setAbsolutePosition({x: xToScreenRes(gui.getX()),
									  y: yToScreenRes(gui.getY())});								  
		nameText.setAbsolutePosition({x: xToScreenRes(gui.getX()),
									  y: yToScreenRes(gui.getY()) + yToScreenRes(gui.getHeight())/2 - 5});								  
		anchor.setAbsolutePosition({x: xToScreenRes(gui.getX()) + xToScreenRes(gui.getWidth()),
									y: yToScreenRes(gui.getY()) + yToScreenRes(gui.getHeight())});
		if (decoration) {
			decoration.setAbsolutePosition({x: xToScreenRes(gui.getX()) + 1,
											y: yToScreenRes(gui.getY()) + 1});
		}	
		if (controls) {
			controls.getChildren()[0].setAbsolutePosition({x: xToScreenRes(gui.getX()) + xToScreenRes(gui.getWidth()) - controlWidth,
														   y: yToScreenRes(gui.getY())});
			controls.getChildren()[1].setAbsolutePosition({x: xToScreenRes(gui.getX()) + xToScreenRes(gui.getWidth()) - controlWidth,
														   y: yToScreenRes(gui.getY()) + yToScreenRes(gui.getHeight())/2 - 5});														   
		}
		guiLayer.draw();
	}
	
	var guiSizeChangedEventHandler = function() {
		setNewSize(xToScreenRes(gui.getWidth()), yToScreenRes(gui.getHeight()));
		guiLayer.draw();
	}
	
	var screenResChangedEventHandler = function() {
		guiPositionChangedEventHandler();
		setNewSize(xToScreenRes(gui.getWidth()), yToScreenRes(gui.getHeight()));
		guiLayer.draw();
	}
	
// ***********************************************************************************************************************
// ************************************************** public stuff *******************************************************
// ***********************************************************************************************************************
	var returnObj = {};
	
	returnObj.setDecoration = function(active) { // bool
		if (active) {
			if (decoration) {
				decoration.visible(true);
				guiLayer.draw();
			} else {
				var img = new Image(mainRect.width() - 2, decorHeight);
				img.addEventListener('load', function() {
					decoration = new Kinetic.Image({image: img,
													x: mainRect.getAbsolutePosition().x + 1,
													y: mainRect.getAbsolutePosition().y + 1,
													width: mainRect.width() - 2,
													height: decorHeight});
					group.add(decoration);
					decoration.moveToTop();
					guiLayer.draw();
				});
				img.src = ACS.vConst.GUIVIEWELEMENT_AREWINDOWDECOIMAGE;
			}
		} else {
			if (decoration) decoration.visible(false);
			guiLayer.draw();
		}
	}
	
	returnObj.setAREControls = function(active) { // bool
		if (controls) {
			controls.visible(active);
		} else {
			var controlRect = new Kinetic.Rect({x: mainRect.getAbsolutePosition().x + mainRect.width() - controlWidth,
												y: mainRect.getAbsolutePosition().y,
												width: controlWidth,
												height: mainRect.height(),
												fill: ACS.vConst.GUIVIEWELEMENT_CONTROLSCOLOR});
			var controlText = new Kinetic.Text({x: controlRect.getX(),
												y: controlRect.getY() + controlRect.height()/2 - 5,
												width: controlWidth,
												text: ACS.vConst.GUIVIEWELEMENT_CONTROLSNAME,
												fontSize: ACS.vConst.GUIVIEWELEMENT_CONTROLSFONTSIZE,
												wrap: 'char',
												align: 'center',
												fill: ACS.vConst.GUIVIEWELEMENT_NAMECOLOR});
			controls = new Kinetic.Group();
			controls.add(controlRect);
			controls.add(controlText);
			group.add(controls);
		}
		if (active) {
			if (decoration) decoration.moveToTop();
			anchor.moveToTop();
		}
		guiLayer.draw();
	}
	
	returnObj.setSizeBoundsMin = function(bounds) { // Object {width, height}
		sizeBoundsMin = {width: xToScreenRes(bounds.width),
						 height: yToScreenRes(bounds.height)};
		for (var i = 0; i < children.length; i++) {
			checkMinBoundsInRelationToChild(children[i]);
		}
	}
	
	returnObj.setSizeBoundsMax = function(bounds) { // Object {width, height}
		sizeBoundsMax = {width: xToScreenRes(bounds.width),
						 height: yToScreenRes(bounds.height)};
	}
	
	returnObj.setDragBounds = function(bounds) { // Object {left, right, upper, lower}
		dragBounds = {left: xToScreenRes(bounds.left),
					  right: xToScreenRes(bounds.right),
					  upper: yToScreenRes(bounds.upper),
					  lower: yToScreenRes(bounds.lower)};
	}
	
	returnObj.setName = function(newName) { // String
		name = newName;
		nameText.text(newName);
		guiLayer.draw();
		
	}
	
	returnObj.setParent = function(p) { // guiViewElement
		parent = p;
	}
	
	returnObj.setFocus = function(focus) { // boolean
		hasFocus = focus;
		if (focus) {
			mainRect.fill(ACS.vConst.GUIVIEWELEMENT_FOCUSCOLOR);
		} else {
			mainRect.fill(backgroundColor);
		}
		guiLayer.draw();
	}	
	
	returnObj.addChildElement = function(newChild) { // guiViewElement
		children.push(newChild);
		group.add(newChild.getKineticGroup());
		checkMinBoundsInRelationToChild(newChild);
		updateSize();
		guiLayer.draw();
	}	
	
	returnObj.reCalcSizeBoundsMin = function() {
		sizeBoundsMin = {width: 30, height: 30};
		for (var i = 0; i < children.length; i++) {
			checkMinBoundsInRelationToChild(children[i]);
		}
	}
	
	returnObj.updatePosition = function() {
		gui.setNewPosition({x: Math.round(xToNormRes(mainRect.getAbsolutePosition().x)),
							y: Math.round(yToNormRes(mainRect.getAbsolutePosition().y))});
	}
	
	returnObj.resize = function(direction) {
		resizeAct = ACS.guiResizeAction(model, gui);
		var moveStep = 1;
		if (editorProperties.getEnableGrid()) moveStep = getGridStep();
		switch (direction) {
			case 'up':		anchor.move({x: 0, y: -moveStep});
							break;
			case 'right':	anchor.move({x: moveStep, y: 0});
							break;
			case 'down':	anchor.move({x: 0, y: moveStep});
							break;
			case 'left':	anchor.move({x: -moveStep, y: 0});
							break;
		}
		var newPos = updatePosAccordingToGrid({x: anchor.getAbsolutePosition().x, y: anchor.getAbsolutePosition().y});
		// check the bounds
		
		// actually update size
		anchor.x(newPos.x);
		anchor.y(newPos.y);
		updateSize();
		resizeAct.execute();		
	}
	
	returnObj.reposition = function(direction) {
		var guis = [gui];
		for (var i = 0; i < children.length; i++) {
			guis.push(children[i].getGui());
		}
		dragAct = ACS.guiDragDropAction(model, guis);
		var moveStep = 1;
		if (editorProperties.getEnableGrid()) moveStep = getGridStep();
		// remember old coordinates to calculate actually moved pixels for positioning children
		var oldPos = {x: mainRect.getAbsolutePosition().x, y: mainRect.getAbsolutePosition().y};
		// move the element
		switch (direction) {
			case 'up':		mainRect.move({x: 0, y: -moveStep});
							break;
			case 'right':	mainRect.move({x: moveStep, y: 0});
							break;
			case 'down':	mainRect.move({x: 0, y: moveStep});
							break;
			case 'left':	mainRect.move({x: -moveStep, y: 0});
							break;
		}
		var newPos = updatePosAccordingToGrid({x: mainRect.getAbsolutePosition().x, y: mainRect.getAbsolutePosition().y});
		// check the bounds
		if (newPos.x < dragBounds.left) newPos.x = dragBounds.left;
		if (newPos.x + mainRect.width() > dragBounds.right) newPos.x = dragBounds.right - mainRect.width();
		if (newPos.y < dragBounds.upper) newPos.y = dragBounds.upper;
		if (newPos.y + mainRect.height() > dragBounds.lower) newPos.y = dragBounds.lower - mainRect.height();		
		// actually update the position
		mainRect.x(newPos.x);
		mainRect.y(newPos.y);
		// calculate actually moved pixels for positioning children
		var moved = {x: newPos.x - oldPos.x, y: newPos.y - oldPos.y};
		// reposition the children
		for (var i = 0; i < children.length; i++) {
			children[i].setX(children[i].getX() + xToNormRes(moved.x));
			children[i].setY(children[i].getY() + yToNormRes(moved.y));
		}		
		updateChildrensPosition();
		returnObj.updatePosition();
		updateSizeBounds();
		dragAct.execute();	
	}	
	
	returnObj.getFocus = function() {
		return hasFocus;
	}
	
	returnObj.getX = function() {
		return xToNormRes(mainRect.getAbsolutePosition().x);
	}

	returnObj.setX = function(newX) {
		mainRect.x(xToScreenRes(newX));
	}
	
	returnObj.getY = function() {
		return yToNormRes(mainRect.getAbsolutePosition().y);
	}
	
	returnObj.setY = function(newY) {
		mainRect.y(yToScreenRes(newY));
	}	

	returnObj.getWidth = function() {
		return xToNormRes(mainRect.width());
	}

	returnObj.getHeight = function() {
		return yToNormRes(mainRect.height());
	}
	
	returnObj.getKineticGroup = function() {
		return group;
	}
	
	returnObj.getGui = function() {
		return gui;
	}	
	
	returnObj.destroy = function() {
		if (group !== null) { // if null we can assume that the element has already been destroyed
			// remove all event handlers registered anywhere
			gui.events.removeHandler('guiPositionChangedEvent', guiPositionChangedEventHandler);
			gui.events.removeHandler('guiSizeChangedEvent', guiSizeChangedEventHandler);
			// destroy children and self
			for (var i = 0; i < children.length; i++) {
				children[i].destroy();
			}
			group.destroyChildren();
			group.destroy();
			group = null;
			mainRect = null;
			nameText = null;
			anchor = null;
			controls = null;
			decoration = null;
		}
	}
	
// ***********************************************************************************************************************
// ************************************************** constructor code ***************************************************
// ***********************************************************************************************************************
	sizeBoundsMax = {width: xToScreenRes(sizeBoundsMaxIn.width), height: yToScreenRes(sizeBoundsMaxIn.height)};
	dragBounds = {left: xToScreenRes(dragBoundsIn.left), right: xToScreenRes(dragBoundsIn.right), upper: yToScreenRes(dragBoundsIn.upper), lower: yToScreenRes(dragBoundsIn.lower)};

	decorHeight = Math.round(yToScreenRes(ACS.vConst.GUIVIEW_DECORATIONHEIGHT));
	controlWidth = Math.round(xToScreenRes(ACS.vConst.GUIVIEW_CONTROLSWIDTH));
	
	
	mainRect = new Kinetic.Rect({x: xToScreenRes(gui.getX()),
								 y: yToScreenRes(gui.getY()),
								 width: xToScreenRes(gui.getWidth()),
								 height: yToScreenRes(gui.getHeight()),
								 fill: backgroundColor});
	nameText = new Kinetic.Text({x: xToScreenRes(gui.getX()),
								 y: yToScreenRes(gui.getY()) + yToScreenRes(gui.getHeight())/2 - 5,
								 width: Math.round(xToScreenRes(gui.getWidth())),
								 text: name,
								 fontSize: ACS.vConst.GUIVIEWELEMENT_FONTSIZE,
								 wrap: 'char',
								 align: 'center',
								 fill: ACS.vConst.GUIVIEWELEMENT_NAMECOLOR});
	anchor = new Kinetic.Shape({x: xToScreenRes(gui.getX()) + xToScreenRes(gui.getWidth()),
								y: yToScreenRes(gui.getY()) + yToScreenRes(gui.getHeight()),
								fill: 'black',
								drawFunc: function(context) {
										context.beginPath();
										context.moveTo(0, 0);
										context.lineTo(0, -10);
										context.lineTo(-10, 0);
										context.lineTo(0, 0);
										context.closePath();
										context.fillStrokeShape(this);
								},
								draggable: true,
								dragBoundFunc: function(pos) {
										var newPos = {x: pos.x,
													  y: pos.y}
										newPos = updatePosAccordingToGrid(newPos);
										// check the bounds
										if (newPos.x - mainRect.getAbsolutePosition().x > sizeBoundsMax.width) newPos.x = sizeBoundsMax.width + mainRect.getAbsolutePosition().x;
										if (newPos.y - mainRect.getAbsolutePosition().y > sizeBoundsMax.height) newPos.y = sizeBoundsMax.height + mainRect.getAbsolutePosition().y;	
										return {x: newPos.x, y: newPos.y};
								}
								});
	group = new Kinetic.Group({	draggable: true,
								dragBoundFunc: function(pos) {
									if ((dragOffsetX === null) || (dragOffsetY === null)) {
										dragOffsetX = mainRect.getAbsolutePosition().x - pos.x;
										dragOffsetY = mainRect.getAbsolutePosition().y - pos.y;
									}
									var newPos = {x: pos.x,
												  y: pos.y};
									newPos = updatePosAccordingToGrid({x: newPos.x + dragOffsetX,
																	   y: newPos.y + dragOffsetY});
									newPos.x = newPos.x - dragOffsetX;
									newPos.y = newPos.y - dragOffsetY;
									// check the bounds
									actBounds = {left: (dragBounds.left - dragOffsetX), right: (dragBounds.right - dragOffsetX), upper: (dragBounds.upper - dragOffsetY), lower: (dragBounds.lower - dragOffsetY)};
									if (newPos.x < actBounds.left) newPos.x = actBounds.left;
									if (newPos.x + mainRect.width() > actBounds.right) newPos.x = actBounds.right - mainRect.width();
									if (newPos.y < actBounds.upper) newPos.y = actBounds.upper;
									if (newPos.y + mainRect.height() > actBounds.lower) newPos.y = actBounds.lower - mainRect.height();
									return {x: newPos.x, y: newPos.y};
								}
							 });

	group.on('dragstart', function(e) {
		var guis = [gui];
		for (var i = 0; i < children.length; i++) {
			guis.push(children[i].getGui());
		}
		dragAct = ACS.guiDragDropAction(model, guis);
	});
	
	group.on('dragend', function(e) {
		dragOffsetX = null;
		dragOffsetY = null;
		updateChildrensPosition();
		returnObj.updatePosition();
		updateSizeBounds();
		dragAct.execute();
	});
									
	anchor.on('mousedown', function(e) {
		group.setDraggable(false);
		this.moveToTop();
	});
	
	anchor.on('dragstart', function(e) {
		resizeAct = ACS.guiResizeAction(model, gui);
		stopEvent(e);
	});
	
	anchor.on('dragmove', function(e) {
		updateSize();
		guiLayer.draw();
	});
	
	anchor.on('dragend', function(e) {
		group.setDraggable(true);
		guiLayer.draw();
		resizeAct.execute();
		stopEvent(e);
	});
	
	anchor.on('mouseover', function(e) {
		document.body.style.cursor = 'nwse-resize';
		guiLayer.draw();
	});
	
	anchor.on('mouseout', function(e) {
		document.body.style.cursor = 'default';
		guiLayer.draw();
	});
	
	group.add(mainRect);
	group.add(nameText);
	group.add(anchor);
	
	guiLayer.add(group);
	guiLayer.draw();
	
	gui.events.registerHandler('guiPositionChangedEvent', guiPositionChangedEventHandler);
	gui.events.registerHandler('guiSizeChangedEvent', guiSizeChangedEventHandler);
	editorProperties.events.registerHandler('screenResChangedEvent', screenResChangedEventHandler);
	
	return returnObj;
}