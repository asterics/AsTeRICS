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

#include <jni.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include <ctype.h>
#include <windows.h>
#include <fstream>

#include "posit.h"
#include "opencv_includes.h"
#include "posit_impl.h"

HANDLE InfoWindowHandle = 0;
DWORD dwPositThreadId;
CPOSIT* ptr_positObj;

int threadMode = 0;
int screenResWidth = 1680, screenResHeight = 1050;


DWORD WINAPI InfoWindowProc(LPVOID lpv)
{	
	int cntEvalCycle;
	int nextEvalPoint;
	int evalPointInterval = 30; //equals ~ 3 seconds between each eval point
	int cntEvalCol, cntEvalRow, crossPosX, crossPosY;
	int evalHDistance, evalVDistance;

	int actThreadMode = 1;
	threadMode = 1;	

	std::string string_buf;
	string_buf.reserve(15);
	
	cv::Mat dst = cv::Mat::zeros(cv::Size(RESWIDTH, RESHEIGHT), CV_8UC3);
	cv::Mat fsimg;
		
	cv::namedWindow("Info",CV_WINDOW_AUTOSIZE);
	cv::waitKey(1);

	//thread Modes: 0=off, 1=run Info Window, 2 = run Evaluation of Accuracy
	while(threadMode != 0)
	{
		//Mode 1: show info window
		if (threadMode == 1)
		{
			ptr_positObj->getDebugImage(dst);
			cv::imshow("Info", dst);
			actThreadMode = 1;
			cv::waitKey(33);
		}

		//Mode 2: Evaluate Accuracy
		//Info window will not be updated in this mode
		if (threadMode == 2) 
		{
			//if last mode was mode 1 or mode 2: open Eval window
			if(actThreadMode == 1)
			{	
				ptr_positObj->clearEvalVectors();
				fsimg = cv::Mat::zeros(cv::Size(screenResWidth, screenResHeight), CV_8UC3);
				cntEvalCycle = 0;
				nextEvalPoint = 0;
				cntEvalCol = 0;
				cntEvalRow = 0;
				crossPosX = 0;
				crossPosY = 0;

				cv::namedWindow("EvalAccuracy",CV_WINDOW_NORMAL);
				cv::waitKey(1);
				cv::setWindowProperty("EvalAccuracy",CV_WND_PROP_FULLSCREEN, CV_WINDOW_FULLSCREEN);
				cv::waitKey(1);
				cv::imshow("EvalAccuracy",fsimg);								
				
				evalHDistance = (screenResWidth-40) / 2;
				evalVDistance = (screenResHeight-40) / 2;
			}

			actThreadMode = threadMode;

			//create sequence for each evaluation point
			if (cntEvalCycle == nextEvalPoint)
			{
				if (nextEvalPoint !=0)
				{
					ptr_positObj->copyTempEyeVal();
					ptr_positObj->copyTRVal();
				}

				if (cntEvalCycle >= (9*evalPointInterval))
				{
					printf("write file!\n");
					ptr_positObj->writeEvalFile();
					ptr_positObj->setThreadMode(1);
					cntEvalCycle = 0;
				}
				else
				{
					crossPosX = 20 + cntEvalCol * evalHDistance;
					crossPosY = 20 + cntEvalRow * evalVDistance;

					if (cntEvalCol >= 2) 
					{
						cntEvalCol = 0;
						cntEvalRow++;
					}
					else cntEvalCol++;

					ptr_positObj->getEvalImage(screenResWidth, screenResHeight, crossPosX, crossPosY, fsimg);
					nextEvalPoint += evalPointInterval;
					cntEvalCycle++;
				}
				
				cv::imshow("EvalAccuracy", fsimg);
			}
			else	cntEvalCycle++;

			cv::waitKey(100);	//wait ~100ms
		}

		threadMode = ptr_positObj->getThreadMode();

		//close no longer required windows
		if (threadMode == 1 && actThreadMode == 2)
		{
			cv::destroyWindow("EvalAccuracy");
			cv::waitKey(1);
		}
	}
	cv::destroyAllWindows();
	cv::waitKey(1);
	return(1);
}

//called when the start or resume buttton of the ACS is pressed
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_activate
  (JNIEnv *env, jobject obj)
{
	ptr_positObj = new CPOSIT;
	ptr_positObj->init();

	return 1;
}

//called when the stop or pause button of the ACS is pressed
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_deactivate
  (JNIEnv *env, jobject obj)
{
	int mode = ptr_positObj->getThreadMode();
	if (mode)
	{
		ptr_positObj->setThreadMode(0);
		DWORD wait = WaitForSingleObject(InfoWindowHandle,1000);
		CloseHandle(InfoWindowHandle);
		InfoWindowHandle = 0;
	}

	delete ptr_positObj;
	return 1;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_runPOSIT
  (JNIEnv *env, jobject obj, jint x1, jint y1, jint x2, jint y2, jint x3, jint y3, jint x4, jint y4)
{
	jclass cls = env->GetObjectClass(obj);
	jmethodID mid;	
	float tmpx, tmpy, tmpz;
	
	ptr_positObj->runPOSIT((int)x1, (int)y1, (int)x2, (int)y2, (int)x3, (int)y3, (int)x4, (int)y4);
	
	if (ptr_positObj->getThreadMode()!=0)
		ptr_positObj->createDebugInfo();

	//callback into Java: pass rotation and translation vector back to Java
	ptr_positObj->getRVrad(tmpx, tmpy, tmpz);
	mid = env->GetMethodID(cls, "newRotationVector_callback", "(FFF)V");
	if (mid == 0)	//no method attached
		return -1;
	env->CallVoidMethod(obj, mid, (jfloat) tmpx, (jfloat) tmpy, (jfloat) tmpz);

	ptr_positObj->getTV(tmpx, tmpy, tmpz);
	mid = env->GetMethodID(cls, "newTranslationVector_callback", "(FFF)V");
	if (mid == 0)	//no method attached
		return -1;
	env->CallVoidMethod(obj, mid, (jfloat) tmpx, (jfloat) tmpy, (jfloat) tmpz);
	
	return 1;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_togglePoseInfoWindow
  (JNIEnv *env, jobject obj)
{
	threadMode = ptr_positObj->getThreadMode();

	if (threadMode == 0)
	{
		ptr_positObj->setThreadMode(1);	
		InfoWindowHandle = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE) InfoWindowProc, (LPVOID) NULL, 0, &dwPositThreadId);
		if (InfoWindowHandle == NULL) { printf("CreateThread for Info Window failed\n"); return(0); }
	}

	if (threadMode >= 1)
	{
		ptr_positObj->setThreadMode(0);	
		DWORD wait = WaitForSingleObject(InfoWindowHandle,1000);
		CloseHandle(InfoWindowHandle);
		InfoWindowHandle = 0;
	}

	return 1;
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_startEval
  (JNIEnv *env, jobject obj, jint screenX, jint screenY)
{
	screenResWidth = screenX;
	screenResHeight = screenY;

	threadMode = ptr_positObj->getThreadMode();

	if (threadMode == 0)
	{
		ptr_positObj->setThreadMode(2);	
		InfoWindowHandle = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE) InfoWindowProc, (LPVOID) NULL, 0, &dwPositThreadId);
		if (InfoWindowHandle == NULL) { printf("CreateThread for Info Window + Start Eval failed\n"); return(0); }
	}

	if (threadMode == 1)
	{
		ptr_positObj->setThreadMode(2);
	}

	return 1;
}

//this method receives the values send by the JVM
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_eyetracker_jni_BridgePOSIT_sendEvalParams
  (JNIEnv *env, jobject obj, jint rawX, jint rawY, jint actX, jint actY)
{
	int tempMode = ptr_positObj->getThreadMode();
	
	if (tempMode == 2)
		ptr_positObj->setTempEyeVal((int)rawX, (int)rawY, (int)actX, (int)actY);
	else
	{//callback to stop sending the eye coordinates
		jclass cls = env->GetObjectClass(obj);
		jmethodID mid;
		mid = env->GetMethodID(cls, "stopSendEyeCoordinates_callback", "()V");
		if (mid == 0)	//no method attached
			return -1;
		env->CallVoidMethod(obj, mid);

		//printf("native Code: call to stop sending eye coordinates\n");
	}
	//printf("received eval values from JVM\n");
	return 1;
}