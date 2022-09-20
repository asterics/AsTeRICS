/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/** 
	@file pxcphoto.h
    Defines the PXCPhoto interface, which defines the photo container.
  */
#pragma once
#include "pxccapture.h"
#include "pxcaddref.h"

/**
    This interface defines the photo container. Call PXCSession::CreatePhoto to create
	an instance of this object. Then initiailize it with different member functions.

	The interface extends PXCMetadata. Use QueryInstance<PXCMetadata>() to access
	the PXCMetadata features.
*/

class PXCPhoto: public PXCBase {
public:

    PXC_CUID_OVERWRITE(PXC_UID('G','D','V','2'));

    /** 
        @brief Import the preview sample content into the photo instance.
        @param[in]  sample				The PXCCapture::Sample instance from the SenseManager QuerySample().
        @return PXC_STATUS_NO_ERROR     Successful execution.
    */
    virtual pxcStatus PXCAPI ImportFromPreviewSample(PXCCapture::Sample *sample)=0;

	/** 
	@brief Check if a file in an XDM file or not.
	@param[in]   filename           The file name.
	@return true if file is XDM and false otherwise.
	*/ 
    virtual pxcBool PXCAPI IsXDM(pxcCHAR *filename)=0;

	/** 
		SubSample: Subsampling rate to load high resolution images faster 
		NO_SUBSAMPLING = 0
		SUBSAMPLE_2 = 2
		SUBSAMPLE_4 = 4
		SUBSAMPLE_8 = 8
	*/
	enum Subsample
	{
		NO_SUBSAMPLING,
		SUBSAMPLE_2,
		SUBSAMPLE_4,
		SUBSAMPLE_8
	};

	/** 
	@brief Import the photo content from the XDM File Format v2.0.
	@param[in]   filename           The file name.
	@return PXC_STATUS_NO_ERROR     Successful execution.
	*/ 
	virtual pxcStatus PXCAPI LoadXDM(pxcCHAR *filename, Subsample subsample) = 0;
	__inline pxcStatus LoadXDM(pxcCHAR *filename) { 
		return LoadXDM(filename, Subsample::NO_SUBSAMPLING);
	}


    /** 
        @brief Export the photo content to the XDM File Format v2.0.
		@param[in]   filename           The file name.
		@return PXC_STATUS_NO_ERROR     Successful execution.
    */ 
    virtual pxcStatus PXCAPI SaveXDM(pxcCHAR *filename)=0;

	/**
	@brief Copy the content from the source photo
	@param[in]  photo	The source photo.
	@return PXC_STATUS_NO_ERROR	Successful execution.
	*/
	virtual pxcStatus PXCAPI CopyPhoto(const PXCPhoto *photo) = 0;

	/**
	@brief Get the container color image of the photo. The container image is usually the processed color image.
			   Also known as reference Image
	@return The PXCImage instance.
	*/
	virtual PXCImage* PXCAPI QueryContainerImage(void) = 0;

	/**
		@brief Copy the camera[0] color image to the container image of the photo.
	*/
	virtual pxcStatus PXCAPI ResetContainerImage(void) = 0;

	/**
	@brief Get the color image in camera[camIdx] of the photo. The unedited image is usually the unprocessed color image in camera[0].
	@return The PXCImage instance.
	*/
	virtual PXCImage* PXCAPI QueryImage(pxcI32 camIdx) = 0;
		
	/**
	@brief Get the unedited color image of the photo. The unedited image is usually the unprocessed color image.
	@return The PXCImage instance.
	*/

	__inline PXCImage* QueryImage(void) { 
		return QueryImage(0);
	}

	/**
	@brief Get the raw depth image of the photo. This would be the unprocessed depth captured from the camera or loaded from a file if it existed.
	this is tagged by R200-rawdepth in VendorInfo->Model
	@return The PXCImage instance.
	*/
	virtual PXCImage* PXCAPI QueryRawDepth(void) = 0;

	/**
	@brief Get the depth map in camera[camIdx] of the photo. The depth map in camera[0] is the holefilled depth.
	@return The PXCImage instance.
	*/
	virtual PXCImage* PXCAPI QueryDepth(pxcI32 camIdx) = 0;
		
	/**
		@brief Get the depth map of the photo. This would be the processed depth if it undergoes processing.
	@return The PXCImage instance.
	*/

	__inline PXCImage* QueryDepth(void) { 
		return QueryDepth(0);
	}

	/**
	@brief Get the device revision. The revision of the XDM spec, e.g. “1.0”. 
	Changes to a minor version number ("1.1", "1.2", etc.) do not break compatibility within
	that major version. See the section on Versioning, under Schema, for more on this. Note 
	that this field is informational; actual compatibility depends on namespace versions.
	nchars input size of the requested buffer for overrun safety 
	@return The Revision string.
	*/
	virtual const pxcCHAR* PXCAPI QueryXDMRevision(void) = 0;

	/**
	@brief VendorInfo
	*/
	struct VendorInfo {
		pxcCHAR model[256];        //The model of the element that created the content
		pxcCHAR manufacturer[256]; //The manufacturer of the element that created the content
		pxcCHAR notes[256];		   //General comments
		pxcCHAR reserved[256];
	};

	/**
	@brief Get the device vendor info.
	@return The VendorInfo struct.
	*/
	virtual void PXCAPI QueryDeviceVendorInfo(VendorInfo &info) = 0;

	/**
	@brief Get the camera[camIdx] vendor info.
	@return The VendorInfo struct.
	*/
	virtual void PXCAPI QueryCameraVendorInfo(pxcI32 camIdx, VendorInfo &info) = 0;

	/**
	@brief Get the number of cameras in the device.
	@return The number of cameras.
	*/
	virtual pxcI32 PXCAPI QueryNumberOfCameras(void) = 0;

	/**
	@brief Get the camera[camIdx] pose = Translation and rotation.
	@return The translation(x,y,z) For the first camera, this is 0. For additional cameras, 
	this is relative to the first camera. For the rotation(x,y,z,w) writers of this format 
	should make an effort to normalize [x,y,z], but readers should not expect the rotation axis to be normalized..
	trans->x = x position in meters. 
	trans->y = y position in meters.
	trans->z = z position in meters.
	rot->x = x component of the rotation vector in the axis-angle representation.
	rot->y = y component of the rotation vector in the axis-angle representation.
	rot->z = z component of the rotation vector in the axis-angle representation.
	rot->w = w rotation angle in radians in the axis-angle representation.
	*/
	virtual void PXCAPI QueryCameraPose(pxcI32 camIdx, PXCPoint3DF32 &translation, PXCPoint4DF32 &rotation) = 0;

	/**
	@brief PerspectiveModel.
	*/
	struct PerspectiveCameraModel{
		pxcCHAR model[256];
		PXCPointF32 focalLength; //(ƒx,fy) / max(width, height) in pixels
		PXCPointF32 principalPoint; //(px/width,py/height) in pixels
		pxcF32 skew; //The skew of the image camera in degrees
		pxcF32 radialDistortion[3]; //k1,k2,k3
		pxcF32 tangentialDistortion[2]; //p1,p2
		pxcF32 reserved[6];
	};

	/**
	@brief Get the PerspectiveCameraModel of camera[camIdx].
	@return The PerspectiveCameraModel
	*/
	virtual void PXCAPI QueryCameraPerspectiveModel(pxcI32 camIdx, PerspectiveCameraModel &model) = 0;

	/** 
	@brief This checks that the signature of the container image did not change. 
	Using Adler 32 for signature. 
	@returns false if signature changed and true otherwise
	*/
	virtual bool PXCAPI CheckSignature() = 0;

	/**
	@brief Increase a reference count of the sample.
	*/
	__inline void AddRef(void) {
		QueryInstance<PXCAddRef>()->AddRef();
	}
};
