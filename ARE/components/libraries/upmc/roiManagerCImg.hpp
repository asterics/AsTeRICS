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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#pragma once

#include <opencv2\opencv.hpp>

#include "CImg\CImg.h"
#include "ir_pupil_detector.h"

namespace ci=cimg_library;

namespace upmc
{

struct roiManagerCImg
{
	typedef enum {MIDLE, MDRAGGING} state;

	static state currentState;
	
	typedef struct roi_ratio
	{
		double x;
		double y;
		double width;
		double height;
	} roi_ratio;

	static roi_ratio ratio;
	
	static void handle(const ci::CImgDisplay& disp, uor::IRPupilDetector& detector )
	{
		if (disp.is_keyCTRLLEFT()|| disp.is_keyCTRLRIGHT())
		{
			//
			double xd = static_cast<double>( disp.mouse_x() );
			double yd = static_cast<double>( disp.mouse_y() );
			
			double currw = xd/static_cast<double>(disp.width());
			double currh = yd/static_cast<double>(disp.height());

			//printf("Ratio: width: %.3f -- height: %.3f\n", ratio.width, ratio.height);

			if (disp.button()&1) //left clickimg
			{
				if(currentState==MDRAGGING)
				{
					//TODO: check if >0
					ratio.width=currw -ratio.x;
					ratio.height=currh-ratio.y;
				}
				else if(currentState==MIDLE)
				{
					currentState=MDRAGGING;	
					ratio.x= currw;
					ratio.y= currh;
				}
			}//if button()
			else
			{
				if (currentState==MDRAGGING)
				{
					currentState=MIDLE;	
					//set
					ratio.width=currw -ratio.x;
					ratio.height=currh-ratio.y;
					//
					cv::Rect roi= 
						roiManagerCImg::getRect(detector.width(), detector.height());
					//
					detector.set_roi(roi);
				}
			} // if-else button()

		}//CTRL
		else
		{
			currentState=MIDLE;
		}
	}//handle

	//
	static cv::Rect getRect(int width, int height) 
	{
		cv::Rect roi;
		///
		roi.x=width*ratio.x;
		roi.y=height*ratio.y;
		roi.width=width*ratio.width;
		roi.height=height*ratio.height;
		return roi;
	}

	};//roiManager
///// Init static variables.
roiManagerCImg::state roiManagerCImg::currentState=roiManagerCImg::MIDLE;

roiManagerCImg::roi_ratio roiManagerCImg::ratio;

//cv::Point2i roiManager::posDrag;
//////////////  ----------------------- //////////////
}//namespace upmc