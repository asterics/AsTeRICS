

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.processor.adjustmentcurve;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import eu.asterics.mw.services.AREServices;



/**
 *   Implements the Graphical User Interface for the
 *   AdjustmentCurve processor plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 28, 2013
 *         Time: 10:55:05 AM
 */
public class GUI extends JPanel implements MouseListener, MouseMotionListener
{ 
    //private MyPanel paintPanel = new MyPanel();
    
    private JPanel dotPanel,paintPanel;
    private JButton saveButton;
    
    private Dimension dotPanelSize;
    
	private double currentInputValue = 0;
	private double currentOutputValue = 0;

	private double actInMin;
	private double actInMax;
	private double actOutMin;
	private double actOutMax;
	private double actInPos;
	
	private int mouseX=-1;
	private int mouseY=-1;
	private int activeNode=-1;
	private int cageXMin=-1;
	private int cageXMax=-1;

	private int areaX=0,areaY=0,areaW=0,areaH=0;

	private int txtHeight;
	
	private GUI thisPanel;
	private final AdjustmentCurveInstance owner;
	private DecimalFormat format; // = new DecimalFormat("#.##");

	private String actCaption;
    /**
     * The class constructor, initialises the GUI
     * @param owner    the dotdisplay instance
     */
    public GUI(final AdjustmentCurveInstance owner, final Dimension space)
    {
        super();
    	this.owner=owner;
    	
    	DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
    	dfs.setDecimalSeparator('.');
    	format = new DecimalFormat("#.##",dfs);
    	
		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);
		updateMinMax();
		addMouseListener(this);
		addMouseMotionListener(this);
		
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
		
		int buttonHeight=height/5;
		if (buttonHeight>50) buttonHeight=50;

    	Dimension buttonSize;
   		buttonSize=new Dimension(width,buttonHeight);
   		dotPanelSize=new Dimension(width,height-buttonHeight);

		dotPanel.setMaximumSize(dotPanelSize);
		dotPanel.setPreferredSize(dotPanelSize);
		dotPanel.setVisible(true);
		
	    this.setLayout(new BorderLayout());
        add (dotPanel,BorderLayout.PAGE_START);

		saveButton = new JButton();
		saveButton.setText("SAVE Curve "+owner.propFilename);

		saveButton.setMaximumSize(buttonSize);
		saveButton.setPreferredSize(buttonSize);
		
	    saveButton.addActionListener(new ActionListener() 
	      {
	        public void actionPerformed(ActionEvent e) 
	        {
	          owner.save();
	        } 
	      });

		
		saveButton.setEnabled(true);
		saveButton.setVisible(true);
		
		
        add (saveButton,BorderLayout.PAGE_END);

        
     
        thisPanel=this;
		//The right panel contains some java graphics.
		//It is essential to wait until the right (or East) panel is shown on 
		//the screen before we call the class that generates the graphics.
		//This is because when using graphics the screen location is very often
		//needed!
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
					//rightPanel.removeAll();
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

    		int X_MARGIN = 10;
    		//int Y_MARGIN = (int) (0.05 * h);
    		int Y_MARGIN = 5;
    		
    		int W_OFFSET = 20;
    		// int H_OFFSET = (int) (0.1 * h);
    		int H_OFFSET = (int) (20+owner.propFontSize);
    		
    		areaX =x+X_MARGIN;
    		areaY =y+Y_MARGIN;
    		areaW = w-W_OFFSET;
    		areaH = h-H_OFFSET;
    		    		
    		super.repaint();
    	}

       
        public void paintComponent(Graphics g) {
            super.paintComponent(g);       
            Graphics2D g2d = (Graphics2D) g;
            //  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,                
            //  RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setFont(actFont);
            
            // get metrics from the graphics
            FontMetrics metrics = g2d.getFontMetrics(actFont);
            // get the height of a line of text in this font and render context
            txtHeight = metrics.getHeight();
            // get the advance of my text in this font and render context
            
        	setBackground(colhex("0xf8f8f8"));

            g2d.drawRect(areaX,areaY,areaW,areaH); 
                       
     	    g2d.setColor(Color.BLACK);
            g2d.drawString(owner.propCaption,areaX+areaW-metrics.stringWidth(owner.propCaption),
            		areaY+txtHeight);

     	    g2d.drawString(format.format(owner.propOutMax),areaX+1,areaY+txtHeight);
     	    g2d.drawString(format.format(actOutMin),areaX+1,areaY+areaH-1);

     	    g2d.drawString(format.format(actInMin),areaX+1,areaY+areaH+txtHeight);
     	    g2d.drawString(format.format(actInMax),areaX+areaW-metrics.stringWidth(format.format(actInMax)),
     	    				areaY+areaH+txtHeight);


     	    actInPos=(int)((currentInputValue-actInMin)/(actInMax-actInMin)*areaW);
     	    g2d.setColor(colhex("0x700000"));     	    
            g2d.fillOval((int)(areaX+actInPos-5),areaY+areaH-5, 10,10);            
            
     	    actCaption=format.format(currentInputValue);
     	    g2d.setColor(colhex("0x202020"));     	    
     	    g2d.drawString(actCaption,areaX+(int)actInPos-metrics.stringWidth(actCaption)/2,
     	    		areaY+areaH+txtHeight);

     	    int ax1,ay1,ax2=0,ay2=0,mx=0,my=0;
         	int minDistance=9999*9999;
    		int mouseDistance;
    		int minIndex=0;

     	    g2d.setStroke(new BasicStroke(3));
     	    for (int i=0;i<owner.curvePoints.size();i++)
     	    {
     	    	CurvePoint c = owner.curvePoints.get(i);
     	    	//System.out.println("C"+i+"=("+c.x+"/"+c.y+")");
     	    	ax1=areaX+(int)((c.x-actInMin)/(actInMax-actInMin)*areaW);
     	    	ay1=areaY+(int)(areaH-((c.y-actOutMin)/(actOutMax-actOutMin)*areaH));
     	    	     	    	
     	    	mouseDistance = (mouseX-ax1)*(mouseX-ax1)+(mouseY-ay1)*(mouseY-ay1);
     	    	if (mouseDistance<minDistance)
     	    	{
     	    		minDistance=mouseDistance;
     	    		minIndex=i; mx=ax1; my=ay1;
     	    	}
     	    
         	    g2d.setColor(colhex("0xa0a000"));     	    
                g2d.fillOval(ax1-5,ay1-5, 10,10);
         	    g2d.setColor(colhex("0x2020c0"));     	    
                if (i>0) g2d.drawLine(ax1,ay1,ax2,ay2);   	
                ax2=ax1;ay2=ay1;
     	    }

     	    if (minDistance < 300)
     	    {
     	    	activeNode=minIndex;
         	    g2d.setColor(colhex("0xf08020"));     	    
                g2d.fillOval(mx-8,my-8, 16,16); 
         	    g2d.setColor(colhex("0xa0a000"));     	    
                g2d.fillOval(mx-5,my-5, 10,10);
         	    actCaption=format.format(owner.curvePoints.get(activeNode).x)
         	                 +" / "+format.format(owner.curvePoints.get(activeNode).y);
         	    
         	    g2d.setColor(colhex("0xf0f0ff"));     	    
         	    g2d.drawString(actCaption,mx-30,my-20);
         	    g2d.fillRect(mx-35,my-20-metrics.getHeight(),metrics.stringWidth(actCaption)+10, metrics.getHeight()+5);
         	    g2d.setColor(colhex("0x202050"));     	    
         	    g2d.drawString(actCaption,mx-30,my-20);
                 
                if (activeNode==0)
                	cageXMin=areaX;
                else
                {
         	    	CurvePoint c = owner.curvePoints.get(activeNode-1);
                	cageXMin=areaX+(int)((c.x-actInMin)/(actInMax-actInMin)*areaW);
                }
                if (activeNode==owner.curvePoints.size()-1)
                	cageXMax=areaX+areaW;
                else
                {
         	    	CurvePoint c = owner.curvePoints.get(activeNode+1);
                	cageXMax=areaX+(int)((c.x-actInMin)/(actInMax-actInMin)*areaW);
                }
     	    }
     	    else activeNode=-1;

     	    g2d.setColor(colhex("0xa02000"));     	    
     	    int yDraw=(int)(areaH-(currentOutputValue-actOutMin)/(actOutMax-actOutMin)*areaH);
            g2d.fillOval((int)(areaX+actInPos-5),areaY+yDraw-5, 10,10);            
     	    
        }
    
     }
        
    /*
     * paints the dot graph after a new input value has arrived
     */ 
    void updateGraph(final double x, final double y)
    {
		currentInputValue=x;
		currentOutputValue=y;

		if (owner.propMode == owner.MODE_CLIPMINMAX)
    	{
	    	   if (currentInputValue>actInMax) currentInputValue= actInMax;
	    	   if (currentInputValue<actInMin) currentInputValue= actInMin;
    	}
		else
		{
			if (actInMin>currentInputValue) 
				actInMin=currentInputValue;
			if (actInMax<currentInputValue) 
				actInMax=currentInputValue;
		}
		
    	thisPanel.repaint();    		
    }
    void updateMinMax()
    {
		  CurvePoint c;
	      actInMin=owner.propInMin;
	      actInMax=owner.propInMax;
	      actOutMin=owner.propOutMin;
	      actOutMax=owner.propOutMax;

     	  for (int index=0;index<owner.curvePoints.size();index++)
     	  {
     		  c=owner.curvePoints.get(index);
			  if (actInMin>c.x)  actInMin=c.x;
			  if (actInMax<c.x)  actInMax=c.x;
			  if (actOutMin>c.y)  actOutMin=c.y;
			  if (actOutMax<c.y)  actOutMax=c.y;
     	  }		
     	  thisPanel.repaint();    		
    }
    
    public static Color colhex( String nm ) throws NumberFormatException
    {
      Integer intval = Integer.decode( nm );
      int i = intval.intValue();
      return new Color( (i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF );
    }

    
    
      public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse pressed (# of clicks: " + e.getClickCount() + ")");
      }

      public void mouseReleased(MouseEvent e) {
    	  //System.out.println("Mouse released (# of clicks: " + e.getClickCount() + ")");
      }

      public void mouseEntered(MouseEvent e) {
    	  //System.out.println("Mouse entered");
      }

      public void mouseExited(MouseEvent e) {
    	  //System.out.println("Mouse exited");
      }

      public void mouseClicked(MouseEvent e) {
    	  //    	  System.out.println("Mouse clicked (# of clicks: " + e.getClickCount() + ")");
    	  if (activeNode==-1)
    	  {
    		  if ((e.getX()>areaX) && (e.getX()<areaX+areaW) && (e.getY()>areaY) && (e.getY()<areaY+areaH))
    		  {
    			  CurvePoint c=new CurvePoint();
    			  
    			  c.x=actInMin+((double)(e.getX()-areaX)/(double)areaW)*(actInMax-actInMin);
    			  c.y=actOutMax-((double)(e.getY()-areaY)/(double)areaH)*(actOutMax-actOutMin);

    			  int index=0;
    	     	  while ((index<owner.curvePoints.size()) && (c.x>owner.curvePoints.get(index).x)) 
    	     	  	index++;
    			  
    	     	  System.out.println("new Curve Point "+index+" set at coordiates "+c.x+"/"+c.y); 
    			  owner.curvePoints.add(index,c);
    		  }
    	  }
    	  else
    	  {
        	  if ((e.getButton() == MouseEvent.BUTTON2) || (e.getButton() == MouseEvent.BUTTON3))
        	  {
        		  String caption ="Adjust mapping of Curve Point "+ activeNode+":";
        		  String suggestion = format.format(owner.curvePoints.get(activeNode).x)
        				  			+"/"+format.format(owner.curvePoints.get(activeNode).y);
        		  String answer = JOptionPane.showInputDialog(null,caption,suggestion);
        		  if (answer != null)
        		  {
        			  int separator = answer.indexOf("/");
        			  if ((separator > 0) && (separator < answer.length()))
        			  {
	        			  String xStr=answer.substring(0, separator);
	        			  String yStr=answer.substring(separator+1,answer.length());
	        			  try {
	        				  System.out.println("parsing:"+xStr+" and "+yStr);
	        				  double xVal = Double.parseDouble(xStr);
	        				  double yVal = Double.parseDouble(yStr);
	        				  System.out.println("result:"+xVal+" and "+yVal);
	        				  
	        				  CurvePoint c=owner.curvePoints.get(activeNode);
	        				  boolean valid=true;
	        				  if (activeNode>0)
	        				  {
	        					  if (owner.curvePoints.get(activeNode-1).x>=xVal) valid=false;
	        				  }
	        				  if (activeNode<owner.curvePoints.size()-1)
	        				  {
	        					  if (owner.curvePoints.get(activeNode+1).x<=xVal) valid=false;
	        				  }
	        				  if (valid)
	        				  {
	        						  c.x=xVal;
	        						  c.y=yVal;
	        						  updateMinMax();
	        				  }
	        			  }
	        			  catch (Exception ex) {}
        			  }
        			  
        		  }
        			 
        		  
        	  }
        	  else if ((owner.curvePoints.size()>1) && (e.getButton() == MouseEvent.BUTTON1))
    		  {
    			  System.out.println("Curve Point "+activeNode+" removed."); 
    			  owner.curvePoints.remove(activeNode);
    			  activeNode=-1;
    		  }
    		  
    	  }
      }
    
      public void mouseDragged(MouseEvent e) {
    	  // System.out.println("Mouse draged to:"+e.getX()+"/"+e.getY());
    	  if (activeNode>-1)
    	  {
    		  if ((e.getX()>cageXMin) && (e.getX()<cageXMax) && (e.getY()>areaY) && (e.getY()<(areaY+areaH)))
    		  {
    			  CurvePoint c;
    			  c=owner.curvePoints.get(activeNode);
    			  c.x=actInMin+((double)(e.getX()-areaX)/(double)areaW)*(actInMax-actInMin);
    			  c.y=actOutMax-((double)(e.getY()-areaY)/(double)areaH)*(actOutMax-actOutMin);
    			      			  
    		  }
        	  mouseX=e.getX();mouseY=e.getY();

    	  }
   	  }

   	  public void mouseMoved(MouseEvent e) {
    	  // System.out.println("Mouse moved:"+e.getX()+"/"+e.getY());
    	  mouseX=e.getX();mouseY=e.getY();
   	  }

}