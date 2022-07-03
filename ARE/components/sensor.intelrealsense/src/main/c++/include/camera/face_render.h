/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <map>
#include "util_render.h"
#include "pxcfacemodule.h"
#include "pxcfacedata.h"

class FaceRender : public UtilRender
{
public:
    FaceRender(pxcCHAR* title = 0);
	void SetFaceData(PXCFaceData *data);
	void Release()
	{
		delete this;
	}

protected:
	PXCFaceData* faceData;
    virtual void DrawMore(HDC hdc, double scaleX, double scaleY);

	bool DrawDetection(PXCFaceData::Face* trackedFace, double scaleX, double scaleY, HDC hdc, PXCRectI32& outLocation);
	void DrawFaceID(PXCFaceData::Face* trackedFace, double scaleX, double scaleY, HDC hdc, PXCRectI32& inLocation);
	void DrawLandmarks(PXCFaceData::Face* trackedFace, double scaleX, double scaleY, HDC hdc);
};

