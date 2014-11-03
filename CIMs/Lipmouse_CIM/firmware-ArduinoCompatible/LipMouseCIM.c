
/*
     AsTeRICS LipMouse CIM Firmware
	 using Teensy 2.0++ Controller board

	 file: LipMouseCIM.c
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


extern struct CIM_frame_t CIM_frame;
//extern unsigned char PIND_Mask;
//extern unsigned char PINB_Mask;

//uint8_t old_PIND,old_PINB;
uint8_t autoreply_num=0x80;   // sequential number for automatic replies, 0x80-0xff


void send_str(char *s)
{
	while (*s) usb_serial_putchar(*s++);
}


void setupHardware(void)
{
	Timer_Init();
	ADC_Init();
	DDRA= (1<<3);
	PORTA= (1<<3);

}


int main(void)
{
//	uint16_t value;

	CPU_PRESCALE (1);

	// initialize the USB, and then wait for the host
	// to set configuration.  If the Teensy is powered
	// without a PC connected to the USB port, this 
	// will wait forever.
	usb_init();
	while (!usb_configured()) /* wait */ ;
	_delay_ms(1000);
	
	setupHardware();
	init_CIM_frame();

	//old_PIND=PIND;
	//old_PINB=PINB;
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
	     
		   //DDRB |= (1<<5); PORTB ^= (1<<5);  // indicate frame send with led
	    }

	}
}

