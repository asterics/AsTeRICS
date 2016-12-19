/*******************************************************************************                                                                                                                                                                                                                          /*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCPointConverter.h
    Defines the PXCPointConverter interface, which allows to convert 2D/3D data points.
 */
#pragma once
#include "pxcbase.h"
#include "pxcpointconverter.h"
#include "pxchanddata.h"
#include "pxcblobdata.h"

/** @class PXCPointConverter 
	Factory class for creating module based point converter
*/
class PXCPointConverterFactory: public PXCBase 
{
public:

	/* Constants */
	PXC_CUID_OVERWRITE(PXC_UID('P','P','C','R')); 
	
	
	/** @brief Create hand joint data PointConverter for PXCHandModule
		The converter will convert joint position to target rectangle/3dbox based on requested hand.
		@note Make sure the handData is constantly updated throughtout the session.
		@example pointConverter->CreateHandJointConverter(handData,PXCHandData::ACCESS_ORDER_BY_TIME,0,PXCHandData::JOINT_WRIST);
		@param[in] handData a pointer to PXCHandData
		@param[in] accessOrder The desired hand access order
		@param[in] index hand index
		@param[in] jointType desired joint type to be converted
		@return pointer to the created PXCPointConverter, or NULL in case of illegal arguments
	*/
	virtual PXCPointConverter*	PXCAPI CreateHandJointConverter(PXCHandData* handData,PXCHandData::AccessOrderType accessOrder,pxcI32 index,PXCHandData::JointType jointType) = 0;

	/** @brief Create hand Extremity data PointConverter for PXCHandModule
		The converter will convert extremity data to target rectangle/3dbox based on requested hand.
		@note Make sure the handData is constantly updated throughtout the session.
		@example pointConverter->CreateHandExtremityConverter(handData,PXCHandData::ACCESS_ORDER_BY_TIME,0,PXCHandData::EXTREMITY_CENTER);
		@param[in] handData a pointer to PXCHandData
		@param[in] accessOrder The desired hand access order
		@param[in] index hand index
		@param[in] extremityType desired extremity type to be converted
		@return pointer to the created PXCPointConverter, or NULL in case of illegal arguments
	*/
	virtual PXCPointConverter*	PXCAPI CreateHandExtremityConverter(PXCHandData* handData,PXCHandData::AccessOrderType accessOrder,pxcI32 index,PXCHandData::ExtremityType extremityType) = 0;

	/** @brief Create blob data PointConverter for PXCBlobModule
		The converter will convert exrtemity data to target rectangle/3dbox based on requested access order and index.
		@note Make sure the blobData is constantly updated throughtout the session.
		@example pointConverter->CreateBlobPointConverter(blobData,PXCBlobData::ACCESS_ORDER_BY_TIME,0,PXCBlobData::EXTREMITY_CENTER);
		@param[in] blobData a pointer to PXCBlobData
		@param[in] accessOrder The desired blob access order
		@param[in] index blob index
		@param[in] extremityType desired exrtemity point to be converted
		@return pointer to the created PXCPointConverter, or NULL in case of illegal arguments
	*/
	virtual PXCPointConverter*	PXCAPI CreateBlobPointConverter(PXCBlobData* blobData,PXCBlobData::AccessOrderType accessOrder,pxcI32 index,PXCBlobData::ExtremityType extremityType) = 0;

	/** @brief Create custom PointConverter
		The converter will convert any data point to target rectangle/3dbox
		@note make sure to call Set2DPoint or Set3DPoint
		@example pointConverter->CreateCustomPointConverter();
		PXCPointF32 point = {22.f,40.f};
		pointConverter->Set2DPoint(point);
		pointConveter->GetConverted2DPoint();
		@return pointer to the created PXCPointConverter
	*/
	virtual PXCPointConverter*	PXCAPI CreateCustomPointConverter() = 0;
};
