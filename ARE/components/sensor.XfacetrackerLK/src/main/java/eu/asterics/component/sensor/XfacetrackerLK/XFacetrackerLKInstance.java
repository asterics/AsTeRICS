
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


import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import java.awt.Dimension;
import java.awt.Point;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import eu.asterics.mw.computervision.FaceDetection;
import eu.asterics.mw.computervision.GrabbedImageListener;
import eu.asterics.mw.computervision.SharedCanvasFrame;
import eu.asterics.mw.computervision.SharedFrameGrabber;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
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
	final IRuntimeOutputPort opNoseX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opNoseY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opChinX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opChinY = new DefaultRuntimeOutputPort();

	FaceDetection faceDetection=new FaceDetection();
	FrameGrabber grabber;
	CanvasFrame frame;
	boolean running=false;
     
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
    	return "";
    }
 
	/**
	 * sets a new value for the given property.
	 * @param propertyName   the name of the property
	 * @param newValue       the desired property value or null if not found
	 */
   public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
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
    		//Get default grabber for this platform (VideoInput for Windows, OpenCV for Linux,...) using default camera (device 0)
			FrameGrabber grabber=SharedFrameGrabber.instance.getDefaultFrameGrabber();
			//register this as listener for grabbed images 
			SharedFrameGrabber.instance.registerGrabbedImageListener("0", this);
			//start grabbing
			SharedFrameGrabber.instance.startGrabbing("0");
			//Create a Canvas/Frame to draw on (this is platform dependant and does not work on Android)
			SharedCanvasFrame.instance.createCanvasFrame("CanvasFrame1", "Face", grabber.getGamma());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
        super.resume();
    }

    @Override
    public void stop()
    {
    	System.out.println("Stopping XFaceTrackerLK, Executed in: "+Thread.currentThread());
    	
    	SharedFrameGrabber.instance.stopGrabbing("0");
    	SharedFrameGrabber.instance.deregisterGrabbedImageListener("0", this);
    	SharedCanvasFrame.instance.disposeFrame("CanvasFrame1");
    	super.stop();
    	System.out.println("Stopped XFaceTrackerLK, Executed in: "+Thread.currentThread());
    }

    /**
     * Callback called when a new frame was grabbed. 
     */
	@Override
	public void imageGrabbed(IplImage image) {
		//System.out.println(".");
		try {
			//Strictly seperate, cv algorithms (detection,...)
			CvRect faceRect=faceDetection.detectFace(image);
			//from drawings on an image
			faceDetection.drawFaceRect(faceRect, image);
			//and a very platform dependant Canvas/Frame!!
			SharedCanvasFrame.instance.showImage("CanvasFrame1", image);
		} catch (java.lang.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}