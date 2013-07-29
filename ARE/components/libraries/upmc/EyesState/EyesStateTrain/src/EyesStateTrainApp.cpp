/*
 * EyesStateTrainApp.cpp
 *
 *  Created on: 19 May 2012
 *      Author: andera
 */
#include "EyesStateTrainApp.h"
/////////////////////////////////////////////////////////////////////////////
#include <sstream>
#include <stdexcept>
#include <iostream>
#include <fstream>
/////////////////////////////////////////////////////////////////////////////
#include <opencv2/core/core.hpp>
#include <opencv2/ml/ml.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/contrib/contrib.hpp>
/////////////////////////////////////////////////////////////////////////////
#include "autolink.hpp"
/////////////////////////////////////////////////////////////////////////////
#include "eyesLogManager.h"
#include "EyesDatasetProcessor.h"
/////////////////////////////////////////////////////////////////////////////
namespace upmc {
/////////////////////////////////////////////////////////////////////////////
EyesStateTrainApp::EyesStateTrainApp():_neigens(12)
							, _nsamples(-1)
							, _resample(-1)
							, _yamlfile("eyeslog.yml")
							, _svmlog("svm.yml")
							, _eigenratio(0.9)
//							, _pcalog("pca.yml")
{
}
/////////////////////////////////////////////////////////////////////////////
EyesStateTrainApp::~EyesStateTrainApp() {

}
/////////////////////////////////////////////////////////////////////////////
void EyesStateTrainApp::run(int argc, const char* argv[]) {
	for (int i = 1; i < argc; ++i) {
		if (_parseCmdArgs(i, argc, argv))
			continue;

		//    if (_parseHelpCmdArg(i, argc, argv))
		//        return;//??break?
		std::ostringstream msg;
		msg << "Unknown command line argument: " << argv[i];
		throw std::runtime_error(msg.str());
	}
	_process();
}
/////////////////////////////////////////////////////////////////////////////
//bool EyesStateTrainApp::_parseHelpCmdArg(int& i, int argc, const char* argv[]) {
//}
/////////////////////////////////////////////////////////////////////////////
bool EyesStateTrainApp::_parseCmdArgs(int& i, int argc, const char* argv[]) {
	std::string arg(argv[i]);

	if (arg == "--eigenratio") {
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --eigenratio";
			throw std::runtime_error(msg.str());
		}
		_eigenratio = atof(argv[i]);

	} else if (arg == "--input") {
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --input";
			throw std::runtime_error(msg.str());
		}
		_yamlfile.assign(argv[i]);

	} else if(arg=="--samples"){
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --samples";
			throw std::runtime_error(msg.str());
		}
		_nsamples = atoi(argv[i]);

	} else if(arg=="--resample"){
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --resample";
			throw std::runtime_error(msg.str());
		}
		_resample = atoi(argv[i]);

	} else if(arg=="--model"){
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after -svmlog";
			throw std::runtime_error(msg.str());
		}
		_svmlog.assign(argv[i]);

		 
	}
//	else if(arg=="--pcalog"){
//		++i;
//		if (i >= argc) {
//			std::ostringstream msg;
//			msg << "Missing value after --pcalog";
//			throw std::runtime_error(msg.str());
//		}
//		_pcalog.assign(argv[i]);
//	}
	else
		return false;

	return true;
}
/////////////////////////////////////////////////////////////////////////////
void EyesStateTrainApp::_process() {

	std::cout << ">> Opening: " <<  _yamlfile << std::endl;
	upmc::EyesLogger eyeslogger;
	{//local scope
		cv::FileStorage fs;
		try {
			fs.open(_yamlfile, cv::FileStorage::READ);
		}
		catch(const std::exception& e){
			std::cout << e.what() << std::endl;
		}

		if (!fs.isOpened()) {
			std::ostringstream msg;
			msg <<  "** Opening file: " << _yamlfile <<  " failed." << std::endl;
			throw std::runtime_error(msg.str());
		}

		//////////////////////////////////////////////////
		fs["eyeslog"] >> eyeslogger;
	}
	//////////////////////////////////////////////////
	//eyeslogger.playWithOpencv(10);
	//////////////////////////////////////////////////
	if(_resample>0){
		std::cout << ">> Resampling dataset to: " << _resample
				<< " pairs." << std::endl;
		eyeslogger.resample(_resample);
		//write back to disk
		cv::FileStorage fs;
		try {
			fs.open(_yamlfile, cv::FileStorage::WRITE);
		}
		catch(const std::exception& e){
			std::cout << e.what() << std::endl;
		}
		fs << "eyeslog" << eyeslogger;
	}
	//////////////////////////////////////////////////
	upmc::dataSet dataset;
	if(_nsamples>0)
		dataset=eyeslogger.get(_nsamples);
	else
		dataset=eyeslogger.get();//#of eyepairs for each class (there will be 2*N + 2*N samples)
	//////////////////////////////////////////////////
	std::cout << "Final PCA Data Matrix: "
			<< "Rows: " << dataset.data.rows  << " "
			<< "Columns: " << dataset.data.cols << " "
			<< "Channels: " << dataset.data.channels()
			<< std::endl;
	/////////////////////////////////////////////////////////////////
	//Process data. dataset.data is [0, 1] CV_32FC1
	upmc::EyesDatasetProcessor processData(eyeslogger.getImSize()); //Retinex transform
	//
	std::cout << ">> Preprocessing data." << std::endl;
	processData(dataset);
	/////////////////////////////////////////////////////////////////
	//
	std::cout << ">> Performing PCA." << std::endl;
	cv::PCA pca(dataset.data, cv::Mat(), CV_PCA_DATA_AS_ROW, dataset.data.cols); //_neigens

	//std::cout << "Eigenvalues: " << pca.eigenvalues << std::endl;

	cv::MatConstIterator_<float> it =pca.eigenvalues.begin<float>();
	cv::MatConstIterator_<float> itend =pca.eigenvalues.end<float>();
	float total=0.0;
	//first scan to get the total variance
	for(;it!=itend; ++it)
	{
		total+=*it;
	}
	std::cout << "Total Variance: " << total << std::endl;
	//Second scan  to get the # of eigens
	float sum=0.0;
	int eigenNum=0;
	it =pca.eigenvalues.begin<float>();
	for(;it!=itend; ++it)
	{
		sum+=*it;
		eigenNum++;

		if( (sum/total) >=_eigenratio)
			break;
	}
	_neigens=eigenNum;
	//recompute (this could be done by selecting just the first _neigens rows ... but I am lazy now.
	pca(dataset.data, cv::Mat(), CV_PCA_DATA_AS_ROW, _neigens); //_neigens
	//std::cout << "Eigenvalues Ratio: " << _eigenratio << std::endl;
	std::cout << "Accounted Variance: " << sum/total << std::endl;
	std::cout << "Final Eigenvalues Num: " << pca.eigenvalues.rows << std::endl;
	std::cout << "Final Eigenvalues: " << pca.eigenvalues << std::endl;

	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	//Contains the pcaset vectors projected onto the pc subspace
	cv::Mat_<float> projected;
	projected.create(dataset.data.rows, _neigens);

	for( int i = 0; i < dataset.data.rows; i++ )
	{
		cv::Mat vec;// = pcaset.row(i);
		dataset.data.row(i).copyTo(vec);

		// compress the vector, the result will be stored
		// in the i-th row of the output matrix (projected)
		cv::Mat coeffs; // = projected.row(i);//Unuseful?
		pca.project(vec, coeffs);
		coeffs.copyTo(projected.row(i));
		{ //Reconstruction
			//Back projection of the compressed vectors (-> image space)
//			cv::Mat reconstructed;
//		// and then reconstruct it
//		pca.backProject(coeffs, reconstructed);
//
//		// and measure the error
//		//printf("%d. diff = %g\n", i, cv::norm(vec, reconstructed, cv::NORM_L2));
//
//		//reconstructed
//		cv::Mat recon = reconstructed.reshape(1, szimg.height);//#rows
//		cv::normalize(recon, recon, 0, 255, cv::NORM_MINMAX, CV_8UC1);
//		//original
//		cv::Mat orig = vec.reshape(1, szimg.height);//# rows
//		cv::normalize(orig, orig, 0, 255, cv::NORM_MINMAX, CV_8UC1);
//		//Join images
//		cv::Mat joint;
//		upmc::joinSingleChannelImages(orig, recon, joint);
//		//show
//		cv::imshow("Reconstructed", joint);
//		cv::waitKey(1);
		}
	}//end projections


	{//SVM section
		float splitRatio = 1.0; //percentage of training samples
		int splitPnt=projected.rows*splitRatio;

		cv::Mat_<float> trainData;
		projected(cv::Range(0, splitPnt), cv::Range::all()).copyTo(trainData);

		cv::Mat classLabels;
		dataset.labels(cv::Range(0,splitPnt), cv::Range::all()).copyTo(classLabels);
		//
		cv::SVM svm;
		///Params
		CvSVMParams param;
		param.svm_type=CvSVM::C_SVC;
		//param.nu=0.1;
		param.kernel_type = CvSVM::RBF;//Linear is ok

		//train
		std::cout << ">> Training SVM" << std::endl;
		svm.train_auto(trainData, classLabels, cv::Mat(), cv::Mat(), param, 10);

		std::cout << ">> Saving SVM model in: " << _svmlog << std::endl;
		svm.save(_svmlog.c_str());

		//Test
		{
			cv::Mat_<float> testData;
			cv::Mat_<float> trueLabel;
			projected(cv::Range( cv::Range::all()), cv::Range::all()).copyTo(testData);
			dataset.labels(cv::Range(cv::Range::all()), cv::Range::all()).copyTo(trueLabel);

			cv::Mat_<float> predicted ( testData.rows , 1 , CV_32F ) ;

			int ok=0;
			for (int k=0; k<testData.rows; k++)
			{
				cv::Mat sample;
				testData.row(k).copyTo(sample);
				predicted.at<float>(k,0) = svm.predict(sample);
				ok+=(trueLabel.at<float>(k,0)==predicted.at<float>(k,0)) ? (1):(0);//add one if match
			}
			std::cout << "Success/Total: " << ok << "/" <<  testData.rows << std::endl;
			std::cout << "Recognition Rate: " << ok/static_cast<float>(testData.rows) << std::endl;

		}
	}//SVM end

	{
		///LOGGING AND STORING
		cv::Mat grid=upmc::joinGridPrincipalComponents(pca, eyeslogger.getImSize());
		cv::imwrite("pc.png", grid);

		{//Save PCA parameters
			cv::FileStorage pcaFs;
			std::cout << ">> Saving PCA model in: " << _svmlog << std::endl;
			pcaFs.open(_svmlog.c_str(), cv::FileStorage::APPEND);

			pcaFs << "pca" << "{"
					<< "eigenvalues" << pca.eigenvalues
					<< "eigenvectors" << pca.eigenvectors
					<< "mean" << pca.mean
					<< "nEigens" << _neigens
					<< "imgSize" << "{"
					<< "width" << eyeslogger.getImSize().width
					<< "height" << eyeslogger.getImSize().height
					<< "}"
					<< "}";
		}

	}
//
//	{//Save projected coefficients as text file readable from matlab
//
//		std::ofstream _log;
//		_log.open("pcafeatures.txt", std::ios::out);
//
//		for(int i=0; i< projected.rows; i++)
//		{
//			cv::Mat_<float> row;
//			projected.row(i).copyTo(row);
//			cv::MatIterator_<float> it=row.begin();
//			cv::MatIterator_<float> it_end =row.end();
//			for(; it!=it_end;++it)
//			{
//				_log << *it << " ";
//			}
//			_log << cLabels.at<float>(i,0);
//			_log << std::endl;
//		}
//
//		_log.close();
//	}

	std::cout << ">> Training Done." << std::endl;
 }
/////////////////////////////////////////////////////////////////////////////

} /* namespace upmc */
