
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.oscilloscope;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;
 

/**
 *   Implements the Oscilloscope plugin, which can display one or two 
 *   signal traces in a GUI
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Nov 24, 2010
 *         Time: 1:00:05 PM
 */
public class OscilloscopeInstance extends AbstractRuntimeComponentInstance
{
	
	final int DISPLAYMODE_INCOMING=0;
	final int DISPLAYMODE_PERIODIC=1;

	private boolean threadStarted = false;	
	private boolean endThread=false;

	
    private double actChnValue = 0;
    private int active = 0;
    
    public int propDisplayBuffer =0;
    public int propDrawingMode = 0;
    public int propDisplayMode = 0;
    public int propDrawingInterval = 0;
    public double propMin =-100; 
    public double propMax =100; 
    public int propGridColor =0;
    public int propChannelColor =10;
    public int propBackgroundColor =11;
    public int propFontSize =14; 
    public String propCaption="oscilloscope";
    public boolean propDisplayGUI=true;

    
    private  GUI gui = null;
    
   /**
    * The class constructor.
    */
    public OscilloscopeInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("in".equalsIgnoreCase(portID))
        {
            return ipIn;
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
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
        if("displayBuffer".equalsIgnoreCase(propertyName))
        {
            return propDisplayBuffer;
        }
        if("drawingMode".equalsIgnoreCase(propertyName))
        {
            return propDrawingMode;
        }
        if("displayMode".equalsIgnoreCase(propertyName))
        {
            return propDisplayMode;
        }
        if("drawingInterval".equalsIgnoreCase(propertyName))
        {
            return propDrawingInterval;
        }
        if("min".equalsIgnoreCase(propertyName))
        {
            return propMin;
        }
        if("max".equalsIgnoreCase(propertyName))
        {
            return propMax;
        }
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            return propGridColor;
        }
        if("channelColor".equalsIgnoreCase(propertyName))
        {
            return propChannelColor;
        }
        if("backgroundColor".equalsIgnoreCase(propertyName))
        {
            return propBackgroundColor;
        }
        if("fontSize".equalsIgnoreCase(propertyName))
        {
            return propFontSize;
        }
        if("caption".equalsIgnoreCase(propertyName))
        {
            return propCaption;
        }        
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            return propDisplayGUI;
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
        if("displayBuffer".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propDisplayBuffer;
            propDisplayBuffer = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("drawingMode".equalsIgnoreCase(propertyName))
        { 
            final Object oldValue = propDrawingMode;
            propDrawingMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("displayMode".equalsIgnoreCase(propertyName))
        { 
            final Object oldValue = propDisplayMode;
            propDisplayMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("drawingInterval".equalsIgnoreCase(propertyName))
        { 
            final Object oldValue = propDrawingInterval;
            propDrawingInterval = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("min".equalsIgnoreCase(propertyName))
        { 
            final Object oldValue = propMin;
            propMin = Double.parseDouble(newValue.toString());
            return oldValue;
        }
        if("max".equalsIgnoreCase(propertyName))
        { 
            final Object oldValue = propMax;
            propMax = Double.parseDouble(newValue.toString());
            return oldValue;
        }
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propGridColor;
            propGridColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("channelColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propChannelColor;
            propChannelColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("backgroundColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propBackgroundColor;
            propBackgroundColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("fontSize".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propFontSize;
            propFontSize = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("caption".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propCaption;
            propCaption = newValue.toString();
            return oldValue;
        }
    	if("displayGUI".equalsIgnoreCase(propertyName))
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

        return null;
    }

   /**
    * Input Port for receiving channel samples.
    */    
    private final IRuntimeInputPort ipIn
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            actChnValue = ConversionUtils.doubleFromBytes(data);
            if ((active == 1) && (propDisplayMode == DISPLAYMODE_INCOMING)) 
            	gui.updateChn(actChnValue);
       }
    };



    /**
     * called when model is started.
     */
    @Override
    public void start()
    {
    	int id=1;    	
      	gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
      	if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
        active=1;
        
        if ((threadStarted==false) && (propDisplayMode==DISPLAYMODE_PERIODIC))
        {
	    	threadStarted = true;	
	    	endThread=false;
			  AstericsThreadPool.instance.execute(new Runnable() {
				  public void run()
				  {
					  while (endThread==false)
					  {
	    				try
	    				{
							   Thread.sleep(propDrawingInterval);
					            if ((active == 1) && (propDisplayMode == DISPLAYMODE_PERIODIC)) 
					            	gui.updateChn(actChnValue);
	    				}
	    				catch (InterruptedException e) {}
					  }
					  threadStarted=false;
				  }
		     }
		     );
        }      
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause()
    {
    	//Logger.getAnonymousLogger().info("OscilloscopeInstance started");
        active=0;
        super.pause();
    }
    
    /**
     * called when model is resumed.
     */
    @Override
    public void resume()
    {
    	//Logger.getAnonymousLogger().info("OscilloscopeInstance resumed");
        active=1;
        super.resume();
    }
        
    /**
     * called when model is stopped.
     */
    @Override
    public void stop()
    {   	
    	//Logger.getAnonymousLogger().info("OscilloscopeInstance stopped");
    	AREServices.instance.displayPanel(gui, this, false);
    	actChnValue = 0;
        active=0;
        endThread=true;
        super.stop();
    }
}