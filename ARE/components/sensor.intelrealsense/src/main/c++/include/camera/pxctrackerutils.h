/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/** @file pxctrackerutils.h
Defines the PXCTrackerUtils interface, which programs may use to perform additional (non-essential) operations for 3D tracking.
*/
#pragma once
#include "pxcsession.h"
#include "pxccapture.h"
#include "pxctracker.h"
#pragma warning(push)
#pragma warning(disable:4201) /* nameless structs/unions */

class PXCTrackerUtils:public PXCBase {
public:
	PXC_CUID_OVERWRITE(PXC_UID('T','R','K','U'));
		
	/**
	* The relative size of a target object.  Specifying the appropriate size helps improve the training initialization
	* process.
	*/
	enum ObjectSize
	{
		VERY_SMALL = 0,		/// Cup sized
		SMALL	   = 5,		/// Desktop sized
		MEDIUM	   = 10,	/// Room sized
		LARGE	   = 15		/// Building sized
	};

	/**
	* Special purpose cosIDs which may be passed in to \c QueryTrackingValues. These values may be used to get the
	* current tracking position of the map creation operations.
	*
	* \sa PXCTracker::QueryTrackingValues
	*/
	enum UtilityCosID
	{
		CALIBRATION_MARKER = -1,	/// Pose of the detected calibration marker
		ALIGNMENT_MARKER   = -2,	/// New pose of the tracked object based on the alignment marker
		IN_PROGRESS_MAP    = -3		/// Current pose of the new map as it is being created
	};

	/**
	*	Interactive version of map creation, similar to the toolbox functionality. Depth is automatically
	*   used if supported in the current camera profile.  Map creation is stopped either explicitly with \c Cancel3DMapCreation
	*	or by pausing the tracking module using pSenseManager->PauseTracker(TRUE).
	*
	*	The map file may be saved at any time with \c Save3DMap.
	*
	*   \param objSize: Relative size of the object to create a map for, an accurate value helps improve the initialization time.
	*
	*	\sa Cancel3DMapCreation
	*	\sa Save3DMap
	*	\sa QueryNumberFeaturePoints
	*	\sa QueryFeaturePoints
	*/	
	virtual pxcStatus PXCAPI Start3DMapCreation(ObjectSize objSize)=0;

	/** Cancel map creation without saving a file, resetting the internal state.
	*
	*	\sa Start3DMapCreation
	*/
	virtual pxcStatus PXCAPI Cancel3DMapCreation()=0;

	/**
	*	Begins extending a previously created 3D Map with additional feature points. Map extension is an interactive process only.
	*	The extended map may be saved using \c Save3DMap at any time.
	*
	*	\sa Load3DMap	
	*/
	virtual pxcStatus PXCAPI Start3DMapExtension()=0;

	/**
	*	Cancel map extension without saving a file, and reset the internal state.
	*
	*	\sa Cancel3DMapExtension
	*/
	virtual pxcStatus PXCAPI Cancel3DMapExtension()=0;

	/**
	*	Load a 3D Map from disk in preparation for map extension or alignment operations.
	*
	*	\param filename: Name of the filename to be loaded
	*/
	virtual pxcStatus PXCAPI Load3DMap(const pxcCHAR *filename)=0;

	/**
	*	Save a 3D Map.  Maps must be saved to disk for further usage, it is not possible to generate a map in memory
	*	and use it for tracking or extension later.
	*
	*	\param filename: Name of the filename to be saved
	*/
	virtual pxcStatus PXCAPI Save3DMap(const pxcCHAR *filename)=0;

	/**
	*	Returns the number of detected feature points during map creation.
	*
	*	\sa QueryFeaturePoints
	*/	
	virtual pxcI32    PXCAPI QueryNumberFeaturePoints()=0;

	/**
	*	Retrieve the detected feature points for map creation.  Active points are ones which have been detected
	*	in the current frame, inactive points were detected previously but are not detected in the current frame.
	*
	*	\param bufferSize:	Length of \c points to hold the 3D feature points
	*	\param points:		Array where the feature points will be stored
	*	\param returnActive: Return the active (currently tracked) features in the array
	*	\param returnInactive: Return the inactive (not currently tracked) features in the array
	*	
	*	\return The number of feature points copied into \c points
	*
	*	\sa QueryNumberFeaturePoints
	*	\sa Start3DMapCreation
	*	\sa Start3DMapExtension
	*/
	virtual pxcI32 PXCAPI QueryFeaturePoints(pxcI32 bufferSize, PXCPoint3DF32 *points, pxcBool returnActive, pxcBool returnInactive)=0;
		
	/**
	*	Aligns a loaded 3D map to the specified marker. Alignment defines the initial pose of the model relative to the axes
	*	printed on the marker (+Z points up out of the page).  By default, the coordinate system pose (origin and rotation)
	*	is in an undefined position with respect to the object. The placement of the marker specifies the (0,0,0) origin as
	*	well as the alignment of the coordinate axes (initial rotation).
	*
	*	Alignment also enhances the returned pose coordinates to be in units of millimeters, instead of an undefined unit.
	*
	*	\param markerID: Integer identifier for the marker (from the marker PDF)
	*	\param markerSize: Size of the marker in millimeters
	*
	*	\sa Stop3DMapAlignment, Is3dMapAlignmentComplete
	*/	
	virtual pxcStatus PXCAPI Start3DMapAlignment(pxcI32 markerID, pxcI32 markerSize)=0;

	/**
	*	Cancel the current 3D Map alignment operation before it is complete.  Any in-progress state will be lost.
	*
	*	\sa Start3DMapAlignment
	*	\sa Is3DMapAlignmentComplete
	*/	
	virtual pxcStatus PXCAPI Cancel3DMapAlignment(void)=0;

	/**
	*	Returns \c TRUE if alignment is complete.  At that point the file may be saved with the new alignment values.
	*
	*	\sa Start3DMapAlignment
	*/	
	virtual pxcBool PXCAPI Is3DMapAlignmentComplete(void)=0;

	/**
	*	Start the camera calibration process. Calibration can improve the tracking results by compensating for
	*	camera distortion and other intrinsic camera values.  A successful calibration requires several frames,
	*	with the marker in different orientations and rotations relative to the camera.
	*
	*	\param markerID: Integer identifier for the marker (from the marker PDF file)
	*	\param markerSize: Size of the printed marker in millimeters
	*
	*	\sa QueryCalibrationProgress
	*	\sa SaveCameraParameters
	*/
	virtual pxcStatus PXCAPI StartCameraCalibration(pxcI32 markerID, pxcI32 markerSize)=0;

	/**
	*	Stop the camera calibration process before it is complete.  No new calibration parameters may be saved.	
	*
	*	\sa StartCameraCalibration
	*	\sa QueryCalibrationProgress
	*/	
	virtual pxcStatus PXCAPI CancelCameraCalibration()=0;

	/**
	*	Return the calibration progress as a percentage (0 - 100%). Calibration requires several different views of the
	*	marker to produce an accurate result, this function returns the relative progress.  A calibration file may be saved
	*	before this function returns 100% but the quality will be degraded.
	*
	*	If calibration has not been started this function returns a negative value
	*
	*	\sa StartCameraCalibration
	*/
	virtual pxcI32 PXCAPI QueryCalibrationProgress(void)=0;
	
	/**
	*	Save the current camera intrinsic parameters to an XML file.
	*
	*	\param filename: Filename to save the XML camera parameters in
	*
	*	\sa SetCameraParameters
	*	\sa	StartCameraCalibration	
	*/
	virtual pxcStatus PXCAPI SaveCameraParametersToFile(const pxcCHAR *filename)=0;
};
