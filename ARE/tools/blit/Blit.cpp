// THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
// ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// Copyright (c) Microsoft Corporation. All rights reserved
//+-----------------------------------------------------------------------------
//
//      Sample Direct2D Application
//
//------------------------------------------------------------------------------

#include "Blit.h"



// double flickerFrequency_in=17.6;
int flickerTime_in=100;
int xPos_in=500;
int yPos_in=300;

int bmHeight=150;
int bmWidth=150;
wchar_t* onImageName= L"arrow_left.bmp";
wchar_t* offImageName= L"none";

int useOffBitmap=false;

FlickerApp app;

static void CALLBACK TimerProc(UINT uiID, UINT uiMsg, DWORD dwUser, DWORD dw1, DWORD dw2)
{
		        app.OnRender();
};



/******************************************************************
*                                                                 *
*  WinMain                                                        *
*                                                                 *
*  Application entry point                                        *
*                                                                 *
******************************************************************/

int WINAPI WinMain(
    HINSTANCE /* hInstance */,
    HINSTANCE /* hPrevInstance */,
    LPSTR str/* lpCmdLine */,
    int /* nCmdShow */
    )
	{
    // Ignore the return value because we want to run the program even in the
    // unlikely event that HeapSetInformation fails.

	char * pch;
	pch = strtok (str," ,");   if (pch) xPos_in=atoi(pch);
	pch = strtok (NULL, " ,"); if (pch) yPos_in=atoi(pch); 
	pch = strtok (NULL, " ,");	if (pch) {flickerTime_in=atoi(pch); if (flickerTime_in < 10) flickerTime_in=10; }
	pch = strtok (NULL, " ,");
	if (pch) 
	{
		int wchars_num =  MultiByteToWideChar( CP_UTF8 , 0 , pch , -1, NULL , 0 );
		onImageName = new wchar_t[wchars_num];
		MultiByteToWideChar( CP_UTF8 , 0 , pch , -1, onImageName , wchars_num );
	}
	pch = strtok (NULL, " ,");
	if (pch) 
	{
		int wchars_num =  MultiByteToWideChar( CP_UTF8 , 0 , pch , -1, NULL , 0 );
		offImageName = new wchar_t[wchars_num];
		MultiByteToWideChar( CP_UTF8 , 0 , pch , -1, offImageName , wchars_num );
	}

	HeapSetInformation(NULL, HeapEnableTerminationOnCorruption, NULL, 0);
    if (SUCCEEDED(CoInitialize(NULL)))
    {
			BITMAP  bm;
			HBITMAP hbm = NULL;
			
			if ( ( hbm = ( HBITMAP ) LoadImage( NULL, onImageName, IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE | LR_CREATEDIBSECTION ) ) != NULL )
			{
					if ( GetObject( hbm, sizeof( bm ), &bm ) != FALSE )
					{
						bmHeight=bm.bmHeight;
						bmWidth=bm.bmWidth;
					}
			
					if (SUCCEEDED(app.Initialize()))
					{
//							MMRESULT mRes = timeSetEvent(int(500/flickerFrequency_in), 0, &TimerProc, 0,TIME_PERIODIC);				    
							MMRESULT mRes = timeSetEvent(flickerTime_in, 0, &TimerProc, 0,TIME_PERIODIC);				    
							app.RunMessageLoop();
							mRes = timeKillEvent(mRes);
					}
			}
			CoUninitialize();
    }
    return 0;
}

/******************************************************************
*                                                                 *
*  FlickerApp::FlickerApp constructor                                   *
*                                                                 *
*  Initialize member data.                                        *
*                                                                 *
******************************************************************/

FlickerApp::FlickerApp() :
    m_hwnd(NULL),
    m_pWICFactory(NULL),
    m_pD2DFactory(NULL),
    m_pRenderTarget(NULL),
    m_pBlackBrush(NULL),
    m_pOnBitmapBrush(NULL),
	m_pOffBitmapBrush(NULL),
    m_pDWriteFactory(NULL),
    m_pTextFormat(NULL),
    m_pTextLayout(NULL)
{
}

/******************************************************************
*                                                                 *
*  FlickerApp::~FlickerApp destructor                                   *
*                                                                 *
*  Release resources.                                             *
*                                                                 *
******************************************************************/

FlickerApp::~FlickerApp()
{
    SafeRelease(&m_pD2DFactory);
    SafeRelease(&m_pRenderTarget);
    SafeRelease(&m_pBlackBrush);
    SafeRelease(&m_pOnBitmapBrush);
	SafeRelease(&m_pOffBitmapBrush);
    SafeRelease(&m_pDWriteFactory);
    SafeRelease(&m_pTextFormat);
    SafeRelease(&m_pTextLayout);
}

/******************************************************************
*                                                                 *
*  FlickerApp::Initialize                                            *
*                                                                 *
*  Create application window and device-independent resources.    *
*                                                                 *
******************************************************************/

HRESULT FlickerApp::Initialize()
{
    HRESULT hr;

    // Initialize device-indpendent resources, such
    // as the Direct2D factory.
    hr = CreateDeviceIndependentResources();
    if (SUCCEEDED(hr))
    {
        // Register the window class.
        WNDCLASSEX wcex = { sizeof(WNDCLASSEX) };
        wcex.style         = CS_HREDRAW | CS_VREDRAW;
        wcex.lpfnWndProc   = FlickerApp::WndProc;
        wcex.cbClsExtra    = 0;
        wcex.cbWndExtra    = sizeof(LONG_PTR);
        wcex.hInstance     = HINST_THISCOMPONENT;
        wcex.hbrBackground = NULL;
        wcex.lpszMenuName  = NULL;
        wcex.hCursor       = LoadCursor(NULL, IDC_ARROW);
        wcex.lpszClassName = L"SSVEPFlickerApp";

        RegisterClassEx(&wcex);

        // Because the CreateWindow function takes its size in pixels, we
        // obtain the system DPI and use it to scale the window size.
        //    FLOAT dpiX, dpiY;
        //    m_pD2DFactory->GetDesktopDpi(&dpiX, &dpiY);
        //    static_cast<UINT>(ceil(800.f * dpiX / 96.f)),
        //    static_cast<UINT>(ceil(800.f * dpiY / 96.f)),

        m_hwnd = CreateWindowEx(
			WS_EX_TOPMOST,
            L"SSVEPFlickerApp",
            L"SSVEP-Panel",
            WS_POPUP,
			//WS_OVERLAPPEDWINDOW,
            xPos_in, yPos_in,
            bmWidth, bmHeight,
            NULL,
            NULL,
            HINST_THISCOMPONENT,
            this
            );

        hr = m_hwnd ? S_OK : E_FAIL;
        if (SUCCEEDED(hr))
        {
            ShowWindow(m_hwnd, SW_SHOWNORMAL);
            UpdateWindow(m_hwnd);
        }
    }

    return hr;
}

/******************************************************************
*                                                                 *
*  FlickerApp::CreateDeviceIndependentResources                      *
*                                                                 *
*  This method is used to create resources which are not bound    *
*  to any device. Their lifetime effectively extends for the      *
*  duration of the app.                                           *
*                                                                 *
******************************************************************/

HRESULT FlickerApp::CreateDeviceIndependentResources()
{
    HRESULT hr;

    // Create a WIC factory.
    hr = CoCreateInstance(
        CLSID_WICImagingFactory,
        NULL,
        CLSCTX_INPROC_SERVER,
        IID_PPV_ARGS(&m_pWICFactory)
        );

    if (SUCCEEDED(hr))
    {
        // Create a Direct2D factory.
        hr = D2D1CreateFactory(
            D2D1_FACTORY_TYPE_SINGLE_THREADED,
            &m_pD2DFactory
            );
    }

    if (SUCCEEDED(hr))
    {
        // Create a shared DirectWrite factory.
        hr = DWriteCreateFactory(
            DWRITE_FACTORY_TYPE_SHARED,
            __uuidof(IDWriteFactory),
            reinterpret_cast<IUnknown**>(&m_pDWriteFactory)
            );
    }

    if (SUCCEEDED(hr))
    {
        hr = m_pDWriteFactory->CreateTextFormat(
            L"Verdana",     // The font family name.
            NULL,           // The font collection (NULL sets it to use the system font collection).
            DWRITE_FONT_WEIGHT_NORMAL,
            DWRITE_FONT_STYLE_NORMAL,
            DWRITE_FONT_STRETCH_NORMAL,
            10.0f,
            L"en-us",
            &m_pTextFormat
            );
    }
    return hr;
}

/******************************************************************
*                                                                 *
*  FlickerApp::CreateDeviceResources                                 *
*                                                                 *
*  This method creates resources which are bound to a particular  *
*  D3D device. It's all centralized here, in case the resources   *
*  need to be recreated in case of D3D device loss (eg. display   *
*  change, remoting, removal of video card, etc).                 *
*                                                                 *
******************************************************************/

HRESULT FlickerApp::CreateDeviceResources()
{
    HRESULT hr = S_OK;

    if (!m_pRenderTarget)
    {
        ID2D1Bitmap *pOnBitmap = NULL;
        ID2D1Bitmap *pOffBitmap = NULL;
		
		useOffBitmap=false;

		RECT rc;
        GetClientRect(m_hwnd, &rc);

        D2D1_SIZE_U size = D2D1::SizeU(
            rc.right - rc.left,
            rc.bottom - rc.top
            );

        // Create a Direct2D render target.
        hr = m_pD2DFactory->CreateHwndRenderTarget(
            D2D1::RenderTargetProperties(),
            D2D1::HwndRenderTargetProperties(m_hwnd, size),
            &m_pRenderTarget
            );

        if (SUCCEEDED(hr))
        {
			hr = m_pRenderTarget->CreateSolidColorBrush(
				D2D1::ColorF(D2D1::ColorF::Black),
				&m_pBlackBrush
				);
		}

        if (SUCCEEDED(hr))
        {
            // Create the bitmap to be used by the bitmap brush.
            hr = LoadFileBitmap(
                m_pRenderTarget,
                m_pWICFactory,
                (LPCWSTR)onImageName,
                &pOnBitmap
                );

			// Create the bitmap brushes

			/*
                D2D1_BITMAP_BRUSH_PROPERTIES propertiesXClampYClamp = D2D1::BitmapBrushProperties(
                    D2D1_EXTEND_MODE_CLAMP,
                    D2D1_EXTEND_MODE_CLAMP,
                    D2D1_BITMAP_INTERPOLATION_MODE_NEAREST_NEIGHBOR
                    );
			*/

            if (SUCCEEDED(hr))
            {

				hr = m_pRenderTarget->CreateBitmapBrush(
                    pOnBitmap,
                    //propertiesXClampYClamp,
                    &m_pOnBitmapBrush
                    );

				if (SUCCEEDED(hr))
				{

					hr = LoadFileBitmap(
						m_pRenderTarget,
						m_pWICFactory,
						(LPCWSTR)offImageName,
						&pOffBitmap
						);
				}

				if (SUCCEEDED(hr))
				{
					hr = m_pRenderTarget->CreateBitmapBrush(
						pOffBitmap,
						//propertiesXClampYClamp,
						&m_pOffBitmapBrush
						);

					if (SUCCEEDED(hr)) { 
						useOffBitmap=true;
					}
				}
            }
        }

        SafeRelease(&pOnBitmap);
        SafeRelease(&pOffBitmap);
    }

    return hr;
}

/******************************************************************
*                                                                 *
*  FlickerApp::DiscardDeviceResources                                *
*                                                                 *
*  Discard device-specific resources which need to be recreated   *
*  when a Direct3D device is lost.                                *
*                                                                 *
******************************************************************/

void FlickerApp::DiscardDeviceResources()
{
    SafeRelease(&m_pRenderTarget);
    SafeRelease(&m_pBlackBrush);
    SafeRelease(&m_pOnBitmapBrush);
    SafeRelease(&m_pOffBitmapBrush);
}

/******************************************************************
*                                                                 *
*  FlickerApp::RunMessageLoop                                        *
*                                                                 *
*  Main window message loop                                       *
*                                                                 *
******************************************************************/

void FlickerApp::RunMessageLoop()
{
    MSG msg;


    while (GetMessage(&msg, NULL, 0, 0))
    {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }


}

/******************************************************************
*                                                                 *
*  FlickerApp::OnRender                                              *
*                                                                 *
*  Called whenever the application needs to display the client    *
*  window.                                                        *
*                                                                 *
*  Note that this function will automatically discard             *
*  device-specific resources if the Direct3D device disappears    *
*  during execution, and will recreate the resources the          *
*  next time it's invoked.                                        *
*                                                                 *
*  This method will demonstrate the differences in                *
*  D2D1_EXTEND_MODE.                                              *
******************************************************************/

HRESULT FlickerApp::OnRender()
{
	static int mode=1;

    HRESULT hr = CreateDeviceResources();

    if (SUCCEEDED(hr))
    {
        m_pRenderTarget->BeginDraw();
		//        m_pRenderTarget->Clear(D2D1::ColorF(D2D1::ColorF::White));

        D2D1_RECT_F exampleRectangle = D2D1::RectF(0, 0, bmWidth, bmHeight);
        //m_pRenderTarget->SetTransform(D2D1::Matrix3x2F::Translation(5,5));
        //m_pBitmapBrush->SetExtendModeX(D2D1_EXTEND_MODE_CLAMP);
        //m_pBitmapBrush->SetExtendModeY(D2D1_EXTEND_MODE_CLAMP);

		if (mode ==1)
		{
		   	  mode=0;
	          m_pRenderTarget->FillRectangle(exampleRectangle, m_pOnBitmapBrush);
			  // m_pRenderTarget->DrawRectangle(exampleRectangle, m_pBlackBrush);
		}
		else
		{ 
			  mode =1;
			  if (useOffBitmap)
		          m_pRenderTarget->FillRectangle(exampleRectangle, m_pOffBitmapBrush);
			  else
	              m_pRenderTarget->FillRectangle(exampleRectangle, m_pBlackBrush);
		}

        hr = m_pRenderTarget->EndDraw();

        if (hr == D2DERR_RECREATE_TARGET)
        {
            hr = S_OK;
            DiscardDeviceResources();
        }
    }
    return hr;
}





/******************************************************************
*                                                                 *
*  FlickerApp::OnResize                                              *
*                                                                 *
*  If the application receives a WM_SIZE message, this method     *
*  resizes the render target appropriately.                       *
*                                                                 *
******************************************************************/

void FlickerApp::OnResize(UINT width, UINT height)
{
    if (m_pRenderTarget)
    {
        // Note: This method can fail, but it's okay to ignore the
        // error here, because the error will be returned again
        // the next time EndDraw is called.
        m_pRenderTarget->Resize(D2D1::SizeU(width, height));
    }
}

/******************************************************************
*                                                                 *
*  FlickerApp::WndProc                                               *
*                                                                 *
*  Window message handler                                         *
*                                                                 *
******************************************************************/

LRESULT CALLBACK FlickerApp::WndProc(HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam)
{
    LRESULT result = 0;

    if (message == WM_CREATE)
    {
        LPCREATESTRUCT pcs = (LPCREATESTRUCT)lParam;
        FlickerApp *pFlickerApp = (FlickerApp *)pcs->lpCreateParams;

        ::SetWindowLongPtrW(
            hwnd,
            GWLP_USERDATA,
            PtrToUlong(pFlickerApp)
            );

        result = 1;
    }
    else
    {
        FlickerApp *pFlickerApp = reinterpret_cast<FlickerApp *>(static_cast<LONG_PTR>(
            ::GetWindowLongPtrW(
                hwnd,
                GWLP_USERDATA
                )));

        bool wasHandled = false;

        if (pFlickerApp)
        {
            switch (message)
            {
            case WM_SIZE:
                {
                    UINT width = LOWORD(lParam);
                    UINT height = HIWORD(lParam);
                    // pFlickerApp->OnResize(width, height);
                }
                wasHandled = true;
                result = 0;
                break;

            case WM_DISPLAYCHANGE:
                {
                    InvalidateRect(hwnd, NULL, FALSE);
                }
                wasHandled = true;
                result = 0;
                break;

            case WM_PAINT:
                {
                  //  pFlickerApp->OnRender();
                    ValidateRect(hwnd, NULL);
                }
                wasHandled = true;
                result = 0;
                break;

            case WM_DESTROY:
                {
                    PostQuitMessage(0);
                }
                wasHandled = true;
                result = 1;
                break;
            }
        }

        if (!wasHandled)
        {
            result = DefWindowProc(hwnd, message, wParam, lParam);
        }
    }

    return result;
}

/******************************************************************
*                                                                 *
*  FlickerApp::LoadFileBitmap                                     *
*                                                                 *
*  This method will create a Direct2D bitmap from a file          *
*                                                                 *
******************************************************************/

HRESULT FlickerApp::LoadFileBitmap(
    ID2D1RenderTarget *pRenderTarget,
    IWICImagingFactory *pIWICFactory,
    PCWSTR fileName,
    ID2D1Bitmap **ppBitmap
    )
{
    HRESULT hr = S_OK;
    IWICBitmapDecoder *pDecoder = NULL;
    IWICBitmapFrameDecode *pSource = NULL;
    IWICStream *pStream = NULL;
    IWICFormatConverter *pConverter = NULL;



    if (SUCCEEDED(hr))
    {
        // Create a WIC stream to map onto the memory.
        hr = pIWICFactory->CreateStream(&pStream);
    }

    if (SUCCEEDED(hr))
    {
		hr = pStream->InitializeFromFilename(fileName, GENERIC_READ);

    }

    if (SUCCEEDED(hr))
    {
        // Create a decoder for the stream.
        hr = pIWICFactory->CreateDecoderFromStream(
            pStream,
            NULL,
            WICDecodeMetadataCacheOnLoad,
            &pDecoder
            );
    }

    if (SUCCEEDED(hr))
    {
        // Create the initial frame.
        hr = pDecoder->GetFrame(0, &pSource);
    }

    if (SUCCEEDED(hr))
    {
        // Convert the image format to 32bppPBGRA
        // (DXGI_FORMAT_B8G8R8A8_UNORM + D2D1_ALPHA_MODE_PREMULTIPLIED).
        hr = pIWICFactory->CreateFormatConverter(&pConverter);
    }

    if (SUCCEEDED(hr))
    {
        hr = pConverter->Initialize(
            pSource,
            GUID_WICPixelFormat32bppPBGRA,
            WICBitmapDitherTypeNone,
            NULL,
            0.f,
            WICBitmapPaletteTypeMedianCut
            );
    }

    if (SUCCEEDED(hr))
    {
        // Create a Direct2D bitmap from the WIC bitmap.
        hr = pRenderTarget->CreateBitmapFromWicBitmap(
            pConverter,
            NULL,
            ppBitmap
            );
    }

    SafeRelease(&pDecoder);
    SafeRelease(&pSource);
    SafeRelease(&pStream);
    SafeRelease(&pConverter);

    return hr;
}

