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
 * BlinkCommon.h
 *
 *  Created on: 4 May 2012
 *      Author: andera
 */

#ifndef BLINKCOMMON_H_
#define BLINKCOMMON_H_

#include <opencv2/core/core.hpp>


namespace upmc {
//Generic mat pair.
typedef std::vector<std::pair<cv::Mat, cv::Mat> > cvMatPair;

#define _ROIISOK_(_IM_, _ROI_)				\
	(										\
	   _ROI_.x >=0							\
	&& _ROI_.y >=0							\
	&& _ROI_.width > 0						\
	&& _ROI_.height > 0						\
	&& _ROI_.x+_ROI_.width < _IM_.cols		\
	&& _ROI_.y+_ROI_.height < _IM_.rows		\
	)

struct EyeFeaturesIdx {
	std::pair<int, int> upperlid; //Unused
	std::pair<int, int> bottomlid; //Unused
	std::pair<int, int> corners;
};

//Always row: #samples cols: #dim
struct dataSet {
	cv::Mat data;
	cv::Mat labels;
	cv::Size imgsize; //original image size
};

struct EyePair {
	cv::Mat left;
	cv::Mat right;
	double leftLidAp; //normalised left eye lid aperture size (ratio w.r.t. width)
	double rightLidAp; ////normalised right eye lid aperture size (ratio w.r.t. width)
};

typedef enum {
	eOPEN, eCLOSE
} eEye;

void joinSingleChannelImages(const cv::Mat& left, const cv::Mat& right,
		cv::Mat& joint);

void joinRGBChannelImages(const cv::Mat& left, const cv::Mat& right,
		cv::Mat& joint);

cv::Mat joinGridPrincipalComponents(cv::PCA const& pca, cv::Size sizeim);

} //namspace

#endif /* BLINKCOMMON_H_ */
