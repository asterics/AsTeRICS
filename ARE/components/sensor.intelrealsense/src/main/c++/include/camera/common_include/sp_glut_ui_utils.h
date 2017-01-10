/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <string>
#include <GL/glut.h>

inline void glutPrint(const int x, const int y, const char * text, const float r, const  float g, const float b, const float a, void *font, 
					  int iFontHeight = 15)
{
	bool blending = false;
	if (glIsEnabled(GL_BLEND))
	{
		blending = true;
	}
	glEnable(GL_BLEND);
	glColor4f(r, g, b, a);
	glRasterPos2i(x, y);

	while(*text)
	{
		if (*text == '\n') 
		{ 
			glRasterPos2i(x, y + iFontHeight);
		}
		else 
		{ 
			glutBitmapCharacter(font, *text); 
		}
		++text;
	}
	if (!blending)
	{
		glDisable(GL_BLEND);
	}
}

inline void DisplayTrackingAccuracy(const PXCScenePerception::TrackingAccuracy &trackingStatus, const int x, const int y, void *font)
{
	static char displayChar[512] = "";
	std::string statusString;

	float statusColor[4] = { 0.0f, };
	switch (trackingStatus)
	{
	case PXCScenePerception::HIGH:
		statusString = "High Accuracy";
		statusColor[1] = 1.0f;
		statusColor[3] = 0.5f;
		break;

	case PXCScenePerception::MED:
		statusString = "Med Accuracy";
		statusColor[0] = statusColor[1] = 1.0f;
		statusColor[3] = 0.5f;
		break;

	case PXCScenePerception::LOW:
		statusString = "Low Accuracy";
		statusColor[0] = 1.0f;
		statusColor[3] = 0.5f;
		break;

	case PXCScenePerception::FAILED:
		statusString = "Failed - Relocalizing...";
		statusColor[0] = 1.0f;
		statusColor[3] = 0.5f;
		break;

	default:
		break;
	}

	sprintf_s(displayChar, "Tracking Accuracy: %s", statusString.c_str());
	glutPrint(x, y, displayChar, statusColor[0], statusColor[1], statusColor[2], statusColor[3], font);
}
