
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

package eu.asterics.component.actuator.bardisplay;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
 
/**

 *   Implements the Bardisplay actuator plugin, which can show values on the
 *   ARE GUI in a simple meter display with adjustable size and color 
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:55:05 AM
 */
public class BardisplayInstance extends AbstractRuntimeComponentInstance
{
	final int MODE_CLIPMINMAX=0;
	final int MODE_AUTOMINMAX=1;
	
    private double inputValue = 0;
    private int active = 0;
    
    public int propDisplayBuffer =0;
    public double propMin =0;
    public double propMax =1000;
    public double propThreshold =500;
    public boolean propDisplayThreshold = false;
    public boolean propIntegerDisplay = false;
    public int propMode =1;
    public int propGridColor =0;
    public int propBarColor =8;
    public int propBackgroundColor =11;
    public int propFontSize =14; 
    public String propCaption="bar-graph";
    public boolean propDisplayGUI=true;    
  
    private  GUI gui = null;
    
    
    /**
     * The class constructor.
     * initializes the GUI
     */
    public BardisplayInstance()
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
        if("min".equalsIgnoreCase(propertyName))
        {
            return propMin;
        }
        if("max".equalsIgnoreCase(propertyName))
        {
            return propMax;
        }
        if("threshold".equalsIgnoreCase(propertyName))
        {
            return propThreshold;
        }
        if("displayThreshold".equalsIgnoreCase(propertyName))
        {
            return propDisplayThreshold;
        }        
        if("integerDisplay".equalsIgnoreCase(propertyName))
        {
            return propIntegerDisplay;
        }        
        if("mode".equalsIgnoreCase(propertyName))
        {
            return propMode;
        }        
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            return propGridColor;
        }        
        if("barColor".equalsIgnoreCase(propertyName))
        {
            return propBarColor;
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
        if("min".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propMin;
           	propMin = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("max".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propMax;
           	propMax = Double.parseDouble(newValue.toString());
           	//gui.updateMinMax();
           	return oldValue;
        }
        if("threshold".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propThreshold;
           	propThreshold = Double.parseDouble(newValue.toString());
           	return oldValue;
        }
        
        if("displayThreshold".equalsIgnoreCase(propertyName))
        {
        	final Object oldValue = propDisplayThreshold;
            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayThreshold = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propDisplayThreshold = false;
            }
            return oldValue;
        }
        if("integerDisplay".equalsIgnoreCase(propertyName))
        { 
        	final Object oldValue = propIntegerDisplay;
            if("true".equalsIgnoreCase((String)newValue))
            {
            	propIntegerDisplay = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
             	 propIntegerDisplay = false;
            }
            return oldValue;
        }
        if("mode".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propMode;
            propMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("gridColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propGridColor;
            propGridColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("barColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propBarColor;
            propBarColor = Integer.parseInt(newValue.toString());
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
    private final IRuntimeInputPort ipInputPort
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            inputValue = ConversionUtils.doubleFromBytes(data);
            if (active == 1) gui.updateInput(inputValue);
       }

    };


    /**
     * called when model is started.
     */
    @Override
    public void start()
    {
    	inputValue=0;
    	// Logger.getAnonymousLogger().info("BardisplayInstance started");
      	gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
      	if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
        
       
        
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
    	
    	// Logger.getAnonymousLogger().info("BardisplayInstance stopped");
        // gui.setVisible(false);
        active=0;
        super.stop();
    }
}