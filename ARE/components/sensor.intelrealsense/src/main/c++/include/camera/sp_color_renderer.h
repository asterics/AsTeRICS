#pragma once
#include <GL/glew.h>
#include <GL/GL.h>
#include "pxcimage.h"
#include <memory>
#include "sp_glut_utils.h"

class ColorRenderer
{
public:

	void SetImageSize(const int iImageWidth, const int iImageHeight)
	{
		m_iImageWidth = iImageWidth;
		m_iImageHeight = iImageHeight;
	}

	void Draw(PXCImage* color)
	{
		if(!color)
		{
			return;
		}

		if(!m_bIsPBOallocated)
		{		
			PXCImage::ImageInfo imageInfo = color->QueryInfo();
			m_iImageWidth = imageInfo.width;
			m_iImageHeight = imageInfo.height;
			m_pColor.reset(new unsigned char[m_iNumOfChannels * imageInfo.width * imageInfo.height]);
			m_iNumOfChannels = 4;
			CreatePBO();
		}
		CopyColorPxcImageToBuffer(color, m_pColor.get(), m_iImageWidth, m_iImageHeight);
		unsigned char *data = m_pColor.get();//@mem(data, UINT8, m_iNumOfChannels, 320, 240, 320*m_iNumOfChannels)
		Draw(m_pColor.get());
	}

	~ColorRenderer()
	{
		if(m_bIsPBOallocated)
		{	
			glDeleteBuffers(1, &m_glPBOid);
		}
	}

	ColorRenderer() :  m_iXpos(0), m_iYpos(0), m_iWidth(0), m_iHeight(0), m_iImageWidth(0), m_iImageHeight(0), m_glPBOid(0), m_bIsPBOallocated(false),
		m_iNumOfChannels(4)
	{

	}

	ColorRenderer(const int xPos, const int yPos, const int width, const int height) : 
		m_iXpos(xPos), m_iYpos(yPos), m_iWidth(width), m_iHeight(height), 
		m_iImageWidth(0), m_iImageHeight(0), m_glPBOid(0), m_bIsPBOallocated(false),
		m_iNumOfChannels(4)
	{

	}
	
	bool SetNumberOfChannels(const int iNumOfChannels)
	{
		if(!m_bIsPBOallocated || m_iNumOfChannels == iNumOfChannels)
		{
			m_iNumOfChannels = iNumOfChannels;
			return true;
		}
		return false;
	}

	void Move(const int xPos, const int yPos)
	{
		m_iXpos = xPos; 
		m_iYpos = yPos;
	}
	void Resize(const int width, const int height)
	{
		m_iWidth = width;
		m_iHeight = height;
	}

	void Draw(const unsigned char* pColor)
	{
		if(!m_bIsPBOallocated)
		{
			CreatePBO();
		}

		glRasterPos2i(m_iXpos, m_iYpos);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ROW_LENGTH, m_iImageWidth);
		glPixelZoom(float((m_iWidth)) / m_iImageWidth, -float((m_iWidth)) / m_iImageWidth);

		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, m_glPBOid);
		glBufferData(GL_PIXEL_UNPACK_BUFFER, m_iImageWidth * m_iImageHeight * m_iNumOfChannels, 0, GL_STREAM_DRAW);
		GLubyte* ptr = (GLubyte *) glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY);
		if(ptr)
		{
			// update data directly on the mapped buffer
			memcpy_s(ptr, m_iImageWidth * m_iImageHeight * m_iNumOfChannels, pColor, m_iImageWidth * m_iImageHeight * m_iNumOfChannels);
			glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER); // release pointer to mapping buffer
		}

		if(m_iNumOfChannels == 4)
		{
			glDrawPixels(m_iImageWidth, m_iImageHeight, GL_BGRA, GL_UNSIGNED_BYTE, 0);
		}
		else
		{
			if(m_iNumOfChannels == 3)
			{
				glDrawPixels(m_iImageWidth, m_iImageHeight, GL_RGB, GL_UNSIGNED_BYTE, 0);
			}
		}
		glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
	}

private:
	int m_iXpos; 
	int m_iYpos; 
	int m_iWidth; 
	int m_iHeight;
	int m_iImageWidth;
	int m_iImageHeight;
	std::unique_ptr<unsigned char []> m_pColor;
	GLuint m_glPBOid;
	bool m_bIsPBOallocated;

	void CreatePBO()
	{
		glGenBuffers(1, &m_glPBOid);
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, m_glPBOid);
		glBufferData(GL_PIXEL_UNPACK_BUFFER, m_iImageWidth * m_iImageHeight * m_iNumOfChannels, 0, GL_STREAM_DRAW);
		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
		m_bIsPBOallocated = true;
	}

	ColorRenderer(ColorRenderer &);
	ColorRenderer& operator=(ColorRenderer &);
	int			   m_iNumOfChannels;
};
