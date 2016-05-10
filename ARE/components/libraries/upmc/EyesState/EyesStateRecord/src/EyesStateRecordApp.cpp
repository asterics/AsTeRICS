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

#include "EyesStateRecordApp.h"
/////////////////////////////////////////////////////////////////////////////
#include <sstream>
#include <stdexcept>
#include <iostream>
/////////////////////////////////////////////////////////////////////////////
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/video/video.hpp>
#include <opencv2/contrib/contrib.hpp>
/////////////////////////////////////////////////////////////////////////////
#include "autolink.hpp"
/////////////////////////////////////////////////////////////////////////////
#define _UPMC_USE_ALT_
/////////////////////////////////////////////////////////////////////////////
#if defined(_UPMC_USE_ALT_)
#include "EyesPreprocessorAlt.h"
#else
#include "EyesPreprocessor.h"
#endif
/////////////////////////////////////////////////////////////////////////////
#include "eyesLogManager.h"
#include "facetrackerLib.h"
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
namespace upmc{
/////////////////////////////////////////////////////////////////////////////
namespace {//anon
///
typedef enum states_t {
	eRecOpen, eRecClose, eIdle
};
///
static states_t state = eIdle;
static std::string window_title = "camera";

} //Anonymous namespace
/////////////////////////////////////////////////////////////////////////////
EyesStatesRecordApp::EyesStatesRecordApp():	_append(true)
, _width(25)
, _resample(-1)
, _yamlfile("eyeslog.yml")
, _image_resol(640)
, _device(0)
{

}
/////////////////////////////////////////////////////////////////////////////
void EyesStatesRecordApp::run(int argc, const char* argv[]) {
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
bool EyesStatesRecordApp::_parseCmdArgs(int& i, int argc, const char* argv[]) {
	std::string arg(argv[i]);

	if (arg == "--create") {
		_append = false;
				++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after " << " --create";
			throw std::runtime_error(msg.str());	
		}
		_yamlfile.assign(argv[i]);

	} else if (arg == "--append") {
		_append = true;
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after " << " --append";
			throw std::runtime_error(msg.str());	
		}
		_yamlfile.assign(argv[i]);

	} else if (arg == "--camera") {
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after " << "Camera Index";
			throw std::runtime_error(msg.str());
		}
		_device = atoi(argv[i]);
	} else if (arg == "--width") {
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after " << "Width Patch";
			throw std::runtime_error(msg.str());
		}
		_width = static_cast<size_t>(atoi(argv[i]));
	}  else if(arg=="--resample"){
		++i;
		if (i >= argc) {
			std::ostringstream msg;
			msg << "Missing value after --resample";
			throw std::runtime_error(msg.str());
		}
		_resample = atoi(argv[i]);

		} else if(arg=="--resolution"){
			++i;
			if (i >= argc) {
				std::ostringstream msg;
				msg << "Missing value after --resolution";
				throw std::runtime_error(msg.str());
				}
			_image_resol=(size_t)atoi(argv[i]);
		} else
			return false;
		
	return true;
}
/////////////////////////////////////////////////////////////////////////////
void EyesStatesRecordApp::_process() {
	cv::Ptr < cv::VideoCapture > vidsource;
	upmc::facetrackerLib facetracker;
	vidsource = new cv::VideoCapture();
	//_source.Ptr()=
	vidsource->open(_device);
	if (!vidsource->isOpened()) {
		std::ostringstream msg;
		msg << "Can't open camera with dev. ID = " << _device;
		throw std::runtime_error(msg.str());
	}

	//Select and set resolution (just two choices: 640 and 320)
	if(_image_resol == 320){
		vidsource->set(CV_CAP_PROP_FRAME_WIDTH, 320);
		vidsource->set(CV_CAP_PROP_FRAME_HEIGHT, 240);
	}
	else{ //Assign default
		//std::cout << "Assigning default resolution 640x480"<< std::endl;
		vidsource->set(CV_CAP_PROP_FRAME_WIDTH, 640);
		vidsource->set(CV_CAP_PROP_FRAME_HEIGHT, 480);
	}

	if (vidsource->isOpened()) {
		std::cout << "Camera Opened" << std::endl;
		//////////////////////////////////////////////////////////
		cv::namedWindow (window_title);
		//////////////////////////////////////////////////////////
		std::ostringstream nodeName;
		//////////////////////////////////////////////////////////
		upmc::EyesLogger eyeslog;
		//////////////////////////////////////////////////////////
		{ //local scope
			cv::FileStorage fs;
			if (_append) {
				//import existing dataset
				std::cout << "Importing Existing Dataset:"
						<<  _yamlfile << " into memory."
						<< std::endl;


				if(fs.open(_yamlfile, cv::FileStorage::READ)){
					fs["eyeslog"] >> eyeslog;
					if (eyeslog.getImSize().width!=static_cast<int>(_width)){
						std::cout << "Mismatch detected between requested sample patch width and actual dataset width size"
								<<  std::endl << "Setting to: " << eyeslog.getImSize().width  << " Instead of " << _width << std::endl;
						_width=eyeslog.getImSize().width;
					}
				}
				else{
					std::ostringstream msg;
					msg << "Cannot open: " << _yamlfile << " for reading.";
					throw std::runtime_error(msg.str());
				}

			}
			fs.release();
		}
		//////////////////////////////////////////////////////////
		bool running = true;

		cv::Mat frameRGB;
		cv::Mat frameGrey;

#if defined(_UPMC_USE_ALT_)
		upmc::EyesPreprocessorAlt eyesProcessor(_width);
#else
		upmc::EyesPreprocessor eyesProcessor(_width);
#endif

		int key = 0;
		std::cout << "Starting Acquisition"<< std::endl;
		while (running) {

			*vidsource >> frameRGB;

			cv::flip(frameRGB, frameRGB, 1);
			cv::cvtColor(frameRGB, frameGrey, CV_BGR2GRAY);

			facetracker.update(frameRGB);

			cv::Mat shape = facetracker.getImagePts();

			upmc::EyePair eyepair;

#if defined(_UPMC_USE_ALT_)
			cv::Mat rotated;//empty
#else
			cv::Mat rotated(frameRGB.rows, frameRGB.cols, CV_8UC3);
#endif
			if (facetracker.found()) {
				if(!eyesProcessor(frameRGB, shape, eyepair, rotated))
				{
					facetracker.reset();
					continue;
				}
			}

			switch (key) {

			case 27: //ESC
				running = false;
				break;

			case ' ': //always change to idle
				state = eIdle;
				break;

			case 'o': //"n" Start/Stop Recording Neutral
				if (state == eIdle) {
					std::cout << "Start Open Eyes Recording" << std::endl;
					state = eRecOpen;
				} else {
					std::cout << "Stop Open Eyes Recording" << std::endl;
					std::cout << "Open Eyes Samples Count: "
							<< eyeslog.getOpenedNumPairs() << std::endl;
					state = eIdle;
				}
				break;

			case 'c': //
				if (state == eRecOpen) {
					//skip
					std::cout << "!! Stop Open Eyes Recording First"
							<< std::endl;
				} else if (state == eIdle) {
					std::cout << "Start  Close Eyes Recording" << std::endl;
					state = eRecClose;
				} else {
					std::cout << "Stop  Close Eyes Recording" << std::endl;
					std::cout << "Close Eyes Samples Count: "
							<< eyeslog.getClosedNumPairs() << std::endl;
					state = eIdle;
				}
				break;

			case 'r': //reset
				facetracker.reset();
				break;

			default:
				break;
			}

			//LOGGING SECTION
			switch (state) {

			case eIdle:
				break;

			case eRecOpen:
				if (facetracker.found()) {
					eyeslog.add(eyepair, upmc::eOPEN);
				} else {
					std::cout << "Visage not found ... resetting" << std::endl;
					facetracker.reset();
					state = eIdle;
				}

				break;

			case eRecClose:
				if (facetracker.found()) {
					eyeslog.add(eyepair, upmc::eCLOSE);
				} else {
					std::cout << "Visage not found ... resetting" << std::endl;
					facetracker.reset();
					state = eIdle;
				}
				break;

			default:
				break;

			} //switch

			//PLOT EYES ON FRAME (RGB)
			if (facetracker.found()) {
				cv::Mat eyes, eyesRGB;
				upmc::joinSingleChannelImages(eyepair.left, eyepair.right,
						eyes);

				cv::cvtColor(eyes, eyesRGB, CV_GRAY2BGR);

				eyesRGB.copyTo(
						frameRGB(cv::Range(0, eyes.rows),
								cv::Range(frameGrey.cols - eyes.cols,
										frameGrey.cols)));

				if (state == eRecClose || state == eRecOpen) {
					cv::rectangle(frameRGB,
							cv::Point(frameRGB.cols - eyes.cols, 0),
							cv::Point(frameRGB.cols, eyes.rows),
							cv::Scalar(0, 0, 255), 2, CV_AA);
				}
			}

			//
			if(!rotated.empty())
			{
				cv::Mat joint;
				upmc::joinRGBChannelImages(frameRGB, rotated, joint);
				cv::resize(joint, joint, cv::Size(), 0.7, 0.7);

				cv::imshow(window_title, joint);
			}
			else{
				cv::resize(frameRGB, frameRGB, cv::Size(), 0.7, 0.7);
				cv::imshow(window_title, frameRGB);
			}

			key = cv::waitKey(1);

		} //while loop

		{ //local scope
			std::cout << "Saving Dataset to: " << _yamlfile << std::endl;

			if(_resample>0)
				eyeslog.resample(_resample);

			cv::FileStorage fs;
			fs.open(_yamlfile, cv::FileStorage::WRITE);
			fs << "eyeslog" << eyeslog;
			fs.release();
		}

		//eyeslog.playWithOpencv();
	} //isOpened()
	else {
		std::cout << "Quitting .. camera not opened." << std::endl;
	}
}
/////////////////////////////////////////////////////////////////////////////
//namespace
/////////////////////////////////////////////////////////////////////////////
}

