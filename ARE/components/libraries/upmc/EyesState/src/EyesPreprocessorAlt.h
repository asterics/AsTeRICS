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

/*
 * preprocessEye.h
 *
 *  Created on: 3 May 2012
 *      Author: andera
 */

#ifndef PREPROCESSEYE_ALT_H_
#define PREPROCESSEYE_ALT_H_

#include <opencv2/core/core.hpp>
#include "BlinkCommon.h"

namespace upmc {


class EyesPreprocessorAlt {
public:
	EyesPreprocessorAlt(size_t width_ref = 60);
	virtual ~EyesPreprocessorAlt();

public:
	bool operator()(cv::Mat& imgRGB, const cv::Mat& imgPts, upmc::EyePair&,
			cv::OutputArray rotated = cv::noArray());

private:
	bool _getroifromcorners(const std::pair<cv::Point2d, cv::Point2d>& corner_, cv::Mat& img, cv::Mat& roi);

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
