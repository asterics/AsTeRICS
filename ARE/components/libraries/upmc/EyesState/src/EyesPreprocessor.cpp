/*
 * preprocessEye.cpp
 *
 *  Created on: 3 May 2012
 *      Author: andera
 */

#include "EyesPreprocessor.h"
#include <cmath>

#include <opencv2/imgproc/imgproc.hpp>

#define _r2d_(rad)(rad*180.0/M_PI)

namespace upmc {

EyesPreprocessor::EyesPreprocessor(size_t w) :
		_roi_reference_width(w) {

	//	_eyeLeftSeq =(cv::Mat_<int>(1,6) << 36, 37, 38, 39, 41, 40);
	//	_eyeRightSeq =(cv::Mat_<int>(1,6) << 42, 43, 44, 45, 46, 47);
	//
	_eyeFeaturesLeft.upperlid = std::make_pair(37, 38);
	_eyeFeaturesLeft.bottomlid = std::make_pair(40, 41);
	_eyeFeaturesLeft.corners = std::make_pair(36, 39);

	_eyeFeaturesRight.upperlid = std::make_pair(43, 44);
	_eyeFeaturesRight.bottomlid = std::make_pair(46, 47);
	_eyeFeaturesRight.corners = std::make_pair(42, 45);

}

EyesPreprocessor::~EyesPreprocessor() {

}

void EyesPreprocessor::operator ()(cv::Mat& imgRGB, const cv::Mat& imgPts,
		upmc::EyePair& eyepair, cv::OutputArray rotated) {

	cv::Mat imgGrey;
	cv::cvtColor(imgRGB, imgGrey, CV_BGR2GRAY);

	int n = imgPts.rows / 2;
	//LEFT EYE
	//take corners coords
	_cornerLeftCoord.first = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesLeft.corners.first),
			imgPts.at<double>(_eyeFeaturesLeft.corners.first + n));
	_cornerLeftCoord.second = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesLeft.corners.second),
			imgPts.at<double>(_eyeFeaturesLeft.corners.second + n));
	//	//RIGHT EYE
	_cornerRightCoord.first = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesRight.corners.first),
			imgPts.at<double>(_eyeFeaturesRight.corners.first + n));
	_cornerRightCoord.second = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesRight.corners.second),
			imgPts.at<double>(_eyeFeaturesRight.corners.second + n));

	double angle, angleL, angleR;
	cv::Point2f center;
	cv::Mat affine2DRot;
	/*cv::Mat imgRotated;*/

	//WARP LEFT
	//Select ROI first
	angleL = std::atan2(_cornerLeftCoord.second.y - _cornerLeftCoord.first.y,
			_cornerLeftCoord.second.x - _cornerLeftCoord.first.x);
	angleR = std::atan2(_cornerRightCoord.second.y - _cornerRightCoord.first.y,
			_cornerRightCoord.second.x - _cornerRightCoord.first.x);

	angle = (angleL + angleR) * 0.5;

	center = (_cornerLeftCoord.first + _cornerLeftCoord.second
			+ _cornerRightCoord.first + _cornerRightCoord.second) * 0.25;

	affine2DRot = cv::getRotationMatrix2D(center, _r2d_(angle), 1.0);
	//TODO: probably inverting the rotation does not make it better
	cv::Mat invAffine2DRot;
	cv::invertAffineTransform(affine2DRot, invAffine2DRot);

	cv::warpAffine(imgGrey, imgRotated, invAffine2DRot //affine2DRot
			, imgGrey.size(), cv::INTER_CUBIC | cv::WARP_INVERSE_MAP);

	//Get 2D Rotation
	cv::Mat M(affine2DRot, cv::Range::all(), cv::Range(0, 2)); //Ranges are: [start, end)
	//Get 2d Translation
	cv::Mat T(affine2DRot, cv::Range::all(), cv::Range(2, 3));
	//Point2d -> cv::Mat
	cv::Mat ll(_cornerLeftCoord.first);
	cv::Mat lr(_cornerLeftCoord.second);
	cv::Mat rl(_cornerRightCoord.first);
	cv::Mat rr(_cornerRightCoord.second);

	//eye corners coordinates on the rotated image (as cv::Mat).
	cv::Mat ll2 = M * ll + T;
	cv::Mat lr2 = M * lr + T;
	cv::Mat rl2 = M * rl + T;
	cv::Mat rr2 = M * rr + T;

	//Annoying but fast (cv::Mat -> cv:Point2f)
	_cornerLeftCoord2.first.x = ll2.at<double>(0);
	_cornerLeftCoord2.first.y = ll2.at<double>(1);

	_cornerLeftCoord2.second.x = lr2.at<double>(0);
	_cornerLeftCoord2.second.y = lr2.at<double>(1);

	_cornerRightCoord2.first.x = rl2.at<double>(0);
	_cornerRightCoord2.first.y = rl2.at<double>(1);

	_cornerRightCoord2.second.x = rr2.at<double>(0);
	_cornerRightCoord2.second.y = rr2.at<double>(1);

	//Now get the ROIs
	const double widthPerc = 0.15;
	const double heightPerc = 0.80;
	double widthL, widthR;
	double widthInc;
	cv::Vec2d p1;
	cv::Vec2d p2;
	int height;

	//LEFT
	//distance between left and right corner
	p1 = cv::Vec2d(_cornerLeftCoord2.second);
	p2 = cv::Vec2d(_cornerLeftCoord2.first);
	widthL = cv::norm(p1, p2, cv::NORM_L2);

	cv::Rect leftRoi;
	widthInc = widthL * widthPerc;
	height = (widthL + 2 * widthInc) * heightPerc;
	leftRoi.x = _cornerLeftCoord2.first.x - (widthInc);
	leftRoi.y = _cornerLeftCoord2.first.y - (height * 0.5);
	leftRoi.width = widthL + (2 * widthInc);
	leftRoi.height = height;

	//RIGHT
	p1 = cv::Vec2d(_cornerRightCoord.second);
	p2 = cv::Vec2d(_cornerRightCoord.first);
	widthR = cv::norm(p1, p2, cv::NORM_L2);

	cv::Rect rightRoi;
	widthInc = widthR * widthPerc;
	height = (widthR + 2 * widthInc) * heightPerc;
	rightRoi.x = _cornerRightCoord2.first.x - (widthInc);
	rightRoi.y = _cornerRightCoord2.first.y - (height * 0.5);
	rightRoi.width = widthR + (2 * widthInc);
	rightRoi.height = height;

	cv::Mat eyeleft = imgRotated(leftRoi).clone();
	cv::Mat eyeright = imgRotated(rightRoi).clone();

	cv::resize(eyeleft, eyepair.left,
			cv::Size(_roi_reference_width, _roi_reference_width * heightPerc),
			0, 0, cv::INTER_CUBIC);
	cv::resize(eyeright, eyepair.right,
			cv::Size(_roi_reference_width, _roi_reference_width * heightPerc),
			0, 0, cv::INTER_CUBIC);

	/////////////////////////////////////////////////////////////////////////////
	////INTERDISTANCE
	//cv::Vec2d midUp, midBottom;
	//std::pair<cv::Vec2d, cv::Vec2d> lidPts;
	////LEFT
	//lidPts.first=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesLeft.upperlid.first), imgPts.at<double>(_eyeFeaturesLeft.upperlid.first+n));
	//lidPts.second=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesLeft.upperlid.second), imgPts.at<double>(_eyeFeaturesLeft.upperlid.second+n));

	//midUp= (lidPts.first+lidPts.second)*0.5;

	//lidPts.first=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesLeft.bottomlid.first), imgPts.at<double>(_eyeFeaturesLeft.bottomlid.first+n));
	//lidPts.second=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesLeft.bottomlid.second), imgPts.at<double>(_eyeFeaturesLeft.bottomlid.second+n));

	//midBottom= (lidPts.first+lidPts.second)*0.5;

	//eyepair.leftLidAp=cv::norm(midUp,midBottom, cv::NORM_L2)/widthL;

	////RIGHT
	//lidPts.first=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesRight.upperlid.first), imgPts.at<double>(_eyeFeaturesRight.upperlid.first+n));
	//lidPts.second=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesRight.upperlid.second), imgPts.at<double>(_eyeFeaturesRight.upperlid.second+n));

	//midUp= (lidPts.first+lidPts.second)*0.5;

	//lidPts.first=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesRight.bottomlid.first), imgPts.at<double>(_eyeFeaturesRight.bottomlid.first+n));
	//lidPts.second=
	//		cv::Vec2d(imgPts.at<double>(_eyeFeaturesRight.bottomlid.second), imgPts.at<double>(_eyeFeaturesRight.bottomlid.second+n));

	//midBottom= (lidPts.first+lidPts.second)*0.5;

	//eyepair.rightLidAp=cv::norm(midUp,midBottom, cv::NORM_L2)/widthL;

	if (!rotated.empty()) {
		//rotated.create(imgRGB.size(), imgRGB.type());
		cv::Mat dst = rotated.getMat();
		cv::cvtColor(imgRotated, dst, CV_GRAY2BGR);
	}

	cv::Scalar c = CV_RGB(255,0,0);
	cv::circle(imgRGB, _cornerLeftCoord.first, 2, c);
	cv::circle(imgRGB, _cornerLeftCoord.second, 2, c);

	c = CV_RGB(0, 255, 0);
	cv::circle(imgRGB, _cornerRightCoord.first, 2, c);
	cv::circle(imgRGB, _cornerRightCoord.second, 2, c);

	c = CV_RGB(128, 128, 255);
	cv::Point2f endpt1(center.x + 100 * cos(angle),
			center.y + 100 * sin(angle));
	cv::line(imgRGB, center, endpt1, c, 1, CV_AA);

	cv::Point2f endpt2(center.x - 100 * cos(angle),
			center.y - 100 * sin(angle));
	cv::line(imgRGB, center, endpt2, c, 1, CV_AA);

	//std::cout << "Width Left: " << width << std::endl<< std::endl;

	//	std::cout << "Eyes  Angle: " << r2d(angle) << std::endl;
	//	std::cout << "Affine Rotation Matrix: " << affine2DRot << std::endl<< std::endl;
	//
	//	std::cout << "Rotation: " << M << std::endl<< std::endl;
	//	std::cout << "Translation: " << T << std::endl<< std::endl;
	//	std::cout << "Center: " << C << std::endl<< std::endl;

#if defined(_UPMC_DEBUG_OUTPUT_)
	c=CV_RGB(255,0,0);
	cv::circle(imgRotated, _cornerLeftCoord2.first, 2, c);
	cv::circle(imgRotated, _cornerLeftCoord2.second, 2, c);

	c=CV_RGB(0, 255, 0);
	cv::circle(imgRotated, _cornerRightCoord2.first, 2, c);
	cv::circle(imgRotated, _cornerRightCoord2.second, 2, c);

	cv::imshow("Rotated",imgRotated);

#endif

	//cv:Mat pair;

}

}/* namespace upmc */
