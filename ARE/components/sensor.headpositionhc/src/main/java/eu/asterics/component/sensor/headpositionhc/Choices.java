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

