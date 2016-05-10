
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

package eu.asterics.component.actuator.keyboard;
import java.util.*;
import java.io.*;
import javax.swing.KeyStroke;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 *   Implements the Keyboard plugin, which creates local keystrokes
 *   via a JNI interface to a function which performs the windows API calls
 *   A list of keycodes is imported from the file "keycodes.txt"
 *   Note: not all keyboard function / keycodes are supported right now.
 *   
 * @author Christoph Veigl [christoph.veigl@technikum-wien.at]
 *         Date: Apr 24, 2011
 *         Time: 04:45:08 PM
 */
public class KeyboardInstance extends AbstractRuntimeComponentInstance
{  
    /**  
     * Statically load the native library
     */  
    static      
    {   
        System.loadLibrary("kbdevent");
    	AstericsErrorHandling.instance.getLogger().fine("Loading \"kbdevent.dll\" for Keystrike generation... ok!");  
    }
      
	private final String ELP_SENDKEYS_NAME 	= "sendKeys";
	private final String ELP_PRESSKEY_NAME 	= "pressKey";
	private final String ELP_HOLDKEY_NAME 	= "holdKey";
	private final String ELP_RELEASEKEY_NAME = "releaseKey";

	private final int MODE_PRESS = 0;
	private final int MODE_HOLD = 1;
	private final int MODE_RELEASE = 2;
	
    private String propKeyCodeString = "";
    private int propInputMethod = 1;
    private int propWaitTime = 1000;

    private int actSendPos = 0;
    private Vector<Integer> keyCodeArray; 
	private long lastUpdate = 0;
	private Hashtable<String, Integer> keyCodeMap;	

	native public int keyPress(int keycode);
	native public int keyRelease(int keycode);
	native public int keyPressSi(int keycode);     // using SendInput
	native public int keyReleaseSi (int keycode);  // using SendInput
	
    /**
     * The class constructor, loads the keycode map
     */	
   public KeyboardInstance()
    {

		try 
		{
			FileReader fin = new FileReader("data/actuator.keyboard/keycodes.txt");
			LineNumberReader in = new LineNumberReader(fin);
			keyCodeMap = new Hashtable<String, Integer>();
			String actLine = null;
			String actKey = null;
			String actCode = null;		
			Integer code=0;
			Integer count=0;
			    
			try
			{
				while ( (actLine = in.readLine()) != null)
				{
				    StringTokenizer st = new StringTokenizer(actLine," ,;\r\n");
				    if (st.hasMoreTokens()) actCode=st.nextToken(); else actCode=null;
				    if (st.hasMoreTokens()) actKey=st.nextToken(); else actKey=null;
				    if ((actKey != null) && (actCode!=null)) 
				    {
				    	try 
				    	{ 
				    		code = Integer.parseInt(actCode);
					    	keyCodeMap.put(actKey, code);
					    	count++;
				    	}
				        catch (NumberFormatException e) 
				        {
				        	AstericsErrorHandling.instance.reportInfo(this, "Wrong key Code " +  actCode);
				        }		
				    }
				}
			}
	 		catch (IOException e)
			{
	 			AstericsErrorHandling.instance.reportInfo(this, "I/O Error reading KeyCode File. ");
				
			}
	 		AstericsErrorHandling.instance.reportInfo(this, "KeyCode file parsed, "+count+" Codes found.");
		}
        catch (FileNotFoundException e) 
        {
        	AstericsErrorHandling.instance.reportInfo(this, "KeyCode File not found ");
        }			
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
   public IRuntimeInputPort getInputPort(String portID)
    {
        if("keyCodes".equalsIgnoreCase(portID))
        { 
            return ipKeyCodes;
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
        if(ELP_SENDKEYS_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpSendKeys;
        }
        else if(ELP_PRESSKEY_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpPressKey;
        }
        else if(ELP_HOLDKEY_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpHoldKey;
        }
        else if(ELP_RELEASEKEY_NAME.equalsIgnoreCase(eventPortID))
        {
            return elpReleaseKey;
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
        if("keyCodeString".equalsIgnoreCase(propertyName))
        {
            return propKeyCodeString;
        }
        if("inputMethod".equalsIgnoreCase(propertyName))
        { 
            return propInputMethod;
        }
        if("waitTime".equalsIgnoreCase(propertyName))
        { 
            return propWaitTime;
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
        if("keyCodeString".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propKeyCodeString;

            propKeyCodeString=(String)newValue;
            keyCodeTranslate();
            return oldValue;
        }
		else if("inputMethod".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = this.propInputMethod;
			propInputMethod = Integer.parseInt((String) newValue);
			return oldValue;
		}
		else if("waitTime".equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = propWaitTime;
			propWaitTime = Integer.parseInt((String) newValue);
			if (propWaitTime<1) propWaitTime=1;
			return oldValue;
		}

        return null;
    }


   /**
    * translates the keycode-string (e.g. "{SHIFT}A") into a Keycode.
    * the keycode string is expected in property variable propKeyCodeString,
    * the Keycodes are returned in vector keyCodeArray 
    */
	public void keyCodeTranslate()
	{
		Integer keyCode=0;
		String actToken;
		int index=0;
        char character;
        keyCodeArray = new Vector<Integer>();

		while (index<propKeyCodeString.length())
		{
			try {character=propKeyCodeString.charAt(index); } 
			catch (Exception e) {character=' ';};
			
			if (character=='{')  // process a masked character specified in keyCode-File
			{
				try { 
					actToken = propKeyCodeString.substring (index, propKeyCodeString.indexOf("}",index)+1);
					// Logger.getAnonymousLogger().info("next Token: " +  actToken);
				} catch (Exception e) {	actToken="invalid";};

				index+=actToken.length();
				Integer n = keyCodeMap.get(actToken);
				 if (n != null) {
					 // System.out.println("Token "+actToken+" found, value = " +  n);
					 keyCode = n;
				 }
			}
			else 
			{
				KeyStroke key = KeyStroke.getKeyStroke("pressed " + Character.toUpperCase(character) );
				if( null != key ) 
				{
				  if ((character>='A')&&(character<='Z'))
				  {
						keyCodeArray.add(16);  // SHIFT
				  }
				  keyCode=key.getKeyCode();
				  index++;
				}
				else
				{
					switch (character) {
					   case ' ': keyCode=32; break;
					   case '-': keyCode=189; break;
					   case '+': keyCode=187; break;
					   case '.': keyCode=190; break;
					   case ',': keyCode=188; break;
					   case '*': keyCode=106; break;
					   case '<': keyCode=226; break;
					   case '#': keyCode=191; break;
					   case ':': keyCodeArray.add(16);keyCode=190; break;
					   case '_': keyCodeArray.add(16);keyCode=189; break;
					   case '(': keyCodeArray.add(16);keyCode=56; break;
					   case ')': keyCodeArray.add(16);keyCode=57; break;
					   case '[': keyCodeArray.add(18);keyCode=56; break;
					   case ']': keyCodeArray.add(18);keyCode=57; break;
					   case '{': keyCodeArray.add(18);keyCode=55; break;
					   case '}': keyCodeArray.add(18);keyCode=58; break;
					   case ';': keyCodeArray.add(16);keyCode=188; break;
					   case '/': keyCodeArray.add(16);keyCode=55; break;
					   case '>': keyCodeArray.add(16);keyCode=226; break;
					   case '?': keyCodeArray.add(16);keyCode=219; break;
					   case '!': keyCodeArray.add(16);keyCode=49; break;
					   case '&': keyCodeArray.add(16);keyCode=54; break;
					   case '$': keyCodeArray.add(16);keyCode=52; break;
					   case '\"': keyCodeArray.add(16);keyCode=50; break;
					   case '\'': keyCodeArray.add(16);keyCode=191; break;
					   case '\\': keyCodeArray.add(18);keyCode=219; break;
					   case '@':  keyCodeArray.add(18);keyCode=81; break;				   
					   default: keyCode=32;break;
					}
					index++;
					
				}
			}
			// System.out.println("Adding Code "+keyCode+" for character "+character);
			keyCodeArray.add(keyCode);
			keyCode=0;			
		}
		actSendPos=0;
	}

   /**
    * sends a keycode of given index (in the keycode vector) and mode
    */
	public int sendKeyCode(int index, int mode)
	{ 
		int i=0;
		int actcode=-1;
		int modifier[]=new int[20];
		int mcount=0;
		boolean waitForKey=true;
	
		if (mode==MODE_HOLD)
			System.out.println("holding Key ");
		else if (mode==MODE_PRESS)
			System.out.println("press Key ");
		else if (mode==MODE_RELEASE)
			System.out.println("release Key ");
		
		
		while ((index<keyCodeArray.size()) && (waitForKey))
		{
			actcode= keyCodeArray.get(index);
			if ((mode == MODE_PRESS) || (mode == MODE_HOLD))
			{
				switch (actcode) {
					case 1: 	System.out.println("waiting: "+propWaitTime);
								try {Thread.sleep(propWaitTime);} catch (Exception e) {}; break;
					case 11:
					case 16:
					case 17: 
					case 18:
					case 91:
					case 92:
					case 157:
					case 164:
						    if (mcount<20) 
							{
								System.out.println("modifier: "+actcode);
								modifier[mcount++]=actcode;
							}
						break;
					default: waitForKey=false;
				}
				if (actcode !=1)
				{
					System.out.println("press "+actcode);
					if (propInputMethod==1)
						keyPressSi(actcode);
					else keyPress(actcode);
				}
			}
			
			index++;
			i++;
		}
		
		if (mode != MODE_HOLD)
		{  
			if (actcode!=-1)
			{
				System.out.println("release "+actcode);
				if (propInputMethod==1)
					keyReleaseSi(actcode);
				else keyRelease(actcode);
			}

			while (mcount>0)
			{
				System.out.println("release modifier "+modifier[mcount-1]);

				if (propInputMethod==1)
					keyReleaseSi(modifier[--mcount]);
				else keyRelease(modifier[--mcount]);
			}
		}
        return(i);
	}

  /**
   * sends all keycodes of the keycode vector
   */
	public void sendAllCodes()
	{
		int code=0;
		int i=0;
		
		while(i<keyCodeArray.size())
		{
			// 	Logger.getAnonymousLogger().fine("sending keycode");
			i+=sendKeyCode(i,MODE_PRESS);			
		}			
	}

	
   /**
    * Input Port for receiving keyCode strings.
    */
   private final IRuntimeInputPort ipKeyCodes
            = new DefaultRuntimeInputPort()
    {
        public void receiveData(byte[] data)
        {
        	// 	Logger.getAnonymousLogger().info("received keycodes: " +  keycodes);
        	propKeyCodeString = ConversionUtils.stringFromBytes(data);
            keyCodeTranslate();
            sendAllCodes();

        }

    };

 
   /**
    * Event Listener Port for sending all keycodes.
    */
   final IRuntimeEventListenerPort elpSendKeys 	= new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
    		//  Logger.getAnonymousLogger().info("received SendKeys event ");           
            sendAllCodes();
    	 }
    };    
    
    
    /**
     * Event Listener Port for pressing next key.
     */
    final IRuntimeEventListenerPort elpPressKey 	= new IRuntimeEventListenerPort()
    {
   	 public void receiveEvent(final String data)
   	 {
			int code=0;
			// AstericsErrorHandling.instance.getLogger().fine(String.format("received keypress event, sending: %04x",keyCodeArray.get(actSendPos)));            
			sendKeyCode(actSendPos,MODE_PRESS);			
			if (++actSendPos>=keyCodeArray.size()) 
				actSendPos=0;
  	 }
    }; 

    /**
     * Event Listener Port for holding next key.
     */
   final IRuntimeEventListenerPort elpHoldKey 	= new IRuntimeEventListenerPort()
    {
   	 public void receiveEvent(final String data)
   	 {
			int code=0;
			//AstericsErrorHandling.instance.getLogger().fine(String.format("received keyhold event, sending: %04x",keyCodeArray.get(actSendPos)));            
			sendKeyCode(actSendPos,MODE_HOLD);			
			//if (++actSendPos>=keyCodeArray.size()) 
				//actSendPos=0;
   	 }
   };  

   /**
    * Event Listener Port for releasing current key.
    */
    final IRuntimeEventListenerPort elpReleaseKey 	= new IRuntimeEventListenerPort()
    {
   	 public void receiveEvent(final String data)
   	 {
			int code=0;
			//AstericsErrorHandling.instance.getLogger().fine("received keyrelease event, sending.");
			sendKeyCode(actSendPos,MODE_RELEASE);			
   	 }
   }; 

   
    
   /**
    * called when model is started.
    */
  @Override
   public void start()
   {
   	//	port = CIMPortManager.getInstance().getConnection(key_ACTUATOR_CIM_ID);
       super.start();
       AstericsErrorHandling.instance.reportInfo(this, "KeyboardInstance started");
   }
  
   /**
    * called when model is paused.
    */
   @Override
   public void pause()
   {
      sendKeyCode(actSendPos,MODE_RELEASE);			
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
		sendKeyCode(actSendPos,MODE_RELEASE);			
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "Keyboard Instance stopped");
    }
}