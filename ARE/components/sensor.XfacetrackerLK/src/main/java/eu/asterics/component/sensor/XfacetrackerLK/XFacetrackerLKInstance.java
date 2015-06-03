
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

 
package eu.asterics.component.sensor.XfacetrackerLK; 


import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_EPS;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvTermCriteria;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindCornerSubPix;
import static org.bytedeco.javacpp.opencv_video.CV_LKFLOW_PYR_A_READY;
import static org.bytedeco.javacpp.opencv_video.cvCalcOpticalFlowPyrLK;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.helper.opencv_core.CvArr;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;

import eu.asterics.mw.computervision.FaceDetection;
import eu.asterics.mw.computervision.GrabbedImageListener;
import eu.asterics.mw.computervision.SharedCanvasFrame;
import eu.asterics.mw.computervision.SharedFrameGrabber;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Impelements a haardcascade combined with Lukas Kanade flow algorithm to detect face tracking. Based on FacetrackerLK from Chris Veigl.
 *   
 * @author Martin Deinhofer [deinhofe@technikum-wien.at]
 *         Date: Feb 19, 2015
 *         Time: 11:00:00 AM 
 */
public class XFacetrackerLKInstance extends AbstractRuntimeComponentInstance implements GrabbedImageListener
{
	private static final int GAIN = 20;

	final IRuntimeOutputPort opNoseX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opNoseY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opChinX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opChinY = new DefaultRuntimeOutputPort();

	FrameGrabber grabber;
	boolean running=false;
	
	//params for face detection
	//utility class to detect faces
	
	//params LK algorithm
	//nr. of points to track with optical flow algorithm

	
    //params for mouse coordinates
	private int lastX=0;
	private int lastY=0;
	
    private static final int MAX_POINTS = 4;
	private CanvasFrame frame;
	private FaceDetection faceDetection=new FaceDetection();
	private CvRect validRect=null;

	private CvRect roiRect=null;
	private CvRect faceRect=null;
	
    int winSize = 15;
    IntPointer nrPoints;
    int flags=0;
    private static final int A=0;
    private static final int B=1;
    
    boolean needToInit=true;

    IplImage[] imgGrey=new IplImage[2];
    IplImage[] imgPyr=new IplImage[2];
    CvPoint2D32f[] points=new CvPoint2D32f[2];
	private double initDistChin2Nosetip;
	private double initDistNoseroot2Nosetip;
    
    private static int NOSE_TIP = 0;
    private static int NOSE_ROOT = 1;
    private static int CHIN_L = 2;
    private static int CHIN_R = 3;

	private String propFrameGrabber="OpenCV";
	private String propCameraSelection="0";
	private Integer propCameraResolution=1;
	private String propFrameGrabberOptions="dshow";

     
	/**
	 * The class constructor.
	 */
   public XFacetrackerLKInstance()
    {
        // empty constructor - needed for OSGi service factory operations
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
        if("noseX".equalsIgnoreCase(portID))
        {
            return opNoseX;
        }
        else if("noseY".equalsIgnoreCase(portID))
        {
            return opNoseY;
        }
        else if("chinX".equalsIgnoreCase(portID))
        {
            return opChinX;
        }
        else if("chinY".equalsIgnoreCase(portID))
        {
            return opChinY;
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
    	if("frameGrabber".equalsIgnoreCase(propertyName)) {
    		return propFrameGrabber;
    	} else if("frameGrabberOptions".equalsIgnoreCase(propertyName)) {
    		return propFrameGrabberOptions;
    	}else if("cameraSelection".equalsIgnoreCase(propertyName)) {
    		return propCameraSelection;
    	}else if("cameraResolution".equalsIgnoreCase(propertyName)) {
    		return propCameraResolution;
    	}

    	return "";
    }
    
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if ("frameGrabber".equalsIgnoreCase(key))
		{
			return SharedFrameGrabber.instance.getFrameGrabberList();
		} else if ("deviceList".equalsIgnoreCase(key))
		{
			return SharedFrameGrabber.instance.getDeviceList(propFrameGrabber);
		}
		return res;
	} 
  
 
	/**
	 * sets a new value for the given property.
	 * @param propertyName   the name of the property
	 * @param newValue       the desired property value or null if not found
	 */
   public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
	   //Stop plugin first, because if the camera selection is changed we would not know any more the previous camera to stop which would
	   //then keep running.
	   boolean wasRunning=running;
	   if(wasRunning) {
		   stop();
	   }
	   Object oldValue=newValue;
       if("frameGrabber".equalsIgnoreCase(propertyName))
		{
			oldValue = propFrameGrabber;
			propFrameGrabber = (String)newValue;
		} else if("frameGrabberOptions".equalsIgnoreCase(propertyName))
		{
			oldValue = propFrameGrabberOptions;
			propFrameGrabberOptions = (String)newValue;
		} else if("cameraSelection".equalsIgnoreCase(propertyName))
		{
			oldValue = propCameraSelection;
			propCameraSelection = (String)newValue;
		} else if("cameraResolution".equalsIgnoreCase(propertyName))
		{
			oldValue = propCameraResolution;
			propCameraResolution = Integer.parseInt((String)newValue);
		}
	   if(wasRunning) {
		   start();
	   }
	   
	   return oldValue;
    }
   
	/**
	 * returns an Event Listener Port 
	 * @param enventPortID   the name of the event listener port
	 * @return       the event listener port or null if not found
	 */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        if("init".equalsIgnoreCase(eventPortID))
        {
            return initEvent;
        }        
        if("showCameraSettings".equalsIgnoreCase(eventPortID))
        {
            return elpShowCameraSettings;
        }       
        if("saveProfile".equalsIgnoreCase(eventPortID))
        {
            return elpSaveProfile;
        }                
        return null;
    }
        
	/**
	 * Event Listener Port for face position initialisation.
	 */
    final IRuntimeEventListenerPort initEvent 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    	 }
    };
 
	/**
	 * Event Listener Port for Camera Settings Window.
	 */
    final IRuntimeEventListenerPort elpShowCameraSettings 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    	 }
    };
    
	/**
	 * Event Listener Port for save profile.
	 */
    final IRuntimeEventListenerPort elpSaveProfile 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    	 }
    };

    @Override
    public void start()
    {
    	running=false;
    	try {
    		resetVariables();
    		//Get default grabber for this platform (VideoInput for Windows, OpenCV for Linux,...) using default camera (device 0)
			FrameGrabber grabber=SharedFrameGrabber.instance.getFrameGrabber(propCameraSelection,propFrameGrabber,propCameraResolution,propFrameGrabberOptions);
			//register this as listener for grabbed images 
			SharedFrameGrabber.instance.registerGrabbedImageListener(propCameraSelection, this);
			//Create a Canvas/Frame to draw on (this is platform dependant and does not work on Android)
			SharedCanvasFrame.instance.createCanvasFrame("CanvasFrame1", "Face", grabber.getGamma());
			//start grabbing
			SharedFrameGrabber.instance.startGrabbing(propCameraSelection);
			running=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AstericsErrorHandling.instance.reportError(this, e.getMessage());
			stop();
		}
        super.start();
    }
    
    @Override
    public void pause()
    {
        super.pause();
    }

    @Override
    public void resume()
    {
    	lastX=0;
    	lastY=0;
        super.resume();
    }

    @Override
    public void stop()
    {
    	//System.out.println("Stopping XFaceTrackerLK, Executed in: "+Thread.currentThread().getName());
    	
    	SharedFrameGrabber.instance.stopGrabbing(propCameraSelection);
    	SharedFrameGrabber.instance.deregisterGrabbedImageListener(propCameraSelection, this);
    	SharedCanvasFrame.instance.disposeFrame("CanvasFrame1");
    	resetVariables();
    	super.stop();
    	running=false;
    	//System.out.println("Stopped XFaceTrackerLK, Executed in: "+Thread.currentThread().getName());
    }
    
    private void resetVariables() {
		lastX=0;
		lastY=0;
		imgGrey[A]=null;
		imgGrey[B]=null;
		imgPyr[A]=null;
		imgPyr[B]=null;
		points[A]=null;
		points[B]=null;
		
    	nrPoints=new IntPointer(1).put(0);
		roiRect=null;
		faceRect=null;
		flags=0;
    }

    private void initTracker(IplImage img) {
		cvFlip(img, img, 1);

		int width  = img.width();
		int height = img.height();
		roiRect= cvRect(0,0,width,height);
		imgGrey[A]    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
		cvCvtColor(img, imgGrey[A], CV_BGR2GRAY);
		flags=0;
		
        CvSize pyr_sz = cvSize(imgGrey[A].width(), imgGrey[A].height());

        imgPyr[A] = IplImage.create(pyr_sz, IPL_DEPTH_8U, 1);
        imgPyr[B] = IplImage.create(pyr_sz, IPL_DEPTH_8U, 1);
		points[A]=null;
    }
    
    /**
     * Callback called when a new frame was grabbed. 
     */
	@Override
	public void imageGrabbed(IplImage img) {
		//System.out.println(".");
		//if this is the first frame, init tracker
		if(imgGrey[A]==null) {
			initTracker(img);
			return;
		}

		cvFlip(img, img, 1);

		imgGrey[B] = IplImage
				.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(img, imgGrey[B], CV_BGR2GRAY);
		cvSetImageROI(imgGrey[B], roiRect);

		if (points[A] == null || needToInit) {
			cvResetImageROI(imgGrey[A]);
			points[A] = findFeatures(imgGrey[A]);
			faceDetection.drawFaceRect(faceRect, img);
		}
		if (points[A] != null && nrPoints.get() > 0) {
			cvSetImageROI(imgGrey[A], roiRect);
			cvSetImageROI(imgGrey[B], roiRect);
			cvSetImageROI(imgPyr[A], roiRect);
			cvSetImageROI(imgPyr[B], roiRect);
			// cvSetImageROI(img,roiRect);

			points[B] = trackOpticalFlow(imgGrey, imgPyr, points[A]);
			if(!needToInit) {
				drawPoints(img, points[B]);
				//faceDetection.drawFaceRect(faceRect, img);
				sendPortData(points);

				points[A] = points[B];
				flags |= CV_LKFLOW_PYR_A_READY;
			}
		}

		// and a very platform dependant Canvas/Frame!!
		SharedCanvasFrame.instance.showImage("CanvasFrame1", img);

		imgGrey[A] = imgGrey[B];
		imgPyr[A] = imgPyr[B];

	}
	
	private void sendPortData(CvPoint2D32f[] points) {
		// send coordinates to output ports
		if(needToInit) {
			return;
		}
		
		int relX = Math.round((points[B].x()- points[A].x()) * GAIN);
		int relY = Math.round((points[B].y()- points[A].y()) * GAIN);;
		//System.out.println("[" + relX + ", " + relY + "]");
		opNoseX.sendData(ConversionUtils.intToBytes(relX));
		opNoseY.sendData(ConversionUtils.intToBytes(relY));	
		
		points[B].position(1);
		points[A].position(1);

		relX = Math.round((points[B].x()- points[A].x()) * GAIN);
		relY = Math.round((points[B].y()- points[A].y()) * GAIN);;
		//System.out.println("[" + relX + ", " + relY + "]");
		opChinX.sendData(ConversionUtils.intToBytes(relX));
		opChinY.sendData(ConversionUtils.intToBytes(relY));			
		
		points[B].position(0);
		points[A].position(0);
	}
	
    public CvPoint2D32f trackOpticalFlow(IplImage[] imgGrey, IplImage[] imgPyr, CvPoint2D32f pointsA) {
        // Call Lucas Kanade algorithm
        BytePointer features_found = new BytePointer(MAX_POINTS);
        FloatPointer feature_errors = new FloatPointer(MAX_POINTS);

        CvPoint2D32f pointsB = new CvPoint2D32f(MAX_POINTS);
        cvCalcOpticalFlowPyrLK(imgGrey[A], imgGrey[B], imgPyr[A], imgPyr[B], pointsA, pointsB,
                nrPoints.get(), cvSize(winSize, winSize), 5,
                features_found, feature_errors,
                cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3),flags);
        
        return rejectBadPoints(pointsA, pointsB, features_found, feature_errors);
    }
    
    private CvPoint2D32f rejectBadPoints(CvPoint2D32f pointsA, CvPoint2D32f pointsB, BytePointer features_found, FloatPointer feature_errors) {
        needToInit=false;
        // Make an image of the results
        for (int i = 0; i < nrPoints.get(); i++) {
            if (features_found.get(i) == 0 || feature_errors.get(i) > 550) {
                System.out.println("Error is " + feature_errors.get(i) + "/n");
                needToInit=true;
                return pointsB;
            }
            //System.out.println("Got it/n");
            
            
            pointsA.position(i);
            pointsB.position(i); 

            //Ignore points lying outside ROI, actually we should try to use cvSetImageROI
            
            if(!(validRect.x()<=pointsB.x()&&pointsB.x()<=(validRect.x()+validRect.width()))||
            !(validRect.y()<=pointsB.y()&&pointsB.y()<=(validRect.y()+validRect.height()))) {
            	System.out.println("out of roi: "+validRect);
            	needToInit=true;
            	return pointsB;
            }
            
            //Reject relative individual point displacements > tresh with base faceRect.width for x rel displacements and faceRect.height for y rel displacements
            double dx=pointsB.x()-pointsA.x();
            double dy=pointsB.y()-pointsA.y();
            double relDispTresh=0.2;
            if(dx/faceRect.width() > relDispTresh || dy/faceRect.height() > relDispTresh) {
            	System.out.println(dx/faceRect.width() +" || "+dy/faceRect.height()+" > "+relDispTresh);
            	needToInit=true;
            	return pointsB;            	
            }
            
        }
        //Reject distchin2nosetip within range (0.9 < distchin2nosetip < 1.2)??               
		double relDistChin2Nosetip=magnitude(pointsB,CHIN_L,NOSE_TIP)/initDistChin2Nosetip;
		double relDistNoseroot2Nosetip=magnitude(pointsB,NOSE_ROOT,NOSE_TIP)/initDistNoseroot2Nosetip;
		double lowerDistChin2NosetipTresh=0.85;
		double upperDistChin2NosetipTresh=1.4;
		if(!(lowerDistChin2NosetipTresh <= relDistChin2Nosetip && relDistChin2Nosetip <= upperDistChin2NosetipTresh)) {
        	System.out.println("chin y-disp out of range: "+lowerDistChin2NosetipTresh +" <= "+relDistChin2Nosetip+" <= "+upperDistChin2NosetipTresh);
        	needToInit=true;
        	return pointsB;            					
		}
		/*
        //Reject nosetip2noseroot distance
		double lowerDistNoseroot2NosetipTresh=0.75;
		double upperDistNoseroot2NosetipTresh=1.15;
		if(!(lowerDistNoseroot2NosetipTresh <= relDistNoseroot2Nosetip && relDistNoseroot2Nosetip <= upperDistNoseroot2NosetipTresh)) {
        	System.out.println("noseroot y-disp out of range: "+lowerDistNoseroot2NosetipTresh +" <= "+relDistNoseroot2Nosetip+" <= "+upperDistNoseroot2NosetipTresh);
        	needToInit=true;
        	return pointsB;            					
		}

        //Reject nosetip2noseroot angle != chin2nosetip angle
        double angleNoseroot2Nosetip=angleVertical(pointsB, NOSE_ROOT, NOSE_TIP);
        //angle of Chin2Nosetip, mutliply by -1 to have same sign as angleNoseroot2Nosetip
        double angleChin2Nosetip=angleVertical(pointsB, CHIN_L, NOSE_TIP)*-1;
        double angleTolerance=25;
    	System.out.println("angle chin2nosetip "+(-1*angleChin2Nosetip)+", angle noseroot2nosetip: "+angleNoseroot2Nosetip);
        if(!((angleNoseroot2Nosetip-angleTolerance) <= angleChin2Nosetip && angleChin2Nosetip <= (angleNoseroot2Nosetip+angleTolerance))) {
        	//System.out.println("angle chin2nosetip out of range: "+(angleNoseroot2Nosetip-angleTolerance) +" <= "+angleChin2Nosetip+" <= "+(angleNoseroot2Nosetip+angleTolerance));
        	needToInit=true;
        	return pointsB;            					        	
        }
        */
        
        pointsA.position(0);
        pointsB.position(0);
        
        return pointsB;
    }
    
    private double magnitude(CvPoint2D32f p, int i1, int i2) {
    	//System.out.println("got: "+p);
    	double dx=p.position(i1).x()-p.position(i2).x();
    	double dy=p.position(i1).y()-p.position(i2).y();
    	return Math.sqrt(dx*dx+dy*dy);
    }
    
    private double angleVertical(CvPoint2D32f p, int i1, int i2) {
    	//System.out.println("got: "+p);
    	double dx=p.position(i1).x()-p.position(i2).x();
    	double dy=p.position(i1).y()-p.position(i2).y();
    	return Math.atan(dx/dy)*180/Math.PI;
    }
    
    private CvPoint2D32f avgPoint2D32f(CvPoint2D32f p, int from, int to) {
    	double l=to-from+1;
    	CvPoint2D32f avg=new CvPoint2D32f(1);
    	double x=0.0;
    	double y=0.0;
    	for(int i=0;i<=l;i++) {
    		x+=p.position(i).x();
    		y+=p.position(i).y();
    	}
    	avg.put((double)x/l, (double)y/l);
    	return avg;
    }

    
    public void drawPoints(IplImage img, CvPoint2D32f corners) {   	
        for (int i = 0; i < nrPoints.get(); i++) {
        	corners.position(i);
			CvPoint p = cvPoint(Math.round(corners.x()),
					Math.round(corners.y()));
			//System.out.println("p"+i+": " + p);
			// cvLine(img, p0, p1, CV_RGB(255, 0, 0),
			// 2, 8, 0);
			cvCircle(img, p, 4, CV_RGB(255, 0, 0), 2, 5, 0);
        }
        corners.position(0);
    }
    
    public CvPoint2D32f findFeatures(IplImage imgA) {
		
		try {
			faceRect = faceDetection.detectFace(imgA);
			if(faceRect!=null) { 
				System.out.println("Found face at: "+faceRect);
				
				//roiRect=cvRect(faceRect.x()-faceRect.width()/2,faceRect.y()-faceRect.height()/2,2*faceRect.width(),2*faceRect.height());
				validRect=cvRect(faceRect.x()-faceRect.width()/2,faceRect.y()-faceRect.height()/2,2*faceRect.width(),2*faceRect.height());

				
				int x = faceRect.x() + faceRect.width()/2;
				int y = faceRect.y();
				//CvPoint initNose[]=new CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.6)),cvPoint(x,y+(int)(faceRect.height()*0.45))};
				CvPoint initNose[]=new CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.6)),cvPoint(x,y+(int)(faceRect.height()*0.45))};

//				CvPoint initChin[]=new CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.95)),cvPoint(x,y+(int)(faceRect.height()*0.9)),cvPoint(x-7,y+(int)(faceRect.height()*0.95)),cvPoint(x+7,y+(int)(faceRect.height()*0.95))};
				//CvPoint initChin[]=new CvPoint[]{cvPoint(x-7,y+(int)(faceRect.height()*0.95)),cvPoint(x+7,y+(int)(faceRect.height()*0.95))};
				CvPoint initChin[]=new CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.97))};

				
				CvPoint2D32f pointsA = new CvPoint2D32f(MAX_POINTS);
				CvArr mask = null;
				
				pointsA=addCvPoints(pointsA,initNose);
				pointsA=addCvPoints(pointsA,initChin);
				nrPoints.put(pointsA.position());
				pointsA.position(0);
				
				//Setting image ROI should improve tracking quality, but disabled it so far
				cvSetImageROI(imgA,roiRect);
				//cvSetImageROI(imgA,faceRect);
				//cvSetImageROI(imgA,faceRect);
				
				//Uses given points and tries to find better trackable ones in the neighbourhood.
				//cvTermCriteria is set to 1, 1 because otherwise the new points would be too far away. 
				
				
				cvFindCornerSubPix(
						imgA,
						pointsA,
						nrPoints.get(),
						cvSize(winSize, winSize),
						cvSize(-1, -1),
//						cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.03));
				cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 1, 1));
						
				//cvResetImageROI(imgA);

				System.out.println("Found trackable points: pointsA: "+pointsA);
				initDistChin2Nosetip=magnitude(pointsA, CHIN_L, NOSE_TIP);
				initDistNoseroot2Nosetip=magnitude(pointsA, NOSE_ROOT, NOSE_TIP);
				
				return pointsA;
			}			
		} catch (java.lang.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    private CvPoint2D32f addCvPoints(CvPoint2D32f corners, CvPoint[] points) {
    	for(CvPoint point : points) {
    		corners.put(point);
    		corners.position(corners.position()+1);
    	}
    	return corners;
    }
}