#ifndef _LOGISTIC_CLASSIFIER_H_INCLUDED_
#define _LOGISTIC_CLASSIFIER_H_INCLUDED_

#include <opencv2/core/core.hpp>

namespace upmc
{
	class logisticClassifier{
	public:
		//ctor
		logisticClassifier();
		//get probability from observation
		double operator()(const cv::Mat&);

	private:
		bool _load_weights();
		int _dim;
		double _bias;
		cv::Mat_<double> _mu;
		cv::Mat_<double> _sigma;
		cv::Mat_<double> _weights;
	};
}//namespace upmc

#endif //_LOGISTIC_CLASSIFIER_H_INCLUDED_