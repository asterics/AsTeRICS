
/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2012-2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file pxccalibration.h
Defines the PXCCalibration interface, which is used to retrieve the calibration data.
*/
#pragma once
#include "pxccapture.h"

class PXCCalibration : public PXCBase {

public:
	PXC_CUID_OVERWRITE(0x494A8538);

	struct StreamTransform
	{
		pxcF32 translation[3];   /* The translation in mm of the camera coordinate system origin to the world coordinate system origin. The world coordinate system coincides with the depth camera coordinate system. */
		pxcF32 rotation[3][3];   /* The rotation of the camera coordinate system with respect to the world coordinate system. The world coordinate system coincides with the depth camera coordinate system. */
	};

	struct StreamCalibration
	{
		PXCPointF32 focalLength;    /* The sensor focal length in pixels along the x and y axes. The parameters vary with the stream resolution setting. */
		PXCPointF32 principalPoint; /*  The sensor principal point in pixels along the x and y axes. The parameters vary with the stream resolution setting. */
		pxcF32 radialDistortion[3];     /*  The radial distortion coefficients, as described by camera model equations. */
		pxcF32 tangentialDistortion[2]; /* The tangential distortion coefficients, as described by camera model equations. */
		PXCCapture::DeviceModel model; /* Defines the distortion model of the device - different device models may use different distortion models */
	};

	/**
	@brief Query camera calibration and transformation data for a sensor.
	@param[in]  streamType      The stream type which is produced by the sensor.
	@param[out] calibration     The intrinsics calibration parameters of the sensor.
	@param[out] transformation  The extrinsics transformation parameters from the sensor to the camera coordinate system origin.
	@return PXC_STATUS_NO_ERROR Successful execution.
	*/
	virtual pxcStatus PXCAPI QueryStreamProjectionParameters(PXCCapture::StreamType streamType, StreamCalibration *calibration, StreamTransform *transformation) = 0;

    class CalibrationEx : public PXCBase {
    public:
        PXC_CUID_OVERWRITE(0x708D3F6A);
        virtual pxcStatus PXCAPI QueryStreamProjectionParametersEx(PXCCapture::StreamType streamType, PXCCapture::Device::StreamOption options, StreamCalibration *calibration, StreamTransform *transformation) = 0;
    };

	/**
	@brief Query camera calibration and transformation data for a sensor according to user defined options.
	@param[in]  streamType      The stream type which is produced by the sensor.
	@param[in]  options         The options that selects specific calibration and transformation data which is produced by the sensor.
	@param[out] calibration     The intrinsics calibration parameters of the sensor.
	@param[out] transformation  The extrinsics transformation parameters from the sensor to the camera coordinate system origin.
	@return PXC_STATUS_NO_ERROR Successful execution.
	*/
    __inline pxcStatus PXCAPI QueryStreamProjectionParametersEx(PXCCapture::StreamType streamType, PXCCapture::Device::StreamOption options, StreamCalibration *calibration, StreamTransform *transformation)
    {
        CalibrationEx *calibrationEx = QueryInstance<CalibrationEx>();
		if (calibrationEx) return calibrationEx->QueryStreamProjectionParametersEx(streamType, options, calibration, transformation);
		if (options == PXCCapture::Device::STREAM_OPTION_ANY) return QueryStreamProjectionParameters(streamType, calibration, transformation);
		return PXC_STATUS_FEATURE_UNSUPPORTED;
    }
};
