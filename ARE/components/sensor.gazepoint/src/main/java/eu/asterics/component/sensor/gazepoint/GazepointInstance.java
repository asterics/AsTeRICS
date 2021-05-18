
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.gazepoint;

import java.awt.Point;
import java.util.LinkedList;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.AREServices;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.awt.*;
 
/**
 * 
 * Interfaces to the Gazepoint Gaze tracker server
 * 
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: 01/2015
 */
public class GazepointInstance extends AbstractRuntimeComponentInstance // implements
                                                                   // ICalibrationProcessHandler
{
    final static IRuntimeOutputPort opGazeX = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opGazeY = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opPosX = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opPosY = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opFixationTime = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opCloseTime = new DefaultRuntimeOutputPort();

    final static IRuntimeEventTriggererPort etpBlink = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpLongblink = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpFixation = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpFixationEnd = new DefaultRuntimeEventTriggererPort();

    int GAZEPOINT_PORT = 4242;
    boolean readerThreadRunning=false;


    final static int STATE_IDLE = 0;
    final static int STATE_CALIBRATION = 1;

    final static int POS_LEFT = 0;
    final static int POS_RIGHT = 1;
    final static int POS_BOTH = 2;

    final static String CALIB_SOUND_START = "./data/sounds/7.wav";
    final static String CALIB_SOUND_NOTICE = "./data/sounds/8.wav";

    static int state = STATE_IDLE;

    static boolean propEnabled = true;
    static int propAveraging = 15;
    static int propMinBlinkTime = 50;
    static int propMidBlinkTime = 200;
    static int propMaxBlinkTime = 2000;
    static int propFixationTime = 700;

    static boolean measuringClose = false;
    static boolean measuringFixation = false;
    static boolean sentFixationEvent = false;
    static long startCloseTimestamp = 0;
    static long startFixationTimestamp = 0;
    static boolean eyePositionValid = false;

    static int gazeX, gazeY, eyeX, eyeY;
    static double oldOffsetX = 0, offsetX = 0, oldOffsetY = 0, offsetY = 0, sameOffset = 0;

    double width=0,height=0;
    private Point offset = new Point(0, 0);

    private final LinkedList<Integer> bufferX = new LinkedList<Integer>();
    private final LinkedList<Integer> bufferY = new LinkedList<Integer>();
    private int sumX = 0, sumY = 0;


    /**
     * The class constructor.
     */
    public GazepointInstance() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.getWidth();
		height = screenSize.getHeight();    
        bufferX.clear();
        bufferY.clear();				
        System.out.println("Gazepoint Screen size X="+width+", Y="+height);
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
        if ("xOffset".equalsIgnoreCase(portID)) {
            return ipXOffset;
        }
        if ("yOffset".equalsIgnoreCase(portID)) {
            return ipYOffset;
        }

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
        if ("gazeX".equalsIgnoreCase(portID)) {
            return opGazeX;
        }
        if ("gazeY".equalsIgnoreCase(portID)) {
            return opGazeY;
        }
        if ("posX".equalsIgnoreCase(portID)) {
            return opPosX;
        }
        if ("posY".equalsIgnoreCase(portID)) {
            return opPosY;
        }
        if ("fixationTime".equalsIgnoreCase(portID)) {
            return opFixationTime;
        }
        if ("closeTime".equalsIgnoreCase(portID)) {
            return opCloseTime;
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
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("calibrateCurrentProfile".equalsIgnoreCase(eventPortID)) {
            return elpCalibrateCurrentProfile;
        }
        if ("activate".equalsIgnoreCase(eventPortID)) {
            return elpActivate;
        }
        if ("deactivate".equalsIgnoreCase(eventPortID)) {
            return elpDeactivate;
        }

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("blink".equalsIgnoreCase(eventPortID)) {
            return etpBlink;
        }
        if ("longblink".equalsIgnoreCase(eventPortID)) {
            return etpLongblink;
        }
        if ("fixation".equalsIgnoreCase(eventPortID)) {
            return etpFixation;
        }
        if ("fixationEnd".equalsIgnoreCase(eventPortID)) {
            return etpFixationEnd;
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
        if ("enabled".equalsIgnoreCase(propertyName)) {
            return propEnabled;
        }
        if ("averaging".equalsIgnoreCase(propertyName)) {
            return propAveraging;
        }
        if ("minBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMinBlinkTime;
        }
        if ("midBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMidBlinkTime;
        }
        if ("maxBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMaxBlinkTime;
        }
        if ("fixationTime".equalsIgnoreCase(propertyName)) {
            return propFixationTime;
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
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("enabled".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propEnabled;

            if ("true".equalsIgnoreCase((String) newValue)) {
                propEnabled = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propEnabled = false;
            }

            return oldValue;
        }
        if ("averaging".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAveraging;
            propAveraging = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("minBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMinBlinkTime;
            propMinBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("midBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMidBlinkTime;
            propMidBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("maxBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxBlinkTime;
            propMaxBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("fixationTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFixationTime;
            propFixationTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipXOffset = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            offsetX += ConversionUtils.doubleFromBytes(data);

        }

    };

    private final IRuntimeInputPort ipYOffset = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            offsetY += ConversionUtils.doubleFromBytes(data);
        }

    };

    final IRuntimeEventListenerPort elpCalibrateCurrentProfile = new IRuntimeEventListenerPort() {

        @Override
        public void receiveEvent(String data) {
        }
    };

    final IRuntimeEventListenerPort elpActivate = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            startTracker();
        }

    };

    final IRuntimeEventListenerPort elpDeactivate = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            stopTracker();
        }
    };

    synchronized public void stopTracker() {
        System.out.println("Stop Tracker");
    }

    synchronized public void startTracker() {
        System.out.println("Start Tracker");
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
	  super.start();
		if (propEnabled == true) {
			startTracker();
		}
	    System.out.println("Starting Gazepoint component");
	    ReaderThread readerT = new ReaderThread(GAZEPOINT_PORT);
	    AstericsThreadPool.instance.execute(readerT);

    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        stopTracker();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        startTracker();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        stopTracker();
          System.out.println("Stopping Gazepoint component");
    	  readerThreadRunning=false;
          super.stop();
        super.stop();
    }

	  private class ReaderThread implements Runnable {   
	    			  
	    // DatagramSocket serverSocket=null;
	    Socket socket=null;
	    private int port;
        byte[] receiveData = new byte[4096];

	    public ReaderThread(int port) {
            this.port = port;
        }
 
        @Override
        public void run() {
 
		    double actX=0;
		    double actY=0;

	        try {
				socket = new Socket("127.0.0.1",port);
				System.out.printf("Listening on tcp:%s:%d%n",
						InetAddress.getLocalHost().getHostAddress(), port);     

				OutputStream output = socket.getOutputStream();
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
				PrintWriter writer = new PrintWriter(output, true);
				writer.println("<SET ID=\"ENABLE_SEND_POG_BEST\" STATE=\"1\" />\r\n");
				writer.println("<SET ID=\"ENABLE_SEND_DATA\" STATE=\"1\" />\r\n");

				readerThreadRunning=true;

				while(readerThreadRunning)
				{
					String line = reader.readLine();    // reads a line of text				  
					// System.out.println("RECEIVED: " + line);
					   
					String[] parts = line.split("[ ]");
					for (String acttoken: parts) {

						if (acttoken.trim().startsWith("BPOGX=")) {
							String valStr=acttoken.substring(7,acttoken.lastIndexOf('\"'));						 
							try
							{
								actX = Double.valueOf(valStr).doubleValue();
								// System.out.println("Gazepoint x = " + actvalue*width);
								// opGazeX.sendData(ConversionUtils.intToBytes((int)(actX*width)));
							}
							catch (NumberFormatException nfe)
							{}
						  } 
						  else if (acttoken.trim().startsWith("BPOGY=")) {
							String valStr=acttoken.substring(7,acttoken.lastIndexOf('\"'));						 
							try
							{
								actY = Double.valueOf(valStr).doubleValue();
								// System.out.println("Gazepoint y = " + actvalue*height);
								// opGazeY.sendData(ConversionUtils.intToBytes((int)(actY*height)));
							}
							catch (NumberFormatException nfe)
							{}
						} 
					}
					  
					bufferX.addFirst((int) (actX*width));
					sumX += (int)(actX*width);
					if (bufferX.size() > propAveraging) {
						sumX -= bufferX.removeLast();
					}

					bufferY.addFirst((int) (actY*height));
					sumY += (int)(actY*height);
					if (bufferY.size() > propAveraging) {
						sumY -= bufferY.removeLast();
					}
						
					opGazeX.sendData(ConversionUtils.intToBytes((int)(sumX / bufferX.size())));
					opGazeY.sendData(ConversionUtils.intToBytes((int)(sumY / bufferY.size())));
					  
				}
			} catch (IOException e) {
	              System.out.println(e);
			}
			finally {
				try  {
					socket.close();
					System.out.println("Gazepoint Reader Thread stopped.");
				} catch (IOException e) {
					System.out.println(e);
				}
			}
	    }
	}
}