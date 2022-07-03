/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013-2016 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCCursorConfiguration.h
    Defines the PXCCursorConfiguration interface, which defines the configuration options of the hand cursor module
 */
#pragma once
#include "pxccursordata.h"

/**
    @class PXCCursorConfiguration
    @brief Handles all the configuration options of the hand cursor module.
    Use this interface to configure the tracking, alerts, gestures and output options.
    @note Updated configuration is applied only when ApplyChanges is called.
*/
class PXCCursorConfiguration: public PXCBase
{
public:

    /* Constants */
    PXC_CUID_OVERWRITE(PXC_UID('C','H','C','G'));
	
	
	/* Event Handlers */
    
    /**    
        @class AlertHandler
        @brief Abstract interface for classes that handle alert events.
        Inherit this class and implement the alert-handling callback function OnFiredAlert.
        @see SubscribeAlert
    */
    class CursorAlertHandler {
    public:
        /**
         @brief The OnFiredAlert method is called when a registered alert event is fired.
         @param[in] alertData - contains all the information for the fired alert.
         @see PXCCursorData::AlertData
        */
        virtual void PXCAPI OnFiredAlert(const PXCCursorData::AlertData & alertData) = 0;
    };

    /** 
        @class GestureHandler
        @brief Abstract interface for classes that handle gesture events.
        Inherit this class and implement the gesture-handling callback function OnFiredGesture.
        @see SubscribeGesture
    */
    class CursorGestureHandler {
    public:

        /**
             @brief The OnFiredGesture method is called when a registered gesture event is fired.
             @param[in] gestureData - contains all the information of the fired gesture event.
             @see PXCCursorData::GestureData
        */
        virtual  void PXCAPI OnFiredGesture(const PXCCursorData::GestureData & gestureData) = 0;
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
        @brief Set the boundaries of the tracking area.
        
        The tracking boundaries create a frustum shape in which the hand is tracked.\n
        (A frustum is a truncated pyramid, with 4 side planes and two rectangular bases.)\n
        When the tracked hand reaches one of the boundaries (near, far, left, right, top, or bottom), the appropriate alert is fired.     
		 @param[in] trackingBounds - the struct that defines the tracking boundaries.
		
		@note The frustum base center are directly opposite the sensor.\n
		
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid argument.
		
		@see PXCCursorData::TrackingBounds
    */
    virtual pxcStatus PXCAPI SetTrackingBounds(PXCCursorData::TrackingBounds trackingBounds) = 0;

    /**
        @brief Get the values defining the tracking boundaries frustum.
        @return PXCCursorData::TrackingBounds  
    */
    virtual PXCCursorData::TrackingBounds PXCAPI QueryTrackingBounds() = 0;
	
	/** 
        @brief Enable/Disable Cursor engagement indication.
        The cursor engagement retrieves an indication that the hand is ready to interact with the user application.
		@param[in] enable - a boolean to turn off/on the feature. 
        @return PXC_STATUS_NO_ERROR - operation succeeded.    
		
		@see PXCCursorData::QueryEngagementPercent
    */
	virtual pxcStatus PXCAPI EnableEngagement(pxcBool enable) = 0;

	/** 
        @brief Set the duration time in milliseconds for engagement of the Cursor.
		The duration is the time needed for the hand to be in front of the camera and static.
        @param[in] timeInMilliseconds - time duration in milliseconds.
        @return PXC_STATUS_NO_ERROR - operation succeeded.    
		@return PXC_STATUS_VALUE_OUT_OF_RANGE - time duration is under 32 milliseconds.
		
		@note: default engagement time is 800 milliseconds
    */
	virtual pxcStatus PXCAPI SetEngagementTime(pxcI32 timeInMilliseconds) = 0;

	/** 
        @brief Get the duration time in milliseconds for engagement of the Cursor.
		The duration is the time needed for the hand to be in front of the camera and static.
        @param[out] timeInMilliseconds - time duration in milliseconds.
    */
	virtual pxcI32 PXCAPI QueryEngagementTime() = 0;
	
	/* Alerts Configuration */
        
    /** 
        @brief Enable alert messaging for a specific event.            
        @param[in] alertEvent - the ID of the event to be enabled.
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid alert type.
        
        @see PXCCursorData::AlertType
    */
    virtual pxcStatus PXCAPI EnableAlert(PXCCursorData::AlertType alertEvent) = 0;
    
    /** 
        @brief Enable all alert messaging events.            
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
    */
    virtual pxcStatus PXCAPI EnableAllAlerts(void) = 0;
    
    /** 
        @brief Test the activation status of the given alert.
        @param[in] alertEvent - the ID of the event to be tested.
        @return true if the alert is enabled, false otherwise.
        
        @see PXCCursorData::AlertType
    */
    virtual pxcBool PXCAPI IsAlertEnabled(PXCCursorData::AlertType alertEvent) const = 0;
    
    /** 
        @brief Disable alert messaging for a specific event.            
        @param[in] alertEvent - the ID of the event to be disabled
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - unsupported parameter.
        @return PXC_STATUS_DATA_NOT_INITIALIZED - data was not initialized.
        
        @see PXCCursorData::AlertType
    */
    virtual pxcStatus PXCAPI DisableAlert(PXCCursorData::AlertType alertEvent) = 0;

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

        @see CursorAlertHandler::OnFiredAlert        
    */
    virtual pxcStatus PXCAPI SubscribeAlert(CursorAlertHandler *alertHandler) = 0;

    /** 
        @brief Unsubscribe an alert handler object.
        @param[in] alertHandler - a pointer to the event handler to unsubscribe.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - illegal alertHandler (null pointer).
    */
    virtual pxcStatus PXCAPI UnsubscribeAlert(CursorAlertHandler *alertHandler) = 0;
    
    /* Gestures Configuration */
    
    /** 
        @brief Enable a gesture, so that events are fired when the gesture is identified.
        @param[in] gestureEvent - the gesture type to be enabled. 
        
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - invalid parameter.
		
		 @see PXCCursorData::GestureType
    */
     virtual pxcStatus PXCAPI EnableGesture(PXCCursorData::GestureType gestureEvent)=0;
     
    /** 
        @brief Enable all gestures, so that events are fired for every gesture identified.        
        @return PXC_STATUS_NO_ERROR - operation succeeded.   
    */
     virtual pxcStatus PXCAPI EnableAllGestures()=0;     

    /** 
        @brief Check whether a gesture is enabled.
        @param[in] gestureEvent - the gesture type. 
        @return true if the gesture is enabled, false otherwise.
		
		@see PXCCursorData::GestureType
    */
    virtual pxcBool PXCAPI IsGestureEnabled(PXCCursorData::GestureType gestureEvent) const = 0;

    /** 
        @brief Deactivate identification of a gesture. Events will no longer be fired for this gesture.            
        @param[in] gestureEvent - the gesture type to be disabled.         
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        PXC_STATUS_PARAM_UNSUPPORTED - invalid gesture name.
    */
    virtual pxcStatus PXCAPI DisableGesture(PXCCursorData::GestureType gestureEvent)=0;

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

        @see CursorGestureHandler::OnFiredGesture        
		
    */
    virtual pxcStatus PXCAPI SubscribeGesture(CursorGestureHandler* gestureHandler) = 0;

    /** 
        @brief Unsubscribe a gesture event handler object.
        After this call no callback events will be sent to the given gestureHandler.
        @param[in] gestureHandler - a pointer to the event handler to unsubscribe.
        @return PXC_STATUS_NO_ERROR - operation succeeded. 
        @return PXC_STATUS_PARAM_UNSUPPORTED - null gesture handler.     
    */
    virtual pxcStatus PXCAPI UnsubscribeGesture(CursorGestureHandler *gestureHandler) = 0;



};
 