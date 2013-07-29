///////////////////////////////////////////////////////////////////////////////
// This file is a C++ wrapper around the FaceTracker library by Jason Mora Saragih.
// The original license which accompany the library is shown below.
/**
 * @author Andrea Carbone
 *
 */
///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2010, Jason Mora Saragih, all rights reserved.
//
// This file is part of FaceTracker.
//
// Redistribution and use in source and binary forms, with or without 
// modification, are permitted provided that the following conditions are met:
//
//     * The software is provided under the terms of this licence stricly for
//       academic, non-commercial, not-for-profit purposes.
//     * Redistributions of source code must retain the above copyright notice, 
//       this list of conditions (licence) and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright 
//       notice, this list of conditions (licence) and the following disclaimer 
//       in the documentation and/or other materials provided with the 
//       distribution.
//     * The name of the author may not be used to endorse or promote products 
//       derived from this software without specific prior written permission.
//     * As this software depends on other libraries, the user must adhere to 
//       and keep in place any licencing terms of those libraries.
//     * Any publications arising from the use of this software, including but
//       not limited to academic journal and conference publications, technical
//       reports and manuals, must cite the following work:
//
//       J. M. Saragih, S. Lucey, and J. F. Cohn. Face Alignment through 
//       Subspace Constrained Mean-Shifts. International Conference of Computer 
//       Vision (ICCV), September, 2009.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED 
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO 
// EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
///////////////////////////////////////////////////////////////////////////////

#ifndef _UPMC_facetrackerLib_H_INCLUDED_
#define _UPMC_facetrackerLib_H_INCLUDED_

#include <memory>
#include <opencv2/opencv.hpp>


namespace upmc{
	///
	namespace detail
	{
		///Forward declaration, the beef is there.
		struct facetracker_impl;
	}

	///
	class facetrackerLib
	{
	public:
		///ctor
		facetrackerLib();
		///dtor
		//~facetrackerLib(){};
		///
		bool found() const;
        ///
        int64 frameCount() const; ///Frame number since last detection (see Tracker.h)
		///
		bool update(const cv::Mat& frame);
		///
		cv::Point2f getPosition() const;
		///
		cv::Point3f getEuler() const;
		///
		double getScale() const;
        ///
        cv::Mat getImagePts() const;
		///
		cv::Point2d getImagePoint(size_t) const;
		//
        cv::Mat getImageVis() const;
		///
		cv::Mat getObjectWeights() const;
        
    public:
		///
		void draw(cv::Mat &image);
		///
		void reset();
        
    public:
        //Logging ...
        //Save or not the BW frame in the log.
        void setSaveFrame(bool);
        //save
        void saveShape(cv::FileStorage&, const std::string&);
 
        
	private:
		std::auto_ptr<detail::facetracker_impl> _impl;
	};//class declaration

}//namespace

#endif //_UPMC_facetrackerLib_H_INCLUDED_