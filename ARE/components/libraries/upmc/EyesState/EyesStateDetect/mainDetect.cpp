/*
 * mainDetector.cpp
 *
 *  Created on: 9 May 2012
 *      Author: andera
 */
///////////////////////////////////////////////
////
/////////////////////////////////////////////
#include <iostream>
#include "autolink.hpp"
/////////////////////////////////////////////
#include "EyesStatesDetectorApp.h"
/////////////////////////////////////////////
int main(int argc, const char* argv[])
{
	try
	{
		upmc::EyesStatesDetectorApp app;
		app.run(argc, argv);
	}
	catch (const std::exception &e)
	{
		std::cout << "Error: " << e.what() << std::endl;
		return -1;
	}
	return 0;
}

//
//int main(int argc, char* argv[]) {
//	// insert code here...
//	upmc::facetrackerLib facetracker;
//	upmc::EyesModelSVM eyesDetector;
//
//	eyesDetector.load(std::string());
//
//	upmc::EyesPreprocessor eyesProcessor(eyesDetector.imgSize().width);
//	upmc::EyesDatasetProcessor process;
//
//	upmc::weightedRetinexBatch retinex(eyesDetector.imgSize());
//
//	//Open Camera stream.
//	cv::VideoCapture camera;
//	const std::string window_title = "camera";
//	//
//	camera.open(0);
//
//	if (camera.isOpened()) {
//
//		camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
//		camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
//
//		std::cout << "Camera Opened" << std::endl;
//		cv::namedWindow(window_title);
//
//		bool running = true;
//
//		cv::Mat frameRGB;
//		cv::Mat frameGrey;
//
//		upmc::EyePair eyepair;
//		upmc::EyePair eyepairX;
//
//		std::pair<int, int> eyeState;
//
//		while (running) {
//
//			camera >> frameRGB;
//			cv::flip(frameRGB, frameRGB, 1);
//			cv::cvtColor(frameRGB, frameGrey, CV_BGR2GRAY);
//
//			facetracker.update(frameRGB);
//
//			if (facetracker.found()) {
//				cv::Mat shape = facetracker.getImagePts();
//				//
//				eyesProcessor(frameRGB, shape, eyepair);
//
//				//
//				cv::flip(eyepair.left, eyepair.left, 1);
//
//				retinex(eyepair.left, eyepair.left);
//				retinex(eyepair.right, eyepair.right);
//				//
//				process(eyepair);
//				//(Internally flips the right eye.
//				eyeState = eyesDetector(eyepair);
//			}
//
//			//PLOT EYES ON FRAME (RGB)
//			if (facetracker.found()) {
//				//Detected
//				cv::Mat eyes, eyesRGB;
//				upmc::joinSingleChannelImages(eyepair.left, eyepair.right,
//						eyes);
//
//				cv::cvtColor(eyes, eyesRGB, CV_GRAY2BGR);
//
////				cv::normalize(eyesRGB, eyesRGB, 0, 255, cv::NORM_MINMAX,
////						CV_8UC3);
//				eyesRGB.convertTo(eyesRGB, CV_8UC3, 255);
//
//				eyesRGB.copyTo(
//						frameRGB(cv::Range(0, eyes.rows),
//								cv::Range(frameRGB.cols - eyes.cols,
//										frameRGB.cols)));
//
//				cv::Scalar c;
//				/////////////////////////////////////////////////
//				if (eyeState.first == upmc::eOPEN) {
//					c = cv::Scalar(0, 255, 0);
//				} else {
//					c = cv::Scalar(0, 0, 255);
//				}
//				/////////////////////////////////////////////////
//				cv::rectangle(frameRGB, cv::Point(frameRGB.cols - eyes.cols, 0),
//						cv::Point(frameRGB.cols - eyes.cols / 2, eyes.rows), c,
//						2, CV_AA);
//				/////////////////////////////////////////////////
//				if (eyeState.second == upmc::eOPEN) {
//					c = cv::Scalar(0, 255, 0);
//				} else {
//					c = cv::Scalar(0, 0, 255);
//				}
//				/////////////////////////////////////////////////
//				cv::rectangle(frameRGB,
//						cv::Point(frameRGB.cols - (eyes.cols / 2), 0),
//						cv::Point(frameRGB.cols, eyes.rows), c, 2, CV_AA);
//				/////////////////////////////////////////////////
//			}
//			//
//			cv::imshow(window_title, frameRGB);
//
//			int key = cv::waitKey(1);
//
//			switch (key) {
//
//			case 27: //ESC
//				running = false;
//				break;
//
//			case 'r': //reset
//				facetracker.reset();
//				//blinkFeatures.reset();
//				break;
//
//			default:
//				break;
//			}
//
//		} //while loop
//
//	} //camera opened
//	else {
//		std::cout << "Quitting .. camera not opened." << std::endl;
//	}
//
//	return 0;
//}
