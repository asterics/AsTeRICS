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

#ifndef ASTERICS_OPENCV_INCLUDES_H_INCLUDED
#define ASTERICS_OPENCV_INCLUDES_H_INCLUDED

#if defined(__ASTERICS_USE_OPENCV097__)
#include "cv.h"
#include "highgui.h"

#pragma comment(lib, "cv.lib")
#pragma comment(lib, "cxcore.lib")
#pragma comment(lib, "highgui.lib")
#else

//#if defined(_WIN32)
#include <opencv2\opencv.hpp>

#if defined(_DEBUG)
#define OPENCV_LIB_SUFFIX "d.lib"
#else
#define OPENCV_LIB_SUFFIX ".lib"
#endif

#define OPENCV_LIB_VER "242"

#pragma comment(lib, "opencv_core" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_highgui" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_objdetect" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_imgproc" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_video" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)
#pragma comment(lib, "opencv_features2d" OPENCV_LIB_VER OPENCV_LIB_SUFFIX)

//Optional. Comment out if opencv does not need those.
//Typically when using the prebuilt verion of the opencv
//available from WillowGarage.
#pragma comment(lib, "zlib")
#pragma comment(lib, "libjasper")
#pragma comment(lib, "libpng")
#pragma comment(lib, "libtiff")
#pragma comment(lib, "libjpeg")

#endif
//#endif//_WIN32

//
#pragma comment(lib, "comctl32")
#pragma comment(lib, "Vfw32")

#endif //ASTERICS_OPENCV_INCLUDES_H_INCLUDED