#include <avr/io.h>
#include <avr/pgmspace.h>
#include <stdint.h>
#include <util/delay.h>
#include "usb_serial.h"
#include "CIM-n.h"
#include "PPM.h"
#include "globals.h"
#include "Timer.h"
#include <avr/interrupt.h>



#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))

int main(void)
{
	CPU_PRESCALE(0);
	usb_init();
	init_CIM_frame(); 
	PIN0_CONFIG;

	channel1 = 0, channel2 = 0, channel3 = 0, channel4 = 0, channel5 = 0, channel6 = 0, channel7 = 0, channel8 = 0;
	//times[18] = {pauseppm, 1100, pauseppm, 1100, pauseppm, 700, pauseppm, 1100, pauseppm, 700, pauseppm, 700, pauseppm, 700, pauseppm, 700, pauseppm, 11168};

	sei(); // enable global interrupts
	
	Timer1_init();

	while (1) 
	{
		// the circle of life		

		// ouput the PPM signal
		generate_frame(channel1, channel2, channel3, channel4, channel5, channel6, channel7, channel8);

		// if CIM is available via USB serial parse it and set new channel values
		if(usb_serial_available())
		{
			parse_CIM_protocol();
		}

	}

}
