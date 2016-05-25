package eu.asterics.mw.services;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;


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



public interface IAREServices {
	
	/**
	 * Used for enabling components to set properties of other components at 
	 * runtime
	 * @param componentID
	 * @param key
	 * @param value
	 * @return
	 */
	public String setComponentProperty 
								(String componentID, String key, String value);
	public void deployFile (String filename);
	public void deployAndStartFile (String filename);
	public void registerAREEventListener (IAREEventListener clazz);
	public void unregisterAREEventListener (IAREEventListener clazz);
	public void displayPanel (JPanel panel, 
			IRuntimeComponentInstance componentInstance, boolean display);
	public Dimension getAvailableSpace(IRuntimeComponentInstance componentInstance);
	public Point getComponentPosition (IRuntimeComponentInstance componentInstance);
	
	/**
	 * This service is used by plugin developers interested in auto-adjusting 
	 * the fonts of their gui components depending on the available space on the
	 * ARE desktop for their plugins. 
	 * @param panel all the internal elements of these JPanels will have 
	 * their fonts auto-adjusted
	 * @param maxFontSize in cases where there is too much space the font size 
	 * will be restricted to this upper bound 
	 * @param minFontSize in case there is too little space the text might not 
	 * be readable, the font size can be restricted to this minimum.
	 * 
	 * @param offset The font size in relation to the dimensions of the
	 * <code>panel</code> and the size of the text. The offset is used in case
	 * you do not want to occupy the whole <code>panel</code> width.  
	 */
	public void adjustFonts(JPanel panel, int maxFontSize, int minFontSize,	int offset); 

	
}
