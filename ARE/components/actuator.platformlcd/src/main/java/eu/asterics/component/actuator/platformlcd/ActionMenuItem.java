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

package eu.asterics.component.actuator.platformlcd;

/**
 * ActionMenuItem is a MenuItem in the core CIM display's menu which can perform
 * some action other than switching to another menu item.
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:55:05 AM
 */
public abstract class ActionMenuItem extends MenuItem {
    /**
     * Constructor for ActionMenuItem
     * 
     * @param caption
     *            the text to be displayed on the display
     * @param owner
     *            the link to the display instance
     */
    public ActionMenuItem(String caption, PlatformLCDInstance owner) {
        super(caption, owner);
    }

    /**
     * The action to be performed by the menu item
     */
    abstract public void action();
}
