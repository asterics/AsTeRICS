/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2015-2016 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/** 
@file pxcobjectrecognitiondata.h
@Defines the PXCObjectRecognitionData interface, which programs may use to process snapshots of captured frames
@to recognize pre-trained objects.
 */

#pragma once
#include "pxcimage.h"

/**
This class defines the standard interface for object recognition algorithms.
*/
class PXCObjectRecognitionData: public PXCBase
{
public:
    /* Constants */
    PXC_CUID_OVERWRITE(PXC_UID('O', 'B', 'J', 'D'));
    PXC_DEFINE_CONST(MAX_OJBECT_NAME_SIZE,256); 

    struct RecognizedObjectData
    {
        pxcI32 label;
        pxcF32 probability;
        PXCRectI32 roi;
        PXCPointF32 centerPos2D;
        PXCPoint3DF32 centerPos3D;
        PXCBox3DF32 boundingBox;
    };
    
        
    /** 
    @brief Query the number of recognized objects in the current frame.
    @note the number of recognized objects is between 0 and the number of classes multiplied by the number of ROIs.
     This function returns the sum of all objects at all ROIs up to the specified threshold (by configuration class).
    @return the number of successfully recognized objects.
    */
    virtual pxcI32 PXCAPI QueryNumberOfRecognizedObjects() const = 0;

    /** 
    @brief Query object data of the specified index with the highest probability.
    @note the index is between 0 and QueryNumberOfRecognizedObjects() in the current frame.
    @see PXCObjectRecognitionData::QueryNumberOfRecognizedObjects()
    
    @param[in]  index - the index of recognition.
    @param[out] objectData - data structure filled with the data of the recognized object.
    
    @return PXC_STATUS_NO_ERROR - operation succeeded.
    @return PXC_STATUS_PARAM_UNSUPPORTED on error.
    */    
    inline virtual pxcStatus PXCAPI QueryRecognizedObjectData(const pxcI32 index, RecognizedObjectData& objectData) const
    {
        return QueryRecognizedObjectData(index,0,objectData);
    }

    /** 
    @brief Query object data of the specified index.
    @note the index is between 0 and QueryNumberOfRecognizedObjects() in the current frame.
    @see PXCObjectRecognitionData::QueryNumberOfRecognizedObjects()
    @param[in]  index - the index of recognition.
    @param[in]  probIndex - the index of probabilty per the recognized object.
    @param[out] objectData - output parameter for data of the recognized object.     
    @return PXC_STATUS_NO_ERROR - operation succeeded.
    @return PXC_STATUS_PARAM_UNSUPPORTED where the index exceeds the number of probabilities   
    */    
    virtual pxcStatus PXCAPI QueryRecognizedObjectData(const pxcI32 index, const pxcI32 probIndex, RecognizedObjectData& objectData) const = 0;
    
    /** 
    @brief Query the segmented image of the selected roi index. 
    @param[in] roiIndex - the index of the ROI the segmented mask will be retrived.
    @param[out] segmentedImage - the segmentaion image as the output parameter.   
    @return PXC_STATUS_NO_ERROR - operation succeeded.
    @reserved 
    */    
    virtual pxcStatus PXCAPI QuerySegmentedImage(const pxcI32 roiIndex, PXCImage* & segmentedImage) const = 0;

    /**
    @brief Updates object recognition data to the most current output.
    */
    virtual pxcStatus PXCAPI Update() = 0;

    /** 
    @brief Return the name of the object by its recognized label.
    @param[in] objectLabel - the label of recognized object.
    @return the corresponding name of the label.
    */    
    virtual const pxcCHAR* PXCAPI QueryObjectNameByID(pxcI32 objectLabel) const = 0;
};