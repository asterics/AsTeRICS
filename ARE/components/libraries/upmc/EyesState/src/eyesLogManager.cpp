/*
 * eyesLogManager.cpp
 *
 *  Created on: 11 May 2012
 *      Author: andera
 */

#include "eyesLogManager.h"
#include <sstream>
#include <algorithm>
#include <opencv2/highgui/highgui.hpp>
////////////////////////////////////////////////////////
namespace upmc {
////////////////////////////////////////////////////////
EyesLogger::EyesLogger() {


}
////////////////////////////////////////////////////////
EyesLogger::~EyesLogger() {

}
////////////////////////////////////////////////////////
void upmc::EyesLogger::resample(size_t howmany) {
	//modify vectors
	//	//Balance  data
	std::vector<int> openIdx;//will be a subset of closeIdx or openIdx depending on who's larger.
	std::vector<int> closeIdx;

	//get correct size
	howmany=std::min(howmany, std::min(_opened_pair.size(), _closed_pair.size()));

	cv::RNG rng=cv::randu<int>();

	closeIdx.resize(howmany) ;
	openIdx.resize(howmany) ;

	//
	std::vector<std::pair<cv::Mat, cv::Mat> > opened_tmp(howmany);
	std::vector<std::pair<cv::Mat, cv::Mat> > closed_tmp(howmany);

	for(size_t ii=0; ii<howmany; ii++){
		opened_tmp[ii]=_opened_pair[rng(_opened_pair.size())];
		closed_tmp[ii]=_closed_pair[rng(_closed_pair.size())];
	}

	//resize and rewrite the vectors
	_opened_pair.clear();
	_closed_pair.clear();

	_opened_pair.resize(howmany);
	_closed_pair.resize(howmany);

	//can do that?
	std::swap(_opened_pair, opened_tmp);
	std::swap(_closed_pair, closed_tmp);
}
////////////////////////////////////////////////////////
void EyesLogger::clear() {
	_closed_pair.clear();
	_opened_pair.clear();
}
////////////////////////////////////////////////////////
upmc::dataSet upmc::EyesLogger::get(size_t howmany) {
	//	//Balance  data
	std::vector<int> openIdx;//will be a subset of closeIdx or openIdx depending on who's larger.
	std::vector<int> closeIdx;

	//get correct size
	howmany=std::min(howmany, std::min(_opened_pair.size(), _closed_pair.size()));

	cv::RNG rng=cv::randu<int>();

	//if(_closed_pair.size() > _opened_pair.size()){
	closeIdx.resize(howmany) ;
	openIdx.resize(howmany) ;
	for(size_t ii=0; ii<howmany; ii++){
		closeIdx[ii]=rng(_closed_pair.size());//generates random indices
		openIdx[ii]=rng(_opened_pair.size());//generates random indices
	}
	//}
	//	else{
	//		closeIdx.resize(howmany) ;
	//		openIdx.resize(howmany) ;
	//		for(size_t ii=0; ii<howmany; ii++){
	//			closeIdx[ii]=rng(_closed_pair.size());//generates random indices
	//			openIdx[ii]=rng(_opened_pair.size());//generates random indices
	//		}
	//	}

	return _get(openIdx, closeIdx);
}
////////////////////////////////////////////////////////
upmc::dataSet upmc::EyesLogger::get() {
	//	//Balance  data
	std::vector<int> openIdx;//will be a subset of closeIdx or openIdx depending on who's larger.
	std::vector<int> closeIdx;

	if(_closed_pair.size()!=_opened_pair.size())
	{
		cv::RNG rng=cv::randu<int>();

		if(_closed_pair.size() > _opened_pair.size()){
			closeIdx.resize(_opened_pair.size()) ;
			openIdx.resize(_opened_pair.size()) ;
			for(size_t ii=0; ii<closeIdx.size(); ii++){
				closeIdx[ii]=rng(_closed_pair.size());//generates random indices
				openIdx[ii]=ii;
			}
		}
		else{
			closeIdx.resize(_closed_pair.size()) ;
			openIdx.resize(_closed_pair.size()) ;
			for(size_t ii=0; ii<_closed_pair.size(); ii++){
				openIdx[ii]=rng(_opened_pair.size());//generates random indices
				closeIdx[ii]=ii;
			}

		}
	}
	else{
		closeIdx.resize(_closed_pair.size()) ;
		openIdx.resize(_closed_pair.size()) ;
		for(size_t ii=0; ii<_closed_pair.size(); ii++){
			openIdx[ii]=ii;
			closeIdx[ii]=ii;
		}
	}

	return _get(openIdx, closeIdx);
}
////////////////////////////////////////////////////////
upmc::dataSet upmc::EyesLogger::_get(std::vector<int> openIdx,
		std::vector<int> closeIdx) {

	CV_Assert(openIdx.size()==closeIdx.size());
	upmc::dataSet dataset;

	std::vector<int>::const_iterator oit, oitend, cit, citend;
	oit=openIdx.begin();	oitend=openIdx.end();
	cit=closeIdx.begin();	citend=closeIdx.end();

	//openIdx.size()==closeIdx.size()
	dataset.data.create(openIdx.size()*4, _szimg.width*_szimg.height, CV_32FC1);
	dataset.labels.create(openIdx.size()*4, 1, CV_32FC1);
	int i=0;
	//
	for(; oit!=oitend, cit!=citend; ++oit, ++cit){
		//2 images each iterator
		//TODO: scaling is not actually necessary here in the current settings.
		dataset.labels.at<float>(i,0)=1;
		_opened_pair[*oit].first.reshape(1,1).convertTo(dataset.data.row(i++), CV_32FC1, 1/255.);
		//(*oit).first.reshape(1,1).copyTo(dataset.data.row(i++));

		dataset.labels.at<float>(i,0)=1;
		_opened_pair[*oit].second.reshape(1,1).convertTo(dataset.data.row(i++), CV_32FC1, 1/255.);
		//(*oit).second.reshape(1,1).copyTo(dataset.data.row(i++));

		dataset.labels.at<float>(i,0)=-1;
		_closed_pair[*cit].first.reshape(1,1).convertTo(dataset.data.row(i++), CV_32FC1, 1/255.);
		//(*cit).first.reshape(1,1).copyTo(dataset.data.row(i++));

		dataset.labels.at<float>(i,0)=-1;
		_closed_pair[*cit].second.reshape(1,1).convertTo(dataset.data.row(i++), CV_32FC1, 1/255.);
		//(*cit).second.reshape(1,1).copyTo(dataset.data.row(i++));

	}

	dataset.imgsize=_szimg;

	return dataset;
}
////////////////////////////////////////////////////////
void upmc::EyesLogger::add(const upmc::EyePair& eyepair,
		upmc::eEye eyestate) {

	cv::Mat left;
	cv::flip(eyepair.left, left,1);

	if(eyestate==eOPEN){
		_opened_pair.push_back(std::make_pair(left.clone(), eyepair.right.clone()));
	}
	else{
		_closed_pair.push_back(std::make_pair(left.clone(), eyepair.right.clone()));
	}

}
////////////////////////////////////////////////////////
void upmc::EyesLogger::read(const cv::FileNode& node) {

	cv::FileNodeIterator it, itend;
	cv::Mat left, right;
	int opencnt, closecnt;
	// TODO: shall I check for emptyness? So that I could read multiple logs?
	node["openedCnt"] >> opencnt;
	_opened_pair.reserve(opencnt);

	node["closedCnt"] >> closecnt;
	_closed_pair.reserve(closecnt);

	cv::FileNode nodeTmp;

	nodeTmp=node["imgSize"];
	nodeTmp["width"]  >> _szimg.width;
	nodeTmp["height"] >> _szimg.height;

	std::ostringstream name;

	for(int i=0; i< opencnt; i++){
		name.str("");
		name << "open" << i;
		nodeTmp = node[name.str()];

		nodeTmp["left"] >> left;
		nodeTmp["right"] >> right;
		_opened_pair.push_back(std::make_pair(left.clone(), right.clone()));
	}

	for(int i=0; i< closecnt; i++){
		name.str("");
		name << "close" << i;
		nodeTmp = node[name.str()];

		nodeTmp["left"] >> left;
		nodeTmp["right"] >> right;
		_closed_pair.push_back(std::make_pair(left.clone(), right.clone()));
	}

	std::cout << "Read " << _opened_pair.size() << " Opened Pairs." << std::endl
			<< "Read "	<< _closed_pair.size() 	<< " Closed Pairs" 	<< std::endl;
}
////////////////////////////////////////////////////////
void upmc::EyesLogger::write(cv::FileStorage& fs) const{
	std::vector<std::pair<cv::Mat, cv::Mat> >::const_iterator it,itend;
	std::ostringstream name;
	//-------------------------------------------
	//int width, height;
	//-------------------------------------------
	fs << "{";
	//-------------------------------------------
	it=_opened_pair.begin();
	itend=_opened_pair.end();
	for(int i=0;it!=itend;++it, ++i)
	{
		name.str("");
		name << "open" << i;
		fs << name.str() << "{"
				<< "left" << (*it).first
				<< "right" << (*it).second
				<< "}";
	}
	//-------------------------------------------
	it=_closed_pair.begin();
	itend=_closed_pair.end();
	for(int i=0;it!=itend;++it, ++i)
	{
		name.str("");
		name << "close" << i;
		fs << name.str() << "{"
				<< "left" << (*it).first
				<< "right" << (*it).second
				<< "}";
	}
	//-------------------------------------------
	if(!(_closed_pair.empty()|| _opened_pair.empty())){
		//-------------------------------------------
		fs << "closedCnt" << (int)_closed_pair.size();//#of pairs
		fs << "openedCnt" << (int)_opened_pair.size();//#of pairs
		//-------------------------------------------
		fs << "imgSize" << "{"
			<< "width" << _opened_pair.begin()->first.cols
			<< "height" << _opened_pair.begin()->first.rows
			<< "}";
	}
	else {
				//-------------------------------------------
		fs << "closedCnt" << 0;//#of pairs
		fs << "openedCnt" << 0;//#of pairs
		//-------------------------------------------
		fs << "imgSize" << "{"
			<< "width" <<0
			<< "height" << 0
			<< "}";
	}
	//-------------------------------------------
	fs << "}";
}
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
void write(cv::FileStorage& fs, const std::string&, const upmc::EyesLogger& data) {
	data.write(fs);
}
////////////////////////////////////////////////////////
void read(const cv::FileNode& node, upmc::EyesLogger& data,
		const upmc::EyesLogger& default_value) {
	if(node.empty())
		data = default_value;
	else
		data.read(node);
}
////////////////////////////////////////////////////////
////////////////////////////////////////////////////////
void EyesLogger::playWithOpencv(int msec) const{
	std::vector<std::pair<cv::Mat, cv::Mat> >::const_iterator
	it(_opened_pair.begin()), itend(_opened_pair.end());

	std::cout << "Opened: " << _opened_pair.size() << " pairs." << std::endl;

	for (; it!=itend; ++it){
		cv::Mat itmp;
		upmc::joinSingleChannelImages(it->first, it->second, itmp);
		cv::imshow("open eyepair", itmp);
		cv::waitKey(33);
	}

	std::cout << "Closed: " << _closed_pair.size() << " pairs." << std::endl;
	it=_closed_pair.begin();
	itend=_closed_pair.end();

	for (; it!=itend; ++it){
		cv::Mat itmp;
		upmc::joinSingleChannelImages((*it).first, (*it).second, itmp);
		cv::imshow("close eyepair", itmp);
		cv::waitKey(msec);
	}

	//cv::normalize(recon, recon, 0, 255, cv::NORM_MINMAX, CV_8UC1);
}
////////////////////////////////////////////////////////
//void EyesLogger::saveToDisk() const {
////	//std::vector<std::pair<cv::Mat, cv::Mat> >::const_iterator it, itend;
////	std::ostringstream name;
////	//	std::cout << "Pairs: " << _pair.size()  << std::endl;
////	//	//cv::Mat itmp;
////	//	for (size_t i=0; i<_pair.size(); i++){
////	//		//upmc::joinSingleChannelImages(_opened.at(i).first, _opened.at(i).second, itmp);
////	//		name.str("");
////	//		name << "pair_" << i << ".png";
////	//		cv::imwrite(name.str(), _pair[i]);
////	//	}
////	//	it=_opened.begin();
////	//	itend=_opened.end();
////	//
////	for (size_t i=0; i<_opened_pair.size(); i++){
////		name.str("");
////		name << "left_" << i << ".png";
////		cv::imwrite(name.str(), _opened_pair[i].first);
////		name.str("");
////		name << "right_" << i << ".png";
////		cv::imwrite(name.str(), _opened_pair[i].second);
////	}
////
////	//	std::cout << "Closed: " << _closed.size() << " pairs." << std::endl;
////	//	it=_closed.begin();
////	//	itend=_closed.end();
////	//	for (; it!=itend; ++it){
////	//		upmc::joinSingleChannelImages((*it).first, (*it).second, itmp);
////	//		cv::imshow("eyepair", itmp);
////	//		cv::waitKey(33);
////	//	}
//}
////////////////////////////////////////////////////////
}
//namespace upmc

