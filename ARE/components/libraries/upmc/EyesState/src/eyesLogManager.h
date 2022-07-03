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

/*
 * eyesLogManager.h
 *
 *  Created on: 11 May 2012
 *      Author: andera
 */

#ifndef EYESLOGMANAGER_H_
#define EYESLOGMANAGER_H_

#include <opencv2/core/core.hpp>
#include <iostream>
#include <utility>
//#include <pair>
#include <vector>
#include <list>
#include "BlinkCommon.h"

namespace upmc {

/*
 *
 */
class EyesLogger {
public:
	EyesLogger();
	virtual ~EyesLogger();

public:
	//void create(std::string name);
	//void append();
	//vod save();
	void resample(size_t nsamples);
	void clear();

public://Interface for file storage
	void read(const cv::FileNode& node);
	void write(cv::FileStorage& fs) const;

public:
	upmc::dataSet get(size_t howmany);
	upmc::dataSet get();

public://infos
	cv::Size 	getImSize() 		const {return _szimg;}
	size_t 		getOpenedNumPairs() const {return _opened_pair.size();}
	size_t 		getClosedNumPairs() const {return _closed_pair.size();}
	void 		playWithOpencv(int msec=30) 	const;
	//void 		saveToDisk() 		const;

private:
	upmc::dataSet _get(std::vector<int> openIdx, std::vector<int> closeIdx);

public:
	void add(const upmc::EyePair& eyepair, upmc::eEye);

private:
	std::string _name;
	//let's keep separated left and right
	std::vector<std::pair<cv::Mat, cv::Mat> > _opened_pair;
	std::vector<std::pair<cv::Mat, cv::Mat> > _closed_pair;
	//std::vector<cv::Mat> _pair;
	cv::Size _szimg;
};
////////////////////////////////////////////////////////
void write(cv::FileStorage& fs, const std::string&,const upmc::EyesLogger& data);
void read(const cv::FileNode& node, upmc::EyesLogger& data, const upmc::EyesLogger& default_value=upmc::EyesLogger());
} /* namespace upmc */



#endif /* EYESLOGMANAGER_H_ */
