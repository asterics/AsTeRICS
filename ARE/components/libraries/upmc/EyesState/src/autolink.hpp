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

#ifndef _UPMC_AUTOLINK_HPP_INCLUDED_
#define _UPMC_AUTOLINK_HPP_INCLUDED_

#if defined(WIN32)

#if defined(_DEBUG)
#define _LIB_SUFFIX_ "d.lib"
//#pragma comment(lib, "tbb_debug"  _LIB_SUFFIX_)
#else
#define _LIB_SUFFIX_ ".lib"
//#pragma comment(lib, "tbb"  _LIB_SUFFIX_)
#endif

#define OPENCV_LIB_VER "242"

#pragma comment(lib, "opencv_core" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "opencv_highgui" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "opencv_calib3d" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "opencv_imgproc" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "opencv_video" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "opencv_ml" OPENCV_LIB_VER _LIB_SUFFIX_)

//#pragma comment(lib, "opencv_features2d" OPENCV_LIB_VER _LIB_SUFFIX_)
//#pragma comment(lib, "opencv_legacy" OPENCV_LIB_VER _LIB_SUFFIX_)
//#pragma comment(lib, "opencv_flann" OPENCV_LIB_VER _LIB_SUFFIX_)
#pragma comment(lib, "zlib")
#pragma comment(lib, "libjasper")
#pragma comment(lib, "libpng")
#pragma comment(lib, "libtiff")
#pragma comment(lib, "libjpeg")
#pragma comment(lib, "comctl32")
#pragma comment(lib, "vfw32")

#endif //WIN32

#endif //_UPMC_AUTOLINK_HPP_INCLUDED_
//#pragma comment(lib, "facetrackerLib" _LIB_SUFFIX_)
