/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013-2016 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCHandConfiguration.h
    Defines the PXCHandConfiguration interface, which defines the configuration options of the hand module
 */
#pragma once
#include "pxchanddata.h"

/**
    @class PXCHandConfiguration
    @brief Handles all the configuration options of the hand module.
    Use this interface to configure the tracking, alerts, gestures and output options.
    @note Updated configuration is applied only when ApplyChanges is called.
*/
class PXCHandConfiguration: public PXCBase
{
public:

    /* Constants */
    PXC_CUID_OVERWRITE(PXC_UID('H','A','C','G'));

    /* Event Handlers */
    
    /**    
        @class AlertHandler
        @brief Abstract interface for classes that handle alert events.
        Inherit this class and implement the alert-handling callback function OnFiredAlert.
        @see SubscribeAlert
    */
    class AlertHandler {
    public:
        /**
         @brief The OnFiredAlert method is called when a registered alert event is fired.
         @param[in] alertData - contains all the information for the fired alert.
         @see PXCHandData::AlertData
        */
        virtual void PXCAPI OnFiredAlert(const PXCHandData::AlertData & alertData) = 0;
    };

    /** 
        @class GestureHandler
        @brief Abstract interface for classes that handle gesture events.
        Inherit this class and implement the gesture-handling callback function OnFiredGesture.
        @see SubscribeGesture
    */
    class GestureHandler {
    public:

        /**
             @brief The OnFiredGesture method is called when a registered gesture event is fired.
             @param[in] gestureData - contains all the information of the fired gesture event.
             @see PXCHandData::GestureData
        */
        virtual  void PXCAPI OnFiredGesture(const PXCHandData::GestureData & gestureData) = 0;
    };

public:

    /* General */

    /**
        @brief Apply the configuration changes to the module.
        This method must be called in order to apply the current configuration changes.
		@note the actual change will have an affect from the next tracked frame.
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - configuration was not initialized.\n                        
    */
    virtual pxcStatus PXCAPI ApplyChanges() = 0;

    /**  
        @brief Restore configuration settings to the default values.
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - configuration was not initialized.\n                        
    */
    virtual pxcStatus PXCAPI RestoreDefaults() = 0;

    /**
        @brief Read current configuration settings from the module into this object.
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - configuration was not read.\n                        
    */
    virtual pxcStatus PXCAPI Update() = 0;

    /* Tracking Configuration */
    
    /** 
        @brief Restart the tracking process and reset all the skeleton information. 
        You might want to call this method, for example, when transitioning from one game level to another, \n
        in order to discard information that is not relevant to the new stage.
        
        @note ResetTracking will be executed only when processing the next frame.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_PROCESS_FAILED - there was a module failure during processing.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - the module was not initialized
     */
    virtual pxcStatus PXCAPI ResetTracking() = 0;

    /**
        @brief Specify the name of the current user for personalization.
        The user name will be used to save and retrieve specific measurements (calibration) for this user.
        @param[in] userName - the name of the current user.
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_PARAM_UNSUPPORTED - illegal user name(e.g. an empty string)  or tracking mode is set to TRACKING_MODE_EXTREMITIES.
    */
    virtual pxcStatus PXCAPI SetUserName(const pxcCHAR *userName) = 0;


    /**
        @brief Get the name of the current user.
        @return A null-terminated string containing the user's name.    
    */
    virtual const pxcCHAR*  PXCAPI QueryUserName() = 0;


    /**
        @brief Activate calculation of the speed of a specific joint, according to the given mode.\n
        
        The output speed is a 3-dimensional vector, containing the the motion of the requested joint in each direction (x, y and z axis).\n
        By default, the joint speed calculation is disabled for all joints, in order to conserve CPU and memory resources.\n
        Typically the feature is only activated for a single fingertip or palm-center joint, as only the overall hand speed is useful.\n
        
        @param[in] jointLabel - the identifier of the joint.
        @param[in] jointSpeed - the speed calculation method. Possible values are:\n
          JOINT_SPEED_AVERAGE  - calculate the average joint speed, over the time period defined in the "time" parameter.\n
          JOINT_SPEED_ABSOLUTE - calculate the average of the absolute-value joint speed, over the time period defined in the "time" parameter.\n
        @param[in] time - the period in milliseconds over which the average speed will be calculated (a value of 0 will return the current speed).
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - one of the arguments is invalid  or tracking mode is set to TRACKING_MODE_EXTREMITIES. 
        
        @see PXCHandData::JointType
        @see PXCHandData::JointSpeedType
    */
    virtual pxcStatus PXCAPI EnableJointSpeed(PXCHandData::JointType jointLabel, PXCHandData::JointSpeedType jointSpeed, pxcI32 time) = 0;
    
    /**
        @brief Disable calculation of the speed of a specific joint.\n
        You may want to disable the feature when it is no longer needed, in order to conserve CPU and memory resources.\n
        @param[in] jointLabel - the identifier of the joint
        
        @return PXC_STATUS_NO_ERROR - operation succeeded.
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid joint label  or tracking mode is set to TRACKING_MODE_EXTREMITIES.
        
        @see PXCHandData::JointType
    */
    virtual pxcStatus PXCAPI DisableJointSpeed(PXCHandData::JointType jointLabel) = 0;        

    /**
        @brief Set the boundaries of the tracking area.
        
        The tracking boundaries create a frustum shape in which the hand is tracked.\n
        (A frustum is a truncated pyramid, with 4 side planes and two rectangular bases.)\n
        When the tracked hand reaches one of the boundaries (near, far, left, right, top, or bottom), the appropriate alert is fired.
        
        @param[in] nearTrackingDistance - nearest tracking distance (distance of small frustum base from sensor).
        @param[in] farTrackingDistance - farthest tracking distance (distance of large frustum base from sensor).
        @param[in] nearTrackingWidth - width of small frustum base.
        @param[in] nearTrackingHeight - height of small frustum base.
        
        @note The frustum base centers are directly opposite the sensor.\n

        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid argument.
        
        @see PXCHandData::JointType
    */
    virtual pxcStatus PXCAPI SetTrackingBounds(pxcF32 nearTrackingDistance, pxcF32 farTrackingDistance, pxcF32 nearTrackingWidth, pxcF32 nearTrackingHeight) = 0;

    /**
        @brief Get the values defining the tracking boundaries frustum.
        
        @param[out] nearTrackingDistance - nearest tracking distance (distance of small frustum base from sensor).
        @param[out] farTrackingDistance - farthest tracking distance (distance of large frustum base from sensor).
        @param[out] nearTrackingWidth - width of small frustum base.
        @param[out] nearTrackingHeight - height of small frustum base.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded.  
    */
    virtual pxcStatus PXCAPI QueryTrackingBounds(pxcF32& nearTrackingDistance, pxcF32& farTrackingDistance, pxcF32& nearTrackingWidth, pxcF32& nearTrackingHeight) = 0;

    /**
        @brief Set the tracking mode, which determines the algorithm that will be applied for tracking hands.
		
        @param[in] trackingMode - the tracking mode to be set. Possible values are:\n
			TRACKING_MODE_FULL_HAND   - track the entire hand skeleton.\n
			TRACKING_MODE_EXTREMITIES - track only the mask and the extremities of the hand (the points that confine the tracked hand).\n			

        @return PXC_STATUS_NO_ERROR - operation succeeded. 
		@return PXC_STATUS_PARAM_UNSUPPORTED - TrackingModeType is invalid.
        
        @see PXCHandData::TrackingModeType
    */
    virtual pxcStatus PXCAPI SetTrackingMode(PXCHandData::TrackingModeType trackingMode) = 0;

    /**
        @brief Retrieve the current tracking mode, which indicates the algorithm that should be applied for tracking hands.
        @return TrackingModeType
        
        @see SetTrackingMode
        @see PXCHandData::TrackingModeType
    */
    virtual PXCHandData::TrackingModeType PXCAPI QueryTrackingMode() = 0;

    /**
        @brief Enable or disable the hand stabilizer feature.\n
        
        Enabling this feature produces smoother tracking of the hand motion, ignoring small shifts and "jitters".\n
        (As a result, in some cases the tracking may be less sensitive to minor movements).
        
        @param[in] enableFlag - true to enable the hand stabilizer; false to disable it.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
		@return PXC_STATUS_PARAM_UNSUPPORTED - tracking mode is set to TRACKING_MODE_EXTREMITIES.
    */
    virtual pxcStatus PXCAPI EnableStabilizer(pxcBool enableFlag) = 0;

    /**
    @brief Return hand stabilizer activation status.
    @return true if hand stabilizer is enabled, false otherwise.
    */
    virtual pxcBool  PXCAPI IsStabilizerEnabled() = 0;

    /**
        @brief Sets the degree of hand motion smoothing.
        "Smoothing" is algorithm which overcomes local problems in tracking and produces smoother, more continuous tracking information.
        
        @param[in] smoothingValue - a float value between 0 (not smoothed) and 1 (maximal smoothing).
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid smoothing value or tracking mode is set to TRACKING_MODE_EXTREMITIES.
    */
    virtual pxcStatus PXCAPI SetSmoothingValue(pxcF32 smoothingValue) = 0;

    /**
        @brief Retrieve the current smoothing value.
        @return The current smoothing value.
        
        @see SetSmoothingValue
    */
    virtual pxcF32  PXCAPI QuerySmoothingValue() = 0;

    /**
        @brief Enable the calculation of a normalized skeleton.\n
        Calculating the normalized skeleton transforms the tracked hand positions to those of a fixed-size skeleton.\n
        The positions of the normalized skeleton's joints can be retrieved by calling IHand::QueryNormalizedJoint.\n
        It is recommended to work with a normalized skeleton so that you can use the same code to identify poses and gestures,\n
        regardless of the hand size. (E.g. the same code can work for a child's hand and for an adult's hand.)

        @param[in] enableFlag - true if the normalized skeleton should be calculated, otherwise false.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - tracking mode is set to TRACKING_MODE_EXTREMITIES.
        @see PXCHandData::IHand::QueryNormalizedJoint
    */
    virtual pxcStatus PXCAPI EnableNormalizedJoints(pxcBool enableFlag) = 0;

    /**
     @brief Retrieve normalized joints calculation status.
     @return true if normalized joints calculation is enabled, false otherwise.
    */
    virtual pxcBool  PXCAPI IsNormalizedJointsEnabled() = 0;

    /**
     @brief Enable calculation of the hand segmentation image.
     The hand segmentation image is an image mask of the tracked hand, where the hand pixels are white and all other pixels are black.
     @param[in] enableFlag - true if the segmentation image should be calculated, false otherwise.
     @return PXC_STATUS_NO_ERROR - operation succeeded. 	 
    */
    virtual pxcStatus PXCAPI EnableSegmentationImage(pxcBool enableFlag) = 0;

    /**
     @brief Retrieve the hand segmentation image calculation status.
     @return true if calculation of the hand segmentation image is enabled, false otherwise.
     @see EnableSegmentationImage
    */
    virtual pxcBool  PXCAPI IsSegmentationImageEnabled() = 0;

    /**
     @brief Enable the retrieval of tracked joints information.
     Enable joint tracking if your application uses specific joint positions; otherwise disable in order to conserve CPU/memory resources.\n
     @note This option doesn't affect the quality of the tracking, but only the availability of the joints info.
     @param[in] enableFlag - true to enable joint tracking, false to disable it.
     @return PXC_STATUS_NO_ERROR - operation was successful
	 @return PXC_STATUS_PARAM_UNSUPPORTED - tracking mode is set to TRACKING_MODE_EXTREMITIES.
    */
    virtual pxcStatus PXCAPI EnableTrackedJoints(pxcBool enableFlag) = 0;

    /**
     @brief Retrieve the joint tracking status.
     @return true if joint tracking is enabled, false otherwise.
    */
    virtual pxcBool  PXCAPI IsTrackedJointsEnabled() = 0;

    /* Alerts Configuration */
        
    /** 
        @brief Enable alert messaging for a specific event.            
        @param[in] alertEvent - the ID of the event to be enabled.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid alert type.
        
        @see PXCHandData::AlertType
    */
    virtual pxcStatus PXCAPI EnableAlert(PXCHandData::AlertType alertEvent) = 0;
    
    /** 
        @brief Enable all alert messaging events.            
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
    */
    virtual pxcStatus PXCAPI EnableAllAlerts(void) = 0;
    
    /** 
        @brief Test the activation status of the given alert.
        @param[in] alertEvent - the ID of the event to be tested.
        @return true if the alert is enabled, false otherwise.
        
        @see PXCHandData::AlertType
    */
    virtual pxcBool PXCAPI IsAlertEnabled(PXCHandData::AlertType alertEvent) const = 0;
    
    /** 
        @brief Disable alert messaging for a specific event.            
        @param[in] alertEvent - the ID of the event to be disabled
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - unsupported parameter.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - data was not initialized.
        
        @see PXCHandData::AlertType
    */
    virtual pxcStatus PXCAPI DisableAlert(PXCHandData::AlertType alertEvent) = 0;

    /** 
        @brief Disable messaging for all alerts.                        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_DATA_NOT_INITIALIZED - data was not initialized.
    */
    virtual pxcStatus PXCAPI DisableAllAlerts(void) = 0;
    
    /** 
        @brief Register an event handler object for the alerts. 
        The event handler's OnFiredAlert method is called each time an alert fires.
        @param[in] alertHandler - a pointer to the event handler.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - null alertHandler pointer.

        @see AlertHandler::OnFiredAlert        
    */
    virtual pxcStatus PXCAPI SubscribeAlert(AlertHandler *alertHandler) = 0;

    /** 
        @brief Unsubscribe an alert handler object.
        @param[in] alertHandler - a pointer to the event handler to unsubscribe.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - illegal alertHandler (null pointer).
    */
    virtual pxcStatus PXCAPI UnsubscribeAlert(AlertHandler *alertHandler) = 0;
    
    /* Gestures Configuration */
    
    /** 
        @brief Load a set of gestures from a specified path. 
        A gesture pack is a collection of pre-trained gestures.\n
        After this call, the gestures that are contained in the pack are available for identification.\n
        @param[in] gesturePackPath - the full path of the gesture pack location.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - empty path or empty list of gestures.
        @note This method should be used only for external gesture packs, and not for the default gesture pack, which is loaded automatically.
    */
    virtual pxcStatus PXCAPI LoadGesturePack(const pxcCHAR* gesturePackPath)=0;

     /** 
        @brief Unload the set of gestures contained in the specified path.
        @param[in] gesturePackPath - the full path of the the gesture pack location.
        @return PXC_STATUS_NO_ERROR - operation succeeded.   
    */
    virtual pxcStatus PXCAPI UnloadGesturePack(const pxcCHAR* gesturePackPath) =0;

     /** 
        @brief Unload all the currently loaded sets of the gestures.\n
        If you are using multiple gesture packs, you may want to load only the packs that are relevant to a particular stage in your application\n
        and unload all others. This can boost the accuracy of gesture recognition, and conserves system resources.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
    */
    virtual pxcStatus PXCAPI UnloadAllGesturesPacks(void)=0;
    
    /**
        @brief Retrieve the total number of available gestures that were loaded from all gesture packs.
        @return The total number of loaded gestures.
    */
    virtual pxcI32 PXCAPI QueryGesturesTotalNumber(void) const = 0;

    /** 
        @brief Retrieve the gesture name that matches the given index.
        
        @param[in] index - the index of the gesture whose name you want to retrieve.
        @param[in] bufferSize - the size of the preallocated gestureName buffer.                        
        @param[out] gestureName - preallocated buffer to be filled with the gesture name.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded.  
        @return PXC_STATUS_ITEM_UNAVAILABLE - no gesture for the given index value.
    */
    virtual pxcStatus PXCAPI QueryGestureNameByIndex(pxcI32 index, pxcI32 bufferSize, pxcCHAR *gestureName) const = 0;
    
    /** 
        @brief Enable a gesture, so that events are fired when the gesture is identified.
        @param[in] gestureName - the name of the gesture to be enabled. 
        @param[in] continuousGesture - set to "true" to get an "in progress" event at every frame for which the gesture is active, or "false" to get only "start" and "end" states of the gesture.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid parameter.
    */
     virtual pxcStatus PXCAPI EnableGesture(const pxcCHAR* gestureName, pxcBool continuousGesture)=0;
     __inline pxcStatus EnableGesture(const pxcCHAR* gestureName) { return EnableGesture(gestureName, false); }

    /** 
        @brief Enable all gestures, so that events are fired for every gesture identified.        
        @param[in] continuousGesture - set to "true" to get an "in progress" event at every frame for which the gesture is active, or "false" to get only "start" and "end" states of the gesture.
        @return PXC_STATUS_NO_ERROR - operation succeeded.   
    */
      virtual pxcStatus PXCAPI EnableAllGestures(pxcBool continuousGesture)=0;
     __inline pxcStatus EnableAllGestures(void) { return EnableAllGestures(false); }

    /** 
        @brief Check whether a gesture is enabled.
        @param[in] gestureName - the name of the gesture to be tested.
        @return true if the gesture is enabled, false otherwise.
    */
    virtual pxcBool PXCAPI IsGestureEnabled(const pxcCHAR* gestureName) const = 0;

    /** 
        @brief Deactivate identification of a gesture. Events will no longer be fired for this gesture.            
        @param[in] gestureName - the name of the gesture to deactivate.            
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        PXC_STATUS_PARAM_UNSUPPORTED - invalid gesture name.
    */
    virtual pxcStatus PXCAPI DisableGesture(const pxcCHAR* gestureName)=0;

    /** 
        @brief Deactivate identification of all gestures. Events will no longer be fired for any gesture.            
        @return PXC_STATUS_NO_ERROR - operation succeeded.  
    */
    virtual pxcStatus PXCAPI DisableAllGestures(void)=0;
           
    /** 
        @brief Register an event handler object to be called on gesture events.
        The event handler's OnFiredGesture method will be called each time a gesture is identified.
        
        @param[in] gestureHandler - a pointer to the gesture handler.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - null gesture handler.

        @see GestureHandler::OnFiredGesture        
    */
    virtual pxcStatus PXCAPI SubscribeGesture(GestureHandler* gestureHandler) = 0;

    /** 
        @brief Unsubscribe a gesture event handler object.
        After this call no callback events will be sent to the given gestureHandler.
        @param[in] gestureHandler - a pointer to the event handler to unsubscribe.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - null gesture handler.     
    */
    virtual pxcStatus PXCAPI UnsubscribeGesture(GestureHandler *gestureHandler) = 0;

    /**
    @brief Loading a calibration file for specific age.
    People of different ages have different hand sizes and usually there is a correlation between the two.
    Knowing in advance the age of the players can improve the tracking in most cases.
    Call this method to load a calibration file that matches specific age. 
    We support specific hand calibrations for ages 4-14. Ages above 14 will use the default calibration.
    
    @Note: The best practice is to let the players perform online calibration, or load specific calibration per user.
    @Note: If you call SetUserName with an existing user name it will override SetDefaultAge
    @see SetUserName
    @param[in] age the expected age of the players
    @return PXC_STATUS_NO_ERROR - operation succeeded. 
    @return PXC_STATUS_PARAM_UNSUPPORTED for illegal ages or tracking mode is set to TRACKING_MODE_EXTREMITIES. 
    */
    virtual pxcStatus PXCAPI SetDefaultAge(const pxcI32 age) = 0;
    
    /**
        @brief Retrieve the current calibration default age value.
        @return The current default age value.

        @see SetDefaultAge
    */
    virtual const pxcI32 PXCAPI QueryDefaultAge() = 0;

	/**
     @brief Retrieve the activation status of the input tracking mode.
     @return true if the input tracking mode is enabled.

	  @see SetTrackingMode
    */
    virtual pxcBool PXCAPI IsTrackingModeEnabled(PXCHandData::TrackingModeType trackingMode) = 0;

};
 