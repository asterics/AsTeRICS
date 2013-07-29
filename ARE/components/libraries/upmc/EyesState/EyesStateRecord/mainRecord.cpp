//============================================================================
// Name        : blink.cpp
// Author      : Andrea Carbone
// Version     :
// Copyright   : 
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>

#include "EyesStateRecordApp.h"


int main(int argc, const char* argv[])
{
	try
	{
		upmc::EyesStatesRecordApp app;
		app.run(argc, argv);
	}
	catch (const std::exception &e)
	{
		std::cout << "Error: " << e.what() << std::endl;
		return -1;
	}
	return 0;
}
