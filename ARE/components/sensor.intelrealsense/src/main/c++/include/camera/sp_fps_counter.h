/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <stdint.h>
#include <windows.h>
#include <chrono>

class FPScounter
{

private:
	uint64_t m_frameCounter;
	const float m_fUpdateFPSeveryNframes;
	LARGE_INTEGER clockFrequency;
	float m_fCurrentFPS;
	LARGE_INTEGER m_startTime;

	void Init()
	{
		memset(&clockFrequency, 0, sizeof(clockFrequency));
		memset(&m_startTime, 0, sizeof(m_startTime));
		QueryPerformanceFrequency(&clockFrequency);
	}

public:
	FPScounter(): m_fUpdateFPSeveryNframes(30.0f), m_frameCounter(0), m_fCurrentFPS(0.0f)
	{
		Init();	
	}

	FPScounter(unsigned int updateNframes):  m_fUpdateFPSeveryNframes(static_cast<float>(updateNframes)), m_frameCounter(0), m_fCurrentFPS(0.0f)
	{
		Init();
	}

	void InitCounter()
	{
		QueryPerformanceCounter(&m_startTime);
	}

	float GetFps()
	{
		return m_fCurrentFPS;
	}

	uint64_t GetFrameCount()
	{	
		return m_frameCounter;
	}

	void AddFrame()
	{
		++m_frameCounter;

		if(!(m_frameCounter % 30)) // 30 frames 
		{
			LARGE_INTEGER endTime = {0};
			QueryPerformanceCounter(&endTime);
			std::chrono::duration<float> trackTime = std::chrono::nanoseconds(((endTime.QuadPart - m_startTime.QuadPart) * std::nano::den) / clockFrequency.QuadPart);

			m_fCurrentFPS = m_fUpdateFPSeveryNframes / trackTime.count(); // 30 frame average fps
			m_startTime = endTime;
		}
	}



	~FPScounter()
	{


	}


private:
	FPScounter& operator=(FPScounter&);
	FPScounter(FPScounter &);
};
