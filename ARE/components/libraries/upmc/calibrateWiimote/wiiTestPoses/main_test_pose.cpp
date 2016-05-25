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

#include <opencv2/opencv.hpp>
#include "wiiPoseSolver.h"

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

cv::Mat rotox(double x)
{
  cv::Mat R = cv::Mat::eye(3,3,CV_64F);
  R.at<double>(1,1) = cos(x);
  R.at<double>(1,2) = -sin(x);
  R.at<double>(2,1) = sin(x);
  R.at<double>(2,2) = cos(x);
  return R;
}

cv::Mat rotoy(double y)
{
  cv::Mat R = cv::Mat::eye(3,3,CV_64F);
  R.at<double>(0,0) = cos(y);
  R.at<double>(0,2) = sin(y);
  R.at<double>(2,0) = -sin(y);
  R.at<double>(2,2) = cos(y);
  return R;
}

cv::Mat rotoz(double z)
{
  cv::Mat R = cv::Mat::eye(3,3,CV_64F);
  R.at<double>(0,0) = cos(z);
  R.at<double>(0,1) = -sin(z);
  R.at<double>(1,0) = sin(z);
  R.at<double>(1,1) = cos(z);
  return R;
}

static double deg2rad(double deg)
{
	return (deg*(CV_PI/180.0));
}

int main(int argc, char* argv[])
{
	std::string ymlfile;
		//Parse Input 1
	if(argc>1)
		ymlfile.assign(argv[1]);
	else
		ymlfile.assign("wiiCalib.yml");

	cv::Mat intrinsic;
	cv::Mat distcoeff;

	{
	cv::FileStorage fs;
	try{
		fs.open(ymlfile, cv::FileStorage::READ);	
		fs["camera_matrix"] >> intrinsic;
		fs["distortion_coefficients"] >> distcoeff;
	}
	catch(cv::Exception& e){
		std::cout << e.msg << std::endl;
		return 1;
	}
	}

	upmc::wiiPoseSolver wii;
	wii.load(ymlfile);

    //Simulate Rotation and Tranlsation
    
    //ROTATION
  cv::Mat Rt(3,4,CV_64F,cv::Scalar(0));

  Rt( cv::Range::all(), cv::Range(0,3) ) 
    = rotox(deg2rad(10))*rotoy(deg2rad(0))*rotoz(deg2rad(10));

    //TRANSLATION
    double T[]={0.25, -0.25, 1.75};
    
    cv::Mat tvecTest(3,1, CV_64F, T);
    
  Rt.at<double>(0,3) = tvecTest.at<double>(0);
  Rt.at<double>(1,3) = tvecTest.at<double>(1);
  Rt.at<double>(2,3) = tvecTest.at<double>(2);
    
    cv::Mat rvecTest;
    cv::Rodrigues(Rt, rvecTest);
    
  
    //DEFINE MODEL POINTS IN 3D
  std::vector<cv::Point3f> model_pts;
  model_pts.push_back(cv::Point3f(-0.25f, -.25f, 0.0f));
  model_pts.push_back(cv::Point3f(0.25f, -.25f, 0.0f));
  model_pts.push_back(cv::Point3f(.25f, .25f ,0.0f));
  model_pts.push_back(cv::Point3f(-.25, .25, 0.0f));
  cv::Mat M(model_pts);

    //CAMERA PROJECTION
    cv::Mat imagePoints;
    cv::projectPoints( M, rvecTest, tvecTest, intrinsic,  
                  distcoeff, imagePoints);
    
  //cv::Mat m = P*M_resh;
  std::cout << "Image points"<< std::endl << imagePoints << std::endl << "rows: " << imagePoints.rows << "\t" << "cols: " << imagePoints.cols << "\t"<<", channels "<< imagePoints.channels() << ", npoints= " << imagePoints.checkVector(3,CV_64F) <<" type: "<< imagePoints.type()<< std::endl;


  cv::Mat rvec, tvec;
      
//    cv::solvePnPRansac(M, imagePoints, intrinsic, distcoeff, rvec, tvec);
//    cv::Mat R_est;
//    cv::Rodrigues(rvec, R_est);
//    
//    cv::Mat difR = Rt( cv::Range::all(), cv::Range(0,3) ) - R_est;
//    
//    std::cout << "RANSAC: difR"<<  std::endl << difR << std::endl;
//    
//    float norm_difference =  cv::norm(difR);
//    std::cout << "t: " << tvec << std::endl;
//    std::cout << "difference: " << norm_difference << std::endl<< std::endl;

  
  cv::Mat wiiR;
  wii.solve(M, imagePoints, wiiR, tvec);

    cv::Mat difR = Rt( cv::Range::all(), cv::Range(0,3) ) - wiiR;

  std::cout << "PnP: difR"<<  std::endl << difR << std::endl;

    float norm_difference =  cv::norm(difR);  
    std::cout << "t: " << tvec << std::endl;
  std::cout << "difference: " << norm_difference << std::endl;

  std::cout << "Press Return to quit" << std::endl;
  getchar();
  
}
