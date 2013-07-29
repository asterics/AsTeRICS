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