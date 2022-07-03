/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2010-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <Windows.h>
#include <windowsx.h>
#include <dxgi1_2.h>
#include <d3d11.h>
#include <d2d1.h>
#include <d2d1_1.h>
#include <mfapi.h>
#include <atlbase.h>
#include "pxcimage.h"
#include "pxcmetadata.h"

#pragma comment(lib, "d2d1.lib")
#pragma comment(lib, "d3d11.lib")
#pragma comment(lib, "dxgi.lib")

class D2D1Render {
public:

	D2D1Render():bitmap(), scale(true), box(), context2(), swapchain() {
		InitializeCriticalSection(&cs);
	}

	virtual ~D2D1Render() {
		if (bitmap) bitmap->Release();
		if (context2) context2->Release();
		if (swapchain) swapchain->Release();
		DeleteCriticalSection(&cs);
	}

	void UpdatePanel(PXCImage* image) {
		if (!image || !context2) return;

		EnterCriticalSection(&cs);
		PXCImage::ImageData data = {};
        PXCImage::Rotation rotation = image->QueryRotation();
 		pxcStatus sts = image->AcquireAccess(PXCImage::ACCESS_READ, PXCImage::PIXEL_FORMAT_RGB32, rotation, PXCImage::OPTION_ANY, &data);
		if (sts >= PXC_STATUS_NO_ERROR) {
			PXCImage::ImageInfo iinfo = image->QueryInfo();
			if (rotation == PXCImage::ROTATION_90_DEGREE || rotation == PXCImage::ROTATION_270_DEGREE)
			{
				int w = iinfo.width;
				iinfo.width = iinfo.height;
				iinfo.height = w;
			}

			HRESULT hr = E_FAIL;
			if (this->bitmap) {
				D2D1_SIZE_U bsize = this->bitmap->GetPixelSize();
				if (bsize.width == iinfo.width && bsize.height == iinfo.height) {
					hr = this->bitmap->CopyFromMemory((const D2D1_RECT_U*)&D2D1::RectU(0, 0, iinfo.width, iinfo.height), data.planes[0], data.pitches[0]);
					if (SUCCEEDED(hr)) UpdatePanel(this->bitmap);
				}
			}
			if (FAILED(hr)) {
				D2D1_BITMAP_PROPERTIES properties = D2D1::BitmapProperties(D2D1::PixelFormat(DXGI_FORMAT_B8G8R8A8_UNORM, D2D1_ALPHA_MODE_IGNORE));
				CComPtr<ID2D1Bitmap> bitmap1;
				HRESULT hr = context2->CreateBitmap(D2D1::SizeU(iinfo.width, iinfo.height), data.planes[0], data.pitches[0], properties, &bitmap1);
				if (SUCCEEDED(hr)) UpdatePanel(bitmap1);
			}
			image->ReleaseAccess(&data);
		}
		LeaveCriticalSection(&cs);
	}

	/* Update the display panel with the bitmap */
	void UpdatePanel(ID2D1Bitmap* bitmap = 0) {
		if (!context2) return;

		EnterCriticalSection(&cs);
		context2->BeginDraw();
		context2->Clear(D2D1::ColorF(D2D1::ColorF::LightGray));

		if (!bitmap) bitmap = this->bitmap;
		if (bitmap) {
			D2D1_SIZE_U bsize = bitmap->GetPixelSize();

			if (scale) {
				D2D1_SIZE_U psize = context2->GetPixelSize();

				float sx = (float)psize.width / (float)bsize.width;
				float sy = (float)psize.height / (float)bsize.height;
				float sxy = min(sx, sy);
				sx = sxy * (float)bsize.width;
				sy = sxy * (float)bsize.height;

				box.left = (psize.width - sx) / 2;
				box.top = (psize.height - sy) / 2;
				box.right = box.left + sx;
				box.bottom = box.top + sy;
			} else {
				box = D2D1::RectF(0, 0, (float)bsize.width, (float)bsize.height);
			}
			UpdatePanelEx(bitmap);

			// Save this bitmap
			if (bitmap != this->bitmap) {
				ID2D1Bitmap *tmp = this->bitmap;
				bitmap->AddRef(); this->bitmap = bitmap;
				if (tmp) tmp->Release();
			}
		}

		context2->EndDraw();
		swapchain->Present(1, 0);

		LeaveCriticalSection(&cs);
	}

	/* Set scaling: the display is scaled to the panel size while keeping the aspect ratio */
	void SetScale(bool scale) { 
		this->scale = scale; 
	}

	/* Get scaling: the display is scaled to the panel size while keeping the aspect ratio */
	bool GetScale() {
		return scale;
	}

	/* Set the display panel window handle */
	void SetHWND(HWND panel) {
		CComPtr<ID2D1Factory> factory2;
		HRESULT hr = D2D1CreateFactory(D2D1_FACTORY_TYPE_MULTI_THREADED, &factory2);
		if (FAILED(hr)) return;

		CComPtr<ID3D11Device> device3;
		hr = D3D11CreateDevice(NULL, D3D_DRIVER_TYPE_HARDWARE, 0, D3D11_CREATE_DEVICE_BGRA_SUPPORT, 0, 0, D3D11_SDK_VERSION, &device3, 0, 0);
		if (FAILED(hr)) {
			hr = D3D11CreateDevice(NULL, D3D_DRIVER_TYPE_WARP, 0, D3D11_CREATE_DEVICE_BGRA_SUPPORT, 0, 0, D3D11_SDK_VERSION, &device3, 0, 0);
			if (FAILED(hr)) return;
		}

		CComPtr<IDXGIDevice> dxgi_device;
		hr = device3->QueryInterface(__uuidof(IDXGIDevice), (void **)&dxgi_device);
		if (FAILED(hr)) return;

		CComPtr<ID2D1Device> device2;
		hr = D2D1CreateDevice(dxgi_device, 0, &device2);
		if (FAILED(hr)) return;

		if (context2) context2->Release();
		hr = device2->CreateDeviceContext(D2D1_DEVICE_CONTEXT_OPTIONS_NONE, &context2);
		if (FAILED(hr)) return;

		D2D1_POINT_2F dpi = { 96, 96 };
		factory2->GetDesktopDpi(&dpi.x, &dpi.y);
		context2->SetDpi(dpi.x, dpi.y);

		DXGI_SWAP_CHAIN_DESC1 desc = {};
		desc.Format = DXGI_FORMAT_B8G8R8A8_UNORM;
		desc.SampleDesc.Count = 1;
		desc.BufferUsage = DXGI_USAGE_RENDER_TARGET_OUTPUT;
		desc.BufferCount = 2;
		desc.SwapEffect = DXGI_SWAP_EFFECT_FLIP_SEQUENTIAL;

		CComPtr<IDXGIAdapter> adapter;
		hr = dxgi_device->GetAdapter(&adapter);
		if (FAILED(hr)) return;

		CComPtr<IDXGIFactory2> factory;
		hr=adapter->GetParent(__uuidof(IDXGIFactory2), (void**)&factory);
		if (FAILED(hr)) return;

		if (swapchain) swapchain->Release();
		hr=factory->CreateSwapChainForHwnd(device3, panel, &desc, 0, 0, &swapchain);
		if (FAILED(hr)) return;

		CComPtr<IDXGIDevice1> dxgi_device1;
		hr= dxgi_device->QueryInterface(__uuidof(IDXGIDevice1), (void **)&dxgi_device1);
		if (SUCCEEDED(hr)) dxgi_device1->SetMaximumFrameLatency(1);

		CreateSwapChainBitmap();
	}

	/* Call to resize the display panel */
	void ResizePanel() {
		if (!context2 || !swapchain) return;

		context2->SetTarget(0);
		HRESULT hr = swapchain->ResizeBuffers(0, 0, 0, DXGI_FORMAT_UNKNOWN, 0);
		if (hr == S_OK)
			CreateSwapChainBitmap();
	}

	ID2D1DeviceContext *QueryDeviceContext() { 
		return context2; 
	}

protected:

	CRITICAL_SECTION        cs;
	ID2D1DeviceContext*     context2;
	IDXGISwapChain1*		swapchain;
	ID2D1Bitmap*			bitmap;
	bool					scale;
	D2D1_RECT_F				box; // in physical pixels

	/* Additional drawing on the display panel */
	virtual void UpdatePanelEx(ID2D1Bitmap *bitmap) {
		D2D1_POINT_2F dpi = { 96, 96 };
		context2->GetDpi(&dpi.x, &dpi.y);
		dpi.x /= 96;
		dpi.y /= 96;

		D2D1_RECT_F box2 = { box.left / dpi.x, box.top / dpi.y, box.right / dpi.x, box.bottom / dpi.y };
		context2->DrawBitmap(bitmap, box2);
	}

	void CreateSwapChainBitmap() {
		CComPtr<IDXGISurface> buffer;
		HRESULT hr=swapchain->GetBuffer(0, __uuidof(IDXGISurface), (void**)&buffer);
		if (FAILED(hr)) return;

		D2D1_POINT_2F dpi = { 96, 96 };
		context2->GetDpi(&dpi.x, &dpi.y);
		D2D1_BITMAP_PROPERTIES1 props = D2D1::BitmapProperties1(D2D1_BITMAP_OPTIONS_TARGET | D2D1_BITMAP_OPTIONS_CANNOT_DRAW, D2D1::PixelFormat(DXGI_FORMAT_B8G8R8A8_UNORM, D2D1_ALPHA_MODE_IGNORE), dpi.x, dpi.y);

		CComPtr<ID2D1Bitmap1> bitmap1;
		hr=context2->CreateBitmapFromDxgiSurface(buffer, &props, &bitmap1);
		if (FAILED(hr)) return;

		context2->SetTarget(bitmap1);
	}
};
