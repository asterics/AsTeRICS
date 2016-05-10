
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

package eu.asterics.component.actuator.dotmeter;
import java.util.HashMap;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
 
/**

 *   Implements the Dotmeter actuator plugin, which can show values on the
 *   ARE GUI in a simple 2D-meter display with adjustable size and color 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Oct 7, 2012
 *         Time: 10:55:05 AM
 */
public class DotmeterInstance extends AbstractRuntimeComponentInstance
{
	final int MODE_CLIPMINMAX=0;
	final int MODE_AUTOMINMAX=1;
	
    private double inputX = 500;
    private double inputY = 500;
    private int active = 0;
    
    public double propXMin =0;
    public double propXMax =1000;
    public double propYMin =0;
    public double propYMax =1000;
    public int propMode =1;
    public int propDotSize =10;
    public boolean propCenterLine = true;
    public boolean propDisplayDot = true;
    public boolean propDisplayCaptions = true;
    public int propGridColor =0;
    public int propDotColor =8;
    public int propBackgroundColor =11;
    public int propFontSize =14; 
    public String propCaption="dotMeter";
    public boolean propDisplayGUI=true;

  
    private  GUI gui = null;
    
    
    /**
     * The class constructor.
     * initializes the GUI
     */
    public DotmeterInstance()
    {
    	//gui.updateMinMax();
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("x".equalsIgnoreCase(portID))
        {
            return ipX;
        }
        if("y".equalsIgnoreCase(portID))
        {
            return ipY;
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
        if("Xmin".equalsIgnoreCase(propertyName))
        {
            return propXMin;
        }
        if("Xmax".equalsIgnoreCase(propertyName))
        {
            return propXMax;
        }
        if("Ymin".equalsIgnoreCase(propertyName))
        {
            return propYMin;
        }
        if("Ymax".equalsIgnoreCase(propertyName))
        {
            return propYMax;
        }
        if("mode".equalsIgnoreCase(propertyName))
        {
            return propMode;
        }        
        if("dotSize".equalsIgnoreCase(propertyName))
        {
            return propDotSize;
        }        
        if("centerLine".equalsIgnoreCase(propertyName))
        {
            return propCenterLine;
        }        
        if("displayDot".equalsIgnoreCase(propertyName))
        {
            return propDisplayDot;
        }        
        if("displayCaptions".equalsIgnoreCase(propertyName))
        {
            return propDisplayCaptions;
        }        
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            return propGridColor;
        }        
        if("dotColor".equalsIgnoreCase(propertyName))
        {
            return propDotColor;
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
     * returns an Event Listener Port.
     * @param portID   the name of the port
     * @return         the event listener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        if("dotOn".equalsIgnoreCase(eventPortID))
        {
            return elpDotOn;
        }
        else if("dotOff".equalsIgnoreCase(eventPortID))
        {
            return elpDotOff;
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
        if("Xmin".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propXMin;
           	propXMin = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("Xmax".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propXMax;
           	propXMax = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("Ymin".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propYMin;
           	propYMin = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("Ymax".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propYMax;
           	propYMax = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("mode".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propMode;
            propMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("dotSize".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propDotSize;
            propDotSize = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        
        if("centerLine".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propCenterLine;
            if("true".equalsIgnoreCase((String)newValue))
            {
            	propCenterLine = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propCenterLine = false;
            }
            return oldValue;
        }
        if("displayDot".equalsIgnoreCase(propertyName))
        { 
        	final Object oldValue = propDisplayDot;
            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayDot = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
             	 propDisplayDot = false;
            }
            return oldValue;
        }
        if("displayCaptions".equalsIgnoreCase(propertyName))
        { 
        	final Object oldValue = propDisplayCaptions;
            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayCaptions = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
             	 propDisplayCaptions = false;
            }
            return oldValue;
        }
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propGridColor;
            propGridColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("dotColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propDotColor;
            propDotColor = Integer.parseInt(newValue.toString());
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
     * Input Port for receiving values.
     */
    private final IRuntimeInputPort ipX
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            inputX = ConversionUtils.doubleFromBytes(data);
            if (active == 1) gui.updateInput(inputX,inputY);
       }

    };
    private final IRuntimeInputPort ipY
    	= new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
		    inputY = ConversionUtils.doubleFromBytes(data);
		    if (active == 1) gui.updateInput(inputX,inputY);
		}
	
	};

	@Override
	public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {
		
		double x=0;
		double y=0;
		
		for (String s: dataRow.keySet())
		{
			
			byte [] data = dataRow.get(s);
			if (s.equals("x"))
			{
				x=ConversionUtils.doubleFromBytes(data);
			}
			if (s.equals("y"))
			{
				y=ConversionUtils.doubleFromBytes(data);
			}
		}
	    if (active == 1) gui.updateInput(x,y);
	}

	
	
	
	
	  /**
	   * Event Listener Ports.
	   */
	  final IRuntimeEventListenerPort elpDotOn 	= new IRuntimeEventListenerPort()
	  {
	  	 public void receiveEvent(final String data)
	  	 {
	         propDisplayDot=true;
	         gui.updateInput(inputX,inputY);
	  	 }
	  };
	  final IRuntimeEventListenerPort elpDotOff 	= new IRuntimeEventListenerPort()
	  {
	  	 public void receiveEvent(final String data)
	  	 {
	         propDisplayDot=false;
	         gui.updateInput(inputX,inputY);
	  	 }
	  };


    /**
     * called when model is started.
     */
    @Override
    public void start()
    {
    	// Logger.getAnonymousLogger().info("DotmeterInstance started");
    	
    	inputX=(propXMin+propXMax)/2;
    	inputY=(propYMin+propYMax)/2;
    	
      	gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
      	if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
        gui.updateInput(inputX,inputY);
         
    	active=1;
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

    	AREServices.instance.displayPanel(gui, this, false);
    	
    	// Logger.getAnonymousLogger().info("DotmeterInstance stopped");
        // gui.setVisible(false);
        active=0;
        super.stop();
    }
}