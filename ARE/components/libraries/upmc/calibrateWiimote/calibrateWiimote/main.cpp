#include <fstream>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <vector>

#if defined(_DEBUG)
#define OPENCV_LIB_SUFFIX "d.lib"
#else
#define OPENCV_LIB_SUFFIX ".lib"
#endif
#define OPENCV_LIB_VER "231"
#pragma comment(lib, "opencv_core" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_highgui" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_calib3d" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_imgproc" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_video" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_features2d" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_legacy" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_flann" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "zlib")
#pragma comment(lib, "libjasper")
#pragma comment(lib, "libpng")
#pragma comment(lib, "libtiff")
#pragma comment(lib, "libjpeg")
#pragma comment(lib, "comctl32")
#pragma comment(lib, "vfw32")
//
struct calibObj
{
	static cv::Point3f topLeft;
	static cv::Point3f topRight;
	static cv::Point3f bottomLeft;
	static cv::Point3f bottomRight;
};

//Initialize

cv::Point3f calibObj::topRight=cv::Point3f(0.04f, -0.0565f, 0.f);//x4, y4 
cv::Point3f calibObj::topLeft=cv::Point3f(-0.04f, -0.0565f, 0.f); //x3, y3 
cv::Point3f calibObj::bottomLeft=cv::Point3f(-0.04f, 0.0565f, 0.f);//x2, y2 
cv::Point3f calibObj::bottomRight=cv::Point3f(0.04f, 0.0465f, 0.f);//x1, y1 

//Gives worst results sometime.
#define _USE_INITIAL_INTRINSIC_GUESS_

int main(int argc, char* argv[])
{
	std::string calibFile="calibration.txt";

	std::ifstream infile;
    infile.open(calibFile.c_str(), std::ios::in);
	
	int x1, y1, x2, y2, x3, y3, x4, y4;

	std::vector<std::vector<cv::Point2f> > imagePoints;
	std::vector<std::vector<cv::Point3f> > objectPoints;
	std::cout << "Reading Points ... "; 
    
    int SKIPCNT;   
    
    if (argc >1)
        SKIPCNT=std::atoi(argv[1]);
    else {
        SKIPCNT=12;
    }

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
			std::vector<cv::Point2f> imgCoord;
		    std::vector<cv::Point3f> objCoord;

			objCoord.push_back(calibObj::bottomRight);
			imgCoord.push_back(cv::Point2f(x1,y1));

			objCoord.push_back(calibObj::bottomLeft);
			imgCoord.push_back(cv::Point2f(x2,y2));
	
			objCoord.push_back(calibObj::topLeft);
			imgCoord.push_back(cv::Point2f(x3,y3));

			objCoord.push_back(calibObj::topRight);
			imgCoord.push_back(cv::Point2f(x4, y4));				
			//pile up
			objectPoints.push_back(objCoord);
			imagePoints.push_back(imgCoord);                
            }

		}//

	}//eof()

	std::cout << objectPoints.size()*4 << " points read." << std::endl;

	cv::Size imageSize(1024, 768);
	cv::Mat distCoeffs = cv::Mat::zeros(5, 1, CV_64F);
	std::vector<cv::Mat> rvecs;
	std::vector<cv::Mat> tvecs;
    
#if defined(_USE_INITIAL_INTRINSIC_GUESS_)
     //initial Guess, Precomputed
    double intrinsics[] ={
       1.3569160361746690e+003, 0., 4.8890398105541738e+002, 0.,
       1.3824407645126275e+003, 6.0816808805730432e+002, 0., 0., 1.        
    };
    cv::Mat cameraMatrix(3,3, CV_64F, intrinsics);   
    //flags=flags|CV_CALIB_USE_INTRINSIC_GUESS;
#else
    cv::Mat cameraMatrix=cv::Mat::eye(3, 3, CV_64F); 
#endif


    
#if defined(_USE_INITIAL_INTRINSIC_GUESS_)
    std::cout << "calibrateCamera" << std::endl;
	double rms = cv::calibrateCamera
    (objectPoints, imagePoints 
     , imageSize 
     , cameraMatrix 
     , distCoeffs 
     , rvecs 
     , tvecs
     , CV_CALIB_USE_INTRINSIC_GUESS);
#else
    std::cout << "calibrateCamera" << std::endl;
	double rms = cv::calibrateCamera
    (objectPoints, imagePoints 
     , imageSize 
     , cameraMatrix 
     , distCoeffs 
     , rvecs 
     , tvecs);
#endif
    


	printf("RMS error reported by calibrateCamera: %g\n", rms);
    
	std::vector<float> reprojErrs;

	std::cout << "Saving calibration file." << std::endl;
	cv::FileStorage fs( "wiiCalib.yml", cv::FileStorage::WRITE );

	    fs << "camera_matrix" << cameraMatrix;
		fs << "distortion_coefficients" << distCoeffs;
		fs << "imageSize" << imageSize;
		fs << "avg_reprojection_error" << rms;

	fs.release();


	std::cout << "Plotting reprojections " << objectPoints.size() << std::endl;
	std::vector<cv::Point2f> reprojPoints;

    cv::Size videoSize(imageSize.width*0.5, imageSize.height*0.5);
	cv::VideoWriter movie("reprojection.avi",CV_FOURCC('D', 'I', 'B', ' '), 4, videoSize);
    cv::Mat videoframe;

	for(int i = 0; i < (int)objectPoints.size(); i++ )
	{
        cv::projectPoints(cv::Mat(objectPoints[i]), rvecs[i], tvecs[i],
                      cameraMatrix, distCoeffs, reprojPoints);

		std::vector<cv::Point2f>::const_iterator itr=reprojPoints.begin();
		std::vector<cv::Point2f>::const_iterator ito=imagePoints[i].begin();
		
		cv::Mat image=cv::Mat::zeros(imageSize.height, imageSize.width,CV_8UC3);


		for (; itr!=reprojPoints.end(), ito!=imagePoints[i].end(); ++itr, ++ito)
		{
			cv::circle(image, *itr, 5,cv::Scalar(0,200,0), -1);
			cv::circle(image, *ito, 5,cv::Scalar(0,0,200), -1);

			cv::line(image, *itr, *ito, cv::Scalar(128,128,128), 1);
		}
        
        cv::resize(image, videoframe, videoSize);
        
        cv::imshow("Reprojection", videoframe);
        
        movie << videoframe;
        
        
        
        int key = cv::waitKey(33);
        
        if (key==27) 
            break;
        //getchar();
	}

	return 0;
}