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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

#ifndef UPMC_OPENCV_INC_H_INCLUDED
#define UPMC_OPENCV_INC_H_INCLUDED

#include <opencv2\opencv.hpp>

//#define _UPMC_USE_KINECT_

#if defined(_WIN32)

#if defined(NDEBUG)
#define OPENCV_LIB_SUFFIX ".lib"
#else
//#define OPENCV_LIB_SUFFIX "d.lib"
#endif

//#pragma comment(lib, "opencv_core231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_highgui231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_objdetect231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_imgproc231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_video231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_legacy231" OPENCV_LIB_SUFFIX)
//#pragma comment(lib, "opencv_flann231" OPENCV_LIB_SUFFIX)
//
//#pragma comment(lib, "OpenNI.lib")

#endif//_WIN32

#endif //UPMC_OPENCV_INC_H_INCLUDED