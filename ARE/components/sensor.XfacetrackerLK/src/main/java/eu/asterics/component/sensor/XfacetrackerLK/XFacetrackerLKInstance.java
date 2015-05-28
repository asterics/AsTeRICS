
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
	private static final int GAIN = 25;

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
	
    private static final int MAX_POINTS = 6;
	private CanvasFrame frame;
	private FaceDetection faceDetection=new FaceDetection();
	private CvRect roiRect=null;
	private CvRect faceRect=null;
	
    int winSize = 15;
    IntPointer nrPoints;
    int flags=0;
    private static final int A=0;
    private static final int B=1;

    IplImage[] imgGrey=new IplImage[2];
    IplImage[] imgPyr=new IplImage[2];
    CvPoint2D32f[] points=new CvPoint2D32f[2];

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
       if("frameGrabber".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrameGrabber;
			propFrameGrabber = (String)newValue;
			return oldValue;
		} else if("frameGrabberOptions".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrameGrabberOptions;
			propFrameGrabberOptions = (String)newValue;
			return oldValue;
		} else if("cameraSelection".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCameraSelection;
			propCameraSelection = (String)newValue;
			return oldValue;
		} else if("cameraResolution".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCameraResolution;
			propCameraResolution = Integer.parseInt((String)newValue);
			return oldValue;
		}
	   return newValue;
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AstericsErrorHandling.instance.reportError(this, e.getMessage());
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

		if (points[A] == null || nrPoints.get() == 0) {
			cvResetImageROI(imgGrey[A]);
			points[A] = findFeatures(imgGrey[A]);
			System.out.println("after findFeatures: nrPoints.get(): "
					+ nrPoints.get());
		}
		if (points[A] != null && nrPoints.get() > 0) {
			cvSetImageROI(imgGrey[A], roiRect);
			cvSetImageROI(imgGrey[B], roiRect);
			cvSetImageROI(imgPyr[A], roiRect);
			cvSetImageROI(imgPyr[B], roiRect);
			// cvSetImageROI(img,roiRect);

			points[B] = trackOpticalFlow(imgGrey, imgPyr, points[A]);
			drawPoints(img, points[B]);
			sendPortData(points);
			
			points[A] = points[B];
			flags |= CV_LKFLOW_PYR_A_READY;
		}

		// and a very platform dependant Canvas/Frame!!
		SharedCanvasFrame.instance.showImage("CanvasFrame1", img);

		imgGrey[A] = imgGrey[B];
		imgPyr[A] = imgPyr[B];

	}
	
	private void sendPortData(CvPoint2D32f[] points) {
		// send coordinates to output ports
		
		int relX = Math.round((points[B].x()- points[A].x()) * GAIN);
		int relY = Math.round((points[B].y()- points[A].y()) * GAIN);;
		System.out.println("[" + relX + ", " + relY + "]");
		opNoseX.sendData(ConversionUtils.intToBytes(relX));
		opNoseY.sendData(ConversionUtils.intToBytes(relY));	
		
		points[B].position(1);
		points[A].position(1);

		relX = Math.round((points[B].x()- points[A].x()) * GAIN);
		relY = Math.round((points[B].y()- points[A].y()) * GAIN);;
		System.out.println("[" + relX + ", " + relY + "]");
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
        int newCornerCount=0;
        CvPoint2D32f newCorners = new CvPoint2D32f(MAX_POINTS);
                
        // Make an image of the results
        for (int i = 0; i < nrPoints.get(); i++) {
            if (features_found.get(i) == 0 || feature_errors.get(i) > 550) {
                System.out.println("Error is " + feature_errors.get(i) + "/n");
                continue;
            }
            //System.out.println("Got it/n");
            pointsA.position(i);
            pointsB.position(i);
            
            newCorners.position(newCornerCount);
            newCorners.put((double)pointsB.x(),(double)pointsB.y());
            newCornerCount++;
            
        }    	

        nrPoints.put(newCornerCount);
        pointsA.position(0);
        pointsB.position(0);
        newCorners.position(0);
        System.out.println("new nrPoints: "+nrPoints.get());

        return newCorners;
    }
    
    public void drawPoints(IplImage img, CvPoint2D32f corners) {   	
        for (int i = 0; i < nrPoints.get(); i++) {
        	corners.position(i);
			CvPoint p = cvPoint(Math.round(corners.x()),
					Math.round(corners.y()));
			System.out.println("p"+i+": " + p);
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
				
				//roiRect=faceRect;
				
				int x = faceRect.x() + faceRect.width()/2;
				int y = faceRect.y() + faceRect.height();
//				CvPoint initNose[]=new CvPoint[]{cvPoint(x,y-10),cvPoint(x-20,y-10),cvPoint(x+20,y-10),cvPoint(x,y+10)};
//				CvPoint initChin[]=new CvPoint[]{cvPoint(x,y+65),cvPoint(x,y+55)};
				CvPoint initNose[]=new CvPoint[]{cvPoint(x,(int)Math.round(y*0.7))};
				CvPoint initChin[]=new CvPoint[]{cvPoint(x,(int)Math.round(y*0.97))};

				
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