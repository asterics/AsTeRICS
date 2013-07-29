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
#ifndef _UPMC_lk_kalman_tracker_t_H_INCLUDED_
#define _UPMC_lk_kalman_tracker_t_H_INCLUDED_

#include "opencv2/video/tracking.hpp"

namespace upmc
{
		///
	typedef std::vector<cv::Point2f> vectorOfPoints;
	///
	struct tracking_data_t
	{
		tracking_data_t()
		{
		};

		~tracking_data_t()
		{
			//std::cout << "tracking_data_t dtor" << std::endl;
		};

		vectorOfPoints points;
		std::vector<uchar> status;
        std::vector<float> err;
	};

	///
	class lk_kalman_tracker_t 
	{
	public:
		lk_kalman_tracker_t();
		~lk_kalman_tracker_t();
		///
		void init_points(vectorOfPoints& points2track, const cv::Mat&);
		/// 
		void track(const cv::Mat&, upmc::tracking_data_t&); 
		///
		bool isInit() const {return _points_initialised;};
		///
		void reset();
		///
		enum{ePrev=0, eCurr=1};
		///
	private:
		int _flags;
		//one for every tracked point: TODO make a unique state ..
		std::vector<cv::KalmanFilter> _KF;
		///
		//upmc::ConfigFile _config;
	private:
				///
		enum {CURR=0, PREV=1};
		///parameters
		double _processNoiseCov;
		double _measurementNoiseCov;
		double _errorCovPost;

		//stores prev and current image.
		std::vector<cv::Mat> _image_buf;
		///
		vectorOfPoints  _points;
		///
		bool _points_initialised;
		bool _buf_initialised;
	};

}//upmc


#endif //_UPMC_lk_kalman_tracker_t_H_INCLUDED_