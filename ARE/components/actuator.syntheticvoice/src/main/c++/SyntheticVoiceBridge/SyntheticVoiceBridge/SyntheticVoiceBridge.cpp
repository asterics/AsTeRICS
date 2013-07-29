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

/**
 * Implements native interface to the Microsoft Speech SDK for Synthetic Voice plugin.
 *    
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 11, 2011
 *         Time: 4:27:47 PM
 */

#include "SyntheticVoiceBridge.h"
#include "SyntheticVoiceBridgeErrors.h"
#include "sapi.h"
#include <spuihelp.h>
#include <string>
#include <algorithm>

using namespace std;

bool initialized=false;
bool COM_Initialized=false;
ISpVoice * pVoice = NULL;
bool useXmlTags=false;

/**
 * Initialize SAPI.
 * @return   if the returned value is less then 0, the value is an error number.
 */
int voiceInitialization()
{
	if(COM_Initialized==false)
	{
		if (FAILED(CoInitializeEx(NULL,COINIT_MULTITHREADED)))
		{
			
			return com_initialize_error;
		}
		else
		{
			COM_Initialized=true;
		}
	}

	HRESULT result = CoCreateInstance(CLSID_SpVoice, NULL, CLSCTX_ALL, IID_ISpVoice, (void **)&pVoice);
    if( FAILED( result ) )
	{
		return voice_initialize_error;
	}

	return 1;
}

enum Similarity {DIFFERENT=0, SIMILAR_IGNORE_CASE=1, SIMILAR=2, THE_SAME_IGNORE_CASE=3, THE_SAME=4};

/**
 * Compares names of the voices.
 * @param userVoice   voice name given by user
 * @param voice   voice name to compare
 * @return   degree of similarity between two voices names
 */
Similarity voiceNameCompare(wchar_t *userVoice, wchar_t *voice)
{
	
	wstring userVoiceName = userVoice;
	wstring voiceName=voice;

	if(userVoiceName.length()>voiceName.length())
	{
		return DIFFERENT;
	}
	else
	{
		if(userVoiceName.length()==voiceName.length())
		{
			if(userVoiceName.compare(voiceName)==0)
			{
				return THE_SAME;
			}
			else
			{
				std::transform(userVoiceName.begin(), userVoiceName.end(), userVoiceName.begin(), tolower);
				std::transform(voiceName.begin(), voiceName.end(), voiceName.begin(), tolower);
				if(userVoiceName.compare(voiceName)==0)
				{
					return THE_SAME_IGNORE_CASE;
				}

				return DIFFERENT;

			}
		}
		else
		{
			if(voiceName.compare(0,userVoiceName.length(),userVoiceName)==0)
			{
				return SIMILAR;
			}
			else
			{
				std::transform(userVoiceName.begin(), userVoiceName.end(), userVoiceName.begin(), tolower);
				std::transform(voiceName.begin(), voiceName.end(), voiceName.begin(), tolower);
				if(voiceName.compare(0,userVoiceName.length(),userVoiceName)==0)
				{
					return SIMILAR_IGNORE_CASE;
				}

				return DIFFERENT;
			}
		}
	}
}

/**
 * Sets the voice.
 * @param voice   voice to set
 * @return   if the returned value is less then 0, the value is an error number.
 */
int setVoice(wchar_t *voice)
{
	ISpObjectToken * cpVoiceToken;
	IEnumSpObjectTokens *cpEnum;

	int voiceLength =wcslen(voice);


	if(voiceLength==0)
	{
		return 1;
	}

	

	HRESULT result;

	result = SpEnumTokens(SPCAT_VOICES, NULL, NULL, &cpEnum);

	if(FAILED(result))
	{
	
	}

	ULONG numberOfVoices=0;

	result = cpEnum->GetCount(&numberOfVoices);

	if(FAILED(result))
	{
	
	}

	ISpObjectToken * cpToken;

	Similarity bestSimilarity=DIFFERENT;
	bool found=false;

	for(int i=0;i<numberOfVoices;i++)
	{
		cpEnum->Next(1, &cpVoiceToken, NULL);
		wchar_t *voiceName;

		cpVoiceToken->GetStringValue(NULL,&voiceName);

		Similarity similarityResult=voiceNameCompare(voice,voiceName);

		if(similarityResult>bestSimilarity)
		{
			if(found)
			{
				cpToken->Release();
			}
			
			found=true;
			cpToken=cpVoiceToken;
			bestSimilarity=similarityResult;
		}
		else
		{	
			
			cpVoiceToken->Release();
			
		}
	}

	if(found)
	{
		pVoice->SetVoice(cpToken);
		cpToken->Release();
	}
	else
	{
		pVoice->SetVoice(NULL);
		return voice_not_found_warning;
	}

	cpEnum->Release();

	return 1;
}


/**
 * Sets the voice parameters.
 * @param volume   voice volume
 * @param speed   voice speed
 * @param voice   voice to set
 * @return   if the returned value is less then 0, the value is an error number.
 */
int setVoiceParameters(int volume,int speed, wchar_t *voice)
{
	if(pVoice==NULL)
	{
		return voice_error;
	}

	int result =setVoice(voice);


	HRESULT hResult;

	hResult=pVoice->SetVolume(volume);

	if(SUCCEEDED(hResult)==FALSE)
	{
		return library_initialize_warning;
	}

	hResult=pVoice->SetRate(speed);

	if(SUCCEEDED(hResult)==FALSE)
	{
		return library_initialize_warning;
	}

	return result;
}

/**
 * uninitializes SAPI.
 * @return   if the returned value is less then 0, the value is an error number.
 */
int closeVoice()
{
	if(pVoice!=NULL)
	{
		pVoice->Release();
		pVoice=NULL;
	}

	if(COM_Initialized)
	{
		CoUninitialize();
		COM_Initialized=false;
	}
	
	return 1;
}

/**
 * Interface function which activates the library
 * @param env   environment variable
 * @param volume   volume parameter
 * @param speech   speed parameter
 * @param voice   voice parameter
 * @param xmlTags   defines if the text to speak will be parsed for the XML tags.
 * @return   if the returned value is less then 0, the value is an error number.
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_syntheticvoice_SyntheticVoiceBridge_activate
  (JNIEnv * env, jobject, jint volume, jint speed, jstring voice, jboolean xmlTags)
{
	if(initialized)
	{
		return library_initialized;
	}

	int result =voiceInitialization();

	if(result<0)
	{
		return result;
	}

	initialized=true;

	const jchar * voiceArray = env->GetStringChars(voice, NULL);
	int voiceSize=env->GetStringLength(voice);
    wchar_t * voiceName = new wchar_t [voiceSize+1];

	memcpy(voiceName, voiceArray, voiceSize*2);
	voiceName[voiceSize]=0;

	if(xmlTags)
	{
		useXmlTags=true;
	}
	else
	{
		useXmlTags=false;
	}

	if((volume<0)||(volume>100))
	{
		volume=100;
	}

	if((speed<-10)||(speed>10))
	{
		speed=0;
	}


	result=setVoiceParameters((int)volume,(int)speed,voiceName);

	env->ReleaseStringChars(voice, voiceArray);
	delete[] voiceName;
	return result;
}

/**
 * Interface function which deactivates the library
 * @return   if the returned value is less then 0, the value is an error number.
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_syntheticvoice_SyntheticVoiceBridge_deactivate
  (JNIEnv *, jobject)
{
	if(!initialized)
	{
		return library_no_initialized;
	}


	closeVoice();

	initialized=false;

	return 1;
}

/**
 * Speech the text.
 * @param text   text to speech
 * @return   if the returned value is less then 0, the value is an error number.
 */
JNIEXPORT jint JNICALL Java_eu_asterics_component_actuator_syntheticvoice_SyntheticVoiceBridge_speech
  (JNIEnv * env, jobject, jstring text)
{
	if(!initialized)
	{
		return library_no_initialized;
	}
	
	if(pVoice==NULL)
	{
		return voice_error;
	}

	const jchar * textArray = env->GetStringChars(text, NULL);
	int textSize=env->GetStringLength(text);
    wchar_t * textToRead = new wchar_t [textSize+1];
	
	memcpy(textToRead, textArray, textSize*2);
	textToRead[textSize]=0;

	HRESULT result;

	if(useXmlTags)
	{
		result=pVoice->Speak(textToRead,SPF_ASYNC|SPF_PURGEBEFORESPEAK|SPF_IS_NOT_XML,NULL);
	}
	else
	{
		result=pVoice->Speak(textToRead,SPF_ASYNC|SPF_PURGEBEFORESPEAK|SPF_IS_XML,NULL);
	}

	env->ReleaseStringChars(text, textArray);
	delete[] textToRead;

	if(SUCCEEDED(result))
	{
		return 1;
	}
	else
	{
		return speak_error;
	}

}