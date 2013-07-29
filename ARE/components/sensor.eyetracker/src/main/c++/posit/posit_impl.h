

#ifndef __h_posit_subunit
#define __h_posit_subunit

#define _USE_MATH_DEFINES

#include "opencv_includes.h"
#include <windows.h>
#include <fstream>
#include <stdio.h>
#include <string>
#include <vector>
#include <math.h>
#include <time.h>

//#define __DebugPOSIT		//deprecated (now: secureThreadMode), controls whether Debug Window is opened or not 
//#define __write2Vid		//whether video of Debug Window is made or not, __DebugPOSIT has to be defined!
//#define __write2TextFile	//whether measurement points are written to .txt file or not

#define RESWIDTH 1024
#define RESHEIGHT 768
#define XVALUE 0
#define YVALUE 1

class CPOSIT {

private:

	CRITICAL_SECTION csMatAccess;
	CRITICAL_SECTION csThreadMode;
	CRITICAL_SECTION csEyeCoordinates;
	CRITICAL_SECTION csTRvecAccess;

	//camera intrinsic values
	double focalLength;
	int resWidth, resHeight;
	int halfResWidth, halfResHeight;
	
	//model parameters
	double cuboidWidth;
	double cuboidHeight;
	double cuboidDepth;
	std::vector<CvPoint3D32f> modelPoints;
	std::vector<CvPoint2D32f> srcImagePoints;
	
	CvPOSITObject* positObject;
	CvTermCriteria criteria;	

	//Reference values are assigned at start up, when sortPoints() is finished
	//They are used to compute the head position compared to the start up
	//rotation matrix (9x1)
	float* rotation_matrix;
	float* rotation_matrix_reference;
	//translation vector(1x3)
	float* translation_vector;
	float* translation_vector_reference;

	int* camPoints;
	int* rawPoints;
	int* ptOrder;
	
	//variables and functions to sort the camera Points to the Model Points
	void sortPoints();
	float radiant2Degree(float radVal);
	bool cmpInt(int* ptr_points, int it);

	int camPt1_modelPt, camPt2_modelPt, camPt3_modelPt, camPt4_modelPt;
	bool needSort;
	bool pt1Valid, pt2Valid, pt3Valid, pt4Valid;

	//used to create images for debug purposes
	cv::Mat blankImg;
	cv::Mat debugImg;

	//structures with the result of the POSIT method
	cv::Mat p_rotation_matrix;
	std::vector<float> p_rotation_vector;
	std::vector<float> p_rotation_vector_degree;
	std::vector<float> p_translation_vector;	

	#ifdef __write2TextFile
	std::ofstream write2file;
	#endif

	//used for evaluation of the accuracy
	int screenResX, screenResY;
	int eyeX, eyeY;
	int actualEyeX, actualEyeY;
	
	std::ofstream write2EvalFile;
	char timestamp[80];
	time_t rawtime;
	struct tm * timeinfo;

	int secureThreadMode;
	int temp_rawEyeX, temp_rawEyeY, temp_actEyeX, temp_actEyeY;
	std::vector<int> evalCrossX;
	std::vector<int> evalCrossY;

public:

	CPOSIT();
	~CPOSIT();
	
	void init();	
	int setCamVals(int, int, int); //set focalLength, resWidth & resHeight if applicable
	int setModelVals(double, double, double);	//set cuboid parameters if applicable
	int runPOSIT(int, int, int, int, int, int, int, int);
	int createDebugInfo();
	int getDebugImage(cv::Mat &retImg);

	void getRVdeg(float &rvx, float &rvy, float &rvz);
	void getRVrad(float &rvx, float &rvy, float &rvz);
	void getTV(float &tvx, float &tvy, float &tvz);

	//methods and variables for evaluation of the accuracy
	int writeEvalFile();
	
	std::vector<int> rawEyeX;
	std::vector<int> rawEyeY;
	std::vector<int> actEyeX;
	std::vector<int> actEyeY;
	std::vector<float> evalTvecX;
	std::vector<float> evalTvecY;
	std::vector<float> evalTvecZ;
	std::vector<float> evalRvecX;
	std::vector<float> evalRvecY;
	std::vector<float> evalRvecZ;

	//methods for thread mode control
	int getThreadMode();
	void setThreadMode(int mode);
	
	void setTempEyeVal(int rawX, int rawY, int actX, int actY);	
	void copyTempEyeVal();
	void copyTRVal();
	int getEvalImage(int, int, int, int, cv::Mat &retImg);
	void clearEvalVectors();

};
#endif