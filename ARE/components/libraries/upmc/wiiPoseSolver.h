#ifndef _UPMC_WII_POSE_SOLVER_H_INCLUDED_
#define _UPMC_WII_POSE_SOLVER_H_INCLUDED_

#include <opencv2/opencv.hpp>

namespace upmc{

///
class wiiPoseSolver
{
public:
	wiiPoseSolver();
	///Load calibration file from disk.
	bool load(const std::string calibfile);

public:
	///Set calibration matrices. 
	void setCalibration(const cv::Mat& intrinsic, const cv::Mat& distCoeff);

public:
	void solve(cv::InputArray objectPoints, cv::InputArray imagePoints, cv::OutputArray rot, cv::OutputArray tvec);

private:
	cv::Mat		_intrinsic;
	cv::Mat		_distcoeff;
	bool _have_calibration;

};//class definition

}//namespace
#endif //_UPMC_WII_POSE_SOLVER_H_INCLUDED_