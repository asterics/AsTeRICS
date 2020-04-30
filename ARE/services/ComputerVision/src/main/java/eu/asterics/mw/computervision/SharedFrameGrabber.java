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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.utils.OSUtils;

/**
 * Contains code to unify/simplify enumeration/initialization/opening/closing of camera devices and frame grabbing. Also supports publish/subscribe mechanism
 * for grabbed frames. It is planned to also support shared camera usage for several plugins.
 * 
 * @author mad
 *
 */
public class SharedFrameGrabber {
    public static final int DEFAULT_FRAME_RATE = 15;
    private static final int DEFAULT_GRABBER_KEY_INDEX = 1;
    public static final String DEFAULT_GRABBER_KEY = "Default";
    public static final String OPENCV_GRABBER_KEY = "OpenCV";
    public static final String VIDEOINPUT_GRABBER_KEY = "VideoInput";
    public static final String OPENKINECT_GRABBER_KEY = "OpenKinect";
    public static final String OPENKINECT2_GRABBER_KEY = "OpenKinect2";
    public static final String REALSENSE_GRABBER_KEY = "RealSense";
    public static final String IPCAMERA_GRABBER_KEY = "IPCamera";
    public static final String PS3Eye_GRABBER_KEY = "PS3Eye";
    public static final String FFMPEG_GRABBER_KEY = "FFmpeg";
    public static final String DC1394_GRABBER_KEY = "DC1394";
    public static final String FLYCAPTURE_GRABBER_KEY = "FlyCapture";
    public static final String FLYCAPTURE2_GRABBER_KEY = "FlyCapture2";

    // timeout for grabber thread to die in ms.
    private static final long GRABBER_STOP_TIMEOUT = 10000;
    public static SharedFrameGrabber instance = new SharedFrameGrabber();

    // maps for mapping between deviceKey and frame grabber, image listener and grabber threads.
    private Map<String, FrameGrabber> device2FrameGrabber = new HashMap<String, FrameGrabber>();
    private Map<String, List<GrabbedImageListener>> listeners = new HashMap<String, List<GrabbedImageListener>>();
    private Map<String, GrabberThread> grabberThreads = new HashMap<String, GrabberThread>();

    private List<String> grabberList = null;
    // Defines preferred order of framegrabbers to use
    private Map<String, String[]> defaultGrabberList = new HashMap<String, String[]>();

    private int[][] RESOLUTIONS = new int[][] { { 160, 120 }, { 320, 240 }, { 352, 288 }, { 640, 480 }, { 800, 600 }, { 1024, 768 }, { 1600, 1200 } };

    Logger logger = AstericsErrorHandling.instance.getLogger();

    public SharedFrameGrabber() {
        // The framegrabbers are checked in the list of available ones.
        defaultGrabberList.put(OSUtils.WINDOWS, new String[] { DEFAULT_GRABBER_KEY, VIDEOINPUT_GRABBER_KEY, OPENCV_GRABBER_KEY, OPENKINECT_GRABBER_KEY,
                REALSENSE_GRABBER_KEY, PS3Eye_GRABBER_KEY, DC1394_GRABBER_KEY, FLYCAPTURE_GRABBER_KEY, FLYCAPTURE2_GRABBER_KEY, IPCAMERA_GRABBER_KEY });
        defaultGrabberList.put(OSUtils.LINUX, new String[] { DEFAULT_GRABBER_KEY, OPENCV_GRABBER_KEY, FFMPEG_GRABBER_KEY, OPENKINECT_GRABBER_KEY,
                REALSENSE_GRABBER_KEY, PS3Eye_GRABBER_KEY, DC1394_GRABBER_KEY, FLYCAPTURE_GRABBER_KEY, FLYCAPTURE2_GRABBER_KEY, IPCAMERA_GRABBER_KEY });
        defaultGrabberList.put(OSUtils.MACOSX, new String[] { DEFAULT_GRABBER_KEY, OPENCV_GRABBER_KEY, OPENKINECT_GRABBER_KEY, REALSENSE_GRABBER_KEY,
                PS3Eye_GRABBER_KEY, DC1394_GRABBER_KEY, FLYCAPTURE_GRABBER_KEY, FLYCAPTURE2_GRABBER_KEY, IPCAMERA_GRABBER_KEY });

    }

    /**
     * This method is used to actually create and initialize a frame grabber with the given parameters. Also sanity checks are done to prevent crashes of native
     * libs.
     * 
     * @param grabberName
     * @param deviceKey
     * @param userWidth
     * @param userHeight
     * @param grabberFormat
     * @param frameRate
     *            TODO
     * @throws Exception
     */
    private void init(String grabberName, String deviceKey, int userWidth, int userHeight, String grabberFormat, int frameRate) throws Exception {
        FrameGrabber grabber = null;

        // on RPi we did not need it, but on windows we needed to set it, but maybe it is different with the new version of ffmpeg.
        if (OSUtils.isWindows() && FFMPEG_GRABBER_KEY.equalsIgnoreCase(grabberName) && (grabberFormat == null || "".equals(grabberFormat))) {
            grabberFormat = "dshow";
        }

        logger.info("Available grabber: " + Arrays.toString(defaultGrabberList.get(OSUtils.getOsName())));
        logger.info("Default FrameGrabber: " + getDefaultFrameGrabberName());
        logger.info("FrameGrabber: " + grabberName);
        logger.info("DeviceKey: " + deviceKey);
        logger.info("Resolution: " + userWidth + "x" + userHeight);
        logger.info("grabberFormat: " + grabberFormat);

        if (device2FrameGrabber.containsKey(deviceKey)) {
            logger.fine("Removing old FrameGrabber with key <" + deviceKey + ">");
            stopGrabbing(deviceKey);
            FrameGrabber _grabber = device2FrameGrabber.get(deviceKey);
            _grabber.release();
            device2FrameGrabber.remove(deviceKey);
        }
        // The available FrameGrabber classes include OpenCVFrameGrabber
        // (opencv_highgui),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.

        // Set default grabber key, if the given one is null or empty or has the value of DEFAULT_GRABBER_KEY
        grabberName = getDefaultFrameGrabberName(grabberName);
        logger.info("Using FrameGrabber: " + grabberName);

        // System.out.println("Using grabber: "+grabberName+", and camIdx:
        // "+camIdx);
        // grabber = FrameGrabber.create(grabberName,camIdx);

        CameraDevice.Settings devSet = new CameraDevice.SettingsImplementation();

        // Check if it is a device nr or a device path
        boolean isDevNr = false;
        int camIdx = 0;
        try {
            camIdx = Integer.parseInt(deviceKey);
            isDevNr = true;
        } catch (NumberFormatException ne) {
        }

        doSanityChecks(grabberName, deviceKey);

        if (isDevNr) {
            logger.info("Setting deviceNr <" + camIdx + ">");
            devSet.setDeviceNumber(camIdx);
        } else if (new File(deviceKey).exists()) {
            logger.info("Setting device filename <" + deviceKey + ">");
            devSet.setDeviceFilename(deviceKey);
        } else {
            logger.info("Setting device path <" + deviceKey + ">");
            devSet.setDevicePath(deviceKey);
        }

        devSet.setName(deviceKey);
        devSet.setFrameGrabber(FrameGrabber.get(grabberName));
        // devSet.setTimeout(20000);
        if (!grabberFormat.equals("mjpeg")) {
            devSet.setImageWidth(userWidth);
            devSet.setImageHeight(userHeight);
        }
        devSet.setFormat(grabberFormat);
        devSet.getDescription();
        System.out.println(devSet.getDescription());

        CameraDevice dev = new CameraDevice(devSet);
        grabber = dev.createFrameGrabber();
        // enable setting framerate
        if (frameRate > 0) {
            logger.info("Setting frame rate to " + frameRate);
            grabber.setFrameRate(frameRate);
        }

        // FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("video=Integrated
        // Camera");
        // grabber.setFormat("dshow");
        // FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("0");
        // grabber.setFormat("vfwcap");

        AstericsErrorHandling.instance.getLogger().fine("Adding FrameGrabber with key <" + deviceKey + ">, grabber <" + grabber + ">");
        device2FrameGrabber.put(deviceKey, grabber);
    }

    /**
     * Do sanity checks to prevent a crash of the selected frame grabber. On Linux this checks for existence of the video device which should be used for the
     * OpenCVFrameGrabber.
     * 
     * @param grabberName
     * @param deviceKey
     * @throws Exception
     */
    private void doSanityChecks(String grabberName, String deviceKey) throws Exception {
        // Some dirty checks to prevent a crash of OpenCV on Linux if the device
        // does not exist.
        if (grabberName.equalsIgnoreCase(OPENCV_GRABBER_KEY) && (OSUtils.isUnix())) {
            try {
                Integer.parseInt(deviceKey);
                File vidFile = new File("/dev/video" + deviceKey);
                if (!vidFile.exists()) {
                    throw new Exception("Cannot create FrameGrabber <" + grabberName + "> for device <" + deviceKey + ">");
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * Maps a given device number to a device path, if possible and dependent on the given frame grabber. e.g. if grabberName == {@link #FFMPEG_GRABBER_KEY} map
     * the device number to /dev/video<deviceNr>. The caller is responsible to use this newly generated deviceKey for all subsequent calls of methods from
     * {@link SharedFrameGrabber}.
     * 
     * @param deviceKey
     * @param grabberName
     * @return
     */
    public String mapDeviceNrToDeviceKey(String deviceKey, String grabberName) {
        // map the DEFAULT framegrabber value to a real framegrabber.
        grabberName = getDefaultFrameGrabberName(grabberName);

        boolean isDevNr = false;
        int camIdx = 0;
        try {
            camIdx = Integer.parseInt(deviceKey);
            isDevNr = true;
        } catch (NumberFormatException ne) {
        }

        // Generally the ffmpeg grabber does not support device numbers but device paths.
        // On Linux ffmpeg is our default choice, so if the user only entered a devNr, map it to
        // standard device paths with /dev/video<devNr>
        if (OSUtils.isUnix() && FFMPEG_GRABBER_KEY.equals(grabberName) && isDevNr) {
            deviceKey = "/dev/video" + camIdx;
            logger.info("Mapping camIdx <" + camIdx + "> to device path: " + deviceKey);
        }
        return deviceKey;
    }

    /**
     * Returns a list of available devices, when using the given grabber.
     * 
     * @param grabberName
     * @return
     */
    public List<String> getDeviceList(String grabberName) {
        String[] s = null;
        try {
            logger.fine("Retrieving device list");
            Class<? extends FrameGrabber> c = FrameGrabber.get(getDefaultFrameGrabberName(grabberName));
            c.getMethod("tryLoad").invoke(null);
            try {
                s = (String[]) c.getMethod("getDeviceDescriptions").invoke(null);
                if (s != null) {
                    return Arrays.asList(s);
                }
            } catch (Throwable t) {
            }
        } catch (Throwable t) {
        }
        return Arrays.asList(new String[] { "Not available" });
    }

    /**
     * Returns a list of theoretically available frame grabbers for the current platform depending on {@link OSUtils#getOsName(). The frame grabber are not
     * actually tested if they can be loaded on the platform, because this could sometimes lead to a crash of the ARE.
     * 
     * @return
     */
    public List<String> getFrameGrabberList() {
        if (grabberList == null) {
            logger.fine("Creating list of available frame grabbers on this platform");
            grabberList = new ArrayList<String>();
            for (String grabberName : defaultGrabberList.get(OSUtils.getOsName())) {
                try {
                    if (grabberName.equals(DEFAULT_GRABBER_KEY)) {
                        grabberList.add(DEFAULT_GRABBER_KEY);
                        continue;
                    }
                    Class<? extends FrameGrabber> c = FrameGrabber.get(grabberName);

                    if (c != null) {
                        // Don't do testing of grabber loading now, because it can be very time-consuming, and sometimes if the library is buggy it can
                        // crash the ARE.
                        /*
                         * System.out.println("\n\n++++++++++Trying to load " + grabberName + "++++++\n\n"); c.getMethod("tryLoad").invoke(null); boolean
                         * mayContainCameras = false; try { String[] s = (String[]) c.getMethod("getDeviceDescriptions").invoke(null); if (s.length > 0) {
                         * mayContainCameras = true; } } catch (Throwable t) { if (t.getCause() instanceof UnsupportedOperationException) { mayContainCameras =
                         * true; } } if (!mayContainCameras) { continue; }
                         */
                        grabberList.add(grabberName);
                    }
                } catch (Throwable t) {
                }
            }
            if (!grabberList.contains(IPCAMERA_GRABBER_KEY)) {
                grabberList.add(IPCAMERA_GRABBER_KEY);
            }
        }
        return grabberList;
    }

    /**
     * Returns the name of the default grabber key for this platform depending on {@link OSUtils#getOsName().
     * 
     * @return
     */
    public String getDefaultFrameGrabberName() {
        return getDefaultFrameGrabberName(null);
    }

    /**
     * Returns the name of the default grabber key for this platform depending on {@link OSUtils#getOsName(), if grabberKey is null or "" or
     * {@link #DEFAULT_GRABBER_KEY}.
     * 
     * @param grabberKey
     * @return
     */
    public String getDefaultFrameGrabberName(String grabberKey) {
        if (grabberKey == null || "".equals(grabberKey) || DEFAULT_GRABBER_KEY.equals(grabberKey)) {
            // Return the first grabber key after the DEFAULT_GRABBER_KEY
            return defaultGrabberList.get(OSUtils.getOsName())[DEFAULT_GRABBER_KEY_INDEX];
        }
        return grabberKey;
    }

    /**
     * Shows camera settings by using VideoInput showCameraSettings method. This method is only available on Windows when using the VideoInput frame grabber.
     * 
     * @param deviceKey
     * @throws Exception 
     */
    public void showCameraSettings(String deviceKey) throws Exception {
        throw new RuntimeException("The showSettings method is not supported by now.");

        /*
        int camIdx=-1;
        try {
            camIdx = Integer.parseInt(deviceKey);
        } catch (NumberFormatException ne) {
            logger.warning("showCameraSettings failed: Could not parse camera index of device key: "+deviceKey);
            return;
        }
        
        stopGrabbing(deviceKey);
        
        videoInput vi = new videoInput();
        try {
            // this is very ugly because we open the device twice, hopefully no
            // crash
            int w = 320;
            int h = 240;

            logger.fine("Showing camera settings for device: " + deviceKey);
            vi.setupDevice(camIdx, w, h);
            vi.showSettingsWindow(camIdx);

            IplImage bgrImage = null;
            BytePointer bgrImageData = null;
            SharedCanvasFrame.instance.createCanvasFrame("showCameraSettings", "Preview Camera Settings", 1, new Point(0, 0), new Dimension(200, 200));

            logger.fine("Showing camera preview for 500 frames");
            for (int i = 0; i < 500; i++) {
                if (bgrImage == null || bgrImage.width() != w || bgrImage.height() != h) {
                    bgrImage = AbstractIplImage.create(w, h, IPL_DEPTH_8U, 3);
                    bgrImageData = bgrImage.imageData();
                }

                if (!vi.getPixels(camIdx, bgrImageData, false, true)) {
                    SharedCanvasFrame.instance.disposeFrame("showCameraSettings");
                    throw new Exception("videoInput.getPixels() Error: Could not get pixels.");
                }
                SharedCanvasFrame.instance.showImage("showCameraSettings", bgrImage);
            }
        }catch (Exception e) {
            logger.fine("Could not show camera settings, reason: "+e.getMessage());
            throw e;
        } finally {
            if(vi!=null) {
                vi.stopDevice(camIdx);
                vi=null;                
            }
            SharedCanvasFrame.instance.disposeFrame("showCameraSettings");
        }
        */
    }

    /**
     * Returns the FrameGrabber instance for the given parameters.
     * 
     * @param deviceKey
     * @param grabberName
     * @param resolutionIdx
     * @param grabberFormat
     * @param frameRate
     * @return
     * @throws Exception
     */
    public FrameGrabber getFrameGrabber(String deviceKey, String grabberName, int resolutionIdx, String grabberFormat, int frameRate) throws Exception {
        if (device2FrameGrabber.containsKey(deviceKey)) {
            return device2FrameGrabber.get(deviceKey);
        }

        init(grabberName, deviceKey, RESOLUTIONS[resolutionIdx][0], RESOLUTIONS[resolutionIdx][1], grabberFormat, frameRate);
        return device2FrameGrabber.get(deviceKey);
    }

    /**
     * Returns the FrameGrabber instance for the given parameters.
     * 
     * @param deviceKey
     * @param grabberName
     * @param resolutionIdx
     * @param grabberFormat
     * @return
     * @throws Exception
     */
    public FrameGrabber getFrameGrabber(String deviceKey, String grabberName, int resolutionIdx, String grabberFormat) throws Exception {
        if (device2FrameGrabber.containsKey(deviceKey)) {
            return device2FrameGrabber.get(deviceKey);
        }

        init(grabberName, deviceKey, RESOLUTIONS[resolutionIdx][0], RESOLUTIONS[resolutionIdx][1], grabberFormat, 0);
        return device2FrameGrabber.get(deviceKey);
    }

    /**
     * Returns the FrameGrabber instance for the given parameters.
     * 
     * @param deviceKey
     * @param grabberName
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    public FrameGrabber getFrameGrabber(String deviceKey, String grabberName, int width, int height) throws Exception {
        if (device2FrameGrabber.containsKey(deviceKey)) {
            return device2FrameGrabber.get(deviceKey);
        }

        init(grabberName, deviceKey, width, height, "", 0);
        return device2FrameGrabber.get(deviceKey);
    }

    /**
     * Returns the FrameGrabber instance for the given parameters.
     * 
     * @param deviceKey
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    public FrameGrabber getFrameGrabber(String deviceKey, int width, int height) throws Exception {
        if (device2FrameGrabber.containsKey(deviceKey)) {
            return device2FrameGrabber.get(deviceKey);
        }

        init(null, deviceKey, width, height, "", 0);
        return device2FrameGrabber.get(deviceKey);
    }

    /**
     * Returns the FrameGrabber instance for the given parameters.
     * 
     * @param deviceKey
     * @return
     * @throws Exception
     */
    public FrameGrabber getFrameGrabber(String deviceKey) throws Exception {
        if (device2FrameGrabber.containsKey(deviceKey)) {
            return device2FrameGrabber.get(deviceKey);
        }

        init(null, deviceKey, 320, 240, "", 0);
        return device2FrameGrabber.get(deviceKey);
    }

    /**
     * Returns the default FrameGrabber for the first device.
     * 
     * @return
     * @throws Exception
     */
    public FrameGrabber getDefaultFrameGrabber() throws Exception {
        return getFrameGrabber("0");
    }

    /**
     * Registers the given {@link GrabbedImageListener} for the given deviceKey to listen for grabbed frames. If grabbing was started with
     * {@link #startGrabbing(String)}, the listener gets notified with each new frame.
     * 
     * @param deviceKey
     * @param listener
     */
    public void registerGrabbedImageListener(String deviceKey, GrabbedImageListener listener) {
        List<GrabbedImageListener> deviceListeners = listeners.get(deviceKey);
        if (deviceListeners == null) {
            deviceListeners = new ArrayList<GrabbedImageListener>();
        }
        deviceListeners.add(listener);
        listeners.put(deviceKey, deviceListeners);
        logger.fine("After registering: Registered grabbing listeners: " + deviceListeners);
    }

    /**
     * Unregisters the given {@link GrabbedImageListener} for the given deviceKey. If no listener is registered for the given deviceKey any more, the grabber
     * thread is stopped and the associated grabber instance is stopped and released.
     * 
     * @param deviceKey
     * @param listener
     */
    public void deregisterGrabbedImageListener(String deviceKey, GrabbedImageListener listener) {
        List<GrabbedImageListener> deviceListeners = listeners.get(deviceKey);
        if (deviceListeners != null) {
            deviceListeners.remove(listener);
        }
        if (deviceListeners == null || (deviceListeners != null && deviceListeners.isEmpty())) {
            // if there are no listeners, stop and remove grabber again.
            stopGrabbing(deviceKey);
            FrameGrabber grabber = device2FrameGrabber.get(deviceKey);
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                    logger.fine("frame grabber for device <" + deviceKey + "> stopped and released.");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    device2FrameGrabber.remove(deviceKey);
                }
            }
        }
        logger.fine("After deregistering: Registered grabbing listeners: " + deviceListeners);
    }

    /**
     * Starts grabbing for the device with the given deviceKey.
     * 
     * @param deviceKey
     */
    public void startGrabbing(final String deviceKey) {
        GrabberThread grabberThread = grabberThreads.get(deviceKey);
        if (grabberThread == null) {
            grabberThread = new GrabberThread(deviceKey);
            grabberThreads.put(deviceKey, grabberThread);
            grabberThread.start();
        }
    }

    /**
     * Stops grabbing for the device with the given deviceKey.
     * 
     * @param deviceKey
     */
    public void stopGrabbing(String deviceKey) {
        logger.fine("Stop grabbing for devicekey <" + deviceKey + ">");
        GrabberThread grabberThread = grabberThreads.get(deviceKey);
        if (grabberThread != null) {
            grabberThread.stopGrabbing();
            try {
                logger.fine("Waiting for thread to die...");
                grabberThread.join(GRABBER_STOP_TIMEOUT);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                AstericsErrorHandling.instance.getLogger().warning("Could not wait for dying of grabber thread.");
            }
            grabberThreads.remove(deviceKey);
        }
    }

    /**
     * Notifies the registered {@link GrabbedImageListener} if new frames are available. if there are more than 1 listeners for a device, the frame is cloned
     * (still experimental and not really working).
     * 
     * @param deviceListeners
     * @param frame
     * @param converter
     */
    private void notifyGrabbedImageListener(List<GrabbedImageListener> deviceListeners, Frame frame, FrameConverter<IplImage> converter) {
        int counter = 0;
        for (GrabbedImageListener listener : deviceListeners) {
            if (++counter > 1) {
                // System.out.print("c");
                frame = frame.clone();
            }
            IplImage image = converter.convert(frame);
            listener.imageGrabbed(image);
        }
    }

    /**
     * This is a private class which does the actual grabbing in a new thread.
     * 
     * @author mad <deinhofe@technikum-wien.at
     * @date 04.12.2017
     *
     */
    private class GrabberThread extends Thread {
        private volatile boolean stopGrabbing = false;
        private String deviceKey;
        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, or OpenCV).
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        public GrabberThread(String deviceKey) {
            super();
            this.deviceKey = deviceKey;
        }

        @Override
        public void run() {
            FrameGrabber grabber=null;

            List<GrabbedImageListener> deviceListeners = listeners.get(deviceKey);
            try {
                grabber = getFrameGrabber(deviceKey);
                logger.fine("Start grabbing for devicekey <" + deviceKey + ">, grabber <" + grabber + ">");
                if (grabber != null) {
                    grabber.start();
                    while (!stopGrabbing) {
                        // FAQ about IplImage and Mat objects from OpenCV:
                        // - For custom raw processing of data, createBuffer() returns an NIO direct
                        // buffer wrapped around the memory pointed by imageData, and under Android we can
                        // also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
                        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
                        // Java2DFrameConverter and OpenCVFrameConverter, one after the other.
                        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
                        // data more directly between BufferedImage and IplImage or Mat via Frame objects.
                        Frame frame = grabber.grab();
                        notifyGrabbedImageListener(deviceListeners, frame, converter);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.warning("Could not start frame grabbing with grabber: "+grabber+", reason: "+e.getMessage());
            } finally {
                if(grabber!=null) {
                    // Grabbing can be safely stopped now
                    try {
                        grabber.stop();
                        grabber.release();
                        logger.fine("Grabbing stopped in grabber thread.");
                    } catch (Exception e) {
                        logger.warning("Could not stop and release framegrabber: "+grabber);
                    }
                }
            }
        }

        public void stopGrabbing() {
            stopGrabbing = true;
        }
    }
}