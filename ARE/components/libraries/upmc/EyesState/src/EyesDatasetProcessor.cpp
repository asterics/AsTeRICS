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
