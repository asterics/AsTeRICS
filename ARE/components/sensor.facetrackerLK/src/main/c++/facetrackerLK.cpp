
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#include "opencv_includes.h"

#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include <ctype.h>
#include <windows.h>

#include "facetrackerLK.h"
#include "videoInput\include\videoInput.h"

using namespace std;

//#define DEBUG_OUTPUT

#define STATUS_IDLE 0
#define STATUS_COULD_NOT_INIT 1
#define STATUS_THREAD_RUNNING 2

#define CAM_CONNECT_TIMEOUT 100   // * 200 ms  = 20 sec
#define FIRST_IMAGE_TIMEOUT 400   // * 10 ms   = 4 sec
#define ERR_THRESHOLD 500
#define GAIN 20

//////////////  C - facetracking functions  /////////////////////////////////////////

int facetracker_work(void );
int facetracker_init(void );
int facetracker_exit(void );
int detect_face (void);
int check_jitter(int mode);


//////////////  C - global variables for facetracking, used from thread  //////////////

#define FLK_NUM_POINTS 2
const char* cascade_name = "data\\sensor.facetrackerLK\\haarcascade_frontalface_alt.xml";

static CvMemStorage* storage = 0;
static CvHaarClassifierCascade* cascade = 0;

int blockCallbacks = false;

//----------------------------------------------

	//capture object
	videoInput * VI=0;
	//info about actual image size
	size_t viWidth;
	size_t viHeight;
	size_t viSize;

static float x_move=0,y_move=0,x_click=0,y_click=0;
//VidFormat vidFmt = {320, 240, 30.0 };

IplImage *grey = 0, *prev_grey = 0, 
         *pyramid = 0, *prev_pyramid = 0, *swap_temp=0,
		 *frame = 0;
unsigned char* frame_buffer =0 ;



int win_size = 11;
HWND drawing_window=0;

CvPoint2D32f* points[2] = {0,0}, *swap_points=0;
char* status = 0;
int need_to_init=1;
int flags = 0;
int initstatus=STATUS_IDLE;

DWORD dwCamStatId;
HANDLE CAMTHREAD=0;
HANDLE CamExitEvent=0;

MSG msg;
//////////////	CImg	//////////////////

#define cimg_plugin1 "CImg\plugins\cimgIPL.h"
#include "CImg\CImg.h"

namespace ci=cimg_library;

ci::CImg<unsigned char> *cimg=0;
//window
ci::CImgDisplay* main_disp=0; //

/////////////////  JNI / Thread interfacing //////////////////////////////////////////

static JavaVM * g_jvm;
static jobject g_obj = NULL;


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


////////////////////////// Here's the beef //////////////////////
int allocate_resources(void)
{

	#ifdef DEBUG_OUTPUT
		 printf("FaceTrackerLK C++ module: now connecting to cam\n" );
	#endif

//----------------------------------------------
	if (!VI) VI= new(videoInput); 
	int numDev=VI->listDevices(false);
	printf("we want to set up %d !\n",cameraSelection);

   //	for (int i=0;i<numDev;i++)
   //	{
   //		printf("Device %d: name=%s height=%d\n",i,VI->getDeviceName(i),VI->getHeight(i));
   //	}
	

	if((numDev<=cameraSelection) || (!strcmp(VI->getDeviceName(cameraSelection),"VDP Source")) || (!strcmp(VI->getDeviceName(cameraSelection),"vfwwdm32.dll")))
	{
		printf("FaceTrackerLK C++ module: ERROR: desired Webcam not available\n");
		return(0);  
	}

	//
	printf("\nFaceTrackerLK C++ module: Opening: %s with desired resolution %dx%d\n", VI->getDeviceName(cameraSelection),xResolution,yResolution);

	VI->setVerbose(false);
	VI->setUseCallback(true);

	VI->setupDevice(cameraSelection, xResolution, yResolution);

	if(!VI->isDeviceSetup(cameraSelection))
	{
		printf("FaceTrackerLK C++ module: ERROR: Could not connect to WebCamera\n");
		return(0);  
	}

	viWidth 	= VI->getWidth(cameraSelection);
	viHeight 	= VI->getHeight(cameraSelection);
	viSize	= VI->getSize(cameraSelection);

	#ifdef DEBUG_OUTPUT
			printf("FaceTrackerLK C++ module: loading classifier cascade\n" );
	#endif
	cascade = (CvHaarClassifierCascade*)cvLoad( cascade_name, 0, 0, 0 );
	if( !cascade )  
	{		
		printf("FaceTrackerLK C++ module: ERROR: Could not load classifier cascade\n" ); 
		return(0); 
	}

	storage = cvCreateMemStorage(0);

	#ifdef DEBUG_OUTPUT
			printf("FaceTrackerLK C++ module: query first frame\n" );
	#endif
	//----------------------------------------------
	//Wait for the camera to be ready
	#ifdef DEBUG_OUTPUT
		std::cout << "FaceTrackerLK C++ module: Waiting for the camera to be ready ....";
	#endif

    int inittimeout=0;
	while (!VI->isFrameNew(cameraSelection) && (inittimeout++<FIRST_IMAGE_TIMEOUT))
	{
		Sleep(10);
	}	

	if(inittimeout==FIRST_IMAGE_TIMEOUT)
	{		
		printf("FaceTrackerLK C++ module: ERROR: Could not acquire image\n" ); 
		return(0); 
	}
			
	#ifdef DEBUG_OUTPUT
		printf ("FaceTrackerLK C++ module: Camera is ready.\n");
	#endif
	//ALLOCATE here the IplImage
	frame=cvCreateImage(cvSize(viWidth, viHeight), IPL_DEPTH_8U, 3);
	//get bytes
	VI->getPixels(cameraSelection, (unsigned char*) frame->imageData, false, true );
	cvFlip(frame, NULL, 1);
	//----------------------------------------------
  	cvWaitKey(1);

	//frame_copy=cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 3 ); 
	//image = cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 3 );
		
	grey = cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 1 );

	prev_grey = cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 1 );
	pyramid = cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 1 ); 
	prev_pyramid = cvCreateImage( cvGetSize(frame), IPL_DEPTH_8U, 1 );

	points[0] = (CvPoint2D32f*)cvAlloc(FLK_NUM_POINTS*sizeof(points[0][0]));
	points[1] = (CvPoint2D32f*)cvAlloc(FLK_NUM_POINTS*sizeof(points[0][0]));

	points[1][1].x=0;
	points[1][1].y=0;

	status = (char*)cvAlloc(FLK_NUM_POINTS);

	//3 channel CImg
	cimg=new ci::CImg<unsigned char>(static_cast<unsigned int>(viWidth),static_cast<unsigned int>(viHeight),1,3);
	return(1);
}

void free_resources(void )
{
	#ifdef DEBUG_OUTPUT
      printf("FaceTrackerLK C++ module: releasing thread resources\n");
	#endif

	if (VI) 
	{
		VI->stopDevice(cameraSelection);
		delete VI; 
		VI=0;
	} 
	if (frame)   { cvReleaseImage( &frame ); frame=0;}			

	if (cascade) { cvFree((void**)&cascade); cascade =0;}
	if (storage) { cvReleaseMemStorage(&storage); storage=0; }
	if (grey)    { cvReleaseImage( &grey ); grey=0;}
	if (prev_grey) {cvReleaseImage( &prev_grey ); prev_grey=0;}
	if (pyramid) { cvReleaseImage( &pyramid ); pyramid=0;}
	if (prev_pyramid) {cvReleaseImage( &prev_pyramid ); prev_pyramid=0;}
	if (points[0]) {cvFree((void**) &points[0] ); points[0]=0;}
	if (points[1]) {cvFree((void**) &points[1] ); points[1]=0;}
	if (status) {cvFree((void**) &status ); status=0;}
	if (cimg) {delete cimg; cimg=0;};
}

void create_paintwindow()
{
	main_disp= new ci::CImgDisplay(*cimg,"CImg");
	main_disp->move(main_disp->screen_width()-viWidth - 20, 30);	//main_disp->screen_height()

	#ifdef DEBUG_OUTPUT
		std::cout << "FaceTrackerLK C++ module: Drawing window ID: " std::endl;
	#endif
}

void resize_paintwindow(int x,int y,int w,int h)
{
	if (main_disp)
	{
		main_disp->move(x,y);
		main_disp->resize(w,h,1); 
	}
	#ifdef DEBUG_OUTPUT
		std::cout << "FaceTrackerLK C++ module: Resizing window ID: "  << std::endl;
	#endif
}

void destroy_paintwindow()
{
	#ifdef DEBUG_OUTPUT
		printf("FaceTrackerLK C++ module: destroying paint window\n");
		cvDestroyWindow( "Detector");
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
		   printf("FaceTrackerLK C++ module: ERROR: Could not allocate resources\n" );
		   free_resources();
	   	   initstatus=STATUS_COULD_NOT_INIT;
		   return (0);
		}

	    if (cameraDisplayUpdate > 0) 
		{
			#ifdef DEBUG_OUTPUT
			  printf( "FaceTrackerLK C++ module: creating paint window ...\n");
			#endif
			create_paintwindow();
		}

		#ifdef DEBUG_OUTPUT
		  printf( "FaceTrackerLK C++ module: camthread is ready, cam connected ...\n");
		#endif

		need_to_init=1;
		flags = 0;
		initstatus=STATUS_THREAD_RUNNING;

		while (!CamThreadDone)  {

			#ifdef DEBUG_OUTPUT
				  printf( "FaceTrackerLK C++ module: CamProc ->calling factracking worker ...\n");
			#endif

            if (blockCallbacks==false)
			{

				facetracker_work();   // perform feature tracking

				// perform JNI callback
				JNIEnv *env;
				g_jvm->AttachCurrentThread((void **)&env, NULL);
				jclass cls = env->GetObjectClass(g_obj);
				jmethodID mid = env->GetMethodID(cls, "newCoordinates_callback", "(IIII)V");
				#ifdef DEBUG_OUTPUT
					printf("FaceTrackerLK C++ module: CamProc -> right before callback %d,%d\n",  (int)x_move , (int)y_move);
				#endif
				if ((x_move<ERR_THRESHOLD)&&(x_move>-ERR_THRESHOLD)&&(y_move<ERR_THRESHOLD)&&(y_move>-ERR_THRESHOLD))
					env->CallVoidMethod(g_obj, mid, (jint) (x_move), (jint) (y_move), (jint) (x_click), (jint) (y_click));
				else {
				#ifdef DEBUG_OUTPUT
					printf("FaceTrackerLK C++ module: CamProc ->dropped out-of-bounds-movement\n");
				#endif
				}

				dwRes = WaitForMultipleObjects(1, hArray, FALSE, 0);
				switch(dwRes)  {
					case WAIT_OBJECT_0: 
						 CamThreadDone = TRUE;
						 #ifdef DEBUG_OUTPUT
						 printf("FaceTrackerLK C++ module: CamProc ->camthread exit event received\n");
		 				 #endif
						 break;
					case WAIT_TIMEOUT:
						 #ifdef DEBUG_OUTPUT
						 printf("FaceTrackerLK C++ module: CamProc ->camthread timed out\n");
		 				 #endif
						 break;                       
					default: break;
			}
		}
	}

	#ifdef DEBUG_OUTPUT
		printf("FaceTrackerLK C++ module: CamProc -> Invoking free_resources();\n");
	#endif

	free_resources();
	destroy_paintwindow();
 	return(1);
}


int facetracker_init( void )
{
	  int inittimeout=0;

	  blockCallbacks=true;
	  initstatus=STATUS_IDLE;

	  #ifdef DEBUG_OUTPUT
		  printf("FaceTrackerLK C++ module: setting up camthread\n" );
	  #endif
      CamExitEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
      if (CamExitEvent == NULL)	  { printf("FaceTrackerLK C++ module: CreateEvent failed (CamThread exit event)\n"); return(0); }

	  CAMTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) CamProc, (LPVOID) NULL, 0, &dwCamStatId);
	  if (CAMTHREAD == NULL) { printf("FaceTrackerLK C++ module: CreateThread failed\n"); return(0); }
	
	  Sleep(200);
	  if (initstatus==STATUS_IDLE) printf("\nWaiting for camera .");
	  while ((initstatus==STATUS_IDLE) && (inittimeout++<CAM_CONNECT_TIMEOUT))
	  {
		  Sleep(200);
		  printf(".");
	  }
	  printf("\n");
 	  blockCallbacks=false;
	  if (initstatus==STATUS_THREAD_RUNNING) return (1);
	  return(0);
}


int facetracker_exit(void)
{
    HANDLE hThreads[1];
    DWORD  dwRes;

    blockCallbacks=true;

	if (CAMTHREAD)	{

      hThreads[0] = CAMTHREAD;
      SetEvent(CamExitEvent);
      dwRes = WaitForMultipleObjects(1, hThreads, FALSE, 10000);

      switch(dwRes)       {

		case WAIT_OBJECT_0:
			#ifdef DEBUG_OUTPUT
				printf("FaceTrackerLK C++ module: facetracker_exit->Thread returned.\n");
			#endif
			break;
		case WAIT_TIMEOUT:
			#ifdef DEBUG_OUTPUT
				printf("FaceTrackerLK C++ module: facetracker_exit->Thread timed out.\n");
			#endif
    	     break;
	    default:
             printf("FaceTrackerLK C++ module: facetracker_exit->Camthread - unknown exit error\n");
             break;
      }

	  // reset thread exit event here
      ResetEvent(CamExitEvent);
      // printf("closing down camthread.\n");
  	  CloseHandle(CamExitEvent);
	  CloseHandle(CAMTHREAD);
  	  blockCallbacks=false;
	  CAMTHREAD=0;
	}

	#ifdef DEBUG_OUTPUT
	   printf("FaceTrackerLK C++ module: facetracker_exit->Camera Module quit.\n");
	#endif
	return(1);
}

long distance_to_act_face(int nx,int ny)
{
	double dx,dy;

	dx=points[1][1].x-(double)nx;
	dy=points[1][1].y-(double)ny;

	return((long)sqrt(dx*dx+dy*dy));
}


int detect_face (void)
{
	CvSeq* faces;
    CvPoint pt1, pt2, nose, chin, best_nose, best_chin;
	long act_distance,best_distance;
	int i;

    //cvPyrDown( frame, frame_copy, CV_GAUSSIAN_5x5 );
    //cvFlip( frame, frame_copy, 0 );

	cvClearMemStorage( storage );

	#ifdef DEBUG_OUTPUT
	   printf("FaceTrackerLK C++ module: detecting face...\n");
	#endif
	faces = cvHaarDetectObjects(grey, cascade, storage,
                            1.2, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(70, 70) );

	best_distance=1000000;

	for( i = 0; i < (faces ? faces->total : 0); i++ ) {
		#ifdef DEBUG_OUTPUT
 		    printf("FaceTrackerLK C++ module:  face found!\n");
		#endif

		CvRect* r = (CvRect*)cvGetSeqElem( faces, i );

		pt1.x = r->x;
		pt2.x = (r->x + r->width);

		pt1.y = (r->y);//frame->height-(r->y);
		pt2.y = (r->y + r->height);//frame->height - (r->y + r->height);

		chin.x=pt1.x+(int)((pt2.x-pt1.x)* 0.5f);
		chin.y=(pt1.y+(int)((pt2.y-pt1.y)* 0.95f));

		nose.x=chin.x;
		nose.y=(pt1.y+(int)((pt2.y-pt1.y)* 0.6f));
		#ifdef DEBUG_OUTPUT
			cvRectangle( grey, pt1, pt2, CV_RGB(255,0,0), 3, 8, 0 );
			cvCircle( grey, chin, 4, CV_RGB(255,255,255), 2, 8,0);
			cvCircle( grey, nose, 4, CV_RGB(255,255,255), 4, 8,0);
			cvShowImage("Detector", grey);
		#endif

		if ((act_distance=distance_to_act_face(nose.x,nose.y)) < best_distance) 
		{ 
			best_distance=act_distance;
			best_nose.x=nose.x;
			best_nose.y=nose.y;
			best_chin.x=chin.x;
			best_chin.y=chin.y;
		}
	}

	if (i>0)
	{
		points[1][0].x=(float)best_chin.x;
		points[1][0].y=(float)best_chin.y;
		cvCircle( frame, best_chin, 4, CV_RGB(255,0,0), 2, 8,0);

		points[1][1].x=(float)best_nose.x;
		points[1][1].y=(float)best_nose.y;
		cvCircle( frame, best_nose, 4, CV_RGB(255,0,0), 2, 8,0);

		points[0][0].x=points[1][0].x;
		points[0][0].y=points[1][0].y;
		points[0][1].x=points[1][1].x;
		points[0][1].y=points[1][1].y;
		return (1);
	}
	return(0);
}


int check_jitter(int mode)
{
    static float orig_dist,orig_angle;
	float dist_error, angle_error;
	
	double c;
	double dx,dy;

	dx=points[1][1].x - points[1][0].x;
	dy=points[1][1].y - points[1][0].y;
    c=sqrt((double)(dx*dx+dy*dy));
	  
    if (c == 0.0) return (1);

	if (mode==1) {  // first calculation of distance and angle 
		  orig_dist=(float)c; dist_error=0.0f; 
		  orig_angle= (float) asin(dx/c) * 57.29577f; angle_error=0.0f;
		  return (0);
	}

	dist_error= (float) fabs(orig_dist-c);
	angle_error=  (float) fabs(orig_angle-asin(dx/c) * 57.29577);
	
	if ((dist_error>=60.0f) || (angle_error>=25.0f)) return (1);
	return(0);
}


int facetracker_work(void)
{
	double acttime;
	int t_display;
	static double old_displaytime=0.;

	acttime = (double)cvGetTickCount();

	#ifdef DEBUG_OUTPUT
		int t_thread;
		static double old_threadtime=0.;
		t_thread = (int)((acttime-old_threadtime)/((double)cvGetTickFrequency()*1000.));
		old_threadtime=acttime;
  		printf("FacetrackerLK C++ module: Thread call (ms latency = %d) !\n", t_thread );
	#endif

	if(!VI->getPixels(cameraSelection, (unsigned char*) frame->imageData, false, true )) return 0;
	cvFlip(frame, NULL, 1);

	cvCvtColor( frame, grey, CV_BGR2GRAY );

	if (need_to_init) {
		if (detect_face()) 	{
	  		need_to_init=0;

			cvFindCornerSubPix( grey, points[1], FLK_NUM_POINTS,
			cvSize(win_size,win_size), cvSize(-1,-1),
			cvTermCriteria(CV_TERMCRIT_ITER,1,1.0));
			//	cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03));
  
			cvCopy(grey,prev_grey,0 );
			cvCopy(pyramid,prev_pyramid,0 );

			points[0][0].x=points[1][0].x;
			points[0][0].y=points[1][0].y;
				
			check_jitter(1);
			flags = 0;				
		} 
		old_displaytime=0.;
	}        
	else  
	{
        cvCalcOpticalFlowPyrLK( prev_grey, grey, prev_pyramid, pyramid,
            points[0], points[1], FLK_NUM_POINTS, cvSize(win_size,win_size), 5, status, 0,
            cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03), flags );

        flags |= CV_LKFLOW_PYR_A_READY;
         
		if ((!status[0] ) || (!status[1]))  
			need_to_init=1; 
		else 
		{
			//[0][x] is 'old', [1][x] is 'new'
			//[x][0] is Chin, [x][1] is Nose

			x_click = (points[1][0].x - points[0][0].x) * GAIN;//Chin
			y_click = (points[1][0].y - points[0][0].y) * GAIN;//Chin
			x_move = (points[1][1].x - points[0][1].x)  * GAIN;//Nose
			y_move = (points[1][1].y - points[0][1].y)  * GAIN;//Nose 
			//Yellow Point CHIN
			cvCircle( frame, cvPointFrom32f(points[1][0]), 4, CV_RGB(255,255,0), 2, 8,0);
			//Green Point NOSE
			cvCircle( frame, cvPointFrom32f(points[1][1]), 4, CV_RGB(0,210,0), 2, 8,0);
    			
			if (check_jitter(0))  need_to_init=1;
		}
	}

  	CV_SWAP( prev_grey, grey, swap_temp );
	CV_SWAP( prev_pyramid, pyramid, swap_temp );
	CV_SWAP( points[0], points[1], swap_points );	

	if (need_to_init)
	{
			x_move=0;
			y_move=0;
			x_click=0;
			y_click=0;
	}

	if (main_disp && (cameraDisplayUpdate>0))
	{
		t_display = (int)((acttime-old_displaytime)/((double)cvGetTickFrequency()*1000.));

		if (t_display>cameraDisplayUpdate)
		{
			old_displaytime=acttime;

			cimg->assign(frame); //Mat -> IplImage -> CImg

			if (!main_disp->is_closed() && main_disp->is_resized())
					main_disp->resize().display(*cimg);
			else
				main_disp->display(*cimg);
			//Get events....
			main_disp->wait(1);
		}
	}
	return(1);
}


///////////////////////////////////////////////////////////////////////////////////////
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_activate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;

	// cout << "FaceTrackerLK C++ module: Webcam with Face Recognition Activated !" << endl;
	error_code = env->GetJavaVM(&g_jvm);

	if(error_code != 0)
	{
		return 0; 
	}

	// todo remove following lines
	jclass cls = env->GetObjectClass(obj);
	jmethodID mid = env->GetMethodID(cls, "newCoordinates_callback", "(IIII)V");
	if (mid == NULL) return -1; /* method not found */
	// explicitly ask for a global reference
	g_obj = env->NewGlobalRef(obj);
	#ifdef DEBUG_OUTPUT
		printf("FaceTrackerLK C++ module: Starting Webcam facetracker\n");
	#endif

	error_code=facetracker_init();
    return (error_code);
}
 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;
	#ifdef DEBUG_OUTPUT
		printf( "FaceTrackerLK C++ module: Deactivate Webcam facetracker\n");
	#endif
	error_code= facetracker_exit();
	env->DeleteGlobalRef(g_obj);
	return error_code;
}

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_getProperty
  (JNIEnv *env, jobject obj, jstring key)
{
	const char *strKey;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

    if(strcmp(cameraSelectionKey, strKey) == 0)
	{
		#ifdef DEBUG_OUTPUT
		 printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraSelectionValue); 
		#endif
		result = env->NewStringUTF(cameraSelectionValue);
	}
    else if(strcmp(cameraResolutionKey, strKey) == 0)
	{
		#ifdef DEBUG_OUTPUT	
		  printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraResolutionValue); 
		#endif
		result = env->NewStringUTF(cameraResolutionValue);
	}
    else if(strcmp(cameraDisplayUpdateKey, strKey) == 0)
	{
		#ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: The value of key \"%s\" is: %s\n", strKey, cameraDisplayUpdateValue); 
		#endif
		result = env->NewStringUTF(cameraDisplayUpdateValue);
	}
	else
	{
		printf("FaceTrackerLK C++ module: Key \"%s\" was not found\n", strKey); 
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

JNIEXPORT jstring JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_setProperty
  (JNIEnv *env, jobject obj, jstring key, jstring value)
{
	const char *strKey;
	const char *strValue;
	jstring result;
	
    if (key == NULL) return NULL; /* OutOfMemoryError already thrown*/
	strKey = env->GetStringUTFChars(key, NULL);

    if (value == NULL) return NULL; /* OutOfMemoryError already thrown */
	strValue = env->GetStringUTFChars(value, NULL);

    if(strcmp(cameraSelectionKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraSelectionValue);
		cameraSelectionValue  = env->GetStringUTFChars(value, NULL);
		cameraSelection=atoi(strValue);
		#ifdef DEBUG_OUTPUT
			printf("Eyetracker C++ module: Set value of \"%s\" to: %d\n", strKey, cameraSelection); 
		#endif
	}
    else if(strcmp(cameraResolutionKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraResolutionValue);
		cameraResolutionValue  = env->GetStringUTFChars(value, NULL);
		cameraResolution=atoi(strValue);
		lookupXYResolution();
		#ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: Set camera resolution to %d/%d\n", xResolution, yResolution); 
		#endif
	}
    else if(strcmp(cameraDisplayUpdateKey, strKey) == 0)
	{
		result = env->NewStringUTF(cameraDisplayUpdateValue);
		cameraDisplayUpdateValue  = env->GetStringUTFChars(value, NULL);
		cameraDisplayUpdate=atoi(strValue);
		#ifdef DEBUG_OUTPUT
		  printf("Eyetracker C++ module: Set value of \"%s\" to: %d\n", strKey, cameraDisplayUpdate); 
		#endif
	}
	else
	{
		printf("FaceTrackerLK C++ module: Key \"%s\" was not found", strKey); 
		result = NULL;
	}
    return result;


}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_initFace
  (JNIEnv *env, jobject obj)
{
		points[1][1].x=0;
		points[1][1].y=0;
		need_to_init=1;
}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_showCameraSettings
  (JNIEnv *env, jobject obj)
{
		#ifdef DEBUG_OUTPUT
			printf("FaceTrackerLK C++ module: show Camera settings !\n");
		#endif
		if(VI->isDeviceSetup(cameraSelection))
		{
			VI->showSettingsWindow(cameraSelection);
		}
}

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_setDisplayPosition
	(JNIEnv *env, jobject obj, int x, int y, int w, int h)
{
    blockCallbacks=true;
	resize_paintwindow(x,y,w,h);
    blockCallbacks=false;
}



JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_saveCameraProfile
  (JNIEnv *env, jobject obj, jstring filename)
{
	const char *strFilename;
	
    if (filename == NULL) return; /* OutOfMemoryError already thrown*/
    blockCallbacks=true;

	strFilename = env->GetStringUTFChars(filename, NULL);

	printf("FacetrackerLK C++ module: saving camera profile %s !\n",strFilename);

	cv::FileStorage profile;

	try {
		 std::string _filename ("data//sensor.facetrackerLK//");
		 _filename.append(strFilename);

		 profile.open(_filename, cv::FileStorage::WRITE);

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
    blockCallbacks=false;

}


JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_loadCameraProfile
  (JNIEnv *env, jobject obj, jstring filename)
{
	const char *strFilename;
	
    if (filename == NULL) return; 
    blockCallbacks=true;

	strFilename = env->GetStringUTFChars(filename, NULL);
	printf("FacetrackerLK C++ module: loading camera profile %s !\n",strFilename);

	cv::FileStorage profile;
	cv::Rect roi;
	std::string _filename ("data//sensor.FacetrackerLK//");
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
    blockCallbacks=false;

}






/*
void log(int lvl, char *fmt, ... )  
{  
    va_list  list;  
    char *s, c;  
    int i;  
  
    char formatted_string[1024];
    char tmp[256];

	formatted_string[0]=0;

    va_start( list, fmt );  
  
     while(*fmt)  
     {  
        if ( *fmt != '%' ) 
		{
			int len = strlen(formatted_string);
			formatted_string[len] = *fmt;
			formatted_string[len + 1] = '\0';
		}
        else  
        {  
           switch ( *++fmt )  
           {  
              case 's':  
                 // set r as the next char in list (string)   
                 s = va_arg( list, char * );  
                 wsprintf(tmp,"%s", s);
				 strcat (formatted_string,tmp);
                 break;  
  
              case 'd':  
                 i = va_arg( list, int );  
                 wsprintf(tmp,"%d", i);
				 strcat (formatted_string,tmp);
                 break;  
  
              case 'c':  
                 c = va_arg( list, char);  
                 wsprintf(tmp,"%c", c);
				 strcat (formatted_string,tmp);
                 break;  
  
              default:  
					int len = strlen(formatted_string);
					formatted_string[len] = *fmt;
					formatted_string[len + 1] = '\0';
                 break;  
           }  
        }  
        ++fmt;  
     }  
     va_end( list );  

	// perform JNI callback
	JNIEnv *env;
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID mid = env->GetMethodID(cls, "report_callback", "(ILjava/lang/String)V");

	env->CallVoidMethod(g_obj, mid, (jint) (lvl), env->NewStringUTF(formatted_string));
}

*/