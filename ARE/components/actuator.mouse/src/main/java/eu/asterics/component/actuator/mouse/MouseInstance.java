
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

package eu.asterics.component.actuator.mouse;
import java.util.*;
import java.util.logging.Logger;

import eu.asterics.mw.data.*;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.event.InputEvent;


/**
 *  Implements the Mouse plugin, which controls the local mouse 
 *  using the Java AWT robot
 *   
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Apr 23, 2011
 *         Time: 2:00:01 PM
 */
public class MouseInstance extends AbstractRuntimeComponentInstance
{
	private final String ACTION_STRING_PREFIX 	= "@MOUSE:";
	
	private final String ELP_LEFTCLICK_NAME 	= "leftClick";
	private final String ELP_MIDDLECLICK_NAME 	= "middleClick";
	private final String ELP_RIGHTCLICK_NAME 	= "rightClick";
	private final String ELP_DOUBLECLICK_NAME 	= "doubleClick";
	private final String ELP_DRAGPRESS_NAME 	= "dragPress";		
	private final String ELP_DRAGRELEASE_NAME 	= "dragRelease";		
	private final String ELP_WHEELUP_NAME 		= "wheelUp";		
	private final String ELP_WHEELDOWN_NAME 	= "wheelDown";		
	private final String ELP_NEXTCLICKRIGHT_NAME   = "nextClickRight";		
	private final String ELP_NEXTCLICKDOUBLE_NAME  = "nextClickDouble";		
	private final String ELP_NEXTCLICKMIDDLE_NAME  = "nextClickMiddle";		
	private final String ELP_NEXTCLICKDRAG_NAME    = "nextClickDrag";		
	private final String ELP_NEXTCLICKRELEASE_NAME = "nextClickRelease";		
	private final String ELP_CENTER_NAME 		= "center";
	private final String ELP_ACTIVATE_NAME 		= "activate";
	private final String ELP_DEACTIVATE_NAME 	= "deactivate";
	private final String ELP_TOGGLE_NAME 		= "toggle";
	private final String ELP_ABSOLUTEPOSITION_NAME 	= "absolutePosition";
	private final String ELP_RELATIVEPOSITION_NAME 	= "relativePosition";

	private final int CLK_LEFT = 0;
	private final int CLK_RIGHT = 1;
	private final int CLK_DOUBLE = 2;
	private final int CLK_MIDDLE = 3;
	private final int CLK_DRAG = 4;
	private final int CLK_DRAGRELEASE = 5;

    private boolean propEnableMouse = false; 
    private boolean propAbsolutePosition = true;
    private int propXMin = 0;
    private int propXMax = 2000;
    private int propYMin = 0;
    private int propYMax = 1024;

    private boolean first=true;

    Robot rob;
    private double mouseXPos = 0;
    private double mouseYPos = 0;
	private double mouseLastXPos = -1;
    private double mouseLastYPos = -1;
    private int mouseActive = 0;
	private int nextClick = CLK_LEFT;
	
    /**
     * The class constructor.
     */	
    public MouseInstance()
    {
    	// create the AWT robot
    	try {
    	 rob = new Robot();
         rob.setAutoDelay(0);
    	}
    	catch(AWTException e){e.printStackTrace();}
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("mouseX".equalsIgnoreCase(portID))
        {
            return ipMouseX;
        }
        else if("mouseY".equalsIgnoreCase(portID))
        {
            return ipMouseY;
        }
        else if("action".equalsIgnoreCase(portID))
        {
            return ipAction;
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
     * @param portID   the name of the port
     * @return         the event listener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        if(ELP_LEFTCLICK_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpLeftClick;
        }
        else if(ELP_MIDDLECLICK_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpMiddleClick;
        }
        else if(ELP_RIGHTCLICK_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpRightClick;
        }
        else if(ELP_DOUBLECLICK_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpDoubleClick;
        }
        else if(ELP_DRAGPRESS_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpDragClick;
        }
        else if(ELP_DRAGRELEASE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpDragRelease;
        }
        else if(ELP_WHEELUP_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpWheelUp;
        }
        else if(ELP_WHEELDOWN_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpWheelDown;
        }
        else if(ELP_NEXTCLICKRIGHT_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpNextClickRight;
        }
        else if(ELP_NEXTCLICKDOUBLE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpNextClickDouble;
        }
        else if(ELP_NEXTCLICKMIDDLE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpNextClickMiddle;
        }
        else if(ELP_NEXTCLICKDRAG_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpNextClickDrag;
        }
        else if(ELP_NEXTCLICKRELEASE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpNextClickRelease;
        }
        else if(ELP_CENTER_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpCenter;
        }
        else if(ELP_ACTIVATE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpActivate;
        }
        else if(ELP_DEACTIVATE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpDeactivate;
        }
        else if(ELP_TOGGLE_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpToggle;
        } else if (ELP_RELATIVEPOSITION_NAME.equalsIgnoreCase(eventPortID)) 
		{
			return elpRelativePosition;
		} else if (ELP_ABSOLUTEPOSITION_NAME.equalsIgnoreCase(eventPortID)) 
		{
			return elpAbsolutePosition;
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
        if("absolutePosition".equalsIgnoreCase(propertyName))
        {
            return propAbsolutePosition;
        }
        else if("enableMouse".equalsIgnoreCase(propertyName))
        {
            return propEnableMouse;
        }
        else if("xMin".equalsIgnoreCase(propertyName))
        {
            return propXMin;
        }
        else if("xMax".equalsIgnoreCase(propertyName))
        {
            return propXMax;
        }
        else if("yMin".equalsIgnoreCase(propertyName))
        {
            return propYMin;
        }
        else if("yMax".equalsIgnoreCase(propertyName))
        {
            return propYMax;
        }

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if("absolutePosition".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propAbsolutePosition;

            if("true".equalsIgnoreCase((String)newValue))
            {
                propAbsolutePosition = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
                propAbsolutePosition = false;
            }

            return oldValue;
        }
        if("enableMouse".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propEnableMouse;

            if("true".equalsIgnoreCase((String)newValue))
            {
                propEnableMouse = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
                propEnableMouse = false;
            }

            return oldValue;
        }
        if("xMin".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propXMin;
            propXMin = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("xMax".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propXMax;
            
            propXMax = Integer.parseInt(newValue.toString());
            if (propXMax == 0) {
            	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            	propXMax = screenSize.width;
            }
            return oldValue;
        }
        if("yMin".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propYMin;
            propYMin = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if("yMax".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propYMax;
            propYMax = Integer.parseInt(newValue.toString());
            if (propYMax == 0) {
            	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            	propYMax=screenSize.height;
            }
            return oldValue;
        }

        return null;
    }
 
    private void updateMousePosition()
    {
		if (!first)
		{
			Point p= MouseInfo.getPointerInfo().getLocation(); 
			
			mouseXPos-= (mouseLastXPos- p.x);
			mouseYPos-= (mouseLastYPos- p.y);
		}
		first=false;

		if (mouseXPos<propXMin) mouseXPos=propXMin;
	    if (mouseXPos>propXMax) mouseXPos=propXMax;
	    if (mouseYPos<propYMin) mouseYPos=propYMin;
	    if (mouseYPos>propYMax) mouseYPos=propYMax;

	    rob.mouseMove((int)mouseXPos, (int)mouseYPos);

		mouseLastXPos = mouseXPos;
		mouseLastYPos = mouseYPos;

    }

    /**
     * Input Port for receiving mouse x coordinates.
     */
    private final IRuntimeInputPort ipMouseX
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            if ((mouseActive == 1) && (propEnableMouse))
            {

	        	if (propAbsolutePosition==true)
	        	{
	                mouseXPos = ConversionUtils.doubleFromBytes(data);
	        	}
	        	else 
	        	{
	                mouseXPos += ConversionUtils.doubleFromBytes(data);
	          	}

				if (mouseXPos != mouseLastXPos) 
					updateMousePosition();
            }
        }
    };

    /**
     * Input Port for receiving mouse y coordinates.
     */
    private final IRuntimeInputPort ipMouseY
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
            if ((mouseActive == 1) && (propEnableMouse))
            {
	        	if (propAbsolutePosition==true)
	        	{
	                mouseYPos = ConversionUtils.doubleFromBytes(data);
	        	}
	        	else 
	        	{
	                mouseYPos += ConversionUtils.doubleFromBytes(data);
	          	}

				if (mouseYPos != mouseLastYPos)
					updateMousePosition();
            }
       }
    };
    
    /**
     * Input Port for receiving mouse action commands.
     * supported commands are:
     * @MOUSE:nextclick,right    next left click will cause a right click
     * @MOUSE:nextclick,double   next left click will cause a double click
     * @MOUSE:nextclick,middle   next left click will cause a middle click
     * @MOUSE:nextclick,drag     next left click will cause a drag click
     * @MOUSE:nextclick,release  next left click will release the mouse button
     * @MOUSE:action,enable      mouse action is enabled
     * @MOUSE:action,disable     mouse action is disabled
     * @MOUSE:action,toggle      mouse action is inverted
     */    
    private final IRuntimeInputPort ipAction = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String text = ConversionUtils.stringFromBytes(data);

    		if (text.startsWith(ACTION_STRING_PREFIX)) {  			
				try {		
					StringTokenizer st = new StringTokenizer(text.substring(ACTION_STRING_PREFIX.length()),", ");
					String command = st.nextToken();
					if (command.equalsIgnoreCase("nextclick"))
					{
						String clickType=st.nextToken();
						if (clickType.equalsIgnoreCase("right")) nextClick=CLK_RIGHT;
						else if (clickType.equalsIgnoreCase("double")) nextClick=CLK_DOUBLE;
						else if (clickType.equalsIgnoreCase("middle")) nextClick=CLK_MIDDLE;
						else if (clickType.equalsIgnoreCase("drag")) nextClick=CLK_DRAG;
						else if (clickType.equalsIgnoreCase("release")) nextClick=CLK_DRAGRELEASE;
						else nextClick=CLK_LEFT;
					}	
					else if (command.equalsIgnoreCase("action"))
					{
						String actionType=st.nextToken();
						if (actionType.equalsIgnoreCase("enable")) propEnableMouse = true;
						else if (actionType.equalsIgnoreCase("disable")) propEnableMouse = false;
						else if (actionType.equalsIgnoreCase("toggle")) propEnableMouse = !propEnableMouse;
					}
				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(e.toString());
				}
    		}
		}
	};

  /**
   * Event Listener Port for left click.
   */
   final IRuntimeEventListenerPort elpLeftClick 	= new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
             if ((mouseActive == 1) && (propEnableMouse)) 
             {
        		 if (nextClick==CLK_LEFT)
        		 {
					rob.mousePress(InputEvent.BUTTON1_MASK);
					rob.mouseRelease(InputEvent.BUTTON1_MASK);
        		 }
	    		 else if (nextClick==CLK_RIGHT)  elpRightClick.receiveEvent(null);
	    		 else if (nextClick==CLK_DOUBLE) elpDoubleClick.receiveEvent(null);
	    		 else if (nextClick==CLK_MIDDLE) elpMiddleClick.receiveEvent(null);
	    		 else if (nextClick==CLK_DRAG)   elpDragClick.receiveEvent(null);
	       		 else if (nextClick==CLK_DRAGRELEASE) elpDragRelease.receiveEvent(null);
        		 nextClick=CLK_LEFT;   			 

             }
    	 }
    };    
    
    
    /**
     * Event Listener Port for right click.
     */
    final IRuntimeEventListenerPort elpRightClick 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	         if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mousePress(InputEvent.BUTTON3_MASK);
				rob.mouseRelease(InputEvent.BUTTON3_MASK);
	         }
	   	 }
    }; 
   
   /**
    * Event Listener Port for middle click.
    */
    final IRuntimeEventListenerPort elpMiddleClick 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	         if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mousePress(InputEvent.BUTTON2_MASK);
				rob.mouseRelease(InputEvent.BUTTON2_MASK);
	         }
	   	 }
   };
   
   /**
    * Event Listener Port for drag click.
    */
   final IRuntimeEventListenerPort elpDragClick 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	   	     if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mousePress(InputEvent.BUTTON1_MASK);
	         }
	   	 }
   }; 
   
   /**
    * Event Listener Port for click release.
    */
    final IRuntimeEventListenerPort elpDragRelease 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	   	     if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mouseRelease(InputEvent.BUTTON1_MASK);
	         }
	   	 }
   }; 
	
   /**
    * Event Listener Port for double click.
    */
   final IRuntimeEventListenerPort elpDoubleClick 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	         if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mousePress(InputEvent.BUTTON1_MASK);
				rob.mouseRelease(InputEvent.BUTTON1_MASK);
				rob.mousePress(InputEvent.BUTTON1_MASK);
				rob.mouseRelease(InputEvent.BUTTON1_MASK);
	         }
	   	 }
    }; 
	
    /**
     * Event Listener Port for wheel up.
     */
    final IRuntimeEventListenerPort elpWheelUp 	= new IRuntimeEventListenerPort()
    {
	   	 public void receiveEvent(final String data)
	   	 {
	   	     if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mouseWheel(1);
	         }
	   	 }
   }; 
   
   /**
    * Event Listener Port for wheel down.
    */
   final IRuntimeEventListenerPort elpWheelDown 	= new IRuntimeEventListenerPort()
   {
	   	 public void receiveEvent(final String data)
	   	 {
	   	     if ((mouseActive == 1) && (propEnableMouse)) 
	         {
				rob.mouseWheel(-1);
	         }
	   	 }
  }; 

  
  /**
   * Event Listener Port for NextClickRight.
   */
  final IRuntimeEventListenerPort  elpNextClickRight = new IRuntimeEventListenerPort()
  {
	   	 public void receiveEvent(final String data)
	   	 {
	   		nextClick=CLK_RIGHT;
	   	 }
 }; 

 /**
  * Event Listener Port for NextClickDouble.
  */
 final IRuntimeEventListenerPort  elpNextClickDouble = new IRuntimeEventListenerPort()
 {
	   	 public void receiveEvent(final String data)
	   	 {
	   		nextClick=CLK_DOUBLE;
	   	 }
}; 

/**
 * Event Listener Port for NextClickMiddle.
 */
final IRuntimeEventListenerPort  elpNextClickMiddle = new IRuntimeEventListenerPort()
{
	   	 public void receiveEvent(final String data)
	   	 {
	   		nextClick=CLK_MIDDLE;
	   	 }
}; 

/**
 * Event Listener Port for NextClickDrag.
 */
final IRuntimeEventListenerPort  elpNextClickDrag = new IRuntimeEventListenerPort()
{
	   	 public void receiveEvent(final String data)
	   	 {
	   		nextClick=CLK_DRAG;
	   	 }
}; 
  

/**
 * Event Listener Port for NextClickRelease.
 */
final IRuntimeEventListenerPort  elpNextClickRelease = new IRuntimeEventListenerPort()
{
	   	 public void receiveEvent(final String data)
	   	 {
	   		nextClick=CLK_DRAGRELEASE;
	   	 }
}; 


  /**
   * Event Listener Port for center mouse action.
   */
  final IRuntimeEventListenerPort elpCenter 	= new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
         mouseXPos = propXMin+(propXMax-propXMin)/2;
         mouseYPos = propYMin+(propYMax-propYMin)/2;
  	 }
  };
  
  /**
   * Event Listener Port for activate mouse action.
   */
  final IRuntimeEventListenerPort elpActivate 	= new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
  		propEnableMouse = true; 
  	 }
  };
  /**
   * Event Listener Port for deactivate mouse action.
   */
  final IRuntimeEventListenerPort elpDeactivate 	= new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
  		propEnableMouse = false; 
  	 }
  };
  /**
   * Event Listener Port for toggle mouse action.
   */
  final IRuntimeEventListenerPort elpToggle 	= new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
  		propEnableMouse = !propEnableMouse; 
  	 }
  };

  
  /**
   * Event Listener Port for set relative positioning
   */
  final IRuntimeEventListenerPort elpRelativePosition = new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
  		propAbsolutePosition = false;
  	 }
  };

  /**
   * Event Listener Port for set absolute positioning
   */
  final IRuntimeEventListenerPort elpAbsolutePosition = new IRuntimeEventListenerPort()
  {
  	 public void receiveEvent(final String data)
  	 {
  		propAbsolutePosition = true;
  	 }
  };
  
  /**
   * called when model is started.
   */
   @Override
    public void start()
    {
        super.start();
        first=true;
        mouseXPos = propXMin +(propXMax - propXMin)/2;
        mouseYPos = propYMin +(propYMax - propYMin)/2;
        mouseActive=1;
    	nextClick = CLK_LEFT;
        // AstericsErrorHandling.instance.reportInfo(this, "Mouse Instance started");     
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
	   first=true;
       super.resume();
   }

   /**
    * called when model is stopped.
    */
   @Override
    public void stop()
    {
        super.stop();
        mouseActive=0;
    }

	@Override
	public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {
		
		double inX = 0;
		double inY = 0;
		
		for (String s: dataRow.keySet())
		{
			
			byte [] data = dataRow.get(s);
			if (s.equals("mouseX"))
			{
				inX = ConversionUtils.doubleFromBytes(data);
			}
			if (s.equals("mouseY"))
			{
				inY = ConversionUtils.doubleFromBytes(data);
			}
		}

	    if ((mouseActive == 1) && (propEnableMouse))
	    {
		
			if (propAbsolutePosition==true)
			{
		        mouseXPos = inX;
		        mouseYPos = inY;
			}
			else 
			{
		        mouseXPos += inX;
		        mouseYPos += inY;
		  	}
	
			if (mouseXPos != mouseLastXPos || mouseYPos != mouseLastYPos)
				updateMousePosition();
	    }
	}
}