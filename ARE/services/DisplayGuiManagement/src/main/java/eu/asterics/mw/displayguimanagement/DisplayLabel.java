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

package eu.asterics.mw.displayguimanagement;

import java.awt.Point;
import java.awt.Rectangle;

import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.services.AstericsThreadPool;

public class DisplayLabel extends DisplayCanvas {

	String caption;
	Rectangle iconLocation = new Rectangle();
	byte [] iconData = null;
	byte [] invertedIconData = null;
	
	boolean drawText = true;
		
	public DisplayLabel(String caption, int x, int y, int w, int h)
	{
		super(x, y, w, h);
		this.caption = caption;
		
	}
	
	public DisplayLabel(String caption)
	{
		this.caption = caption;
	}
	
	public DisplayLabel()
	{
		this.caption = "";
	}
	
	
	@Override
	public void draw() 
	{
		DisplayGuiManager.debugMessage("DisplayLabel.draw():"+ canvasName);
		// set text window
		DisplayGuiManager.instance.writeToDisplay((short) 0x71, locationToBytes());

		setVisible();
		
		if (drawText)
		{
			DisplayGuiManager.debugMessage("DisplayLabel.draw():"+ canvasName + " drawing text");
			// write text
			DisplayGuiManager.instance.writeToDisplay((short) 0x72, locationToBytes());
			DisplayGuiManager.instance.writeToDisplay((short) 0x74, (caption).getBytes());
		}
		
		if (iconData != null)
		{
			DisplayGuiManager.debugMessage("DisplayLabel.draw():"+ canvasName + " drawing icon");
			byte [] data = new byte[8 + iconData.length]; 
			
			System.arraycopy(iconLocationToBytes(), 0, data, 0, 8);
			System.arraycopy(iconData, 0, data, 8, iconData.length);

			/* for (int i = 0; i< data.length; i++)
			{
				DisplayGuiManager.debugMessage(String.format("%2x, ", data[i]));
				if (((i + 1) % 8) == 0) 
				{
					System.out.print("\n"); 
				}
			}
			*/ 
			DisplayGuiManager.instance.writeToDisplay((short) 0x75, data);

		}

	}

	public void drawInverted() 
	{
		DisplayGuiManager.debugMessage("DisplayLabel.drawInverted():"+ canvasName);
		// set text window
		DisplayGuiManager.instance.writeToDisplay((short) 0x71, locationToBytes());

		setVisible();
		
		if (drawText)
		{
			DisplayGuiManager.debugMessage("DisplayLabel.draw():"+ canvasName + " drawing inverted text");
			DisplayGuiManager.instance.writeToDisplay((short) 0x72, locationToBytes());				
			DisplayGuiManager.instance.writeToDisplay((short) 0x74, ("\f\37" + caption + "\36").getBytes());	  
		}

		if (invertedIconData != null)
		{
			DisplayGuiManager.debugMessage("DisplayLabel.draw():"+ canvasName + " drawing inverted icon");

			byte [] data = new byte[8 + invertedIconData.length]; 
			System.arraycopy(iconLocationToBytes(), 0, data, 0, 8);
			System.arraycopy(invertedIconData, 0, data, 8, invertedIconData.length);

			DisplayGuiManager.instance.writeToDisplay((short) 0x75, data);
		}
	}

	
	byte [] iconLocationToBytes()
	{
		
		int x = absPosition.x + iconLocation.x;
		int y = absPosition.y + iconLocation.y;
		DisplayGuiManager.debugMessage(String.format("DisplayCanvas.iconLocationToBytes() on %s, x: %d, y: %d", canvasName, x, y ));		

		byte [] data = new byte[8];
		data[0] = (byte) ( x & 0xff);
		data[1] = (byte) ((x >> 8) & 0xff);
		data[2] = (byte) ( y & 0xff);
		data[3] = (byte) ((y >> 8) & 0xff);
		data[4] = (byte) ( iconLocation.width & 0xff);
		data[5] = (byte) ((iconLocation.width >> 8) & 0xff);
		data[6] = (byte) ( iconLocation.height & 0xff);
		data[7] = (byte) ((iconLocation.height >> 8) & 0xff);

		return data;
	}	
	
	void setIcon(byte [] iconData)
	{
		this.iconData = iconData;
		invertedIconData = new byte[iconData.length]; 
		
		for (int i = 0; i< iconData.length; i++)
		{
			invertedIconData[i]=(byte) (255-iconData[i]);
		}
		
	}
	
	void setIconLocation(int x, int y, int w, int h)
	{
		iconLocation.setBounds(x, y, w, h);
	}
	
	void enableText(boolean drawText)
	{
		this.drawText = drawText;
	}
	
}
