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
 * Interfaces the Phone Library for the AsTeRICS phone plugin
 *    
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Oct 14, 2011
 *         Time: 2:24:23 PM
 */

#include "PhoneBridge.h"
#include "PhoneLibrary.h"
#include "PhoneBridgeErrors.h"


bool initated=false;
HINSTANCE hLib;

Init init =NULL;
SearchDevices searchDevices =NULL;
Close close =NULL;
ConnectToDevice connectToDevice =NULL;
Disconnect disconnect  =NULL;
SendSMS sendSMS =NULL;
MakePhoneCall makePhoneCall =NULL;
AcceptCall acceptCall =NULL;
DropCall dropCall =NULL;
GetPhoneState getPhoneState =NULL;

//HANDLE hEventDeviceFound=NULL;

wchar_t sPhoneBTName[150];
int nPhoneBTNameSize;
unsigned _int64 nPhoneAddress;
int nPhonePort=-1;

bool connected=false;

static JavaVM * g_jvm;
static jobject g_obj = NULL;


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
	searchDevices =NULL;
	close =NULL;
	connectToDevice =NULL;
	disconnect  =NULL;
	sendSMS =NULL;
	makePhoneCall =NULL;
	acceptCall =NULL;
	dropCall =NULL;
	getPhoneState =NULL;
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
	
	hLib=LoadLibrary(L"PhoneLibrary.dll");

	
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

	searchDevices=(SearchDevices)GetProcAddress((HMODULE)hLib, "searchDevices");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	connectToDevice=(ConnectToDevice)GetProcAddress((HMODULE)hLib, "connectToDevice");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	disconnect=(Disconnect)GetProcAddress((HMODULE)hLib, "disconnect");
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

	makePhoneCall=(MakePhoneCall)GetProcAddress((HMODULE)hLib, "makePhoneCall");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	acceptCall=(AcceptCall)GetProcAddress((HMODULE)hLib, "acceptCall");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	dropCall=(DropCall)GetProcAddress((HMODULE)hLib, "dropCall");
	if(init==NULL)
	{
		clear();
		return get_function_error;
	}

	getPhoneState=(GetPhoneState)GetProcAddress((HMODULE)hLib, "getPhoneState");
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

bool bDeviceSearchFinish=false;

int devicePort;

/**
 * Called when the device search is finished
 */
void deviceFoundFinished()
{
	if(initated==false)
	{
		return;
	}

	JNIEnv *env;
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID state_callback = env->GetMethodID(cls, "connectionState_callback", "(I)V");
	
	int result=0;

	if(nPhoneAddress==0)
	{
		result=phone_not_found;
	}
	else
	{
		result= connectToDevice(nPhoneAddress,devicePort);
		nPhonePort=devicePort;
		if(result>0)
		{
			connected=true;
		}
	}

	env->CallVoidMethod(g_obj, state_callback, (jint)result);
}

/**
 * Called when the new Bluetooth device is found.
 *
 * @param deviceAddress device BT address
 * @param deviceName name of the device
 * @param param parameter given by user
 */
void __stdcall deviceFound (unsigned _int64 deviceAddress, LPWSTR deviceName, LPVOID param)
{

	if(deviceAddress!=0)
	{
		int deviceNameSize=wcslen(deviceName);
	
		if(deviceNameSize==nPhoneBTNameSize)
		{
			if(wcsncmp(sPhoneBTName,deviceName,nPhoneBTNameSize)==0)
			{
				if(bDeviceSearchFinish==false)
				{
					bDeviceSearchFinish=true;
					nPhoneAddress=deviceAddress;
				}
			}
		}

	}
	else
	{
		deviceFoundFinished();
	}
	
	
}

/**
 * Called when the new SMS is available.
 *
 * @param PhoneID sender phone ID
 * @param subject SMS content
 * @param param parameter given by user
 */
void __stdcall newSMS (LPWSTR PhoneID, LPWSTR subject, LPVOID param)
{
	JNIEnv *env;
	
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID sms_callback = env->GetMethodID(cls, "newSMS_callback", "(Ljava/lang/String;Ljava/lang/String;)V");

	jsize jPhoneSize = wcslen(PhoneID);
	jsize jSubjectSize = wcslen(subject);
	jstring jPhoneID = env->NewString((jchar*)PhoneID,jPhoneSize);
	jstring jSubject=env->NewString((jchar*)subject,jSubjectSize);
	
	env->CallVoidMethod(g_obj, sms_callback,jPhoneID,jSubject);
	env->DeleteLocalRef(jPhoneID);
	env->DeleteLocalRef(jSubject);
}

/**
 * Called when the phone state is changed.
 *
 * @param phoneState new phone state
 * @param PhoneID phone ID
 * @param param parameter given by user
 */
void __stdcall phoneStateChanged (PhoneState phoneState, LPWSTR phoneID , LPVOID param)
{
	JNIEnv *env;
	g_jvm->AttachCurrentThread((void **)&env, NULL);
	jclass cls = env->GetObjectClass(g_obj);
	jmethodID phone_callback = env->GetMethodID(cls, "phoneStateChange_callback", "(ILjava/lang/String;)V");

	jsize jPhoneSize = wcslen(phoneID);
	jstring jPhoneID = env->NewString((jchar*)phoneID,jPhoneSize);
	int state=0;

	switch(phoneState)
	{
	case PS_IDLE:
		state=1;
		break;
	case PS_RING:
		state=2;
		break;
	case PS_CONNECTED:
		state=3;
		break;
	}

	env->CallVoidMethod(g_obj, phone_callback, (jint)state,jPhoneID);
	env->DeleteLocalRef(jPhoneID);
}

/**
 * Searches the phone.
 *
 * @return if the returned value is less then 0, the value is an error number
 */
int searchPhone()
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	bDeviceSearchFinish=false;
	nPhoneAddress=0;
	int result=searchDevices();
	
	return result;

	/*
	if(result<0)
	{
		return result;
	}

	
	DWORD dwWaitResult;
	dwWaitResult = WaitForSingleObject(hEventDeviceFound,20*1000); //30s

	ResetEvent(hEventDeviceFound);
	
	if(dwWaitResult==WAIT_OBJECT_0)
	{
		if(nPhoneAddress==0)
		{
			return phone_not_found;
		}
		else
		{
			return 1;
		}
	}
	else if (dwWaitResult==WAIT_TIMEOUT)
	{
		return phone_search_error;
	}
	else
	{
		return phone_search_error;
	}
	

	//nPhoneAddress=0x17E8BCA048;
	return 1;*/
}

/**
 * Activates the library
 *
 * @param env environment variable
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_activate
  (JNIEnv * env, jobject obj, jstring phoneName, jint port)
{
	int result=0;
	

	if(initated)
	{
		return Library_initialized;
	}


	result = activate();


	if(result<0)
	{
		return Library_initialize_error;
	}

	result = init(deviceFound,newSMS,phoneStateChanged,0);

	if(result<0)
	{
		if(result==Library_initialized)
		{
			close();
			result = init(deviceFound,newSMS,phoneStateChanged,0);
			if(result<0)
			{
				clear ();
				return result;
			}
		}
		else
		{
			clear ();
			return result;
		}
	}

	const jchar* sPhoneName = env->GetStringChars(phoneName, NULL);
	int nPhoneNameSize=env->GetStringLength(phoneName);

	wchar_t * sPhoneNameString = new wchar_t [nPhoneNameSize+1];

	memcpy(sPhoneNameString, sPhoneName, nPhoneNameSize*2);
	sPhoneNameString[nPhoneNameSize]=0;


	wcscpy(sPhoneBTName,sPhoneNameString);
	nPhoneBTNameSize=nPhoneNameSize;

	devicePort=port;

	result=env->GetJavaVM(&g_jvm);
	g_obj = env->NewGlobalRef(obj);

	result=searchPhone();

	env->ReleaseStringChars(phoneName, sPhoneName);
	delete[] sPhoneNameString;

	if(result<0)
	{
		close();
		clear ();
		env->DeleteGlobalRef(g_obj);
		return result;
	}

	/*
	result= connectToDevice(nPhoneAddress,port);
	nPhonePort=port;
	if(result<0)
	{
		close();
		clear();
		return result;
	}*/
	
	
	return 1;
}


/**
 * Re-connects the phone.
 *
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_reconnectPhone
  (JNIEnv *, jobject, jstring, jint)
{
	if(!initated)
	{
		return Library_no_initialized;
	}
	
	int result;

	result = disconnect();
	Sleep(500);
	result= connectToDevice(nPhoneAddress,nPhonePort);
	if(result<0)
	{
		return result;
	}
	connected=true;
	return 1;
}

/**
 * deactivates the library.
 *
 * @param env environment variable
 * @return if the returned value is less then 0, the value is an error number
 */

JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_deactivate
  (JNIEnv * env, jobject g_ob)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	int result=0;
	result = disconnect();
	connected=false;
	result=close();
	
	if(result<0)
	{
		return result;
	}
	clear ();

	env->DeleteGlobalRef(g_obj);
	return 1;
}


/**
 * Accepts the phone call.
 *
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_acceptCall
  (JNIEnv *, jobject)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	int result = acceptCall();
	return result;
}


/**
 * Drops the phone call.
 *
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_dropCall
  (JNIEnv *, jobject)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	int result=dropCall();

	return result;
}


/**
 * Makes the phone call.
 *
 * @param env environment variable
 * @param phoneID phone ID
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_makePhoneCall
  (JNIEnv * env, jobject, jstring phoneID)
{

	if(!initated)
	{
		return Library_no_initialized;
	}

	int nPhoneIDSize=env->GetStringLength(phoneID);

	if(nPhoneIDSize==0)
	{
		return Data_empty;
	}

	const jchar * sPhoneID = env->GetStringChars(phoneID, NULL);

	wchar_t * sPhoneString = new wchar_t [nPhoneIDSize+1];

	memcpy(sPhoneString, sPhoneID, nPhoneIDSize*2);
	sPhoneString[nPhoneIDSize]=0;

	int result=makePhoneCall((LPWSTR)sPhoneID);

	delete[] sPhoneString;

	env->ReleaseStringChars(phoneID, sPhoneID);
	return result;
}


/**
 * Sends the SMS.
 *
 * @param env environment variable
 * @param phoneID phone ID
 * @param smsContent content of the SMS
 * @return if the returned value is less then 0, the value is an error number
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_phonecontrol_PhoneControlBridge_sendSMS
  (JNIEnv * env, jobject, jstring phoneID, jstring smsContent)
{
	if(!initated)
	{
		return Library_no_initialized;
	}

	int nPhoneIDSize=env->GetStringLength(phoneID);
	int sSMSContentSize=env->GetStringLength(smsContent);

	if(nPhoneIDSize==0||sSMSContentSize==0)
	{
		return Data_empty;
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
