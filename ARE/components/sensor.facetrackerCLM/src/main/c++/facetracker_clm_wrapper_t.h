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

#ifndef _UPMC_facetracker_clm_wrapper_t_H_INCLUDED_
#define _UPMC_facetracker_clm_wrapper_t_H_INCLUDED_
//-------------------------------------------------------------------------++
#include "facetrackerLib\upmc\facetrackerLib.h"
//-------------------------------------------------------------------------++
#include "lk_kalman_tracker_t.h"
//-------------------------------------------------------------------------++
#include "videoInput\include\videoInput.h"
//-------------------------------------------------------------------------++
#define cimg_plugin1 "CImg\plugins\cimgIPL.h"
#include "CImg\CImg.h"
namespace ci=cimg_library;
//-------------------------------------------------------------------------++
#if defined(_UPMC_JNI_IMPL_)
#include <jni.h>
#endif
////////////////////////////////////////////////////////////////////////////
//-------------------------------------------------------------------------++
//upmc
//-------------------------------------------------------------------------++
#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/shared_array.hpp>
#include <boost/weak_ptr.hpp>
//-------------------------------------------------------------------------++
#include <memory>
//-------------------------------------------------------------------------++
namespace upmc {
	///
	typedef enum {
		 e160_120=0
		,e320_240
		,e640_480
	}camRes;

	///
	class facetracker_clm_wrapper_t : boost::noncopyable
	{
	public:
		static boost::shared_ptr<facetracker_clm_wrapper_t> get();		
		///dtor
		~facetracker_clm_wrapper_t();
	private:
		///ctor
		facetracker_clm_wrapper_t();
		static boost::weak_ptr<facetracker_clm_wrapper_t> _singleton;

	public://runtime management
		bool activate();
		bool suspend();
		bool resume();
		bool deactivate();
		void showCameraSettings();

	public://external triggers
		void initFace();
		void setResolution(camRes res);
		void setDevice();
		void setReferencePose();

		//setup gui elements
		void setup_window();
		void adjust_window(const cv::Rect&);
		void clear_window();

	//Properties ///////////////////////////////////////////////////////////
	public:
		//std::pair<std::string, int>			paintperiod;
		std::pair<std::string, int>			cameraDisplayUpdate;
		std::pair<std::string, int>			cameraIndex;
		std::pair<std::string, camRes>		cameraResolution;
		std::pair<std::string, std::string>	modelName;

	private: //window repaint management

		///camera index
		int _device;//cameraIndex
		//Not exported property (for now)
		cv::Rect _window_pos;
	//********** ///////////////////////////////////////////////////////////

	private:
		//the main loop
		void run_loop();

		///others
		void _reset();

	private:
		///
		upmc::facetrackerLib _facetracker;
		//
		upmc::lk_kalman_tracker_t _lktracker;
		//
		videoInput _capture;
		//
		boost::shared_array<unsigned char> _rawframe;
		size_t _width;
		size_t _height;
		//
		const std::string _window_title;
		///
		cv::Mat _frame;
		cv::Point3f _euler;

		///CImg stuff
		std::auto_ptr<ci::CImg<unsigned char> > _cimg;
		std::auto_ptr<ci::CImgDisplay>			_main_disp;

	private://thread private section
		std::auto_ptr<boost::thread> _thread_main_ptr;
		//
		boost::mutex _mutex;

		//state flags
		volatile bool _running;
		volatile bool _suspended;
		volatile bool _camera_opened;
		volatile cv::Point2f _posref;
		volatile bool _reset_ref;
		//Blink
		std::string	  _svmfile;
		//////////////////////////////////////////////////
	//JNI interface
#if defined(_UPMC_JNI_IMPL_)
	public:
		void set_jvm(JavaVM* jvm) {_jvm=jvm;}
		void set_jobj(jobject jobj) {_jobject=jobj;}
	private:
		JavaVM * _jvm;
		jobject  _jobject;
#endif
//////////////////////////////////////////////////
	};//class


}//namespace

#endif //_UPMC_facetracker_clm_wrapper_t_H_INCLUDED_