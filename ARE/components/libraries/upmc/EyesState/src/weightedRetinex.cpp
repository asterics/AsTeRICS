/*
 * weightedRetinex.cpp
 *
 *  Created on: 4 May 2012
 *      Author: andera
 */

#include "weightedRetinex.h"
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <cmath>
#include <iostream>

namespace upmc {

weightedRetinex::weightedRetinex():_ksize(3), _tau(0.8), _eta(21) {
	cv::getDerivKernels(_kx, _ky, 1, 1, _ksize, true, CV_32F );

}

weightedRetinex::~weightedRetinex() {

}


void weightedRetinex::operator ()(const cv::Mat& greyImg, cv::Mat& outImg) {
	cv::Mat  fSrc, img32;

	greyImg.convertTo(img32, CV_32F);
	//Normalise [0,1];
	cv::normalize(img32, img32, 0, 1, cv::NORM_MINMAX);

	//double minVal, maxVal;
	img32+=0.01;

	cv::filter2D(img32, _dx, -1, _kx); //, Point anchor=Point(-1,-1), double delta=0, int borderType=BORDER_DEFAULT )
	cv::filter2D(img32, _dy, -1, _ky.t()); //, Point anchor=Point(-1,-1), double delta=0, int borderType=BORDER_DEFAULT )

	_dx=cv::abs(_dx);
	_dy=cv::abs(_dy);
//
//	cv::normalize(_dx, _dx, 0, 1, cv::NORM_MINMAX);
//	cv::normalize(_dy, _dy, 0, 1, cv::NORM_MINMAX);

	cv::max(_dx, _dy, _maxd);
	cv::normalize(_maxd, _maxd, 0, 1, cv::NORM_MINMAX);

	cv::log(img32, _lnSrc);
	cv::boxFilter(_lnSrc, fSrc, -1, cv::Size(_eta, _eta)); //, cv::Point(-1,-1), bool normalize=true, int borderType=BORDER_DEFAULT )

	_updateLambda();

	fSrc.mul(_lambda);

	outImg=_lnSrc-fSrc;


	cv::normalize(outImg, outImg, 0, 1, cv::NORM_MINMAX);
	outImg+=0.001;

#if defined(_UPMC_DEBUG_OUTPUT_)
//	cv::minMaxLoc(outImg, &minVal, &maxVal);
//	outImg+= minVal;
//	if( !checkRange(outImg
//			, true
//			, 0
//			, 0.0
//			, 1.1) )
//	{
//		std::cout << "Out of Range" << std::endl;
//	}
//	cv::minMaxLoc(outImg, &minVal, &maxVal);
//	std::cout << "After Normalisation- Min: " << minVal << " Max: " << maxVal << std::endl << std::endl;
#endif

#if defined(_UPMC_DEBUG_OUTPUT_)
	//cv::imshow("Image Norm", img32);
//	cv::imshow("Ln Src", _lnSrc);
//	cv::imshow("fSrc", fSrc);

//	cv::imshow("retinex dx", _dx);
//	cv::imshow("retinex dy", _dy);
//	cv::imshow("Max Deriv", _maxd);
	//std::cout << "Lambda: " << _lambda << std::endl << std::endl;
	//std::cout << "_maxd: " << _maxd << std::endl << std::endl;
	//std::cout << "_lnSrc: " << _lnSrc << std::endl << std::endl;
	//std::cout << "fSrc: " << fSrc << std::endl << std::endl;
	//std::cout << "outImg: " << outImg << std::endl << std::endl;
#endif
}

void weightedRetinex::_updateLambda() {

	    CV_Assert(_lnSrc.depth() != sizeof(float));

	    int channels = _lnSrc.channels();

	    _lambda.create(_lnSrc.size(), _lnSrc.type());

	    int nRows = _lnSrc.rows * channels;
	    int nCols = _lnSrc.cols;

	    if (_lnSrc.isContinuous() && _maxd.isContinuous() && _lambda.isContinuous())
	    {
	        nCols *= nRows;
	        nRows = 1;
	    }

	    int i,j;
	    float* p;
	    for( i = 0; i < nRows; ++i)
	    {
	        p = _lambda.ptr<float>(i);
	        for ( j = 0; j < nCols; ++j)
	        {
	        	p[j]=_lambdaxy(i,j);
	        }
	    }
}


double weightedRetinex::_lambdaxy(int x, int y) {
	return(0.5+ 0.5*(std::tanh((15*(_tau-std::abs(_maxd.at<float>(x,y)))))));
}

} /* namespace upmc */
