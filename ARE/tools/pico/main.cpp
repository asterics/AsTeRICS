#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "TtsEngine.h"

#define OUTPUT_BUFFER_SIZE (128 * 1024)

using namespace android;

static bool synthesis_complete = false;

static FILE *outfp = stdout;

// @param [inout] void *&       - The userdata pointer set in the original
//                                 synth call
// @param [in]    uint32_t      - Track sampling rate in Hz
// @param [in] tts_audio_format - The audio format
// @param [in]    int           - The number of channels
// @param [inout] int8_t *&     - A buffer of audio data only valid during the
//                                execution of the callback
// @param [inout] size_t  &     - The size of the buffer
// @param [in] tts_synth_status - indicate whether the synthesis is done, or
//                                 if more data is to be synthesized.
// @return TTS_CALLBACK_HALT to indicate the synthesis must stop,
//         TTS_CALLBACK_CONTINUE to indicate the synthesis must continue if
//            there is more data to protools/picotts/tts/main.cppduce.
tts_callback_status synth_done(void *& userdata, uint32_t sample_rate,
        tts_audio_format audio_format, int channels, int8_t *& data, size_t& size, tts_synth_status status)
{
	//fprintf(stderr, "TTS callback, rate: %d, data size: %d, status: %i\n", sample_rate, size, status);

	if (status == TTS_SYNTH_DONE)
	{
		synthesis_complete = true;
	}

	if ((size == OUTPUT_BUFFER_SIZE) || (status == TTS_SYNTH_DONE))
	{
		fwrite(data, size, 1, outfp);
	}

	return TTS_CALLBACK_CONTINUE;
}

static void usage(void)
{
	fprintf(stderr, "\nUsage:\n\n" \
					"tts [-o filename] [-l language] \"Text to speak\"\n\n" \
		   			"  -o\tFile to write audio to (default stdout)\n"
		   			"  -l\tLanguage for speech synth (en/de/es/fr/it/us, default:en)\n\n");
	exit(0);
}

int main(int argc, char *argv[])
{
	tts_result result;
	TtsEngine* ttsEngine = getTtsEngine();
	uint8_t* synthBuffer;
	char* synthInput = NULL;
	int currentOption;
    char* outputFilename = NULL;
    char* languageName = NULL;

	fprintf(stderr, "Pico TTS Test App\n");

	if (argc == 1)
	{
		usage();
	}

    while ( (currentOption = getopt(argc, argv, "o:l:h")) != -1)
    {
        switch (currentOption)
        {
        case 'o':
        	outputFilename = optarg;
            fprintf(stderr, "Output audio to file '%s'\n", outputFilename);
            break;
        case 'l':
        	languageName = optarg;
            fprintf(stderr, "Language '%s'\n", languageName);
            break;
        case 'h':
        	usage();
            break;
        default:
            printf ("Getopt returned character code 0%o ??\n", currentOption);
        }
    }

    if (optind < argc)
    {
    	synthInput = argv[optind];
    }

    if (!synthInput)
    {
    	fprintf(stderr, "Error: no input string\n");
    	usage();
    }

    fprintf(stderr, "Input string: \"%s\"\n", synthInput);

	synthBuffer = new uint8_t[OUTPUT_BUFFER_SIZE];

	result = ttsEngine->init(synth_done, "tools/pico/lang/");  // attention! relative path from ARE folder !

	if (result != TTS_SUCCESS)
	{
		fprintf(stderr, "Failed to init TTS\n");
	}

	if (!strcmp(languageName,"de")) 
	{
		fprintf(stderr, "Init TTS for German langunge.\n");
		result = ttsEngine->setLanguage("deu", "DEU", "");
	}
	else if (!strcmp(languageName,"es")) 
	{
		fprintf(stderr, "Init TTS for Spanish langunge.\n");
		result = ttsEngine->setLanguage("spa", "SPA", "");
	}
	else if (!strcmp(languageName,"it")) 
	{
		fprintf(stderr, "Init TTS for Italian langunge.\n");
		result = ttsEngine->setLanguage("ita", "ITA", "");
	}
	else if (!strcmp(languageName,"fr")) 
	{
		fprintf(stderr, "Init TTS for French langunge.\n");
		result = ttsEngine->setLanguage("fra", "FRA", "");
	}
	else if (!strcmp(languageName,"us")) 
	{
		fprintf(stderr, "Init TTS for american english langunge.\n");
		result = ttsEngine->setLanguage("eng", "USA", "");
	}
	else
	{
		fprintf(stderr, "Init TTS for british English langunge.\n");
	    result = ttsEngine->setLanguage("eng", "GBR", "");
	}
	
	if (result != TTS_SUCCESS)
	{
		fprintf(stderr, "Failed to load language\n");
		exit(0);
	}

	if (outputFilename)
	{
		outfp = fopen(outputFilename, "wb");
	}
	else
	{
		fprintf(stderr, "creating temp file\n");		
		outfp = fopen("/tmp/tmp.wav", "wb");
	}

	fprintf(stderr, "Synthesising text...\n");

	result = ttsEngine->synthesizeText(synthInput, synthBuffer, OUTPUT_BUFFER_SIZE, NULL);

	if (result != TTS_SUCCESS)
	{
		fprintf(stderr, "Failed to synth text\n");
		fclose(outfp);
		exit(0);
	}

	while(!synthesis_complete)
	{
		usleep(100);
	}

	fprintf(stderr, "Completed.\n");

	if (outputFilename)
	{
		fclose(outfp);
	}

	result = ttsEngine->shutdown();

	if (result != TTS_SUCCESS)
	{
		fprintf(stderr, "Failed to shutdown TTS\n");
	}

	fprintf(stderr, "Now starting aplay !\n");

	system ("aplay /tmp/tmp.wav --rate=16000 --channels=1 --format=S16_LE");

	delete [] synthBuffer;

	return 0;
}
