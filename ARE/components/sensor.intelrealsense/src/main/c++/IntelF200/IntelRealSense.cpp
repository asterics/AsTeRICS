#include "eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense.h"
#include <jni.h>
#include <stdio.h>
#include <string>
#include <iostream>


#include "pxcfacemodule.h"
#include "pxcfacedata.h"
#include "pxcfaceconfiguration.h"
#include "pxcsensemanager.h"
#include "util_render.h"
#include "pxchandmodule.h"


using std::string;

PXCSenseManager *senseManager;
PXCFaceData *faceData;
BOOL isExpressionsOn;
BOOL showDisplay;
BOOL isAquireFrameTrue;
BOOL isDeactivateCalled;

/*
* Class:     eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense
* Method:    activate
* Signature: (III)I
*/
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_init
(JNIEnv *env, jobject obj, jint devNr, jint enableExpressions, jint displayGUI){
	printf("\n\nActivate was called (C++)\n");
	
	if (enableExpressions == 0) {
		isExpressionsOn = FALSE;
	}
	else {
		isExpressionsOn = TRUE;
	}
	if (displayGUI == 0) {
		showDisplay = FALSE;
	}
	else {
		showDisplay = TRUE;
	}
	
	isDeactivateCalled = false;

	PXCSession *session = PXCSession::CreateInstance();
	senseManager = session->CreateSenseManager();
	if (senseManager == NULL) {
		printf("Fail to create sense Manager\n\n");
		return -1;
	}
	else {
		printf("senseManager created\n");
	}
	
	senseManager->EnableFace();
	PXCFaceModule *faceModul = senseManager->QueryFace();
	if (faceModul == NULL) {
		printf("Fail to create faceModul\n\n");
		return -1;
	}
	else {
		printf("faceModul created\n");
	}


	PXCFaceConfiguration *faceConfig = faceModul->CreateActiveConfiguration();
	if (faceConfig == NULL) {
		printf("Fail to create Face Config\n\n");
		return -1;
	}
	else {
		printf("faceConfig created\n");
	}

	faceConfig->SetTrackingMode(PXCFaceConfiguration::TrackingModeType::FACE_MODE_COLOR_PLUS_DEPTH);
	faceConfig->detection.isEnabled = true;

	if (isExpressionsOn) {
		PXCFaceConfiguration::ExpressionsConfiguration *expressionConfig = faceConfig->QueryExpressions();
		if (expressionConfig == NULL) {
			printf("Fail to create ExpressionsConfiguration\n\n");
			return -1;
		}
		else {
			printf("expressionConfig created\n");
		}

		expressionConfig->Enable();
		expressionConfig->EnableAllExpressions();
		if (expressionConfig->IsEnabled()) {
			printf("ExpressionConfig is Enable\n");
		}
		else {
			printf("Canot enable expressionConfig\n");
		}
	}

	faceConfig->ApplyChanges();
	faceConfig->Update();

	faceData = faceModul->CreateOutput();
	if (faceData == NULL) {
		printf("Fail to create faceData\n\n");
		return -1;
	}
	else {
		printf("faceData created\n");
	}
	
	PXCSession::ImplDesc desc1 = {};
	desc1.group = PXCSession::IMPL_GROUP_SENSOR;
	desc1.subgroup = PXCSession::IMPL_SUBGROUP_VIDEO_CAPTURE;
	for (int m = 0;; m++) {
		if (m > 20) {
			printf("No Intel Real Sense Camera found!");
			return -1;
		}

		PXCSession::ImplDesc desc2;
		if (session->QueryImpl(&desc1, m, &desc2) < PXC_STATUS_NO_ERROR) {
			printf("session->QueryImpl has an ERROR\n");
			continue;
		}
		else {
			printf("session->QueryImpl has no Error\n");
		}
		PXCCapture *capture = 0;
		pxcStatus sts = session->CreateImpl<PXCCapture>(&desc2, &capture);
		if (sts < PXC_STATUS_NO_ERROR) {
			printf("session->CreateImpl has an ERROR\n");
			continue;
		}
		else {
			printf("session->CreateImpl has no ERROR\n");
		}
		// print out all device information
		for (int d = 0;; d++) {
			printf("Find device round; %d.%d\n", m, d);
			PXCCapture::DeviceInfo dinfo;
			sts = capture->QueryDeviceInfo(d, &dinfo);
			if (sts != PXC_STATUS_NO_ERROR) {
				printf("Intel Real Sense not found\n");
				break;
			}
			if (dinfo.model != devNr) {
				std::cout << dinfo.name << "is not the right model. DevNr.: " << dinfo.model;
				printf("%c is not the right model. DevNr.: %d\n", dinfo.name, dinfo.model);

				if (d > 10){
					break; 
				}
				else{
					continue;
				}
			}
			else {
				PXCCaptureManager *captureManager = senseManager->QueryCaptureManager();
				senseManager->QueryCaptureManager()->FilterByDeviceInfo(dinfo.name, dinfo.did, dinfo.didx);
				printf("Use: %s\n",dinfo.name);
				capture->Release();
				return 0;
			}
			
		}
		capture->Release();
		
	}
	printf("Init successful finished!\n");
}
	


/*
* Class:     eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense
* Method:    deactivate
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_deactivate
(JNIEnv *env, jobject obj) {
	isDeactivateCalled = true;
	while (isAquireFrameTrue) {
		//wait while Frame is true
		}
	printf("Intel Real Sense deactivated\n");
	faceData->Release();
	senseManager->ReleaseFrame();
	senseManager->AcquireFrame(false);
	senseManager->Close();
	isDeactivateCalled = false;
}

/*
* Class:     eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense
* Method:    startTracking
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_startTracking
(JNIEnv *env, jobject obj) {

	printf("Hello from StartTracking!! (C++)\n");

	UtilRender renderc(L"Color");
	senseManager->EnableStream(PXCCapture::STREAM_TYPE_COLOR, 640, 480, 30);
	senseManager->Init();

	if (showDisplay) {
		PXCCapture::Device *device = senseManager->QueryCaptureManager()->QueryDevice();
		if (device != NULL) {
			device->ResetProperties(PXCCapture::STREAM_TYPE_ANY);
			device->SetMirrorMode(PXCCapture::Device::MirrorMode::MIRROR_MODE_HORIZONTAL);
		}
	}

	

	if (senseManager->AcquireFrame(true) >= PXC_STATUS_NO_ERROR) {
		isAquireFrameTrue = true;
		printf("Acquire'Frame has no Error\n");
	}
	else {
		printf("Acquire Frame has an Error\n");
	}
	if (!isExpressionsOn) {
		printf("Expression is not active\n");
	}
	while (senseManager->AcquireFrame(true) >= PXC_STATUS_NO_ERROR && !isDeactivateCalled) {

		pxcI32 nfaces;
		senseManager->AcquireFrame(true);
		faceData->Update();

		if (showDisplay) {
			//show Frame
			const PXCCapture::Sample *sample = senseManager->QuerySample();
			if (sample) {
				if (sample->color && !renderc.RenderFrame(sample->color)) {
					printf("sample->color OR renderc.RenderFrame(sample->color) has an FAILURE\n");
					Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_deactivate(env, obj);
				}
			}
		}
		

		nfaces = faceData->QueryNumberOfDetectedFaces();

		for (pxcI32 i = 0; i < nfaces; i++) {
			// Retrieve the data instance

			PXCFaceData::DetectionData *detectionData = faceData->QueryFaceByIndex(i)->QueryDetection();
			if (detectionData == NULL) {
				printf("detectionData is NULL\n");
				break;
			}
			//get possition
			PXCRectI32 rect;
			detectionData->QueryBoundingRect(&rect);

			//get Pose Data
			PXCFaceData::PoseData *poseData = faceData->QueryFaceByIndex(i)->QueryPose();
			if (poseData == NULL) {
				printf("poseData is NULL\n");
				break;
			}
			PXCFaceData::PoseEulerAngles poseEulerAngles;
			poseData->QueryPoseAngles(&poseEulerAngles);
			

			jclass cls = env->GetObjectClass(obj);
			jmethodID mid = env->GetMethodID(cls, "poseDataCallback", "(IIIIIII)V");
			if (mid == NULL) printf("callback method not found\n"); //return;    // method not found
										// explicitly ask for a global reference
			jobject g_obj = env->NewGlobalRef(obj);
			env->CallVoidMethod(g_obj, mid, (jint)(rect.h), (jint)(rect.w), (jint)(rect.x), (jint)(rect.y), 
						(jint)(poseEulerAngles.roll), (jint)(poseEulerAngles.yaw), (jint)(poseEulerAngles.pitch));
			
			

			PXCFaceData::Face *face = faceData->QueryFaceByIndex(i);
			if (isExpressionsOn) {
				PXCFaceData::ExpressionsData *edata = face->QueryExpressions();
				if (edata == NULL) {
					printf("edata is NULL\n");
					break;
				}

				// get the expression information
				PXCFaceData::ExpressionsData::FaceExpressionResult score_brow_raiser_left;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_brow_raiser_right;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_brow_lowerer_left;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_brow_lowerer_right;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_smile;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_kiss;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_mouth_open;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_tongue_out;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_closed_left;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_closed_right;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_turn_left;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_turn_right;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_up;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_eyes_down;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_puff_left;
				PXCFaceData::ExpressionsData::FaceExpressionResult score_puff_right;
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_BROW_RAISER_LEFT, &score_brow_raiser_left);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_BROW_RAISER_RIGHT, &score_brow_raiser_right);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_BROW_LOWERER_LEFT, &score_brow_lowerer_left);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_BROW_LOWERER_RIGHT, &score_brow_lowerer_right);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_SMILE, &score_smile);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_KISS, &score_kiss);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_MOUTH_OPEN, &score_mouth_open);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_TONGUE_OUT, &score_tongue_out);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_CLOSED_LEFT, &score_eyes_closed_left);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_CLOSED_RIGHT, &score_eyes_closed_right);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_TURN_LEFT, &score_eyes_turn_left);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_TURN_RIGHT, &score_eyes_turn_right);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_UP, &score_eyes_up);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_EYES_DOWN, &score_eyes_down);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_PUFF_LEFT, &score_puff_left);
				edata->QueryExpression(PXCFaceData::ExpressionsData::EXPRESSION_PUFF_RIGHT, &score_puff_right);
			
				//store Data into an intarray and send it to java
				jint expressions[16] = {
					(int) score_brow_raiser_left.intensity,
					(int) score_brow_raiser_right.intensity,
					(int) score_brow_lowerer_left.intensity,
					(int) score_brow_lowerer_right.intensity,
					(int) score_smile.intensity,
					(int) score_kiss.intensity,
					(int) score_mouth_open.intensity,
					(int) score_tongue_out.intensity,
					(int) score_eyes_closed_left.intensity,
					(int) score_eyes_closed_right.intensity,
					(int) score_eyes_turn_left.intensity,
					(int) score_eyes_turn_right.intensity,
					(int) score_eyes_up.intensity,
					(int) score_eyes_down.intensity,
					(int) score_puff_left.intensity,
					(int) score_puff_right.intensity,
				};
				
				jintArray returnValue = env->NewIntArray(16);
				env->SetIntArrayRegion(returnValue, 0, 16, expressions);
				jclass cls = env->GetObjectClass(obj);
				jmethodID mid = env->GetMethodID(cls, "expressionCallback", "([I)V");
				if (mid == NULL) printf("callback method not found\n"); //return; // method not found 
										 // explicitly ask for a global reference
				jobject g_obj = env->NewGlobalRef(obj);
				env->CallVoidMethod(g_obj, mid, (returnValue));
				
			}
		}
		senseManager->ReleaseFrame();
		isAquireFrameTrue = false;
	}

}

/*
* Class:     eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense
* Method:    pause
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_pause
(JNIEnv *env, jobject obj) {
	printf("pause called (c++)");
	isDeactivateCalled = true;
}

/*
* Class:     eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense
* Method:    resume
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_resume
(JNIEnv *env, jobject obj) {
	printf("resume calles (c++)");
	isDeactivateCalled = false;
	Java_eu_asterics_component_sensor_intelrealsense_BridgeIntelRealSense_startTracking(env, obj);
}
