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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */
#include "lk_kalman_tracker_t.h"

#include "opencv2/video/tracking.hpp"
#include "opencv2/imgproc/imgproc.hpp"

//#include <boost/foreach.hpp>

#include <iostream>

namespace upmc
{
	/////////////////////////////////////////////////////////////////////
	///
	lk_kalman_tracker_t::lk_kalman_tracker_t():_flags(0)
		, _points_initialised(false)
		,_buf_initialised(false)
		, _processNoiseCov(0.1)
		, _measurementNoiseCov(0.01)
		, _errorCovPost(0.0001) 
	{
		_image_buf.resize(2);
	}
	/////////////////////////////////////////////////////////////////////
	///
	lk_kalman_tracker_t::~lk_kalman_tracker_t()
	{

	}
	/////////////////////////////////////////////////////////////////////
	void lk_kalman_tracker_t::init_points(vectorOfPoints& points2track, const cv::Mat& im)
	{
		/*sparse_optical_flow_tracker_i::init_points(points2track);*/
		//cv::InputArray inputArray(points2track);
		_points=points2track;
		_points_initialised=true;

			//TODO: first convert?
		cv::Mat gray;

		if(im.channels()>1)
			cv::cvtColor(im, gray, CV_BGR2GRAY); 
		else
			gray=im.clone();

		//REFINE
		cv::cornerSubPix(gray
			, points2track
			, cv::Size(11,11)
			, cv::Size(-1,-1)
			, cv::TermCriteria(cv::TermCriteria::MAX_ITER | cv::TermCriteria::EPS, 20, 1)
			);
		//
		vectorOfPoints::const_iterator it=points2track.begin();

		for(; it!=points2track.end(); ++it)
		{
			cv::KalmanFilter kf(4, 2, 0);//state, measurement
			// x y u v
			kf.transitionMatrix =(cv::Mat_<float>(4, 4) << 1,0,1,0 , 0,1,0,1 , 0,0,1,0 , 0,0,0,1);
			// x y
			kf.measurementMatrix =(cv::Mat_<float>(2, 4) << 1,0,0,0 , 0,1,0,0);// , 0,0,1,0 ,  0,0,0,1);

			//cv::setIdentity(kf.measurementMatrix);
			cv::setIdentity(kf.processNoiseCov, cv::Scalar::all(_processNoiseCov));
			cv::setIdentity(kf.measurementNoiseCov, cv::Scalar::all(_measurementNoiseCov));
			cv::setIdentity(kf.errorCovPost, cv::Scalar::all(_errorCovPost));

			kf.statePost=(cv::Mat_<float>(4,1)<< it->x,it->y, 0, 0);//start with zero velocity
			kf.statePre=(cv::Mat_<float>(4,1)<< it->x,it->y, 0, 0);//start with zero velocity

			_KF.push_back(kf);
		}
	}
	/////////////////////////////////////////////////////////////////////
	void lk_kalman_tracker_t::track(const cv::Mat& image, upmc::tracking_data_t& tracking_data)
	{
		//cv::TermCriteria termcrit(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03);
		
		//tracking_data_t tracking_data;
		tracking_data.points.resize(_points.size());
		tracking_data.status.resize(_points.size());
		tracking_data.err.resize(_points.size());

		//TODO: first convert?
		cv::Mat gray;

		if(image.channels()>1)
			cv::cvtColor(image, gray, CV_BGR2GRAY); 
		else
			gray=image.clone();

		_image_buf[eCurr]=gray;

		if (_points_initialised)
		{
			if(!_buf_initialised)
			{
				//fill the buffer then
				_image_buf[ePrev]=gray;
				//
				_buf_initialised=true;
			}//_buf_initialised

			////Predict first: loop through the K trackers
			std::vector<cv::KalmanFilter>::iterator it=_KF.begin();
			std::vector<cv::KalmanFilter>::const_iterator it_end=_KF.end();
			//
			std::vector<cv::Point2f>::iterator pit=tracking_data.points.begin();
			std::vector<cv::Point2f>::const_iterator pit_end=tracking_data.points.end();

			///
			for(;it!=it_end, pit!=pit_end; ++it, ++pit)
			{
				it->predict();
				//
				pit->x=it->statePre.at<float>(0,0);
				pit->y=it->statePre.at<float>(1,0);
				/////DEBUG
				//std::cout
				//	<< std::endl << "Posterior: " 
				//	<< it->statePost.at<float>(0)
				//	<< " : "
				//	<< it->statePost.at<float>(1)
				//	<< std::endl
				//	<< "Predicted: " 
				//	<< it->statePre.at<float>(0)
				//	<< " : "
				//	<< it->statePre.at<float>(1)
				//	<< std::endl << std::endl;
			}

			//_flags|=cv::OPTFLOW_USE_INITIAL_FLOW;
			//std::vector<cv::Point2f> points;
			//std::vector<uchar> status;
			//std::vector<float> err;
			//Trackit!
			cv::calcOpticalFlowPyrLK(
				 _image_buf.at(ePrev)//prev
				, _image_buf.at(eCurr)//curr
				, _points
				, tracking_data.points
				, tracking_data.status
				, tracking_data.err
				, cv::Size(11,11)
				, 3
				, cv::TermCriteria(cv::TermCriteria::MAX_ITER | cv::TermCriteria::EPS, 20, 0.03)
				, _flags
				);
#if 1
			/////loop again to update the state
			it=_KF.begin();
			it_end=_KF.end();
			pit=tracking_data.points.begin();//now with the new points
			pit_end=tracking_data.points.end();
			/////
			//std::cout << "UPDATE" << std::endl;
			/////
			for(;it!=it_end, pit!=pit_end; ++it, ++pit)
			{
				cv::Mat measurement=(cv::Mat_<float>(2,1) 
					<< pit->x, pit->y
					);
				it->correct(measurement);
				//
				pit->x=it->statePost.at<float>(0,0);
				pit->y=it->statePost.at<float>(1,0);					
			}
#endif
			///update
			_points=tracking_data.points;
			///
			cv::swap(_image_buf[ePrev], _image_buf[eCurr]);

		}//_points_initialised

		//return tracking_data;
	}///track
	/////////////////////////////////////////////////////////////////////
	void lk_kalman_tracker_t::reset()
	{
		_points_initialised=false;
		_buf_initialised=false;

		_image_buf.clear();
		_image_buf.resize(2);

		_flags=0;
		_KF.clear();
		//reset();
	}
	/////////////////////////////////////////////////////////////////////

}//namespace