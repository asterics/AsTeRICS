
/*
     AsTeRICS LipMouseIR CIM Firmware
	 using Teensy 2.0++ Controller board

	 file: LipMouseCIM.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW), extended by  Christoph Ulbinger (FHTW)
	 Date: 11/11/2011

*/

// #define IR_LED_WITH_MOSFET				// For the use of a lipmouse with mosfet amplified IR Leds


// #define POWER_PRESSURE_SENSOR_VIA_GPIO   // use this for interim version of lipmouse with GPIO-powered pressuresensor !


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
#include "IR.h"

#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))

extern struct CIM_frame_t CIM_frame;
extern uint8_t buttonval;     // actual button states (defined in CimProtocol.c)



void send_str(char *s)
{
	while (*s) usb_serial_putchar(*s++);
}


void setupHardware(void)     // setup GPIO, Timer and ADC
{
	Timer_Init();
	ADC_Init();

	#ifdef POWER_PRESSURE_SENSOR_VIA_GPIO
  	  DDRB=  (1<<1)|(1<<4);  // B1 and B2 output
	  PORTB= (1<<1);         // B1 5V to power up the pressure sensor !
	#else
	  PORTC= (1<<3);         // pullup  for internal buttons at C1 
	  PORTD= (1<<2)|(1<<3);  // pullups for external buttons at B1 and B2 
	  DDRE=  (1<<6)|(1<<7);  // indicator leds 
	  DDRB=  (1<<0);         // indicator leds 
	#endif
}


int main(void)
{

	CPU_PRESCALE (1);
	autoreply_num=0x80;
	// initialize the USB, and then wait for the host
	// to set configuration.  If the Teensy is powered
	// without a PC connected to the USB port, this 
	// will wait forever.
	usb_init();
	while (!usb_configured()) /* wait */ ;
	_delay_ms(1000);
	
	setupHardware();   // setup GPIO pins
	init_CIM_frame();

	Init_IR();

    sei();           // enable global interrupts

	while (1)
	{  
		parse_CIM_protocol();

	    if (send_ADCFrame_now)   // this is updated in the timer ISR !!
	    {
		    send_ADCFrame_now=0;
		    autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;

		    CIM_frame.cim_feature=LIPMOUSE_CIM_FEATURE_ADCREPORT;
		    CIM_frame.serial_number=autoreply_num;
		    CIM_frame.reply_code=CMD_EVENT_REPLY;
			generate_ADCFrame();
			reply_DataFrame();

		    if (update_Buttonval())  // if buttonstate has changed 
			{
			    autoreply_num++; 
			    if (autoreply_num==0) autoreply_num=0x80;

			    CIM_frame.cim_feature=LIPMOUSE_CIM_FEATURE_BUTTONREPORT;
			    CIM_frame.serial_number=autoreply_num;
			    CIM_frame.reply_code=CMD_EVENT_REPLY;
				generate_ButtonFrame();  // send new buttonframe
				reply_DataFrame();
			}

	    }

		switch(set_ir_status)
		{
			case 0:				// Idle
				break;
			case 1:				// Sending	

				stop_send_ir();
				stop_record_ir();

				start_send_ir();

				set_ir_status = 0;
				break;
			case 2:				// Recording

				stop_send_ir();
				stop_record_ir();

				start_record_ir();

				set_ir_status = 0;
				break;
			default:
				stop_send_ir();
				stop_record_ir();

				set_ir_status = 0;
				break;
		}

	}
}

