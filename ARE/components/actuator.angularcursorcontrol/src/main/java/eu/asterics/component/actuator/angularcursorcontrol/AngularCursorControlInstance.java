

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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.angularcursorcontrol;


import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
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
 */
public class AngularCursorControlInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpClickEvent = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	boolean propAbsoluteAngle = false;
	int propCreateClickEvent = 1000;
	int propArrowWidth = 200;
	int propArrowLength = 200;
	int propAcceleration = 100;
	int propAngularVelocity = 100;

	// declare member variables here

    // declare member variables here
    private GUI gui = null;
    private float actangle =0;
    private boolean running;
    
    volatile long elapsedIdleTime=Long.MAX_VALUE;
  
    
   /**
    * The class constructor.
    */
    public AngularCursorControlInstance()
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
		if ("angle".equalsIgnoreCase(portID))
		{
			return ipAngle;
		}
		if ("move".equalsIgnoreCase(portID))
		{
			return ipMove;
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
		if ("select".equalsIgnoreCase(eventPortID))
		{
			return elpSelect;
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
		if ("clickEvent".equalsIgnoreCase(eventPortID))
		{
			return etpClickEvent;
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
		if ("absoluteAngle".equalsIgnoreCase(propertyName))
		{
			return propAbsoluteAngle;
		}
		if ("createClickEvent".equalsIgnoreCase(propertyName))
		{
			return propCreateClickEvent;
		}
		if ("arrowWidth".equalsIgnoreCase(propertyName))
		{
			return propArrowWidth;
		}
		if ("arrowLength".equalsIgnoreCase(propertyName))
		{
			return propArrowLength;
		}
		if ("acceleration".equalsIgnoreCase(propertyName))
		{
			return propAcceleration;
		}
		if ("angularVelocity".equalsIgnoreCase(propertyName))
		{
			return propAngularVelocity;
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
		if ("absoluteAngle".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAbsoluteAngle;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAbsoluteAngle = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAbsoluteAngle = false;
			}
			return oldValue;
		}
		if ("createClickEvent".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCreateClickEvent;
			propCreateClickEvent = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("arrowWidth".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArrowWidth;
			propArrowWidth = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("arrowLength".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArrowLength;
			propArrowLength = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("acceleration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAcceleration;
			propAcceleration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angularVelocity".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngularVelocity;
			propAngularVelocity = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipAngle  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
            elapsedIdleTime=Long.MAX_VALUE;
		    if (propAbsoluteAngle==true)  {
		        actangle=(float)ConversionUtils.doubleFromBytes(data);
		    }
		    else {
	              actangle+=(float)ConversionUtils.doubleFromBytes(data);
            }
            gui.setShape(actangle);
		}
	};
	private final IRuntimeInputPort ipMove  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
            elapsedIdleTime=System.currentTimeMillis();
            double actmove=ConversionUtils.doubleFromBytes(data);
            double dx= actmove * Math.sin(actangle);
            double dy= - (actmove * Math.cos(actangle));
            gui.moveCursor(dx,dy);
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSelect = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    elapsedIdleTime=System.currentTimeMillis();
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
          gui = new GUI(this, new Dimension(propArrowWidth,propArrowLength));
          if(gui!=null) {
              //gui.addMouseListener(this);
              //gui.addMouseMotionListener(this);
          }
          
          super.start();
          
          elapsedIdleTime=Long.MAX_VALUE;
          running = true;
          
          AstericsThreadPool.instance.execute(new Runnable() {
              @Override
              public void run() {
                  while (running) {
                  try {
                      Thread.sleep(20);
                      if ((System.currentTimeMillis()-elapsedIdleTime)>propCreateClickEvent)
                      {
                          etpClickEvent.raiseEvent();
                          gui.setOnTop();
                          elapsedIdleTime=Long.MAX_VALUE;
                      }
                  } catch (InterruptedException e) {
                  }
                 }
              }
          });

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
          running=false;
          final GUI guiToDestroy=gui;
          gui=null;
          SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                  //now the cleanup of the window can be done at any time in the event dispatch thread wihtout interfering the other code.
                  if (guiToDestroy != null) {
                      //guiToDestroy.removeMouseListener(HoverPanelInstance.this);
                      //guiToDestroy.removeMouseMotionListener(HoverPanelInstance.this);
                      guiToDestroy.setVisible(false);
                      guiToDestroy.dispose();
                  }
              }
          });
      }
}