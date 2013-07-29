/*
 * preprocessEye.cpp
 *
 *  Created on: 3 May 2012
 *      Author: andera
 */

#include "EyesPreprocessorAlt.h"
#include <cmath>

#include <opencv2/imgproc/imgproc.hpp>

#define _r2d_(rad)(rad*180.0/M_PI)



    //CV_Assert( 0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= m.cols &&
    //          0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= m.rows );

namespace upmc {

EyesPreprocessorAlt::EyesPreprocessorAlt(size_t w) :
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

EyesPreprocessorAlt::~EyesPreprocessorAlt() {

}

bool EyesPreprocessorAlt::operator ()(cv::Mat& imgRGB, const cv::Mat& imgPts,
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

	if (!_getroifromcorners(_cornerLeftCoord, imgGrey, eyepair.left))
		return false;


	//	//RIGHT EYE
	_cornerRightCoord.first = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesRight.corners.first),
			imgPts.at<double>(_eyeFeaturesRight.corners.first + n));
	_cornerRightCoord.second = cv::Point2d(
			imgPts.at<double>(_eyeFeaturesRight.corners.second),
			imgPts.at<double>(_eyeFeaturesRight.corners.second + n));

	if(!_getroifromcorners(_cornerRightCoord, imgGrey, eyepair.right))
		return false;

//	if (!rotated.empty()) {
//		//rotated.create(imgRGB.size(), imgRGB.type());
//		cv::Mat dst = rotated.getMat();
//		cv::cvtColor(imgRotated, dst, CV_GRAY2BGR);
//	}

	cv::Scalar c = CV_RGB(255,0,0);
	cv::circle(imgRGB, _cornerLeftCoord.first, 2, c);
	cv::circle(imgRGB, _cornerLeftCoord.second, 2, c);

	c = CV_RGB(0, 255, 0);
	cv::circle(imgRGB, _cornerRightCoord.first, 2, c);
	cv::circle(imgRGB, _cornerRightCoord.second, 2, c);

//	c = CV_RGB(128, 128, 255);
//	cv::Point2f endpt1(center.x + 100 * cos(angle),
//			center.y + 100 * sin(angle));
//	cv::line(imgRGB, center, endpt1, c, 1, CV_AA);
//
//	cv::Point2f endpt2(center.x - 100 * cos(angle),
//			center.y - 100 * sin(angle));
//	cv::line(imgRGB, center, endpt2, c, 1, CV_AA);

	//std::cout << "Width Left: " << width << std::endl<< std::endl;

	//	std::cout << "Eyes  Angle: " << r2d(angle) << std::endl;
	//	std::cout << "Affine Rotation Matrix: " << affine2DRot << std::endl<< std::endl;
	//
	//	std::cout << "Rotation: " << M << std::endl<< std::endl;
	//	std::cout << "Translation: " << T << std::endl<< std::endl;
	//	std::cout << "Center: " << C << std::endl<< std::endl;

//#if defined(_UPMC_DEBUG_OUTPUT_)
//	c=CV_RGB(255,0,0);
//	cv::circle(imgRotated, _cornerLeftCoord2.first, 2, c);
//	cv::circle(imgRotated, _cornerLeftCoord2.second, 2, c);
//
//	c=CV_RGB(0, 255, 0);
//	cv::circle(imgRotated, _cornerRightCoord2.first, 2, c);
//	cv::circle(imgRotated, _cornerRightCoord2.second, 2, c);
//
//	cv::imshow("Rotated",imgRotated);
//
//#endif

	//cv:Mat pair;
	return true;
}

bool EyesPreprocessorAlt::_getroifromcorners(
		const std::pair<cv::Point2d, cv::Point2d>& corner_, cv::Mat& img, cv::Mat& roi)
{

	double angle;
	cv::Point2f center;
	cv::Mat affine2DRot;
	std::pair<cv::Point2d, cv::Point2d> corner2_;
	/*cv::Mat imgRotated;*/

	//WARP LEFT
	//Select ROI first
	angle = std::atan2(corner_.second.y - corner_.first.y,
			corner_.second.x - corner_.first.x);

	center = (corner_.first + corner_.second
			+ corner_.first + corner_.second) * 0.5;

	affine2DRot = cv::getRotationMatrix2D(center, _r2d_(angle), 1.0);
	//TODO: probably inverting the rotation does not make it better
	cv::Mat invAffine2DRot;
	cv::invertAffineTransform(affine2DRot, invAffine2DRot);

	cv::warpAffine(img, imgRotated, invAffine2DRot //affine2DRot
			, img.size(), cv::INTER_CUBIC | cv::WARP_INVERSE_MAP);

	//Get 2D Rotation
	cv::Mat M(affine2DRot, cv::Range::all(), cv::Range(0, 2)); //Ranges are: [start, end)
	//Get 2d Translation
	cv::Mat T(affine2DRot, cv::Range::all(), cv::Range(2, 3));
	//Point2d -> cv::Mat
	cv::Mat ll(corner_.first);
	cv::Mat lr(corner_.second);

	//eye corners coordinates on the rotated image (as cv::Mat).
	cv::Mat ll2 = M * ll + T;
	cv::Mat lr2 = M * lr + T;

	//Annoying but fast (cv::Mat -> cv:Point2f)
	corner2_.first.x = ll2.at<double>(0);
	corner2_.first.y = ll2.at<double>(1);

	corner2_.second.x = lr2.at<double>(0);
	corner2_.second.y = lr2.at<double>(1);

	//Now get the ROIs
	const double widthPerc = 0.20;
	const double heightPerc = 0.80;

	//LEFT
	//distance between left and right corner
	cv::Vec2d p1 = cv::Vec2d(corner2_.second);
	cv::Vec2d p2 = cv::Vec2d(corner2_.first);
	double width = cv::norm(p1, p2, cv::NORM_L2);
	cv::Rect theRoi;

	double widthInc = width * widthPerc;
	int height = (width + 2 * widthInc) * heightPerc;
	theRoi.x = corner2_.first.x - (widthInc);
	theRoi.y = corner2_.first.y - (height * 0.5);
	theRoi.width = width + (2 * widthInc);
	theRoi.height = height;

	//TODO: check the ROI!!
	if(_ROIISOK_(imgRotated, theRoi))
	{
		cv::Mat imgRoi = imgRotated(theRoi).clone();

		cv::resize(imgRoi, roi,
			cv::Size(_roi_reference_width, _roi_reference_width * heightPerc),
			0, 0, cv::INTER_CUBIC);
		return true;
	}
	return false;
}

}/* namespace upmc */
