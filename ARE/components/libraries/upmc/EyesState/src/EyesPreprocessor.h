/*
 * preprocessEye.h
 *
 *  Created on: 3 May 2012
 *      Author: andera
 */

#ifndef PREPROCESSEYE_H_
#define PREPROCESSEYE_H_

#include <opencv2/core/core.hpp>
#include "BlinkCommon.h"

namespace upmc {

///
class EyesPreprocessor {
public:
	EyesPreprocessor(size_t width_ref = 60);
	virtual ~EyesPreprocessor();

public:
	void operator()(cv::Mat& imgRGB, const cv::Mat& imgPts, upmc::EyePair&,
			cv::OutputArray rotated = cv::noArray());

private:
	//right and left correspond to the left and right
	//part of the mesh as seen as looking to the image
	//original image
	std::pair<cv::Point2d, cv::Point2d> _cornerLeftCoord;
	std::pair<cv::Point2d, cv::Point2d> _cornerRightCoord;
	//rotated image
	std::pair<cv::Point2d, cv::Point2d> _cornerLeftCoord2;
	std::pair<cv::Point2d, cv::Point2d> _cornerRightCoord2;
	//Features indexes
	EyeFeaturesIdx _eyeFeaturesLeft;
	EyeFeaturesIdx _eyeFeaturesRight;
	//
	size_t _roi_reference_width;
	//
	cv::Mat imgRotated;
};

} /* namespace upmc */
#endif /* PREPROCESSEYE_H_ */
