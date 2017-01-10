/******************************************************************************
    INTEL CORPORATION PROPRIETARY INFORMATION
    This software is supplied under the terms of a license agreement or nondisclosure
    agreement with Intel Corporation and may not be copied or disclosed except in
    accordance with the terms of that agreement
    Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.
*******************************************************************************/
#pragma once
#include "pxcimage.h"

/**
    @Class PXCBlobData 
    @brief A class that contains extracted blob and contour line data.
    The extracted data refers to the sensor's frame image at the time PXCBlobModule.CreateOutput() was called.
*/
class PXCBlobData: public PXCBase
{
public:

    /* Constants */
    
    PXC_CUID_OVERWRITE(PXC_UID('B','M','D','T'));
    PXC_DEFINE_CONST(NUMBER_OF_EXTREMITIES,6);
    
    /* Enumerations */

    /** 
    @enum AccessOrderType
    @brief Each AccessOrderType value indicates the order in which the extracted blobs can be accessed.
    Use one of these values when calling QueryBlobByAccessOrder().
    */
    enum AccessOrderType {
        /// From the nearest to the farthest blob in the scene   
        ACCESS_ORDER_NEAR_TO_FAR = 0
        , ACCESS_ORDER_LARGE_TO_SMALL /// From the largest to the smallest blob in the scene           
        , ACCESS_ORDER_RIGHT_TO_LEFT /// From the right-most to the left-most blob in the scene           
    };

    
    /**
        @enum ExtremityType
        @brief The identifier of an extremity point of the extracted blob.
        6 extremity points are identified (see values below).\n
        Use one of the extremity types when calling IBlob.QueryExtremityPoint().
    */
    enum ExtremityType {
        /// The closest point to the sensor in the tracked blob
        EXTREMITY_CLOSEST=0 
        , EXTREMITY_LEFT_MOST       /// The left-most point of the tracked blob
        , EXTREMITY_RIGHT_MOST      /// The right-most point of the tracked blob 
        , EXTREMITY_TOP_MOST		/// The top-most point of the tracked blob
        , EXTREMITY_BOTTOM_MOST     /// The bottom-most point of the tracked blob
        , EXTREMITY_CENTER          /// The center point of the tracked blob            
    };

    /**
        @enum SegmentationImageType
		@brief Each SegmentationImageType value indicates the extracted blobs data mapping.
		Use one of these values when calling QueryBlob().
    */
    enum SegmentationImageType
    {
		/// The blob data mapped to depth image
        SEGMENTATION_IMAGE_DEPTH = 0
        ,SEGMENTATION_IMAGE_COLOR	/// The blob data mapped to color image
    };

    

    /* Interfaces */

	/** 
        @class IContour
        @brief An interface that provides access to the contour line data.
        A contour is represented by an array of 2D points, which are the vertices of the contour's polygon.
    */
    class IContour
    {
    public:
        /** 
            @brief Get the contour size (number of points in the contour line).
            This is the size of the points array that you should allocate.
            @return The contour size (number of points in the contour line).
        */
        virtual pxcI32 PXCAPI QuerySize() const = 0;    

        /** 
        @brief Get the point array representing a contour line.
                
        @param[in] maxSize - the size of the array allocated for the contour points.
        @param[out] contour - the contour points stored in the user-allocated array.
        
        @return PXC_STATUS_NO_ERROR - successful operation.        
        */
        virtual pxcStatus PXCAPI QueryPoints(const pxcI32 maxSize, PXCPointI32* contour) = 0;    
        
        /** 
            @brief Return true for the blob's outer contour; false for inner contours.
            @return true for the blob's outer contour; false for inner contours.
        */
        virtual pxcBool PXCAPI IsOuter() const = 0;
    };



    /** 
        @class IBlob
        @brief An interface that provides access to the blob and contour line data.
    */
    class IBlob
    {
    public:
    
    /**            
            @brief Retrieves the 2D segmentation image of a tracked blob.      
            In the segmentation image, each pixel occupied by the blob is white (value of 255) and all other pixels are black (value of 0).
            @param[out] image - the segmentation image of the tracked blob.
            @return PXC_STATUS_NO_ERROR - successful operation.
            @return PXC_STATUS_DATA_UNAVAILABLE - the segmentation image is not available.               
    */    
    virtual pxcStatus PXCAPI QuerySegmentationImage(PXCImage* & image) const = 0; 
    
    
        
    /**         
        @brief Get an extremity point location using a specific ExtremityType.
        @param[in] extremityLabel - the extremity type to be retrieved.
        @return The extremity point location data.        
        @see ExtremityType       
    */
    virtual PXCPoint3DF32 PXCAPI QueryExtremityPoint(ExtremityType extremityLabel) const = 0; 
    

    /** 
        @brief Get the number of pixels in the blob.
        @return The number of pixels in the blob.
    */
    virtual pxcI32 PXCAPI QueryPixelCount() const = 0; 
    
    
    
    /** 
            @brief Get the number of contour lines extracted (both external and internal).
            @return The number of contour lines extracted.
    */
    virtual pxcI32 PXCAPI QueryNumberOfContours() const = 0;


	/**
        @brief Retrieve an IContour object using index (that relates to the given order).
        @param[in] index - the zero-based index of the requested contour (between 0 and QueryNumberOfContours()-1 ).
		@param[out] contourData - contains the extracted contour line data.
        
        @return PXC_STATUS_NO_ERROR - successful operation.
        @return PXC_STATUS_DATA_UNAVAILABLE  - index >= number of detected contours. 

		@see IContour       
    */
	virtual pxcStatus PXCAPI QueryContour(const pxcI32 index, IContour*& contourData) const = 0;
	

	inline pxcStatus PXC_DEPRECATED("Deprecated. Use QueryContour instead.") QueryContourPoints(const pxcI32 index, const pxcI32 maxSize, PXCPointI32* contour) { IContour* c; if(QueryContour(index,c)==pxcStatus::PXC_STATUS_NO_ERROR){ return c->QueryPoints(maxSize, contour);} else {return  PXC_STATUS_ITEM_UNAVAILABLE;}} 
	inline pxcBool   PXC_DEPRECATED("Deprecated. Use QueryContour instead.") IsContourOuter(const pxcI32 index) { IContour* c; if(QueryContour(index,c)==pxcStatus::PXC_STATUS_NO_ERROR){ return c->IsOuter();}else { return false; }};
	inline pxcI32    PXC_DEPRECATED("Deprecated. Use QueryContour instead.") QueryContourSize(const pxcI32 index) const 
	{ 
		IContour* c;
		if(QueryContour(index,c)==pxcStatus::PXC_STATUS_NO_ERROR)
		{ return c->QuerySize();}
		else {return  0;} 
	}
    
   
    /**    
        @brief Return the location and dimensions of the blob, represented by a 2D bounding box (defined in pixels).
		@return The location and dimensions of the 2D bounding box.
    */
    virtual const PXCRectI32& PXCAPI QueryBoundingBoxImage() const = 0; 

	/**    
        @brief Return the location and dimensions of the blob, represented by a 3D bounding box.
		@return The location and dimensions of the 3D bounding box.
    */
	virtual const PXCBox3DF32& PXCAPI QueryBoundingBoxWorld() const= 0; 

    };    // class IBlob

public:
    /* General */

    /**
    @brief Updates the extracted blob data to the latest available output. 

	@return PXC_STATUS_NO_ERROR - successful operation.
	@return PXC_STATUS_DATA_NOT_INITIALIZED  - when the BlobData is not available. 
    */
    virtual pxcStatus PXCAPI Update() = 0;
    
    /* Blob module's Outputs */
    
    /** 
            @brief Get the number of extracted blobs.    
            @return The number of extracted blobs.
    */
    virtual pxcI32 PXCAPI QueryNumberOfBlobs(void) const = 0;
        
    /**
        @brief Retrieve an IBlob object using a specific AccessOrder and index (that relates to the given order).
        @param[in] index - the zero-based index of the requested blob (between 0 and QueryNumberOfBlobs()-1 ).
		@param[in] segmentationImageType - the image type which the blob will be mapped to. To get data mapped to color see PXCBlobConfiguration::EnableColorMapping.
        @param[in] accessOrder - the order in which the blobs are enumerated.
        @param[out] blobData - contains the extracted blob data.
        
        @return PXC_STATUS_NO_ERROR - successful operation.
        @return PXC_STATUS_PARAM_UNSUPPORTED - index >= configured maxBlobs.
        @return PXC_STATUS_DATA_UNAVAILABLE  - index >= number of detected blobs or blob data is invalid.  
        
        @see AccessOrderType
		@see SegmentationImageType
    */
	virtual pxcStatus PXCAPI QueryBlob(const pxcI32 index,SegmentationImageType segmentationImageType,  AccessOrderType accessOrderType, IBlob*& blobData) = 0;
	__inline pxcStatus PXC_DEPRECATED("Deprecated. Use QueryBlob instead.") QueryBlobByAccessOrder(const pxcI32 index, AccessOrderType accessOrderType, IBlob*& blobData) {return QueryBlob(index,SegmentationImageType::SEGMENTATION_IMAGE_DEPTH, accessOrderType, blobData);  }


};
