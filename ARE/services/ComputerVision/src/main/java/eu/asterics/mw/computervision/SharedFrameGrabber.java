package eu.asterics.mw.computervision;

import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Contains code to unify/simplify enumeration/initialization/opening/closing of camera devices and frame grabbing. 
 * Also supports publish/subscribe mechanism for grabbed frames. It is planned to also support shared camera usage over several plugins.
 * 
 * @author mad
 *
 */
public class SharedFrameGrabber {
	public static SharedFrameGrabber instance=new SharedFrameGrabber();
	
	private FrameGrabber grabber=null;
	private Map<String,FrameGrabber> device2FrameGrabber=new HashMap<String,FrameGrabber>();
	private Map<String, List<GrabbedImageListener>> listeners=new HashMap<String, List<GrabbedImageListener>>(); 
	private Map<String, GrabberThread> grabberThreads=new HashMap<String, GrabberThread>();

	
	private void init() throws Exception {
		init(-1,"0",320,240);
	}
	private void init(int frameGrabberIdx, String deviceKey, int userWidth, int userHeight) throws Exception {
		
		if(device2FrameGrabber.containsKey(deviceKey)) {
			AstericsErrorHandling.instance.getLogger().fine("Removing old FrameGrabber with key <"+deviceKey+">");
			FrameGrabber _grabber=device2FrameGrabber.get(deviceKey);
			_grabber.stop();
			device2FrameGrabber.remove(deviceKey);
		}
        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_highgui),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
             
		int camIdx=Integer.parseInt(deviceKey);
		if(frameGrabberIdx == -1) {
			System.out.println("Creating default FrameGrabber");
			grabber=FrameGrabber.createDefault(camIdx);
		} else {
	        System.out.println("List of grabbers (indices): "+FrameGrabber.list);
	        System.out.println("Using grabber: "+FrameGrabber.list.get(frameGrabberIdx)+", and camIdx: "+camIdx);
	        grabber = FrameGrabber.create(FrameGrabber.list.get(frameGrabberIdx),camIdx);
	        //FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("video=Integrated Camera");
	        //grabber.setFormat("dshow");
	        //FFmpegFrameGrabber grabber =new FFmpegFrameGrabber("0");
	        //grabber.setFormat("vfwcap");
		}
        
        //FrameGrabber grabber = FrameGrabber.createDefault(frameGrabberIdx);
        grabber.setImageHeight(userHeight);
        grabber.setImageWidth(userWidth);
        //grabber.setFrameRate(10);
        
		AstericsErrorHandling.instance.getLogger().fine("Adding FrameGrabber with key <"+deviceKey+">");
        device2FrameGrabber.put(deviceKey, grabber);
        grabber.start();		
	}
	
	public List<String> getDeviceStrings() {
		 return new ArrayList<String>();
	}
	
	public FrameGrabber getFrameGrabber(String deviceKey) throws Exception {
		if(device2FrameGrabber.containsKey(deviceKey)) {
			return device2FrameGrabber.get(deviceKey);
		}

		init(-1,deviceKey,320,240);
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
		System.out.println("After deregistering: Registered grabbing listeners: "+deviceListeners);
	}
	
	public void startGrabbing(final String deviceKey) {
		GrabberThread grabberThread=grabberThreads.get(deviceKey);
		if(grabberThread==null) {
			grabberThread=new GrabberThread(deviceKey);
			grabberThreads.put(deviceKey, grabberThread);
		}
		grabberThread.start();
	}
	
	public void stopGrabbing(String deviceKey) {
		System.out.println("Stop grabbing for devicekey <"+deviceKey+">");
		GrabberThread grabberThread=grabberThreads.get(deviceKey);
		if(grabberThread!=null) {
			grabberThread.stopGrabbing();
			try {
				System.out.println("Wainting for thread to die...");
				grabberThread.join();				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				System.out.println("Start grabbing for devicekey <"+deviceKey+">");
				grabber = getFrameGrabber(deviceKey);
				while(!stopGrabbing) {
					IplImage image=grabber.grab();
					notifyGrabbedImageListener(deviceListeners, image);
				}
				System.out.println("Grabbing stopped");
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
