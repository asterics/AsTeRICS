
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.textdisplay;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.Rectangle2D;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;

/**
 *   Implements the Graphical User Interface for the
 *   Text Display plugin
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 19, 2011
 *         Time: 12:31:41 AM
 */
public class GUI extends JPanel 
{
    
    private JPanel guiPanel;  
    private Dimension guiPanelSize;
    private JLabel textLabel=null;
    
    //private final double verticalOffset=1;
    private final float fontSizeMax=150;
    private final float fontIncrementStep=0.5f;


	private final TextDisplayInstance owner;
	final IRuntimeEventTriggererPort etpClicked;
	
    /**
     * The class constructor, initialises the GUI
     * @param owner    the Slider instance
     */
    public GUI(final TextDisplayInstance owner, final Dimension space)
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
		
		//guiPanel.setBackground(getColorProperty(owner.getBackgroundColor()));
		
		TextPosition position = owner.getTextPosition();
		
		switch(position)
		{
		case Left:
			textLabel = new JLabel (owner.getDefaultText(),JLabel.LEFT);
			break;
		case Center:
			textLabel = new JLabel (owner.getDefaultText(),JLabel.CENTER);
			break;
		case Right:
			textLabel = new JLabel (owner.getDefaultText(),JLabel.RIGHT);
			break;
		default:
			textLabel = new JLabel (owner.getDefaultText(),JLabel.CENTER);
		}
		
		
		addMouseListener(new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	            //System.out.println("click ");
	        	etpClicked.raiseEvent();
	          } 
	        }); 
		
		Insets panelBorderInsets=guiPanel.getBorder().getBorderInsets(guiPanel);
		
		double labelWidth=width-panelBorderInsets.left-panelBorderInsets.right-1;
		double labelHeight=height-panelBorderInsets.bottom - panelBorderInsets.top-1;
		
		Dimension labelDimension = new Dimension((int)labelWidth,(int)labelHeight);
		textLabel.setPreferredSize(labelDimension);
		textLabel.setMinimumSize(labelDimension);
		textLabel.setMaximumSize(labelDimension);
		
		String testString="THIS IS TEST STRING";
		
		float fontSize=0;
		boolean finish=false;
		
		do
		{
			fontSize=fontSize+fontIncrementStep;
			
			Font font=textLabel.getFont();
			font=font.deriveFont(fontSize);
			FontMetrics fontMetrics = textLabel.getFontMetrics(font);
			Rectangle2D tmpFontSize=fontMetrics.getStringBounds(testString, textLabel.getGraphics());
			
			double fontHeight=tmpFontSize.getHeight();
			//double fontWidth=tmpFontSize.getWidth();
			
			if(fontHeight>=labelHeight)
			{
				finish=true;
				fontSize=fontSize-1;
			}
			else
			{
    	
				if(fontSize>fontSizeMax)
				{
					finish=true;
				}
			}
		}
		while(!finish);
		
		textLabel.setOpaque(true);
		textLabel.setForeground(getColorProperty(owner.getTextColor()));
		textLabel.setBackground(getColorProperty(owner.getBackgroundColor()));
		
		Font font=textLabel.getFont();
		font=font.deriveFont(fontSize);
		textLabel.setFont(font);
		
		guiPanel.add(textLabel,BorderLayout.CENTER);
        
	    this.setLayout(new BorderLayout());
        add (guiPanel,BorderLayout.PAGE_START);
	    
	}
	

	/**
     * Sets the label text
     * @param text new label text
     */
	void setText(String text)
	{
		if(textLabel!=null)
		{
			textLabel.setText(text);
			
		}
	}
	
	/**
     * returns a color for a given color index
     * @param index    the color index
     * @return         the associated color
     */
    Color getColorProperty(int index)
    {
    	switch (index) {
    	case 0: return(Color.BLACK); 
    	case 1: return(Color.BLUE); 
    	case 2: return(Color.CYAN); 
    	case 3: return(Color.DARK_GRAY); 
    	case 4: return(Color.GRAY); 
    	case 5: return(Color.GREEN); 
    	case 6: return(Color.LIGHT_GRAY);
    	case 7: return(Color.MAGENTA); 
    	case 8: return(Color.ORANGE); 
    	case 9: return(Color.PINK); 
    	case 10: return(Color.RED); 
    	case 11: return(Color.WHITE);
    	case 12: return(Color.YELLOW); 
    	default: return(Color.BLUE);
    	}
    }
  
  
   // add state change listeners or action listeners here
   // interact with output port e.g. via
   //  owner.opMyOutPort.sendData(ConversionUtils.intToBytes(source.getValue())
  
}