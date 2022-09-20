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

#include "posit_impl.h"

#define EVAL_CURSORPOS

void CPOSIT::sortPoints()
{
	/* check if 4 points are detected and sort the cam points
	** care: 0,0 of the rawPoints is in the right upper corner
	**
	*/

	if (rawPoints[0+XVALUE] != 1023 && 
		rawPoints[0+YVALUE] != 1023 &&
		rawPoints[2+XVALUE] != 1023 && 
		rawPoints[2+YVALUE] != 1023 &&
		rawPoints[4+XVALUE] != 1023 &&
		rawPoints[4+YVALUE] != 1023 &&
		rawPoints[6+XVALUE] != 1023 &&
		rawPoints[6+YVALUE] != 1023 )
	{		
		
		int highestXVal = 0;
		int secondhighestXVal = 0;
		int highestYVal = 0;
		int secondhighestYVal = 0;
		
		if (needSort)
		{
			for (int i = 0; i<4; i++)
			{
				//find the 2 highest X Values (2*i)
				if (rawPoints[2*i]>secondhighestXVal)
				{
					if (rawPoints[2*i]>highestXVal)
					{
						secondhighestXVal = highestXVal;
						highestXVal = rawPoints[2*i+XVALUE];						
					}
					else
						secondhighestXVal = rawPoints[2*i+XVALUE];
				}
				
				//find the 2 highest Y Values (2*i+YVALUE)
				if (rawPoints[2*i+YVALUE]>secondhighestYVal)
				{
					if (rawPoints[2*i+1]>highestYVal)
					{
						secondhighestYVal = highestYVal;
						highestYVal = rawPoints[2*i+YVALUE];
					}
					else
						secondhighestYVal = rawPoints[2*i+YVALUE];
				}			
			}

			//assign the sequence for the ptorder-array
			//zero-point for the IR-optical sensor is the right top corner, 
			//thus the x-values are increasing on the left side and y-values increase to the bottom side
			for (int i = 0; i<4; i++)
			{
				if (rawPoints[2*i] >= secondhighestXVal)
				{
					if (rawPoints[2*i+1] >= secondhighestYVal)
						ptOrder[0]=2*i;	//bottom left
					else
						ptOrder[1]=2*i;	//top left
				}
				else
				{
					if (rawPoints[2*i+1] >= secondhighestYVal)
						ptOrder[3]=2*i;	//bottom right
					else
						ptOrder[2]=2*i;	//top right		
				}
			}
			//check if all the points 1-4 exist once
			if (cmpInt(&ptOrder[0],4)) 
				needSort = true;
			else 
				needSort = false;
		}
		//update camPoints with new values according to sorted order
		//if no new valid value available keep old values and run sort procedure with next invoke
		camPoints[0] = resWidth-rawPoints[ptOrder[0]+XVALUE];
		camPoints[1] = rawPoints[ptOrder[0]+YVALUE];
		pt1Valid = true;
						
		camPoints[2] = resWidth-rawPoints[ptOrder[1]+XVALUE];
		camPoints[3] = rawPoints[ptOrder[1]+YVALUE];
		pt2Valid = true;		
			
		camPoints[4] = resWidth-rawPoints[ptOrder[2]+XVALUE];
		camPoints[5] = rawPoints[ptOrder[2]+YVALUE];
		pt3Valid = true;

		camPoints[6] = resWidth-rawPoints[ptOrder[3]+XVALUE];
		camPoints[7] = rawPoints[ptOrder[3]+YVALUE];
		pt4Valid = true;		
	}	
	else	//(when not all points are valid)
	{
		needSort = true;

		if	(rawPoints[0] != 1023)
		{
			camPoints[0] = resWidth-rawPoints[0+XVALUE];
			camPoints[1] = rawPoints[0+YVALUE];
			pt1Valid = true;
		}
		else pt1Valid = false;
				
	
		if	(rawPoints[2] != 1023)
		{
			camPoints[2] = resWidth-rawPoints[2+XVALUE];
			camPoints[3] = rawPoints[2+YVALUE];
			pt2Valid = true;
		}
		else pt2Valid = false;
		
		if	(rawPoints[4] != 1023)
		{
			camPoints[4] = resWidth-rawPoints[4+XVALUE];
			camPoints[5] = rawPoints[4+YVALUE];
			pt3Valid = true;
		}
		else pt3Valid = false;

		if	(rawPoints[6] != 1023)
		{
			camPoints[6] = resWidth-rawPoints[6+XVALUE];
			camPoints[7] = rawPoints[6+YVALUE];
			pt4Valid = true;
		}
		else pt4Valid = false;
	}
}

float CPOSIT::radiant2Degree(float radVal)
{
	float temp;
	temp = (float)(radVal*(180/M_PI));
	return temp;
}

bool CPOSIT::cmpInt(int* ptr_points, int it)
{
	bool isEqual = false;
	
	if ( it > 1)
	{
		it = it--;
		isEqual |= cmpInt((ptr_points+1), it);
	}

	for(int i = 1; i<=it; i++)
	{
		if (*ptr_points == *(ptr_points+it))
			isEqual |= true;
	}
	
	return (false||isEqual);
}

CPOSIT::CPOSIT()
{	
	rotation_matrix = new float[9];
	translation_vector = new float[3];
	rotation_matrix_reference = new float[9];
	translation_vector_reference = new float[3];
	ptOrder = new int[4];
	
	camPoints = new int[8];
	rawPoints = new int[8];
}

void CPOSIT::init()
{

#ifdef __write2Vid
	cv::VideoWriter recordVid;
	recordVid.open("record.avi", CV_FOURCC('P','I','M','1'), 20, cv::Size(RESWIDTH,RESHEIGHT));
#endif

	InitializeCriticalSection(&csMatAccess);
	InitializeCriticalSection(&csThreadMode);
	InitializeCriticalSection(&csEyeCoordinates);
	InitializeCriticalSection(&csTRvecAccess);

	focalLength = 1350.0;
	
	cuboidWidth = 80.0f;
	cuboidHeight = 104.0f;
	cuboidDepth = 88.0f;
	resWidth = RESWIDTH;
	resHeight = RESHEIGHT;
	halfResWidth = resWidth/2;
	halfResHeight = resHeight/2;

	blankImg = cv::Mat::zeros(cv::Size(resWidth, resHeight), CV_8UC3);
	debugImg = cv::Mat::zeros(cv::Size(resWidth, resHeight), CV_8UC3);
	
	p_rotation_matrix = cv::Mat::zeros(3, 3, CV_32F);

	needSort = true;

	for (int i = 0; i<4; i++){	
		ptOrder[i] = 2*i;	
	}
	for (int i = 0; i<3; i++){	
		translation_vector[i]=0;
	}
	for (int i = 0; i<9; i++){	
		rotation_matrix[i] = 0;	
	}
	rotation_matrix [0] = 1;
	rotation_matrix [4] = 1;
	rotation_matrix [8] = 1;

	criteria = cvTermCriteria(CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 1000, 1.0e-4f);	//terminate when iteration = 1000 OR epsilon <0.0001
	modelPoints.clear();

	modelPoints.push_back(cvPoint3D32f(
		(float) 0.0, 
		(float) 0.0, 
		(float) 0.0));
	modelPoints.push_back(cvPoint3D32f(
		(float) 0.0, 
		(float) -cuboidHeight, 
		(float) cuboidDepth));
	modelPoints.push_back(cvPoint3D32f(
		(float) cuboidWidth, 
		(float) -cuboidHeight, 
		(float) 0.0));
	modelPoints.push_back(cvPoint3D32f(
		(float) cuboidWidth, 
		(float) 0.0, 
		(float) cuboidDepth));
	positObject = cvCreatePOSITObject(&modelPoints[0],4);

	secureThreadMode = 0;
}

CPOSIT::~CPOSIT()
{	
	DeleteCriticalSection(&csMatAccess);
	DeleteCriticalSection(&csThreadMode);
	DeleteCriticalSection(&csEyeCoordinates);
	DeleteCriticalSection(&csTRvecAccess);

	cvReleasePOSITObject(&positObject);
		
	delete [] rotation_matrix;
	delete [] translation_vector;
	delete [] rotation_matrix_reference;
	delete [] translation_vector_reference;
	delete [] camPoints;
	delete [] rawPoints;
	delete [] ptOrder;
}

int CPOSIT::setCamVals(int, int, int){return 1;}
int CPOSIT::setModelVals(double, double, double){return 1;}

//runs POSIT with cam coordinates
int CPOSIT::runPOSIT(	int camx1, int camy1, int camx2, int camy2, 
							int camx3, int camy3, int camx4, int camy4)
{
	float tmpDegreeVal;

	rawPoints[0+XVALUE] = camx1;
	rawPoints[0+YVALUE] = camy1;
	rawPoints[2+XVALUE] = camx2;
	rawPoints[2+YVALUE] = camy2;
	rawPoints[4+XVALUE] = camx3;
	rawPoints[4+YVALUE] = camy3;
	rawPoints[6+XVALUE] = camx4;
	rawPoints[6+YVALUE] = camy4;
	sortPoints();
	
	//run POSIT if no sorting is needed
	if (!needSort)
	{			
		srcImagePoints.clear();
		srcImagePoints.push_back( cvPoint2D32f( (double)camPoints[0] - halfResWidth, (double)camPoints[1] - halfResHeight) );
		srcImagePoints.push_back( cvPoint2D32f( (double)camPoints[2] - halfResWidth, (double)camPoints[3] - halfResHeight) );
		srcImagePoints.push_back( cvPoint2D32f( (double)camPoints[4] - halfResWidth, (double)camPoints[5] - halfResHeight) );
		srcImagePoints.push_back( cvPoint2D32f( (double)camPoints[6] - halfResWidth, (double)camPoints[7] - halfResHeight) );
		
		cvPOSIT( positObject, &srcImagePoints[0], focalLength, criteria, &rotation_matrix[0], &translation_vector[0]);
	}

	//*******Critical Section begins
	EnterCriticalSection(&csTRvecAccess);
	p_rotation_vector.clear();		//clear vectors
	p_rotation_vector_degree.clear();
	p_translation_vector.clear();	

	//note: p_rotation_matrix is a cv::Mat structure
	//whereas p_translation_vector is a std::vector structure
	for (int i=0; i<3; i++)
	{		
		p_translation_vector.push_back((float)translation_vector[i]);
				
		for (int j=0; j<3; j++)
		{ p_rotation_matrix.at<float>(i,j) = (float)rotation_matrix[i+3*j]; }
	}	
		
	cv::Rodrigues(p_rotation_matrix, p_rotation_vector);	//convert rotation matrix to a rotation vector
	
	for (int i = 0; i<3; i++)	//convert rotation vector from radian to degree
	{
		tmpDegreeVal = radiant2Degree(p_rotation_vector.at(i));
		p_rotation_vector_degree.push_back(tmpDegreeVal);
	}
	
	LeaveCriticalSection(&csTRvecAccess);
	//*******Critical Section Ends

	return 1;
}


int CPOSIT::createDebugInfo(){
	cv::Mat dstImg = blankImg.clone();
	std::string string_buf;
	string_buf.reserve(15);
	

#ifdef __write2TextFile
	
	write2file.open ("test.txt", std::ios::out | std::ios::app);
	if (write2file.is_open())
	{
		if(pt1Valid)
			write2file << camPoints[0] <<" " << camPoints[1] <<" ";
		else
			write2file << -1 <<" " << -1 <<" ";
		
		if(pt2Valid)
			write2file << camPoints[2] <<" " << camPoints[3] <<" ";
		else
			write2file << -1 <<" " << -1 <<" ";
		
		if(pt3Valid)
			write2file << camPoints[4] <<" " << camPoints[5] <<" ";
		else
			write2file << -1 <<" " << -1 <<" ";
		
		if(pt4Valid)
			write2file << camPoints[6] <<" " << camPoints[7] <<"\n";
		else
			write2file << -1 <<" " << -1 <<"\n";										
	}
	write2file.close();
#endif
#ifdef __write2Vid
		recordVid.write(dst);
#endif		

	if (pt1Valid)
		cv::circle(dstImg,cv::Point(camPoints[0],camPoints[1]), 5, cv::Scalar(0,255,255), 3, 8, 0);
	else
		cv::circle(dstImg,cv::Point(camPoints[0],camPoints[1]), 5, cv::Scalar(0,0,255), 3, 8, 0);
	cv::putText(dstImg,"1",cv::Point(camPoints[0]+5,camPoints[1]+10), 0, 0.8, cv::Scalar(237,149,100), 1, 1);
	
	if (pt2Valid)
		cv::circle(dstImg,cv::Point(camPoints[2],camPoints[3]), 5, cv::Scalar(0,255,255), 3, 8, 0);
	else
		cv::circle(dstImg,cv::Point(camPoints[2],camPoints[3]), 5, cv::Scalar(0,0,255), 3, 8, 0);
	cv::putText(dstImg,"2",cv::Point(camPoints[2]+5,camPoints[3]+10), 0, 0.8, cv::Scalar(237,149,100), 1, 1);
	
	if (pt3Valid)
		cv::circle(dstImg,cv::Point(camPoints[4],camPoints[5]), 5, cv::Scalar(0,255,255), 3, 8, 0);
	else
		cv::circle(dstImg,cv::Point(camPoints[4],camPoints[5]), 5, cv::Scalar(0,0,255), 3, 8, 0);
	cv::putText(dstImg,"3",cv::Point(camPoints[4]+5,camPoints[5]+10), 0, 0.8, cv::Scalar(237,149,100), 1, 1);
	
	if (pt4Valid)
		cv::circle(dstImg,cv::Point(camPoints[6],camPoints[7]), 5, cv::Scalar(0,255,255), 3, 8, 0);
	else	
		cv::circle(dstImg,cv::Point(camPoints[6],camPoints[7]), 5, cv::Scalar(0,0,255), 3, 8, 0);
	cv::putText(dstImg,"4",cv::Point(camPoints[6]+5,camPoints[7]+10), 0, 0.8, cv::Scalar(237,149,100), 1, 1);
	
	//*****Critical Section Begins
	EnterCriticalSection(&csTRvecAccess);
	if (p_rotation_vector.size()>=3 && p_translation_vector.size()>=3)
	{
		cv::putText(dstImg, "rotation matrix", cv::Point(200,(585)), 0, 0.5, cv::Scalar(120,120,120), 1, 1);
		for (int i = 0; i<3; i++)
		{
			_snprintf(&string_buf[0], 10, "%4.3f", (float)rotation_matrix[3*i]);
			cv::putText(dstImg, &string_buf[0], cv::Point(200,(600+15*i)), 0, 0.5, cv::Scalar(120,120,120), 1, 1);

			_snprintf(&string_buf[0], 10, "%4.3f", (float)rotation_matrix[3*i+1]);
			cv::putText(dstImg, &string_buf[0], cv::Point(280,(600+15*i)), 0, 0.5, cv::Scalar(120,120,120), 1, 1);

			_snprintf(&string_buf[0], 10, "%4.3f", (float)rotation_matrix[3*i+2]);
			cv::putText(dstImg, &string_buf[0], cv::Point(360,(600+15*i)), 0, 0.5, cv::Scalar(120,120,120), 1, 1);
		}

		cv::putText(dstImg, "rotation vector", cv::Point(10,(670)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);
		cv::putText(dstImg, "rad", cv::Point(10,(685)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);
		cv::putText(dstImg, "deg", cv::Point(100,(685)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);
		cv::putText(dstImg, "translation vector", cv::Point(10,(585)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);

		for (int i = 0; i<3; i++)
		{
			_snprintf(&string_buf[0], 10, "%4.3f", (float)p_rotation_vector.at(i));
			cv::putText(dstImg, &string_buf[0], cv::Point(10,(700+15*i)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);

			_snprintf(&string_buf[0], 10, "%4.3f", (float)p_rotation_vector_degree.at(i));
			cv::putText(dstImg, &string_buf[0], cv::Point(100,(700+15*i)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);

			_snprintf(&string_buf[0], 10, "%4.3f", (float)p_translation_vector.at(i));
			cv::putText(dstImg, &string_buf[0], cv::Point(10,(600+15*i)), 0, 0.5, cv::Scalar(120,120,0), 1, 1);
		}
	}
	LeaveCriticalSection(&csTRvecAccess);
	//******Critical Section Ends

	//draw crosses at the raw Points
	//cv::line(dstImg,cv::Point(resWidth-rawPoints[0]-5,rawPoints[1]),cv::Point(resWidth-rawPoints[0]+5,rawPoints[1]),cv::Scalar(0,255,0));
	//cv::line(dstImg,cv::Point(resWidth-rawPoints[0],rawPoints[1]-5),cv::Point(resWidth-rawPoints[0],rawPoints[1]+5),cv::Scalar(0,255,0));

	//cv::line(dstImg,cv::Point(resWidth-rawPoints[2]-5,rawPoints[3]),cv::Point(resWidth-rawPoints[2]+5,rawPoints[3]),cv::Scalar(0,255,0));
	//cv::line(dstImg,cv::Point(resWidth-rawPoints[2],rawPoints[3]-5),cv::Point(resWidth-rawPoints[2],rawPoints[3]+5),cv::Scalar(0,255,0));

	//cv::line(dstImg,cv::Point(resWidth-rawPoints[4]-5,rawPoints[5]),cv::Point(resWidth-rawPoints[4]+5,rawPoints[5]),cv::Scalar(0,255,0));
	//cv::line(dstImg,cv::Point(resWidth-rawPoints[4],rawPoints[5]-5),cv::Point(resWidth-rawPoints[4],rawPoints[5]+5),cv::Scalar(0,255,0));

	//cv::line(dstImg,cv::Point(resWidth-rawPoints[6]-5,rawPoints[7]),cv::Point(resWidth-rawPoints[6]+5,rawPoints[7]),cv::Scalar(0,255,0));
	//cv::line(dstImg,cv::Point(resWidth-rawPoints[6],rawPoints[7]-5),cv::Point(resWidth-rawPoints[6],rawPoints[7]+5),cv::Scalar(0,255,0));

	EnterCriticalSection(&csMatAccess);
	//debugImg = dstImg.clone();
	dstImg.copyTo(debugImg);
	LeaveCriticalSection(&csMatAccess);

	return 1;
}

int CPOSIT::getDebugImage(cv::Mat &retImg){
	
	/* 
	** "The same cv::Mat can be used in different threads because 
	** the reference-counting operations use the architecture-specific atomic instructions"
	*/
	
	static int count = 0;
	std::string string_buf;

	if(TryEnterCriticalSection(&csMatAccess))
	{
		retImg = debugImg.clone();
		_snprintf(&string_buf[0], 10, "%d", (int)count);
		cv::putText(retImg, &string_buf[0], cv::Point(900, 20), 0, 0.5, cv::Scalar(120,120,120), 1, 1);
		LeaveCriticalSection(&csMatAccess);
	}
	else
	{
		count++;
	}
	
	return 1;
}

//"getter" for the rotation vector in degree
void CPOSIT::getRVdeg(float &rvx, float &rvy, float &rvz)
{
	EnterCriticalSection(&csTRvecAccess);
	rvx = p_rotation_vector_degree.at(0);
	rvy = p_rotation_vector_degree.at(1);
	rvz = p_rotation_vector_degree.at(2);
	LeaveCriticalSection(&csTRvecAccess);
}

//"getter" for the rotation vector in radiant
void CPOSIT::getRVrad(float &rvx, float &rvy, float &rvz)
{
	EnterCriticalSection(&csTRvecAccess);
	rvx = p_rotation_vector.at(0);
	rvy = p_rotation_vector.at(1);
	rvz = p_rotation_vector.at(2);
	LeaveCriticalSection(&csTRvecAccess);
}

//"getter" for the translation vector in millimeter
void CPOSIT::getTV(float &tvx, float &tvy, float &tvz)
{
	EnterCriticalSection(&csTRvecAccess);
	tvx = p_translation_vector.at(0);
	tvy = p_translation_vector.at(1);
	tvz = p_translation_vector.at(2);
	LeaveCriticalSection(&csTRvecAccess);
}


int CPOSIT::writeEvalFile(){
	int tmpVecSize;
	tmpVecSize = (int)rawEyeX.size();	
	time (&rawtime);
	timeinfo = localtime (&rawtime);
	strftime(&timestamp[0],80,"%y%m%d %H%M%S.txt",timeinfo);	
	
	write2EvalFile.open (timestamp, std::ios::out | std::ios::app);
	
	if (write2EvalFile.is_open())
	{
		write2EvalFile <<"evalCrossX evalCrossY rawEyeX rawEyeY actEyeX actEyeY TvecX TvecY TvecZ RvecX RvecY RvecZ\n";
		for(int i=0; i<tmpVecSize; i++)
		{
			write2EvalFile << (int)evalCrossX.at(i) << " " << (int)evalCrossY.at(i) << " "; 
			write2EvalFile << (int)rawEyeX.at(i) << " " << (int)rawEyeY.at(i) << " " << (int)actEyeX.at(i) << " " << (int)actEyeY.at(i) << " ";
			write2EvalFile << (float)evalTvecX.at(i) << " " << (float)evalTvecY.at(i) << " " << (float)evalTvecZ.at(i) << " ";
			write2EvalFile << (float)evalRvecX.at(i) << " " << (float)evalRvecY.at(i) << " " << (float)evalRvecZ.at(i) << "\n";
		}
		printf("file written");
	}
	write2EvalFile.close();
	
	return 1;
}

//getter for thread mode with critical section
int CPOSIT::getThreadMode(){
	int temp;
	EnterCriticalSection(&csThreadMode);
	temp = secureThreadMode;
	LeaveCriticalSection(&csThreadMode);
	return temp;
}

//setter for thread mode with critical section
void CPOSIT::setThreadMode(int mode){
	EnterCriticalSection(&csThreadMode);
	secureThreadMode = mode;
	LeaveCriticalSection(&csThreadMode);
}

//called by JVM: all eye coordinates are stored into the temp_.... variables
void CPOSIT::setTempEyeVal(int rawX, int rawY, int actX, int actY)
{
	EnterCriticalSection(&csEyeCoordinates);
	temp_rawEyeX = rawX;
	temp_rawEyeY = rawY;
	temp_actEyeX = actX;
	temp_actEyeY = actY;
	LeaveCriticalSection(&csEyeCoordinates);
}

//stores the current eye coordinates into a vector for further processing
void CPOSIT::copyTempEyeVal()
{
	std::string string_buf;
	string_buf.reserve(15);

	EnterCriticalSection(&csEyeCoordinates);

#ifdef EVAL_CURSORPOS
	POINT pt;
	GetCursorPos(&pt); 
	rawEyeX.push_back((int)pt.x);
	rawEyeY.push_back((int)pt.y);
	actEyeX.push_back((int)pt.x);
	actEyeY.push_back((int)pt.y);

#else
	rawEyeX.push_back((int)temp_rawEyeX);
	rawEyeY.push_back((int)temp_rawEyeY);
	actEyeX.push_back((int)temp_actEyeX);
	actEyeY.push_back((int)temp_actEyeY);
#endif
	LeaveCriticalSection(&csEyeCoordinates);
	/*
	printf("copyTempEyeVal\n");
	_snprintf(&string_buf[0], 10, "%i ", (int)rawEyeX.back());
	printf(&string_buf[0]);
	_snprintf(&string_buf[0], 10, "%i ", (int)rawEyeY.back());
	printf(&string_buf[0]);
	_snprintf(&string_buf[0], 10, "%i ", (int)actEyeX.back());
	printf(&string_buf[0]);
	_snprintf(&string_buf[0], 10, "%i\n", (int)actEyeY.back());
	printf(&string_buf[0]);
	*/
}

//stores the current pose into a vector for further processing
void CPOSIT::copyTRVal()
{	
	float tempX, tempY, tempZ;

#ifdef EVAL_CURSORPOS
	evalTvecX.push_back(0);
	evalTvecY.push_back(0);
	evalTvecZ.push_back(0);
	evalRvecX.push_back(0);
	evalRvecY.push_back(0);
	evalRvecZ.push_back(0);
#else
	getTV(tempX, tempY, tempZ);
	evalTvecX.push_back((float)tempX);
	evalTvecY.push_back((float)tempY);
	evalTvecZ.push_back((float)tempZ);

	getRVrad(tempX, tempY, tempZ);
	evalRvecX.push_back((float)tempX);
	evalRvecY.push_back((float)tempY);
	evalRvecZ.push_back((float)tempZ);
#endif
	//printf("copyTRVal\n");
}

int CPOSIT::getEvalImage(int resWidth, int resHeight, int x, int y, cv::Mat &retImg)
{
	cv::Mat evalImage = cv::Mat(cv::Size(resWidth, resHeight), CV_8UC3);
	evalCrossX.push_back(x);
	evalCrossY.push_back(y);

	cv::line(evalImage, cv::Point(x-8, y), cv::Point(x+8, y), cv::Scalar(0,255,0), 2);
	cv::line(evalImage, cv::Point(x, y-8), cv::Point(x, y+8), cv::Scalar(0,255,0), 2);
	
	evalImage.copyTo(retImg);

	return 1;
}

void CPOSIT::clearEvalVectors()
{
	evalCrossX.clear();
	evalCrossY.clear();
	rawEyeX.clear();
	rawEyeY.clear();
	actEyeX.clear();
	actEyeY.clear();
	evalTvecX.clear();
	evalTvecY.clear();
	evalTvecZ.clear();
	evalRvecX.clear();
	evalRvecY.clear();
	evalRvecZ.clear();
}