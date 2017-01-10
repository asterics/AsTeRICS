/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2012-2013 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include <list>
#include <vector>
#include "pxcsession.h"

class UtilCmdLine {
public:
    static const pxcI32 DEFAULT_FRAMES = 50000;
    UtilCmdLine(PXCSession *session, pxcUID iuid = 0);
    bool Parse(const pxcCHAR *options, int argc, pxcCHAR *argv[]);

    std::list<std::pair<PXCSizeI32, pxcI32> >   m_csize;
    std::list<std::pair<PXCSizeI32, pxcI32> >   m_dsize;
    std::list<std::pair<PXCSizeI32, pxcI32> >   m_isize;
    std::list<std::pair<PXCSizeI32, pxcI32> >   m_rsize;
    std::list<std::pair<PXCSizeI32, pxcI32> >   m_lsize;
    pxcUID      m_iuid;
    pxcI32      m_nframes;
    pxcCHAR     *m_sdname;
    pxcI32      m_nchannels;
    pxcI32      m_sampleRate;
    pxcF32      m_volume;
    std::vector<pxcCHAR*> m_grammar;
    pxcCHAR     *m_recordedFile;
    pxcCHAR     *m_meshFormat;
    pxcBool     m_realtime;
    pxcCHAR     *m_ttstext;
    pxcEnum     m_language;
    bool        m_bRecord;
    pxcCHAR     *m_traceFile;
    pxcI32      m_eos;
    bool        m_bFace;
    bool        m_bGesture;
    bool        m_bVoice;
    bool        m_bNoRender;
    bool        m_bMirror;
    bool        m_bObject;
    bool        m_bSolid;
    bool        m_bLandmarks;
    pxcCHAR     *m_outFile;
	PXCSize3DF32 m_shape;
	pxcI32		m_resolution;
    bool        m_bHead;
    bool        m_bBody;
    bool        m_bVariable;
    bool        m_bTexture;
    bool        m_bNoHtml;
    pxcI32      m_minFramesBeforeScanStart;

protected:
    PXCSession  *m_session;
};

