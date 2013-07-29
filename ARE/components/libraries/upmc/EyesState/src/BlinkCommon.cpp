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

