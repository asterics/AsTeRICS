/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or non-disclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013-2016 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCHandModule.h
    Defines the PXCHandModule interface, which gives access the hand module's configuration and output data.
 */
#pragma once
#include "pxcbase.h"


class PXCHandConfiguration;
class PXCHandData;



/**
    @Class PXCHandModule 
    The main interface to the hand module's classes.\n
    Use this interface to access the hand module's configuration and output data.
*/
class PXCHandModule : public PXCBase 
{
public:

    PXC_CUID_OVERWRITE(PXC_UID('H','A','N','N'));


	/** 
    @brief Create a new instance of the hand module's active configuration.
    Multiple configuration instances can be created in order to define different configurations for different stages of the application.
    You can switch between the configurations by calling the ApplyChanges method of the required configuration instance.
    @return A pointer to the configuration instance.
    @see PXCHandConfiguration
    */
    virtual PXCHandConfiguration* PXCAPI CreateActiveConfiguration() = 0;

    /** 
    @brief Create a new instance of the hand module's current output data.
    Multiple instances of the output can be created in order to store previous tracking states. 
    @return A pointer to the output data instance.
    @see PXCHandData
    */
    virtual PXCHandData* PXCAPI CreateOutput() = 0;


	

};
