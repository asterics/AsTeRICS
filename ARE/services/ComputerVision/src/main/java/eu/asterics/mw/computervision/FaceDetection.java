package eu.asterics.mw.computervision;

import java.io.File;
import java.net.URL;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

import eu.asterics.mw.services.AstericsErrorHandling;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

/**
 * This class is meant to contain methods simplifying cv/detection-algorithms. Currently there is also code for drawing elements on an image. 
 * This should maybe moved to a seperate class.  
 * @author mad
 *
 */

public class FaceDetection {
	String classifierName = "data/service.computervision/haarcascade_frontalface_alt.xml";
	CvHaarClassifierCascade classifier=null;
	// Objects allocated with a create*() or clone() factory method are automatically released
	// by the garbage collector, but may still be explicitly released by calling release().
	// You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
	CvMemStorage storage;


	public FaceDetection() {
		super();
		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		// We can "cast" Pointer objects by instantiating a new object of the desired class.
		System.out.println("Loading haarcascade classifier");
		classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
		if (classifier.isNull()) {        	
			System.err.println("Error loading classifier file \"" + classifierName + "\".");
		}              		
		
		// Objects allocated with a create*() or clone() factory method are automatically released
		// by the garbage collector, but may still be explicitly released by calling release().
		// You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
		storage = CvMemStorage.create();
	}

	public CvRect detectFace(IplImage grayImage) throws Exception {
		// Let's try to detect some faces! but we need a grayscale image...
		/*
		int width  = grabbedImage.width();
		int height = grabbedImage.height();
		IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);        
		cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
*/
		
		// FAQ about IplImage:
		// - For custom raw processing of data, getByteBuffer() returns an NIO direct
		//   buffer wrapped around the memory pointed by imageData, and under Android we can
		//   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
		// - To get a BufferedImage from an IplImage, we may call getBufferedImage().
		// - The createFrom() factory method can construct an IplImage from a BufferedImage.
		// - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
		//IplImage grabbedImage = grabber.grab();

		cvClearMemStorage(storage);
		//storage.release();

		long sTime=System.currentTimeMillis();
		
		CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
				1.1, 1, CV_HAAR_DO_ROUGH_SEARCH | CV_HAAR_FIND_BIGGEST_OBJECT);
			
		
		//CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,1.2, 2, CV_HAAR_DO_CANNY_PRUNING | CV_HAAR_FIND_BIGGEST_OBJECT);
		long eTime=System.currentTimeMillis();
		//System.out.println("faceDetection took "+(eTime-sTime)+" ms");

		int total = faces.total();

		//grabbedImage.release();
		/*
		grayImage.release();
		grayImage=null;
		*/
		if(total > 0) {
			
			CvRect faceRect=new CvRect(cvGetSeqElem(faces, 0));
			
			//storage.release();
			return faceRect;
		} 
		//storage.release();
		return null;
	}
	
	public IplImage convertToGrayScaleIplImage(IplImage orig) {
		int width  = orig.width();
		int height = orig.height();
		IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
		cvCvtColor(orig, grayImage, CV_BGR2GRAY);
		return grayImage;
	}

	public void drawFaceRect(CvRect faceRect, IplImage image) {
		if(faceRect != null && image != null) {
			int x = faceRect.x(), y = faceRect.y(), w = faceRect.width(), h = faceRect.height();
			cvRectangle(image, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);
		}
	}

}