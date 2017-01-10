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
 * OskaCommandSizeHandler handles the information SIZE from the OSKA.
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *
 */
class OskaCommandSizeHandler implements IOskaCommandHandler {

    /**
     * Handles the size information by updating the grid size for highlighting
     * 
     * @param arguments
     *            the arguments of the command
     * @return true if the command could be handled, false otherwise
     */
    @Override
    public boolean handleCommand(String[] arguments) {

        if (arguments[0].trim().startsWith("SIZE:")) {
            try {
                int columns = Integer.parseInt(arguments[2].trim());
                int rows = Integer.parseInt(arguments[3].trim());

                OskaInstance.instance.highlighter.setGridDimensions(columns, rows);
                return true;
            } catch (Exception e) {
                AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, "Received malformed size response!");
            }
        }
        return false;
    }
}
