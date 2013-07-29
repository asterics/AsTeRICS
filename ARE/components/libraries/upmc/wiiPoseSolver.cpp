#include "wiiPoseSolver.h"

namespace upmc{
	/////////////////////////////////////////////////////////////////////////
	wiiPoseSolver::wiiPoseSolver():_have_calibration(false)
	{
	}
	/////////////////////////////////////////////////////////////////////////
	void wiiPoseSolver::setCalibration(const cv::Mat& intrinsic, const cv::Mat& distCoeff)
	{
		_intrinsic=intrinsic;
		_distcoeff=distCoeff;

		_have_calibration=true;
	}
	/////////////////////////////////////////////////////////////////////////
	bool wiiPoseSolver::load(const std::string calibfile)
	{
		cv::FileStorage fs;
		try{
		fs.open(calibfile, cv::FileStorage::READ);
		}
		catch(cv::Exception& e){
			std::cout << e.msg << std::endl;
			return false;
		}

		fs["camera_matrix"] >> _intrinsic;
		fs["distortion_coefficients"] >> _distcoeff;

		_have_calibration=true;
		return true;
	}
	/////////////////////////////////////////////////////////////////////////
	void wiiPoseSolver::solve(cv::InputArray objectPoints, cv::InputArray imagePoints,cv::OutputArray R_rot, cv::OutputArray tvec)
	{
		if(_have_calibration)
		{
			cv::Mat rvec;
			///
			cv::solvePnP(objectPoints
				, imagePoints
				,  _intrinsic
				, _distcoeff
				, rvec
				, tvec
				, false //useExtrinsicGuess
				);

			cv::Rodrigues(rvec, R_rot);
			
		}
	}
	/////////////////////////////////////////////////////////////////////////
}//namespace