
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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

#include "ir_pupil_detector.h"
#include "opencv_includes.h"

#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include <ctype.h>
#include <windows.h>

#include "eyetracker.h"
#include "videoInput\include\videoInput.h"

using namespace std;

//#define DEBUG_OUTPUT

#define STATUS_IDLE 0
#define STATUS_COULD_NOT_INIT 1
#define STATUS_THREAD_RUNNING 2

#define CAM_CONNECT_TIMEOUT 100   // * 200 ms  = 20 sec
#define FIRST_IMAGE_TIMEOUT 400   // * 10 ms   = 4 sec

#define _ASTERICS_USE_CIMG_

uor::IRPupilDetector detector;
cv::Rect roi =detector.get_roi();
cv::RotatedRect rect;
cv::Mat image;


//////////////  C - eyetracking functions  /////////////////////////////////////////
int eyetracker_work(void );
int eyetracker_init(void );
int eyetracker_exit(void );


//----------------------------------------------

	//capture object
	videoInput * VI=0;
	//info about actual image size
	size_t viWidth;
	size_t viHeight;
	size_t viSize;


IplImage  *frame = 0;
unsigned char* frame_buffer =0 ;

float x_location=0,y_location=0;
float x_center=0,y_center=0;
int need_to_init=0;

int currentFrameNumber=0;
HWND drawing_window=0;
int initstatus=STATUS_IDLE;

DWORD dwCamStatId;
HANDLE CAMTHREAD=0;
HANDLE CamExitEvent=0;

static const std::string window_name="Eyetracker";

MSG msg;

/////////////////  JNI / Thread interfacing //////////////////////////////////////////

static JavaVM * g_jvm;
static jobject g_obj = NULL;


// properties 

int cameraDisplayUpdate=100;
const char * cameraDisplayUpdateKey = "cameraDisplayUpdate";
const char * cameraDisplayUpdateValue = "100";

int cameraResolution = 1;
const char * cameraResolutionKey = "cameraResolution";
const char * cameraResolutionValue = "1";
int xResolution=320;
int yResolution=240;

int cameraSelection=0;
const char * cameraSelectionKey = "cameraSelection";
const char * cameraSelectionValue = "0";

int minArea=100;
const char * minAreaKey = "minArea";
const char * minAreaValue = "100";

int maxArea=3000;
const char * maxAreaKey = "maxArea";
const char * maxAreaValue = "3000";

int glintBrightness=100;
const char * glintBrightnessKey = "glintBrightness";
const char * glintBrightnessValue = "100";

double roundness=0.92;
const char * roundnessKey = "roundness";
const char * roundnessValue = "0.92";

void on_mouse( int event, int x, int y, int flags, void* param )
{
	
}
//////////////	CImg	//////////////////

#define cimg_plugin1 "CImg\plugins\cimgIPL.h"
#include "CImg\CImg.h"
#include "upmc\roiManagerCImg.hpp"

namespace ci=cimg_library;

ci::CImg<unsigned char> *cimg=0;
ci::CImgDisplay* main_disp=0; //window

int allocate_resources(void)
{

	if (!VI) VI= new(videoInput); 
	int numDev=VI->listDevices(false);
	printf("we want to set up %d !\n",cameraSelection);


	if((numDev<=cameraSelection) || (!strcmp(VI->getDeviceName(cameraSelection),"VDP Source")) || (!strcmp(VI->getDeviceName(cameraSelection),"vfwwdm32.dll")))
	{
		printf("Eyetracker C++ module: ERROR: desired Webcam not available\n");
		return(0);  
	}

	printf("\nEyetracker C++ module: Opening: %s with desired resolution %dx%d\n", VI->getDeviceName(cameraSelection),xResolution,yResolution);

	VI->setVerbose(false);
	VI->setUseCallback(true);
	VI->setupDevice(cameraSelection, xResolution, yResolution);

	viWidth 	= VI->getWidth(cameraSelection);
	viHeight 	= VI->getHeight(cameraSelection);

	if ((viWidth<(roi.x+roi.width)) || (viHeight<(roi.y+roi.height)))
	{
		roi.x=0;roi.y=0;
		roi.width=viWidth-1;roi.height=viHeight-1;
		detector.set_roi(roi);
		printf("Eyetracker C++ module: ROI error: ROI reset to full size\n");
	}

	viSize	= VI->getSize(cameraSelection);

	if(!VI->isDeviceSetup(cameraSelection))
	{
		printf("Eyetracker C++ module: ERROR: Could not connect to WebCamera\n");
		return(0);  
	}


	#ifdef DEBUG_OUTPUT
			printf("Eyetracker C++ module: Waiting for the camera to be ready ....\n" );
	#endif

	//  Wait for the camera to be ready
    int inittimeout=0;
	while (!VI->isFrameNew(cameraSelection) && (inittimeout++<FIRST_IMAGE_TIMEOUT))
	{
		Sleep(10);
	}	

	if(inittimeout==FIRST_IMAGE_TIMEOUT)
	{		
		printf("Eyetracker C++ module: ERROR: Could not acquire image\n" ); 
		return(0); 
	}
		
	#ifdef DEBUG_OUTPUT
		printf ("Eyetracker C++ module: Camera is ready.\n");
	#endif

	//ALLOCATE here the IplImage (RGB)
	frame=cvCreateImage(cvSize(viWidth, viHeight), IPL_DEPTH_8U, 3);

	//get first image from camera
	VI->getPixels(cameraSelection, (unsigned char*) frame->imageData, false, true );
	cvFlip(frame, NULL, 1);
    cvWaitKey(1);

	//3 channel CImg
	cimg=new ci::CImg<unsigned char>(static_cast<unsigned int>(viWidth),static_cast<unsigned int>(viHeight),1,3);
	return(1);
}

void free_resources(void )
{
	#ifdef DEBUG_OUTPUT
      printf("Eyetracker C++ module: releasing thread resources\n");
	#endif

	 if (VI)
	 {
		VI->stopDevice(cameraSelection);
		delete VI; VI=0;
	 }

	 if (frame)   { cvReleaseImage( &frame ); frame=0;}			
	 if (cimg) {delete cimg; cimg=0;};
}

void create_paintwindow()
{
		#ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: creating paint window\n");
		#endif
		main_disp= new ci::CImgDisplay(*cimg, window_name.c_str());
}

void resize_paintwindow(int x,int y,int w,int h)
{
	if (main_disp)
	{
		main_disp->move(x,y);
		main_disp->resize(w,h,1); 
	}
	#ifdef DEBUG_OUTPUT
		std::cout << "Eyetracker C++ module: Resizing window ID: "  << std::endl;
	#endif
}

void destroy_paintwindow()
{
		#ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: destroying paint window\n");
		#endif
		if (main_disp) {delete main_disp; main_disp=0;}
}


/////////////////  Camera thread: polls picture and calls featuretracking ////////////
/////////////////  JNI callback of feature positions is performed     ////////////////
DWORD WINAPI CamProc(LPVOID lpv)
{
    HANDLE     hArray[1];
	DWORD dwRes;
	BOOL CamThreadDone=FALSE;
	hArray[0] = CamExitEvent;

	static int cnt =0;

	    if (!allocate_resources())
		{
		   printf("Eyetracker C++ module: ERROR: Could not allocate resources\n" );
		   free_resources();
   	   	   initstatus=STATUS_COULD_NOT_INIT;
		   return (0);
		}

	    if (cameraDisplayUpdate > 0) 
		{
			create_paintwindow();
		}

		#ifdef DEBUG_OUTPUT
		  printf( "Eyetracker C++ module: camthread is ready, cam connected ...\n");
		#endif

		need_to_init=1;
		initstatus=STATUS_THREAD_RUNNING;

		while (!CamThreadDone)  {

			if (eyetracker_work())   // perform feature tracking
			{
				// perform JNI callback
				JNIEnv *env;
				g_jvm->AttachCurrentThread((void **)&env, NULL);
				jclass cls = env->GetObjectClass(g_obj);
				jmethodID mid = env->GetMethodID(cls, "newCoordinates_callback", "(II)V");
				#ifdef DEBUG_OUTPUT
					printf("Eyetracker C++ module: CamProc -> right before callback %d,%d\n",  (int)x_location , (int)y_location);
				#endif
				env->CallVoidMethod(g_obj, mid, (jint) (x_location-x_center), (jint) (y_location-y_center));
			}

			dwRes = WaitForMultipleObjects(1, hArray, FALSE, 0);
			switch(dwRes)  {
				case WAIT_OBJECT_0: 
					 CamThreadDone = TRUE;
					 #ifdef DEBUG_OUTPUT
					 printf("Eyetracker C++ module: CamProc ->camthread exit event received\n");
		 			 #endif
					 break;
				case WAIT_TIMEOUT:
					 #ifdef DEBUG_OUTPUT
					 printf("Eyetracker C++ module: CamProc ->camthread timed out\n");
		 			 #endif
					 break;                       
				default: break;
		}
	}

	free_resources();
	destroy_paintwindow();
 	return(1);
}


int eyetracker_init( void )
{
	  int inittimeout=0;

	  initstatus=STATUS_IDLE;

	  #ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: setting up camthread\n" );
	  #endif
      CamExitEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
      if (CamExitEvent == NULL)	  { printf("Eyetracker C++ module: CreateEvent failed (CamThread exit event)\n"); return(0); }

	  CAMTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) CamProc, (LPVOID) NULL, 0, &dwCamStatId);
	  if (CAMTHREAD == NULL) { printf("Eyetracker C++ module: CreateThread failed\n"); return(0); }
	
	  Sleep(200);
	  if (initstatus==STATUS_IDLE) printf("\nWaiting for camera .");
	  while ((initstatus==STATUS_IDLE) && (inittimeout++<CAM_CONNECT_TIMEOUT))
	  {
		  Sleep(200);
		  printf(".");
	  }
	  printf("\n");
	  if (initstatus==STATUS_THREAD_RUNNING) return (1);
	  return(0);
}


int eyetracker_exit(void)
{
    HANDLE hThreads[1];
    DWORD  dwRes;

	if (CAMTHREAD)	{

      hThreads[0] = CAMTHREAD;
      SetEvent(CamExitEvent);
      dwRes = WaitForMultipleObjects(1, hThreads, FALSE, 10000);

      switch(dwRes)       {

		case WAIT_OBJECT_0:
			#ifdef DEBUG_OUTPUT
				printf("Eyetracker C++ module: eyetracker_exit->Thread returned.\n");
			#endif
			break;
		case WAIT_TIMEOUT:
			#ifdef DEBUG_OUTPUT
				printf("Eyetracker C++ module: eyetracker_exit->Thread timed out.\n");
			#endif
    	     break;
	    default:
             printf("Eyetracker C++ module: eyetracker_exit->Camthread - unknown exit error\n");
             break;
      }

	  // reset thread exit event here
      ResetEvent(CamExitEvent);
      // printf("closing down camthread.\n");
  	  CloseHandle(CamExitEvent);
	  CloseHandle(CAMTHREAD);
	  CAMTHREAD=0;
	}

	#ifdef DEBUG_OUTPUT
	   printf("Eyetracker C++ module: eyetracker_exit->Camera Module quit.\n");
	#endif
	return(1);
}



int eyetracker_work(void)
{
	double acttime;
	int t_display;
	static double old_displaytime=0.;
	int valid;

	acttime = (double)cvGetTickCount();

	#ifdef DEBUG_OUTPUT
		int t_thread;
		static double old_threadtime=0.;
		t_thread = (int)((acttime-old_threadtime)/((double)cvGetTickFrequency()*1000.));
		old_threadtime=acttime;
  		printf("Eyetracker C++ module: Thread call (ms latency = %d) !\n", t_thread );
	#endif

	if(!VI->getPixels(cameraSelection, (unsigned char*) frame->imageData, false, true )) return 0;
	cvFlip(frame, NULL, 1);
	image=frame;   // set the cvMat header to the iplImage

	// DETECT PUPIL
    if(detector.detectPupil(image, rect))
  	{
		valid=1;
	    cv::ellipse(image, rect, cv::Scalar(0,0,255), 2);
	}
	else //draw best found with another color (just debug)
	{
		valid=0;
		cv::ellipse(image, rect, cv::Scalar(255,0,0), 2);
	}

	// scale x/y location to minimize later int rounding errors
	x_location=rect.center.x*20;
	y_location=rect.center.y*20;

	if (need_to_init)
	{
			x_center=x_location;
			y_center=y_location;
			need_to_init=0;
	}

	if (main_disp && (cameraDisplayUpdate>0))
	{
		t_display = (int)((acttime-old_displaytime)/((double)cvGetTickFrequency()*1000.));

		if (t_display>cameraDisplayUpdate)
		{
			old_displaytime=acttime;

			//DRAW CURRENT ROI
			//handles ROI events first
			upmc::roiManagerCImg::handle(*main_disp, detector);
			////////////////////////////////////////////////
			if(upmc::roiManagerCImg::currentState==upmc::roiManagerCImg::MIDLE)
			{
				cv::rectangle(image, detector.get_roi(), cv::Scalar(0,255,0), 2);
			}
			else //changing ROI
			{
				cv::Rect roi= upmc::roiManagerCImg::getRect(detector.width(), detector.height());
				cv::rectangle(image, roi, cv::Scalar(0,0,220), 2);
			}

			//Finally DRAW
			cimg->assign(&image.operator IplImage()); //Mat -> IplImage -> CImg

			if (!main_disp->is_closed() && main_disp->is_resized())
					main_disp->resize().display(*cimg);
			else main_disp->display(*cimg);
			//Get events....
			main_disp->wait(1);
		}
	}
	return(valid);
}


///////////////////////////////////////////////////////////////////////////////////////
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_activate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;

	// cout << "Eyetracker C++ module activated !" << endl;
	error_code = env->GetJavaVM(&g_jvm);

	if(error_code != 0)
	{
		return 0; 
	}

	// todo remove following lines
	jclass cls = env->GetObjectClass(obj);
	jmethodID mid = env->GetMethodID(cls, "newCoordinates_callback", "(II)V");
	if (mid == NULL) return -1; /* method not found */
	// explicitly ask for a global reference
	g_obj = env->NewGlobalRef(obj);
	#ifdef DEBUG_OUTPUT
		printf("Eyetracker C++ module: Starting Webcam eyetracker\n");
	#endif
	error_code=eyetracker_init();
    return (error_code);
}
 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;

	#ifdef DEBUG_OUTPUT
		printf( "Eyetracker C++ module: Deactivate Webcam eyetracker\n");
	#endif
	error_code= eyetracker_exit();
	env->DeleteGlobalRef(g_obj);
	return error_code;
}

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_getProperty
  (JNIEnv *env, jobject obj, jstring key)
{
	const char *strKey;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

	// printf("Getting property with key: %s\n", strKey); 
    if(strcmp(cameraSelectionKey, strKey) == 0)
	{
		//printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraSelectionValue); 
		result = env->NewStringUTF(cameraSelectionValue);
	}
    else if(strcmp(cameraResolutionKey, strKey) == 0)
	{
		//printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraResolutionValue); 
		result = env->NewStringUTF(cameraResolutionValue);
	}
    else if(strcmp(cameraDisplayUpdateKey, strKey) == 0)
	{
		//printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraDisplayUpdateValue); 
		result = env->NewStringUTF(cameraDisplayUpdateValue);
	}
    else if(strcmp(minAreaKey, strKey) == 0)
	{
		result = env->NewStringUTF(minAreaValue);
	}
    else if(strcmp(maxAreaKey, strKey) == 0)
	{
		result = env->NewStringUTF(maxAreaValue);
	}
    else if(strcmp(glintBrightnessKey, strKey) == 0)
	{
		result = env->NewStringUTF(glintBrightnessValue);
	}
    else if(strcmp(roundnessKey, strKey) == 0)
	{
		result = env->NewStringUTF(roundnessValue);
	}
	else
	{
		printf("Eyetracker C++ module: Key \"%s\" was not found\n", strKey); 
		result = NULL;
	}
    return result;
}

void lookupXYResolution(void)
{
	switch (cameraResolution) {
		case 0: xResolution=160; yResolution=120; break; 
		case 1: xResolution=320; yResolution=240; break; 
		case 2: xResolution=352; yResolution=288; break; 
		case 3: xResolution=640; yResolution=480; break; 
		case 4: xResolution=800; yResolution=600; break; 
		case 5: xResolution=1024; yResolution=768; break; 
		case 6: xResolution=1600; yResolution=1200; break; 
		default: xResolution=320; yResolution=240; break; 
	}
}

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_setProperty
  (JNIEnv *env, jobject obj, jstring key, jstring value)
{
	const char *strKey;
	const char *strValue;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

    if (value == NULL) return NULL; /* OutOfMemoryError already thrown */
	strValue = env->GetStringUTFChars(value, NULL);
	//printf("Setting property %s to %s\n", strKey,strValue); 
    if(strcmp(cameraSelectionKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraSelectionValue);
		cameraSelectionValue  = env->GetStringUTFChars(value, NULL);
		cameraSelection=atoi(strValue);
		//printf("Eyetracker C++ module: Set value of \"%s\" to: %d\n", strKey, cameraSelection); 
	}
    else if(strcmp(cameraResolutionKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraResolutionValue);
		cameraResolutionValue  = env->GetStringUTFChars(value, NULL);
		cameraResolution=atoi(strValue);
		lookupXYResolution();
		//printf("Eyetracker C++ module: Set camera resolution to %d/%d\n", xResolution, yResolution); 
	}
    else if(strcmp(cameraDisplayUpdateKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraDisplayUpdateValue);
		cameraDisplayUpdateValue  = env->GetStringUTFChars(value, NULL);
		cameraDisplayUpdate=atoi(strValue);
		//printf("Eyetracker C++ module: Set value of \"%s\" to: %d\n", strKey, cameraDisplayUpdate); 
	}
    else if(strcmp(minAreaKey, strKey) == 0)
	{
		result = env->NewStringUTF(minAreaValue);
		minAreaValue  = env->GetStringUTFChars(value, NULL);
		minArea=atoi(strValue);
		detector.set_minArea(minArea);
	}
    else if(strcmp(maxAreaKey, strKey) == 0)
	{
		result = env->NewStringUTF(maxAreaValue);
		maxAreaValue  = env->GetStringUTFChars(value, NULL);
		maxArea=atoi(strValue);
		detector.set_maxArea(maxArea);
	}
    else if(strcmp(glintBrightnessKey, strKey) == 0)
	{
		result = env->NewStringUTF(minAreaValue);
		glintBrightnessValue  = env->GetStringUTFChars(value, NULL);
		glintBrightness=atoi(strValue);
		detector.set_max_grey_value(glintBrightness);
	}
    else if(strcmp(roundnessKey, strKey) == 0)
	{
		result = env->NewStringUTF(minAreaValue);
		roundnessValue  = env->GetStringUTFChars(value, NULL);
		roundness=atof(strValue);
		detector.set_best_roundness(roundness);
	}
	else
	{
		/* printf("Key \"%s\" was not found", strKey); */
		result = NULL;
	}

    //env->ReleaseStringUTFChars(key, strKey);
    //env->ReleaseStringUTFChars(value, strValue);
    return result;

}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_calibrate
  (JNIEnv *env, jobject obj)
{
		printf("Eyetracker C++ module: calibration called !\n");
		need_to_init=1;
}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_showCameraSettings
  (JNIEnv *env, jobject obj)
{
		printf("Eyetracker C++ module: show Camera settings !\n");
		if(VI->isDeviceSetup(cameraSelection))
		{

//			VI->setVideoSettingCameraPct(cameraSelection,0,0.6,NULL); // PAN
//			VI->setVideoSettingCamera(cameraSelection,0,2,NULL,false);

//			VI->setVideoSettingCameraPct(cameraSelection,1,0.4,NULL); // TILT
//			VI->setVideoSettingCamera(cameraSelection,1,3,NULL,false);

//			VI->setVideoSettingCameraPct(cameraSelection,3,0.3,NULL); // ZOOM
//			VI->setVideoSettingCamera(cameraSelection,3,1,NULL,false);

			/*
			VI->setVideoSettingFilterPct(cameraSelection,0,0.3,NULL);  // Brightness
			VI->setVideoSettingFilterPct(cameraSelection,1,0.4,NULL);  // Contrast
			VI->setVideoSettingFilterPct(cameraSelection,2,0.5,NULL);  // Hue
			VI->setVideoSettingFilterPct(cameraSelection,3,0.6,NULL);  // Saturation
			VI->setVideoSettingFilterPct(cameraSelection,4,0.7,NULL);  // Sharpness
			VI->setVideoSettingFilterPct(cameraSelection,5,0.8,NULL);  // Gamma
			VI->setVideoSettingFilterPct(cameraSelection,6,0.9,NULL);  // 
			VI->setVideoSettingFilterPct(cameraSelection,7,0.8,NULL);  // 
			VI->setVideoSettingFilterPct(cameraSelection,8,0.7,NULL);  // Colour activation
			VI->setVideoSettingFilterPct(cameraSelection,9,0.4,NULL);  // Background


			*/

			VI->showSettingsWindow(cameraSelection);
		}
}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_setDisplayPosition
	(JNIEnv *env, jobject obj, int x, int y, int w, int h)
{
	   resize_paintwindow(x,y,w,h);
}



JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_saveCameraProfile
  (JNIEnv *env, jobject obj, jstring filename)
{
	const char *strFilename;
	
    if (filename == NULL) return; /* OutOfMemoryError already thrown*/
	strFilename = env->GetStringUTFChars(filename, NULL);

	printf("Eyetracker C++ module: saving camera profile %s !\n",strFilename);

	cv::FileStorage profile;
	cv::Rect roi;
	roi=detector.get_roi();

	try {
		 std::string _filename ("data//sensor.eyetracker//");
		 _filename.append(strFilename);

		 profile.open(_filename, cv::FileStorage::WRITE);
		 //ROI
		 profile << "roi" << "{" 
			<< "x"<< roi.x
			<< "y" << roi.y
			<< "width" << roi.width
			<< "height" << roi.height
			<< "}";

		 long value;
		 long tmp;
		 std::string paramname;
		 char str[100];

		 for (int i=0;i<15;i++)
		 { 	
			wsprintf(str,"FilterParameter_%d",i);
			paramname=str;

			VI->getVideoSettingFilter(cameraSelection,i,tmp,tmp,tmp,value,tmp,tmp);
			// std::cout << "Writing " << paramname << "value =" << value << std::endl;
			profile << paramname << (int)value ;
		 }
		 printf("\n");
		 for (int i=0;i<15;i++)
		 { 
			wsprintf(str,"CameraParameter_%d",i);
			paramname=str;

			VI->getVideoSettingCamera(cameraSelection,i,tmp,tmp,tmp,value,tmp,tmp);
			// std::cout << "Writing " << paramname << ", value =" << value << std::endl;
			profile << paramname << (int)value ;
		 }
		 profile.release();
	}
	catch(const cv::Exception& exp){
		std::cout << exp.msg << std::endl;
	}

}


JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_Bridge_loadCameraProfile
  (JNIEnv *env, jobject obj, jstring filename)
{
	const char *strFilename;
	
    if (filename == NULL) return; /* OutOfMemoryError already thrown*/
	strFilename = env->GetStringUTFChars(filename, NULL);
	printf("Eyetracker C++ module: loading camera profile %s !\n",strFilename);

	cv::FileStorage profile;
	cv::Rect roi;
	std::string _filename ("data//sensor.eyetracker//");
	_filename.append(strFilename);

	try {
		profile.open(_filename, cv::FileStorage::READ);
	}
	catch(const cv::Exception& exp){
		//std::cout << exp.msg << std::endl;
		profile.release();
	}

	if(profile.isOpened())
	{//Read and set values.

		cv::FileNode node = profile["roi"];
		node["x"] >> roi.x;
		node["y"] >> roi.y;
		node["width"] >> roi.width;
		node["height"] >> roi.height;
		detector.set_roi(roi);
		
		 int value;
		 std::string paramname;
	     char str[100];

		 for (int i=0;i<15;i++)
		 { 			
			wsprintf(str,"FilterParameter_%d",i);
			paramname=str;
			profile[paramname] >> value;
			// std::cout << "Setting " << paramname << "value =" << value << std::endl;
			VI->setVideoSettingFilter(cameraSelection,i,(long)value,0,false);
		 }

		 for (int i=0;i<15;i++)
		 { 
			wsprintf(str,"CameraParameter_%d",i);
			paramname=str;
			profile[paramname] >> value;
			// std::cout << "Setting " << paramname << "value =" << value << std::endl;
			VI->setVideoSettingCamera(cameraSelection,i,(long)value,0,false);
		 }
	}
	profile.release();
}

