#pragma once

#include<iostream>
#include <fstream> 
#include <string>
#include <sstream> 

using namespace std;

class TestLog
{
public:
	TestLog(int logNB);
	~TestLog(void);
	void log(string logThis);
	void log(int logThis);
	void log(unsigned _int64 logThis);
	void log(wchar_t* logThis);
private:
	int fileNB;
	string fileName;
};

