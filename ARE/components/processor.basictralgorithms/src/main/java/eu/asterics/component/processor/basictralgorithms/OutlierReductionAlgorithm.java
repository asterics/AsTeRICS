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

import java.math.*;

/**
 * 
 * Implements the Outlier Reduction algorithm for absolute position points. 
 * In this algorithm, if the distance between two followed cursor positions is greater than the the maximum allowed distance, 
 * the distance between them will be reduced to the maximum allowed distance.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 15, 2012
 *         Time: 10:06:15 AM
 */
class OutlierReductionAlgorithm extends TremorReductionBasicAlgorithm
{
	
	private double distance;
	private AlgorithmPoint lastPoint=null;
	
	/**
	* The class constructor.
	*/
	public OutlierReductionAlgorithm()
	{
		distance=10.0;
	}
	
	/**
	* The class constructor.
	* @param distance maximum allowed distance
	*/
	public OutlierReductionAlgorithm(double distance)
	{
		this.distance=distance;
	}
	
	/**
	* Calculates the new cursor position.
	* @param inputPoint current cursor position
	* @return new cursor position
	*/
	public AlgorithmPoint calcualteNewPoint(AlgorithmPoint inputPoint)
	{
		if(lastPoint==null)
		{
			lastPoint = new AlgorithmPoint(inputPoint);
			return inputPoint;
		}
		
		double factor=(lastPoint.getX()-inputPoint.getX())*(lastPoint.getX()-inputPoint.getX())+(lastPoint.getY()-inputPoint.getY())*(lastPoint.getY()-inputPoint.getY());
		factor=Math.sqrt(factor);
		
		if(factor>distance)
		{
			double x=(double)(lastPoint.getX()-inputPoint.getX())/factor;
			double y=(double)(lastPoint.getY()-inputPoint.getY())/factor;
			
			int newX = (int) ((double)lastPoint.getX()-x*distance);
			int newY = (int) ((double)lastPoint.getY()-y*distance);
			
			if((double)lastPoint.getX()-x*distance-(double)newX>0.5)
			{
				newX++;
			}
			
			if((double)lastPoint.getY()-y*distance-(double)newY>0.5)
			{
				newY++;
			}
			
			AlgorithmPoint outputPoint = new AlgorithmPoint(newX,newY);
			lastPoint = new AlgorithmPoint(outputPoint);
			return outputPoint;
			
		}
		else
		{
			lastPoint = new AlgorithmPoint(inputPoint);
			return inputPoint;
		}
		
	}
	
	/**
	* Sets the new maximum distance.
	* @param distance maximum allowed distance
	*/
	public void SetDistance(double distance)
	{
		if(distance>0)
		{
			this.distance=distance;
		}
	}
	
	/**
	* Removes the last cursor position.
	*/
	public void clean()
	{
		 lastPoint=null;
	}
}