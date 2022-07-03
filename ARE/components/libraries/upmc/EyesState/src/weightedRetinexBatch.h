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
