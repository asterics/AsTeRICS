/*
 * EyesDatasetProcessor.h
 *
 *  Created on: 15 May 2012
 *      Author: andera
 */

#ifndef EYESDATASETPROCESSOR_H_
#define EYESDATASETPROCESSOR_H_

#include "BlinkCommon.h"
#include "weightedRetinexBatch.h"
#include <memory>

namespace upmc {

/*
 *
 */
class EyesDatasetProcessor {
public:
	EyesDatasetProcessor(const cv::Size& patchsize);
	virtual ~EyesDatasetProcessor();
public:
	//inplace operation for the training matrix
	void operator()(upmc::dataSet& dataset);
	//inplace operation for the eyepair
	void operator()(upmc::EyePair& eyepair);

private:
	cv::Size _patchsize;//patch size
	std::auto_ptr<upmc::weightedRetinexBatch> _retinex;

};

} /* namespace upmc */
#endif /* EYESDATASETPROCESSOR_H_ */
