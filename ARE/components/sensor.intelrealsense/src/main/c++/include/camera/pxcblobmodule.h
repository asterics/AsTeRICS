/******************************************************************************
	INTEL CORPORATION PROPRIETARY INFORMATION
	This software is supplied under the terms of a license agreement or nondisclosure
	agreement with Intel Corporation and may not be copied or disclosed except in
	accordance with the terms of that agreement
	Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.
*******************************************************************************/
#pragma once
#include "pxcblobconfiguration.h"
#include "pxcblobdata.h"

/**
	@Class PXCBlobModule 
	@brief The main interface to the blob module's classes.
	
	The blob module allows you to extract "blobs" (silhouettes of objects identified by the sensor) and their contour lines.
	Use the PXCBlobModule interface to access to the module's configuration and blob and contour line data.
*/
class PXCBlobModule : public PXCBase 
{
public:

	PXC_CUID_OVERWRITE(PXC_UID('B','M','M','D'));

	/** 
	@brief Create a new instance of the blob module's active configuration.
	Use the PXCBlobConfiguration object to examine the current configuration or to set new configuration values.
	@return A pointer to the configuration instance.
	@see PXCBlobConfiguration
	*/
	virtual PXCBlobConfiguration* PXCAPI CreateActiveConfiguration() = 0;

	/** 
	@brief Create a new instance of the blob module's output data (extracted blobs and contour lines).
	@return A pointer to a PXCBlobData instance.
	@see PXCBlobData
	*/
	virtual PXCBlobData* PXCAPI CreateOutput() = 0;
};
