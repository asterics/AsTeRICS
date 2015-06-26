
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

package eu.asterics.component.processor.motionanalysis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
 

/**
 *   Implements the Oscilloscope plugin, which can display one or two 
 *   signal traces in a GUI
 *  
 * @author Armin Schmoldas [armin.schmoldas@technikum-wien.at]
 *         Date: Apr 21, 2015
 *         Time: 1:00:05 PM
 */
public class MotionAnalysisInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opPercent = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opResult = new DefaultRuntimeOutputPort();
	
	final IRuntimeEventTriggererPort etpInrange = new DefaultRuntimeEventTriggererPort();
	
	final int DISPLAYMODE_INCOMING=0;
	final int DISPLAYMODE_PERIODIC=1;
	private int Chncount = 0;
	private int loop = 0;

	private boolean threadStarted = false;	 
	private boolean endThread=false;
	private boolean Chn1 = false, Chn2 = false, Chn3 = false, Chn4 = false;
	public boolean startable = false;

	
	private double actdrawValue = 0;
	private double actChannel1Value = 0;
    private double actChannel2Value = 0;
    private double actChannel3Value = 0;
    private double actChannel4Value = 0;
    private double misscount = 0;
    private double correct = 0;
    private int active = 0;
    
    public int propDisplayBuffer =0;
    public int propDrawingMode = 0;
    public int propDisplayMode = 0;
    public int propDrawingInterval = 0;
    public double propMin =0; 
    public double propMax =10; 
    public int propGridColor =0;
    public int propDrawchannelColor =10;
    public int propLoadchannelColor =1;
    public int propBackgroundColor =11;
    public int propFontSize =14; 
    public String propCaption ="Exercise";
    public String propFilename ="Example";
    public String propFilepath = ".\\data\\actuator.motionanalysis\\";
    
    public String savePath = ".\\data\\actuator.motionanalysis\\";
    public String saveName = "Example";
    
    public double propLimitation = 80;
    public double propDeviation = 20;
    
    public String filepath = "";
    
    private  GUI gui = null;
    
   /**
    * The class constructor.
    */
    public MotionAnalysisInstance()
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
        if("channel1".equalsIgnoreCase(portID))
        {
            return ipChannel1;
        }
        if("channel2".equalsIgnoreCase(portID))
        {
            return ipChannel2;
        }
        if("channel3".equalsIgnoreCase(portID))
        {
            return ipChannel3;
        }
        if("channel4".equalsIgnoreCase(portID))
        {
            return ipChannel4;
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
    	if ("result".equalsIgnoreCase(portID))
		{
			return opResult;
		}
    	if ("percent".equalsIgnoreCase(portID))
		{
			return opPercent;
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
		if ("Start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		if ("Stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		if ("Save".equalsIgnoreCase(eventPortID))
		{
			return elpSave;
		}
		if ("Stopsave".equalsIgnoreCase(eventPortID))
		{
			return elpStopsave;
		}
		if ("Load".equalsIgnoreCase(eventPortID))
		{
			return elpLoad;
		}

        return null;
    }

    /**
     * returns an Event Trigger Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	if("Inrange".equalsIgnoreCase(eventPortID))
        {
            return etpInrange;
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
        if("loadchannelColor".equalsIgnoreCase(propertyName))
        {
            return propLoadchannelColor;
        }
        if("drawchannelColor".equalsIgnoreCase(propertyName))
        {
            return propDrawchannelColor;
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
        if("filname".equalsIgnoreCase(propertyName))
        {
            return propFilename;
        }
        if("filepath".equalsIgnoreCase(propertyName))
        {
            return propFilepath;
        }
        if("deviation".equalsIgnoreCase(propertyName))
        {
            return propDeviation;
        }
        if("limitation".equalsIgnoreCase(propertyName))
        {
            return propLimitation;
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
        if("loadchannelColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propLoadchannelColor;
            propLoadchannelColor = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("drawchannelColor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propDrawchannelColor;
            propDrawchannelColor = Integer.parseInt(newValue.toString());
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
        if("filename".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propFilename;
            propFilename = newValue.toString();
            return oldValue;
        }
        if("filepath".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propFilepath;
            propFilepath = newValue.toString();
            return oldValue;
        }
        if("alloweddeviation".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propDeviation;
            propDeviation = Double.parseDouble(newValue.toString());
            return oldValue;
        }
        if("deviation".equalsIgnoreCase(propertyName))
        {
            final Object oldValue =  propLimitation;
            propLimitation = Double.parseDouble(newValue.toString());
            return oldValue;
        }

        return null;
    }

   /**
    * Input Port for receiving channel samples.
    */    
    
    private final IRuntimeInputPort ipChannel1
    = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			actChannel1Value = ConversionUtils.doubleFromBytes(data);
		    Chn1 = true;
		    if (loop < 4)
		    {
		    	loop++;
		    }
		    if (Chncount < 1)
		    {
		    	Chncount = 1;
		    }
		    drawcheck();
		}
	};
	
    private final IRuntimeInputPort ipChannel2
    = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			actChannel2Value = ConversionUtils.doubleFromBytes(data);
		    Chn2 = true;
		    if (loop < 4)
		    {
		    	loop++;
		    }
		    if (Chncount < 2)
		    {
		    	Chncount = 2;
		    }
		    drawcheck();
		}
	};
	
    private final IRuntimeInputPort ipChannel3
    = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			actChannel3Value = ConversionUtils.doubleFromBytes(data);
		    Chn3 = true;
		    if (loop < 4)
		    {
		    	loop++;
		    }
		    if (Chncount < 3)
		    {
		    	Chncount = 3;
		    }
		    drawcheck();
		}
	};
	
    private final IRuntimeInputPort ipChannel4
    = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			actChannel4Value = ConversionUtils.doubleFromBytes(data);
		    Chn4 = true;
		    if (loop < 4)
		    {
		    	loop++;
		    }
		    if (Chncount < 4)
		    {
		    	Chncount = 4;
		    }
		    drawcheck();
		}
	};
	
    /**
     * Event Listerner Ports.
     */
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (startable == true)
					resume();
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			pause();
		}
	};
	final IRuntimeEventListenerPort elpSave = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			pause();
			gui.clearClick();
			startable = true;
			gui.save();
		}
	};
	final IRuntimeEventListenerPort elpStopsave = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			gui.stopsave();
			pause();
			gui.clearClick();
			startable = false;
		}
	};	
	final IRuntimeEventListenerPort elpLoad = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			gui.stopsave();
			gui.clearClick();
			gui.allloadValues = new ArrayList<Double>();
			gui.alldrawValues = new ArrayList<Double>();
			gui.roundloadValues = new ArrayList<double[]>();
			gui.loadchnValues = new double [gui.MAX_SIZE];
			gui.drawchnValues = new double [gui.MAX_SIZE];
			startable = true;
			resume();
			gui.load();
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
        AREServices.instance.displayPanel(gui, this, true);
        active=1;
        pause();
        
        if ((threadStarted==false) && (propDisplayMode==DISPLAYMODE_PERIODIC))
        {
    		gui.checkPath();
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
					            	gui.updatedrawChn(actdrawValue);
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
    	
    	gui.stopsave();
    	actdrawValue = 0;
    	actChannel1Value = 0;
    	actChannel2Value = 0;
    	actChannel3Value = 0;
    	actChannel4Value = 0;
        active = 0;
        Chncount = 0;
        loop = 0;
        endThread=true;
        
        if (gui.out != null)
		{
			try {
				gui.out.close();
			} catch (IOException e) {
				AstericsErrorHandling.instance.reportInfo(this, "Error closing file");
			}
		}
        
        super.stop();
    }

    /**
     *  Checks how many channels are connected. After all channels have sent data the draw channel is updated.
     */
    public void drawcheck()
    {
    	if (loop == 4)
    	{
	    	switch (Chncount)
	    	{
	    	case 0:
	    		
	    		break;
	    	case 1:
				if ((active == 1) && (propDisplayMode == DISPLAYMODE_INCOMING) && (Chn1 == true))
				{
			    	gui.updatedrawChn(actChannel1Value);
			    	Chn1 = false;
				}
	    		break;
	    	case 2:
				if ((active == 1) && (propDisplayMode == DISPLAYMODE_INCOMING) && (Chn1 == true) && (Chn2 == true))
				{
			    	gui.updatedrawChn(actChannel1Value+actChannel2Value);
					Chn1 = false;
					Chn2 = false;
				}
	    		break;
	    	case 3:
	    		if ((active == 1) && (propDisplayMode == DISPLAYMODE_INCOMING) && (Chn1 == true) && (Chn2 == true) && (Chn3 == true)) 
	    		{
			    	gui.updatedrawChn(actChannel1Value+actChannel2Value+actChannel3Value);
					Chn1 = false;
					Chn2 = false;
					Chn3 = false;
	    		}
	    		break;
	    	case 4:
	    		if ((active == 1) && (propDisplayMode == DISPLAYMODE_INCOMING) && (Chn1 == true) && (Chn2 == true) && (Chn3 == true) && (Chn4 == true))
	    		{
			    	gui.updatedrawChn(actChannel1Value+actChannel2Value+actChannel3Value+actChannel4Value);
					Chn1 = false;
					Chn2 = false;
					Chn3 = false;
					Chn4 = false;
	    		}
	    		break;
	    	}
    	}
    }
    
    /**
     * Calculates the relative difference between the load channel and the draw channel.
     */
    public void compare()
    {
    	if (gui.loadchnValues.length != 0)
    	{
    		misscount = 0;
    		ArrayList<Double> percent = new ArrayList<Double>();
	    	if (propMax-propMin != 0)
	    	{
	    		for (int i = 0; i < gui.allloadValues.size(); i++)
		    	{
	    				percent.add(Math.abs(gui.alldrawValues.get(i) - gui.allloadValues.get(i)) / Math.abs(propMax-propMin));
		    	}
	    	}
	    	else
	    	{
	    		AstericsErrorHandling.instance.reportInfo(this, "Error: No Values");
	    	}
	    	

	        for (int i = 0; i < percent.size(); i++) {
	            if(percent.get(i) < propDeviation/100)
	            {
	            	misscount++;
	            }
	        }
	        
	        correct = misscount * 100 / percent.size();
	        String output = Double.toString(correct);
	        opResult.sendData(ConversionUtils.stringToBytes(output));
	        
	        
	        if (correct >= propLimitation)
	        {
	        	etpInrange.raiseEvent();
	        }
    	}
    	else
    	{
    		AstericsErrorHandling.instance.reportInfo(this, "Error: No Exercise is loaded");
    	}
    }
    
    /**
     * Sends the actual position of the exercise in percent to the percent output port.
     */
    public void percent()
    {
    	double percent = (gui.alldrawValues.size()*100/gui.allloadValues.size());
    	opPercent.sendData(ConversionUtils.doubleToBytes(percent));
    }
}