#include "PPM.h"
#include "globals.h"
#include "Timer.h"



// calculate the PPM frames and output it on PD0
void generate_frame(uint16_t channel1, uint16_t channel2, uint16_t channel3, uint16_t channel4, uint16_t channel5, uint16_t channel6, uint16_t channel7, uint16_t channel8)
{


	int baseframe = 700;
	int completeframe = 22000;
	
	//double relativator = 3.1372; //(8 bit values)
	double relativator = 0.8;



	channel1frame = channel1 * relativator + baseframe;
	channel2frame = channel2 * relativator + baseframe;
	channel3frame = channel3 * relativator + baseframe;
	channel4frame = channel4 * relativator + baseframe;
	channel5frame = channel5 * relativator + baseframe;
	channel6frame = channel6 * relativator + baseframe;
	channel7frame = channel7 * relativator + baseframe;
	channel8frame = channel8 * relativator + baseframe;
	
	if(channel1frame >= 1500)
		channel1frame = 1500;
	if(channel2frame >= 1500)
		channel1frame = 1500;
	if(channel3frame >= 1500)
		channel3frame = 1500;
	if(channel4frame >= 1500)
		channel4frame = 1500;
	if(channel5frame >= 1500)
		channel5frame = 1500;
	if(channel6frame >= 1500)
		channel6frame = 1500;
	if(channel7frame >= 1500)
		channel7frame = 1500;
	if(channel8frame >= 1500)
		channel8frame = 1500;


	startframe = completeframe - ((channel1frame + channel2frame + channel3frame + channel4frame + channel5frame + channel6frame + channel7frame + channel8frame) + 3600);


}
