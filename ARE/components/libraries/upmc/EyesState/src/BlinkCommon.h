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
