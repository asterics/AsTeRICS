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

//Copyright hint: Many code snippets have been copied and modified from the API docuementation examples: https://tobii.github.io/stream_engine/

#include <tobii/tobii_streams.h>
#include <stdio.h>
#include <assert.h>
#include <eu_asterics_component_sensor_tobii4cheadtracker_Bridge.h>
#include <jni.h>
#include <Windows.h>

int deactivate();
int activate();
int main_loop();
void head_pose_callback( tobii_head_pose_t const*, void*);
void url_receiver( char const*, void*);
int enumerate_devices();
void presence_callback( tobii_user_presence_status_t, int64_t, void*);
void notifications_callback( tobii_notification_t const* notification, void* user_data );

//Tobii global references
tobii_api_t* api;
tobii_device_t* device;

//Other global vars
static bool do_run_main_loop=false;
static bool main_loop_finished=false;
static bool tracker_activated=false;

//JNI global references, needed for performing the callback to the Java code.
static JavaVM * g_jvm;
static jobject g_obj = NULL;

/*
  Callback that receives calls through notifications subscription.
*/
void notifications_callback( tobii_notification_t const* notification, void* user_data )
{
	JNIEnv *env;
	jclass cls;
	jmethodID mid;

	// perform JNI callback
	g_jvm->AttachCurrentThread((void **)&env, NULL);
    cls = env->GetObjectClass(g_obj);
	float not_used_val[] = {0.0f, 0.0f, 0.0f};

	switch(notification->value_type) {
	case TOBII_NOTIFICATION_VALUE_TYPE_STATE:
		mid = env->GetMethodID(cls, "notification_state_callback", "(III)V");
		env->CallVoidMethod(g_obj, mid, (jint) notification->type,(jint) notification->value_type,(jint) notification->value.state);
		break;
	case TOBII_NOTIFICATION_VALUE_TYPE_FLOAT:
		mid = env->GetMethodID(cls, "notification_float_value_callback", "(IIF)V");
		env->CallVoidMethod(g_obj, mid, (jint) notification->type,(jint) notification->value_type,(jfloat) notification->value.float_);
		break;
	case TOBII_NOTIFICATION_VALUE_TYPE_DISPLAY_AREA:
		if(notification->type==TOBII_NOTIFICATION_TYPE_DISPLAY_AREA_CHANGED) {
			mid = env->GetMethodID(cls, "notification_float_xyz_area_callback", "(II[F[F[F[F[F[F[F[F)V");

			//Allocate jfloatArray variables to be passed back to the Java class
			jfloatArray outVals[8];
			for(int i=0;i<8;i++) {
				outVals[i]=env->NewFloatArray(3);  // allocate			
			}
			env->SetFloatArrayRegion(outVals[0], 0 , 3, notification->value.display_area.top_left_mm_xyz);  // copy
			env->SetFloatArrayRegion(outVals[1], 0 , 3, notification->value.display_area.top_right_mm_xyz);  // copy
			env->SetFloatArrayRegion(outVals[2], 0 , 3, notification->value.display_area.bottom_left_mm_xyz);  // copy
			env->SetFloatArrayRegion(outVals[3], 0 , 3, not_used_val);  // copy
			env->SetFloatArrayRegion(outVals[4], 0 , 3, not_used_val);  // copy
			env->SetFloatArrayRegion(outVals[5], 0 , 3, not_used_val);  // copy
			env->SetFloatArrayRegion(outVals[6], 0 , 3, not_used_val);  // copy
			env->SetFloatArrayRegion(outVals[7], 0 , 3, not_used_val);  // copy

			env->CallVoidMethod(g_obj, mid, (jint) notification->type,(jint) notification->value_type,
				(jfloatArray) outVals[0],(jfloatArray) outVals[1],(jfloatArray) outVals[2],(jfloatArray) outVals[3],(jfloatArray) outVals[4],(jfloatArray) outVals[5],(jfloatArray) outVals[6],(jfloatArray) outVals[7]);

			//Dealloc arrays again
			for(int i=0;i<8;i++) {
				jboolean isCopy=true;
				env->ReleaseFloatArrayElements(outVals[i],env->GetFloatArrayElements(outVals[i],&isCopy),0);			
			}
		}
		break;
	}
	/*
    if( notification->type == TOBII_NOTIFICATION_TYPE_CALIBRATION_STATE_CHANGED )
    {
        if( notification->value.state == TOBII_STATE_BOOL_TRUE )
            printf( "Calibration started\n" );
        else
            printf( "Calibration stopped\n" );
    }

    if( notification->type == TOBII_NOTIFICATION_TYPE_FRAMERATE_CHANGED )
        printf( "Framerate changed\nNew framerate: %f\n", notification->value.float_ );

	if(notification->type == TOBII_NOTIFICATION_TYPE_DEVICE_PAUSED_STATE_CHANGED) {
        if( notification->value.state == TOBII_STATE_BOOL_TRUE )
            printf( "Tracker started\n" );
        else
            printf( "Tracker paused\n" );
	}

    if(notification->type == TOBII_NOTIFICATION_TYPE_DISPLAY_AREA_CHANGED) {
		printf("mew display area: %f, %f, %f",notification->value.display_area.top_left_mm_xyz[0],notification->value.display_area.top_right_mm_xyz[0],notification->value.display_area.bottom_left_mm_xyz[0]);
	}*/
}

/*
  Callback that receives calls through user presence subscription.
*/
void presence_callback( tobii_user_presence_status_t status, int64_t timestamp_us, void* user_data )
{
	JNIEnv *env;
	jclass cls;
	jmethodID mid;

	// perform JNI callback
	g_jvm->AttachCurrentThread((void **)&env, NULL);
    cls = env->GetObjectClass(g_obj);
	mid = env->GetMethodID(cls, "user_presence_callback", "(I)V");
	env->CallVoidMethod(g_obj, mid, (jint) status);
	/*
    switch( status )
    {
        case TOBII_USER_PRESENCE_STATUS_UNKNOWN:
            printf( "User presence status is unknown.\n" );
            break;
        case TOBII_USER_PRESENCE_STATUS_AWAY:
            printf( "User is away.\n" );
            break;
        case TOBII_USER_PRESENCE_STATUS_PRESENT:
            printf( "User is present.\n" );
            break;
    }*/
}

/*
  Callback that receives calls through head pose subscription.
*/
void head_pose_callback( tobii_head_pose_t const* head_pose, void* user_data )
{
	int i=0;
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	
    if(head_pose->position_validity != TOBII_VALIDITY_VALID) {
        //printf( "Position: (%f, %f, %f)\n", head_pose->position_xyz[ 0 ], head_pose->position_xyz[ 1 ], head_pose->position_xyz[ 2 ] );
		printf("p:-(");
		return;
	}

    //printf( "Rotation:\n" );
	for(i = 0; i < 3; ++i ) {
        if( head_pose->rotation_validity_xyz[ i ] != TOBII_VALIDITY_VALID ) {
            //printf( "%f\n", head_pose->rotation_xyz[ i ] );
			printf("r:-(");
			return;
		}
	}
	
	// perform JNI callback
	g_jvm->AttachCurrentThread((void **)&env, NULL);
    cls = env->GetObjectClass(g_obj);
	mid = env->GetMethodID(cls, "head_pose_callback", "(FFFFFF)V");
	env->CallVoidMethod(g_obj, mid, (jfloat) (head_pose->position_xyz[ 0 ]), (jfloat) (head_pose->position_xyz[ 1 ]), (jfloat) (head_pose->position_xyz[ 2 ]), 
		(jfloat) (head_pose->rotation_xyz[ 0 ]), (jfloat) (head_pose->rotation_xyz[ 1 ]), (jfloat) (head_pose->rotation_xyz[ 2 ]));
}

/*
  Method to activate the tracker (create an instance to the API and the device).
  In case of an error, the deactivate function is called to cleanup and then the error code is returned back to Java.

  return: error code
*/
int activate() {
	//If, for any reason the previous deactivate was not done properly.
	if(api != NULL || device!=NULL) {
		deactivate();
	}

	do_run_main_loop=false;
	main_loop_finished=false;
	tobii_error_t error=TOBII_ERROR_NO_ERROR;

	//Retrieve API version just for informative reasons.
	tobii_version_t version;
    error = tobii_get_api_version( &version );
    if( error == TOBII_ERROR_NO_ERROR )
        printf( "Tobii-4C: Current API version: %d.%d.%d\n", version.major, version.minor, 
            version.revision );

	//Create an instance to the API
    error = tobii_api_create( &api, NULL, NULL );
	if( error != TOBII_ERROR_NO_ERROR ) {
		//cleanup, just to be sure
		deactivate();
		return error;
	}

	//Do device enumeration of 4C devices, if none is found abort with an error.
	//We do this to exclude older Tobii devices e.g. EyeX
	if(enumerate_devices()==0) {
		printf("Tobii-4C: enumerate_devices error: no devices\n");
		return TOBII_ERROR_NOT_AVAILABLE;
	}

	//although we did an enumeration above, simply use the default device --> don't provide deviceURL but use NULL instead.
	error = tobii_device_create( api, NULL, &device );
	printf("Tobii-4C: device != null? : %s\n",device!=NULL? "found device" : "no device"); 
    if( error != TOBII_ERROR_NO_ERROR ) {
		printf("Tobii-4C: tobii_device_create error: %s\n",tobii_error_message(error));
		deactivate();
		return error;
	}

	//Subscribe for the head pose data
    error = tobii_head_pose_subscribe( device, head_pose_callback, 0 );
	if( error != TOBII_ERROR_NO_ERROR ) return error;

	//Subscribe for user presence
	error = tobii_user_presence_subscribe( device, presence_callback, 0 );
	if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: user_presence subscription error: %s\n",tobii_error_message(error));

	error = tobii_notifications_subscribe( device, notifications_callback, 0 );
	if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: notifications subscription error: %s\n",tobii_error_message(error));

	//remember activated state
	tracker_activated=true;

	return error;
}

/*
  This is the main loop that waits for callbacks and does the processing of the callbacks.
  This method is seperated from activate and deactivate so that it can be called from another thread on the Java side.
*/
int main_loop() {
	tobii_error_t error=TOBII_ERROR_NO_ERROR;
	do_run_main_loop=true;
    while( do_run_main_loop==true)
    {
        error = tobii_wait_for_callbacks( device );
		if( (error != TOBII_ERROR_NO_ERROR && error != TOBII_ERROR_TIMED_OUT)) return error;

        error = tobii_process_callbacks( device );
        if( error != TOBII_ERROR_NO_ERROR ) return error;
    }
	main_loop_finished=true;
	printf("Tobii-4C: main_loop stopped\n");
	return 0;
}

/*
  This method deactivates a tracker device.
  Stops the main loop, which is executed in another Java thread and then unsubscribes from everything and cleans up device and api handles.

  return: error code
*/
int deactivate() {
	tobii_error_t error=TOBII_ERROR_NO_ERROR;

	do_run_main_loop=false;
	for(int tries=20;!main_loop_finished && tries > 0; tries --) {
		printf("Tobii-4C: Waiting for main_loop to finish\n");
		Sleep(100);
	}
	printf("Tobii-4C: Deactivating device: %s\n",device != NULL ? "!=null" : "null");
    error = tobii_head_pose_unsubscribe( device );
	if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: head_pose unsubscription error: %s\n",tobii_error_message(error));

	error = tobii_user_presence_unsubscribe( device );
    if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: user_presence unsubscription error: %s\n",tobii_error_message(error));

	error = tobii_notifications_unsubscribe( device );
    if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: notifications unsubscription error: %s\n",tobii_error_message(error));

    error = tobii_device_destroy( device );
    if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: device destroy error: %s\n",tobii_error_message(error));

    error = tobii_api_destroy( api );
    if( error != TOBII_ERROR_NO_ERROR ) printf("Tobii-4C: api destroy error: %s\n",tobii_error_message(error));

	//remember deactivated state
    do_run_main_loop=false;
    main_loop_finished=false;
    tracker_activated=false;
	api=NULL;
	device=NULL;

	printf("Tobii-4C: Device/API deactivated successfully\n");
    return 0;
}

/*
  This method is a callback function for enumerate_devices() and collects the devices found.
*/
void url_receiver( char const* url, void* user_data )
{
    int* count = (int*) user_data;
    ++(*count);
    printf( "Tobii-4C: %d. %s\n", *count, url );
}

/*
	Enumerates devices of type Tobii 4C (TOBII_DEVICE_GENERATION_IS4)
	return: Count of Tobii 4C devices
*/
int enumerate_devices() {
	tobii_error_t error=TOBII_ERROR_NO_ERROR;
	int count = 0;
    error = tobii_enumerate_local_device_urls_ex( api, url_receiver, &count,
        TOBII_DEVICE_GENERATION_IS4 );
    if( error == TOBII_ERROR_NO_ERROR )
        printf( "Tobii-4C: Found %d devices.\n", count );
    else {
        printf( "Tobii-4C: Enumeration failed.\n" );		
	}

	return count;
}

//common part
/*
  Only needed to test the program as .exe
*/
int main()
{
	activate();
	main_loop();
	deactivate();
}

/* Start JNI bridge impelementation */

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_tobii4cheadtracker_Bridge_activate(JNIEnv *env, jobject obj, jstring deviceURL) {
	//JNI: Iinit global pointer to JavaVM
	jint error_code = env->GetJavaVM(&g_jvm);
	// explicitly ask for a global reference
	g_obj = env->NewGlobalRef(obj);

	//The deviceURL is currently ignored because it would need much more code to support multiple devices within the lib.
	return activate();
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_tobii4cheadtracker_Bridge_main_1loop
  (JNIEnv *env, jobject obj) {
	  return main_loop();
}

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_tobii4cheadtracker_Bridge_deactivate
  (JNIEnv *env, jobject obj) {
	  return deactivate();
}