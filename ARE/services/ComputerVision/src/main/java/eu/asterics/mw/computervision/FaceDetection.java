package eu.asterics.mw.computervision;

import java.io.File;
import java.net.URL;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;

import eu.asterics.mw.services.AstericsErrorHandling;
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

	public FaceDetection() {
		super();
		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		// We can "cast" Pointer objects by instantiating a new object of the desired class.
		classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
		if (classifier.isNull()) {        	
			AstericsErrorHandling.instance.getLogger().warning("Error loading classifier file \"" + classifierName + "\".");
		}              		
	}

	public CvRect detectFace(IplImage grabbedImage) throws Exception {


		// FAQ about IplImage:
		// - For custom raw processing of data, getByteBuffer() returns an NIO direct
		//   buffer wrapped around the memory pointed by imageData, and under Android we can
		//   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
		// - To get a BufferedImage from an IplImage, we may call getBufferedImage().
		// - The createFrom() factory method can construct an IplImage from a BufferedImage.
		// - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
		//IplImage grabbedImage = grabber.grab();
		int width  = grabbedImage.width();
		int height = grabbedImage.height();
		IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);        

		// Objects allocated with a create*() or clone() factory method are automatically released
		// by the garbage collector, but may still be explicitly released by calling release().
		// You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
		CvMemStorage storage = CvMemStorage.create();


		cvClearMemStorage(storage);

		// Let's try to detect some faces! but we need a grayscale image...
		cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);


		CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
				1.1, 1, CV_HAAR_DO_ROUGH_SEARCH | CV_HAAR_FIND_BIGGEST_OBJECT);

		int total = faces.total();

		if(total > 0) {
			/*
			CvRect faceRect=new CvRect(cvGetSeqElem(faces, 0));
			opencv_core.cvReleaseImage(grayImage);
			grayImage=null;
			 */
			return faceRect;
		} 
		return null;
	}

	public void drawFaceRect(CvRect faceRect, IplImage image) {
		if(faceRect != null && image != null) {
			int x = faceRect.x(), y = faceRect.y(), w = faceRect.width(), h = faceRect.height();
			cvRectangle(image, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);
		}
	}

}