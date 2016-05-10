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

package eu.asterics.mw.are;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;


public class AREProperties extends Properties 
{
	public static AREProperties instance = new AREProperties();
	static final String PROPERTY_FILENAME = "areProperties";
	private static Logger logger = null;
	
	private AREProperties()
	{
		try 
		{
			logger = AstericsErrorHandling.instance.getLogger();
			FileInputStream in;
			in = new FileInputStream(PROPERTY_FILENAME);
			load(in);
			in.close();

		} 
		catch (IOException e) 
		{
			//Try to create the file if does not exist
			File file = new File(PROPERTY_FILENAME);
			boolean success;
			try {
				success = file.createNewFile();
				if (success) 
				{
					logger.warning(this.getClass().getName()+".AREProperties(): " +
						"Properties file was missing, it has been created.");
				} 
			} catch (IOException ioe) {
				logger.severe(this.getClass().getName()+"." +
						"AREProperties(): Options file was missing and " +
						"couldn't be created.");
			}
		}
	}
	
	public void storeProperties()
	{
		try
		{
			FileOutputStream out = new FileOutputStream(PROPERTY_FILENAME);
			store(out, "ARE Properties");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkProperty(String key, String expectedValue)
	{
		if (containsKey(key))
		{
			return getProperty(key).equals(expectedValue);
		}
		return false;
	}
}
