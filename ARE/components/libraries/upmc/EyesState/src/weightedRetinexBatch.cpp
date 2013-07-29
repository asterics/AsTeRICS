/*
 * weightedRetinexBatch.cpp
 *
 *  Created on: 9 May 2012
 *      Author: andera
 */

#include "weightedRetinexBatch.h"

#include "weightedRetinex.h"
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <cmath>
#include <iostream>

namespace upmc {

weightedRetinexBatch::weightedRetinexBatch(cv::Size szimg, size_t buflen) :
		_ksize(5), _tau(0.8), _eta(5) {

	_sz = szimg;
	_buflen = buflen;

	//create buffer
	_buffer = cv::Mat::zeros(_buflen, _sz.width * _sz.height, CV_32FC1);
	_buffer01 = cv::Mat::zeros(_buflen, _sz.width * _sz.height, CV_32FC1);

	_ibuf = 0; //points to the current writable slot (a row).
	cv::getDerivKernels(_kx, _ky, 1, 1, _ksize, true, CV_32F);
}

weightedRetinexBatch::~weightedRetinexBatch() {

}

void upmc::weightedRetinexBatch::operator ()(const cv::Mat& img, cv::Mat& out) {
	cv::Mat fSrc, img32;
	//
	if (img.type() != CV_32F || img.type() != CV_32FC1)
		img.convertTo(img32, CV_32FC1);
	else
		img.copyTo(img32);

	//wants to implement a pseudo circular buffer
	//we keep track only of the tail.
	size_t idx = _ibuf % _buflen;
	img32.reshape(1, 1).copyTo(_buffer.row(idx));
	_ibuf++; //update counter

	//Normalize all values in [0, 1]
	cv::normalize(_buffer, _buffer01, 0, 1, cv::NORM_MINMAX);

	//The idx index now points to the normalized patch/img
	_buffer01.row(idx).reshape(1, img.rows).copyTo(img32); //back to patch(rectangular shape)

	//double minVal, maxVal;
	img32 += 0.01; //TODO verify that this is needed.
	//
	cv::filter2D(img32, _dx, -1, _kx); //, Point anchor=Point(-1,-1), double delta=0, int borderType=BORDER_DEFAULT )
	cv::filter2D(img32, _dy, -1, _ky.t()); //, Point anchor=Point(-1,-1), double delta=0, int borderType=BORDER_DEFAULT )
	//
	_dx = cv::abs(_dx);
	_dy = cv::abs(_dy);
	////
		cv::normalize(_dx, _dx, 0, 1, cv::NORM_MINMAX);
		cv::normalize(_dy, _dy, 0, 1, cv::NORM_MINMAX);

	cv::max(_dx, _dy, _maxd);
	//cv::normalize(_maxd, _maxd, 0, 1, cv::NORM_MINMAX);

	cv::log(img32, _lnSrc);
	cv::boxFilter(_lnSrc, fSrc, -1, cv::Size(_eta, _eta)); //, cv::Point(-1,-1), bool normalize=true, int borderType=BORDER_DEFAULT )

	_updateLambda();

	fSrc.mul(_lambda);

	out = _lnSrc - fSrc;

	cv::normalize(out, out, 0, 1, cv::NORM_MINMAX, CV_32F);
	//out+=0.001;

}

void upmc::weightedRetinexBatch::_updateLambda() {

	CV_Assert(_lnSrc.depth() != sizeof(float));

	int channels = _lnSrc.channels();

	_lambda.create(_lnSrc.size(), _lnSrc.type());

	int nRows = _lnSrc.rows * channels;
	int nCols = _lnSrc.cols;

	//if (_lnSrc.isContinuous() && _maxd.isContinuous()
	//		&& _lambda.isContinuous()) {
	//	nCols *= nRows;
	//	nRows = 1;
	//}

	int i, j;
	float* p;
	for (i = 0; i < nRows; ++i) {
		p = _lambda.ptr<float>(i);
		for (j = 0; j < nCols; ++j) {
			p[j] = _lambdaxy(i, j);
		}
	}
}

double upmc::weightedRetinexBatch::_lambdaxy(int x, int y) {
	return (0.5
			+ 0.5 * (std::tanh((15 * (_tau - std::abs(_maxd.at<float>(x, y)))))));
}

} /* namespace upmc */
