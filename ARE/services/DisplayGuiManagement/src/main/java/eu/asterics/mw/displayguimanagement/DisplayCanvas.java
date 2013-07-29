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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.displayguimanagement.IDisplayItem.NavigationDirection;



/**
 * A canvas can be drawn, pressed and react to navigation input
 * @author weissch
 *
 */
public class DisplayCanvas implements IDisplayItem
{
	String canvasName;
	
	Rectangle relPosition = new Rectangle(); // position in parent canvas
	Rectangle absPosition = new Rectangle(); // position on display
	
	boolean positionSet = false;
	boolean dimensionSet = false;
	
	DisplayCanvas parent = null;
	List<DisplayCanvas> children = new ArrayList<DisplayCanvas>();
	
	private boolean visible = false;
	
	public void setInvisible() {
		this.visible = false;
		for (DisplayCanvas c : children)
		{
			c.setInvisible();
		}
	}
	
	public void setVisible()
	{
		this.visible = true;
	}

	public DisplayCanvas()
	{
	}
	
	public DisplayCanvas(int x, int y, int w, int h)
	{
		this();
		relPosition.setBounds(x, y, w, h);
		absPosition.setSize(w, h);
		updateAbsolutePosition();
	}
	
	public void addChild(DisplayCanvas canvas)
	{
		DisplayGuiManager.debugMessage("DisplayCanvas.addChild(), " + canvasName + ", adding:" + canvas.canvasName);
		canvas.parent = this;
		canvas.updateAbsolutePosition();
		children.add(canvas);
	}
	
	void updateAbsolutePosition()
	{
		absPosition.setLocation(getAbsolutePosition());
		for (DisplayCanvas c : children)
		{
			c.updateAbsolutePosition();
		}
	}
	
	public void draw()
	{
		DisplayGuiManager.debugMessage("DisplayCanvas.draw(), " + canvasName + "at ("+ 
				absPosition.x + "," + absPosition.y + "), children.size=" + children.size());
		
		DisplayGuiManager.instance.writeToDisplay((short) 0x71, locationToBytes());
		setVisible();
		Object [] array = children.toArray();
		for (Object c : array)
		{
			DisplayCanvas dc = (DisplayCanvas) c;
			DisplayGuiManager.debugMessage("Drawing:" + dc.canvasName);
			dc.draw();
		}
	}
	
	public void press(int x, int y)
	{
		DisplayGuiManager.debugMessage("DisplayCanvas.press() on " + canvasName + " children=" + children.size() );
		if (pressOnCanvas(x,y))
		{
			Object [] canvas = children.toArray();
			for (Object c : canvas)
			{
				((DisplayCanvas) c).press(x, y);
			}
		}
	}

	protected boolean pressOnCanvas(int x, int y)
	{
		if (visible)
		{
			return absPosition.contains(new Point(x, y));
		}
		DisplayGuiManager.debugMessage("DisplayCanvas.pressOnCanvas(): " + canvasName + "is not visible");
		return false;
	}

	
	public void navigate(NavigationDirection nav)
	{
		// no navigation in basic canvas
	}
	
	void setPosition(int x, int y)
	{
		relPosition.setLocation(x, y);	
		updateAbsolutePosition();
	}
	
	void setSize(int width, int height)
	{
		relPosition.setSize(width, height);
		updateAbsolutePosition();
	}
	
	void setName(String name)
	{
		this.canvasName = name;
	}
	
	Point getAbsolutePosition()
	{
		DisplayGuiManager.debugMessage("DisplayCanvas.getAbsolutePosition() on " 
				+ canvasName );

		if (parent == null)
			return (new Point(relPosition.x, relPosition.y));
		
		int x = relPosition.x; 
		int y = relPosition.y;
		
		DisplayCanvas c = parent;
		
		while(c != null)
		{
			x += c.relPosition.x;
			y += c.relPosition.y;
			c = c.parent;
		}
		return (new Point(x,y));
	}
	
	byte [] locationToBytes()
	{
		DisplayGuiManager.debugMessage("DisplayCanvas.locationToBytes() on " + canvasName );
//		Point p = getAbsolutePosition();
		
		byte [] data = new byte[8];
		data[0] = (byte) ( absPosition.x & 0xff);
		data[1] = (byte) ((absPosition.x >> 8) & 0xff);
		data[2] = (byte) ( absPosition.y & 0xff);
		data[3] = (byte) ((absPosition.y >> 8) & 0xff);
		data[4] = (byte) ( relPosition.width & 0xff);
		data[5] = (byte) ((relPosition.width >> 8) & 0xff);
		data[6] = (byte) ( relPosition.height & 0xff);
		data[7] = (byte) ((relPosition.height >> 8) & 0xff);

		return data;
	}
}
