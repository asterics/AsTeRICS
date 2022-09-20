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


#pragma once

namespace Transform7BitUnicode
{

const int code7BitSize=128;

int septetToUnicode[code7BitSize]={
 0x40,
 0xA3,
 0x24,
 0xA5,
 0xE8,
 0xE9,
 0xF9,
 0xEC,
 0xF2,
 0xC7,
 0x0A,
 0xD8,
 0xF8,
 0x0D,
 0xC5,
 0xE5,
 0x394,
 0x5F,
 0x3A6,
 0x393,
 0x39B,
 0x3A9,
 0x3A0,
 0x3A8,
 0x3A3,
 0x398,
 0x39E,
 0x00,  //Extension
 0xC6,
 0xE6,
 0xDF,
 0xC9,
 0x20,
 0x21,
 0x22,
 0x23,
 0xA4,
 0x25,
 0x26,
 0x27,
 0x28,
 0x29,
 0x2A,
 0x2B,
 0x2C,
 0x2D,
 0x2E,
 0x2F,
 0x30,
 0x31,
 0x32,
 0x33,
 0x34,
 0x35,
 0x36,
 0x37,
 0x38,
 0x39,
 0x3A,
 0x3B,
 0x3C,
 0x3D,
 0x3E,
 0x3F,
 0xA1,
 0x41,
 0x42,
 0x43,
 0x44,
 0x45,
 0x46,
 0x47,
 0x48,
 0x49,
 0x4A,
 0x4B,
 0x4C,
 0x4D,
 0x4E,
 0x4F,
 0x50,
 0x51,
 0x52,
 0x53,
 0x54,
 0x55,
 0x56,
 0x57,
 0x58,
 0x59,
 0x5A,
 0xC4,
 0xD6,
 0xD1,
 0xDC,
 0xA7,
 0xBF,
 0x61,
 0x62,
 0x63,
 0x64,
 0x65,
 0x66,
 0x67,
 0x68,
 0x69,
 0x6A,
 0x6B,
 0x6C,
 0x6D,
 0x6E,
 0x6F,
 0x70,
 0x71,
 0x72,
 0x73,
 0x74,
 0x75,
 0x76,
 0x77,
 0x78,
 0x79,
 0x7A,
 0xE4,
 0xF6,
 0xF1,
 0xFC,
 0xE0
};

const int extensionValue=0x1b;

const int extensionTableSize=10;

int extensinoTable[extensionTableSize][2]={
////{7bit,unicode}
{10,0x0C},
{20,0x5E},
{40,0x7B},
{41,0x7D},
{47,0x5C},
{60,0x5B},
{61,0x7E},
{62,0x5D},
{64,0x7C},
{101,0x20AC}
};

/*
int septetFromUnicode(int unicode)
{
	for(int i =0;i< code7BitSize;i++)
	{
		if((septetToUnicode[i]==unicode)&&(i!=extensionValue))
		{
			return i;
		}

	}

	return -1;

}

int septetFromExtenstionTable(int unicode)
{
	for(int i=0;i<extensionTableSize;i++)
	{
		if(extensinoTable[i][1]==unicode)
		{
			return extensinoTable[i][0];
		}
	}

	return -1;
}

int unicodeFromExtensionTable(int septet)
{
	for(int i=0;i<extensionTableSize;i++)
	{
		if(extensinoTable[i][0]==septet)
		{
			return extensinoTable[i][1];
		}
	}

	return -1;
}*/

}