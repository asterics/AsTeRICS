
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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.eyetracker;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.asterics.component.sensor.eyetracker.POSIT.trVectorclass;
import eu.asterics.mw.data.*;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the calibration thread for the eyetracker plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 8, 2012
 *         Time: 11:14:00 PM
 */


public class CalibrationGenerator implements Runnable
{

	int DEBUG_OUTPUT=0;
	
	final int ACTION_SETCURSOR=0;
	final int ACTION_START_CALIB=1;
	final int ACTION_GET_CALIBVALUES=2;

	int calibRow, calibCol;
	int action;

    private final int WAVE_BUFFER_SIZE = 524288; // 128Kb 

    int yMin,yMax,xMin,xMax;
    int calibColumns,calibRows;
	int calibXStep=0;
	int calibYStep=0;
	
	int calibXLocation;
	int calibYLocation;
	
	private final int CAL_ARRAY_SIZE = 20;
	private final int MAX_CALIB_ERROR = 200;

	private int[] calValuesX = new int[CAL_ARRAY_SIZE];
    private int[] calValuesY = new int[CAL_ARRAY_SIZE];
    private int calPos,calEntries;

	public int xCalMin,xCalMax,yCalMin,yCalMax;
	public int xAvg,yAvg;

    
	
	Thread t;	
	long startTime,currentTime;
	boolean active=false;
	int count=0; 

	final EyetrackerInstance owner;

	public class calibPoint {
	    public int xLocation;
	    public int yLocation;
	    public int eyeX;
	    public int eyeY;
	     
	    // Class constructor
	    public calibPoint()
	    {
	        xLocation = 0;
	        yLocation = 0;
	        eyeX=10000;
	        eyeY=10000;
	    }
	   }

	private calibPoint[][] calibMatrix; 

	
	
	
	/**
	 * The class constructor.
	 */
	public CalibrationGenerator(final EyetrackerInstance owner)
	{
		this.owner = owner;
		
		   calibMatrix = new calibPoint[owner.propCalibColumns+1][owner.propCalibRows+1];
		   for(int i = 0; i <= owner.propCalibColumns; i++)
		   {	   
			   for(int j = 0; j <= owner.propCalibRows; j++)
			   {	   
				   calibMatrix[i][j]=new calibPoint();		  
			   }
		   }

	}

	   
    private void playWavFile(String filename)
    {
        File soundFile = new File(filename);
        if (!soundFile.exists()) { 
        	return;
        } 

        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) { 
            e1.printStackTrace();
            return;
        } catch (IOException e1) { 
            e1.printStackTrace();
            return;
        } 

        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) { 
            e.printStackTrace();
            return;
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        } 

        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[WAVE_BUFFER_SIZE];

        try { 
            while (nBytesRead != -1) { 
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
            e.printStackTrace();
            return;
        } finally { 
            auline.drain();
            auline.close();
        }
   
    }
    
    public void updateCalibParams(int propXMin, int propXMax, int propYMin, int propYMax, int propCalibColumns, int propCalibRows)
    {
    	xMin=propXMin;
    	xMax=propXMax;
    	yMin=propYMin;
    	yMax=propYMax;
    	calibColumns=propCalibColumns;
    	calibRows=propCalibRows;
    	calibYStep=(yMax-yMin)/calibRows;
    	calibXStep=(xMax-xMin)/calibRows;
    }
    
	/**
	 * called when calibration is started.
	 */
	public void startCalibration()	
	{	
		System.out.println("Starting Calibration\n");

		calibCol=0;
		calibRow=0;

		calPos=0;
		calEntries=0;
		action=ACTION_SETCURSOR;
		startTime=System.currentTimeMillis();
		active=true;
		owner.state=owner.STATE_WAIT_FOR_NEXT_CALIBPOINT;
		AstericsThreadPool.instance.execute(this);
	}


	/**
	 * called when model is stopped or paused.
	 */
	public void stopCalibration()	
	{	
		active=false;
	}


	/**
	 * the time generation thread.
	 */
	public void run()
	{
		
		while(active==true)
		{
			currentTime=System.currentTimeMillis()-startTime;

			if (currentTime>owner.propTimePeriod) 
			{
				startTime=System.currentTimeMillis();
				//			owner.etpPeriodFinished.raiseEvent();
			    //			owner.bridge.calibrate();
	
				switch (action)
				{
					case ACTION_SETCURSOR:				
						System.out.println("Setting cursor for calibration point "+calibCol+"/"+calibRow+"\n");
						
						calibXLocation=xMin+calibCol*calibXStep;
						calibYLocation=yMin+calibRow*calibYStep;
						if (calibCol==0) calibXLocation+=10;
						if (calibCol==owner.propCalibColumns) calibXLocation-=20;
						if (calibRow==0) calibYLocation+=10;
						if (calibRow==owner.propCalibRows) calibYLocation-=20;

						owner.setCursor(calibXLocation,calibYLocation);
						playWavFile("./data/sounds/4.wav");
						action=ACTION_START_CALIB;
						break;
							
					case ACTION_START_CALIB:
						System.out.println("starting calibration of point "+calibCol+"/"+calibRow);
						calPos=0; calEntries=0;
						action=ACTION_GET_CALIBVALUES;
						owner.state=owner.STATE_CALIBRATION;
						break;
						
					case ACTION_GET_CALIBVALUES:
						  // wait for eye values
						System.out.println("Waiting for stable eye coordinates, XError= "+(xCalMax-xCalMin)+", YError = "+(yCalMax-yCalMin));
						break;
				}

			}
			else
			{
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}

			}

		}
	}


    public void captureCalibrationValues(int eyeX, int eyeY)
    {
    	calValuesX[calPos]=eyeX;
    	calValuesY[calPos]=eyeY;
    	
        calPos= (calPos+1) % CAL_ARRAY_SIZE;
        if (calEntries<CAL_ARRAY_SIZE-1) calEntries++;
        else
        {
        	calEntries=0;
	    	xCalMin=yCalMin=10000;
	    	xCalMax=yCalMax=-10000;
	    	xAvg=yAvg=0;
        	
        	for (int i=0;i<CAL_ARRAY_SIZE;i++)
        	{
        		xAvg+=calValuesX[i];
        		yAvg+=calValuesY[i];
        		if (calValuesX[i]<xCalMin) xCalMin=calValuesX[i];
        		if (calValuesX[i]>xCalMax) xCalMax=calValuesX[i];
        		if (calValuesY[i]<yCalMin) yCalMin=calValuesY[i];
        		if (calValuesY[i]>yCalMax) yCalMax=calValuesY[i];
        		
        	}
        	xAvg/=CAL_ARRAY_SIZE;
        	yAvg/=CAL_ARRAY_SIZE;
        	
        	if ((xCalMax-xCalMin < MAX_CALIB_ERROR) && (yCalMax-yCalMin < MAX_CALIB_ERROR))
        	{	
				owner.state=owner.STATE_WAIT_FOR_NEXT_CALIBPOINT;  // point done: stop feeding eye values for calibration

    			calibMatrix[calibCol][calibRow].eyeX=xAvg;
    			calibMatrix[calibCol][calibRow].eyeY=yAvg;

    			if (owner.propTrackingMode == owner.MODE_EYETRACKING)
    			{
    				// use plain screen coordinates
	    			calibMatrix[calibCol][calibRow].xLocation=calibXLocation;
	    			calibMatrix[calibCol][calibRow].yLocation=calibYLocation;
    			}
    			else if (owner.propTrackingMode == owner.MODE_EYETRACKING_HEADPOSE)
    			{
    				//POSIT Code begin
    				trVectorclass tmpclass = owner.positObj.new trVectorclass();    				
    				tmpclass = owner.positObj.readTRvalues();    				
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].tvecx = tmpclass.tvecx;
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].tvecy = tmpclass.tvecy;
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].tvecz = tmpclass.tvecz;
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].rvecx = tmpclass.rvecx;
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].rvecy = tmpclass.rvecy;
    				owner.positObj.calibTRVecMatrix[calibCol][calibRow].rvecz = tmpclass.rvecz;    				
    				owner.positObj.calcAvgTRval(calibCol, calibRow);
    				//POSIT Code end
    				
    				calibMatrix[calibCol][calibRow].xLocation=calibXLocation;
	    			calibMatrix[calibCol][calibRow].yLocation=calibYLocation;	    	
					
    			}
    			
    			int calibError=0;
    			
    			for (int i=0;i<calibCol;i++) if (calibMatrix[i][calibRow].eyeX > xAvg) calibError=1;
    			for (int i=0;i<calibRow;i++) if (calibMatrix[calibCol][i].eyeY > yAvg) calibError=1;
    			
    			if (calibError==0)
    			{
					calibCol++;
					if (calibCol>owner.propCalibColumns)
					{ 
						calibCol=0; 
						calibRow++; 
						if (calibRow>owner.propCalibRows) 
						{
							playWavFile("./data/sounds/6.wav");
							System.out.println("Calibration done");
							owner.state=owner.STATE_RUNNING;
							active=false;
						}
					}
					action=ACTION_SETCURSOR;
    			}
    			else
    			{
					playWavFile("./data/sounds/7.wav");
					System.out.println("Calibration error, please retry");
					active=false;
    			}
    	    }
        }
    }

    double avgX[]= new double[20];
    double avgY[]= new double[20];
    double avgXuncomp[]= new double[20];
    double avgYuncomp[]= new double[20];
    int avgPos=0;
    int counter =0;

    public void getCalibratedLocations(int xe, int ye)
    {
    	int yIndex,xIndex,row;
    	double x1,x2,y1,y2,x3,y3;
    	double k1,k2,ke,d1,d2,xi,f1,f2;
    	double actX,actY;
    	
    	int ln,rn,un,dn;
    	
    	for (yIndex=0;(yIndex<=calibRows) && (ye>calibMatrix[0][yIndex].eyeY);yIndex++) ;
    	if (yIndex<=calibRows) row=yIndex; else row=calibRows;
    	for (xIndex=0;(xIndex<=calibColumns) && (xe>calibMatrix[xIndex][row].eyeX);xIndex++) ;
    	

    	
    	ln=xIndex-1; if (ln<0) ln=0;
    	rn=xIndex; if (rn>calibColumns) rn=calibColumns;
    	un=yIndex-1; if (un<0) un=0;
    	dn=yIndex; if (dn>calibRows) dn=calibRows;

    	if (counter==15)	
    	{
    		System.out.println("\nxe/ye: "+xe+"/"+ye);
    		System.out.println("LeftUp:"+ln+"/"+un+"  RightUp:"+rn+"/"+un+"  LeftDown:"+ln+"/"+dn+"  RightDown:"+rn+"/"+dn);
        }
    	
    	
    	if ((ln!=rn) && (un!=dn))
    	{
    		x1=calibMatrix[ln][dn].eyeX;
    		y1=calibMatrix[ln][dn].eyeY;
    		x3=calibMatrix[rn][un].eyeX;
    		y3=calibMatrix[rn][un].eyeY;


    		k2=(y3-y1)/(x3-x1);
    		ke=(ye-y1)/(xe-x1);
    		
    		if (ke>k2)
    		{
    			if (counter==15) System.out.println("lower half");
        		x2=calibMatrix[rn][dn].eyeX;
        		y2=calibMatrix[rn][dn].eyeY;
    		}
    		else
    		{
    			if (counter==15) System.out.println("upper half");
        		x2=calibMatrix[ln][un].eyeX;
        		y2=calibMatrix[ln][un].eyeY;
    		}

    		if (counter==15)	
    		{
    			System.out.println("x1/y1:"+x1+"/"+y1+"  x2/y2:"+x2+"/"+y2+"  x3/y3:"+x3+"/"+y3);
        		System.out.println("k2:"+k2);
        		System.out.println("ke:"+ke);
    		}

   		
    		k1=(y2-y1)/(x2-x1);
    		d1=y1-k1*x1;
    		d2=ye-k2*xe;
    		
    		xi=(d2-d1)/(k1-k2);
    		
    		f1=(xi-x1)/(x2-x1);
    		f2=(xe-xi)/(x3-x1);
    		
    		if (counter==15)
    		{ 
    			System.out.println("x/yLocation:"+calibMatrix[ln][dn].xLocation+"/"+calibMatrix[ln][dn].yLocation);
    			System.out.println("f1/f2:"+f1+"/"+f2);
    		}

    		if (ke>k2)
    		{
	    		actX=(double)calibMatrix[ln][dn].xLocation+f1*(double)calibXStep+f2*(double)calibXStep;    		
	    		actY=(double)calibMatrix[ln][dn].yLocation-f2*(double)calibYStep; 
    		}
    		else
    		{
	    		actX=(double)calibMatrix[ln][dn].xLocation+f2*(double)calibXStep;    		
	    		actY=(double)calibMatrix[ln][dn].yLocation-f1*(double)calibYStep-f2*(double)calibYStep; 
    		}
    	}
    	else
    	{
    		
    		if ((ln==rn) && (dn==un))   // corner point
    		{
    			if (counter==15) System.out.println("cornercase");
		    	actX=calibMatrix[ln][un].xLocation;
		    	actY=calibMatrix[ln][un].yLocation;
    		}
    		else if (ln==rn)  // point is on vertical line
    		{
    			if (counter==15) System.out.println("vertical bordercase");

        		y1=calibMatrix[ln][un].eyeY;
        		y2=calibMatrix[ln][dn].eyeY;
    			
    			f1=(double)(ye-y1)/(double)(y2-y1);

	    		actX=calibMatrix[ln][un].xLocation; //+f2*deltaX);    		
	    		actY=(double)calibMatrix[ln][un].yLocation+f1*(double)calibYStep; 
    		}
    		else   // point is on horizontal line
    		{
    			if (counter==15) System.out.println("horizontal bordercase");

        		x1=calibMatrix[ln][un].eyeX;
        		x2=calibMatrix[rn][un].eyeX;
    			
    			f1=(double)(xe-x1)/(double)(x2-x1);

	    		actX=(double)calibMatrix[ln][un].xLocation+f1*(double)calibXStep;     		
	    		actY=calibMatrix[ln][un].yLocation; //+f2*deltaY);
    		}
    	}
    	    	
    	avgX[avgPos]=actX;
    	avgY[avgPos]=actY;
    	if (++avgPos>owner.propAveraging) avgPos=0;
    	
    	double sumX=0, sumY=0;
    	for (int z=0;z<owner.propAveraging;z++)
    	{
    		sumX+=avgX[z];
    		sumY+=avgY[z];    		
    	}

    	owner.calibratedX=(int)(sumX/owner.propAveraging);
    	owner.calibratedY=(int)(sumY/owner.propAveraging);

    	if (DEBUG_OUTPUT == 1)
    	{
	    	if (counter==15)	
	    	{
	    		System.out.println("cal x/y-value: "+owner.calibratedX+"/"+owner.calibratedY);
	    		counter=0;
	        }
	    	else counter++;   	
    	}
    	
    }
    
    
    public void getCalibratedLocationsWithPOSIT(int xe, int ye)
    {
    	trVectorclass currentpose = owner.positObj.readTRvalues();
    	int yIndex,xIndex,row;
    	double x1,x2,y1,y2,x3,y3;
    	double k1,k2,ke,d1,d2,xi,f1,f2;
    	double actX,actY, uncompensatedX, uncompensatedY;
    	
    	int ln,rn,un,dn;
    	
    	for (yIndex=0; (yIndex<=calibRows) && (ye>calibMatrix[0][yIndex].eyeY); yIndex++) ;
    	if (yIndex<=calibRows) row=yIndex; else row=calibRows;
    	for (xIndex=0; (xIndex<=calibColumns) && (xe>calibMatrix[xIndex][row].eyeX); xIndex++) ;
    	
    	
/*    	ln=xIndex-1; if (ln<0) ln=0;
    	rn=xIndex; if (rn>calibColumns) rn=calibColumns;
    	un=yIndex-1; if (un<0) un=0;
    	dn=yIndex; if (dn>calibRows) dn=calibRows;
*/
    	
    	int bc=0;
    	
    	ln=xIndex-1;
    	rn=xIndex;
    	un=yIndex-1;
    	dn=yIndex;
   	
    	 if (ln<0) {ln++;rn++; bc=1;}
    	 if (rn>calibColumns) {ln--; rn--; bc=2;}
    	 if (un<0) {un++; dn++; bc=3;}
    	 if (dn>calibRows) {un--;dn--; bc=4;}
    	
    	
    	if (counter==15)	
    	{
    		System.out.println("\nxe/ye: "+xe+"/"+ye);
    		System.out.println("LeftUp:"+ln+"/"+un+"  RightUp:"+rn+"/"+un+"  LeftDown:"+ln+"/"+dn+"  RightDown:"+rn+"/"+dn);
    		if (bc>0) System.out.println("\n ** Bordercase ("+bc+") **");
        }
    	
/*    	
    	if ((ln!=rn) && (un!=dn))
    	{
*/    	
		x1=calibMatrix[ln][dn].eyeX;
		y1=calibMatrix[ln][dn].eyeY;
		x3=calibMatrix[rn][un].eyeX;
		y3=calibMatrix[rn][un].eyeY;

		k2=(y3-y1)/(x3-x1);
		ke=(ye-y1)/(xe-x1);
		
		if (ke>k2)
		{
			if (counter==15) System.out.println("lower half");
    		x2=calibMatrix[rn][dn].eyeX;
    		y2=calibMatrix[rn][dn].eyeY;
		}
		else
		{
			if (counter==15) System.out.println("upper half");
    		x2=calibMatrix[ln][un].eyeX;
    		y2=calibMatrix[ln][un].eyeY;
		}

		if (counter==15)	
		{
			System.out.println("x1/y1:"+x1+"/"+y1+"  x2/y2:"+x2+"/"+y2+"  x3/y3:"+x3+"/"+y3);
    		System.out.println("k2:"+k2);
    		System.out.println("ke:"+ke);
		}

	
		k1=(y2-y1)/(x2-x1);
		d1=y1-k1*x1;
		d2=ye-k2*xe;
		
		xi=(d2-d1)/(k1-k2);
		
		f1=(xi-x1)/(x2-x1);
		f2=(xe-xi)/(x3-x1);
		
		if (counter==15)
		{ 
			System.out.println("x/yLocation:"+calibMatrix[ln][dn].xLocation+"/"+calibMatrix[ln][dn].yLocation);
			System.out.println("f1/f2:"+f1+"/"+f2);
		}

		if (ke>k2)
		{
    		actX=(double)calibMatrix[ln][dn].xLocation+f1*(double)calibXStep+f2*(double)calibXStep;
    		actY=(double)calibMatrix[ln][dn].yLocation-f2*(double)calibYStep;
    		
    		uncompensatedX = actX;
    		uncompensatedY = actY;
    		actX-=(currentpose.tvecx-owner.positObj.calibTRVecMatrix[ln][dn].tvecx)*owner.positObj.getDpmm();
    		actY-=(currentpose.tvecy-owner.positObj.calibTRVecMatrix[ln][dn].tvecy)*owner.positObj.getDpmm();
		}
		else
		{
    		actX=(double)calibMatrix[ln][dn].xLocation+f2*(double)calibXStep;    		
    		actY=(double)calibMatrix[ln][dn].yLocation-f1*(double)calibYStep-f2*(double)calibYStep; 
    		
    		uncompensatedX = actX;
    		uncompensatedY = actY;
    		actX-=(currentpose.tvecx-owner.positObj.calibTRVecMatrix[ln][dn].tvecx)*owner.positObj.getDpmm();
    		actY-=(currentpose.tvecy-owner.positObj.calibTRVecMatrix[ln][dn].tvecy)*owner.positObj.getDpmm();
		}
/*
    }
    	else
    	{    		
    		if ((ln==rn) && (dn==un))   // corner point
    		{
    			if (counter==15) System.out.println("cornercase");
		    	actX=calibMatrix[ln][un].xLocation;
		    	actY=calibMatrix[ln][un].yLocation;
		    	
		    	uncompensatedX = actX;
	    		uncompensatedY = actY;
		    	actX-=(currentpose.tvecx-owner.positObj.calibTRVecMatrix[ln][un].tvecx)*owner.positObj.getDpmm();
	    		actY-=(currentpose.tvecy-owner.positObj.calibTRVecMatrix[ln][un].tvecy)*owner.positObj.getDpmm();
    		}
    		else if (ln==rn)  // point is on vertical line
    		{
    			if (counter==15) System.out.println("vertical bordercase");

        		y1=calibMatrix[ln][un].eyeY;
        		y2=calibMatrix[ln][dn].eyeY;
    			
    			f1=(double)(ye-y1)/(double)(y2-y1);

	    		actX=calibMatrix[ln][un].xLocation; //+f2*deltaX);    		
	    		actY=(double)calibMatrix[ln][un].yLocation+f1*(double)calibYStep; 
	    		
	    		uncompensatedX = actX;
	    		uncompensatedY = actY;
	    		actX-=(currentpose.tvecx-owner.positObj.calibTRVecMatrix[ln][un].tvecx)*owner.positObj.getDpmm();
	    		actY-=(currentpose.tvecy-owner.positObj.calibTRVecMatrix[ln][un].tvecy)*owner.positObj.getDpmm();
    		}
    		else   // point is on horizontal line
    		{
    			if (counter==15) System.out.println("horizontal bordercase");

        		x1=calibMatrix[ln][un].eyeX;
        		x2=calibMatrix[rn][un].eyeX;
    			
    			f1=(double)(xe-x1)/(double)(x2-x1);

	    		actX=(double)calibMatrix[ln][un].xLocation+f1*(double)calibXStep;     		
	    		actY=calibMatrix[ln][un].yLocation; //+f2*deltaY);
	    		
	    		uncompensatedX = actX;
	    		uncompensatedY = actY;
	    		actX-=(currentpose.tvecx-owner.positObj.calibTRVecMatrix[ln][un].tvecx)*owner.positObj.getDpmm();
	    		actY-=(currentpose.tvecy-owner.positObj.calibTRVecMatrix[ln][un].tvecy)*owner.positObj.getDpmm();
    		}
    	}
 */
		
    	//calculate average value for the compensated coordinates (w/ POSIT)
    	avgX[avgPos]=actX;
    	avgY[avgPos]=actY;
    	if (++avgPos>owner.propAveraging) avgPos=0;
    	
    	double sumX=0, sumY=0;
    	for (int z=0;z<owner.propAveraging;z++)
    	{
    		sumX+=avgX[z];
    		sumY+=avgY[z];    		
    	}
    	owner.calibratedX=(int)(sumX/owner.propAveraging);
    	owner.calibratedY=(int)(sumY/owner.propAveraging);
    	
    	//calculate average values for the raw coordinates (w/o POSIT)
    	avgXuncomp[avgPos]=uncompensatedX;
    	avgYuncomp[avgPos]=uncompensatedY;    	
    	
    	sumX=0;
    	sumY=0;
    	for (int z=0;z<owner.propAveraging;z++)
    	{
    		sumX+=avgXuncomp[z];
    		sumY+=avgYuncomp[z];    		
    	}    	
    	owner.rawCalibratedX = (int)(sumX/owner.propAveraging);
    	owner.rawCalibratedY = (int)(sumY/owner.propAveraging);
    	
    	if (DEBUG_OUTPUT == 1)
    	{
	    	if (counter==15)	
	    	{
	    		System.out.println("cal x/y-value: "+owner.calibratedX+"/"+owner.calibratedY);
	    		counter=0;
	        }
	    	else counter++;
    	}
    }
}
