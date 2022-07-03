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

#include "EyesStatesDetectorApp.h"
/////////////////////////////////////////////////////////////////////////////
#include <sstream>
#include <stdexcept>
#include <iostream>
/////////////////////////////////////////////////////////////////////////////
#include <opencv2/core/core.hpp>
#include <opencv2/ml/ml.hpp>
#include <opencv2/highgui/highgui.hpp>
/////////////////////////////////////////////////////////////////////////////
#include "autolink.hpp"
/////////////////////////////////////////////////////////////////////////////
#include "facetrackerLib.h"
#include "EyesModelSVM.h"
#include "EyesPreprocessor.h"
#include "EyesPreprocessorAlt.h"
#include "EyesDatasetProcessor.h"
#include "weightedRetinexBatch.h"
/////////////////////////////////////////////////////////////////////////////
static std::string window_title = "camera";
/////////////////////////////////////////////////////////////////////////////
namespace upmc {

EyesStatesDetectorApp::EyesStatesDetectorApp(): _device(0)
, _svmfile("svm.yml"){
}

void EyesStatesDetectorApp::run(int argc, const char* argv[])
{
	for (int i = 1; i < argc; ++i)
	{
		if (_parseCmdArgs(i, argc, argv))
			continue;

		//        if (_parseHelpCmdArg(i, argc, argv))
		//            return;

		std::ostringstream msg;
		msg << "Unknown command line argument: " << argv[i];
		throw std::runtime_error(msg.str());
	}

	_process();
}

bool EyesStatesDetectorApp::_parseCmdArgs(int& i, int argc,
		const char* argv[]) {
	std::string arg(argv[i]);

	if (arg == "--model") {
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --model";
			throw std::runtime_error(msg.str());
		}
		_svmfile.assign(argv[i]);

	}
	//	else if (arg == "--pcamodel") {
	//		++i;
	//		if (i >= argc) {
	//			std::ostringstream msg;
	//			msg << "Missing value after --pcamodel";
	//			throw std::runtime_error(msg.str());
	//		}
	//		_pcafile.assign(argv[i]);
	//
	//	}
	else if(arg=="--camera"){
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --camera";
			throw std::runtime_error(msg.str());
		}
		_device = atoi(argv[i]);

	} else
		return false;

	return true;
}

void EyesStatesDetectorApp::_process() {
	//	// insert code here...
	upmc::facetrackerLib facetracker;
	upmc::EyesModelSVM eyesDetector;
	//
	try{
		eyesDetector.load(_svmfile);
	}
	catch(const std::exception& exp){
		throw exp;
		return;
	}

	//upmc::EyesPreprocessor eyesProcessor(eyesDetector.imgSize().width);
	upmc::EyesPreprocessorAlt eyesProcessor(eyesDetector.imgSize().width);

	upmc::EyesDatasetProcessor process(eyesDetector.imgSize());
	//
	cv::Ptr < cv::VideoCapture > vidsource;
	vidsource = new cv::VideoCapture();
	vidsource->open(_device);

	if (!vidsource->isOpened()) {
		std::ostringstream msg;
		msg << "Can't open camera with dev. ID = " << _device;
		throw std::runtime_error(msg.str());
	}


	//	if (vidsource->isOpened()) {//TODO: unuseful now
	std::cout << "Camera Opened" << std::endl;
	vidsource->set(CV_CAP_PROP_FRAME_WIDTH, 640);
	vidsource->set(CV_CAP_PROP_FRAME_HEIGHT, 480);
	//
	std::cout << "Camera Opened" << std::endl;
	cv::namedWindow(window_title);
	//
	bool running = true;
	cv::Mat frameRGB;
	cv::Mat frameGrey;
	upmc::EyePair eyepair;
	std::pair<int, int> eyeState;
	//
	while (running) {
		*vidsource >> frameRGB;
		cv::flip(frameRGB, frameRGB, 1);
		cv::cvtColor(frameRGB, frameGrey, CV_BGR2GRAY);
		//
		facetracker.update(frameRGB);
		//
		//PLOT EYES ON FRAME (RGB)
		if (facetracker.found()) {
			cv::Mat shape = facetracker.getImagePts();
			//
			if(eyesProcessor(frameRGB, shape, eyepair))
			{

				//
				cv::flip(eyepair.left, eyepair.left, 1);
				//
				process(eyepair);
				//SVM Predict
				eyeState = eyesDetector(eyepair);
			}
			//Detected
			cv::Mat eyes, eyesBig, eyesRGB;
			upmc::joinSingleChannelImages(eyepair.left, eyepair.right,
					eyes);
			float ratio=eyes.cols/eyes.rows;
			cv::resize(eyes, eyesBig, cv::Size(50*ratio,50));

			cv::cvtColor(eyesBig, eyesRGB, CV_GRAY2BGR);

			//				cv::normalize(eyesRGB, eyesRGB, 0, 255, cv::NORM_MINMAX,
			//						CV_8UC3);
			eyesRGB.convertTo(eyesRGB, CV_8UC3, 255.);

			eyesRGB.copyTo(
					frameRGB(cv::Range(0, eyesBig.rows),
							cv::Range(frameRGB.cols - eyesBig.cols,
									frameRGB.cols)));

			cv::Scalar c;
			/////////////////////////////////////////////////
			if (eyeState.first == upmc::eOPEN) {
				c = cv::Scalar(0, 255, 0);
			} else {
				c = cv::Scalar(0, 0, 255);
			}
			/////////////////////////////////////////////////
			cv::rectangle(frameRGB, cv::Point(frameRGB.cols - eyesBig.cols, 0),
					cv::Point(frameRGB.cols - eyesBig.cols / 2, eyesBig.rows), c,
					2, CV_AA);
			/////////////////////////////////////////////////
			if (eyeState.second == upmc::eOPEN) {
				c = cv::Scalar(0, 255, 0);
			} else {
				c = cv::Scalar(0, 0, 255);
			}
			/////////////////////////////////////////////////
			cv::rectangle(frameRGB,
					cv::Point(frameRGB.cols - (eyesBig.cols / 2), 0),
					cv::Point(frameRGB.cols, eyesBig.rows), c, 2, CV_AA);
			/////////////////////////////////////////////////
		}//plot on frame
		//			//
		cv::resize(frameRGB, frameRGB, cv::Size(), 0.7, 0.7);
		cv::imshow(window_title, frameRGB);

		int key = cv::waitKey(1);
		//
		switch (key) {

		case 27: //ESC
			running = false;
			break;

		case 'r': //reset
			facetracker.reset();
			//blinkFeatures.reset();
			break;

		default:
			break;
		}
		//
	} //while loop
	//
	//	} //camera opened
	//	else {
	//		std::cout << "Quitting .. camera not opened." << std::endl;
	//	}
}

}//namespace
