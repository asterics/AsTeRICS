
/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
	 file: Leds.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010


*/

#include "Leds.h"


void stupid_sleep(unsigned int time){
	volatile unsigned int i = 0;
	volatile unsigned int j = 0;
	for(i=0;i<time;i++){
		for(j=0;j<530;j++){
			//Do nothing
		}
	}
}


void blink_Leds(unsigned int blinks, unsigned int sleeptime){

    LEDs_SetAllLEDs(NO_LEDS);
	for (int i=0; i < blinks; i++){

		LEDs_ToggleLEDs(LED6);
		stupid_sleep(sleeptime);
		LEDs_ToggleLEDs(LED6);
		stupid_sleep(sleeptime);
	
	}
}

void scroll_leds (void)
{
		LEDs_ToggleLEDs(LED6);
}
 

