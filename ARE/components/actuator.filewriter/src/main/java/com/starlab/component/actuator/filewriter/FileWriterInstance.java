
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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package com.starlab.component.actuator.filewriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

/**
 *   Implements a file writer actuator plugin, which can write the values on
 *   its input in ASCII mode to a file
 *  
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: May 5, 2011
 *         Time: 01:06:51 PM
 */
public class FileWriterInstance extends AbstractRuntimeComponentInstance
{
	private String propFileName = "filewriter";
	private BufferedWriter out = null;
	
	/**
     * The class constructor.
     */
    public FileWriterInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("input".equalsIgnoreCase(portID))
        {
            return ipInputPort;
        }
        return null;
    }

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID)
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
		if("FileName".equalsIgnoreCase(propertyName))
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
        if("FileName".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propFileName;

            propFileName=(String)newValue;
            return oldValue;
        }
        return null;
    }

    /**
     * Input Port for receiving values.
     */
    private final IRuntimeInputPort ipInputPort
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            double valueToWrite = ConversionUtils.doubleFromBytes(data);
			try {
				out.write(Double.toString(valueToWrite) + System.getProperty("line.separator"));
			} catch (IOException e) {
				AstericsErrorHandling.instance.getLogger().severe("Error writing file");
			}
        }

    };

    /**
     * called when model is started.
     */
    @Override
    public void start()
    {
		if (out != null)
		{
			try {
				out.close();
			} catch (IOException e) {
				AstericsErrorHandling.instance.reportInfo(this, "Error closing previous file");
			}
		}
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			out = new BufferedWriter(new FileWriter(propFileName + "_" + sdf.format(cal.getTime()) + ".txt"));
		} catch (IOException e) {
			AstericsErrorHandling.instance.reportInfo(this, "Error creating file");
		}	
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