/*

	Used for the recieve part of the application.
	Recieved values are temporarily stored within channel1 - channel8

*/

extern uint16_t channel1; 
extern uint16_t channel2;
extern uint16_t channel3;
extern uint16_t channel4;
extern uint16_t channel5;
extern uint16_t channel6;
extern uint16_t channel7;
extern uint16_t channel8;

/*

	Used for PPM signal generation.
	Calculated time slots are stored in here
	and passed to the timer array which controls
	the ISR.

*/

volatile int channel1frame;
volatile int channel2frame;
volatile int channel3frame;
volatile int channel4frame;
volatile int channel5frame;
volatile int channel6frame;
volatile int channel7frame;
volatile int channel8frame;
volatile int startframe;


extern int timecounter;
//extern int times[18];
