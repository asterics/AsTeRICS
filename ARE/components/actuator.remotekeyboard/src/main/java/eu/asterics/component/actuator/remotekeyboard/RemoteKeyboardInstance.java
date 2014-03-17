
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

package eu.asterics.component.actuator.remotekeyboard;
import java.util.*;
import java.io.*;
import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;



/**
 *   RemoteKeyboardInstance interfaces whith the HID actuator
 *   USB-dongle to emulate a keyboard on a remote PC via USB HID
 *   A list of keycodes is imported from the file "HID_keycodes.txt"
 *   Note: not all keyboard function / keycodes are supported right now.
 *  
 * @author Christoph Veigl [christoph.veigl@technikum-wien.at]
 *         Date: Jan 25, 2011
 *         Time: 06:45:08 PM
 */
public class RemoteKeyboardInstance extends AbstractRuntimeComponentInstance
{

	private CIMPortController port = null;

	private final String ELP_SENDKEYS_NAME 	= "sendKeys";
	private final String ELP_PRESSKEY_NAME 	= "pressKey";
	private final String ELP_HOLDKEY_NAME 	= "holdKey";
	private final String ELP_RELEASEKEY_NAME = "releaseKey";
	
	private static final short HID_ACTUATOR_CIM_ID = 0x0101;
	
	private static final short HID_FEATURE_KEYPRESS = 0x10;
	private static final short HID_FEATURE_KEYHOLD =   0x11;
	private static final short HID_FEATURE_KEYRELEASE = 0x12;

	
	private static final short HID_KEYBOARD_MODIFER_LEFTCTRL =  (1 << 0);
	private static final short HID_KEYBOARD_MODIFER_LEFTSHIFT=  (1 << 1);
	private static final short HID_KEYBOARD_MODIFER_LEFTALT  =  (1 << 2);
	private static final short HID_KEYBOARD_MODIFER_LEFTGUI  =  (1 << 3);
	private static final short HID_KEYBOARD_MODIFER_RIGHTCTRL=  (1 << 4);
	private static final short HID_KEYBOARD_MODIFER_RIGHTSHIFT =(1 << 5);
	private static final short HID_KEYBOARD_MODIFER_RIGHTALT =  (1 << 6);
	private static final short HID_KEYBOARD_MODIFER_RIGHTGUI =  (1 << 7);
	
    private String propKeyCodeString = "";
	private String propUniqueID = "not used"; 

	private Integer hidCode=0;
    private int actSendPos = 0;
    private Hashtable<String, Integer> hidCodeMap;
    private Vector<Integer> hidCodeArray; 

	
    /**
     * The class constructor.
     * loads the HID code map
     */	
    public RemoteKeyboardInstance()
    {
        // constructor
    	// open the keycode-file and create hashtable

		try 
		{
			FileReader fin = new FileReader("data/actuator.hidkeyboard/HID_keycodes.txt");
			LineNumberReader in = new LineNumberReader(fin);
			hidCodeMap = new Hashtable<String, Integer>();
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
				    		code = Integer.parseInt(actCode,16);
					    	hidCodeMap.put(actKey, code);
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
        if("uniqueID".equalsIgnoreCase(propertyName))
        {
            return propUniqueID;
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
            keycodeTranslate();
            return oldValue;
        }
		
		if("uniqueID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUniqueID;
			propUniqueID = (String)newValue;
			CIMPortController tempPort = openCIM (HID_ACTUATOR_CIM_ID, propUniqueID);
			if (tempPort != null)
			{
				port=tempPort;
				if ((!propUniqueID.equals("")) && (!propUniqueID.equals("not used")))
				{
					for (int i=0;i<4;i++)
					{
					  CIMPortManager.getInstance().sendPacket  (port, null, 
							  CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER, 
							  CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
					  try { Thread.sleep (100); }  catch (InterruptedException e) {}
					}
				}
			} 
			return oldValue;
		}
        return null;
    }
    
	public CIMPortController openCIM(short CIMID, String uniqueID)
	{
	   if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID==""))
	   {
		    return (CIMPortManager.getInstance().getConnection(HID_ACTUATOR_CIM_ID));
	   }
	   else
	   {
			Long id;
			try {
				id=Long.parseLong(propUniqueID);
				return (CIMPortManager.getInstance().getConnection(HID_ACTUATOR_CIM_ID, id));
			} catch (Exception e) {
				return(null);
			}	   
	   }
	}
 

	/**
	 * Returns all the filenames inside the path folder data/music
	 * and data/sounds
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("uniqueID")==0)
		{
			res.add("not used");
			Vector<Long> ids;
			ids=CIMPortManager.getInstance().getUniqueIdentifiersofCIMs(HID_ACTUATOR_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					res.add(l.toString());
					// System.out.println(" found unique ID: "+l.toString());
				}
			}
		}
		return res;
	} 
	
   
   
   
   
   
   
   
   
   
   /**
    * translates the keycode-string (e.g. "{SHIFT}A") into a HID Keycode.
    * the keycode string is expected in property variable propKeyCodeString,
    * the HID codes is returned in the vector hidCodeArray 
    */
	public void keycodeTranslate()
	{
		String actToken;
		int index=0;
        char character;
        hidCodeArray = new Vector<Integer>();

		while (index<propKeyCodeString.length())
		{
			try {character=propKeyCodeString.charAt(index); } 
			catch (Exception e) {character=' ';};
			
			if (character=='{')  // process a masked character specified in hidCode-File
			{
				try { 
					actToken = propKeyCodeString.substring (index, propKeyCodeString.indexOf("}",index)+1);
					// Logger.getAnonymousLogger().info("next Token: " +  actToken);
				} catch (Exception e) {actToken="invalid";};

				index+=actToken.length();
				Integer n = hidCodeMap.get(actToken);
				 if (n != null) {
					 // Logger.getAnonymousLogger().info("Token found, value = " +  n);
					 hidCode |= n;
				 }
			}
			else    // process a single non-masked character
			{
				if ((character>='a') && (character<='z'))
					hidCode |= character-'a'+4;
				else if ((character>='A') && (character<='Z'))
					hidCode |= (HID_KEYBOARD_MODIFER_LEFTSHIFT<<8)+(character-'A'+4);
				else if ((character>='0') && (character<='9'))
				{
					if (character=='0') hidCode |= 39;
					else hidCode |= character-'1'+30;
				}
				else if (character==' ')
					hidCode |= 44;
				else if (character=='.')
					hidCode |= 55;
				else if (character==':')
					hidCode |= HID_KEYBOARD_MODIFER_LEFTSHIFT<<8+55;
				else if (character=='-')
					hidCode |= 45;
				else if (character=='_')
					hidCode |= HID_KEYBOARD_MODIFER_LEFTSHIFT<<8+45;
				else if (character=='!')
					hidCode |= HID_KEYBOARD_MODIFER_LEFTSHIFT<<8+30;
				else hidCode |= 44; // unknown characters are intepreted as SPACEBAR
				
				index++;
			}

			if ((hidCode.intValue() & 0xff) != 0)  // more than just modifies -> valid keycode !
			{
				hidCodeArray.add(hidCode);
				hidCode=0;
			}
		}
		actSendPos=0;
	}

   /**
    * stores the value of a HID code with given index 
    * into a 2-byte array for sending to the HID actuator
    */
	public void getHidCode(int index ,byte[] hidCode)
	{
        Integer I = hidCodeArray.get(index);
        hidCode[0]=(byte)(I.intValue()&0xff);
        hidCode[1]=(byte)(I.intValue()>>8);
	}

   /**
    * send all HID codes in the hidcode array at once
    */
	public void sendAllCodes()
	{
		byte [] b = new byte[2];
			
		if (port != null)
		{

	        //TBD: this needs a buffer and an own sending thread...
			for (int i=0;i<hidCodeArray.size();i++)
			{
				//if (System.currentTimeMillis() - last_update > REFRESH_INTERVAL)
				{
					// 	Logger.getAnonymousLogger().fine("sending keycode");
					getHidCode(i,b);
					if (b[1]==0x0f)   // special character for waiting 1 second 
					{
			    		// Logger.getAnonymousLogger().info("Sleeping 1 second");           
						try {Thread.sleep(1000);} catch (Exception e) {};
					}
					else 
						CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_KEYPRESS, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
				}
			}			
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
        	propKeyCodeString = ConversionUtils.stringFromBytes(data);
    		keycodeTranslate();
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
			byte [] b = new byte[2];
			// AstericsErrorHandling.instance.getLogger().fine(String.format("received keypress event, sending: %04x",hidCodeArray.get(actSendPos)));            
			getHidCode(actSendPos,b);
			if (port != null)
			{
				CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_KEYPRESS, 
					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
			
			if (++actSendPos>=hidCodeArray.size()) 
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
			byte [] b = new byte[2];
			// AstericsErrorHandling.instance.getLogger().fine(String.format("received keyhold event, sending: %04x",hidCodeArray.get(actSendPos)));            
			getHidCode(actSendPos,b);
			if (port != null)
			{
				CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_KEYHOLD, 
					      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
			
			//if (++actSendPos>=hidCodeArray.size()) 
			//	actSendPos=0;
   	 }
   }; 

   /**
    * Event Listener Port for releasing current key.
    */
   final IRuntimeEventListenerPort elpReleaseKey 	= new IRuntimeEventListenerPort()
    {
   	 public void receiveEvent(final String data)
   	 {
			byte [] b = new byte[2];
			// AstericsErrorHandling.instance.getLogger().fine("received keyrelease event, sending.");
			getHidCode(actSendPos,b);
			
			if (port != null)
			{
				CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_KEYRELEASE, 
			      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
   	 }
   }; 

       
   /**
    * called when model is started.
    */
   @Override
   public void start()
   {
	   port = openCIM (HID_ACTUATOR_CIM_ID,propUniqueID);
	   if (port != null)
		  AstericsErrorHandling.instance.reportInfo(this, "RemoteKeyboard Instance (ID "+propUniqueID+") started.");
	   else
     		AstericsErrorHandling.instance.reportError(this, "Could not find RemoteKeyboard. Please verify that the HID actuator USB dongle is connected to the remote system and correctly installed on this computer.");
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
	    port = null;
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "RemoteKeyboardInstance stopped");
    }
}