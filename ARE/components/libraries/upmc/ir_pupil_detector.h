///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2011, Matia Pizzoli and Andrea Carbone, all rights reserved.
// pizzoli@dis.uniroma1.it http://www.dis.uniroma1.it/~pizzoli/
// carbone@isir.upmc.fr people.isir.upmc.fr/carbone/
// 
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
//		Fiora Pirri, Matia Pizzoli, Alessandro Rudi: A general method for the point of 
//		regard estimation in 3D space. CVPR 2011: 921-928
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
#ifndef UOR_IR_PUPIL_DETECTOR_H_INCLUDED
#define UOR_IR_PUPIL_DETECTOR_H_INCLUDED
///////////////////////////////////////////////////////////////////////////////
#include <opencv2\opencv.hpp>
///////////////////////////////////////////////////////////////////////////////
#include <vector>
#include <memory>
///////////////////////////////////////////////////////////////////////////////
#include "MSERParameters.h"
///////////////////////////////////////////////////////////////////////////////
namespace uor{

//Small utility: generates N samples from a Rotated Rect.
	//The main axis of the rotated rect are interpreted
	//as the two principal direction(singular values) 
	//of a MV covariance matrix (2x2)
	inline cv::Mat sample2DCov(const cv::RotatedRect& R, size_t N)
	{
		cv::Mat samples;//TODO: allocate
		

		
		return samples;
	};

///////////////////////////////////////////////////////////////////////////////
class IRPupilDetector
{
public:
	///ctor
  IRPupilDetector();
  ///dtor
  ~IRPupilDetector();

  ///Main method call
  bool detectPupil(const cv::Mat&, cv::RotatedRect&);

	///Inner struct. Holds info about selected MSER.
	struct blob_stats_t
	{
		double roundness;

		std::vector<cv::Point > points; //UNUSED
		std::vector<cv::Point > hull;
		cv::RotatedRect rrect; //experimental

		void reset(){roundness=0;}
		bool operator > (const blob_stats_t& other)
		{
			return this->roundness > other.roundness;
		}
	}; 
	
	///potentially dangerous, needs a lock
	void reset();
	void reset(const upmc::MSERParameters&);

	///util
	bool check_roi(cv::Rect const& _roi, size_t width, size_t height ) const;

	// Setters/Getters
	//--------------------------------------------------------------------++
	///ROI ----------------------------------------------------------------+
	inline cv::Rect roi() const
	{
		return _roi;
	}
	//reference overload (can be used to modify the roi)
	inline cv::Rect& get_roi() 
	{
		return _roi;
	}
	///
	inline void set_roi(cv::Rect roi) 
	{
		if (check_roi(roi, _width, _height))
		{
			std::cout << "New ROI Area: " << roi.width *roi.height << std::endl;
			_roi=roi;
		}
	}
	///ALL MSER ----------------------------------------------------------------+
	inline upmc::MSERParameters MSERParams() const
	{
		return _mserPar;
	}

	inline upmc::MSERParameters& getMSERParams() 
	{
		return _mserPar;
	}

	///min Area ----------------------------------------------------------------+
	inline int minArea() const
	{
		return _mserPar.min_area;
	}
	//
	inline void set_minArea(int marea) 
	{
		_mserPar.min_area=marea;
		reset();
	}

	///Max Area ----------------------------------------------------------------+
	inline int maxArea() const
	{
		return _mserPar.max_area;
	}
	//
	inline void set_maxArea(int Marea) 
	{
		_mserPar.max_area = Marea;
		reset();
	}
	///image width/height ----------------------------------------------------------+
	inline int width() const
	{
		return _width;
	}
	//-------------------------------------------------------------------------------
	inline int height() const
	{
		return _height;
	}
	// Delta		----------------------------------------------------------------+
	inline int delta() const
	{
		return _mserPar.delta;
	}
	//set
	inline void set_delta(int delta)
	{
		_mserPar.delta=delta;
	}
	// Max Var      ----------------------------------------------------------------+
	inline float max_variation() const
	{
		return _mserPar.max_variation;
	}
	//
	inline void set_max_variation(float maxvar)
	{
		_mserPar.max_variation=maxvar;
	}
	// Min Div      ----------------------------------------------------------------+
	inline float min_diversity() const
	{
	}
	inline void set_min_diversity(float mindiv)
	{
		_mserPar.min_diversity=mindiv;
	}
	// Max Evo      ----------------------------------------------------------------+
	inline int max_evolution() const
	{
		return _mserPar.max_evolution;
	}
	//
	inline void set_max_evolution(int maxevo)
	{
		_mserPar.max_evolution=maxevo;
	}
	// area thresh  ----------------------------------------------------------------+
	inline double area_threshold() const
	{
		return _mserPar.area_threshold;
	}
	inline void set_area_threshold(double areath)
	{
		_mserPar.area_threshold=areath;
	}
	// min margin  ----------------------------------------------------------------+
	inline double min_margin() const
	{
		return _mserPar.min_margin;
	}
	inline void set_min_margin(double minmarg)
	{
		_mserPar.min_margin=minmarg;
	}
	// _edge_blur_size  ---------------------------------------------------------- +
	inline int edge_blur_size() const
	{
		return _mserPar.edge_blur_size;
	}
	inline void set_edge_blur_size(int edgeblur)
	{
		_mserPar.edge_blur_size=edgeblur;
	}
	// _max_grey_value  ---------------------------------------------------------- +
	 inline int max_grey_value() const
	 {
		 return _max_grey_value;
	 }
	 inline void set_max_grey_value(int value) 
	 {
		 _max_grey_value=std::max(std::min(value,255),0);
	 }
	// _best_roundness  ---------------------------------------------------------- +
	 inline double best_roundness() const
	 {
		 return _best_roundness;
	 }
	 inline void set_best_roundness(double value) 
	 {
		 _best_roundness=value;
	 }
private://[methods]
  ///
  void _save_config();
  ///
  void _do_stats(const std::vector<cv::Point >&, blob_stats_t& ) ;
  ///
  float _get_average_luminance(const cv::Mat& img, const cv::RotatedRect& _rrect) const;
  ///
  cv::Scalar _get_average_luminance2(const cv::Mat& img, const cv::RotatedRect& _rrect) const;
  ///
  bool _is_uniform(const cv::Mat&) const;

private://[data]
	//
	cv::FileStorage _cfg;
	std::string _ymlfilename;
	//--------------------------------------------------------------------++
	upmc::MSERParameters _mserPar;
	//--------------------------------------------------------------------++
	///working image
	cv::Mat _grey;
	//[MSER]
	std::auto_ptr<cv::MSER> _mser;
	///Extremal Regions container
	std::vector<std::vector<cv::Point> > _extremalRegions;
	///The region of interest, centered around the expected pupil region. 
	cv::Rect _roi;
	//
	int _width;
	int _height;
	///
	blob_stats_t _current_shape;
	///
	blob_stats_t _best_shape;
	///
	double _best_roundness;

	/// 
	int  _max_grey_value;

	/// FLAGS
	volatile bool _check_uniform_greylevel;
	volatile bool _filter_white_blobs;

};
///////////////////////////////////////////////////////////////////////////////
}//namespace uor
///////////////////////////////////////////////////////////////////////////////
#endif //UOR_IR_PUPIL_DETECTOR_H_INCLUDED
