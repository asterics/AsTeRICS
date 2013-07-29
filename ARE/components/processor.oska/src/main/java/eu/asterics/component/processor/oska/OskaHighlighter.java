/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.    
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b. 
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P"
 *
 *
 *                    homepage: http://www.asterics.org
 *
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */
package eu.asterics.component.processor.oska;

import eu.asterics.component.processor.oska.OskaHighlighter.ResetMode;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * The OskaHighlighter manages all the issues that concern highlighting of a
 * certain key, row or column on OSKA. The class is only visible within the 
 * package and should be accessed via the OskaInstance singleton.
 * @author weissch
 *
 */
class OskaHighlighter {
	
	OskaInstance owner;
	
	enum HighlightMode
	{
		HIGHLIGHT_ROW,
		HIGHLIGHT_COL,
		HIGHLIGHT_CELL
	}

	enum ResetMode
	{
		RESETPOS_DISABLED,
		RESETPOS_TOPLEFT,
		RESETPOS_CENTER,
		RESETPOS_BOTTOMRIGHT
	}
	
	
	int rows = 1;
	int columns = 1;
	double stepsizeX = 1;
	double stepsizeY = 1;
	
	int [] resetPos = { 0, 0 };
	int [] highlightPos = { 0, 0 };
	private int currentHighlightStyle = -1;
	HighlightMode lastMode = null;
	private ResetMode resetMode = ResetMode.RESETPOS_TOPLEFT;
	private boolean useFloatRange;
	
	void initHighlighter()
	{
		resetPos[0] = 0;
		highlightPos[0] = 0;
		resetPos[1] = 0;
		highlightPos[1] = 0;
		currentHighlightStyle = -1;
		lastMode = null;
	}
	
	/**
	 * Updates the grid dimensions of the highlighter. Recalculates the step 
	 * size for the input ports and resets the highlighting position.
	 * @param columns the number of columns in the OSKA grid
	 * @param rows the number of rows in the OSKA grid
	 */
	void setGridDimensions(int columns, int rows)
	{
		this.rows = rows;
		this.columns = columns;
		
		stepsizeX = 1 / (double) columns;
		stepsizeY = 1 / (double) rows;
		
		highlightPos[0] = 0;
		highlightPos[1] = 0;
		
		AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
				String.format("Updated grid dimensions to %d, %d, step x: %f, step y: %f", 
						columns,rows, stepsizeX, stepsizeY));
	}
	
	/**
	 * Highlights the row indicated by the input value. Input takes values 
	 * between 0 and 1 and maps linearly them to the corresponding position in
	 * the grid.
	 * @param input the position in the grid as a double between 0 and 1
	 */
	void highlightRow(double input)
	{
		updateHighlightingOnOska(
				highlightPos[0] * (useFloatRange ? stepsizeX : 1), 
				input, HighlightMode.HIGHLIGHT_ROW);
	}
	
	/**
	 * Highlights the column indicated by the input value. Input takes values 
	 * between 0 and 1 and maps linearly them to the corresponding position in
	 * the grid.
	 * @param input the position in the grid as a double between 0 and 1
	 */
	void highlightColumn(double input)
	{
		updateHighlightingOnOska(input, 
				highlightPos[1] * (useFloatRange ? stepsizeY : 1), 
				HighlightMode.HIGHLIGHT_COL);
	}

	/**
	 * Highlights a cell in the currently selected row as indicated by the input 
	 * value. Input takes values between 0 and 1 and maps linearly them to the 
	 * corresponding position in the grid.
	 * @param input the position in the grid as a double between 0 and 1
	 */
	void highlightCellX(double input)
	{
		updateHighlightingOnOska(input, 
				highlightPos[1] * (useFloatRange ? stepsizeY : 1), 
				HighlightMode.HIGHLIGHT_CELL);
	}

	/**
	 * Highlights a cell in the currently selected column as indicated by the 
	 * input value. Input takes values between 0 and 1 and maps linearly them 
	 * to the corresponding position in the grid.
	 * @param input the position in the grid as a double between 0 and 1
	 */
	void highlightCellY(double input)
	{
		updateHighlightingOnOska(
				highlightPos[0] * (useFloatRange ? stepsizeX : 1),
				input,HighlightMode.HIGHLIGHT_CELL);
	}
	
	/**
	 * Highlights a cell as indicated by the input 
	 * values. Inputs take values between 0 and 1 and maps linearly them to the 
	 * corresponding position in the grid.
	 * @param x the column position in the grid as a double between 0 and 1
	 * @param y the row position in the grid as a double between 0 and 1
	 */
	void highlightCell(double x, double y)
	{
		updateHighlightingOnOska(x,y,HighlightMode.HIGHLIGHT_CELL);
	}
	
	/**
	 * Highlights the next column in the grid.
	 */
	void highlightNextColumn()
	{
		highlightPos[0]++;
		if (highlightPos[0] >= columns)
		{
			highlightPos[0] = 0;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_COL);
	}
	
	/**
	 * Highlights the previous column in the grid.
	 */
	void highlightPrevColumn()
	{
		highlightPos[0]--;
		if (highlightPos[0] < 0)
		{
			highlightPos[0] = columns - 1;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_COL);
	}
	
	/**
	 * Highlights the next row in the grid.
	 */
	void highlightNextRow()
	{
		highlightPos[1]++;
		if (highlightPos[1] >= rows)
		{
			highlightPos[1] = 0;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_ROW);
	}

	/**
	 * Highlights the previous row in the grid.
	 */
	void highlightPrevRow()
	{
		highlightPos[1]--;
		if (highlightPos[1] < 0)
		{
			highlightPos[1] = rows - 1;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_ROW);
	}
	
	/**
	 * Highlights the next key in the grid.
	 * @param pos 0 for x axis, 1 for y axis
	 */
	void highlightNextKey(int pos)
	{
		highlightPos[pos]++;
		if (highlightPos[pos] >= ((pos == 1) ? rows : columns ))
		{
			highlightPos[pos] = 0;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_CELL);
	}
	
	/**
	 * Highlights the previous key in the grid.
	 * @param pos 0 for x axis, 1 for y axis
	 */
	void highlightPrevKey(int pos)
	{
		highlightPos[pos]--;
		if (highlightPos[pos] < 0) 
		{
			highlightPos[pos] = ( (pos == 1) ? rows : columns ) - 1;
		}
		updateHighlightingOnOska(HighlightMode.HIGHLIGHT_CELL);
	}
	
	/**
	 * Calculates the position in the grid and updates highlighting if necessary.
	 * Dependent on the value range used it will either cast the inputs to 
	 * integer values or clip them to a float range from 0.0 to 1.0.
	 *  
	 * @param x the column position
	 * @param y the row position 
	 * @param mode an enum describing whether to highlight row, column or cell
	 */
	private void updateHighlightingOnOska(double x, double y,HighlightMode mode)
	{
		boolean update = false;
		
		int itemX = (int) x;
		int itemY = (int) y;

		
		if (useFloatRange)
		{
	    	if (x >= 1)
	    		x = 0.99;
	    	else if (x < 0)
	    		x = 0;
	
	    	if (y >= 1)
	    		y = 0.99;
	    	else if (y < 0)
	    		y = 0;

			itemX = (int) (x / stepsizeX);
			itemY = (int) (y / stepsizeY);

//			System.out.println(String.format("updateHighlightingOnOska: x:%f, y:%f, stepX: %f, stepY: %f, itemX:%d, itemY:%d ", x, y, stepsizeX, stepsizeY, itemX, itemY));
			
		}
		else
		{
			if (itemX >= columns)
				itemX = columns - 1;
			else if (itemX < 0 )
				itemX = 0;

			if (itemY >= rows)
				itemY = rows - 1;
			else if (itemY < 0 ) 
				itemY = 0;
		}
		
		if ((mode != HighlightMode.HIGHLIGHT_ROW) && (highlightPos[0] != itemX))
		{
			highlightPos[0] = itemX;
			update = true;
		}
		if ((mode != HighlightMode.HIGHLIGHT_COL) && (highlightPos[1] != itemY))
		{
			highlightPos[1] = itemY;
			update = true;
		}
		
		
		
		if (lastMode != mode)
		{
			update = true;
			lastMode = mode;
		}

		if (update)
			updateHighlightingOnOska(mode);
			
	}

	/**
	 * Creates the command to be sent to OSKA depending on the highlight mode
	 * and invokes the communication controller to send it to OSKA
	 * @param mode the requested highlight mode
	 */
	private void updateHighlightingOnOska(HighlightMode mode)
	{
		StringBuffer cmd = new StringBuffer();
		switch (mode)
		{
		case HIGHLIGHT_ROW: 
			cmd.append("HighlightRow:"); 
        	cmd.append(Integer.toString(highlightPos[1]));
			break;
		case HIGHLIGHT_COL:
			cmd.append("HighlightColumn:"); 
        	cmd.append(Integer.toString(highlightPos[0]));
			break;
		case HIGHLIGHT_CELL: 
			cmd.append("HighlightKey:"); 
			cmd.append(Integer.toString(highlightPos[0]));
			cmd.append(",");
			cmd.append(Integer.toString(highlightPos[1]));
			break;
		}

//		AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
//				"Sending to OSKA: "+ cmd.toString()); 
		lastMode = mode;
		
		OskaInstance.instance.communication.sendToOska(cmd.toString());
	}

	/**
	 * Positions the highlighting according to reset mode and highlights the
	 * position according to highlight mode argument
	 * @param mode declares whether row, column or cell should be highlighted
	 */
	void resetHighlightingPos(HighlightMode mode) {
		switch (resetMode)
		{
		case RESETPOS_TOPLEFT:
			highlightPos[0] = 0;
			highlightPos[1] = 0;
			break;
		case RESETPOS_CENTER:
			highlightPos[0] = this.columns / 2;
			highlightPos[1] = this.rows /2;
			break;
		case RESETPOS_BOTTOMRIGHT:
			highlightPos[0] = this.columns - 1;
			highlightPos[1] = this.rows - 1;
			break;
		}
		updateHighlightingOnOska(mode);
	}
	
	/**
	 * Sets the highlighting style on OSKA
	 * @param newHighlightStyle a value between 0 and 2, will be clipped 
	 * otherwise
	 */
	void setHighlightingStyle(int newHighlightStyle)
	{
//		if (currentHighlightStyle != newHighlightStyle)
//		{
			if (newHighlightStyle < 0)
				currentHighlightStyle = 0;
			else if (newHighlightStyle > 2)
				currentHighlightStyle = 2;
			else
				currentHighlightStyle = newHighlightStyle;
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					String.format("Setting highlight style to %d", 
							currentHighlightStyle));
			OskaInstance.instance.communication.sendToOska("SetHighlightStyle:" 
					+ currentHighlightStyle);
//		}
	}
	
	/**
	 * Generates the press command with the current highlighting position. Will
	 * become deprecated with the next version of OSKA 
	 * @deprecated
	 * @return a String containing the press command
	 */
	String generatePressCommand()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("Press:")
			.append(highlightPos[0])
			.append(",")
			.append(highlightPos[1]);
		return buf.toString();
	}
	
	/**
	 * Updates the internal scanning speed on OSKA
	 * @param speed an integer from 1 to 10
	 * @return true if sending to OSKA worked, false otherwise
	 */
	boolean setScanSpeed(int speed)
	{
		return OskaInstance.instance.communication.setInternalScanSpeed(speed);
	}
	
	/**
	 * Sets the highlighter to the request highlight positioning mode
	 * @param highlightResetPosition 0 for top left, 1 for center, 2 for bottom
	 * left
	 * @return true
	 */
	boolean setResetPosition(int highlightResetPosition)
	{
		switch (highlightResetPosition)
		{
		case 0:
			resetMode = ResetMode.RESETPOS_DISABLED;
			break;
		case 1:
			resetMode = ResetMode.RESETPOS_TOPLEFT;
			break;
		case 2:
			resetMode = ResetMode.RESETPOS_CENTER;
			break;
		case 3:
			resetMode = ResetMode.RESETPOS_BOTTOMRIGHT;
			break;
		}
		return true;
	}

	/**
	 * Sets the highlighter to either use a mapped float range 0.0 to 1.0 or
	 * the incoming values as integer for updating the highlight position
	 * @param inputRange if true the range from 0.0 to 1.0 is used, integers 
	 * otherwise.
	 */
	void setInputRange(int inputRange) {
		useFloatRange = false;
		if (inputRange == 0)
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					"Using float range 0.0 to 1.0");
			useFloatRange = true;
		}
		else
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
			"Using integer range 1 to n");
		}
	}
}
