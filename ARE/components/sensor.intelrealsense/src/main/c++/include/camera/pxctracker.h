/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/** @file pxctracker.h
Defines the PXCTracker interface, which programs may use for 3D tracking.
*/
#pragma once
#include "pxcsession.h"
#include "pxccapture.h"
#pragma warning(push)
#pragma warning(disable:4201) /* nameless structs/unions */

class PXCTrackerUtils;

/**
This class defines a standard interface for 3D tracking algorithms. 
*/
class PXCTracker:public PXCBase {
public:
	PXC_CUID_OVERWRITE(PXC_UID('T','R','K','R'));

	/**
	* The tracking states of a target.
	*
	* The state of a target usually starts with ETS_NOT_TRACKING.
	* When it is found in the current camera image, the state change to
	* ETS_FOUND for one image, the following images where the location of the
	* target is successfully determined will have the state ETS_TRACKING.
	*
	* Once the tracking is lost, there will be one single frame ETS_LOST, then
	* the state will be ETS_NOT_TRACKING again. In case there is extrapolation 
	* of the pose requested, the transition may be from ETS_TRACKING to ETS_EXTRAPOLATED.
	*
	* To sum up, these are the state transitions to be expected:
	*  ETS_NOT_TRACKING -> ETS_FOUND 
	*  ETS_FOUND        -> ETS_TRACKING
	*  ETS_TRACKING     -> ETS_LOST
	*  ETS_LOST         -> ETS_NOT_TRACKING
	*
	* With additional extrapolation, these transitions can occur as well:
	*  ETS_TRACKING     -> ETS_EXTRAPOLATED
	*  ETS_EXTRAPOLATED -> ETS_LOST
	*
	* "Event-States" do not necessarily correspond to a complete frame but can be used to 
	* flag individual tracking events or replace tracking states to clarify their context:
	*  ETS_NOT_TRACKING -> ETS_REGISTERED -> ETS_FOUND for edge based initialization
	*/
	enum ETrackingState
	{
		ETS_UNKNOWN		 = 0,	///< Tracking state is unknown
		ETS_NOT_TRACKING = 1,	///< Not tracking
		ETS_TRACKING	 = 2,	///< Tracking
		ETS_LOST		 = 3,	///< Target lost
		ETS_FOUND		 = 4,	///< Target found
		ETS_EXTRAPOLATED = 5,	///< Tracking by extrapolating
		ETS_INITIALIZED	 = 6,	///< The tracking has just been loaded

		ETS_REGISTERED	 = 7,	///< Event-State: Pose was just registered for tracking
		ETS_INIT_FAILED  = 8	///< Initialization failed (such as instant 3D tracking)
	};

	struct TrackingValues {
		ETrackingState			state;			///< The state of the tracking values

		PXCPoint3DF32           translation;	///< Translation component of the pose in the camera coordinate system
		PXCPoint4DF32           rotation;		///< Rotation component of the pose

		/** 
		* Quality of the tracking values.
		* Value between 0 and 1 defining the tracking quality.
		* A higher value means better tracking results. More specifically:
		* - 1 means the system is tracking perfectly.
		* - 0 means that we are not tracking at all.
		*/
		pxcF32				quality;

		pxcF64				timeElapsed;				///< Time elapsed (in ms) since last state change of the tracking system
		pxcF64				trackingTimeMs;				///< Time (in milliseconds) used for tracking the respective frame
		pxcI32				cosID;						///< The ID of the coordinate system
		pxcCHAR				targetName[256];            ///< The name of the target object
		pxcCHAR				additionalValues[256];      ///< Extra space for information provided by a sensor that cannot be expressed with translation and rotation properly.
		pxcCHAR				sensor[256];                ///< The sensor that provided the values

		PXCPointF32			translationImage;			///< The translation component of the pose projected onto the color image, in pixels

		pxcI32				reserved[30];               // 0 - reserved for module specific parameters
	};
	
	/// Returns TRUE if the current state is actively tracking (valid pose information is available)
	static pxcBool __inline PXCAPI IsTracking(ETrackingState state)
	{
		return (state == ETS_FOUND) || (state == ETS_TRACKING) || (state == ETS_EXTRAPOLATED);
	};

	/// Set the camera parameters, which can be the result of camera calibration from the toolbox
	virtual pxcStatus PXCAPI SetCameraParameters(const pxcCHAR *filename)=0;
	
	/**
	* Add a 2D reference image for tracking an object
	* \param filename: path to image file
	* \param[out] cosID: coordinate system ID of added target
	* \param widthMM: width of the physical target in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param heightMM: width of the physical target height in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param qualityThreshold: minimal similarity [0-1] that needs to be met for the image to be reported as tracked
	*/
	virtual pxcStatus PXCAPI Set2DTrackFromFile(const pxcCHAR *filename, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold)=0;

	/**
	* Add a 2D reference image for tracking an object
	* \param filename: path to image file
	* \param[out] cosID: coordinate system ID of added target
	*/
	pxcStatus __inline Set2DTrackFromFile(const pxcCHAR *filename, pxcUID& cosID) { return Set2DTrackFromFile(filename, cosID, 0, 0, 0.8f); }

	/**
	* Add a 2D reference image for tracking an object
	* \param image: the image instance
	* \param[out] cosID: coordinate system ID of added target
	* \param widthMM: width of the physical target in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param heightMM: width of the physical target height in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param qualityThreshold: minimal similarity [0-1] that needs to be met for the image to be reported as tracked
	*/
	virtual pxcStatus PXCAPI Set2DTrackFromImage(PXCImage *image, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold)=0;

	/**
	* Add a 2D reference image for tracking an object
	* \param image: the image instance
	* \param[out] cosID: coordinate system ID of added target
	*/
	pxcStatus __inline Set2DTrackFromImage(PXCImage *image, pxcUID& cosID) { return Set2DTrackFromImage(image, cosID, 0, 0, 0.8f); }

	/**
	* Add a 3D tracking configuration for a target
	*
	* This file can be generated with the Toolbox
	*
	* \param filename The full path to the configuration file (*.slam, *.xml)
	* \param[out] firstCosID: coordinate system ID of the first added target
	* \param[out] lastCosID: coordinate system ID of the last added target (may be the same as \c firstCosID)
	*/
	virtual pxcStatus PXCAPI Set3DTrack(const pxcCHAR *filename, pxcUID& firstCosID, pxcUID& lastCosID)=0;

	/**
	* Enable instant 3D tracking (SLAM).  This form of tracking does not require an object model to be
	* previously created and loaded.
	*
	* \param egoMotion: Specify the coordinate system origin and orientation of the tracked object.
	*					\c true uses the first image captured from the camera
	*					\c false (default) uses the "main plane" of the scene which is determined heuristically	
	*/
	virtual pxcStatus PXCAPI Set3DInstantTrack(pxcBool egoMotion)=0;

	/**
	* Enable instant 3D tracking (SLAM).  This form of tracking does not require an object model to be
	* previously created and loaded.
	*/
	pxcStatus __inline Set3DInstantTrack(void) { return Set3DInstantTrack(false); }
	
	/**
	* Get the number of targets currently tracking
	* \return The number of active tracking targets
	*
	* \sa QueryTrackingValues, QueryAllTrackingValues
	*/
	virtual pxcI32 PXCAPI QueryNumberTrackingValues() const = 0;

	/**
	* Get information for all of the active tracking targets
	* 
	* \param trackingValues: Pointer to store the tracking results at.  The passed in block must be
	*						 at least QueryNumberTrackingValues() elements long
	*/
	virtual pxcStatus PXCAPI QueryAllTrackingValues(PXCTracker::TrackingValues *trackingValues)=0;

	/**
	* Return information for a particular coordinate system ID.  This value can be returned from Set2DTrackFromFile(),
	* Set2DTrackFromImage(), or Set3DTrack().  Coordinate system IDs for Set3DInstantTrack() are generated dynamically as
	* targets are determined in the scene.
	*
	* \param cosID: The coordinate system ID to return the status for
	* \param outTrackingValues: The returned tracking values
	*/
	virtual pxcStatus PXCAPI QueryTrackingValues(pxcUID cosID, TrackingValues& outTrackingValues)=0;	

	/**
	*	Remove a previously returned cosID from tracking.
	*/
	virtual pxcStatus PXCAPI RemoveTrackingID(pxcUID cosID)=0;

	/**
	*	Remove all previous created cosIDs from tracking
	*/
	virtual pxcStatus PXCAPI RemoveAllTrackingIDs()=0;

protected:

	virtual pxcStatus PXCAPI Set2DTrackFromFileExt(const pxcCHAR *filename, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold, pxcBool extensible) = 0;

public:

	/**
	* Add a 2D reference image for tracking an object
	* \param filename: path to image file
	* \param[out] cosID: coordinate system ID of added target
	* \param widthMM: width of the physical target in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param heightMM: width of the physical target height in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param qualityThreshold: minimal similarity [0-1] that needs to be met for the image to be reported as tracked
	* \param extensible: Use features from the environment to improve tracking
	*/
	__inline pxcStatus Set2DTrackFromFile(const pxcCHAR *filename, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold, pxcBool extensible) {
		return Set2DTrackFromFileExt(filename, cosID, widthMM, heightMM, qualityThreshold, extensible);
	}

	/**
	* Add a 2D reference image for tracking an object
	* \param filename: path to image file
	* \param[out] cosID: coordinate system ID of added target
	* \param extensible: Use features from the environment to improve tracking
	*/
	__inline pxcStatus Set2DTrackFromFile(const pxcCHAR *filename, pxcUID& cosID, pxcBool extensible) { return Set2DTrackFromFile(filename, cosID, 0.f, 0.f, 0.8f, extensible); }

protected:

	virtual pxcStatus PXCAPI Set2DTrackFromImageExt(PXCImage *image, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold, pxcBool extensible) = 0;

public:

	/**
	* Add a 2D reference image for tracking an object
	* \param image: the image instance
	* \param[out] cosID: coordinate system ID of added target
	* \param widthMM: width of the physical target in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param heightMM: width of the physical target height in mm (optional). Only used to improve the mm unit accuracy in RGB tracking.
	* \param qualityThreshold: minimal similarity [0-1] that needs to be met for the image to be reported as tracked
	* \param extensible: Use features from the environment to improve tracking
	*/
	__inline pxcStatus Set2DTrackFromImage(PXCImage *image, pxcUID& cosID, pxcF32 widthMM, pxcF32 heightMM, pxcF32 qualityThreshold, pxcBool extensible) {
		return Set2DTrackFromImageExt(image, cosID, widthMM, heightMM, qualityThreshold, extensible);
	}

	/**
	* Add a 2D reference image for tracking an object
	* \param image: the image instance
	* \param[out] cosID: coordinate system ID of added target
	* \param extensible: Use features from the environment to improve tracking
	*/
	__inline pxcStatus Set2DTrackFromImage(PXCImage *image, pxcUID& cosID, pxcBool extensible) { return Set2DTrackFromImage(image, cosID, 0.f, 0.f, 0.8f, extensible); }

protected:

	virtual pxcStatus PXCAPI Set3DTrackExt(const pxcCHAR *filename, pxcUID& firstCosID, pxcUID& lastCosID, pxcBool extensible)=0;

public:

	/**
	* Add a 3D tracking configuration for a target
	*
	* This file can be generated with the Toolbox
	*
	* \param filename The full path to the configuration file (*.slam, *.xml)
	* \param[out] firstCosID: coordinate system ID of the first added target
	* \param[out] lastCosID: coordinate system ID of the last added target (may be the same as \c firstCosID)
	* \param extensible: Use features from the environment to improve tracking
	*/
	__inline pxcStatus Set3DTrack(const pxcCHAR *filename, pxcUID& firstCosID, pxcUID& lastCosID, pxcBool extensible) {
		return Set3DTrackExt(filename, firstCosID, lastCosID, extensible);
	}

protected:

	virtual pxcStatus PXCAPI Set3DInstantTrackExt(pxcBool egoMotion, pxcI32 framesToSkip)=0;

public:

	/**
	* Enable instant 3D tracking (SLAM).  This form of tracking does not require an object model to be
	* previously created and loaded.
	*
	* \param egoMotion: Specify the coordinate system origin and orientation of the tracked object.
	*					\c true uses the first image captured from the camera
	*					\c false (default) uses the "main plane" of the scene which is determined heuristically	
	*
	* \param framesToSkip: Instant tracking may fail to initialize correctly if the camera image has not stabilized
	*                      or is not pointing at the desired object when the first frames are processed.  This parameter
	*					   skips the initial frames which may have automatic adjustments such as contrast occuring.
	*                      This parameter may be 0 if instant 3D tracking should initialize from the next frame.
	*/
	__inline pxcStatus Set3DInstantTrack(pxcBool egoMotion, pxcI32 framesToSkip) {
		return Set3DInstantTrackExt(egoMotion, framesToSkip);
	}

	/**
	* Return the \c PXCTrackerUtils instance which may be used to configure advanced tracking features,
	* and perform 3D map creation tasks.
	*/
	virtual PXCTrackerUtils* QueryTrackerUtils()=0;

	/**
	*	Specify a region of interest (ROI) for use when processing images during tracking and map creation.
	*	Only pixels within the ROI rectangle are used, for example to remove a cluttered background.
	*
	*	Specifying a width or height of 0 will remove the ROI and use the full image. The entire rectangle
	*   must lie inside of the color image boundaries.
	*	
	*	\param roi: The rectangle region of interest to be considered on subsequent frames
	*/
	virtual pxcStatus PXCAPI SetRegionOfInterest(const PXCRectI32& roi)=0;
};
