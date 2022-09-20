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
 * EyesModelSVM.cpp
 *
 *  Created on: 8 May 2012
 *      Author: andera
 */

#include "EyesModelSVM.h"
#include <stdexcept>

namespace upmc {

EyesModelSVM::EyesModelSVM() {
	// TODO Auto-generated constructor stub

}

EyesModelSVM::~EyesModelSVM() {
	// TODO Auto-generated destructor stub
}

void EyesModelSVM::load(std::string model) {
	{//read PCA local scope
		//PCA
		cv::FileStorage fs;
		//How it was saved.
		//	pcaFs << "pca" << "{"
		//			<< "eigenvalues" << pca.eigenvalues
		//			<< "eigenvectors" << pca.eigenvectors
		//			<< "mean" << pca.mean
		//			<< "nEigens" << nEigens
		//			<< "imgSize" << "{"
		//			<< "width" << szimg.width
		//			<< "height" << szimg.height
		//			<< "}"
		//			<< "}";

		if(!fs.open(model, cv::FileStorage::READ))
		{
			throw std::runtime_error("Cannot open model.");
		}

		cv::FileNode node = fs["pca"];
		node["eigenvalues"] >> _pca.eigenvalues;
		node["eigenvectors"] >> _pca.eigenvectors;
		node["mean"] >> _pca.mean;
		node["nEigen"] >> _neigen;
		cv::FileNode nodesz = node["imgSize"];
		nodesz["width"] >> _sizeim.width;
		nodesz["height"] >> _sizeim.height;
		fs.release();
	}//Read PCA end

	///
	_svm.load(model.c_str());

}

std::pair<int, int> EyesModelSVM::operator ()(const upmc::EyePair& eyepair) {
	//
	cv::Mat left;
	cv::Mat right(eyepair.right);

	cv::Mat rightFlip;
	cv::flip(right, rightFlip, 1);
	//reshape
	//	eyepair.left.reshape(1, 1).convertTo(left, CV_32F, 1/255.);
	//	rightFlip.reshape(1, 1).convertTo(right, CV_32F, 1/255.);
	eyepair.left.reshape(1, 1).copyTo(left);
	rightFlip.reshape(1, 1).copyTo(right);
	//Project
	cv::Mat_<float> lcoeffs;
	cv::Mat_<float> rcoeffs;
	//
	_pca.project(left, lcoeffs);
	_pca.project(right, rcoeffs);
	//predict
	float leftis = _svm.predict(lcoeffs, true);
	float rightis = _svm.predict(rcoeffs, true);
	//
	//	upmc::eEye isl=(leftis==1)  ? (upmc::eOPEN):(upmc::eCLOSE);
	//	upmc::eEye isr=(rightis==1) ? (upmc::eOPEN):(upmc::eCLOSE);
	//	std::cout << "Left margin: " << leftis << std::endl;
	//	std::cout << "Right margin: " << rightis << std::endl << std::endl;

	upmc::eEye isl = (leftis < 0) ? (upmc::eOPEN) : (upmc::eCLOSE);
	upmc::eEye isr = (rightis < 0) ? (upmc::eOPEN) : (upmc::eCLOSE);
	//
	return std::make_pair(isl, isr);
}

}/* namespace upmc */
