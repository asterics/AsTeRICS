/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#pragma once

#include <opencv2\opencv.hpp>

namespace upmc{

	class MSERParameters
	{
	public:
		MSERParameters():									
				  delta(5) //MSER ....
				, min_area(150)
				, max_area(650)
				, max_variation(.25)
				, min_diversity(.5)
				, max_evolution(200)
				, area_threshold(1.01)
				, min_margin(0.003)
				, edge_blur_size(5)

		{}
	
		
		///MSER ctor params
		int		delta;
		int		min_area;
		int		max_area;
		float	max_variation;
		float	min_diversity; 
		int		max_evolution;
		double	area_threshold;
		double	min_margin;
		int		edge_blur_size;

		//serialization routines
		inline void write(cv::FileStorage& fs) const
		{
			fs << "{"
				<< "delta"			<< delta 
			    << "minarea"		<< min_area
				<< "maxarea"		<< max_area
				<< "max_variation"	<< max_variation
				<< "min_diversity"	<< min_diversity
				<< "max_evolution"	<< max_evolution
				<< "area_threshold" << area_threshold
				<< "min_margin"		<< min_margin
				<< "edge_blur_size" << edge_blur_size
				<< "}";
		}

		inline void read(const cv::FileNode& node)
		{
			delta= (int) node["delta"];
			min_area=(int) node["minarea"];
			max_area=(int) node["maxarea"];
			max_variation=(float)node["max_variation"];
			min_diversity=(float)node["min_diversity"];
			max_evolution=(int)node["max_evolution"];
			area_threshold= (double) node["area_threshold"];
			min_margin= (double) node["min_margin"];
			edge_blur_size = (int) node["edge_blur_size"];
		}
	};//MSERParameters

	///These write and read functions must be defined for the serialization in FileStorage to work
	inline void write(cv::FileStorage& fs, const std::string&, const upmc::MSERParameters& x)
	{
		x.write(fs);
	}
	///
	inline void read(const cv::FileNode& node, upmc::MSERParameters& x, const upmc::MSERParameters& default_value =upmc::MSERParameters()){
		if(node.empty())
			x = default_value;
		else
			x.read(node);
	}

}//namespace upmc


