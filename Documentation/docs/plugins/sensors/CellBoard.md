---
title: CellBoard
---

# CellBoard

### Component Type: Sensor (Subcategory: Graphical User Interface)

The CellBoard plugin is a GUI plugin which can display a panel with multiple cells. Each cell can contain text and a picture, and it can provide acoustic feedback on selection. The CellBoard plugin provides basic scanning options (to select a cell via a stepwise process). It provides a small editor for designing the cells and can be used as a simple on-screen keyboard.

![Screenshot:
        CellBoard plugin](./img/CellBoard.jpg "Screenshot: CellBoard plugin")  
CellBoard plugin

## Using the cell editor

The cell editor allows to define the content of a cell (caption, action string, image and sounds for cell-selection, cell-scanning and cell- aucousticPreview). The resulting keyboard can be saved as .xml file - which can be specified as property "keyboardFile".

![Screenshot:
        CellBoard editor](./img/CellBoardEditor.jpg "Screenshot: CellBoard editor")  
CellBoard editor

## Input Port Description

- **row \[integer\]:** This port can be used in "directed scanning" mode to set the row of the highlighted cell.
- **column \[integer\]:** This port can be used in "directed scanning" mode to set the column of the highlighted cell.
- **cellNumber \[integer\]:** This port can be used in "directed scanning" mode to set the number of the highlighted cell.
- **xmlFile \[string\]:** Name of a keyboard file (default location in data/cellBoardKeyboards) which can be loaded or saved.

## Output Port Description

- **actCell \[integer\]:** Sends the number of the currently scanned cell.
- **actCellCaption \[string\]:** Sends the cell caption of the currently scanned cell.
- **actCellText \[string\]:** Sends the action string of the currently scanned cell
- **selectedCell \[integer\]:** Sends the number of the selected cell.
- **selectedCellCaption \[string\]:** Sends the cell caption of the selected cell.
- **selectedCellText \[string\]:** Sends the action string of the currently active cell

## Event Listener Description

- **scanMove:** Moves the scanning highlight frame in the row-column and column-row scanning modes.
- **scanSelect:** Selects the highlighted cell in the row-column, column-row and directed scanning modes.
- **moveUp:** Moves the scanning highlight up in the directed scanning mode (wrap around is possible).
- **moveRight:** Moves the scanning highlight to the right in the directed scanning mode (wrap around is possible).
- **moveLeft:** Moves the scanning highlight to the left in the directed scanning mode (wrap around is possible).
- **moveDown:** Moves the scanning highlight down in the directed scanning mode (wrap around is possible).
- **load:** loads a keyboard from the given xml-filename (property "keyboardFile").

## Event Trigger Description

- **cellClicked:** This event is triggered when one of the cells is clicked.
- **cell1...cell36:** This event is triggered when the given cell is selected.

## Properties

- **caption \[string\]:** The component caption.
- **rows \[integer\]:** The number of the cell rows (1-36, rows x cols <= 100).
- **columns \[integer\]:** The number of the cell columns (1-36, rows x cols <= 100).
- **textColor \[integer\]:** The color of the cell text.
- **backgroundColor \[integer\]:** The color of the cell background.
- **scanColor \[integer\]:** The color of the scanning highlighting.
- **scanMode \[integer\]:** This property defines the scanning mode:
  - _"none":_ The board is not scanned. Users can select the cell by clicking on it.
  - _"row-column":_ In this mode, rows of the cells are scanned. When the user selects the row, cells in the row are scanned. The scanning frame can be moved using the scanMove event port. The row and the cell itself can be selected using the scanSelect event port.
  - _"column-row":_ In this mode, columns of the cells are scanned. When the user selects the column, cells in the column are scanned. The scanning frame can be moved using the scanMove event port. The column and the cell itself can be selected using the scanSelect event port.
  - _"directed":_ In this mode only one cell is highlighted. The user can move the scan highlighting via the moveUp, moveRight, moveLeft or moveDown event ports or the row, cellNumber and column input ports. The cell can be selected using the scanSelect event port.
  - _"hover selection":_ In this mode the user can select the cell by hovering the mouse pointer above the cell.
- **hoverTime \[integer\]:** This parameter is used in the "hover selection" mode. It defines the time of hovering needed to select the cell (in milliseconds).
- **enableEdit \[boolean\]:** If selected, the cells can be right-clicked to display the cell editor, which also allows to save the xml-keyboard file.
- **enableClickSelection \[boolean\]:**If selected, all cells can be selected via a left click, regardless of the scanning process
- **keyboardFile \[string\]:** Name of a keyboard file (xml file located in in data/cellBoardKeyboards) to be loaded at startup or to be saved from the cell editor.**Supports value suggestions for existing xml-files from the ARE (dynamic property)**
- **dispayGUI \[boolean\]:** If selected, the component will be displayed in the ARE GUI. Otherwise the componentent will be hidden !
- **cellText1...cellText36 \[string\]:** The text displayed on the cell.
- **cellImage1...cellImage36 \[string\]:** The path of the image displayed on the cell.
- **actionText1...actionText36 \[string\]:** The text sent through the selectedCellText output port, when the cell is selected.
