
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

package eu.asterics.component.sensor.cellboard;


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsThreadPool;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 *   Implements the cell panel for the
 *   cell board plugin
 *  
 *  @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jan 17, 2012
 *         Time: 12:31:41 AM
 */

public class GUICell extends JPanel implements Runnable
{
	private GUI owner;
	final IRuntimeEventTriggererPort etpClicked;
	final IRuntimeOutputPort opSelectedCell;
	final IRuntimeOutputPort opSelectedCellText;
	
	final IRuntimeEventTriggererPort etpGeralEvent;
	
	private boolean blockSendEvent=true;
	private int index=-1;
	private float fontSize=-1;
	
	BufferedImage  image=null;
	BufferedImage scalledImage=null;
	
	int scaledImageWidth=-1;
	int scaledImageHeight=-1;
	
	String text="";
	String actionText="";
	
	private final float fontSizeMax=150;
	private final float fontIncrementStep=0.5f;
	
	private final int frameWidth=1;
	private final int scanFrameWidth=4;
	
	boolean scanActive=false;
	
	 /**
     * The class constructor.
     * @param owner    the GUI instance
     * @param eventPort the event port
     * @param cellNumberPort the cell number output port
     * @param cellTextOutputPort the cell text output port
     */
	public GUICell(GUI owner,IRuntimeEventTriggererPort eventPort,IRuntimeOutputPort cellNumberPort,IRuntimeOutputPort cellTextOutputPort,IRuntimeEventTriggererPort generalEvent)
	{
		super();
		this.owner=owner;
		this.etpClicked=eventPort;
		opSelectedCell=cellNumberPort;
		opSelectedCellText=cellTextOutputPort;
		etpGeralEvent=generalEvent;
		
		blockSendEvent=true;
		hoverFinish=false;
		
		addMouseListener(new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	            
	        	
	        	if(blockSendEvent==false)
	            {
	            	etpClicked.raiseEvent();
	            	opSelectedCell.sendData(ConversionUtils.intToBytes(index+1));
	            	opSelectedCellText.sendData(ConversionUtils.stringToBytes(actionText));
	            	 AstericsThreadPool.instance.execute(selectFeedback);
	            }
	        	
	        	sendGeneralEvent();
	          }
	          public void mouseEntered(MouseEvent e){
	        	  if(hoverSelection){
	        		  hoverExit=false;
	        		  cellhovering=true;
	        		  hoverSelected=false;
	        		  repaintNow();
	        		  AstericsThreadPool.instance.execute(hoverTimer);
	        	  }else{
	        		  if(!blockSendEvent)
	        		  {
	        			  cellhovering=true;
	        			  repaintNow();
	        		  }
	        	  }
	        	  //
	          }
	          public void mouseExited(MouseEvent e){
	        	  hoverExit=true;
	        	  cellhovering=false;
	        	  if(!blockSendEvent)
        		  {
        			  repaintNow();
        		  }
	          }
	        }); 
	}
	
	public void repaintNow()
	{
		if(scanActive)
		{
			Color scanBorderColor=new Color(255-getColorProperty(owner.getScanColor()).getRed(),255-getColorProperty(owner.getScanColor()).getGreen(),255-getColorProperty(owner.getScanColor()).getBlue());
			setBorder(BorderFactory.createLineBorder(scanBorderColor,scanFrameWidth));
		}
		else
		{
			if(cellhovering)
			{
				Color scanBorderColor=new Color(getColorProperty(owner.getScanColor()).getRed(),getColorProperty(owner.getScanColor()).getGreen(),getColorProperty(owner.getScanColor()).getBlue());
				setBorder(BorderFactory.createLineBorder(scanBorderColor,scanFrameWidth));
			}
			else
			{
				setBorder(BorderFactory.createLineBorder(getColorProperty(owner.getTextColor()),frameWidth));
			}
		}
		
		this.repaint();
		this.revalidate();
		
	}
	
	/**
     * Paints the picture
     * @param g    Graphics
     */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		
		if(scanActive)
		{
			setBackground(getColorProperty(owner.getScanColor()));
		}
		else
		{
			if(cellhovering)
			{
				
				if(hoverSelected)
				{
					setBackground(getColorProperty(owner.getScanColor()));
				}
				else
				{
					setBackground(getColorProperty(owner.getBackgroundColor()));
				}
			}
			else
			{
				setBackground(getColorProperty(owner.getBackgroundColor()));
			}
		}
		
		
		if(image!=null)
		{
			
			double pictureWidth=image.getWidth();
			double pictureHeight=image.getHeight();
			
			double panelWidth;
			double panelHeight;
			
			if(text.length()>0)
			{
				panelWidth=this.getWidth();
				panelHeight=(2*this.getHeight())/3;
			
			}
			else
			{
				panelWidth=this.getWidth();
				panelHeight=this.getHeight();;
			}
			
			pictureWidth=image.getWidth();
			pictureHeight=image.getHeight();
			
			double ratioWidth=pictureWidth/panelWidth;
			double ratioHeight=pictureHeight/panelHeight;
			double ratio=0;
			
			if(ratioWidth>ratioHeight)
			{
				ratio=ratioWidth;
			}
			else
			{
				ratio=ratioHeight;
			}
			
			double scaledWidth=pictureWidth/ratio;
			double scaledHeight=pictureHeight/ratio;
			double positionX=(panelWidth-scaledWidth)/2;
			double positionY=(panelHeight-scaledHeight)/2;
			
			/*if(((int)scaledWidth!=scaledImageWidth) ||((int)scaledHeight!=scaledImageHeight))
			{
				scaleImage((int)scaledWidth,(int)scaledHeight);
			}*/
			g.drawImage(image, (int)positionX, (int)positionY, (int)scaledWidth, (int)scaledHeight,null);
		}
		
		
		if((text.length()>0)&&(fontSize>0))
		{
			Font font = this.getFont();
			font=font.deriveFont(fontSize);
			FontMetrics fontMetrics =this.getFontMetrics(font);
			Rectangle2D tmpFontSize=fontMetrics.getStringBounds(text, this.getGraphics());
			System.out.println(text + " --> " + fontSize + " pt\n");
			double panelWidth=this.getWidth();
			double panelHeight=this.getHeight();
			
			double dbHeight=tmpFontSize.getHeight();
			double dbWidth=tmpFontSize.getWidth();
			
			double positionX=((double)panelWidth -dbWidth)/2;
			double positionY;
			
			if(image!=null)
			{
				double textHeight=(double)panelHeight/3.0;
				
				positionY=((double)textHeight-dbHeight)/2.0 + (2.0*(double)panelHeight)/3.0;
			}
			else
			{
				positionY=((double)panelHeight-dbHeight)/2.0;
			}
			
			g.setFont(font);
			FontMetrics fm   = g.getFontMetrics(font);
			int ascent=fm.getMaxAscent();
			
			g.setColor(getColorProperty(owner.getTextColor()));
			g.drawString(text, (int)positionX, (int)positionY+ascent);
					
			
		}
		
	}
	
	/**
     * Scale image.
     * @param width the width of the image
     * @param height the height of the image
     */
	void scaleImage(int width, int height)
	{
		scaledImageWidth=width;
		scaledImageHeight=height;
		
		if(scalledImage!=null)
		{
			scalledImage.flush();
		}
		scalledImage=null;
		
		scalledImage=new BufferedImage((int)width, (int)height,BufferedImage.TYPE_INT_ARGB);  
		Graphics2D g = scalledImage.createGraphics(); 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		if (scanActive)
			g.drawImage(image, 0, 0, scaledImageWidth, scaledImageHeight, 0, 0, image.getWidth(), image.getHeight(),Color.BLUE, null);
		else
			g.drawImage(image, 0, 0, scaledImageWidth, scaledImageHeight, 0, 0, image.getWidth(), image.getHeight(),Color.GREEN, null);
        g.dispose();
	}
	
	 /**
     * Sets the picture path.
     * @param path path of the picture
     */
	void setPicturePath(String path)
	{
		if(index<0)
		{
			return;
		}
		
		if(path.length()==0)
		{
			if(image!=null)
			{
				image.flush();
			}
			image=null;
		}
		else
		{
			try
			{
				if(image!=null)
				{
					image.flush();
				}
				image=null;
				File imageFile = new File(path.trim());
				image = ImageIO.read(imageFile);
			}
			catch(Exception ex)
			{
				if(image!=null)
				{
					image.flush();
				}
				image=null;
				AstericsErrorHandling.instance.getLogger().warning("Can not open picture: "+ path + " " + ex.getMessage());
			}
		}
	}
	
	
	/**
     * Sets the cell index.
     * @param index    the cell4 index
     */
	void setIndex(int index)
	{
		this.index=index;
	}
	
	/**
     * Returns the cell index
     * @return    the cell index
     */
	int getIndex()
	{
		return index;
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
     * Sets or removes the scanning frame.
     * @param scanActive if true, the scanning frame is activated.
     */
    public void setScanActive(boolean scanActive)
    {
    	this.scanActive=scanActive;
    }
    

	/**
     * Sets the cell text.
     * @param text text of the cell
     */
    public void setText(String text)
    {
    	this.text=text;
    }
    
    /**
     * Returns the cell text.
     * @return cell text
     */
    public String getText()
    {
    	return text;
    }
    
    /**
     * Sets the cell action text.
     * @param text action text of the cell
     */
    public void setActionText(String text)
    {
    	this.actionText=text;
    }
    
    /**
     * Returns the cell action text.
     * @return cell action text
     */
    public String getActionText()
    {
    	return actionText;
    }
    

	/**
     * Sets the font size.
     * @param fontSize font size
     */
    public void setFontSize(float fontSize)
    {
    	this.fontSize=fontSize;
    }
    

	/**
     * Block the cell actions during scanning
     * @param block if true, cell action are blocked.
     */
    public void setEventBlock(boolean block)
    {
    	blockSendEvent=block;
    }
    
    private int hoverTime=1000;
    private boolean hoverSelection=false;
    
    /**
     * Sets hover time.
     * @param hoverTime hover time.
     */
    public void setHoverTime(int hoverTime)
    {
    	this.hoverTime=hoverTime;
    }
    
    /**
     * Sets hover slection.
     * @param hoverSelection hover selection.
     */
    public void setHoverSelection(boolean hoverSelection)
    {
    	this.hoverSelection=hoverSelection;
    }
    
    /**
     * Returns the maximum size of the font, which can be used to display the text. 
     * @return size of the font
     */
    public float getMaxFont()
    {
    	if(text.length()<1)
    	{
    		return -1;
    	}
    	
    	float fontSize=0;
		boolean finish=false;
		
		double panelWidth=this.getWidth();
		double panelHeight=this.getHeight();
		//System.out.println("Panelwidth is " + panelWidth);
		//System.out.println("Panelheight is " + panelHeight);
		if(image!=null)
		{
			//System.out.println("There is an image!");
			panelHeight=panelHeight/3;
			//System.out.println("New PanelHeight is "+panelHeight);
		}
		
		
		
		do
		{
			fontSize=fontSize+fontIncrementStep;
			Font font = this.getFont();
			font=font.deriveFont(fontSize);
			FontMetrics fontMetrics =this.getFontMetrics(font);
			Rectangle2D tmpFontSize=fontMetrics.getStringBounds(text, this.getGraphics());
			
			double height=tmpFontSize.getHeight();
			double width=tmpFontSize.getWidth();
			
			if((height>=panelHeight)||(width>=panelWidth))
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
			
		}while(!finish);
    	//System.out.println(text + " optimal length is " + fontSize);
    	return fontSize;
    }
    
    /**
     * Returns the maximum size of the font, which can be used to display the text. 
     * @return size of the font
     */
    public float getMaxFont(int width, int height)
    {
    	if(text.length()<1)
    	{
    		return -1;
    	}
    	
    	float fontSize=0;
		boolean finish=false;
		
		double panelWidth=width;
		double panelHeight=height;
		if(image!=null)
		{
			panelHeight=panelHeight/3;
		}
		
		
		
		do
		{
			fontSize=fontSize+fontIncrementStep;
			Font font = this.getFont();
			font=font.deriveFont(fontSize);
			FontMetrics fontMetrics =this.getFontMetrics(font);
			Rectangle2D tmpFontSize=fontMetrics.getStringBounds(text, this.getGraphics());
			
			double theight=tmpFontSize.getHeight();
			double twidth=tmpFontSize.getWidth();
			
			if((theight>=panelHeight)||(twidth>=panelWidth))
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
			
		}while(!finish);
    	return fontSize;
    }
    
    
    /**
     * Sends deleyed general click event.
     */
    private void sendGeneralEvent()
    {
    	AstericsThreadPool.instance.execute(this);
    }
    
    /**
     * Prepares to close.
     */
    void close()
    {
    	hoverFinish=true;
    }
    
    /**
     * Thread function.
     */
    @Override
	public void run() {
    	try{
			Thread.sleep(100);
			etpGeralEvent.raiseEvent();
		}catch (InterruptedException e) {}
    	
    }
    
    private int hoverSleep=30;
    private boolean hoverExit=false;
    private boolean hoverFinish=false;
    private boolean cellhovering=false;
    private boolean hoverSelected=false;
    //private boolean hoverStarted=false;
    
    /**
     * Timer user in the hover selection mode.
     */
    private final Runnable hoverTimer = new Runnable(){
		
    	/**
         * Thread function.
         */
		@Override
		public void run() {
			boolean finish=false;
			int currentTime=0;
			do{
				try{
					Thread.sleep(hoverSleep);
				}catch (InterruptedException e) {}
				currentTime=currentTime+hoverSleep;
				if(currentTime>=hoverTime)
				{
					hoverSelected=true;
					etpClicked.raiseEvent();
	            	opSelectedCell.sendData(ConversionUtils.intToBytes(index+1));
	            	opSelectedCellText.sendData(ConversionUtils.stringToBytes(actionText));
	            	sendGeneralEvent();
	            	repaintNow();
	            	try{
						Thread.sleep(200);
					}catch (InterruptedException e) {}
	            	hoverSelected=false;
	            	cellhovering=false;
	            	repaintNow();
					finish=true;
				}
				
				if(hoverExit||hoverFinish)
				{
					finish=true;
					cellhovering=false;
					
					repaintNow();
				}
				
			}while(!finish);
			//cellhovering=false;
			
		}

	};
	
	/**
     * None scan mode feedback.
     */
private final Runnable selectFeedback = new Runnable(){
		
	/**
     * Thread function.
     */	
	@Override
	public void run() {
			hoverSelected=true;
			repaintNow();
        	try{
				Thread.sleep(20);
			}catch (InterruptedException e) {}
        	repaintNow();
        	hoverSelected=false;
		}

	};
    
    
}