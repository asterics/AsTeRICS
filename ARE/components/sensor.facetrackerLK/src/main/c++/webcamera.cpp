
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



 // #define _USE_WIN7_LAPTOPCAM_

#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include "cv.h"
//#include "cvcam.h"
#include "highgui.h"
#include "webcamera.h"
#include <ctype.h>
#include <windows.h>



using namespace std;

// #define DEBUG_OUTPUT

#define STATUS_IDLE 0
#define STATUS_COULD_NOT_INIT 1
#define STATUS_THREAD_RUNNING 2

#define CAM_CONNECT_TIMEOUT 10000
#define ERR_THRESHOLD 500
#define GAIN 20

//////////////  C - facetracking functions  /////////////////////////////////////////

int facetracker_work(void );
int facetracker_init(void );
int facetracker_exit(void );
int detect_face (void);
int check_jitter(int mode);


//////////////  C - global variables for facetracking, used from tread  //////////////

#define NUM_POINTS 2
const char* cascade_name = "data\\sensor.facetrackerLK\\haarcascade_frontalface_alt.xml";

static CvMemStorage* storage = 0;
static CvHaarClassifierCascade* cascade = 0;
CvCapture* capture = 0;
static float x_move=0,y_move=0,x_click=0,y_click=0;

//VidFormat vidFmt = {320, 240, 30.0 };

IplImage *image = 0, *grey = 0, *prev_grey = 0, 
         *pyramid = 0, *prev_pyramid = 0, *swap_temp=0,
		 *frame = 0, *frame_copy = 0;
unsigned char* frame_buffer =0 ;



int win_size = 11;
int paintcnt=0;
int paintperiod=5;
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

/////////////////  JNI / Thread interfacing //////////////////////////////////////////

static JavaVM * g_jvm;
static jobject g_obj = NULL;

const char * pollingIntervalKey = "displayFrame";
const char * pollingIntervalValue = "5";



void on_mouse( int event, int x, int y, int flags, void* param )
{
	
}

int allocate_resources(void)
{

	
		#ifdef DEBUG_OUTPUT
			 printf("now connecting to cam\n" );
		#endif
		capture = cvCaptureFromCAM( 0 );
	    //     cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH, 320 ); 
	    //     cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT, 240 ); 
	    if(!capture )   
	    { 
			printf("C++: ERROR: Could not connect to WebCamera\n");
			return(0);  
	    }



		#ifdef DEBUG_OUTPUT
			 printf("loading classifier cascade\n" );
		#endif
	    cascade = (CvHaarClassifierCascade*)cvLoad( cascade_name, 0, 0, 0 );
	    if( !cascade )  
	    {		
		  printf("C++: ERROR: Could not load classifier cascade\n" ); 
		  return(0); 
	    }

        storage = cvCreateMemStorage(0);

		#ifdef DEBUG_OUTPUT
			 printf("query first frame\n" );
		#endif
		frame = cvQueryFrame( capture );
  	    cvWaitKey(1);

		frame_copy=cvCreateImage( cvGetSize(frame), 8, 3 );
		image = cvCreateImage( cvGetSize(frame), 8, 3 );
		image->origin = frame->origin;
		grey = cvCreateImage( cvGetSize(frame), 8, 1 );
		prev_grey = cvCreateImage( cvGetSize(frame), 8, 1 );
		pyramid = cvCreateImage( cvGetSize(frame), 8, 1 );
		prev_pyramid = cvCreateImage( cvGetSize(frame), 8, 1 );

		points[0] = (CvPoint2D32f*)cvAlloc(NUM_POINTS*sizeof(points[0][0]));
		points[1] = (CvPoint2D32f*)cvAlloc(NUM_POINTS*sizeof(points[0][0]));

		points[1][1].x=0;
		points[1][1].y=0;

		status = (char*)cvAlloc(NUM_POINTS);
		return(1);
}

void free_resources(void )
{
	#ifdef DEBUG_OUTPUT
      printf("releasing thread resources\n");
	#endif
	if (capture) { cvReleaseCapture( &capture ); capture=0; }
	if (cascade) { cvFree((void**)&cascade); cascade =0;}
	if (storage) { cvReleaseMemStorage(&storage); storage=0; }
	if (frame_copy)   { cvReleaseImage( &frame_copy ); frame_copy=0;}
	if (image)   { cvReleaseImage( &image ); image=0;}
	if (grey)    { cvReleaseImage( &grey ); grey=0;}
	if (prev_grey) {cvReleaseImage( &prev_grey ); prev_grey=0;}
	if (pyramid) { cvReleaseImage( &pyramid ); pyramid=0;}
	if (prev_pyramid) {cvReleaseImage( &prev_pyramid ); prev_pyramid=0;}
	if (points[0]) {cvFree((void**) &points[0] ); points[0]=0;}
	if (points[1]) {cvFree((void**) &points[1] ); points[1]=0;}
	if (status) {cvFree((void**) &status ); status=0;}
}

void create_paintwindow()
{
		#ifdef DEBUG_OUTPUT
		  printf("creating paint window\n");
		#endif
  		cvNamedWindow( "Camera", 0 );
		cvWaitKey(1);
		//cvSetMouseCallback( "Camera", on_mouse, 0 );
		drawing_window=FindWindow(0, (LPCSTR) "Camera");
}

void destroy_paintwindow()
{
		#ifdef DEBUG_OUTPUT
		  printf("destroying paint window\n");
		#endif
		drawing_window=0;
  		cvDestroyWindow( "Camera");
		cvWaitKey(1);
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
		   printf("C++: ERROR: Could not allocate resources\n" );
		   free_resources();
		   return (0);
		}

	    if (paintperiod > 0) 
		{
			#ifdef DEBUG_OUTPUT
			  printf( "creating paint window ...\n");
			#endif
			create_paintwindow();
		}

		#ifdef DEBUG_OUTPUT
		  printf( "camthread is ready, cam connected ...\n");
		#endif

		need_to_init=1;
		flags = 0;
		initstatus=STATUS_THREAD_RUNNING;

		while (!CamThreadDone)  {

			#ifdef DEBUG_OUTPUT
				  printf( "calling factracking worker ...\n");
			#endif

			facetracker_work();   // perform feature tracking

			// perform JNI callback
			JNIEnv *env;
			g_jvm->AttachCurrentThread((void **)&env, NULL);
			jclass cls = env->GetObjectClass(g_obj);
			jmethodID mid = env->GetMethodID(cls, "newCoordinates_callback", "(IIII)V");
			#ifdef DEBUG_OUTPUT
				printf("C++ right before callback %d,%d\n",  (int)x_move , (int)y_move);
			#endif
			if ((x_move<ERR_THRESHOLD)&&(x_move>-ERR_THRESHOLD)&&(y_move<ERR_THRESHOLD)&&(y_move>-ERR_THRESHOLD))
				env->CallVoidMethod(g_obj, mid, (jint) (x_move), (jint) (y_move), (jint) (x_click), (jint) (y_click));
			else {
			#ifdef DEBUG_OUTPUT
				printf("dropped out-of-bounds-movement\n");
			#endif
			}

			dwRes = WaitForMultipleObjects(1, hArray, FALSE, 0);
			switch(dwRes)  {
				case WAIT_OBJECT_0: 
					 CamThreadDone = TRUE;
					 #ifdef DEBUG_OUTPUT
					 printf("camthread exit event received\n");
		 			 #endif
					 break;
				case WAIT_TIMEOUT:
					 #ifdef DEBUG_OUTPUT
					 printf("camthread timed out\n");
		 			 #endif
					 break;                       
				default: break;
		}
	}

	free_resources();
	destroy_paintwindow();
 	return(1);
}


int facetracker_init( void )
{
	  int inittimeout=0;

	  initstatus=STATUS_COULD_NOT_INIT;

	  #ifdef DEBUG_OUTPUT
		  printf("setting up camthread\n" );
	  #endif
      CamExitEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
      if (CamExitEvent == NULL)	  { printf("CreateEvent failed (CamThread exit event)\n"); return(0); }

	  CAMTHREAD =   CreateThread( NULL, 0, (LPTHREAD_START_ROUTINE) CamProc, (LPVOID) NULL, 0, &dwCamStatId);
	  if (CAMTHREAD == NULL) { printf("CreateThread failed\n"); return(0); }
	
	  while ((initstatus==STATUS_COULD_NOT_INIT) && (inittimeout++<CAM_CONNECT_TIMEOUT))
	  {
		  cvWaitKey(1);
		  Sleep(1);
	  }
	  if (initstatus==STATUS_THREAD_RUNNING) return (1);
	  return(0);
}


int facetracker_exit(void)
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
				printf("Thread returned.\n");
			#endif
			break;
		case WAIT_TIMEOUT:
			#ifdef DEBUG_OUTPUT
				printf("Thread timed out.\n");
			#endif
    	     break;
	    default:
             printf("C++: Camthread - unknown exit error\n");
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
	   printf("Camera Module quit.\n");
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

/*
int detect_face (void)
{
    int scale = 1;
	CvSeq* faces;
    CvPoint pt1, pt2, ptd;

    //cvPyrDown( frame, frame_copy, CV_GAUSSIAN_5x5 );
	   
	   if( frame->origin == IPL_ORIGIN_TL ) cvCopy( frame, frame_copy, 0 );
       else cvFlip( frame, frame_copy, 0 );

	   cvClearMemStorage( storage );
	   #ifdef DEBUG_OUTPUT
	     printf("detecting face...\n");
       #endif		
	   faces = cvHaarDetectObjects( frame_copy, cascade, storage,
                               1.2, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(70, 70) );

	   //  for( i = 0; i < (faces ? faces->total : 0); i++ )

	   if (faces->total)   {   // there has been at least one face detected, take the first
			#ifdef DEBUG_OUTPUT
 		       printf("face found!\n");
			#endif
			CvRect* r = (CvRect*)cvGetSeqElem( faces, 0); //i );
			pt1.x = r->x*scale;
			pt2.x = (r->x+r->width)*scale;
			pt1.y = r->y*scale;
		    pt2.y = (r->y+r->height)*scale;
			cvRectangle( frame_copy, pt1, pt2, CV_RGB(255,0,0), 3, 8, 0 );

			ptd.x=pt1.x+(int)((pt2.x-pt1.x)* 0.5f);
			ptd.y=pt1.y+(int)((pt2.y-pt1.y)* 0.95f);
			points[1][0].x=(float)ptd.x;
			points[1][0].y=(float)(frame->height-ptd.y);
			cvCircle( frame_copy, ptd, 4, CV_RGB(255,0,0), 2, 8,0);

			ptd.x=pt1.x+(int)((pt2.x-pt1.x)* 0.5f);
			ptd.y=pt1.y+(int)((pt2.y-pt1.y)* 0.6f);
			points[1][1].x=(float)ptd.x;
			points[1][1].y=(float)(frame->height-ptd.y);
			cvCircle( frame_copy, ptd, 4, CV_RGB(255,0,0), 2, 8,0);

			points[0][0].x=points[1][0].x;
			points[0][0].y=points[1][0].y;
			points[0][1].x=points[1][1].x;
			points[0][1].y=points[1][1].y;

			cvFlip( frame_copy, image, 0 );
			paintcnt=1000;
			return(1);	  
    }
	return(0);
}
*/

int detect_face (void)
{
	CvSeq* faces;
    CvPoint pt1, pt2, nose, chin, best_nose, best_chin;
	long act_distance,best_distance;
	int i;

    //cvPyrDown( frame, frame_copy, CV_GAUSSIAN_5x5 );
	   
	   cvFlip( frame, frame_copy, 0 );

	   cvClearMemStorage( storage );
	   #ifdef DEBUG_OUTPUT
	     printf("detecting face...\n");
       #endif		
	   faces = cvHaarDetectObjects( frame_copy, cascade, storage,
                               1.2, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(70, 70) );

	   best_distance=1000000;
	   //if (faces->total)   {   // there has been at least one face detected, take the first
	   for( i = 0; i < (faces ? faces->total : 0); i++ ) {
			#ifdef DEBUG_OUTPUT
 		       printf("face found!\n");
			#endif
			CvRect* r = (CvRect*)cvGetSeqElem( faces, i );
			pt1.x = r->x;
			pt2.x = (r->x+r->width);
			pt1.y = frame->height-(r->y);
		    pt2.y = frame->height-(r->y+r->height);
			cvRectangle( frame, pt1, pt2, CV_RGB(255,0,0), 3, 8, 0 );

			chin.x=pt1.x+(int)((pt2.x-pt1.x)* 0.5f);
			chin.y=(pt1.y+(int)((pt2.y-pt1.y)* 0.95f));
			nose.x=chin.x;
			nose.y=(pt1.y+(int)((pt2.y-pt1.y)* 0.6f));

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
	double t;
	static double old_count=0.;

		t = (double)cvGetTickCount()-old_count;
		old_count=(double)cvGetTickCount();

		#ifdef DEBUG_OUTPUT
  			printf("Thread call (ms latency = %d) !\n", (int)(t/((double)cvGetTickFrequency()*1000.)));
		#endif
	
		frame = cvQueryFrame( capture );
		if(!frame) { printf("C++: empty frame ...\n"); return(0);}

//		if( frame->origin == IPL_ORIGIN_TL )
//			cvFlip( frame, image, 0 );
//		else
//			cvCopy( frame, image, 0 );

		cvCvtColor( frame, grey, CV_BGR2GRAY );

		if (need_to_init) {
			if (detect_face()) 	{
	  			need_to_init=0;

				cvFindCornerSubPix( grey, points[1], NUM_POINTS,
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
			paintcnt=paintperiod;
		}        
		else  {
			#ifdef DEBUG_OUTPUT
				printf("calculating optical flow\n");
			#endif
            cvCalcOpticalFlowPyrLK( prev_grey, grey, prev_pyramid, pyramid,
                points[0], points[1], NUM_POINTS, cvSize(win_size,win_size), 5, status, 0,
                cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03), flags );
            flags |= CV_LKFLOW_PYR_A_READY;
         
			if ((!status[0] ) || (!status[1]))  need_to_init=1; 
			else {
				x_click = (points[0][0].x - points[1][0].x) * GAIN;
				y_click = (points[0][0].y - points[1][0].y) * GAIN; 
				x_move = (points[0][1].x - points[1][1].x)  * GAIN; 
				y_move = (points[0][1].y - points[1][1].y)  * GAIN; 
				
                cvCircle( frame, cvPointFrom32f(points[1][0]), 4, CV_RGB(255,255,0), 2, 8,0);
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

		if ((drawing_window)&&(++paintcnt>=paintperiod))
		{
			#ifdef DEBUG_OUTPUT
				printf("show\n");
			#endif
	        cvShowImage( "Camera", frame );
			cvWaitKey(1);   // needed to pump openCV message queue
  		    //   while (PeekMessage(&msg, drawing_window, 0, 0, PM_REMOVE))
		    //      DispatchMessage(&msg);
		    paintcnt=0;
		}
	    return(1);
}


///////////////////////////////////////////////////////////////////////////////////////



JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_activate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;

	// cout << "C++: Webcam with Face Recognition Activated !" << endl;
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
		printf("C++: Starting Webcam facetracker\n");
	#endif
	error_code=facetracker_init();
    return (error_code);
}
 
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_deactivate
  (JNIEnv * env, jobject obj)
{
	jint error_code = 0;

	#ifdef DEBUG_OUTPUT
		printf( "C++: Deactivate Webcam facetracker\n");
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

	 printf("Getting property with key: %s\n", strKey); 
    if(strcmp(pollingIntervalKey, strKey) == 0)
	{
		printf("The value of key \"%s\" is: %s", strKey, pollingIntervalValue); 
		result = env->NewStringUTF(pollingIntervalValue);
	}
	else
	{
		 printf("Key \"%s\" was not found", strKey); 
		result = NULL;
	}
    return result;
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
	//printf("Setting property %s to %s\n", strKey,strValue); 
    if(strcmp(pollingIntervalKey, strKey) == 0)
	{
		result = env->NewStringUTF(pollingIntervalValue);
		pollingIntervalValue  = env->GetStringUTFChars(value, NULL);
		paintperiod=atoi(strValue);
		// printf("Set value of key \"%s\" to: %d\n", strKey, paintperiod); 
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

JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_facetrackerLK_jni_Bridge_initFace
  (JNIEnv *env, jobject obj)
{
		points[1][1].x=0;
		points[1][1].y=0;
		need_to_init=1;
}
