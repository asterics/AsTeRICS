/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2013-2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include "pxcsession.h"
#include "pxcpowerstate.h"

class PXCPowerStateServiceClient :public PXCBase {
public:
    PXC_CUID_OVERWRITE(PXC_UID('P','W','M','C'));

	/* Queries unique id for desired device & stream and client id */
	virtual pxcUID QueryUniqueId(pxcI32 deviceId, pxcI32 streamId, pxcI32 mId)=0;

    /* Register module with the Power Manager */
    virtual pxcStatus RegisterModule(pxcUID uId, PXCSession::ImplGroup group, PXCSession::ImplSubgroup subGroup) = 0;

    /* Unregister module from certain device & stream. All further requests for this device from this module will be ignored */
    virtual pxcStatus UnregisterModule(pxcUID uId) = 0;

    /* Request state for stream on device, module may call QueryState to test if the state was actually set */
    virtual pxcStatus SetState(pxcUID uId, PXCPowerState::State state) = 0;

    /* Query power state on stream on device */
    virtual pxcStatus QueryState(pxcUID uId, PXCPowerState::State* state) = 0;
};

