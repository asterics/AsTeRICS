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

#ifndef _UPMC_EYESSTATES_RECORD_APP_H_INCLUDED_
#define _UPMC_EYESSTATES_RECORD_APP_H_INCLUDED_

#include <opencv2/core/core.hpp>

namespace upmc{

	class EyesStatesRecordApp{
	
	public:
		EyesStatesRecordApp();

	public:
		void run(int argc, const char* argv[]);
	
	private:
		//
		bool _parseHelpCmdArg(int& i, int argc, const char* argv[]);
		bool _parseCmdArgs(int& i, int argc, const char* argv[]);
		//
		void _process();

	private:
		bool _append;
		int _device;
		size_t _width;
		int _resample;
		std::string _yamlfile;
		size_t _image_resol;
	};

};//namespace

#endif //_UPMC_EYESSTATES_RECORD_APP_H_INCLUDED_
