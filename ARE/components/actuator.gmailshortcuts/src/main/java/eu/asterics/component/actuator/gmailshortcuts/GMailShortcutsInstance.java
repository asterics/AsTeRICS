

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

package eu.asterics.component.actuator.gmailshortcuts;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
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
import eu.asterics.mw.utils.OSUtils;
import eu.asterics.mw.services.AREServices;

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
public class GMailShortcutsInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public GMailShortcutsInstance()
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

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
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

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipKeyCode  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			//System.out.println("*!!!!!!!!!!!!!!!!");
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 String KeyCode = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
				
				String Use = KeyCode;
			 	//System.out.println("******* hey, got keystring:" +Use);
			 	
			 	 
			 	
			 	
				 try{	 
					
					 Robot robot = new Robot();
//					 LowerCase Alphabet
				
			 
					 
					 	if(Use.equals("A")) {
						 
					 		robot.keyPress(KeyEvent.VK_A);
					 		robot.keyRelease(KeyEvent.VK_A);
					 	}else  	if(Use.equals("B")) {
						 
					 		robot.keyPress(KeyEvent.VK_B);
					 		robot.keyRelease(KeyEvent.VK_B);
					 	}else  	if(Use.equals("C")) {
						 
					 		robot.keyPress(KeyEvent.VK_C);
					 		robot.keyRelease(KeyEvent.VK_C);
					 	}else  	if(Use.equals("D")) {
						 
					 		robot.keyPress(KeyEvent.VK_D);
					 		robot.keyRelease(KeyEvent.VK_D);
					 	}else  	if(Use.equals("E")) {
						 
					 		robot.keyPress(KeyEvent.VK_E);
					 		robot.keyRelease(KeyEvent.VK_E);
					 	}else  	if(Use.equals("F")) {
						 
					 		robot.keyPress(KeyEvent.VK_F);
					 		robot.keyRelease(KeyEvent.VK_F);
					 	}else  	if(Use.equals("G")) {
						 
					 		robot.keyPress(KeyEvent.VK_G);
					 		robot.keyRelease(KeyEvent.VK_G);
					 	}else  	if(Use.equals("H")) {
						 
					 		robot.keyPress(KeyEvent.VK_H);
					 		robot.keyRelease(KeyEvent.VK_H);
					 	}else  	if(Use.equals("I")) {
						 
					 		robot.keyPress(KeyEvent.VK_I);
					 		robot.keyRelease(KeyEvent.VK_I);
					 	}else  	if(Use.equals("J")) {
						 
					 		robot.keyPress(KeyEvent.VK_J);
					 		robot.keyRelease(KeyEvent.VK_J);
					 	}else  	if(Use.equals("K")) {
						 
					 		robot.keyPress(KeyEvent.VK_K);
					 		robot.keyRelease(KeyEvent.VK_K);
					 	}else  	if(Use.equals("L")) {
						 
					 		robot.keyPress(KeyEvent.VK_L);
					 		robot.keyRelease(KeyEvent.VK_L);
					 	}else  	if(Use.equals("M")) {
						 
					 		robot.keyPress(KeyEvent.VK_M);
					 		robot.keyRelease(KeyEvent.VK_M);
					 	}else  	if(Use.equals("N")) {
						 
					 		robot.keyPress(KeyEvent.VK_N);
					 		robot.keyRelease(KeyEvent.VK_N);
					 	}else  	if(Use.equals("O")) {
						 
					 		robot.keyPress(KeyEvent.VK_O);
					 		robot.keyRelease(KeyEvent.VK_O);
					 	}else  	if(Use.equals("P")) {
						 
					 		robot.keyPress(KeyEvent.VK_P);
					 		robot.keyRelease(KeyEvent.VK_P);
					 	}else  	if(Use.equals("Q")) {
						 
					 		robot.keyPress(KeyEvent.VK_Q);
					 		robot.keyRelease(KeyEvent.VK_Q);
					 	}else  	if(Use.equals("R")) {
						 
					 		robot.keyPress(KeyEvent.VK_R);
					 		robot.keyRelease(KeyEvent.VK_R);
					 	}else  	if(Use.equals("S")) {
						 
					 		robot.keyPress(KeyEvent.VK_S);
					 		robot.keyRelease(KeyEvent.VK_S);
					 	}else  	if(Use.equals("T")) {
						 
					 		robot.keyPress(KeyEvent.VK_T);
					 		robot.keyRelease(KeyEvent.VK_T);
					 	}else  	if(Use.equals("U")) {
						 
					 		robot.keyPress(KeyEvent.VK_U);
					 		robot.keyRelease(KeyEvent.VK_U);
					 	}else  	if(Use.equals("V")) {
						 
					 		robot.keyPress(KeyEvent.VK_V);
					 		robot.keyRelease(KeyEvent.VK_V);
					 	}else  	if(Use.equals("W")) {
						 
					 		robot.keyPress(KeyEvent.VK_W);
					 		robot.keyRelease(KeyEvent.VK_W);
					 	}else  	if(Use.equals("X")) {
						 
					 		robot.keyPress(KeyEvent.VK_X);
					 		robot.keyRelease(KeyEvent.VK_X);
					 	}else  	if(Use.equals("Y")) {
						 
					 		robot.keyPress(KeyEvent.VK_Y);
					 		robot.keyRelease(KeyEvent.VK_Y);
					 	}else  	if(Use.equals("Z")) {
						 
					 		robot.keyPress(KeyEvent.VK_Z);
					 		robot.keyRelease(KeyEvent.VK_Z);
					 	}
					 
					 
//					 	Zahlen
					 	
					 	else  	if(Use.equals("1")) {
						 
					 		robot.keyPress(KeyEvent.VK_1);
					 		robot.keyRelease(KeyEvent.VK_1);
					 	}else  	if(Use.equals("2")) {
						 
					 		robot.keyPress(KeyEvent.VK_2);
					 		robot.keyRelease(KeyEvent.VK_2);
					 	}else  	if(Use.equals("3")) {
						 
					 		robot.keyPress(KeyEvent.VK_3);
					 		robot.keyRelease(KeyEvent.VK_3);
					 	}else  	if(Use.equals("4")) {
						 
					 		robot.keyPress(KeyEvent.VK_4);
					 		robot.keyRelease(KeyEvent.VK_4);
					 	}else  	if(Use.equals("6")) {
						 
					 		robot.keyPress(KeyEvent.VK_6);
					 		robot.keyRelease(KeyEvent.VK_6);
					 	}else  	if(Use.equals("7")) {
						 
					 		robot.keyPress(KeyEvent.VK_7);
					 		robot.keyRelease(KeyEvent.VK_7);
					 	}else  	if(Use.equals("8")) {
						 
					 		robot.keyPress(KeyEvent.VK_8);
					 		robot.keyRelease(KeyEvent.VK_8);
					 	}else  	if(Use.equals("9")) {
						 
					 		robot.keyPress(KeyEvent.VK_9);
					 		robot.keyRelease(KeyEvent.VK_9);
					 	}	else  	if(Use.equals("0")) {
						 
					 		robot.keyPress(KeyEvent.VK_0);
					 		robot.keyRelease(KeyEvent.VK_0);
					 	}
					 	
//					 	Navigation
					 	
						else  	if(Use.equals("UP")) {
							 
					 		robot.keyPress(KeyEvent.VK_UP);
					 		robot.keyRelease(KeyEvent.VK_UP);
					 	}else  	if(Use.equals("DOWN")) {
						 
					 		robot.keyPress(KeyEvent.VK_DOWN);
					 		robot.keyRelease(KeyEvent.VK_DOWN);
					 	}else  	if(Use.equals("LEFT")) {
						 
					 		robot.keyPress(KeyEvent.VK_LEFT);
					 		robot.keyRelease(KeyEvent.VK_LEFT);
					 	}else  	if(Use.equals("RIGHT")) {
						 
					 		robot.keyPress(KeyEvent.VK_RIGHT);
					 		robot.keyRelease(KeyEvent.VK_RIGHT);
					 	}else  	if(Use.equals("ESC")) {
					 		robot.keyPress(KeyEvent.VK_ESCAPE);
					 		robot.keyRelease(KeyEvent.VK_ESCAPE);
					 	}else  	if(Use.equals("TAB")) {
					 		robot.keyPress(KeyEvent.VK_TAB);
					 		robot.keyRelease(KeyEvent.VK_TAB);
					 	}else  	if(Use.equals("ENTER")) {
							 
					 		robot.keyPress(KeyEvent.VK_ENTER);
					 		robot.keyRelease(KeyEvent.VK_ENTER);
					 	}else  	if(Use.equals("BACKSPACE")) {
							 
					 		robot.keyPress(KeyEvent.VK_BACK_SPACE);
					 		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
					 	}
					 	
//					 	Sonderzeichen
					 	
					 	else  	if(Use.equals("Shift")) {
					 		robot.keyPress(KeyEvent.VK_CAPS_LOCK);
					 		robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
					 	}else  	if(Use.equals(".")) {
					 		robot.keyPress(KeyEvent.VK_PERIOD);
					 		robot.keyRelease(KeyEvent.VK_PERIOD);
					 	}else  	if(Use.equals("@")) {
					 		robot.keyPress(KeyEvent.VK_AT);
					 		robot.keyRelease(KeyEvent.VK_AT);
					 	}else  	if(Use.equals(":")) {
					 		robot.keyPress(KeyEvent.VK_COLON);
					 		robot.keyRelease(KeyEvent.VK_COLON);
					 	}else  	if(Use.equals(";")) {
					 		robot.keyPress(KeyEvent.VK_SEMICOLON);
					 		robot.keyRelease(KeyEvent.VK_SEMICOLON);
					 	}else  	if(Use.equals(",")) {
					 		robot.keyPress(KeyEvent.VK_COMMA);
					 		robot.keyRelease(KeyEvent.VK_COMMA);
					 	}else  	if(Use.equals("!")) {
					 		robot.keyPress(KeyEvent.VK_EXCLAMATION_MARK);
					 		robot.keyRelease(KeyEvent.VK_EXCLAMATION_MARK);
					 	}else  	if(Use.equals("?")) {
							 
					 		robot.keyPress(KeyEvent.VK_SHIFT);
					 		robot.keyPress(KeyEvent.VK_SLASH);
					 		robot.keyRelease(KeyEvent.VK_SLASH);
					 		robot.keyRelease(KeyEvent.VK_SHIFT);
					 	}else  	if(Use.equals("_")) {
							 
					 		robot.keyPress(KeyEvent.VK_SHIFT);
					 		robot.keyPress(KeyEvent.VK_MINUS);
					 		robot.keyRelease(KeyEvent.VK_MINUS);
					 		robot.keyRelease(KeyEvent.VK_SHIFT);
					 	}
					 	
					 	
					 						 	
					 	
//					 	Gmail Shortcuts
					 	
					 	else  	if(Use.equals("SENDEN")) {
							 
					 		robot.keyPress(OSUtils.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
					 		robot.keyPress(KeyEvent.VK_ENTER);
					 		robot.keyRelease(KeyEvent.VK_ENTER);
					 		robot.keyRelease(OSUtils.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
					 	}else  	if(Use.equals("CC")) {
							 
					 		robot.keyPress(OSUtils.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
					 		robot.keyPress(KeyEvent.VK_SHIFT);
					 		robot.keyPress(KeyEvent.VK_C);
					 		robot.keyRelease(KeyEvent.VK_C);
					 		robot.keyRelease(KeyEvent.VK_SHIFT);
					 		robot.keyRelease(OSUtils.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
					 	}else  	if(Use.equals("NEW")) {
							 
					 		robot.keyPress(KeyEvent.VK_TAB);
					 		robot.keyPress(KeyEvent.VK_C);
					 		robot.keyRelease(KeyEvent.VK_C);
					 		robot.keyRelease(KeyEvent.VK_TAB);
					 	}else  	if(Use.equals("SEARCH")) {
							 
					 		robot.keyPress(KeyEvent.VK_TAB);
					 		robot.keyPress(KeyEvent.VK_G);
					 		robot.keyPress(KeyEvent.VK_L);
					 		robot.keyRelease(KeyEvent.VK_L);
					 		robot.keyRelease(KeyEvent.VK_G);
//					 		robot.keyRelease(KeyEvent.VK_TAB);

					 	}else  	if(Use.equals("INBOX")) {
							 
					 		robot.keyPress(KeyEvent.VK_TAB);
					 		robot.keyPress(KeyEvent.VK_G);
					 		robot.keyPress(KeyEvent.VK_I);
					 		robot.keyRelease(KeyEvent.VK_I);
					 		robot.keyRelease(KeyEvent.VK_G);
					 		robot.keyRelease(KeyEvent.VK_TAB);
					 	}else  	if(Use.equals("REPLY")) {
							 
					 		robot.keyPress(KeyEvent.VK_TAB);
					 		robot.keyPress(KeyEvent.VK_R);
					 		robot.keyRelease(KeyEvent.VK_R);
					 		robot.keyRelease(KeyEvent.VK_TAB);
					 	}else if(Use.equals("SELECT")){
					 			
					 		robot.keyPress(KeyEvent.VK_X);
					 		robot.keyRelease(KeyEvent.VK_X);
					 	
					 	}
					 
				}catch (AWTException e)	{
							
					e.printStackTrace();
				}
				 
				
				
		}
				 
				 
				 
		
	};
//	else  	if(Use.equals("strg_A")) {
//		 
// 		
// 		robot.keyPress(KeyEvent.VK_META);
// 		robot.keyPress(KeyEvent.VK_A);
// 		robot.keyRelease(KeyEvent.VK_A);
// 		robot.keyRelease(KeyEvent.VK_META);
// 	}

     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

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
      }
}