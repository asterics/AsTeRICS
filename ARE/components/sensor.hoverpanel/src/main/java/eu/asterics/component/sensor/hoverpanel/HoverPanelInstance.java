

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.hoverpanel;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

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
import eu.asterics.mw.services.AstericsThreadPool;

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
public class HoverPanelInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpSelected = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEnter = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpExit = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propDataSource = 0;
	String propCaption = "hover";
	int propFontSize =14;
	int propTextColor = 6;
	int propBackgroundColor = 0;
	int propActivationColor = 2;
	int propDwellTime = 200;
	int propIdleTime = 50;
	int propOpacity = 50;
	int currentX = -10;
	int currentY = -10;
	
	boolean propStayActive = false;

	// declare member variables here
	private  GUI gui = null;

	int hoverState=0;
	int idleState=0;
	int selected=0;
	long hoverTime=0;
	long idleTime=0;
	boolean active = true;
  
    
   /**
    * The class constructor.
    */
    public HoverPanelInstance()
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
		if ("x".equalsIgnoreCase(portID))
		{
			return ipX;
		}
		if ("y".equalsIgnoreCase(portID))
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
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("activate".equalsIgnoreCase(eventPortID))
		{
			return elpActivate;
		}
		if ("deactivate".equalsIgnoreCase(eventPortID))
		{
			return elpDeactivate;
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
		if ("selected".equalsIgnoreCase(eventPortID))
		{
			return etpSelected;
		}
		if ("enter".equalsIgnoreCase(eventPortID))
		{
			return etpEnter;
		}
		if ("exit".equalsIgnoreCase(eventPortID))
		{
			return etpExit;
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
		if ("dataSource".equalsIgnoreCase(propertyName))
		{
			return propDataSource;
		}
		if ("caption".equalsIgnoreCase(propertyName))
		{
			return propCaption;
		}
		if ("fontSize".equalsIgnoreCase(propertyName))
		{
			return propFontSize;
		}
		if ("textColor".equalsIgnoreCase(propertyName))
		{
			return propTextColor;
		}
		if ("backgroundColor".equalsIgnoreCase(propertyName))
		{
			return propBackgroundColor;
		}
		if ("activationColor".equalsIgnoreCase(propertyName))
		{
			return propActivationColor;
		}
		if ("dwellTime".equalsIgnoreCase(propertyName))
		{
			return propDwellTime;
		}
		if ("idleTime".equalsIgnoreCase(propertyName))
		{
			return propIdleTime;
		}
		if ("opacity".equalsIgnoreCase(propertyName))
		{
			return propOpacity;
		}
		if ("stayActive".equalsIgnoreCase(propertyName))
		{
			return propStayActive;
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
		if ("dataSource".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDataSource;
			propDataSource = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("caption".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCaption;
			propCaption = (String)newValue;
			return oldValue;
		}
		if ("fontSize".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFontSize;
			propFontSize = Integer.parseInt(newValue.toString());
			if (propFontSize <0) propFontSize=0;
			return oldValue;
		}
		if ("textColor".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTextColor;
			propTextColor = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("backgroundColor".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBackgroundColor;
			propBackgroundColor = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("activationColor".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propActivationColor;
			propActivationColor = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("dwellTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDwellTime;
			propDwellTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("idleTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIdleTime;
			propIdleTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("opacity".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propOpacity;
			propOpacity = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("stayActive".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStayActive;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propStayActive = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propStayActive = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipX  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			 currentX = ConversionUtils.intFromBytes(data); 
			 checkHoverState();
		}
	};
	private final IRuntimeInputPort ipY  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			 currentY = ConversionUtils.intFromBytes(data);
			 checkHoverState();
		}
	};

	
	
    Color getColorProperty(int index)
    {
    	switch (index) {
    	case 0: return(Color.BLACK); 
    	case 1: return(Color.BLUE); 
    	case 2: return(Color.CYAN); 
    	case 3: return(Color.DARK_GRAY); 
    	case 4: return(Color.GRAY); 
    	case 5: return(Color.GREEN); 
    	case 6: return(Color.LIGHT_GRAY);
    	case 7: return(Color.MAGENTA); 
    	case 8: return(Color.ORANGE); 
    	case 9: return(Color.PINK); 
    	case 10: return(Color.RED); 
    	case 11: return(Color.WHITE);
    	case 12: return(Color.YELLOW); 
    	default: return(Color.BLUE);
    	}
    }

	 float dRed = 0,r=0;
	 float dGreen = 0,g=0;
	 float dBlue = 0,b=0;
	 float c=0;

	private void initColorDeltas() 
	{
	  dRed = getColorProperty(propActivationColor).getRed()-getColorProperty(propBackgroundColor).getRed();
	  dGreen = getColorProperty(propActivationColor).getGreen()-getColorProperty(propBackgroundColor).getGreen();
	  dBlue = getColorProperty(propActivationColor).getBlue()-getColorProperty(propBackgroundColor).getBlue();
	}
    
	private void checkHoverState()
	{
		
	 if ((idleState==1) || (active ==false)) return;
	 
	 if ((currentX > position.x) && (currentX < position.x + dimension.width) 
			  && (currentY > position.y) && (currentY < position.y + dimension.height))
	 {
		 
		 if (hoverState==0)
		 {
			 hoverState=1;
			 hoverTime=System.currentTimeMillis();
			 etpEnter.raiseEvent();
			 			 
			  AstericsThreadPool.instance.execute(new Runnable() {
				  public void run()
				  {
					  while ((System.currentTimeMillis()-hoverTime < propDwellTime) && (hoverState == 1) && (active==true))
					  {
		    				try
		    				{
								   Thread.sleep(10);
								   c= (float)(System.currentTimeMillis()-hoverTime)/(float)propDwellTime;
								   if (c>1.0f) c=1.0f;

								   r= getColorProperty(propBackgroundColor).getRed()+dRed*c;
								   g= getColorProperty(propBackgroundColor).getGreen()+dGreen*c;
								   b= getColorProperty(propBackgroundColor).getBlue()+dBlue*c;

								   //float b= 255.0f*c;
								   setPanelBackground(new Color((int)r,(int)g,(int)b));

		    				}
		    				catch (InterruptedException e) {}
					  }
					  if ((hoverState==1) && (active == true))
					  {
						  selected=1;
						  etpSelected.raiseEvent();
						  setPanelBackground(Color.CYAN);
					  }
					  else
					  {
						  setPanelBackground(getColorProperty(propBackgroundColor));
					  }

		    	  }
			  });
	      } 
     }
	 else  // coordinates are not within the hoverpanel
	 {
		 if (hoverState==1)
		 {
			 etpExit.raiseEvent();
		     hoverState=0;
		     
		     if (selected==1)
		     {
		       selected=0;
		       idleState=1;
			   idleTime=System.currentTimeMillis();
			   AstericsThreadPool.instance.execute(new Runnable() {
				  public void run()
				  {
					  while ((System.currentTimeMillis()-idleTime < propIdleTime) && (active==true))
					  {
		    				try
		    				{
								   Thread.sleep(10);
								   c= (float)(System.currentTimeMillis()-idleTime)/(float)propIdleTime;
								   if (c>1.0f) c=1.0f;
								   r= getColorProperty(propActivationColor).getRed()-dRed*c;
								   g= getColorProperty(propActivationColor).getGreen()-dGreen*c;
								   b= getColorProperty(propActivationColor).getBlue()-dBlue*c;
			   
								   setPanelBackground(new Color((int)r,(int)g,(int)b));

		    				}
		    				catch (InterruptedException e) {}
					  }
					  
					  setPanelBackground(getColorProperty(propBackgroundColor));
					  idleState=0;
		    	  }
			  });
		     }
		     else
				  setPanelBackground(getColorProperty(propBackgroundColor));

		 }
	 }
	}
	
	
	void setPanelBackground(final Color c)
	{
		SwingUtilities.invokeLater(new Runnable() {  			
			@Override
			public void run() {
				if (gui != null)
				{
					  gui.getContentPane().setBackground(c);
				}				
			}
		});

	}
     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpActivate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				active=true; 
		}
	};
	final IRuntimeEventListenerPort elpDeactivate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			active=false;
		    setPanelBackground(getColorProperty(propBackgroundColor));
		}
	};

	 Point position;
	 Dimension dimension;

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	    initColorDeltas();
    	    position = AREServices.instance.getComponentPosition(this);
    	    dimension = AREServices.instance.getAvailableSpace(this);
    	    active=true;
   	  
		    gui = new GUI(this,position,dimension);
    	  
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
    	   super.stop();
    	   
    	   active=false;
    	   selected=0;
    	   hoverState=0;
    	   idleState=0;
    	   
    	   System.out.println("HoverPanel "+propCaption+" Stop called !!");
    	   
    	   // gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
    	   // System.out.println("HoverPanel "+propCaption+"after dispatch !!");

    		SwingUtilities.invokeLater(new Runnable() {  			
    			@Override
    			public void run() {
    				if (gui != null)
    				{
    				    gui.setVisible(false);
    					gui.dispose();
    					gui=null;
    			        System.out.println("HoverPanel "+propCaption+"after dispatch !!");
    				}				
    			}
    		});
      }
}