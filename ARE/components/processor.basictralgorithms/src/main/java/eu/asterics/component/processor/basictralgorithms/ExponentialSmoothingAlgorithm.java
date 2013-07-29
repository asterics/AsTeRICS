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
import java.util.ArrayList;

/**
 * 
 * Implements the Exponential Smoothing algorithm.
 * @see <a href="http://en.wikipedia.org/wiki/Exponential_smoothing">Exponential_smoothing</a> 
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 15, 2012
 *         Time: 10:06:15 AM
 */
class ExponentialSmoothingAlgorithm extends TremorReductionBasicAlgorithm
{
	private double factor;
	private int degree;
	
	private ArrayList<AlgorithmPoint> buffer=new ArrayList();
	
	/**
	* The class constructor.
	*/
	public ExponentialSmoothingAlgorithm()
	{
		factor=0.5;
		degree=1;
	}
	
	/**
	* The class constructor.
	* @param factor equation factor
	* @param degree degree of the equation
	*/
	public ExponentialSmoothingAlgorithm(double factor, int degree)
	{
		if((factor>0)&&(factor<1))
		{
			this.factor=factor;
		}
		else
		{
			this.factor=0.5;
		}
		
		if((degree>0)&&(degree<5))
		{
			this.degree=degree;
		}
		else
		{
			this.degree=1;
		}
	}
	
	/**
	* Calculates the new cursor position.
	* @param inputPoint current cursor position
	* @return the new cursor position
	*/
	public AlgorithmPoint calcualteNewPoint(AlgorithmPoint inputPoint)
	{
		if(buffer.size()==0)
		{
			buffer.add(inputPoint);
			return inputPoint;
		}
		else
		{
			
			double x=0;
			double y=0;
			
			int tmpDegree=buffer.size();
			if(tmpDegree>degree)
			{
				tmpDegree=degree;
			}
			
			for(int i=0;i<tmpDegree-1;i++)
			{
				x=x+factor*Math.pow(1-factor, i+1)* (double)buffer.get(i).getX();
				y=y+factor*Math.pow(1-factor, i+1)* (double)buffer.get(i).getY();
			}
			
			x=x+Math.pow(1-factor, tmpDegree)* (double)buffer.get(tmpDegree-1).getX();
			y=y+Math.pow(1-factor, tmpDegree)* (double)buffer.get(tmpDegree-1).getY();
			
			x=x+factor*inputPoint.getX();
			y=y+factor*inputPoint.getY();
			
			AlgorithmPoint newPoint=new AlgorithmPoint((int)x,(int)y);
			
			
			if(Math.abs(x-(double)newPoint.getX())>0.5)
			{
				if(x>=0)
				{
					newPoint.setX(newPoint.getX()+1);
				}
				else
				{
					newPoint.setX(newPoint.getX()-1);
				}
			}
			
			if(Math.abs(y-(double)newPoint.getY())>0.5)
			{
				if(y>=0)
				{
					newPoint.setY(newPoint.getY()+1);
				}
				else
				{
					newPoint.setY(newPoint.getY()-1);
				}
			}
			
			buffer.add(0,newPoint);
			
			int pointsToRemove=buffer.size()-degree;
			
			for(int i=0;i<pointsToRemove;i++)
			{
				buffer.remove(buffer.size()-1);
			}
			
			return newPoint;
		}
		
	}
	
	/**
	* Sets the new factor value.
	* @param factor the new factor value
	*/
	public void setFactor(double factor)
	{
		if((factor>0)&&(factor<1))
		{
			this.factor=factor;
		}
	}
	
	/**
	* Changes degree of the equation.
	* @param degree the new degree of the equation
	*/
	public void setDegree(int degree)
	{
		if((degree>0)&&(degree<5))
		{
			this.degree=degree;
		}
		
		int pointsToRemove=buffer.size()-degree;
		
		for(int i=0;i<pointsToRemove;i++)
		{
			buffer.remove(buffer.size()-1);
		}
	}
	
	/**
	* Removes the past cursor positions
	*/
	public void clean()
	{
		buffer.clear();
	}
}