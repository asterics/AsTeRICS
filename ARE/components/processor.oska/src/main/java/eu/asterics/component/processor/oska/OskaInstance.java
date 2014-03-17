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

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.RemoteConnectionManager;

/**
 * OskaInstance is a component which communicates with the external OSKA player
 * application. It uses the TCP connection that OSKA tries to set up during
 * start up to communicate. Communication is bidirectional allowing the plugin
 * to operate the highlighting on the displayed grid in OSKA as well as having
 * OSKA transfer commands back to the plugin
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *         Date: March 2, 2011
 *         Time: 10:22:08 AM
 */
public class OskaInstance extends AbstractRuntimeComponentInstance
{
	static boolean oskaStarted = false;
	static OskaInstance instance;
	
	OskaHighlighter highlighter = null;
	OskaCommunication communication = null;
	OskaCommandManager commandManager = null;

	// properties
	int propPort = 4546;
	int propScanSpeed = 0;
	int propHighlightStyle = -1;
	String propTitle = "OSKA-ARE Sample Communication";
	String propOskaPath = "";
	String propKbdPath = "";
	String propSettingsFile = "";
	int [] propKeyboardPos = { 0, 0 };
	boolean propWindowDecorated = true;
	
	// internals
	Process process = null;
	
	// port wrappers
	OskaInputs  inputs  = new OskaInputs();
	OskaOutputs outputs = new OskaOutputs();
	private int propHighlightResetPosition;
	private boolean propEventScanningEnabled;
	private boolean propValueScanningEnabled;
	private int propInputRange;
	private boolean propResizeEnabled;
	
	/**
	 * Constructs the OSKA component instance
	 */
    public OskaInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    	//instance = this;
    }

    /**
     * Starts the component, sets up connection to OSKA, starts OSKA if
     * necessary
     */
    public void start()
    {
    	instance = this;
    	highlighter = new OskaHighlighter();
		communication = new OskaCommunication();
    	commandManager = new OskaCommandManager();
    	
    	commandManager.addCommand("SIZE", 
    			new OskaCommandSizeHandler());
    	commandManager.addCommand("KEYBOARDLOADED",
    			new OskaCommandKeyboardLoadedHandler());
    	
    	commandManager.addActionString("@ARE", new OskaCommandAtAreHandler());
    	commandManager.addActionString("@KBD", new OskaCommandAtKbdHandler());
    	commandManager.addActionString("@OSKA", new OskaCommandAtOskaHandler());
    	commandManager.addActionString("SendTCPMessage", 
    			new OskaActionStringSendTCPMessageHandler());
    	
    	if (!communication.openConnection(propPort))
    	{
	    	try
	    	{
	    		if (!oskaStarted)
	    		{
	    			StringBuffer buf = new StringBuffer();
	    			buf.append(propSettingsFile)
	    			.append("\" \"")
	    			.append(propKbdPath)
	    			.append("\" ");

	    			Point pos = AREServices.instance.getComponentPosition(this);
	    	    	Dimension d = AREServices.instance.getAvailableSpace(this);
	    			
	    	    	if (propResizeEnabled)
	    	    		buf.append(String.format("Position(%d,%d,%d,%d", pos.x, pos.y, d.width, d.height));
	    	    	else
	    	    		buf.append(String.format("Position(%d,%d", pos.x, pos.y));
	    	    	
	    			if (!propWindowDecorated)
	    			{
	    				buf.append(",-");
	    			}
	    			buf.append(")");
		    		AstericsErrorHandling.instance.reportInfo(this, 
		    				"Started OSKA with: " + buf.toString());	
		    		String[] b = {propOskaPath,buf.toString()};
//	    			process = Runtime.getRuntime().exec(buf.toString());
	    			process = Runtime.getRuntime().exec(b);
	    			oskaStarted = true;
	    		}

	        	
	    	}
	    	catch (IOException e)
	    	{
	    		AstericsErrorHandling.instance.reportError(this, 
	    				"IOException while starting OSKA - make sure OSKA is installed in teh correct path.");
	    	}
	    	catch (IllegalArgumentException e)
	    	{
	    		AstericsErrorHandling.instance.reportInfo(this, 
	    				"oskaPath property is empty"+ propOskaPath);
	    	}
    	}
        super.start();
        
    }

    /**
     * Pauses the component, does nothing
     */
    public void pause()
    {
        super.pause();
    }

    /**
     * Resumes the component, does nothing
     */
    public void resume()
    {
    	instance = this;
    	super.resume();
    }

    /**
     * Stops the component, closes OSKA and the remote connection
     */
    public void stop()
    {
        super.stop();
        if (process != null)
        {
        	process.destroy();
        	process = null;
        }
		try
		{
			communication.closeOska();
			oskaStarted = false;
		}
		catch (Exception e)	{ }        
        RemoteConnectionManager.instance
        	.closeConnection(Integer.toString(propPort));
    	highlighter = null;
		communication = null;
    	commandManager = null;
        instance = null;
    }
    
    /**
     * Performs all the initialization necessary after the startup of OSKA
     */
    public void initializeOska()
    {
/*
    	try {
            Thread.sleep(2000);
        } catch (Exception e) { }
    	communication.loadKeyboard(propKbdPath);
*/    	
    	communication.sendToOska("SIZE");
    	communication.setTitle(propTitle);
    	
    	highlighter.initHighlighter();
    	highlighter.setScanSpeed(propScanSpeed);
    	highlighter.setHighlightingStyle(propHighlightStyle);
    	highlighter.setResetPosition(propHighlightResetPosition);
		highlighter.setInputRange(propInputRange);

    }
    

	/**
	 * Returns an input port for a specified portID
	 * @param portID the ID of the requested port
	 * @return the requested port instance
	 */
    public IRuntimeInputPort getInputPort(String portID)
    {
    	if("wavefile".equalsIgnoreCase(portID))
        {
            return inputs.ipWavefile;
        }
    	else if("speak".equalsIgnoreCase(portID))
        {
            return inputs.ipSpeak;
        }
    	else if("posColumn".equalsIgnoreCase(portID))
        {
            return inputs.ipPosColumn;
        }
    	else if("posRow".equalsIgnoreCase(portID))
        {
            return inputs.ipPosRow;
        }
    	else if("posKey".equalsIgnoreCase(portID))
        {
            return inputs.ipPosKey;
        }
    	else if("posRowHighlight".equalsIgnoreCase(portID))
        {
            return inputs.ipPosRowHighlighter;
        }
    	return null;
    }

	/**
	 * Returns an output port for a specified portID
	 * @param portID the ID of the requested port
	 * @return the requested port instance
	 */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
    	if("action".equalsIgnoreCase(portID))
        {
            return outputs.opAction;
        }
    	else if("keycodes".equalsIgnoreCase(portID))
        {
            return outputs.opKeycodes;
        }

    	return null;
    }

	/**
	 * Returns an event listener port for a specified portID
	 * @param eventPortID the ID of the requested port
	 * @return the requested port instance
	 */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
    	if (eventPortID.equalsIgnoreCase("press"))
    	{
    		return inputs.elpPress;    		
    	}
    	else if (eventPortID.equalsIgnoreCase("pressSwitch1"))
    	{
    		return inputs.elpSwitchpress[0];
    	}
    	else if (eventPortID.equalsIgnoreCase("pressSwitch2"))
    	{
    		return inputs.elpSwitchpress[1];
    	}
    	else if (eventPortID.equalsIgnoreCase("switch"))
    	{
    		return inputs.elpSwitch;
    	}
    	else if (eventPortID.equalsIgnoreCase("increaseScanspeed"))
    	{
    		return inputs.elpIncreaseScanSpeed;
    	}
    	else if (eventPortID.equalsIgnoreCase("decreaseScanspeed"))
    	{
    		return inputs.elpDecreaseScanSpeed;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightNext"))
    	{
    		return inputs.elpHighlightNext;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightPrev"))
    	{
    		return inputs.elpHighlightPrev;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightNextX"))
    	{
    		return inputs.elpHighlightKeyNextX;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightPrevX"))
    	{
    		return inputs.elpHighlightKeyPrevX;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightNextY"))
    	{
    		return inputs.elpHighlightKeyNextY;
    	}
    	else if (eventPortID.equalsIgnoreCase("highlightPrevY"))
    	{
    		return inputs.elpHighlightKeyPrevY;
    	}
    	return null;
    }
    
	/**
	 * Returns a property for a specified property name
	 * @param propertyName the ID of the requested property
	 * @return the value of the property 
	 */
    public Object getRuntimePropertyValue(String propertyName)
    {
    	
    	if ("port".equalsIgnoreCase(propertyName))
    	{
    		return propPort;
    	}
    	else if ("title".equalsIgnoreCase(propertyName))
    	{
    		return propTitle;
    	}
    	else if ("oskaPath".equalsIgnoreCase(propertyName))
    	{
    		return propOskaPath;
    	}
    	else if ("keyboardPath".equalsIgnoreCase(propertyName))
    	{
    		return propKbdPath;
    	}
    	else if ("scanSpeed".equalsIgnoreCase(propertyName))
    	{
    		return propScanSpeed;
    	}
    	else if ("windowDecorated".equalsIgnoreCase(propertyName))
    	{
    		return propWindowDecorated;
    	}
    	else if ("highlightResetPosition".equalsIgnoreCase(propertyName))
    	{
    		return propHighlightResetPosition;
    	}
    	else if ("inputRange".equalsIgnoreCase(propertyName))
    	{
    		return propInputRange;
    	}
    	else if ("resizeEnabled".equalsIgnoreCase(propertyName))
    	{
    		return propResizeEnabled;
    	}
    	
        return null;
    }
  
	/**
	 * Sets the value of a property for a specified property name
	 * @param propertyName the ID of the requested property
	 * @param newValue the new value of the property as an object
	 * @return null 
	 */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	if ("port".equalsIgnoreCase(propertyName))
    	{
    		propPort = Integer.parseInt((String) newValue);
    	}
    	else if ("title".equalsIgnoreCase(propertyName))
    	{
    		propTitle = (String) newValue;
    		if (communication != null)
    		{
    			communication.setTitle(propTitle);
    		}
    		
    	}
    	else if ("oskaPath".equalsIgnoreCase(propertyName))
    	{
    		propOskaPath = (String) newValue;
    	}
    	else if ("keyboardPath".equalsIgnoreCase(propertyName))
    	{
    		propKbdPath = (String) newValue;
    		if (communication != null)
    		{
    			communication.loadKeyboard(propKbdPath);
    		}
    	}
    	else if ("scanSpeed".equalsIgnoreCase(propertyName))
    	{
    		propScanSpeed = Integer.parseInt((String) newValue);
    		if (highlighter != null)
    		{
    			highlighter.setScanSpeed(propScanSpeed);
    		}
    	}
    	else if ("settingsFile".equalsIgnoreCase(propertyName))
    	{
    		propSettingsFile = (String) newValue;
    	}
    	else if ("highlightStyle".equalsIgnoreCase(propertyName))
    	{
    		propHighlightStyle =Integer.parseInt((String) newValue);
    		if (highlighter != null)
    		{
    			highlighter.setHighlightingStyle(propHighlightStyle);
    		}
    	}
    	else if ("windowDecorated".equalsIgnoreCase(propertyName))
    	{
            if("true".equalsIgnoreCase((String)newValue))
            	propWindowDecorated= true;
            else
            	propWindowDecorated= false;
    	}
    	else if ("eventScanningEnabled".equalsIgnoreCase(propertyName))
    	{
            if("true".equalsIgnoreCase((String)newValue))
            	propEventScanningEnabled= true;
            else
            	propEventScanningEnabled= false;
            OskaInputs.enableEventScanningInputs(propEventScanningEnabled);
    	}
    	else if ("valueScanningEnabled".equalsIgnoreCase(propertyName))
    	{
            if("true".equalsIgnoreCase((String)newValue))
            	propValueScanningEnabled= true;
            else
            	propValueScanningEnabled= false;
            OskaInputs.enableValueScanningInputs(propValueScanningEnabled);
    	}
    	else if ("highlightResetPosition".equalsIgnoreCase(propertyName))
    	{
    		propHighlightResetPosition = Integer.parseInt((String) newValue);
    		if (highlighter != null)
    		{
    			highlighter.setResetPosition(propHighlightResetPosition);
    		}
    	}
    	else if ("inputRange".equalsIgnoreCase(propertyName))
    	{
    		propInputRange = Integer.parseInt((String) newValue);
    	}
    	else if ("resizeEnabled".equalsIgnoreCase(propertyName))
    	{
            if("true".equalsIgnoreCase((String)newValue))
            	propResizeEnabled= true;
            else
            	propResizeEnabled= false;
    	}
        return null;
    }
    
    /**
     * Decreases the internal scanning speed
     * @return true if message was sent successfully to OSKA, false otherwise
     */
	boolean decreaseScanSpeed()
	{
		if (propScanSpeed < 50) // RO: changed from 10 to 50, 10 was in specifications, but to fast for some users
			propScanSpeed++;

		if (highlighter != null)
			return highlighter.setScanSpeed(propScanSpeed);
		return false;
	}
	
    /**
     * Increases the internal scanning speed
     * @return true if message was sent successfully to OSKA, false otherwise
     */
	boolean increaseScanSpeed()
	{
		if (propScanSpeed > 1)
			propScanSpeed--;

		if (highlighter != null)
			return highlighter.setScanSpeed(propScanSpeed);
		return false;
	}

	@Override
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) 
	{		
		for (int i = 1; i <= outputs.etpEventOut.length; i++)
		{
			if (eventPortID.equals("eventOut" + i))
			{
				return outputs.etpEventOut[i-1];
			}
		}
		return null;
	}

	@Override
	public void syncedValuesReceived(HashMap<String, byte[]> dataRow) 
	{
		boolean foundX = false, foundY = false;
		double x = -1, y = -1;
		for (String s: dataRow.keySet())
		{
			byte [] data = dataRow.get(s);
			if (s.equals("posRow"))
			{
				y = ConversionUtils.doubleFromBytes(data);
				foundY = true;
				
			}
			if (s.equals("posColumn"))
			{
				x = ConversionUtils.doubleFromBytes(data);
				foundX = true;				
			}
		}
		
		if (foundX && foundY)
		{
			highlighter.highlightCell(x, y);
		}
	}
	
	
	 private static List<File> findFiles(File where, String extension, int maxDeep)
	 {
		 File[] files = where.listFiles();
		 ArrayList<File> result = new ArrayList<File>();
	
		 if(files != null)
		 {
			 for (File file : files) 
			 {
				 if (file.isFile() && file.getName().endsWith(extension))
					 result.add(file);
				 else if ( (file.isDirectory()) &&( maxDeep-1 > 0 ) )
				 {
					 // do the recursive crawling
			         List<File> temp = findFiles(file, extension, maxDeep-1);
		             for(File thisFile : temp)
		                   result.add(thisFile);
				 }
			 }
		 }
		 return result;
	 }
	
	/**
	 * Returns all the filenames inside the path folder data/music
	 * and data/sounds
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>();
		try
		{
			if (key.equals("settingsFile"))
			{
				List<File> files = findFiles(new File("../oska/settings"), ".kst", 200);
				for (File file : files)
				{
					res.add(file.getPath().substring(file.getPath().indexOf("set")));
				}
			}
			else if (key.equals("keyboardPath"))
			{
				List<File> files = findFiles(new File("../oska/keyboards"), ".xml", 200);
				for (File file : files)
				{
					res.add(file.getPath().substring(file.getPath().indexOf("keyb")));
				}
			}

		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	} 
}