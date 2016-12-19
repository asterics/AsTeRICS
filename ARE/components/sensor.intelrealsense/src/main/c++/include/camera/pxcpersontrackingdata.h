/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/

#pragma once
#include "pxcpersontrackingmodule.h"
#include "pxcimage.h"

/**
	@class PXCPersonTrackingData	
*/
class PXCPersonTrackingData: public PXCBase
{
public:
	/* Constants */
	PXC_CUID_OVERWRITE(PXC_UID('P','O','T','D')); 
	
	/** @enum AlertType
		Identifiers for the events that can be detected and fired by the person module.
	*/
	enum AlertType {
		 ALERT_PERSON_DETECTED				= 0x0001,
		 ALERT_PERSON_NOT_DETECTED			= 0x0002,
		 ALERT_PERSON_OCCLUDED				= 0X0004,
		 ALERT_PERSON_TOO_CLOSE				= 0X0008,
		 ALERT_PERSON_TOO_FAR				= 0X0010
	};
	
	/** 
		@enum AccessOrderType
		Orders in which the person can be accessed.
	*/
	enum AccessOrderType {
		ACCESS_ORDER_BY_ID=0			/// By unique ID of the person
#ifdef PT_MW_DEV
		, ACCESS_ORDER_BY_TIME 			/// From oldest to newest person in the scene           
		, ACCESS_ORDER_NEAR_TO_FAR		/// From nearest to farthest person in scene
		, ACCESS_ORDER_LEFT_TO_RIGHT	/// Ordered from left to right in scene
#endif
	};	

	/**
		@enum TrackingState
		The current state of the module, either tracking specific people or performing full detection
	*/
	enum TrackingState
	{
		TRACKING_STATE_TRACKING = 0,
		TRACKING_STATE_DETECTING
	};

	/* Data Structures */
   
	/** 
		@struct AlertData		
    */
    struct AlertData 
	{
		AlertType	label;	    	/// The type of alert
		pxcUID      personId;	    /// The ID of the person that triggered the alert, if relevant and known
		pxcI64      timeStamp;		/// The time-stamp in which the event occurred
		pxcI32      frameNumber;    /// The number of the frame in which the event occurred (relevant for recorded sequences)
	};
	
	struct PoseEulerAngles
	{		
		pxcF32 yaw;
		pxcF32 pitch;
		pxcF32 roll;
		pxcI32 reserved[10];
	};

	struct BoundingBox2D
	{
		PXCRectI32 rect;
		pxcI32 confidence;
	};

	/* Interfaces */

#ifdef PT_MW_DEV
	class PersonPose
	{
	public:
		/** 
			@enum Position
			Describes the position of the person
		*/
		enum PositionState
		{
			POSITION_LYING_DOWN = 0,
			POSITION_SITTING,
			POSITION_CROUCHING,
			POSITION_STANDING,
			POSITION_JUMPING
		};

		struct PositionInfo
		{
			PositionState position;
			pxcI32 confidence;
		};

		struct LeanInfo
		{
			PoseEulerAngles leanData;
			pxcI32 confidence;
		};

		/**
			@brief Get the person's position data
			@param[out] position describes the person's position
			@param[out] confidence is the algorithm's confidence in the determined position
		*/
		virtual PositionInfo PXCAPI QueryPositionState() = 0;

		/**
			@brief Get the direction a person is leaning towards
			@param[out] leanData describes where the person is leaning to in terms of yaw, pitch, and roll
			@param[out] confidence is the algorithm's confidence in the determined orientation
			@return false if lean data doesn't exist, true otherwise
		*/
		virtual pxcBool PXCAPI QueryLeanData(LeanInfo& leanInfo) = 0;
	};
#endif

	class PersonRecognition
	{
	public:
		/**
		@brief Register a user in the Recognition database.
		@return The unique user ID assigned to the registered person by the Recognition module.
		*/
		virtual pxcUID PXCAPI RegisterUser() = 0;

		/**
		@brief Removes a user from the Recognition database.
		*/
		virtual void PXCAPI UnregisterUser() = 0;

		/**
		@brief Checks if a user is registered in the Recognition database.
		@return true - if user is in the database, false otherwise.
		*/
		virtual pxcBool PXCAPI IsRegistered() const = 0;

		/**
		@brief Returns the ID assigned to the current person by the Recognition module
		@return The ID assigned by the Recognition module, or -1 if person was not recognized.
		*/
		virtual pxcUID PXCAPI QueryRecognitionID() const = 0;
	};

	class RecognitionModuleData
	{
	public:
		/**
		@brief Retrieves the size of the recognition database for the user to be able to allocate the db buffer in the correct size
		@return The size of the database in bytes.
		*/
		virtual pxcI32 PXCAPI QueryDatabaseSize() const = 0;

		/**
		@brief Copies the recognition database buffer to the user. Allows user to store it for later usage.
		@param[in] buffer A user allocated buffer to copy the database into. The user must make sure the buffer is large enough (can be determined by calling QueryDatabaseSize()).
		@return true if database has been successfully copied to db. false - otherwise.
		*/
		virtual void PXCAPI QueryDatabaseBuffer(pxcBYTE* buffer) const = 0;

		/**
		@brief Unregisters a user from the database by user ID
		@param[in] the ID of the user to unregister
		*/
		virtual void PXCAPI UnregisterUserByID(pxcUID userID) = 0;
	};

#ifdef PT_MW_DEV
	class PersonGestures
	{
	public:
		struct PointingInfo
		{
			PXCPointF32		originColor;
			PXCPoint3DF32	originWorld;
			PXCPointF32		directionColor;
			PXCPoint3DF32	directionWorld;
			int             confidence;
		};

		/**
		@brief Retrieves information about a person pointing their hand, in the form of a point of origin and a vector for the direction.
		@return the structure that describes the pointing gesture.
		*/
		virtual PointingInfo QueryPointingInfo() = 0;
		
		/**
		@brief Indicates whether a pointing gesture is occurring.
		@return true if the person is pointing, false otherwise.
		*/
		virtual bool IsPointing() = 0;
	};
#endif

	class PersonJoints
	{
	public:
		enum JointType
		{
			JOINT_ANKLE_LEFT,
			JOINT_ANKLE_RIGHT,
			JOINT_ELBOW_LEFT,
			JOINT_ELBOW_RIGHT,
			JOINT_FOOT_LEFT,
			JOINT_FOOT_RIGHT,
			JOINT_HAND_LEFT,
			JOINT_HAND_RIGHT,
			JOINT_HAND_TIP_LEFT,
			JOINT_HAND_TIP_RIGHT,
			JOINT_HEAD,
			JOINT_HIP_LEFT,
			JOINT_HIP_RIGHT,
			JOINT_KNEE_LEFT,
			JOINT_KNEE_RIGHT,
			JOINT_NECK,
			JOINT_SHOULDER_LEFT,
			JOINT_SHOULDER_RIGHT,	
			JOINT_SPINE_BASE,
			JOINT_SPINE_MID,
			JOINT_SPINE_SHOULDER,
			JOINT_THUMB_LEFT,
			JOINT_THUMB_RIGHT,  
			JOINT_WRIST_LEFT,
			JOINT_WRIST_RIGHT
		};

		struct SkeletonPoint
		{
			JointType jointType;
			pxcI32 confidenceImage;
			pxcI32 confidenceWorld;
			PXCPoint3DF32 world;
			PXCPointF32   image;
			pxcI32 reserved[10];
		};

		struct Bone
		{
			JointType startJoint;
			JointType endJoint;
			PoseEulerAngles orientation;
			pxcI32 orientationConfidence;
			pxcI32 reserved[10];
		};

		/**
			@brief Returns the number of tracked joints
		*/
		virtual pxcI32 PXCAPI QueryNumJoints() = 0;
		
		/**
			@brief Retrieves all joints
			@param[out] joints the joints' locations are copied into this array. The application is expected to allocate this array (size retrieved from QueryNumJoints())
			@return Returns true if data and parameters exist, false otherwise.
		*/
		virtual pxcBool PXCAPI QueryJoints(SkeletonPoint* joints) = 0;
		
#ifdef PT_MW_DEV
		/**
			@brief Returns the number of tracked bones
		*/
		virtual pxcI32 PXCAPI QueryNumBones() = 0;

		/**
			@brief Retrieves all bones
			@param[out] bones the bones' locations are copied into this array. The application is expected to allocate this array (size retrieved from QueryNumBones())
			@return Returns true if data and parameters exist, false otherwise.
		*/
		virtual pxcBool PXCAPI QueryBones(Bone* bones) = 0;
#endif
	};

    /** 
		@class PXCPersonDetection
    */
    class PersonTracking
	{
	public:
		struct BoundingBox3D
		{
			PXCBox3DF32 box;
			pxcI32 confidence;
		};

		struct SpeedInfo
		{
			PXCPoint3DF32 direction;
			pxcF32 magnitude;
		};

		struct PointInfo
		{
			PXCPoint3DF32 point;
			pxcI32 confidence;
		};
		
		struct PointCombined
		{
			PointInfo image;
			PointInfo world;
			pxcI32 reserved[10];
		};

	    /**	
			@brief Return the person's unique identifier.
		*/
		virtual pxcUID PXCAPI QueryId() const = 0; 
		
	   /**	
			@brief Return the location and dimensions of the tracked person, represented by a 2D bounding box (defined in pixels).
		*/
		virtual BoundingBox2D PXCAPI Query2DBoundingBox() const = 0; 

		/**
			@brief Retrieve the 2D image mask of the tracked person.
		*/
		virtual PXCImage* PXCAPI QuerySegmentationImage() = 0;

		/**
			@brief Retrieves the center mass of the tracked person
			@return Center of mass in image and world coordinates
		*/
		virtual PointCombined PXCAPI QueryCenterMass() = 0;

		/**
		@brief Return the location and dimensions of the tracked person's head, represented by a 2D bounding box (defined in pixels).
		*/
		virtual BoundingBox2D PXCAPI QueryHeadBoundingBox() const = 0;

		/**
		@brief Retrieve the 3D image mask of the tracked person.
		*/
		virtual PXCImage* PXCAPI QueryBlobMask() = 0;

#ifdef PT_MW_DEV
		/**
			@brief Return the location and dimensions of the tracked person, represented by a 3D bounding box.
		*/
		virtual BoundingBox3D PXCAPI Query3DBoundingBox() const = 0;

		/**
		  @brief Return the speed of person in 3D world coordinates
		  @param[out] direction the direction of the movement
		  @param[out] magnitude the magnitude of the movement in meters/second
		  @return true if data exists, false otherwise
		*/
		virtual pxcBool PXCAPI QuerySpeed(SpeedInfo& speed) const = 0;

		/** 
			@brief Get the number of pixels in the blob
		*/
		virtual pxcI32 PXCAPI Query3DBlobPixelCount() const = 0;

		/**
			@brief Retrieves the 3D blob of the tracked person
			@param[out] blob The array of 3D points to which the blob will be copied. Must be allocated by the application
		*/
		virtual pxcStatus PXCAPI Query3DBlob(PXCPoint3DF32* blob) const = 0;

		/**
			@brief Retrieve the tracked person's height in millimeters
			@return Person height in millimeters
		*/
		virtual pxcF32 PXCAPI QueryHeight() const = 0;

		/** 
			@brief Get the contour size (number of points in the contour)
		*/
		virtual pxcI32 PXCAPI QueryContourSize() const = 0;
		
		/** 
			@brief Get the data of the contour line
		*/
		virtual pxcStatus PXCAPI QueryContourPoints(PXCPointI32* contour) = 0; 
#endif
	};

	class Person
	{
	public:
		/**
			@brief Returns the Person Detection interface
		*/
		virtual PXCPersonTrackingData::PersonTracking* PXCAPI QueryTracking() = 0;
		
		/**
			@brief Returns the Person Recognition interface
		*/
		virtual PXCPersonTrackingData::PersonRecognition* PXCAPI QueryRecognition() = 0;

		/**
			@brief Returns the Person Joints interface
		*/
		virtual PXCPersonTrackingData::PersonJoints* PXCAPI QuerySkeletonJoints() = 0;
#ifdef PT_MW_DEV
		/**
			@brief Returns the Person Gestures interface
		*/
		virtual PXCPersonTrackingData::PersonGestures* PXCAPI QueryGestures() = 0;
		
		/**
			@brief Returns the Person Pose interface
		*/
		virtual PXCPersonTrackingData::PersonPose* PXCAPI QueryPose() = 0;
#endif
	};

public:
	/* General */

    /* Person Outputs */
	
	/** 
		@brief Return the number of persons detected in the current frame.            
	*/
	virtual pxcI32 PXCAPI QueryNumberOfPeople(void) = 0;
	
	/** 
		@brief Retrieve the person object data using a specific AccessOrder and related index.
	*/
	virtual Person* PXCAPI QueryPersonData(AccessOrderType accessOrder, pxcI32 index) = 0;

	/** 
		@brief Retrieve the person object data by its unique Id.		
	*/
	 virtual Person* PXCAPI QueryPersonDataById(pxcUID personID) = 0;

	 /**
		@brief Enters the person to the list of tracked people starting from next frame
	 */
	 virtual void PXCAPI StartTracking(pxcUID personID) = 0;

	 /**
	 @brief Removes the person from tracking
	 */
	 virtual void PXCAPI StopTracking(pxcUID personID) = 0;

	 /**
		@brief Retrieve current tracking state of the Person Tracking module
	*/
	 virtual TrackingState PXCAPI GetTrackingState() = 0;

	 /**
	 @brief Returns the Person Recognition module interface
	 */
	 virtual PXCPersonTrackingData::RecognitionModuleData* PXCAPI QueryRecognitionModuleData() = 0;

#ifdef PT_MW_DEV
	 /* Alerts Outputs */

	 /**
	 @brief Return the number of fired alerts in the current frame.
	 */
	 virtual pxcI32 PXCAPI QueryFiredAlertsNumber(void) const = 0;

	 /**
	 @brief Get the details of the fired alert with the given index.
	 */
	 virtual pxcStatus PXCAPI QueryFiredAlertData(pxcI32 index, AlertData & alertData) const = 0;

	 /**
	 @brief Return whether the specified alert is fired in the current frame, and retrieve its data if it is.
	 */
	 virtual pxcBool PXCAPI IsAlertFired(AlertType alertEvent, AlertData & alertData) const = 0;
#endif
};


