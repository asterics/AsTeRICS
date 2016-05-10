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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */
package eu.asterics.component.processor.oska;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * OskaCommandManager handles all the commands that can be received from the 
 * OSKA. It evaluates the incoming string, extracts the command keywords and 
 * action strings and handles these commands. 
 * @author Christoph Weiss [weissch@technikum-wien.at]
*/
class OskaCommandManager {
	
	Hashtable<String, IOskaCommandHandler> commands 
		= new Hashtable<String,IOskaCommandHandler>();

	Hashtable<String, IOskaActionStringHandler> actionStrings 
		= new Hashtable<String,IOskaActionStringHandler>();
	
	IOskaActionStringHandler defaultHandler = new IOskaActionStringHandler()
	{

		@Override
		public void handleActionString(String action) {
			OskaInstance.instance.outputs.opAction.sendData(action.getBytes());
		}
	};

	
	/**
	 * Adds a handler for a certain command to the list of known commands  
	 * @param cmd the command name
	 * @param handler the handler implementation for said command
	 */
	void addCommand(String cmd, IOskaCommandHandler handler)
	{
		commands.put(cmd, handler);
	}
	
	/**
	 * Adds a handler for a certain action string to the list of 
	 * known action strings  
	 * @param cmd the command name
	 * @param handler the handler implementation for said command
	 */
	void addActionString(String action, IOskaActionStringHandler handler)
	{
		actionStrings.put(action, handler);
	}	
	
	/**
	 * Removes all commands from the list of known commands
	 */
	void clearHandlers()
	{
		commands.clear();
		actionStrings.clear();
	}
	
	/**
	 * Extracts all the action strings enclosed by < and > from a string.
	 * @param input the string received from the OSKA
	 * @return the action string if existent, null otherwise
	 */
	private List<String> extractActionStrings(String input)
	{
		int ltIndex = 0;
		int gtIndex = 0;
		ArrayList<String> stringList = new ArrayList<String>();
		ltIndex = input.indexOf('<');
		gtIndex = input.indexOf('>');
		while ( (ltIndex != -1) &&  (gtIndex != -1) )
		{
			if (gtIndex > ltIndex + 1)  
			{
				String action = input.substring(ltIndex + 1, gtIndex);
				AstericsErrorHandling.instance.reportDebugInfo(OskaInstance.instance, 
						String.format("Adding action string: %s", action));
				stringList.add(action);
			}
			ltIndex = gtIndex + 1;
			ltIndex = input.indexOf('<', ltIndex);
			gtIndex = input.indexOf('>', ltIndex);
		}
		
		return stringList;
	}
	
	/**
	 * Receives incoming strings from the OSKA and dispatches them to the 
	 * corresponding handler
	 * @param input the string received from OSKA
	 * @return true if handler found for extracted command, false if default
	 * handler was applied
	 */
	boolean handleCommand(String input)
	{
		String [] inputs = input.split("\1", -1);
		
		if ((inputs != null) && (inputs.length >= 1))
		{
			String command = inputs[0].substring(0, inputs[0].indexOf(':'));
			
	
			IOskaCommandHandler handler = null;
			handler = commands.get(command);
			if (handler != null)
			{
				handler.handleCommand(inputs); 
				return true;
			}
			else
			{
				handleDefaultCommand(inputs);
			}
		}
		return false;
	}

	/**
	 * Handles input from OSKA if no command handler was found. Will check for
	 * @ character at start of string and if existent forwards the string to the
	 * action output and ignore it otherwise.
	 * @param input the input from  OSKA or the action string if it could be 
	 * extracted
	 */
	private void handleDefaultCommand(String [] input) 
	{
		if (input[0].trim().startsWith("COMMAND:"))
		{
			// action string attached?
			if (input.length > 3)
			{
				List<String> actions = extractActionStrings(input[3]);
				for (String actionString : actions)
				{
					int colonIdx = actionString.indexOf(':');
					IOskaActionStringHandler handler = 
						actionStrings.get((colonIdx != -1) ? 
								actionString.substring(0, colonIdx): actionString);
					((handler != null) ? handler : defaultHandler)
						.handleActionString(actionString);
					
					 try { Thread.sleep(1); } catch (InterruptedException e) { }				
				}
			}
		}
		else
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					String.format("Unknown command %s, skipping", input[0]));
		}
	}
}
