package eu.asterics.mw.computervision;


import java.io.File;
import java.util.*;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.utils.OSUtils;

/**
 * Contains code to unify/simplify enumeration/initialization/opening/closing of camera devices and frame grabbing. 
 * Also supports publish/subscribe mechanism for grabbed frames. It is planned to also support shared camera usage over several plugins.
 * 
 * @author mad
 *
 */
public class SharedFrameGrabber {
	//timeout for grabber thread to die in ms.
	public static final String DEFAULT_GRABBER_KEY="Default";
	public static final String OPENCV_GRABBER_KEY="OpenCV";
	public static final String VIDEOINPUT_GRABBER_KEY="VideoInput";
	public static final String OPENKINECT_GRABBER_KEY="OpenKinect";
	public static final String IPCAMERA_GRABBER_KEY="IPCamera";
	public static final String PS3Eye_GRABBER_KEY="PS3Eye";
	public static final String FFMPEG_GRABBER_KEY="FFmpeg";
	
	private static final long GRABBER_STOP_TIMEOUT = 10000;
	public static SharedFrameGrabber instance=new SharedFrameGrabber();
	
	private Map<String,FrameGrabber> device2FrameGrabber=new HashMap<String,FrameGrabber>();
	private Map<String, List<GrabbedImageListener>> listeners=new HashMap<String, List<GrabbedImageListener>>(); 
	private Map<String, GrabberThread> grabberThreads=new HashMap<String, GrabberThread>();
	
	private List<String> grabberList=null;
	private String[] defaultGrabberList=new String[]{OPENCV_GRABBER_KEY, VIDEOINPUT_GRABBER_KEY, FFMPEG_GRABBER_KEY};
	private int[][] RESOLUTIONS=new int[][]{{160,120},{320,240},{352,288},{640,480},{800,600},{1024,768},{1600,1200}};
	
	public SharedFrameGrabber() {
	}
	private void init(String grabberName, String deviceKey, int userWidth, int userHeight, String grabberOptions) throws Exception {
		FrameGrabber grabber=null;

		System.out.println("Available grabber: "+getFrameGrabberList());
		System.out.println("Default FrameGrabber: "+getDefaultFrameGrabberName());
		System.out.println("FrameGrabber: "+grabberName);
		System.out.println("DeviceKey: "+deviceKey);
		System.out.println("Resolution: "+userWidth+"x"+userHeight);
		System.out.println("grabberOptions: "+grabberOptions);

		if(device2FrameGrabber.containsKey(deviceKey)) {
			AstericsErrorHandling.instance.getLogger().fine("Removing old FrameGrabber with key <"+deviceKey+">");
			stopGrabbing(deviceKey);
			FrameGrabber _grabber=device2FrameGrabber.get(deviceKey);
			_grabber.release();
			device2FrameGrabber.remove(deviceKey);
		}
		// The available FrameGrabber classes include OpenCVFrameGrabber (opencv_highgui),
		// DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
		// PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.

		if(grabberName == null || grabberName.equals(DEFAULT_GRABBER_KEY)) {			
			grabberName=getDefaultFrameGrabberName();
			System.out.println("Creating default FrameGrabber: "+grabberName);
		}
		doSanityChecks(grabberName, deviceKey);

		//System.out.println("Using grabber: "+grabberName+", and camIdx: "+camIdx);
		//grabber = FrameGrabber.create(grabberName,camIdx);

		CameraDevice.Settings devSet=new CameraDevice.SettingsImplementation();

		try{
			int camIdx=Integer.parseInt(deviceKey);
			devSet.setDeviceNumber(camIdx);
		}catch(NumberFormatException ne) {
			if(new File(deviceKey).exists()) {
				devSet.setDeviceFilename(deviceKey);
			} else {
				devSet.setDevicePath(deviceKey);
			}				
		}

		devSet.setName(deviceKey);
		devSet.setFrameGrabber(FrameGrabber.get(grabberName));
		//devSet.setTimeout(20000);
		devSet.setImageWidth(userWidth);
		devSet.setImageHeight(userHeight);    
		devSet.setFormat(grabberOptions);
		devSet.getDescription();
		System.out.println(devSet.getDescription());

		CameraDevice dev=new CameraDevice(devSet);  
		grabber=dev.createFrameGrabber();
		

		//FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("video=Integrated Camera");
		//grabber.setFormat("dshow");
		//FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("0");
		//grabber.setFormat("vfwcap");

		AstericsErrorHandling.instance.getLogger().fine("Adding FrameGrabber with key <"+deviceKey+">, grabber <"+grabber+">");
		device2FrameGrabber.put(deviceKey, grabber);
	}
	
	private void doSanityChecks(String grabberName, String deviceKey) throws Exception {
		//Some dirty checks to prevent a crash of OpenCV on Linux if the device does not exist.
		if(grabberName.equalsIgnoreCase(OPENCV_GRABBER_KEY) && (OSUtils.isUnix()||OSUtils.isMac())) {
			try{
				int camIdx=Integer.parseInt(deviceKey);
				File vidFile=new File("/dev/video"+deviceKey);
				if(!vidFile.exists()) {
					throw new Exception("Cannot create FrameGrabber <"+grabberName+"> for device <"+deviceKey+">");
				}
			}catch(NumberFormatException e) {}
		}
	}
	
	public List<String> getDeviceList(String grabberName) {
		String[] s=null;
        try {
            Class<? extends FrameGrabber> c = FrameGrabber.get(grabberName);
            c.getMethod("tryLoad").invoke(null);
            try {
                s = (String[])c.getMethod("getDeviceDescriptions").invoke(null);
                if(s!=null) {
                	return Arrays.asList(s);
                }
            } catch (Throwable t) { 
            }
        } catch (Throwable t) { }
       	return Arrays.asList(new String[]{"Not available"});
    }
	
	public List<String> getFrameGrabberList() {
		if(grabberList==null) {
			grabberList=new ArrayList<String>();
			grabberList.add(DEFAULT_GRABBER_KEY);
			for(String grabberName : FrameGrabber.list) {
				try {
					Class<? extends FrameGrabber> c = FrameGrabber.get(grabberName);
					c.getMethod("tryLoad").invoke(null);
					grabberList.add(grabberName);
				} catch (Throwable t) { }			
			}
		}
		return grabberList;
	}
	
	public String getDefaultFrameGrabberName() {
		List<String> grabberList=getFrameGrabberList();
		for(String grabberName : defaultGrabberList) {
			if(grabberList.contains(grabberName)) {
				return grabberName;
			}
		}
		return "";
	}

	public FrameGrabber getFrameGrabber(String deviceKey, String grabberName, int resolutionIdx, String grabberOptions) throws Exception {
		if(device2FrameGrabber.containsKey(deviceKey)) {
			return device2FrameGrabber.get(deviceKey);
		}

		init(grabberName,deviceKey,RESOLUTIONS[resolutionIdx][0],RESOLUTIONS[resolutionIdx][1], grabberOptions);
		return device2FrameGrabber.get(deviceKey);		
	}

	public FrameGrabber getFrameGrabber(String deviceKey, String grabberName, int width, int height) throws Exception {
		if(device2FrameGrabber.containsKey(deviceKey)) {
			return device2FrameGrabber.get(deviceKey);
		}

		init(grabberName,deviceKey,width,height, "");
		return device2FrameGrabber.get(deviceKey);		
	}

	public FrameGrabber getFrameGrabber(String deviceKey, int width, int height) throws Exception {
		if(device2FrameGrabber.containsKey(deviceKey)) {
			return device2FrameGrabber.get(deviceKey);
		}

		init(null,deviceKey,width,height, "");
		return device2FrameGrabber.get(deviceKey);		
	}
	
	public FrameGrabber getFrameGrabber(String deviceKey) throws Exception {
		if(device2FrameGrabber.containsKey(deviceKey)) {
			return device2FrameGrabber.get(deviceKey);
		}

		init(null,deviceKey,320,240, "");
		return device2FrameGrabber.get(deviceKey);
	}
	
	public FrameGrabber getDefaultFrameGrabber() throws Exception {
		return getFrameGrabber("0");
	}
	
	public void registerGrabbedImageListener(String deviceKey, GrabbedImageListener listener) {
		List<GrabbedImageListener> deviceListeners=listeners.get(deviceKey);
		if(deviceListeners==null) {
			deviceListeners=new ArrayList<GrabbedImageListener>();
		}
		deviceListeners.add(listener);
		listeners.put(deviceKey, deviceListeners);
		System.out.println("After registering: Registered grabbing listeners: "+deviceListeners);
	}
	
	public void deregisterGrabbedImageListener(String deviceKey, GrabbedImageListener listener) {
		List<GrabbedImageListener> deviceListeners=listeners.get(deviceKey);
		if(deviceListeners!=null) {
			deviceListeners.remove(listener);
		}
		if(deviceListeners==null || (deviceListeners!= null && deviceListeners.isEmpty())) {
			//if there are no listeners, stop and remove grabber again.
			stopGrabbing(deviceKey);
			FrameGrabber grabber=device2FrameGrabber.get(deviceKey);
			if(grabber!=null) {
				try {
					grabber.stop();
					grabber.release();
					device2FrameGrabber.remove(deviceKey);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("After deregistering: Registered grabbing listeners: "+deviceListeners);
	}
	
	public void startGrabbing(final String deviceKey) {
		GrabberThread grabberThread=grabberThreads.get(deviceKey);
		if(grabberThread==null) {
			grabberThread=new GrabberThread(deviceKey);
			grabberThreads.put(deviceKey, grabberThread);
			grabberThread.start();
		}
	}
	
	public void stopGrabbing(String deviceKey) {
		System.out.println("Stop grabbing for devicekey <"+deviceKey+">");
		GrabberThread grabberThread=grabberThreads.get(deviceKey);
		if(grabberThread!=null) {
			grabberThread.stopGrabbing();
			try {
				System.out.println("Waiting for thread to die...");
				grabberThread.join(GRABBER_STOP_TIMEOUT);				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				AstericsErrorHandling.instance.getLogger().warning("Could not wait for dying of grabber thread.");
			}
			grabberThreads.remove(deviceKey);
		}
	}
	
	private void notifyGrabbedImageListener(List<GrabbedImageListener> deviceListeners, IplImage image) {
		for(GrabbedImageListener listener : deviceListeners) {
			listener.imageGrabbed(image);
		}
	}
	
	class GrabberThread extends Thread {
		boolean stopGrabbing=false;
		private String deviceKey;
		
		public GrabberThread(String deviceKey) {
			super();
			this.deviceKey = deviceKey;
		}

		@Override
		public void run() {
			FrameGrabber grabber;
			List<GrabbedImageListener> deviceListeners=listeners.get(deviceKey);
			try {
				grabber = getFrameGrabber(deviceKey);
				System.out.println("Start grabbing for devicekey <"+deviceKey+">, grabber <"+grabber+">");
				if(grabber!=null) {
					grabber.start();
					while(!stopGrabbing) {
						IplImage image=grabber.grab();
						notifyGrabbedImageListener(deviceListeners, image);
					}
					grabber.stop();
					System.out.println("Grabbing stopped");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void stopGrabbing() {
			stopGrabbing=true;
		}
	}
}
