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

package eu.asterics.mw.computervision;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

public class HaarCascadeDetection {
	private static String HAAR_DIR = "";
	public static final String LEFT_EAR = "haarcascade_mcs_leftear.xml";
	public static final String RIGHT_EAR = "haarcascade_mcs_rightear.xml";
	public static final String MOUTH = "haarcascade_mcs_mouth.xml";
	public static final String EYE="haarcascade_frontaleye.xml";

	
	private static CvMemStorage storage;
	  	  
	public static void setHAAR_DIR(String hAAR_DIR) {
		HAAR_DIR = hAAR_DIR;
	}

	public HaarCascadeDetection()
	{
		storage = CvMemStorage.create();
	}
	  
	  public CvRect detectFeature(IplImage im, String featureName, String haarFnm, CvRect selectRect, int detection)
	  /* Load the Haar classifier from the named file, and apply it to im
	     restricted to the area specified by the selection rectangle (selectRect).
	   
	     Return the first matching feature rectangle (fRect) 
	  */
	  {
		  Loader.load(opencv_objdetect.class);
		  String classifierName = HAAR_DIR + haarFnm;
		  CvHaarClassifierCascade classifier=null;
		  classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
		
		  if (classifier.isNull()) {
			  System.out.println("Could not load the classifier: " + haarFnm + " for " + featureName);
			  return null;
		  }
		  
	    // use selection rectangle to apply a ROI to the image
	    if (selectRect != null) {
	      cvSetImageROI(im, selectRect);
	    }
	
	    CvSeq featureSeq = new CvSeq();
	    featureSeq = cvHaarDetectObjects(im, classifier, storage, 1.1, detection, CV_HAAR_DO_CANNY_PRUNING | CV_HAAR_FIND_BIGGEST_OBJECT);
	
	    cvClearMemStorage(storage);
	    cvResetImageROI(im);
	    
	    int total = featureSeq.total();
	    if (total == 0) {
	      return null;
	    }
	
	    if (total > 1) // this case should not happen, but included for safety
	      System.out.println("Multiple features detected (" + total + ") for " +
	                              featureName + "; using the first");
	
	    CvRect fRect = new CvRect(cvGetSeqElem(featureSeq, 0));
	    return fRect;
	  } 
	
	  public double detectFeatures(IplImage roiImage, CvRect roiRect, CvRect earRect, CvRect mouthRect, Boolean left)
	  {	    
		CvRect helpRect = new CvRect();
		
	    int wF = roiRect.width()/2;
	    int hF = roiRect.height();
	    CvRect selectRect = new CvRect((wF) * hF); 
	    
	    selectRect.y(roiRect.y());
	    selectRect.width(wF);
	    selectRect.height(hF);
	    
	    if (left)
	    {
	    	// section for the left ear on the left side
	    	selectRect.x(roiRect.x());
	    	helpRect = detectFeature(roiImage, "left ear", LEFT_EAR, selectRect, 3);
	    	selectRect.x(roiRect.x()+wF);
	    }
	    else
	    {
	    	// section for the right ear on the right side
	    	selectRect.x(roiRect.x()+wF);
	
	    	helpRect = detectFeature(roiImage, "right ear", RIGHT_EAR, selectRect, 3);
	    	selectRect.x(roiRect.x());
	    }
	
	    
	    if(helpRect == null)
	    {
	    	setCvRectZero(earRect);
	    	setCvRectZero(mouthRect);
	    	return 400; // no ear detected
	    }	    
	    copyCvRect(earRect, helpRect);
	    
	    // if ear detected, detect mouth
	    helpRect = detectFeature(roiImage, "mouth", MOUTH, selectRect,4);    
	    
	    if(helpRect == null)
	    {
	    	setCvRectZero(mouthRect);
	    	return 500;
	    }
	    copyCvRect(mouthRect, helpRect);
	    return calcAngle (earRect , mouthRect);
	  } 
	  
	  private void setCvRectZero(CvRect targetRect)
	  {
		  targetRect.x(0);
		  targetRect.y(0);
		  targetRect.width(0);
		  targetRect.height(0);
	  }
	  
	  private void copyCvRect(CvRect targetRect, CvRect sourceRect)
	  {
		  targetRect.x(sourceRect.x());
		  targetRect.y(sourceRect.y());
		  targetRect.width(sourceRect.width());
		  targetRect.height(sourceRect.height());
	  }
	  
	  private double calcAngle (CvRect earRect, CvRect mouthRect)
	  {
		    //get center of ear
		    Point ear = new Point();
		    ear = centerOfRect(earRect);

		    //get center of mouth
		    Point mouth = new Point();
		    mouth = centerOfRect(mouthRect);
		    
		    //calculate angle between ear and mouth	
		    double angle =0;
		    angle = Math.atan2(mouth.y() - ear.y(), mouth.x() - ear.x()) * 180 / Math.PI;
		    return angle;
	  }
	  
	  private Point centerOfRect (CvRect rect)
	  {
		  Point point = new Point();
		  point.x(rect.x()+(rect.width()/2));
		  point.y(rect.y()+(rect.height()/2));
		  
		  return point;
	  }
}
