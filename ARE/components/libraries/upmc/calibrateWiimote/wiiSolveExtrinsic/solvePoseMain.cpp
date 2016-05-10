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

#include <fstream>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <vector>

#if defined(_DEBUG)
#define OPENCV_LIB_SUFFIX "d.lib"
#else
#define OPENCV_LIB_SUFFIX ".lib"
#endif
#define OPENCV_LIB_VER "241"
#pragma comment(lib, "opencv_core" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_highgui" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_calib3d" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_imgproc" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_video" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
//Needed because of the way opencv were compiled.
#pragma comment(lib, "zlib")
#pragma comment(lib, "libjasper")
#pragma comment(lib, "libpng")
#pragma comment(lib, "libtiff")
#pragma comment(lib, "libjpeg")
#pragma comment(lib, "comctl32")
#pragma comment(lib, "vfw32")
#pragma comment(lib, "opengl32")
#pragma comment(lib, "glu32")

#include "wiiPoseSolver.h"
#include "screenGeometry.h"

int main(int argc, char* argv[])
{
	upmc::wiiPoseSolver wii;
	std::string ymlfile;
	std::string points;

	//Parse Input 1
	if(argc>1)
		ymlfile.assign(argv[1]);
	else
		ymlfile.assign("wiiCalib.yml");

	std::cout <<"Calibration File: " << ymlfile << std::endl;

	//Parse Input 2
	if(argc>2)
		points.assign(argv[2]);
	else
		points.assign("points.txt");

	std::cout <<"Points File: " << points << std::endl;

    int SKIPCNT;   
	//Parse Input 3  
    if (argc > 3)
        SKIPCNT=std::atoi(argv[3]);
    else {
        SKIPCNT=12;
    }

	std::cout <<"SKIPCNT: " << SKIPCNT << std::endl;


	if(!wii.load(ymlfile))
	{
		std::cout << "Cannot find calibration file" << std::endl;
		return 1;
	}

	std::ifstream infile;
    infile.open(points.c_str(), std::ios::in);
	
	int x1, y1, x2, y2, x3, y3, x4, y4;

	std::vector<std::vector<cv::Point2f> > imagePoints;
	std::vector<std::vector<cv::Point3f> > objectPoints;
	std::cout << "Reading Points ... "; 
    
    int skipCnt = SKIPCNT;
    
	while(!infile.eof())
	{
		infile >> x1 >> y1 >> x2 >> y2 >> x3 >> y3 >> x4 >> y4;
		//std::cout
		//	<< "(" << x1 << ", " << y1 << ")" << " - "
		//	<< "(" << x2 << ", " << y2 << ")" << " - "
		//	<< "(" << x3 << ", " << y3 << ")" << " - "
		//	<< "(" << x4 << ", " << y4 << ")" 
		//	<< std::endl;
		//At least four valid points.
   
        if(skipCnt<0) 
            skipCnt=SKIPCNT;
        
        //printf("skipCnt: %d: \n", skipCnt);
        
		if(!(x1<0 || y1<0  || x2<0 ||  y2<0 || x3<0 ||  y3<0 || x4<0 ||  y4<0))
		{		
            if(--skipCnt == 0)
            {
			std::vector<cv::Point2f> cornersImg;//image coordinates of screen corners
		    std::vector<cv::Point3f> cornersObj;//3D object coordinates of screen corners

			cornersObj.push_back(upmc::screenGeometry::bottomRight);
			cornersImg.push_back(cv::Point2f(x1,y1));

			cornersObj.push_back(upmc::screenGeometry::bottomLeft);
			cornersImg.push_back(cv::Point2f(x2,y2));
	
			cornersObj.push_back(upmc::screenGeometry::topLeft);
			cornersImg.push_back(cv::Point2f(x3,y3));

			cornersObj.push_back(upmc::screenGeometry::topRight);
			cornersImg.push_back(cv::Point2f(x4, y4));				
			//pile up
			objectPoints.push_back(cornersObj);
			imagePoints.push_back(cornersImg);                
            }

		}//

	}//eof()

	std::cout << objectPoints.size()*4 << " Points read." << std::endl;

	std::vector<std::vector<cv::Point3f>>::const_iterator 
		objIt=objectPoints.begin();

	std::vector<std::vector<cv::Point2f>>::const_iterator 
		imgIt=imagePoints.begin();

	cv::Mat rvec;
	cv::Mat T_vec;
	cv::Mat R_mat;

	cv::FileStorage out;
	out.open("SolvedPoses.yml", cv::FileStorage::WRITE);

	for (; objIt!=objectPoints.end(),imgIt!=imagePoints.end(); ++objIt, ++imgIt)
	{
		wii.solve(*objIt, *imgIt, R_mat, T_vec);

		out << "pose" << "{"
			<< "R" << R_mat
			<< "T" << T_vec
			<< "}";
	}

	return 0;
}