
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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.cellboard;


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *   Implements the Graphical User Interface for the
 *   Cell Board plugin
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 06, 2012
 *         Time: 11:21:43 AM
 */
public class GUI extends JPanel 
{
    
    private JPanel guiPanel;  
    private Dimension guiPanelSize;
    private GUICell cells[][];
    private JPanel gridPanel;
    // private JLabel myLabel;
    // add more GUI elements here

    private Lock lock = new ReentrantLock();
	private CellBoardInstance owner;
	int rows;
	int columns;
    /**
     * The class constructor, initialises the GUI
     * @param owner    the CellBoard instance
     * @param space  the size of the component
     */
    public GUI(final CellBoardInstance owner, final Dimension space)
    {
    	super();
    	this.owner=owner;
		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);  	
    }

    
	/**
	 * set up the panel and its elements for the given size 
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
		//Create Panels
		guiPanel = new JPanel ();
		guiPanelSize = new Dimension (width, height);

		guiPanel.setMaximumSize(guiPanelSize);
		guiPanel.setPreferredSize(guiPanelSize);
		
		guiPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK),owner.getCaption()));
		
		
		
		gridPanel = new JPanel();
		
		columns = owner.getColumnCount();
		rows = owner.getRowCount();
		
		
		cells=new GUICell[rows][columns];
		Insets panelBorderInsets=guiPanel.getBorder().getBorderInsets(guiPanel);
		int gridPanelWidth = width-panelBorderInsets.left-panelBorderInsets.right;
		int gridPanelHeight= height-panelBorderInsets.top-panelBorderInsets.bottom-5;
		Dimension gridPanelSize = new Dimension (gridPanelWidth, gridPanelHeight);
		gridPanel.setMaximumSize(gridPanelSize);
		gridPanel.setPreferredSize(gridPanelSize);
		
		GridLayout gridLayout = new GridLayout(rows,columns);
		gridPanel.setLayout(gridLayout);
		
		int index=0;
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				cells[i][j]=new GUICell(this,owner.getEventPort(index),owner.getCellOutputPort(),owner.getCellTextOutputPort(),owner.getGeneralEventPort());
				cells[i][j].setVisible(true);
				cells[i][j].setIndex(index);
				cells[i][j].setText(owner.getText(index));
				cells[i][j].setActionText(owner.getActionText(index));
				cells[i][j].setPicturePath(owner.getImagePath(index));
				cells[i][j].setScanActive(false);
				cells[i][j].setHoverTime(owner.getHoverTime());
				index++;
				gridPanel.add(cells[i][j]);
			}
		}
		
		gridPanel.setVisible(true);
		guiPanel.add(gridPanel,BorderLayout.CENTER);
		
		guiPanel.setVisible(true);
		
		//this.setBorder(new TitledBorder(owner.propMyTitle));     
		// myLabel = new JLabel (owner.propMyLabelCaption);
		// guiPanel.add(myLabel);
        
	    //this.setLayout(new BorderLayout());
		this.setLayout(new GridLayout());
        add (guiPanel);
        
	    
	}
	
	public void repaintCells() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<columns;j++)
					{
						cells[i][j].repaintNow();
					}
				}
			}
		});
	}
	
	public void update(final Dimension space,final float pfontSize) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				GUI.this.remove(guiPanel);
				GUI.this.rows = owner.getRowCount();
				GUI.this.columns = owner.getColumnCount();
				design(space.width,space.height);
				if (pfontSize == -1) {
					defineTextFontSize(space);
				} else {
					defineTextFontSize(pfontSize);
				}
				clearScanState();
				setScanning();
				repaintCells();
				gridPanel.repaint();
				gridPanel.invalidate();
			}
		});		
	}
	
	/**
	 * Search for the optimal text size of cells.
	 */
	public void  defineTextFontSize(final float pfontSize)
	{
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {

				float fontSize=-1;

				if (pfontSize < 0) {
					for(int i=0;i<rows;i++)
					{
						for(int j=0;j<columns;j++)
						{
							float size=cells[i][j].getMaxFont();
							if(size>0)
							{
								if(fontSize<0)
								{
									fontSize=size;
								}
								else
								{
									if(size<fontSize)
									{
										fontSize=size;
									}

								}
							}
						}
					}
				} else {
					fontSize = pfontSize;
				}
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<columns;j++)
					{
						cells[i][j].setFontSize(fontSize);
					}
				}
			}
		});				
	}
  
	
	/**
	 * Search for the optimal text size of cells.
	 */
	private void  defineTextFontSize(Dimension space)
	{
		float fontSize=-1;
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				float size=cells[i][j]. getMaxFont((int)(space.width/columns),(int)(space.height/rows));
				if(size>0)
				{
					if(fontSize<0)
					{
						fontSize=size;
					}
					else
					{
						if(size<fontSize)
						{
							fontSize=size;
						}
					
					}
				}
			}
		}
		
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				cells[i][j].setFontSize(fontSize);
			}
		}
		
		
	}
	
	/**
     * Returns the background color.
     * @return   color of the background
     */
	int getBackgroundColor()
	{
		return owner.getBackgroundColor();
	}
	
	 /**
     * Returns the color of the text.
     * @return   color of the text
     */
	int getTextColor()
	{
		return owner.getTextColor();
	}
	
	/**
     * Returns the background color for the active cell during scanning.
     * @return   color of the active cell background
     */
	int getScanColor()
	{
		return owner.getScanColor();
	}
	
	private int level=0;
	private int scanRow=0;
	private int scanColumn=0;
	private int scanType=0;
	private int repeatCount=0;
	private final int maxRepeatCount=3;
	
	public enum ScanSelectionDirection{
		up,down,left,right
	}
	
	
	
	/**
     * Removes scanning frames from all cells.
     */
	private void clearScanState()
	{
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				cells[i][j].setScanActive(false);
			}
		}
	}
	
	/**
     * Initializes the scanning.
     */
	public void setScanning()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				scanType=owner.getScanType();
				
				clearScanState();
				
				
				for(int i=0;i<rows;i++)
				{
					for(int j=0;j<columns;j++)
					{
						if((scanType==0)||(scanType==4))
						{
							
							if(scanType==4)
							{
								cells[i][j].setEventBlock(true);
								cells[i][j].setHoverSelection(true);
							}
							else
							{
								cells[i][j].setEventBlock(false);
								cells[i][j].setHoverSelection(false);
							}
						}
						else
						{
							cells[i][j].setEventBlock(true);
							cells[i][j].setHoverSelection(false);
						}
					}
				}
				
				if(scanType==3)
				{
					cells[0][0].setScanActive(true);
					scanRow=0;
					scanColumn=0;
				}
				else
				{
					if((scanType==1)||(scanType==2))
					{
						if((rows==1)||(columns==1))
						{
							cells[0][0].setScanActive(true);
							scanRow=0;
							scanColumn=0;
							level=1;
						}
						else
						{
							if(scanType==1)
							{
								for(int j=0;j<columns;j++)
								{
									cells[0][j].setScanActive(true);
								}
							}
							else
							{
								for(int i=0;i<rows;i++)
								{
									cells[i][0].setScanActive(true);
								}
							}
							
							level=0;
							scanRow=0;
							scanColumn=0;
							repeatCount=0;
						}
					}
				}
				
			}
		});		
	}
	
	/**
     * Sets the cell row in the directed mode.
     * @param cellRow the row of the cell
     */
	public void setSelectionRow(final int cellRow)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(scanType!=3)
				{
					return;
				}
				try
				{
					//lock.lock();
					int row=cellRow-1;
					if(row<0)
					{
						row=0;
					}
					if(row>=rows)
					{
						row=rows-1;
					}
					
					cells[scanRow][scanColumn].setScanActive(false);
					scanRow=row;
					cells[scanRow][scanColumn].setScanActive(true);
				}
				finally
				{
					//lock.unlock();
				}
			}
		});
	}
	
	/**
     * Sets the cell column in the directed mode.
     * @param cellColumn the column of the cell
     */
	public void setSelectionColumn(final int cellColumn)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(scanType!=3)
				{
					return;
				}
				try
				{
					//lock.lock();
					int column=cellColumn-1;
					if(column<0)
					{
						column=0;
					}
					if(column>=columns)
					{
						column=columns-1;
					}
					
					cells[scanRow][scanColumn].setScanActive(false);
					scanColumn=column;
					cells[scanRow][scanColumn].setScanActive(true);
				}
				finally
				{
					//lock.unlock();
				}
			}
		});
	}
	
	/**
     * Sets the cell column in the directed mode.
     * @param cellColumn the column of the cell
     */
	public void setSelectionNumber(final int cellIndex)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(scanType!=3)
				{
					return;
				}
				try
				{
					//lock.lock();
					
					
					boolean found=false;
					
					for(int i=0;i<rows;i++)
					{
						for(int j=0;j<columns;j++)
						{
							if(cells[i][j].getIndex()==cellIndex-1)
							{
								cells[scanRow][scanColumn].setScanActive(false);
								scanRow=i;
								scanColumn=j;
								cells[scanRow][scanColumn].setScanActive(true);
								break;
							}
						}
						
						if(found)
						{
							break;
						}
					}
					
				}
				finally
				{
					//lock.unlock();
				}
			}
		});
	}
	
	/**
     * Changes the active cell in the directed mode.
     */
	public void scanSelectionMove(final ScanSelectionDirection direction)
	{	
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					//lock.lock();
					if(scanType==3)
					{	
						switch (direction)
						{
							case up:
							{
								if(scanRow-1>=0)
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanRow=scanRow-1;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								else
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanRow=rows;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								break;
							}
							case down:
							{
								if(scanRow+1<rows)
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanRow=scanRow+1;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								else
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanRow=0;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								break;
							}
							case left:
							{
								if(scanColumn-1>=0)
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanColumn=scanColumn-1;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								else
								{
									cells[scanRow][scanColumn].setScanActive(false);
									scanColumn=columns;
									cells[scanRow][scanColumn].setScanActive(true);
								}
								break;
							}
							case right:
							{
								if(scanColumn+1<columns)
								{	
									cells[scanRow][scanColumn].setScanActive(false);
									scanColumn=scanColumn+1;
									cells[scanRow][scanColumn].setScanActive(true);	
								}
								else
								{	
									cells[scanRow][scanColumn].setScanActive(false);
									scanColumn=0;
									cells[scanRow][scanColumn].setScanActive(true);	
								}
								break;
							}
					
						}
						 
						GUI.this.revalidate();
						GUI.this.repaint();
						 
					}
				}
				finally
				{
					 //lock.unlock();
				}
				
				repaintCells();
			}
		});		
	}
	
	/**
     * Moves the scanning frame.
     */
	public void scanMove()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					//lock.lock();
					if((scanType==1)||(scanType==2))
					{
						if((rows>1)&&(columns>1))
						{
							if(level==0)
							{
								if(scanType==1)
								{
									for(int j=0;j<columns;j++)
									{
										cells[scanRow][j].setScanActive(false);
									}
									if(scanRow+1<rows)
									{
										scanRow=scanRow+1;
									}
									else
									{
										scanRow=0;
									}
								
									for(int j=0;j<columns;j++)
									{
										cells[scanRow][j].setScanActive(true);
									}
								}
								else
								{
									for(int i=0;i<rows;i++)
									{
										cells[i][scanColumn].setScanActive(false);
									}
								
									if(scanColumn+1<columns)
									{
										scanColumn=scanColumn+1;
									}
									else
									{
										scanColumn=0;
									}
								
									for(int i=0;i<rows;i++)
									{
										cells[i][scanColumn].setScanActive(true);
									}
								}	
							}
							else if(level==1)
							{
								if(scanType==1)
								{
									cells[scanRow][scanColumn].setScanActive(false);
									if(scanColumn+1<columns)
									{
										scanColumn=scanColumn+1;
									}
									else
									{
										scanColumn=0;
										repeatCount=repeatCount+1;
									}
									cells[scanRow][scanColumn].setScanActive(true);
								}
								else
								{
									cells[scanRow][scanColumn].setScanActive(false);
									if(scanRow+1<rows)
									{
										scanRow=scanRow+1;
									}
									else
									{
										scanRow=0;
										repeatCount=repeatCount+1;
									}
									cells[scanRow][scanColumn].setScanActive(true);
								}
							
								if(repeatCount>=maxRepeatCount)
								{
									cells[scanRow][scanColumn].setScanActive(false);
									level=0;
									repeatCount=0;
									scanRow=0;
									scanColumn=0;
									if(scanType==1)
									{
										for(int j=0;j<columns;j++)
										{
											cells[scanRow][j].setScanActive(true);
										}
									}
									else
									{	
										for(int i=0;i<rows;i++)
										{
											cells[i][scanColumn].setScanActive(true);
										}
									}
								
								}
							}
						}
						else
						{
							cells[scanRow][scanColumn].setScanActive(false);
						
							if(rows==1)
							{
								if(scanColumn+1<columns)
								{
									scanColumn=scanColumn+1;
								}
								else
								{
									scanColumn=0;
								}
							}
							else
							{
								if(scanRow+1<rows)
								{
									scanRow=scanRow+1;
								}
								else
								{
									scanRow=0;
								}
							}
						
							cells[scanRow][scanColumn].setScanActive(true);
						}
					}
				}
				finally
				{
					 //lock.unlock();
				}
				
				repaintCells();
			}
		});
	}
	
	/**
     * Performs the selecting scanning action.
     */
	public void scanSelect ()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					//lock.lock();
					if((scanType==1)||(scanType==2))
					{
						if((rows>1)&&(columns>1))
						{
							if(level==0)
							{
								level=1;
								if(scanType==1)
								{
									for(int j=0;j<columns;j++)
									{
										cells[scanRow][j].setScanActive(false);
									}
							
									scanColumn=0;
							
									cells[scanRow][scanColumn].setScanActive(true);
							
								}
								else
								{
									for(int i=0;i<rows;i++)
									{
										cells[i][scanColumn].setScanActive(false);
									}
							
									scanRow=0;
						
									cells[scanRow][scanColumn].setScanActive(true);
							
								}
							}
							else if(level==1)
							{
								cells[scanRow][scanColumn].setScanActive(false);
								makeCellAction(scanRow,scanColumn);
								level=0;
								repeatCount=0;
								scanRow=0;
								scanColumn=0;
								if(scanType==1)
								{
									for(int j=0;j<columns;j++)
									{
										cells[scanRow][j].setScanActive(true);
									}
								}
								else
								{	
									for(int i=0;i<rows;i++)
									{
										cells[i][scanColumn].setScanActive(true);
									}
								}	
							}
						}
						else
						{
							cells[scanRow][scanColumn].setScanActive(false);
							makeCellAction(scanRow,scanColumn);
							level=1;
							scanRow=0;
							scanColumn=0;
							cells[scanRow][scanColumn].setScanActive(true);
						}
					}
					else
					{
						if(scanType==3)
						{
							makeCellAction(scanRow,scanColumn);
						}
					}
				}
				finally
				{
					 //lock.unlock();
					 
				}
				
				repaintCells();
			}
		});
	}
	
	/**
     * Performs the cell action.
     */
	private void makeCellAction(int row,int column)
	{
		int index=cells[row][column].getIndex();
		owner.getEventPort(index).raiseEvent();
		owner.getCellOutputPort().sendData(ConversionUtils.intToBytes(index+1));
		owner.getCellTextOutputPort().sendData(ConversionUtils.stringToBytes(cells[row][column].getActionText()));
	
	}
	
	/**
     * Prepares the cells to close.
     */
	void prepareToClose()
	{
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				cells[i][j].close(); 
			}
		}
	}
	
}