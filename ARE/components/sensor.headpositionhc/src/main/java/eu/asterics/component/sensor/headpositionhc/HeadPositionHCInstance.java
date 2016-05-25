

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

package eu.asterics.component.sensor.headpositionhc;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.logging.Logger;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;

import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.computervision.GrabbedImageListener;
import eu.asterics.mw.computervision.HaarCascadeDetection;
import eu.asterics.mw.computervision.SharedCanvasFrame;
import eu.asterics.mw.computervision.SharedFrameGrabber;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author Cornelia Salomon [cornelia.salomon@technikum-wien.at]
 *         Date: 
 *         Time: 
 */
public class HeadPositionHCInstance extends AbstractRuntimeComponentInstance implements GrabbedImageListener
{
	final IRuntimeOutputPort opCellNumber = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpSelect = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propChoicesEachSide = 3;
	int propAngle1LeftSide = 160;
	int propAngle2LeftSide = 120;
	int propAngle3LeftSide = 100;
	int propAngle4LeftSide = 65;
	int propAngle5LeftSide = -100000;
	int propAngle6LeftSide = 100000;
	int propAngle1RightSide = 20;
	int propAngle2RightSide = 60;
	int propAngle3RightSide = 80;
	int propAngle4RightSide = 110;
	int propAngle5RightSide = 100000;
	int propAngle6RightSide = -100000;
	String propPathForHaarCascade = "./data/service.computervision/";
	int propCameraID = 0;
	int propCounterResettingROI = 10;
	int propCounterToSendSelectEvent = 4;

	// declare member variables here

    /* dimensions of each image; the panel is the same size as the image */
	  private static final int WIDTH = 640;  
	  private static final int HEIGHT = 480;
	  
	  private static final String FACE = "haarcascade_frontalface_alt.xml";

	  private CvRect roiRect = null;
	  private CvRect mouthRect = new CvRect();
	  private CvRect leftearRect = new CvRect();
	  private CvRect rightearRect = new CvRect();
	  private Choices[] choices;
	  private int numChoices;
	  public int counter;
	  private HaarCascadeDetection facedetection;

	private String instanceId;
    
   /**
    * The class constructor.
    */
    public HeadPositionHCInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("cellNumber".equalsIgnoreCase(portID))
		{
			return opCellNumber;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("select".equalsIgnoreCase(eventPortID))
		{
			return etpSelect;
		}

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("choicesEachSide".equalsIgnoreCase(propertyName))
		{
			return propChoicesEachSide;
		}
		if ("angle1LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle1LeftSide;
		}
		if ("angle2LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle2LeftSide;
		}
		if ("angle3LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle3LeftSide;
		}
		if ("angle4LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle4LeftSide;
		}
		if ("angle5LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle5LeftSide;
		}
		if ("angle6LeftSide".equalsIgnoreCase(propertyName))
		{
			return propAngle6LeftSide;
		}
		if ("angle1RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle1RightSide;
		}
		if ("angle2RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle2RightSide;
		}
		if ("angle3RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle3RightSide;
		}
		if ("angle4RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle4RightSide;
		}
		if ("angle5RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle5RightSide;
		}
		if ("angle6RightSide".equalsIgnoreCase(propertyName))
		{
			return propAngle6RightSide;
		}
		if ("pathForHaarCascade".equalsIgnoreCase(propertyName))
		{
			return propPathForHaarCascade;
		}
		if ("cameraID".equalsIgnoreCase(propertyName))
		{
			return propCameraID;
		}
		if ("counterResettingROI".equalsIgnoreCase(propertyName))
		{
			return propCounterResettingROI;
		}
		if ("counterToSendSelectEvent".equalsIgnoreCase(propertyName))
		{
			return propCounterToSendSelectEvent;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("choicesEachSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propChoicesEachSide;
			propChoicesEachSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle1LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle1LeftSide;
			propAngle1LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle2LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle2LeftSide;
			propAngle2LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle3LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle3LeftSide;
			propAngle3LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle4LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle4LeftSide;
			propAngle4LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle5LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle5LeftSide;
			propAngle5LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle6LeftSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle6LeftSide;
			propAngle6LeftSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle1RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle1RightSide;
			propAngle1RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle2RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle2RightSide;
			propAngle2RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle3RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle3RightSide;
			propAngle3RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle4RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle4RightSide;
			propAngle4RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle5RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle5RightSide;
			propAngle5RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("angle6RightSide".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAngle6RightSide;
			propAngle6RightSide = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("pathForHaarCascade".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPathForHaarCascade;
			propPathForHaarCascade = (String)newValue;
			return oldValue;
		}
		if ("cameraID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCameraID;
			propCameraID = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("counterResettingROI".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCounterResettingROI;
			propCounterResettingROI = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("counterToSendSelectEvent".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCounterToSendSelectEvent;
			propCounterToSendSelectEvent = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		  HaarCascadeDetection.setHAAR_DIR(propPathForHaarCascade);
    	  try{
  			FrameGrabber grabber=SharedFrameGrabber.instance.getFrameGrabber(Integer.toString(this.propCameraID), 640,480);
  			//Integer.toString(this.propCameraID)
  			SharedFrameGrabber.instance.registerGrabbedImageListener(Integer.toString(this.propCameraID), this);
  			SharedFrameGrabber.instance.startGrabbing(Integer.toString(this.propCameraID));

			instanceId=DeploymentManager.instance.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(this);
  			Point pos = AREServices.instance.getComponentPosition(this);
  			Dimension d = AREServices.instance.getAvailableSpace(this);

  			SharedCanvasFrame.instance.createCanvasFrame(instanceId, "Face", grabber.getGamma(),pos,d);
    	  }catch (Exception e)
    	  {}
    	  
    	  initialize();

          super.start();
      }
      private void initialize()
      {
    	  numChoices = propChoicesEachSide+1;
    	  choices = new Choices[numChoices*2];
    	  HaarCascadeDetection.setHAAR_DIR(propPathForHaarCascade);
    	  counter= propCounterResettingROI+1; //if no feature is found counter ++ => until counterReset then a new ROI is build
    	  facedetection  = new HaarCascadeDetection();
  	      CreateChoices();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
		  SharedFrameGrabber.instance.stopGrabbing(Integer.toString(this.propCameraID));
      	  SharedFrameGrabber.instance.deregisterGrabbedImageListener(Integer.toString(this.propCameraID), this);
      	  SharedCanvasFrame.instance.disposeFrame(instanceId);

          super.stop();
      }
      /**
	   * Callback called when a new frame was grabbed. 
	  */
	  @Override
	  public void imageGrabbed(IplImage snapIm) {
			try {				
			    counter++;			    
			    if (snapIm == null)
			    	return;
			    
			    //Flip Image horizontally
			    cvFlip(snapIm, snapIm, 1);
			    
			    // convert to grey image
			    IplImage greyImage = convertFrame(snapIm);
			    
			    Graphics g = SharedCanvasFrame.instance.getCanvasFrame(instanceId).getGraphics();
			      
			   
			    //detect frontal face	
			    if(roiRect == null || counter > propCounterResettingROI) //get new ROI as starting position
			    {
			    	CvRect helpRect = new CvRect();
			        helpRect = facedetection.detectFeature(greyImage, "Face", FACE, null, 2);	     
			         
			        if(helpRect != null)
			        {
			        	setROIRect(helpRect, snapIm);//sets the ROI-Rectangle
			        	counter=0;
			        }
			        paintComponent(g, snapIm); // draws the Rectangles and Choices in the current image and displays the image
			        greyImage.release();
			        return;
			      } 
			      	      
		    	double angle = 0;
		    	//detects left ear and  mouth and calculates the angle
		    	angle = facedetection.detectFeatures(greyImage, roiRect, leftearRect, mouthRect,true);
		    	if(angle < 400)
		    	{
		    		counter =0;
		    		detectChoiceRightSide(angle);
		    		paintComponent(g, snapIm);
		    		greyImage.release();
		    		return;	    		
		    	}
		    	
		    	setChoicesfalse(true);
		    	//detects right ear and mouth and calculates the angle
		    	angle = facedetection.detectFeatures(greyImage, roiRect, rightearRect, mouthRect,false);
		    	
		    	if(angle < 400)
		    	{
		    		counter =0;
		    		detectChoiceLeftSide(angle);
		    		greyImage.release();
		    		greyImage = null;
		    		paintComponent(g, snapIm);
		    		return;
		    	}
		    	setChoicesfalse(false);
		    	paintComponent(g, snapIm);
		    	greyImage.release();
		    }
			catch (java.lang.Exception e) {
				e.printStackTrace();
			}
	  }	  
	  
	  private void setChoicesfalse(boolean right) 
	  {
		int start =1;
		if(!right)
			start = 0;
		
		for (int i = start; i<(numChoices*2); i+=2)
		{
			choices[i].setSelected(false);
		}		
	  }
	  
	  private void setAllChoicesfalseButOne(int index) 
	  {
		  if(index >= choices.length)
		  {
			  //System.out.println("out of bounds index "+ index);
			  return;
		  }
		  
	      opCellNumber.sendData(ConversionUtils.intToBytes(index+1));
		  choices[index].setSelected(true);
		  
		  for(int i=0; i <(numChoices*2);i++)
			{
				if(i==index)
					continue;
				
				choices[i].setSelected(false);
			}		
	  }

	  private void detectChoiceLeftSide(double angle)
	  {
    		//System.out.println("detected ear " + angle);			
    			
    		if(angle<propAngle1LeftSide && angle>propAngle2LeftSide)
    		{
    			setAllChoicesfalseButOne(0);
    			return;
    		}
    		if(angle<propAngle2LeftSide && angle>propAngle3LeftSide)
    		{
    			setAllChoicesfalseButOne(2);
    			return;
    		}
    		if(angle<propAngle3LeftSide && angle>propAngle4LeftSide)
    		{
    			setAllChoicesfalseButOne(4);
    			return;
    		}
    		if(angle<propAngle4LeftSide && angle>propAngle5LeftSide)
    		{
    			setAllChoicesfalseButOne(6);
    			return;
    		}
    		if(angle<propAngle5LeftSide && angle>propAngle6LeftSide)
    		{
    			setAllChoicesfalseButOne(8);
    			return;
    		}
    		if(angle<propAngle6LeftSide)
    		{
    			setAllChoicesfalseButOne(10);
    			return;
    		}
	  }  
	
	  private void detectChoiceRightSide(double angle)
	  {
  		//System.out.println("detected ear " + angle);			
  			
  		if(angle>propAngle1RightSide && angle<propAngle2RightSide)
  		{
  			setAllChoicesfalseButOne(1);
  			return;
  		}
  		if(angle>propAngle2RightSide && angle<propAngle3RightSide)
  		{
  			setAllChoicesfalseButOne(3);
  			return;
  		}
  		if(angle>propAngle3RightSide && angle<propAngle4RightSide)
  		{
  			setAllChoicesfalseButOne(5);
  			return;
  		}
  		if(angle>propAngle4RightSide && angle<propAngle5RightSide)
  		{
  			setAllChoicesfalseButOne(7);
  			return;
  		}
  		if(angle>propAngle5RightSide && angle<propAngle6RightSide)
  		{
  			setAllChoicesfalseButOne(9);
  			return;
  		}
  		if(angle>propAngle6RightSide)
		{
			setAllChoicesfalseButOne(11);
			return;
		}
	  }
	
	  private void setROIRect(CvRect helpRect, IplImage snapIm)
	  {
		    int resizex = 80;
		    int resizey = 80;
	        roiRect = new CvRect();
	        roiRect.x(helpRect.x());
	        roiRect.y(helpRect.y());
	        roiRect.width(helpRect.width());
	        roiRect.height(helpRect.height());
	        
	        //resize roiRectangle
	        int rightcornerup = roiRect.x() + roiRect.width();
	      	int rightcornerdown = roiRect.y() + roiRect.height();
	      	if ((rightcornerup + resizex/2) > snapIm.width())
	      	{
	      		 resizex = ((snapIm.width()-rightcornerup) /2)-1;
	      	}
	      	
	      	if ((rightcornerdown + resizey/2) > snapIm.height())
	      	{
	      		resizey = ((snapIm.height()-rightcornerdown) /2)-1;
	      	}
      	 
	        roiRect = roiRect.height(roiRect.height() + resizey);
	        roiRect = roiRect.width(roiRect.width() + resizex);
	         
	        //reposition roiRectangle
	        roiRect = roiRect.x(roiRect.x()-resizex/2);
	        roiRect = roiRect.y(roiRect.y()-resizey/2);
	        //System.out.println("detected face for ROI");
	  }
	  
	  private void CreateChoices()
	  {
		  
		  int height = HEIGHT/numChoices;
		  int width = WIDTH/3;
		  
		  for(int i=0; i<(numChoices*2); i+=2)
		  {
			  String name = "Auswahl " + (i+1);
			  choices[i]=new Choices(name, 0,height*(i/2),width, height, propCounterToSendSelectEvent, this);
			  name = "Auswahl " + (i+2);
			  choices[i+1]=new Choices(name, width*2,height*(i/2),width, height, propCounterToSendSelectEvent, this);
		  }
	  }
	  
	  private IplImage convertFrame(IplImage img)
	   {
	     // convert to grayscale
	     IplImage grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
	     cvCvtColor(img, grayImg, CV_BGR2GRAY);  

	     cvEqualizeHist(grayImg, grayImg);       // spread out the grayscale range

	     return grayImg;
	  }  
	  
	  public void paintComponent(Graphics g, IplImage snapIm)
	  { 
		  
		  //g.drawImage(snapIm.getBufferedImage(), 0, 0, null);
		  paintROIs(g, snapIm);
		  
		  for(Choices choice:choices)
		  {
			  if(choice != null)
				  choice.draw(g, snapIm);
		  }
		  SharedCanvasFrame.instance.showImage(instanceId, snapIm);
		  //SharedCanvasFrame.instance.getCanvasFrame("CanvasFrame1").repaint();
		  
	  }
	  
	  private void paintROIs(Graphics g, IplImage snapImage)
	  {
		  if(roiRect != null)
	      {
	    	  cvRectangle(snapImage, cvPoint(roiRect.x(), roiRect.y()), cvPoint(roiRect.x()+roiRect.width(), roiRect.y()+roiRect.height()), CvScalar.RED, 1, CV_AA, 0);
	      }
	      if(leftearRect.x() != 0)
	      {
	    	  int x= leftearRect.x()+roiRect.x();
	    	  int y = leftearRect.y()+roiRect.y();	    	  
	    	  cvRectangle(snapImage, cvPoint(x, y), cvPoint(x+leftearRect.width(), y+leftearRect.height()), CvScalar.YELLOW, 1, CV_AA, 0);
	      }
	      if(mouthRect.x() != 0)
	      {
	    	  if(leftearRect.x() != 0)
	    	  {
	    		  int x= mouthRect.x()+roiRect.x()+(roiRect.width()/2);
		    	  int y = mouthRect.y()+roiRect.y();	    	  
		    	  cvRectangle(snapImage, cvPoint(x, y), cvPoint(x+mouthRect.width(), y+mouthRect.height()), CvScalar.MAGENTA, 1, CV_AA, 0);
	    	  }
	    	  else
	    	  {
	    		  int x= mouthRect.x()+roiRect.x();
		    	  int y = mouthRect.y()+roiRect.y();	    	  
		    	  cvRectangle(snapImage, cvPoint(x, y), cvPoint(x+mouthRect.width(), y+mouthRect.height()), CvScalar.MAGENTA, 1, CV_AA, 0);
	    	  }
	      }
	      if(rightearRect.x() != 0)
	      {
	    	  int x= rightearRect.x()+roiRect.x()+(roiRect.width()/2);
	    	  int y = rightearRect.y()+roiRect.y();	    	  
	    	  cvRectangle(snapImage, cvPoint(x, y), cvPoint(x+rightearRect.width(), y+rightearRect.height()), CvScalar.WHITE, 1, CV_AA, 0);
	      }
	  }
}