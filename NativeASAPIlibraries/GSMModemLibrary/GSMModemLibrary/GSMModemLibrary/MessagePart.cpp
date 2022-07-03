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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */

/**
 * This class contains the part of the multipart message.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include "StdAfx.h"
#include "MessagePart.h"

/**
 * The class constructor.
 */
MessagePart::MessagePart(void)
{
	message=L"";
	positionInMemory=0;
	numberOfPart=0;
}

/**
 * The class destructor.
 */
MessagePart::~MessagePart(void)
{
}

/**
 * This function returns true if number of the first MessagePart is smaller than the number of the second MessagePart, otherwise it returns false.
 * @param  first first message part
 * @param second second message part
 * @return result of comparison
 */
bool MessagePart::compare( MessagePart first, MessagePart second)
{
	if(first.numberOfPart<second.numberOfPart)
	{
		return true;
	}

	return false;
}