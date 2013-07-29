
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.imagebox;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 *   Implements the Graphical User Interface for the
 *   ImageBox plugin
 *  
 *  @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jan 17, 2012
 *         Time: 12:31:41 AM
 */
public class GUI extends JPanel 
{
    
    private JPanel guiPanel;  
    private Dimension guiPanelSize;
    // private JLabel myLabel;
    // add more GUI elements here

	private final ImageBoxInstance owner;
	final IRuntimeEventTriggererPort etpClicked;
	
	ImageBoxPanel imageBoxPanel=null;
	
    /**
     * The class constructor, initialises the GUI
     * @param owner    the Slider instance
     */
    public GUI(final ImageBoxInstance owner, final Dimension space)
    {
        super();
    	this.owner=owner;
    	this.etpClicked=owner.etpClicked;
    	
		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);  	
    }

    
	/**
	 * set up the panel and its elements for the given size 
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
		//Create Panels
		guiPanel = new JPanel (new BorderLayout());
		
		guiPanelSize = new Dimension (width, height);

		guiPanel.setMaximumSize(guiPanelSize);
		guiPanel.setPreferredSize(guiPanelSize);
		
		guiPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK),owner.getCaption()));
		
		
		addMouseListener(new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	            //System.out.println("click ");
	        	etpClicked.raiseEvent();
	          } 
	        }); 
		
		
		Insets panelBorderInsets=guiPanel.getBorder().getBorderInsets(guiPanel);
		
		imageBoxPanel=new ImageBoxPanel();
		imageBoxPanel.setOwner(this);
		imageBoxPanel.setPicturePath(owner.getDefaultPicturePath());
		
		guiPanel.setVisible(true);
		
		//this.setBorder(new TitledBorder(owner.propMyTitle));     
		// myLabel = new JLabel (owner.propMyLabelCaption);
		// guiPanel.add(myLabel);
		guiPanel.add(imageBoxPanel,BorderLayout.CENTER);
		
	    this.setLayout(new BorderLayout());
        add (guiPanel,BorderLayout.PAGE_START);
	    
	}
	
	
	 /**
     * Returns the background color.
     * @return   background color
     */
    int getBackgroundColor()
    {
  	  return owner.getBackgroundColor();
    }
	
	
    /**
     * Sets the picture path.
     * @param path path of the picture
     */
	void setPicturePath(String path)
	{
		if(imageBoxPanel!=null)
		{
			imageBoxPanel.setPicturePath(path);
			imageBoxPanel.repaint();
			imageBoxPanel.revalidate();
		}
	}
	
  
   // add state change listeners or action listeners here
   // interact with output port e.g. via
   //  owner.opMyOutPort.sendData(ConversionUtils.intToBytes(source.getValue())
  
}