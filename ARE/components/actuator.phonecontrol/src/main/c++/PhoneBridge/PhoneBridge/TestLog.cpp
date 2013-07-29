#include "TestLog.h"


TestLog::TestLog(int logNB)
{
	fileNB=logNB;
	fileName="c:\\logx\\log";
	stringstream strm;
	strm<<fileNB;
	fileName=fileName+strm.str();
	fileName=fileName+".txt";
}


TestLog::~TestLog(void)
{
}

/*
void TestLog::log(string logThis)
{
	ofstream out(fileName,ios::app|ios::ate|ios::out);
	out<<logThis<<endl;
	out.close();
}*/

void TestLog::log(wchar_t* logThis)
{
	wofstream out(fileName,ios::app|ios::ate|ios::out);
	out<<logThis<<endl;
	out.close();
}

void TestLog::log(int logThis)
{
	ofstream out(fileName,ios::app|ios::ate|ios::out);
	out<<logThis<<endl;
	out.close();
}

/*
void TestLog::log(unsigned _int64 logThis)
{
	ofstream out(fileName,ios::app|ios::ate|ios::out);
	out<<logThis<<endl;
	out.close();
}*/
