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

package eu.asterics.component.sensor.kinect;


import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

import javax.naming.spi.DirectoryManager;
import javax.swing.JFrame;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.OutArg;
import org.OpenNI.ScriptNode;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.UserGenerator;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * The Kinect plugin tracks the skeleton of one user with the 
 * openni framework and the kinect camera.
 * In this version the coordinates of the following joints can be accessed:
 *  - head
 *  - left hand
 *  - right hand
 *  - left foot
 *  - right foot
 * 
 * @author David Thaller dt@ki-i.at Date: 20.01.2012 Time: 10:22
 */

public class KinectInstance extends AbstractRuntimeComponentInstance {
	final OutputPort opHeadX = new OutputPort();
	final OutputPort opHeadY = new OutputPort();
	final OutputPort opHeadZ = new OutputPort();

	final OutputPort opLeftHandX = new OutputPort();
	final OutputPort opLeftHandY = new OutputPort();
	final OutputPort opLeftHandZ = new OutputPort();

	final OutputPort opRightHandX = new OutputPort();
	final OutputPort opRightHandY = new OutputPort();
	final OutputPort opRightHandZ = new OutputPort();

	final OutputPort opRightFootX = new OutputPort();
	final OutputPort opRightFootY = new OutputPort();
	final OutputPort opRightFootZ = new OutputPort();

	final OutputPort opLeftFootX = new OutputPort();
	final OutputPort opLeftFootY = new OutputPort();
	final OutputPort opLeftFootZ = new OutputPort();


	boolean propVisualize = true;
	boolean propCenterZeroPoint = false;
	
	// declare member variables here
	private SkeletonTracker st;

	private JFrame f;

	private OutArg<ScriptNode> scriptNode;
	private Context context;
	private DepthGenerator depthGen;
	private UserGenerator userGen;
	private SkeletonCapability skelCap;

	private SkeletonPanel sp;
	
	private SkeletonListener sendListener;
	
	private final String SAMPLE_XML_FILE = "data/sensor.kinect/SamplesConfig.xml";

	private int xOffset,yOffset;
	
	/**
	 * The class constructor.
	 */
	public KinectInstance() {
		if (propCenterZeroPoint) {
			xOffset = 320;
			yOffset = 240;
		} else {
			xOffset = 0;
			yOffset = 0;
		}
		sendListener = new SkeletonListener() {

			@Override
			public void skeletonUpdate(Skeleton skel) {
				Point3D head = skel.head;
				opHeadX.sendData(head.getX()-xOffset);
				opHeadY.sendData(head.getY()-yOffset);
				opHeadZ.sendData(head.getZ());

				Point3D lhand = skel.leftHand;
				opLeftHandX.sendData(lhand.getX()-xOffset);
				opLeftHandY.sendData(lhand.getY()-yOffset);
				opLeftHandZ.sendData(lhand.getZ());

				Point3D rhand = skel.rightHand;
				opRightHandX.sendData(rhand.getX()-xOffset);
				opRightHandY.sendData(rhand.getY()-yOffset);
				opRightHandZ.sendData(rhand.getZ());

				Point3D lfoot = skel.leftFoot;
				opLeftFootX.sendData(lfoot.getX()-xOffset);
				opLeftFootY.sendData(lfoot.getY()-yOffset);
				opLeftFootZ.sendData(lfoot.getZ());

				Point3D rfoot = skel.rightFoot;
				opRightFootX.sendData(rfoot.getX()-xOffset);
				opRightFootY.sendData(rfoot.getY()-yOffset);
				opRightFootZ.sendData(rfoot.getZ());
			}
		};
	}

	/**
	 * returns an Input Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the input port or null if not found
	 */
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
	public IRuntimeOutputPort getOutputPort(String portID) {
		if ("headX".equalsIgnoreCase(portID)) {
			return opHeadX;
		}
		if ("headY".equalsIgnoreCase(portID)) {
			return opHeadY;
		}
		if ("headZ".equalsIgnoreCase(portID)) {
			return opHeadZ;
		}
		if ("leftHandX".equalsIgnoreCase(portID)) {
			return opLeftHandX;
		}
		if ("leftHandY".equalsIgnoreCase(portID)) {
			return opLeftHandY;
		}
		if ("leftHandZ".equalsIgnoreCase(portID)) {
			return opLeftHandZ;
		}
		if ("rightHandX".equalsIgnoreCase(portID)) {
			return opRightHandX;
		}
		if ("rightHandY".equalsIgnoreCase(portID)) {
			return opRightHandY;
		}
		if ("rightHandZ".equalsIgnoreCase(portID)) {
			return opRightHandZ;
		}
		if ("rightFootX".equalsIgnoreCase(portID)) {
			return opRightFootX;
		}
		if ("rightFootX".equalsIgnoreCase(portID)) {
			return opRightFootX;
		}
		if ("rightFootY".equalsIgnoreCase(portID)) {
			return opRightFootY;
		}
		if ("rightFootZ".equalsIgnoreCase(portID)) {
			return opRightFootZ;
		}
		if ("leftFootX".equalsIgnoreCase(portID)) {
			return opRightFootX;
		}
		if ("leftFootY".equalsIgnoreCase(portID)) {
			return opRightFootY;
		}
		if ("leftFootZ".equalsIgnoreCase(portID)) {
			return opRightFootZ;
		}
		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventListener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

		return null;
	}

	/**
	 * returns an Event Triggerer Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventTriggerer port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {

		return null;
	}

	/**
	 * returns the value of the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("visualize".equalsIgnoreCase(propertyName)) {
			return propVisualize;
		}

		return null;
	}

	/**
	 * sets a new value for the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param newValue
	 *            the desired property value or null if not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		if ("visualize".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propVisualize;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propVisualize = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propVisualize = false;
			}
			return oldValue;
		}

		return null;
	}

	/**
	 * Input Ports for receiving values.
	 */

	/**
	 * Event Listerner Ports.
	 */

	/**
	 * called when model is started.
	 */
	@Override
	public void start() {
		java.lang.Runtime runtime = Runtime.getRuntime();
		try {
			// dirty workaround to circumvent random freezes of the openni driver and pointserver
			runtime.exec("taskkill /F /IM XnSensorServer.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			scriptNode = new OutArg<ScriptNode>();
			File f = new File("data/sur");
			if (!f.exists()) {
				f.mkdir();
			}
			context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
			depthGen = DepthGenerator.create(context);
			userGen = UserGenerator.create(context);
			skelCap = userGen.getSkeletonCapability();
		} catch (GeneralException e) {
			AstericsErrorHandling.instance.reportError(this, String.format("Error initializing the openni Framework. Reason: " + e.getMessage()));
		}
		super.start();
		st = new SkeletonTracker(context, depthGen, userGen, skelCap);
		st.start();
		if (propVisualize) {
			sp = new SkeletonPanel();
			st.addSkeletonListener(sp);
			f = new JFrame();
			f.setSize(640, 480);
			f.add(sp);
			f.setVisible(true);
		}
		st.addSkeletonListener(sendListener);
		AstericsErrorHandling.instance.getLogger().fine("Kinect initialized successfully! Waiting for user.");
	}

	/**
	 * called when model is paused. Stops the Thread 
	 */
	@Override
	public void pause() {
		super.pause();
		if (st != null)
			st.pauseTracking();
	}

	/**
	 * called when model is resumed.
	 */
	@Override
	public void resume() {
		super.resume();
		st.resumeTracking();
	}

	/**
	 * called when model is stopped.
	 */
	@Override
	public void stop() {
		super.stop();
		if (propVisualize)
			f.setVisible(false);
		if (st != null) {
			st.stopTracking();
			try {
				st.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * implementation of the default output port for sending double
	 */
	public class OutputPort extends DefaultRuntimeOutputPort {
		public void sendData(double data) {
			super.sendData(ConversionUtils.doubleToBytes(data));
		}
	}
}