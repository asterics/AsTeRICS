
/*
     AsTeRICS Arduino CIM Firmware

	 file: Arduino.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <inttypes.h>
#include <stdio.h>
#include <string.h>
#include <util/delay.h>
#include "Uart.h"
#include "Adc.h"
#include "Timer.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;
extern unsigned char PIND_Mask;
extern unsigned char PINB_Mask;

volatile uint8_t check_PINChange_now=0;   // flag for pinstate update
uint8_t pinChangeCounter = 0;

uint8_t old_PIND,old_PINB;
uint8_t autoreply_num=0x80;   // sequential number for automatic replies, 0x80-0xff

void setupHardware(void)
{
	MCUSR &= ~(1 << WDRF); 	// Disable watchdog if enabled by bootloader/fuses 
 	//	wdt_disable();
	//	LEDs_Init();	
	UART_Init(115200);
	ADC_Init();
	Timer_Init();
	
	//pin change interrupt activation
	PCICR = (1<<PCIE0)|(1<<PCIE2);
}



int main(void  )
{
 	setupHardware();
	init_CIM_frame();

    sei();           // enable global interrupts

	while (1)
	{  
		parse_CIM_protocol();

	    if (send_ADCFrame_now)   // this is updated in the timer ISR !!
	    {
		    send_ADCFrame_now=0;
		    autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;

		    CIM_frame.cim_feature=ARDUINO_CIM_FEATURE_ADCREPORT;
		    CIM_frame.serial_number=autoreply_num;
		    CIM_frame.reply_code=CMD_EVENT_REPLY;
			generate_ADCFrame();
			reply_DataFrame();
	     
		   //DDRB |= (1<<5); PORTB ^= (1<<5);  // indicate frame send with led
	    }

		if (check_PINChange_now && (pinChangeCounter++ == 50))  // this is updated in the pinchange ISR
		{ 
		    check_PINChange_now=0;
		    pinChangeCounter = 0;

		    autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;

		    CIM_frame.cim_feature=ARDUINO_CIM_FEATURE_GET_PINVALUES;
		    CIM_frame.serial_number=autoreply_num;
			CIM_frame.reply_code=CMD_EVENT_REPLY;
		    generate_PINFrame();	     
		    reply_DataFrame();
		}

	}
}



//Pin change ISR, setting the pin change bit (the CIM frame is transmitted from main)
ISR(PCINT0_vect)
{
	check_PINChange_now=1;
}

//Pin change ISR, setting the pin change bit (the CIM frame is transmitted from main)
ISR(PCINT2_vect)
{
	check_PINChange_now=1;
}


