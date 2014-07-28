

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

package eu.asterics.component.sensor.eyetribe;

import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.ICalibrationProcessHandler;
import com.theeyetribe.client.data.CalibrationResult;

import com.theeyetribe.client.*;

import java.util.logging.Logger;
import java.awt.Point;

import eu.asterics.component.sensor.eyetribe.CalibrationGenerator;
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


/**
 * 
 * Interfaces to the EyeTribe Gaze tracker server
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class EyeTribeInstance extends AbstractRuntimeComponentInstance // implements ICalibrationProcessHandler
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
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	final static int STATE_IDLE=0;
	final static int STATE_CALIBRATION=1;
	final static int STATE_OFFSETCORRECTION=2;
	final static int STATE_GETOFFSET=3;

    static int state = STATE_IDLE;
	
	static int propMinBlinkTime = 50;
	static int propMaxBlinkTime = 200;
	static int propFixationTime = 700;
	static int propOffsetCorrectionRadius=150;
	
	static boolean measuringClose=false; 
	static boolean measuringFixation=false;
	static boolean firstFixation=false;
	static long startCloseTimestamp=0;
	static long startFixationTimestamp=0;

	
	static long  offsetCorrectionStartTime;
	static int  gazeX,gazeY,leftEyeX,leftEyeY;
	static int  correctedGazeX,correctedGazeY,weakGazePointX,weakGazePointY, saveCorrectedGazeX, saveCorrectedGazeY;
	
	private final CalibrationGenerator calib = new CalibrationGenerator(this);

	final GazeManager gm = GazeManager.getInstance();        
 	final GazeListener gazeListener = new GazeListener();
    
   /**
    * The class constructor.
    */
    public EyeTribeInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("gazeX".equalsIgnoreCase(portID))
		{
			return opGazeX;
		}
		if ("gazeY".equalsIgnoreCase(portID))
		{
			return opGazeY;
		}
		if ("posX".equalsIgnoreCase(portID))
		{
			return opPosX;
		}
		if ("posY".equalsIgnoreCase(portID))
		{
			return opPosY;
		}
		if ("fixationTime".equalsIgnoreCase(portID))
		{
			return opFixationTime;
		}
		if ("closeTime".equalsIgnoreCase(portID))
		{
			return opCloseTime;
		}
		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("startCalibration".equalsIgnoreCase(eventPortID))
		{
			return elpStartCalibration;
		}
		if ("offsetCorrection".equalsIgnoreCase(eventPortID))
		{
			return elpOffsetCorrection;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("blink".equalsIgnoreCase(eventPortID))
		{
			return etpBlink;
		}
		if ("longblink".equalsIgnoreCase(eventPortID))
		{
			return etpLongblink;
		}
		if ("fixation".equalsIgnoreCase(eventPortID))
		{
			return etpFixation;
		}
		if ("fixationEnd".equalsIgnoreCase(eventPortID))
		{
			return etpFixationEnd;
		}
        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("minBlinkTime".equalsIgnoreCase(propertyName))
		{
			return propMinBlinkTime;
		}
		if ("maxBlinkTime".equalsIgnoreCase(propertyName))
		{
			return propMaxBlinkTime;
		}
		if ("fixationTime".equalsIgnoreCase(propertyName))
		{
			return propFixationTime;
		}
		if ("offsetCorrectionRadius".equalsIgnoreCase(propertyName))
		{
			return propOffsetCorrectionRadius;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("minBlinkTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMinBlinkTime;
			propMinBlinkTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("maxBlinkTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMaxBlinkTime;
			propMaxBlinkTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("fixationTime".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFixationTime;
			propFixationTime = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("offsetCorrectionRadius".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propOffsetCorrectionRadius;
			propOffsetCorrectionRadius = Integer.parseInt(newValue.toString());
			return oldValue;
		}
        return null;
    }

     /**
      * Event Listerner Ports.
      */
    final IRuntimeEventListenerPort elpStartCalibration 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public synchronized void receiveEvent(String data)
    	 {
    			state=STATE_CALIBRATION;
    			measuringClose=false;
    			measuringFixation=false;
    			calib.startCalibration();
    	 }
    };
    
    final void calibrationDone()
    {
    	state=STATE_IDLE;
    }
    
	/**
	 * Event Listener Port for offset correction.
	 */
    final IRuntimeEventListenerPort elpOffsetCorrection 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public synchronized void receiveEvent(String data)
    	 {
    		if (state!=STATE_CALIBRATION)
    		{
				calib.playWavFile("./data/sounds/7.wav");

   				System.out.println("Offset correction triggered."); 
    			state=STATE_OFFSETCORRECTION;
    			measuringClose=false;
    			measuringFixation=false;
     		    offsetCorrectionStartTime=System.currentTimeMillis();
    		}
    	 }
    };    

	
    
    private class GazeListener implements IGazeListener
    {
        @Override
        public synchronized void onGazeUpdate(GazeData gazeData)
        {
        	//  System.out.println(gazeData.toString());
            
            if ((state==STATE_CALIBRATION) || (gazeData.state & 7) == 0)     // tracking lost ?
            { 
            	// System.out.print("-");
            	return; 
            } 
             
            gazeX = (int)gazeData.smoothedCoordinates.x;
            gazeY = (int)gazeData.smoothedCoordinates.y;

            Point offset = calib.calcOffset(gazeX, gazeY);
    		correctedGazeX=gazeX+offset.x;
            correctedGazeY=gazeY+offset.y;

            leftEyeX=(int)(gazeData.leftEye.pupilCenterCoordinates.x*1000);
            leftEyeY=(int)(gazeData.leftEye.pupilCenterCoordinates.y*1000);

            if (state == STATE_OFFSETCORRECTION)
            {
            	if (System.currentTimeMillis()>=offsetCorrectionStartTime+1000)
        	    {
            		weakGazePointX=gazeX;
            	    weakGazePointY=gazeY;
            	    
            	    saveCorrectedGazeX=correctedGazeX;
            	    saveCorrectedGazeY=correctedGazeY;

            	    calib.playWavFile("./data/sounds/8.wav");
            		state=STATE_GETOFFSET;
        	    }
            }
            if (state == STATE_GETOFFSET) 
            {
            	if (System.currentTimeMillis()>=offsetCorrectionStartTime+2000)
            	{
                    Point oldOffset = calib.calcOffset(weakGazePointY, weakGazePointY);
               		calib.newOffsetPoint(weakGazePointX,weakGazePointY,oldOffset.x+(saveCorrectedGazeX-correctedGazeX),oldOffset.y+(saveCorrectedGazeY-correctedGazeY));
            		state = STATE_IDLE;
            	}
            	return;
            } 
            
            
            if ((gazeData.leftEye.pupilCenterCoordinates.x ==0) && (gazeX==0))   // eyes closed
            {
            	// System.out.print("*");

            	if (measuringClose == false)
            	{
                	startCloseTimestamp = System.currentTimeMillis();
                	measuringClose = true;
            	}
            	opCloseTime.sendData(ConversionUtils.intToBytes((int)(System.currentTimeMillis() - startCloseTimestamp)));
            }
            else
            { 
            	// System.out.print("+");

            	if (measuringClose == true)
            	{
            		long blinktime=System.currentTimeMillis() - startCloseTimestamp;
  
            		if ((blinktime > propMinBlinkTime) && (blinktime < propMaxBlinkTime))
            			etpBlink.raiseEvent();
            		else if (blinktime > propMaxBlinkTime)
            			etpLongblink.raiseEvent();
            		
            	    measuringClose = false;
            	}
            	
            	if ((gazeX != 0) || (gazeY != 0))
            	{
		            opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX));
		            opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY));
            	}
            	
            	if ((leftEyeX != 0) || (leftEyeY != 0))
            	{
		            opPosX.sendData(ConversionUtils.intToBytes(leftEyeX));
		            opPosY.sendData(ConversionUtils.intToBytes(leftEyeY));
            	}
	
	            if ((gazeData.isFixated==true) && (measuringFixation ==false))
	            {
	            	startFixationTimestamp = System.currentTimeMillis();
	            	measuringFixation = true;
	            	firstFixation=false;
	            }
	
	            if (gazeData.isFixated==false) measuringFixation = false;
	            
	            if (measuringFixation == true)
	            {
	            	opFixationTime.sendData(ConversionUtils.intToBytes((int)(System.currentTimeMillis() - startFixationTimestamp)));
	            	if ((System.currentTimeMillis() - startFixationTimestamp > propFixationTime) && (firstFixation==false))
	            	{
	            		etpFixation.raiseEvent(); 
	            		firstFixation=true;
	            	}
	            }
            }
        }
    }
     
    

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
       
    	  boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
    	  
     	  gm.addGazeListener(gazeListener);

    	  super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          gm.removeGazeListener(gazeListener);
          gm.deactivate();

          super.stop();
      }
}