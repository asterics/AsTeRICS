
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.XfacetrackerLK;

import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_EPS;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_SIMPLEX;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvTermCriteria;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindCornerSubPix;
import static org.bytedeco.javacpp.opencv_imgproc.cvInitFont;
import static org.bytedeco.javacpp.opencv_imgproc.cvPutText;
import static org.bytedeco.javacpp.opencv_video.CV_LKFLOW_PYR_A_READY;
import static org.bytedeco.javacpp.opencv_video.cvCalcOpticalFlowPyrLK;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvFont;
import org.bytedeco.javacpp.helper.opencv_core.AbstractIplImage;
import org.bytedeco.javacv.FrameGrabber;

import eu.asterics.mw.are.DeploymentManager;
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
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 * Impelements a haardcascade combined with Lukas Kanade flow algorithm to detect face tracking. Based on FacetrackerLK from Chris Veigl.
 * 
 * @author Martin Deinhofer [deinhofe@technikum-wien.at] Date: Feb 19, 2015 Time: 11:00:00 AM
 */
public class XFacetrackerLKInstance extends AbstractRuntimeComponentInstance implements GrabbedImageListener {
    private static final int GAIN = 20;

    final IRuntimeOutputPort opNoseX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opNoseY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opChinX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opChinY = new DefaultRuntimeOutputPort();

    FrameGrabber grabber;
    boolean running = false;

    // params for face detection
    // utility class to detect faces

    // params LK algorithm
    // nr. of points to track with optical flow algorithm

    private static final int MAX_POINTS = 2;
    private FaceDetection faceDetection = new FaceDetection();
    private CvRect validRect = null;

    private CvRect roiRect = null;
    private CvRect faceRect = null;

    int winSize = 15;
    IntPointer nrPoints;
    int flags = 0;
    private static final int A = 0;
    private static final int B = 1;

    boolean needToInit = true;

    IplImage[] imgGrey = new IplImage[2];
    IplImage[] imgPyr = new IplImage[2];
    CvPoint2D32f[] points = new CvPoint2D32f[2];
    private double initDistChin2Nosetip;
    private static int NOSE_TIP = 0;
    // private static int NOSE_ROOT = 1;
    private static int CHIN_L = 1;
    // private static int CHIN_R = 3;

    private String propFrameGrabber = "OpenCV";
    private String propCameraSelection = "0";
    private Integer propCameraResolution = 1;
    private String propFrameGrabberFormat = "dshow";
    private String propTitleVideoFrameWindow = "";
    private Integer propFrameRate = 0;
    private boolean propDisplayGUI = true;
    private boolean propEnableOverlaySettings=true;

    private String instanceId = "XFacetrackerLK";

    private CvFont overlayTextFont = new CvFont();
    private CvScalar[] COLORS = { CvScalar.GREEN, CvScalar.YELLOW, CvScalar.RED, CvScalar.BLUE };
    private int frameCount = 0;
    private long frameCountStart = 0;
    private int initCycles = 0;
    private long realFrameRate = 0;

    /**
     * The class constructor.
     */
    public XFacetrackerLKInstance() {
        // empty constructor - needed for OSGi service factory operations
        // If we want to draw an overlay text, we have to init it first.
        cvInitFont(overlayTextFont, FONT_HERSHEY_SIMPLEX, 1, 1, 1, 2, 4);
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("noseX".equalsIgnoreCase(portID)) {
            return opNoseX;
        } else if ("noseY".equalsIgnoreCase(portID)) {
            return opNoseY;
        } else if ("chinX".equalsIgnoreCase(portID)) {
            return opChinX;
        } else if ("chinY".equalsIgnoreCase(portID)) {
            return opChinY;
        }

        return null;
    }

    /**
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("frameGrabber".equalsIgnoreCase(propertyName)) {
            return propFrameGrabber;
        } else if ("frameGrabberFormat".equalsIgnoreCase(propertyName)) {
            return propFrameGrabberFormat;
        } else if ("cameraSelection".equalsIgnoreCase(propertyName)) {
            return propCameraSelection;
        } else if ("cameraResolution".equalsIgnoreCase(propertyName)) {
            return propCameraResolution;
        } else if ("titleVideoFrameWindow".equalsIgnoreCase(propertyName)) {
            return propTitleVideoFrameWindow;
        } else if ("displayGUI".equalsIgnoreCase(propertyName)) {
            return propDisplayGUI;
        } else if ("frameRate".equalsIgnoreCase(propertyName)) {
            return propFrameRate;
        } else if ("enableOverlaySettings".equalsIgnoreCase(propertyName)) {
            return propEnableOverlaySettings;
        }

        return "";
    }

    @Override
    public List<String> getRuntimePropertyList(String key) {
        List<String> res = new ArrayList<String>();
        if ("frameGrabber".equalsIgnoreCase(key)) {
            return SharedFrameGrabber.instance.getFrameGrabberList();
        } else if ("deviceList".equalsIgnoreCase(key)) {
            return SharedFrameGrabber.instance.getDeviceList(propFrameGrabber);
        }
        return res;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        AstericsErrorHandling.instance.getLogger().fine("setRuntimePropertyValue: Setting " + propertyName + "=" + newValue);

        // Stop plugin first, because if the camera selection is changed we
        // would not know any more the previous camera to stop which would
        // then keep running.
        boolean wasRunning = running;
        if (wasRunning) {
            stop();
        }
        Object oldValue = newValue;
        if ("frameGrabber".equalsIgnoreCase(propertyName)) {
            oldValue = propFrameGrabber;
            propFrameGrabber = (String) newValue;
        } else if ("frameGrabberFormat".equalsIgnoreCase(propertyName)) {
            oldValue = propFrameGrabberFormat;
            propFrameGrabberFormat = (String) newValue;
        } else if ("cameraSelection".equalsIgnoreCase(propertyName)) {
            oldValue = propCameraSelection;
            propCameraSelection = (String) newValue;
            // map an eventual device nr to a real device key dependeing on the used frame grabber
            propCameraSelection = SharedFrameGrabber.instance.mapDeviceNrToDeviceKey(propCameraSelection, propFrameGrabber);
        } else if ("cameraResolution".equalsIgnoreCase(propertyName)) {
            oldValue = propCameraResolution;
            propCameraResolution = Integer.parseInt((String) newValue);
        } else if ("titleVideoFrameWindow".equalsIgnoreCase(propertyName)) {
            oldValue = propTitleVideoFrameWindow;
            propTitleVideoFrameWindow = (String) newValue;
        } else if ("displayGUI".equalsIgnoreCase(propertyName)) {
            oldValue = propDisplayGUI;

            if ("true".equalsIgnoreCase((String) newValue)) {
                propDisplayGUI = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propDisplayGUI = false;
            }
            return oldValue;
        } else if ("frameRate".equalsIgnoreCase(propertyName)) {
            oldValue = propFrameRate;
            propFrameRate = 0;
            try {
                propFrameRate = Integer.parseInt((String) newValue);
            } catch (NumberFormatException e) {
            }
        } else if ("enableOverlaySettings".equalsIgnoreCase(propertyName)) {
            oldValue = propEnableOverlaySettings;

            if ("true".equalsIgnoreCase((String) newValue)) {
                propEnableOverlaySettings = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propEnableOverlaySettings = false;
            }
            return oldValue;
        }

        if (wasRunning) {
            start();
        }

        return oldValue;
    }

    /**
     * returns an Event Listener Port
     * 
     * @param enventPortID
     *            the name of the event listener port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

        if ("init".equalsIgnoreCase(eventPortID)) {
            return initEvent;
        }
        if ("showCameraSettings".equalsIgnoreCase(eventPortID)) {
            return elpShowCameraSettings;
        }
        if ("saveProfile".equalsIgnoreCase(eventPortID)) {
            return elpSaveProfile;
        }
        return null;
    }

    /**
     * Event Listener Port for face position initialisation.
     */
    final IRuntimeEventListenerPort initEvent = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            needToInit = true;
            initCycles = 0;
        }
    };

    /**
     * Event Listener Port for Camera Settings Window.
     */
    final IRuntimeEventListenerPort elpShowCameraSettings = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            AstericsErrorHandling.instance.reportDebugInfo(XFacetrackerLKInstance.this, "Opening camera settings");
            // Stop plugin first, because if the camera selection is changed we
            // would not know any more the previous camera to stop which would
            // then keep running.
            boolean wasRunning = running;
            if (wasRunning) {
                stop();
            }
            try {
                SharedFrameGrabber.instance.showCameraSettings(propCameraSelection);
                if (wasRunning) {
                    start();
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().warning(e.getMessage());
            }
        }
    };

    /**
     * Event Listener Port for save profile.
     */
    final IRuntimeEventListenerPort elpSaveProfile = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
        }
    };

    @Override
    public void start() {
        running = false;
        try {
            resetVariables();
            // Get default grabber for this platform (VideoInput for Windows,
            // FFmpeg for Linux, OpenCV for Mac...) using default camera (device 0)
            // mapping device nr to device key in case of FFmpeg frame grabber
            propCameraSelection = SharedFrameGrabber.instance.mapDeviceNrToDeviceKey(propCameraSelection, propFrameGrabber);
            FrameGrabber grabber = SharedFrameGrabber.instance.getFrameGrabber(propCameraSelection, propFrameGrabber, propCameraResolution,
                    propFrameGrabberFormat, propFrameRate);
            // register this as listener for grabbed images
            SharedFrameGrabber.instance.registerGrabbedImageListener(propCameraSelection, this);

            // Create a Canvas/Frame for showing the video frame.
            instanceId = DeploymentManager.instance.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(this);
            String title = instanceId;
            if (propTitleVideoFrameWindow != null && propTitleVideoFrameWindow != "") {
                title = propTitleVideoFrameWindow;
            }
            double camGamma = 1.0;
            if (!SharedFrameGrabber.IPCAMERA_GRABBER_KEY.equals(propFrameGrabber)) {
                camGamma = grabber.getGamma();
            }
            Point pos = AREServices.instance.getComponentPosition(this);
            Dimension d = AREServices.instance.getAvailableSpace(this);

            // only show video frames, if enabled
            if (propDisplayGUI) {
                SharedCanvasFrame.instance.createCanvasFrame(instanceId, title, camGamma, pos, d);
            }
            // start grabbing
            SharedFrameGrabber.instance.startGrabbing(propCameraSelection);
            running = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AstericsErrorHandling.instance.reportError(this, e.getMessage());
            stop();
        }
        super.start();
    }

    @Override
    public void pause() {
        super.pause();
        // Simply pause tracking by unregistering as frame listener
        SharedFrameGrabber.instance.deregisterGrabbedImageListener(propCameraSelection, this);
    }

    @Override
    public void resume() {
        super.resume();
        SharedFrameGrabber.instance.registerGrabbedImageListener(propCameraSelection, this);
    }

    @Override
    public void stop() {
        propCameraSelection = SharedFrameGrabber.instance.mapDeviceNrToDeviceKey(propCameraSelection, propFrameGrabber);
        SharedFrameGrabber.instance.stopGrabbing(propCameraSelection);
        SharedFrameGrabber.instance.deregisterGrabbedImageListener(propCameraSelection, this);
        SharedCanvasFrame.instance.disposeFrame(instanceId);
        resetVariables();
        super.stop();
        running = false;
        // System.out.println("Stopped XFaceTrackerLK, Executed in:
        // "+Thread.currentThread().getName());
    }

    /**
     * This method resets all variables to their initial values.
     */
    private void resetVariables() {
        imgGrey[A] = null;
        imgGrey[B] = null;
        imgPyr[A] = null;
        imgPyr[B] = null;
        points[A] = null;
        points[B] = null;

        nrPoints = new IntPointer(1).put(0);
        roiRect = null;
        faceRect = null;
        flags = 0;

        frameCount = 0;
        frameCountStart = 0;
        initCycles = 0;
        realFrameRate = 0;
    }

    /**
     * Initialize some variables for tracking like empty IplImage objects.
     * 
     * @param img
     */
    private void initTracker(IplImage img) {
        cvFlip(img, img, 1);

        int width = img.width();
        int height = img.height();
        roiRect = cvRect(0, 0, width, height);
        imgGrey[A] = AbstractIplImage.create(width, height, IPL_DEPTH_8U, 1);
        cvCvtColor(img, imgGrey[A], CV_BGR2GRAY);
        flags = 0;

        CvSize pyr_sz = cvSize(imgGrey[A].width(), imgGrey[A].height());

        imgPyr[A] = AbstractIplImage.create(pyr_sz, IPL_DEPTH_8U, 1);
        imgPyr[B] = AbstractIplImage.create(pyr_sz, IPL_DEPTH_8U, 1);
        points[A] = null;
    }

    /**
     * Callback called when a new frame was grabbed. Does the actual tracking.
     */
    @Override
    public void imageGrabbed(IplImage img) {
        // System.out.println(".");
        // if this is the first frame, init tracker
        if (imgGrey[A] == null) {
            initTracker(img);
            return;
        }

        // flip image, otherwise it would look mirrored.
        cvFlip(img, img, 1);

        imgGrey[B] = AbstractIplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        // use a grey scale image (improves tracking quality)
        cvCvtColor(img, imgGrey[B], CV_BGR2GRAY);
        cvSetImageROI(imgGrey[B], roiRect);

        // If time passed more than 800ms, reset initCycles variable
        // --> so a subsequent face detection will be done immediately
        if (System.currentTimeMillis() % 1000 > 800) {
            initCycles = 0;
        }

        // if points[A]==null no face was found so far
        // As face detection produces a high CPU load, limit detection run to 3 times for a certain amount of time.
        if ((points[A] == null || needToInit) && initCycles <= 3) {
            initCycles++;
            cvResetImageROI(imgGrey[A]);
            points[A] = findFeatures(imgGrey[A]);
            faceDetection.drawFaceRect(faceRect, img);
        }
        // if a face was detected use the optical flow algorithm to track the movement of the found points (nose, chin)
        if (points[A] != null && nrPoints.get() > 0) {
            cvSetImageROI(imgGrey[A], roiRect);
            cvSetImageROI(imgGrey[B], roiRect);
            cvSetImageROI(imgPyr[A], roiRect);
            cvSetImageROI(imgPyr[B], roiRect);
            // cvSetImageROI(img,roiRect);

            points[B] = trackOpticalFlow(imgGrey, imgPyr, points[A]);
            if (!needToInit) {
                drawPoints(img, points[B]);
                // faceDetection.drawFaceRect(faceRect, img);
                sendPortData(points);

                points[A] = points[B];
                flags |= CV_LKFLOW_PYR_A_READY;
            }
        }

        //Only show fps and device name if enabled by the property.
        if (propEnableOverlaySettings) {
            // Count frames after tracking to reflect real frame rate including tracking.
            frameCount++;
            long now = System.currentTimeMillis();
            if (frameCountStart == 0) {
                frameCountStart = now;
            }
            long duration = now - frameCountStart;
            if (duration > 1000) {
                realFrameRate = frameCount / (duration / 1000);
                frameCount = 0;
                frameCountStart = now;
            }
            cvPutText(img, "Dev: " + propCameraSelection, new int[] { 20, 20 }, overlayTextFont, CvScalar.BLACK);
            cvPutText(img, "FPS: " + realFrameRate, new int[] { 20, 50 }, overlayTextFont, CvScalar.BLACK);
        }

        // show the image in the canvas including the tracked points and the overlay text.
        SharedCanvasFrame.instance.showImage(instanceId, img);

        imgGrey[A] = imgGrey[B];
        imgPyr[A] = imgPyr[B];

    }

    /**
     * Sends out the tracked relative changes of head movements to the output ports.
     * 
     * @param points
     */
    private void sendPortData(CvPoint2D32f[] points) {
        // send coordinates to output ports
        if (needToInit) {
            return;
        }

        int relX = Math.round((points[B].x() - points[A].x()) * GAIN);
        int relY = Math.round((points[B].y() - points[A].y()) * GAIN);
        ;
        // System.out.println("[" + relX + ", " + relY + "]");
        opNoseX.sendData(ConversionUtils.intToBytes(relX));
        opNoseY.sendData(ConversionUtils.intToBytes(relY));

        points[B].position(1);
        points[A].position(1);

        relX = Math.round((points[B].x() - points[A].x()) * GAIN);
        relY = Math.round((points[B].y() - points[A].y()) * GAIN);
        ;
        // System.out.println("[" + relX + ", " + relY + "]");
        opChinX.sendData(ConversionUtils.intToBytes(relX));
        opChinY.sendData(ConversionUtils.intToBytes(relY));

        points[B].position(0);
        points[A].position(0);
    }

    /**
     * Executes the OpenCV optical flow functions for the given images.
     * 
     * @param imgGrey
     * @param imgPyr
     * @param pointsA
     * @return
     */
    public CvPoint2D32f trackOpticalFlow(IplImage[] imgGrey, IplImage[] imgPyr, CvPoint2D32f pointsA) {
        // Call Lucas Kanade algorithm
        BytePointer features_found = new BytePointer(MAX_POINTS);
        FloatPointer feature_errors = new FloatPointer(MAX_POINTS);

        CvPoint2D32f pointsB = new CvPoint2D32f(MAX_POINTS);
        cvCalcOpticalFlowPyrLK(imgGrey[A], imgGrey[B], imgPyr[A], imgPyr[B], pointsA, pointsB, nrPoints.get(), cvSize(winSize, winSize), 5, features_found,
                feature_errors, cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3), flags);

        return rejectBadPoints(pointsA, pointsB, features_found, feature_errors);
    }

    /**
     * Does semantic checks on the tracked points to find out drifts of tracking or really absolutely bad points which can't be within the face. If any bad
     * point was found, needToInit=true. This reinitializes the tracking.
     * 
     * @param pointsA
     * @param pointsB
     * @param features_found
     * @param feature_errors
     * @return
     */
    private CvPoint2D32f rejectBadPoints(CvPoint2D32f pointsA, CvPoint2D32f pointsB, BytePointer features_found, FloatPointer feature_errors) {
        needToInit = false;
        // Make an image of the results
        for (int i = 0; i < nrPoints.get(); i++) {
            if (features_found.get(i) == 0 || feature_errors.get(i) > 550) {
                System.out.println("Error is " + feature_errors.get(i) + "/n");
                needToInit = true;
                initCycles = 0;
                return pointsB;
            }

            pointsA.position(i);
            pointsB.position(i);

            // Ignore points lying outside ROI, actually we should try to use
            // cvSetImageROI
            if (!(validRect.x() <= pointsB.x() && pointsB.x() <= (validRect.x() + validRect.width()))
                    || !(validRect.y() <= pointsB.y() && pointsB.y() <= (validRect.y() + validRect.height()))) {
                System.out.println("out of roi: " + validRect);
                needToInit = true;
                initCycles = 0;
                return pointsB;
            }

            // Reject relative individual point displacements > tresh with base
            // faceRect.width for x rel displacements and faceRect.height for y
            // rel displacements
            double dx = pointsB.x() - pointsA.x();
            double dy = pointsB.y() - pointsA.y();
            double relDispTresh = 0.15;
            if (dx / faceRect.width() > relDispTresh || dy / faceRect.height() > relDispTresh) {
                System.out.println(dx / faceRect.width() + " || " + dy / faceRect.height() + " > " + relDispTresh);
                needToInit = true;
                initCycles = 0;
                return pointsB;
            }

        }
        // Reject distchin2nosetip within range (0.9 < distchin2nosetip < 1.2)??
        double relDistChin2Nosetip = magnitude(pointsB, CHIN_L, NOSE_TIP) / initDistChin2Nosetip;
        // double
        // relDistNoseroot2Nosetip=magnitude(pointsB,NOSE_ROOT,NOSE_TIP)/initDistNoseroot2Nosetip;
        double lowerDistChin2NosetipTresh = 0.4;
        double upperDistChin2NosetipTresh = 1.8;
        if (!(lowerDistChin2NosetipTresh <= relDistChin2Nosetip && relDistChin2Nosetip <= upperDistChin2NosetipTresh)) {
            System.out.println("chin y-disp out of range: " + lowerDistChin2NosetipTresh + " <= " + relDistChin2Nosetip + " <= " + upperDistChin2NosetipTresh);
            needToInit = true;
            initCycles = 0;
            return pointsB;
        }

        // Reject chin2nosetip angle > 25
        // double angleNoseroot2Nosetip=angleVertical(pointsB, NOSE_ROOT,
        // NOSE_TIP);
        // angle of Chin2Nosetip, mutliply by -1 to have same sign as
        // angleNoseroot2Nosetip
        // double angleChin2Nosetip=angleVertical(pointsB, CHIN_L, NOSE_TIP)*-1;
        double angleChin2Nosetip = angleVertical(pointsB, CHIN_L, NOSE_TIP);
        // System.out.println("angle chin2nosetip: "+angleChin2Nosetip);
        double angleTolerance = 45;
        if (!((-1 * angleTolerance) <= angleChin2Nosetip && angleChin2Nosetip <= angleTolerance)) {
            System.out.println("angle chin2nosetip out of range: " + (-1 * angleTolerance) + " <= " + angleChin2Nosetip + " <= " + angleTolerance);
            needToInit = true;
            initCycles = 0;
            return pointsB;
        }

        /*
         * //Reject nosetip2noseroot distance double lowerDistNoseroot2NosetipTresh=0.75; double upperDistNoseroot2NosetipTresh=1.15;
         * if(!(lowerDistNoseroot2NosetipTresh <= relDistNoseroot2Nosetip && relDistNoseroot2Nosetip <= upperDistNoseroot2NosetipTresh)) {
         * System.out.println("noseroot y-disp out of range: " +lowerDistNoseroot2NosetipTresh +" <= "+relDistNoseroot2Nosetip+
         * " <= "+upperDistNoseroot2NosetipTresh); needToInit=true; return pointsB; }
         * 
         * //Reject nosetip2noseroot angle != chin2nosetip angle double angleNoseroot2Nosetip=angleVertical(pointsB, NOSE_ROOT, NOSE_TIP); //angle of
         * Chin2Nosetip, mutliply by -1 to have same sign as angleNoseroot2Nosetip double angleChin2Nosetip=angleVertical(pointsB, CHIN_L, NOSE_TIP)*-1; double
         * angleTolerance=25; System.out.println( "angle chin2nosetip "+(angleChin2Nosetip)+ ", angle noseroot2nosetip: "+angleNoseroot2Nosetip);
         * if(!((angleNoseroot2Nosetip-angleTolerance) <= angleChin2Nosetip && angleChin2Nosetip <= (angleNoseroot2Nosetip+angleTolerance))) {
         * //System.out.println("angle chin2nosetip out of range: " +(angleNoseroot2Nosetip-angleTolerance) +" <= "+angleChin2Nosetip+
         * " <= "+(angleNoseroot2Nosetip+angleTolerance)); needToInit=true; return pointsB; }
         */

        pointsA.position(0);
        pointsB.position(0);

        return pointsB;
    }

    /**
     * Calculates the magnitude (euclidean distance) of 2 points.
     * 
     * @param p
     * @param i1
     * @param i2
     * @return
     */
    private double magnitude(CvPoint2D32f p, int i1, int i2) {
        // System.out.println("got: "+p);
        double dx = p.position(i1).x() - p.position(i2).x();
        double dy = p.position(i1).y() - p.position(i2).y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the vertical angle (the angle to a vertical line) of the given points.
     * 
     * @param p
     * @param i1
     * @param i2
     * @return
     */
    private double angleVertical(CvPoint2D32f p, int i1, int i2) {
        // System.out.println("got: "+p);
        double dx = p.position(i1).x() - p.position(i2).x();
        double dy = p.position(i1).y() - p.position(i2).y();
        return Math.atan(dx / dy) * 180 / Math.PI;
    }

    private CvPoint2D32f avgPoint2D32f(CvPoint2D32f p, int from, int to) {
        double l = to - from + 1;
        CvPoint2D32f avg = new CvPoint2D32f(1);
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i <= l; i++) {
            x += p.position(i).x();
            y += p.position(i).y();
        }
        avg.put((double) x / l, (double) y / l);
        return avg;
    }

    /**
     * Draws the given points to the given image.
     * 
     * @param img
     * @param corners
     */
    public void drawPoints(IplImage img, CvPoint2D32f corners) {
        for (int i = 0; i < nrPoints.get(); i++) {
            corners.position(i);
            CvPoint p = cvPoint(Math.round(corners.x()), Math.round(corners.y()));
            // System.out.println("p"+i+": " + p);

            // Draws a circle and selects one of 4 colors (one for each point).
            // If there are more than 4 points, color 1 is chosen again.
            cvCircle(img, p, 4, COLORS[i % COLORS.length], 2, 5, 0);
        }
        corners.position(0);
    }

    /**
     * Detects the face in the image an returns the points of the nose and the chin.
     * 
     * @param imgA
     * @return
     */
    public CvPoint2D32f findFeatures(IplImage imgA) {

        try {
            faceRect = faceDetection.detectFace(imgA);
            if (faceRect != null) {
                System.out.println("\nFound face at: " + faceRect);

                // roiRect=cvRect(faceRect.x()-faceRect.width()/2,faceRect.y()-faceRect.height()/2,2*faceRect.width(),2*faceRect.height());
                int roiFact = 4;
                validRect = cvRect(faceRect.x() - faceRect.width() / roiFact, faceRect.y() - faceRect.height() / roiFact,
                        faceRect.width() + 2 * faceRect.width() / roiFact, faceRect.height() + 2 * faceRect.height() / roiFact);

                int x = faceRect.x() + faceRect.width() / 2;
                int y = faceRect.y();
                // CvPoint initNose[]=new
                // CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.6)),cvPoint(x,y+(int)(faceRect.height()*0.45))};
                // CvPoint initNose[]=new
                // CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.6)),cvPoint(x,y+(int)(faceRect.height()*0.45))};
                CvPoint initNose[] = new CvPoint[] { cvPoint(x, y + (int) (faceRect.height() * 0.6)) };

                // CvPoint initChin[]=new
                // CvPoint[]{cvPoint(x,y+(int)(faceRect.height()*0.95)),cvPoint(x,y+(int)(faceRect.height()*0.9)),cvPoint(x-7,y+(int)(faceRect.height()*0.95)),cvPoint(x+7,y+(int)(faceRect.height()*0.95))};
                // CvPoint initChin[]=new
                // CvPoint[]{cvPoint(x-7,y+(int)(faceRect.height()*0.95)),cvPoint(x+7,y+(int)(faceRect.height()*0.95))};
                CvPoint initChin[] = new CvPoint[] { cvPoint(x, y + (int) (faceRect.height() * 0.97)) };

                CvPoint2D32f pointsA = new CvPoint2D32f(MAX_POINTS);
                pointsA = addCvPoints(pointsA, initNose);
                pointsA = addCvPoints(pointsA, initChin);
                nrPoints.put((int) pointsA.position());
                pointsA.position(0);

                // Setting image ROI should improve tracking quality, but
                // disabled it so far
                cvSetImageROI(imgA, roiRect);
                // cvSetImageROI(imgA,faceRect);
                // cvSetImageROI(imgA,faceRect);

                // Uses given points and tries to find better trackable ones in
                // the neighbourhood. cvTermCriteria is set to 1, 1 because otherwise the new
                // points would be too far away.
                cvFindCornerSubPix(imgA, pointsA, nrPoints.get(), cvSize(winSize, winSize), cvSize(-1, -1),
                        // cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS,
                        // 20, 0.03));
                        cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 1, 1));

                // cvResetImageROI(imgA);

                System.out.println("Found trackable points: pointsA: " + pointsA);
                initDistChin2Nosetip = magnitude(pointsA, CHIN_L, NOSE_TIP);
                // initDistNoseroot2Nosetip=magnitude(pointsA, NOSE_ROOT,
                // NOSE_TIP);

                return pointsA;
            }
        } catch (java.lang.Exception e) {
            // TODO Auto-generated catch block
            AstericsErrorHandling.instance.getLogger().fine("Face detection failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds the given points array to the CvPoint2D32f object.
     * 
     * @param corners
     * @param points
     * @return
     */
    private CvPoint2D32f addCvPoints(CvPoint2D32f corners, CvPoint[] points) {
        for (CvPoint point : points) {
            corners.put(point);
            corners.position(corners.position() + 1);
        }
        return corners;
    }
}
