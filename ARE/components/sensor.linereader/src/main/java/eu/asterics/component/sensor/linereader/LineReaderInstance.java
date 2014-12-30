

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.linereader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * Reads single lines from a text file and sends the content to an output port
 * 
 * 
 *  
 * @author Chris Veigl
 *         Date: 29 12 2014
 */
public class LineReaderInstance extends AbstractRuntimeComponentInstance
{
	
    final IRuntimeEventTriggererPort etpEndOfFile = new DefaultRuntimeEventTriggererPort();    

	final IRuntimeOutputPort opActLine = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propFileName = "test.txt";

	// declare member variables here
	File  textFile = null;
	InputStream    fis = null;
	BufferedReader br = null;
	String         line = null;

  
    
   /**
    * The class constructor.
    */
    public LineReaderInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
    	if ("skipLines".equalsIgnoreCase(portID))
		{
			return ipSkipLines;
		}
		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("actLine".equalsIgnoreCase(portID))
		{
			return opActLine;
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
		if ("readNextLine".equalsIgnoreCase(eventPortID))
		{
			return elpReadNextLine;
		}
		if ("resetToFirstLine".equalsIgnoreCase(eventPortID))
		{
			return elpResetToFirstLine;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	if("endOfFile".equalsIgnoreCase(eventPortID))
        {
            return etpEndOfFile;
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
		if ("fileName".equalsIgnoreCase(propertyName))
		{
			return propFileName;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("fileName".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFileName;
			propFileName = (String)newValue;
			return oldValue;
		}

        return null;
    }


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpReadNextLine = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 String actLine = readNextLine();
				 if (actLine != null) opActLine.sendData(ConversionUtils.stringToBytes(actLine));
				 else etpEndOfFile.raiseEvent();
		}
	};
	final IRuntimeEventListenerPort elpResetToFirstLine = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			fileCleanup();
			tryToOpenFile();
		}
	};

	private final IRuntimeInputPort ipSkipLines  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int linesToSkip = ConversionUtils.intFromBytes(data);
			while (linesToSkip > 0) 
			{
				if (readNextLine()==null) linesToSkip=0;
				else linesToSkip--;
			}
		}

	};

	
	public void tryToOpenFile ()
	{
		try 
		{
			textFile  = new File(propFileName);
			// File localFile = new File(fullFilePath.toString() + fileName);
			 System.out.println("Trying to open File "+propFileName);
			if (textFile.exists())
			{
				fis = new FileInputStream(textFile);
				br = new BufferedReader(new InputStreamReader(fis));
	  		    System.out.println("Reader created ! ");
	
			}
	     } catch(Exception e){
	    	 System.out.println("Error opening File !");
	     }

		
	}

	public String readNextLine ()
	{
		if (br!=null)
		{
			try 
			{
				if ((line = br.readLine()) != null) {
					return (line);
				}
				
		     } catch(Exception e){
		    	 System.out.println("Error reading next Line !");
		     }
		}
		return (null);
	}

	public void fileCleanup()
	{
		try{
			if (br!=null)
				br.close();
			if (fis!=null) 
				fis.close();
	     } catch(Exception e){
	    	 System.out.println("Error closing the file !");
	     }

	}

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
    	  tryToOpenFile ();
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
		  fileCleanup();
          super.stop();
      }
}