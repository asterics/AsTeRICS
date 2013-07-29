/*
 * EyesStateTrainApp.h
 *
 *  Created on: 19 May 2012
 *      Author: andera
 */

#ifndef EYESSTATETRAINAPP_H_
#define EYESSTATETRAINAPP_H_

#include <string>
#include "autolink.hpp"

namespace upmc {

/*
 *
 */
class EyesStateTrainApp {
public:
	EyesStateTrainApp();
	virtual ~EyesStateTrainApp();

public:
	void run(int argc, const char* argv[]);

private:
	//
	bool _parseHelpCmdArg(int& i, int argc, const char* argv[]);
	bool _parseCmdArgs(int& i, int argc, const char* argv[]);
	//
	void _process();

private:
	int _neigens;
	int _nsamples;
	int _resample;
	std::string _yamlfile;
	std::string _svmlog;
	float _eigenratio;
//	std::string _pcalog;
};

} /* namespace upmc */
#endif /* EYESSTATETRAINAPP_H_ */
