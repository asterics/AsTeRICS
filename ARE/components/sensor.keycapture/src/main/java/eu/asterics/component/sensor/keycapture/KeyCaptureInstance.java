

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

package eu.asterics.component.sensor.keycapture; 


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import java.util.concurrent.AbstractExecutorService;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.NativeInputEvent;
import java.lang.reflect.Field;

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
public class KeyCaptureInstance extends AbstractRuntimeComponentInstance implements NativeKeyListener 
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpKeyPressed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpKeyReleased = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propKeyCode = 0;
	boolean propBlock = false;
	boolean enabled = true;
	
	// declare member variables here
	private boolean pressed;
   /**
    * The class constructor.
    */
    public KeyCaptureInstance()
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
		if ("keyCode".equalsIgnoreCase(portID))
		{
			return ipKeyCode;
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
		if ("enable".equalsIgnoreCase(eventPortID))
		{
			return elpEnable;
		}
		if ("disable".equalsIgnoreCase(eventPortID))
		{
			return elpDisable;
		}
		if ("block".equalsIgnoreCase(eventPortID))
		{
			return elpBlock;
		}
		if ("unblock".equalsIgnoreCase(eventPortID))
		{
			return elpUnblock;
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
		if ("keyPressed".equalsIgnoreCase(eventPortID))
		{
			return etpKeyPressed;
		}
		if ("keyReleased".equalsIgnoreCase(eventPortID))
		{
			return etpKeyReleased;
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
		if ("keyCode".equalsIgnoreCase(propertyName))
		{
			return propKeyCode;
		}
		if ("block".equalsIgnoreCase(propertyName))
		{
			return propBlock;
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
		if ("keyCode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propKeyCode;
			propKeyCode = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("block".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBlock;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propBlock = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propBlock = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipKeyCode  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 propKeyCode = ConversionUtils.intFromBytes(data); 
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpEnable = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 enabled = true;
		}
	};
	final IRuntimeEventListenerPort elpDisable = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 enabled = false;
		}
	};
	final IRuntimeEventListenerPort elpBlock = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 propBlock = true;
				 
		}
	};
	final IRuntimeEventListenerPort elpUnblock = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 propBlock = false;
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		  pressed = false;
		  try 
		  {
			GlobalScreen.getInstance().setEventDispatcher(new VoidExecutorService());
			GlobalScreen.registerNativeHook();
		  } catch (NativeHookException ne) 
		  {
		  }
          GlobalScreen.getInstance().addNativeKeyListener(this);
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
		  GlobalScreen.unregisterNativeHook();
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
		  pressed = false;
		  try 
		  {
			GlobalScreen.registerNativeHook();
		  } catch (NativeHookException ne) 
		  { 
		  }
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {	  
		  GlobalScreen.unregisterNativeHook();
          super.stop();
      }
	  
	 @Override
	public void nativeKeyPressed(NativeKeyEvent nke) 
	{
		if (enabled == false)
			return;
		int keyCode = nke.getKeyCode();
		if (pressed == false && keyCode == propKeyCode) {
			etpKeyPressed.raiseEvent();
			pressed = true;
			if (propBlock) 
			{
				try 
				{
					Field f = NativeInputEvent.class.getDeclaredField("reserved");
					f.setAccessible(true);
					f.setShort(nke, (short) 0x01);
				} catch (NoSuchFieldException nsfe) {
				} catch (IllegalAccessException iae) { 
				}
				
			}
		}		
    }
    
	
	
    @Override
    public void nativeKeyReleased(NativeKeyEvent nke) {
		if (enabled == false)
			return;
        int keyCode = nke.getKeyCode();
		if (keyCode == propKeyCode) {
			etpKeyReleased.raiseEvent();
			pressed = false;
		}
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nke) {
        
    }
	
	private static class VoidExecutorService extends AbstractExecutorService {
        private boolean isRunning;

        public VoidExecutorService() {
            isRunning = true;
        }

        public void shutdown() {
            isRunning = false;
        }

        public List<Runnable> shutdownNow() {
            return new ArrayList<Runnable>(0);
        }

        public boolean isShutdown() {
            return !isRunning;
        }

        public boolean isTerminated() {
            return !isRunning;
        }

        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return true;
        }

        public void execute(Runnable r) {
            r.run();
        }
    }
    
}