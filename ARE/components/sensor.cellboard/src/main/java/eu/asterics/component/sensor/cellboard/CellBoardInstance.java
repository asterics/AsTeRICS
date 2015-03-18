

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


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;









import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.ComponentUtils;
import eu.asterics.mw.services.MidiManager;

/**
 * 
 * Implement GUI Cell Board component. The component can display up to 36 cells with text or/and picture.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 06, 2012
 *         Time: 11:21:43 AM
 */
public class CellBoardInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opSelectedCell = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSelectedCellCaption = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSelectedCellText = new DefaultRuntimeOutputPort();

	final IRuntimeOutputPort opActCell = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opActCellCaption = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opActCellText = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	//final IRuntimeEventTriggererPort etpSelectedCell = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
    final int NUMBER_OF_CELLS = 100;
    final int MAX_ROWS=36;
    final int MAX_COLUMNS=36;
	private static final int MAX_MATRIX_ROWS_COLS = 9;
	private static final String FILE_PATH_PREFIX="data/cellBoardKeyboards/";
    
    private final String ETP_CELL_CLICKED="cellClicked";
    private final String ETP_CELL = "cell";
    private final String ELP_SCAN_MOVE="scanMove";
    private final String ELP_SCAN_SELECT="scanSelect";
    private final String ELP_MOVE_UP="moveUp";
    private final String ELP_LOAD="load";
    private final String ELP_MOVE_RIGHT="moveRight";
    private final String ELP_MOVE_LEFT="moveLeft";
    private final String ELP_MOVE_DOWN="moveDown";
    private final String PROP_CAPTION="caption";
    private final String PROP_ROWS="rows";
    private final String PROP_COLUMNS="columns";
    private final String PROP_SCAN_TYPE="scanMode";
    private final String PROP_CELL_TEXT="cellText";
    private final String PROP_CELL_IMAGE="cellImage";
    private final String PROP_CELL_ACTION_TEXT="actionText";
    private final String PROP_BACKGROUND_COLOR="backgroundColor";
    private final String PROP_SCAN_COLOR="scanColor";
    private final String PROP_TEXT_COLOR="textColor";
    private final String PROP_HOVER_TIME="hoverTime";
    private final String PROP_ENABLE_EDIT="enableEdit";
    private final String PROP_ENABLE_CLICKSELECTION="enableClickSelection";
    private final String PROP_KEYBOARD_FILE="keyboardFile";

    private final String OP_SELECTED_CELL="selectedCell";
    private final String OP_SELECTED_CELL_CAPTION="selectedCellCaption";
    private final String OP_SELECTED_CELL_TEXT="selectedCellText";
    private final String OP_ACT_CELL="actCell";
    private final String OP_ACT_CELL_CAPTION="actCellCaption";
    private final String OP_ACT_CELL_TEXT="actCellText";
    private final String IP_ROW="row";
    private final String IP_COLUMN="column";
    private final String IP_CELL_NUMBER="cellNumber";
    private final String IP_XMLFILE="xmlfile";
    
    private String xmlFile = null;
    private Dimension space;
	public float propFontSize = -1;
	public int propRows = 2;
	public int propColumns = 2;
	public int propScanType = 2;
	public int propTextColor=0;
	public int propBackgroundColor=11;
	public int propScanColor=10;
	public int propHoverTime=1000;
	public boolean propEnableEdit = true;
	public boolean propEnableClickSelection = true;
	public String propCaption="Cell Board";
	public String propKeyboardFile="";
    public boolean propDisplayGUI=true;

	final EventPort [] etpCellArray = new EventPort[NUMBER_OF_CELLS];
	public String [] propCellCaptionArray = new String[NUMBER_OF_CELLS];
	public String [] propCellTextArray = new String[NUMBER_OF_CELLS];
	public String [] propCellImageArray = new String[NUMBER_OF_CELLS];
	public String [] propCellSoundArray = new String[NUMBER_OF_CELLS];
	public String [] propCellSoundPreviewArray = new String[NUMBER_OF_CELLS];

	final IRuntimeEventTriggererPort etpCellClicked = new DefaultRuntimeEventTriggererPort();
	
	// declare member variables here
	private  GUI gui = null;
    private boolean guiReady=false;
    
   /**
    * The class constructor.
    */
    public CellBoardInstance()
    {
        for(int i=0;i<NUMBER_OF_CELLS;i++)
        {
        	etpCellArray[i]=new EventPort();
        	propCellCaptionArray[i]="";
        	propCellImageArray[i]="";
        	propCellTextArray[i]="";
        	propCellSoundArray[i]="";
        	propCellSoundPreviewArray[i]="";
        }
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public  IRuntimeInputPort getInputPort(String portID)
    {
    	if (IP_ROW.equalsIgnoreCase(portID))
		{
			return ipRow;
		}
    	if (IP_COLUMN.equalsIgnoreCase(portID))
		{
			return ipColumn;
		}
    	if (IP_CELL_NUMBER.equalsIgnoreCase(portID))
		{
			return ipCellNumber;
		}
		if (IP_XMLFILE.equalsIgnoreCase(portID)) {
			return ipXmlFile;
		}
		
		return null;
	}
    

    
    /**
     * Input Ports for receiving values.
     */
	private final IRuntimeInputPort ipRow  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int rowValue=ConversionUtils.intFromBytes(data);
			
			if(guiReady)
			{
				gui.setSelectionRow(rowValue);
			}
			
		}
	
	};
	
	private final IRuntimeInputPort ipColumn  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int columnValue=ConversionUtils.intFromBytes(data);
			
			if(guiReady)
			{
				gui.setSelectionColumn(columnValue);
			}
		}
	};
	
	private final IRuntimeInputPort ipCellNumber  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int columnValue=ConversionUtils.intFromBytes(data);
			
			if(guiReady)
			{
				gui.setSelectionNumber(columnValue);
			}
		}
		
	};
	
	private final IRuntimeInputPort ipXmlFile  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			xmlFile = ConversionUtils.stringFromBytes(data);
		}
		
	};

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if (OP_SELECTED_CELL.equalsIgnoreCase(portID))
		{
			return opSelectedCell;
		}
		if (OP_SELECTED_CELL_CAPTION.equalsIgnoreCase(portID))
		{
			return opSelectedCellCaption;
		}
		if (OP_SELECTED_CELL_TEXT.equalsIgnoreCase(portID))
		{
			return opSelectedCellText;
		}
		if (OP_ACT_CELL.equalsIgnoreCase(portID))
		{
			return opActCell;
		}
		if (OP_ACT_CELL_CAPTION.equalsIgnoreCase(portID))
		{
			return opActCellCaption;
		}
		if (OP_ACT_CELL_TEXT.equalsIgnoreCase(portID))
		{
			return opActCellText;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
    
    	if (ELP_SCAN_MOVE.equalsIgnoreCase(eventPortID))
		{
			return elpScanMove;
		}
		if (ELP_SCAN_SELECT.equalsIgnoreCase(eventPortID))
		{
			return elpScanSelect;
		}
		if (ELP_MOVE_UP.equalsIgnoreCase(eventPortID))
		{
			return elpMoveUp;
		}
		if (ELP_MOVE_RIGHT.equalsIgnoreCase(eventPortID))
		{
			return elpMoveRight;
		}
		if (ELP_MOVE_LEFT.equalsIgnoreCase(eventPortID))
		{
			return elpMoveLeft;
		}
		if (ELP_MOVE_DOWN.equalsIgnoreCase(eventPortID))
		{
			return elpMoveDown;
		}
		if (ELP_LOAD.equalsIgnoreCase(eventPortID)) {
			return elpLoad;
		}		
        return null;
    }
    
    /**
     * Event generated by button press
     */
    public class EventPort extends DefaultRuntimeEventTriggererPort
    {
      public void raiseEvent()
      {
        super.raiseEvent();
      }
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	if (ETP_CELL_CLICKED.equalsIgnoreCase(eventPortID))
		{
			return etpCellClicked;
		}
    	else
    	{
    		int elpCellSize=ETP_CELL.length();
    		if(eventPortID.length()>elpCellSize)
    		{
    			String testName=eventPortID.substring(0,elpCellSize);
    			if(testName.equalsIgnoreCase(ETP_CELL))
    			{
    				String cellNumberText=eventPortID.substring(elpCellSize);
    				int cellNumberValue;
    	    	  
    				try
    				{
    					cellNumberValue = Integer.parseInt(cellNumberText);
    				}
    				catch(NumberFormatException ex)
    				{
    					return null;
    				}
    	    			
    				if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
    				{
    					return etpCellArray[cellNumberValue-1];
    				}
    				else
    				{
    					return null;
    				}
    			}
    		}
    	}

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
    	if (PROP_CAPTION.equalsIgnoreCase(propertyName))
		{
			return propCaption;
		}
    	else if (PROP_ROWS.equalsIgnoreCase(propertyName))
		{
			return propRows;
		}
		else if (PROP_COLUMNS.equalsIgnoreCase(propertyName))
		{
			return propColumns;
		}
		else if (PROP_SCAN_TYPE.equalsIgnoreCase(propertyName))
		{
			return propScanType;
		}
		else if (PROP_TEXT_COLOR.equalsIgnoreCase(propertyName))
		{
			return propTextColor;
		}
		else if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			return propBackgroundColor;
		}
		else if (PROP_SCAN_COLOR.equalsIgnoreCase(propertyName))
		{
			return propScanColor;
		}
		else if (PROP_HOVER_TIME.equalsIgnoreCase(propertyName))
		{
			return propHoverTime;
		}
		else if (PROP_ENABLE_EDIT.equalsIgnoreCase(propertyName))
		{
			return propEnableEdit;
		}		
		else if (PROP_ENABLE_CLICKSELECTION.equalsIgnoreCase(propertyName))
		{
			return propEnableClickSelection;
		}		
		else if (PROP_KEYBOARD_FILE.equalsIgnoreCase(propertyName))
		{
			return propKeyboardFile;
		} 
		else if("displayGUI".equalsIgnoreCase(propertyName))
        {
            return propDisplayGUI;
        }
		else 
		{
			int propCellTextSize=PROP_CELL_TEXT.length();
		    if(propertyName.length()>propCellTextSize)
		    {
		    	String testName=propertyName.substring(0,propCellTextSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_TEXT))
		    	{
		    	  String cellNumberText=propertyName.substring(propCellTextSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	  
		    	  try
		    	  {
		    	    cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	    			
		    	  if(finish==false)
		    	  {
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  return propCellCaptionArray[cellNumberValue-1];
		    		  }
		    	  }
		    	  
		        }
		    }
		    
		    int propImageSize=PROP_CELL_IMAGE.length();
		    if(propertyName.length()>propImageSize)
		    {
		    	String testName=propertyName.substring(0,propImageSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_IMAGE))
		    	{
		    	  String cellNumberText=propertyName.substring(propImageSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	    	  
		    	  try
		    	  {
		    		  cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	  
		    	  if(finish==false)
		    	  {
		    	    			
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  return propCellImageArray[cellNumberValue-1];
		    		  }
		    	  }
		       }
		    }
		    
		    
		    int propCellActionTextSize=PROP_CELL_ACTION_TEXT.length();
		    if(propertyName.length()>propCellActionTextSize)
		    {
		    	String testName=propertyName.substring(0,propCellActionTextSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_ACTION_TEXT))
		    	{
		    	  String cellNumberText=propertyName.substring(propCellActionTextSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	    	  
		    	  try
		    	  {
		    	    cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	    			
		    	  if(finish==false)
		    	  {
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  return propCellTextArray[cellNumberValue-1];
		    		  }
		    	  }
		        }
		    }
		    return null;
		}
    }

    
    
    /**
     * sanity check for matrix size.
     */
	private void checkMatrixSize() {
		if((propRows*propColumns) >= NUMBER_OF_CELLS ) {
			propRows=MAX_MATRIX_ROWS_COLS;
			propColumns=MAX_MATRIX_ROWS_COLS;
		}
	}

    
    
    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	if (PROP_CAPTION.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCaption;
			propCaption = (String)newValue;
			return oldValue;
		}
    	else if (PROP_ROWS.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propRows;
			propRows = Integer.parseInt(newValue.toString());
			if(propRows<0)
			{
				propRows=0;
			}
			if(propRows>MAX_ROWS)
			{
				propRows=MAX_ROWS;
			}
			checkMatrixSize();
			return oldValue;
		}
		else if (PROP_COLUMNS.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propColumns;
			propColumns = Integer.parseInt(newValue.toString());
			if(propColumns<0)
			{
				propColumns=0;
			}
			if(propColumns>MAX_COLUMNS)
			{
				propColumns=MAX_COLUMNS;
			}
			checkMatrixSize();
			return oldValue;
		}
		else if (PROP_SCAN_TYPE.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propScanType;
			propScanType = Integer.parseInt(newValue.toString());
			if(propScanType<0)
			{
				propScanType=0;
			}
			if(propScanType>4)
			{
				propScanType=4;
			}
			return oldValue;
		}
		else if (PROP_TEXT_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTextColor;
			propTextColor = Integer.parseInt(newValue.toString());
			if((propTextColor<0)||(propTextColor>12))
			{
				propTextColor=0;
			}
			return oldValue;
		}
		else if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBackgroundColor;
			propBackgroundColor = Integer.parseInt(newValue.toString());
			if((propBackgroundColor<0)||(propBackgroundColor>12))
			{
				propBackgroundColor=11;
			}
			return oldValue;
		}
		else if (PROP_SCAN_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propScanColor;
			propScanColor = Integer.parseInt(newValue.toString());
			if((propScanColor<0)||(propScanColor>12))
			{
				propScanColor=10;
			}
			return oldValue;
		}
		else if (PROP_HOVER_TIME.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propHoverTime;
			propHoverTime= Integer.parseInt(newValue.toString());
			return oldValue;
		}
	    else if(PROP_ENABLE_EDIT.equalsIgnoreCase(propertyName))
	    {
	    	final Object oldValue = propEnableEdit;
	        if("true".equalsIgnoreCase((String)newValue))
	        {
	        	propEnableEdit = true;
	        }
	        else if("false".equalsIgnoreCase((String)newValue))
	        {
	        	propEnableEdit = false;
	        }
	        return oldValue;
	    }    	
	    else if(PROP_ENABLE_CLICKSELECTION.equalsIgnoreCase(propertyName))
	    {
	    	final Object oldValue = propEnableClickSelection;
	        if("true".equalsIgnoreCase((String)newValue))
	        {
	        	propEnableClickSelection = true;
	        }
	        else if("false".equalsIgnoreCase((String)newValue))
	        {
	        	propEnableClickSelection = false;
	        }
	        return oldValue;
	    }    	
		else if (PROP_KEYBOARD_FILE.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propKeyboardFile;
			propKeyboardFile= (String) newValue;
			//also set the xmlFile variable to the new value, so the next time the cellboard can be loaded from the file
			xmlFile=propKeyboardFile;
			return oldValue;
		}
		else if("displayGUI".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propDisplayGUI;

            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = false;
            }
            return oldValue;
        }    	
		else 
		{
			int propCellTextSize=PROP_CELL_TEXT.length();
		    if(propertyName.length()>propCellTextSize)
		    {
		    	String testName=propertyName.substring(0,propCellTextSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_TEXT))
		    	{
		    	  String cellNumberText=propertyName.substring(propCellTextSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	    	  
		    	  try
		    	  {
		    	    cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	    			
		    	  if(finish==false)
		    	  {
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  final String oldValue = propCellCaptionArray[cellNumberValue-1];         
		    			  propCellCaptionArray[cellNumberValue-1]=(String)newValue;
		    			  return oldValue;
		    		  }
		    	  
		    	  }
		        }
		    }
		    
		    int propImageSize=PROP_CELL_IMAGE.length();
		    if(propertyName.length()>propImageSize)
		    {
		    	String testName=propertyName.substring(0,propImageSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_IMAGE))
		    	{
		    	  String cellNumberText=propertyName.substring(propImageSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	    	  
		    	  try
		    	  {
		    		  cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	    			
		    	  if(finish==false)
		    	  {
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  final String oldValue = propCellImageArray[cellNumberValue-1];   
		    			  propCellImageArray[cellNumberValue-1]=(String)newValue;
		    			  return oldValue;
		    		  }
		    	  
		    	  }
		       }
		    }
		    
		    int propCellActionTextSize=PROP_CELL_ACTION_TEXT.length();
		    if(propertyName.length()>propCellActionTextSize)
		    {
		    	String testName=propertyName.substring(0,propCellActionTextSize);
		    	if(testName.equalsIgnoreCase(PROP_CELL_ACTION_TEXT))
		    	{
		    	  String cellNumberText=propertyName.substring(propCellActionTextSize);
		    	  int cellNumberValue=0;
		    	  boolean finish=false;
		    	    	  
		    	  try
		    	  {
		    	    cellNumberValue = Integer.parseInt(cellNumberText);
		    	  }
		    	  catch(NumberFormatException ex)
		    	  {
		    		  finish=true;
		    	  }
		    	    			
		    	  if(finish==false)
		    	  {
		    		  if(cellNumberValue>0 && cellNumberValue<=NUMBER_OF_CELLS)
		    		  {
		    			  final String oldValue = propCellTextArray[cellNumberValue-1];         
		    			  propCellTextArray[cellNumberValue-1]=(String)newValue;
		    			  return oldValue;
		    		  }
		    	  
		    	  }
		        }
		    }
		    return null;
		}
    }

    /**
     * Get the list of available Keyboard xml files from ARE
     */
    public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>();
		res.add("");
		try
		{
			if (key.equals(PROP_KEYBOARD_FILE))
			{
				List<File> files = ComponentUtils.findFiles(new File("data/cellBoardKeyboards"), ".xml", 200);
				for (File file : files)
				{
					res.add(file.getName()); //.getPath().substring(file.getPath().indexOf("set")));
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			AstericsErrorHandling.instance.getLogger().warning("Could not search for keyboard files: "+e.getMessage());
		}
		return res;
	} 

    

     /**
      * Event Listener Ports.
      */
	final IRuntimeEventListenerPort elpScanMove = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(guiReady)
			{
				gui.scanMove();
			}
		}
	};
	
	final IRuntimeEventListenerPort elpScanSelect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(guiReady)
			{
				gui.scanSelect();
			}
		}
	};
	
	final IRuntimeEventListenerPort elpMoveUp = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{	 
			if(guiReady)
			{
				gui.scanSelectionMove(GUI.ScanSelectionDirection.up);
			}
		}
	};
	
	final IRuntimeEventListenerPort elpMoveRight = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(guiReady)
			{
				gui.scanSelectionMove(GUI.ScanSelectionDirection.right);
			}	 
		}
	};
	
	final IRuntimeEventListenerPort elpMoveLeft = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(guiReady)
			{
				gui.scanSelectionMove(GUI.ScanSelectionDirection.left);
			}
		}
	};
	
	final IRuntimeEventListenerPort elpMoveDown = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(guiReady)
			{
				gui.scanSelectionMove(GUI.ScanSelectionDirection.down);
			}
		}
	};
	
	private Dimension getAvailableSpace() {
		return AREServices.instance.getAvailableSpace(this);
	}
	
	private void reportError(String msg) {
		AstericsErrorHandling.instance.reportError(this,msg);
	}
	
	final IRuntimeEventListenerPort elpLoad = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			loadXmlFile();
		}

	};

	private void loadXmlFile() {
		if(xmlFile==null || "".equals(xmlFile))
		{
			AstericsErrorHandling.instance.getLogger().fine("CellBoard: no xmlFile for keyboard set");
			return;
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			XMLCellBoardLoader handler =  new XMLCellBoardLoader(NUMBER_OF_CELLS,this);
			saxParser.parse( new File(FILE_PATH_PREFIX,xmlFile), handler );
			
			
			checkMatrixSize();
			
			if (propRows == -1 || propColumns == -1) {
				reportError("Error parsing rows or cols attribute of CellBoard plugin");
				return;
			}
			/*
			int hheight = handler.getHeight();
			if (hheight >= 0)
				space.height = hheight;
			else 
				space.height = getAvailableSpace().height;
			int hwidth = handler.getWidth();
			if (hwidth >= 0) 
				space.width = hwidth;
			else 
				space.width = getAvailableSpace().width;
			*/
		    paintCells.run();
			gui.update(space,propFontSize);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	 /**
     * saves current cell board to an xml file.
     * @param fileName name (and path) of the xml file to be created
     */
	public void saveXmlFile(String fileName)
	{
		XMLCellBoardWriter writer = new XMLCellBoardWriter(this);
		writer.writeXML(fileName);
	}
		
	 /**
     * Returns the color of the text.
     * @return   color of the text
     */
	int getTextColor()
	{
		return propTextColor;
	}
	
	/**
     * Returns the background color.
     * @return   color of the background
     */
	int getBackgroundColor()
	{
		return propBackgroundColor;
	}
	
	/**
     * Returns the background color for the active cell during scanning.
     * @return   color of the active cell background
     */
	int getScanColor()
	{
		return propScanColor;
	}
	
	/**
     * Returns the number of the rows.
     * @return   number of the rows
     */
	int getRowCount()
	{
		return propRows+1;
	}
	
	/**
     * Returns the number of the columns.
     * @return   number of the columns
     */
	int getColumnCount()
	{
		return propColumns+1;
	}
	
	
	/**
     * Returns the component caption.
     * @return   caption of the component
     */
	String getCaption()
	{
		return propCaption;
	}

	boolean getEnableEdit()
	{
		return propEnableEdit;
	}
	
	boolean getEnableClickSelection()
	{
		return propEnableClickSelection;
	}
	
	boolean getDisplayGUI()
	{
		return propDisplayGUI;
	}

	
	/**
     * Returns the image path of the cell defined by index.
     * @param index index of the cell
     * @return   image path
     */
	String getImagePath(int index)
	{
		return propCellImageArray[index];
	}

	/**
     * Sets the image path of the cell defined by index.
     * @param index index of the cell
     * @param path path of the cell image file
     */
	void setImagePath(int index, String path)
	{
		propCellImageArray[index]=path;
	}

	/**
     * Returns the path of the sound file of the cell defined by index.
     * @param index index of the cell
     * @return   sound file path
     */
	String getSoundPath(int index)
	{
		return propCellSoundArray[index];
	}

	/**
     * Sets the image path of the cell defined by index.
     * @param index index of the cell
     * @param path  path of the sound file
     */
	void setSoundPath(int index, String path)
	{
		propCellSoundArray[index]=path;
	}


	/**
     * Returns the path of the sound preview file of the cell defined by index.
     * @param index index of the cell
     * @return   preview sound file path
     */
	String getSoundPreviewPath(int index)
	{
		return propCellSoundPreviewArray[index];
	}

	/**
     * Sets the preview sound file path of the cell defined by index.
     * @param index index of the cell
     * @param path  path of the preview sound file
     */
	void setSoundPreviewPath(int index, String path)
	{
		propCellSoundPreviewArray[index]=path;
	}
	
	/**
     * Returns the text of the cell defined by index.
     * @param index index of the cell
     * @return   text of the cell
     */
	String getCellCaption(int index)
	{
		return propCellCaptionArray[index];
	}

	/**
     * Sets the caption of the cell defined by index.
     * @param index index of the cell
     * @param text caption of the cell
     */
	void setCellCaption(int index, String text)
	{
		propCellCaptionArray[index]=text;
	}
	
	/**
     * Returns the action text of the cell defined by index.
     * @param index index of the cell
     * @return   action text of the cell
     */
	String getCellText(int index)
	{
		return propCellTextArray[index];
	}
	
	/**
     * Sets the action text of the cell defined by index.
     * @param index index of the cell
     * @param text action text of the cell
     * @return   action text of the cell
     */
	void setCellText(int index, String text)
	{
		propCellTextArray[index]=text;
	}
	
	/**
     * Returns type of the scanning.
     * @return   scanning type
     */
	int getScanType()
	{
		return propScanType;
	}
	
	/**
     * Returns the general event port.
     * @return   general event port
     */
	IRuntimeEventTriggererPort getGeneralEventPort()
	 {
		 return etpCellClicked;
	 }
	
	/**
     * Returns the event port for the cell defined by index.
     * @param index index of the cell
     * @return   event port
     */
	EventPort getEventPort(int index)
	 {
		 return etpCellArray[index];
	 }

	/**
	  * Returns the hover time.
	  * @return   hover time.
	  */
	int getHoverTime()
	 {
		 return propHoverTime;
	 }

	
	 /**
	  * getter methods for the plugin's output ports 
	  */
	IRuntimeOutputPort getSelectedCellOutputPort()
	 {
		 return opSelectedCell;
	 }

	IRuntimeOutputPort getSelectedCellCaptionOutputPort()
	 {
		 return opSelectedCellCaption;
	 }

	IRuntimeOutputPort getSelectedCellTextOutputPort()
	 {
		 return opSelectedCellText;
	 }

	IRuntimeOutputPort getActCellOutputPort()
	 {
		 return opActCell;
	 }

	IRuntimeOutputPort getActCellCaptionOutputPort()
	 {
		 return opActCellCaption;
	 }

	IRuntimeOutputPort getActCellTextOutputPort()
	 {
		 return opActCellText;
	 }


     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  space = AREServices.instance.getAvailableSpace(this);
    	  gui = new GUI(this,space);
    	  if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
		  if(xmlFile!=null && !"".equals(xmlFile)) {
			  loadXmlFile();			  
		  } else {
			 paintCells.run();
		  }
		  
		  //AstericsThreadPool.instance.execute(paintCells);
		  //SwingUtilities.invokeLater(paintCells);
		  //gui.defineTextFontSize();
		  //gui.setScanning();
          super.start();
          //guiReady=true;
          //gui.repaintCells();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {

    	  gui.prepareToClose();
    	  guiReady=false;
    	  AREServices.instance.displayPanel(gui, this, false);
    	  gui=null;
          super.stop();
      }
      
      
      private final Runnable paintCells = new Runnable(){
  		
    		/**
    	     * Thread function.
    	     */	
    		@Override
    		public void run() {
    	        	gui.defineTextFontSize(propFontSize);
					gui.setScanning();
			        guiReady=true;
			        gui.repaintCells();
    			}

    		};
}