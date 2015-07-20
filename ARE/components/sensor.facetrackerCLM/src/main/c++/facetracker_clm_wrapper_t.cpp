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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

#include "facetracker_clm_wrapper_t.h"

#include "autolink.hpp"

#include <boost\bind.hpp>
#include <boost\chrono.hpp>

#include "EyesModelSVM.h"
#include "EyesPreprocessorAlt.h"
#include "EyesDatasetProcessor.h"
#include "weightedRetinexBatch.h"

#include "tictoc_t.hpp"

#include "logisticClassifier.h"

#include <iostream>

static float const r2d = 57.295779513;

static int counter=0;
static int showPoint=14;


namespace upmc
{		
		boost::weak_ptr<facetracker_clm_wrapper_t> facetracker_clm_wrapper_t::_singleton;

		///Get singleton instance
		////////////////////////////////////////////////////////////////////////////
		boost::shared_ptr<facetracker_clm_wrapper_t> facetracker_clm_wrapper_t::get() {
		boost::shared_ptr<facetracker_clm_wrapper_t> instance = _singleton.lock();
		if (!instance) {
			 instance.reset(new facetracker_clm_wrapper_t());
			_singleton = instance;
		}
		 return instance;
		}
		////////////////////////////////////////////////////////////////////////////
		///ctor
		facetracker_clm_wrapper_t::facetracker_clm_wrapper_t():
			_running(true)
		,	_suspended(true)
		,   _camera_opened(false)
		,   _reset_ref(true)
		,   _width(320)
		,   _height(240)
		,	_window_title("CLM Facetracker")
		,   cameraDisplayUpdate("cameraDisplayUpdate", 15)
		,	cameraIndex("cameraSelection", 0)
		,	cameraResolution("cameraResolution", e640_480)
		,	modelName("modelName", "Generic")
		{
			_device=cameraIndex.second;
		}
		///dtor
		////////////////////////////////////////////////////////////////////////////
		facetracker_clm_wrapper_t::~facetracker_clm_wrapper_t()
		{
			if (_capture.isDeviceSetup(_device))
				_capture.stopDevice(_device);
		}
		////////////////////////////////////////////////////////////////////////////
		bool facetracker_clm_wrapper_t::activate()
		{	
			std::cout<<"Using OpenCV version "
			<< CV_VERSION
			<< CV_MAJOR_VERSION << "."
			<< CV_MINOR_VERSION << "."
			<< CV_SUBMINOR_VERSION
			<< std::endl 
			<< "VideoInput: "
			<< VI_VERSION
			<< std::endl;
			
			std::cout << "Device Index: " 
				<< cameraIndex.second
				<< std::endl;
			
			_device=cameraIndex.second;

			int numDevices = videoInput::listDevices();	

			if(numDevices <=_device){
				printf("FaceTrackerCLM C++ module: ERROR: desired Webcam index: %d not available\n", _device);
				return false;
			}

			if(!strcmp(_capture.getDeviceName(_device),"VDP Source") || (!strcmp(_capture.getDeviceName(_device),"vfwwdm32.dll")) )
			{
				printf("FaceTrackerCLM C++ module: ERROR: desired device index: %d is not USB \n", _device);
				return false;
			}

			//helper function: sets desired _width and _height according to the integer values set in ACS
			setResolution(cameraResolution.second);

			if(!_capture.setupDevice(_device,   _width, _height, VI_USB)){
				printf("FaceTrackerCLM C++ module: ERROR: Video Input Setup Failed on Webcam index: %d\n", _device);
				return false;
			}

			//reassign, just in case the requested resolution is not available.
			_width=_capture.getWidth(_device);
			_height=_capture.getHeight(_device);
			//ALLOCATE image buffer
			_rawframe.reset(new unsigned char[_capture.getSize(_device)] );

			//LOCK! CRUCIAL
			boost::mutex::scoped_lock lock(_mutex);
			//reset runtime flags
			_running=true;
			_suspended=false;
			//
			_camera_opened=false;

			//
			std::cout << ">> Setting Up Window" << std::endl;
			setup_window();

			_thread_main_ptr.reset( new boost::thread
        	(
        	boost::bind(&upmc::facetracker_clm_wrapper_t::run_loop, this) 
        	)
			);

			_suspended=false;

			boost::chrono::system_clock::time_point start = boost::chrono::system_clock::now();

			//Wait for a while
			while(!_camera_opened)
			{
				boost::chrono::duration<double> sec = boost::chrono::system_clock::now() - start;
				if (sec.count()>10.0) break;
			};

			return _camera_opened;
		}
		////////////////////////////////////////////////////////////////////////////
		bool facetracker_clm_wrapper_t::suspend()
		{
			boost::mutex::scoped_lock lock(_mutex);

			_suspended=true;
			std::cout<<"CLM Facetracker plugin SUSPENDED ... " << std::endl;

			return true;
		}
		////////////////////////////////////////////////////////////////////////////
		bool facetracker_clm_wrapper_t::resume()
		{

			if(_suspended)
			{
			boost::mutex::scoped_lock lock(_mutex);

			_reset();
			_suspended=false;

			//setup_window();
			}
			std::cout<<"CLM Facetracker plugin RESUMED ... " << std::endl;
			return true;
		}
		////////////////////////////////////////////////////////////////////////////
		bool facetracker_clm_wrapper_t::deactivate()
		{

			//{
			//boost::mutex::scoped_lock lock(_mutex);
			_running=false;
			//}

			if(_thread_main_ptr.get())
				_thread_main_ptr->join();

			//clear_window();
			return true;
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::showCameraSettings() 
		{
			if (_camera_opened)
				_capture.showSettingsWindow(_device);
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::run_loop()
		{
#if defined (_UPMC_JNI_IMPL_)
		JNIEnv *env;
		_jvm->AttachCurrentThread((void **)&env, NULL);

		jclass cls = env->GetObjectClass(_jobject);

		jmethodID midSurpriseEvt = env->GetMethodID(cls, "raiseGestureSurpriseEvt", "()V");
		if (midSurpriseEvt == 0) {
			std::cout << "env->GetMethodID(cls, \"raiseGestureSurpriseEvt\", \"()V\");  FAILED" << std::endl;
		}

		jmethodID midOutputPorts = env->GetMethodID(cls, "newValuesCallback", "(DDDDDDIIII)V");
		if (midOutputPorts == 0) {
			std::cout << "env->GetMethodID(cls, \"newValuesCallback\", \"(DDDDDDIIII)V\");  FAILED" << std::endl;
		}
#endif
		////
		cv::Mat		_gray;
		cv::Point3f euler;
		cv::Point2f pos;
		///
		if(_capture.isDeviceSetup(_device))
		{
			//Wait for the camera to be ready
			std::cout << "Waiting for the camera to be ready ....";
			while (!_capture.isFrameNew(_device) && _running)
			{
				//std::cout << ".";
				boost::thread::yield();
			}	
			///////////////////////////////////////////////////////////////////////////////
			if(_running) {
				std::cout << "Camera Ready." << std::endl;
				_camera_opened=true;
			}
			else
				return;
			///////////////////////////////////////////////////////////////////////////////
			////Allocate CImg 
			_cimg.reset( 
				 new ci::CImg<unsigned char>(
				  static_cast<unsigned int>(_capture.getWidth(_device))
				 ,static_cast<unsigned int>(_capture.getHeight(_device))
				 ,1
				 ,3) 
				 );
			////
			//setup_window();
			///////////////////////////////////////////////////////////////////////////////
			//Init Blink Detection
			upmc::EyesModelSVM eyesDetector;
			//
			try{
				std::ostringstream modelname;
				modelname <<".\\data\\sensor.facetrackerCLM\\EyeStateModels\\" << modelName.second << ".yml";
				std::cout << ">> Loading model file: " << modelname.str() << std::endl;
				eyesDetector.load(modelname.str());
			}
			catch(const std::exception& exp){
				throw exp;
				return;
			}
			upmc::EyesPreprocessorAlt	eyesProcessor(eyesDetector.imgSize().width);
			upmc::EyesDatasetProcessor	process(eyesDetector.imgSize());
			upmc::EyePair eyepair;
			std::pair<int, int> eyeState;
			///////////////////////////////////////////////////////////////////////////////
			//Init Gesture Detection
			upmc::logisticClassifier gesturesDetector;
			///////////////////////////////////////////////////////////////////////////////
			//
			std::cout << "Camera Ready ...." << std::endl;
			double yawRef=0, pitchRef=0, rollRef=0;
			///////////////////////////////////////////////////////////////////////////////
			upmc::tictoc_t tictoc;
			tictoc.tic();
			_posref.x=-1;
			_posref.y=-1;
			///////////////////////////////////////////////////////////////////////////////
			int screenw=_main_disp->screen_width();
			int screenh=_main_disp->screen_height();
			std::cout << ">> Screen resolution: " << screenw << " : " << screenh << std::endl;
			///////////////////////////////////////////////////////////////////////////////
			///The inner loop
			while (_running)
			{		
				//std::cout << "Run Loop ...." << std::endl;
				///
				if(!_suspended)
				{
				//here's the meat
				boost::mutex::scoped_lock lock(_mutex);	

				if (_capture.getPixels(_device, _rawframe.get(), false, true ))
				{				
					cv::Mat image(_height, _width, CV_8UC3, _rawframe.get());
					_frame = image;
					cv::flip(_frame,_frame,1);
				}
				//////////////////////////////
				// FACE DETECT
				//////////////////////////////
				_facetracker.update(_frame);
				//////////////////////////////
				//////////////////////////////				
				if(_facetracker.found())
				{
					//
					euler=_facetracker.getEuler();
					///////////////////////////////////////////////////////
					//Blink
					cv::Mat shape = _facetracker.getImagePts();
					//
					if(eyesProcessor(_frame, shape, eyepair))
					{
						//
						cv::flip(eyepair.left, eyepair.left, 1);
						//
						process(eyepair);
						//SVM Predict
						eyeState = eyesDetector(eyepair);
					}
					else
					{
						eyeState.first=-1;
						eyeState.second=-1;
					}
					///////////////////////////////////////////////////////
					// Gesture
					/////////////////////////////////////////////////////// 
					cv::Mat p=_facetracker.getObjectWeights();
					double prob=gesturesDetector(p);
					//std::cout << "Gesture Detector Prob: " << prob << std::endl;
					if(prob > 0.9){
						
#if defined (_UPMC_JNI_IMPL_)
						
						env->CallVoidMethod(_jobject, midSurpriseEvt);
#else
						std::cout << "C++/DLL >> Sending Event SurpriseEvt" << std::endl;
#endif
					}
					///////////////////////////////////////////////////////
					///
					cv::Point2d nose = _facetracker.getImagePoint(33);
					//std::cout << "Nose coordinates:" << nose.x << "/" << nose.y << std::endl;

					// cv::Point2d brow = _facetracker.getImagePoint(showPoint);
					// cv::circle(_frame, brow, 4, cv::Scalar(255,255,255), 2, CV_AA);
					// std::cout << "Point " << showPoint << " coordinates:" << brow.x << "::" << brow.y << std::endl;

					double gain = 100.0/_facetracker.getScale();
					int lipDistance= (int)(gain*(_facetracker.getImagePoint(64).y-_facetracker.getImagePoint(61).y));
					int browLift= (int)(gain*(_facetracker.getImagePoint(38).y-_facetracker.getImagePoint(19).y));
					//std::cout << "Mount =" << mouth << "    Brow =" << brow << "   scale=" << _facetracker.getScale() <<std::endl;
									

					///////////////////////////////////////////////////////
					///TRACK
					if(!_lktracker.isInit())
					{
						//cv::Point2d nose = _facetracker.getImagePoint(33);
						upmc::vectorOfPoints pts2track;
	
						pts2track.push_back(nose);
						_lktracker.init_points(pts2track, _frame); 
					
						_posref.x=  pts2track.begin()->x;
						_posref.y=  pts2track.begin()->y;
					}
					///////////////////////////////////////////////////////
					upmc::tracking_data_t trackedPts;
					_lktracker.track(_frame, trackedPts);
					cv::circle(_frame, *trackedPts.points.begin(), 4, cv::Scalar(255,255,0), 2, CV_AA);
					///////////////////////////////////////////////////////
					//DRAW
					cv::Mat eyes, eyesBig, eyesRGB;
					upmc::joinSingleChannelImages(eyepair.left, eyepair.right,
						eyes);
					float ratio=eyes.cols/eyes.rows;
					cv::resize(eyes, eyesBig, cv::Size(35*ratio,35));

					cv::cvtColor(eyesBig, eyesRGB, CV_GRAY2BGR);

					eyesRGB.convertTo(eyesRGB, CV_8UC3, 255.);

					eyesRGB.copyTo(
						_frame(cv::Range(0, eyesBig.rows),
							cv::Range(_frame.cols - eyesBig.cols,
									_frame.cols)));

					cv::Scalar c(0,0,0);
					/////////////////////////////////////////////////
					if (eyeState.first == upmc::eOPEN) {
						c = cv::Scalar(0, 255, 0);
					} else if(eyeState.first == upmc::eCLOSE){
						c = cv::Scalar(0, 0, 255);
					}
					/////////////////////////////////////////////////
					cv::rectangle(_frame, cv::Point(_frame.cols - eyesBig.cols, 0),
						cv::Point(_frame.cols - eyesBig.cols / 2, eyesBig.rows), c,
						2, CV_AA);
					/////////////////////////////////////////////////
					if (eyeState.second == upmc::eOPEN) {
						c = cv::Scalar(0, 255, 0);
					} else if(eyeState.second == upmc::eCLOSE){
						c = cv::Scalar(0, 0, 255);
					}
					/////////////////////////////////////////////////
					cv::rectangle(_frame,
						cv::Point(_frame.cols - (eyesBig.cols / 2), 0),
						cv::Point(_frame.cols, eyesBig.rows), c, 2, CV_AA);
					/////////////////////////////////////////////////
					//
					//pos=_facetracker.getPosition();
					//
					double scale=_facetracker.getScale();
					///////////////////////////////////////////////////////
					double distance=
						std::sqrt(std::pow(_posref.x - nose.x,2)+std::pow(_posref.y-nose.y,2));
					//std::cout << "Distance: " << distance << std::endl;
					////
					if(distance> 35)  {
						_lktracker.reset();
						_facetracker.reset();
					}
					else 
					{
#if defined (_UPMC_JNI_IMPL_)
					//Send DATA CallBACK
					env->CallVoidMethod(_jobject, midOutputPorts
						 , r2d*euler.z //roll	
						 , r2d*(pitchRef-euler.x) //should be pitch
						 , r2d*(euler.y-yawRef) //should be yaw
						 , -_posref.x + trackedPts.points.begin()->x
						 , -_posref.y + trackedPts.points.begin()->y
						 , scale
						 , browLift
						 , lipDistance
						 , eyeState.first //CLOSE 1, OPEN 0
						 , eyeState.second
						 );
				
#endif
					///////
					_posref.x=  trackedPts.points.begin()->x;
					_posref.y=  trackedPts.points.begin()->y;

					yawRef=euler.y; //should be yaw
					pitchRef=euler.x; //should be pitch
					rollRef=euler.z;

					_facetracker.draw(_frame);
					}
				}//face  found
				else //face not found
				{
					//something to do here?
					_lktracker.reset();
				}
				//////////////////////////////
				if(tictoc.toc_msec()>cameraDisplayUpdate.second)
				{
					//cv::imshow(_window_title,_frame);
					_cimg->assign(&_frame.operator IplImage()); //Mat -> IplImage -> CImg

					if (!_main_disp->is_closed() && _main_disp->is_resized())
						_main_disp->resize().display(*_cimg.get());
					else
						_main_disp->display(*_cimg.get());

					tictoc.tic();

					//Get events....
					_main_disp->wait(1);

				}
				//////////////////////////////
				}//_suspended
				boost::thread::yield();
				//////////////////////////////
				//cv::waitKey(1); 
				_main_disp->wait(1);
			}//_running

			//
			clear_window();	
			_capture.stopDevice(_device);
			_facetracker.reset();
			_lktracker.reset();

		}//isopened
		else
		{
			std::cout << "Camera NOT Opened!! ...." << std::endl;
		}
		_camera_opened=false;
		}//run_loop
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::initFace()
		{
			boost::mutex::scoped_lock lock(_mutex);

			_reset();
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::setResolution(camRes res)
		{
			boost::mutex::scoped_lock lock(_mutex);
			switch(res)
			{
			case e320_240:
				_width=320;
				_height=240;
				cameraResolution.second=e320_240;
				break;

			case e640_480:
				_width=640;
				_height=480;
				cameraResolution.second=e640_480;
				break;

			case e160_120:
				_width=160;
				_height=120;
				cameraResolution.second=e160_120;
				break;

			default:
				break;
			}
			//_reset();
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::setReferencePose()
		{
			boost::mutex::scoped_lock lock(_mutex);
			_reset_ref=true;
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::setup_window()
		{
			std::cout << ">> facetracker_clm_wrapper_t::setup_window" << std::endl;
			///
			_main_disp.reset(
				new ci::CImgDisplay(_width
				, _height
				,_window_title.c_str())
				);

			cv::FileStorage fs;

			try{
				//Stored as percentage of W and H
				double x, y, width, height;
				///
				fs.open(".//data//sensor.facetrackerCLM//settings.yml", cv::FileStorage::READ);
				cv::FileNode node=fs["window_position"];
				node["x"] >> x;
				node["y"] >> y;
				node["width"]  >> width;
				node["height"] >> height;				
				fs.release();
				///
				int screenw =_main_disp->screen_height();
				int screenh =_main_disp->screen_width();

				_window_pos.x=screenw *x;
				_window_pos.y =screenh*y;
				_window_pos.width=screenw*width;
				_window_pos.height=screenh*height;
				adjust_window(_window_pos);

			}
			catch (const cv::Exception& ){
				//...
			}
		}
		void facetracker_clm_wrapper_t::adjust_window(const cv::Rect& wrect)
		{
			std::cout << ">> Adjusting Window" << std::endl;
			_window_pos=wrect;
			_main_disp->move(wrect.x, wrect.y);
			_main_disp->resize(wrect.width, wrect.height);
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::clear_window()
		{
			cv::FileStorage fs;
			try{
				fs.open(".//data//sensor.facetrackerCLM//settings.yml", cv::FileStorage::WRITE);
				double screenw =_main_disp->screen_height();
				double screenh =_main_disp->screen_width();
				fs << "window_position" << "{" 
					<< "x" << _main_disp->window_x()/screenw 
					<< "y" << _main_disp->window_y()/screenh 
					<< "width" << _main_disp->window_width()/screenw
					<< "height" << _main_disp->window_height()/screenh
					<< "}";
				fs.release();
			}
			catch(const cv::Exception& ){
				//...
			}
			_main_disp.reset(0);
		}
		////////////////////////////////////////////////////////////////////////////
		void facetracker_clm_wrapper_t::_reset()
		{
			_facetracker.reset();
			_lktracker.reset();
		}
		////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////
}//namespace