package eu.asterics.component.actuator.gui_tester;
       
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;

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

/**
 * @author Konstantinos Kakousis
 * This class generates a JPanel to be displayed on the AsTeRICS Desktop.
 * It can be used as a prototype from developers interested in creating plugins
 * with gui elements. This plugin includes several Swing components as well as
 * Graphics. Everything is defined in terms of available space which is passed 
 * as a constructor argument.
 * 
 * Date: Sep 16, 2011
 */

public class GuiTesterInstance extends AbstractRuntimeComponentInstance
{
	
    private GuiTesterGui gui;
    public GuiTesterInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    public IRuntimeInputPort getInputPort(String portID)
    {
       return null;
    }

    public IRuntimeOutputPort getOutputPort(String portID)
    {
        return null;
    }


    public Object getRuntimePropertyValue(String propertyName)
    {
        
        return null;
    }

    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        
        return null;
    }

    private final IRuntimeInputPort chn1InputPort
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
           ;
       }

	
    };

    private final IRuntimeInputPort chn2InputPort
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
        	;
        }

		
    };
	

    @Override
    public void start()
    {
    	//The ACS defines specific space for each plugin.
    	//This space should be requested from the ARE using the following 
    	//method. Plugin GUIs should be constructed in dependence of this space.
    	gui = new GuiTesterGui(AREServices.instance.getAvailableSpace(this));
    	//Calling this service will show the created Panel on the main window
    	//a.k.a Desktop.
    	AREServices.instance.displayPanel(gui, this, true);
        super.start();
    }

    @Override
    public void pause()
    {
        super.pause();
    }
    
    @Override
    public void resume()
    {
  
        super.resume();
    }
        
    @Override
    public void stop()
    {
    	//When the displayPanel method is called with false as its last 
    	//argument the gui elements of the plugin are removed from the desktop.
    	//Developers are expected to call this method.
    	AREServices.instance.displayPanel(gui, this, false);     
        super.stop();
    }
    
}