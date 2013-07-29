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

import eu.asterics.component.processor.oska.OskaHighlighter.HighlightMode;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * OskaInputs encapsulates all inputs to the OSKA plug-in. This class only 
 * works as a container and provides no additional functions. 
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *
 */
class OskaInputs {
	
	private static boolean eventScanningEnabled = true;
	private static boolean valueScanningEnabled = true;
	private static boolean useFloatRange;

	/**
	 * Enables or disables all event listener ports that move the highlighter 
	 * @param enable if true enabled, disabled otherwise
	 */
	static void enableEventScanningInputs(boolean enable)
	{
		eventScanningEnabled = enable;
	}

	/**
	 * Enables or disables all input ports that move the highlighter 
	 * @param enable if true enabled, disabled otherwise
	 */
	static void enableValueScanningInputs(boolean enable)
	{
		valueScanningEnabled = enable;
	}

	/**
	 * Enums for deciding which way to highlight on the OSKA grid
	 * @author Christoph Weiss [weissch@technikum-wien.at]
	 *
	 */
    private enum HighlightingMode
    {
    	HL_COL,
    	HL_ROW,
    	HL_KEY
    }
	
	InputPortOskaCommand ipWavefile = new InputPortOskaCommand("Play");
	InputPortOskaCommand ipSpeak = new InputPortOskaCommand("Speak");
	
	InputPortPos ipPosColumnHighlighter 
		= new InputPortPos(0, HighlightingMode.HL_COL);
	InputPortPos ipPosRowHighlighter 	
		= new InputPortPos(0, HighlightingMode.HL_ROW);
	InputPortPos ipPosColumn 			
		= new InputPortPos(0, HighlightingMode.HL_KEY); 
	InputPortPos ipPosRow 				
		= new InputPortPos(1, HighlightingMode.HL_KEY);
	IRuntimeInputPort ipPosKey 			
		= new InputPortPosKey();
		
	IRuntimeEventListenerPort elpPress = new RuntimeEventListenerPortPress();
	IRuntimeEventListenerPort [] elpSwitchpress = { 
			new RuntimeEventListenerPortSwitchPress(0), 
			new RuntimeEventListenerPortSwitchPress(1) 
	};
	
	IRuntimeEventListenerPort elpSwitch = new RuntimeEventListenerPortSwitch();

	IRuntimeEventListenerPort elpIncreaseScanSpeed = 
		new RuntimeEventListenerPortScanSpeedChange(true);
	IRuntimeEventListenerPort elpDecreaseScanSpeed = 
		new RuntimeEventListenerPortScanSpeedChange(false);

	
	private IRuntimeEventListenerPort elpHighlightColumnNext = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_COL, 0, true);

	private IRuntimeEventListenerPort elpHighlightColumnPrev = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_COL, 0, false);
	
	IRuntimeEventListenerPort elpHighlightKeyNextY = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_KEY, 1, true);
 
	IRuntimeEventListenerPort elpHighlightKeyPrevY = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_KEY, 1, false);

	IRuntimeEventListenerPort elpHighlightKeyNextX = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_KEY, 0, true);

	IRuntimeEventListenerPort elpHighlightKeyPrevX = new 
	  RuntimeEventListenerHighlightGridPosition(HighlightingMode.HL_KEY, 0, false);
	
	
	IRuntimeEventListenerPort elpHighlightNext = new 
		RuntimeEventListenerSwitchingHighlighter(true);
	IRuntimeEventListenerPort elpHighlightPrev = new 
		RuntimeEventListenerSwitchingHighlighter(false);
	
	/**
	 * Input port that is created for a specific command and will receive 
	 * strings on its input port which are used as parameters for the command
	 * 
	 * @author weissch
	 *
	 */
    private class InputPortOskaCommand extends DefaultRuntimeInputPort
    {
    	String cmd;
    	
    	/**
    	 * Creates the port, saving the command to be used
    	 * @param cmd
    	 */
    	public InputPortOskaCommand(String cmd)
    	{
    		this.cmd = cmd;
    	}
    	
    	/**
    	 * Called with new data and creates the command string and sends
    	 * it to OSKA
    	 */
        public void receiveData(byte[] data)
        {
        	StringBuffer buf = new StringBuffer();
        	buf.append(cmd);
        	buf.append(":");
        	buf.append(new String(data));
        	
        	OskaInstance.instance.communication.sendToOska(buf.toString());
        }

		
    }
    
    /**
     * Input port which highlights one cell, row or column in an OSKA grid 
     * @author weissch
     *
     */
    private class InputPortPos extends DefaultRuntimeInputPort
    {
    	HighlightingMode mode;
    	int pos; 
    	
    	/**
    	 * Constructs the port and saves whether it should work on the x or 
    	 * the y axis or on both
    	 * @param pos 
    	 * @param cmd
    	 */
    	public InputPortPos(int pos, HighlightingMode mode)
    	{
    		this.pos = pos;
    		this.mode = mode;
    	}
    	
    	
    	/**
    	 * Receives input between 0 and 1 and maps the input to the correct 
    	 * highlight position in the grid
    	 */
        public void receiveData(byte[] data)
        {
        	if (valueScanningEnabled)
        	{
	        	double input = ConversionUtils.doubleFromBytes(data);
	        	
	        	switch (mode)
	        	{
	        	case HL_COL: 
	        		OskaInstance.instance.highlighter.highlightColumn(input);
	        		break;
	        	case HL_ROW: 
	        		OskaInstance.instance.highlighter.highlightRow(input);
	        		break;
	        	case HL_KEY: 
	        		if (pos == 0)
	            		OskaInstance.instance.highlighter.highlightCellX(input);
	        		else
	            		OskaInstance.instance.highlighter.highlightCellY(input);
	        		break;
	        	}
        	}
        }


	
    }

    /**
     * Input port implementation which handles column and cell highlighting in 
     * a two step process.
     * @author weissch
     *
     */
    private class InputPortPosKey extends DefaultRuntimeInputPort
    {
    	/**
    	 * Forwards the incoming data to either the column highlighting port or
    	 * to the cell highlighter depending on scan state 
    	 */
        public void receiveData(byte[] data)
        {
        	if (valueScanningEnabled)
        	{
	        	switch (onePosInputState)
	        	{
	        	case 0:
	        		ipPosColumnHighlighter.receiveData(data);
	        		break;
	        	case 1:
	        		ipPosRow.receiveData(data);
	        		break;
	        	}
        	}
        }

	
    }

	int onePosInputState = 0;    
    
    /**
     * Event listener which switches between scanning states. Depending on the 
     * state of the scanning state machine, it either switches from column 
     * highlighting to cell highlighting or it issues the press of the 
     * highlighted cell.
     * @author weissch
     *
     */
	private class RuntimeEventListenerPortSwitch implements IRuntimeEventListenerPort
	{
		/**
		 * Selects the way of handling the input dependent on state of scanning
		 * state machine
		 */
		public void receiveEvent(final String data)
	   	{
			switch (onePosInputState)
			{
			case 0:
				AstericsErrorHandling.instance.reportDebugInfo(
						OskaInstance.instance, "Switch event in onePos state 0");
				
				onePosInputState = 1;
				OskaInstance.instance.highlighter.highlightCellY(0);
				break;
			case 1:
//				AstericsErrorHandling.instance.getLogger()
//					.info("Switch event in onePos state 1");
				elpPress.receiveEvent(data);
				OskaInstance.instance.highlighter
					.resetHighlightingPos(HighlightMode.HIGHLIGHT_COL);
				onePosInputState = 0;
				break;
			}
	   	}
	}
    
	/**
	 * An event input port which causes the transfer of 
	 * @author weissch
	 *
	 */
	private class RuntimeEventListenerPortPress implements IRuntimeEventListenerPort
	{
		/**
		 * Reacts to incoming events by send the press command to OSKA
		 */
		public void receiveEvent(final String data)
	   	{
			AstericsErrorHandling.instance.getLogger()
					.info("Received press event");
			OskaInstance.instance.communication.sendPressCommand();
			
			
		    OskaInstance.instance.highlighter.resetHighlightingPos(HighlightMode.HIGHLIGHT_CELL);
	   	}
	}	

	/**
	 * Event input port which causes the transfer of a switch press command to
	 * OSKA. This is needed for OSKA's internal scanning feature. Receives a 
	 * switch number upon construction which will indicate which switch to press 
	 * in the command.
	 * @author weissch
	 *
	 */
	private class RuntimeEventListenerPortSwitchPress 
		implements IRuntimeEventListenerPort
	{
		int key;
		
		/**
		 * Constructs the port and stores the switch number
		 * @param owner the parent instance
		 * @param key the number of the switch to be pressed (1 or 2)
		 */
		public RuntimeEventListenerPortSwitchPress(int key)
		{
			this.key = key;
		}
		
		/**
		 * Incoming events will cause transfer of the command 
		 * "SwitchPress:[key]"
		 */
		public void receiveEvent(final String data)
	   	{
//			OskaInstance.instance.communication.useDoubleQuotes = false;
			AstericsErrorHandling.instance.getLogger()
				.info("Received switch press event");
			OskaInstance.instance.communication
				.sendToOska("SwitchPress:" + key);
//			OskaInstance.instance.communication.useDoubleQuotes = true;
	   	}
	}	

	/**
	 * Event input port which effects the increase or decrease of the internal 
	 * scan speed of OSKA. Incoming events will cause the "SetSpeed" command to
	 * be sent to OSKA. 
	 * @author weissch
	 *
	 */
	private class RuntimeEventListenerPortScanSpeedChange 
		implements IRuntimeEventListenerPort
	{
		boolean increase; 
		
		/**
		 * Constructs the port and stores whether events increase or decrease
		 * the speed.
		 * @param owner the parent component
		 * @param increase if true, speed will be increase, decreased otherwise
		 */
		public RuntimeEventListenerPortScanSpeedChange(boolean increase)
		{
			this.increase = increase;
		}
		
		/**
		 * On incoming events the speed property is incremented or decremented
		 * and the command is sent to OSKA
		 */
		public void receiveEvent(final String data)
	   	{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					"Received scan speed change event: " +  
					(increase ? "increase" : "decrease"));
			
			if (increase)
			{
				OskaInstance.instance.increaseScanSpeed();
			}
			else
			{
				OskaInstance.instance.decreaseScanSpeed();
			}
	   	}
	}

	/**
	 * An event listener port which will cause key highlighting in OSKA to 
	 * switch to another grid position. Can be configured to highlight rows,
	 * columns and cells.
	 * @author weissch
	 *
	 */
	private class RuntimeEventListenerHighlightGridPosition 
	implements IRuntimeEventListenerPort
	{
	    	HighlightingMode mode;
	    	int pos; 
	    	boolean up;

	    /**
	     * Constructs the port and stores necessary information
	     * @param mode the highlighting mode that will be used upon switching
	     * @param pos necessary for key highlighting, if 0 the highlight box 
	     * will move horizontally, if 1 vertically
	     * @param up if true the highlight will switch to the next position, to
	     * the previous otherwise 
	     */
		public RuntimeEventListenerHighlightGridPosition(HighlightingMode mode,
				int pos, boolean up)
		{
			this.mode = mode;
			this.pos = pos;
			this.up = up;
		}
		
		/**
		 * Incoming events will cause the highlighting to switch to the next 
		 * position according to the configuration
		 */
		public void receiveEvent(final String data)
	   	{
			if (eventScanningEnabled)
			{
	        	switch (mode)
	        	{
	        	case HL_COL:
	        		if (up)
	        			OskaInstance.instance.highlighter.highlightNextColumn();
	        		else 
	        			OskaInstance.instance.highlighter.highlightPrevColumn();
	        		break;
	        	case HL_ROW: 
	        		if (up)
	        			OskaInstance.instance.highlighter.highlightNextRow();
	        		else 
	        			OskaInstance.instance.highlighter.highlightPrevRow();
	        		break;
	        	case HL_KEY: 
	        		if (up)
	        			OskaInstance.instance.highlighter.highlightNextKey(pos);
	        		else 
	        			OskaInstance.instance.highlighter.highlightPrevKey(pos);
	        		break;
	        	}
			}
	   	}
	}	
	
	/**
	 * An event listener port for one dimensional scanning using events. Works
	 * together the RuntimeEventListenerPortSwitch class. First skips through
	 * the columns and after a press through the keys of the selected column.
	 * 
	 * @author weissch
	 *
	 */
	private class RuntimeEventListenerSwitchingHighlighter 
	implements IRuntimeEventListenerPort
	{
	    	boolean up;
		
	    /**
	     * Constructs the port
	     * @param up selects the direction, up if true, down otherwise  
	     */
		public RuntimeEventListenerSwitchingHighlighter(boolean up)
		{
			this.up = up;
		}
		
		/**
		 * Incoming events will cause the highlighting to switch to the next 
		 * position according to the configuration and the state of the one 
		 * dimensional scanning state machine
		 * 
		 */
		public void receiveEvent(final String data)
	   	{
			if (eventScanningEnabled)
			{
				switch (onePosInputState)
				{
				case 0:
					if (up)
						elpHighlightColumnNext.receiveEvent(data);
					else
						elpHighlightColumnPrev.receiveEvent(data);
					break;
				case 1:
					if (up)
						elpHighlightKeyNextY.receiveEvent(data);
					else
						elpHighlightKeyPrevY.receiveEvent(data);
					break;
				}
			}
	   	}
	}
}

