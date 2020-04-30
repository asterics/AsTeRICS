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

import org.bytedeco.opencv.global.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_tracking.*;
import org.bytedeco.opencv.opencv_optflow.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_video.*;
import static org.bytedeco.opencv.global.opencv_optflow.*;
import static org.bytedeco.opencv.global.opencv_tracking.*;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class HaarCascadeDetection {
    private static String HAAR_DIR = "";
    public static final String LEFT_EAR = "haarcascade_mcs_leftear.xml";
    public static final String RIGHT_EAR = "haarcascade_mcs_rightear.xml";
    public static final String MOUTH = "haarcascade_mcs_mouth.xml";
    public static final String EYE = "haarcascade_frontaleye.xml";

    private static CvMemStorage storage;

    public static void setHAAR_DIR(String hAAR_DIR) {
        HAAR_DIR = hAAR_DIR;
    }

    public HaarCascadeDetection() {
        storage = AbstractCvMemStorage.create();
    }

    public CvRect detectFeature(IplImage im, String featureName, String haarFnm, CvRect selectRect, int detection)
    /*
     * Load the Haar classifier from the named file, and apply it to im
     * restricted to the area specified by the selection rectangle (selectRect).
     * 
     * Return the first matching feature rectangle (fRect)
     */
    {
//        Loader.load(opencv_objdetect.class);
        String classifierName = HAAR_DIR + haarFnm;
        CascadeClassifier classifier = null;
        classifier = new CascadeClassifier(classifierName);

        if (classifier.isNull()) {
            System.out.println("Could not load the classifier: " + haarFnm + " for " + featureName);
            return null;
        }

        Mat grayFrame=cvarrToMat(im);       
        
        // use selection rectangle to apply a ROI to the image
        if (selectRect != null) {
            cvSetImageROI(im, selectRect);
        }

        RectVector faces = new RectVector();
        classifier.detectMultiScale(grayFrame, faces,
                1.1, 3, CASCADE_FIND_BIGGEST_OBJECT | CASCADE_DO_ROUGH_SEARCH, null, null);
        
//        cvClearMemStorage(storage);
        cvResetImageROI(im);

        long total = faces.size();
        if (total > 1) {
            System.out.println("Multiple features detected (" + total + ") for " + featureName + "; using the first");
            
            Rect faceRect = faces.get(0);
            return new CvRect(faceRect.x(),faceRect.y(),faceRect.width(),faceRect.height());
        }

        return null;
    }

    public double detectFeatures(IplImage roiImage, CvRect roiRect, CvRect earRect, CvRect mouthRect, Boolean left) {
        CvRect helpRect = new CvRect();

        int wF = roiRect.width() / 2;
        int hF = roiRect.height();
        CvRect selectRect = new CvRect((wF) * hF);

        selectRect.y(roiRect.y());
        selectRect.width(wF);
        selectRect.height(hF);

        if (left) {
            // section for the left ear on the left side
            selectRect.x(roiRect.x());
            helpRect = detectFeature(roiImage, "left ear", LEFT_EAR, selectRect, 3);
            selectRect.x(roiRect.x() + wF);
        } else {
            // section for the right ear on the right side
            selectRect.x(roiRect.x() + wF);

            helpRect = detectFeature(roiImage, "right ear", RIGHT_EAR, selectRect, 3);
            selectRect.x(roiRect.x());
        }

        if (helpRect == null) {
            setCvRectZero(earRect);
            setCvRectZero(mouthRect);
            return 400; // no ear detected
        }
        copyCvRect(earRect, helpRect);

        // if ear detected, detect mouth
        helpRect = detectFeature(roiImage, "mouth", MOUTH, selectRect, 4);

        if (helpRect == null) {
            setCvRectZero(mouthRect);
            return 500;
        }
        copyCvRect(mouthRect, helpRect);
        return calcAngle(earRect, mouthRect);
    }

    private void setCvRectZero(CvRect targetRect) {
        targetRect.x(0);
        targetRect.y(0);
        targetRect.width(0);
        targetRect.height(0);
    }

    private void copyCvRect(CvRect targetRect, CvRect sourceRect) {
        targetRect.x(sourceRect.x());
        targetRect.y(sourceRect.y());
        targetRect.width(sourceRect.width());
        targetRect.height(sourceRect.height());
    }

    private double calcAngle(CvRect earRect, CvRect mouthRect) {
        // get center of ear
        Point ear = new Point();
        ear = centerOfRect(earRect);

        // get center of mouth
        Point mouth = new Point();
        mouth = centerOfRect(mouthRect);

        // calculate angle between ear and mouth
        double angle = 0;
        angle = Math.atan2(mouth.y() - ear.y(), mouth.x() - ear.x()) * 180 / Math.PI;
        return angle;
    }

    private Point centerOfRect(CvRect rect) {
        Point point = new Point();
        point.x(rect.x() + (rect.width() / 2));
        point.y(rect.y() + (rect.height() / 2));

        return point;
    }
}
