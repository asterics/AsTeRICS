/*
 * EyesModelSVM.h
 *
 *  Created on: 8 May 2012
 *      Author: Andrea Carbone
 *      carbone@isir.upmc.fr
 */
/////////////////////////////////////////////
#ifndef EYESMODELSVM_H_
#define EYESMODELSVM_H_
/////////////////////////////////////////////
#include <opencv2/core/core.hpp>
#include <opencv2/ml/ml.hpp>
#include <utility>
/////////////////////////////////////////////
#include "BlinkCommon.h"
/////////////////////////////////////////////
namespace upmc {
/////////////////////////////////////////////
/*
 *
 */
class EyesModelSVM {
public:
	EyesModelSVM();
	virtual ~EyesModelSVM();

public:
	void load(std::string model);

	//std::tr1::tuple<int, int, double> do();

	std::pair<int, int>
	 	 operator()(const upmc::EyePair& eyepair);
public:
	cv::Size imgSize() const {
		return _sizeim;
	}

private:
	cv::PCA _pca;
	int _neigen;
	cv::Size _sizeim;
	cv::SVM _svm;
};

} /* namespace upmc */
/////////////////////////////////////////////
#endif /* EYESMODELSVM_H_ */
