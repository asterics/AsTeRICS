
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

package eu.asterics.component.sensor.textfieldreader;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;

import java.util.StringTokenizer;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the textfieldreader plugin, which compares the text input
 *   for an edit field to up to seven command strings. On a string match,
 *   a corresponding event is raised.
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 2, 2011
 *         Time: 19:35:00 PM
 */
public class TextfieldreaderInstance extends AbstractRuntimeComponentInstance
{
	private final int NUMBER_OF_COMMANDS = 7;
	private final String KEY_PROPERTY_COMMAND = "command";
	private final String KEY_PROPERTY_EVENT = "recognizedCommand";

	private final OutportKeys opKeys = new OutportKeys();
	private final OutportWords opWords = new OutportWords();
	final IRuntimeEventTriggererPort [] etpCommandRecognized = new DefaultRuntimeEventTriggererPort[NUMBER_OF_COMMANDS];    

	private int wordcnt_old=0;
	private String act_string="";
    public boolean propDisplayGUI=true;

	private final GUI gui = new GUI(this);
	private String[] commands = {"one","two","three","four","five","six","seven"};

	/**
	 * The class constructor, creates the event ports
	 */
	public TextfieldreaderInstance() 
	{
		for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			etpCommandRecognized[i] = new DefaultRuntimeEventTriggererPort();
		}
	}

	/**
	 * returns an Input Port.
	 * @param portID   the name of the port
	 * @return         the input port or null if not found
	 */
	public IRuntimeInputPort getInputPort(String portID)
	{
		return null;
	}

	/**
	 * returns an Output Port.
	 * @param portID   the name of the port
	 * @return         the output port or null if not found
	 */	 
	public IRuntimeOutputPort getOutputPort(String portID)
	{
		if("keys".equalsIgnoreCase(portID))
		{
			return opKeys;
		}
		else if("words".equalsIgnoreCase(portID))
		{
			return opWords;
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
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            return propDisplayGUI;
        }
    	else
		for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			String s = KEY_PROPERTY_COMMAND + (i + 1);
			if (s.equalsIgnoreCase(propertyName))
			{
				return (commands[i]);
			}
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
    	else
		for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			String s = KEY_PROPERTY_COMMAND + (i + 1);
			if (s.equalsIgnoreCase(propertyName))
			{
				commands[i] = (String)newValue;
				// Logger.getAnonymousLogger().info(String.format("Setting command %d to %s", i+1, newValue));
			}
		}
		return null;
	}

	/**
	 * returns an Event Triggerer Port 
	 * @param enventPortID   the name of the event trigger port
	 * @return       the event trigger port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
	{
		for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			String s = KEY_PROPERTY_EVENT + (i + 1);
			if (s.equalsIgnoreCase(eventPortID))
			{
				return etpCommandRecognized[i];
			}
		}
		return null;
	}


	/**
	 * performs comparison of types string with command strings
	 * and raises event on positive match.
	 */
	public void processKeyboardInput (char actchar,int keycode)
	{
		String last_word="";

		opKeys.sendData(keycode);

		if (keycode==16) return;  // ignore shift key
		act_string+=actchar;
		//Logger.getAnonymousLogger().info(String.format("received: %c, keybuffer:%s", actchar, act_string)));

		if (actchar == ' ')
		{
			int wordcnt=0;
			StringTokenizer st = new StringTokenizer(act_string);
			while (st.hasMoreTokens()) {
				wordcnt++;
				last_word=st.nextToken();
			}

			if (wordcnt!=wordcnt_old)
			{
				opWords.sendData(last_word);
				wordcnt_old=wordcnt;
				AstericsErrorHandling.instance.reportInfo(this, String.format("word sent: %s", last_word));
			}
		}

		for (int i=0;i<NUMBER_OF_COMMANDS;i++)
		{
			if (act_string.length()>=commands[i].length())
				if (commands[i].equalsIgnoreCase(
						act_string.substring(act_string.length()-commands[i].length())))
				{
					etpCommandRecognized[i].raiseEvent();
					gui.logCommand(commands[i]);
					// Logger.getAnonymousLogger().info(String.format("command: %d found, event raised!", i+1));
				}
		}

		if (act_string.length()>80) 
		{
			act_string=act_string.substring(40,act_string.length());
			//wordcnt_old=0;
		}
	}  


	/**
	 * Output Port for current key.
	 */
	public class OutportKeys extends DefaultRuntimeOutputPort
	{
		public void sendData(int data)
		{
			super.sendData(ConversionUtils.intToByteArray(data));
		}
	}

	/**
	 * Output Port for last typed word.
	 */
	public class OutportWords extends DefaultRuntimeOutputPort
	{
		public void sendData(String data)
		{
			super.sendData(ConversionUtils.stringToBytes(data));
		}
	}    

	/**
	 * called when model is started.
	 */
	@Override
	public void start()
	{
		if (propDisplayGUI) gui.setVisible(true);
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
		gui.setVisible(false);
		super.stop();
	}

}