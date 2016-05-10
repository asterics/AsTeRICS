
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

package eu.asterics.component.actuator.oscilloscope;
import javax.swing.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;


/**
 *   Implements the GUI for the Oscilloscope plugin
 *  
 * 
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Nov 22, 2010
 *         Time: 12:17:02 PM
 */
public class GUI extends JPanel
{
	private JPanel osciPanel,paintPanel; 
	private Dimension osciPanelSize;
	private GUI thisPanel;

	private final int MAX_SIZE=3000;
	private final int DRAWINGMODE_AUTOUPDATE=0;
	private final int DRAWINGMODE_CROP=1;

	private int paintCount = 0;

	private double[] chnValues = new double [MAX_SIZE];
	private int chnPos = 0;
	private int xSize = 300;
	private double drawFactorX=1;
	private double drawFactorY=1;

	private double chnMin = Double.MAX_VALUE;
	private double chnMax = Double.MIN_VALUE;

	private final OscilloscopeInstance owner;
	private DecimalFormat format = new DecimalFormat("#.##");

	/**
	 * The class constructor, initializes the GUI
	 * @param owner    the Oscilloscope instance
	 */
	public GUI(final OscilloscopeInstance owner,  final Dimension space)
	{
		super();
		this.owner=owner;

		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);

	}


	/**
	 * The GUI consists of one panel with a drawing area for the bar graph.
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
		//Create Panels
		osciPanel = new JPanel ();
		osciPanelSize = new Dimension (width, height);

		osciPanel.setMaximumSize(osciPanelSize);
		osciPanel.setPreferredSize(osciPanelSize);

		//	title = new JLabel("Oscilloscope Display");
		//	osciPanel.add(title);
		osciPanel.setVisible(true);

		//this.setBorder(new TitledBorder(owner.propCaption));

		this.setLayout(new BorderLayout());
		add (osciPanel,BorderLayout.PAGE_START);


		thisPanel=this;

		if (owner.propDrawingMode==DRAWINGMODE_AUTOUPDATE)
		{
			chnMin = Double.MAX_VALUE;
			chnMax = Double.MIN_VALUE;
		}
		else
		{
			chnMin = owner.propMin;
			chnMax = owner.propMax;
		}
		
		//It is essential to wait until the all panels are shown on 
		//the screen before we call the class that generates the graphics.
		//This is because we need the final screen location for painting

		osciPanel.addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) 
			{
				if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) !=0 
						&& osciPanel.isShowing()) 
				{

					paintPanel = new PanelWithOsciGraph();
					paintPanel.setMaximumSize(osciPanelSize);
					paintPanel.setPreferredSize(osciPanelSize);
					//rightPanel.removeAll();
					osciPanel.add(paintPanel);	
					paintPanel.repaint(osciPanel.getX(), 
							osciPanel.getY(), 
							osciPanelSize.width, osciPanelSize.height);
					osciPanel.repaint();
					osciPanel.revalidate();
					thisPanel.revalidate();
				}
			}
		});

	}  


	/**
	 * The drawing panel.
	 */

	class PanelWithOsciGraph extends JPanel {
		// private int X_MARGIN, Y_MARGIN, W_OFFSET, H_OFFSET;
		private int x, y, w, h;
		private int actheight;
		Font actFont;


		public PanelWithOsciGraph() {
			setBorder(BorderFactory.createLineBorder(Color.black));
			setLayout (new FlowLayout(FlowLayout.LEFT));

			int fontSize = owner.propFontSize; 
			actFont=new Font ("Arial", 0, fontSize);
		}

		//Each time the position changes we reset the coordinates so that our 
		//graphics are always painted inside the parent container.
		public void repaint (int x, int y, int w, int h)
		{
			this.x =x;//+(int) (0.05 * w) // x OFFSET;
			this.y =y;//+(int) (0.05 * h) // y OFFSET;
			this.w = w-1;//-(int) (0.05 * w) // x MARGIN;
			this.h = h-6;//-(int) (0.05 * h) // y MARGIN;

			xSize=this.w;

			super.repaint();
		}


		public synchronized void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			int new_y,old_y=0;

			super.paintComponent(g);       
			//  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,                
			//  RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setFont(actFont);          
			// get metrics from the graphics
			FontMetrics metrics = g.getFontMetrics(actFont);
			// get the height of a line of text in this font and render context
			int txtHeight = metrics.getHeight();
			// get the advance of my text in this font and render context
			int txtWidth = metrics.stringWidth(owner.propCaption);

			if (chnMax!=chnMin) drawFactorY=h/(chnMax-chnMin);
			if (xSize>0)    	drawFactorX=w/xSize;

			setBackground(getColorProperty(owner.propBackgroundColor));
			//  g.drawRect(x,y,w,h); 


			// Draw Text
			g.setColor(getColorProperty(owner.propGridColor));
			g.drawString(owner.propCaption,
					x+w-metrics.stringWidth(owner.propCaption),y+txtHeight);

			if (chnMax!=Double.MIN_VALUE)
			{
				g.drawString("max:"+format.format(chnMax),5,txtHeight);
				g.drawString("min:"+format.format(chnMin),5,y+h-2);
			}
			try {
				if (chnPos>0) 
					g.drawString(" "+format.format(chnValues[chnPos-1]),
							x+w-metrics.stringWidth(" "+format.format(chnValues[chnPos-1])),y+h-2);
	
				g.drawRect(x,y,w,h);
				g.drawLine(x, y+h/2,x+w, y+h/2);
	
				g.setColor(getColorProperty(owner.propChannelColor));
				for (int i = 0;i<chnPos;i++)
				{
					new_y=y+h-(int)((chnValues[i]-chnMin)* drawFactorY);
					if (i>0)
					{
						g.drawLine(x+(int)(i*drawFactorX), old_y,
								x+(int)((i+1)*drawFactorX),new_y);
					}
					old_y=new_y; 
				}
			}
			catch (ArrayIndexOutOfBoundsException e) { System.out.println("exception in paint paintComponent, Oscilloscope"); }
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
	 * updates channel values, draws if paint buffer full
	 */
	synchronized void updateChn(double actValue)
	{
		if (owner.propDrawingMode==DRAWINGMODE_AUTOUPDATE)
		{
			if (chnMin>actValue) chnMin=actValue;
			if (chnMax<actValue) chnMax=actValue;
		}
		else
		{
			if (chnMin>actValue) actValue=chnMin;
			if (chnMax<actValue) actValue=chnMax;
		}
		
		chnValues[chnPos]=actValue;
		chnPos++;
		if (chnPos >= xSize) {
			chnPos=0;
		}

		if (++paintCount > owner.propDisplayBuffer) 
		{
			paintCount=0;
			if (paintPanel!=null ) {
				paintPanel.setBackground(getColorProperty(owner.propBackgroundColor));
				paintPanel.repaint();    		
			}
		}
	}


	/**
	 * clears the drawing area and sets drawing position to zero
	 */
	synchronized void clearClick()
	{
		chnPos=0;
		chnMin = Double.MAX_VALUE;
		chnMax = Double.MIN_VALUE;
		paintCount=0;
		paintPanel.repaint();    		

	}

}