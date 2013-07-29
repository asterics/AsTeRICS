///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2011, Matia Pizzoli and Andrea Carbone, all rights reserved.
// pizzoli@dis.uniroma1.it http://www.dis.uniroma1.it/~pizzoli/
// carbone@isir.upmc.fr people.isir.upmc.fr/carbone/
//
// Redistribution and use in source and binary forms, with or without 
// modification, are permitted provided that the following conditions are met:
//
//     * The software is provided under the terms of this licence stricly for
//       academic, non-commercial, not-for-profit purposes.
//     * Redistributions of source code must retain the above copyright notice, 
//       this list of conditions (licence) and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright 
//       notice, this list of conditions (licence) and the following disclaimer 
//       in the documentation and/or other materials provided with the 
//       distribution.
//     * The name of the author may not be used to endorse or promote products 
//       derived from this software without specific prior written permission.
//     * As this software depends on other libraries, the user must adhere to 
//       and keep in place any licencing terms of those libraries.
//     * Any publications arising from the use of this software, including but
//       not limited to academic journal and conference publications, technical
//       reports and manuals, must cite the following work:
//
//		Fiora Pirri, Matia Pizzoli, Alessandro Rudi: A general method for the point of 
//		regard estimation in 3D space. CVPR 2011: 921-928
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED 
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO 
// EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
///////////////////////////////////////////////////////////////////////////////
#include "ir_pupil_detector.h"
///////////////////////////////////////////////////////////////////////////////
#include <iostream>
///////////////////////////////////////////////////////////////////////////////
namespace uor {
///////////////////////////////////////////////////////////////////////////////
#define _ROIISOK_(_IM_, _ROI_)				\
(										\
   _ROI_.x >=0							\
&& _ROI_.y >=0							\
&& _ROI_.width > 0						\
&& _ROI_.height > 0						\
&& _ROI_.x+_ROI_.width < _IM_.cols		\
&& _ROI_.y+_ROI_.height < _IM_.rows		\
)

///////////////////////////////////////////////////////////////////////////////
IRPupilDetector::IRPupilDetector():	 
									  _ymlfilename("data//sensor.eyetracker//ir_pupil_detector.yml")
									, _roi(50,25,200,150)///default init for ROI
									, _best_roundness(0.92) //roundness									
									, _width(320)
									, _height(240)
									, _max_grey_value(100)
									, _check_uniform_greylevel(true)
{ 

	try {
		_cfg.open(_ymlfilename, cv::FileStorage::READ);
	}
	catch(const cv::Exception& exp){
		//std::cout << exp.msg << std::endl;
		_cfg.release();
	}

	///
	if(!_cfg.isOpened())
	{//create it and save it with default values.

		// printf("Creating Parameter File!"); 

		_cfg.open(_ymlfilename, cv::FileStorage::WRITE);
		//std::cout << "Writing Configuration file: " << ymlfilename << std::endl;
		//ROI
		//_cfg << "roiX" << _roi.x;
		//_cfg << "roiY" << _roi.y;
		//_cfg << "roiWidth" << _roi.width;
		//_cfg << "roiHeight" << _roi.height;

		//ROI
		_cfg << "roi" << "{" 
			<< "x"<< _roi.x
			<< "y" << _roi.y
			<< "width" << _roi.width
			<< "height" << _roi.height
			<< "}";

		// Roundness
		_cfg << "bestRoundness"<< _best_roundness;
		_cfg << "max_grey_value" << _max_grey_value;
		//default image dimensions
		_cfg << "width" << _width;
		_cfg << "height" << _height;

		//MSER
		_cfg << "MSERParams" << _mserPar;

	}
	else
	{//Read and set values.
		//std::cout << "Reading Configuration file: " << ymlfilename << std::endl;
		//_cfg["roiX"] >> _roi.x;
		//_cfg["roiY"] >> _roi.y;
		//_cfg["roiWidth"] >> _roi.width;
		//_cfg["roiHeight"] >> _roi.height;

		// printf("Reading values from Parameter File!"); 

		cv::FileNode node = _cfg["roi"];

		node["x"] >> _roi.x;
		node["y"] >> _roi.y;
		node["width"] >> _roi.width;
		node["height"] >> _roi.height;

		// Roundness
		_cfg["bestRoundness"] >> _best_roundness;
		//default image dimensions
		_cfg["width"]  >>_width;
		_cfg["height"] >> _height;

		//MSER
		_cfg["MSERParams"] >> _mserPar;
		////Max Grey Value
		_cfg["max_grey_value"] >> _max_grey_value;
	}
	_cfg.release();

	///Finally create MSER
	_mser.reset(new cv::MSER( 
		_mserPar.delta
	  , _mserPar.min_area
	  , _mserPar.max_area
	  , _mserPar.max_variation
	  , _mserPar.min_diversity
	  , _mserPar.max_evolution
	  , _mserPar.area_threshold
	  , _mserPar.min_margin
	  , _mserPar.edge_blur_size)
	  );
}



///////////////////////////////////////////////////////////////////////////////
IRPupilDetector::~IRPupilDetector()
{
    _save_config();
}

void IRPupilDetector::_save_config()
{
	//dump MSER parameters in case they're changed
	try {
	     // printf("\n Writing Parameter File: \n  width=%d, height=%d\n",_roi.width,_roi.height); 

		_cfg.open(_ymlfilename, cv::FileStorage::WRITE);
		//ROI
		_cfg << "roi" << "{" 
			<< "x"<< _roi.x
			<< "y" << _roi.y
			<< "width" << _roi.width
			<< "height" << _roi.height
			<< "}";
		// Roundness
		_cfg << "bestRoundness"<< _best_roundness;		
		//Max grey value
		_cfg << "max_grey_value" << _max_grey_value;
		//default image dimensions
		_cfg << "width" << _width;
		_cfg << "height" << _height;
		//MSER
		_cfg << "MSERParams" << _mserPar;
		_cfg.release();

	}
	catch(const cv::Exception& exp){
		std::cout << exp.msg << std::endl;
	}
}
///////////////////////////////////////////////////////////////////////////////
bool IRPupilDetector::detectPupil(const cv::Mat& inputImage, cv::RotatedRect& outputRect)
{
  if(inputImage.type() == CV_8UC3)
    cv::cvtColor(inputImage, _grey, CV_BGR2GRAY);
  else
    _grey = inputImage.clone();

  //update if necessary ... this will be redundant most of the time.
  _width=_grey.cols;
  _height=_grey.rows;

  //
  if (!check_roi(_roi,_width,_height)) 
  {
		// printf("Roi not set\n"); 
		return false;
  }

  cv::Mat greyROI = _grey(_roi);
  cv::Mat mserROI;

  //cv::imshow("ROI", greyROI);
  //cv::waitKey(1);
 
  ///Check if ROI has some texture.
  if (_check_uniform_greylevel)
  {
	  if(_is_uniform(greyROI))
		  return false;
  }

  //cv::equalizeHist(greyROI,greyROI);

  _mser->operator()(greyROI, _extremalRegions, mserROI);

  std::vector<std::vector<cv::Point > >::const_iterator 
	  it = _extremalRegions.begin();

  std::vector<std::vector<cv::Point > >::const_iterator 
	  it_end = _extremalRegions.end();

  //initialize at minima
  _best_shape.reset();

  //Loop through the extremal regions found.
  for( ;
	  it != it_end;
	  ++it )
    {
      _do_stats((*it), _current_shape );

	  //
	  if (_current_shape > _best_shape ) 
		{
			//_current_shape.rrect
			if(_current_shape.hull.size() > 5)//only in this case we can call fitEllipse
			{
				_current_shape.rrect=
					cv::fitEllipse(_current_shape.hull);

				cv::Scalar avgBright= _get_average_luminance2(greyROI, _current_shape.rrect);
				//cv::ellipse(greyROI, _current_shape.rrect, cv::Scalar(0,0,255), 2);
				//std::cout << avgBright.val[0] << std::endl;
				if(avgBright.val[0] < _max_grey_value)
				{
				_best_shape = _current_shape;
				}
			}
		}				
	  //cv::imshow("groi", greyROI);
	  cv::waitKey(1);
    }//for

  if(_best_shape.roundness > _best_roundness)
    {
		//outputRect = cv::fitEllipse(_best_shape.hull);
		outputRect=_best_shape.rrect;
		outputRect.center.x += _roi.x;
		outputRect.center.y += _roi.y;
	 
      return true;
    }
  return false;
}//detectPupil
///////////////////////////////////////////////////////////////////////////////
void IRPupilDetector::reset()
{
	///
	_mser.reset(new cv::MSER(
		_mserPar.delta
	  , _mserPar.min_area
	  , _mserPar.max_area
	  , _mserPar.max_variation
	  , _mserPar.min_diversity
	  , _mserPar.max_evolution
	  , _mserPar.area_threshold
	  , _mserPar.min_margin
	  , _mserPar.edge_blur_size)
	  );
}
///////////////////////////////////////////////////////////////////////////////
void IRPupilDetector::reset(const upmc::MSERParameters& newpar)
{
	///
	_mserPar=newpar;
	///
	_mser.reset(new cv::MSER(
		_mserPar.delta
	  , _mserPar.min_area
	  , _mserPar.max_area
	  , _mserPar.max_variation
	  , _mserPar.min_diversity
	  , _mserPar.max_evolution
	  , _mserPar.area_threshold
	  , _mserPar.min_margin
	  , _mserPar.edge_blur_size)
	  );
}
///////////////////////////////////////////////////////////////////////////////
bool IRPupilDetector::check_roi(cv::Rect const& roi, size_t width, size_t height ) const
	{
		return (roi.x > 0 && roi.y > 0 
					&& roi.x+roi.width < width 
				&&  roi.y+roi.height < height
				&& roi.width>0 && roi.height>0);
	}
///////////////////////////////////////////////////////////////////////////////
void IRPupilDetector::_do_stats(const std::vector<cv::Point >&  points, blob_stats_t& blobstats) 
{
	cv::convexHull(points, blobstats.hull);
	double perimeter = cv::arcLength(blobstats.hull, true);
	double area = cv::contourArea(blobstats.hull);
	blobstats.roundness= 4*CV_PI*area/(perimeter*perimeter);
	blobstats.points=points;
	//blobstats.rrect = cv::fitEllipse(blobstats.hull);
}

///////////////////////////////////////////////////////////////////////////////
cv::Scalar IRPupilDetector::_get_average_luminance2(const cv::Mat& img, const cv::RotatedRect& _rrect) const
{
	cv::Mat rotationMatrix=cv::getRotationMatrix2D(_rrect.center, _rrect.angle, 1.0);
	//Get 2D Rotation
	cv::Mat R(rotationMatrix, cv::Range::all(), cv::Range(0, 2)); //Ranges are: [start, end)
	//Get 2d Translation
	cv::Mat T(rotationMatrix, cv::Range::all(), cv::Range(2, 3));

	//float average=0.0;	
	cv::Mat imgdraw=img.clone();
	cv::Mat rotated;
	cv::Size sz(img.size().width*2, img.size().height*2);
	cv::warpAffine(imgdraw, rotated, rotationMatrix, sz);

	cv::Mat c1(cv::Point2d(_rrect.center.x, _rrect.center.y));
	cv::Mat c2 = R * c1 + T;
	//Drawings

	//cv::ellipse(imgdraw, _rrect, cv::Scalar(0,0,255), 2);

	cv::Rect rotatedROI(c2.at<double>(0)
		,  c2.at<double>(1)
		, _rrect.size.width
		, _rrect.size.height);

	//cv::circle(imgdraw
	//	, _rrect.center
	//	, 2
	//	, cv::Scalar(1.0, 1.0 , 1.0)
	//	, 2
	//	, CV_FILLED);

	//cv::circle(rotated
	//	, cv::Point(c2.at<double>(0), c2.at<double>(1))
	//	, 2
	//	, cv::Scalar(0.0,0.0, 0.0)
	//	, 2
	//	, CV_FILLED);

	//cv::imshow("IMG", imgdraw);
	//cv::imshow("Rotated", rotated);
	//cv::waitKey(1);


	if( _ROIISOK_(rotated,rotatedROI) )
	{
		cv::Mat cropped=rotated(rotatedROI);
		//cv::imshow("Cropped", cropped);
		return cv::mean(cropped);
	}
	else
		return 255.0;
}
///////////////////////////////////////////////////////////////////////////////
bool IRPupilDetector::_is_uniform(const cv::Mat& theroi) const
{
	cv::Mat mean;
	cv::Mat std;
	cv::meanStdDev(theroi, mean, std);
	//std::cout << "Mean: " << mean.at<double>(0) 
	//	<< "Std: " << std.at<double>(0)
	//	<< std::endl;
	if (std.at<double>(0) < 1.0) 
		return true;

	return false;
}
///////////////////////////////////////////////////////////////////////////////
}//namespace uor
///////////////////////////////////////////////////////////////////////////////