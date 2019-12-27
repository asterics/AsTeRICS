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

ACS.tabPanel = function (id, // String; the id of the div-container holding the tabPanel
	tab, // String; class of the elements that are supposed to become tabs
	panel) { // String; class of the elements that are supposed to become panels

	// ***********************************************************************************************************************
	// ************************************************** private variables **************************************************
	// ***********************************************************************************************************************
	var $panel = $('#' + id); // the jQuery object for the panel
	var $tabs = $panel.find('.' + tab); // Array of panel tabs.
	var $panels = $panel.children('.' + panel); // Array of panels.

	// ***********************************************************************************************************************
	// ************************************************** private methods ****************************************************
	// ***********************************************************************************************************************
	var initPanel = function () {
		var $tab; // the selected tab

		// add aria-hidden attribute and displayNone-class to the panels
		$panels.attr('aria-hidden', 'true').addClass('displayNone');
		// get the selected tab
		$tab = $tabs.filter('[aria-selected="true"]');
		// set to first tab, in case none is selected yet
		if ($tab === undefined) {
			$tab = $tabs.first();
		}
		// make the tab navigable
		$tab.attr('tabindex', '0');

		// show the panel that the selected tab controls, set aria-hidden to false and eliminate class displayNone
		$panel.find('#' + $tab.attr('aria-controls')).attr('aria-hidden', 'false').removeClass('displayNone');
	}

	var switchTabs = function ($curTab, // jQuery object of the currently selected tab
		$newTab) { // jQuery object of new tab to switch to
		$curTab.removeClass('focus');
		// remove tab from the tab order and update its aria-selected attribute
		$curTab.attr('tabindex', '-1').attr('aria-selected', 'false');
		// Highlight the new tab and update its aria-selected attribute
		$newTab.attr('aria-selected', 'true');
		// swap displayed tabs:
		// hide the current tab panel
		$panel.find('#' + $curTab.attr('aria-controls')).attr('aria-hidden', 'true').addClass('displayNone');
		// show the new tab panel
		$panel.find('#' + $newTab.attr('aria-controls')).attr('aria-hidden', 'false').removeClass('displayNone');

		// Make new tab navigable
		$newTab.attr('tabindex', '0');
		// give the new tab focus
		$newTab.focus();
		// tell others that a new tab is focussed
		returnObj.events.fireEvent('tabSwitchedEvent');
	}

	var bindEventHandlers = function () {
		// Bind handlers for the tabs:
		$tabs.keydown(function (e) {
			return handleTabKeyDown($(this), e);
		});

		$tabs.keypress(function (e) {
			return handleTabKeyPress($(this), e);
		});

		$tabs.click(function (e) {
			return handleTabClick($(this), e);
		});

		$tabs.focus(function (e) {
			return handleTabFocus($(this), e);
		});

		$tabs.blur(function (e) {
			return handleTabBlur($(this), e);
		});

		// Bind handlers for the panels:
		$panels.keydown(function (e) {
			return handlePanelKeyDown($(this), e);
		});

		$panels.keypress(function (e) {
			return handlePanelKeyPress($(this), e);
		});
	}

	var handleTabKeyDown = function ($tab, // jquery object of the tab being processed
		e) { // the associated event object
		// returns true if propagating; false if consuming event
		if (e.altKey) {
			// do nothing
			return true;
		}

		switch (e.keyCode) {
		case ACS.tabPanelKeyCodes.ENTER:
		case ACS.tabPanelKeyCodes.SPACE:
			return true;

		case ACS.tabPanelKeyCodes.LEFT:
		case ACS.tabPanelKeyCodes.UP: {
				var $newTab;

				if (e.ctrlKey) {
					// Ctrl+arrow moves focus from panel content to the open tab header
				} else {
					var curIdx = $tabs.index($tab);
					var counter = 0;
					do {
						if (curIdx === 0) {
							// tab is the first one - set newTab to last tab
							$newTab = $tabs.last();
							curIdx = $tabs.length - 1;
						} else {
							// set newTab to previous
							$newTab = $tabs.eq(curIdx - 1);
							curIdx--;
						}
						counter++;
					} while ($newTab[0].classList.contains("displayNone") && counter < $tabs.length);
					switchTabs($tab, $newTab);
				}
				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.RIGHT:
		case ACS.tabPanelKeyCodes.DOWN: {
				var $newTab;
				var curIdx = $tabs.index($tab);
				var counter = 0;
				do {
					if (curIdx === $tabs.length - 1) {
						// tab is the last one - set newTab to first tab
						$newTab = $tabs.first();
						curIdx = 0;
					} else {
						// set newTab to next tab
						$newTab = $tabs.eq(curIdx + 1);
						curIdx++;
					}
					counter++;
				} while ($newTab[0].classList.contains("displayNone") && counter < $tabs.length);
				switchTabs($tab, $newTab);

				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.POS1: {
				// switch to the first tab
				switchTabs($tab, $tabs.first());
				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.END: {
				// switch to the last tab
				switchTabs($tab, $tabs.last());
				e.stopPropagation();
				return false;
			}
		}
	}

	var handleTabKeyPress = function ($tab, // jquery object of the tab being processed
		e) { // the associated event object
		// returns true if propagating; false if consuming event

		if (e.altKey) {
			// do nothing
			return true;
		}

		switch (e.keyCode) {
		case ACS.tabPanelKeyCodes.ENTER:
		case ACS.tabPanelKeyCodes.SPACE:
		case ACS.tabPanelKeyCodes.LEFT:
		case ACS.tabPanelKeyCodes.UP:
		case ACS.tabPanelKeyCodes.RIGHT:
		case ACS.tabPanelKeyCodes.DOWN:
		case ACS.tabPanelKeyCodes.POS1:
		case ACS.tabPanelKeyCodes.END: {
				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.PGUP:
		case ACS.tabPanelKeyCodes.PGDOWN: {

				// The tab keypress handler must consume pageup and pagedown
				// keypresses to prevent Firefox from switching tabs
				// on ctrl+pageup and ctrl+pagedown

				if (!e.ctrlKey) {
					return true;
				}

				e.stopPropagation();
				return false;
			}
		}

		return true;

	}

	var handleTabClick = function ($tab, // jQuery object of the tab being processed
		e) { // the associated event object
		// hide the panels
		$panels.attr('aria-hidden', 'true').addClass('displayNone');
		// remove all tabs from the tab order and reset their aria-selected attribute
		$tabs.attr('tabindex', '-1').attr('aria-selected', 'false');
		// Update the selected tab's aria-selected attribute
		$tab.attr('aria-selected', 'true');
		// show the clicked tab panel
		$panel.find('#' + $tab.attr('aria-controls')).attr('aria-hidden', 'false').removeClass('displayNone');
		// make clicked tab navigable
		$tab.attr('tabindex', '0');
		// give the tab focus
		$tab.focus();
		// tell others that a new tab is focussed
		returnObj.events.fireEvent('tabSwitchedEvent');

		return true;
	}

	var handleTabFocus = function ($tab, // jQuery object of the tab being processed
		e) { // the associated event object
		// Add the focus class to the tab
		$tab.addClass('focus');

		return true;
	}

	var handleTabBlur = function ($tab, // jQuery object of the tab being processed
		e) { // the associated event object
		// Remove the focus class to the tab
		$tab.removeClass('focus');

		return true;
	}

	var handlePanelKeyDown = function ($elem, // jquery object of the element being processed
		e) { // the associated event object
		// returns true if propagating; false if consuming event
		if (e.altKey) {
			// do nothing
			return true;
		}

		switch (e.keyCode) {
		case ACS.tabPanelKeyCodes.ESC: {
				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.LEFT:
		case ACS.tabPanelKeyCodes.UP: {
				if (!e.ctrlKey) {
					// do not process
					return true;
				}
				// get the jQuery object of the tab
				var $tab = $('#' + $elem.attr('aria-labelledby'));
				// Move focus to the tab
				$tab.focus();

				e.stopPropagation();
				return false;
			}
		case ACS.tabPanelKeyCodes.PGUP: {
				var $newTab;

				if (!e.ctrlKey) {
					// do not process
					return true;
				}

				// get the jQuery object of the tab
				var $tab = $tabs.filter('[aria-selected="true"]');
				// get the index of the tab in the tab list
				var curNdx = $tabs.index($tab);
				if (curNdx === 0) {
					// this is the first tab, set focus on the last one
					$newTab = $tabs.last();
				} else {
					// set focus on the previous tab
					$newTab = $tabs.eq(curNdx - 1);
				}
				// switch to the new tab
				switchTabs($tab, $newTab);

				e.stopPropagation();
				e.preventDefault();
				return false;
			}
		case ACS.tabPanelKeyCodes.PGDOWN: {
				var $newTab;

				if (!e.ctrlKey) {
					// do not process
					return true;
				}
				// get the jQuery object of the tab
				var $tab = $('#' + $elem.attr('aria-labelledby'));
				// get the index of the tab in the tab list
				var curNdx = $tabs.index($tab);
				if (curNdx === $tabs.length - 1) {
					// this is the last tab, set focus on the first one
					$newTab = $tabs.first();
				} else {
					// set focus on the next tab
					$newTab = $tabs.eq(curNdx + 1);
				}
				// switch to the new tab
				switchTabs($tab, $newTab);

				e.stopPropagation();
				e.preventDefault();
				return false;
			}
		}

		return true;
	}

	var handlePanelKeyPress = function ($elem, // jquery object of the element being processed
		e) { // the associated event object
		// returns true if propagating; false if consuming event
		if (e.altKey) {
			// do nothing
			return true;
		}

		if (e.ctrlKey && (e.keyCode == ACS.tabPanelKeyCodes.PGUP || e.keyCode == ACS.tabPanelKeyCodes.PGDOWN)) {
			e.stopPropagation();
			e.preventDefault();
			return false;
		}
		switch (e.keyCode) {
		case ACS.tabPanelKeyCodes.ESC: {
				e.stopPropagation();
				e.preventDefault();
				return false;
			}
		}

		return true;
	}

	// ***********************************************************************************************************************
	// ************************************************** public stuff *******************************************************
	// ***********************************************************************************************************************
	var returnObj = {};

	returnObj.events = ACS.eventManager();

	returnObj.updatePanel = function () {
		$panel = $('#' + id); // the jQuery object for the panel
		$tabs = $panel.find('.' + tab); // Array of panel tabs.
		$panels = $panel.children('.' + panel); // Array of panels.
		bindEventHandlers();
		initPanel();
	}

	// ***********************************************************************************************************************
	// *********************************************** constructor code ******************************************************
	// ***********************************************************************************************************************
	bindEventHandlers();
	initPanel();

	return returnObj;
}
