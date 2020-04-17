---
title: CellBoard
---

# CellBoard

### Component Type: Sensor (Subcategory: Graphical User Interface)

The CellBoard plugin is a GUI plugin which can display a board with the cells. Each cell can contain text and a picture. The CellBoard plugin provides basic scanning options and can be used as a simple on-screen keyboard.

![Screenshot:
        CellBoard plugin](./img/CellBoard.jpg "Screenshot: CellBoard plugin")  
CellBoard plugin

## Input Port Description

- **row \[integer\]:** This port can be used in directed scanning mode to set the row of the highlighted cell.
- **column \[integer\]:** This port can be used in directed scanning mode to set the column of the highlighted cell.
- **cellNumber \[integer\]:** This port can be used in directed scanning mode to set the number of the highlighted cell.
- **xmlFile \[string\]:** Name of keyboard file (in data/cellBoardKeyboards) which is used when the 'load' event occurs.

## Output Port Description

- **selectedCell \[integer\]:** Sends the number of the selected cell.
- **selectedCellText \[string\]:** Sends the action text of the selected cell.

## Event Listener Description

- **scanMove:** Moves the scanning highlight frame in the row-column and column-row scanning modes.
- **scanSelect:** Selects the highlighted cell in the row-column, column-row and directed scanning modes.
- **moveUp:** Moves the scanning highlight up in the directed scanning mode.
- **moveRight:** Moves the scanning highlight to the right in the directed scanning mode.
- **moveLeft:** Moves the scanning highlight to the left in the directed scanning mode.
- **moveDown:** Moves the scanning highlight down in the directed scanning mode.

## Event Trigger Description

- **cellClicked:** This event is triggered when one of the cells is clicked.
- **load:** Loads the xml file with the keyboard definition set by the xmlFile input port
- **cell1...cell36:** This event is triggered when a cell is selected.

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
- **hoverTime \[integer\]:** This parameter is used in the hover selection mode. It defines the time of hovering needed to select the cell (in milliseconds).
- **keyboardFile \[string\]:** Name of keyboard file (in data/cellBoardKeyboards) to load at startup.**Supports value suggestions from ARE (dynamic property)**
- **cellText1...cellText36 \[string\]:** The text displayed on the cell.
- **cellImage1...cellImage36 \[string\]:** The path of the image displayed on the cell.
- **actionText1...actionText36 \[string\]:** The text sent through the selectedCellText output port, when the cell is selected.
