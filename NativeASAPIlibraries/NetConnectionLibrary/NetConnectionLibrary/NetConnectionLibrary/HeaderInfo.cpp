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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

#include "StdAfx.h"
#include "HeaderInfo.h"

/**
 * 
 * This class stores the header data
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Sep 14, 2012
 *         Time: 13:24:48 AM
 */

/**
* The class constructor
*/
HeaderInfo::HeaderInfo(void)
{
	command=None;
	int dataSize=0;
	int port=0;
}

/**
* Sets the header data.
* @param command command type
* @param port the command port
* @param dataSize size of the command data
*/
HeaderInfo::HeaderInfo(Command command,int port, int dataSize)
{
	this->command=command;
	this->dataSize=dataSize;
	this->port=port;
}

/**
* The class destructor.
*/
HeaderInfo::~HeaderInfo(void)
{
}

/**
* Returns the command.
* @return the command
*/
Command HeaderInfo::getCommand(){
	return command;
}
	
/**
* Returns the data size.
* @return the data size
*/
int HeaderInfo::getDataSize(){
	return dataSize;
}
	
/**
* Returns the port.
* @return the port
*/
int HeaderInfo::getPort(){
	return port;
}
	
/**
* Sets the data size.
* @param dataSize data size
*/
void HeaderInfo::setDataSize(int dataSize)
{
	this->dataSize=dataSize;
}
	
/**
* Sets the command.
* @param command command type.
*/
void HeaderInfo::setCommand(Command command)
{
	this->command=command;
}
	
/**
* Sets the command port.
* @param port command port
*/
void HeaderInfo::setPort(int port){
	this->port=port;
}
	