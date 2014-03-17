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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */
package eu.asterics.component.processor.oska;

import java.util.StringTokenizer;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * OskaCommandKeyboardLoadedHandler handles the KeyboardLoaded command that can 
 * be transferred by the OSKA. Retrieves the size of the new grid and sets the
 * highlighting system.
 * @author Christoph Weiss [weissch@technikum-wien.at]
 */
class OskaCommandKeyboardLoadedHandler implements IOskaCommandHandler {

	/**
	 * Extracts the row and column count of the new grid and uses it to update
	 * the highlighter
	 * @param arguments the arguments of the command
	 * @return true if the command could be handled, false otherwise
	 */
	@Override
	public boolean handleCommand(String [] arguments) 
	{
		if ( (arguments.length < 7) || 
				!arguments[0].trim().startsWith("KEYBOARDLOADED"))
		{
			return false;
		}
		
		try
		{
			int columns = Integer.parseInt(arguments[5].trim());
			
			// this is a workaround for the double "KEYBOARDLOADED" message !
            String dummy =arguments[6].trim();
            if (dummy.indexOf("KEYBOARDLOADED") != -1)
            {
                AstericsErrorHandling.instance.reportInfo(OskaInstance.instance,
                        String.format("Received malformed row: %s", dummy));
               
                dummy=dummy.substring(0,dummy.indexOf("KEYBOARDLOADED"));
                AstericsErrorHandling.instance.reportInfo(OskaInstance.instance,
                        String.format("corrected: %s", dummy));

            }
            int rows     = Integer.parseInt(dummy);
			
			OskaInstance.instance.highlighter.setGridDimensions(columns, rows);

			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					"Handled keyboard loaded command");
		}
		catch (Exception e)
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
				String.format("Received malformed keyboardloaded " +
						"message"));
			return false;
		}

		return true;
	}

}
