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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

/**
 * This library interfaces GSM modem library fot the GSM modem plugin.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 11, 2011
 *         Time: 3:22:17 PM
 */

#include"GSMModemBridge.h"
#include"GSMModemLibrary.h"
#include"GSMModemErrors.h"
//#include<iostream>

bool initated=false;
HINSTANCE hLib;

Init init=NULL;
Close close=NULL;
SendSMS sendSMS=NULL;
GetModemPortNumber getModemPortNumber=NULL;

static JavaVM * g_jvm;
static jobject g_obj = NULL;
bool ready=false;

/**
 * Clears the library.
 *
 * @return 1
 */
int clear ()
{
	FreeLibrary((HMODULE)hLib);
	hLib=NULL;
	init =NULL;
	close =NULL;
	sendSMS =NULL;
	getModemPortNumber=NULL;
	initated=false;
	//CloseHandle(hEventDeviceFound);
	return 1;
}


/**
 * Activates the library.
 *
 * @return if the returned value is less then 0, the value is an error number
 */
int activate ()
{
	
	hLib=LoadLibrary(L"GSMModemLibrary.dll");

	
	if(hLib==NULL)
	{
		return dll_library_not_found;
	}

	init=(Init)GetProcAddress((HMODULE)hLib, "init");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	sendSMS=(SendSMS)GetProcAddress((HMODULE)hLib, "sendSMS");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	getModemPortNumber=(GetModemPortNumber)GetProcAddress((HMODULE)hLib, "getModemPortNumber");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	close=(Close)GetProcAddress((HMODULE)hLib, "close");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	initated=true;

	//hEventDeviceFound=CreateEvent(NULL,TRUE,FALSE,NULL);

	return 1;
}

/**
 * Called when the new SMS is available.
 *
 * @param PhoneID sender phone ID
 * @param subject SMS content
 * @param param parameter given by user
 */
void __stdcall newSMS (LPCWSTR phoneID, LPCWSTR subject, LPVOID param)
{
	JNIEnv *env;
	
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID sms_callback = env->GetMethodID(cls, "newSMS_callback", "(Ljava/lang/String;Ljava/lang/String;)V");

	jsize jPhoneSize = wcslen(phoneID);
	jsize jSubjectSize = wcslen(subject);
	jstring jPhoneID = env->NewString((jchar*)phoneID,jPhoneSize);
	jstring jSubject=env->NewString((jchar*)subject,jSubjectSize);
	
	env->CallVoidMethod(g_obj, sms_callback,jPhoneID,jSubject);
	env->DeleteLocalRef(jPhoneID);
	env->DeleteLocalRef(jSubject);
}

bool started=false;
const int bufferSize=100;
wchar_t pinParameter[bufferSize*2];
wchar_t centerParameter[bufferSize*2];

int portCount=0;
int portIndex=0;
const int MaxPort=10;
wchar_t usedPort[MaxPort][bufferSize];

/**
 * Called when the error is found.
 *
 * @param result error code
 * @param param parameter given by user
 */
void __stdcall errorCallback (int result, LPVOID param)
{
	int initResult=1;

	if(result==2)
	{
		ready=true;
		started=false;
	}
	else
	{
		if(result<0)
		{
			if(started)
			{
				portIndex++;
				if(portIndex<portCount)
				{
					close();
					Sleep(500);
					int initResult=init(usedPort[portIndex],newSMS,errorCallback,pinParameter,centerParameter,NULL);
				}
			}
		}
	}
	

	JNIEnv *env;
	
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID error_callback = env->GetMethodID(cls, "error_callback", "(I)V");
	env->CallVoidMethod(g_obj, error_callback,result);
	if(initResult<0)
	{
		env->CallVoidMethod(g_obj, error_callback,initResult);
	}
	

}



/**
 * This function gets first modem available.
 *
 * @param port modem port
 * @param modemName Name of the modem
 * @param param parameter given by user
 */
void __stdcall modemSearchResult (LPCWSTR port,LPCWSTR modemName, LPVOID param)
{
	if(wcslen(port)>0)
	{
		if(portCount<MaxPort)
		{
			int portLen=wcslen(port);

			if(portLen>bufferSize)
			{
				//to do
				return;
			}

			portCount++;

			wcscpy_s(usedPort[portCount-1],bufferSize,port);
			usedPort[portCount-1][portLen]=0;
		}

	}
}


/**
 * Activates the library.
 *
 * @param env environment variable
 * @param obj
 * @param port modem port
 * @param pin SIM card PIN
 * @param smsCenter SMS center ID
 * @return error code
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_gsmmodem_GSMModemBridge_activate
  (JNIEnv * env, jobject obj, jstring port, jstring pin, jstring smsCenter)
{
	int result=0;
	ready=false;

	if(initated)
	{
		return Library_initialized;
	}


	result = activate();


	if(result<0)
	{
		return result;
	}

	const jchar* sPort = env->GetStringChars(port, NULL);
	int nPortSize=env->GetStringLength(port);
	wchar_t * sPortString = new wchar_t [nPortSize+1];
	memcpy(sPortString, sPort, nPortSize*2);
	sPortString[nPortSize]=0;

	const jchar* sPin = env->GetStringChars(pin, NULL);
	int nPinSize=env->GetStringLength(pin);
	wchar_t * sPinString = new wchar_t [nPinSize+1];
	memcpy(sPinString, sPin, nPinSize*2);
	sPinString[nPinSize]=0;

	const jchar* sSmsCenter = env->GetStringChars(smsCenter, NULL);
	int nSmsCenterSize=env->GetStringLength(smsCenter);
	wchar_t * sSmsCenterString = new wchar_t [nSmsCenterSize+1];
	memcpy(sSmsCenterString, sSmsCenter, nSmsCenterSize*2);
	sSmsCenterString[nSmsCenterSize]=0;

	if(nPinSize<2*bufferSize)
	{
		wcscpy_s(pinParameter,2*bufferSize,sPinString);
		pinParameter[nPinSize]=0;
	}
	else
	{
		pinParameter[0]=0;
	}

	if(nSmsCenterSize<2*bufferSize)
	{
		wcscpy_s(centerParameter,2*bufferSize,sSmsCenterString);
		centerParameter[nSmsCenterSize]=0;
	}
	else
	{
		centerParameter[0]=0;
	}

	if(wcslen(sPortString)>0)
	{
		result=init(sPortString,newSMS,errorCallback,sPinString,sSmsCenterString,NULL);
	}
	else
	{
		portCount=0;
		result=getModemPortNumber(modemSearchResult,NULL);
		Sleep(500);
		if(portCount==0)
		{
			clear ();
			return modem_not_found;
		}
		portIndex=0;
		started=true;
		result=init(usedPort[portIndex],newSMS,errorCallback,sPinString,sSmsCenterString,NULL);
	}

	env->ReleaseStringChars(port, sPort);
	delete[] sPortString;
	env->ReleaseStringChars(pin, sPin);
	delete[] sPinString;
	env->ReleaseStringChars(smsCenter, sSmsCenter);
	delete[] sSmsCenterString;

	result=env->GetJavaVM(&g_jvm);
	g_obj = env->NewGlobalRef(obj);

	if(result<0)
	{
		close();
		clear ();
		env->DeleteGlobalRef(g_obj);
		return result;
	}

	return 1;
}

/**
 * Sends SMS.
 *
 * @param env environment variable.
 * @param phoneID receiver Phone ID
 * @param smsContent SMS content
 * @return error code
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_gsmmodem_GSMModemBridge_sendSMS
  (JNIEnv *env, jobject, jstring phoneID, jstring smsContent)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	if(!ready)
	{
		return library_not_ready;
	}

	int nPhoneIDSize=env->GetStringLength(phoneID);
	int sSMSContentSize=env->GetStringLength(smsContent);

	if(nPhoneIDSize==0||sSMSContentSize==0)
	{
		return message_or_phoneID_empty;
	}

	const jchar * sPhoneID = env->GetStringChars(phoneID, NULL);

	const jchar * sSMSContent = env->GetStringChars(smsContent, NULL);
	

	wchar_t * sPhoneString = new wchar_t [nPhoneIDSize+1];
	wchar_t * sSMSContentString = new wchar_t [sSMSContentSize+1];

	memcpy(sPhoneString, sPhoneID, nPhoneIDSize*2);
	sPhoneString[nPhoneIDSize]=0;

	memcpy(sSMSContentString, sSMSContent, sSMSContentSize*2);
	sSMSContentString[sSMSContentSize]=0;

	int result=sendSMS(sPhoneString,sSMSContentString);

	env->ReleaseStringChars(smsContent, sSMSContent);
	env->ReleaseStringChars(phoneID, sPhoneID);

	delete[] sPhoneString;
	delete[] sSMSContentString;

	return result;
}

/**
 * Closes the library
 *
 * @return error code
 */

JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_gsmmodem_GSMModemBridge_close
  (JNIEnv *env, jobject)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	int result=0;
	result=close();

	clear ();

	env->DeleteGlobalRef(g_obj);
	return 1;
}