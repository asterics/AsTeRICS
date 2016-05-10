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

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 * ModelMenuItem represents all menu entries which will load a stored model 
 * from the ARE's model storage. Pressing ok on an instance of this class will
 * cause the ARE to stop the current model and deploy and start the new one.
 *  
 * @author Chris Weiss [weissch@technikum-wien.at]
 *         Date: Mar 7, 2011
 *         Time: 10:55:05 AM
 */
public class ModelMenuItem extends ActionMenuItem
{
	/**
	 * Constructs a menu item displaying the name of the model 
	 * @param caption the text to be displayed (usually the name of the model)
	 * @param owner the owning display instance
	 */
	public ModelMenuItem(String caption, PlatformLCDInstance owner)
	{
		super(caption, owner);
	}
	
	/**
	 * Will call the ARE service layer and ask the runtime to load the model
	 * associated with this menu item.
	 */
	public void action() 
	{
		AstericsErrorHandling.instance.getLogger().fine("Loading model: " + caption);
//		AREServices.instance.stopModel();
		AREServices.instance.deployFile(caption);
		try
		{
			AREServices.instance.runModel();
		}
		catch (AREAsapiException e)
		{
			AstericsErrorHandling.instance.getLogger().severe("Could not run model: " + caption);
		}
	}
}

