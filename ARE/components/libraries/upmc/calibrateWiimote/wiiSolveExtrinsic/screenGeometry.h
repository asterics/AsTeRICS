#ifndef _UPMC_SCREEN_GEOMETRY_H_INCLUDED_
#define _UPMC_SCREEN_GEOMETRY_H_INCLUDED_

#include <opencv2/opencv.hpp>

namespace upmc{

	struct screenGeometry
	{
	static cv::Point3f topLeft;
	static cv::Point3f topRight;
	static cv::Point3f bottomLeft;
	static cv::Point3f bottomRight;
	};

}//namespace

#endif //_UPMC_SCREEN_GEOMETRY_H_INCLUDED_