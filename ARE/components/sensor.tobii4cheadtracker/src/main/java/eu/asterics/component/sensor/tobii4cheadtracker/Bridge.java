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

package eu.asterics.component.sensor.tobii4cheadtracker;
import java.io.Console;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.mw.services.AstericsThreadPool;

public class Bridge {
	//Definition of enum types, copied from tobii.h and tobii_streams.h
	enum TOBI_ERROR{
	    TOBII_ERROR_NO_ERROR,
	    TOBII_ERROR_INTERNAL,
	    TOBII_ERROR_INSUFFICIENT_LICENSE,
	    TOBII_ERROR_NOT_SUPPORTED,
	    TOBII_ERROR_NOT_AVAILABLE,
	    TOBII_ERROR_CONNECTION_FAILED,
	    TOBII_ERROR_TIMED_OUT, 
	    TOBII_ERROR_ALLOCATION_FAILED,
	    TOBII_ERROR_INVALID_PARAMETER,
	    TOBII_ERROR_CALIBRATION_ALREADY_STARTED,
	    TOBII_ERROR_CALIBRATION_NOT_STARTED,
	    TOBII_ERROR_ALREADY_SUBSCRIBED,
	    TOBII_ERROR_NOT_SUBSCRIBED,
	    TOBII_ERROR_BUFFER_TOO_SMALL,
	    TOBII_ERROR_OPERATION_FAILED,
	    TOBII_ERROR_FIRMWARE_NO_RESPONSE
	};
	
	enum TOBII_USER_PRESENCE_STATUS
	{
	    TOBII_USER_PRESENCE_STATUS_UNKNOWN,
	    TOBII_USER_PRESENCE_STATUS_AWAY,
	    TOBII_USER_PRESENCE_STATUS_PRESENT,
	};
	
	enum TOBII_NOTIFICATION_TYPE
	{
	    TOBII_NOTIFICATION_TYPE_CALIBRATION_STATE_CHANGED,
	    TOBII_NOTIFICATION_TYPE_EXCLUSIVE_MODE_STATE_CHANGED,
	    TOBII_NOTIFICATION_TYPE_TRACK_BOX_CHANGED,
	    TOBII_NOTIFICATION_TYPE_DISPLAY_AREA_CHANGED,
	    TOBII_NOTIFICATION_TYPE_FRAMERATE_CHANGED,
	    TOBII_NOTIFICATION_TYPE_POWER_SAVE_STATE_CHANGED,
	    TOBII_NOTIFICATION_TYPE_DEVICE_PAUSED_STATE_CHANGED,
	};
	
	enum TOBII_NOTIFICATION_VALUE_TYPE
	{
	    TOBII_NOTIFICATION_VALUE_TYPE_NONE,
	    TOBII_NOTIFICATION_VALUE_TYPE_FLOAT,
	    TOBII_NOTIFICATION_VALUE_TYPE_STATE,
	    TOBII_NOTIFICATION_VALUE_TYPE_DISPLAY_AREA,
	};
	
	enum TOBII_STATE
	{
	    TOBII_STATE_POWER_SAVE_ACTIVE,
	    TOBII_STATE_REMOTE_WAKE_ACTIVE,
	    TOBII_STATE_DEVICE_PAUSED,
	    TOBII_STATE_EXCLUSIVE_MODE
	};

	enum TOBII_STATE_BOOL
	{
	    TOBII_STATE_BOOL_FALSE,
	    TOBII_STATE_BOOL_TRUE,
	}

    private static boolean nativeLibsLoaded=false;
    Logger logger=AstericsErrorHandling.instance.getLogger();
    Tobii4CHeadTrackerInstance owner;
	
	native int activate(String deviceURL);
	native int main_loop();
	native int deactivate();

	public static void main(String[] args) throws Exception {
		Bridge bridge=new Bridge(null);
		
		bridge.startTracking();
		Scanner scan = new Scanner(System.in);
		while(!scan.next().equals("e")) {
			System.out.println("x");
		}
			
		System.out.println("Deactivating...");
		int errCode=bridge.deactivate();
		if(errCode!=0) {
			System.out.println("Error during deactivation --> Exiting with error code: "+errCode);
			return;		
		}	
	}
	
	public Bridge(Tobii4CHeadTrackerInstance owner) {
		super();
		this.owner=owner;
	}
	
	void loadNativeLibs() {
	    if(!nativeLibsLoaded) {
	        System.loadLibrary("tobii_stream_engine");
	        System.loadLibrary("tobii4C_stream_engine_JNI_bridge");
	        nativeLibsLoaded=true;
	    } else {
	        logger.info("Native libs already loaded.");
	    }
	}
	
	public void startTracking() throws Exception {
	    logger.fine("Activating...");
        int errCode=activate("tobii-4C-"+System.currentTimeMillis());
        if(errCode!=0) {
            throw new Exception("Tobii-4C: An error occured during tracker activation: "+TOBI_ERROR.values()[errCode]);            
        }
	    
        logger.fine("After Activating...");
	    /*
	    Future activationFuture=AstericsThreadPool.getInstance().execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
	    //wait for activation to finish and propagate exceptions to the caller.
	    try {
	        activationFuture.get(2000,TimeUnit.MILLISECONDS);
	    }catch(TimeoutException te) {
	        //If a time out occured, try to deactivate everything and then propagate error message back.
	        deactivate();
	        throw te;
	    }
	    */
        AstericsThreadPool.getInstance().execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                logger.fine("Bridge: Starting main loop...");
                int errCode=main_loop();
                logger.fine("Bridge: main_loop finished");
                if(errCode!=0) {
                    throw new Exception("Tobii-4C: An error occured during tracker main loop: "+TOBI_ERROR.values()[errCode]);
                }
                return null;
            }
        });	    
	}
	
	public void stopTracking() throws Exception {
	    logger.fine("Tobii-4C: Deactivating");
	    int errCode=deactivate();
	    logger.fine("Tobii-4C: After Deactivating");
	    if(errCode!=0) {
	        throw new Exception("Tobii-4C: An error occured during tracker main loop: "+TOBI_ERROR.values()[errCode]);
	    }
	}
	
	void head_pose_callback(final float pos_x, final float pos_y, final float pos_z, final float rot_x, final float rot_y, final float rot_z) {
		//System.out.printf("pos: [%f,%f,%f], rot: [%f, %f, %f]\n", pos_x, pos_y, pos_z, rot_x, rot_y, rot_z);
	    
	    if(owner!=null) {
	        // Hand over callback data to model executor thread to ensure that the
	        // corresponding coordinate
	        // data is processed together without mixing up the coordinates.
	        AstericsModelExecutionThreadPool.instance.execute(new Runnable() {
	            @Override
	            public void run() {
	                //use ConversionUtils.doubleToBytes because the output ports are of that type.
	                //Java will implicitly cast from float to double for us!
	                owner.opHeadPosX.sendData(ConversionUtils.doubleToBytes(pos_x));
	                owner.opHeadPosY.sendData(ConversionUtils.doubleToBytes(pos_y));
	                owner.opHeadPosZ.sendData(ConversionUtils.doubleToBytes(pos_z));
	                owner.opHeadRotX.sendData(ConversionUtils.doubleToBytes(rot_x));
	                owner.opHeadRotY.sendData(ConversionUtils.doubleToBytes(rot_y));
	                owner.opHeadRotZ.sendData(ConversionUtils.doubleToBytes(rot_z));
	            }
	        });	    
	    }
	}
	
	void user_presence_callback(int user_presence_state) {
		logger.fine("User presence state changed to: "+TOBII_USER_PRESENCE_STATUS.values()[user_presence_state]);
	    if(user_presence_state==TOBII_USER_PRESENCE_STATUS.TOBII_USER_PRESENCE_STATUS_AWAY.ordinal()) {
	        owner.etpUserPresenceStatusAway.raiseEvent();
	    } else if(user_presence_state==TOBII_USER_PRESENCE_STATUS.TOBII_USER_PRESENCE_STATUS_PRESENT.ordinal()) {
	        owner.etpUserPresenceStatusPresent.raiseEvent();
	    }		
	}
	
	void notification_state_callback(int notification_type, int value_type, int state) {
		logger.fine("Notifications received: state cb: "+TOBII_NOTIFICATION_TYPE.values()[notification_type]);
		if(notification_type==TOBII_NOTIFICATION_TYPE.TOBII_NOTIFICATION_TYPE_CALIBRATION_STATE_CHANGED.ordinal()) {
		    if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_TRUE.ordinal()) {
		        owner.etpCalibrationStarted.raiseEvent();
		    } else if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_FALSE.ordinal()) {
		        owner.etpCalibrationFinished.raiseEvent();
		    }
		}
        if(notification_type==TOBII_NOTIFICATION_TYPE.TOBII_NOTIFICATION_TYPE_DEVICE_PAUSED_STATE_CHANGED.ordinal()) {
            if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_TRUE.ordinal()) {
                owner.etpDeviceOff.raiseEvent();
            } else if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_FALSE.ordinal()) {
                owner.etpDeviceOn.raiseEvent();
            }
        }
        if(notification_type==TOBII_NOTIFICATION_TYPE.TOBII_NOTIFICATION_TYPE_POWER_SAVE_STATE_CHANGED.ordinal()) {
            if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_TRUE.ordinal()) {
                owner.etpPowerSaveStateTrue.raiseEvent();
            } else if(state==TOBII_STATE_BOOL.TOBII_STATE_BOOL_FALSE.ordinal()) {
                owner.etpPowerSaveStateFalse.raiseEvent();
            }
        }
	}
	
	void notification_float_value_callback(int notification_type, int value_type, float vlaue) {
		logger.fine("Notifications received: float: "+TOBII_NOTIFICATION_TYPE.values()[notification_type]);
	}
	
	void notification_float_xyz_area_callback(int notification_type, int value_type, float[] p_xyz_1, float[] p_xyz_2, float[] p_xyz_3, float[] p_xyz_4, float[] p_xyz_5, float[] p_xyz_6, float[] p_xyz_7, float[] p_xyz_8) {
		logger.fine("Notifications received: float_xyz_area: "+TOBII_NOTIFICATION_TYPE.values()[notification_type]+", top_l: "+Arrays.toString(p_xyz_1)+", top_r: "+Arrays.toString(p_xyz_2)+", bottom_l: "+Arrays.toString(p_xyz_3));
        if(notification_type==TOBII_NOTIFICATION_TYPE.TOBII_NOTIFICATION_TYPE_DISPLAY_AREA_CHANGED.ordinal()) {
            owner.etpDisplayAreaChanged.raiseEvent();
        }
	}
}
