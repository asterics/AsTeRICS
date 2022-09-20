/*******************************************************************************
INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014 Intel Corporation. All Rights Reserved.
*******************************************************************************/
/// @file pxc3dseg.h
/// User Segmentation video module interface

#ifndef PXC3DSEG_H
#define PXC3DSEG_H
#include "pxccapture.h"

class PXC3DSeg : public PXCBase
{
public:
    /// Return a reference to the most recent segmented image.
    /// The returned object's Release method can be used to release the reference.
    virtual PXCImage* PXCAPI AcquireSegmentedImage(void)=0;

    PXC_CUID_OVERWRITE(PXC_UID('S', 'G', 'I', '1'));

    enum AlertEvent 
    {
        ALERT_USER_IN_RANGE = 0,
        ALERT_USER_TOO_CLOSE,
        ALERT_USER_TOO_FAR
    };

    struct AlertData 
    {
        pxcI64     timeStamp;
        AlertEvent label;
        pxcI32     reserved[5];
    };

    class AlertHandler
    {
    public:
        virtual void PXCAPI OnAlert(const AlertData& data)=0;
    };

    /// Optionally register to receive event notifications.
    /// A subsequent call will replace the previously registered handler object.
    /// Subscribe(NULL) to unsubscribe.
    virtual void PXCAPI Subscribe(AlertHandler* handler)=0;

	virtual pxcStatus PXCAPI SetFrameSkipInterval(pxcI32 skipInterval)=0;
};
#endif


