#ifndef _UPMC_EYESSTATES_DETECTOR_APP_H_INCLUDED_
#define _UPMC_EYESSTATES_DETECTOR_APP_H_INCLUDED_

#include <string>

namespace upmc{

	class EyesStatesDetectorApp{
	
	public:
		EyesStatesDetectorApp();

	public:
		void run(int argc, const char* argv[]);
	
	private:
		//
		//bool _parseHelpCmdArg(int& i, int argc, const char* argv[]){return true;};
		bool _parseCmdArgs(int& i, int argc, const char* argv[]);
		//
		void _process();

	private:
		int _device;
		std::string _svmfile;
		std::string _pcafile;
	};

};//namespace

#endif //_UPMC_EYESSTATES_DETECTOR_APP_H_INCLUDED_
