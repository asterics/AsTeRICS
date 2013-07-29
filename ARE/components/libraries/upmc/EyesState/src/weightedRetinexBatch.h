/*
 * weightedRetinexBatch.h
 *
 *  Created on: 9 May 2012
 *      Author: andera
 */

#ifndef WEIGHTEDRETINEXBATCH_H_
#define WEIGHTEDRETINEXBATCH_H_

#include <opencv2/core/core.hpp>

namespace upmc {

/*
 *
 */
class weightedRetinexBatch {
public:
	weightedRetinexBatch(cv::Size szimg, size_t buflen = 32);
	weightedRetinexBatch(const weightedRetinexBatch&);
	weightedRetinexBatch& operator =(const weightedRetinexBatch&);
	virtual ~weightedRetinexBatch();

public:
	void operator ()(const cv::Mat& img, cv::Mat& out);

	int getEta() const {
		return _eta;
	}

	void setEta(int eta) {
		_eta = eta;
	}

	int getKsize() const {
		return _ksize;
	}

	void setKsize(int ksize) {
		_ksize = ksize;
	}

	double getTau() const {
		return _tau;
	}

	void setTau(double tau) {
		_tau = tau;
	}

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

private:
	cv::Mat _buffer;
	cv::Mat _buffer01;
	size_t _ibuf;
	size_t _buflen;
	cv::Size _sz;

private:
	double _tau;
	int _eta;
};

} /* namespace upmc */
#endif /* WEIGHTEDRETINEXBATCH_H_ */
