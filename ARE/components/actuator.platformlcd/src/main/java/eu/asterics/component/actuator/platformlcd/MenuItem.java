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

package eu.asterics.component.actuator.platformlcd;



/**
 * A basic menu item on the display of the core CIM
 * 
 * @author Chris Weiss [weissch@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:55:05 AM
 */
public class MenuItem
{
	MenuItem left;
	MenuItem right;
	MenuItem up;
	MenuItem down;
	MenuItem ok;
	String caption;
	
	PlatformLCDInstance owner; 
	
	/**
	 * Base constructor for a menu item
	 * @param c the text to 
	 * @param owner
	 */
	public MenuItem(String c, PlatformLCDInstance owner)
	{
		this.owner = owner;
		caption = c;
		left = null;
		right = null;
		up = null;
		down = null;
	}
	
	/**
	 * Sets the item left of this item in the menu tree
	 * @param item the item to be placed
	 */
	public void setLeft(MenuItem item)
	{
		left = item;
	}

	/**
	 * Sets the item right of this item in the menu tree
	 * @param item the item to be placed
	 */
	public void setRight(MenuItem item)
	{
		right = item;
	}

	/**
	 * Sets the item up of this item in the menu tree
	 * @param item the item to be placed
	 */
	public void setUp(MenuItem item)
	{
		up = item;
	}
	
	/**
	 * Sets the item down of this item in the menu tree
	 * @param item the item to be placed
	 */
	public void setDown(MenuItem item)
	{
		down = item;
	}
	
	/**
	 * Sets the item on the ok action of this item in the menu tree
	 * @param item the item to be placed
	 */
	public void setOk(MenuItem item)
	{
		ok = item;
	}

	/**
	 * Writes the stored caption of the menu item to the display
	 */
	public void display()
	{
		owner.writeToDisplay(caption, false);
	}
}


