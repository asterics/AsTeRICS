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
 * BlinkCommon.cpp
 *
 *  Created on: 7 May 2012
 *      Author: andera
 */

#include "BlinkCommon.h"
#include <cmath>
#include <opencv2/highgui/highgui.hpp>

namespace upmc {

void joinSingleChannelImages(const cv::Mat& left, const cv::Mat& right,
		cv::Mat& joint) {
	cv::Size szleft(left.size());
	cv::Size szright(right.size());
	CV_Assert(szleft.height == szright.height);
	cv::Size target(szleft.width + szright.width, szleft.height);
	joint.create(target, left.type());
	cv::Mat leftSide = joint(cv::Range::all(), cv::Range(0, szleft.width));
	left.copyTo(leftSide);
	cv::Mat rightSide = joint(cv::Range::all(),
			cv::Range(szleft.width, szleft.width + szright.width));
	right.copyTo(rightSide);
}

void joinRGBChannelImages(const cv::Mat& left, const cv::Mat& right,
		cv::Mat& joint) {

	cv::Size szleft(left.size());
	cv::Size szright(right.size());

	CV_Assert(szleft.height == szright.height);
	CV_Assert(left.channels()==right.channels());

	cv::Size target(szleft.width + szright.width, szleft.height);

	joint.create(target, left.type());

	cv::Mat leftSide = joint(cv::Range::all(), cv::Range(0, szleft.width));
	left.copyTo(leftSide);

	cv::Mat rightSide = joint(cv::Range::all(),
			cv::Range(szleft.width, szleft.width + szright.width));
	right.copyTo(rightSide);
}

cv::Mat joinGridPrincipalComponents(const cv::PCA& pca, cv::Size sizeim) {
	int npc = pca.eigenvectors.rows;
	int tot = npc + 1;
	size_t iw = sizeim.width;
	size_t ih = sizeim.height;
	size_t sq1 = std::sqrt(float(tot));
	float lo = sq1 * sq1;
	float hi = ((sq1 + 1) * (sq1 + 1));
	sq1 += (std::abs(tot - lo) < std::abs(tot - hi)) ? (0) : (1);
	int rem = tot - (sq1 * sq1);
	int nrows, ncols;
	ncols = sq1;
	nrows = sq1;
	if (rem > 0) {
		nrows += 1;
	}
	cv::Mat grid = cv::Mat::zeros(ih * nrows, iw * ncols, CV_8UC1);
	cv::Mat avg = pca.mean.reshape(1, ih);
	cv::normalize(avg, avg, 0, 255, cv::NORM_MINMAX, CV_8UC1);
	cv::Mat dst = grid(cv::Range(0, ih), cv::Range(0, iw));
	avg.copyTo(dst);

	for (int pc = 0; pc < npc; ++pc) {
		//
		int i = (pc + 1) / ncols;
		int j = (pc + 1) % ncols;

		//get princ comp
		cv::Mat prinComp;
		cv::normalize(pca.eigenvectors.row(pc).reshape(1, ih), prinComp, 0, 255,
				cv::NORM_MINMAX, CV_8UC1);

		dst = grid(cv::Range(i * ih, (i + 1) * ih),
				cv::Range(j * iw, (j + 1) * iw));
		prinComp.copyTo(dst);
	}

	return grid;
}

} // namespace

