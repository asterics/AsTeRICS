/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2015-2016 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/** 
@file pxcobjectrecognitionmodule.h
@Defines the PXCObjectRecognitionModule interface, which programs may use to process snapshots of captured frames
@to recognize pre-trained objects.
*/

#include "pxcobjectrecognitiondata.h"
#include "pxcobjectrecognitionconfiguration.h"

/**
@Class PXCObjectRecognitionModule 
The main interface to the ObjectRecognition module's classes.
Use this interface to access the ObjectRecognition module's configuration and output data.
*/
class PXCObjectRecognitionModule :public PXCBase
{
public:
    PXC_CUID_OVERWRITE(PXC_UID('O', 'B', 'J', 'M'));
      
    /** 
    @brief Create a new instance of the ObjectRecognition module's current output data.
    Multiple instances of the output can be created in order to store previous tracking states. 
    @return a PXCObjectRecognitionData instance.
    @see PXCObjectRecognitionData 
    */
    virtual PXCObjectRecognitionData* PXCAPI CreateOutput() const =0; 
    
    /** 
    @brief Create a new instance of the ObjectRecognition module's active configuration.
    Multiple configuration instances can be created in order to define different configurations for different stages of the application.
    The configurations can be switched by calling the ApplyChanges method of the required configuration instance.
    @return a PXCObjectRecognitionConfiguration instance.
    @see PXCObjectRecognitionConfiguaration
    */
    virtual PXCObjectRecognitionConfiguration* PXCAPI CreateActiveConfiguration() const =0;

    
};