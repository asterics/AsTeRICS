

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

package eu.asterics.component.actuator.linewriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

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
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class LineWriterInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propFileName = "outfile.txt";
	int propLineEndMark = 0;
	int propTimestamp = 0;
	String propTitleCaption = "";
	boolean propAppend = false;
	boolean propAddTimeToFileName = false;

	// declare member variables here
	private BufferedWriter out = null;
	String newline="\n";
	long starttime=0;
  
    
   /**
    * The class constructor.
    */
    public LineWriterInstance()
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
		if ("actLine".equalsIgnoreCase(portID))
		{
			return ipActLine;
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

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {

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
		if ("titleCaption".equalsIgnoreCase(propertyName))
		{
			return propTitleCaption;
		}
		if ("timestamp".equalsIgnoreCase(propertyName))
		{
			return propTimestamp;
		}
		if ("lineEndMark".equalsIgnoreCase(propertyName))
		{
			return propLineEndMark;
		}
		if ("append".equalsIgnoreCase(propertyName))
		{
			return propAppend;
		}
		if ("addTimeToFileName".equalsIgnoreCase(propertyName))
		{
			return propAddTimeToFileName;
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
		if ("titleCaption".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTitleCaption;
			propTitleCaption = (String)newValue;
			return oldValue;
		}
		if ("timestamp".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTimestamp;
			propTimestamp = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("lineEndMark".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propLineEndMark;
			propLineEndMark = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("append".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAppend;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAppend = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAppend = false;
			}
			return oldValue;
		}
		if ("addTimeToFileName".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddTimeToFileName;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAddTimeToFileName = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAddTimeToFileName = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipActLine  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if (out==null) return; 
            String valueToWrite = ConversionUtils.stringFromBytes(data);
			try {
				switch (propTimestamp) {
					case 0:out.write(valueToWrite + newline);
						break;
					case 1:out.write((System.currentTimeMillis()-starttime)+", "+valueToWrite + newline);
						break;
				}
			} catch (IOException e) {
				AstericsErrorHandling.instance.getLogger().severe("Error writing file");
			}
		}
	};


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  	starttime=System.currentTimeMillis();
	  		if (out != null)
	  		{
	  			try {
	  				out.close();
	  			} catch (IOException e) {
	  				AstericsErrorHandling.instance.reportInfo(this, "Error closing previous file");
	  			}
	  		}
	
	  		switch(propLineEndMark) {
	  			case 0: newline=System.getProperty("line.separator");
	  				break;
	  			case 2: newline="\r\n";
					break;
	  			default: newline="\n";
					break;
	  		}
	  		
	  		Calendar cal = Calendar.getInstance();
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	  		try {
	  			if (propAddTimeToFileName)
	  				out = new BufferedWriter(new FileWriter(propFileName + "_" + sdf.format(cal.getTime()) + ".txt",propAppend));
	  			else
	  	  			out = new BufferedWriter(new FileWriter(propFileName+".txt",propAppend));
	
	  			if (propTitleCaption!="")
					out.write(propTitleCaption + newline);
	
	
	  		} catch (IOException e) {
	  			AstericsErrorHandling.instance.reportInfo(this, "Error creating file");
	  		}	
    	  	starttime=System.currentTimeMillis();
	  		super.start();
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

  		if (out != null)
  		{
  			try {
  				out.close();
  			} catch (IOException e) {
  				AstericsErrorHandling.instance.reportInfo(this, "Error closing file");
  			}
  		}
          super.stop();
      }
}