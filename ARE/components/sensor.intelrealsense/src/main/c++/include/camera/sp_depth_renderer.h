/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <GL/glew.h>
#include <GL/GL.h>
#include "pxcimage.h"
#include <memory>
#include "sp_glut_utils.h"

class DepthRenderer
{
public:

	DepthRenderer::DepthRenderer() : m_iXpos(0), m_iYpos(0), m_iWidth(0), m_iHeight(0), m_iImageWidth(0), m_iImageHeight(0)
	{

	}

	DepthRenderer::DepthRenderer(int xPos, int yPos, int width, int height) : 
		m_iXpos(xPos), m_iYpos(yPos), m_iWidth(width), m_iHeight(height), m_iImageWidth(0), m_iImageHeight(0)
	{

	}

	void DepthRenderer::Move(int xPos, int yPos)
	{
		m_iXpos = xPos; 
		m_iYpos = yPos;
	}

	void DepthRenderer::Resize(int width, int height)
	{
		m_iWidth = width;
		m_iHeight = height;
	}

	void DepthRenderer::Draw(PXCImage* depth)
	{
		glRasterPos2i(m_iXpos, m_iYpos);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		if(!m_pRawDepth || !m_pDepth)
		{
			PXCImage::ImageInfo imageInfo = depth->QueryInfo();
			m_iImageWidth = imageInfo.width;
			m_iImageHeight = imageInfo.height;

			m_pRawDepth.reset(new unsigned short[m_iImageWidth * m_iImageHeight]);
			m_pDepth.reset(new unsigned char[3 * m_iImageWidth * m_iImageHeight]);
		}
		glPixelStorei(GL_UNPACK_ROW_LENGTH, m_iImageWidth);
		glPixelZoom(float(m_iWidth) / m_iImageWidth, -float(m_iWidth) / m_iImageWidth);
		CopyDepthPxcImageToBuffer(depth, m_pRawDepth.get(), m_iImageWidth, m_iImageHeight);
		ConvertDepthToRGBUsingHistogram(m_pRawDepth.get(), m_pDepth.get());
		glDrawPixels(m_iImageWidth, m_iImageHeight, GL_RGB, GL_UNSIGNED_BYTE, m_pDepth.get());
	}

	void DepthRenderer::Draw(unsigned short* pDepth)
	{
		glRasterPos2i(m_iXpos, m_iYpos);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		if(!m_pDepth)
		{
			m_pDepth.reset(new unsigned char[3 * m_iImageWidth * m_iImageHeight]);
		}
		glPixelStorei(GL_UNPACK_ROW_LENGTH, m_iImageWidth);
		glPixelZoom(float(m_iWidth) / m_iImageWidth, -float(m_iWidth) / m_iImageWidth);
		ConvertDepthToRGBUsingHistogram(pDepth, m_pDepth.get());
		glDrawPixels(m_iImageWidth, m_iImageHeight, GL_RGB, GL_UNSIGNED_BYTE, m_pDepth.get());
	}

	void DepthRenderer::ConvertDepthToRGBUsingHistogram(const unsigned short* depthImage, unsigned char* rgbImage)
	{
		const static unsigned char nearColor[] = { 255, 0, 0 }, farColor[] = { 20, 40, 255 };

		// Produce a cumulative histogram of depth values
		int histogram[256 * 256] = { 1 };
		const int imageSize = m_iImageWidth * m_iImageHeight;
		const unsigned short *pTempDepthPtr = depthImage;
		for(int i = 0; i < imageSize; ++i, pTempDepthPtr++)
		{
			if(*pTempDepthPtr)
			{
				++histogram[*pTempDepthPtr];
			}
		}
		for(unsigned int i = 1; i < 256 * 256; ++i)
		{
			histogram[i] += histogram[i - 1];
		}

		// Remap the cumulative histogram to the range 0..256
		for(unsigned int i = 1; i < 256 * 256; i++)
		{
			histogram[i] = (histogram[i] << 8) / histogram[256 * 256 - 1];
		}

		// Produce RGB image by using the histogram to interpolate between two colors
		memset(rgbImage, 0, m_iImageWidth * m_iImageHeight * 3 * sizeof(unsigned char));
		pTempDepthPtr = depthImage;
		for(int i = 0; i < imageSize; ++i, ++pTempDepthPtr)
		{
			if(*pTempDepthPtr) // For valid depth values (depth > 0)
			{
				const auto t = histogram[*pTempDepthPtr]; // Use the histogram entry (in the range of 0..256) to interpolate between nearColor and farColor
				*rgbImage++ = ((256 - t) * nearColor[0] + t * farColor[0]) >> 8;
				*rgbImage++ = ((256 - t) * nearColor[1] + t * farColor[1]) >> 8;
				*rgbImage++ = ((256 - t) * nearColor[2] + t * farColor[2]) >> 8;
			}
			else
			{
				rgbImage += 3;
			}
		}
	}

	DepthRenderer::~DepthRenderer()
	{

	}

	void SetImageSize(const int iImageWidth, const int iImageHeight)
	{
		m_iImageWidth = iImageWidth;
		m_iImageHeight = iImageHeight;
	}

private:
	
	int m_iXpos; 
	int m_iYpos; 
	int m_iWidth; 
	int m_iHeight;
	int m_iImageWidth;
	int m_iImageHeight;
	std::unique_ptr<unsigned char []> m_pDepth;
	std::unique_ptr<unsigned short []> m_pRawDepth;

	DepthRenderer(DepthRenderer &);
	DepthRenderer& operator=(DepthRenderer &);
};
