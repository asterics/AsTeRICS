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

ACS.view = function(
  modelList, // ACS.modelList
  clipBoard
) {
  // ACS.clipBoard

  // ***********************************************************************************************************************
  // ************************************************** private variables **************************************************
  // ***********************************************************************************************************************
  var menu = ACS.menuView(modelList);
  var canvas = ACS.canvasView(modelList, clipBoard);
  var propertyEditor = ACS.propertyEditor(
    modelList,
    canvas.getCanvasModelViewList(),
    canvas.getEditorProperties()
  );
  var actModelView;

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
  };

  var catchArrowKey = function(e, direction) {
    if (e.shiftKey) {
      if (
        actModelView.getKeyboardMode() &&
        !actModelView.getPortMode() &&
        !actModelView.getChannelMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // move component
        actModelView.moveComponent(direction);
        return true;
      } else if (
        actModelView.getGuiKeyboardMode() &&
        $("#guiPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // move GUI
        actModelView.moveGuiElement(direction);
        return true;
      }
    } else if (e.altKey) {
      if (
        actModelView.getGuiKeyboardMode() &&
        $("#guiPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // resize GUI
        actModelView.resizeGuiElement(direction);
        return true;
      }
    } else {
      if (
        actModelView.getPortMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // switch ports
        actModelView.focusNextPort(direction);
        return true;
      } else if (
        actModelView.getListPortMode() &&
        $("#listPanel" + actModelView.getModelContainerId())
          .find("*")
          .is(":focus")
      ) {
        // switch ports in listview
        actModelView.focusNextListPort(direction);
        return true;
      } else if (
        actModelView.getChannelMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // switch channels
        actModelView.focusNextChannel(direction);
        return true;
      } else if (
        actModelView.getListChannelMode() &&
        $("#listPanel" + actModelView.getModelContainerId())
          .find("*")
          .is(":focus")
      ) {
        // switch channels in listview
        actModelView.focusNextListChannel(direction);
        return true;
      } else if (
        actModelView.getKeyboardMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // switch components
        actModelView.focusNextComponent(direction);
        return true;
      } else if (
        actModelView.getGuiKeyboardMode() &&
        $("#guiPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        // switch Gui Elements
        actModelView.focusNextGuiElement(direction);
        return true;
      } else if (
        actModelView.getListKeyboardMode() &&
        $("#listPanel" + actModelView.getModelContainerId())
          .find("*")
          .is(":focus")
      ) {
        // switch components in listview
        actModelView.focusNextListComponent(direction);
        return true;
      }
    }
    return false;
  };

  var dropStartedChannel = function() {
    var actModel = modelList.getActModel();
    if (
      (actModel.dataChannelList.length > 0 &&
        !actModel.dataChannelList[
          actModel.dataChannelList.length - 1
        ].getInputPort()) || // unfinished dataChannel to be dropped
      (actModel.eventChannelList.length > 0 &&
        !actModel.eventChannelList[actModel.eventChannelList.length - 1]
          .endComponent)
    ) {
      // unfinished eventChannel to be dropped
      var ch = actModel.undoStack.pop();
      ch.undo();
      return true;
    }
    return false;
  };

  // ********************************************** handlers ***********************************************************
  var handleMenuShortcutClick = function() {
    var tablist = document
      .getElementById("mainMenuTablist")
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        tablist.item(i).focus();
      }
    }
  };

  var handleModelPanelShortcutClick = function() {
    var tablist = document
      .getElementById(ACS.vConst.CANVASVIEW_TABLIST)
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        tablist.item(i).focus();
      }
    }
  };

  var handleModelDesignerShortcutClick = function() {
    var tablist = document
      .getElementById(ACS.vConst.CANVASVIEW_TABLIST)
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        var panelId = tablist.item(i).attributes.getNamedItem("id").value;
        panelId = panelId.slice(9, panelId.length); // get rid of the word "canvasTab"
        var tab = document.getElementById("modelTabcanvasPanel" + panelId);
        if (tab) tab.click();
      }
    }
  };

  var handleGuiDesignerShortcutClick = function() {
    var tablist = document
      .getElementById(ACS.vConst.CANVASVIEW_TABLIST)
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        var panelId = tablist.item(i).attributes.getNamedItem("id").value;
        panelId = panelId.slice(9, panelId.length); // get rid of the word "canvasTab"
        var tab = document.getElementById("guiTabcanvasPanel" + panelId);
        if (tab) tab.click();
      }
    }
  };

  var handleListViewShortcutClick = function() {
    var tablist = document
      .getElementById(ACS.vConst.CANVASVIEW_TABLIST)
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        var panelId = tablist.item(i).attributes.getNamedItem("id").value;
        panelId = panelId.slice(9, panelId.length); // get rid of the word "canvasTab"
        var tab = document.getElementById("listTabcanvasPanel" + panelId);
        if (tab) tab.click();
      }
    }
  };

  var handlePropertyEditorShortcutClick = function() {
    var tablist = document
      .getElementById("propertyEditorTabList")
      .getElementsByClassName("tab");
    for (var i = 0; i < tablist.length; i++) {
      if (
        tablist.item(i).attributes.getNamedItem("aria-selected").value ===
        "true"
      ) {
        tablist.item(i).focus();
      }
    }
  };

  var handleKeydown = function(e) {
    if (e.repeat) {
      return;
    }
    // catch Del to delete selected items
    var keyCode = e.keyCode || e.which;
    var ctrlOrMeta = e.ctrlKey || e.metaKey; //metaKey is macOS Mac key
    if (keyCode === 46) {
      // Del can't be caught by keyPress for not all browsers act consistently (see: http://unixpapa.com/js/key.html)
      if (
        !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
          .find("*")
          .is(":focus")
      ) {
        // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
        deleteSelectionHandler();
        stopEvent(e);
        return false;
      }
    }
    if (e.key === "F1") {
      helpHandler();
      stopEvent(e);
      return false;
    }

    switch (keyCode) {
      case 48: // Ctrl-0 for menu
        if (ctrlOrMeta) {
          handleMenuShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 49: // Ctrl-1 for model panel (file)
        if (ctrlOrMeta) {
          handleModelPanelShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 50: // Ctrl-2 for model designer
        if (ctrlOrMeta) {
          handleModelDesignerShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 51: // Ctrl-3 for gui designer
        if (ctrlOrMeta) {
          handleGuiDesignerShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 52: // CTRL-4 for list view
        if (ctrlOrMeta) {
          handleListViewShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 53: // Ctrl-5 for property editor
        if (ctrlOrMeta) {
          handlePropertyEditorShortcutClick();
          stopEvent(e);
          return false;
        }
        break;
      case 88: // Ctrl-x for cut
        if (ctrlOrMeta) {
          if (
              !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
                  .find("*")
                  .is(":focus")
          ) {
            // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
            cutHandler();
            stopEvent(e);
            return false;
          }
        }
        break;
      case 67: // Ctrl-c for copy
        if (ctrlOrMeta) {
          if (
              !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
                  .find("*")
                  .is(":focus")
          ) {
            // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
            copyHandler();
            stopEvent(e);
            return false;
          }
        }
        break;
      case 86: // Ctrl-v for paste
        if (ctrlOrMeta) {
          if (
              !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
                  .find("*")
                  .is(":focus")
          ) {
            // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
            pasteHandler();
            stopEvent(e);
            return false;
          }
        }
        break;
      case 90: // Ctrl-z for undo
        if (ctrlOrMeta) {
          if (
              !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
                  .find("*")
                  .is(":focus")
          ) {
            // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
            undoHandler();
            stopEvent(e);
            return false;
          }
        }
        break;
      case 89: // Ctrl-y for redo
        if (ctrlOrMeta) {
          if (
              !$("#" + ACS.vConst.PROPERTYEDITOR_MOTHERPANEL)
                  .find("*")
                  .is(":focus")
          ) {
            // if focus is not somewhere inside propertyEditor (otherwise key could not be used in propertyEditor)
            redoHandler();
            stopEvent(e);
            return false;
          }
        }
        break;
      case 68: // Ctrl-d for drop channel
        if (ctrlOrMeta) {
          dropStartedChannel();
          stopEvent(e);
          return false;
        }
        break;
      case 32: // SPACE
        if (
            actModelView.getKeyboardMode() &&
            $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
        ) {
          if (actModelView.getPortMode()) {
            if (e.shiftKey) {
              // begin new channel or connect incomplete channel to the selected port
              actModelView.connectChannelAtActPort();
              actModelView.setPortMode(false);
            } else {
              actModelView.setChannelMode(true);
            }
            stopEvent(e);
            return false;
          } else if (!actModelView.getChannelMode()) {
            actModelView.setPortMode(true);
            stopEvent(e);
            return false;
          }
        } else if (
            actModelView.getListKeyboardMode() &&
            $("#listPanel" + actModelView.getModelContainerId())
                .find("*")
                .is(":focus")
        ) {
          if (actModelView.getListPortMode()) {
            if (e.shiftKey) {
              // begin new channel or connect incomplete channel to the selected port
              actModelView.connectChannelAtActPort();
            } else {
              actModelView.setListChannelMode(true);
            }
            stopEvent(e);
            return false;
          } else if (!actModelView.getListChannelMode()) {
            actModelView.setListPortMode(true, true);
            stopEvent(e);
            return false;
          }
        }
    }

    switch (keyCode) {
      case 37: // arrow left
        if (catchArrowKey(e, "left")) {
          stopEvent(e);
          return false;
        }
        break;
      case 38: // arrow up
        if (catchArrowKey(e, "up")) {
          stopEvent(e);
          return false;
        }
        break;
      case 39: // arrow right
        if (catchArrowKey(e, "right")) {
          stopEvent(e);
          return false;
        }
        break;
      case 40: // arrow down
        if (catchArrowKey(e, "down")) {
          stopEvent(e);
          return false;
        }
        break;
    }
  };

  var handleKeyup = function(e) {
    // ESC and ENTER need to be caught on keyup, since firefox does not send the keydown event
    let keyCode = e.keyCode || e.which;
    if (keyCode === 27) {
      // one step back up in the hierarchy of keyboard modes
      if (
        actModelView.getKeyboardMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        if (actModelView.getChannelMode()) {
          actModelView.setChannelMode(false);
        } else if (actModelView.getPortMode()) {
          actModelView.setPortMode(false);
        }
        stopEvent(e);
        return false;
      } else if (
        actModelView.getListKeyboardMode() &&
        $("#listPanel" + actModelView.getModelContainerId())
          .find("*")
          .is(":focus")
      ) {
        if (actModelView.getListChannelMode()) {
          actModelView.setListChannelMode(false);
        } else if (actModelView.getListPortMode()) {
          actModelView.setListPortMode(false, false);
        }
        stopEvent(e);
        return false;
      }
    } else if (keyCode === 13) {
      // ENTER to enter keyboard mode
      if (
        $("#" + actModelView.getModelContainerId()).has(":focus").length > 0 ||
        $("#" + actModelView.getModelContainerId())
          .find("*")
          .has(":focus").length > 0
      ) {
        // enter keyboard mode only, when the focus on or somewhere in the canvasPanel
        if (
          $("#modelTab" + actModelView.getModelContainerId()).attr(
            "aria-selected"
          ) === "true"
        ) {
          $("#modelPanel" + actModelView.getModelContainerId()).focus(); // set keyboardFocus to modelPanel (otherwise tabPanel would consume the arrow-keys)
          if (!actModelView.getKeyboardMode())
            actModelView.setKeyboardMode(true);
          stopEvent(e);
          return false;
        } else if (
          $("#guiTab" + actModelView.getModelContainerId()).attr(
            "aria-selected"
          ) === "true"
        ) {
          $("#guiPanel" + actModelView.getModelContainerId()).focus(); // set keyboardFocus to guiPanel (otherwise tabPanel would consume the arrow-keys)
          if (!actModelView.getGuiKeyboardMode())
            actModelView.setGuiKeyboardMode(true);
          stopEvent(e);
          return false;
        } else if (
          $("#listTab" + actModelView.getModelContainerId()).attr(
            "aria-selected"
          ) === "true"
        ) {
          if (!actModelView.getListKeyboardMode())
            actModelView.setListKeyboardMode(true);
          stopEvent(e);
          return false;
        }
      }
    }
  };

  var cutHandler = function() {
    if (!actModelView.getPortMode() && !actModelView.getChannelMode())
      clipBoard.cut(modelList.getActModel());
  };

  var copyHandler = function() {
    if (!actModelView.getPortMode() && !actModelView.getChannelMode())
      clipBoard.copy(modelList.getActModel());
  };

  var pasteHandler = function() {
    if (!actModelView.getPortMode() && !actModelView.getChannelMode())
      clipBoard.paste(modelList.getActModel());
  };

  var deleteSelectionHandler = function() {
    log.debug("deleteBtnPressed");
    if (!$("#guiPanel" + actModelView.getModelContainerId()).is(":focus")) {
      var remAct;
      if (
        actModelView.getChannelMode() &&
        $("#modelPanel" + actModelView.getModelContainerId()).is(":focus")
      ) {
        remAct = ACS.removeItemListAction(
          modelList.getActModel(),
          modelList.getActModel().selectedItemsList.slice(1)
        ); // omits the first selected item, so that the operation is only performed on the channel
      } else {
        remAct = ACS.removeItemListAction(
          modelList.getActModel(),
          modelList.getActModel().selectedItemsList
        );
      }
      remAct.execute();
    }
  };

  var undoHandler = function() {
    // find out, if the Model Designer or the GUI Designer is active, then pop from the corresponding stack
    var modelTabs = null;
    var canvasPanels = document.getElementsByClassName("canvasPanel");
    for (var i = 0; i < canvasPanels.length; i++) {
      if (canvasPanels[i].getAttribute("aria-hidden") === "false") {
        modelTabs = document.getElementsByClassName("modelTab");
      }
    }
    if (modelTabs[1].getAttribute("aria-selected") === "true") {
      if (modelList.getActModel().guiUndoStack.length > 0)
        modelList
          .getActModel()
          .guiUndoStack.pop()
          .undo();
    } else {
      if (modelList.getActModel().undoStack.length > 0)
        modelList
          .getActModel()
          .undoStack.pop()
          .undo();
    }
  };

  var redoHandler = function() {
    // find out, if the Model Designer or the GUI Designer is active, then pop from the corresponding stack
    var modelTabs = null;
    var canvasPanels = document.getElementsByClassName("canvasPanel");
    for (var i = 0; i < canvasPanels.length; i++) {
      if (canvasPanels[i].getAttribute("aria-hidden") === "false") {
        modelTabs = document.getElementsByClassName("modelTab");
      }
    }
    if (modelTabs[1].getAttribute("aria-selected") === "true") {
      if (modelList.getActModel().guiRedoStack.length > 0)
        modelList
          .getActModel()
          .guiRedoStack.pop()
          .execute();
    } else {
      if (modelList.getActModel().redoStack.length > 0)
        modelList
          .getActModel()
          .redoStack.pop()
          .execute();
    }
  };

  var helpHandler = function() {
    openHelp(ACS.vConst.VIEW_ONLINE_HELP_PATH);
  };

  function openHelp(pathToHelp) {
    // load the help system
    var actModel = modelList.getActModel();
    if (
      actModel.selectedItemsList.length === 1 &&
      typeof actModel.selectedItemsList[0].getComponentTypeId() !== "undefined"
    ) {
      // thus there is one single item selected and this item is a component
      var directory;
      switch (actModel.selectedItemsList[0].getType()) {
        case ACS.componentType.SENSOR:
          directory = "sensors";
          break;
        case ACS.componentType.PROCESSOR:
          directory = "processors";
          break;
        case ACS.componentType.ACTUATOR:
          directory = "actuators";
          break;
      }
      var file = actModel.selectedItemsList[0].getComponentTypeId() + ".html";
      if (file.indexOf("Oska") === -1) file = file.slice(9); // the slice eliminates the "asterics."
      window.open(pathToHelp + "/plugins/" + directory + "/" + file);
    } else {
      window.open(pathToHelp + ACS.vConst.VIEW_PATHTOACSHELPSTARTPAGE);
    }
  }

  var AREStatusChangedEventHandler = function() {
    switch (ACS.areStatus.getStatus()) {
      case ACS.statusType.DISCONNECTED:
        document.getElementById("AREstatus").textContent = "Disconnected";
        break;
      case ACS.statusType.CONNECTING:
        document.getElementById("AREstatus").textContent = "Trying to connect";
        break;
      case ACS.statusType.CONNECTED:
        document.getElementById("AREstatus").textContent = "Connected";
        break;
      case ACS.statusType.STARTING:
        document.getElementById("AREstatus").textContent =
          "Attempting to start model";
        break;
      case ACS.statusType.STARTED:
        document.getElementById("AREstatus").textContent = "Model running";
        break;
      case ACS.statusType.PAUSING:
        document.getElementById("AREstatus").textContent =
          "Attempting to pause model";
        break;
      case ACS.statusType.PAUSED:
        document.getElementById("AREstatus").textContent = "Model paused";
        break;
      case ACS.statusType.RESUMING:
        document.getElementById("AREstatus").textContent =
          "Attempting to resume model";
        break;
      case ACS.statusType.STOPPING:
        document.getElementById("AREstatus").textContent =
          "Attempting to stop model";
        break;
      case ACS.statusType.STOPPED:
        document.getElementById("AREstatus").textContent = "Model stopped";
        break;
      case ACS.statusType.CONNECTIONLOST:
        document.getElementById("AREstatus").textContent = "Connection lost";
        break;
    }
  };

  var ARESynchronisationChangedEventHandler = function() {
    if (
      ACS.areStatus.getStatus() === ACS.statusType.DISCONNECTED ||
      ACS.areStatus.getStatus() === ACS.statusType.CONNECTIONLOST ||
      ACS.areStatus.getStatus() === ACS.statusType.CONNECTING
    ) {
      document.getElementById("synchronisationStatus").textContent = "";
    } else {
      switch (ACS.areStatus.getSynchronised()) {
        case true:
          document.getElementById("synchronisationStatus").textContent =
            " / synchronised";
          break;
        case false:
          document.getElementById("synchronisationStatus").textContent =
            " / NOT synchronised";
          break;
        case undefined:
          document.getElementById("synchronisationStatus").textContent =
            " / synchronisation status unknown";
          break;
      }
    }
  };

  var actModelChangedEventHandler = function() {
    actModelView = canvas.getActModelView();
  };

  // ***********************************************************************************************************************
  // ************************************************** public stuff *******************************************************
  // ***********************************************************************************************************************
  var returnObj = {};

  // ***********************************************************************************************************************
  // *********************************************** constructor code ******************************************************
  // ***********************************************************************************************************************
  menu.setComponentMenu();
  // catch keyboard shortcuts
  document.addEventListener("keydown", handleKeydown);
  document.addEventListener("keyup", handleKeyup);
  // register handlers for button-presses in menu
  menu.events.registerHandler("cutBtnPressedEvent", cutHandler);
  menu.events.registerHandler("copyBtnPressedEvent", copyHandler);
  menu.events.registerHandler("pasteBtnPressedEvent", pasteHandler);
  menu.events.registerHandler("deleteBtnPressedEvent", deleteSelectionHandler);
  menu.events.registerHandler("undoBtnPressedEvent", undoHandler);
  menu.events.registerHandler("redoBtnPressedEvent", redoHandler);
  menu.events.registerHandler("helpBtnPressedEvent", helpHandler);
  // register handlers for areStatus events
  ACS.areStatus.events.registerHandler(
    "AREStatusChangedEvent",
    AREStatusChangedEventHandler
  );
  ACS.areStatus.events.registerHandler(
    "ARESynchronisationChangedEvent",
    ARESynchronisationChangedEventHandler
  );
  // register handlers for shortcuts
  $("#AKmenu")
    .click(handleMenuShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handleMenuShortcutClick();
    });
  $("#AKactModelPanel")
    .click(handleModelPanelShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handleModelPanelShortcutClick();
    });
  $("#AKmodelDesigner")
    .click(handleModelDesignerShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handleModelDesignerShortcutClick();
    });
  $("#AKguiDesigner")
    .click(handleGuiDesignerShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handleGuiDesignerShortcutClick();
    });
  $("#AKlistView")
    .click(handleListViewShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handleListViewShortcutClick();
    });
  $("#AKPropertyEditor")
    .click(handlePropertyEditorShortcutClick)
    .keypress(function(e) {
      if (e.keyCode === 13) handlePropertyEditorShortcutClick();
    });
  // register handler for change of the actModel
  modelList.events.registerHandler(
    "actModelChangedEvent",
    actModelChangedEventHandler
  );
  // get the actModelView (needed for keyboard navigation)
  actModelView = canvas.getActModelView();

  // deactivate keyboardModes when using the mouse
  $("body").mousedown(function() {
    if (actModelView.getKeyboardMode()) actModelView.setKeyboardMode(false);
    if (actModelView.getGuiKeyboardMode())
      actModelView.setGuiKeyboardMode(false);
    if (actModelView.getListKeyboardMode())
      actModelView.setListKeyboardMode(false);
  });

  // if autoConnect is set true by querystring, connect directly to the ARE
  if (ACS.autoConnect) {
    document.getElementById("connectAREBtn").click();
  }

  return returnObj;
};
