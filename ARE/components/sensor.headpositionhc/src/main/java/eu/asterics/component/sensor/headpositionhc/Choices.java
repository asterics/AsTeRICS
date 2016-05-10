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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.headpositionhc;

import static org.bytedeco.javacpp.opencv_core.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class Choices{

	// choice info
	private String name;
	private int x, y, fieldWidth, fieldHeight;
	private Boolean selected = false;
	private int numberSelected =0;
	private HeadPositionHCInstance owner;
	private final int threshold;
	
	// for drawing the choice name
	private Font msgFont;
	private FontMetrics fm = null;
	private int xNamePos, yNamePos;
	
	public void setSelected(Boolean selected) {
		if(selected && numberSelected>=threshold)
		{
			numberSelected = 0;
			//owner.counter = owner.propCounterResetingROI;
			if (this.selected != true)
				owner.etpSelect.raiseEvent();
			
			this.selected = true;
			return;
		}
		else if(selected && numberSelected<threshold)
		{
			numberSelected++;
			return;
		}
		
		this.selected = false;
		numberSelected=0;			
	}  
	
	public Choices(String choicename, int positionX, int positionY, int width, int height, int threshold, HeadPositionHCInstance owner)
	{
		name = choicename;
		fieldWidth = width;
		fieldHeight = height;
		x = positionX;
		y = positionY;
		msgFont = new Font("SansSerif", Font.BOLD, 18);
		this.owner = owner;
		this.threshold = threshold;
	}

	public void draw(Graphics g, IplImage snapImage)
	  {
	    if (fm == null) {
	    	
	      fm = g.getFontMetrics(msgFont);
	      xNamePos = x + (fieldWidth/2) - fm.stringWidth(name)/2;
	      yNamePos = y + (fieldHeight/2)  + fm.getAscent() - (fm.getAscent() + fm.getDescent())/2;
	    }

	    CvScalar color;
	    if(selected)
	    {
	    	color = CvScalar.RED;
	    }
	    else
	    {
	    	color = CvScalar.WHITE;
	    }
	    
	    IplImage helpImage = snapImage.clone();
	    
	    cvRectangle(helpImage, cvPoint(x, y), cvPoint(x+fieldWidth, y+fieldHeight), color, CV_FILLED, CV_AA, 0);
	    cvAddWeighted(helpImage,0.3, snapImage, 0.7, 0.0, snapImage);

	    CvFont font = new CvFont();
	    cvInitFont(font, CV_FONT_HERSHEY_SIMPLEX, 0.7, 0.7);
	    cvPutText(snapImage,name, cvPoint(xNamePos, yNamePos), font, CvScalar.GRAY);
	  }
}

