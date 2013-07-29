#include "screenGeometry.h"

namespace upmc{

	//Initialize
cv::Point3f screenGeometry::topRight=cv::Point3f(0.04f, -0.0565f, 0.f);//x4, y4 
cv::Point3f screenGeometry::topLeft=cv::Point3f(-0.04f, -0.0565f, 0.f); //x3, y3 
cv::Point3f screenGeometry::bottomLeft=cv::Point3f(-0.04f, 0.0565f, 0.f);//x2, y2 
cv::Point3f screenGeometry::bottomRight=cv::Point3f(0.04f, 0.0465f, 0.f);//x1, y1 

}//namespace