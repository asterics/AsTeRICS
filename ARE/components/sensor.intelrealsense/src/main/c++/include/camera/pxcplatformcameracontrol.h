#pragma once
#include "pxccapture.h"

class PXCPlatformCameraControl : public PXCBase
{
public:
	class Handler
	{
	public:
		virtual void PXCAPI OnPlatformCameraSample(PXCCapture::Sample& sample) = 0;
		virtual void PXCAPI OnPlatformCameraError() = 0;
	};

	PXC_CUID_OVERWRITE(PXC_UID('D', 'P', 'C', 'C'));
	
	virtual pxcStatus PXCAPI EnumPhotoProfile(pxcI32 idx, PXCCapture::Device::StreamProfile* photoProfile) = 0;
	virtual pxcStatus PXCAPI TakePhoto(const PXCCapture::Device::StreamProfile& photoProfile, PXCPlatformCameraControl::Handler* handler) = 0;
	virtual PXCProjection* PXCAPI CreatePhotoProjection(const PXCCapture::Device::StreamProfile& profile) = 0;
};