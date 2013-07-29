/*
 * EyesDatasetProcessor.cpp
 *
 *  Created on: 15 May 2012
 *      Author: andera
 */

#include "EyesDatasetProcessor.h"
#include "weightedRetinexBatch.h"
#include <opencv2/imgproc/imgproc.hpp>

namespace upmc {

EyesDatasetProcessor::EyesDatasetProcessor(const cv::Size& patchsize):_patchsize(patchsize){
	_retinex.reset(new upmc::weightedRetinexBatch(_patchsize));
}

EyesDatasetProcessor::~EyesDatasetProcessor() {
	// TODO Auto-generated destructor stub
}

void upmc::EyesDatasetProcessor::operator ()(upmc::dataSet& dataset) {
	//retinex processor
	CV_Assert(dataset.imgsize==_patchsize);


	//cycle through all the rows.
	for (int i = 0; i < dataset.data.rows; i++) {
		cv::Mat patch;
		dataset.data.row(i).reshape(1, dataset.imgsize.height).copyTo(patch);
		//PROCESSING PART
		cv::medianBlur(patch, patch, 3);
		(*_retinex)(patch, patch);
		patch.reshape(1, 1).copyTo(dataset.data.row(i));
	}
}

void upmc::EyesDatasetProcessor::operator ()(upmc::EyePair& eyepair) {

	cv::medianBlur(eyepair.left, eyepair.left, 3);
	cv::medianBlur(eyepair.right, eyepair.right, 3);
	(*_retinex)(eyepair.left, eyepair.left);
	(*_retinex)(eyepair.right, eyepair.right);
}

}
/* namespace upmc */
