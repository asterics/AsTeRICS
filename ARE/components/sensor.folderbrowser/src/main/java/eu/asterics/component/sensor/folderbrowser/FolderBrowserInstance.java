

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

package eu.asterics.component.sensor.folderbrowser;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
 * browser for folders / directories ad files in a filesystem
 * 
 * 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 2016-12-16
 */
public class FolderBrowserInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opFolderName = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFileNames = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propInitialFolder = ".";
	boolean propWrapAround = true;
	boolean propIncludeFolderPath = true;
	boolean propIncludeFilePath = true;
	boolean propExitInitialFolder = false;
	boolean propAutoListFiles = false;

	// declare member variables here

	List<String> folderList = null; 
	int currentIndex=0;

    
   /**
    * The class constructor.
    */
    public FolderBrowserInstance()
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

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("folderName".equalsIgnoreCase(portID))
		{
			return opFolderName;
		}
		if ("fileNames".equalsIgnoreCase(portID))
		{
			return opFileNames;
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
		if ("next".equalsIgnoreCase(eventPortID))
		{
			return elpNext;
		}
		if ("previous".equalsIgnoreCase(eventPortID))
		{
			return elpPrevious;
		}
		if ("enter".equalsIgnoreCase(eventPortID))
		{
			return elpEnter;
		}
		if ("exit".equalsIgnoreCase(eventPortID))
		{
			return elpExit;
		}
		if ("current".equalsIgnoreCase(eventPortID))
		{
			return elpCurrent;
		}
		if ("listFiles".equalsIgnoreCase(eventPortID))
		{
			return elpListFiles;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("initialFolder".equalsIgnoreCase(propertyName))
		{
			return propInitialFolder;
		}
		if ("wrapAround".equalsIgnoreCase(propertyName))
		{
			return propWrapAround;
		}
		if ("includeFolderPath".equalsIgnoreCase(propertyName))
		{
			return propIncludeFolderPath;
		}
		if ("includeFilePath".equalsIgnoreCase(propertyName))
		{
			return propIncludeFilePath;
		}
		if ("exitInitialFolder".equalsIgnoreCase(propertyName))
		{
			return propExitInitialFolder;
		}
		if ("autoListFiles".equalsIgnoreCase(propertyName))
		{
			return propAutoListFiles;
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
		if ("initialFolder".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInitialFolder;
			propInitialFolder = (String)newValue;
			return oldValue;
		}
		if ("wrapAround".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propWrapAround;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propWrapAround = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propWrapAround = false;
			}
			return oldValue;
		}
		if ("includeFolderPath".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIncludeFolderPath;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propIncludeFolderPath = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propIncludeFolderPath = false;
			}
			return oldValue;
		}
		if ("includeFilePath".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIncludeFilePath;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propIncludeFilePath = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propIncludeFilePath = false;
			}
			return oldValue;
		}
		if ("exitInitialFolder".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propExitInitialFolder;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propExitInitialFolder = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propExitInitialFolder = false;
			}
			return oldValue;
		}
		if ("autoListFiles".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutoListFiles;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutoListFiles = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutoListFiles = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpNext = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
			System.out.println("act element="+folderList.get(currentIndex));
			if (currentIndex < folderList.size()) currentIndex++;
			else if (propWrapAround == true ) currentIndex=0;

		}
	};
	final IRuntimeEventListenerPort elpPrevious = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpEnter = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpExit = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpCurrent = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpListFiles = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};

	
	
	public List<String> getFolderList(String root) 
	{

		List<String> res = new ArrayList<String>(); 

		List<String> nextDir = new ArrayList<String>(); //Directories
		nextDir.add(root);	
		System.out.println("folderBrowser: root folder=" + root);
			
		try 
		{
			while(nextDir.size() > 0) 
			{
				File pathName = new File(nextDir.get(0)); 
				String[] fileNames = pathName.list();  // lists all files in the directory

				for(int i = 0; i < fileNames.length; i++) 
				{ 
					File f = new File(pathName.getPath(), fileNames[i]); // getPath converts abstract path to path in String, 
					// constructor creates new File object with fileName name   
					if (f.isDirectory()) 
					{  
						nextDir.add(f.getPath()); 
						System.out.println("adding sub folder: " + f.getPath());

					} 
					else 
					{
						res.add(f.getPath());
						System.out.println("adding file: " + f.getPath());
					}
				} 
				nextDir.remove(0); 
			} 
		}
		catch (Exception e) {System.out.println ("could not find directories !");}
		return res;
	} 

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          folderList=getFolderList(propInitialFolder);
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

          super.stop();
      }
}