
/*
     AsTeRICS Proximity CIM Firmware
	 using Teensy 2.0++ Controller board

	 file: ProximityCIM.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/


#include <avr/io.h>
#include <avr/pgmspace.h>
#include <stdint.h>
#include <util/delay.h>
#include <inttypes.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <inttypes.h>
#include <stdio.h>
#include <string.h>
#include <util/delay.h>
#include "Adc.h"
#include "Timer.h"
#include "CimProtocol.h"
#include "usb_serial.h"


#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))


#define AUTOUPDATE_VALUES     0
#define THRESHOLD_BELOW_ABOVE 1
#define THRESHOLD_ABOVE_BELOW 2
#define THRESHOLD_BOTH        3


extern struct CIM_frame_t CIM_frame;
uint16_t oldVal;


void send_str(char *s)
{
	while (*s) usb_serial_putchar(*s++);
}


void setupHardware(void)
{
	Timer_Init();
	ADC_Init();

	DDRD |= (1<<5);	PORTD &= ~(1<<5);
	DDRD |= (1<<6);	PORTD &= ~(1<<6);

	DDRB |= (1<<5); PORTB &= ~(1<<5); // PB5 bzw. OC1A --> Output, init: low
	DDRB &= ~(1<<0); PORTB |= (1<<0); // PB0: input with pullup
	
	// start_timer1();
}


int main(void)
{
	uint16_t value;

	CPU_PRESCALE (0);

	// initialize the USB, and then wait for the host
	// to set configuration.  If the Teensy is powered
	// without a PC connected to the USB port, this 
	// will wait forever.
	usb_init();
	while (!usb_configured()) /* wait */ ;
	_delay_ms(1000);
	
	setupHardware();
	init_CIM_frame();

	sei();

	while (1) 
	{	
		parse_CIM_protocol();   // look if new command arrived from ARE and process it

		if (ADC_updates)   // is a new buffer of ADC-values available ? 
						   // (updated via the ADC ISR)
		{
			value = mittl_berechnen();  // calculate averaged value 
		    ADC_updates=0;

			switch (selection)   {

				case AUTOUPDATE_VALUES:
					generate_ADCFrame(value);
				break;

				case THRESHOLD_BELOW_ABOVE:
					if ((oldVal < threshold) && (value >= threshold))
						generate_EventFrame(0);
					break;

				case THRESHOLD_ABOVE_BELOW:
					if ((oldVal > threshold) && (value <= threshold))
						generate_EventFrame(1);
					break;

				case THRESHOLD_BOTH:
					if ((oldVal < threshold) && (value >= threshold))
						generate_EventFrame(0);
					else if ((oldVal > threshold) && (value <= threshold))
						generate_EventFrame(1);
					break;
			}
			oldVal = value; 
		}
	}	
}


