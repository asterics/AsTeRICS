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
