/*
 * main.cpp
 *
 *  Created on: 7 May 2012
 *      Author: Andrea Carbone
 *      carbone@isir.upmc.fr
 */
/////////////////////////////////////////////
#include <iostream>
/////////////////////////////////////////////
#include "EyesStateTrainApp.h"
/////////////////////////////////////////////
int main(int argc, const char* argv[])
{
	try
	{
		upmc::EyesStateTrainApp app;
		app.run(argc, argv);
	}
	catch (const std::exception &e)
	{
		std::cout << "Error: " << e.what() << std::endl;
		return -1;
	}
	return 0;
}


