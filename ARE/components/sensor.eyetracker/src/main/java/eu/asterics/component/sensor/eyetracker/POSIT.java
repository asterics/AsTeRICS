
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

 	

/**	 
	   
	  POSIT related methods for head pose correction
	  
	   * @author Yat Sing Yeung 
	   *       Date: June 3rd, 2012
	 
*/  

package eu.asterics.component.sensor.eyetracker;

import eu.asterics.component.sensor.eyetracker.EyetrackerInstance;
import eu.asterics.component.sensor.eyetracker.jni.BridgePOSIT;
import eu.asterics.mw.services.AstericsErrorHandling;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.locks.*;


public class POSIT 
{
    private EyetrackerInstance owner;    
    final BridgePOSIT bridgePOSIT = new BridgePOSIT(this.owner);

    volatile private float tvecx, tvecy, tvecz;		//translation and rotation vector for data exchange
	volatile private float rvecx, rvecy, rvecz;		//units for translation: mm; for rotation: rad
		
	final Lock tvlock = new ReentrantLock();		//locks to ensure safe write and read of the vectors
	final Lock rvlock = new ReentrantLock();
	
	protected class trVectorclass {
		protected float tvecx;
		protected float tvecy;
		protected float tvecz;		
		protected float rvecx;
		protected float rvecy;
		protected float rvecz;
		
		//class constructor
		protected trVectorclass()
		{
			tvecx = 0;
			tvecy = 0;
			tvecz = 0;		
			rvecx = 0;
			rvecy = 0;
			rvecz = 0;
		}
	}
	
	//variables to calculate average values during runtime
	protected trVectorclass[][] calibTRVecMatrix;
	private float calibAvgTvecx = 0, calibAvgTvecy = 0, calibAvgTvecz = 0;	//average values of the vectors after calibration
	private float calibAvgRvecx = 0, calibAvgRvecy = 0, calibAvgRvecz = 0;
	final int avgArraySize = 10;
	private double[] deltaX_array = new double[25];
	private int cntDeltaX_array = 0;
	private double[] deltaY_array = new double[25];
	private int cntDeltaY_array = 0;
	
	private float dpmm;	//dots per millimeter
	
	private int cntPrintResult = 0;
    
	private int sendEyeCoordinates = 0;
	
	/**
	 * The class constructor.
	 */
	public POSIT(final EyetrackerInstance owner)
	{
		this.owner = owner;
		bridgePOSIT.positObj = this;
		this.setDisplayProperties(1680, 1050, 22);
		calibTRVecMatrix = new trVectorclass[owner.propCalibColumns+1][owner.propCalibRows+1];
		
		for (int i=0; i<=owner.propCalibColumns; i++)
		{
			for(int j=0; j<=owner.propCalibRows; j++)
			{
				calibTRVecMatrix[i][j] = new trVectorclass();
			}
		}		
	}
 	
    public void startPosit()
    {      
 	   
        if (bridgePOSIT.activate() == 0)
     	   AstericsErrorHandling.instance.reportInfo(this.owner, "Could not init POSIT");
     	else
     	   AstericsErrorHandling.instance.reportDebugInfo(this.owner, "POSIT activated");
        
    }

    public void pausePosit()
    {
        bridgePOSIT.deactivate();        
    }

    public void resumePosit()
    {    	
        if (bridgePOSIT.activate() == 0)
      	   AstericsErrorHandling.instance.reportInfo(this.owner, "Could not init POSIT");
      	else
      	   AstericsErrorHandling.instance.reportDebugInfo(this.owner, "POSIT activated");
            	
    }

    public void stopPosit()
    {
        bridgePOSIT.deactivate();
    }
    
    /**
     * starts the evaluation of the systems accuracy
     * sets switch to send the eye coordinates to the native code
     */
    protected synchronized void startEvaluation()
    {
    	sendEyeCoordinates = 1;
    	int errormsg;
    	Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
    	errormsg = bridgePOSIT.startEval(screenResolution.width, screenResolution.height);
    	if(errormsg != 1)	AstericsErrorHandling.instance.reportInfo(this.owner, String.format("could not start evaluation: %x", errormsg));    		
    }
    
    /**
     * sets switch to stop sending the eye coordinates
     */
    public synchronized void stopSendEyeCoordinates()
    {
    	//AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("JVM: stopped sending eye coordinates to native code"));
    	sendEyeCoordinates = 0;    	
    }
    
    /**
     * Returns the value of the switch "sendCamCoordinates"
     * @return sendCamCoordinates
     */
    protected synchronized int sendEyeCoordinates()
    {
    	return sendEyeCoordinates;
    }
    
    /**
     * opens / closes the information window
     */
    protected void togglePoseInfoWindow()
    {
    	int errormsg;
    	errormsg = bridgePOSIT.togglePoseInfoWindow();
    	if(errormsg != 1)	AstericsErrorHandling.instance.reportInfo(this.owner, String.format("could not toggle Info Window: %x", errormsg));
    }
        
    /**
     * store the translation vectors
     * @param x translation vector X
     * @param y translation vector Y
     * @param z translation vector Z
     */
    public void writeTvec(float x, float y, float z)
    {
    	tvlock.lock();    	
    	tvecx = x;
    	tvecy = y;
    	tvecz = z;    	
    	tvlock.unlock();    	
    }
    
    /**
     * store the rotation vectors
     * @param x rotation vector X
     * @param y rotation vector Y
     * @param z rotation vector Z
     */
    public void writeRvec(float x, float y, float z)
    {
    	rvlock.lock();    	
    	rvecx = x;
    	rvecy = y;
    	rvecz = z;    	
    	rvlock.unlock();
    }
    
    /**
     * read the translation and rotation vectors
     * @return a class with the vectors
     */
    public trVectorclass readTRvalues()
    {
    	trVectorclass tmpclass = new trVectorclass();
    	
    	rvlock.lock();
    	tvlock.lock();
    	tmpclass.tvecx = tvecx;
    	tmpclass.tvecy = tvecy;
    	tmpclass.tvecz = tvecz;  	    	
    	tmpclass.rvecx = rvecx;
    	tmpclass.rvecy = rvecy;
    	tmpclass.rvecz = rvecz;
    	rvlock.unlock();
    	tvlock.unlock();
    	
    	return tmpclass;
    };
    
    /**
     * calculates dots per millimeter, result is stored internally
     * @param resH horizontal display resolution (i.e. 1920)
     * @param resV vertical display resolution	(i.e. 1080)
     * @param size diagonal display size in inch / "Zoll" (i.e. 24")
     */
    public void setDisplayProperties(int resH, int resV, double size)
    {
    	double diagonalPixels;
    	diagonalPixels = Math.sqrt(resV*resV+resH*resH);
    	dpmm = (float) ((diagonalPixels/size)/25.4);
    	AstericsErrorHandling.instance.reportDebugInfo(this.owner,(String.format("dots per millimeter: %.2f ", dpmm)));
    }
    
    /**
     * returns Dots per Millimeter
     * @return
     */
    public float getDpmm()
    {
    	return dpmm;
    }
        
    /**
     * calculates the average pose with the previously stored rotation and translation vectors (stored in calibTRVecMatrix)
     * @param column amount of columns-1
     * @param row amount of rows-1
     */
    protected void calcAvgTRval(int column, int row)
    {
    	int tmpCalibPoints = (column+1)*(row+1);
    	calibAvgTvecx = 0;
    	calibAvgTvecy = 0;
    	calibAvgTvecz = 0;
    	calibAvgRvecx = 0; 
    	calibAvgRvecy = 0;
    	calibAvgRvecz = 0;
    	
    	for (int i=0; i<=column; i++)
		{
			for(int j=0; j<=row; j++)
			{
				calibAvgTvecx = calibAvgTvecx + calibTRVecMatrix[i][j].tvecx;
				calibAvgTvecy = calibAvgTvecy + calibTRVecMatrix[i][j].tvecy;
				calibAvgTvecz = calibAvgTvecz + calibTRVecMatrix[i][j].tvecz;								
				calibAvgRvecx = calibAvgRvecx + calibTRVecMatrix[i][j].rvecx;
				calibAvgRvecy = calibAvgRvecy + calibTRVecMatrix[i][j].rvecy;
				calibAvgRvecz = calibAvgRvecz + calibTRVecMatrix[i][j].rvecz;		
			}
		}
		
    	calibAvgRvecx = calibAvgRvecx/tmpCalibPoints;
    	calibAvgRvecy = calibAvgRvecy/tmpCalibPoints;
    	calibAvgRvecz = calibAvgRvecz/tmpCalibPoints;
    	calibAvgTvecx = calibAvgTvecx/tmpCalibPoints;
    	calibAvgTvecy = calibAvgTvecy/tmpCalibPoints;
    	calibAvgTvecz = calibAvgTvecz/tmpCalibPoints;
    }
        
    /**
     * calculates the x and y deviation due to head movements
     * translation and rotation around x and y axis are considered (rotation around the z axis is not considered)
     * calibratedX and calibratedY of eyetrackerInstance are corrected with the deviation
     */
    protected void doSimplePOSITCorrection()
    {
    	double deltaX = 0, deltaY = 0;
    	
    	trVectorclass tmp = new trVectorclass();
    	tmp = readTRvalues();
    	
    	//******* Method 1: calculate the correction factor with the angle	
    	//deltaX = (Math.tan(tmp.rvecy)*(tmp.tvecz-70)) - (Math.tan(calibAvgRvecy)*(calibAvgTvecz-70));
    	//deltaY = (Math.tan(-tmp.rvecx)*(tmp.tvecz-70)) - (Math.tan(-calibAvgRvecx)*(calibAvgTvecz-70));
    	
    	//******* addition to method 1: translation can be added to the calculation for smoothing the result
    	//deltaX = (deltaX + 2*(tmp.tvecx-calibAvgTvecx)) / 3;
    	//deltaY = (deltaY + 2*(tmp.tvecy-calibAvgTvecy)) / 3;
    	
    	//******* Method 2: calculate the correction factor with the translation
    	deltaX = tmp.tvecx-calibAvgTvecx;
    	deltaY = tmp.tvecy-calibAvgTvecy;
    	    	
    	if (cntPrintResult == 25)
    	{
    		AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("deltaX (mm): %.2f", deltaX));
    		AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("deltaY (mm): %.2f", deltaY));
    		cntPrintResult = 0;
    	}
    	else cntPrintResult++;
    	
    	deltaX = deltaX * dpmm;
    	deltaY = deltaY * dpmm;
    	
    	owner.calibratedX = owner.calibratedX - (int) calcAvgDeltaX(deltaX);
    	owner.calibratedY = owner.calibratedY - (int) calcAvgDeltaY(deltaY);
    }
    
    /**
     * calculates an average value for x-axis correction
     * @param x
     * @return
     */
    private double calcAvgDeltaX (double x)
    {
    	double avgX = 0;
    	double low = x;
    	double high = x;
    	
    	deltaX_array[cntDeltaX_array] = x;		//write current value
    	
    	if (cntDeltaX_array == (avgArraySize-1))	//increment index
    		cntDeltaX_array = 0;
    	else
    		cntDeltaX_array++;
    	
    	 	
    	for (int i = 0; i<avgArraySize; i++)		//sum values, search for highest and lowest value
    	{
    		avgX += deltaX_array[cntDeltaX_array];
    		
    		if (deltaX_array[cntDeltaX_array]>high)
    		{
    			high = deltaX_array[cntDeltaX_array];
    		}
    		else
    		{
    			if(deltaX_array[cntDeltaX_array]<low)
    				low = deltaX_array[cntDeltaX_array];
    		}
    	}
    	
    	avgX = ((avgX - low) - high)/(avgArraySize-2);    	//calculate average without highest and lowest value
    	return avgX;
    }
    
    /**
     * calculates an average value for y-axis correction
     * @param y
     * @return
     */
    private double calcAvgDeltaY (double y)
    {
    	double avgY = 0;
    	double low = y;
    	double high = y;
    	
    	deltaY_array[cntDeltaY_array] = y;
    	
    	if (cntDeltaY_array == (avgArraySize-1))
    		cntDeltaY_array = 0;
    	else
    		cntDeltaY_array++;    	    	    	
    	
    	for (int i = 0; i<avgArraySize; i++)
    	{
    		avgY += deltaY_array[cntDeltaY_array];
    		
    		if (deltaY_array[cntDeltaY_array]>high)
    		{
    			high = deltaY_array[cntDeltaY_array];
    		}
    		else
    		{
    			if(deltaY_array[cntDeltaY_array]<low)
    				low = deltaY_array[cntDeltaY_array];
    		}
    	}
    	
    	avgY = ((avgY - low) - high)/(avgArraySize-2);    	
    	
    	return avgY;
    }
    
    /**
     * sends eye coordinates to native code
     */
    public void sendEvalValues()
    {
    	bridgePOSIT.sendEvalParams(owner.rawCalibratedX, owner.rawCalibratedY, owner.calibratedX, owner.calibratedY);
    	//bridgePOSIT.sendEvalParams(100, 110, 300, 310);
    	//AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("sending eval values"));
    }
    
}
