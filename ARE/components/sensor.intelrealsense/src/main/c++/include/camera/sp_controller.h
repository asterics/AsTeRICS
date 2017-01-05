/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once

#include "PXCSenseManager.h"
#include "PXCScenePerception.h"
#include "pxcprojection.h"
#include "sp_math_utils.h"
#include "pxcmetadata.h"
#include "service/pxcsessionservice.h"
#include "util_cmdline.h"
#include <iostream>
#include <windows.h>

class IScenePerceptionMeshFetcher
{
public:
	IScenePerceptionMeshFetcher() {}
	virtual pxcStatus DoMeshingUpdate(PXCBlockMeshingData *blockMeshingData, PXCScenePerception::MeshingUpdateInfo  &meshingUpdateInfo) = 0;
	virtual PXCBlockMeshingData* CreatePXCBlockMeshingData() = 0;
	virtual ~IScenePerceptionMeshFetcher() {};
};

class ScenePerceptionController : public IScenePerceptionMeshFetcher
{
	public:
		enum COORDINATE_SYSTEM
		{
			RSSDK_DEFAULT,
			RSSDK_OPENCV
		};

		ScenePerceptionController(const int iColorWidth, const int iColorHeight, 
								  const int iDepthWidth, const int iDepthHeight,
								  const int iFPS) : m_pScenePerception(NULL),
													m_pSenseMgr(NULL),
													m_pProjection(NULL),
													m_pPreviewImage(NULL),
													m_pCaptureManager(NULL),
													m_iFPS(iFPS),
													m_iColorWidth(iColorWidth),
													m_iColorHeight(iColorHeight),
													m_iDepthWidth(iDepthWidth),
													m_iDepthHeight(iDepthHeight),
													m_coordinateSystem(RSSDK_OPENCV),
													m_bIsPlayback(false),
													m_bIsRecording(false),
													m_bFrameCaptureSuccessful(false),
													m_bIsScenePerceptionPaused(false)
		{
			
		}

		bool Init(std::wstring sample_name, int argc, WCHAR* argvW[], const pxcCHAR *options)
		{
			m_pSenseMgr = PXCSenseManager::CreateInstance();

			if(!m_pSenseMgr)
			{
				std::cout << "Failed to create an SDK SenseManager" << std::endl;
				return false;
			}
			PXCSession *pSession = m_pSenseMgr->QuerySession();

			UtilCmdLine cmd(pSession);
			if(!cmd.Parse(options, argc, argvW))
			{		
				std::cout << "Failed To Parse Command Line Arguments" << std::endl;
				return false;
			}
			
			pSession->SetCoordinateSystem(PXCSession::COORDINATE_SYSTEM_REAR_OPENCV);
			
			if(cmd.m_csize.size() > 0) 
			{
				m_iColorWidth = cmd.m_csize.front().first.width; 
				m_iColorHeight = cmd.m_csize.front().first.height;
				m_iFPS = cmd.m_csize.front().second;
			}

			if(cmd.m_dsize.size() > 0) 
			{
				m_iDepthWidth  = cmd.m_dsize.front().first.width;
				m_iDepthHeight = cmd.m_dsize.front().first.height;
				m_iFPS = cmd.m_dsize.front().second;
			}
			
			m_pCaptureManager = m_pSenseMgr->QueryCaptureManager();
			pxcStatus sts = m_pCaptureManager->SetFileName(cmd.m_recordedFile, cmd.m_bRecord);

			//Live and/or Record
			if(!cmd.m_recordedFile || (cmd.m_recordedFile && cmd.m_bRecord))
			{
				if(cmd.m_bRecord)
					m_bIsRecording = true;
				sts = m_pSenseMgr->EnableStream(PXCCapture::STREAM_TYPE_COLOR, m_iColorWidth, m_iColorHeight, static_cast<float>(m_iFPS));
				sts = m_pSenseMgr->EnableStream(PXCCapture::STREAM_TYPE_DEPTH, m_iDepthWidth, m_iDepthHeight, static_cast<float>(m_iFPS));
			}
			else
			{
				m_bIsPlayback = true;
				m_pCaptureManager->SetRealtime(0);

				sts = m_pSenseMgr->EnableStream(PXCCapture::STREAM_TYPE_COLOR, 0, 0, 0);
				sts = m_pSenseMgr->EnableStream(PXCCapture::STREAM_TYPE_DEPTH, 0, 0, 0);
			}

			m_coordinateSystem = (!(pSession->QueryCoordinateSystem() & PXCSession::COORDINATE_SYSTEM_REAR_OPENCV)) ? RSSDK_DEFAULT: RSSDK_OPENCV;

			sts = m_pSenseMgr->EnableScenePerception();
			if(sts < PXC_STATUS_NO_ERROR)
			{
				std::cout << "Failed to enable scene perception module" << std::endl;		
				return false;
			}

			m_pScenePerception = m_pSenseMgr->QueryScenePerception();
			if(m_pScenePerception == NULL)
			{
				std::cout << "Failed to Query scene perception module" << std::endl;		
				return false;
			}
			return true;
		}

		bool InitPipeline()
		{
			m_pScenePerception->EnableInertialSensorSupport(0);
			
			if(m_pSenseMgr->Init() < PXC_STATUS_NO_ERROR) 
			{
				std::cout << "SenseManager Init Failed\n";
				return false;
			}
			QueryCaptureSize();
			return true;
		}
		 
		bool ProcessNextFrame(PXCCapture::Sample * &pSample, pxcF32 curPose[12], PXCScenePerception::TrackingAccuracy& accuracy, float& imageQuality)
		{	
			CleanupFrame();

			pxcStatus pxcSts = m_pSenseMgr->AcquireFrame(true);
			if(pxcSts < PXC_STATUS_NO_ERROR)
			{
				if(pxcSts == PXC_STATUS_DEVICE_LOST)
				{		
					std::cout << "Capture Device Lost" << std::endl;
				}
				return false;
			}

			if(!m_bIsScenePerceptionPaused)
			{
				pSample = m_pSenseMgr->QueryScenePerceptionSample();
				m_pScenePerception->GetCameraPose(curPose);
				accuracy = m_pScenePerception->QueryTrackingAccuracy();
			}
			else
			{
				pSample = m_pSenseMgr->QuerySample();
				imageQuality = m_pScenePerception->CheckSceneQuality(pSample);
			}
				
			m_bFrameCaptureSuccessful = true;
			return true;
		}

		float CheckSceneQuality(PXCCapture::Sample *pSample)
		{
			return m_pScenePerception->CheckSceneQuality(pSample);
		}

		void QueryCaptureSize(int &uiColorWidth, int &uiColorHeight, int &uiDepthWidth, int &uiDepthHeight)
		{
			uiColorWidth  = m_iColorWidth;
			uiColorHeight =	m_iColorHeight;

			uiDepthWidth  =	m_iDepthWidth;
			uiDepthHeight =	m_iDepthHeight;
		}

		bool IsPlaybackOrRecording() const
		{
			return (m_bIsPlayback || m_bIsRecording);
		}

		bool SetVoxelResolution(PXCScenePerception::VoxelResolution voxelResolution)
		{
			if(m_pScenePerception->SetVoxelResolution(voxelResolution) == PXC_STATUS_NO_ERROR)
				return true;
			return false;
		}
		
		PXCBlockMeshingData* CreatePXCBlockMeshingData()
		{
			return m_pScenePerception->CreatePXCBlockMeshingData();
		}

		float GetDimension()
		{
			float fDimension = 0.0f;
			if(m_pScenePerception)
			{
				switch(m_pScenePerception->QueryVoxelResolution())
				{
					case PXCScenePerception::LOW_RESOLUTION:
						fDimension = 4.0f;
						break;
					case PXCScenePerception::MED_RESOLUTION:
						fDimension = 2.0f;
						break;
					case PXCScenePerception::HIGH_RESOLUTION:
						fDimension = 1.0f;
						break;
				}
			}
			return fDimension;
		}

		void ScenePerceptionController::PauseScenePerception(bool bPause)
		{
			m_bIsScenePerceptionPaused = bPause;
			m_pSenseMgr->PauseScenePerception(bPause);
		}
		
		COORDINATE_SYSTEM ScenePerceptionController::GetCoordinateSystem()
		{
			return m_coordinateSystem;
		}

		PXCSenseManager *QuerySenseManager()
		{
			return m_pSenseMgr;
		}

		void EnableReconstruction(bool bEnable)
		{
			m_pScenePerception->EnableSceneReconstruction(bEnable);
		}
		
		PXCImage* QueryVolumePreview(const AppUtils::PoseMatrix4f& renderMatrix)
		{
			if(m_pPreviewImage == NULL)
			{
				m_pPreviewImage = m_pScenePerception->QueryVolumePreview(renderMatrix.m_data);
			}
			return m_pPreviewImage;
		}

		// Called by the meshing thread to collect new meshing data.  It does not need to be synchronized to ProcessNextFrame
		pxcStatus DoMeshingUpdate(PXCBlockMeshingData *blockMeshingData, PXCScenePerception::MeshingUpdateInfo  &meshingUpdateInfo)
		{
			pxcStatus status = m_pScenePerception->DoMeshingUpdate(blockMeshingData, 0, &meshingUpdateInfo);		
			m_pScenePerception->SetMeshingThresholds(0.03f, 0.005f);		
			return status;
		}

		// Reset the scene perception module state without destroying the entire pipeline and re-creating it.
		// Also set the initial pose of the camera
		bool ResetScenePerception()
		{
			m_pScenePerception->SetMeshingThresholds(0.0f, 0.0f);
			const auto pxcSts = m_pScenePerception->Reset();
			return (pxcSts >= PXC_STATUS_NO_ERROR) ? true: false;
		}

		PXCScenePerception * QueryScenePerception()
		{
			return m_pScenePerception;
		}
		
		bool GetScenePerceptionCameraIntrinsics(PXCScenePerception::ScenePerceptionIntrinsics *spIntrinsics)
		{
			if(PXC_STATUS_NO_ERROR <= m_pScenePerception->GetInternalCameraIntrinsics(spIntrinsics))
				return true;
			return false;
		}

		~ScenePerceptionController()
		{
			CleanupFrame();
			if(m_pProjection)
			{
				m_pProjection->Release();
			}

			if(m_pSenseMgr)
			{
				m_pSenseMgr->Close();
				m_pSenseMgr->Release();
			}
		}

		bool InitializeProjection()
		{
			PXCCapture::Device *pDevice = m_pCaptureManager->QueryDevice();
			m_pProjection = pDevice->CreateProjection();
			return (m_pProjection != NULL) ? true : false;
		}

		PXCProjection* QueryProjection()
		{
			return m_pProjection;
		}	

	private:
		PXCSenseManager*	m_pSenseMgr;
		PXCScenePerception* m_pScenePerception;
		PXCProjection *		m_pProjection;
		PXCImage *			m_pPreviewImage;
		COORDINATE_SYSTEM   m_coordinateSystem;
		
		int					m_iFPS;
		int					m_iColorWidth;
		int					m_iColorHeight;
		int					m_iDepthWidth;
		int					m_iDepthHeight;

		bool				m_bIsPlayback;
		bool				m_bIsRecording;
		bool				m_bIsScenePerceptionPaused;
		bool				m_bFrameCaptureSuccessful;
		
		PXCCaptureManager*  m_pCaptureManager;

		ScenePerceptionController();
		ScenePerceptionController(ScenePerceptionController &);
		ScenePerceptionController& operator=(ScenePerceptionController &);

		void CleanupFrame()
		{
			if(m_bFrameCaptureSuccessful)
			{
				if (m_pPreviewImage) 
				{
					m_pPreviewImage->Release(); 
					m_pPreviewImage = NULL; 
				}
				m_pSenseMgr->ReleaseFrame();
			}
		}

		void QueryCaptureSize()
		{
			PXCSizeI32 streamSize = { 0 };
			streamSize = m_pCaptureManager->QueryImageSize(PXCCapture::STREAM_TYPE_COLOR),
			m_iColorWidth  = streamSize.width;
			m_iColorHeight = streamSize.height;

			streamSize = m_pCaptureManager->QueryImageSize(PXCCapture::STREAM_TYPE_DEPTH),
			m_iDepthWidth  = streamSize.width;
			m_iDepthHeight = streamSize.height;
		}
};