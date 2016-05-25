


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

package eu.asterics.component.actuator.dotmeter;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.services.AREServices;

import java.awt.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;



/**
 *   Implements the Graphical User Interface for the
 *   Dotmeter actuator plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Oct 7, 2012
 *         Time: 10:55:05 AM
 */
public class GUI extends JPanel
{
    //private MyPanel paintPanel = new MyPanel();
    
    private JPanel dotPanel,paintPanel; 
    private Dimension dotPanelSize;

    
	private double drawXValue = 0;
	private double drawYValue = 0;

	private double actXMin;
	private double actXMax;
	private double actYMin;
	private double actYMax;

	private double drawXFactor=1;
	private double drawYFactor=1;
	
	private GUI thisPanel;
	private final DotmeterInstance owner;
	private DecimalFormat format = new DecimalFormat("#.##");

	private String actCaption;
    /**
     * The class constructor, initialises the GUI
     * @param owner    the dotdisplay instance
     */
    public GUI(final DotmeterInstance owner, final Dimension space)
    {
        super();
    	this.owner=owner;
    	actXMin=owner.propXMin;
    	actXMax=owner.propXMax;
    	actYMin=owner.propYMin;
    	actYMax=owner.propYMax;

		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);
  	
    }

    
	/**
	 * The GUI consists of one panel with a drawing area for the dot graph.
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
		//Create Panels
		dotPanel = new JPanel ();
		dotPanelSize = new Dimension (width, height);

		dotPanel.setMaximumSize(dotPanelSize);
		dotPanel.setPreferredSize(dotPanelSize);
		
	//	title = new JLabel("DotMeter");
	//	dotPanel.add(title);
		dotPanel.setVisible(true);
		

		//this.setBorder(new TitledBorder(owner.propCaption));
		
	    this.setLayout(new BorderLayout());
        add (dotPanel,BorderLayout.PAGE_START);
	    
//	    add(dotCaption, BorderLayout.SOUTH);
     
        thisPanel=this;
		dotPanel.addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) 
			{
				if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) !=0 
						&& dotPanel.isShowing()) 
				{
					
					paintPanel = new PanelWithDotGraph();
					paintPanel.setMaximumSize(dotPanelSize);
					paintPanel.setPreferredSize(dotPanelSize);
					dotPanel.add(paintPanel);	
					paintPanel.repaint(dotPanel.getX(), 
							dotPanel.getY(), 
							dotPanelSize.width, dotPanelSize.height);
					dotPanel.repaint();
					dotPanel.revalidate();
					thisPanel.revalidate();
				}
			}
		});
              
 
	}
    /**
     * The drawing Panel
     */
     class PanelWithDotGraph extends JPanel {
    	// private int X_MARGIN, Y_MARGIN, W_OFFSET, H_OFFSET;
    	private int x, y, w, h;
    	private int actYPos;
    	private int actXPos;
    	Font actFont;
    	
    	
    		
        public PanelWithDotGraph() {
        	setLayout (new FlowLayout(FlowLayout.LEFT));
           // setBorder(BorderFactory.createLineBorder(Color.black));

        	int fontSize = owner.propFontSize; //(w-x)/CHARS;
            actFont=new Font ("Arial", 0, fontSize);
        }

        //Each time the position changes we reset the coordinates so that our 
    	//graphics are always painted inside the parent container.
    	public void repaint (int x, int y, int w, int h)
    	{
    		//X_MARGIN = (int) (0.05 * w);
    		//Y_MARGIN = (int) (0.1 * w);
    		//W_OFFSET = (int) (0.05 * w);
    		//H_OFFSET = (int) (0.35 * h);
    		
    		this.x =x;//+X_MARGIN;
    		this.y =y;//+Y_MARGIN;
    		this.w = w-1;//-W_OFFSET;
    		this.h = h-6;//-H_OFFSET;
    		    		
    		super.repaint();
    	}

       
        public void paintComponent(Graphics g) {
            super.paintComponent(g);       
            Graphics2D g2d = (Graphics2D) g;
            //  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,                
            //  RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setFont(actFont);
            
            // get metrics from the graphics
            FontMetrics metrics = g.getFontMetrics(actFont);
            // get the height of a line of text in this font and render context
            int txtHeight = metrics.getHeight();
            // get the advance of my text in this font and render context
            
            
        	drawYFactor=1/(actYMax-actYMin)*h;
        	actYPos=(int)((drawYValue-actYMin)*drawYFactor);

        	drawXFactor=1/(actXMax-actXMin)*w;
        	actXPos=(int)((drawXValue-actXMin)*drawXFactor);

        	setBackground(getColorProperty(owner.propBackgroundColor));

            g.drawRect(x,y,w,h); 
                       
     	    g.setColor(getColorProperty(owner.propGridColor));
     	    if (owner.propCenterLine==true)
     	    {
	            g.drawLine(x+actXPos,y+actYPos,x+w/2,y+h/2);   	    
     	    }

     	    if (owner.propDisplayCaptions==true)
     	    {
	            g.drawString(owner.propCaption,x+w-metrics.stringWidth(owner.propCaption),
	            		y+txtHeight);
	            
	     	    g.drawString("("+format.format(actXMin)+"/"+format.format(actYMin)+")",x+1,y+txtHeight);
	     	    actCaption="("+format.format(actXMax)+"/"+format.format(actYMax)+")";
	     	    g.drawString(actCaption,x+w-metrics.stringWidth(actCaption),y+h-2);
	
	            g.drawString("("+format.format(drawYValue)+")",x+1,y+h-2);
     	    }
            
            if (owner.propDisplayDot==true)
            {
	     	    g.setColor(getColorProperty(owner.propDotColor));
	            g.fillOval((int)(x+actXPos-owner.propDotSize/2),
	            		   (int)(y+actYPos-owner.propDotSize/2),
	            		   owner.propDotSize,owner.propDotSize);   	    
            }
            else
	            g.drawOval((int)(x+actXPos-owner.propDotSize/2),
	            		   (int)(y+actYPos-owner.propDotSize/2),
	            		   owner.propDotSize,owner.propDotSize);   	    

                     	    
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

   
    /**
     * paints the dot graph after a new input value has arrived
     */ 
    void updateInput(final double xValue,final double yValue)
    {
		drawXValue=xValue;
		drawYValue=yValue;
    	if (owner.propMode == owner.MODE_CLIPMINMAX)
    	{
    	   if (drawXValue>actXMax) drawXValue= actXMax;
    	   if (drawXValue<actXMin) drawXValue= actXMin;
    	   if (drawYValue>actYMax) drawYValue= actYMax;
    	   if (drawYValue<actYMin) drawYValue= actYMin;
    	}
    	else if (owner.propMode == owner.MODE_AUTOMINMAX)
    	{
    		if (actXMin>drawXValue) 
    			actXMin=drawXValue;
    		if (actXMax<drawXValue) 
    			actXMax=drawXValue;
    		if (actYMin>drawYValue) 
    			actYMin=drawYValue;
    		if (actYMax<drawYValue) 
    			actYMax=drawYValue;
    	}    		
    	thisPanel.repaint();    		
    }

    /**
     *  repaints the panel and resets min and max value
     */
    void clearClick()
    {
       actXMin=owner.propXMin;
       actXMax=owner.propXMax;
       actYMin=owner.propYMin;
       actYMax=owner.propYMax;
       thisPanel.repaint();    		
    }

}

