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
