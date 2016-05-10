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

#include "logisticClassifier.h"
#include <iostream>

namespace upmc{
	////////////////////////////////////////////////////////////////////////////
	logisticClassifier::logisticClassifier()
	{
		if(_load_weights())
		{
			//OK message
			std::cout << ">> Weights Loaded Successfully." << std::endl;
		}
		else
		{
			//assigning default values ..
			std::cerr <<  ">> Failed to open Weights coefficients file." << std::endl;
		}
	}
	////////////////////////////////////////////////////////////////////////////
	bool logisticClassifier::_load_weights()
	{
		std::string weightsFile(".\\data\\sensor.facetrackerCLM\\gestures.yml");
		cv::FileStorage fs;
		if(fs.open(weightsFile, cv::FileStorage::READ))
		{
				fs["dim"]		>>	_dim ;
				fs["bias"]		>>	_bias;
				fs["weights"]	>>	_weights;
				fs["mu"]		>>	_mu;
				fs["sigma"]		>>	_sigma;
				//std::cout << _dim << std::endl << _weights << std::endl;
				return true;
		}
		
		return false;
	}
	////////////////////////////////////////////////////////////////////////////
	double logisticClassifier::operator()(const cv::Mat& p)
	{
		//Standardize input
		cv::Mat_<double> zm;
		cv::subtract(p, _mu, zm);
		cv::Mat_<double> pstd;
		cv::divide(zm,_sigma, pstd);

		cv::Mat wt(_weights.t());

		cv::Mat s=_weights.t()*pstd;

		double v=_bias+s.at<double>(0,0);

		return ( 1.0/(1+std::exp(-v)) );
	}
	////////////////////////////////////////////////////////////////////////////
}//namespace upmc