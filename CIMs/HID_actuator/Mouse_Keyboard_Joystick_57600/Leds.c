
/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
	 file: Leds.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010


*/

#ifndef F_CPU
#define F_CPU 16000000
#endif

#include "Leds.h"
#include <util/delay.h>



void blink_Led(unsigned int blinks, unsigned int sleeptime){

    LEDs_SetAllLEDs(NO_LEDS);
	for (int i=0; i < blinks; i++){

		LEDs_ToggleLEDs(LED7);
		_delay_ms(sleeptime);
	
	}
}


