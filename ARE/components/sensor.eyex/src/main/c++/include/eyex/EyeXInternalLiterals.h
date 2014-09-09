/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXInternalLiterals.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_INTERNALLITERALS__H__)
#define __TOBII_TX_INTERNALLITERALS__H__


/*********************************************************************************************************************
 * Literals
 *********************************************************************************************************************/



    static const char* TX_INTERNALLITERAL_INTERACTORS = "Interactors";
    static const char* TX_INTERNALLITERAL_METADATA = "Metadata";
    static const char* TX_INTERNALLITERAL_SERIALNUMBER = "SerialNumber";


    /**
    *   Message Type Literals
    */
    static const char* TX_INTERNALLITERAL_MESSAGETYPE = "MessageType";


    /**
    *   Snapshot internal literals
    */
    static const char* TX_INTERNALLITERAL_NONAUTHORITATIVEWINDOWIDS = "NonAuthoritativeWindowIds";


    /**
    *   Snapshot Literals
    */
    static const char* TX_INTERNALLITERAL_WINDOWIDS = "WindowIds";


    /**
    *   Interactor Literals
    */
    static const char* TX_INTERNALLITERAL_BEHAVIORS = "Behaviors";
    static const char* TX_INTERNALLITERAL_BEHAVIORTYPE = "BehaviorType";
    static const char* TX_INTERNALLITERAL_PARENTID = "ParentId";
    static const char* TX_INTERNALLITERAL_INTERACTORID = "InteractorId";
    static const char* TX_INTERNALLITERAL_ISENABLED = "IsEnabled";
    static const char* TX_INTERNALLITERAL_ISDELETED = "IsDeleted";
    static const char* TX_INTERNALLITERAL_WINDOWID = "WindowId";

    /**
    *   Command Literals
    */
    static const char* TX_LITERAL_COMMANDTYPE = "CommandType";


    /**
    *   Callback Response Literals
    */
    static const char* TX_INTERNALLITERAL_NOTIFICATIONTYPE = "NotificationType";    
    static const char* TX_INTERNALLITERAL_ISCANCELLED = "IsCancelled";

/*********************************************************************************************************************/


    /**
    *    State literals
    */    
    static const char* TX_INTERNALLITERAL_STATEPATH = "StatePath";

    /**
    *  Termination Literals
    */
    static const char* TX_INTERNALLITERAL_TERMINATEGXWINDOWS = "TerminateGxWindows";
    static const char* TX_INTERNALLITERAL_GLOBALTERMINATEGXSERVER = "Global\\TerminateGxServer";
    

   /**
    *   Internal Raw Gaze Data Literals
    **/
    static const char* TX_INTERNALLITERAL_EYEPOSITIONFROMSCREENCENTERMM = "EyePositionFromScreenCenterMM";
    static const char* TX_INTERNALLITERAL_EYEPOSITIONINTRACKBOXNORMALIZED = "EyePositionInTrackBoxNormalized";
    static const char* TX_INTERNALLITERAL_GAZEPOINTFROMSCREENCENTERMM = "GazePointFromScreenCenterMM";
    static const char* TX_INTERNALLITERAL_GAZEPOINTONDISPLAYNORMALIZED = "GazePointOnDisplayNormalized";
    static const char* TX_INTERNALLITERAL_BOTTOMLEFT = "BottomLeft";
    static const char* TX_INTERNALLITERAL_TOPLEFT = "TopLeft";
    static const char* TX_INTERNALLITERAL_TOPRIGHT = "TopRight";
    static const char* TX_INTERNALLITERAL_SCREENBOUNDSMM = "ScreenBoundsMm";
    static const char* TX_INTERNALLITERAL_SCREENBOUNDSPIXELS = "ScreenBoundsPixels";
    static const char* TX_INTERNALLITERAL_TRACKEDEYES = "TrackedEyes";    
    //these below could be in an enum, but wont be, since its an internal stream
    static const char* TX_INTERNALLITERAL_TRACKEDEYESNONE = "TrackedEyesNone";
    static const char* TX_INTERNALLITERAL_TRACKEDEYESBOTH = "TrackedEyesBoth";
    static const char* TX_INTERNALLITERAL_TRACKEDEYESLEFT = "TrackedEyesLeft";
    static const char* TX_INTERNALLITERAL_TRACKEDEYESRIGHT = "TrackedEyesRight";

    static const char* TX_INTERNALLITERAL_DETECTEDEYES = "DetectedEyes";
    static const char* TX_INTERNALLITERAL_DETECTEDEYESNONE = "DetectedEyesNone";
    static const char* TX_INTERNALLITERAL_DETECTEDEYESBOTH = "DetectedEyesBoth";
    static const char* TX_INTERNALLITERAL_DETECTEDEYESLEFT = "DetectedEyesLeft";
    static const char* TX_INTERNALLITERAL_DETECTEDEYESRIGHT = "DetectedEyesRight";

   /**
    *   Internal Zoom Literals
    **/
    static const char* TX_INTERNALLITERAL_ZOOMDIRECTION = "ZoomDirection";
    static const char* TX_INTERNALLITERAL_ZOOMGAZEPOINTX = "ZoomGazePointX";
    static const char* TX_INTERNALLITERAL_ZOOMGAZEPOINTY = "ZoomGazePointY";




    
/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_INTERNALLITERALS__H__) */

/*********************************************************************************************************************/
