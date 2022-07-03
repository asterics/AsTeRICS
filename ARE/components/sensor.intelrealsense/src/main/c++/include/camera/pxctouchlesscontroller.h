/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include "pxccapture.h"
#include "pxchandmodule.h"
#include "pxchanddata.h"
/**
 @class	PXCTouchlessController

 @brief	This module interpert user actions to UX commands intended to control a windows 8 computer, 
  such as scroll, zoom, select atile etc. The module fire events for each such action as well as inject 
  touch, mouse and keyboard event to the operating system to perform the action. 
  Developer may listen to such events to enable application specific reactions, or just enable this module 
  and depend on the normal OS reactions to the injected events.
  There is also a WPF dll that provides default visual feedback which may be easily linked to those events, 
  see touchless_controller_visual_feedback sample.
  Few configuration options are available for developers to influence the way the module opareate, 
  like enabling or disabling specific behaviors.
 */

class PXCTouchlessController: public PXCBase {
public:

    PXC_CUID_OVERWRITE(PXC_UID('F','L','K','S'));

    struct ProfileInfo
    {
		typedef pxcEnum Configuration; // an or value of UX options relevant to specific application
        enum {
			Configuration_None = 0x00000000, // No option is selected - use default behavior 
			Configuration_Allow_Zoom = 0x00000001, // Should zoom be allowed
			Configuration_Use_Draw_Mode = 0x00000002, // Use draw mode - should be used for applications the need continues interaction (touch + movement) like drawing
			Configuration_Scroll_Horizontally = 0x00000004, // Enable horizontal scrolling using pinch gesture
			Configuration_Scroll_Vertically = 0x00000008, // Enable vertical scrolling  using pinch gesture
			Configuration_Meta_Context_Menu = 0x00000010, // Should Meta menu events be fired, triggered by v gesture
			Configuration_Enable_Injection = 0x00000020, // Disable the injection of keyboar/mouse/touch events
			Configuration_Edge_Scroll_Horizontally = 0x00000040, // Enable horizontal scrolling when pointer is on the edge of the screen
			Configuration_Edge_Scroll_Vertically = 0x00000080, // Enable vertical scrolling  when pointer is on the edge of the screen
			Configuration_Allow_Back = 0x00000200, //  Enable Back Gesture
			Configuration_Allow_Selection = 0x00000400, //  Enable Selection Gesture (Default value)
			Configuration_Disable_On_Mouse_Move = 0x00000800 // if enabled TouchlessController will stop tracking the hand while the mouse moves
		};

		
        PXCHandModule*		handModule;   //the HandAnalysis module used by this module, dont set it when using SenseManager - this is just an output parameter
        Configuration       config;   // An or value of configuration options - Default value is Configuration_Allow_Selection

		ProfileInfo() : handModule(0),config(Configuration_Allow_Selection)
		{
		}
    };
   
    /** 
        @brief Return the configuration parameters of the SDK's TouchlessController
        @param[out] pinfo the profile info structure of the configuration parameters.
        @return PXC_STATUS_NO_ERROR if the parameters were returned successfully; otherwise, return one of the following errors:
        PXC_STATUS_ITEM_UNAVAILABLE - Item not found/not available.\n
        PXC_STATUS_DATA_NOT_INITIALIZED - Data failed to initialize.\n                        
    */
    virtual pxcStatus  PXCAPI QueryProfile(ProfileInfo *pinfo)=0;

    /** 
        @brief Set configuration parameters of the SDK TouchlessController. 
        @param[in] pinfo the profile info structure of the configuration parameters.
        @return PXC_STATUS_NO_ERROR if the parameters were set correctly; otherwise, return one of the following errors:
        PXC_STATUS_INIT_FAILED - Module failure during initialization.\n
        PXC_STATUS_DATA_NOT_INITIALIZED - Data failed to initialize.\n                        
    */
    virtual pxcStatus  PXCAPI SetProfile(ProfileInfo *pinfo)=0;

	/*
		@breif Describe a UXEvent,
	*/
	struct UXEventData
	{
		/**
		 @enum	UXEventType
		
		 @brief	Values that represent UXEventType.
		 */
		enum UXEventType 
		{
			UXEvent_StartZoom,			// the user start performing a zoom operation - pan my also be performed during zoom
			UXEvent_Zoom,				// Fired while zoom operation is ongoing
			UXEvent_EndZoom,			// User stoped zooming
			UXEvent_StartScroll,		// the user start performing a scroll or pan operation
			UXEvent_Scroll,				// Fired while scroll operation is ongoing
			UXEvent_EndScroll,			// User stoped scrolling (panning)
			UXEvent_StartDraw,			// User started drawing - deprecated
			UXEvent_Draw,				// Fired while draw operation is ongoing  - deprecated
			UXEvent_EndDraw,			// User finshed drawing - deprecated
			UXEvent_CursorMove,			// Cursor moved while not in any other mode
			UXEvent_Select,				// user selected a button
			UXEvent_GotoStart,			// Got to windows start screen
			UXEvent_CursorVisible,		// Cursor turned visible
			UXEvent_CursorNotVisible,	// Cursor turned invisible
			UXEvent_ReadyForAction,		// The user is ready to perform a zoom or scroll operation
			UXEvent_StartMetaCounter,   // Start Meta Menu counter visual
			UXEvent_StopMetaCounter,    // Abort Meta Menu Counter Visual
			UXEvent_ShowMetaMenu,       // Show Meta Menu
			UXEvent_HideMetaMenu,       // Hide Meta Menu
			UXEvent_MetaPinch,			// When a pinch was detected while in meta mode
			UXEvent_MetaOpenHand,       // When a pinch ends while in meta mode
			UXEvent_Back,				// User perform back gesture
			UXEvent_ScrollUp,			// Edge Scroll up was started by moving cursor to upper screen edge
			UXEvent_ScrollDown,			// Edge Scroll down was started by moving cursor to down screen edge
			UXEvent_ScrollLeft,			// Edge Scroll left was started by moving cursor to left screen edge
			UXEvent_ScrollRight,		// Edge Scroll right was started by moving cursor to right screen edge
		};
		UXEventType type; // type of the event
		PXCPoint3DF32 position; // position where event happen values are in rang [0,1]
		PXCHandData::BodySideType bodySide; // the hand that issued the event

	};
	
	/**	
		@class UXEventHandler
		Interface for a callback for all categories of events 
	*/
	class UXEventHandler{
	public:
		/**
		@brief virtual destructor
		*/
		virtual ~UXEventHandler(){}
		/**
		 @brief The OnFiredUXEvent method is called when a UXWvent is fired.
		 @param[in] uxEventData contains all the information for the fired event.
		 @see UXEventData
		*/
		virtual  void PXCAPI OnFiredUXEvent(const UXEventData *uxEventData)=0;
	};


	/** 
    @brief Register an event handler object for UX Event. The event handler's OnFiredUXEvent method will be called each time a UX event is identified.
    @param[in] uxEventHandler a pointer to the event handler.
    @see UXEventHandler::OnFiredUXEvent
    @return PXC_STATUS_NO_ERROR if the registering an event handler was successful; otherwise, return the following error:
    PXC_STATUS_DATA_NOT_INITIALIZED - Data failed to initialize.\n        
    */
    virtual pxcStatus PXCAPI SubscribeEvent(UXEventHandler *uxEventHandler) = 0;

    /** 
        @brief Unsubscribe an event handler object for UX events.
        @param[in] uxEventHandler a pointer to the event handler that should be removed.
        @return PXC_STATUS_NO_ERROR if the unregistering the event handler was successful, an error otherwise.
    */
    virtual pxcStatus PXCAPI UnsubscribeEvent(UXEventHandler *uxEventHandler) = 0;

	/**
		 @struct	AlertData
	
		 @brief	An alert data, contain data describing an alert.
	*/
	struct AlertData
	{
		/**
		 @enum	AlertType
		
		 @brief	Values that represent AlertType.
		 */
		enum AlertType { 
			Alert_TooClose,         // The user hand is too close to the 3D camera
			Alert_TooFar,           // The user hand is too far from the 3D camera
			Alert_NoAlerts          // A previous alerted situation was ended
		};
		AlertType type; // the  type of the alert
	};

	/**	
		@class AlertHandler
		Interface for a callback for all categories of alerts 
	*/
	class AlertHandler{
	public:
		/**
		@brief virtual destructor
		*/
		virtual ~AlertHandler(){}
		/**
		 @brief The OnFiredAlert method is called when a registered alert event is fired.
		 @param[in] alertData contains all the information for the fired alert.
		 @see AlertData
		*/
		virtual  void PXCAPI OnFiredAlert(const AlertData *alertData)=0;
	};

	/** 
        @brief Register an event handler object for alerts. The event handler's OnFiredAlert method will be called each time an alert is identified.
        @param[in] alertHandler a pointer to the event handler.
        @see AlertHandler::OnFiredAlert
        @return PXC_STATUS_NO_ERROR if the registering an event handler was successful; otherwise, return the following error:
        PXC_STATUS_DATA_NOT_INITIALIZED - Data failed to initialize.\n        
    */
    virtual pxcStatus PXCAPI SubscribeAlert(AlertHandler *alertHandler) = 0;

    /** 
        @brief Unsubscribe an event handler object for alerts.
        @param[in] alertHandler a pointer to the event handler that should be removed.
        @return PXC_STATUS_NO_ERROR if the unregistering the event handler was successful, an error otherwise.
    */
    virtual pxcStatus PXCAPI UnsubscribeAlert(AlertHandler *alertHandler) = 0;

	/**
	 @enum	Action
	
	 @brief	Values that represent Action. Those are actions the module will inject to the OS
	 */
	enum Action 
		{
			Action_None=0,		// No action will be injected
			Action_LeftKeyPress,	// Inject left arrow key - can be used to go to the previous item (Page/Slide/Photo etc.)
			Action_RightKeyPress,	// Inject right arrow key - can be used to Go to the next item (Page/Slide/Photo etc.)
			Action_BackKeyPress,	// Inject backspace key - can be used to Go to the previous item (Page/Slide/Photo etc.)
			Action_PgUpKeyPress,	// Inject page up key - can be used to Go to the previous item (Page/Slide/Photo etc.)
			Action_PgDnKeyPress,	// Inject page down key - can be used to Go to the next item (Page/Slide/Photo etc.)
			Action_VolumeUp,		// Inject "volume up" medai key
			Action_VolumeDown,		// Inject "volume down" media key
			Action_Mute,			// Inject "mute" media key
			Action_NextTrack,		// Inject "next" media key
			Action_PrevTrack,		// Inject "previous" media key
			Action_PlayPause,		// Inject "play/pause" media key
			Action_Stop,			// Inject "stop" media key
			Action_ToggleTabs,		// Toggle tabs menu in Metro Internet Explorer
		};

	/**	
		@class AlertHandler
		Interface for a callback for all categories of actions 
	*/
	class ActionHandler {
	public:
		/**
		@brief virtual destructor
		*/
		virtual ~ActionHandler(){}
		/**
		 @brief The OnFiredAction method is called when a registered action mapping is triggered
		 @param[in] action the action that was fired
		 @see Action
		*/
		virtual  void PXCAPI OnFiredAction(const Action action)=0;
	};

	/**
	 @brief	Adds a gesture action mapping.
	 @param [in]	gestureName  	If non-null, name of the gesture.
	 @param	action					 	The action.
	 @param [in]	actionHandler	(Optional) If non-null, an action handler that will be called when the gesture will be recognized.	
	 @return PXC_STATUS_NO_ERROR if the mapping was successful, an error otherwise.
	 */
	virtual pxcStatus PXCAPI AddGestureActionMapping(pxcCHAR* gestureName,Action action,ActionHandler* actionHandler) = 0;
	__inline pxcStatus AddGestureActionMapping(pxcCHAR* gestureName,Action action) {
		return AddGestureActionMapping(gestureName, action, 0);
	}
	
	/**
		@brief Clear all previous Gesture to Action mappings
		@return PXC_STATUS_NO_ERROR if the mapping was successful, an error otherwise.
	*/
	virtual pxcStatus PXCAPI ClearAllGestureActionMappings(void) = 0;

    /**
		@brief Sets the scroll speed sensitivity
		@return PXC_STATUS_NO_ERROR if the parameter were set correctly;
	*/
    virtual pxcStatus PXCAPI SetScrollSensitivity(float sensitivity) = 0;

	/**
	 @enum	PointerSensitivity
	
	 @brief	Values that represent Sensitivity level for the pointer movement
	 */
	enum PointerSensitivity
	{		
		PointerSensitivity_Smoothed, // most smoothed pointer setting
		PointerSensitivity_Balanced, // medium setting 
		PointerSensitivity_Sensitive // most sensetive pointer setting			
	};

	 /**
		@brief Sets the pointer sensitivity
		@return PXC_STATUS_NO_ERROR if the parameter was set correctly;
	*/
    virtual pxcStatus PXCAPI SetPointerSensitivity(PointerSensitivity sensitivity) = 0;
};
