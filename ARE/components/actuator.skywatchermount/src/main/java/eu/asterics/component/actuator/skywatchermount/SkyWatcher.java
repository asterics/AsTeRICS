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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.skywatchermount;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils; 
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.cimcommunication.*;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * 
 *  
 * @author David Thaller [dt@ki-i.at]
 *         Date: 02.09.2013
 *         Time: 14:30
 */
public class SkyWatcher extends AbstractRuntimeComponentInstance
{
	private enum Status {STOP,LEFT,RIGHT,UP,DOWN};
	private Status panStatus, tiltStatus;
	private OutputStream out = null;
	private InputStream in = null;
	private CIMPortController portController = null;
	private String serialPort;
	private Thread readerThread;
	private Thread posUpdateThread;
	private Thread senderThread;
	private ArrayList<String> cmdList;
	private boolean cmdReady;
	private ArrayList<PositionEventListener> posListeners;
	private String speedTable []= {"FF0000","AA0000","990000","880000","770000","5500000","4400000","1800000","1100000","050000"};
	private String speed;
	private int totalStepsPan, totalStepsTilt;
	private int maxLeft, maxRight, maxUp, maxDown;
	private boolean limitAxis = false;
	private int panPosition, tiltPosition;
   /**
    * The class constructor.
    */
    public SkyWatcher(String serialPort)
    {
		panStatus = Status.STOP;
		tiltStatus = Status.STOP;
        this.serialPort = serialPort;
		cmdList = new ArrayList<String>();
		speed = speedTable[2];
		posListeners = new ArrayList<PositionEventListener>();
		cmdReady = true;
		init();
    }
	
	
	public void init() 
	{
		boolean result = openSerialPort();
		if (result = false) {
			try {
				Thread.sleep(2000);
				openSerialPort();
			} catch (InterruptedException ie) {}
		}
		if (portController == null || out == null)
			return;
		out = portController.getOutputStream();
		initAxis();
	}
	
	public void addPositionListener(PositionEventListener listener) {
		posListeners.add(listener);
	}
	
	public void removePositionListener(PositionEventListener listener) {
		posListeners.remove(listener);
	}
	
	private void firePositionEvent(int axis, int position) {
		for (PositionEventListener l : posListeners) {
			l.updatePosition(axis,position);
		}
	}
	
	private void sendCommand(String cmd) {
		cmdList.add(cmd);
	}
	
	public void setLimitActive(boolean state) {
		this.limitAxis = state;
		System.out.println("Move limits state: "+state);
	}
	
	public void setMaxLeft(int value) {
		System.out.println("Setting maxLeft to " + value);
		this.maxLeft = value;
	}
	
	
	public void setMaxRight(int value) {
		this.maxRight = value;
	}
	
	public void setMaxUp(int value) {
		this.maxUp= value;
	}
	
	public void setMaxDown(int value) {
		this.maxDown = value;
	}
	
	private void sendNextCommand() {
		if (out == null || portController == null || cmdList == null || cmdList.isEmpty() || cmdReady == false) {
			System.out.println("Something is null: out --> " + out + " portController --> "+portController + " cmdList empty --> " + cmdList.isEmpty() + " cmdReady --> " + cmdReady );
			return;
		}
		String cmd = "";
		try {
			cmdReady = false;
			cmd = cmdList.remove(0);
			if (cmd != null) {
				out.write(cmd.getBytes());
			}
		} catch (IOException ex) {
			AstericsErrorHandling.instance.reportError(this,"Could not send command "+cmd);
		}
	}
	
	private void initAxis() {
		sendCommand(":F1\r");
		sendCommand(":a1\r");
		sendCommand(":F2\r");
		sendCommand(":a2\r");
	}
	
	private String TranslatePosition(int pos) {
		String temp;
		temp = Integer.toHexString(pos);
		StringBuilder dest = new StringBuilder(temp);
		for (int i = temp.length(); i < 6; i++) {
			dest.append("0");
		}
		StringBuilder dest2 = new StringBuilder("");
		dest2.append(dest.charAt(4));
		dest2.append(dest.charAt(5));
		dest2.append(dest.charAt(2));
		dest2.append(dest.charAt(3));
		dest2.append(dest.charAt(0));
		dest2.append(dest.charAt(1));
		return dest2.toString().toUpperCase();
	}
	
	private void processResponse(String cmd, String response) {
		if (cmd.startsWith(":j")) {
			if (response.charAt(0) != '=') {
				return;
			}
			// cut '=' character
			response = response.substring(1);
			if (response.length() < 6)
				return;
			StringBuilder sortedHexValue = new StringBuilder();
			sortedHexValue.append(response.charAt(4));
			sortedHexValue.append(response.charAt(5));
			
			sortedHexValue.append(response.charAt(2));
			sortedHexValue.append(response.charAt(3));
			
			sortedHexValue.append(response.charAt(0));
			sortedHexValue.append(response.charAt(1));
			try {
				int position = Integer.parseInt(sortedHexValue.toString(),16);
				int axis = 1;
				boolean fireEvent = false;
				if (cmd.charAt(2) == '2') {
					axis = 2;
					fireEvent = position != tiltPosition;
					tiltPosition = position;
					if (limitAxis) {
						if (panStatus == Status.LEFT) {
							if (panPosition <= maxLeft)
								stopPan();
						} else if (panStatus == Status.RIGHT) {
							if (panPosition >= maxRight)
								stopPan();
						}
					}
				} else {
					fireEvent = position !=  panPosition;
					panPosition = position;
					if (limitAxis) {
						if (tiltStatus == Status.UP) {
							if (tiltPosition >= maxUp)
								stopTilt();
						} else if (tiltStatus == Status.DOWN) {
							if (tiltPosition <= maxDown)
								stopTilt();
						}
					}
				}
				if (fireEvent)
					firePositionEvent(axis,position);
			} catch (NumberFormatException ne) {}
		} else if (cmd.startsWith(":a")) {
			if (response.charAt(0) != '=') {
				return;
			}
			// cut '=' character
			response = response.substring(1); 
			if (response.length() < 6)
				return;
			StringBuilder sortedHexValue = new StringBuilder();
			sortedHexValue.append(response.charAt(4));
			sortedHexValue.append(response.charAt(5));
			
			sortedHexValue.append(response.charAt(2));
			sortedHexValue.append(response.charAt(3));
			
			sortedHexValue.append(response.charAt(0));
			sortedHexValue.append(response.charAt(1));
			try {
				int axis = 1;
				if (cmd.charAt(2) == '2')
					axis = 2;
				if (axis == 1) {
					totalStepsPan = Integer.parseInt(sortedHexValue.toString(),16);
					System.out.println("TotalSteps Pan: "+totalStepsPan);
				} else {
					totalStepsTilt = Integer.parseInt(sortedHexValue.toString(),16);
					System.out.println("TotalSteps Tilt: "+totalStepsTilt);
				}  
				
			} catch (NumberFormatException ne) {}
		}
	}
	
	public void goToRandomPosition() {
				Random r = new Random(System.currentTimeMillis());
				int pan = maxLeft + r.nextInt(maxRight - maxLeft);
				int tilt = maxDown + r.nextInt(maxUp - maxDown);
				System.out.println("Random pos: " + pan + "x" + tilt);
				goToPan(pan);
				goToTilt(tilt);
	}
	
	
	public boolean openSerialPort () 
	{
		portController = CIMPortManager.getInstance().getRawConnection(this.serialPort,9600,true);
    	if (portController == null) 
		{
			AstericsErrorHandling.instance.reportError(this,"Could not construct raw port controller");
			return false; 
    	} else  
		{
			out = portController.getOutputStream();
			in = portController.getInputStream();
			readerThread = new Thread(new Runnable() 
			{

				@Override
				public void run() 
				{
					boolean cmdEnded = false;
					StringBuilder lastCommand = new StringBuilder();
					StringBuilder response = new StringBuilder();
					int breakCount = 0;
					while (readerThread.isInterrupted() == false) 
					{
						try { 
							if (in.available() > 0) 
							{
								int val = in.read();
								if (val == 13) 
								{
									breakCount++;
									if (breakCount == 2) {
										breakCount = 0;
										processResponse(lastCommand.toString(),response.toString());
										lastCommand = new StringBuilder();
										response = new StringBuilder();
										cmdReady = true;
									}
								} else {
									if (breakCount == 0)
										lastCommand.append((char)val);
									else 
										response.append((char)val);
								}
								
								
							} else {
								Thread.sleep(10);
							}
						} catch (InterruptedException ie) 
						{
						} catch (IOException io) 
						{
							io.printStackTrace();
						}
					}
				}
    			  
    		});
    		readerThread.start();
			posUpdateThread = new Thread(new Runnable() 
			{

				@Override
				public void run() 
				{
					while (posUpdateThread.isInterrupted() == false) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {
						}
						readPanPosition();
						readTiltPosition();
					}
				}
			});
			posUpdateThread.start();
			senderThread = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					while (senderThread.isInterrupted() == false) {
						if (portController != null && out != null && cmdList.isEmpty() == false && cmdReady == true) {
							sendNextCommand();
						} else {
							try {
								Thread.sleep(20);
							} catch (InterruptedException ie) {}
						}	
					}
				}
			});
			senderThread.start();
		}
		return true;
	}
	
	public void setSpeed(int index) {
		if (index >= 0 && index < 10) {
			this.speed = speedTable[index];
		}
	}
	
	public void goRight() {
		stopPan();
		if (limitAxis) {
			System.out.println("Axis is " + panPosition + " limit is " + maxRight);
			if (panPosition >= maxRight)
				return;
		}
		sendCommand(":G132\r");
		sendCommand(":I1"+speed+"\r");
		sendCommand(":J1\r");
		panStatus = Status.RIGHT;
	}
	
	public void goLeft() {
		stopPan();
		if (limitAxis) {
			System.out.println("Axis is " + panPosition + " limit is " + maxLeft);
			if (panPosition <= maxLeft)
				return;
		}
		sendCommand(":G131\r");
		sendCommand(":I1"+speed+"\r");
		sendCommand(":J1\r");
		panStatus = Status.LEFT;
		
	}
	
	public void goDown() {
		stopTilt();
		if (limitAxis) {
			if (tiltPosition <= maxDown)
				return;
		}
		sendCommand(":G231\r");
		sendCommand(":I2"+speed+"\r");
		sendCommand(":J2\r");
		tiltStatus = Status.DOWN;
	}
	
	public void goUp() {
		stopTilt();
		if (limitAxis) {
			if (tiltPosition >= maxUp)
				return;
		}
		sendCommand(":G232\r");
		sendCommand(":I2"+speed+"\r");
		sendCommand(":J2\r");
		tiltStatus = Status.UP;
	}
	
	public void goToPan(int position) {
		stopPan();
		sendCommand(":G140\r");
		sendCommand(":S1"+TranslatePosition(position)+"\r");
		System.out.println("GoTo "+position);
		sendCommand(":J1\r");
	}
	
	public void goToTilt(int position) {
		stopTilt();
		sendCommand(":G240\r");
		sendCommand(":S2"+TranslatePosition(position)+"\r");
		System.out.println("GoTo "+position);
		sendCommand(":J2\r");
	}
	
	
	public void readPanPosition() {
		sendCommand(":j1\r");
	}
	
	public void setTrigger(boolean state) {
		System.out.println("Set trigger to "+state);
		if (state) 
			sendCommand(":O11\r");
		else
			sendCommand(":O10\r");
	}
	
	public void readTiltPosition() {
		sendCommand(":j2\r");
	}
	
	public void stopPan() {
		sendCommand(":L1\r");
		panStatus = Status.STOP;
	}
	
	public void stopTilt() {
		sendCommand(":L2\r");
		tiltStatus = Status.STOP;
	}
	
	public void stop() {
		stopPan();
		stopTilt();
	}
	
	public void close() 
	{
		if (portController != null) {
        	CIMPortManager.getInstance().closeRawConnection(serialPort);
  			portController = null;
  			AstericsErrorHandling.instance.reportInfo(this, "Skywatcher connection closed");
        }
		try {
			if (readerThread != null) {
				readerThread.interrupt();
				readerThread.join(500);
			}
			if (posUpdateThread != null) {
				posUpdateThread.interrupt();
				posUpdateThread.join(500);
			}
			if (senderThread != null) {
				senderThread.interrupt();
				senderThread.join(500);
			}
		} catch (InterruptedException ie) {}
	}
}
