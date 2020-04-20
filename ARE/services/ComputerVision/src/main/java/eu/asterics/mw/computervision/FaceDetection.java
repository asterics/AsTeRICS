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

import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;


/**
 * This class is meant to contain methods simplifying cv/detection-algorithms.
 * Currently there is also code for drawing elements on an image. This should
 * maybe moved to a seperate class.
 * 
 * @author mad
 *
 */

public class FaceDetection {
    // min face dimensions are empirical values and only work for a resolution
    // of at least 320x240
    private static final int MIN_FACE_HEIGHT = 80;
    private static final int MIN_FACE_WIDTH = 80;
    String classifierName = "data/service.computervision/haarcascade_frontalface_alt.xml";
    CascadeClassifier classifier = null;
    
    //Resize image to height of 75 to speed up face detection tremendously!
    private static final int RESIZE_HEIGHT = 75;
    
    // Objects allocated with a create*() or clone() factory method are
    // automatically released
    // by the garbage collector, but may still be explicitly released by calling
    // release().
    // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on
    // objects allocated this way.
    CvMemStorage storage;

    public FaceDetection() {
        super();
        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the
        // desired class.
        System.out.println("Loading haarcascade classifier");
        classifier = new CascadeClassifier(classifierName);
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
        }
    }

    /**
     * Detect the biggest face in given grayscale image.
     * 
     * @param grayImage
     * @return
     * @throws Exception
     */
    public CvRect detectFace(IplImage grayImage) throws Exception {
        // We already expect getting a grayscale image.
        Mat grayFrame=cvarrToMat(grayImage);
        
        //Resize frame to speed up face detection tremendously.
        //Thanks to learnopencv and Satya Mallick for this trick!!
        float RESIZE_SCALE=grayImage.height()/RESIZE_HEIGHT;
        Mat smallFrame=new Mat();
        resize(grayFrame, smallFrame,new Size(),1.0/RESIZE_SCALE,1.0/RESIZE_SCALE,CV_INTER_AREA);
        
        /*
         * Tuning parameters from
         * "Mastering OpenCV with Practical Computer Vision Projects" book
         * searchScaleFactor: The parameter determines how many different sizes
         * of faces to look for; typically it would be 1.1 for good detection,
         * or 1.2 for faster detection that does not find the face as often.
         * minNeighbors: This parameter determines how sure the detector should
         * be that it has detected a face, typically a value of 3 but you can
         * set it higher if you want more reliable faces, even if many faces are
         * not detected. flags: This parameter allows you to specify whether to
         * look for all faces (default) or only look for the largest face
         * (CASCADE_FIND_BIGGEST_OBJECT). If you only look for the largest face,
         * it should run faster. There are several other parameters you can add
         * to make the detection about one percent or two percent faster, such
         * as CASCADE_DO_ROUGH_SEARCH or CASCADE_SCALE_IMAGE.
         */
        // For any reason the javacv binding does not provide the minFeatureSize
        // method signature of cvHaarDetectObjects.

        RectVector faces = new RectVector();
        classifier.detectMultiScale(smallFrame, faces,
                1.1, 3, CASCADE_FIND_BIGGEST_OBJECT | CASCADE_DO_ROUGH_SEARCH, null, null);

        long total = faces.size();
        if (total > 0) {

            Rect faceRect = faces.get(0);
            if ((faceRect.width()*RESIZE_SCALE) > MIN_FACE_WIDTH && (faceRect.height()*RESIZE_SCALE) > MIN_FACE_HEIGHT) {
            	int x = (int)(faceRect.x()*RESIZE_SCALE), y = (int)(faceRect.y()*RESIZE_SCALE), w = (int)(faceRect.width()*RESIZE_SCALE), h = (int)(faceRect.height()*RESIZE_SCALE);
                return new CvRect(x,y,w,h);
            }
            // System.out.println("ignoring face with width:
            // "+faceRect.width());
        }
        return null;
    }

    /**
     * Converst the given IplImage to a grayscale image.
     * 
     * @param orig
     * @return
     */
    public IplImage convertToGrayScaleIplImage(IplImage orig) {
        int width = orig.width();
        int height = orig.height();
        IplImage grayImage = AbstractIplImage.create(width, height, IPL_DEPTH_8U, 1);
        cvCvtColor(orig, grayImage, CV_BGR2GRAY);
        return grayImage;
    }

    /**
     * Draws the given rectangular onto the given image.
     * 
     * @param faceRect
     * @param image
     */
    public void drawFaceRect(CvRect faceRect, IplImage image) {
        if (faceRect != null && image != null) {
            int x = faceRect.x(), y = faceRect.y(), w = faceRect.width(), h = faceRect.height();
            cvRectangle(image, cvPoint(x, y), cvPoint(x + w, y + h), AbstractCvScalar.RED, 1, CV_AA, 0);
        }
    }

}