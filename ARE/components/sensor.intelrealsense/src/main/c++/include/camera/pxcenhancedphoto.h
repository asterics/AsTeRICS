/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCEnhancedPhoto.h
	Defines the PXCEnhancedPhoto interface, which programs may use to process snapshots of captured frames
*/
#pragma once
#include "pxcphoto.h"
#include "pxcsession.h"

/**
	This class defines a standard interface for Enhanced Photography Algorithms.
*/
class PXCEnhancedPhoto: public PXCBase {

public:

	PXC_CUID_OVERWRITE(PXC_UID('E','P','I','N'));

	/**
		This class defines a standard interface for Enhanced Photography Depth Mask Generator Algorithms.
	*/
	class DepthMask: public PXCBase {
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','D','M'));

		_inline static DepthMask* CreateInstance(PXCSession* session){
			DepthMask *me=0;
			session->CreateImpl<DepthMask>(&me);
			return me;
		}

		struct MaskParams{
			pxcF32 frontObjectDepth;
			pxcF32 backOjectDepth;
			pxcF32 nearFallOffDepth;
			pxcF32 farFallOffDepth;
			pxcF32 reserved[4];

			MaskParams() {
				frontObjectDepth = -1;
				backOjectDepth = -1;
				nearFallOffDepth = -1;
				farFallOffDepth = -1;
			};
		};

		/**
			Init: the function initializes the Depth Mask function with the photo that needs processing. 
			photo: 2D+depth input image.
			returns PXCStatus.
		*/
		virtual pxcStatus PXCAPI Init(const PXCPhoto *photo) = 0;

		/** 
			ComputeFromThreshold: calculates a mask from the threshold computed 
			depthThreshold: depth threshold. 
			pxcF32 frontObjectDepth: foreground depth
			pxcF32 backOjectDepth: background depth
			pxcF32 nearFallOffDepth: ??
			pxcF32 farFallOffDepth ::?
			Returns a mask in the form of PXCImage for blending with the current photo frame.

			Notes:
			For every pixel, if the mask is between the range of [POIdepth - frontObjectDepth, POIdepth + backObjectDepth], mask[p] -1.
			For every pixel p with depth in the range [POI - frontObjectDepth - nearFalloffDepth, POI - frontObjectDepth], mask[p] equals the "smoothstep" function value.
			For every pixel p with depth in the range [POI  + backObjectDepth , POI + backOjectDepth + farFallOffDepth], mask[p] equals the "smoothstep" function value.
			For every pixel p with other depth value, mask[p] = 1.
		*/
		virtual PXCImage* PXCAPI ComputeFromThreshold(pxcF32 depthThreshold, MaskParams *maskParams) = 0;
		__inline PXCImage* ComputeFromThreshold(pxcF32 depthThreshold){

			MaskParams maskParams;
			return ComputeFromThreshold(depthThreshold, &maskParams);
		};

		/** 
			ComputeFromCoordinate: convenience function that creates a mask directly from a depth coordinate.
			coord: input (x,y) coordinates on the depth map.  
			Returns a mask in the form of PXCImage for blending with the current photo frame.
			Note: This function simply calls ComputeFromThreshold underneath.
		*/
		virtual PXCImage* PXCAPI ComputeFromCoordinate(PXCPointI32 coord, MaskParams *maskParams) = 0;
		__inline PXCImage* ComputeFromCoordinate(PXCPointI32 coord){
			MaskParams maskParams;
			return ComputeFromCoordinate(coord, &maskParams);
		};

	};

	/**
		This class defines a standard interface for Enhanced Photography Motion Effect Algorithms.
	*/
	class MotionEffect: public PXCBase {
	public:
		
		PXC_CUID_OVERWRITE(PXC_UID('E','P','M','E'));

		_inline static MotionEffect* CreateInstance(PXCSession* session){
			MotionEffect *me=0;
			session->CreateImpl<MotionEffect>(&me);
			return me;
		}
	
		/**
			Init: the function initializes the 6DoF parallax function with the photo that needs processing. 
			photo: 2D+depth input image.
			returns PXCStatus.
		*/
		virtual pxcStatus PXCAPI Init(const PXCPhoto *photo) = 0;

		/**
			Apply: the function applies a 6DoF parallax effect which is the difference in the apparent position of an object
			when it is viewed from two different positions or viewpoints. 

			motion[3]: is the right, up, and forward motion when (+ve), and Left, down and backward motion when (-ve)
			motion[0]: + right   / - left 
			motion[1]: + up      / - down
			motion[2]: + forward / - backward
			rotation[3]: is the Pitch, Yaw, Roll rotations in degrees in the Range: 0-360. 
			rotaion[0]: pitch 
			rotaion[1]: yaw
			rotaion[2]: roll
			zoomFactor: + zoom in / - zoom out
			PXCImage: the returned parallaxed image.
		*/
		virtual PXCImage* PXCAPI Apply(pxcF32 motion[3], pxcF32 rotation[3], pxcF32 zoomFactor) = 0;

	};
	
	/**
		This class defines a standard interface for Enhanced Photography Depth Refocus Algorithms.
	*/
	class DepthRefocus: public PXCBase{
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','D','R'));

		_inline static DepthRefocus* CreateInstance(PXCSession* session){
			DepthRefocus *dr=0;
			session->CreateImpl<DepthRefocus>(&dr);
			return dr;
		}

		/**
			Init: the function initializes the Depth Refocus function with the photo that needs processing. 
			photo: 2D+depth input image.
			returns PXCStatus.
		*/
		virtual pxcStatus PXCAPI Init(const PXCPhoto *photo) = 0;
		
		/**
			Apply: Refocus the image at input focusPoint by using depth data refocus
			focusPoint: is the selected point foir refocussing.
			aperture: Range of the blur area = focal length/f-number. approximate range [7, 160]  =  [f/22, f/1.1] 
			Note: The application must release the returned refocussed image
		*/
		virtual PXCPhoto* PXCAPI Apply(PXCPointI32 focusPoint, pxcF32 aperture) = 0;
			__inline PXCPhoto* Apply(PXCPointI32 focusPoint) { 
				return Apply(focusPoint, 50.0); }

	};

	/**
		This class defines a standard interface for Enhanced Photography Utility Algorithms.
	*/
	class PhotoUtils:public PXCBase {
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','U','T'));

		_inline static PhotoUtils* CreateInstance(PXCSession* session){
			PhotoUtils *me=0;
			session->CreateImpl<PhotoUtils>(&me);
			return me;
		}

		/** 
		Input param for Depth fill Quality: 
		High: better quality, slow execution for post processing (image)
		Low : lower quality, fast execution for realtime processing (live video sequence)
		*/
		enum DepthFillQuality {
			HIGH = 0, 
			LOW, 
		};
		
		/**
			EnhanceDepth: enhance the depth image quality by filling holes and denoising
			outputs the enhanced depth image
			photo: input color, depth photo, and calibration data
			depthQuality: Depth fill Quality: HIGH or LOW for post or realtime processing respectively
			Note: The application must release the returned enhanced depth image
		*/
		virtual PXCPhoto* PXCAPI EnhanceDepth(const PXCPhoto *photo, DepthFillQuality depthQuality)= 0;

		/** 
		DepthMapQuality: output param for Depth Map Quality: 
		BAD : ??,
		FAIR: ??,
		GOOD: ??
		*/
		enum DepthMapQuality
		{
			BAD = 0,
			FAIR = 1,
			GOOD = 2
		};

		/**
		DepthQuality: retruns the quality of the the depth map
		depthIm: input raw depth map
		depthQuality: BAD, FAIR, GOOD
		*/
		virtual DepthMapQuality PXCAPI GetDepthQuality(const PXCImage *depthIm)= 0;

		/** 
		CommonFOV: Matches the Feild Of View (FOV) of color and depth in the photo. Useful for still images.
		photo: input image color+depth
		Returns a photo with primary,unedited color images, and depthmaps cropped to the 
		common FOV and the camera meatadata recalculated accordingly.
		Note: Returns a nullptr if function fails
		*/
		virtual PXCPhoto* PXCAPI CommonFOV(const PXCPhoto *photo) = 0;

		/**
		PreviewCommonFOV: Matches the Field of View (FOV) of color and depth in depth photo. Useful for live stream.
		Use the returned roi to crop the photo

		photo: input image color+depth
		outRect: Output. Returns roi in color image that matches to FOV of depth image that is suitable for all photos in the live stream.

		 @return pxcStatus : PXC_STATUS_NO_ERRROR for successfu operation; PXC_STATUS_DATA_UNAVAILABLE otherwise
		*/
		virtual pxcStatus PXCAPI PreviewCommonFOV(const PXCPhoto *photo, PXCRectI32 *outRect) = 0;


		/** 
		Crop: The primary image, the camera[0] RGB and depth images are cropped 
		and the intrinsic / extrinsic info is updated.
		photo: input image color+depth
		rect : top left corner (x,y) plus width and height of the window to keep 
		and crop all the rest
		Returns a photo that has all its images cropped and metadata fixed accordingly.
		Note: Returns a nullptr if function fails
		*/
		virtual PXCPhoto* PXCAPI PhotoCrop(const PXCPhoto *photo, PXCRectI32 rect) = 0;

		/** 
		UpScaleDepth: Change the size of the enhanced depth map. 
		This function preserves aspect ratio, so only new width is required.
		photo: input image color+depth
		width: the new width.
		enhancementType: if the inPhoto has no enhanced depth, then do this type of depth enhancement before resizing.
		Returns a Depth map that has the same aspect ratio as the color image resolution.
		Note: Returns a nullptr if the aspect ratio btw color and depth is not preserved
		*/
		virtual PXCPhoto* PXCAPI DepthResize(const PXCPhoto *photo, pxcI32 width, DepthFillQuality enhancementType) = 0;
		__inline PXCPhoto* DepthResize(const PXCPhoto *photo, pxcI32 width) { 
			return DepthResize(photo, width, DepthFillQuality::HIGH);
		}

		/** 
		PhotoResize: Change the size of the reference (primary) image. 
		This function preserves aspect ratio, so only new width is required.
		Only the primary image is resized.
		photo - input photo.
		width - the new width.
		Returns a photo with the reference (primary) color image resized while maintaining the aspect ratio.
		Note: Returns a nullptr when the fcn fails
		*/
		virtual PXCPhoto* PXCAPI ColorResize(const PXCPhoto *photo, pxcI32 width) = 0;

		/**
		PhotoRotate: rotates a Photo (color, depth and metadata).
		this function rotates the primary image, the RGB and depth images in camera 0, and updates 
		the corresponding intrinsic/extrinsic info.
		photo: input photo.
		degrees: the angle of rotation around the center of the color image in degrees +/- sign for clockwise and counterclockwise.
		Returns a rotated photo.
		Note: Returns a nullptr when the function fails
		*/	
		virtual PXCPhoto* PXCAPI PhotoRotate(const PXCPhoto *photo, pxcF32 degrees) = 0;
	};

	/**
		This class defines a standard interface for Enhanced Photography Segmentation Algorithms.
	*/
	class Segmentation:public PXCBase {
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','S','G'));

		_inline static Segmentation* CreateInstance(PXCSession* session){
			Segmentation *me=0;
			session->CreateImpl<Segmentation>(&me);
			return me;
		}

		/** 
		ObjectSegment: generates an initial mask for any object selected by the bounding mask. 
		The mask can then be refined by hints supplied by the user in RefineMask() function. 
		photo: input color and depth photo.
		inMask : a mask signaling the foreground or the object to be segmented. the pixels of the object should be set to 255.  
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual PXCImage* PXCAPI ObjectSegment(const PXCPhoto *sample, const PXCImage *inMask) = 0;
			
		/** 
		RefineMask: refines the mask generated by the ObjectSegment() function by using hints.
		points: input arrays with hints' coordinates.
		length: length of the array
		isForeground: bool set to true if input hint locations are foreground and false if background
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual PXCImage* PXCAPI RefineMask(const PXCPointI32* points, pxcI32 length, bool isForeground) = 0;
		
		/** 
		Undo: undo last hints.
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual PXCImage* PXCAPI Undo() = 0;
		
		/** 
		Redo: Redo the previously undone hint.
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual PXCImage* PXCAPI Redo() = 0;

		/** 
		ObjectSegment: generates an initial mask for any object selected by the bounding box. 
		The mask can then be refined by hints supplied by the user in RefineMask() function. 
		photo: input color and depth photo.
		topLeftCoord    : top left corner of the object to segment.  
		bottomRightCoord: Bottom right corner of the object to segment.
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual __declspec(deprecated("Use ObjectSegment instead")) PXCImage* PXCAPI ObjectSegmentDeprecated(const PXCPhoto *photo, PXCPointI32 topLeftCoord, PXCPointI32 bottomRightCoord) = 0;
		_inline PXCImage* ObjectSegment(const PXCPhoto *photo, PXCPointI32 topLeftCoord, PXCPointI32 bottomRightCoord){
			return ObjectSegmentDeprecated(photo, topLeftCoord, bottomRightCoord);
		}

		/** 
		RefineMask: refines the mask generated by the ObjectSegment() function by using hints.
		hints: input mask with hints. hint values.
		0 = no hint
		1 = foreground
		2 = background
		Returns a mask in the form of PXCImage with detected pixels set to 255 and undetected pixels set to 0.
		*/
		virtual __declspec(deprecated("Use RefineMask instead")) PXCImage* PXCAPI RefineMaskDeprecated(const PXCImage *hints) = 0;
		_inline PXCImage* RefineMask(const PXCImage *hints){
			return RefineMaskDeprecated(hints);
		}
	};

	/**
		This class defines a standard interface for Enhanced Photography Paster Algorithms.
	*/
	class Paster: public PXCBase {
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','P','P'));

		_inline static Paster* CreateInstance(PXCSession* session){
			Paster *me=0;
			session->CreateImpl<Paster>(&me);
			return me;
		}

		/** 
		PasteEffects:  
		matchIllumination: flag to match Illumination, default value is true		
		transparency: (default) 0.0f = opaque, 1.0f = transparent sticker
		embossHighFreqPass: High Frequency Pass during emboss, default 0.0f no emboss, 1.0f max	
		byPixelCorrection: default false, flag to use by pixel illumination correction, takes shadows in account
		colorCorrection: default false, flag to add color correction		
		*/
		struct PasteEffects {
			pxcBool matchIllumination; // flag to match Illumination, default value is true		
			pxcF32 transparency;       // (default) 0.0f = opaque, 1.0f = transparent sticker
			pxcF32 embossHighFreqPass; // High Frequency Pass during emboss, default 0.0f no emboss, 1.0f max	
			pxcBool shadingCorrection;   // default false, flag to use pixel illumination correction, takes shadows in account
			pxcBool colorCorrection;     // default false, flag to add color correction		
			pxcF32 reserved[6];

			PasteEffects(){
				matchIllumination = true;
				transparency = 0.0;
				embossHighFreqPass = 0.0;	
				shadingCorrection = false;
				colorCorrection = false;
			};
		};

		/** 
		SetPhoto: sets the photo that needs to be processed.
		photo: photo to be processed [color + depth] 
		Returns PXC_STATUS_NO_ERROR if success. PXC_STATUS_PROCESS_FAILED if process failed
		*/
		virtual pxcStatus PXCAPI SetPhoto(const PXCPhoto *photo) = 0;
		
		/**
		GetPlanesMap: return plane indices map for current SetPhoto
		Returns a PXCImage of the plane indices in a form of a mask.
		*/
		virtual PXCImage* PXCAPI GetPlanesMap() = 0;

		/** 
		StickerData:  
		coord : insertion coordinates
		height: sticker height in mm, default -1 auto-scale		
		rotation: in-plane rotation in degree, default 0	
		isCenter: Anchor point. False means coordinate is top left, true means coordinate is center.
		*/
		struct StickerData {
			pxcF32  height; 
			pxcF32  rotation;
			pxcBool isCenter;
			pxcF32 reserved[6];

			StickerData(){
				height = -1.0f;
				rotation = 0.0f;	
				isCenter = false;
			};
		};

		/** 
		SetSticker: sets the sticker that will be pasted with all configurations needed and paste effects.
		sticker: the image to paste onto the photo (foreground image)
		coord : insertion coordinates
		stickerData: the sticker size, paste location and anchor point.
		pasteEffects: the pasting effects.
		Returns PXC_STATUS_NO_ERROR if success. PXC_STATUS_PROCESS_FAILED if process failed
		*/
		virtual pxcStatus PXCAPI SetSticker(PXCImage* sticker, PXCPointI32 coord, StickerData *stickerData, PasteEffects* pasteEffects) = 0;
		_inline pxcStatus PXCAPI SetSticker(PXCImage* sticker, PXCPointI32 coord, StickerData *stickerData){
			PasteEffects pasteEffects;
			return SetSticker(sticker, coord, stickerData, &pasteEffects);
		}
		_inline pxcStatus PXCAPI SetSticker(PXCImage* sticker, PXCPointI32 coord){
			PasteEffects pasteEffects;
			StickerData  stickerData;
			return SetSticker(sticker, coord, &stickerData, &pasteEffects);
		}

		/** 
		PreviewSticker: returns a sticker mask showing the location of the pasted sticker.
		Returns a PXCImage of the previewed sticker in a form of a mask.
		*/
		virtual PXCImage* PXCAPI PreviewSticker() = 0;

		/** 
		Paste: pastes a smaller 2D image (sticker) onto a bigger color + depth image (background).
		The smaller foreground image, is rendered according to a
		user-specified position and an auto-detected plane orientation onto the background image.
		The auto-oriented foreground image and the color data of the background image are composited together
		according to the alpha channal of the foreground image.

		Returns the embeded foreground with background image.
		*/
		virtual PXCPhoto* PXCAPI Paste() = 0;
		
		/** 
		PasteOnPlane: This function is provided for texturing a smaller 2D image (foreground)
		onto a bigger color + depth image (background). The smaller foreground image, is rendered according to a
		user-specified position and an auto-detected plane orientation onto the background image.
		The auto-oriented foreground image and the color data of the background image are composited together
		according to the alpha channal of the foreground image.

		embedIm: the image to embed in the photo (foreground image)
		topLeftCoord, bottomLeftCoord: are the top left corner and the bottom left corner of where the user wants to embed the image.
		Returns the embeded foreground with background image.
		*/
		virtual __declspec(deprecated("Use PasteOnPlane instead")) PXCPhoto* PXCAPI PasteOnPlaneDeprecated(const PXCPhoto *photo, PXCImage* embedIm, PXCPointI32 topLeftCoord, PXCPointI32 bottomLeftCoord, PasteEffects* pasteEffects) = 0;
		_inline PXCPhoto* PXCAPI PasteOnPlane(const PXCPhoto *photo, PXCImage* embedIm, PXCPointI32 topLeftCoord, PXCPointI32 bottomLeftCoord, PasteEffects* pasteEffects){
			return PasteOnPlaneDeprecated(photo, embedIm, topLeftCoord, bottomLeftCoord, pasteEffects);
		}

		/** 
		PasteOnPlane: This function is provided for texturing a smaller 2D image (foreground)
		onto a bigger color + depth image (background). The smaller foreground image, is rendered according to a
		user-specified position and an auto-detected plane orientation onto the background image.
		The auto-oriented foreground image and the color data of the background image are composited together
		according to the alpha channal of the foreground image.

		embedIm: the image to embed in the photo (foreground image)
		topLeftCoord, bottomLeftCoord: are the top left corner and the bottom left corner of where the user wants to embed the image.
		Returns the embeded foreground with background image.
		*/
		_inline PXCPhoto* PXCAPI PasteOnPlane(const PXCPhoto *photo, PXCImage* embedIm, PXCPointI32 topLeftCoord, PXCPointI32 bottomLeftCoord){
			PasteEffects pasteEffects;
			return PasteOnPlaneDeprecated(photo, embedIm, topLeftCoord, bottomLeftCoord, &pasteEffects);
		}

	};
	
	/**
	EXPERIMENTAL: This class defines a standard interface for Enhanced Photography Measurement Algorithms.
	*/
	class Measurement: public PXCBase {
	public:

		PXC_CUID_OVERWRITE(PXC_UID('E','P','M','D'));

		_inline static Measurement* CreateInstance(PXCSession* session){
			Measurement *me=0;
			session->CreateImpl<Measurement>(&me);
			return me;
		}

		/**	DistanceType: indicator whether the Two points measured lie on a the same planar surface */
		enum DistanceType {
			UNKNOWN = 0,
			COPLANAR,
			NONCOPLANAR, 
		};
		
		/** This represents a point in 3D world space in millimeter (mm) units. */
		struct WorldPoint {
			PXCPoint3DF32 coord; /**< Coordinates in mm. */
			pxcF32 confidence;   /**< Confidence for this point. The confidence ranges from 0.0 (no confidence) to 1.0 (high confidence). This should be set to NaN if confidence is not available. */ 
			pxcF32 precision;    /**< Precision for this point. Precision is given in mm and represents the percision of the depth value at this point in 3D space. This should be set to NaN if precision is not available. */
			pxcF32 reserved[6];
		};

		/** This represents the distance between two world points in millimeters (mm). */
		struct MeasureData {
			pxcF32 distance;       /**< The distance measured in mm. */
			pxcF32 confidence;     /**< Confidence for this point. The confidence ranges from 0.0 (no confidence) to 1.0 (high confidence). This should be set to NaN if confidence is not available. */ 
			pxcF32 precision;      /**< Precision for this point. Precision is given in mm and represents the percision of the depth value at this point in 3D space. This should be set to NaN if precision is not available. */
			WorldPoint startPoint; /**< The first of the two points from which the distance is measured. */
			WorldPoint endPoint;   /**< The second of the two points from which the distance is measured. */
			DistanceType distType; /**< Provides indication whether both points were detected lie on a planar surface*/
			pxcF32 reserved[6];
		};

		/** 
		MeasureDistance: measure the distance between 2 points in mm
		photo: is the photo instance
		startPoint, endPoint: are the start pont and end point of the distance that need to be measured.
		Note: depth data must be availible and accurate at the start and end point selected. 
		*/
		virtual pxcStatus PXCAPI MeasureDistance(const PXCPhoto *photo, PXCPointI32 startPoint, PXCPointI32 endPoint, MeasureData *outData) = 0;
	
		/** 
		MeasureUADistance: (Experimental) measure the distance between 2 points in mm by using a experimental algortihm for a User Assisted (UA) measure.
		photo: is the photo instance
		startPoint, endPoint: are the user selected start point and end point of the distance that needs to be measured.
		returns the MeasureData that has the highest confidence value.  
		Note: depth data must be available and accurate at the start and end point selected. 
		*/
		virtual pxcStatus PXCAPI MeasureUADistance(const PXCPhoto *photo, PXCPointI32 startPoint, PXCPointI32 endPoint, MeasureData *outData) = 0;
	
		/** 
		QueryUADataSize: (Experimental) returns the size of the MeasureData possibilites. The number of possibilities varries according 
		to the selected points, if they lie on a common plane or independent planes.
		*/
		virtual pxcI32 PXCAPI QueryUADataSize() = 0;

		/** 
		QueryUAData: (Experimental) returns an array of the MeasureData possibilites. the size of the array is equal to the value returned
		by the QueryUADataSize(). the array needs to be allocated and deallocated by the user.
		*/
		virtual pxcStatus PXCAPI QueryUAData(MeasureData *outDataArr) = 0;
	
	};
};