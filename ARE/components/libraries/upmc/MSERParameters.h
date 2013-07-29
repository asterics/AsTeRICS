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


