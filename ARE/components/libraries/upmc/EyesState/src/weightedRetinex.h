/*
 * weightedRetinex.h
 *
 *  Created on: 4 May 2012
 *      Author: andera
 */

#ifndef WEIGHTEDRETINEX_H_
#define WEIGHTEDRETINEX_H_

#include <opencv2/core/core.hpp>

namespace upmc {

class weightedRetinex {
public:
	weightedRetinex();
	virtual ~weightedRetinex();

public:
	void operator()(const cv::Mat& src, cv::Mat& dst);

private:
	void _updateLambda();
	double _lambdaxy(int x, int y);

private:
	int _ksize;
	cv::Mat _kx;
	cv::Mat _ky;
	cv::Mat _dx;
	cv::Mat _dy;
	cv::Mat _lnSrc;
	cv::Mat _lambda;
	cv::Mat _maxd;

private: //params
	double _tau;
	int    _eta;
};

} /* namespace upmc */
#endif /* WEIGHTEDRETINEX_H_ */
