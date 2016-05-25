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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 *   Interfaces the 3D Mouse Library for the 3D Mouse plugin.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jul 05, 2011
 *         Time: 11:51:00 AM
 */

#include "Mouse3DBridge.h"
#include <Windows.h>
#include "Mouse3DBridgeErrors.h"

typedef int  (__stdcall *Init) ();
typedef int  (__stdcall *Close) ();
typedef int  (__stdcall *Get3DMouseState)(long *x, long *y, long *z, long *Rx, long *Ry, long *Rz, long* buttons);

Init init=NULL;
Close close=NULL;
Get3DMouseState get3DMouseState=NULL;

HINSTANCE hLib;
bool initated=false;

/**
 * Clears initialized data.
 */
void Clear()
{
	FreeLibrary((HMODULE)hLib);
	init=NULL;
	close=NULL;
	get3DMouseState=NULL;
	hLib=NULL;
	initated=false;
}

/**
* Activates the library.
* @return  if the returned value is less then 0, the value is an error number.
*/

JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_spacenavigtor3Dmouse_SpaceNavigtor3DMouseBridge_activate
  (JNIEnv *, jobject)
{
	if(initated)
	{
		return library_initialized;
	}
	
	hLib=LoadLibrary(L"Mouse3Dlibrary.dll");

	if(hLib==NULL)
	{
		return dll_library_not_found;
	}

	init=(Init)GetProcAddress((HMODULE)hLib, "init");
	if(init==NULL)
	{
		Clear();
		return get_function_error;
	}

	close=(Close)GetProcAddress((HMODULE)hLib, "close");
	if(close==NULL)
	{
		Clear();
		return get_function_error;
	}

	get3DMouseState=(Get3DMouseState)GetProcAddress((HMODULE)hLib, "get3DMouseState");
	if(init==NULL)
	{
		Clear();
		return get_function_error;
	}

	int nResult = init();

	if(nResult<0)
	{
		Clear();
		return nResult;
	}

	initated=true;

	return 1;
}

/**
* Dectivates the library.
* @return  if the returned value is less then 0, the value is an error number.
*/
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_spacenavigtor3Dmouse_SpaceNavigtor3DMouseBridge_deactivate
  (JNIEnv *, jobject)
{
	
	if(initated==false)
	{
		return library_no_initialized;
	}

	int nResult=close();
	if(nResult<0)
	{
		return nResult;
	}

	Clear();

	return 1;
}

long tx;
long ty;
long tz;

long tRx;
long tRy;
long tRz;

long buttons;

/**
* Reads the 3D mouse data.
* @param env  environment data. 
* @param outArray  Array of the mouse data.
* @return  if the returned value is less then 0, the value is an error number.
*/
JNIEXPORT jint JNICALL Java_eu_asterics_component_sensor_spacenavigtor3Dmouse_SpaceNavigtor3DMouseBridge_getData
  (JNIEnv * env, jobject, jlongArray outArray)
{
	if(initated==false)
	{
		return library_no_initialized;
	}

	if(outArray==NULL)
	{
		return out_array_null;
	}

	get3DMouseState(&tx,&ty,&tz,&tRx,&tRy,&tRz,&buttons);

	jlong* dataOutArray = env->GetLongArrayElements(outArray, 0);

	if(dataOutArray==NULL)
	{
		return array_null;
	}


	dataOutArray[0]=tx;
	dataOutArray[1]=ty;
	dataOutArray[2]=tz;

	dataOutArray[3]=tRx;
	dataOutArray[4]=tRy;
	dataOutArray[5]=tRz;

	dataOutArray[6]=buttons;

	env->ReleaseLongArrayElements(outArray, dataOutArray, 0);

	return 1;
}
