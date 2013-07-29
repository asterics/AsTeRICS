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

package eu.asterics.component.processor.basictralgorithms;

import java.util.ArrayList;
import java.util.List;
import java.math.*;

/**
 * 
 * Implements the Arithmetic Mean Algorithm for the tremor reduction.
 * The new cursor position is calculated as a average of the past cursor positions.
 * Number of past positions stored is defined by the bufferSize parameter. 
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 15, 2012
 *         Time: 10:06:15 AM
 */
class ArithmeticMeanAlgorithm extends TremorReductionBasicAlgorithm
{
	private int bufferSize;
	private ArrayList<AlgorithmPoint> buffer=new ArrayList();
	
	/**
	* The class constructor.
	*/
	public ArithmeticMeanAlgorithm()
	{
		this.bufferSize=0;
	}
	
	/**
	* The class constructor.
	* @param bufferSize size of the buffer.
	*/
	public ArithmeticMeanAlgorithm(int bufferSize)
	{
		this.bufferSize=bufferSize;
	}
	
	/**
	* Calculates the new cursor position.
	* @param inputPoint current cursor position
	* @return the new cursor position
	*/
	public AlgorithmPoint calcualteNewPoint(AlgorithmPoint inputPoint)
	{
		addNewPoint(inputPoint);
		
		
		double newX=0;
		double newY=0;
		int size=buffer.size();
		
		
		for(int i=0;i<size;i++)
		{
			newX=newX + buffer.get(i).getX();
			newY=newY + buffer.get(i).getY();
		}
		
		newX=newX/(double)size;
		newY=newY/(double)size;
		
		AlgorithmPoint outputPoint = new AlgorithmPoint((int)newX,(int)newY);
		
		int x=outputPoint.getX();
		int y=outputPoint.getY();
		
		
		if(Math.abs(newX-x)>0.5)
		{
			if(x>=0)
			{
				x++;
			}
			else
			{
				x--;
			}
			outputPoint.setX(x);
		}
		
		if(Math.abs(newY-y)>0.5)
		{
			if(y>=0)
			{
				y++;
			}
			else
			{
				y--;
			}
			outputPoint.setY(y);
		}
		
		return outputPoint;
		
		
		
	}
	
	/**
	* Sets the new buffer size.
	* @param bufferSize the new buffer size.
	*/
	public void setBufferSize(int bufferSize)
	{
		if(bufferSize>0)
		{
			this.bufferSize=bufferSize;
			
			int pointsToRemove=buffer.size()-bufferSize;
			
			if(pointsToRemove>0)
			{
				for(int i=0;i<pointsToRemove;i++)
				{
					buffer.remove(0);
				}
			}
		}
	}
	
	/*
	private void updateLastPoint(AlgorithmPoint p)
	{
		buffer.get(buffer.size()-1).setX(p.getX());
		buffer.get(buffer.size()-1).setY(p.getY());
	}*/
	
	/**
	* Adds the new new point the the buffer.
	* @param p the new point.
	*/
	private void addNewPoint(AlgorithmPoint p)
	{
		
		if(buffer.size()<bufferSize)
		{
			buffer.add(p);
		}
		else
		{
			int pointsToRemove=buffer.size()-bufferSize + 1;
			
			for(int i=0;i<pointsToRemove;i++)
			{
				buffer.remove(0);
			}
			
			buffer.add(p);
		}
	}
	
	/**
	* Removes all points from the buffer.
	*/
	public void clean()
	{
		buffer.clear();
	}
}