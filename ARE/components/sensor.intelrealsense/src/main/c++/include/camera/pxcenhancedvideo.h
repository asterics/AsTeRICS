/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file pxcenhancedvideo.h
    Defines the PXCEnhancedVideo interface, which programs may use to process a video stream 
	using enhanced videography features.
 */
#pragma once
#include "pxcbase.h"
#include "pxcimage.h"

/**
	This class defines a standard interface for enhanced photography algorithms.
*/
class PXCEnhancedVideo:public PXCBase {
public:

	PXC_CUID_OVERWRITE(PXC_UID('E','V','I','N'));

	/* 
		Input param for Tracking Method: 
		LAYER: track the depth layer in each frame
		OBJECT: track the slected object in each frame
	*/
	enum TrackMethod {
		LAYER = 0, 
		OBJECT, 
	};


   	/**
	 *  EnableTracker: creates an object tracker with a specific tracking method and an 
     *  initial bounding mask as a hint for the object to detect. 
	 *  boundingMask: a hint on what object to detect setting the target pixels to 255 and background to 0.  
	 *  method: Tracking method for depth layer tracking or object tracking.
	*/
	virtual pxcStatus PXCAPI EnableTracker(const PXCImage *boundingMask, TrackMethod method) = 0;
	__inline pxcStatus EnableTracker(const PXCImage *boundingMask) { 
		return EnableTracker(boundingMask, TrackMethod::LAYER);
	}

	/* 
	 *  QueryTrackedObject: returns the tracked object selected in EnableTracker() after every processed frame.
	 *  Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
	 *   returned PXCImage is managed internally APP should not release: TO DO!!
	*/
	virtual PXCImage* PXCAPI QueryTrackedObject() = 0; 

};