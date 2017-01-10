/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/

#pragma once
#include "sp_math_utils.h"
#include "pxcimage.h"
#include "pxcprojection.h"
#include "pxcsceneperception.h"

//! calculate orthographic matrix to be used with camera's intrinsic matrix
//! to derive camera projection matrix. 
inline AppUtils::Matrix4f CalculateOrthographicProjection(int imgWidth, int imgHeight, float nearClip, float farClip)
{
	AppUtils::Matrix4f orth;
	orth.m_data[0 * 4 + 0] = 2.0f / imgWidth;
	orth.m_data[0 * 4 + 3] = -1.0f;
	orth.m_data[1 * 4 + 1] = 2.0f / imgHeight;
	orth.m_data[1 * 4 + 3] = -1.0f;
	orth.m_data[2 * 4 + 2] = -2.0f / (farClip - nearClip);
	orth.m_data[2 * 4 + 3] = -(farClip + nearClip) / (farClip - nearClip);
	orth.m_data[3 * 4 + 3] = 1.0f;
	return orth;
}

//! derive camera's intrinsic parameter matrix based on 
//! focal lengths (fx, fy) and principle points (u, v).
inline AppUtils::Matrix4f SetUnityCamParamsMatrix(float alpha, float beta, float skew, float u0, float v0, float nearClip, float farClip)
{
	AppUtils::Matrix4f K;
	K.m_data[0 * 4 + 0] = alpha;//negative for mirroring
	K.m_data[0 * 4 + 2] = -u0;
	K.m_data[1 * 4 + 1] = beta;//negative for flipping
	K.m_data[1 * 4 + 2] = -v0;
	K.m_data[2 * 4 + 2] = nearClip + farClip;
	K.m_data[2 * 4 + 3] = nearClip * farClip;
	K.m_data[3 * 4 + 2] = -1.0f;
	return K;
}

//! redefine main camera's projection matrix, overriding unity's default.
//! projection matrix is calculated based on orthographic matrix and 
//! camera's intrinsic parameter matrix. 
inline AppUtils::Matrix4f ConfigureAugmentedCamera(float alpha, float beta, float skew, float u0, float v0, 
												   int imgWidth, int imgHeight, float nearClip, float farClip)
{
	const AppUtils::Matrix4f orth = CalculateOrthographicProjection(imgWidth, imgHeight, nearClip, farClip);
	
	const AppUtils::Matrix4f K = SetUnityCamParamsMatrix(alpha, beta, skew, u0, v0, nearClip, farClip);

	return (orth * K).Transpose();
}

inline AppUtils::Matrix4f ConfigureAugmentedCamera(const PXCScenePerception::ScenePerceptionIntrinsics* spIntrinsics,
												   float skew, float nearClip, float farClip)
{
	return ConfigureAugmentedCamera(spIntrinsics->focalLength.x, spIntrinsics->focalLength.y, skew, 
									spIntrinsics->principalPoint.x, spIntrinsics->imageSize.height - spIntrinsics->principalPoint.y, 
									spIntrinsics->imageSize.width, spIntrinsics->imageSize.height, 
									nearClip, farClip);
}

inline pxcStatus CopyColorPxcImageToBuffer(PXCImage* color, unsigned char* cdata, const unsigned int uiWidth, const unsigned int uiHeight)
{
	PXCImage::ImageData colorPxcImageData;
	pxcStatus result = color->AcquireAccess(PXCImage::ACCESS_READ, PXCImage::PIXEL_FORMAT_RGB32, &colorPxcImageData);
	if(result >= PXC_STATUS_NO_ERROR) 	
	{
		const unsigned int ncBytes = uiWidth * 4U;
		for(unsigned int y = 0; y < uiHeight; ++y)
		{
			const char* pcolor = (char *)(colorPxcImageData.planes[0] + colorPxcImageData.pitches[0] * y);
			memcpy_s(cdata, ncBytes, pcolor, ncBytes);
			cdata += ncBytes;
		}
		color->ReleaseAccess(&colorPxcImageData);
	}
	return result;
}

inline pxcStatus CopyDepthPxcImageToBuffer(PXCImage* depth, unsigned short *ddata, const unsigned int uiWidth, const unsigned int uiHeight)
{
	PXCImage::ImageData depthPxcImageData;
	pxcStatus result = depth->AcquireAccess(PXCImage::ACCESS_READ, PXCImage::PIXEL_FORMAT_DEPTH, &depthPxcImageData);
	if(result >= PXC_STATUS_NO_ERROR) 
	{
		char* pDdata = reinterpret_cast<char *>(ddata);
		const unsigned int ndBytes = uiWidth * sizeof(unsigned short);
		for(unsigned int y = 0; y < uiHeight; ++y)
		{
			const char* pdepth = (char *)(depthPxcImageData.planes[0] + depthPxcImageData.pitches[0] * y);
			memcpy_s(pDdata, ndBytes, pdepth, ndBytes);
			pDdata += ndBytes;
		}
		depth->ReleaseAccess(&depthPxcImageData);
	}
	return result;
}

inline void FillHolesInAlignedDepth(unsigned short * pDepthImg, const unsigned int uiDepthWidth, const unsigned int uiDepthHeight)
{
	for(unsigned int y = 0; y < uiDepthHeight; ++y)
	{
		for(unsigned int x = 0; x < uiDepthWidth; ++x)
		{
			if(!*pDepthImg)
			{
				if(x + 1 < uiDepthWidth && pDepthImg[1])
				{
					*pDepthImg = pDepthImg[1];
				}
				else
				{
					if(y + 1 < uiDepthHeight && pDepthImg[uiDepthWidth])
					{
						*pDepthImg = pDepthImg[uiDepthWidth];
					}
					else
					{
						if(x + 1 < uiDepthWidth && y + 1 < uiDepthHeight && pDepthImg[uiDepthWidth + 1])
						{
							*pDepthImg = pDepthImg[uiDepthWidth + 1];
						}
					}
				}
			}
			++pDepthImg;
		}
	}
}

inline pxcStatus GetAlignedDepth(PXCProjection * pProjection, PXCImage *depth, PXCImage *color, 
								 unsigned short *pDepthImg, const unsigned int uiDepthWidth, const unsigned int uiDepthHeight)
{
	PXCImage *mappedPxcDepthImage = pProjection->CreateDepthImageMappedToColor(depth, color);
	if(mappedPxcDepthImage) 
	{
		CopyDepthPxcImageToBuffer(mappedPxcDepthImage, pDepthImg, uiDepthWidth, uiDepthHeight);
		mappedPxcDepthImage->Release();

		FillHolesInAlignedDepth(pDepthImg, uiDepthWidth, uiDepthHeight);
		return PXC_STATUS_NO_ERROR;
	}
	return PXC_STATUS_PARAM_UNSUPPORTED;
}
