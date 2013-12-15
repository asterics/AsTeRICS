
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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.midi;


import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

 

/**
 * GUI.java
 * Purpose of this module:
 * Graphic User Interface for MidiInstance.java
 * 
 * @Author Dominik Koller [dominik.koller@gmx.at]
 */
public class GUI extends JPanel 
{
	private JPanel feedbackPanel; 
    private Dimension  feedbackPanelSize;
    private GUI thisPanel;    
    public int selectedNote;
    private final MidiInstance owner;


    /**
     * The class constructor, initializes the GUI
     * @param owner    the Slider instance
     */
    public GUI(final MidiInstance owner, final Dimension space)
    {
        super();
        this.owner=owner;
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
		feedbackPanel = new JPanel();
	
		//set Panel size
		feedbackPanelSize = new Dimension (width, height);
		feedbackPanel.setMaximumSize(feedbackPanelSize); 
		feedbackPanel.setPreferredSize(feedbackPanelSize);
		
		this.setBorder(new TitledBorder("Midi Output")); 
		       
		//place everything in the main panel
	    this.setLayout(new BorderLayout());
		add(feedbackPanel, BorderLayout.CENTER);
	    		
		this.setVisible(true);
		thisPanel = this;
		
		feedbackPanel.addHierarchyListener(new HierarchyListener() { 

			@Override
			public void hierarchyChanged(HierarchyEvent e) 
			{
				if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) !=0 
						&& feedbackPanel.isShowing()) 
				{
					
					JPanel newPanel = new FeedbackGUI();
					newPanel.setMaximumSize(feedbackPanelSize);
					newPanel.setPreferredSize(feedbackPanelSize);
					feedbackPanel.removeAll();
					feedbackPanel.add(newPanel);	
					newPanel.repaint(feedbackPanel.getX(), feedbackPanel.getY(), feedbackPanelSize.width, feedbackPanelSize.height);
					feedbackPanel.repaint();
					feedbackPanel.revalidate();
					thisPanel.revalidate();
				}
			}
		}); 
	}
	
	public class FeedbackGUI extends JPanel {



	private double ellipseWidth, ellipseHeight;
	private double rectWidth, rectHeight, xRect, yRect;
	private double xEllipse, yEllipse, w1, h1, x1, y1;
	
	public void repaint (int x, int y, int w, int h)
	{
		w1=w;h1=h;x1=x;y1=y;
		super.repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		ellipseWidth = w1 / owner.amountOfNotes;
		ellipseHeight = h1/4;
		xEllipse = x1;
		yEllipse = y1 + ellipseHeight/2;
		rectWidth = w1;
		rectHeight = h1; 
		xRect = x1;
		yRect = yEllipse + ellipseHeight + ellipseHeight/3;
	
		Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,                
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        for(int i = 0; i <= owner.amountOfNotes-1; i++)
        {
            g2d.setPaint(Color.BLACK);
            g2d.fill (new Ellipse2D.Double(xEllipse + ellipseWidth*i, yEllipse, ellipseWidth, ellipseHeight)); 
        }
        
        g2d.setPaint(Color.BLACK);
        g2d.fill(new Rectangle2D.Double(xRect, yRect, rectWidth, rectHeight));
        
        g2d.setPaint(Color.RED);
        g2d.fill(new Rectangle2D.Double(xRect + getXLocation() - rectWidth/200, yRect, rectWidth/100, rectHeight));
        
        g2d.setPaint(Color.GREEN);
        g2d.fill (new Ellipse2D.Double(xEllipse + ellipseWidth*owner.selectedNote, yEllipse, ellipseWidth, ellipseHeight));
	}
	
	public double getXLocation()
	{
		return ((owner.pitchInput-owner.propPitchMin)*(rectWidth/(owner.propPitchMax-owner.propPitchMin)));
	}
	
	
	}
  
}