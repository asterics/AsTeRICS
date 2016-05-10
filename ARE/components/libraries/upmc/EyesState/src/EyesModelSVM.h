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
