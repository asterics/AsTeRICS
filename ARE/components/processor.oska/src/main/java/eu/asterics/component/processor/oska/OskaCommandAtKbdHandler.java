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

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * OskaCommandAtKbdHandler handles the @KBD commands that can be transferred by
 * the OSKA. Retrieves the keycode and sends it to the key codes output port of
 * the OSKA plug-in.
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *
 */
class OskaCommandAtKbdHandler implements IOskaActionStringHandler 
{
	/**
	 * Handles the @KBD command from OSKA. Extracts the keycode and forwards it 
	 * to the output port
	 * @param arguments the arguments of the command
	 * @return true if the command could be handled, false otherwise
	 */
	@Override
	public void handleActionString(String arguments) {
		
		String output = arguments.substring("@KBD:".length()); 
		
		AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
			String.format("Sending keycode string to " +
					" keycodes output: %s", output));
		
		OskaInstance.instance.outputs.opKeycodes.sendData(output.getBytes());
	}

}
