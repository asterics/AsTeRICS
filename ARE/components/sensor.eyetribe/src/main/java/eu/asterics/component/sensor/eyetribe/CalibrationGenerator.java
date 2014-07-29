
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

package eu.asterics.component.sensor.eyetribe;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.Point;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.asterics.mw.data.*;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 *   Implements the calibration thread for the eyetribe plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 8, 2012
 *         Time: 11:14:00 PM
 */


public class CalibrationGenerator implements Runnable
{

	int DEBUG_OUTPUT=0;
    private final int WAVE_BUFFER_SIZE = 524288; // 128Kb 
    
    Robot rob;

	Thread t;	
	long startTime,currentTime;
	boolean active=false;

	final EyeTribeInstance owner;

	public class calibPoint {
	    public int xLocation;
	    public int yLocation;
	    public int xOffset;
	    public int yOffset;
	     
	    // Class constructor
	    public calibPoint(int i)
	    {
	    	int width=Toolkit.getDefaultToolkit().getScreenSize().width;
	    	int height=Toolkit.getDefaultToolkit().getScreenSize().height;
	        xLocation = width/2*(i%3);
	        yLocation = height/2*(int)(i/3);
	        
			if (xLocation==0) xLocation+=10;
			if (xLocation==width) xLocation-=20;
			if (yLocation==0) yLocation+=10;
			if (yLocation==height) yLocation-=20;

	        xOffset=0;
	        yOffset=0;
	    }
	    public calibPoint(int x, int y, int xo, int yo)
	    {
	        xLocation = x;
	        yLocation = y;
	        xOffset=xo;
	        yOffset=yo;
	    }
	   }

	private calibPoint[] calibPoints; 
	List<calibPoint> offsetPoints = new ArrayList<calibPoint>();
	
	/**
	 * The class constructor.
	 */
	public CalibrationGenerator(final EyeTribeInstance owner)
	{
			this.owner = owner;
			
	    	try {
	       	 	rob = new Robot();
	            rob.setAutoDelay(0);
	       	}
	       	catch(AWTException e){e.printStackTrace();}

		
		   calibPoints = new calibPoint[9];
		   for(int i = 0; i < 9; i++)
			  calibPoints[i]=new calibPoint(i);
	}

	   
    public void playWavFile(String filename)
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
    
    
	/**
	 * called when calibration is started.
	 */
	public void startCalibration()	
	{	
		System.out.println("Starting Calibration\n");
		offsetPoints.clear();
		startTime=System.currentTimeMillis();
		active=true;
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
	    owner.gm.getInstance().calibrationStart(9, null); 
		
		for (calibPoint point : calibPoints)
		{
			currentTime=System.currentTimeMillis()-startTime;

			try {
	
				if (active == false) break;
			    rob.mouseMove(point.xLocation , point.yLocation);
				System.out.println("starting calibration of point "+point.xLocation+"/"+point.yLocation);
				Thread.sleep(200);				
				playWavFile("./data/sounds/4.wav");

				owner.gm.getInstance().calibrationPointStart(point.xLocation, point.yLocation);
				Thread.sleep(800);
			    owner.gm.getInstance().calibrationPointEnd();
				playWavFile("./data/sounds/5.wav");


				} catch (InterruptedException e) {}
		}
		owner.calibrationDone();
	}

	public int removeOffsetPoint()	
	{	
		if (offsetPoints.size()>0)
			offsetPoints.remove(offsetPoints.size()-1);
		return(offsetPoints.size());
	}

	public void newOffsetPoint(int x,int y,int xOffset,int yOffset)	
	{	
		int dist;
		Iterator<calibPoint> iterator = offsetPoints.iterator();
		while (iterator.hasNext())
		{
			calibPoint act=iterator.next();
			dist=(int)Math.sqrt((act.xLocation-x)*(act.xLocation-x)+(act.yLocation-y)*(act.yLocation-y));
			if (dist<owner.propOffsetCorrectionRadius)
				iterator.remove();
		} 
		offsetPoints.add(new calibPoint(x,y,xOffset,yOffset));
		System.out.println("add calib point "+x+"/"+y+" with offset "+xOffset+"/"+yOffset+", new list has "+offsetPoints.size()+" elements.");
	}

	public Point calcOffset(int x, int y)	
	{	
		double dist;

		for (calibPoint act: offsetPoints )
		{
			dist=Math.sqrt((act.xLocation-x)*(act.xLocation-x)+(act.yLocation-y)*(act.yLocation-y));
			if (dist<owner.propOffsetCorrectionRadius)
			{
				double factor = 1-(dist/(float)owner.propOffsetCorrectionRadius);
				Point result=new Point((int)(act.xOffset*factor),(int)(act.yOffset*factor));
				System.out.println("correction "+(int)(factor*100)+"% , values "+result.x+"/"+result.y);

				return(result);				
			}
		}
		return(new Point (0,0));
	}

}
