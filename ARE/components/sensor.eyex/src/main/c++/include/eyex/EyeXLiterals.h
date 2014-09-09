/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXLiterals.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_LITERALS__H__)
#define __TOBII_TX_LITERALS__H__

/*********************************************************************************************************************
 * Literals
 *********************************************************************************************************************/



    /**
    *   Message Literals
    */
    static const char* TX_LITERAL_HEADER = "Header";
    static const char* TX_LITERAL_BODY = "Body";
    static const char* TX_LITERAL_ID = "Id";
    static const char* TX_LITERAL_PROCESSID = "ProcessId";
    
    /**
    *    Client Literals
    */
    static const char* TX_LITERAL_AGENTID = "AgentId";
    static const char* TX_LITERAL_TARGETPROCESSID = "TargetProcessId";
    
    /**
    *   Miscellaneous Literals
    */
	static const char* TX_LITERAL_TYPE = "Type";
    static const char* TX_LITERAL_TIMESTAMP = "Timestamp";
    static const char* TX_LITERAL_DATA = "Data";
    static const char* TX_LITERAL_X = "X";
    static const char* TX_LITERAL_Y = "Y";
    static const char* TX_LITERAL_Z = "Z";
    
    /**
    *   Bounds Literals
    */
    static const char* TX_LITERAL_BOUNDS = "Bounds";
    static const char* TX_LITERAL_BOUNDSTYPE = "BoundsType";
    static const char* TX_LITERAL_NONE = "None";    
    static const char* TX_LITERAL_RECTANGULAR = "Rectangular";
    static const char* TX_LITERAL_TOP = "Top";
    static const char* TX_LITERAL_LEFT = "Left";
    static const char* TX_LITERAL_RIGHT = "Right";
    static const char* TX_LITERAL_BOTTOM = "Bottom";
    static const char* TX_LITERAL_WIDTH = "Width";
    static const char* TX_LITERAL_HEIGHT = "Height";
    
    /**
    *   Interactor Literals
    */
    static const char* TX_LITERAL_ROOTID = "RootId";
    static const char* TX_LITERAL_GLOBALINTERACTORWINDOWID = "GlobalInteractorWindowId";
    static const char* TX_LITERAL_MASK = "Mask";
    static const char* TX_LITERAL_MASKID = "MaskId";
    static const char* TX_LITERAL_MASKBOUNDS = "MaskBounds";
        
    /**
    *   Mask Literals
    */
    static const char* TX_LITERAL_MASKTYPE = "MaskType";
    static const char* TX_LITERAL_ROWCOUNT = "RowCount";
    static const char* TX_LITERAL_COLUMNCOUNT = "ColumnCount";
    
    /**
    * Gaze Point Data Behavior Literals
    */
    static const char* TX_LITERAL_GAZEPOINTDATAMODE = "GazePointDataMode";
    static const char* TX_LITERAL_GAZEPOINTDATAEVENTTYPE = "GazePointDataEventType";
    
    /**
    *    Activation Behavior Literals
    */
    static const char* TX_LITERAL_ACTIVATABLEEVENTTYPE = "ActivatableEventType";
    static const char* TX_LITERAL_HASACTIVATIONFOCUS = "HasActivationFocus";    
    static const char* TX_LITERAL_HASTENTATIVEACTIVATIONFOCUS = "HasTentativeActivationFocus";
    static const char* TX_LITERAL_ISACTIVATED = "IsActivated";
    static const char* TX_LITERAL_ISTENTATIVEFOCUS = "IsTentativeFocusEnabled";

    /**
    * Fixation Data Behavior Literals
    */
    static const char* TX_LITERAL_FIXATIONDATAMODE = "FixationDataMode";
    static const char* TX_LITERAL_FIXATIONDATAEVENTTYPE = "FixationDataEventType";

    /**
    * Action data Behavior Literals 
    */
    static const char* TX_LITERAL_ACTIONDATAEVENTTYPE = "ActionDataEventType";
    static const char* TX_LITERAL_ACTIVATIONMISSED = "ActivationMissed";
    
    /**
    * Gaze-Aware Behavior Literals
    */
    static const char* TX_LITERAL_HASGAZE = "HasGaze";
    static const char* TX_LITERAL_GAZEAWAREMODE = "GazeAwareMode";
    static const char* TX_LITERAL_DELAYTIME = "DelayTime";
    
    /**
    * Gaze Data Diagnostics Behavior Literals
    */
    static const char* TX_LITERAL_QUALITY = "Quality";    
    static const char* TX_LITERAL_NOISE = "Noise";
    static const char* TX_LITERAL_INSACCADE = "InSaccade";
    static const char* TX_LITERAL_INFIXATION = "InFixation";
    
    /**
    * Eye Position Behavior Literals
    */
    static const char* TX_LITERAL_LEFTEYEPOSITION = "LeftEyePosition";    
    static const char* TX_LITERAL_RIGHTEYEPOSITION = "RightEyePosition";
    static const char* TX_LITERAL_HASLEFTEYEPOSITION = "HasLeftEyePosition";
    static const char* TX_LITERAL_HASRIGHTEYEPOSITION = "HasRightEyePosition";

    /**
    * Presence Behavior Literals
    */
    static const char* TX_LITERAL_PRESENCEDATA = "Presence";


    /**
    * Pannable Behavior Literals
    */
    static const char* TX_LITERAL_PANVELOCITYX = "PanVelocityX";
    static const char* TX_LITERAL_PANVELOCITYY = "PanVelocityY";
    static const char* TX_LITERAL_PANSTEPX = "PanStepX";
    static const char* TX_LITERAL_PANSTEPY = "PanStepY";
    static const char* TX_LITERAL_PANSTEPDURATION = "PanStepDuration";
    static const char* TX_LITERAL_PANHANDSFREE = "PanHandsFree";
    static const char* TX_LITERAL_PANPROFILE = "Profile";
    static const char* TX_LITERAL_PANDIRECTIONSAVAILABLE = "PanDirectionsAvailable";
    static const char* TX_LITERAL_PANPEAKVELOCITY = "PeakVelocity";
    static const char* TX_LITERAL_PANADAPTVELOCITYTOVIEWPORT = "AdaptVelocityToViewport";
    static const char* TX_LITERAL_PANMAXZONERELATIVESIZE = "MaxPanZoneRelativeSize";
    static const char* TX_LITERAL_PANMAXZONESIZE = "MaxPanZoneSize";
    static const char* TX_LITERAL_PANZONESIZE = "PanZoneSize";
    static const char* TX_LITERAL_PANNABLEEVENTTYPE = "PannableEventType";

    /**
    *   Callback Response Literals
    */
    static const char* TX_LITERAL_REQUESTID = "RequestId";
    static const char* TX_LITERAL_ERRORMESSAGE = "ErrorMessage";
    static const char* TX_LITERAL_RESULT = "Result";

    /**
    *   Interaction Mode Literals
    */
    static const char* TX_LITERAL_ACTIONTYPE = "ActionType";    

    /**
    *   State literals
    */
    static const char* TX_LITERAL_STATEPATH = "StatePath";
    static const char* TX_LITERAL_STATEPATHDELIMITER = ".";

    /**
    *  Version literals
    */
    static const char* TX_LITERAL_VERSIONMAJOR = "VersionMajor";
    static const char* TX_LITERAL_VERSIONMINOR = "VersionMinor";
    static const char* TX_LITERAL_VERSIONBUILD = "VersionBuild";




/*********************************************************************************************************************/

/**
 * Literals for state paths.
 * 
 * @field TX_STATEPATH_EYETRACKING:
 *   The root node for all eyetracking information.
 *   GETTABLE.
 *
 * @field TX_STATEPATH_SCREENBOUNDS:
 *   Holds the virtual screen bounds in pixels. 
 *   The value can be retrieved from the state bag as a TX_RECT structure with GetStateValueAsRectangle.
 *   If the screen bounds can not be determined screen bounds (0, 0, 0, 0) will be returned.
 *   GETTABLE.
 *
 * @field TX_STATEPATH_DISPLAYSIZE:
 *   Holds the display size in millimeters as width and height. 
 *   The value can be retrieved from the state bag as a TX_SIZE2 structure with GetStateValueAsSize2.
 *   If the display size can not be determined Width and Height (0, 0) will be returned.
 *   GETTABLE.
 *
 * @field TX_STATEPATH_STATE: 
 *   Holds the eyetracking status. The value is of type TX_EYETRACKINGDEVICESTATUS.
 *   GETTABLE.
 *
 * @field TX_STATEPATH_PRESENCEDATA:
 *   Holds data about user presence. The value is of type TX_PRESENCEDATA.
 *   GETTABLE.
 *
 * @field TX_STATEPATH_ACTIONDATA:
 *   Pulses data when interactions fails. The value is of type TX_ACTIONDATA.
 *   GETTABLE.
 */
    
    static const char* TX_STATEPATH_EYETRACKING = "eyetracking";
    static const char* TX_STATEPATH_SCREENBOUNDS = "eyetracking.screenBounds";
    static const char* TX_STATEPATH_DISPLAYSIZE = "eyetracking.displaySize";
    static const char* TX_STATEPATH_STATE = "eyetracking.state";
    static const char* TX_STATEPATH_PRESENCEDATA = "presenceData";
    static const char* TX_STATEPATH_ACTIONDATA = "actionData";


/*********************************************************************************************************************/

/**
 * Constants for mask weights.
 *
 * @field TX_MASKWEIGHT_NONE:
 *	Use this mask weight to indicate that a region of an interactor has no weight (not interactable).
 *
 * @field TX_MASKWEIGHT_DEFAULT:
 *  Use this mask weight to indicate that a region of an interactor has a default weight.
 
 * @field TX_MASKWEIGHT_HIGH:
 *  Use this mask weight to indicate that a region of an interactor has a high weight (more likely to be interacted with).
 */

	static const unsigned char None = 0;
	static const unsigned char Default = 1;
	static const unsigned char High = 255;

    
/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_LITERALS__H__) */

/*********************************************************************************************************************/
